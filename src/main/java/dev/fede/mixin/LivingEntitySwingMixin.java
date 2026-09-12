package dev.fede.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LivingEntity.class})
public class LivingEntitySwingMixin {
   @ModifyReturnValue(
      method = {"method_6028"},
      at = {@At("RETURN")}
   )
   private int FeClient$swingSpeed(int original) {
      LivingEntity self = (LivingEntity)(Object)this;
      if (MinecraftClient.getInstance().player != self) {
         return original;
      } else {
         ModuleManager modules = FeClient.modules();
         if (modules != null && modules.swingSpeed != null && modules.swingSpeed.isEnabled()) {
            float multiplier = modules.swingSpeed.multiplier();
            return multiplier <= 0.01F ? original : Math.max(1, Math.round(original / multiplier));
         } else {
            return original;
         }
      }
   }
}



