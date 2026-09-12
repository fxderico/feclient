package dev.fede.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface MinecraftAccessor {
   @Accessor("field_1752")
   void FeClient$setRightClickDelay(int var1);

   @Invoker("method_1536")
   boolean FeClient$startAttack();

   @Invoker("method_1583")
   void FeClient$startUseItem();
}

