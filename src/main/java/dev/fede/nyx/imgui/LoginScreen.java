package dev.fede.nyx.imgui;

import cn.enaium.fabric.imgui.FabricImGui;
import dev.fede.nyx.auth.AuthState;
import imgui.ImGui;
import imgui.ImGuiIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public final class LoginScreen extends Screen {
   private final Screen previous;

   public LoginScreen(Screen var1) {
      super(Text.literal("Code Engine — License"));
      this.previous = var1;
      LoginOverlay.reset();
   }

   public boolean shouldPause() {
      return true;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void tick() {
      if (AuthState.isEnabled()) {
         this.restorePrevious();
      }
   }

   private void restorePrevious() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null) {
         try {
            if (!(this.previous instanceof LoginScreen) && this.previous != this) {
               var1.setScreen(this.previous);
            } else {
               var1.setScreen(new TitleScreen());
            }
         } catch (Throwable var5) {
            try {
               var1.setScreen(null);
            } catch (Throwable var4) {
            }
         }
      }
   }

   public void render(DrawContext var1, int var2, int var3, float var4) {
      MinecraftClient var5 = MinecraftClient.getInstance();

      try {
         FabricImGui.IMGUI.draw(var1x -> {
            try {
               LoginOverlay.render(var5);
            } catch (Throwable var3x) {
               System.err.println("[Auth] login overlay draw failed: null");
            }
         });
      } catch (Throwable var7) {
         System.err.println("[Auth] FabricImGui.draw threw: null");
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
      return true;
   }

   public boolean keyPressed(KeyInput var1) {
      int var2 = var1.key();
      return var2 == 256 ? true : true;
   }
}

