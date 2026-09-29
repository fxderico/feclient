package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.render.c$bUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.suschunk.ServerLightCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

/**
 * LightBaseFinder — the merge of LightFinder + LightDebug, rebuilt to actually
 * find bases instead of painting a heatmap you have to read by eye.
 *
 * Three light signals are fused per cell:
 *   1. SERVER block-light (ServerLightCache) — the value the server streams for
 *      every chunk, so a torch behind a sealed wall still lights the cell. This
 *      is the real through-walls base signal.
 *   2. CLIENT block-light (World.getLightLevel) — covers everything the client
 *      has recomputed; catches areas the server cache hasn't (yet) filled.
 *   3. Visible light-SOURCE blocks (the old LightFinder block set) — a direct
 *      confirmation and, with Bloom on, a glow highlight.
 * A cell counts as "lit" when max(server, client) >= MinLight. A cell the server
 * lights but the client renders dark = a walled-off / buried source: the single
 * strongest base tell, so it's weighted heavier when scoring clusters.
 *
 * The recall win over both originals: lit cells are binned in the XZ plane and a
 * bin with >= MinCluster lit cells becomes a BASE MARKER (footprint box + vertical
 * beam). Concentrated lighting = a base; a lone cave torch never reaches the
 * cluster threshold, so you see bases, not noise. Heatmap and Bloom remain as
 * optional modes so nothing from the old two modules is lost.
 */
