package dev.fede.nyx.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class c$bUtils {
   private static final List<c$bUtils.Inner2> list = new ArrayList<>();
   private static final int intVal = 4096;
   private static volatile boolean bool = false;
   private static volatile long longVal = 0L;
   private static final long longVal2 = 500000000L;
   private static SimpleFramebuffer class6367;
   private static SimpleFramebuffer class63672;
   private static SimpleFramebuffer class63673;
   private static int intVal2 = -1;
   private static int intVal3 = -1;
   private static volatile boolean bool2 = false;
   private static volatile boolean bool3 = false;
   private static volatile boolean bool4 = false;
   private static final int intVal4 = 8388608;
   private static final BufferAllocator class9799 = BufferAllocator.fixedSized(8388608);
   private static final Immediate class4597class4598 = VertexConsumerProvider.immediate(class9799);
   private static volatile boolean bool5 = false;
   private static final double[] doubleArray = new double[]{0.0, 0.15, 0.4, 0.85};
   private static final int[] intArray = new int[]{224, 112, 40, 10};
   private static final int intVal5 = 2000;
   private static final int intVal6 = 5000;
   private static volatile int intVal7 = 0;
   private static long longVal3 = 0L;

   private c$bUtils() {
   }

   public static void run(BlockPos var0, int var1) {
      run2(var0, var1, 4);
   }

   public static void run2(BlockPos var0, int var1, int var2) {
      if (var0 != null) {
         if (list.size() < 4096) {
            int var3 = var2;
            if (var2 < 1) {
               var3 = 1;
            }

            if (var3 > 10) {
               var3 = 10;
            }

            list.add(new c$bUtils.Inner2(var0.getX(), var0.getY(), var0.getZ(), var1, var3));
         }
      }
   }

   public static void run5(boolean var0) {
      bool = var0;
   }

   public static void run3(MatrixStack var0, float var1, Camera var2) {
      try {
         run7();
         run11();
      } catch (Throwable var4) {
         run13(var4);
      }
   }

   public static void run4() {
      list.clear();
   }

   public static void run6() {
      list.clear();

      try {
         if (class6367 != null) {
            class6367.delete();
         }
      } catch (Throwable var3) {
      }

      try {
         if (class63672 != null) {
            class63672.delete();
         }
      } catch (Throwable var2) {
      }

      try {
         if (class63673 != null) {
            class63673.delete();
         }
      } catch (Throwable var1) {
      }

      class6367 = null;
      class63672 = null;
      class63673 = null;
      intVal2 = -1;
      intVal3 = -1;
   }

   private static void run7() {
      if (!bool3) {
         bool3 = true;

         try {
            bool2 = c$bUtils.Inner1.class1921 != null;
            if (!bool5) {
               bool5 = true;
               WorldRenderEvents.AFTER_ENTITIES.register(c$bUtils::run15);
            }
         } catch (Throwable var1) {
            bool2 = false;
            run13(var1);
         }
      }
   }

   private static void run19() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      Framebuffer var1 = var0.getFramebuffer();
      if (var1 != null) {
         int var2 = var1.textureWidth;
         int var3 = var1.textureHeight;
         if (var2 > 0 && var3 > 0) {
            if (var2 != intVal2 || var3 != intVal3 || class6367 == null || class63672 == null || class63673 == null) {
               try {
                  if (class6367 == null) {
                     class6367 = new SimpleFramebuffer("codeengine:bloom_mask", var2, var3, false);
                  } else {
                     class6367.resize(var2, var3);
                  }

                  if (class63672 == null) {
                     class63672 = new SimpleFramebuffer("codeengine:bloom_pingx", var2, var3, false);
                  } else {
                     class63672.resize(var2, var3);
                  }

                  if (class63673 == null) {
                     class63673 = new SimpleFramebuffer("codeengine:bloom_pingy", var2, var3, false);
                  } else {
                     class63673.resize(var2, var3);
                  }

                  intVal2 = var2;
                  intVal3 = var3;
               } catch (Throwable var5) {
                  bool2 = false;
                  run13(var5);
               }
            }
         }
      }
   }

   private static void run15(WorldRenderContext var0) {
      if (!list.isEmpty()) {
         int var1 = 0;

         try {
            if (bool2 && c$bUtils.Inner1.class1921 != null) {
               var1 = intOf(var0);
               return;
            }

            var1 = getInt();
         } catch (Throwable var6) {
            run13(var6);
            return;
         } finally {
            list.clear();
            if (bool) {
               intVal7 = var1;
            }
         }
      }
   }

   private static int intOf(WorldRenderContext var0) {
      int var1 = list.size();
      byte var2;
      if (var1 <= 2000) {
         var2 = 3;
      } else if (var1 <= 5000) {
         var2 = 2;
      } else {
         var2 = 1;
      }

      MatrixStack var3 = var0.matrices();
      Vec3d var4 = MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
      double var5 = var4.x;
      double var7 = var4.y;
      double var9 = var4.z;
      RenderLayer var11 = c$bUtils.Inner1.class1921;
      VertexConsumer var12 = class4597class4598.getBuffer(var11);
      Matrix4f var13 = var3.peek().getPositionMatrix();
      int var14 = 0;

      try {
         for (c$bUtils.Inner2 var16 : list) {
            run8(var12, var13, var16, var2, var5, var7, var9);
            var14++;
         }
      } finally {
         try {
            class4597class4598.draw(var11);
         } catch (IllegalArgumentException var24) {
            run12(var24);
         } catch (RuntimeException var25) {
            run12(var25);
         }
      }

      return var14;
   }

   private static int getInt() {
      int var0 = list.size();
      byte var1;
      if (var0 <= 2000) {
         var1 = 3;
      } else if (var0 <= 5000) {
         var1 = 2;
      } else {
         var1 = 1;
      }

      int var2 = 0;

      for (c$bUtils.Inner2 var4 : list) {
         run9(var4, var1);
         var2++;
      }

      return var2;
   }

   private static void run8(VertexConsumer var0, Matrix4f var1, c$bUtils.Inner2 var2, int var3, double var4, double var6, double var8) {
      double var10 = var2.intVal;
      double var12 = var2.intVal2;
      double var14 = var2.intVal3;
      double var16 = var10 + 1.0;
      double var18 = var12 + 1.0;
      double var20 = var14 + 1.0;
      int var22 = var2.intVal4 & 16777215;
      double var23 = (var2.intVal4 >>> 24 & 0xFF) / 128.0;
      if (var23 < 0.05) {
         var23 = 0.05;
      }

      if (var23 > 2.0) {
         var23 = 2.0;
      }

      double var25 = var2.intVal5 / 4.0;
      int var27 = var3;
      if (var3 > doubleArray.length - 1) {
         var27 = doubleArray.length - 1;
      }

      for (int var28 = var27; var28 >= 0; var28--) {
         double var29 = doubleArray[var28] * var25;
         int var31 = intOf4((int)Math.round(intArray[var28] * var23));
         if (var31 >= 2) {
            int var32 = intOf3(var31, var22);
            run10(var0, var1, var10 - var29, var12 - var29, var14 - var29, var16 + var29, var18 + var29, var20 + var29, var4, var6, var8, var32);
         }
      }
   }

   private static void run9(c$bUtils.Inner2 var0, int var1) {
      double var2 = var0.intVal;
      double var4 = var0.intVal2;
      double var6 = var0.intVal3;
      double var8 = var2 + 1.0;
      double var10 = var4 + 1.0;
      double var12 = var6 + 1.0;
      int var14 = var0.intVal4 & 16777215;
      double var15 = (var0.intVal4 >>> 24 & 0xFF) / 128.0;
      if (var15 < 0.05) {
         var15 = 0.05;
      }

      if (var15 > 2.0) {
         var15 = 2.0;
      }

      double var17 = var0.intVal5 / 4.0;
      int var19 = var1;
      if (var1 > doubleArray.length - 1) {
         var19 = doubleArray.length - 1;
      }

      for (int var20 = var19; var20 >= 0; var20--) {
         double var21 = doubleArray[var20] * var17;
         int var23 = intOf4((int)Math.round(intArray[var20] * var15));
         if (var23 >= 2) {
            Box var24 = new Box(var2 - var21, var4 - var21, var6 - var21, var8 + var21, var10 + var21, var12 + var21);
            ListUtils.run3(var24, intOf3(var23, var14), true);
         }
      }
   }

   private static void run10(
      VertexConsumer var0,
      Matrix4f var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      double var18,
      int var20
   ) {
      float var21 = (float)(var2 - var14);
      float var22 = (float)(var4 - var16);
      float var23 = (float)(var6 - var18);
      float var24 = (float)(var8 - var14);
      float var25 = (float)(var10 - var16);
      float var26 = (float)(var12 - var18);
      var0.vertex(var1, var21, var22, var26).color(var20);
      var0.vertex(var1, var24, var22, var26).color(var20);
      var0.vertex(var1, var24, var22, var23).color(var20);
      var0.vertex(var1, var21, var22, var23).color(var20);
      var0.vertex(var1, var21, var25, var23).color(var20);
      var0.vertex(var1, var24, var25, var23).color(var20);
      var0.vertex(var1, var24, var25, var26).color(var20);
      var0.vertex(var1, var21, var25, var26).color(var20);
      var0.vertex(var1, var21, var22, var23).color(var20);
      var0.vertex(var1, var24, var22, var23).color(var20);
      var0.vertex(var1, var24, var25, var23).color(var20);
      var0.vertex(var1, var21, var25, var23).color(var20);
      var0.vertex(var1, var21, var25, var26).color(var20);
      var0.vertex(var1, var24, var25, var26).color(var20);
      var0.vertex(var1, var24, var22, var26).color(var20);
      var0.vertex(var1, var21, var22, var26).color(var20);
      var0.vertex(var1, var21, var22, var26).color(var20);
      var0.vertex(var1, var21, var25, var26).color(var20);
      var0.vertex(var1, var21, var25, var23).color(var20);
      var0.vertex(var1, var21, var22, var23).color(var20);
      var0.vertex(var1, var24, var22, var23).color(var20);
      var0.vertex(var1, var24, var25, var23).color(var20);
      var0.vertex(var1, var24, var25, var26).color(var20);
      var0.vertex(var1, var24, var22, var26).color(var20);
   }

   private static void run11() {
      if (bool) {
         long var0 = System.nanoTime();
         if (var0 - longVal >= 500000000L) {
            longVal = var0;
            int var2 = intVal7;
         }
      }
   }

   private static int intOf4(int var0) {
      if (var0 < 0) {
         return 0;
      } else {
         return var0 > 255 ? 255 : var0;
      }
   }

   private static int intOf3(int var0, int var1) {
      return (var0 & 0xFF) << 24 | var1 & 16777215;
   }

   private static void run12(Throwable var0) {
      long var1 = System.currentTimeMillis();
      if (var1 - longVal3 >= 5000L) {
         longVal3 = var1;
      }
   }

   private static void run13(Throwable var0) {
      if (!bool4) {
         bool4 = true;
      }
   }

final static class Inner1 {
   static final RenderPipeline renderPipeline;
   static final RenderLayer class1921;

   private Inner1() {
   }

   static {
      RenderPipeline var0 = null;
      RenderLayer var1 = null;

      try {
         BlendFunction var2;
         try {
            var2 = BlendFunction.ADDITIVE;
         } catch (Throwable var4) {
            var2 = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         }

         var0 = RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
            .withLocation(Identifier.of("codeengine", "pipeline/light_bloom_glow"))
            .withBlend(var2)
            .withCull(false)
            .withDepthWrite(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build();
         RenderSetup var3 = RenderSetup.builder(var0).build();
         var1 = RenderLayer.of("codeengine_light_bloom_glow", var3);
      } catch (Throwable var5) {
      }

      renderPipeline = var0;
      class1921 = var1;
   }
}

final static class Inner2 {
   final int intVal;
   final int intVal2;
   final int intVal3;
   final int intVal4;
   final int intVal5;

   Inner2(int var1, int var2, int var3, int var4, int var5) {
      this.intVal = var1;
      this.intVal2 = var2;
      this.intVal3 = var3;
      this.intVal4 = var4;
      this.intVal5 = var5;
   }
}
}

