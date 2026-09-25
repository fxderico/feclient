package dev.fede.nyx.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import dev.fede.nyx.util.Matrix4fUtils;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.memory.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public class MixinWorldRenderer {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void nyxCaptureMatrices(
      ObjectAllocator var1,
      RenderTickCounter var2,
      boolean var3,
      Camera var4,
      Matrix4f var5, // positionMatrix (view)
      Matrix4f var6, // basicProjectionMatrix (1.21.11: new, NOT the render projection)
      Matrix4f var7, // projectionMatrix (the real one used for rendering + frustum)
      GpuBufferSlice var8,
      Vector4f var9,
      boolean var10,
      CallbackInfo var11
   ) {
      // 1.21.11 inserted basicProjectionMatrix ahead of the real projection, so the
      // screen-space projection (Tracers etc.) must use var7, not var6 — otherwise
      // every world->screen point lands in the wrong place.
      Matrix4fUtils.run(var5, var7, var4.getCameraPos());
   }
}

