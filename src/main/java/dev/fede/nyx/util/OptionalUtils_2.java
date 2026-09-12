package dev.fede.nyx.util;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class OptionalUtils_2 {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();

   private OptionalUtils_2() {
   }

   public static Optional<OptionalUtils_2.Inner1> optionalOf(ClientPlayerEntity var0, int var1, boolean var2) {
      if (var0 == null) {
         return Optional.empty();
      } else if (var0.getBlockY() <= var1 + 1) {
         return Optional.empty();
      } else {
         BlockPos var3 = var0.getBlockPos().down();
         return Optional.of(new OptionalUtils_2.Inner1(var3, Direction.UP, var2));
      }
   }

   public static boolean check(BlockPos var0) {
      if (class310.world != null && var0 != null) {
         BlockPos var1 = var0.down();
         if (var1.getY() < class310.world.getBottomY()) {
            return false;
         } else {
            BlockState var2 = class310.world.getBlockState(var1);

            try {
               if (var2.getHardness(class310.world, var1) < 0.0F) {
                  return false;
               }
            } catch (Throwable var5) {
            }

            if (!var2.getFluidState().isEmpty()) {
               Fluid var3 = var2.getFluidState().getFluid();
               String var4 = Registries.FLUID.getId(var3).getPath();
               if (var4.contains("lava")) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static int intOf(BlockPos var0, int var1) {
      if (var0 == null) {
         return 0;
      } else {
         int var2 = Math.max(0, var0.getY() - (var1 + 1));
         return var2 * 12;
      }
   }

public record Inner1(BlockPos target, Direction hitFace, boolean waitForFall) {

}
}

