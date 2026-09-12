package dev.fede.nyx.imgui;

import cn.enaium.fabric.imgui.FabricImGui;
import imgui.ImGui;
import imgui.ImGuiIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class InputCaptureScreen extends Screen {
   public InputCaptureScreen() {
      super(Text.literal(""));
   }

   public boolean shouldPause() {
      return false;
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      try {
         FabricImGui.IMGUI.draw(var5 -> {
            ImGuiHud.render();
            ImGuiClickGui.render();
         });
      } catch (Throwable var6) {
      }
   }

   public void renderBackground(DrawContext var1, int var2, int var3, float var4) {
   }

   protected void applyBlur(DrawContext var1) {
   }

   protected void renderDarkening(DrawContext var1) {
   }

   public void blur() {
   }

   private static float toFbX(double x) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      double var3 = var2 != null && var2.getWindow() != null ? var2.getWindow().getScaleFactor() : 1.0;
      return (float)(x * var3);
   }

   private static float toFbY(double y) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      double var3 = var2 != null && var2.getWindow() != null ? var2.getWindow().getScaleFactor() : 1.0;
      return (float)(y * var3);
   }

   public boolean mouseClicked(Click var1, boolean var2) {
      ImGuiIO var3 = ImGui.getIO();
      var3.addMousePosEvent(toFbX(var1.x()), toFbY(var1.y()));
      var3.addMouseButtonEvent(var1.button(), true);
      return true;
   }

   public boolean mouseReleased(Click var1) {
      ImGuiIO var2 = ImGui.getIO();
      var2.addMousePosEvent(toFbX(var1.x()), toFbY(var1.y()));
      var2.addMouseButtonEvent(var1.button(), false);
      return true;
   }

   public boolean mouseDragged(Click var1, double var2, double var4) {
      ImGui.getIO().addMousePosEvent(toFbX(var1.x()), toFbY(var1.y()));
      return true;
   }

   public void mouseMoved(double x, double y) {
      ImGui.getIO().addMousePosEvent(toFbX(x), toFbY(y));
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      ImGui.getIO().addMouseWheelEvent((float)horizontalAmount, (float)verticalAmount);
      return true;
   }

   public boolean charTyped(CharInput var1) {
      try {
         int var2 = var1.codepoint();
         ImGui.getIO().addInputCharacter(var2);
         return true;
      } catch (Throwable var3) {
         return super.charTyped(var1);
      }
   }

   public boolean keyPressed(KeyInput var1) {
      int var2 = var1.key();
      if (var2 == 256) {
         if (ImGuiClickGui.isCapturingBind()) {
            return true;
         } else {
            ImGuiClickGui.toggle();
            return true;
         }
      } else if (var2 == 344) {
         if (ImGuiClickGui.isCapturingBind()) {
            return true;
         } else {
            ImGuiClickGui.toggle();
            return true;
         }
      } else {
         return super.keyPressed(var1);
      }
   }
}

