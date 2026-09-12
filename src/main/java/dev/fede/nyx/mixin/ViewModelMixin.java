package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.render.ViewModelModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HeldItemRenderer.class})
public class ViewModelMixin {
   @Inject(
      method = {"renderFirstPersonItem(Lnet/minecraft/AbstractClientPlayerEntity;FFLnet/minecraft/Hand;FLnet/minecraft/ItemStack;FLnet/minecraft/MatrixStack;Lnet/minecraft/OrderedRenderCommandQueue;I)V"},
      at = {@At("HEAD")}
   )
   private void nyx$applyViewModel(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10,
      CallbackInfo var11
   ) {
      if (ViewModelModule.isEnabled_s()) {
         float var12 = ViewModelModule.getFloat();
         float var13 = ViewModelModule.getFloat2();
         float var14 = ViewModelModule.getFloat3();
         float var15 = ViewModelModule.getFloat4();
         float var16 = ViewModelModule.getFloat5();
         float var17 = ViewModelModule.getFloat6();
         float var18 = ViewModelModule.getFloat7();
         if (var12 != 0.0F || var13 != 0.0F || var14 != 0.0F) {
            var8.translate(var12, var13, var14);
         }

         if (var15 != 0.0F || var16 != 0.0F || var17 != 0.0F) {
            Quaternionf var19 = new Quaternionf()
               .rotateX((float)Math.toRadians(var15))
               .rotateY((float)Math.toRadians(var16))
               .rotateZ((float)Math.toRadians(var17));
            var8.multiply(var19);
         }

         if (var18 != 1.0F && var18 > 0.0F) {
            var8.scale(var18, var18, var18);
         }
      }
   }
}

