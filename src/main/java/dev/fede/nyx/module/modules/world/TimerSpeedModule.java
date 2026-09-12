package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.lang.reflect.Field;
import java.util.Locale;
import net.minecraft.client.render.RenderTickCounter.Dynamic;

public class TimerSpeedModule extends Module {
   private final NumberSetting multiplier = new NumberSetting("Multiplier", 1.5, 0.1, 10.0, 0.1);
   private FloatUnaryOperator floatUnaryOperator;
   private FloatUnaryOperator floatUnaryOperator2;
   private boolean bool;
   private float floatVal = Float.NaN;
   private static volatile Field field;

   public TimerSpeedModule() {
      super("TimerSpeed", "Client-side game-speed multiplier (timer cheat)", Category.WORLD);
      this.run6(new Setting[]{this.multiplier});
   }

   @Override
   public void run() {
      if (class310 != null) {
         boolean var1 = this.check((float)this.multiplier.getValue());
         if (!var1 && !this.bool) {
            NotificationUtils.run8(
               "TimerSpeed",
               "Could not access RenderTickCounter.Dynamic.targetMillisPerTick; check module opens for net.minecraft.client.render",
               INFO.UNKNOWN_4
            );
         }
      }
   }

   @Override
   public void run2() {
      if (this.floatUnaryOperator2 != null && this.floatUnaryOperator != null) {
         Dynamic var1 = this.getclass9779class9781();
         if (var1 != null) {
            try {
               run4(var1, this.floatUnaryOperator);
            } catch (Throwable var3) {
            }

            this.floatUnaryOperator2 = null;
            this.floatVal = Float.NaN;
         }
      }
   }

   @Override
   public void run3() {
      float var1 = (float)this.multiplier.getValue();
      if (Float.compare(var1, this.floatVal) != 0) {
         this.check(var1);
      }
   }

   private boolean check(float var1) {
      Dynamic var2 = this.getclass9779class9781();
      if (var2 == null) {
         return this.bool;
      } else {
         try {
            if (this.floatUnaryOperator == null) {
               this.floatUnaryOperator = floatUnaryOperatorOf(var2);
               if (this.floatUnaryOperator == null) {
                  return false;
               }
            }

            float var3 = var1 <= 0.0F ? 0.001F : var1;
            FloatUnaryOperator var4 = this.floatUnaryOperator;
            FloatUnaryOperator var5 = f -> 0.0f;
            run4(var2, var5);
            this.floatUnaryOperator2 = var5;
            this.floatVal = var1;
            this.bool = true;
            return true;
         } catch (Throwable var6) {
            return this.bool;
         }
      }
   }

   private Dynamic getclass9779class9781() {
      return class310.getRenderTickCounter() instanceof Dynamic var2 ? var2 : null;
   }

   private static Field getField() throws NoSuchFieldException {
      Field var0 = field;
      if (var0 != null) {
         return var0;
      } else {
         for (Field var4 : Dynamic.class.getDeclaredFields()) {
            if (FloatUnaryOperator.class.equals(var4.getType())) {
               var4.setAccessible(true);
               field = var4;
               return var4;
            }
         }

         throw new NoSuchFieldException("No FloatUnaryOperator field on RenderTickCounter.Dynamic — yarn refactor?");
      }
   }

   private static FloatUnaryOperator floatUnaryOperatorOf(Dynamic var0) throws Exception {
      return getField().get(var0) instanceof FloatUnaryOperator var2 ? var2 : null;
   }

   private static void run4(Dynamic var0, FloatUnaryOperator var1) throws Exception {
      getField().set(var0, var1);
   }

   @Override
   public String getString3() {
      return "§7" + String.format(Locale.ROOT, "%.1fx", this.multiplier.getValue());
   }

   private static float floatOf(FloatUnaryOperator var0, float var1, float var2) {
      return var0.apply(var2) / var1;
   }
}

