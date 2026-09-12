package dev.fede.water.setting;

import java.util.List;

public final class ModeSetting extends Setting<String> {
   private final List<String> g;
   private final List<String> h;

   public ModeSetting(String name, String defaultValue, String... modes) {
      this(name, defaultValue, new String[0], modes);
   }

   public ModeSetting(String name, String defaultValue, String[] legacyNames, String... modes) {
      super(name, defaultValue);
      if (modes != null && modes.length != 0) {
         this.g = List.of(modes);
         this.h = legacyNames == null ? List.of() : List.of(legacyNames);
         this.setValue(defaultValue);
      } else {
         throw new IllegalArgumentException("ModeSetting requires at least one mode");
      }
   }

   public void be() {
      this.setValue(this.c(1));
   }

   public void bf() {
      this.setValue(this.c(-1));
   }

   public boolean d(String mode) {
      return this.h(mode).equalsIgnoreCase(this.getValue());
   }

   public void setValue(String value) {
      super.setValue(this.g(value));
   }

   @Override
   public boolean matchesName(String settingName) {
      if (super.matchesName(settingName)) {
         return true;
      } else {
         settingName = this.h(settingName);

         for (String var3 : this.h) {
            if (var3.equalsIgnoreCase(settingName)) {
               return true;
            }
         }

         return false;
      }
   }

   private String c(int direction) {
      int var2 = this.g.size();
      if (var2 == 0) {
         return "";
      } else {
         String var3 = this.getValue();

         for (int var4 = 0; var4 < var2; var4++) {
            if (this.g.get(var4).equalsIgnoreCase(var3)) {
               direction = Math.floorMod(var4 + direction, var2);
               return this.g.get(direction);
            }
         }

         return this.g.getFirst();
      }
   }

   private String g(String value) {
      value = this.h(value);

      for (String var3 : this.g) {
         if (var3.equalsIgnoreCase(value)) {
            return var3;
         }
      }

      return this.g.getFirst();
   }

   private String h(String value) {
      return value == null ? "" : value.trim();
   }
}

