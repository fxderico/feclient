package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.SkinProtectModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractClientPlayerEntity.class})
public class AbstractClientPlayerMixin {
   @Inject(
      method = {"method_52814"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void FeClient$replaceSkin(CallbackInfoReturnable<SkinTextures> cir) {
      ModuleManager modules = FeClient.modules();
      if (modules != null) {
         SkinProtectModule skinProtect = modules.skinProtect;
         if (skinProtect != null && skinProtect.isEnabled()) {
            SkinTextures replacement = skinProtect.replacementSkin();
            if (replacement != null) {
               AbstractClientPlayerEntity self = (AbstractClientPlayerEntity)(Object)this;
               if (skinProtect.shouldReplace(self.getUuid())) {
                  cir.setReturnValue(replacement);
               }
            }
         }
      }
   }
}



