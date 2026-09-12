package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.NoFallModule;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
abstract class NoFallOnGroundSpoofMixin {
   @Unique
   private boolean nyx$stashedOnGround;
   @Unique
   private boolean nyx$swapped;

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("HEAD")}
   )
   private void nyx$stampOnGround(CallbackInfo ci) {
      if (!NoFallModule.bool) {
         this.nyx$swapped = false;
      } else {
         ClientPlayerEntity var2 = (ClientPlayerEntity)(Object)this;
         this.nyx$stashedOnGround = var2.isOnGround();
         var2.setOnGround(true);
         this.nyx$swapped = true;
      }
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("RETURN")}
   )
   private void nyx$restoreOnGround(CallbackInfo ci) {
      if (this.nyx$swapped) {
         ClientPlayerEntity var2 = (ClientPlayerEntity)(Object)this;
         var2.setOnGround(this.nyx$stashedOnGround);
         this.nyx$swapped = false;
      }
   }
}

