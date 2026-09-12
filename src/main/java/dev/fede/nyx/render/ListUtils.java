package dev.fede.nyx.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.util.Identifier;

import dev.fede.nyx.util.Matrix4fUtils;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Matrix4f;

public final class ListUtils {
   private static final int intVal = 16777216;
   private static final int intVal2 = 8388608;
   private static final BufferAllocator class9799 = BufferAllocator.fixedSized(16777216);
   private static final BufferAllocator class97992 = BufferAllocator.fixedSized(8388608);
   private static final Immediate class4597class4598 = VertexConsumerProvider.immediate(class9799);
   private static final Immediate class4597class45982 = VertexConsumerProvider.immediate(class97992);
   private static final String string = "custom NO_DEPTH_TEST pipeline (via AccessWidener)";
   private static volatile boolean bool = false;
   private static volatile boolean bool2 = false;
   private static final int intVal3 = 4194304;
   private static final BufferAllocator class97993 = BufferAllocator.fixedSized(4194304);
   private static final Immediate class4597class45983 = VertexConsumerProvider.immediate(class97993);
   private static volatile boolean bool3 = false;
   private static volatile boolean bool4 = false;
   private static final int intVal4 = 4194304;
   private static final BufferAllocator class97994 = BufferAllocator.fixedSized(4194304);
   private static final Immediate class4597class45984 = VertexConsumerProvider.immediate(class97994);
   private static volatile boolean bool5 = false;
   private static volatile boolean bool6 = false;
   private static final List<ListUtils.Inner1> list = new ArrayList<>();
   private static final List<ListUtils.Inner1> list2 = new ArrayList<>();
   private static final List<ListUtils.Inner8> list3 = new ArrayList<>();
   private static final List<ListUtils.Inner8> list4 = new ArrayList<>();
   private static final List<ListUtils.Inner2> list5 = new ArrayList<>();
   private static final List<ListUtils.Inner2> list6 = new ArrayList<>();
   private static final List<ListUtils.Inner7> list7 = new ArrayList<>();
   private static final List<ListUtils.Inner7> list8 = new ArrayList<>();
   private static final List<ListUtils.Inner3> list9 = new ArrayList<>();
   private static final List<ListUtils.Inner3> list10 = new ArrayList<>();
   private static final List<ListUtils.Inner4> list11 = new ArrayList<>();
   private static final List<ListUtils.Inner4> list12 = new ArrayList<>();
   private static final List<ListUtils.Inner12> list13 = new ArrayList<>();
   private static final List<ListUtils.Inner12> list14 = new ArrayList<>();
   private static final List<ListUtils.Inner11> list15 = new ArrayList<>();
   private static long longVal = 0L;

   private static void run(Throwable var0) {
      if (!bool2) {
         bool2 = true;
         System.err
            .println(
               "[EspBoxRenderer] TW pipeline init FAILED ("
                  + var0.getClass().getSimpleName()
                  + ": "
                  + var0.getMessage()
                  + ") — falling back to depth-tested LINES for through-walls submissions."
            );
         var0.printStackTrace();
      }
   }

   private static void run8(String var0, Throwable var1) {
      if (!bool4) {
         bool4 = true;
         System.err
            .println(
               "[EspBoxRenderer] fell back to LINES pipeline: smooth-lines "
                  + var0
                  + " init failed ("
                  + var1.getClass().getSimpleName()
                  + ": "
                  + var1.getMessage()
                  + ")"
            );
         var1.printStackTrace();
      }
   }

   private static void run2(String var0, Throwable var1) {
      if (!bool6) {
         bool6 = true;
         System.err
            .println(
               "[EspBoxRenderer] filled-quads pipeline init FAILED ("
                  + var0
                  + ": "
                  + var1.getClass().getSimpleName()
                  + ": "
                  + var1.getMessage()
                  + ") — falling back to debugQuads for filled-box submissions (TW filled boxes will occlude in fallback)."
            );
         var1.printStackTrace();
      }
   }

   private ListUtils() {
   }

   public static boolean isEnabled() {
      return ListUtils.Inner13.class1921 != null;
   }

   public static boolean isEnabled2() {
      return ListUtils.Inner9.class1921 != null && ListUtils.Inner10.class1921 != null;
   }

   public static boolean isEnabled8() {
      return ListUtils.Inner5.class1921 != null && ListUtils.Inner6.class1921 != null;
   }

   public static void run3(Box var0, int var1, boolean var2) {
      if (var0 != null) {
         (var2 ? list12 : list11).add(new ListUtils.Inner4(var0, var1));
      }
   }

