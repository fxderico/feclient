package dev.fede.mixin;

import dev.fede.suschunk.ServerLightCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class LightPacketMixin {
   @Inject(
      method = {"method_11143"},
      at = {@At("TAIL")}
   )
   private void FeClient$captureLightUpdate(LightUpdateS2CPacket packet, CallbackInfo ci) {
      ServerLightCache.get().ingest(packet.getChunkX(), packet.getChunkZ(), packet.getData(), MinecraftClient.getInstance().world);
   }

   @Inject(
      method = {"method_11128"},
      at = {@At("TAIL")}
   )
   private void FeClient$captureChunkLight(ChunkDataS2CPacket packet, CallbackInfo ci) {
      ServerLightCache.get().ingest(packet.getChunkX(), packet.getChunkZ(), packet.getLightData(), MinecraftClient.getInstance().world);
   }
}

