package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

/**
 * Silent-aims an ender pearl at the nearest enemy so your throw lands on them
 * without your camera moving. Ported/adapted from Echo & Expa's TargetPearl.
 *
 * Uses feclient's existing silent-aim engine (AntiAFKModuleUtil sets the spoofed
 * server rotation; PlayerMoveC2SPacketMixin stamps it onto outgoing movement
 * packets and restores the real view right after). While you're holding a pearl
 * and a target is in range, this feeds the aim each tick — you just right-click
 * and the server throws the pearl at them. Clears the aim the moment you stop
 * holding a pearl or there's no target, so it never fights your normal look.
 */
public class TargetPearlModule extends Module {
   private final NumberSetting range = new NumberSetting("Range", 20.0, 3.0, 60.0, 1.0);
   private final BooleanSetting requirePearl = new BooleanSetting("OnlyHoldingPearl", true);

   public TargetPearlModule() {
      super("TargetPearl", "Silent-aims a pearl at the nearest enemy", Category.COMBAT);
      this.run6(new Setting[]{this.range, this.requirePearl});
   }

   @Override
   public void run2() {
      AntiAFKModuleUtil.run2();
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.world == null) {
         AntiAFKModuleUtil.run2();
         return;
      }

      boolean holdingPearl = class310.player.getMainHandStack().isOf(Items.ENDER_PEARL)
         || class310.player.getOffHandStack().isOf(Items.ENDER_PEARL);
      if (this.requirePearl.getValue() && !holdingPearl) {
         AntiAFKModuleUtil.run2();
         return;
      }

      PlayerEntity target = this.nearestTarget();
      if (target == null) {
         AntiAFKModuleUtil.run2();
         return;
      }

      // aim at the target's mid-body from the eye position
      Vec3d eye = class310.player.getEyePos();
      Vec3d aim = target.getEntityPos().add(0.0, target.getHeight() * 0.5, 0.0);
      double dx = aim.x - eye.x;
      double dy = aim.y - eye.y;
      double dz = aim.z - eye.z;
      double flat = Math.sqrt(dx * dx + dz * dz);
      float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(dy, flat)));

      AntiAFKModuleUtil.run(yaw, pitch);
   }

   private PlayerEntity nearestTarget() {
      double max = this.range.getValue();
      double best = max * max;
      PlayerEntity found = null;
      for (PlayerEntity p : class310.world.getPlayers()) {
         if (p == class310.player || !p.isAlive() || p.isSpectator()) {
            continue;
         }

         double d = class310.player.squaredDistanceTo(p);
         if (d < best) {
            best = d;
            found = p;
         }
      }

      return found;
   }

   @Override
   public String getString3() {
      return AntiAFKModuleUtil.isEnabled2() ? "§alocked" : null;
   }
}