   public static void run4(Vec3d var0, Vec3d var1, Vec3d var2, int var3, boolean var4) {
      if (var0 != null && var1 != null && var2 != null) {
         (var4 ? list14 : list13).add(new ListUtils.Inner12(var0, var1, var2, var3));
      }
   }

   public static void run5(Box var0, int var1, float var2, boolean var3) {
      if (var0 != null) {
         (var3 ? list2 : list).add(new ListUtils.Inner1(var0, var1, var2, var3));
      }
   }

   public static void run6(Box var0, int var1, float var2, float var3, boolean var4) {
      if (var0 != null) {
         float var5 = var3;
         if (var3 < 0.0F) {
            var5 = 0.0F;
         }

         if (var5 > 0.5F) {
            var5 = 0.5F;
         }

         if (var5 != 0.0F) {
            (var4 ? list10 : list9).add(new ListUtils.Inner3(var0, var1, var2, var5, var4));
         }
      }
   }

   public static void run7(Vec3d var0, double var1, int var3, float var4, int var5) {
      run9(var0, var1, var3, var4, var5, false);
   }

   public static void run9(Vec3d var0, double var1, int var3, float var4, int var5, boolean var6) {
      if (var0 != null && !(var1 <= 0.0) && var5 >= 3) {
         (var6 ? list6 : list5).add(new ListUtils.Inner2(var0, var1, var3, var4, var5));
      }
   }

   public static void run10(Vec3d var0, double var1, double var3, long var5, double var7, int var9, float var10, int var11, boolean var12) {
      if (var0 != null && !(var7 <= 0.0) && var11 >= 3) {
         double var13;
         if (var5 <= 0L) {
            var13 = var1;
         } else {
            long var15 = System.nanoTime() / 1000000L % var5;
            double var17 = (double)var15 / var5 * 2.0 * Math.PI;
            var13 = var1 + Math.sin(var17) * var3;
         }

         Vec3d var19 = new Vec3d(var0.x, var0.y + var13, var0.z);
         run9(var19, var7, var9, var10, var11, var12);
      }
   }

   public static void run11(Vec3d var0, Vec3d var1, int var2, float var3) {
      run12(var0, var1, var2, var3, false);
   }

   public static void run12(Vec3d var0, Vec3d var1, int var2, float var3, boolean var4) {
      if (var0 != null && var1 != null) {
         (var4 ? list8 : list7).add(new ListUtils.Inner7(var0, var1, var2, var3));
      }
   }

   public static void run13(double var0, double var2, double var4, double var6, double var8, int var10) {
      run14(var0, var2, var4, var6, var8, var10, false);
   }

   public static void run14(double var0, double var2, double var4, double var6, double var8, int var10, boolean var11) {
      (var11 ? list4 : list3).add(new ListUtils.Inner8(var0, var2, var4, var6, var8, var10));
   }

