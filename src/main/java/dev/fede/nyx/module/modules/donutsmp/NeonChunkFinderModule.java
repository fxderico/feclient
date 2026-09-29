package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

/**
 * NeonChunkFinder — block-entity + dungeon recon (ported from WaterClient Dev).
 *
 * Deep-scans loaded chunks for the block entities a base is built around
 * (spawners, chests, trapped chests, barrels, shulkers, ender chests) plus the
 * two natural-dungeon block signatures (mossy+cobble, cobweb+mossy). Every hit
 * is cached with a type colour and drawn as a through-walls outline box with an
 * optional full-height beam and a camera tracer so you can beeline to it.
 *
 * Improvement over the Water original: per-type toggles, a real Y-max setting
 * (was hardcoded), a max render distance, and the through-walls ListUtils
 * renderer instead of the old GL immediate lines. The packet-leak sub-signal
 * (seeing OTHER players mine) is intentionally left out here — it needs a packet
 * hook the nyx module base doesn't expose; it belongs in a small mixin, added
 * separately rather than bolted on fragile.
 */
public final class NeonChunkFinderModule extends Module {
   private final NumberSetting radius = new NumberSetting("ChunkRadius", 6.0, 1.0, 12.0, 1.0);
   private final NumberSetting yMax = new NumberSetting("YMax", 50.0, -64.0, 320.0, 1.0);
   private final NumberSetting maxDist = new NumberSetting("MaxDistance", 256.0, 32.0, 512.0, 1.0);
   private final NumberSetting scanTicks = new NumberSetting("ScanTicks", 5.0, 2.0, 40.0, 1.0);
   private final BooleanSetting detectSpawner = new BooleanSetting("Spawner", true);
   private final BooleanSetting detectChest = new BooleanSetting("Chest", true);
   private final BooleanSetting detectBarrel = new BooleanSetting("Barrel", true);
   private final BooleanSetting detectShulker = new BooleanSetting("Shulker", true);
   private final BooleanSetting detectEnder = new BooleanSetting("EnderChest", true);
   private final BooleanSetting detectDungeon = new BooleanSetting("Dungeon", true);
   private final BooleanSetting beam = new BooleanSetting("Beam", true);
   private final BooleanSetting tracers = new BooleanSetting("Tracers", true);
   private final NumberSetting tracerWidth = new NumberSetting("TracerWidth", 1.0, 0.1, 3.0, 0.1);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.5, 0.5, 4.0, 0.1);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final BooleanSetting countHUD = new BooleanSetting("CountHUD", true);

   private final Map<Long, Integer> cache = new ConcurrentHashMap<>(); // packed pos -> argb
   private int tick;

   public NeonChunkFinderModule() {
      super("NeonChunkFinder", "Spawner/chest/dungeon recon (through walls)", Category.DONUTSMP);
      this.run6(new Setting[]{
         this.radius, this.yMax, this.maxDist, this.scanTicks,
         this.detectSpawner, this.detectChest, this.detectBarrel, this.detectShulker, this.detectEnder, this.detectDungeon,
         this.beam, this.tracers, this.tracerWidth, this.lineWidth, this.throughWalls, this.countHUD
      });
      this.tracerWidth.visibleWhen(this.tracers::getValue);
   }

   @Override
   public void run() {
      this.cache.clear();
      this.tick = 0;
   }

   @Override
   public void run2() {
      this.cache.clear();
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
      try {
         scanNow();
      } catch (Throwable t) {
         this.cache.clear();
      }
   }

   private void scanNow() {
      int pcx = class310.player.getChunkPos().x;
      int pcz = class310.player.getChunkPos().z;
      int r = this.radius.getValueInt();
      int yCap = (int) Math.round(this.yMax.getValue());

      // cull out-of-range cache entries first
      double maxSq = this.maxDist.getValue() * this.maxDist.getValue();
      double px = class310.player.getX();
      double pz = class310.player.getZ();
      this.cache.keySet().removeIf(k -> {
         BlockPos p = BlockPos.fromLong(k);
         double dx = p.getX() - px;
         double dz = p.getZ() - pz;
         return dx * dx + dz * dz > maxSq;
      });

      for (int cx = pcx - r; cx <= pcx + r; cx++) {
         for (int cz = pcz - r; cz <= pcz + r; cz++) {
            WorldChunk chunk = class310.world.getChunkManager().getWorldChunk(cx, cz, false);
            if (chunk != null && !chunk.isEmpty()) {
               scanChunk(chunk, cx, cz, yCap);
            }
         }
      }
   }

   private void scanChunk(WorldChunk chunk, int cx, int cz, int yCap) {
      for (Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
         BlockPos pos = entry.getKey();
         if (pos.getY() > yCap) {
            continue;
         }
         BlockEntity be = entry.getValue();
         if (this.detectSpawner.getValue() && be instanceof MobSpawnerBlockEntity) {
            add(pos, 0xFFFF1E1E);
         } else if (this.detectChest.getValue() && (be instanceof ChestBlockEntity || be instanceof TrappedChestBlockEntity)) {
            add(pos, 0xFFFFC81E);
         } else if (this.detectBarrel.getValue() && be instanceof BarrelBlockEntity) {
            add(pos, 0xFFC88232);
         } else if (this.detectShulker.getValue() && be instanceof ShulkerBoxBlockEntity) {
            add(pos, 0xFFC832DC);
         } else if (this.detectEnder.getValue() && be instanceof EnderChestBlockEntity) {
            add(pos, 0xFF8C50DC);
         }
      }

      if (this.detectDungeon.getValue()) {
         ChunkSection[] sections = chunk.getSectionArray();
         int minY = chunk.getBottomY();
         for (int si = 0; si < sections.length; si++) {
            int baseY = minY + si * 16;
            if (baseY > yCap) {
               break;
            }
            ChunkSection sec = sections[si];
            if (sec == null || sec.isEmpty()) {
               continue;
            }
            int mossy = 0;
            int cobble = 0;
            int web = 0;
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = 0; y < 16; y++) {
                     Block b = sec.getBlockState(x, y, z).getBlock();
                     if (b == Blocks.MOSSY_COBBLESTONE) {
                        mossy++;
                     } else if (b == Blocks.COBBLESTONE) {
                        cobble++;
                     } else if (b == Blocks.COBWEB) {
                        web++;
                     }
                  }
               }
            }
            if (mossy >= 3 && cobble >= 5) {
               add(new BlockPos(cx * 16 + 8, baseY + 8, cz * 16 + 8), 0xFFFF641E);
            }
            if (web >= 3 && mossy >= 2) {
               add(new BlockPos(cx * 16 + 8, baseY + 8, cz * 16 + 8), 0xFF963232);
            }
         }
      }
   }

   private void add(BlockPos pos, int argb) {
      this.cache.putIfAbsent(pos.toImmutable().asLong(), argb);
   }

   @Override
   public void run4(DrawContext ctx, float tickDelta) {
      if (class310.player == null || class310.world == null || this.cache.isEmpty()) {
         if (this.countHUD.getValue()) {
            renderHud(ctx);
         }
         return;
      }
      boolean tw = this.throughWalls.getValue();
      float w = this.lineWidth.getValueFloat();
      boolean drawBeam = this.beam.getValue();
      boolean drawTracer = this.tracers.getValue();
      float tracerW = this.tracerWidth.getValueFloat();
      Vec3d cam = class310.gameRenderer.getCamera().getCameraPos();
      int wb = class310.world.getBottomY();
      int wt = wb + class310.world.getHeight();

      for (Entry<Long, Integer> e : this.cache.entrySet()) {
         BlockPos p = BlockPos.fromLong(e.getKey());
         int argb = e.getValue();
         Box box = new Box(p.getX(), p.getY(), p.getZ(), p.getX() + 1.0, p.getY() + 1.0, p.getZ() + 1.0);
         ListUtils.run5(box, argb, w, tw);
         if (drawBeam) {
            ListUtils.run11(new Vec3d(p.getX() + 0.5, wb, p.getZ() + 0.5),
               new Vec3d(p.getX() + 0.5, wt, p.getZ() + 0.5), (0x3C << 24) | (argb & 0xFFFFFF), w);
         }
         if (drawTracer) {
            ListUtils.run11(new Vec3d(cam.x, cam.y - 0.2, cam.z),
               new Vec3d(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5), argb, tracerW);
         }
      }

      if (this.countHUD.getValue()) {
         renderHud(ctx);
      }
   }

   private void renderHud(DrawContext ctx) {
      if (class310.textRenderer == null) {
         return;
      }
      String s = "Neon  " + this.cache.size();
      int w = class310.textRenderer.getWidth(s) + 16;
      ctx.fill(6, 144, 6 + w, 163, -535817448);
      ctx.fill(6, 144, 9, 163, -4523);
      ctx.drawText(class310.textRenderer, s, 16, 149, -4523, false);
   }

   @Override
   public String getString3() {
      int n = this.cache.size();
      return n > 0 ? "§7" + n : null;
   }
}
