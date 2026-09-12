package dev.fede.water.utils.renderer.glass;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
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

public class LiquidGlassPipeline {
   private static RenderPipeline pipeline;
   private static GpuBuffer uniformBuffer;
   private static final int UNIFORM_SIZE = 176;

   public static void init() {
      if (pipeline == null) {
         try {
            pipeline = RenderPipeline.builder()
               .withLocation(Identifier.of("water", "liquidglass"))
               .withVertexShader(Identifier.of("water", "liquidglass_vertex"))
               .withFragmentShader(Identifier.of("water", "liquidglass_fragment"))
               .withVertexFormat(VertexFormat.builder().build(), DrawMode.TRIANGLES)
               .withUniform("Uniforms", UniformType.UNIFORM_BUFFER)
               .withSampler("Sampler0")
               .withBlend(BlendFunction.TRANSLUCENT)
               .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
               .withCull(false)
               .build();
            uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "LiquidGlass Uniforms", 136, 176L);
         } catch (Exception var1) {
            System.err.println("[LiquidGlass] Failed to init: " + var1.getMessage());
            var1.printStackTrace();
         }
      }
   }

   public static void draw(
      Matrix4f var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float[] var5,
      int var6,
      float var7,
      float var8,
      int var9,
      float var10,
      boolean var11,
      float var12,
      float var13,
      float var14,
      float var15
   ) {
      if (pipeline == null) {
         init();
      }

      if (pipeline != null && uniformBuffer != null) {
         MinecraftClient var16 = MinecraftClient.getInstance();
         Framebuffer var17 = var16.getFramebuffer();
         if (var17 != null && var17.getColorAttachmentView() != null) {
            float var18 = (var9 >> 16 & 0xFF) / 255.0F;
            float var19 = (var9 >> 8 & 0xFF) / 255.0F;
            float var20 = (var9 & 0xFF) / 255.0F;
            float var21 = (var9 >> 24 & 0xFF) / 255.0F;
            float var22 = var5.length > 0 ? var5[0] : 0.0F;
            float var23 = var5.length > 1 ? var5[1] : var22;
            float var24 = var5.length > 2 ? var5[2] : var22;
            float var25 = var5.length > 3 ? var5[3] : var22;
            int var26 = var17.textureWidth;
            int var27 = var17.textureHeight;
            ByteBuffer var28 = MemoryUtil.memAlloc(176);
            var28.putFloat(var0.m00()).putFloat(var0.m01()).putFloat(var0.m02()).putFloat(var0.m03());
            var28.putFloat(var0.m10()).putFloat(var0.m11()).putFloat(var0.m12()).putFloat(var0.m13());
            var28.putFloat(var0.m20()).putFloat(var0.m21()).putFloat(var0.m22()).putFloat(var0.m23());
            var28.putFloat(var0.m30()).putFloat(var0.m31()).putFloat(var0.m32()).putFloat(var0.m33());
            var28.position(64);
            var28.putFloat(var1).putFloat(var2).putFloat(var3).putFloat(var4);
            var28.putFloat(var26).putFloat(var27);
            var28.position(96);
            var28.putFloat(var22).putFloat(var23).putFloat(var24).putFloat(var25);
            var28.position(112);
            var28.putFloat(var14);
            var28.putFloat(2.0F);
            var28.putFloat(var7);
            var28.putFloat(var8);
            var28.position(128);
            var28.putFloat(var18).putFloat(var19).putFloat(var20).putFloat(var21);
            var28.position(144);
            var28.putFloat(var10);
            var28.putInt(var11 ? 1 : 0);
            var28.putFloat(var12);
            var28.putFloat(var13);
            var28.putFloat(var15);
            var28.putFloat(0.0F);
            var28.flip();
            CommandEncoder var29 = RenderSystem.getDevice().createCommandEncoder();
            var29.writeToBuffer(uniformBuffer.slice(), var28);
            MemoryUtil.memFree(var28);
            GpuSampler var30 = RenderSystem.getSamplerCache().get(FilterMode.LINEAR);

            try (RenderPass var31 = var29.createRenderPass(
                  () -> "LiquidGlass", var17.getColorAttachmentView(), OptionalInt.empty(), var17.getDepthAttachmentView(), OptionalDouble.of(1.0)
               )) {
               RenderUtil.applyScissor(var31);
               var31.setPipeline(pipeline);
               var31.setUniform("Uniforms", uniformBuffer);
               var31.bindTexture("Sampler0", var17.getColorAttachmentView(), var30);
               var31.draw(0, 6);
            }
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

