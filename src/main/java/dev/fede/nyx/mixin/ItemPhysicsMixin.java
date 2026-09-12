package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.render.ItemPhysicsModule;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemEntityRenderer.class})
public class ItemPhysicsMixin {
   @Inject(
      method = {"render(Lnet/minecraft/ItemEntityRenderState;Lnet/minecraft/MatrixStack;Lnet/minecraft/OrderedRenderCommandQueue;Lnet/minecraft/CameraRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void nyx$applyExtraPhysics(ItemEntityRenderState var1, MatrixStack var2, OrderedRenderCommandQueue var3, CameraRenderState var4, CallbackInfo var5) {
      if (ItemPhysicsModule.isEnabled_s()) {
         float var6 = ItemPhysicsModule.getFloat();
         float var7 = ItemPhysicsModule.getFloat2();
         if (!(var6 <= 0.0F) || !(var7 <= 0.0F)) {
            float var8 = var1.age + var1.uniqueOffset;
            float var9 = var8 / 20.0F;
            if (var7 > 0.0F) {
               float var10 = MathHelper.sin(var8 * 0.1F) * var7;
               var2.translate(0.0F, var10, 0.0F);
            }

            if (var6 > 0.0F) {
               float var12 = var9 * var6 * 360.0F;
               float var11 = var12 - (float)(Math.floor(var12 / 360.0F) * 360.0);
               var2.multiply(new Quaternionf().rotateY((float)Math.toRadians(var11)));
            }
         }
      }
   }
}

