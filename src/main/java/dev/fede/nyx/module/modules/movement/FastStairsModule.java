package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Auto-jumps the instant you sprint into a step-up so you climb stairs/blocks
 * without manually spamming space. Ported from Echo's FastStairs into the tick
 * model: while sprinting forward on the ground and bumping a step (horizontal
 * collision) with headroom to jump, it fires a jump. "Only Stairs" restricts it
 * to actual stair blocks so it doesn't hop every wall.
 */
public class FastStairsModule extends Module {
   private final BooleanSetting onlyStairs = new BooleanSetting("OnlyStairs", false);

   public FastStairsModule() {
      super("FastStairs", "Auto-jumps up stairs/steps while sprinting", Category.MOVEMENT);
      this.run6(new Setting[]{this.onlyStairs});
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.world == null) {
         return;
      }

      if (!class310.player.isOnGround()
         || !class310.player.isSprinting()
         || class310.player.forwardSpeed == 0.0F
         || class310.player.isTouchingWater()
         || class310.player.isInLava()
         || !class310.player.horizontalCollision) {
         return;
      }

      // block directly ahead at foot level, from the facing yaw
      double yaw = Math.toRadians(class310.player.getYaw());
      double fx = -Math.sin(yaw);
      double fz = Math.cos(yaw);
      BlockPos feet = BlockPos.ofFloored(class310.player.getX() + fx, class310.player.getBoundingBox().minY, class310.player.getZ() + fz);

      if (this.onlyStairs.getValue() && !(class310.world.getBlockState(feet).getBlock() instanceof StairsBlock)) {
         return;
      }

      // need the step itself solid, and clear space to rise into (head + one above)
      boolean stepSolid = !class310.world.getBlockState(feet).getCollisionShape(class310.world, feet).isEmpty();
      boolean headClear = class310.world.getBlockState(feet.up()).getCollisionShape(class310.world, feet.up()).isEmpty();
      boolean aboveClear = class310.world.getBlockState(feet.up(2)).getCollisionShape(class310.world, feet.up(2)).isEmpty();
      if (stepSolid && headClear && aboveClear) {
         Vec3d v = class310.player.getVelocity();
         class310.player.setVelocity(v.x, 0.42, v.z);
      }
   }
}
