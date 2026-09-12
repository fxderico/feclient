package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.render.HoleEspRenderer;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.ModeSetting;

public class DebugHoleEspModule extends Module {
   public final ModeSetting depth = this.addSetting(new ModeSetting("Depth", "Hole depth check", "2", "2", "3", "Any"));
   public final ColorSetting safe = this.addSetting(new ColorSetting("Safe", "Safe hole color", -12654960));
   public final ColorSetting unsafe = this.addSetting(new ColorSetting("Unsafe", "Unsafe hole color", -45715));
   public final BooleanSetting tracers = this.addSetting(new BooleanSetting("Tracers", "Draw lines from the crosshair to each hole", false));

   public DebugHoleEspModule() {
      super("DebugHoleESP", "Marks safe crystal-pvp holes", Category.RENDER);
   }

   @Override
   public void onTick() {
      HoleEspRenderer.scan(this);
   }

   @Override
   protected void onDisable() {
      HoleEspRenderer.clear();
   }
}

