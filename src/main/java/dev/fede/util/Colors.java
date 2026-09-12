package dev.fede.util;

public final class Colors {
   private Colors() {
   }

   public static int argb(int a, int r, int g, int b) {
      return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
   }

   public static int rgb(int r, int g, int b) {
      return argb(255, r, g, b);
   }

   public static int withAlpha(int color, int alpha) {
      return color & 16777215 | (alpha & 0xFF) << 24;
   }

   public static int withAlpha(int color, float alpha) {
      return withAlpha(color, (int)(Math.clamp(alpha, 0.0F, 1.0F) * 255.0F));
   }

   public static int alpha(int color) {
      return color >>> 24 & 0xFF;
   }

   public static int red(int color) {
      return color >> 16 & 0xFF;
   }

   public static int green(int color) {
      return color >> 8 & 0xFF;
   }

   public static int blue(int color) {
      return color & 0xFF;
   }

   public static int lerp(int from, int to, float t) {
      t = Math.clamp(t, 0.0F, 1.0F);
      int a = (int)(alpha(from) + (alpha(to) - alpha(from)) * t);
      int r = (int)(red(from) + (red(to) - red(from)) * t);
      int g = (int)(green(from) + (green(to) - green(from)) * t);
      int b = (int)(blue(from) + (blue(to) - blue(from)) * t);
      return argb(a, r, g, b);
   }

   public static int lighten(int color, float t) {
      return lerp(color, withAlpha(-1, alpha(color)), t);
   }

   public static int darken(int color, float t) {
      return lerp(color, withAlpha(-16777216, alpha(color)), t);
   }

   public static float[] rgbToHsv(int argb) {
      float r = red(argb) / 255.0F;
      float g = green(argb) / 255.0F;
      float b = blue(argb) / 255.0F;
      float max = Math.max(r, Math.max(g, b));
      float min = Math.min(r, Math.min(g, b));
      float delta = max - min;
      float h;
      if (delta == 0.0F) {
         h = 0.0F;
      } else if (max == r) {
         h = 60.0F * ((g - b) / delta % 6.0F);
      } else if (max == g) {
         h = 60.0F * ((b - r) / delta + 2.0F);
      } else {
         h = 60.0F * ((r - g) / delta + 4.0F);
      }

      if (h < 0.0F) {
         h += 360.0F;
      }

      float s = max == 0.0F ? 0.0F : delta / max;
      return new float[]{h, s, max};
   }

   public static int hsvToRgb(float h, float s, float v) {
      float c = v * s;
      float x = c * (1.0F - Math.abs(h / 60.0F % 2.0F - 1.0F));
      float m = v - c;
      float r;
      float g;
      float b;
      if (h < 60.0F) {
         r = c;
         g = x;
         b = 0.0F;
      } else if (h < 120.0F) {
         r = x;
         g = c;
         b = 0.0F;
      } else if (h < 180.0F) {
         r = 0.0F;
         g = c;
         b = x;
      } else if (h < 240.0F) {
         r = 0.0F;
         g = x;
         b = c;
      } else if (h < 300.0F) {
         r = x;
         g = 0.0F;
         b = c;
      } else {
         r = c;
         g = 0.0F;
         b = x;
      }

      return rgb(Math.round((r + m) * 255.0F), Math.round((g + m) * 255.0F), Math.round((b + m) * 255.0F));
   }
}

