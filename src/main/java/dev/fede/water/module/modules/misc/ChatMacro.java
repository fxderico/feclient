package dev.fede.water.module.modules.misc;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.glfw.GLFW;

public final class ChatMacro extends Module {
   private final Setting<String> Q = new Setting<>("Macro 1 Text", "");
   private final Setting<Integer> R = new Setting<>("Macro 1 Key", 0, 0, 348);
   private final Setting<String> S = new Setting<>("Macro 2 Text", "");
   private final Setting<Integer> T = new Setting<>("Macro 2 Key", 0, 0, 348);
   private final Setting<String> U = new Setting<>("Macro 3 Text", "");
   private final Setting<Integer> V = new Setting<>("Macro 3 Key", 0, 0, 348);
   private final Setting<String> W = new Setting<>("Macro 4 Text", "");
   private final Setting<Integer> X = new Setting<>("Macro 4 Key", 0, 0, 348);
   private final Setting<String> Y = new Setting<>("Macro 5 Text", "");
   private final Setting<Integer> Z = new Setting<>("Macro 5 Key", 0, 0, 348);
   private final Map<Integer, Boolean> i = new HashMap<>();

   public ChatMacro() {
      super("Chat Macro", Category.c);
      this.addSetting(this.Q);
      this.addSetting(this.R);
      this.addSetting(this.S);
      this.addSetting(this.T);
      this.addSetting(this.U);
      this.addSetting(this.V);
      this.addSetting(this.W);
      this.addSetting(this.X);
      this.addSetting(this.Y);
      this.addSetting(this.Z);
   }

   @Override
   public void onTick() {
      if (mc.player != null && mc.currentScreen == null) {
         this.b(this.R.getValue(), this.Q.getValue());
         this.b(this.T.getValue(), this.S.getValue());
         this.b(this.V.getValue(), this.U.getValue());
         this.b(this.X.getValue(), this.W.getValue());
         this.b(this.Z.getValue(), this.Y.getValue());
      }
   }

   private void b(int keyCode, String text) {
      if (keyCode > 0 && text != null && !text.isBlank()) {
         boolean var3 = this.b(keyCode);
         boolean var4 = this.i.getOrDefault(keyCode, false);
         if (var3 && !var4) {
            this.r(text.trim());
         }

         this.i.put(keyCode, var3);
      }
   }

   private boolean b(int keyCode) {
      if (mc.getWindow() == null) {
         return false;
      } else {
         try {
            return GLFW.glfwGetKey(mc.getWindow().getHandle(), keyCode) == 1;
         } catch (Exception var2) {
            return false;
         }
      }
   }

   private void r(String text) {
      mc.execute(() -> {
         if (mc.player != null && mc.getNetworkHandler() != null) {
            if (text.startsWith("/")) {
               mc.getNetworkHandler().sendChatCommand(text.substring(1));
            } else {
               mc.getNetworkHandler().sendChatMessage(text);
            }
         }
      });
   }
}

