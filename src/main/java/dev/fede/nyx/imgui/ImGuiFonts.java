package dev.fede.nyx.imgui;

import imgui.ImDrawList;
import imgui.ImFont;

public final class ImGuiFonts {
   public static ImFont POPPINS;
   public static ImFont FONTAWESOME;

   private ImGuiFonts() {
   }

   public static boolean tryEnsureLoaded() {
      return POPPINS != null;
   }

   public static void drawTextStroked(ImDrawList dl, ImFont font, float fontPx, float x, float y, int u32, String s) {
      if (dl != null && s != null && !s.isEmpty()) {
         int var7 = u32 >>> 24 & 0xFF;
         int var8 = var7 * 192 / 255;
         int var9 = (var8 & 0xFF) << 24;
         int var10 = (int)Math.max(1.0F, fontPx);
         if (font != null) {
            dl.addText(font, var10, x + 1.0F, y, var9, s);
            dl.addText(font, var10, x - 1.0F, y, var9, s);
            dl.addText(font, var10, x, y + 1.0F, var9, s);
            dl.addText(font, var10, x, y - 1.0F, var9, s);
            dl.addText(font, var10, x, y, u32, s);
         } else {
            dl.addText(x + 1.0F, y, var9, s);
            dl.addText(x - 1.0F, y, var9, s);
            dl.addText(x, y + 1.0F, var9, s);
            dl.addText(x, y - 1.0F, var9, s);
            dl.addText(x, y, u32, s);
         }
      }
   }
}

