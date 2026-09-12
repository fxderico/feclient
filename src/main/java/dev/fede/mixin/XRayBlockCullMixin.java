package dev.fede.mixin;

import dev.fede.module.impl.XRayModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hooks Block.shouldDrawSide to hide non-ore blocks when XRay is active.
 * Runs at HEAD with priority so it fires before the nyx BlockMixin shim.
 */
@Mixin(value = Block.class, priority = 1100)
public abstract class XRayBlockCullMixin {

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void fe$xrayCull(BlockState state, BlockState adjacentState,
                                     Direction direction,
                                     CallbackInfoReturnable<Boolean> cir) {
        if (!XRayModule.ACTIVE) return;

        boolean selfVisible = XRayModule.isVisible(state.getBlock());
        boolean adjVisible  = XRayModule.isVisible(adjacentState.getBlock());

        if (!selfVisible) {
            // This block is hidden — never draw any of its faces
            cir.setReturnValue(false);
        } else if (!adjVisible) {
            // Ore block exposed to a hidden wall — force-draw this face
            cir.setReturnValue(true);
        }
        // Both visible → let normal culling run (don't cancel)
    }
}
