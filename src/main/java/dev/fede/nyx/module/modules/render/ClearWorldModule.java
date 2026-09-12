package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;

public class ClearWorldModule extends Module {
   private final BooleanSetting noWeather = new BooleanSetting("NoWeather", true);
   private final BooleanSetting noFog = new BooleanSetting("NoFog", true);
   private final BooleanSetting noParticles = new BooleanSetting("NoParticles", false);
   private static volatile boolean bool;
   private static volatile boolean bool2;
   private static volatile boolean bool3;

   public ClearWorldModule() {
      super("ClearWorld", "Suppress weather / fog / particles for visibility", Category.RENDER);
      this.run6(new Setting[]{this.noWeather, this.noFog, this.noParticles});
   }

   @Override
   public void run() {
      this.run6();
   }

   @Override
   public void run2() {
      bool = false;
      bool2 = false;
      bool3 = false;
   }

   @Override
   public void run3() {
      this.run6();
   }

   private void run6() {
      bool = this.noWeather.getValue();
      bool2 = this.noFog.getValue();
      bool3 = this.noParticles.getValue();
   }

   public static boolean isEnabled_s() {
      return bool;
   }

   public static boolean isEnabled2() {
      return bool2;
   }

   public static boolean isEnabled3_s() {
      return bool3;
   }
}

