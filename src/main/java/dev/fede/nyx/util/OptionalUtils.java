package dev.fede.nyx.util;

import java.util.Optional;
import java.util.function.LongPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class OptionalUtils {
   private OptionalUtils() {
   }

   public static Optional<OptionalUtils.Inner1> optionalOf(ClientPlayerEntity var0, int var1, int var2) {
      return optionalOf2(var0, var1, var2, null);
   }

   public static Optional<OptionalUtils.Inner1> optionalOf2(ClientPlayerEntity var0, int var1, int var2, LongPredicate var3) {
      if (var0 == null) {
         return Optional.empty();
      } else {
         MinecraftClient var4 = MinecraftClient.getInstance();
         ClientWorld var5 = var4.world;
         if (var5 == null) {
            return Optional.empty();
         } else {
            Direction var6 = var0.getHorizontalFacing();
            Direction var7 = var6.getOpposite();
            BlockPos var8 = var0.getBlockPos();
            Direction var9 = addSetting(var6);
            int[] var10 = intArrayOf(var1);
            int[] var11 = intArrayOf2(var2);

            for (int var15 : var11) {
               for (int var19 : var10) {
                  BlockPos var20 = var8.offset(var6).offset(var9, var15).up(var19);
                  BlockState var21 = var5.getBlockState(var20);
                  if (check(var21, var20) && (var3 == null || !var3.test(var20.asLong()))) {
                     return Optional.of(new OptionalUtils.Inner1(var20.toImmutable(), var7));
                  }
               }
            }

            return Optional.empty();
         }
      }
   }

   private static int[] intArrayOf(int var0) {
      int var1 = Math.max(1, Math.min(3, var0));

      return switch (var1) {
         case 1 -> new int[]{1};
         case 2 -> new int[]{1, 0};
         default -> new int[]{1, 0, 2};
      };
   }

   private static int[] intArrayOf2(int var0) {
      int var1 = Math.max(1, Math.min(3, var0));

      return switch (var1) {
         case 1 -> new int[]{0};
         case 2 -> new int[]{0, 1};
         default -> new int[]{0, -1, 1};
      };
   }

   private static Direction addSetting(Direction var0) {
      return switch (var0) {
         case NORTH, SOUTH -> Direction.EAST;
         case EAST, WEST -> Direction.NORTH;
         default -> Direction.NORTH;
      };
   }

   private static boolean check(BlockState var0, BlockPos var1) {
      if (var0.isAir()) {
         return false;
      } else if (!var0.getFluidState().isEmpty()) {
         return false;
      } else {
         MinecraftClient var2 = MinecraftClient.getInstance();

         try {
            float var3 = var0.getHardness(var2.world, var1);
            if (var3 < 0.0F) {
               return false;
            }
         } catch (Throwable var4) {
         }

         return true;
      }
   }

public record Inner1(BlockPos target, Direction hitFace) {

}
}

