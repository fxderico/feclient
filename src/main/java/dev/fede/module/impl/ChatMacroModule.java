package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.KeybindSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

public class ChatMacroModule extends Module {
   private static final int SLOTS = 3;
   public final ModeSetting slot = this.addSetting(new ModeSetting("Slot", "Macro slot to edit.", "1", "1", "2", "3"));
   public final BooleanSetting sendInstantly = this.addSetting(new BooleanSetting("Send Instantly", "Skip the chat preview and send immediately.", true));
   private final StringSetting[] messages = new StringSetting[3];
   private final KeybindSetting[] keys = new KeybindSetting[3];

   public ChatMacroModule() {
      super("ChatMacro", "Bindable chat command macros.", Category.CLIENT);

      for (int i = 0; i < 3; i++) {
         String num = Integer.toString(i + 1);
         this.messages[i] = this.addSetting(new StringSetting("Message null", "Message or /command for slot null", "", 256, "/say hi"));
         this.keys[i] = this.addSetting(new KeybindSetting("Key null", "Key that runs slot null", -1));
         this.messages[i].visibleWhen(() -> this.slot.check(num));
         this.keys[i].visibleWhen(() -> this.slot.check(num));
      }
   }

   @Override
   public boolean onKeyPress(int keyCode) {
      boolean handled = false;

      for (int i = 0; i < 3; i++) {
         if (this.keys[i].matches(keyCode)) {
            this.run(this.messages[i].get());
            handled = true;
         }
      }

      return handled;
   }

   private void run(String message) {
      if (message != null && !message.isBlank()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.player != null && mc.player.networkHandler != null) {
            if (!this.sendInstantly.get()) {
               mc.setScreen(new ChatScreen(message, false));
            } else {
               if (message.startsWith("/")) {
                  mc.player.networkHandler.sendChatCommand(message.substring(1));
               } else {
                  mc.player.networkHandler.sendChatMessage(message);
               }
            }
         }
      }
   }
}

