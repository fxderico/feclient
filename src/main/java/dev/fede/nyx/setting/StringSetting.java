package dev.fede.nyx.setting;

public class StringSetting extends Setting {
   private String value;
   private final int maxLen;

   public StringSetting(String name, String def, int maxLen) {
      super(name);
      this.maxLen = maxLen;
      this.value = this.clamp(def);
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = this.clamp(value);
   }

   public int getMaxLen() {
      return this.maxLen;
   }

   private String clamp(String s) {
      if (s == null) {
         return "";
      } else {
         return this.maxLen > 0 && s.length() > this.maxLen ? s.substring(0, this.maxLen) : s;
      }
   }
}

