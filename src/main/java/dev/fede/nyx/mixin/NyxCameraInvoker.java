package dev.fede.nyx.mixin;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Camera.class})
public interface NyxCameraInvoker {
   @Invoker("setPos")
   void nyx$setPos(double var1, double var3, double var5);

   @Invoker("setRotation")
   void nyx$setRotation(float var1, float var2);
}