   public static void run15(WorldRenderContext var0) {
      if (!list.isEmpty()
         || !list2.isEmpty()
         || !list3.isEmpty()
         || !list4.isEmpty()
         || !list5.isEmpty()
         || !list6.isEmpty()
         || !list7.isEmpty()
         || !list8.isEmpty()
         || !list9.isEmpty()
         || !list10.isEmpty()
         || !list11.isEmpty()
         || !list12.isEmpty()
         || !list13.isEmpty()
         || !list14.isEmpty()) {
         MatrixStack var1 = var0.matrices();
         Vec3d var2 = MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
         double var3 = var2.x;
         double var5 = var2.y;
         double var7 = var2.z;
         int var10 = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
         double var11 = ((Integer)MinecraftClient.getInstance().options.getFov().getValue()).intValue();
         float var9 = var10 > 0 ? (float)(2.0 * Math.tan(Math.toRadians(var11) / 2.0) / var10) : 0.002F;

         try {
            try {
               boolean var47 = !list2.isEmpty() || !list10.isEmpty() || !list6.isEmpty();
               if (var47) {
                  RenderLayer var48 = ListUtils.Inner13.class1921;
                  boolean var12 = var48 != null;
                  if (!var12) {
                     var48 = RenderLayers.LINES;
                  }

                  if (!bool) {
                     bool = true;
                     System.out
                        .println(
                           "[EspBoxRenderer] through-walls ACTIVE via "
                              + (var12 ? "custom NO_DEPTH_TEST pipeline (via AccessWidener)" : "FALLBACK depth-tested LINES (init failed)")
                        );
                  }

                  VertexConsumer var13 = class4597class4598.getBuffer(var48);
                  run16(var13, var1, list2, list10, list6, var3, var5, var7);
                  class4597class4598.draw(var48);
               }

               if (!list8.isEmpty()) {
                  RenderLayer var49 = ListUtils.Inner10.class1921;
                  if (var49 != null) {
                     run19(true);
                     VertexConsumer var55 = class4597class45983.getBuffer(var49);
                     run17(var55, var1, list8, var3, var5, var7, var9);
                     class4597class45983.draw(var49);
                  } else {
                     run19(false);
                     RenderLayer var56 = ListUtils.Inner13.class1921 != null ? ListUtils.Inner13.class1921 : RenderLayers.LINES;
                     VertexConsumer var62 = class4597class4598.getBuffer(var56);
                     run18(var62, var1, list8, var3, var5, var7);
                     class4597class4598.draw(var56);
                  }
               }

               if (!list.isEmpty() || !list9.isEmpty() || !list5.isEmpty()) {
                  VertexConsumer var50 = class4597class4598.getBuffer(RenderLayers.LINES);
                  run16(var50, var1, list, list9, list5, var3, var5, var7);
               }

               if (!list7.isEmpty()) {
                  RenderLayer var51 = ListUtils.Inner9.class1921;
                  if (var51 != null) {
                     run19(true);
                     VertexConsumer var57 = class4597class45983.getBuffer(var51);
                     run17(var57, var1, list7, var3, var5, var7, var9);
                  } else {
                     run19(false);
                     VertexConsumer var58 = class4597class4598.getBuffer(RenderLayers.LINES);
                     run18(var58, var1, list7, var3, var5, var7);
                  }
               }

               if (!list3.isEmpty() || !list4.isEmpty()) {
                  VertexConsumer var52 = class4597class45982.getBuffer(RenderLayers.debugQuads());
                  Matrix4f var59 = var1.peek().getPositionMatrix();

                  for (ListUtils.Inner8 var14 : list4) {
                     run23(var52, var59, var14, var3, var5, var7);
                  }

                  for (ListUtils.Inner8 var67 : list3) {
                     run23(var52, var59, var67, var3, var5, var7);
                  }
               }

               if (!list12.isEmpty() || !list14.isEmpty()) {
                  RenderLayer var53 = ListUtils.Inner6.class1921;
                  boolean var60 = var53 != null;
                  if (!var60) {
                     var53 = RenderLayers.debugQuads();
                  }

                  run22(var60);
                  VertexConsumer var65 = class4597class45984.getBuffer(var53);
                  Matrix4f var68 = var1.peek().getPositionMatrix();

                  for (ListUtils.Inner4 var16 : list12) {
                     run20(var65, var68, var16, var3, var5, var7);
                  }

                  for (ListUtils.Inner12 var73 : list14) {
                     run21(var65, var68, var73, var3, var5, var7);
                  }

                  class4597class45984.draw(var53);
               }

               if (!list11.isEmpty() || !list13.isEmpty()) {
                  RenderLayer var54 = ListUtils.Inner5.class1921;
                  boolean var61 = var54 != null;
                  if (!var61) {
                     var54 = RenderLayers.debugQuads();
                  }

                  run22(var61);
                  VertexConsumer var66 = class4597class45984.getBuffer(var54);
                  Matrix4f var69 = var1.peek().getPositionMatrix();

                  for (ListUtils.Inner4 var74 : list11) {
                     run20(var66, var69, var74, var3, var5, var7);
                  }

                  for (ListUtils.Inner12 var75 : list13) {
                     run21(var66, var69, var75, var3, var5, var7);
                  }
               }
            } finally {
               try {
                  class4597class4598.draw();
               } catch (IllegalArgumentException var43) {
                  run24("LINE_BUFFER", var43);
               } catch (RuntimeException var44) {
                  run24("LINE_BUFFER", var44);
               }

               try {
                  class4597class45983.draw();
               } catch (IllegalArgumentException var41) {
                  run24("SMOOTH_LINE_BUFFER", var41);
               } catch (RuntimeException var42) {
                  run24("SMOOTH_LINE_BUFFER", var42);
               }

               try {
                  class4597class45982.draw();
               } catch (IllegalArgumentException var39) {
                  run24("QUAD_BUFFER", var39);
               } catch (RuntimeException var40) {
                  run24("QUAD_BUFFER", var40);
               }

               try {
                  class4597class45984.draw();
               } catch (IllegalArgumentException var37) {
                  run24("FILLED_QUAD_BUFFER", var37);
               } catch (RuntimeException var38) {
                  run24("FILLED_QUAD_BUFFER", var38);
               }

               list.clear();
               list2.clear();
               list3.clear();
               list4.clear();
               list5.clear();
               list6.clear();
               list7.clear();
               list8.clear();
               list9.clear();
               list10.clear();
               list11.clear();
               list12.clear();
               list13.clear();
               list14.clear();
            }
         } catch (RuntimeException var46) {
            run24("renderFrame (outer)", var46);
         }
      }
   }

