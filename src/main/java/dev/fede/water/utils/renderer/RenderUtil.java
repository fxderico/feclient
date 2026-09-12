package dev.fede.water.utils.renderer;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderPass;
import dev.fede.water.utils.renderer.arc.ArcOutlinePipeline;
import dev.fede.water.utils.renderer.arc.ArcPipeline;
import dev.fede.water.utils.renderer.blur.KawasePipeline;
import dev.fede.water.utils.renderer.glass.LiquidGlassPipeline;
import dev.fede.water.utils.renderer.outline.OutlinePipeline;
import dev.fede.water.utils.renderer.rect.RectPipeline;
import dev.fede.water.utils.renderer.texture.TexturePipeline;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
   private static final List<Runnable> OVERRIDE_TASKS = new ArrayList<>();
   private static final float Z_OVERRIDE = 0.0F;
   private static final int FIXED_GUI_SCALE = 1;
   private static boolean scissorActive = false;
   private static int scissorX;
   private static int scissorY;
   private static int scissorWidth;
   private static int scissorHeight;

   public static int getFixedScaledWidth() {
      Window var0 = MinecraftClient.getInstance().getWindow();
      return (int)Math.ceil(var0.getWidth() / 1.0);
   }

   public static int getFixedScaledHeight() {
      Window var0 = MinecraftClient.getInstance().getWindow();
      return (int)Math.ceil(var0.getHeight() / 1.0);
   }

   public static float getScaleFactor() {
      Window var0 = MinecraftClient.getInstance().getWindow();
      int var1 = var0.getScaleFactor();
      return var1 / 1.0F;
   }

   public static float convertX(float var0) {
      return var0 * getScaleFactor();
   }

   public static float convertY(float var0) {
      return var0 * getScaleFactor();
   }

   public static float convertSize(float var0) {
      return var0 * getScaleFactor();
   }

   public static Matrix4f createProjection() {
      return new Matrix4f().ortho(0.0F, getFixedScaledWidth(), getFixedScaledHeight(), 0.0F, -1000.0F, 1000.0F);
   }

   public static Matrix4f createProjection(DrawContext var0) {
      Window var1 = MinecraftClient.getInstance().getWindow();
      Matrix3x2fStack var2 = var0.getMatrices();
      return new Matrix4f()
         .ortho(0.0F, var1.getScaledWidth(), var1.getScaledHeight(), 0.0F, -1000.0F, 1000.0F)
         .mul(new Matrix4f(var2.m00, var2.m01, 0.0F, 0.0F, var2.m10, var2.m11, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, var2.m20, var2.m21, 0.0F, 1.0F));
   }

   public static boolean hasScreenOpen() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0.currentScreen != null && !(var0.currentScreen instanceof ChatScreen);
   }

   public static void drawRoundedRect(DrawContext var0, float var1, float var2, float var3, float var4, float var5, int var6, boolean var7) {
      drawRoundedRect(createProjection(var0), var1, var2, var3, var4, var5, var6, var7);
   }

   public static void drawRoundedRect(DrawContext var0, float var1, float var2, float var3, float var4, float var5, boolean var6, int... var7) {
      drawRoundedRect(createProjection(var0), var1, var2, var3, var4, var5, var5, var5, var5, var6, var7);
   }

   public static void drawRoundedRect(
      DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, boolean var9, int... var10
   ) {
      drawRoundedRect(createProjection(var0), var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static void drawRoundedRect(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, int var6, boolean var7) {
      drawRoundedRect(var0, var1, var2, var3, var4, var5, var5, var5, var5, var7, var6);
   }

   public static void drawRoundedRect(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, boolean var9, int... var10
   ) {
      if (var9) {
         OVERRIDE_TASKS.add(() -> RectPipeline.draw(var0, var1, var2, var3, var4, var5, var6, var7, var8, 0.0F, var10));
      } else {
         RectPipeline.draw(var0, var1, var2, var3, var4, var5, var6, var7, var8, 0.0F, var10);
      }
   }

   public static void drawRoundedRect(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, boolean var6, int... var7) {
      drawRoundedRect(var0, var1, var2, var3, var4, var5, var5, var5, var5, var6, var7);
   }

   public static void drawOutline(DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7, boolean var8) {
      drawOutline(createProjection(var0), var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8);
   }

   public static void drawOutline(
      DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, boolean var11
   ) {
      drawOutline(createProjection(var0), var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void drawOutline(DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, int... var8) {
      drawOutline(createProjection(var0), var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8);
   }

   public static void drawOutline(
      DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, boolean var10, int... var11
   ) {
      drawOutline(createProjection(var0), var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void drawOutline(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7, boolean var8) {
      drawOutline(var0, var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8);
   }

   public static void drawOutline(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, boolean var11
   ) {
      if (var11) {
         OVERRIDE_TASKS.add(() -> OutlinePipeline.draw(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, 0.0F, var10));
      } else {
         OutlinePipeline.draw(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, 0.0F, var10);
      }
   }

   public static void drawOutline(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, int... var8) {
      drawOutline(var0, var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8);
   }

   public static void drawOutline(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, boolean var10, int... var11
   ) {
      if (var10) {
         OVERRIDE_TASKS.add(() -> OutlinePipeline.draw(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, 0.0F, var11));
      } else {
         OutlinePipeline.draw(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, 0.0F, var11);
      }
   }

   public static void drawBlur(DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, boolean var7) {
      Matrix4f var8 = createProjection(var0);
      if (var7) {
         OVERRIDE_TASKS.add(() -> KawasePipeline.draw(var8, var1, var2, var3, var4, var5, var6, 0.0F));
      } else {
         KawasePipeline.draw(var8, var1, var2, var3, var4, var5, var6, 0.0F);
      }
   }

   public static void drawArc(DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7, boolean var8) {
      drawArc(createProjection(var0), var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void drawArc(DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, int... var8) {
      drawArc(createProjection(var0), var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void drawArc(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7, boolean var8) {
      if (var8) {
         OVERRIDE_TASKS.add(() -> ArcPipeline.draw(var0, var1, var2, var3, var4, var5, var6, 0.0F, var7));
      } else {
         ArcPipeline.draw(var0, var1, var2, var3, var4, var5, var6, 0.0F, var7);
      }
   }

   public static void drawArc(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, boolean var7, int... var8) {
      if (var7) {
         OVERRIDE_TASKS.add(() -> ArcPipeline.draw(var0, var1, var2, var3, var4, var5, var6, 0.0F, var8));
      } else {
         ArcPipeline.draw(var0, var1, var2, var3, var4, var5, var6, 0.0F, var8);
      }
   }

   public static void arcOutline(
      DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, boolean var10
   ) {
      arcOutline(createProjection(var0), var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static void arcOutline(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, boolean var10
   ) {
      if (var10) {
         OVERRIDE_TASKS.add(() -> ArcOutlinePipeline.draw(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, 0.0F));
      } else {
         ArcOutlinePipeline.draw(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, 0.0F);
      }
   }

   public static void drawTexture(DrawContext var0, float var1, float var2, float var3, Identifier var4, int var5, float var6, boolean var7) {
      drawTexture(createProjection(var0), var1, var2, var3, var4, var5, var6, var7);
   }

   public static void drawTexture(Matrix4f var0, float var1, float var2, float var3, Identifier var4, int var5, float var6, boolean var7) {
      MinecraftClient var8 = MinecraftClient.getInstance();
      AbstractTexture var9 = var8.getTextureManager().getTexture(var4);
      if (var9 != null) {
         if (var7) {
            OVERRIDE_TASKS.add(() -> TexturePipeline.draw(var0, var1, var2, var3, var9.getGlTextureView(), var5, var6, 0.0F));
            return;
         }

         TexturePipeline.draw(var0, var1, var2, var3, var9.getGlTextureView(), var5, var6, 0.0F);
      }
   }

   public static void drawLiquidGlass(
      DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, boolean var9
   ) {
      Matrix4f var10 = createProjection(var0);
      float[] var11 = new float[]{var7 * var5 / 2.0F, var7 * var5 / 2.0F, var7 * var5 / 2.0F, var7 * var5 / 2.0F};
      float var12 = (var8 >> 24 & 0xFF) / 255.0F;
      float var13 = var4 == 240.0F ? 100.0F : 50.0F;
      int var14 = var8 | 0xFF000000;
      float var15 = 1.0F;
      boolean var16 = true;
      float var17 = 0.0F;
      if (var9) {
         OVERRIDE_TASKS.add(
            () -> LiquidGlassPipeline.draw(var10, var1, var2, var3, var4, var11, var8, var12, var13, var14, var15, var16, var17, var6, var5, 0.0F)
         );
      } else {
         LiquidGlassPipeline.draw(var10, var1, var2, var3, var4, var11, var8, var12, var13, var14, var15, var16, var17, var6, var5, 0.0F);
      }
   }

   public static void addOverrideTask(Runnable var0) {
      OVERRIDE_TASKS.add(var0);
   }

   public static void renderOverrides(DrawContext var0) {
      if (!OVERRIDE_TASKS.isEmpty()) {
         GL11.glDisable(2929);
         OVERRIDE_TASKS.forEach(Runnable::run);
         OVERRIDE_TASKS.clear();
         GL11.glEnable(2929);
         scissorActive = false;
         GlStateManager._disableScissorTest();
      }
   }

   public static void clearOverrideTasks() {
      OVERRIDE_TASKS.clear();
   }

   public static boolean hasOverrideTasks() {
      return !OVERRIDE_TASKS.isEmpty();
   }

   public static void setScissor(float var0, float var1, float var2, float var3, boolean var4) {
      if (var4) {
         OVERRIDE_TASKS.add(() -> setScissorTasks(var0, var1, var2, var3));
      } else {
         setScissorTasks(var0, var1, var2, var3);
      }
   }

   private static void setScissorTasks(float var0, float var1, float var2, float var3) {
      Window var4 = MinecraftClient.getInstance().getWindow();
      double var5 = var4.getScaleFactor();
      int var7 = var4.getFramebufferWidth();
      int var8 = var4.getFramebufferHeight();
      scissorActive = true;
      scissorX = (int)var0;
      scissorY = (int)var1;
      scissorWidth = (int)var2;
      scissorHeight = (int)var3;
      int var9 = (int)Math.round(scissorX * var5);
      int var10 = (int)Math.round(scissorY * var5);
      int var11 = (int)Math.round(scissorWidth * var5);
      int var12 = (int)Math.round(scissorHeight * var5);
      int var14 = var8 - (var10 + var12);
      int var17 = Math.max(0, var9);
      int var18 = Math.max(0, var14);
      int var19 = Math.min(var7, var9 + var11);
      int var20 = Math.min(var8, var14 + var12);
      int var21 = Math.max(0, var19 - var17);
      int var22 = Math.max(0, var20 - var18);
      if (var21 != 0 && var22 != 0) {
         GlStateManager._enableScissorTest();
         GlStateManager._scissorBox(var17, var18, var21, var22);
      } else {
         scissorActive = false;
         GlStateManager._disableScissorTest();
      }
   }

   public static void applyScissor(RenderPass var0) {
      if (scissorActive) {
         var0.enableScissor(scissorX, scissorY, scissorX + scissorWidth, scissorY + scissorHeight);
      }
   }

   public static void clearScissor(boolean var0) {
      if (var0) {
         OVERRIDE_TASKS.add(() -> {
            scissorActive = false;
            GlStateManager._disableScissorTest();
         });
      } else {
         scissorActive = false;
         GlStateManager._disableScissorTest();
      }
   }

   public static boolean isOverrideActive() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return var0.currentScreen == null || var0.currentScreen instanceof ChatScreen;
   }

   public static void unscaledProjection(DrawContext var0) {
      Window var1 = MinecraftClient.getInstance().getWindow();
      double var2 = var1.getScaleFactor();
      var0.getMatrices().scale((float)(1.0 / var2), (float)(1.0 / var2));
   }

   public static void scaledProjection(DrawContext var0) {
      Window var1 = MinecraftClient.getInstance().getWindow();
      double var2 = var1.getScaleFactor();
      var0.getMatrices().scale((float)var2, (float)var2);
   }

   public static int multiplyColor(int var0, float var1) {
      int var2 = var0 & 0xFF000000;
      float var3 = (var0 >> 16 & 0xFF) / 255.0F;
      float var4 = (var0 >> 8 & 0xFF) / 255.0F;
      float var5 = (var0 & 0xFF) / 255.0F;
      var3 = Math.min(var3 * var1, 1.0F);
      var4 = Math.min(var4 * var1, 1.0F);
      var5 = Math.min(var5 * var1, 1.0F);
      int var6 = (int)(var3 * 255.0F);
      int var7 = (int)(var4 * 255.0F);
      int var8 = (int)(var5 * 255.0F);
      return var2 | var6 << 16 | var7 << 8 | var8;
   }

   public static int interpolateColor(int var0, int var1, float var2) {
      int var3 = var0 >> 24 & 0xFF;
      int var4 = var0 >> 16 & 0xFF;
      int var5 = var0 >> 8 & 0xFF;
      int var6 = var0 & 0xFF;
      int var7 = var1 >> 24 & 0xFF;
      int var8 = var1 >> 16 & 0xFF;
      int var9 = var1 >> 8 & 0xFF;
      int var10 = var1 & 0xFF;
      int var11 = (int)(var3 + (var7 - var3) * var2);
      int var12 = (int)(var4 + (var8 - var4) * var2);
      int var13 = (int)(var5 + (var9 - var5) * var2);
      int var14 = (int)(var6 + (var10 - var6) * var2);
      return var11 << 24 | var12 << 16 | var13 << 8 | var14;
   }
}

