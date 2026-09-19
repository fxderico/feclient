package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;

public class MobEspModule extends Module {
   public final ModeSetting style = this.addSetting(new ModeSetting("Style", "Highlight style", "Outline", "Box", "Outline", "Glow", "2D Box"));
   public final ColorSetting hostile = this.addSetting(new ColorSetting("Hostile", "Hostile color", -45715));
   public final ColorSetting passive = this.addSetting(new ColorSetting("Passive", "Passive color", -12654960));
   public final BooleanSetting passiveToo = this.addSetting(new BooleanSetting("Passive Too", "Include passive mobs", false));
   public final BooleanSetting tracers = this.addSetting(new BooleanSetting("Tracers", "Draw lines from the crosshair to each mob", false));
   public final SliderSetting range = this.addSetting(new SliderSetting("Range", "Max distance to highlight mobs at", 100.0, 1.0, 100.0, 1.0, "c"));

   public MobEspModule() {
      super("MobESP", "Highlights hostile mobs", Category.RENDER);
   }
}

