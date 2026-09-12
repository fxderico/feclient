package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.render.ClearWorldModule;
import net.minecraft.client.render.WeatherRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WeatherRendering.class})
class ClearWorldWeatherMixin {
   @Inject(
      method = {"buildPrecipitationPieces"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$skipBuild(CallbackInfo ci) {
      if (ClearWorldModule.isEnabled_s()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderPrecipitation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$skipRender(CallbackInfo ci) {
      if (ClearWorldModule.isEnabled_s()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"addParticlesAndSound"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$skipAmbience(CallbackInfo ci) {
      if (ClearWorldModule.isEnabled_s()) {
         ci.cancel();
      }
   }
}

