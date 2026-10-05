package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.client.VersionSpooferModule;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Rewrites the client brand returned by ClientBrandRetriever.getClientModName()
 * (what the game sends the server on join) when VersionSpoofer is on, so a Fabric
 * client can report "vanilla" instead of "fabric".
 */
@Mixin(ClientBrandRetriever.class)
public class ClientBrandRetrieverMixin {
   @Inject(method = "getClientModName", at = @At("HEAD"), cancellable = true)
   private static void nyx$spoofBrand(CallbackInfoReturnable<String> cir) {
      String b = VersionSpooferModule.spoofedBrand();
      if (b != null && !b.isEmpty()) {
         cir.setReturnValue(b);
      }
   }
}
