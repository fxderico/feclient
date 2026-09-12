package dev.fede.water.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MinecraftClient.class})
public interface MinecraftClientAccessor {
   @Accessor("itemUseCooldown")
   int water$getItemUseCooldown();

   @Accessor("itemUseCooldown")
   void water$setItemUseCooldown(int var1);
}

