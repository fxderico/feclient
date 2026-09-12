package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.util.math.Vec3d;

public class SpiderModule extends Module {
   private final BooleanSetting requireForward = new BooleanSetting("RequireInputForward", true);
   private final NumberSetting climbSpeed = new NumberSetting("ClimbSpeed", 0.2, 0.05, 1.0, 0.05);

   public SpiderModule() {
      super("Spider", "Climb any wall you press against", Category.MOVEMENT);
      this.run6(new Setting[]{this.requireForward, this.climbSpeed});
   }

   @Override
   public void run2() {
      if (class310.player != null) {
         if (class310.player.horizontalCollision) {
            if (!this.requireForward.getValue() || !(class310.player.forwardSpeed <= 0.0F)) {
               double var1 = this.climbSpeed.getValue();
               Vec3d var3 = class310.player.getVelocity();
               class310.player.setVelocity(var3.x, var1, var3.z);
               class310.player.fallDistance = 0.0;
            }
         }
      }
   }
}