   private static void run16(
      VertexConsumer var0,
      MatrixStack var1,
      List<ListUtils.Inner1> var2,
      List<ListUtils.Inner3> var3,
      List<ListUtils.Inner2> var4,
      double var5,
      double var7,
      double var9
   ) {
      for (ListUtils.Inner1 var12 : var2) {
         VoxelShape var13 = VoxelShapes.cuboid(var12.box);
         VertexRendering.drawOutline(var1, var0, var13, -var5, -var7, -var9, var12.color, var12.lineWidth);
      }

      if (!var3.isEmpty() || !var4.isEmpty()) {
         Entry var35 = var1.peek();
         Matrix4f var36 = var35.getPositionMatrix();

         for (ListUtils.Inner3 var14 : var3) {
            run27(var0, var36, var35, var14, var5, var7, var9);
         }

         for (ListUtils.Inner2 var39 : var4) {
            float var15 = (float)(var39.center.x - var5);
            float var16 = (float)(var39.center.y - var7);
            float var17 = (float)(var39.center.z - var9);
            int var18 = var39.segments;
            double var19 = var39.radius;
            double var21 = (Math.PI * 2) / var18;
            float var23 = var15 + (float)var19;
            float var24 = var17;

            for (int var25 = 0; var25 < var18; var25++) {
               double var26 = (var25 + 1) * var21;
               float var28 = var15 + (float)(Math.cos(var26) * var19);
               float var29 = var17 + (float)(Math.sin(var26) * var19);
               float var30 = var28 - var23;
               float var31 = var29 - var24;
               float var32 = (float)Math.sqrt(var30 * var30 + var31 * var31);
               float var33 = var32 > 1.0E-6F ? var30 / var32 : 1.0F;
               float var34 = var32 > 1.0E-6F ? var31 / var32 : 0.0F;
               var0.vertex(var36, var23, var16, var24).color(var39.color).normal(var35, var33, 0.0F, var34).lineWidth(var39.lineWidth);
               var0.vertex(var36, var28, var16, var29).color(var39.color).normal(var35, var33, 0.0F, var34).lineWidth(var39.lineWidth);
               var23 = var28;
               var24 = var29;
            }
         }
      }
   }

   private static void run17(VertexConsumer var0, MatrixStack var1, List<ListUtils.Inner7> var2, double var3, double var5, double var7, float var9) {
      if (!var2.isEmpty()) {
         Matrix4f var10 = var1.peek().getPositionMatrix();

         for (ListUtils.Inner7 var12 : var2) {
            float var13 = (float)(var12.class243.x - var3);
            float var14 = (float)(var12.class243.y - var5);
            float var15 = (float)(var12.class243.z - var7);
            float var16 = (float)(var12.class2432.x - var3);
            float var17 = (float)(var12.class2432.y - var5);
            float var18 = (float)(var12.class2432.z - var7);
            float var19 = var16 - var13;
            float var20 = var17 - var14;
            float var21 = var18 - var15;
            float var22 = (float)Math.sqrt(var19 * var19 + var20 * var20 + var21 * var21);
            if (!(var22 <= 1.0E-6F)) {
               float var23 = var19 / var22;
               float var24 = var20 / var22;
               float var25 = var21 / var22;
               float var26 = (var13 + var16) * 0.5F;
               float var27 = (var14 + var17) * 0.5F;
               float var28 = (var15 + var18) * 0.5F;
               float var29 = (float)Math.sqrt(var26 * var26 + var27 * var27 + var28 * var28);
               float var30;
               float var31;
               float var32;
               if (var29 <= 1.0E-6F) {
                  var30 = 0.0F;
                  var31 = 1.0F;
                  var32 = 0.0F;
               } else {
                  var30 = -var26 / var29;
                  var31 = -var27 / var29;
                  var32 = -var28 / var29;
               }

               float var33 = var24 * var32 - var25 * var31;
               float var34 = var25 * var30 - var23 * var32;
               float var35 = var23 * var31 - var24 * var30;
               float var36 = (float)Math.sqrt(var33 * var33 + var34 * var34 + var35 * var35);
               if (var36 <= 1.0E-4F) {
                  var33 = var24 * 0.0F - var25 * 1.0F;
                  var34 = var25 * 0.0F - var23 * 0.0F;
                  var35 = var23 * 1.0F - var24 * 0.0F;
                  var36 = (float)Math.sqrt(var33 * var33 + var34 * var34 + var35 * var35);
                  if (var36 <= 1.0E-4F) {
                     continue;
                  }
               }

               var33 /= var36;
               var34 /= var36;
               var35 /= var36;
               float var37 = (float)Math.sqrt(var13 * var13 + var14 * var14 + var15 * var15);
               float var38 = (float)Math.sqrt(var16 * var16 + var17 * var17 + var18 * var18);
               float var39 = 0.5F * var12.lineWidth * var37 * var9;
               float var40 = 0.5F * var12.lineWidth * var38 * var9;
               if (var39 < 1.0E-4F) {
                  var39 = 1.0E-4F;
               }

               if (var40 < 1.0E-4F) {
                  var40 = 1.0E-4F;
               }

               float var41 = var33 * var39;
               float var42 = var34 * var39;
               float var43 = var35 * var39;
               float var44 = var33 * var40;
               float var45 = var34 * var40;
               float var46 = var35 * var40;
               int var47 = var12.color;
               var0.vertex(var10, var13 - var41, var14 - var42, var15 - var43).color(var47);
               var0.vertex(var10, var13 + var41, var14 + var42, var15 + var43).color(var47);
               var0.vertex(var10, var16 + var44, var17 + var45, var18 + var46).color(var47);
               var0.vertex(var10, var16 - var44, var17 - var45, var18 - var46).color(var47);
            }
         }
      }
   }

