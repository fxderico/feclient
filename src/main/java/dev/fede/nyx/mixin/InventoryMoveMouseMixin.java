package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.InventoryMoveModule;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Mouse.class})
public class InventoryMoveMouseMixin {
   @Redirect(
      method = {"tick()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/Mouse;isCursorLocked()Z"
      ),
      require = 0
   )
   private boolean nyx$maybeForceCursorLocked(Mouse var1) {
      boolean var2 = var1.isCursorLocked();
      return var2 ? true : InventoryMoveModule.isEnabled_s();
   }
}

