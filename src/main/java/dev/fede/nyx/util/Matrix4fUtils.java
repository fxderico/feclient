package dev.fede.nyx.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class Matrix4fUtils {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();
   private static Matrix4f matrix4f = new Matrix4f();
   private static Matrix4f matrix4f2 = new Matrix4f();
   private static Vec3d class243 = Vec3d.ZERO;

   private Matrix4fUtils() {
   }

   public static void run(Matrix4f var0, Matrix4f var1, Vec3d var2) {
      matrix4f = new Matrix4f(var0);
      matrix4f2 = new Matrix4f(var1);
      class243 = var2;
   }

   public static double[] doubleArrayOf(Vec3d var0) {
      float var1 = (float)(var0.x - class243.x);
      float var2 = (float)(var0.y - class243.y);
      float var3 = (float)(var0.z - class243.z);
      Matrix4f var4 = new Matrix4f(matrix4f2).mul(matrix4f);
      Vector4f var5 = new Vector4f(var1, var2, var3, 1.0F);
      var5.mul(var4);
      if (var5.w() <= 0.0F) {
         return null;
      } else {
         float var6 = var5.x() / var5.w();
         float var7 = var5.y() / var5.w();
         int var8 = class310.getWindow().getScaledWidth();
         int var9 = class310.getWindow().getScaledHeight();
         double var10 = (var6 * 0.5 + 0.5) * var8;
         double var12 = (1.0 - (var7 * 0.5 + 0.5)) * var9;
         return new double[]{var10, var12};
      }
   }
}

