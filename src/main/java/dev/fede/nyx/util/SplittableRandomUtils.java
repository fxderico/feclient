package dev.fede.nyx.util;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class SplittableRandomUtils {
   private static final Map<Long, SplittableRandomUtils.Inner1> map = new HashMap<>();
   private static final double doubleVal = 8.0;
   private static final double doubleVal2 = 0.25;
   private static final double doubleVal3 = 0.2;
   private static final double doubleVal4 = 12.0;
   private static long longVal = 0L;

   private SplittableRandomUtils() {
   }

   public static float[] floatArrayOf(BlockPos var0, float var1, float var2) {
      if (var0 == null) {
         return new float[]{var1, var2};
      } else {
         AutoTunnelHelper var3 = AutoTunnelHelper.getk();
         if (!var3.isEnabled2()) {
            return new float[]{var1, var2};
         } else {
            SplittableRandomUtils.Inner1 var4 = map.computeIfAbsent(var0.asLong(), k -> raOf(var0, k));
            var4.doubleVal = doubleOf(var4.doubleVal, var4.splittableRandom, 0.25);
            var4.doubleVal2 = doubleOf(var4.doubleVal2, var4.splittableRandom2, 0.2);
            double var5 = var4.intVal > 0 ? 1.5 : 1.0;
            if (var4.intVal > 0) {
               var4.intVal--;
            }

            double var7 = doubleOf3(var4.doubleVal * var5, -12.0, 12.0);
            double var9 = doubleOf3(var4.doubleVal2 * var5, -12.0, 12.0);
            if (var3.check(0.05)) {
               var7 += var3.check(0.5) ? 0.1 : -0.1;
               var9 += var3.check(0.5) ? 0.05 : -0.05;
            }

            float var11 = MathHelper.wrapDegrees((float)(var1 + var7));
            float var12 = MathHelper.clamp((float)(var2 + var9), -90.0F, 90.0F);
            if (!check(var0, var11, var12)) {
               var11 = MathHelper.wrapDegrees((float)(var1 + var7 * 0.5));
               var12 = MathHelper.clamp((float)(var2 + var9 * 0.5), -90.0F, 90.0F);
               if (!check(var0, var11, var12)) {
                  return new float[]{var1, var2};
               }
            }

            var4.doubleVal3 = var11;
            var4.doubleVal4 = var12;
            return new float[]{var11, var12};
         }
      }
   }

   public static Vec3d class243Of(BlockPos var0, Vec3d var1, Vec3d var2, float var3, float var4) {
      if (var0 != null && var1 != null && var2 != null) {
         AutoTunnelHelper var5 = AutoTunnelHelper.getk();
         if (!var5.isEnabled2()) {
            return var2;
         } else {
            float[] var6 = floatArrayOf(var0, var3, var4);
            double var7 = var1.distanceTo(var2);
            double var9 = Math.toRadians(var6[0] + 90.0);
            double var11 = Math.toRadians(var6[1]);
            double var13 = Math.cos(var11) * Math.cos(var9);
            double var15 = -Math.sin(var11);
            double var17 = Math.cos(var11) * Math.sin(var9);
            return var1.add(var13 * var7, var15 * var7, var17 * var7);
         }
      } else {
         return var2;
      }
   }

   public static void run4(BlockPos var0) {
      if (var0 != null) {
         map.remove(var0.asLong());
      }
   }

   public static void run() {
      map.clear();
   }

   private static double doubleOf(double var0, SplittableRandom var2, double var3) {
      double var7 = var0 * (1.0 - 1.0 / 8.0);
      double var9 = var3 * Math.sqrt(2.0 * 1.0 / 8.0) * doubleOf2(var2);
      return var7 + var9;
   }

   private static double doubleOf2(SplittableRandom var0) {
      double var1 = Math.max(1.0E-12, var0.nextDouble());
      double var3 = var0.nextDouble();
      return Math.sqrt(-2.0 * Math.log(var1)) * Math.cos((Math.PI * 2) * var3);
   }

   private static double doubleOf3(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }

   private static long longOf(BlockPos var0) {
      long var1 = 0L;

      try {
         ClientPlayerEntity var3 = MinecraftClient.getInstance().player;
         if (var3 != null) {
            UUID var4 = var3.getUuid();
            var1 = var4.getMostSignificantBits() ^ var4.getLeastSignificantBits();
         }
      } catch (Throwable var5) {
      }

      long var6 = var0.asLong();
      var6 ^= var1;
      var6 ^= longVal;
      var6 ^= var6 >>> 33;
      var6 *= -49064778989728563L;
      var6 ^= var6 >>> 33;
      var6 *= -4265267296055464877L;
      return var6 ^ var6 >>> 33;
   }

   private static boolean check(BlockPos var0, float var1, float var2) {
      try {
         MinecraftClient var3 = MinecraftClient.getInstance();
         ClientPlayerEntity var4 = var3.player;
         if (var4 != null && var3.world != null) {
            Vec3d var5 = var4.getEyePos();
            double var6 = Math.toRadians(var1 + 90.0);
            double var8 = Math.toRadians(var2);
            double var10 = Math.cos(var8) * Math.cos(var6);
            double var12 = -Math.sin(var8);
            double var14 = Math.cos(var8) * Math.sin(var6);
            Vec3d var18 = var5.add(var10 * 5.0, var12 * 5.0, var14 * 5.0);
            RaycastContext var19 = new RaycastContext(var5, var18, ShapeType.OUTLINE, FluidHandling.NONE, var4);
            BlockHitResult var20 = var3.world.raycast(var19);
            return var20 != null && var20.getType() == Type.BLOCK ? var20.getBlockPos().equals(var0) : false;
         } else {
            return true;
         }
      } catch (Throwable var21) {
         return true;
      }
   }

   private static SplittableRandomUtils.Inner1 raOf(BlockPos var0, Long var1) {
      return new SplittableRandomUtils.Inner1(longOf(var0));
   }

   static {
      longVal = System.nanoTime();
   }

final static class Inner1 {
   double doubleVal;
   double doubleVal2;
   int intVal;
   double doubleVal3 = Double.NaN;
   double doubleVal4 = Double.NaN;
   final SplittableRandom splittableRandom;
   final SplittableRandom splittableRandom2;

   Inner1(long var1) {
      this.splittableRandom = new SplittableRandom(var1);
      this.splittableRandom2 = new SplittableRandom(var1 ^ -7046029254386353131L);
      this.intVal = 3;
   }
}
}

