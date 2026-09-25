package dev.fede.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

/**
 * Fullbright, gamma path — 1.21.11 rewrite.
 *
 * The lightmap went GPU/shader-based (BILT_SCREEN_LIGHTMAP). getBrightness()
 * is now a dead static helper that no longer drives the world lightmap, so the
 * old getBrightness-output override can't brighten anything. BUT
 * LightmapTextureManager.update(float) still reads the gamma option value and
 * hands it to the shader as Math.max(0, gamma - darkness). So we override the
 * gamma the shader sees: with fullbright on, feed it the module's Gamma slider
 * (1..15) — well past vanilla's 1.0 cap — which the shader turns into a fully
 * lit world. Previously this hooked the stale intermediary `method_3313`, which
 * no longer maps to update(), so the override never applied and fullbright went
 * dark. Targeting `update` by name fixes it.
 */
@Mixin(LightmapTextureManager.class)
public class LightTextureMixin {
   @ModifyExpressionValue(
      method = "update",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"
      ),
      slice = @Slice(
         from = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions;getGamma()Lnet/minecraft/client/option/SimpleOption;"
         )
      )
   )
   private Object FeClient$fullbrightGamma(Object original) {
      ModuleManager modules = FeClient.modules();
      return modules != null && modules.fullbright != null && modules.fullbright.isEnabled()
         ? (double)modules.fullbright.gamma.getFloat()
         : original;
   }
}
