package dev.fede.nyx.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LivingEntity.class})
public interface LivingEntityAccessor {
   @Accessor("jumpingCooldown")
   int nyx$getJumpingCooldown();

   @Accessor("jumpingCooldown")
   void nyx$setJumpingCooldown(int var1);
}

