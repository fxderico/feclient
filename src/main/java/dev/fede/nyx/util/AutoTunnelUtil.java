package dev.fede.nyx.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class AutoTunnelUtil {
   private static final double doubleVal = 0.49;

   private AutoTunnelUtil() {
   }

   public static float[] floatArrayOf(Vec3d var0, BlockPos var1, Direction var2) {
      double var3 = var1.getX() + 0.5 + var2.getOffsetX() * 0.49;
      double var5 = var1.getY() + 0.5 + var2.getOffsetY() * 0.49;
      double var7 = var1.getZ() + 0.5 + var2.getOffsetZ() * 0.49;
      double var9 = var3 - var0.x;
      double var11 = var5 - var0.y;
      double var13 = var7 - var0.z;
      double var15 = Math.sqrt(var9 * var9 + var13 * var13);
      float var17 = (float)(Math.toDegrees(Math.atan2(var13, var9)) - 90.0);
      float var18 = (float)(-Math.toDegrees(Math.atan2(var11, var15)));
      return new float[]{MathHelper.wrapDegrees(var17), MathHelper.clamp(var18, -90.0F, 90.0F)};
   }
}

