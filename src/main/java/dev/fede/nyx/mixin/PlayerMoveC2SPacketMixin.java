package dev.fede.nyx.mixin;

import dev.fede.nyx.util.AntiAFKModuleUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
public abstract class PlayerMoveC2SPacketMixin {
   @Unique
   private float nyx$stashedYaw;
   @Unique
   private float nyx$stashedPitch;
   @Unique
   private boolean nyx$swapped;

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("HEAD")}
   )
   private void nyx$swapRotation(CallbackInfo ci) {
      float[] var2 = AntiAFKModuleUtil.getFloatArray();
      if (var2 == null) {
         this.nyx$swapped = false;
      } else {
         ClientPlayerEntity var3 = (ClientPlayerEntity)(Object)this;
         this.nyx$stashedYaw = var3.getYaw();
         this.nyx$stashedPitch = var3.getPitch();
         var3.setYaw(var2[0]);
         var3.setPitch(var2[1]);
         this.nyx$swapped = true;
      }
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("RETURN")}
   )
   private void nyx$restoreRotation(CallbackInfo ci) {
      if (this.nyx$swapped) {
         ClientPlayerEntity var2 = (ClientPlayerEntity)(Object)this;
         var2.setYaw(this.nyx$stashedYaw);
         var2.setPitch(this.nyx$stashedPitch);
         this.nyx$swapped = false;
      }
   }
}

