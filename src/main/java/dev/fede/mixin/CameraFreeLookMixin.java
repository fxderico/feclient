package dev.fede.mixin;

import dev.fede.module.impl.FreeLookModule;
import dev.fede.module.impl.FreecamModule;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({Camera.class})
public abstract class CameraFreeLookMixin {
   @ModifyArgs(
      method = {"method_19321"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_4184;method_19325(FF)V"
      )
   )
   private void FeClient$freeLookRotation(Args args) {
      FreeLookModule freeLook = FreeLookModule.get();
      if (freeLook != null && freeLook.isActive()) {
         FreecamModule freecam = FreecamModule.get();
         if (freecam == null || !freecam.isActive()) {
            args.set(0, freeLook.getCameraYaw());
            args.set(1, freeLook.getCameraPitch());
         }
      }
   }

   @Inject(
      method = {"method_19318"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$freeLookThroughWalls(float distance, CallbackInfoReturnable<Float> cir) {
      FreeLookModule freeLook = FreeLookModule.get();
      if (freeLook != null && freeLook.seeThroughWalls()) {
         FreecamModule freecam = FreecamModule.get();
         if (freecam == null || !freecam.isActive()) {
            cir.setReturnValue(distance);
         }
      }
   }
}

