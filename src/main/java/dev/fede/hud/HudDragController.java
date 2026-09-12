package dev.fede.hud;

import dev.fede.render.OverlayRenderer;
import dev.fede.render.nanovg.NVGRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

public final class HudDragController {
   private static HudComponent dragging;
   private static float grabDx;
   private static float grabDy;

   private HudDragController() {
   }

   public static boolean isEditing() {
      return MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
   }

   public static boolean isDragging() {
      return dragging != null;
   }

   public static boolean tryStartDrag(HudManager hud) {
      NVGRenderer vg = NVGRenderer.get();
      float mx = OverlayRenderer.uiMouseX();
      float my = OverlayRenderer.uiMouseY();

      for (HudManager.Placement p : hud.layout(vg, OverlayRenderer.uiWidth(), OverlayRenderer.uiHeight(), true)) {
         if (p.contains(mx, my)) {
            float scale = p.component().getScale();
            if (p.component().onEditClick((mx - p.getFloat()) / scale, (my - p.getFloat2()) / scale)) {
               return true;
            }

            dragging = p.component();
            grabDx = mx - p.getFloat();
            grabDy = my - p.getFloat2();
            return true;
         }
      }

      return false;
   }

   public static boolean tryResize(HudManager hud, double scrollY) {
      NVGRenderer vg = NVGRenderer.get();
      float mx = OverlayRenderer.uiMouseX();
      float my = OverlayRenderer.uiMouseY();

      for (HudManager.Placement p : hud.layout(vg, OverlayRenderer.uiWidth(), OverlayRenderer.uiHeight(), true)) {
         if (p.contains(mx, my)) {
            HudComponent component = p.component();
            component.setScale(component.getScale() + (float)scrollY * 0.06F);
            return true;
         }
      }

      return false;
   }

   public static void updateDrag(NVGRenderer vg) {
      if (dragging != null) {
         float uiWidth = OverlayRenderer.uiWidth();
         float uiHeight = OverlayRenderer.uiHeight();
         float scale = dragging.getScale();
         float w = dragging.measureWidth(vg) * scale;
         float h = dragging.measureHeight(vg) * scale;
         float freeW = Math.max(1.0F, uiWidth - w);
         float freeH = Math.max(1.0F, uiHeight - h);
         dragging.setPosition((OverlayRenderer.uiMouseX() - grabDx) / freeW, (OverlayRenderer.uiMouseY() - grabDy) / freeH);
      }
   }

   public static void stopDrag() {
      dragging = null;
   }

   public static HudComponent draggedComponent() {
      return dragging;
   }
}

