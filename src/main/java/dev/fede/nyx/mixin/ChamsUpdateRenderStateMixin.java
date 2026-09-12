package dev.fede.nyx.mixin;

import dev.fede.nyx.duck.ChamsLivingEntityRendererMixinHelper;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public class ChamsUpdateRenderStateMixin {
   @Inject(
      method = {"updateRenderState(Lnet/minecraft/Entity;Lnet/minecraft/EntityRenderState;F)V"},
      at = {@At("HEAD")}
   )
   private void nyx$stashChamsEntity(Entity var1, EntityRenderState var2, float var3, CallbackInfo var4) {
      ((ChamsLivingEntityRendererMixinHelper)var2).nyx$setChamsEntity(var1);
   }
}

