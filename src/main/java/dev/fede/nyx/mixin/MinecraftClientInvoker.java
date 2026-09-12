package dev.fede.nyx.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface MinecraftClientInvoker {
   @Invoker("doAttack")
   boolean nyx$doAttack();

   @Invoker("doItemUse")
   void nyx$doItemUse();
}

