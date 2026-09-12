package dev.fede.nyx.module.modules.addons;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.CodeEngineScreenUtil2;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DragonWingsModule extends Module {
   private static final int intVal = 4;
   private static final double doubleVal = 1.6;
   private static final double doubleVal2 = 0.42;
   private static final double doubleVal3 = 20.0;
   private static final double doubleVal4 = 35.0;
   private static final double[] doubleArray = new double[]{10.0, 35.0, 65.0, 100.0};
   private static final double[] doubleArray2 = new double[]{30.0, 15.0, 0.0, -10.0};
   private static final double[] doubleArray3 = new double[]{0.72, 0.68, 0.6, 0.48};
   private final BooleanSetting selfOnly = new BooleanSetting("SelfOnly", true);
   private final NumberSetting size = new NumberSetting("Size", 1.0, 0.5, 3.0, 0.1);
   private final ModeSetting colorMode = new ModeSetting("Color", "Black", "Black", "White", "Purple", "Rainbow");
   private final BooleanSetting animation = new BooleanSetting("Animation", true);
   private final NumberSetting flapSpeed = new NumberSetting("FlapSpeed", 1.0, 0.2, 3.0, 0.1);
   private final NumberSetting alphaMembrane = new NumberSetting("AlphaMembrane", 0.75, 0.0, 1.0, 0.05);
   private final NumberSetting alphaBones = new NumberSetting("AlphaBones", 1.0, 0.0, 1.0, 0.05);
   private final BooleanSetting hideInFirstPerson = new BooleanSetting("HideInFirstPerson", true);

   public DragonWingsModule() {
      super("DragonWings", "Animated 3D dragon wings (bones + triangulated membrane) rendered from the shoulder blades", Category.ADDONS);
      this.run6(
         new Setting[]{this.selfOnly, this.size, this.colorMode, this.animation, this.flapSpeed, this.alphaMembrane, this.alphaBones, this.hideInFirstPerson}
      );
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         int var3 = (int)Math.round(this.alphaMembrane.getValue() * 255.0);
         int var4 = (int)Math.round(this.alphaBones.getValue() * 255.0);
         if (var3 > 0 || var4 > 0) {
            if (var3 > 255) {
               var3 = 255;
            }

            if (var4 > 255) {
               var4 = 255;
            }

            int var5 = this.getInt();
            int var6 = var3 > 0 ? CodeEngineScreenUtil2.intOf3(var5, var3) : 0;
            int var7 = var4 > 0 ? CodeEngineScreenUtil2.intOf3(var5, var4) : 0;
            double var8 = 1.6 * this.size.getValueFloat();
            double var10 = 0.0;
            if (this.animation.getValue()) {
               double var12 = this.flapSpeed.getValueFloat();
               double var14 = (Math.PI * 8.0 / 5.0) * var12;
               double var16 = Math.toRadians(25.0) * Math.sqrt(var12);
               double var18 = System.nanoTime() * 1.0E-9;
               var10 = var16 * Math.sin(var18 * var14);
            }

            boolean var20 = this.hideInFirstPerson.getValue()
               && class310.options.getPerspective().isFirstPerson()
               && class310.getCameraEntity() == class310.player;
            if (this.selfOnly.getValue()) {
               if (!var20) {
                  this.run(class310.player, var2, var8, var6, var7, var10);
               }
            } else {
               for (PlayerEntity var21 : class310.world.getPlayers()) {
                  if (var21 != class310.player || !var20) {
                     this.run(var21, var2, var8, var6, var7, var10);
                  }
               }
            }
         }
      }
   }

   private void run(PlayerEntity var1, float var2, double var3, int var5, int var6, double var7) {
      if (var1 != null && var1.isAlive() && !var1.isInvisible()) {
         double var9 = MathHelper.lerp(var2, var1.lastRenderX, var1.getX());
         double var11 = MathHelper.lerp(var2, var1.lastRenderY, var1.getY());
         double var13 = MathHelper.lerp(var2, var1.lastRenderZ, var1.getZ());
         float var15 = var1.lastBodyYaw + MathHelper.wrapDegrees(var1.bodyYaw - var1.lastBodyYaw) * var2;
         double var16 = Math.toRadians(var15);
         Vec3d var18 = new Vec3d(-Math.sin(var16), 0.0, Math.cos(var16));
         Vec3d var19 = new Vec3d(Math.cos(var16), 0.0, Math.sin(var16));
         double var20 = var1.getHeight();
         Vec3d var22 = new Vec3d(var9, var11 + var20 * 0.72, var13).add(var18.multiply(-0.15));
         this.run2(var22, var18, var19, -1, var3, var5, var6, var7);
         this.run2(var22, var18, var19, 1, var3, var5, var6, var7);
      }
   }

   private void run2(Vec3d var1, Vec3d var2, Vec3d var3, int var4, double var5, int var7, int var8, double var9) {
      Vec3d var11 = var1.add(var3.multiply(0.18 * var4));
      Vec3d var12 = var3.multiply(var4);
      Vec3d var13 = var2.multiply(-1.0);
      Vec3d var14 = new Vec3d(0.0, 1.0, 0.0);
      double var15 = var9 * var4;
      Vec3d var17 = class243Of(20.0, 35.0, var12, var13, var14);
      var17 = class243Of2(var17, var2, var15).normalize();
      Vec3d var18 = var11.add(var17.multiply(var5 * 0.42));
      if (var8 != 0) {
         ListUtils.run12(var11, var18, var8, 2.0F, true);
      }

      Vec3d[] var19 = new Vec3d[4];

      for (int var20 = 0; var20 < 4; var20++) {
         Vec3d var21 = class243Of(doubleArray[var20], doubleArray2[var20], var12, var13, var14);
         var21 = class243Of2(var21, var2, var15).normalize();
         Vec3d var22 = var18.add(var21.multiply(var5 * doubleArray3[var20]));
         var19[var20] = var22;
         if (var8 != 0) {
            float var23 = var20 == 0 ? 1.9F : 1.4F;
            ListUtils.run12(var18, var22, var8, var23, true);
         }
      }

      if (var7 != 0) {
         ListUtils.run4(var18, var19[0], var19[1], var7, true);
         ListUtils.run4(var18, var19[1], var19[2], var7, true);
         ListUtils.run4(var18, var19[2], var19[3], var7, true);
         ListUtils.run4(var11, var18, var19[0], var7, true);
         ListUtils.run4(var11, var19[3], var18, var7, true);
      }
   }

   private static Vec3d class243Of(double var0, double var2, Vec3d var4, Vec3d var5, Vec3d var6) {
      double var7 = Math.toRadians(var0);
      double var9 = Math.toRadians(var2);
      double var11 = Math.cos(var7);
      double var13 = Math.sin(var7);
      double var15 = Math.cos(var9);
      double var17 = Math.sin(var9);
      return var4.multiply(var15 * var11).add(var5.multiply(var15 * var13)).add(var6.multiply(var17));
   }

   private static Vec3d class243Of2(Vec3d var0, Vec3d var1, double var2) {
      if (var2 == 0.0) {
         return var0;
      } else {
         double var4 = Math.cos(var2);
         double var6 = Math.sin(var2);
         double var8 = var0.dotProduct(var1);
         Vec3d var10 = var1.crossProduct(var0);
         return var0.multiply(var4).add(var10.multiply(var6)).add(var1.multiply(var8 * (1.0 - var4)));
      }
   }

   public int getInt() {
      String var1 = this.colorMode.getMode();
      switch (var1.hashCode()) {
         case -1893076004:
            if (var1.equals("Purple")) {
               return -5226241;
            }
            break;
         case -1656737386:
            if (var1.equals("Rainbow")) {
               return CodeEngineScreenUtil2.intOf4(0, 1.0F, 1.0F);
            }
            break;
         case 83549193:
            if (var1.equals("White")) {
               return -1;
            }
      }

      return -15724528;
   }
}

