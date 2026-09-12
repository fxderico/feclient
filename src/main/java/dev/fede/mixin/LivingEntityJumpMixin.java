package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
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
}



