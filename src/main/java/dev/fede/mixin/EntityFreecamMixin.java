package dev.fede.mixin;

import dev.fede.module.impl.FreecamModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public class EntityFreecamMixin {
   @Inject(
      method = {"method_5756"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$freecamSeeOwnBody(PlayerEntity viewer, CallbackInfoReturnable<Boolean> cir) {
      Entity self = (Entity)(Object)this;
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && self == mc.player && viewer == mc.player) {
         FreecamModule freecam = FreecamModule.get();
         if (freecam != null && freecam.isActive() && freecam.isShowPlayerModel()) {
            cir.setReturnValue(false);
         }
      }
   }
}

