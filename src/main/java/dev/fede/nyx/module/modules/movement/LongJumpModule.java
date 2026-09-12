package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class LongJumpModule extends Module {
   private final NumberSetting boost = new NumberSetting("Boost", 1.5, 1.0, 4.0, 0.1);
   private final NumberSetting duration = new NumberSetting("Duration", 6.0, 1.0, 40.0, 1.0);
   private boolean bool;
   private boolean bool2;
   private int intVal;

   public LongJumpModule() {
      super("LongJump", "Amplifies horizontal jump distance", Category.MOVEMENT);
      this.run6(new Setting[]{this.boost, this.duration});
   }

   @Override
   public void run() {
      this.bool = class310.options != null && class310.options.jumpKey.isPressed();
      this.bool2 = class310.player != null && class310.player.isOnGround();
      this.intVal = 0;
   }

   @Override
   public void run2() {
      this.intVal = 0;
   }

   @Override
   public void run3() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null) {
         boolean var2 = class310.options.jumpKey.isPressed();
         boolean var3 = var2 && !this.bool;
         boolean var4 = var1.isOnGround();
         if (var3 && (var4 || this.bool2) && !var1.hasVehicle()) {
            this.intVal = this.duration.getValueInt();
         }

         if (this.intVal > 0) {
            if (var4 && !var3) {
               this.intVal = 0;
            } else {
               this.run4(var1);
               this.intVal--;
            }
         }

         this.bool = var2;
         this.bool2 = var4;
      }
   }

   private void run4(ClientPlayerEntity var1) {
      double var2 = this.boost.getValue();
      Vec3d var4 = var1.getVelocity();
      float var5 = var1.forwardSpeed;
      float var6 = var1.sidewaysSpeed;
      if (var5 == 0.0F && var6 == 0.0F) {
         var1.setVelocity(var4.x * var2, var4.y, var4.z * var2);
      } else {
         double var7 = Math.toRadians(var1.getYaw());
         double var9 = -Math.sin(var7) * var5 + Math.cos(var7) * var6;
         double var11 = Math.cos(var7) * var5 + Math.sin(var7) * var6;
         double var13 = Math.sqrt(var9 * var9 + var11 * var11);
         if (var13 <= 0.0) {
            var1.setVelocity(var4.x * var2, var4.y, var4.z * var2);
         } else {
            double var15 = Math.sqrt(var4.x * var4.x + var4.z * var4.z);
            double var17 = var15 * var2;
            double var19 = var9 / var13;
            double var21 = var11 / var13;
            var1.setVelocity(var19 * var17, var4.y, var21 * var17);
         }
      }
   }
}

