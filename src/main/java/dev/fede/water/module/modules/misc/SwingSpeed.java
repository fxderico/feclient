package dev.fede.water.module.modules.misc;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;

public final class SwingSpeed extends Module {
   public static SwingSpeed instance;
   private final Setting<Float> swingSpeed = new Setting<>("Swing Speed", 1.0F, 0.1F, 2.0F);

   public SwingSpeed() {
      super("SwingSpeed", Category.c);
      instance = this;
      this.addSetting(this.swingSpeed);
   }

   public float getSwingSpeed() {
      float var1 = this.swingSpeed.getValue() == null ? 1.0F : this.swingSpeed.getValue();
      return var1 < 0.1F ? 0.1F : Math.min(var1, 2.0F);
   }
}

