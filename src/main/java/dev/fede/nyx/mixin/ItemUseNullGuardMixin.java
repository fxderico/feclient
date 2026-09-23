package dev.fede.nyx.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Crash guard for the item-use path.
 *
 * Observed crash (feClient 1.2.1): NPE in ClientPlayerInteractionManager.interactItem
 * ("client.player is null") reached from MinecraftClient.doItemUse during a
 * tick — the classic disconnect / world-unload race where the local player is
 * torn down while right-click is still being processed. Not Enough Crashes
 * caught it, but it still kicks you to the crash screen.
 *
 * FastPlace makes it far more likely by zeroing the item-use cooldown, so
 * doItemUse fires every tick while use is held. Several feClient modules also
 * invoke doItemUse directly (AutoEat, AutoFish, AutoClicker, MiddleClick,
 * AutoTunnel, the miner helper) via MinecraftClientInvoker.
 *
 * Cancelling doItemUse at HEAD when player/world/interactionManager are null
 * protects the vanilla tick call AND every one of those callers in one place.
 * It cannot fully close a true cross-thread null (player nulled mid-method on
 * another thread), but it eliminates the common already-null entry and is a
 * cheap, side-effect-free guard.
 */
@Mixin(MinecraftClient.class)
public class ItemUseNullGuardMixin {
   @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
   private void nyx$guardItemUse(CallbackInfo ci) {
      MinecraftClient mc = (MinecraftClient)(Object)this;
      if (mc.player == null || mc.world == null || mc.interactionManager == null) {
         ci.cancel();
      }
   }
}
