package dev.fede.nyx.mixin;

import dev.fede.nyx.storage.Chest$KindUtils;
import dev.fede.nyx.storage.ClientPlayNetworkHandlerMixinUtil;
import dev.fede.nyx.util.m$aUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class ClientPlayNetworkHandlerMixin {
   @Inject(
      method = {"onBlockEntityUpdate"},
      at = {@At("HEAD")}
   )
   private void nyx$onBlockEntityUpdate(BlockEntityUpdateS2CPacket var1, CallbackInfo var2) {
      BlockPos var3 = var1.getPos();
      if (var3 != null) {
         BlockPos var4 = var3.toImmutable();
         MinecraftClient var5 = MinecraftClient.getInstance();
         var5.execute(() -> {
            if (var5.world != null) {
               BlockEntity var2x = var5.world.getBlockEntity(var4);
               if (var2x != null) {
                  Chest$KindUtils.onBlockEntity(var4, var2x);
               }
            }
         });
      }
   }

   @Inject(
      method = {"onChunkData"},
      at = {@At("HEAD")}
   )
   private void nyx$onChunkData(ChunkDataS2CPacket var1, CallbackInfo var2) {
      int var3 = var1.getChunkX();
      int var4 = var1.getChunkZ();
      MinecraftClient.getInstance().execute(() -> ClientPlayNetworkHandlerMixinUtil.enqueueChunk(var3, var4));
   }

   @Inject(
      method = {"onBlockUpdate"},
      at = {@At("HEAD")}
   )
   private void nyx$onBlockUpdate(BlockUpdateS2CPacket var1, CallbackInfo var2) {
      BlockPos var3 = var1.getPos();
      BlockState var4 = var1.getState();
      if (var3 != null && var4 != null) {
         BlockPos var5 = var3.toImmutable();
         MinecraftClient.getInstance().execute(() -> m$aUtils.run2(var5, var4));
      }
   }

   @Inject(
      method = {"onChunkDeltaUpdate"},
      at = {@At("HEAD")}
   )
   private void nyx$onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket var1, CallbackInfo var2) {
      MinecraftClient var3 = MinecraftClient.getInstance();
      var1.visitUpdates((var1x, var2x) -> {
         if (var1x != null && var2x != null) {
            BlockPos var3x = var1x.toImmutable();
            var3.execute(() -> m$aUtils.run2(var3x, var2x));
         }
      });
   }
}

