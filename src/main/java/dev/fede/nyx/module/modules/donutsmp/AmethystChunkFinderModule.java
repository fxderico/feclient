package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

/**
 * AmethystChunkFinder — geode-density + buried-chest base finder (ported from
 * WaterClient Dev's render/ChunkFinder).
 *
 * Two signals: chunks whose amethyst (cluster/block) count in the Y<=32 band
 * clears a threshold (a geode someone has hollowed / built around), and chunks
 * with a clump of chests below Y0 (a stash tucked under the world). Amethyst
 * chunks get a translucent pane at Y47; chest chunks pulse white on top.
 *
 * Improvement over the Water original: the original drew randomised decoy
 * "shape forms" that deliberately painted the WRONG chunks to camouflage the
 * find — actively unhelpful. This renders the real hit chunks, through walls,
 * with the count exposed in the HUD.
 */
public final class AmethystChunkFinderModule extends Module {
   private static final long FLASH_ON_MS = 150L;
   private static final long FLASH_CYCLE_MS = 400L;

   private final NumberSetting scanRadius = new NumberSetting("ChunkRadius", 5.0, 1.0, 12.0, 1.0);
   private final NumberSetting threshold = new NumberSetting("MinAmethyst", 10.0, 1.0, 200.0, 1.0);
   private final NumberSetting renderY = new NumberSetting("RenderY", 47.0, -64.0, 200.0, 1.0);
   private final ColorSetting fillColor = new ColorSetting("FillColor", -1023410380); // reddish
   private final NumberSetting alpha = new NumberSetting("FillAlpha", 60.0, 0.0, 255.0, 1.0);
   private final NumberSetting scanTicks = new NumberSetting("ScanTicks", 5.0, 2.0, 40.0, 1.0);
   private final BooleanSetting baseFlash = new BooleanSetting("ChestFlash", true);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final BooleanSetting countHUD = new BooleanSetting("CountHUD", true);

   private volatile Set<ChunkPos> amethystHits = ConcurrentHashMap.newKeySet();
   private volatile Set<ChunkPos> baseHits = ConcurrentHashMap.newKeySet();
   private ExecutorService scanExec;
   private final AtomicBoolean scanning = new AtomicBoolean(false);
   private long flashAnchorMs;
   private int tick;

   public AmethystChunkFinderModule() {
      super("AmethystChunkFinder", "Geode + buried-chest base finder (through walls)", Category.DONUTSMP);
      this.run6(new Setting[]{
         this.scanRadius, this.threshold, this.renderY, this.fillColor, this.alpha,
         this.scanTicks, this.baseFlash, this.throughWalls, this.countHUD
      });
   }

   @Override
   public void run() {
      reset();
   }

   @Override
   public void run2() {
      reset();
      if (this.scanExec != null) {
         this.scanExec.shutdownNow();
      }
   }

   private void reset() {
      this.amethystHits = ConcurrentHashMap.newKeySet();
      this.baseHits = ConcurrentHashMap.newKeySet();
      this.scanning.set(false);
      this.flashAnchorMs = 0L;
      this.tick = 0;
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.world == null) {
         return;
      }
      int every = Math.max(2, (int) Math.round(this.scanTicks.getValue()));
      if (this.tick++ % every != 0) {
         return;
      }
      if (!this.scanning.compareAndSet(false, true)) {
         return;
      }

      if (this.scanExec == null || this.scanExec.isShutdown()) {
         this.scanExec = Executors.newSingleThreadExecutor(t -> {
            Thread th = new Thread(t, "amethystfinder-scan");
            th.setDaemon(true);
            return th;
         });
      }

      ChunkPos center = class310.player.getChunkPos();
      int r = this.scanRadius.getValueInt();
      int need = (int) Math.round(this.threshold.getValue());
      List<ChunkPos> pos = new ArrayList<>();
      List<WorldChunk> chunks = new ArrayList<>();
      for (int dx = -r; dx <= r; dx++) {
         for (int dz = -r; dz <= r; dz++) {
            ChunkPos cp = new ChunkPos(center.x + dx, center.z + dz);
            WorldChunk wc = class310.world.getChunkManager().getWorldChunk(cp.x, cp.z, false);
            if (wc != null && !wc.isEmpty()) {
               pos.add(cp);
               chunks.add(wc);
            }
         }
      }

