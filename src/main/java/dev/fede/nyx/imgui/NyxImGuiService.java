package dev.fede.nyx.imgui;

import cn.enaium.fabric.imgui.DefaultImGui;
import imgui.ImFontConfig;
import imgui.ImFontGlyphRangesBuilder;
import imgui.ImGuiIO;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public final class NyxImGuiService extends DefaultImGui {
   public NyxImGuiService() {
      super(null);
   }

   public void configure(ImGuiIO var1) {
      var1.getFonts().addFontDefault();

      try {
         byte[] var2 = readResource("/assets/codeengine/fonts/Poppins-SemiBold.ttf");
         if (var2 != null && var2.length >= 16) {
            ImFontConfig var3 = new ImFontConfig();

            try {
               var3.setOversampleH(2);
               var3.setOversampleV(2);
               var3.setPixelSnapH(true);
               ImFontGlyphRangesBuilder var4 = new ImFontGlyphRangesBuilder();
               var4.addRanges(var1.getFonts().getGlyphRangesDefault());
               var4.addRanges(var1.getFonts().getGlyphRangesCyrillic());
               var4.addRanges(new short[]{8192, 8303, 0});
               short[] var5 = var4.buildRanges();
               ImGuiFonts.POPPINS = var1.getFonts().addFontFromMemoryTTF(var2, 16.0F, var3, var5);
            } finally {
               var3.destroy();
            }

            System.out.println("[ImGui] Poppins-SemiBold registered in atlas (" + var2.length + " bytes, Latin+Cyrillic+Punctuation)");
         } else {
            System.err.println("[ImGui] Poppins-SemiBold.ttf missing on classpath, arraylist will use default font");
         }
      } catch (Throwable var19) {
         ImGuiFonts.POPPINS = null;
         System.err.println("[ImGui] Poppins load failed: null");
      }

      try {
         byte[] var20 = readResource("/assets/codeengine/fonts/fa-solid-900.ttf");
         if (var20 != null && var20.length >= 16) {
            ImFontConfig var21 = new ImFontConfig();

            try {
               var21.setMergeMode(true);
               var21.setPixelSnapH(true);
               var21.setOversampleH(2);
               var21.setOversampleV(2);
               ImFontGlyphRangesBuilder var22 = new ImFontGlyphRangesBuilder();
               var22.addRanges(new short[]{-8192, -1793, 0});
               short[] var23 = var22.buildRanges();
               ImGuiFonts.FONTAWESOME = var1.getFonts().addFontFromMemoryTTF(var20, 16.0F, var21, var23);
            } finally {
               var21.destroy();
            }

            System.out.println("[ImGui] FontAwesome Free Solid merged into Poppins atlas (" + var20.length + " bytes)");
         } else {
            System.err.println("[ImGui] fa-solid-900.ttf missing on classpath, icon glyphs will render as boxes");
         }
      } catch (Throwable var18) {
         ImGuiFonts.FONTAWESOME = null;
         System.err.println("[ImGui] FontAwesome load failed: null");
      }

      var1.getFonts().build();
   }

   private static byte[] readResource(String path) throws Exception {
      byte[] var5;
      try (InputStream var1 = NyxImGuiService.class.getResourceAsStream(path)) {
         if (var1 == null) {
            return null;
         }

         ByteArrayOutputStream var2 = new ByteArrayOutputStream(Math.max(4096, var1.available()));
         byte[] var3 = new byte[8192];

         int var4;
         while ((var4 = var1.read(var3)) > 0) {
            var2.write(var3, 0, var4);
         }

         var5 = var2.toByteArray();
      }

      return var5;
   }
}

