package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.mixin.OptionInstanceAccessor;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BindSetting;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;

public class ZoomModule extends Module {
   private static final int intVal = 4;
   private final NumberSetting amount = new NumberSetting("Amount", 0.3, 0.1, 1.0, 0.05);
   private final ModeSetting mode = new ModeSetting("Mode", "Hold", "Hold", "Toggle");
   private final BooleanSetting smoothing = new BooleanSetting("Smoothing", true);
   private final BooleanSetting mouseSensScale = new BooleanSetting("MouseSensScale", true);
   private final BindSetting zoomKey = new BindSetting("ZoomKey", 67);
   private static volatile double doubleVal = 1.0;
   private boolean bool;
   private boolean bool2;
   private Double doubleVal2;

   public ZoomModule() {
      super("Zoom", "Overrides FOV while a key is held (or toggled) to zoom in", Category.RENDER);
      this.run6(new Setting[]{this.amount, this.mode, this.smoothing, this.mouseSensScale, this.zoomKey});
   }

   @Override
   public void run2() {
      this.run5();
      this.bool = false;
      this.bool2 = false;
      doubleVal = 1.0;
   }

   @Override
   public void run() {
      if (class310 != null) {
         boolean var1 = false;
         if (class310.currentScreen == null && !this.zoomKey.isUnbound() && class310.getWindow() != null) {
            try {
               var1 = InputUtil.isKeyPressed(class310.getWindow(), this.zoomKey.getValue());
            } catch (Throwable var9) {
            }
         }

         boolean var2;
         if (this.mode.check("Toggle")) {
            if (var1 && !this.bool2) {
               this.bool = !this.bool;
            }

            var2 = this.bool;
         } else {
            var2 = var1;
            this.bool = false;
         }

         this.bool2 = var1;
         double var3 = var2 ? this.amount.getValue() : 1.0;
         if (!this.smoothing.getValue()) {
            doubleVal = var3;
         } else {
            double var5 = 1.0 / 4;
            double var7 = doubleVal;
            if (Math.abs(var7 - var3) < var5) {
               var7 = var3;
            } else if (var7 < var3) {
               var7 += var5;
            } else {
               var7 -= var5;
            }

            if (var7 < 0.05) {
               var7 = 0.05;
            }

            if (var7 > 1.0) {
               var7 = 1.0;
            }

            doubleVal = var7;
         }

         if (this.mouseSensScale.getValue() && doubleVal < 0.9999) {
            this.run3(doubleVal);
         } else {
            this.run5();
         }
      }
   }

   public static double getDouble() {
      return doubleVal;
   }

   @Override
   public String getString3() {
      double var1 = doubleVal;
      if (var1 > 0.9999) {
         return null;
      } else {
         double var3 = 1.0 / Math.max(var1, 0.05);
         return "§7" + String.format("%.1fx", var3);
      }
   }

   private OptionInstanceAccessor<Double> getOptionInstanceAccessor() {
      if (class310 != null && class310.options != null) {
         SimpleOption var1 = class310.options.getMouseSensitivity();
         return (OptionInstanceAccessor<Double>)(Object)(var1 == null ? null : var1);
      } else {
         return null;
      }
   }

   private void run3(double var1) {
      try {
         OptionInstanceAccessor var3 = this.getOptionInstanceAccessor();
         if (var3 == null) {
            return;
         }

         Double var4 = (Double)var3.nyx$getValue();
         if (var4 == null) {
            return;
         }

         if (this.doubleVal2 == null) {
            if (var4 > 0.0 && var4 <= 1.0) {
               this.doubleVal2 = var4;
            } else {
               this.doubleVal2 = 0.5;
            }
         }

         double var5 = this.doubleVal2 * var1;
         if (var5 < 0.005) {
            var5 = 0.005;
         }

         if (var5 > 1.0) {
            var5 = 1.0;
         }

         if (var4 == null || Math.abs(var4 - var5) > 1.0E-4) {
            var3.nyx$setValue(var5);
         }
      } catch (Throwable var7) {
      }
   }

   private void run5() {
      if (this.doubleVal2 != null) {
         try {
            OptionInstanceAccessor var1 = this.getOptionInstanceAccessor();
            if (var1 != null) {
               var1.nyx$setValue(this.doubleVal2);
            }
         } catch (Throwable var5) {
         } finally {
            this.doubleVal2 = null;
         }
      }
   }
}

