package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.addons.KillEffectsModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class KillEffectsEntityStatusMixin {
   @Inject(
      method = {"onEntityStatus"},
      at = {@At("HEAD")}
   )
   private void nyx$onEntityStatus(EntityStatusS2CPacket var1, CallbackInfo var2) {
      if (var1.getStatus() == 3) {
         MinecraftClient var3 = MinecraftClient.getInstance();
         var3.execute(() -> {
            if (var3.world != null) {
               Entity var2x;
               try {
                  var2x = var1.getEntity(var3.world);
               } catch (Throwable var4) {
                  return;
               }

               if (var2x != null) {
                  KillEffectsModule.run4(var2x);
               }
            }
         });
      }
   }
}

