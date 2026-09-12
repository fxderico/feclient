package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;

public class ViewModelModule extends Module {
   private final NumberSetting offsetX = new NumberSetting("OffsetX", 0.0, -1.5, 1.5, 0.01);
   private final NumberSetting offsetY = new NumberSetting("OffsetY", 0.0, -1.5, 1.5, 0.01);
   private final NumberSetting offsetZ = new NumberSetting("OffsetZ", 0.0, -1.5, 1.5, 0.01);
   private final NumberSetting rotX = new NumberSetting("RotX", 0.0, -180.0, 180.0, 1.0);
   private final NumberSetting rotY = new NumberSetting("RotY", 0.0, -180.0, 180.0, 1.0);
   private final NumberSetting rotZ = new NumberSetting("RotZ", 0.0, -180.0, 180.0, 1.0);
   private final NumberSetting scale = new NumberSetting("Scale", 1.0, 0.1, 3.0, 0.05);
   private final BooleanSetting restoreOnDisable = new BooleanSetting("RestoreOnDisable", true);
   private static volatile boolean bool;
   private static volatile float floatVal;
   private static volatile float floatVal2;
   private static volatile float floatVal3;
   private static volatile float floatVal4;
   private static volatile float floatVal5;
   private static volatile float floatVal6;
   private static volatile float floatVal7 = 1.0F;

   public ViewModelModule() {
      super("ViewModel", "Reposition / rescale / rotate the first-person held item", Category.RENDER);
      this.run6(new Setting[]{this.offsetX, this.offsetY, this.offsetZ, this.rotX, this.rotY, this.rotZ, this.scale, this.restoreOnDisable});
   }

   @Override
   public void run() {
      this.run4();
   }

   @Override
   public void run2() {
      bool = false;
      if (this.restoreOnDisable.getValue()) {
         floatVal3 = 0.0F;
         floatVal2 = 0.0F;
         floatVal = 0.0F;
         floatVal6 = 0.0F;
         floatVal5 = 0.0F;
         floatVal4 = 0.0F;
         floatVal7 = 1.0F;
      }
   }

   @Override
   public void run3() {
      this.run4();
   }

   private void run4() {
      bool = true;
      floatVal = (float)this.offsetX.getValue();
      floatVal2 = (float)this.offsetY.getValue();
      floatVal3 = (float)this.offsetZ.getValue();
      floatVal4 = (float)this.rotX.getValue();
      floatVal5 = (float)this.rotY.getValue();
      floatVal6 = (float)this.rotZ.getValue();
      floatVal7 = (float)this.scale.getValue();
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

   public static float getFloat3() {
      return floatVal3;
   }

   public static float getFloat4() {
      return floatVal4;
   }

   public static float getFloat5() {
      return floatVal5;
   }

   public static float getFloat6() {
      return floatVal6;
   }

   public static float getFloat7() {
      return floatVal7;
   }
}

