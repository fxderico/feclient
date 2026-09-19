package dev.fede.module.impl;

import dev.fede.FeClient;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.Colors;
import java.util.Locale;

public class CustomGlintModule extends Module {
   public final ModeSetting style = this.addSetting(
      new ModeSetting(
         "Style",
         "Glint texture. Default recolors the vanilla foil; the rest are custom patterns",
         "Default",
         "Default",
         "Ender",
         "Void",
         "Galaxy",
         "Toxic",
         "Amethyst",
         "Cyber",
         "Solar",
         "Prismatic",
         "Abyss"
      )
   );
   public final ModeSetting mode = this.addSetting(new ModeSetting("Mode", "Where the glint color comes from", "Solid", "Solid", "Rainbow", "Theme"));
   public final ColorSetting color = this.addSetting(new ColorSetting("Color", "Glint color", -49508));
   public final SliderSetting strength = this.addSetting(new SliderSetting("Strength", "Glint intensity", 60.0, 0.0, 100.0, 5.0, "%"));
   public final SliderSetting speed = this.addSetting(new SliderSetting("Speed", "Rainbow cycle speed", 100.0, 10.0, 300.0, 10.0, "%"));

   public CustomGlintModule() {
      super("CustomGlint", "Recolors or restyles the enchantment glint", Category.ADDONS);
      this.mode.visibleWhen(() -> this.style.check("Default"));
      this.color.visibleWhen(() -> this.style.check("Default") && this.mode.check("Solid"));
      this.speed.visibleWhen(() -> this.style.check("Default") && this.mode.check("Rainbow"));
   }

   public boolean isActive() {
      return this.isEnabled();
   }

   public boolean usesTexture() {
      return !this.style.check("Default");
   }

   public String textureName() {
      return this.style.get().toLowerCase(Locale.ROOT);
   }

   public float strengthUnit() {
      return this.strength.getFloat() / 100.0F;
   }

   public int glintColor() {
      int var10000;
      label18: {
         String s = this.mode.get();
         byte r = -1;
         switch (s.hashCode()) {
            case -1656737386:
               if (s.equals("Rainbow")) {
                  var10000 = this.rainbow();
                  break label18;
               }
               break;
            case 80774569:
               if (s.equals("Theme")) {
                  var10000 = FeClient.themes().current().accent();
                  break label18;
               }
         }

         var10000 = this.color.get();
      }

      int base = var10000;
      float s = this.strength.getFloat() / 100.0F;
      int r = Math.round(Colors.red(base) * s);
      int g = Math.round(Colors.green(base) * s);
      int b = Math.round(Colors.blue(base) * s);
      return Colors.rgb(r, g, b);
   }

   private int rainbow() {
      double seconds = System.nanoTime() % 1000000000000L / 1.E9;
      double hue = seconds * 36.0 * (this.speed.getFloat() / 100.0F) % 360.0;
      return Colors.hsvToRgb((float)hue, 0.85F, 1.0F);
   }
}



