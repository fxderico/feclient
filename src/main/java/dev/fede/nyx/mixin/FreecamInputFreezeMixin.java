package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.FreecamModule;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({KeyboardInput.class})
public abstract class FreecamInputFreezeMixin extends Input {
   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void codeengine$freezeIfFreecam(CallbackInfo ci) {
      if (FreecamModule.bool) {
         this.playerInput = new PlayerInput(false, false, false, false, false, false, false);
         ci.cancel();
      }
   }
}

