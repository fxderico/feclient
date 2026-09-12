package dev.fede.nyx.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import dev.fede.nyx.render.c$bUtils;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.memory.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public class LightBloomWorldRendererMixin {
   @Inject(
      method = {"render"},
      at = {@At("TAIL")},
      require = 0
   )
   private void nyx$compositeLightBloom(
      ObjectAllocator var1,
      RenderTickCounter var2,
      boolean var3,
      Camera var4,
      Matrix4f var5,
      Matrix4f var6,
      Matrix4f var7,
      GpuBufferSlice var8,
      Vector4f var9,
      boolean var10,
      CallbackInfo var11
   ) {
      try {
         float var12 = var2 == null ? 0.0F : var2.getTickProgress(true);
         MatrixStack var13 = new MatrixStack();
         c$bUtils.run3(var13, var12, var4);
      } catch (Throwable var14) {
      }
   }
}

