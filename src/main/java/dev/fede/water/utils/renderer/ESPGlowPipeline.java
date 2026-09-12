package dev.fede.water.utils.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

public final class ESPGlowPipeline {
   private static RenderPipeline pipeline;
   private static GpuBuffer uniformBuffer;
   private static final int UNIFORM_SIZE = 128;
   public static float defaultGlowSize = 12.0F;

   private ESPGlowPipeline() {
   }

   public static void init() {
      if (pipeline == null) {
         try {
            pipeline = RenderPipeline.builder()
               .withLocation(Identifier.of("water", "esp_glow_box"))
               .withVertexShader(Identifier.of("water", "esp_glow_box_vertex"))
               .withFragmentShader(Identifier.of("water", "esp_glow_box_fragment"))
               .withVertexFormat(VertexFormat.builder().build(), DrawMode.TRIANGLES)
               .withUniform("Uniforms", UniformType.UNIFORM_BUFFER)
               .withBlend(BlendFunction.TRANSLUCENT)
               .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
               .withCull(false)
               .build();
            uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "ESPGlow Uniforms", 136, 128L);
         } catch (Exception var1) {
            System.err.println("[ESPGlowPipeline] Init failed: " + var1.getMessage());
         }
      }
   }

   public static void drawGlowBox(Matrix4f var0, float var1, float var2, float var3, float var4, Color var5, float var6, float var7) {
      if (pipeline == null) {
         init();
      }

      if (pipeline != null && uniformBuffer != null) {
         ByteBuffer var8 = MemoryUtil.memAlloc(128);

         try {
            var8.putFloat(var0.m00()).putFloat(var0.m01()).putFloat(var0.m02()).putFloat(var0.m03());
            var8.putFloat(var0.m10()).putFloat(var0.m11()).putFloat(var0.m12()).putFloat(var0.m13());
            var8.putFloat(var0.m20()).putFloat(var0.m21()).putFloat(var0.m22()).putFloat(var0.m23());
            var8.putFloat(var0.m30()).putFloat(var0.m31()).putFloat(var0.m32()).putFloat(var0.m33());
            var8.putFloat(var1).putFloat(var2).putFloat(var3).putFloat(var4);
            var8.putFloat(var5.getRed() / 255.0F);
            var8.putFloat(var5.getGreen() / 255.0F);
            var8.putFloat(var5.getBlue() / 255.0F);
            var8.putFloat(var5.getAlpha() / 255.0F);
            var8.putFloat(var6);
            var8.putFloat(var7);
            var8.putFloat(0.0F).putFloat(0.0F);
            var8.flip();
            CommandEncoder var9 = RenderSystem.getDevice().createCommandEncoder();
            var9.writeToBuffer(uniformBuffer.slice(), var8);
            Framebuffer var10 = MinecraftClient.getInstance().getFramebuffer();

            try (RenderPass var11 = var9.createRenderPass(
                  () -> "ESPGlow", var10.getColorAttachmentView(), OptionalInt.empty(), var10.getDepthAttachmentView(), OptionalDouble.empty()
               )) {
               RenderUtil.applyScissor(var11);
               var11.setPipeline(pipeline);
               var11.setUniform("Uniforms", uniformBuffer);
               var11.draw(0, 6);
            }
         } finally {
            MemoryUtil.memFree(var8);
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

