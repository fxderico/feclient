package dev.fede.water.module.modules.misc;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

public final class HomeSetter extends Module {
   private final Setting<Boolean> aK = new Setting<>("Chat Feedback", true);
   private final Setting<Float> aL = new Setting<>("Home Slot", 1.0F, 1.0F, 5.0F);
   private volatile boolean af = false;

   public HomeSetter() {
      super("HomeSetter", Category.c);
      this.addSetting(this.aK);
      this.addSetting(this.aL);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (!this.af) {
         if (mc != null && mc.player != null && mc.world != null) {
            this.af = true;
            int var1 = Math.round(this.aL.getValue());
            mc.execute(() -> {
               this.o("/delhome " + var1);
               new Thread(() -> {
                  try {
                     Thread.sleep(750L);
                  } catch (InterruptedException var2) {
                  }

                  mc.execute(() -> {
                     this.o("/sethome " + var1);
                     if (this.aK.getValue()) {
                        try {
                           mc.inGameHud.getChatHud().addMessage(Text.literal("§aHome " + var1 + " deleted and set successfully!"));
                        } catch (Exception var2x) {
                        }
                     }

                     this.af = false;
                     this.toggle();
                  });
               }, "HomeSetter-DelayThread").start();
            });
         } else {
            this.toggle();
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.af = false;
   }

   private void o(String command) {
      if (mc != null) {
         ClientPlayNetworkHandler var2 = null;

         try {
            if (mc.player != null) {
               var2 = mc.player.networkHandler;
            }
         } catch (Throwable var7) {
         }

         if (var2 == null) {
            try {
               var2 = mc.getNetworkHandler();
            } catch (Throwable var6) {
            }
         }

         if (var2 != null) {
            String var3 = command.startsWith("/") ? command.substring(1) : command;

            try {
               var2.sendChatCommand(var3);
            } catch (Throwable var5) {
               try {
                  var2.sendChatMessage(command);
               } catch (Throwable var4) {
               }
            }
         }
      }
   }
}