      this.scanExec.submit(() -> {
         try {
            Set<ChunkPos> amethyst = ConcurrentHashMap.newKeySet();
            Set<ChunkPos> bases = ConcurrentHashMap.newKeySet();
            for (int i = 0; i < pos.size(); i++) {
               WorldChunk wc = chunks.get(i);
               if (countAmethyst(wc, need) >= need) {
                  amethyst.add(pos.get(i));
               }
               if (hasChestsBelowZero(wc)) {
                  bases.add(pos.get(i));
               }
            }
            this.amethystHits = amethyst;
            this.baseHits = bases;
         } catch (Throwable ignored) {
         } finally {
            this.scanning.set(false);
         }
      });
   }

   private static int countAmethyst(WorldChunk chunk, int stopAt) {
      int count = 0;
      ChunkSection[] sections = chunk.getSectionArray();
      int minY = chunk.getBottomY();
      for (int si = 0; si < sections.length; si++) {
         if (minY + si * 16 > 32) {
            break;
         }
         ChunkSection s = sections[si];
         if (s != null && !s.isEmpty() && s.hasAny(AmethystChunkFinderModule::isAmethyst)) {
            for (int x = 0; x < 16; x++) {
               for (int y = 0; y < 16; y++) {
                  for (int z = 0; z < 16; z++) {
                     if (isAmethyst(s.getBlockState(x, y, z)) && ++count >= stopAt) {
                        return count;
                     }
                  }
               }
            }
         }
      }
      return count;
   }

   private static boolean isAmethyst(BlockState state) {
      return state.isOf(Blocks.AMETHYST_CLUSTER) || state.isOf(Blocks.AMETHYST_BLOCK) || state.isOf(Blocks.BUDDING_AMETHYST);
   }

   private static boolean hasChestsBelowZero(WorldChunk chunk) {
      int chestCount = 0;
      ChunkSection[] sections = chunk.getSectionArray();
      int minY = chunk.getBottomY();
      for (int si = 0; si < sections.length; si++) {
         int base = minY + si * 16;
         if (base >= 0) {
            break;
         }
         ChunkSection s = sections[si];
         if (s != null && !s.isEmpty() && s.hasAny(b -> b.isOf(Blocks.CHEST) || b.isOf(Blocks.TRAPPED_CHEST))) {
            int lyMax = Math.min(15, -base - 1);
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = 0; y <= lyMax; y++) {
                     BlockState bs = s.getBlockState(x, y, z);
                     if ((bs.isOf(Blocks.CHEST) || bs.isOf(Blocks.TRAPPED_CHEST)) && ++chestCount >= 10) {
                        return true;
                     }
                  }
               }
            }
         }
      }
      return false;
   }

   @Override
   public void run4(DrawContext ctx, float tickDelta) {
      Set<ChunkPos> amethyst = this.amethystHits;
      Set<ChunkPos> bases = this.baseHits;
      if (class310.player == null || class310.world == null) {
         return;
      }
      if (amethyst.isEmpty() && bases.isEmpty()) {
         if (this.countHUD.getValue()) {
            renderHud(ctx);
         }
         return;
      }

      boolean tw = this.throughWalls.getValue();
      double y = this.renderY.getValue();
      int baseColor = this.fillColor.getValue() & 0xFFFFFF;
      int a = (int) Math.round(this.alpha.getValue());
      int fill = (a << 24) | baseColor;

      for (ChunkPos c : amethyst) {
         double x1 = c.x << 4;
         double z1 = c.z << 4;
         ListUtils.run14(x1, y, z1, x1 + 16.0, z1 + 16.0, fill, tw);
      }

      if (this.baseFlash.getValue() && !bases.isEmpty()) {
         long now = System.currentTimeMillis();
         if (this.flashAnchorMs == 0L) {
            this.flashAnchorMs = now;
         }
         long inCycle = (now - this.flashAnchorMs) % FLASH_CYCLE_MS;
         if (inCycle < FLASH_ON_MS) {
            float t = (float) inCycle / FLASH_ON_MS;
            int flashA = (int) ((1.0F - t) * 200.0F);
            int flash = (flashA << 24) | 0xFFFFFF;
            for (ChunkPos c : bases) {
               double x1 = c.x << 4;
               double z1 = c.z << 4;
               ListUtils.run14(x1, y + 0.3, z1, x1 + 16.0, z1 + 16.0, flash, tw);
            }
         }
      } else {
         this.flashAnchorMs = 0L;
      }

      if (this.countHUD.getValue()) {
         renderHud(ctx);
      }
   }

   private void renderHud(DrawContext ctx) {
      if (class310.textRenderer == null) {
         return;
      }
      String s = "Geodes " + this.amethystHits.size() + (this.baseHits.isEmpty() ? "" : "  Stash " + this.baseHits.size());
      int w = class310.textRenderer.getWidth(s) + 16;
      ctx.fill(6, 166, 6 + w, 185, -535817448);
      ctx.fill(6, 166, 9, 185, -4523);
      ctx.drawText(class310.textRenderer, s, 16, 171, -4523, false);
   }

   @Override
   public String getString3() {
      int n = this.amethystHits.size() + this.baseHits.size();
      return n > 0 ? "§7" + n : null;
   }
}