   private static void run18(VertexConsumer var0, MatrixStack var1, List<ListUtils.Inner7> var2, double var3, double var5, double var7) {
      if (!var2.isEmpty()) {
         Entry var9 = var1.peek();
         Matrix4f var10 = var9.getPositionMatrix();

         for (ListUtils.Inner7 var12 : var2) {
            float var13 = (float)(var12.class243.x - var3);
            float var14 = (float)(var12.class243.y - var5);
            float var15 = (float)(var12.class243.z - var7);
            float var16 = (float)(var12.class2432.x - var3);
            float var17 = (float)(var12.class2432.y - var5);
            float var18 = (float)(var12.class2432.z - var7);
            float var19 = var16 - var13;
            float var20 = var17 - var14;
            float var21 = var18 - var15;
            float var22 = (float)Math.sqrt(var19 * var19 + var20 * var20 + var21 * var21);
            if (!(var22 <= 1.0E-6F)) {
               float var23 = var19 / var22;
               float var24 = var20 / var22;
               float var25 = var21 / var22;
               var0.vertex(var10, var13, var14, var15).color(var12.color).normal(var9, var23, var24, var25).lineWidth(var12.lineWidth);
               var0.vertex(var10, var16, var17, var18).color(var12.color).normal(var9, var23, var24, var25).lineWidth(var12.lineWidth);
            }
         }
      }
   }

   private static void run19(boolean var0) {
      if (!bool3) {
         bool3 = true;
         System.out
            .println(
               "[EspBoxRenderer] smooth-lines ACTIVE via "
                  + (
                     var0
                        ? "custom POSITION_COLOR + QUADS pipeline (billboard-quad tubes, MSAA-aware)"
                        : "FALLBACK vanilla LINES pipeline (smooth-line init failed)"
                  )
            );
      }
   }

