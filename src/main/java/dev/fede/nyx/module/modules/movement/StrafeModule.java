package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.util.math.Vec3d;

public class StrafeModule extends Module {
   private static final double doubleVal = 1.0E-6;
   private final NumberSetting airSpeed = new NumberSetting("AirSpeed", 0.3, 0.1, 1.0, 0.01);
   private final NumberSetting groundSpeed = new NumberSetting("GroundSpeed", 0.22, 0.1, 1.0, 0.01);
   private final BooleanSetting requireJumping = new BooleanSetting("RequireJumping", false);

   public StrafeModule() {
      super("Strafe", "Snap horizontal velocity to look direction", Category.MOVEMENT);
      this.run6(new Setting[]{this.airSpeed, this.groundSpeed, this.requireJumping});
   }

   @Override
   public void run2() {
      if (class310.player != null) {
         boolean var1 = class310.player.forwardSpeed != 0.0F || class310.player.sidewaysSpeed != 0.0F;
         if (var1) {
            if (!this.requireJumping.getValue() || class310.options.jumpKey.isPressed()) {
               boolean var2 = class310.player.isOnGround();
               double var3 = var2 ? this.groundSpeed.getValue() : this.airSpeed.getValue();
               double var5 = Math.toRadians(class310.player.getYaw());
               double var7 = -Math.sin(var5) * class310.player.forwardSpeed + Math.cos(var5) * class310.player.sidewaysSpeed;
               double var9 = Math.cos(var5) * class310.player.forwardSpeed + Math.sin(var5) * class310.player.sidewaysSpeed;
               double var11 = Math.sqrt(var7 * var7 + var9 * var9);
               if (!(var11 < 1.0E-6)) {
                  double var13 = var7 / var11 * var3;
                  double var15 = var9 / var11 * var3;
                  Vec3d var17 = class310.player.getVelocity();
                  class310.player.setVelocity(var13, var17.y, var15);
               }
            }
         }
      }
   }
}

