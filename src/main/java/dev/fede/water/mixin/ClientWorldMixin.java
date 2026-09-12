package dev.fede.water.mixin;

import dev.fede.water.module.modules.render.NoRender;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientWorld.class})
public class ClientWorldMixin {
   @Inject(
      method = {"getLightningTicksLeft"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void water$hideLightningFlash(CallbackInfoReturnable<Integer> var1) {
      if (NoRender.hideThunder()) {
         var1.setReturnValue(0);
      }
   }
}

