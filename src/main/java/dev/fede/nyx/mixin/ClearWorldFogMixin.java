package dev.fede.nyx.mixin;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;

import dev.fede.nyx.module.modules.render.ClearWorldModule;
import net.minecraft.client.render.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({FogRenderer.class})
class ClearWorldFogMixin {
   private static final float NO_FOG_DISTANCE = 999999.0F;

   @ModifyArgs(
      method = {"applyFog(Lnet/minecraft/Camera;ILnet/minecraft/RenderTickCounter;FLnet/minecraft/ClientWorld;)Lorg/joml/Vector4f;"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/FogRenderer;applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"
      )
   )
   private void nyx$pushFogAway(Args args) {
      if (ClearWorldModule.isEnabled2()) {
         args.set(3, 999999.0F);
         args.set(4, 999999.0F);
         args.set(5, 999999.0F);
         args.set(6, 999999.0F);
         args.set(7, 999999.0F);
         args.set(8, 999999.0F);
      }
   }
}

