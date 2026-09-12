package dev.fede.mixin;

import dev.fede.module.impl.FreecamModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
public abstract class LocalPlayerFreecamMixin {
   @Inject(
      method = {"method_6007"},
      at = {@At("HEAD")}
   )
   private void FeClient$freecamReapplyBeforeAiStep(CallbackInfo ci) {
      this.FeClient$reapply();
   }

   @Inject(
      method = {"method_3136"},
      at = {@At("HEAD")}
   )
   private void FeClient$freecamReapplyBeforeSendPosition(CallbackInfo ci) {
      this.FeClient$reapply();
   }

   private void FeClient$reapply() {
      ClientPlayerEntity self = (ClientPlayerEntity)(Object)this;
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player == self) {
         FreecamModule f = FreecamModule.get();
         if (f != null && f.isActive()) {
            FreecamModule.reapplyBodyInput(mc);
         }
      }
   }
}

