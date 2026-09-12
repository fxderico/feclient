package dev.fede.water.module.modules.misc;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;

public class NameProtect extends Module {
   public final Setting<String> fakeName = new Setting<>("FakeName", "Player");
   public static NameProtect instance;

   public NameProtect() {
      super("NameProtect", Category.c);
      instance = this;
      this.addSetting(this.fakeName);
   }

   public String getFakeName() {
      return this.fakeName.getValue();
   }
}

