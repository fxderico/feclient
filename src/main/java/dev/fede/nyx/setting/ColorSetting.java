package dev.fede.nyx.setting;

import java.awt.Color;

public class ColorSetting extends Setting {
   private int value;

   public ColorSetting(String name, int argb) {
      super(name);
      this.value = argb;
   }

   public int getValue() {
      return this.value;
   }

   public void setValue(int argb) {
      this.value = argb;
   }

   public int getAlpha() {
      return this.value >>> 24 & 0xFF;
   }

   public int getRed() {
      return this.value >>> 16 & 0xFF;
   }

   public int getGreen() {
      return this.value >>> 8 & 0xFF;
   }

   public int getBlue() {
      return this.value & 0xFF;
   }

   public void setAlpha(int a) {
      this.value = this.value & 16777215 | (a & 0xFF) << 24;
   }

   public void setRed(int r) {
      this.value = this.value & -16711681 | (r & 0xFF) << 16;
   }

   public void setGreen(int g) {
      this.value = this.value & -65281 | (g & 0xFF) << 8;
   }

   public void setBlue(int b) {
      this.value = this.value & -256 | b & 0xFF;
   }

   public float getHue() {
      return this.hsb()[0];
   }

   public float getSaturation() {
      return this.hsb()[1];
   }

   public float getBrightness() {
      return this.hsb()[2];
   }

   public void setHSB(float hue, float saturation, float brightness) {
      float var4 = clamp01(hue);
      float var5 = clamp01(saturation);
      float var6 = clamp01(brightness);
      int var7 = Color.HSBtoRGB(var4, var5, var6) & 16777215;
      this.value = this.getAlpha() << 24 | var7;
   }

   public void setHue(float hue) {
      this.setHSB(hue, this.getSaturation(), this.getBrightness());
   }

   public void setSaturation(float saturation) {
      this.setHSB(this.getHue(), saturation, this.getBrightness());
   }

   public void setBrightness(float brightness) {
      this.setHSB(this.getHue(), this.getSaturation(), brightness);
   }

   private float[] hsb() {
      return Color.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null);
   }

   private static float clamp01(float f) {
      if (f < 0.0F) {
         return 0.0F;
      } else {
         return f > 1.0F ? 1.0F : f;
      }
   }

   public String toHex() {
      return String.format("#%08X", this.value);
   }

   public void fromHex(String var1) {
      if (var1 != null) {
         String var2 = var1.trim();
         if (var2.startsWith("#")) {
            var2 = var2.substring(1);
         }

         try {
            if (var2.length() == 6) {
               this.value = 0xFF000000 | (int)(Long.parseLong(var2, 16) & 16777215L);
            } else if (var2.length() == 8) {
               this.value = (int)(Long.parseLong(var2, 16) & 4294967295L);
            }
         } catch (NumberFormatException var4) {
         }
      }
   }
}

