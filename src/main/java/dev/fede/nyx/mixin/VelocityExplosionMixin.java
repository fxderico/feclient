package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.combat.VelocityModule;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class VelocityExplosionMixin {
   @Inject(
      method = {"onExplosion"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$onExplosion(ExplosionS2CPacket var1, CallbackInfo var2) {
      if (VelocityModule.bool) {
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3.player != null && var3.world != null) {
            Optional var4 = var1.playerKnockback();
            if (!var4.isEmpty()) {
               if (!VelocityModule.bool3 || var3.player.getAttacker() instanceof PlayerEntity) {
                  double var5 = VelocityModule.doubleVal;
                  double var7 = VelocityModule.doubleVal2;
                  boolean var9 = VelocityModule.bool2;
                  if (var5 != 1.0 || var7 != 1.0 || var9) {
                     Vec3d var10 = (Vec3d)var4.get();
                     double var11 = var10.x * var5 * (var9 ? -1.0 : 1.0);
                     double var13 = var10.y * var7;
                     double var15 = var10.z * var5 * (var9 ? -1.0 : 1.0);
                     var3.execute(() -> {
                        if (var3.player != null) {
                           var3.player.addVelocity(var11 - var10.x, var13 - var10.y, var15 - var10.z);
                           var3.player.velocityDirty = true;
                        }
                     });
                  }
               }
            }
         }
      }
   }
}

