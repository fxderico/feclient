package dev.fede.theme;

import dev.fede.util.Colors;

public class Theme {
   private String name;
   private int accent;
   private boolean custom;

   public Theme(String name, int accent, boolean custom) {
      this.name = name;
      this.accent = accent;
      this.custom = custom;
   }

   public String getName() {
      return this.name;
   }

   public int accent() {
      return this.accent;
   }

   public void setAccent(int accent) {
      this.accent = accent;
   }

   public boolean isCustom() {
      return this.custom;
   }

   public int accentBright() {
      return Colors.lighten(this.accent(), 0.35F);
   }

   public int accentHover() {
      return Colors.withAlpha(this.accent(), 0.35F);
   }

   public int background() {
      return Colors.withAlpha(-15593450, 0.9F);
   }

   public int backgroundTo() {
      return Colors.withAlpha(-15067872, 0.9F);
   }

   public int headerTop() {
      return Colors.withAlpha(-14870490, 0.95F);
   }

   public int headerBottom() {
      return Colors.withAlpha(-15396839, 0.95F);
   }

   public int moduleActiveFill() {
      return Colors.withAlpha(Colors.lerp(-15593450, this.accent(), 0.22F), 0.95F);
   }

   public int textPrimary() {
      return -1185038;
   }

   public int textMuted() {
      return -6646872;
   }

   public int textDisabled() {
      return -9607552;
   }

   public int statusEnabled() {
      return -11671924;
   }

   public int statusDisabled() {
      return -11054753;
   }
}

