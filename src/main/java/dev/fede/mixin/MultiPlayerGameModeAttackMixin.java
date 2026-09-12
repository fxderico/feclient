package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.CriticalsModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerInteractionManager.class})
public class MultiPlayerGameModeAttackMixin {
   @Inject(
      method = {"method_2918"},
      at = {@At("HEAD")}
   )
   private void FeClient$onAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
      if (player == MinecraftClient.getInstance().player && target != player) {
         ModuleManager modules = FeClient.modules();
         if (modules != null) {
            // Criticals — fake tiny fall distance so the server registers a crit
            if (CriticalsModule.ACTIVE
                  && !player.isTouchingWater()
                  && !player.isOnGround()
                  && player.fallDistance == 0.0f) {
               player.fallDistance = 0.00001f + (float)(Math.random() * 0.00009f);
            }

            // Hit particles
            if (modules.hitParticles != null && modules.hitParticles.isEnabled()) {
               modules.hitParticles.onHit(target);
            }
         }
      }
   }
}



