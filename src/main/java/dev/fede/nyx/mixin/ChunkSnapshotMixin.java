package dev.fede.nyx.mixin;

import dev.fede.nyx.util.Map$EntryUtils;
import dev.fede.nyx.util.PacketSenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class ChunkSnapshotMixin {
   @Inject(
      method = {"onChunkData(Lnet/minecraft/ChunkDataS2CPacket;)V"},
      at = {@At("RETURN")}
   )
   private void codeengine$snapshotChunk(ChunkDataS2CPacket var1, CallbackInfo var2) {
      if (PacketSenderUtils.bool) {
         if (!Map$EntryUtils.isEnabled()) {
            MinecraftClient var3 = MinecraftClient.getInstance();
            if (var3 != null) {
               int var4 = var1.getChunkX();
               int var5 = var1.getChunkZ();
               var3.execute(() -> PacketSenderUtils.run5(var4, var5));
            }
         }
      }
   }
}

