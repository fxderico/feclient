package dev.fede.mixin;

import dev.fede.module.impl.FreecamModule;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Camera.class})
public abstract class CameraFreecamMixin {
   @Shadow
   protected abstract void setPos(Vec3d var1);

   @Shadow
   protected abstract void setRotation(float var1, float var2);

   @Inject(
      method = {"method_19321"},
      at = {@At("TAIL")}
   )
   private void FeClient$freecam(World level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
      FreecamModule freecam = FreecamModule.get();
      if (freecam != null && freecam.isActive()) {
         this.setRotation(freecam.getInterpolatedYaw(partialTick), freecam.getInterpolatedPitch(partialTick));
         this.setPos(freecam.getInterpolatedPos(partialTick));
      }
   }
}

