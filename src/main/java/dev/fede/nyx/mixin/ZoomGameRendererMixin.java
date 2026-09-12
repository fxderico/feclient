package dev.fede.nyx.mixin;
import net.minecraft.client.render.Camera;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.module.modules.render.ZoomModule;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({GameRenderer.class})
public class ZoomGameRendererMixin {
   @ModifyReturnValue(
      method = {"getFov(Lnet/minecraft/Camera;FZ)F"},
      at = {@At("RETURN")}
   )
   private float nyx$applyZoom(float original) {
      double var2 = ZoomModule.getDouble();
      if (!(var2 >= 0.9999) && !Double.isNaN(var2)) {
         if (var2 < 0.05) {
            var2 = 0.05;
         }

         return (float)(original * var2);
      } else {
         return original;
      }
   }
}

