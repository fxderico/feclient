package dev.fede.nyx.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class AutoCrystalModuleUtil {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();

   private AutoCrystalModuleUtil() {
   }

   public static float[] floatArrayOf(Vec3d var0) {
      Vec3d var1 = class310.player.getEyePos();
      double var2 = var0.x - var1.x;
      double var4 = var0.y - var1.y;
      double var6 = var0.z - var1.z;
      double var8 = Math.sqrt(var2 * var2 + var6 * var6);
      float var10 = (float)Math.toDegrees(Math.atan2(var6, var2)) - 90.0F;
      float var11 = (float)(-Math.toDegrees(Math.atan2(var4, var8)));
      return new float[]{var10, MathHelper.clamp(var11, -90.0F, 90.0F)};
   }

   public static float[] floatArrayOf2(Entity var0) {
      return floatArrayOf(var0.getBoundingBox().getCenter());
   }

   public static float floatOf(float[] var0) {
      float var1 = MathHelper.wrapDegrees(var0[0] - class310.player.getYaw());
      float var2 = MathHelper.wrapDegrees(var0[1] - class310.player.getPitch());
      return MathHelper.sqrt(var1 * var1 + var2 * var2);
   }
}

