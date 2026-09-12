package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.mixin.SmartCullAccessor;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.Map$EntryUtils;
import dev.fede.nyx.util.PacketSenderUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FreecamModule extends Module {
   private static final double doubleVal = 0.32;
   private static final double doubleVal2 = 0.68;
   private static final double doubleVal3 = 5.0E-4;
   private static final double doubleVal4 = -256.0;
   private static final double doubleVal5 = 512.0;
   private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.1, 10.0, 0.1);
   public static volatile boolean bool;
   public static Vec3d class243 = Vec3d.ZERO;
   public static Vec3d class2432 = Vec3d.ZERO;
   public static float floatVal;
   public static float floatVal2;
   private Vec3d class2433 = Vec3d.ZERO;
   private float floatVal3;
   private float floatVal4;
   private boolean bool2;
   private boolean bool3;
   private Perspective class5498 = Perspective.FIRST_PERSON;
   private boolean bool4 = true;
   private double doubleVal6;
   private double doubleVal7;
   private double doubleVal8;

   public FreecamModule() {
      super("Freecam", "Detach the camera and fly it while your body stays put", Category.MOVEMENT);
      this.run6(new Setting[]{this.speed});
   }

   @Override
   public void run() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null && class310.world != null) {
         this.class2433 = var1.getEntityPos();
         this.floatVal3 = var1.getYaw();
         this.floatVal4 = var1.getPitch();
         this.bool2 = var1.getAbilities().flying;
         this.bool3 = var1.getAbilities().allowFlying;
         var1.setPosition(this.class2433.x, this.class2433.y, this.class2433.z);
         var1.lastRenderX = this.class2433.x;
         var1.lastRenderY = this.class2433.y;
         var1.lastRenderZ = this.class2433.z;
         var1.setLastPositionAndAngles(this.class2433, this.floatVal3, this.floatVal4);
         var1.setVelocity(Vec3d.ZERO);
         var1.fallDistance = 0.0;
         class243 = var1.getEyePos();
         class2432 = class243;
         floatVal = this.floatVal3;
         floatVal2 = this.floatVal4;
         this.doubleVal6 = 0.0;
         this.doubleVal7 = 0.0;
         this.doubleVal8 = 0.0;
         PacketSenderUtils.bool = true;
         if (class310.options != null) {
            this.class5498 = class310.options.getPerspective();
            if (this.class5498 == Perspective.FIRST_PERSON) {
               class310.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            }
         }

         try {
            SmartCullAccessor var2 = (SmartCullAccessor)class310;
            this.bool4 = var2.nyx$getSmartCull();
            var2.nyx$setSmartCull(false);
         } catch (Throwable var3) {
         }

         bool = true;
      } else {
         bool = false;
         this.run5(false);
      }
   }

   @Override
   public void run2() {
      bool = false;
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null) {
         var1.setYaw(this.floatVal3);
         var1.setPitch(this.floatVal4);
         var1.lastYaw = this.floatVal3;
         var1.lastPitch = this.floatVal4;
         var1.headYaw = this.floatVal3;
         var1.lastHeadYaw = this.floatVal3;
         var1.bodyYaw = this.floatVal3;
         var1.lastBodyYaw = this.floatVal3;
         var1.getAbilities().flying = this.bool2;
         var1.getAbilities().allowFlying = this.bool3;
      }

      if (class310.options != null && this.class5498 != null) {
         class310.options.setPerspective(this.class5498);
      }

      try {
         ((SmartCullAccessor)class310).nyx$setSmartCull(this.bool4);
      } catch (Throwable var3) {
      }
   }

   @Override
   public void run3() {
      if (bool) {
         ClientPlayerEntity var1 = class310.player;
         if (var1 != null && class310.world != null) {
            float var2 = var1.getYaw() - this.floatVal3;
            float var3 = var1.getPitch() - this.floatVal4;
            floatVal += var2;
            floatVal2 = MathHelper.clamp(floatVal2 + var3, -89.9F, 89.9F);
            var1.setYaw(this.floatVal3);
            var1.setPitch(this.floatVal4);
            var1.lastYaw = this.floatVal3;
            var1.lastPitch = this.floatVal4;
            var1.headYaw = this.floatVal3;
            var1.lastHeadYaw = this.floatVal3;
            var1.bodyYaw = this.floatVal3;
            var1.lastBodyYaw = this.floatVal3;
            var1.setPosition(this.class2433.x, this.class2433.y, this.class2433.z);
            var1.lastRenderX = this.class2433.x;
            var1.lastRenderY = this.class2433.y;
            var1.lastRenderZ = this.class2433.z;
            var1.setLastPositionAndAngles(this.class2433, this.floatVal3, this.floatVal4);
            var1.setVelocity(Vec3d.ZERO);
            var1.fallDistance = 0.0;
            var1.getAbilities().flying = this.bool2;
            var1.getAbilities().allowFlying = this.bool3;
            Window var4 = class310.getWindow();
            boolean var5 = class310.currentScreen == null && InputUtil.isKeyPressed(var4, 87);
            boolean var6 = class310.currentScreen == null && InputUtil.isKeyPressed(var4, 83);
            boolean var7 = class310.currentScreen == null && InputUtil.isKeyPressed(var4, 65);
            boolean var8 = class310.currentScreen == null && InputUtil.isKeyPressed(var4, 68);
            boolean var9 = class310.currentScreen == null && InputUtil.isKeyPressed(var4, 32);
            boolean var10 = class310.currentScreen == null && (InputUtil.isKeyPressed(var4, 340) || InputUtil.isKeyPressed(var4, 344));
            boolean var11 = class310.currentScreen == null && (InputUtil.isKeyPressed(var4, 341) || InputUtil.isKeyPressed(var4, 345));
            double var12 = this.speed.getValue();
            if (var11) {
               var12 *= 4.0;
            }

            if (var9 && var10) {
               var12 *= 0.25;
            }

            double var14 = Math.toRadians(floatVal);
            double var16 = Math.toRadians(floatVal2);
            double var18 = 0.0;
            double var20 = 0.0;
            double var22 = 0.0;
            if (var5) {
               var18 += -Math.sin(var14) * Math.cos(var16);
               var20 += -Math.sin(var16);
               var22 += Math.cos(var14) * Math.cos(var16);
            }

            if (var6) {
               var18 += Math.sin(var14) * Math.cos(var16);
               var20 += Math.sin(var16);
               var22 += -Math.cos(var14) * Math.cos(var16);
            }

            if (var7) {
               var18 += Math.cos(var14);
               var22 += Math.sin(var14);
            }

            if (var8) {
               var18 += -Math.cos(var14);
               var22 += -Math.sin(var14);
            }

            if (var9 && !var10) {
               var20++;
            }

            if (var10 && !var9) {
               var20--;
            }

            double var24 = Math.sqrt(var18 * var18 + var20 * var20 + var22 * var22);
            if (var24 > 0.001) {
               var18 = var18 / var24 * var12;
               var20 = var20 / var24 * var12;
               var22 = var22 / var24 * var12;
               this.doubleVal6 = this.doubleVal6 + (var18 - this.doubleVal6) * 0.32;
               this.doubleVal7 = this.doubleVal7 + (var20 - this.doubleVal7) * 0.32;
               this.doubleVal8 = this.doubleVal8 + (var22 - this.doubleVal8) * 0.32;
            } else {
               this.doubleVal6 *= 0.68;
               this.doubleVal7 *= 0.68;
               this.doubleVal8 *= 0.68;
               if (Math.abs(this.doubleVal6) < 5.0E-4) {
                  this.doubleVal6 = 0.0;
               }

               if (Math.abs(this.doubleVal7) < 5.0E-4) {
                  this.doubleVal7 = 0.0;
               }

               if (Math.abs(this.doubleVal8) < 5.0E-4) {
                  this.doubleVal8 = 0.0;
               }
            }

            class2432 = class243;
            double var26 = MathHelper.clamp(class243.y + this.doubleVal7, -256.0, 512.0);
            class243 = new Vec3d(class243.x + this.doubleVal6, var26, class243.z + this.doubleVal8);

            try {
               Map$EntryUtils.intOf(class243, 8);
            } catch (Throwable var29) {
            }
         }
      }
   }

   public static Vec3d class243Of(float var0) {
      return !bool
         ? null
         : new Vec3d(
            MathHelper.lerp(var0, class2432.x, class243.x), MathHelper.lerp(var0, class2432.y, class243.y), MathHelper.lerp(var0, class2432.z, class243.z)
         );
   }

   public static Vec3d getclass243() {
      return bool ? class243 : null;
   }

   public static float getFloat() {
      return floatVal;
   }

   public static float getFloat2() {
      return floatVal2;
   }

   public double getDouble() {
      return bool ? class243.distanceTo(this.class2433) : 0.0;
   }

   @Override
   public String getString3() {
      return !bool ? null : "§7" + (int)Math.round(class243.distanceTo(this.class2433)) + "m";
   }
}

