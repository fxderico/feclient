package dev.fede.water.setting;

public final class ToggleableIntegerSetting extends Setting<Integer> {
   private boolean enabled;

   public ToggleableIntegerSetting(String name, boolean enabled, int value, int min, int max) {
      super(name, value, min, max);
      this.enabled = enabled;
   }

   public String q() {
      return this.enabled + "|" + this.getValue();
   }

   public void u(String serialized) {
      if (serialized != null && !serialized.isBlank()) {
         String[] var2 = serialized.split("\\|", 2);

         try {
            if (var2.length == 2) {
               this.enabled = Boolean.parseBoolean(var2[0]);
               this.setValue(Integer.parseInt(var2[1]));
               return;
            }

            this.setValue(Integer.parseInt(serialized));
         } catch (NumberFormatException var3) {
         }
      }
   }
}

