package dev.fede.nyx.module.modules.client;

import dev.fede.gui.ClickGuiScreen;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import net.minecraft.client.MinecraftClient;

/**
 * Dead code — this whole nyx.module.ModuleManager tree is never instantiated
 * (only dev.fede.module.ModuleManager is, from FeClient.onInitializeClient).
 * Kept compiling (rather than deleted) because two other equally-dead nyx
 * files (nyx/module/ModuleManager.java, nyx/screen/CodeEngineScreen.java)
 * still reference the ClickGUIModule *type*. Originally called
 * ImGuiClickGui.toggle() — that class is gone (see tonight's ImGui-shell
 * cleanup), so this now opens the real, live ClickGuiScreen instead, same as
 * the fix applied to dev.fede.module.Modules.ClickGuiModule.
 */
public class ClickGUIModule extends Module {
   public ClickGUIModule() {
      super("ClickGUI", "Opens the ImGui click GUI", Category.CLIENT, 0);
      this.run8(true);
   }

   @Override
   public void run() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc != null && mc.currentScreen == null) {
         mc.setScreen(new ClickGuiScreen());
      }
      this.run5(false);
   }
}
