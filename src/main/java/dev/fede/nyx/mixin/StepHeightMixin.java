package dev.fede.nyx.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.module.modules.movement.StepModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Entity.class})
abstract class StepHeightMixin {
   @ModifyReturnValue(
      method = {"getStepHeight()F"},
      at = {@At("RETURN")}
   )
   private float nyx$stepHeight(float original) {
      if (StepModule.floatVal <= 0.0F) {
         return original;
      } else {
         Entity var2 = (Entity)(Object)this;
         return MinecraftClient.getInstance().player != var2 ? original : StepModule.floatOf2(original);
      }
   }
}

