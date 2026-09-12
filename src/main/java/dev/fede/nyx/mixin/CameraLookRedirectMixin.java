package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.FreecamModule;
import dev.fede.nyx.module.modules.movement.FreelookModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public abstract class CameraLookRedirectMixin {
   @Inject(
      method = {"changeLookDirection(DD)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$redirectMouseLook(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
      MinecraftClient var6 = MinecraftClient.getInstance();
      if (var6 != null && ((Object)this) == var6.player) {
         if (FreecamModule.bool || FreelookModule.bool) {
            float var7 = (float)(cursorDeltaX * 0.15);
            float var8 = (float)(cursorDeltaY * 0.15);
            if (FreecamModule.bool) {
               FreecamModule.floatVal += var7;
               FreecamModule.floatVal2 = MathHelper.clamp(FreecamModule.floatVal2 + var8, -89.9F, 89.9F);
            } else {
               FreelookModule.floatVal += var7;
               FreelookModule.floatVal2 = MathHelper.clamp(FreelookModule.floatVal2 + var8, -89.9F, 89.9F);
            }

            ci.cancel();
         }
      }
   }
}

