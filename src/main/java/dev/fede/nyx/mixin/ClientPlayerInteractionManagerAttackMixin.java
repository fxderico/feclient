package dev.fede.nyx.mixin;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.modules.combat.CriticalsModule;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerInteractionManager.class})
public abstract class ClientPlayerInteractionManagerAttackMixin {
   @Inject(
      method = {"attackEntity(Lnet/minecraft/PlayerEntity;Lnet/minecraft/Entity;)V"},
      at = {@At("HEAD")}
   )
   private void nyx$critsBeforeAttack(PlayerEntity var1, Entity var2, CallbackInfo var3) {
      if (NyxClient.MODULES != null) {
         CriticalsModule var4 = NyxClient.MODULES.moduleOf2(CriticalsModule.class);
         if (var4 != null && var4.isEnabled3()) {
            if (CriticalsModule.isEnabled2()) {
               var4.run7(var2);
            }
         }
      }
   }

   @Inject(
      method = {"attackEntity(Lnet/minecraft/PlayerEntity;Lnet/minecraft/Entity;)V"},
      at = {@At("RETURN")}
   )
   private void nyx$critsAfterAttack(PlayerEntity var1, Entity var2, CallbackInfo var3) {
      if (NyxClient.MODULES != null) {
         CriticalsModule var4 = NyxClient.MODULES.moduleOf2(CriticalsModule.class);
         if (var4 != null && var4.isEnabled3()) {
            if (CriticalsModule.isEnabled2()) {
               var4.run8();
            }
         }
      }
   }
}

