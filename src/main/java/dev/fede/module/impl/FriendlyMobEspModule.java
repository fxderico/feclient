package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;

/**
 * Separate from MobESP's "Passive Too" checkbox — this is its own toggle,
 * own color, own range, so you can run friendly-mob esp independently of
 * (or with different settings from) hostile mob esp.
 */
public class FriendlyMobEspModule extends Module {
   public final ModeSetting style = this.addSetting(new ModeSetting("Style", "Highlight style", "Outline", "Box", "Outline", "Glow", "2D Box"));
   public final ColorSetting color = this.addSetting(new ColorSetting("Color", "Highlight color", -12278808));
   public final BooleanSetting villagers = this.addSetting(new BooleanSetting("Villagers", "Include villagers & wandering traders", true));
   public final BooleanSetting tamed = this.addSetting(new BooleanSetting("Tamed", "Include tamed wolves, cats & parrots", true));
   public final BooleanSetting tracers = this.addSetting(new BooleanSetting("Tracers", "Draw lines from the crosshair to each mob", false));
   public final SliderSetting range = this.addSetting(new SliderSetting("Range", "Max distance to highlight mobs at", 100.0, 1.0, 100.0, 1.0, "c"));

   public FriendlyMobEspModule() {
      super("FriendlyMobESP", "Highlights all friendly/passive mobs through walls", Category.RENDER);
   }
}
