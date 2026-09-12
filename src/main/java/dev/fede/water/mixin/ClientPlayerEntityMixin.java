package dev.fede.water.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
public class ClientPlayerEntityMixin {
   @Inject(
      method = {"tickMovement"},
      at = {@At("HEAD")}
   )
   private void onTickMovement(CallbackInfo var1) {
   }
}

