package dev.fede.nyx.util;

public final class CodeEngineScreenUtil2 {
   public static final int intVal = -12976364;
   public static final int intVal2 = -1072557550;
   public static final int intVal3 = -15066598;
   public static final int intVal4 = -14671840;
   public static final int intVal5 = -1513240;
   public static final int intVal6 = -7697782;

   private CodeEngineScreenUtil2() {
   }

   public static int intOf(int var0, int var1, int var2, int var3) {
      return (var3 & 0xFF) << 24 | (var0 & 0xFF) << 16 | (var1 & 0xFF) << 8 | var2 & 0xFF;
   }

   public static int intOf3(int var0, int var1) {
      return var0 & 16777215 | (var1 & 0xFF) << 24;
   }

   public static int intOf2(int var0, int var1, float var2) {
      if (var2 <= 0.0F) {
         return var0;
      } else if (var2 >= 1.0F) {
         return var1;
      } else {
         int var3 = var0 >>> 24 & 0xFF;
         int var4 = var0 >>> 16 & 0xFF;
         int var5 = var0 >>> 8 & 0xFF;
         int var6 = var0 & 0xFF;
         int var7 = var1 >>> 24 & 0xFF;
         int var8 = var1 >>> 16 & 0xFF;
         int var9 = var1 >>> 8 & 0xFF;
         int var10 = var1 & 0xFF;
         int var11 = var3 + Math.round((var7 - var3) * var2);
         int var12 = var4 + Math.round((var8 - var4) * var2);
         int var13 = var5 + Math.round((var9 - var5) * var2);
         int var14 = var6 + Math.round((var10 - var6) * var2);
         return var11 << 24 | var12 << 16 | var13 << 8 | var14;
      }
   }

   public static int intOf4(int var0, float var1, float var2) {
      float var3 = (float)((System.currentTimeMillis() + var0) % 4000L) / 4000.0F;
      return 0xFF000000 | intOf5(var3, var1, var2) & 16777215;
   }

   public static int intOf5(float var0, float var1, float var2) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      if (var1 == 0.0F) {
         var3 = var4 = var5 = (int)(var2 * 255.0F + 0.5F);
      } else {
         float var6 = (var0 - (float)Math.floor(var0)) * 6.0F;
         float var7 = var6 - (float)Math.floor(var6);
         float var8 = var2 * (1.0F - var1);
         float var9 = var2 * (1.0F - var1 * var7);
         float var10 = var2 * (1.0F - var1 * (1.0F - var7));
         switch ((int)var6) {
            case 0:
               var3 = (int)(var2 * 255.0F + 0.5F);
               var4 = (int)(var10 * 255.0F + 0.5F);
               var5 = (int)(var8 * 255.0F + 0.5F);
               break;
            case 1:
               var3 = (int)(var9 * 255.0F + 0.5F);
               var4 = (int)(var2 * 255.0F + 0.5F);
               var5 = (int)(var8 * 255.0F + 0.5F);
               break;
            case 2:
               var3 = (int)(var8 * 255.0F + 0.5F);
               var4 = (int)(var2 * 255.0F + 0.5F);
               var5 = (int)(var10 * 255.0F + 0.5F);
               break;
            case 3:
               var3 = (int)(var8 * 255.0F + 0.5F);
               var4 = (int)(var9 * 255.0F + 0.5F);
               var5 = (int)(var2 * 255.0F + 0.5F);
               break;
            case 4:
               var3 = (int)(var10 * 255.0F + 0.5F);
               var4 = (int)(var8 * 255.0F + 0.5F);
               var5 = (int)(var2 * 255.0F + 0.5F);
               break;
            case 5:
               var3 = (int)(var2 * 255.0F + 0.5F);
               var4 = (int)(var8 * 255.0F + 0.5F);
               var5 = (int)(var9 * 255.0F + 0.5F);
         }
      }

      return var3 << 16 | var4 << 8 | var5;
   }
}

