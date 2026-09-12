package dev.fede.nyx.util;

public final class CornerBoxESPModuleUtil {
   private CornerBoxESPModuleUtil() {
   }

   public static int intOf(int var0, double var1, double var3) {
      return intOf2(var0, var1, var3, 0.0F);
   }

   public static int intOf2(int var0, double var1, double var3, float var5) {
      int var6 = var0 >>> 24 & 0xFF;
      if (var6 != 0 && !(var3 <= 0.0)) {
         double var7 = var3 * var3;
         double var9;
         if (var1 >= var7) {
            var9 = 0.0;
         } else if (var1 <= 0.0) {
            var9 = 1.0;
         } else {
            var9 = 1.0 - var1 / var7;
         }

         float var11 = floatOf2(var5);
         float var12 = (float)var9;
         if (var12 < var11) {
            var12 = var11;
         }

         if (var12 > 1.0F) {
            var12 = 1.0F;
         }

         int var13 = Math.round(var6 * var12);
         if (var13 < 0) {
            var13 = 0;
         }

         if (var13 > 255) {
            var13 = 255;
         }

         return var0 & 16777215 | var13 << 24;
      } else {
         return var0;
      }
   }

   private static float floatOf2(float var0) {
      if (var0 < 0.0F) {
         return 0.0F;
      } else {
         return var0 > 1.0F ? 1.0F : var0;
      }
   }
}

