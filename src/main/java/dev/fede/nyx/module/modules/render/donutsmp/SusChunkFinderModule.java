package dev.fede.nyx.module.modules.render.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.tracker.ChunkActivityScanner;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;

public class SusChunkFinderModule extends Module {
   private final NumberSetting simDistance = new NumberSetting("SimDistance", 100.0, 1.0, 255.0, 1.0);
   private final NumberSetting threshold = new NumberSetting("Threshold", 5.0, 1.0, 500.0, 1.0);
   private final NumberSetting yMin = new NumberSetting("YMin", 16.0, -64.0, 128.0, 1.0);
   private final NumberSetting yMax = new NumberSetting("YMax", 128.0, -64.0, 320.0, 1.0);
   private final NumberSetting alpha = new NumberSetting("Alpha", 100.0, 20.0, 255.0, 1.0);
   private final BooleanSetting smartAdjust = new BooleanSetting("SmartAdjust", false);
   private final BooleanSetting tracer = new BooleanSetting("Tracer", false);
   private final BooleanSetting sigKelp = new BooleanSetting("Kelp", true);
   private final BooleanSetting sigCaveVines = new BooleanSetting("CaveVines", true);
   private final BooleanSetting sigVines = new BooleanSetting("Vines", true);
   private final BooleanSetting sigAmethyst = new BooleanSetting("Amethyst", true);
   private final BooleanSetting sigBamboo = new BooleanSetting("Bamboo", true);
   private final BooleanSetting sigCocoa = new BooleanSetting("Cocoa", true);
   private final BooleanSetting sigBeeNest = new BooleanSetting("BeeNest", true);
   private final BooleanSetting sigRotatedDeepslate = new BooleanSetting("RotatedDeepslate", true);
   private final BooleanSetting sigSkullCandle = new BooleanSetting("SkullCandle", true);
   private final BooleanSetting sigWitherSkull = new BooleanSetting("WitherSkull", true);
   private final BooleanSetting sigEntities = new BooleanSetting("Entities", true);
   private final BooleanSetting showHits = new BooleanSetting("ShowHitBoxes", true);
   private final BooleanSetting showSlab = new BooleanSetting("ShowChunkSlab", true);
   private final NumberSetting slabY = new NumberSetting("SlabY", 63.0, -64.0, 320.0, 1.0);
   private final NumberSetting slabThick = new NumberSetting("SlabThickness", 0.5, 0.05, 4.0, 0.05);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final BooleanSetting outline = new BooleanSetting("Outline", true);
   private final NumberSetting outlineWidth = new NumberSetting("OutlineWidth", 1.5, 0.5, 4.0, 0.1);
   private final NumberSetting maxDraw = new NumberSetting("MaxDraw", 2000.0, 100.0, 5000.0, 100.0);
   private final BooleanSetting showTooltip = new BooleanSetting("HUDTooltip", true);
   private final BooleanSetting stickyMemory = new BooleanSetting("StickyMemory", true);

   public SusChunkFinderModule() {
      super("SusChunkFinder", "Krypton-style base/farm chunk finder", Category.DONUTSMP);
      this.run6(
         new Setting[]{
            this.simDistance,
            this.threshold,
            this.yMin,
            this.yMax,
            this.alpha,
            this.smartAdjust,
            this.tracer,
            this.sigKelp,
            this.sigCaveVines,
            this.sigVines,
            this.sigAmethyst,
            this.sigBamboo,
            this.sigCocoa,
            this.sigBeeNest,
            this.sigRotatedDeepslate,
            this.sigSkullCandle,
            this.sigWitherSkull,
            this.sigEntities,
            this.showHits,
            this.showSlab,
            this.slabY,
            this.slabThick,
            this.throughWalls,
            this.outline,
            this.outlineWidth,
            this.maxDraw,
            this.showTooltip,
            this.stickyMemory
         }
      );
   }

   @Override
   public void run() {
      
      ChunkActivityScanner.run5(true);
      ChunkActivityScanner.run9(class310);

      try {
         if (class310 != null && class310.world != null && class310.player != null) {
            int var1 = class310.player.getChunkPos().x;
            int var2 = class310.player.getChunkPos().z;
            int var3 = class310.options != null ? (Integer)class310.options.getViewDistance().getValue() : 8;

            for (int var4 = -var3; var4 <= var3; var4++) {
               for (int var5 = -var3; var5 <= var3; var5++) {
                  WorldChunk var6 = class310.world.getChunkManager().getWorldChunk(var1 + var4, var2 + var5, false);
                  if (var6 != null) {
                     ChunkActivityScanner.run12(var6);
                  }
               }
            }
         }
      } catch (Throwable var7) {
      }
   }

   @Override
   public void run2() {
      ChunkActivityScanner.run7(class310);
      ChunkActivityScanner.run5(false);
   }

   public static void run3_s() {
      
   }

