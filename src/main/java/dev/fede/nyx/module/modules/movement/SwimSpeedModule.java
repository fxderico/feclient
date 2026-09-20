package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.util.math.Vec3d;

/**
 * Cancels the water movement penalty by driving horizontal velocity along
 * your input direction while you're touching water -- same horizontal-boost
 * trick SpeedModule's Strafe mode uses (compute the yaw-relative input
 * vector, normalize it, scale it to a target speed, write x/z), but gated on
 * isTouchingWater() so it only ever fires when the water slowdown is what's
 * actually holding you back. Y velocity is left untouched, so swimming up
 * with jump / sinking with sneak still behaves exactly like vanilla.
 */
public class SwimSpeedModule extends Module {
   // 1-5 "levels" like a plain speed dial. each level adds a flat chunk of
   // horizontal blocks/tick: L1 ~0.10 (already above vanilla's ~0.06 swim
   // crawl), L5 ~0.50 (roughly sprint-on-land pace, in water).
   private final NumberSetting speed = new NumberSetting("Speed", 2.0, 1.0, 5.0, 1.0);
   // same rationale as Speed/Fly's Humanize: a dead-flat velocity value every
   // tick is a mechanical tell. eases toward the target instead of snapping
   // and adds a little noise. generic "less robotic" smoothing, not built
   // against any specific anticheat.
   private final BooleanSetting humanize = new BooleanSetting("Humanize", false);
   private final NumberSetting jitter = new NumberSetting("Jitter", 0.02, 0.0, 0.1, 0.005);
   private double currentSpeedFactor;
   private final java.util.concurrent.ThreadLocalRandom rng = java.util.concurrent.ThreadLocalRandom.current();

   public SwimSpeedModule() {
      super("Swim Speed", "Removes the in-water movement slowdown", Category.MOVEMENT);
      this.run6(new Setting[]{this.speed, this.humanize, this.jitter});
   }

   @Override
   public void run2() {
      this.currentSpeedFactor = 0.0;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.player.isTouchingWater()) {
         boolean hasInput = class310.player.forwardSpeed != 0.0F || class310.player.sidewaysSpeed != 0.0F;
         if (hasInput) {
            double yaw = Math.toRadians(class310.player.getYaw());
            double dirX = -Math.sin(yaw) * class310.player.forwardSpeed + Math.cos(yaw) * class310.player.sidewaysSpeed;
            double dirZ = Math.cos(yaw) * class310.player.forwardSpeed + Math.sin(yaw) * class310.player.sidewaysSpeed;
            double len = Math.sqrt(dirX * dirX + dirZ * dirZ);
            if (!(len < 1.0E-6)) {
               double target = this.speed.getValue() * 0.1;
               if (this.humanize.getValue()) {
                  this.currentSpeedFactor += (target - this.currentSpeedFactor) * 0.35;
                  target = this.currentSpeedFactor;
               } else {
                  this.currentSpeedFactor = target;
               }

               double velX = dirX / len * target;
               double velZ = dirZ / len * target;
               if (this.humanize.getValue()) {
                  double j = this.jitter.getValue();
                  if (j > 0.0) {
                     velX += (this.rng.nextDouble() - 0.5) * j;
                     velZ += (this.rng.nextDouble() - 0.5) * j;
                  }
               }

               Vec3d vel = class310.player.getVelocity();
               class310.player.setVelocity(velX, vel.y, velZ);
            }
         } else {
            this.currentSpeedFactor = 0.0;
         }
      }
   }

   @Override
   public String getString3() {
      return "§7" + this.speed.getValueInt();
   }
}
