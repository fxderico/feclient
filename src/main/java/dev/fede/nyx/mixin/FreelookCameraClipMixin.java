package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.FreelookModule;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Camera.class})
public abstract class FreelookCameraClipMixin {
   @Inject(
      method = {"clipToSpace"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$freelookNoClip(float desiredDistance, CallbackInfoReturnable<Float> cir) {
      if (FreelookModule.bool) {
         cir.setReturnValue(desiredDistance);
      }
   }
}

