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
      if (screen instanceof ClickGuiScreen
            || screen instanceof ChatScreen
            || screen instanceof HandledScreen
            || screen instanceof AbstractSignEditScreen
            || screen instanceof BookEditScreen) {
         return false;
      }
      // don't hijack shift while the user is typing — text fields are frequently
      // nested inside container widgets (server-add, search boxes, config panels),
      // so getFocused() one level down isn't enough. walk the whole focus chain.
      return !isTypingInTextField(screen);
   }

   private static boolean isTypingInTextField(Screen screen) {
      net.minecraft.client.gui.Element focused = screen.getFocused();
      int guard = 0;
      while (focused != null && guard++ < 32) {
         if (focused instanceof TextFieldWidget field && field.isFocused()) {
            return true;
         }
         if (focused instanceof net.minecraft.client.gui.widget.EditBoxWidget box && box.isFocused()) {
            return true;
         }
         if (focused instanceof net.minecraft.client.gui.ParentElement parent) {
            focused = parent.getFocused();
         } else {
            break;
         }
      }
      return false;
   }
}



