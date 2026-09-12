package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.render.ClearWorldModule;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ParticleManager.class})
class ClearWorldParticleMixin {
   @Inject(
      method = {"addParticle(Lnet/minecraft/ParticleEffect;DDDDDD)Lnet/minecraft/Particle;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$suppressParticle(
      ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12, CallbackInfoReturnable<Particle> var14
   ) {
      if (ClearWorldModule.isEnabled3_s()) {
         var14.setReturnValue(null);
      }
   }
}

