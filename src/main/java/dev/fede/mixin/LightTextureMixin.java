package dev.fede.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin({LightmapTextureManager.class})
public class LightTextureMixin {
   @ModifyExpressionValue(
      method = {"method_3313"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_7172;method_41753()Ljava/lang/Object;"
      )},
      slice = {@Slice(
         from = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_315;method_42473()Lnet/minecraft/class_7172;"
         )
      )}
   )
   private Object FeClient$fullbrightGamma(Object original) {
      ModuleManager modules = FeClient.modules();
      return modules != null && modules.fullbright != null && modules.fullbright.isEnabled() ? (double)modules.fullbright.gamma.getFloat() : original;
   }
}



