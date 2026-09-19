package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.nyx.module.modules.movement.HighJumpModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public class LivingEntityJumpMixin {
   @Inject(
      method = {"method_6043"},
      at = {@At("HEAD")}
   )
   private void FeClient$onJump(CallbackInfo ci) {
      if ((Object)this instanceof ClientPlayerEntity player && player == MinecraftClient.getInstance().player) {
         ModuleManager modules = FeClient.modules();
         if (modules != null && modules.jumpCircles != null && modules.jumpCircles.isEnabled()) {
            modules.jumpCircles.onPlayerJump(player);
         }
      }
   }

   /**
    * Jump module's actual boost — injected straight into the real jump
    * impulse (TAIL, after vanilla's own jump() already ran and set its
    * 0.42 velocity) instead of racing a per-tick isOnGround check against
    * vanilla's own jump timing. Fires on every single jump, no missed
    * presses, no re-press needed.
    */
   @Inject(
      method = {"method_6043"},
      at = {@At("TAIL")}
   )
   private void FeClient$onJumpBoost(CallbackInfo ci) {
      if (!HighJumpModule.ACTIVE) return;
      if ((Object)this instanceof ClientPlayerEntity player && player == MinecraftClient.getInstance().player) {
         if (HighJumpModule.ONLY_ON_GROUND && !player.isOnGround()) return;
         if (player.hasVehicle()) return;
         Vec3d v = player.getVelocity();
         player.setVelocity(v.x, HighJumpModule.JUMP_VELOCITY, v.z);
         player.velocityDirty = true;
      }
   }
}



