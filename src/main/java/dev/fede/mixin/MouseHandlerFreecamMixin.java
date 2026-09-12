package dev.fede.mixin;

import dev.fede.module.impl.FreecamModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Mouse.class})
public abstract class MouseHandlerFreecamMixin {
   @Shadow
   private double cursorDeltaX;
   @Shadow
   private double cursorDeltaY;

   @Inject(
      method = {"method_1606"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$freecamMouse(double d, CallbackInfo ci) {
      FreecamModule freecam = FreecamModule.get();
      if (freecam != null && freecam.isActive()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.currentScreen == null) {
            double sensitivity = (Double)mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
            double factor = sensitivity * sensitivity * sensitivity * 8.0;
            double dx = this.cursorDeltaX * factor * freecam.getLookSensitivity();
            double dy = this.cursorDeltaY * factor * freecam.getLookSensitivity();
            float newYaw = freecam.getCurrentYaw() + (float)dx * 0.15F;
            float newPitch = freecam.getCurrentPitch() + (float)dy * 0.15F;
            freecam.setRotation(newYaw, newPitch);
            ci.cancel();
         }
      }
   }
}

