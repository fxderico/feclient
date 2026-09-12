package dev.fede.water.mixin;

import dev.fede.water.module.modules.render.NoRender;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({World.class})
public class WorldMixin {
   @Inject(
      method = {"getRainGradient"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void water$hideRainGradient(float var1, CallbackInfoReturnable<Float> var2) {
      if (NoRender.hideRainGradient()) {
         var2.setReturnValue(0.0F);
      }
   }

   @Inject(
      method = {"getThunderGradient"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void water$hideThunderGradient(float var1, CallbackInfoReturnable<Float> var2) {
      if (NoRender.hideThunder()) {
         var2.setReturnValue(0.0F);
      }
   }

   @Inject(
      method = {"playSoundClient(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void water$cancelWeatherPointSound(
      double var1, double var3, double var5, SoundEvent var7, SoundCategory var8, float var9, float var10, boolean var11, CallbackInfo var12
   ) {
      if (NoRender.shouldCancelWeatherSound(var7)) {
         var12.cancel();
      }
   }

   @Inject(
      method = {"playSoundAtBlockCenterClient(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void water$cancelWeatherBlockSound(BlockPos var1, SoundEvent var2, SoundCategory var3, float var4, float var5, boolean var6, CallbackInfo var7) {
      if (NoRender.shouldCancelWeatherSound(var2)) {
         var7.cancel();
      }
   }
}

