package dev.fede.water.utils.renderer.texture;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import dev.fede.water.utils.renderer.RenderUtil;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

public class TexturePipeline {
   private static RenderPipeline pipeline;
   private static GpuBuffer uniformBuffer;
   private static final int UNIFORM_SIZE = 128;

   public static void init() {
      if (pipeline == null) {
         try {
            pipeline = RenderPipeline.builder()
               .withLocation(Identifier.of("water", "texture"))
               .withVertexShader(Identifier.of("water", "texture_vertex"))
               .withFragmentShader(Identifier.of("water", "texture_fragment"))
               .withVertexFormat(VertexFormat.builder().build(), DrawMode.TRIANGLES)
               .withUniform("Uniforms", UniformType.UNIFORM_BUFFER)
               .withSampler("Sampler0")
               .withBlend(BlendFunction.TRANSLUCENT)
               .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
               .withCull(false)
               .build();
            uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "Texture2D Uniforms", 136, 128L);
         } catch (Exception var1) {
            System.err.println("[Texture2D] Failed to init: " + var1.getMessage());
            var1.printStackTrace();
         }
      }
   }

   public static void draw(Matrix4f var0, float var1, float var2, float var3, GpuTextureView var4, int var5, float var6, float var7) {
      if (pipeline == null) {
         init();
      }

      if (pipeline != null && uniformBuffer != null && var4 != null) {
         float var8 = (var5 >> 16 & 0xFF) / 255.0F;
         float var9 = (var5 >> 8 & 0xFF) / 255.0F;
         float var10 = (var5 & 0xFF) / 255.0F;
         float var11 = (var5 >> 24 & 0xFF) / 255.0F;
         ByteBuffer var12 = MemoryUtil.memAlloc(128);
         var12.putFloat(var0.m00()).putFloat(var0.m01()).putFloat(var0.m02()).putFloat(var0.m03());
         var12.putFloat(var0.m10()).putFloat(var0.m11()).putFloat(var0.m12()).putFloat(var0.m13());
         var12.putFloat(var0.m20()).putFloat(var0.m21()).putFloat(var0.m22()).putFloat(var0.m23());
         var12.putFloat(var0.m30()).putFloat(var0.m31()).putFloat(var0.m32()).putFloat(var0.m33());
         var12.position(64);
         var12.putFloat(var1).putFloat(var2).putFloat(var3).putFloat(var3);
         var12.putFloat(var8).putFloat(var9).putFloat(var10).putFloat(var11);
         var12.putFloat(var6).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
         var12.putFloat(var7).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
         var12.flip();
         CommandEncoder var13 = RenderSystem.getDevice().createCommandEncoder();
         var13.writeToBuffer(uniformBuffer.slice(), var12);
         MemoryUtil.memFree(var12);
         GpuSampler var14 = RenderSystem.getSamplerCache().get(FilterMode.LINEAR);
         Framebuffer var15 = MinecraftClient.getInstance().getFramebuffer();

         try (RenderPass var16 = var13.createRenderPass(
               () -> "Texture2D", var15.getColorAttachmentView(), OptionalInt.empty(), var15.getDepthAttachmentView(), OptionalDouble.of(1.0)
            )) {
            RenderUtil.applyScissor(var16);
            var16.setPipeline(pipeline);
            var16.setUniform("Uniforms", uniformBuffer);
            var16.bindTexture("Sampler0", var4, var14);
            var16.draw(0, 6);
         }
      }
   }

   public static void drawWH(Matrix4f var0, float var1, float var2, float var3, float var4, GpuTextureView var5, int var6, float var7) {
      if (pipeline == null) {
         init();
      }

      if (pipeline != null && uniformBuffer != null && var5 != null) {
         float var8 = (var6 >> 16 & 0xFF) / 255.0F;
         float var9 = (var6 >> 8 & 0xFF) / 255.0F;
         float var10 = (var6 & 0xFF) / 255.0F;
         float var11 = (var6 >> 24 & 0xFF) / 255.0F;
         ByteBuffer var12 = MemoryUtil.memAlloc(128);
         var12.putFloat(var0.m00()).putFloat(var0.m01()).putFloat(var0.m02()).putFloat(var0.m03());
         var12.putFloat(var0.m10()).putFloat(var0.m11()).putFloat(var0.m12()).putFloat(var0.m13());
         var12.putFloat(var0.m20()).putFloat(var0.m21()).putFloat(var0.m22()).putFloat(var0.m23());
         var12.putFloat(var0.m30()).putFloat(var0.m31()).putFloat(var0.m32()).putFloat(var0.m33());
         var12.position(64);
         var12.putFloat(var1).putFloat(var2).putFloat(var3).putFloat(var4);
         var12.putFloat(var8).putFloat(var9).putFloat(var10).putFloat(var11);
         var12.putFloat(0.0F).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
         var12.putFloat(var7).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
         var12.flip();
         CommandEncoder var13 = RenderSystem.getDevice().createCommandEncoder();
         var13.writeToBuffer(uniformBuffer.slice(), var12);
         MemoryUtil.memFree(var12);
         GpuSampler var14 = RenderSystem.getSamplerCache().get(FilterMode.LINEAR);
         Framebuffer var15 = MinecraftClient.getInstance().getFramebuffer();

         try (RenderPass var16 = var13.createRenderPass(
               () -> "AwtFont", var15.getColorAttachmentView(), OptionalInt.empty(), var15.getDepthAttachmentView(), OptionalDouble.of(1.0)
            )) {
            RenderUtil.applyScissor(var16);
            var16.setPipeline(pipeline);
            var16.setUniform("Uniforms", uniformBuffer);
            var16.bindTexture("Sampler0", var5, var14);
            var16.draw(0, 6);
         }
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

