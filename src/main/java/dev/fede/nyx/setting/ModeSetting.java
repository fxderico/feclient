package dev.fede.nyx.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting {
   private String mode;
   private final List<String> modes;

   public ModeSetting(String name, String mode, List<String> modes) {
      super(name);
      this.modes = new ArrayList<>(modes);
      this.mode = mode;
   }

   public ModeSetting(String name, String mode, String... modes) {
      this(name, mode, Arrays.asList(modes));
   }

   public String getMode() {
      return this.mode;
   }

   public void setMode(String mode) {
      if (this.modes.contains(mode)) {
         this.mode = mode;
      }
   }

   public boolean check(String mode) {
      return this.mode.equalsIgnoreCase(mode);
   }

   public List<String> getModes() {
      return this.modes;
   }

   public int getIndex() {
      return this.modes.indexOf(this.mode);
   }

   public void cycle() {
      int var1 = this.modes.indexOf(this.mode) + 1;
      if (var1 >= this.modes.size()) {
         var1 = 0;
      }

      this.mode = this.modes.get(var1);
   }

   public void cycleBack() {
      int var1 = this.modes.indexOf(this.mode) - 1;
      if (var1 < 0) {
         var1 = this.modes.size() - 1;
      }

      this.mode = this.modes.get(var1);
   }
}