public final class LightBaseFinderModule extends Module {
   private final ModeSetting mode = new ModeSetting("Mode", "Bases", "Bases", "Heatmap", "Both");
   private final NumberSetting radius = new NumberSetting("Radius", 64.0, 8.0, 128.0, 1.0);
   private final NumberSetting yMin = new NumberSetting("YMin", -64.0, -64.0, 320.0, 1.0);
   private final NumberSetting yMax = new NumberSetting("YMax", 96.0, -64.0, 320.0, 1.0);
   private final NumberSetting minLight = new NumberSetting("MinLight", 8.0, 1.0, 15.0, 1.0);
   private final NumberSetting throttle = new NumberSetting("ScanTicks", 10.0, 2.0, 40.0, 1.0);
   // clustering
   private final NumberSetting bin = new NumberSetting("ClusterSize", 8.0, 2.0, 16.0, 1.0);
   private final NumberSetting minCluster = new NumberSetting("MinCluster", 10.0, 1.0, 300.0, 1.0);
   // signal sources
   private final BooleanSetting serverLight = new BooleanSetting("ServerLight", true);
   private final BooleanSetting clientLight = new BooleanSetting("ClientLight", true);
   private final BooleanSetting hiddenOnly = new BooleanSetting("HiddenOnly", false);
   // render
   private final BooleanSetting beam = new BooleanSetting("Beam", true);
   private final BooleanSetting sourceBloom = new BooleanSetting("SourceBloom", false);
   private final NumberSetting bloomSpread = new NumberSetting("BloomSpread", 4.0, 1.0, 10.0, 1.0);
   private final ColorSetting markerColor = new ColorSetting("BaseColor", -256); // yellow
   private final NumberSetting alpha = new NumberSetting("Alpha", 160.0, 0.0, 255.0, 1.0);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.5, 0.5, 4.0, 0.1);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final BooleanSetting countHUD = new BooleanSetting("CountHUD", true);

   // scan outputs (built on the tick thread, read on the render thread — swapped
   // by reference so render never sees a half-filled collection)
   private volatile List<BaseMarker> markers = new ArrayList<>();
   private volatile Map<Long, Integer> heatCells = new HashMap<>();
   private volatile List<Long> bloomSources = new ArrayList<>();
   private int tick;

   private static final java.util.Set<Block> SOURCES = buildSourceSet();

   public LightBaseFinderModule() {
      super("LightBaseFinder", "Fused server+client light base finder (through walls) — clusters light into bases", Category.DONUTSMP);
      this.run6(new Setting[]{
         this.mode, this.radius, this.yMin, this.yMax, this.minLight, this.throttle,
         this.bin, this.minCluster,
         this.serverLight, this.clientLight, this.hiddenOnly,
         this.beam, this.sourceBloom, this.bloomSpread,
         this.markerColor, this.alpha, this.lineWidth, this.throughWalls, this.countHUD
      });
      this.bin.visibleWhen(this::clusteringOn);
      this.minCluster.visibleWhen(this::clusteringOn);
      this.beam.visibleWhen(this::clusteringOn);
      this.bloomSpread.visibleWhen(this.sourceBloom::getValue);
      this.hiddenOnly.visibleWhen(this.serverLight::getValue);
   }

   private Boolean clusteringOn() {
      return !"Heatmap".equals(this.mode.getMode());
   }

   @Override
   public void run() {
      this.tick = 0;
      this.markers = new ArrayList<>();
      this.heatCells = new HashMap<>();
      this.bloomSources = new ArrayList<>();
   }

   @Override
   public void run2() {
      this.markers = new ArrayList<>();
      this.heatCells = new HashMap<>();
      this.bloomSources = new ArrayList<>();
      this.tick = 0;
      try {
         c$bUtils.run4();
      } catch (Throwable ignored) {
      }
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.world == null) {
         return;
      }
      int every = Math.max(2, (int) Math.round(this.throttle.getValue()));
      if (this.tick++ % every != 0) {
         return;
      }
      try {
         scanNow();
      } catch (Throwable t) {
         this.markers = new ArrayList<>();
         this.heatCells = new HashMap<>();
         this.bloomSources = new ArrayList<>();
      }
   }

   private void scanNow() {
      int r = this.radius.getValueInt();
      int threshold = (int) Math.round(this.minLight.getValue());
      String m = this.mode.getMode();
      boolean wantClusters = !"Heatmap".equals(m);
      boolean wantHeat = !"Bases".equals(m);
      boolean useServer = this.serverLight.getValue();
      boolean useClient = this.clientLight.getValue();
      boolean hidden = useServer && this.hiddenOnly.getValue();
      boolean bloom = this.sourceBloom.getValue();
      int binSize = Math.max(2, this.bin.getValueInt());
      ServerLightCache cache = useServer ? ServerLightCache.get() : null;

      int px = (int) Math.floor(class310.player.getX());
      int py = (int) Math.floor(class310.player.getY());
      int pz = (int) Math.floor(class310.player.getZ());

      int worldBottom = class310.world.getBottomY();
      int worldTop = worldBottom + class310.world.getHeight() - 1;
      int yLo = Math.max(worldBottom, (int) Math.round(this.yMin.getValue()));
      int yHi = Math.min(worldTop, (int) Math.round(this.yMax.getValue()));
      // also clamp to the player's vertical reach of the scan radius so a huge
      // Y range doesn't cost more than the horizontal radius already does
      yLo = Math.max(yLo, py - Math.max(r, 96));
      yHi = Math.min(yHi, py + Math.max(r, 96));

      Map<Long, Bin> bins = wantClusters ? new HashMap<>() : null;
      Map<Long, Integer> heat = wantHeat ? new HashMap<>() : null;
      List<Long> sources = bloom ? new ArrayList<>() : null;

      Mutable pos = new Mutable();
      for (int dx = -r; dx <= r; dx++) {
         int x = px + dx;
         for (int dz = -r; dz <= r; dz++) {
            int z = pz + dz;

            // per-column best (for heatmap column mode + cluster representative Y)
            int colBestY = Integer.MIN_VALUE;
            int colBestLevel = 0;

            for (int y = yLo; y <= yHi; y++) {
               pos.set(x, y, z);

               int serverLevel = -1;
               if (useServer) {
                  serverLevel = cache.serverBlockLight(x, y, z);
               }
               int clientLevel = 0;
               if (useClient) {
                  try {
                     clientLevel = class310.world.getLightLevel(LightType.BLOCK, pos);
                  } catch (Throwable t) {
                     clientLevel = 0;
                  }
               }

               int level = Math.max(serverLevel, clientLevel);
               if (level < threshold) {
                  // source-block bloom is independent of the light threshold so a
                  // just-placed / dim source still highlights
                  if (bloom && isSource(x, y, z, pos)) {
                     sources.add(BlockPos.asLong(x, y, z));
                  }
                  continue;
               }

               boolean isHidden = serverLevel > clientLevel; // server-lit, client-dark = walled
               if (hidden && !isHidden) {
                  if (bloom && isSource(x, y, z, pos)) {
                     sources.add(BlockPos.asLong(x, y, z));
                  }
                  continue;
               }

               // lit cell
               if (heat != null) {
                  if (level > colBestLevel) {
                     colBestLevel = level;
                     colBestY = y;
                  }
               }
               if (bins != null) {
                  long key = binKey(Math.floorDiv(x, binSize), Math.floorDiv(z, binSize));
                  Bin b = bins.get(key);
                  if (b == null) {
                     b = new Bin();
                     bins.put(key, b);
                  }
                  b.count++;
                  b.sumX += x;
                  b.sumZ += z;
                  if (level > b.maxLevel) {
                     b.maxLevel = level;
                     b.bestY = y;
                  }
                  if (isHidden) {
                     b.hidden++;
                  }
               }
               if (bloom && isSource(x, y, z, pos)) {
                  sources.add(BlockPos.asLong(x, y, z));
               }
            }

            if (heat != null && colBestY != Integer.MIN_VALUE) {
               heat.put(BlockPos.asLong(x, colBestY, z), colBestLevel);
            }
         }
      }

      // promote qualifying bins to base markers
      List<BaseMarker> newMarkers = new ArrayList<>();
      if (bins != null) {
         int need = Math.max(1, (int) Math.round(this.minCluster.getValue()));
         for (Bin b : bins.values()) {
            if (b.count >= need) {
               double cx = b.sumX / (double) b.count + 0.5;
               double cz = b.sumZ / (double) b.count + 0.5;
               newMarkers.add(new BaseMarker(cx, cz, b.bestY, b.count, b.hidden));
            }
         }
         // strongest (most lit cells, then most hidden) first — the biggest bases
         newMarkers.sort((a, b2) -> {
            int byCount = Integer.compare(b2.count, a.count);
            return byCount != 0 ? byCount : Integer.compare(b2.hidden, a.hidden);
         });
      }

      this.markers = newMarkers;
      this.heatCells = heat != null ? heat : new HashMap<>();
      this.bloomSources = sources != null ? sources : new ArrayList<>();
   }

   private boolean isSource(int x, int y, int z, Mutable pos) {
      try {
         return SOURCES.contains(class310.world.getBlockState(pos).getBlock());
      } catch (Throwable t) {
         return false;
      }
   }

   @Override
   public void run4(DrawContext ctx, float tickDelta) {
      if (class310.player == null || class310.world == null) {
         return;
      }
      boolean tw = this.throughWalls.getValue();
      int a = (int) Math.round(MathHelper.clamp(this.alpha.getValue(), 0.0, 255.0));
      float w = this.lineWidth.getValueFloat();

      // heatmap cells
      Map<Long, Integer> heat = this.heatCells;
      if (!heat.isEmpty()) {
         for (Map.Entry<Long, Integer> e : heat.entrySet()) {
            BlockPos p = BlockPos.fromLong(e.getKey());
            int color = heatColor(e.getValue(), a);
            Box box = new Box(p.getX(), p.getY(), p.getZ(), p.getX() + 1.0, p.getY() + 1.0, p.getZ() + 1.0);
            ListUtils.run5(box, color, w, tw);
         }
      }

      // base markers: footprint box + vertical beam at the cluster centroid
      List<BaseMarker> ms = this.markers;
      if (!ms.isEmpty()) {
         int baseC = this.markerColor.getValue() & 0xFFFFFF;
         int argb = (a << 24) | baseC;
         double half = Math.max(2.0, this.bin.getValueInt() * 0.5);
         boolean drawBeam = this.beam.getValue();
         int wb = class310.world.getBottomY();
         int wt = wb + class310.world.getHeight();
         for (BaseMarker mk : ms) {
            Box box = new Box(mk.x - half, mk.y, mk.z - half, mk.x + half, mk.y + 1.0, mk.z + half);
            ListUtils.run5(box, argb, w + 0.5F, tw);
            if (drawBeam) {
               ListUtils.run11(new Vec3d(mk.x, wb, mk.z), new Vec3d(mk.x, wt, mk.z), (Math.min(a, 90) << 24) | baseC, w);
            }
         }
      }

      // visible source-block bloom (old LightFinder look)
      List<Long> sources = this.bloomSources;
      if (this.sourceBloom.getValue() && !sources.isEmpty()) {
         int spread = MathHelper.clamp(this.bloomSpread.getValueInt(), 1, 10);
         int bloomArgb = (a << 24) | (this.markerColor.getValue() & 0xFFFFFF);
         for (long key : sources) {
            try {
               c$bUtils.run2(BlockPos.fromLong(key), bloomArgb, spread);
            } catch (Throwable ignored) {
            }
         }
      }

      if (this.countHUD.getValue()) {
         renderHud(ctx);
      }
   }

   private void renderHud(DrawContext ctx) {
      TextRenderer tr = class310.textRenderer;
      if (tr == null) {
         return;
      }
      int bases = this.markers.size();
      int lit = this.heatCells.size();
      String s = "Bases " + bases + (lit > 0 ? "  Lit " + lit : "");
      int w = tr.getWidth(s) + 16;
      ctx.fill(6, 100, 6 + w, 119, -535817448);
      ctx.fill(6, 100, 9, 119, -4523);
      ctx.drawText(tr, s, 16, 105, -4523, false);
   }

   // level 1..15 -> blue (dim) through red (bright)
   private static int heatColor(int level, int alpha) {
      float t = MathHelper.clamp((level - 1) / 14.0F, 0.0F, 1.0F);
      float hue = (1.0F - t) * 0.66F;
      int rgb = MathHelper.hsvToRgb(hue, 0.9F, 1.0F) & 0xFFFFFF;
      return (alpha & 0xFF) << 24 | rgb;
   }

   private static long binKey(int bx, int bz) {
      return (long) bx << 32 | (bz & 0xFFFFFFFFL);
   }

   @Override
   public String getString3() {
      int n = this.markers.size();
      return n > 0 ? "§7" + n : null;
   }

   private static java.util.Set<Block> buildSourceSet() {
      java.util.HashSet<Block> s = new java.util.HashSet<>();
      Block[] arr = {
         Blocks.TORCH, Blocks.WALL_TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH,
         Blocks.LANTERN, Blocks.SOUL_LANTERN, Blocks.GLOWSTONE, Blocks.JACK_O_LANTERN,
         Blocks.SEA_LANTERN, Blocks.BEACON, Blocks.REDSTONE_LAMP, Blocks.CAMPFIRE,
         Blocks.SOUL_CAMPFIRE, Blocks.SHROOMLIGHT, Blocks.END_ROD,
         Blocks.OCHRE_FROGLIGHT, Blocks.VERDANT_FROGLIGHT, Blocks.PEARLESCENT_FROGLIGHT
      };
      for (Block b : arr) {
         if (b != null && b != Blocks.AIR) {
            s.add(b);
         }
      }
      try {
         Block lit = Registries.BLOCK.get(Identifier.of("minecraft", "lit_jack_o_lantern"));
         if (lit != null && lit != Blocks.AIR) {
            s.add(lit);
         }
      } catch (Throwable ignored) {
      }
      return s;
   }

   private static final class Bin {
      int count;
      int hidden;
      long sumX;
      long sumZ;
      int maxLevel;
      int bestY;
   }

   private static final class BaseMarker {
      final double x;
      final double z;
      final int y;
      final int count;
      final int hidden;

      BaseMarker(double x, double z, int y, int count, int hidden) {
         this.x = x;
         this.z = z;
         this.y = y;
         this.count = count;
         this.hidden = hidden;
      }
   }
}
