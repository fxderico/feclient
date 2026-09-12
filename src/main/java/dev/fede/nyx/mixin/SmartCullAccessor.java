package dev.fede.nyx.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MinecraftClient.class})
public interface SmartCullAccessor {
   @Accessor("chunkCullingEnabled")
   boolean nyx$getSmartCull();

   @Accessor("chunkCullingEnabled")
   void nyx$setSmartCull(boolean var1);
}

