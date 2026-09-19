package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;

public class PlayerEspModule extends Module {
   // 3D Box/Outline draw a real world-space wireframe around the hitbox
   // (through walls, perspective-correct). Glow is the same box with a
   // thicker, brighter outline + soft fill approximating a glow — a real
   // blurred silhouette glow (like liquidbounce/water's shader-based one)
   // is a bigger lift than tonight's budget; this is the honest middle
   // ground. 2D Box projects the hitbox corners to screen space and draws
   // a flat on-screen rectangle instead — doesn't rotate/foreshorten with
   // the entity, liquidbounce-style.
   public final ModeSetting style = this.addSetting(new ModeSetting("Style", "Highlight style", "Outline", "Box", "Outline", "Glow", "2D Box"));
   public final ColorSetting color = this.addSetting(new ColorSetting("Color", "Highlight color", -49508));
   public final BooleanSetting tracers = this.addSetting(new BooleanSetting("Tracers", "Draw lines from the crosshair to each player", false));
   public final SliderSetting range = this.addSetting(new SliderSetting("Range", "Max distance to highlight players at", 100.0, 1.0, 100.0, 1.0, "c"));

   public PlayerEspModule() {
      super("PlayerESP", "Highlights players through walls", Category.RENDER);
   }
}

