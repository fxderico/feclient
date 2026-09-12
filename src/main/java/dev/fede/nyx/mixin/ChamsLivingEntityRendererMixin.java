package dev.fede.nyx.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.duck.ChamsLivingEntityRendererMixinHelper;
import dev.fede.nyx.module.modules.render.Chams;
import dev.fede.nyx.render.Sampler0;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LivingEntityRenderer.class})
public abstract class ChamsLivingEntityRendererMixin {
   @Shadow
   public abstract Identifier getTexture(LivingEntityRenderState var1);

   @ModifyReturnValue(
      method = {"getRenderLayer(Lnet/minecraft/LivingEntityRenderState;ZZZ)Lnet/minecraft/RenderLayer;"},
      at = {@At("RETURN")}
   )
   private RenderLayer nyx$chamsSwap(RenderLayer var1, LivingEntityRenderState var2, boolean var3, boolean var4, boolean var5) {
      if (var1 == null) {
         return null;
      } else {
         Chams var6 = Chams.chams;
         if (var6 == null || !var6.isEnabled3()) {
            return var1;
         } else if (!Sampler0.isEnabled()) {
            return var1;
         } else {
            Entity var7 = ((ChamsLivingEntityRendererMixinHelper)var2).nyx$getChamsEntity();
            if (var7 == null) {
               return var1;
            } else if (!Chams.check(var7)) {
               return var1;
            } else {
               Identifier var8 = this.getTexture(var2);
               if (var8 == null) {
                  return var1;
               } else {
                  RenderLayer var9 = Sampler0.class1921Of(var8);
                  return var9 != null ? var9 : var1;
               }
            }
         }
      }
   }
}

