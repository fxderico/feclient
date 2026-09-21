package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Cancels the cobweb movement penalty. Cobwebs clamp your velocity to a crawl;
 * while any part of the hitbox is inside a web this drives horizontal velocity
 * along your input direction at a normal speed instead (jump/sneak still move
 * you up/down). Ported from Echo's NoWeb (Grim mode) into feclient's tick model.
 */
public class NoWebModule extends Module {
   private final NumberSetting speed = new NumberSetting("Speed", 0.35, 0.1, 0.65, 0.01);

   public NoWebModule() {
      super("NoWeb", "Removes the cobweb slowdown", Category.MOVEMENT);
      this.run6(new Setting[]{this.speed});
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.world == null || !this.inWeb()) {
         return;
      }

      double s = this.speed.getValue();
      float fwd = class310.player.forwardSpeed;
      float side = class310.player.sidewaysSpeed;
      double x = 0.0;
      double z = 0.0;
      if (fwd != 0.0F || side != 0.0F) {
         double yaw = Math.toRadians(class310.player.getYaw());
         double dx = -Math.sin(yaw) * fwd + Math.cos(yaw) * side;
         double dz = Math.cos(yaw) * fwd + Math.sin(yaw) * side;
         double len = Math.sqrt(dx * dx + dz * dz);
         if (len > 1.0E-6) {
            x = dx / len * s;
            z = dz / len * s;
         }
      }

      double y;
      if (class310.options.jumpKey.isPressed()) {
         y = 0.65;
      } else if (class310.options.sneakKey.isPressed()) {
         y = -0.65;
      } else {
         y = class310.player.getVelocity().y;
      }

      class310.player.setVelocity(x, y, z);
   }

   private boolean inWeb() {
      Box box = class310.player.getBoundingBox();
      BlockPos min = BlockPos.ofFloored(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
      BlockPos max = BlockPos.ofFloored(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
      for (int bx = min.getX(); bx <= max.getX(); bx++) {
         for (int by = min.getY(); by <= max.getY(); by++) {
            for (int bz = min.getZ(); bz <= max.getZ(); bz++) {
               if (class310.world.getBlockState(new BlockPos(bx, by, bz)).getBlock() == Blocks.COBWEB) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
