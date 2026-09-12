package dev.fede.theme;

import dev.fede.util.Colors;

public class RainbowTheme extends Theme {
   public RainbowTheme() {
      super("Rainbow", -49508, false);
   }

   @Override
   public int accent() {
      double seconds = System.nanoTime() % 1000000000000L / 1.E9;
      return Colors.hsvToRgb((float)(seconds * 36.0 % 360.0), 0.72F, 1.0F);
   }
}

