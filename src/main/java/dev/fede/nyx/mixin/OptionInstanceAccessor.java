package dev.fede.nyx.mixin;

import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SimpleOption.class})
public interface OptionInstanceAccessor<T> {
   @Accessor("value")
   T nyx$getValue();

   @Accessor("value")
   void nyx$setValue(T var1);
}

