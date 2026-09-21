package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.util.math.Vec3d;

/**
 * Auto-jumps the instant you take knockback while on the ground, which resets
 * the ground-friction window and cuts the horizontal knockback you keep. Ported
 * from Echo's JumpReset. Note: reducing/altering incoming velocity like this is
 * the kind of thing anticheats flag at high chance values — the Chance setting
 * lets you throttle how often it fires.
 */
public class JumpResetModule extends Module {
   private final NumberSetting chance = new NumberSetting("Chance", 100.0, 0.0, 100.0, 1.0);
   private final BooleanSetting liquidCheck = new BooleanSetting("LiquidCheck", true);
   private boolean firedThisHurt;

   public JumpResetModule() {
      super("JumpReset", "Auto-jumps on knockback to reduce incoming velocity", Category.MOVEMENT);
      this.run6(new Setting[]{this.chance, this.liquidCheck});
   }

   @Override
   public void run2() {
      this.firedThisHurt = false;
   }

   @Override
   public void run3() {
      if (class310.player == null) {
         return;
      }

      // hurtTime ticks down from a hit; fire once per hit, on the ground only
      if (class310.player.hurtTime <= 0) {
         this.firedThisHurt = false;
         return;
      }

      if (this.firedThisHurt || !class310.player.isOnGround()) {
         return;
      }

      if (this.liquidCheck.getValue() && (class310.player.isTouchingWater() || class310.player.isInLava())) {
         return;
      }

      if (Math.random() * 100.0 > this.chance.getValue()) {
         return;
      }

      // base jump velocity — same approach AirJump uses (avoids the protected
      // LivingEntity.jump()); the reset comes from leaving the ground on the hit tick
      Vec3d v = class310.player.getVelocity();
      class310.player.setVelocity(v.x, 0.42, v.z);
      class310.player.fallDistance = 0.0;
      this.firedThisHurt = true;
   }
}