   private static void run20(VertexConsumer var0, Matrix4f var1, ListUtils.Inner4 var2, double var3, double var5, double var7) {
      Box var9 = var2.box;
      float var10 = (float)(var9.minX - var3);
      float var11 = (float)(var9.minY - var5);
      float var12 = (float)(var9.minZ - var7);
      float var13 = (float)(var9.maxX - var3);
      float var14 = (float)(var9.maxY - var5);
      float var15 = (float)(var9.maxZ - var7);
      int var16 = var2.color;
      var0.vertex(var1, var10, var11, var15).color(var16);
      var0.vertex(var1, var13, var11, var15).color(var16);
      var0.vertex(var1, var13, var11, var12).color(var16);
      var0.vertex(var1, var10, var11, var12).color(var16);
      var0.vertex(var1, var10, var14, var12).color(var16);
      var0.vertex(var1, var13, var14, var12).color(var16);
      var0.vertex(var1, var13, var14, var15).color(var16);
      var0.vertex(var1, var10, var14, var15).color(var16);
      var0.vertex(var1, var10, var11, var12).color(var16);
      var0.vertex(var1, var13, var11, var12).color(var16);
      var0.vertex(var1, var13, var14, var12).color(var16);
      var0.vertex(var1, var10, var14, var12).color(var16);
      var0.vertex(var1, var10, var14, var15).color(var16);
      var0.vertex(var1, var13, var14, var15).color(var16);
      var0.vertex(var1, var13, var11, var15).color(var16);
      var0.vertex(var1, var10, var11, var15).color(var16);
      var0.vertex(var1, var10, var11, var15).color(var16);
      var0.vertex(var1, var10, var14, var15).color(var16);
      var0.vertex(var1, var10, var14, var12).color(var16);
      var0.vertex(var1, var10, var11, var12).color(var16);
      var0.vertex(var1, var13, var11, var12).color(var16);
      var0.vertex(var1, var13, var14, var12).color(var16);
      var0.vertex(var1, var13, var14, var15).color(var16);
      var0.vertex(var1, var13, var11, var15).color(var16);
   }

   private static void run21(VertexConsumer var0, Matrix4f var1, ListUtils.Inner12 var2, double var3, double var5, double var7) {
      float var9 = (float)(var2.class243.x - var3);
      float var10 = (float)(var2.class243.y - var5);
      float var11 = (float)(var2.class243.z - var7);
      float var12 = (float)(var2.class2432.x - var3);
      float var13 = (float)(var2.class2432.y - var5);
      float var14 = (float)(var2.class2432.z - var7);
      float var15 = (float)(var2.class2433.x - var3);
      float var16 = (float)(var2.class2433.y - var5);
      float var17 = (float)(var2.class2433.z - var7);
      int var18 = var2.color;
      var0.vertex(var1, var9, var10, var11).color(var18);
      var0.vertex(var1, var12, var13, var14).color(var18);
      var0.vertex(var1, var15, var16, var17).color(var18);
      var0.vertex(var1, var15, var16, var17).color(var18);
   }

   private static void run22(boolean var0) {
      if (!bool5) {
         bool5 = true;
         System.out
            .println(
               "[EspBoxRenderer] filled-quads ACTIVE via "
                  + (
                     var0
                        ? "custom POSITION_COLOR + QUADS pipeline (cull off, TW variant available=" + (ListUtils.Inner6.class1921 != null) + ")"
                        : "FALLBACK debugQuads (init failed — TW filled boxes will occlude)"
                  )
            );
      }
   }

   private static void run23(VertexConsumer var0, Matrix4f var1, ListUtils.Inner8 var2, double var3, double var5, double var7) {
      float var9 = (float)(var2.doubleVal - var3);
      float var10 = (float)(var2.doubleVal4 - var3);
      float var11 = (float)(var2.doubleVal3 - var7);
      float var12 = (float)(var2.doubleVal5 - var7);
      float var13 = (float)(var2.doubleVal2 - var5);
      var0.vertex(var1, var9, var13, var11).color(var2.color);
      var0.vertex(var1, var9, var13, var12).color(var2.color);
      var0.vertex(var1, var10, var13, var12).color(var2.color);
      var0.vertex(var1, var10, var13, var11).color(var2.color);
   }

   private static void run24(String var0, Throwable var1) {
      long var2 = System.currentTimeMillis();
      if (var2 - longVal >= 5000L) {
         longVal = var2;
         System.err
            .println(
               "[EspBoxRenderer] "
                  + var0
                  + " draw failed ("
                  + var1.getClass().getSimpleName()
                  + ": "
                  + var1.getMessage()
                  + ") — batch dropped. Reduce ESP MaxDistance or disable heavy layers."
            );
      }
   }

   public static void run25(Vec3d var0, int var1, float var2) {
      if (var0 != null) {
         list15.add(new ListUtils.Inner11(var0, var1, var2));
      }
   }

   public static void run26(DrawContext var0, double var1, double var3) {
      if (!list15.isEmpty()) {
         try {
            for (ListUtils.Inner11 var6 : list15) {
               double[] var7 = Matrix4fUtils.doubleArrayOf(var6.target);
               if (var7 != null) {
                  run28(var0, var1, var3, var7[0], var7[1], var6.color, var6.widthPx);
               }
            }
         } finally {
            list15.clear();
         }
      }
   }

