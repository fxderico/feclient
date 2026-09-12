package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.ChunkSpoof;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ClientCommonNetworkHandler.class})
public class ChunkSpoofPacketMixin {
   private static boolean nyx$loggedFirstMutation = false;

   @ModifyVariable(
      method = {"sendPacket(Lnet/minecraft/Packet;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private Packet<?> nyx$chunkSpoofRewriteMove(Packet<?> var1) {
      if (var1 instanceof PlayerMoveC2SPacket var2) {
         Vec3d var3 = ChunkSpoof.getclass243();
         if (var3 == null) {
            return var1;
         } else {
            return (Packet<?>)(var2.changesLook()
               ? new Full(var3.x, var3.y, var3.z, var2.getYaw(0.0F), var2.getPitch(0.0F), false, false)
               : new PositionAndOnGround(var3.x, var3.y, var3.z, false, false));
         }
      } else {
         return var1;
      }
   }
}

