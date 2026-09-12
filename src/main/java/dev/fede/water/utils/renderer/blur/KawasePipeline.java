package dev.fede.water.utils.renderer.blur;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import dev.fede.water.utils.renderer.RenderUtil;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

public class KawasePipeline {
   private static final int BLUR_ITERATIONS = 5;
   private static final int DOWNSAMPLE_SCALE = 2;
   private static final int BUFFER_SIZE = 256;
   private static final RenderPipeline PIPELINE_BLUR = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(Identifier.of("water", "pipeline/blur_pass"))
         .withVertexShader(Identifier.of("water", "blur_pass_vertex"))
         .withFragmentShader(Identifier.of("water", "blur_pass_fragment"))
         .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
         .withUniform("BlurData", UniformType.UNIFORM_BUFFER)
         .withSampler("Sampler0")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .build()
   );
   private static final RenderPipeline PIPELINE_FINAL = RenderPipelines.register(
      RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(Identifier.of("water", "pipeline/blur_final"))
         .withVertexShader(Identifier.of("water", "blur_final_vertex"))
         .withFragmentShader(Identifier.of("water", "blur_final_fragment"))
         .withVertexFormat(VertexFormats.EMPTY, DrawMode.TRIANGLES)
         .withUniform("BlurData", UniformType.UNIFORM_BUFFER)
         .withSampler("Sampler0")
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .build()
   );
   private static final Vector4f COLOR_MODULATOR = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private static final Vector3f MODEL_OFFSET = new Vector3f(0.0F, 0.0F, 0.0F);
   private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
   private static GpuBuffer uniformBuffer;
   private static GpuBuffer dummyVertexBuffer;
   private static ByteBuffer dataBuffer;
   private static final int PASS_POOL_SIZE = 24;
   private static final GpuBuffer[] passBuffers = new GpuBuffer[24];
   private static final ByteBuffer[] passData = new ByteBuffer[24];
   private static int passCursor = 0;
   private static GpuTexture copyTexture;
   private static GpuTextureView copyTextureView;
   private static GpuTexture[] pingPongTextures = new GpuTexture[2];
   private static GpuTextureView[] pingPongViews = new GpuTextureView[2];
   private static int lastWidth = 0;
   private static int lastHeight = 0;
   private static boolean initialized = false;
   private static long lastFrameTime = -1L;
   private static int cachedBlurSrc = 0;
   private static float cachedStrength = 0.0F;
   private static boolean frameDirty = true;

   public static void beginFrame() {
      frameDirty = true;
   }

   public static void init() {
      if (!initialized) {
         ByteBuffer var0 = MemoryUtil.memAlloc(4);
         var0.putInt(0);
         var0.flip();
         dummyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "water:blur_dummy_vertex", 32, var0);
         MemoryUtil.memFree(var0);
         initialized = true;
      }
   }

   public static void captureFramebuffer() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      int var1 = var0.getFramebuffer().textureWidth;
      int var2 = var0.getFramebuffer().textureHeight;
      ensureTextures(var1, var2);
      CommandEncoder var3 = RenderSystem.getDevice().createCommandEncoder();
      var3.copyTextureToTexture(var0.getFramebuffer().getColorAttachment(), copyTexture, 0, 0, 0, 0, 0, var1, var2);
      lastFrameTime = -1L;
   }

   private static void ensureTextures(int var0, int var1) {
      int var2 = var0 / 2;
      int var3 = var1 / 2;
      if (copyTexture == null || var0 != lastWidth || var1 != lastHeight) {
         if (copyTextureView != null) {
            copyTextureView.close();
            copyTextureView = null;
         }

         if (copyTexture != null) {
            copyTexture.close();
            copyTexture = null;
         }

         copyTexture = RenderSystem.getDevice().createTexture(() -> "water:blur_copy", 5, TextureFormat.RGBA8, var0, var1, 1, 1);
         copyTextureView = RenderSystem.getDevice().createTextureView(copyTexture);

         for (int var4 = 0; var4 < 2; var4++) {
            if (pingPongViews[var4] != null) {
               pingPongViews[var4].close();
               pingPongViews[var4] = null;
            }

            if (pingPongTextures[var4] != null) {
               pingPongTextures[var4].close();
               pingPongTextures[var4] = null;
            }

            int var5 = var4;
            pingPongTextures[var4] = RenderSystem.getDevice().createTexture(() -> "water:blur_pp_" + var5, 13, TextureFormat.RGBA8, var2, var3, 1, 1);
            pingPongViews[var4] = RenderSystem.getDevice().createTextureView(pingPongTextures[var4]);
         }

         lastWidth = var0;
         lastHeight = var1;
         lastFrameTime = -1L;
      }
   }

   public static void draw(Matrix4f var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      MinecraftClient var8 = MinecraftClient.getInstance();
      if (var8.getFramebuffer() != null) {
         if (var8.getFramebuffer().getColorAttachment() != null) {
            init();
            int var9 = var8.getFramebuffer().textureWidth;
            int var10 = var8.getFramebuffer().textureHeight;
            int var11 = var9 / 2;
            int var12 = var10 / 2;
            ensureTextures(var9, var10);
            long var13 = System.nanoTime() / 16666666L;
            boolean var15 = frameDirty || Math.abs(var6 - cachedStrength) > 0.01F;
            GpuSampler var16 = RenderSystem.getSamplerCache().get(FilterMode.LINEAR);
            GpuBufferSlice var17 = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), COLOR_MODULATOR, MODEL_OFFSET, TEXTURE_MATRIX);
            CommandEncoder var18 = RenderSystem.getDevice().createCommandEncoder();
            if (var15) {
               var18.copyTextureToTexture(var8.getFramebuffer().getColorAttachment(), copyTexture, 0, 0, 0, 0, 0, var9, var10);
               prepareBlurData(var9, var10, var11, var12, 1.0F, var6);
               var18.writeToBuffer(uniformBuffer.slice(), dataBuffer);

               try (RenderPass var19 = var18.createRenderPass(
                     () -> "water:blur_downsample", pingPongViews[0], OptionalInt.empty(), null, OptionalDouble.empty()
                  )) {
                  var19.setPipeline(PIPELINE_BLUR);
                  var19.setVertexBuffer(0, dummyVertexBuffer);
                  var19.bindTexture("Sampler0", copyTextureView, var16);
                  RenderSystem.bindDefaultUniforms(var19);
                  var19.setUniform("DynamicTransforms", var17);
                  var19.setUniform("BlurData", uniformBuffer);
                  var19.draw(0, 6);
               }

               int var35 = Math.min(10, Math.max(2, (int)(5.0F * var6)));
               float[] var20 = new float[]{1.0F, 2.0F, 2.0F, 3.0F};

               for (int var21 = 0; var21 < var35; var21++) {
                  int var22 = var21 % 2;
                  int var23 = (var21 + 1) % 2;
                  float var24 = var21 < var20.length ? var20[var21] : 3.0F;
                  int var25 = var21;
                  prepareBlurData(var11, var12, var11, var12, var24, 1.0F);
                  var18.writeToBuffer(uniformBuffer.slice(), dataBuffer);

                  try (RenderPass var26 = var18.createRenderPass(
                        () -> "water:blur_" + var25, pingPongViews[var23], OptionalInt.empty(), null, OptionalDouble.empty()
                     )) {
                     var26.setPipeline(PIPELINE_BLUR);
                     var26.setVertexBuffer(0, dummyVertexBuffer);
                     var26.bindTexture("Sampler0", pingPongViews[var22], var16);
                     RenderSystem.bindDefaultUniforms(var26);
                     var26.setUniform("DynamicTransforms", var17);
                     var26.setUniform("BlurData", uniformBuffer);
                     var26.draw(0, 6);
                  }
               }

               cachedBlurSrc = var35 % 2;
               lastFrameTime = var13;
               cachedStrength = var6;
               frameDirty = false;
            }

            float[] var36 = new float[]{var5, var5, var5, var5};
            int var37 = RenderUtil.getFixedScaledWidth();
            int var38 = RenderUtil.getFixedScaledHeight();
            prepareFinalData(var0, var1, var2, var3, var4, var37, var38, var36, var7);
            var18.writeToBuffer(uniformBuffer.slice(), dataBuffer);

            try (RenderPass var39 = var18.createRenderPass(
                  () -> "water:blur_final",
                  var8.getFramebuffer().getColorAttachmentView(),
                  OptionalInt.empty(),
                  var8.getFramebuffer().getDepthAttachmentView(),
                  OptionalDouble.of(1.0)
               )) {
               RenderUtil.applyScissor(var39);
               var39.setPipeline(PIPELINE_FINAL);
               var39.setVertexBuffer(0, dummyVertexBuffer);
               var39.bindTexture("Sampler0", pingPongViews[cachedBlurSrc], var16);
               RenderSystem.bindDefaultUniforms(var39);
               var39.setUniform("DynamicTransforms", var17);
               var39.setUniform("BlurData", uniformBuffer);
               var39.draw(0, 6);
            }
         }
      }
   }

   private static void advancePassSlot() {
      passCursor = (passCursor + 1) % 24;
      if (passData[passCursor] == null) {
         passData[passCursor] = MemoryUtil.memAlloc(256);
      }

      dataBuffer = passData[passCursor];
   }

   private static void prepareBlurData(int var0, int var1, int var2, int var3, float var4, float var5) {
      advancePassSlot();
      dataBuffer.clear();

      for (int var6 = 0; var6 < 16; var6++) {
         dataBuffer.putFloat(0.0F);
      }

      dataBuffer.putFloat(0.0F).putFloat(0.0F).putFloat(var0).putFloat(var1);
      dataBuffer.putFloat(var0).putFloat(var1).putFloat(1.0F).putFloat(var4);
      dataBuffer.putFloat(var2).putFloat(var3).putFloat(1.0F).putFloat(var5);
      dataBuffer.putFloat(0.0F).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
      dataBuffer.putFloat(0.0F).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
      dataBuffer.flip();
      ensureBuffer();
   }

   private static void prepareFinalData(Matrix4f var0, float var1, float var2, float var3, float var4, int var5, int var6, float[] var7, float var8) {
      advancePassSlot();
      dataBuffer.clear();
      dataBuffer.putFloat(var0.m00()).putFloat(var0.m01()).putFloat(var0.m02()).putFloat(var0.m03());
      dataBuffer.putFloat(var0.m10()).putFloat(var0.m11()).putFloat(var0.m12()).putFloat(var0.m13());
      dataBuffer.putFloat(var0.m20()).putFloat(var0.m21()).putFloat(var0.m22()).putFloat(var0.m23());
      dataBuffer.putFloat(var0.m30()).putFloat(var0.m31()).putFloat(var0.m32()).putFloat(var0.m33());
      dataBuffer.putFloat(var1).putFloat(var2).putFloat(var3).putFloat(var4);
      dataBuffer.putFloat(var5).putFloat(var6).putFloat(0.0F).putFloat(0.0F);
      dataBuffer.putFloat(var5).putFloat(var6).putFloat(1.0F).putFloat(0.0F);
      dataBuffer.putFloat(var7[0]).putFloat(var7[1]).putFloat(var7[2]).putFloat(var7[3]);
      dataBuffer.putFloat(var8).putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
      dataBuffer.flip();
      ensureBuffer();
   }

   private static void ensureBuffer() {
      int var0 = dataBuffer.remaining();
      if (passBuffers[passCursor] == null || passBuffers[passCursor].size() < var0) {
         if (passBuffers[passCursor] != null) {
            passBuffers[passCursor].close();
         }

         int var1 = passCursor;
         passBuffers[passCursor] = RenderSystem.getDevice().createBuffer(() -> "water:blur_uniform_" + var1, 136, var0);
      }

      uniformBuffer = passBuffers[passCursor];
   }

   public static GpuTextureView getBlurTextureView() {
      return initialized && pingPongViews[cachedBlurSrc] != null ? pingPongViews[cachedBlurSrc] : null;
   }

   public static void shutdown() {
      uniformBuffer = null;
      dataBuffer = null;

      for (int var0 = 0; var0 < 24; var0++) {
         if (passBuffers[var0] != null) {
            passBuffers[var0].close();
            passBuffers[var0] = null;
         }

         if (passData[var0] != null) {
            MemoryUtil.memFree(passData[var0]);
            passData[var0] = null;
         }
      }

      passCursor = 0;
      if (dummyVertexBuffer != null) {
         dummyVertexBuffer.close();
         dummyVertexBuffer = null;
      }

      if (copyTextureView != null) {
         copyTextureView.close();
         copyTextureView = null;
      }

      if (copyTexture != null) {
         copyTexture.close();
         copyTexture = null;
      }

      for (int var1 = 0; var1 < 2; var1++) {
         if (pingPongViews[var1] != null) {
            pingPongViews[var1].close();
            pingPongViews[var1] = null;
         }

         if (pingPongTextures[var1] != null) {
            pingPongTextures[var1].close();
            pingPongTextures[var1] = null;
         }
      }

      lastWidth = 0;
      lastHeight = 0;
      initialized = false;
      lastFrameTime = -1L;
   }
}

