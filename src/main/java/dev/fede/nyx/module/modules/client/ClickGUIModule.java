package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.imgui.ImGuiClickGui;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import net.minecraft.client.MinecraftClient;

public class ClickGUIModule extends Module {
   public ClickGUIModule() {
      super("ClickGUI", "Opens the ImGui click GUI", Category.CLIENT, 0);
      this.run8(true);
   }

   @Override
   public void run() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && var1.mouse != null && var1.currentScreen == null) {
         ImGuiClickGui.toggle();
         this.run5(false);
      } else {
         this.run5(false);
      }
   }
}

