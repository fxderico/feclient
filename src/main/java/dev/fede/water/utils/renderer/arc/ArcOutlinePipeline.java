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

public class ArcOutlinePipeline {
   private static RenderPipeline pipeline;
   private static GpuBuffer uniformBuffer;
   private static final int UNIFORM_SIZE = 160;

   public static void init() {
      if (pipeline == null) {
         try {
            pipeline = RenderPipeline.builder()
               .withLocation(Identifier.of("water", "arc_outline"))
               .withVertexShader(Identifier.of("water", "arc_outline_vertex"))
               .withFragmentShader(Identifier.of("water", "arc_outline_fragment"))
               .withVertexFormat(VertexFormat.builder().build(), DrawMode.TRIANGLES)
               .withUniform("Uniforms", UniformType.UNIFORM_BUFFER)
               .withBlend(BlendFunction.TRANSLUCENT)
               .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
               .withCull(false)
               .build();
            uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "ArcOutline2D Uniforms", 136, 160L);
         } catch (Exception var1) {
            System.err.println("[ArcOutline2D] Failed to init: " + var1.getMessage());
         }
      }
   }

   public static void draw(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9, float var10) {
      if (pipeline == null) {
         init();
      }

      if (pipeline != null && uniformBuffer != null) {
         float var11 = (var8 >> 16 & 0xFF) / 255.0F;
         float var12 = (var8 >> 8 & 0xFF) / 255.0F;
         float var13 = (var8 & 0xFF) / 255.0F;
         float var14 = (var8 >> 24 & 0xFF) / 255.0F;
         float var15 = (var9 >> 16 & 0xFF) / 255.0F;
         float var16 = (var9 >> 8 & 0xFF) / 255.0F;
         float var17 = (var9 & 0xFF) / 255.0F;
         float var18 = (var9 >> 24 & 0xFF) / 255.0F;
         ByteBuffer var19 = MemoryUtil.memAlloc(160);
         var19.putFloat(var0.m00()).putFloat(var0.m01()).putFloat(var0.m02()).putFloat(var0.m03());
         var19.putFloat(var0.m10()).putFloat(var0.m11()).putFloat(var0.m12()).putFloat(var0.m13());
         var19.putFloat(var0.m20()).putFloat(var0.m21()).putFloat(var0.m22()).putFloat(var0.m23());
         var19.putFloat(var0.m30()).putFloat(var0.m31()).putFloat(var0.m32()).putFloat(var0.m33());
         var19.position(64);
         var19.putFloat(var1).putFloat(var2).putFloat(var3).putFloat(var3);
         var19.putFloat(var3).putFloat(var4).putFloat(var5).putFloat(var6);
         var19.putFloat(var10).putFloat(var7).putFloat(0.0F).putFloat(0.0F);
         var19.putFloat(var11).putFloat(var12).putFloat(var13).putFloat(var14);
         var19.putFloat(var15).putFloat(var16).putFloat(var17).putFloat(var18);
         var19.flip();
         CommandEncoder var20 = RenderSystem.getDevice().createCommandEncoder();
         var20.writeToBuffer(uniformBuffer.slice(), var19);
         MemoryUtil.memFree(var19);
         Framebuffer var21 = MinecraftClient.getInstance().getFramebuffer();

         try (RenderPass var22 = var20.createRenderPass(
               () -> "ArcOutline2D", var21.getColorAttachmentView(), OptionalInt.empty(), var21.getDepthAttachmentView(), OptionalDouble.of(1.0)
            )) {
            RenderUtil.applyScissor(var22);
            var22.setPipeline(pipeline);
            var22.setUniform("Uniforms", uniformBuffer);
            var22.draw(0, 6);
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

