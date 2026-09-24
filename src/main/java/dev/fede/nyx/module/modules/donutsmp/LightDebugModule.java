package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

/**
 * LightDebug — base finding via raw server block-light.
 *
 * Unlike LightFinder (which highlights light-source BLOCKS you can see), this
 * reads the stored BLOCK light level that the server streams for every chunk
 * (World.getLightLevel(LightType.BLOCK, pos)). Torches inside a walled-off or
 * buried base still push block light into the light engine, so lit pockets show
 * up as a glowing heatmap even when the source blocks are hidden behind walls.
 * Fly over terrain and clusters of colour = someone's base.
 *
 * Ported in spirit from Krypton's "light debug" (its own impl lives in a native
 * blob and can't be decompiled). Uses the public light API, no mixins needed.
 */
public class LightDebugModule extends Module {
   private final NumberSetting radius = new NumberSetting("Radius", 32.0, 8.0, 96.0, 1.0);
   private final NumberSetting minLight = new NumberSetting("MinLight", 4.0, 1.0, 15.0, 1.0);
   private final NumberSetting alpha = new NumberSetting("Alpha", 0.55, 0.1, 1.0, 0.05);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.0, 0.5, 4.0, 0.1);
   // one marker per (x,z) column (the brightest block) keeps the render sane and
   // reads like a top-down heatmap; off = box every lit block (heavier).
   private final BooleanSetting perColumn = new BooleanSetting("PerColumn", true);
   private final BooleanSetting countHUD = new BooleanSetting("CountHUD", true);
   // packed BlockPos -> block-light level (1..15), so render colours without a re-read
   private final Map<Long, Integer> hits = new HashMap<>();
   private int tick;

   public LightDebugModule() {
      super("LightDebug", "Server block-light heatmap (through walls) — base recon", Category.DONUTSMP);
      this.run6(new Setting[]{this.radius, this.minLight, this.alpha, this.lineWidth, this.perColumn, this.countHUD});
   }

   // onEnable
   @Override
   public void run() {
      this.hits.clear();
      this.tick = 0;
   }

   // onDisable
   @Override
   public void run2() {
      this.hits.clear();
      this.tick = 0;
   }

   // onTick — throttled block-light sweep (the cube scan is the only cost).
   @Override
   public void run3() {
      if (class310.player == null || class310.world == null) {
         return;
      }
      if (this.tick++ % 8 != 0) {
         return;
      }
      try {
         this.scanNow();
      } catch (Throwable t) {
         this.hits.clear();
      }
   }

   @Override
   public void run4(DrawContext ctx, float tickDelta) {
      if (class310.player == null || class310.world == null || this.hits.isEmpty()) {
         if (this.countHUD.getValue()) {
            this.renderHud(ctx);
         }
         return;
      }

      int a = (int)Math.round(MathHelper.clamp(this.alpha.getValue(), 0.0, 1.0) * 255.0);
      float w = this.lineWidth.getValueFloat();

      for (Map.Entry<Long, Integer> e : this.hits.entrySet()) {
         BlockPos pos = BlockPos.fromLong(e.getKey());
         int color = colorFor(e.getValue(), a);
         Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
         ListUtils.run5(box, color, w, true);
      }

      if (this.countHUD.getValue()) {
         this.renderHud(ctx);
      }
   }

   private void scanNow() {
      this.hits.clear();
      int r = this.radius.getValueInt();
      int threshold = (int)Math.round(this.minLight.getValue());
      boolean columnMode = this.perColumn.getValue();

      int px = (int)Math.floor(class310.player.getX());
      int py = (int)Math.floor(class310.player.getY());
      int pz = (int)Math.floor(class310.player.getZ());

      int bottom = class310.world.getBottomY();
      int top = bottom + class310.world.getHeight() - 1;
      int yLo = Math.max(bottom, py - r);
      int yHi = Math.min(top, py + r);

      Mutable m = new Mutable();
      // reused per-column best when columnMode is on
      for (int dx = -r; dx <= r; dx++) {
         int x = px + dx;
         for (int dz = -r; dz <= r; dz++) {
            int z = pz + dz;

            int bestY = Integer.MIN_VALUE;
            int bestLevel = 0;

            for (int y = yLo; y <= yHi; y++) {
               m.set(x, y, z);
               int level;
               try {
                  level = class310.world.getLightLevel(LightType.BLOCK, m);
               } catch (Throwable t) {
                  continue;
               }

               if (level >= threshold) {
                  if (columnMode) {
                     if (level > bestLevel) {
                        bestLevel = level;
                        bestY = y;
                     }
                  } else {
                     this.hits.put(BlockPos.asLong(x, y, z), level);
                  }
               }
            }

            if (columnMode && bestY != Integer.MIN_VALUE) {
               this.hits.put(BlockPos.asLong(x, bestY, z), bestLevel);
            }
         }
      }
   }

   // level 1..15 -> blue (cold/dim) through red (hot/bright)
   private static int colorFor(int level, int alpha) {
      float t = MathHelper.clamp((level - 1) / 14.0F, 0.0F, 1.0F);
      float hue = (1.0F - t) * 0.66F; // 0.66 = blue, 0.0 = red
      int rgb = MathHelper.hsvToRgb(hue, 0.9F, 1.0F) & 0xFFFFFF;
      return (alpha & 0xFF) << 24 | rgb;
   }

   private void renderHud(DrawContext ctx) {
      if (class310.textRenderer != null) {
         String s = "Lit  " + this.hits.size();
         int w = class310.textRenderer.getWidth(s) + 16;
         ctx.fill(6, 122, 6 + w, 141, -535817448);
         ctx.fill(6, 122, 9, 141, -4523);
         ctx.drawText(class310.textRenderer, s, 16, 127, -4523, false);
      }
   }

   @Override
   public String getString3() {
      int n = this.hits.size();
      return n > 0 ? "§7" + n : null;
   }
}