   private static void run27(VertexConsumer var0, Matrix4f var1, Entry var2, ListUtils.Inner3 var3, double var4, double var6, double var8) {
      Box var10 = var3.box;
      float var11 = (float)(var10.maxX - var10.minX);
      float var12 = (float)(var10.maxY - var10.minY);
      float var13 = (float)(var10.maxZ - var10.minZ);
      float var14 = var3.cornerLengthFraction;
      float var15 = var11 * var14;
      float var16 = var12 * var14;
      float var17 = var13 * var14;
      int var18 = var3.color;
      float var19 = (float)(var10.minX - var4);
      float var20 = (float)(var10.minY - var6);
      float var21 = (float)(var10.minZ - var8);
      float var22 = (float)(var10.maxX - var4);
      float var23 = (float)(var10.maxY - var6);
      float var24 = (float)(var10.maxZ - var8);
      float var25 = var3.lineWidth;

      for (int var26 = 0; var26 < 8; var26++) {
         boolean var27 = (var26 & 1) != 0;
         boolean var28 = (var26 & 2) != 0;
         boolean var29 = (var26 & 4) != 0;
         float var30 = var27 ? var22 : var19;
         float var31 = var28 ? var23 : var20;
         float var32 = var29 ? var24 : var21;
         float var33 = var27 ? -1.0F : 1.0F;
         float var34 = var28 ? -1.0F : 1.0F;
         float var35 = var29 ? -1.0F : 1.0F;
         if (var15 > 1.0E-6F) {
            var0.vertex(var1, var30, var31, var32).color(var18).normal(var2, var33, 0.0F, 0.0F).lineWidth(var25);
            var0.vertex(var1, var30 + var33 * var15, var31, var32).color(var18).normal(var2, var33, 0.0F, 0.0F).lineWidth(var25);
         }

         if (var16 > 1.0E-6F) {
            var0.vertex(var1, var30, var31, var32).color(var18).normal(var2, 0.0F, var34, 0.0F).lineWidth(var25);
            var0.vertex(var1, var30, var31 + var34 * var16, var32).color(var18).normal(var2, 0.0F, var34, 0.0F).lineWidth(var25);
         }

         if (var17 > 1.0E-6F) {
            var0.vertex(var1, var30, var31, var32).color(var18).normal(var2, 0.0F, 0.0F, var35).lineWidth(var25);
            var0.vertex(var1, var30, var31, var32 + var35 * var17).color(var18).normal(var2, 0.0F, 0.0F, var35).lineWidth(var25);
         }
      }
   }

   private static void run28(DrawContext var0, double var1, double var3, double var5, double var7, int var9, float var10) {
      double var11 = var5 - var1;
      double var13 = var7 - var3;
      double var15 = Math.max(Math.abs(var11), Math.abs(var13));
      if (!(var15 < 1.0)) {
         double var17 = var11 / var15;
         double var19 = var13 / var15;
         int var21 = Math.max(1, (int)Math.ceil(var10 * 0.5));
         int var22 = (int)var15;

         for (int var23 = 0; var23 <= var22; var23++) {
            int var24 = (int)Math.round(var1 + var17 * var23);
            int var25 = (int)Math.round(var3 + var19 * var23);
            var0.fill(var24 - var21, var25 - var21, var24 + var21, var25 + var21, var9);
         }
      }
   }

record Inner1(Box box, int color, float lineWidth, boolean throughWalls) {

}

final static class Inner10 {
   static final RenderPipeline renderPipeline;
   static final RenderLayer class1921;

   private Inner10() {
   }

   static {
      RenderPipeline var0 = null;
      RenderLayer var1 = null;

      try {
         var0 = RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
            .withLocation(Identifier.of("codeengine", "pipeline/smooth_lines_through_walls"))
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .build();
         RenderSetup var2 = RenderSetup.builder(var0).build();
         var1 = RenderLayer.of("codeengine_smooth_lines_through_walls", var2);
         System.out
            .println(
               "[EspBoxRenderer] smooth-lines pipeline built: codeengine:pipeline/smooth_lines_through_walls (NO_DEPTH_TEST, POSITION_COLOR + QUADS, TRANSLUCENT, MSAA-aware)"
            );
      } catch (Throwable var3) {
         ListUtils.run8("through-walls", var3);
      }

      renderPipeline = var0;
      class1921 = var1;
   }
}

record Inner11(Vec3d target, int color, float widthPx) {

}

record Inner12(Vec3d class243, Vec3d class2432, Vec3d class2433, int color) {


   public Vec3d getclass243() {
      return this.class243;
   }

   public Vec3d getclass2432() {
      return this.class2432;
   }

