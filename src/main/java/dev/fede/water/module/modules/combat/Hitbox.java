package dev.fede.water.module.modules.combat;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;

public final class Hitbox extends Module {
   public static Hitbox INSTANCE;
   public final Setting<Float> size = new Setting<>("Expand", 1.0F, 0.5F, 2.0F);

   public Hitbox() {
      super("Hitbox", Category.field_a_1);
      this.addSetting(this.size);
      INSTANCE = this;
   }
}

