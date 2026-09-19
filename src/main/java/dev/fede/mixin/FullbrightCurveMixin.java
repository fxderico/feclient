package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The existing fullbright hook (LightTextureMixin) only forces the *input*
 * to vanilla's brightness curve (the gamma option value) — it still runs
 * through LightmapTextureManager.getBrightness()'s falloff curve afterward,
 * which can still leave genuinely unlit spots (light level 0 — sealed
 * caves, the far side of builds) dark even with gamma maxed, since the
 * curve was never designed to produce true white at the low end no matter
 * the gamma input. This hooks the curve's *output* directly instead: both
 * getBrightness overloads (one dimension-aware for aggregate skylight+
 * blocklight, one the raw per-channel version) return 1.0 outright when
 * fullbright is on, so every light level — including 0 — renders fully lit.
 * Keeping the gamma-input mixin too; harmless, and it's what applies if
 * this one is ever removed.
 */
@Mixin(LightmapTextureManager.class)
public class FullbrightCurveMixin {

   @Inject(method = "getBrightness(Lnet/minecraft/world/dimension/DimensionType;I)F", at = @At("HEAD"), cancellable = true)
   private static void fe$fullbrightDimension(DimensionType type, int lightLevel, CallbackInfoReturnable<Float> cir) {
      if (isFullbright()) cir.setReturnValue(1.0F);
   }

   @Inject(method = "getBrightness(FI)F", at = @At("HEAD"), cancellable = true)
   private static void fe$fullbrightRaw(float skyDarkness, int lightLevel, CallbackInfoReturnable<Float> cir) {
      if (isFullbright()) cir.setReturnValue(1.0F);
   }

   // the Warden's Darkness effect isn't part of the lightmap curve at all —
   // it's a separate pulsing screen-dimming factor mixed in here. zeroing it
   // when fullbright's on kills that pulse too, which most real fullbright
   // implementations do since otherwise you'd still periodically go dark
   // while under the effect even with every light level maxed.
   @Inject(method = "getDarkness(Lnet/minecraft/entity/LivingEntity;FF)F", at = @At("HEAD"), cancellable = true)
   private void fe$fullbrightDarkness(LivingEntity entity, float tickDelta, float partialTick, CallbackInfoReturnable<Float> cir) {
      if (isFullbright()) cir.setReturnValue(0.0F);
   }

   private static boolean isFullbright() {
      ModuleManager modules = FeClient.modules();
      return modules != null && modules.fullbright != null && modules.fullbright.isEnabled();
   }
}