   public Vec3d getclass2433() {
      return this.class2433;
   }
}

final static class Inner13 {
   static final RenderPipeline renderPipeline;
   static final RenderLayer class1921;

   private Inner13() {
   }

   static {
      RenderPipeline var0 = null;
      RenderLayer var1 = null;

      try {
         var0 = RenderPipeline.builder(new Snippet[]{RenderPipelines.RENDERTYPE_LINES_SNIPPET})
            .withLocation(Identifier.of("codeengine", "pipeline/through_walls_lines"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withCull(false)
            .build();
         RenderSetup var2 = RenderSetup.builder(var0).build();
         var1 = RenderLayer.of("codeengine_lines_through_walls", var2);
      } catch (Throwable var3) {
         ListUtils.run(var3);
      }

      renderPipeline = var0;
      class1921 = var1;
   }
}

record Inner2(Vec3d center, double radius, int color, float lineWidth, int segments) {

}

record Inner3(Box box, int color, float lineWidth, float cornerLengthFraction, boolean throughWalls) {

}

record Inner4(Box box, int color) {

}

final static class Inner5 {
   static final RenderPipeline renderPipeline;
   static final RenderLayer class1921;

   private Inner5() {
   }

   static {
      RenderPipeline var0 = null;
      RenderLayer var1 = null;

      try {
         var0 = RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
            .withLocation(Identifier.of("codeengine", "pipeline/filled_quads"))
            .withCull(false)
            .build();
         RenderSetup var2 = RenderSetup.builder(var0).build();
         var1 = RenderLayer.of("codeengine_filled_quads", var2);
         System.out
            .println(
               "[EspBoxRenderer] filled-quads pipeline built: codeengine:pipeline/filled_quads (depth-tested, POSITION_COLOR + QUADS, TRANSLUCENT, cull off)"
            );
      } catch (Throwable var3) {
         ListUtils.run2("depth-tested", var3);
      }

      renderPipeline = var0;
      class1921 = var1;
   }
}

final static class Inner6 {
   static final RenderPipeline renderPipeline;
   static final RenderLayer class1921;

   private Inner6() {
   }

   static {
      RenderPipeline var0 = null;
      RenderLayer var1 = null;

      try {
         var0 = RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
            .withLocation(Identifier.of("codeengine", "pipeline/filled_quads_through_walls"))
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .build();
         RenderSetup var2 = RenderSetup.builder(var0).build();
         var1 = RenderLayer.of("codeengine_filled_quads_through_walls", var2);
         System.out
            .println(
               "[EspBoxRenderer] filled-quads pipeline built: codeengine:pipeline/filled_quads_through_walls (NO_DEPTH_TEST, POSITION_COLOR + QUADS, TRANSLUCENT, cull off)"
            );
      } catch (Throwable var3) {
         ListUtils.run2("through-walls", var3);
      }

      renderPipeline = var0;
      class1921 = var1;
   }
}

record Inner7(Vec3d class243, Vec3d class2432, int color, float lineWidth) {


   public Vec3d getclass243() {
      return this.class243;
   }

   public Vec3d getclass2432() {
      return this.class2432;
   }
}

record Inner8(double doubleVal, double doubleVal2, double doubleVal3, double doubleVal4, double doubleVal5, int color) {


   public double getDouble() {
      return this.doubleVal;
   }

   public double getDouble2() {
      return this.doubleVal2;
   }

   public double getDouble3() {
      return this.doubleVal3;
   }

   public double getDouble4() {
      return this.doubleVal4;
   }

   public double getDouble5() {
      return this.doubleVal5;
   }
}

final static class Inner9 {
   static final RenderPipeline renderPipeline;
   static final RenderLayer class1921;

   private Inner9() {
   }

   static {
      RenderPipeline var0 = null;
      RenderLayer var1 = null;

      try {
         var0 = RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
            .withLocation(Identifier.of("codeengine", "pipeline/smooth_lines"))
            .withCull(false)
            .build();
         RenderSetup var2 = RenderSetup.builder(var0).build();
         var1 = RenderLayer.of("codeengine_smooth_lines", var2);
         System.out
            .println(
               "[EspBoxRenderer] smooth-lines pipeline built: codeengine:pipeline/smooth_lines (depth-tested, POSITION_COLOR + QUADS, TRANSLUCENT, MSAA-aware)"
            );
      } catch (Throwable var3) {
         ListUtils.run8("depth-tested", var3);
      }

      renderPipeline = var0;
      class1921 = var1;
   }
}
}

