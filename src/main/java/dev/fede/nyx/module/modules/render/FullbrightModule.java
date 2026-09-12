package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.mixin.OptionInstanceAccessor;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import net.minecraft.client.option.SimpleOption;

public class FullbrightModule extends Module {
   private static final double doubleVal = 16.0;
   private Double doubleVal2;

   public FullbrightModule() {
      super("Fullbright", "Forces gamma to 16.0 for full-brightness rendering", Category.RENDER);
   }

   @Override
   public void run() {
      try {
         OptionInstanceAccessor var1 = getOptionInstanceAccessor();
         if (var1 == null) {
            return;
         }

         Double var2 = (Double)var1.nyx$getValue();
         if (var2 != null && var2 > 1.0) {
            var1.nyx$setValue(1.0);
            var2 = 1.0;
         }

         if (this.doubleVal2 == null) {
            this.doubleVal2 = var2;
         }

         var1.nyx$setValue(16.0);
      } catch (Throwable var3) {
      }
   }

   @Override
   public void run2() {
      try {
         OptionInstanceAccessor var1 = getOptionInstanceAccessor();
         if (var1 != null) {
            Double var2 = this.doubleVal2 != null ? this.doubleVal2 : 1.0;
            var1.nyx$setValue(var2);
         }
      } catch (Throwable var11) {
      } finally {
         this.doubleVal2 = null;

         try {
            if (class310 != null && class310.options != null) {
               class310.options.write();
            }
         } catch (Throwable var10) {
         }
      }
   }

   @Override
   public void run3() {
      try {
         OptionInstanceAccessor var1 = getOptionInstanceAccessor();
         if (var1 == null) {
            return;
         }

         Double var2 = (Double)var1.nyx$getValue();
         if (var2 == null || var2 < 15.5) {
            var1.nyx$setValue(16.0);
         }
      } catch (Throwable var3) {
      }
   }

   private static OptionInstanceAccessor<Double> getOptionInstanceAccessor() {
      if (class310 != null && class310.options != null) {
         SimpleOption var0 = class310.options.getGamma();
         return (OptionInstanceAccessor<Double>)(Object)(var0 == null ? null : var0);
      } else {
         return null;
      }
   }
}

