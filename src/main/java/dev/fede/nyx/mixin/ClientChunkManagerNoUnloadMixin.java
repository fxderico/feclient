package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.FreecamModule;
import dev.fede.nyx.util.PacketSenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientChunkManager.class})
public class ClientChunkManagerNoUnloadMixin {
   @Inject(
      method = {"unload(Lnet/minecraft/ChunkPos;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void codeengine$blockFreecamUnload(ChunkPos var1, CallbackInfo var2) {
      if (FreecamModule.bool) {
         if (var1 != null) {
            if (FreecamModule.class243 != null) {
               int var4 = (int)Math.floor(FreecamModule.class243.x) >> 4;
               int var5 = (int)Math.floor(FreecamModule.class243.z) >> 4;
               boolean var6 = Math.abs(var1.x - var4) <= 9 && Math.abs(var1.z - var5) <= 9;
               boolean var7 = false;

               try {
                  MinecraftClient var8 = MinecraftClient.getInstance();
                  if (var8 != null && var8.player != null) {
                     int var9 = var8.player.getBlockX() >> 4;
                     int var10 = var8.player.getBlockZ() >> 4;
                     var7 = Math.abs(var1.x - var9) <= 9 && Math.abs(var1.z - var10) <= 9;
                  }
               } catch (Throwable var11) {
               }

               if (var6 || var7) {
                  var2.cancel();
                  boolean var12 = PacketSenderUtils.class2672Of(var1.x, var1.z) != null;
               }
            }
         }
      }
   }
}

