package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.gui.ClickGuiScreen;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Keyboard.class})
public class KeyboardHandlerMixin {
   @Inject(
      method = {"method_1466(JILnet/minecraft/class_11908;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$dispatchModuleKeybinds(long window, int action, KeyInput keyEvent, CallbackInfo ci) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      if (FeClient.modules() == null) return;

      // PRESS (action=1): open or toggle the GUI
      if (action == 1) {
         if (minecraft.currentScreen == null && minecraft.world != null) {
            if (FeClient.modules().onKeyPressed(keyEvent.key())) {
               ci.cancel();
            }
         } else {
            if ((keyEvent.key() == 340 || keyEvent.key() == 344) && minecraft.currentScreen != null && shiftOpensGui(minecraft.currentScreen)) {
               minecraft.setScreen(new ClickGuiScreen(minecraft.currentScreen));
               ci.cancel();
            }
         }
      }

      // REPEAT (action=2): swallow repeat events for the GUI keybind so holding
      // the key doesn't immediately close the screen that was just opened on PRESS.
      if (action == 2) {
         if (minecraft.currentScreen instanceof ClickGuiScreen) {
            int key = keyEvent.key();
            if (key == 340 || key == 344 || (FeClient.modules() != null && FeClient.modules().clickGui.getKeybind().matches(key))) {
               ci.cancel();
            }
         }
      }
   }

   private static boolean shiftOpensGui(Screen screen) {
      return !(screen instanceof ClickGuiScreen)
            && !(screen instanceof ChatScreen)
            && !(screen instanceof HandledScreen)
            && !(screen instanceof AbstractSignEditScreen)
            && !(screen instanceof BookEditScreen)
         ? !(screen.getFocused() instanceof TextFieldWidget editBox && editBox.isFocused())
         : false;
   }
}



