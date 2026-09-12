package dev.fede.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Entity.class})
public class EntityHitboxMixin {
   @ModifyReturnValue(
      method = {"method_5829"},
      at = {@At("RETURN")}
   )
   private Box FeClient$expandHitbox(Box original) {
      return original;
   }
}

