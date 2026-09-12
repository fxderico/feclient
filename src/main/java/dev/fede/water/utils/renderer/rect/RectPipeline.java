package dev.fede.water.utils.renderer.rect;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import dev.fede.water.utils.renderer.RenderUtil;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

public class RectPipeline {
   private static RenderPipeline pipeline;
   private static GpuBuffer uniformBuffer;
   private static final int UNIFORM_SIZE = 256;

   public static void init() {
      if (pipeline == null) {
         try {
            pipeline = RenderPipeline.builder()
               .withLocation(Identifier.of("water", "rectangle"))
               .withVertexShader(Identifier.of("water", "rectangle_vertex"))
               .withFragmentShader(Identifier.of("water", "rectangle_fragment"))
               .withVertexFormat(VertexFormat.builder().build(), DrawMode.TRIANGLES)
               .withUniform("Uniforms", UniformType.UNIFORM_BUFFER)
               .withBlend(BlendFunction.TRANSLUCENT)
               .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
               .withCull(false)
               .build();
            uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "Rect2D Uniforms", 136, 256L);
         } catch (Exception var1) {
            System.err.println("[Rect2D] Failed to init: " + var1.getMessage());
            var1.printStackTrace();
         }
      }
   }

   public static void draw(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int... var10
   ) {
      if (pipeline == null) {
         init();
      }

      if (pipeline != null && uniformBuffer != null) {
         int[] var11 = normalizeColors(var10);
         ByteBuffer var12 = MemoryUtil.memAlloc(256);
         var12.putFloat(var0.m00()).putFloat(var0.m01()).putFloat(var0.m02()).putFloat(var0.m03());
         var12.putFloat(var0.m10()).putFloat(var0.m11()).putFloat(var0.m12()).putFloat(var0.m13());
         var12.putFloat(var0.m20()).putFloat(var0.m21()).putFloat(var0.m22()).putFloat(var0.m23());
         var12.putFloat(var0.m30()).putFloat(var0.m31()).putFloat(var0.m32()).putFloat(var0.m33());
         var12.position(64);
         var12.putFloat(var1).putFloat(var2).putFloat(var3).putFloat(var4);
         var12.putFloat(var6).putFloat(var7).putFloat(var5).putFloat(var8);
         var12.position(96);
         var12.putFloat(var9).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
         var12.position(112);

         for (int var13 = 0; var13 < 9; var13++) {
            int var14 = var11[var13];
            var12.putFloat((var14 >> 16 & 0xFF) / 255.0F);
            var12.putFloat((var14 >> 8 & 0xFF) / 255.0F);
            var12.putFloat((var14 & 0xFF) / 255.0F);
            var12.putFloat((var14 >> 24 & 0xFF) / 255.0F);
         }

         var12.flip();
         CommandEncoder var20 = RenderSystem.getDevice().createCommandEncoder();
         var20.writeToBuffer(uniformBuffer.slice(), var12);
         MemoryUtil.memFree(var12);
         Framebuffer var21 = MinecraftClient.getInstance().getFramebuffer();

         try (RenderPass var15 = var20.createRenderPass(
               () -> "Rect2D", var21.getColorAttachmentView(), OptionalInt.empty(), var21.getDepthAttachmentView(), OptionalDouble.of(1.0)
            )) {
            RenderUtil.applyScissor(var15);
            var15.setPipeline(pipeline);
            var15.setUniform("Uniforms", uniformBuffer);
            var15.draw(0, 6);
         }
      }
   }

   private static int[] normalizeColors(int[] var0) {
      if (var0.length == 1) {
         int var3 = var0[0];
         return new int[]{var3, var3, var3, var3, var3, var3, var3, var3, var3};
      } else if (var0.length >= 9) {
         return var0;
      } else {
         int[] var1 = new int[9];

         for (int var2 = 0; var2 < 9; var2++) {
            var1[var2] = var2 < var0.length ? var0[var2] : var0[var0.length - 1];
         }

         return var1;
      }
   }

   public static void shutdown() {
      if (uniformBuffer != null) {
         uniformBuffer.close();
         uniformBuffer = null;
      }

      pipeline = null;
   }
}

