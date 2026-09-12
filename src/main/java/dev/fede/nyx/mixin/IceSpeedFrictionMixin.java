package dev.fede.nyx.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.module.modules.movement.IceSpeedModule;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Block.class})
public class IceSpeedFrictionMixin {
   @ModifyReturnValue(
      method = {"getSlipperiness()F"},
      at = {@At("RETURN")}
   )
   private float nyx$overrideFriction(float original) {
      if (!IceSpeedModule.bool) {
         return original;
      } else {
         Block var2 = (Block)(Object)this;
         return IceSpeedModule.floatOf(var2, original);
      }
   }
}

