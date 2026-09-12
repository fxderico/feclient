package dev.fede.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.FreecamModule;
import dev.fede.render.BlurHook;
import dev.fede.render.OverlayRenderer;
import dev.fede.render.WorldProjection;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
   @Inject(
      method = {"method_3192(Lnet/minecraft/class_9779;Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_11228;method_70879()V",
         shift = Shift.AFTER
      )}
   )
   private void FeClient$renderOverlay(RenderTickCounter deltaTracker, boolean bl, CallbackInfo ci) {
      OverlayRenderer.render();
   }

   @Inject(
      method = {"method_3188(Lnet/minecraft/class_9779;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/class_10366;)V"
      )}
   )
   private void FeClient$captureProjection(RenderTickCounter deltaTracker, CallbackInfo ci, @Local(ordinal = 0) Matrix4f matrix4f) {
      WorldProjection.capture(matrix4f, deltaTracker.getTickProgress(false));
   }

   @ModifyArg(
      method = {"method_3192(Lnet/minecraft/class_9779;Z)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_11284;method_71116(IIDJLnet/minecraft/class_9779;ILnet/minecraft/class_4184;Z)V"
      ),
      index = 5
   )
   private int FeClient$overrideBlurRadius(int blurriness) {
      return BlurHook.apply(blurriness);
   }

   @ModifyReturnValue(
      method = {"method_3196(Lnet/minecraft/class_4184;FZ)F"},
      at = {@At("RETURN")}
   )
   private float FeClient$zoomFov(float original) {
      ModuleManager modules = FeClient.modules();
      if (modules != null && modules.zoom != null) {
         double factor = modules.zoom.currentFactor();
         return factor > 1.0001 ? (float)(original / factor) : original;
      } else {
         return original;
      }
   }

   @ModifyExpressionValue(
      method = {"method_3196(Lnet/minecraft/class_4184;FZ)F"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_3532;method_16439(FFF)F",
         ordinal = 0
      )}
   )
   private float FeClient$customFov(float sprintMultiplier) {
      ModuleManager modules = FeClient.modules();
      return modules != null && modules.customFov != null ? modules.customFov.fovMultiplier(sprintMultiplier) : sprintMultiplier;
   }

   @ModifyExpressionValue(
      method = {"method_3172"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_5498;method_31034()Z"
      )}
   )
   private boolean FeClient$freecamRenderFirstPersonHands(boolean isFirstPerson) {
      FreecamModule freecam = FreecamModule.get();
      return freecam != null && freecam.isActive() && freecam.renderHands() ? true : isFirstPerson;
   }
}



