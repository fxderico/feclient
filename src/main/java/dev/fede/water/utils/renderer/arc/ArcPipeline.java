package dev.fede.water.utils.renderer.arc;

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

public class ArcPipeline {
   private static RenderPipeline pipeline;
   private static GpuBuffer uniformBuffer;
   private static final int UNIFORM_SIZE = 256;

   public static void init() {
      if (pipeline == null) {
         try {
            pipeline = RenderPipeline.builder()
               .withLocation(Identifier.of("water", "arc"))
               .withVertexShader(Identifier.of("water", "arc_vertex"))
               .withFragmentShader(Identifier.of("water", "arc_fragment"))
               .withVertexFormat(VertexFormat.builder().build(), DrawMode.TRIANGLES)
               .withUniform("Uniforms", UniformType.UNIFORM_BUFFER)
               .withBlend(BlendFunction.TRANSLUCENT)
               .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
               .withCull(false)
               .build();
            uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "Arc2D Uniforms", 136, 256L);
         } catch (Exception var1) {
            System.err.println("[Arc2D] Failed to init: " + var1.getMessage());
            var1.printStackTrace();
         }
      }
   }

   public static void draw(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int... var8) {
      if (pipeline == null) {
         init();
      }

      if (pipeline != null && uniformBuffer != null) {
         int[] var9 = normalizeColors(var8);
         ByteBuffer var10 = MemoryUtil.memAlloc(256);
         var10.putFloat(var0.m00()).putFloat(var0.m01()).putFloat(var0.m02()).putFloat(var0.m03());
         var10.putFloat(var0.m10()).putFloat(var0.m11()).putFloat(var0.m12()).putFloat(var0.m13());
         var10.putFloat(var0.m20()).putFloat(var0.m21()).putFloat(var0.m22()).putFloat(var0.m23());
         var10.putFloat(var0.m30()).putFloat(var0.m31()).putFloat(var0.m32()).putFloat(var0.m33());
         var10.position(64);
         var10.putFloat(var1).putFloat(var2).putFloat(var3).putFloat(var3);
         var10.putFloat(var3).putFloat(var4).putFloat(var5).putFloat(var6);
         var10.putFloat(var7).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
         var10.position(96);

         for (int var11 = 0; var11 < 9; var11++) {
            int var12 = var9[var11];
            var10.putFloat((var12 >> 16 & 0xFF) / 255.0F);
            var10.putFloat((var12 >> 8 & 0xFF) / 255.0F);
            var10.putFloat((var12 & 0xFF) / 255.0F);
            var10.putFloat((var12 >> 24 & 0xFF) / 255.0F);
         }

         var10.flip();
         CommandEncoder var18 = RenderSystem.getDevice().createCommandEncoder();
         var18.writeToBuffer(uniformBuffer.slice(), var10);
         MemoryUtil.memFree(var10);
         Framebuffer var19 = MinecraftClient.getInstance().getFramebuffer();

         try (RenderPass var13 = var18.createRenderPass(
               () -> "Arc2D", var19.getColorAttachmentView(), OptionalInt.empty(), var19.getDepthAttachmentView(), OptionalDouble.of(1.0)
            )) {
            RenderUtil.applyScissor(var13);
            var13.setPipeline(pipeline);
            var13.setUniform("Uniforms", uniformBuffer);
            var13.draw(0, 6);
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

