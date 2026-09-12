package dev.fede.nyx.setting;

public class BooleanSetting extends Setting {
   private boolean value;

   public BooleanSetting(String name, boolean value) {
      super(name);
      this.value = value;
   }

   public boolean getValue() {
      return this.value;
   }

   public void setValue(boolean value) {
      this.value = value;
   }

   public void toggle() {
      this.value = !this.value;
   }
}

