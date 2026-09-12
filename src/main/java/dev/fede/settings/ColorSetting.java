package dev.fede.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ColorSetting extends Setting<Integer> {
   public ColorSetting(String name, String description, int defaultArgb) {
      super(name, description, defaultArgb);
   }

   public int red() {
      return this.get() >> 16 & 0xFF;
   }

   public int green() {
      return this.get() >> 8 & 0xFF;
   }

   public int blue() {
      return this.get() & 0xFF;
   }

   public int alpha() {
      return this.get() >>> 24 & 0xFF;
   }

   public void setRed(int r) {
      this.set(this.get() & -16711681 | (r & 0xFF) << 16);
   }

   public void setGreen(int g) {
      this.set(this.get() & -65281 | (g & 0xFF) << 8);
   }

   public void setBlue(int b) {
      this.set(this.get() & -256 | b & 0xFF);
   }

   public String hex() {
      return String.format("#%06X", this.get() & 16777215);
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive(this.value);
   }

   @Override
   public void fromJson(JsonElement element) {
      if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
         this.value = element.getAsInt();
      }
   }
}

