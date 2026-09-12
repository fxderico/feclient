package dev.fede.nyx.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({KeyBinding.class})
public interface KeyBindingAccessor {
   @Accessor("timesPressed")
   int nyx$getTimesPressed();

   @Accessor("timesPressed")
   void nyx$setTimesPressed(int var1);

   @Accessor("boundKey")
   Key nyx$getBoundKey();
}

