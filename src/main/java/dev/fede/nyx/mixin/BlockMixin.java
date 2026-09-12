package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Block.class})
public class BlockMixin {
   @Inject(
      method = {"shouldDrawSide"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void nyx$xrayCull(BlockState var0, BlockState var1, Direction var2, CallbackInfoReturnable<Boolean> var3) {
      if (XRay.isEnabled_s()) {
         boolean var4 = XRay.check3(var0.getBlock());
         if (!var4) {
            var3.setReturnValue(false);
         } else {
            if (!XRay.check3(var1.getBlock())) {
               var3.setReturnValue(true);
            }
         }
      }
   }
}

