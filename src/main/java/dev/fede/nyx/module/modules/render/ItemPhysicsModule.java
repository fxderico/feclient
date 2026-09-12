package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;

public class ItemPhysicsModule extends Module {
   private final NumberSetting spinSpeed = new NumberSetting("SpinSpeed", 2.0, 0.0, 10.0, 0.1);
   private final NumberSetting wobbleAmplitude = new NumberSetting("WobbleAmplitude", 0.15, 0.0, 0.5, 0.01);
   private static volatile boolean bool;
   private static volatile float floatVal;
   private static volatile float floatVal2;

   public ItemPhysicsModule() {
      super("ItemPhysics", "Makes dropped items spin and wobble more visibly", Category.RENDER);
      this.run6(new Setting[]{this.spinSpeed, this.wobbleAmplitude});
   }

   @Override
   public void run() {
      this.run6();
   }

   @Override
   public void run2() {
      bool = false;
   }

   @Override
   public void run3() {
      this.run6();
   }

   private void run6() {
      bool = true;
      floatVal = (float)this.spinSpeed.getValue();
      floatVal2 = (float)this.wobbleAmplitude.getValue();
   }

   public static boolean isEnabled_s() {
      return bool;
   }

   public static float getFloat() {
      return floatVal;
   }

   public static float getFloat2() {
      return floatVal2;
   }
}

