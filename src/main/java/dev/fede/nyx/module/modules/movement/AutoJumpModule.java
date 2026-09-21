package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import net.minecraft.util.math.Vec3d;

/**
 * Continuously auto-jumps whenever you're on the ground (or wading in shallow
 * water), so you bunny-hop without holding space. Ported from Expa's AutoJump.
 * Preserves horizontal velocity so sprint-jumping still carries momentum.
 */
public class AutoJumpModule extends Module {
   public AutoJumpModule() {
      super("AutoJump", "Automatically jumps when on the ground", Category.MOVEMENT);
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.currentScreen != null) {
         return;
      }

      boolean canJump = class310.player.isOnGround()
         || (class310.player.isTouchingWater() && !class310.player.isSubmergedInWater());
      if (canJump) {
         Vec3d v = class310.player.getVelocity();
         class310.player.setVelocity(v.x, 0.42, v.z);
      }
   }
}
