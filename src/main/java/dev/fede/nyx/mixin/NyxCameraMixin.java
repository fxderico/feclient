package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.movement.FreecamModule;
import dev.fede.nyx.module.modules.movement.FreelookModule;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Camera.class})
public class NyxCameraMixin {
   @Inject(
      method = {"update"},
      at = {@At("TAIL")}
   )
   private void nyx$freecamOverride(World var1, Entity var2, boolean var3, boolean var4, float var5, CallbackInfo var6) {
      Camera var7 = (Camera)(Object)this;
      NyxCameraInvoker var8 = (NyxCameraInvoker)var7;
      if (FreecamModule.bool) {
         Vec3d var26 = FreecamModule.class243Of(var5);
         if (var26 != null) {
            var8.nyx$setRotation(FreecamModule.getFloat(), FreecamModule.getFloat2());
            var8.nyx$setPos(var26.x, var26.y, var26.z);
         }
      } else {
         if (FreelookModule.bool && var2 != null) {
            float var9 = FreelookModule.getFloat();
            float var10 = FreelookModule.getFloat2();
            var8.nyx$setRotation(var9, var10);
            Vec3d var11 = var2.getCameraPosVec(var5);
            double var12 = var3 ? 4.0 : 0.0;
            double var14 = Math.toRadians(var9);
            double var16 = Math.toRadians(var10);
            double var18 = Math.cos(var16);
            double var20 = Math.sin(var14) * var18 * var12;
            double var22 = Math.sin(var16) * var12;
            double var24 = -Math.cos(var14) * var18 * var12;
            var8.nyx$setPos(var11.x + var20, var11.y + var22, var11.z + var24);
         }
      }
   }
}

