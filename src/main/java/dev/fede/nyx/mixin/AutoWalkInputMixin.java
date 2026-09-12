package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.AutoWalkModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({KeyboardInput.class})
public abstract class AutoWalkInputMixin extends Input {
   @Inject(
      method = {"tick()V"},
      at = {@At("TAIL")}
   )
   private void nyx$autoWalk(CallbackInfo ci) {
      AutoWalkModule var2 = AutoWalkModule.autoWalkModule;
      if (var2 != null && var2.isEnabled3()) {
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3 == null || var3.currentScreen == null) {
            PlayerInput var4 = this.playerInput;
            if (var4 != null) {
               this.playerInput = new PlayerInput(true, false, var4.left(), var4.right(), var4.jump(), var4.sneak(), var2.isEnabled() || var4.sprint());
            }
         }
      }
   }
}

