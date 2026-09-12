package dev.fede.nyx.mixin;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.modules.combat.VelocityModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class VelocityPacketMixin {
   @Inject(
      method = {"onEntityVelocityUpdate"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket var1, CallbackInfo var2) {
      if (VelocityModule.bool) {
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3.player != null && var3.world != null) {
            if (var1.getEntityId() == var3.player.getId()) {
               if (!VelocityModule.bool3 || isSourcePlayer(var3)) {
                  double var4 = VelocityModule.doubleVal;
                  double var6 = VelocityModule.doubleVal2;
                  boolean var8 = VelocityModule.bool2;
                  Vec3d var9 = var1.getVelocity();
                  double var10 = var9.x * var4 * (var8 ? -1.0 : 1.0);
                  double var12 = var9.y * var6;
                  double var14 = var9.z * var4 * (var8 ? -1.0 : 1.0);
                  if (var4 == 1.0 && var6 == 1.0 && !var8) {
                     notifyObserved();
                  } else {
                     var2.cancel();
                     var3.execute(() -> {
                        if (var3.player != null) {
                           if (var10 == 0.0 && var12 == 0.0 && var14 == 0.0) {
                              var3.player.setVelocityClient(Vec3d.ZERO);
                           } else {
                              var3.player.setVelocityClient(new Vec3d(var10, var12, var14));
                           }
                        }
                     });
                     notifyObserved();
                  }
               }
            }
         }
      }
   }

   private static boolean isSourcePlayer(MinecraftClient var0) {
      if (var0.player == null) {
         return false;
      } else {
         LivingEntity var1 = var0.player.getAttacker();
         return var1 instanceof PlayerEntity;
      }
   }

   private static void notifyObserved() {
      if (NyxClient.MODULES != null) {
         if (NyxClient.MODULES.moduleOf("Velocity") instanceof VelocityModule var1 && var1.isEnabled3()) {
            var1.run5();
         }
      }
   }
}

