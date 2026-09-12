package dev.fede.nyx.internal.util;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public final class ThrowableUtils {
   private static final int intVal = 12;
   private static final int intVal2 = 24;
   private static final float floatVal = 0.5F;
   private static final int intVal3 = 32;
   private static volatile boolean bool = false;

   private ThrowableUtils() {
   }

   public static void run(DrawContext var0, float var1, float var2, float var3, float var4, float var5, int var6) {
      if (!(var3 <= var1) && !(var4 <= var2)) {
         RenderPipeline var7 = ThrowableUtils.Inner2.renderPipeline;
         if (var7 == null) {
            var0.fill((int)var1, (int)var2, (int)var3, (int)var4, var6);
         } else {
            float var8 = floatOf(var5, var1, var2, var3, var4);
            if (var8 <= 0.0F) {
               var0.fill((int)var1, (int)var2, (int)var3, (int)var4, var6);
            } else {
               float[] var10 = new float[324];
               int var11 = 0;
               var11 = intOf(var10, var11, var1, var2 + var8, var3, var4 - var8);
               var11 = intOf(var10, var11, var1 + var8, var2, var3 - var8, var2 + var8);
               var11 = intOf(var10, var11, var1 + var8, var4 - var8, var3 - var8, var4);
               var11 = intOf2(var10, var11, var1 + var8, var2 + var8, var8, (float) Math.PI, (float) (Math.PI * 3.0 / 2.0));
               var11 = intOf2(var10, var11, var3 - var8, var2 + var8, var8, (float) (Math.PI * 3.0 / 2.0), (float) (Math.PI * 2));
               var11 = intOf2(var10, var11, var3 - var8, var4 - var8, var8, 0.0F, (float) (Math.PI / 2));
               var11 = intOf2(var10, var11, var1 + var8, var4 - var8, var8, (float) (Math.PI / 2), (float) Math.PI);
               run6(
                  var0,
                  var7,
                  var10,
                  var11,
                  var6,
                  (int)Math.floor(var1),
                  (int)Math.floor(var2),
                  (int)Math.ceil(var3 - var1) + 1,
                  (int)Math.ceil(var4 - var2) + 1
               );
            }
         }
      }
   }

   public static void run2(DrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      if (!(var3 <= var1) && !(var4 <= var2) && !(var6 <= 0.0F)) {
         RenderPipeline var8 = ThrowableUtils.Inner2.renderPipeline;
         if (var8 == null) {
            int var14 = (int)var1;
            int var15 = (int)var2;
            int var16 = (int)var3;
            int var17 = (int)var4;
            int var26 = Math.max(1, (int)var6);
            var0.fill(var14, var15, var16, var15 + var26, var7);
            var0.fill(var14, var17 - var26, var16, var17, var7);
            var0.fill(var14, var15 + var26, var14 + var26, var17 - var26, var7);
            var0.fill(var16 - var26, var15 + var26, var16, var17 - var26, var7);
         } else {
            float var9 = floatOf(var5, var1, var2, var3, var4);
            float var10 = Math.max(var9 - var6, 0.0F);
            byte var11 = 104;
            float[] var12 = new float[624];
            int var13 = 0;
            var13 = intOf(var12, var13, var1 + var9, var2, var3 - var9, var2 + var6);
            var13 = intOf(var12, var13, var1 + var9, var4 - var6, var3 - var9, var4);
            var13 = intOf(var12, var13, var1, var2 + var9, var1 + var6, var4 - var9);
            var13 = intOf(var12, var13, var3 - var6, var2 + var9, var3, var4 - var9);
            var13 = intOf3(var12, var13, var1 + var9, var2 + var9, var10, var9, (float) Math.PI, (float) (Math.PI * 3.0 / 2.0));
            var13 = intOf3(var12, var13, var3 - var9, var2 + var9, var10, var9, (float) (Math.PI * 3.0 / 2.0), (float) (Math.PI * 2));
            var13 = intOf3(var12, var13, var3 - var9, var4 - var9, var10, var9, 0.0F, (float) (Math.PI / 2));
            var13 = intOf3(var12, var13, var1 + var9, var4 - var9, var10, var9, (float) (Math.PI / 2), (float) Math.PI);
            run6(var0, var8, var12, var13, var7, (int)Math.floor(var1), (int)Math.floor(var2), (int)Math.ceil(var3 - var1) + 1, (int)Math.ceil(var4 - var2) + 1);
         }
      }
   }

   public static void run3(DrawContext var0, float var1, float var2, float var3, float var4, float var5, int var6, float var7, float var8) {
      if (!(var3 <= var1) && !(var4 <= var2) && !(var7 <= 0.0F) && !(var8 <= 0.0F)) {
         RenderPipeline var9 = ThrowableUtils.Inner1.renderPipeline;
         boolean var10 = var9 != null;
         if (!var10) {
            var9 = ThrowableUtils.Inner2.renderPipeline;
            if (var9 == null) {
               return;
            }
         }

         int var11 = var6 & 16777215;
         float var12 = var7 / 24.0F;
         float var13 = var10 ? 1.0F : 1.6F;
         float[] var15 = new float[324];

         for (int var16 = 0; var16 < 24; var16++) {
            float var17 = (var16 + 1) * var12;
            float var18 = var1 - var17;
            float var19 = var2 - var17;
            float var20 = var3 + var17;
            float var21 = var4 + var17;
            float var22 = var5 + var17;
            var22 = floatOf(var22, var18, var19, var20, var21);
            if (!(var22 <= 0.0F)) {
               float var23 = var16 / 24.0F;
               float var24 = (float)Math.exp(-4.0 * var23 * var23);
               int var25 = intOf4(Math.round(255.0F * var24 * var8 * var13));
               if (var25 > 0) {
                  int var26 = var25 << 24 | var11;
                  int var27 = 0;
                  var27 = intOf(var15, var27, var18, var19 + var22, var20, var21 - var22);
                  var27 = intOf(var15, var27, var18 + var22, var19, var20 - var22, var19 + var22);
                  var27 = intOf(var15, var27, var18 + var22, var21 - var22, var20 - var22, var21);
                  var27 = intOf2(var15, var27, var18 + var22, var19 + var22, var22, (float) Math.PI, (float) (Math.PI * 3.0 / 2.0));
                  var27 = intOf2(var15, var27, var20 - var22, var19 + var22, var22, (float) (Math.PI * 3.0 / 2.0), (float) (Math.PI * 2));
                  var27 = intOf2(var15, var27, var20 - var22, var21 - var22, var22, 0.0F, (float) (Math.PI / 2));
                  var27 = intOf2(var15, var27, var18 + var22, var21 - var22, var22, (float) (Math.PI / 2), (float) Math.PI);
                  run6(
                     var0,
                     var9,
                     var15,
                     var27,
                     var26,
                     (int)Math.floor(var18),
                     (int)Math.floor(var19),
                     (int)Math.ceil(var20 - var18) + 1,
                     (int)Math.ceil(var21 - var19) + 1
                  );
               }
            }
         }
      }
   }

   public static void run4(DrawContext var0, float var1, float var2, float var3, float var4, int var5, int var6) {
      if (!(var3 <= var1) && !(var4 <= var2)) {
         RenderPipeline var7 = ThrowableUtils.Inner2.renderPipeline;
         if (var7 == null) {
            var0.fill((int)var1, (int)var2, (int)var3, (int)var4, var5);
         } else {
            Matrix3x2fc var8 = matrix3x2fcOf(var0);
            ScreenRect var9 = new ScreenRect((int)Math.floor(var1), (int)Math.floor(var2), (int)Math.ceil(var3 - var1) + 1, (int)Math.ceil(var4 - var2) + 1);
            float[] var10 = new float[]{var1, var2, var1, var4, var3, var4, var1, var2, var3, var4, var3, var2};
            int[] var11 = new int[]{var5, var6, var6, var5, var6, var5};
            run7(var0, new ThrowableUtils.Inner3(var7, var8, var10, var11, var9));
         }
      }
   }

   public static void run5(DrawContext var0, float var1, float var2, float var3, int var4) {
      if (!(var3 <= 0.0F)) {
         RenderPipeline var5 = ThrowableUtils.Inner2.renderPipeline;
         if (var5 == null) {
            int var18 = (int)Math.floor(var1 - var3);
            int var19 = (int)Math.floor(var2 - var3);
            int var25 = (int)Math.ceil(var1 + var3);
            int var26 = (int)Math.ceil(var2 + var3);
            var0.fill(var18, var19, var25, var26, var4);
         } else {
            byte var6 = 32;
            float[] var7 = new float[192];
            int var8 = 0;
            double var9 = Math.PI / 16;
            float var11 = var1 + var3;
            float var12 = var2;

            for (int var13 = 1; var13 <= 32; var13++) {
               double var14 = var13 * var9;
               float var16 = var1 + (float)(Math.cos(var14) * var3);
               float var17 = var2 + (float)(Math.sin(var14) * var3);
               var7[var8++] = var1;
               var7[var8++] = var2;
               var7[var8++] = var11;
               var7[var8++] = var12;
               var7[var8++] = var16;
               var7[var8++] = var17;
               var11 = var16;
               var12 = var17;
            }

            run6(
               var0,
               var5,
               var7,
               var8,
               var4,
               (int)Math.floor(var1 - var3),
               (int)Math.floor(var2 - var3),
               (int)Math.ceil(var3 * 2.0F) + 1,
               (int)Math.ceil(var3 * 2.0F) + 1
            );
         }
      }
   }

   private static int intOf(float[] var0, int var1, float var2, float var3, float var4, float var5) {
      var0[var1++] = var2;
      var0[var1++] = var3;
      var0[var1++] = var2;
      var0[var1++] = var5;
      var0[var1++] = var4;
      var0[var1++] = var5;
      var0[var1++] = var2;
      var0[var1++] = var3;
      var0[var1++] = var4;
      var0[var1++] = var5;
      var0[var1++] = var4;
      var0[var1++] = var3;
      return var1;
   }

   private static int intOf2(float[] var0, int var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = (var6 - var5) / 12.0F;
      float var8 = var2 + (float)(Math.cos(var5) * var4);
      float var9 = var3 + (float)(Math.sin(var5) * var4);

      for (int var10 = 1; var10 <= 12; var10++) {
         float var11 = var5 + var7 * var10;
         float var12 = var2 + (float)(Math.cos(var11) * var4);
         float var13 = var3 + (float)(Math.sin(var11) * var4);
         var0[var1++] = var2;
         var0[var1++] = var3;
         var0[var1++] = var8;
         var0[var1++] = var9;
         var0[var1++] = var12;
         var0[var1++] = var13;
         var8 = var12;
         var9 = var13;
      }

      return var1;
   }

   private static int intOf3(float[] var0, int var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = (var7 - var6) / 12.0F;
      float var9 = var2 + (float)(Math.cos(var6) * var4);
      float var10 = var3 + (float)(Math.sin(var6) * var4);
      float var11 = var2 + (float)(Math.cos(var6) * var5);
      float var12 = var3 + (float)(Math.sin(var6) * var5);

      for (int var13 = 1; var13 <= 12; var13++) {
         float var14 = var6 + var8 * var13;
         float var15 = var2 + (float)(Math.cos(var14) * var4);
         float var16 = var3 + (float)(Math.sin(var14) * var4);
         float var17 = var2 + (float)(Math.cos(var14) * var5);
         float var18 = var3 + (float)(Math.sin(var14) * var5);
         var0[var1++] = var9;
         var0[var1++] = var10;
         var0[var1++] = var11;
         var0[var1++] = var12;
         var0[var1++] = var17;
         var0[var1++] = var18;
         var0[var1++] = var9;
         var0[var1++] = var10;
         var0[var1++] = var17;
         var0[var1++] = var18;
         var0[var1++] = var15;
         var0[var1++] = var16;
         var9 = var15;
         var10 = var16;
         var11 = var17;
         var12 = var18;
      }

      return var1;
   }

   private static void run6(DrawContext var0, RenderPipeline var1, float[] var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (var3 >= 6) {
         float[] var9 = new float[var3];
         System.arraycopy(var2, 0, var9, 0, var3);
         Matrix3x2fc var10 = matrix3x2fcOf(var0);
         ScreenRect var11 = new ScreenRect(var5, var6, var7, var8);
         run7(var0, new ThrowableUtils.Inner4(var1, var10, var9, var4, var11));
      }
   }

   private static void run7(DrawContext var0, SimpleGuiElementRenderState var1) {
      var0.state.addSimpleElement(var1);
   }

   private static Matrix3x2fc matrix3x2fcOf(DrawContext var0) {
      return new Matrix3x2f(var0.getMatrices());
   }

   private static float floatOf(float var0, float var1, float var2, float var3, float var4) {
      float var5 = (var3 - var1) * 0.5F;
      float var6 = (var4 - var2) * 0.5F;
      float var7 = Math.min(var5, var6);
      if (var0 > var7) {
         var0 = var7;
      }

      if (var0 < 0.0F) {
         var0 = 0.0F;
      }

      return var0;
   }

   private static int intOf4(int var0) {
      if (var0 < 0) {
         return 0;
      } else {
         return var0 > 255 ? 255 : var0;
      }
   }

   private static void run8(String var0, Throwable var1) {
      if (!bool) {
         bool = true;
         System.err
            .println(
               "[HudGfx] pipeline init FAILED for "
                  + var0
                  + " ("
                  + var1.getClass().getSimpleName()
                  + ": "
                  + var1.getMessage()
                  + ") — falling back to DrawContext.fill (no smooth corners / no bloom)."
            );
         var1.printStackTrace();
      }
   }

final static class Inner1 {
   static final RenderPipeline renderPipeline;

   private Inner1() {
   }

   static {
      RenderPipeline var0 = null;

      try {
         var0 = RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
            .withLocation(Identifier.of("codeengine", "pipeline/hud_additive"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLES)
            .withBlend(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .build();
      } catch (Throwable var2) {
         ThrowableUtils.run8("codeengine:pipeline/hud_additive", var2);
      }

      renderPipeline = var0;
      if (var0 != null) {
         System.out.println("[HudGfx] additive pipeline built: codeengine:pipeline/hud_additive (POSITION_COLOR + TRIANGLES, SRC_ALPHA/ONE additive)");
      }
   }
}

final static class Inner2 {
   static final RenderPipeline renderPipeline;

   private Inner2() {
   }

   static {
      RenderPipeline var0 = null;

      try {
         var0 = RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
            .withLocation(Identifier.of("codeengine", "pipeline/hud_alpha"))
            .withVertexFormat(VertexFormats.POSITION_COLOR, DrawMode.TRIANGLES)
            .withBlend(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .build();
      } catch (Throwable var2) {
         ThrowableUtils.run8("codeengine:pipeline/hud_alpha", var2);
      }

      renderPipeline = var0;
      if (var0 != null) {
         System.out.println("[HudGfx] alpha pipeline built: codeengine:pipeline/hud_alpha (POSITION_COLOR + TRIANGLES, alpha-over blend)");
      }
   }
}

record Inner3(RenderPipeline pipeline, Matrix3x2fc pose, float[] floatArray, int[] cols, ScreenRect bounds) implements SimpleGuiElementRenderState {
   public TextureSetup textureSetup() { return TextureSetup.empty(); }
   public ScreenRect scissorArea() { return this.bounds(); }


   public void setupVertices(VertexConsumer var1) {
      int var2 = this.floatArray.length / 2;
      for (int var3 = 0; var3 < var2; var3++) {
         var1.vertex(this.pose, this.floatArray[var3 * 2], this.floatArray[var3 * 2 + 1]).color(this.cols[var3]);
      }
   }

   public TextureSetup getclass11231() {
      return TextureSetup.empty();
   }

   public ScreenRect getclass8030() {
      return null;
   }

   public float[] getFloatArray() {
      return this.floatArray;
   }
}

record Inner4(RenderPipeline pipeline, Matrix3x2fc pose, float[] floatArray, int argb, ScreenRect bounds) implements SimpleGuiElementRenderState {
   public TextureSetup textureSetup() { return TextureSetup.empty(); }
   public ScreenRect scissorArea() { return this.bounds(); }


   public void setupVertices(VertexConsumer var1) {
      for (byte var2 = 0; var2 < this.floatArray.length; var2 += 2) {
         var1.vertex(this.pose, this.floatArray[var2], this.floatArray[var2 + 1]).color(this.argb);
      }
   }

   public TextureSetup getclass11231() {
      return TextureSetup.empty();
   }

   public ScreenRect getclass8030() {
      return null;
   }

   public float[] getFloatArray() {
      return this.floatArray;
   }
}
}

