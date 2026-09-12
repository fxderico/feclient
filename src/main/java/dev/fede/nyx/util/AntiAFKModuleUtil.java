package dev.fede.nyx.util;

public final class AntiAFKModuleUtil {
   private static volatile float[] floatArray = null;

   private AntiAFKModuleUtil() {
   }

   public static void run(float var0, float var1) {
      floatArray = new float[]{var0, var1};
   }

   public static float[] getFloatArray() {
      return floatArray;
   }

   public static boolean isEnabled2() {
      return floatArray != null;
   }

   public static void run2() {
      floatArray = null;
   }
}

