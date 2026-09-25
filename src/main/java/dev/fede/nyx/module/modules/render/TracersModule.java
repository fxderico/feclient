package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.module.modules.movement.FreecamModule;
import dev.fede.nyx.module.modules.movement.FreelookModule;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.CodeEngineScreenUtil2;
import dev.fede.nyx.util.CornerBoxESPModuleUtil;
import dev.fede.nyx.util.Matrix4fUtils;
import java.util.Iterator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TracersModule extends Module {
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting mobs = new BooleanSetting("Mobs", false);
   private final BooleanSetting hostile = new BooleanSetting("Hostile", true);
   private final BooleanSetting passive = new BooleanSetting("Passive", false);
   private final BooleanSetting self = new BooleanSetting("Self", false);
   private final ColorSetting friendColor = new ColorSetting("FriendColor", -12976364);
   private final ColorSetting enemyColor = new ColorSetting("EnemyColor", -53200);
   private final ColorSetting mobColor = new ColorSetting("MobColor", -24576);
   private final ColorSetting anchorColor = new ColorSetting("AnchorColor", -1);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.5, 0.5, 5.0, 0.1);
   private final NumberSetting alpha = new NumberSetting("Alpha", 200.0, 0.0, 255.0, 1.0);
   private final ModeSetting anchor = new ModeSetting("Anchor", "BottomScreen", "BottomScreen", "Crosshair", "CameraOrigin");
   private final ModeSetting gradientMode = new ModeSetting("GradientMode", "Solid", "Solid", "StartToTargetColor", "Chroma");
   private final NumberSetting segments = new NumberSetting("Segments", 1.0, 1.0, 20.0, 1.0);
   private final NumberSetting chromaSpeed = new NumberSetting("ChromaSpeed", 2.0, 0.2, 10.0, 0.1);
   private final BooleanSetting distanceFade = new BooleanSetting("DistanceFade", false);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 96.0, 8.0, 512.0, 1.0);

   public TracersModule() {
      super("Tracers", "Screen-space lines from a HUD anchor to each target entity", Category.RENDER);
      this.run6(
         new Setting[]{
            this.players,
            this.mobs,
            this.hostile,
            this.passive,
            this.self,
            this.friendColor,
            this.enemyColor,
            this.mobColor,
            this.anchorColor,
            this.lineWidth,
            this.alpha,
            this.anchor,
            this.gradientMode,
            this.segments,
            this.chromaSpeed,
            this.distanceFade,
            this.maxDistance
         }
      );
      this.hostile.visibleWhen(this::getBoolean6);
      this.passive.visibleWhen(this::getBoolean5);
      this.anchorColor.visibleWhen(this::getBoolean4);
      this.segments.visibleWhen(this::getBoolean3);
      this.chromaSpeed.visibleWhen(this::getBoolean2);
      this.maxDistance.visibleWhen(this::getBoolean);
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         int var3 = intOf5((int)Math.round(this.alpha.getValue()));
         if (var3 != 0) {
            double var15;
            double var17;
            label195: {
               float var4;
               String var5;
               int var6;
               boolean var7;
               double var8;
               Vec3d var10;
               long var11;
               boolean var37;
               byte var40;
               Iterator var43;
               label202: {
                  var4 = this.lineWidth.getValueFloat();
                  var5 = this.gradientMode.getMode();
                  var6 = Math.max(1, this.segments.getValueInt());
                  var7 = this.distanceFade.getValue();
                  var8 = this.maxDistance.getValue();
                  var10 = class310.player.getEntityPos();
                  var11 = System.currentTimeMillis();
                  int var13 = class310.getWindow().getScaledWidth();
                  int var14 = class310.getWindow().getScaledHeight();
                  String var19 = this.anchor.getMode();
                  var40 = -1;
                  switch (var19.hashCode()) {
                     case -893036542:
                        if (var19.equals("Crosshair")) {
                           boolean var42 = false;
                           var15 = var13 * 0.5;
                           var17 = var14 * 0.5;
                           if ("StartToTargetColor".equals(var5) && var6 > 1) {
                              var37 = true;
                              var40 = (byte)(this.self.getValue() ? 1 : 0);
                              var43 = class310.world.getEntities().iterator();
                              if (!var43.hasNext()) {
                                 break label195;
                              }
                           } else {
                              var37 = false;
                              var40 = (byte)(this.self.getValue() ? 1 : 0);
                              var43 = class310.world.getEntities().iterator();
                              if (!var43.hasNext()) {
                                 break label195;
                              }
                           }
                           break label202;
                        }
                        break;
                     case -885783861:
                        if (var19.equals("CameraOrigin")) {
                           label179: {
                              label178: {
                                 float var24;
                                 float var25;
                                 net.minecraft.util.math.Vec3d var21 = null;
                                 label177: {
                                    label176: {
                                       boolean var39 = true;
                                       var21 = MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
                                       Entity var22 = class310.getCameraEntity();
                                       Object var23 = null;
                                       if (FreecamModule.bool) {
                                          var24 = FreecamModule.getFloat();
                                          var25 = FreecamModule.getFloat2();
                                       } else if (FreelookModule.bool) {
                                          var24 = FreelookModule.getFloat();
                                          var25 = FreelookModule.getFloat2();
                                       } else {
                                          if (var22 == null) {
                                             var24 = 0.0F;
                                             var25 = 0.0F;
                                             if (var22 != null) {
                                                break label177;
                                             }
                                             break label176;
                                          }

                                          var24 = var22.getYaw();
                                          var25 = var22.getPitch();
                                       }

                                       if (var22 != null) {
                                          break label177;
                                       }
                                    }

                                    if (!FreecamModule.bool && !FreelookModule.bool) {
                                       break label178;
                                    }
                                 }

                                 Vec3d var26 = Vec3d.fromPolar(var25, var24);
                                 double[] var47 = Matrix4fUtils.doubleArrayOf(var21.add(var26));
                                 if (var47 != null) {
                                    var15 = var47[0];
                                    var17 = var47[1];
                                    break label179;
                                 }
                              }

                              var15 = var13 * 0.5;
                              var17 = var14 * 0.5;
                           }

                           if ("StartToTargetColor".equals(var5) && var6 > 1) {
                              var37 = true;
                              boolean var41 = this.self.getValue();
                              var43 = class310.world.getEntities().iterator();
                              if (var43.hasNext()) {
                                 do {
                                    Entity var45 = (Entity)var43.next();
                                    if (var45 instanceof LivingEntity var48
                                       && var48.isAlive()
                                       && (var41 || var48 != class310.player && var48 != class310.getCameraEntity())
                                       && this.check3(var48)) {
                                       int var50 = this.intOf4(var48);
                                       int var52 = this.intOf(var50, var5, var3, var11);
                                       Vec3d var32;
                                       if (var7) {
                                          double var54 = var48.squaredDistanceTo(var10);
                                          var52 = CornerBoxESPModuleUtil.intOf2(var52, var54, var8, 0.15F);
                                          var54 = MathHelper.lerp(var2, var48.lastRenderX, var48.getX());
                                          double var28 = MathHelper.lerp(var2, var48.lastRenderY, var48.getY()) + var48.getHeight() * 0.5;
                                          double var30 = MathHelper.lerp(var2, var48.lastRenderZ, var48.getZ());
                                          var32 = new Vec3d(var54, var28, var30);
                                       } else {
                                          double var56 = MathHelper.lerp(var2, var48.lastRenderX, var48.getX());
                                          double var60 = MathHelper.lerp(var2, var48.lastRenderY, var48.getY()) + var48.getHeight() * 0.5;
                                          double var63 = MathHelper.lerp(var2, var48.lastRenderZ, var48.getZ());
                                          var32 = new Vec3d(var56, var60, var63);
                                       }

                                       double[] var33 = Matrix4fUtils.doubleArrayOf(var32);
                                       if (var33 != null) {
                                          int var34 = CodeEngineScreenUtil2.intOf3(this.anchorColor.getValue(), var3);
                                          if (var7) {
                                             double var35 = var48.squaredDistanceTo(var10);
                                             var34 = CornerBoxESPModuleUtil.intOf2(var34, var35, var8, 0.15F);
                                             run(var1, var15, var17, var33[0], var33[1], var34, var52, var6, var4);
                                          } else {
                                             run(var1, var15, var17, var33[0], var33[1], var34, var52, var6, var4);
                                          }
                                       }
                                    }
                                 } while (var43.hasNext());
                              }

                              ListUtils.run26(var1, var15, var17);
                              return;
                           }

                           var37 = false;
                           var40 = (byte)(this.self.getValue() ? 1 : 0);
                           var43 = class310.world.getEntities().iterator();
                           if (!var43.hasNext()) {
                              break label195;
                           }
                           break label202;
                        }
                  }

                  var15 = var13 * 0.5;
                  var17 = var14;
                  if ("StartToTargetColor".equals(var5) && var6 > 1) {
                     var37 = true;
                     var40 = (byte)(this.self.getValue() ? 1 : 0);
                     var43 = class310.world.getEntities().iterator();
                     if (!var43.hasNext()) {
                        break label195;
                     }
                  } else {
                     var37 = false;
                     var40 = (byte)(this.self.getValue() ? 1 : 0);
                     var43 = class310.world.getEntities().iterator();
                     if (!var43.hasNext()) {
                        break label195;
                     }
                  }
               }

               do {
                  Entity var46 = (Entity)var43.next();
                  if (var46 instanceof LivingEntity var49
                     && var49.isAlive()
                     && ((var40 != 0) || var49 != class310.player && var49 != class310.getCameraEntity())
                     && this.check3(var49)) {
                     int var53;
                     Vec3d var66;
                     label155: {
                        int var51 = this.intOf4(var49);
                        var53 = this.intOf(var51, var5, var3, var11);
                        if (var7) {
                           double var57 = var49.squaredDistanceTo(var10);
                           var53 = CornerBoxESPModuleUtil.intOf2(var53, var57, var8, 0.15F);
                           var57 = MathHelper.lerp(var2, var49.lastRenderX, var49.getX());
                           double var61 = MathHelper.lerp(var2, var49.lastRenderY, var49.getY()) + var49.getHeight() * 0.5;
                           double var64 = MathHelper.lerp(var2, var49.lastRenderZ, var49.getZ());
                           var66 = new Vec3d(var57, var61, var64);
                           if (!var37) {
                              break label155;
                           }
                        } else {
                           double var59 = MathHelper.lerp(var2, var49.lastRenderX, var49.getX());
                           double var62 = MathHelper.lerp(var2, var49.lastRenderY, var49.getY()) + var49.getHeight() * 0.5;
                           double var65 = MathHelper.lerp(var2, var49.lastRenderZ, var49.getZ());
                           var66 = new Vec3d(var59, var62, var65);
                           if (!var37) {
                              break label155;
                           }
                        }

                        double[] var67 = Matrix4fUtils.doubleArrayOf(var66);
                        if (var67 != null) {
                           int var69 = CodeEngineScreenUtil2.intOf3(this.anchorColor.getValue(), var3);
                           if (var7) {
                              double var71 = var49.squaredDistanceTo(var10);
                              var69 = CornerBoxESPModuleUtil.intOf2(var69, var71, var8, 0.15F);
                              run(var1, var15, var17, var67[0], var67[1], var69, var53, var6, var4);
                           } else {
                              run(var1, var15, var17, var67[0], var67[1], var69, var53, var6, var4);
                           }
                        }
                        continue;
                     }

                     ListUtils.run25(var66, var53, var4);
                  }
               } while (var43.hasNext());
            }

            ListUtils.run26(var1, var15, var17);
         }
      }
   }

   private int intOf(int var1, String var2, int var3, long var4) {
      switch (var2.hashCode()) {
         case -1688189515:
            if (var2.equals("StartToTargetColor")) {
               return CodeEngineScreenUtil2.intOf3(var1, var3);
            }
            break;
         case 80066187:
            if (var2.equals("Solid")) {
               return CodeEngineScreenUtil2.intOf3(var1, var3);
            }
            break;
         case 2017705622:
            if (var2.equals("Chroma")) {
               double var8 = Math.max(0.2, this.chromaSpeed.getValue());
               int var10 = (int)(var4 * var8 % 4000.0);
               int var11 = CodeEngineScreenUtil2.intOf4(var10, 1.0F, 1.0F);
               return CodeEngineScreenUtil2.intOf3(var11, var3);
            }
      }

      return CodeEngineScreenUtil2.intOf3(var1, var3);
   }

   private static void run(DrawContext var0, double var1, double var3, double var5, double var7, int var9, int var10, int var11, float var12) {
      // Clip the target endpoint to the screen along the anchor->target direction.
      // Off-screen / beside-you targets project to extreme coordinates; without
      // this the line rasterizer draws all the way out to that garbage point
      // (the stray lines-to-the-horizon). Clipping keeps the line on-screen and
      // still pointing at the target, and preserves the exact direction (unlike a
      // plain rectangular clamp of the point).
      int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
      int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
      if (var5 < 0.0 || var5 > sw || var7 < 0.0 || var7 > sh) {
         double dx = var5 - var1;
         double dy = var7 - var3;
         double s = 1.0;
         if (dx < 0.0) {
            s = Math.min(s, (0.0 - var1) / dx);
         } else if (dx > 0.0) {
            s = Math.min(s, (sw - var1) / dx);
         }
         if (dy < 0.0) {
            s = Math.min(s, (0.0 - var3) / dy);
         } else if (dy > 0.0) {
            s = Math.min(s, (sh - var3) / dy);
         }
         s = Math.max(0.0, Math.min(1.0, s));
         var5 = var1 + dx * s;
         var7 = var3 + dy * s;
      }

      double var13 = var5 - var1;
      double var15 = var7 - var3;
      double var17 = Math.hypot(var13, var15);
      if (!(var17 < 1.0)) {
         double var19 = var13 / var11;
         double var21 = var15 / var11;

         for (int var23 = 0; var23 < var11; var23++) {
            double var24 = (var23 + 0.5) / var11;
            int var26 = intOf2(var9, var10, (float)var24);
            double var27 = var1 + var19 * var23;
            double var29 = var3 + var21 * var23;
            double var31 = var1 + var19 * (var23 + 1);
            double var33 = var3 + var21 * (var23 + 1);
            run2(var0, var27, var29, var31, var33, var26, var12);
         }
      }
   }

   private static void run2(DrawContext var0, double var1, double var3, double var5, double var7, int var9, float var10) {
      double var11 = var5 - var1;
      double var13 = var7 - var3;
      double var15 = Math.max(Math.abs(var11), Math.abs(var13));
      if (!(var15 < 1.0)) {
         double var17 = var11 / var15;
         double var19 = var13 / var15;
         int var21 = Math.max(1, (int)Math.ceil(var10 * 0.5));
         int var22 = (int)var15;

         for (int var23 = 0; var23 <= var22; var23++) {
            int var24 = (int)Math.round(var1 + var17 * var23);
            int var25 = (int)Math.round(var3 + var19 * var23);
            var0.fill(var24 - var21, var25 - var21, var24 + var21, var25 + var21, var9);
         }
      }
   }

   private static int intOf2(int var0, int var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(1.0F, var2));
      int var4 = intOf5(Math.round(intOf3(var0, 24) + (intOf3(var1, 24) - intOf3(var0, 24)) * var3));
      int var5 = intOf5(Math.round(intOf3(var0, 16) + (intOf3(var1, 16) - intOf3(var0, 16)) * var3));
      int var6 = intOf5(Math.round(intOf3(var0, 8) + (intOf3(var1, 8) - intOf3(var0, 8)) * var3));
      int var7 = intOf5(Math.round(intOf3(var0, 0) + (intOf3(var1, 0) - intOf3(var0, 0)) * var3));
      return var4 << 24 | var5 << 16 | var6 << 8 | var7;
   }

   private static int intOf3(int var0, int var1) {
      return var0 >>> var1 & 0xFF;
   }

   private boolean check3(LivingEntity var1) {
      if (var1 instanceof PlayerEntity) {
         return this.players.getValue();
      } else if (!this.mobs.getValue()) {
         return false;
      } else if (var1 instanceof Monster) {
         return this.hostile.getValue();
      } else {
         return var1 instanceof PassiveEntity ? this.passive.getValue() : this.passive.getValue();
      }
   }

   private int intOf4(LivingEntity var1) {
      if (var1 instanceof PlayerEntity var2) {
         return class310.player != null && class310.player.isTeammate(var2) ? this.friendColor.getValue() : this.enemyColor.getValue();
      } else {
         return this.mobColor.getValue();
      }
   }

   private static int intOf5(int var0) {
      if (var0 < 0) {
         return 0;
      } else {
         return var0 > 255 ? 255 : var0;
      }
   }

   private Boolean getBoolean() {
      return this.distanceFade.getValue();
   }

   private Boolean getBoolean2() {
      return "Chroma".equals(this.gradientMode.getMode());
   }

   private Boolean getBoolean3() {
      return "StartToTargetColor".equals(this.gradientMode.getMode());
   }

   private Boolean getBoolean4() {
      return "StartToTargetColor".equals(this.gradientMode.getMode());
   }

   private Boolean getBoolean5() {
      return this.mobs.getValue();
   }

   private Boolean getBoolean6() {
      return this.mobs.getValue();
   }
}

