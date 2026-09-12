package dev.fede.water.module.modules.render;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.client.option.SimpleOption;

public final class FullBright extends Module {
   private double i = 1.0;
   private boolean aj = false;
   private static Field a;

   public FullBright() {
      super("FullBright", Category.b);
   }

   @Override
   public void onEnable() {
      if (mc.options != null) {
         this.i = mc.options.getGamma().getValue();
         this.a(10.0);
         this.aj = true;
      }
   }

   @Override
   public void onDisable() {
      if (mc.options != null) {
         this.a(this.aj ? this.i : 1.0);
         this.aj = false;
      }
   }

   @Override
   public void onTick() {
      if (mc.options != null) {
         if (!this.aj) {
            this.i = mc.options.getGamma().getValue();
            if (Math.abs(this.i - 10.0) < 1.0E-4) {
               this.i = 1.0;
            }

            this.a(10.0);
            this.aj = true;
         } else {
            try {
               double var1 = mc.options.getGamma().getValue();
               if (Math.abs(var1 - 10.0) > 1.0E-4) {
                  this.a(10.0);
               }
            } catch (Exception var3) {
            }
         }
      }
   }

   private void a(double gamma) {
      SimpleOption var3 = mc.options.getGamma();
      Field var4 = a;
      if (var4 == null) {
         var4 = this.a(var3);
         a = var4;
      }

      if (var4 != null) {
         try {
            var4.set(var3, gamma);
            Double var7 = (Double)var3.getValue();
            if (var7 != null && Math.abs(var7 - gamma) <= 1.0E-4) {
               return;
            }

            a = null;
         } catch (Exception var6) {
         }
      }

      try {
         var3.setValue(gamma);
      } catch (Exception var5) {
      }
   }

   private Field a(SimpleOption<?> option) {
      Object var2;
      try {
         var2 = option.getValue();
      } catch (Exception var9) {
         var2 = null;
      }

      Field var3 = null;

      for (Field var7 : SimpleOption.class.getDeclaredFields()) {
         if (!Modifier.isStatic(var7.getModifiers())) {
            if ("value".equals(var7.getName())) {
               var3 = var7;
            }

            var7.setAccessible(true);

            try {
               Object var8 = var7.get(option);
               if (var2 == null ? var8 == null : var2.equals(var8)) {
                  return var7;
               }
            } catch (Exception var10) {
            }
         }
      }

      if (var3 != null) {
         var3.setAccessible(true);
         return var3;
      } else {
         return null;
      }
   }
}

