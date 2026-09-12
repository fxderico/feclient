package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.InventoryMoveModule;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({KeyBinding.class})
public class InventoryMoveKeyBindingMixin {
   @Inject(
      method = {"isPressed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$forceMovementPressed(CallbackInfoReturnable<Boolean> cir) {
      KeyBinding var2 = (KeyBinding)(Object)this;
      if (InventoryMoveModule.check(var2)) {
         cir.setReturnValue(true);
      }
   }
}

