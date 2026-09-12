package dev.fede.mixin;

import dev.fede.module.impl.FreeLookModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public class EntityFreeLookMixin {
   @Inject(
      method = {"method_5872(DD)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$freeLookTurn(double d, double e, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if ((Object)this == mc.player) {
         FreeLookModule freeLook = FreeLookModule.get();
         if (freeLook != null && freeLook.cameraMode()) {
            freeLook.addCameraLook(d, e);
            ci.cancel();
         }
      }
   }
}

