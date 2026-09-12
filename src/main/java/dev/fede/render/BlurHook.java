package dev.fede.render;

public final class BlurHook {
   private static volatile float override = -1.0F;

   private BlurHook() {
   }

   public static void set(float radius) {
      override = radius;
   }

   public static void clear() {
      override = -1.0F;
   }

   public static int apply(int vanillaValue) {
      float value = override;
      return value < 0.0F ? vanillaValue : Math.round(Math.clamp(value, 0.0F, 10.0F));
   }

   public static boolean isActive() {
      return override >= 0.0F;
   }
}