   private void run4() {
      ChunkActivityScanner.run(this.simDistance.getValueInt(), this.threshold.getValueInt());
      ChunkActivityScanner.run2(this.yMin.getValueInt(), this.yMax.getValueInt());
      ChunkActivityScanner.run8(this.stickyMemory.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.KELP, this.sigKelp.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.CAVE_VINES, this.sigCaveVines.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.VINES, this.sigVines.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.AMETHYST, this.sigAmethyst.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.BAMBOO, this.sigBamboo.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.COCOA, this.sigCocoa.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.BEE_NEST, this.sigBeeNest.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.ROTATED_DEEPSLATE, this.sigRotatedDeepslate.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.SKULL_CANDLE, this.sigSkullCandle.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.WITHER_SKULL, this.sigWitherSkull.getValue());
      ChunkActivityScanner.run3(ChunkActivityScanner.Signal.ENTITIES, this.sigEntities.getValue());
   }

   public void run5(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         int var3 = Math.max(20, Math.min(255, this.alpha.getValueInt()));
         int var4 = var3 << 24 | 14686232;
         int var5 = var3 << 24 | 16726072;
         int var6 = Math.min(255, var3 + 60) << 24 | 16736352;
         int var7 = this.maxDraw.getValueInt();
         int var8 = 0;
         double var9 = this.slabY.getValue();
         double var11 = Math.max(0.05, this.slabThick.getValue());
         boolean var13 = this.outline.getValue();
         float var14 = this.outlineWidth.getValueFloat();
         boolean var15 = this.throughWalls.getValue();
         int var16 = class310.player.getChunkPos().x;
         int var17 = class310.player.getChunkPos().z;
         int var18 = this.simDistance.getValueInt();
         Map var19 = ChunkActivityScanner.getMap2();

         for (Entry var21 : ((java.util.Map<?,?>)var19).entrySet()) {
            if (var8 >= var7) {
               break;
            }

            long var22 = (Long)var21.getKey();
            ChunkActivityScanner.Inner1 var24 = (ChunkActivityScanner.Inner1)var21.getValue();
            int var25 = ChunkActivityScanner.intOf(var22);
            int var26 = ChunkActivityScanner.intOf2(var22);
            if (Math.max(Math.abs(var25 - var16), Math.abs(var26 - var17)) <= var18) {
               double var27 = var25 * 16.0;
               double var29 = var26 * 16.0;
               double var31 = var27 + 16.0;
               double var33 = var29 + 16.0;
               if (this.showSlab.getValue()) {
                  Box var35 = new Box(var27, var9, var29, var31, var9 + var11, var33);

                  try {
                     ListUtils.run3(var35, var5, var15);
                  } catch (Throwable var45) {
                  }

                  if (var13) {
                     try {
                        ListUtils.run5(var35, var6, var14, var15);
                     } catch (Throwable var44) {
                     }
                  }

                  var8++;
               }

               if (this.showHits.getValue() && var24.hits() != null) {
                  for (BlockPos var37 : var24.hits()) {
                     if (var8 >= var7) {
                        break;
                     }

                     Box var38 = new Box(var37.getX(), var37.getY(), var37.getZ(), var37.getX() + 1.0, var37.getY() + 1.0, var37.getZ() + 1.0);

                     try {
                        ListUtils.run3(var38, var4, var15);
                     } catch (Throwable var43) {
                     }

                     if (var13) {
                        try {
                           ListUtils.run5(var38, var6, var14, var15);
                        } catch (Throwable var42) {
                        }
                     }

                     var8++;
                  }
               }
            }
         }

         if (this.sigEntities.getValue()) {
            for (Entity var47 : ChunkActivityScanner.listOf(class310)) {
               if (var8 >= var7) {
                  break;
               }

               int var48 = var47.getBlockPos().getX() >> 4;
               int var23 = var47.getBlockPos().getZ() >> 4;
               if (Math.max(Math.abs(var48 - var16), Math.abs(var23 - var17)) <= var18) {
                  Box var49 = var47.getBoundingBox();

                  try {
                     ListUtils.run3(var49, var4, var15);
                  } catch (Throwable var41) {
                  }

                  if (var13) {
                     try {
                        ListUtils.run5(var49, var6, var14, var15);
                     } catch (Throwable var40) {
                     }
                  }

                  var8++;
               }
            }
         }

         if (this.showTooltip.getValue()) {
            this.run14(var1);
         }
      }
   }

   private void run14(DrawContext var1) {
      if (class310.player != null && class310.textRenderer != null) {
         int var2 = (int)Math.floor(class310.player.getX()) >> 4;
         int var3 = (int)Math.floor(class310.player.getZ()) >> 4;
         long var4 = ChunkActivityScanner.longOf(var2, var3);
         Map var6 = ChunkActivityScanner.getMap2();
         ChunkActivityScanner.Inner1 var7 = (ChunkActivityScanner.Inner1)var6.get(var4);
         if (var7 != null) {
            TextRenderer var8 = class310.textRenderer;
            Objects.requireNonNull(var8);
            var1.fill(6, 60, 206, 100, -535817448);
            var1.fill(6, 60, 9, 100, -49104);
            var1.drawText(var8, "SUSPICIOUS CHUNK", 16, 66, -49104, false);
            var1.drawText(var8, "hits " + var7.totalScore() + " (thr " + ChunkActivityScanner.getInt() + ")", 16, 77, -723464, false);
         }
      }
   }
}

