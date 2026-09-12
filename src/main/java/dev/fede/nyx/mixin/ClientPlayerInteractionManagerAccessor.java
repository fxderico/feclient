package dev.fede.nyx.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ClientPlayerInteractionManager.class})
public interface ClientPlayerInteractionManagerAccessor {
   @Accessor("blockBreakingCooldown")
   int nyx$getBlockBreakingCooldown();

   @Accessor("currentBreakingPos")
   BlockPos nyx$getCurrentBreakingPos();

   @Accessor("currentBreakingProgress")
   float nyx$getCurrentBreakingProgress();
}

