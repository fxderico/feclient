package dev.fede.nyx.internal;

import dev.fede.nyx.util.CodeEngineScreenUtil2;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.StyleSpriteSource.Font;

public final class CodeEngineScreenUtil {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();

   private CodeEngineScreenUtil() {
   }

   public static void run(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > var1 && var4 > var2) {
         int var7 = Math.min(var6, Math.min((var3 - var1) / 2, (var4 - var2) / 2));
         if (var7 <= 0) {
            var0.fill(var1, var2, var3, var4, var5);
         } else {
            var0.fill(var1 + var7, var2, var3 - var7, var4, var5);
            var0.fill(var1, var2 + var7, var3, var4 - var7, var5);
            run3(var0, var1 + var7, var2 + var7, var7, -1, -1, var5);
            run3(var0, var3 - var7, var2 + var7, var7, 1, -1, var5);
            run3(var0, var1 + var7, var4 - var7, var7, -1, 1, var5);
            run3(var0, var3 - var7, var4 - var7, var7, 1, 1, var5);
         }
      }
   }

   public static void run2(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > var1 && var4 > var2) {
         int var7 = Math.min(var6, Math.min((var3 - var1) / 2, (var4 - var2) / 2));
         if (var7 <= 0) {
            var0.fill(var1, var2, var3, var2 + 1, var5);
            var0.fill(var1, var4 - 1, var3, var4, var5);
            var0.fill(var1, var2, var1 + 1, var4, var5);
            var0.fill(var3 - 1, var2, var3, var4, var5);
         } else {
            var0.fill(var1 + var7, var2, var3 - var7, var2 + 1, var5);
            var0.fill(var1 + var7, var4 - 1, var3 - var7, var4, var5);
            var0.fill(var1, var2 + var7, var1 + 1, var4 - var7, var5);
            var0.fill(var3 - 1, var2 + var7, var3, var4 - var7, var5);
            run4(var0, var1 + var7, var2 + var7, var7, -1, -1, var5);
            run4(var0, var3 - var7, var2 + var7, var7, 1, -1, var5);
            run4(var0, var1 + var7, var4 - var7, var7, -1, 1, var5);
            run4(var0, var3 - var7, var4 - var7, var7, 1, 1, var5);
         }
      }
   }

   private static void run3(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      double var7 = var3 + 0.5;
      double var9 = var7 * var7;

      for (int var11 = 0; var11 < var3; var11++) {
         double var12 = Math.sqrt(var9 - var11 * var11);
         int var14 = (int)Math.floor(var12);
         if (var14 > 0) {
            int var15 = var2 + var5 * var11 + (var5 < 0 ? -1 : 0);
            int var16 = var1 + (var4 < 0 ? -var14 : 0);
            int var17 = var1 + (var4 < 0 ? 0 : var14);
            var0.fill(var16, var15, var17, var15 + 1, var6);
         }
      }
   }

   private static void run4(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      double var7 = var3 + 0.5;
      double var9 = var7 * var7;
      double var11 = (var7 - 1.0) * (var7 - 1.0);

      for (int var13 = 0; var13 < var3; var13++) {
         double var14 = Math.sqrt(var9 - var13 * var13);
         double var16 = var13 < var7 - 1.0 ? Math.sqrt(Math.max(0.0, var11 - var13 * var13)) : 0.0;
         int var18 = (int)Math.floor(var14);
         int var19 = (int)Math.floor(var16);
         if (var18 > 0) {
            int var20 = var2 + var5 * var13 + (var5 < 0 ? -1 : 0);
            int var21 = var1 + (var4 < 0 ? -var18 : var19);
            int var22 = var1 + (var4 < 0 ? -var19 : var18);
            if (var22 > var21) {
               var0.fill(var21, var20, var22, var20 + 1, var6);
            }
         }
      }
   }

   public static void run5(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > var1 && var4 > var2) {
         int var7 = var4 - var2;
         if (var7 == 1) {
            var0.fill(var1, var2, var3, var4, var5);
         } else {
            float var8 = 1.0F / (var7 - 1);

            for (int var9 = var2; var9 < var4; var9++) {
               float var10 = (var9 - var2) * var8;
               int var11 = CodeEngineScreenUtil2.intOf2(var5, var6, var10);
               var0.fill(var1, var9, var3, var9 + 1, var11);
            }
         }
      }
   }

   public static void run6(DrawContext var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > var1 && var4 > var2) {
         int var7 = var3 - var1;
         if (var7 == 1) {
            var0.fill(var1, var2, var3, var4, var5);
         } else {
            float var8 = 1.0F / (var7 - 1);

            for (int var9 = var1; var9 < var3; var9++) {
               float var10 = (var9 - var1) * var8;
               int var11 = CodeEngineScreenUtil2.intOf2(var5, var6, var10);
               var0.fill(var9, var2, var9 + 1, var4, var11);
            }
         }
      }
   }

   public static void run7(DrawContext var0, int var1, int var2, int var3, int var4, int var5) {
      if (var3 > var1 && var4 > var2) {
         InternalUtil.run();
         int var6 = var3 - var1;
         int var7 = var4 - var2;
         var0.fill(var1, var2, var3, var4, var5);
         var0.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InternalUtil.class2960, 32, 32, 0, 0, var1, var2, var6, var7, var5);
      }
   }

   public static void run8(DrawContext var0, String var1, int var2, int var3, int var4, int var5) {
      if (var1 != null && !var1.isEmpty()) {
         TextRenderer var6 = class310.textRenderer;
         int var7 = var1.length();
         int var8 = var2;
         if (var7 == 1) {
            var0.drawTextWithShadow(var6, var1, var2, var3, var4);
         } else {
            float var9 = 1.0F / (var7 - 1);

            for (int var10 = 0; var10 < var7; var10++) {
               String var11 = String.valueOf(var1.charAt(var10));
               int var12 = CodeEngineScreenUtil2.intOf2(var4, var5, var10 * var9);
               var0.drawTextWithShadow(var6, var11, var8, var3, var12);
               var8 += var6.getWidth(var11);
            }
         }
      }
   }

   public static void run9(DrawContext var0, String var1, int var2, int var3, long var4, float var6) {
      if (var1 != null && !var1.isEmpty()) {
         TextRenderer var7 = class310.textRenderer;
         if (var4 <= 0L) {
            var4 = 4000L;
         }

         long var8 = System.currentTimeMillis();
         float var10 = (float)(var8 % var4) / (float)var4;
         float var11 = var6 / 360.0F;
         int var12 = var2;

         for (int var13 = 0; var13 < var1.length(); var13++) {
            String var14 = String.valueOf(var1.charAt(var13));
            float var15 = var10 + var13 * var11;
            var15 -= (float)Math.floor(var15);
            int var16 = 0xFF000000 | CodeEngineScreenUtil2.intOf5(var15, 1.0F, 1.0F) & 16777215;
            var0.drawTextWithShadow(var7, var14, var12, var3, var16);
            var12 += var7.getWidth(var14);
         }
      }
   }

   public static int intOf(DrawContext var0, String var1, int var2, int var3, int var4, int var5) {
      TextRenderer var6 = class310.textRenderer;
      int var7 = var6.getWidth(var1);
      Objects.requireNonNull(var6);
      int var13 = var2 + var7 + 6;
      int var14 = var3 + 9 + 2;
      run(var0, var2, var3, var13, var14, var4, 2);
      var0.drawTextWithShadow(var6, var1, var2 + 3, var3 + 1, var5);
      return var13 - var2;
   }

   public static void run10(DrawContext var0, String var1, int var2, int var3, int var4) {
      MutableText var5 = Text.literal(var1).styled(CodeEngineScreenUtil::addSetting);
      var0.drawTextWithShadow(class310.textRenderer, var5, var2, var3, var4);
   }

   public static int intOf2(String var0) {
      return class310.textRenderer.getWidth(var0);
   }

   public static int getInt() {
      return 9;
   }

   private static Style addSetting(Style var0) {
      return var0.withFont(new Font(HUDModuleData.class2960));
   }
}

