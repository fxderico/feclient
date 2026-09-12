package dev.fede.water.module.modules.misc;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.client.option.SimpleOption;

public final class AutoRender extends Module {
   private final Setting<Float> aA = new Setting<>("Low Chunks", 2.0F, 2.0F, 32.0F);
   private final Setting<Float> aB = new Setting<>("High Chunks", 8.0F, 2.0F, 32.0F);
   private final Setting<Float> aC = new Setting<>("Low Ticks", 8.0F, 1.0F, 40.0F);
   private final Setting<Float> aD = new Setting<>("Reset Up Y", 4.0F, 1.0F, 32.0F);
   private final Setting<Float> aE = new Setting<>("Trigger Down Y", 3.0F, 1.0F, 32.0F);
   private int aj = -1;
   private int ak = 0;
   private int al = -1;
   private double b = 0.0;
   private double c = 0.0;
   private boolean ac = false;
   private boolean ad = false;
   private boolean ae = false;
   private static Field a;

   public AutoRender() {
      super("AUTO RENDER", Category.c);
      this.addSetting(this.aA);
      this.addSetting(this.aB);
      this.addSetting(this.aC);
      this.addSetting(this.aD);
      this.addSetting(this.aE);
   }

   @Override
   public void onEnable() {
      if (mc.options != null && mc.player != null) {
         this.aj = mc.options.getClampedViewDistance();
         this.al = -1;
         this.b = mc.player.getY();
         this.c = this.b;
         this.an();
      }
   }

   @Override
   public void onDisable() {
      if (mc.options != null) {
         if (this.aj >= 2) {
            this.j(this.aj);
         }

         this.aj = -1;
         this.ak = 0;
         this.al = -1;
         this.ac = false;
         this.ad = false;
         this.ae = false;
      }
   }

   @Override
   public void onTick() {
      if (mc.options != null && mc.world != null && mc.player != null) {
         double var1 = mc.player.getY();
         if (this.ac) {
            this.ak--;
            if (this.ak <= 0) {
               this.ap();
               this.ac = false;
               this.ad = true;
               this.ae = false;
               this.c = var1;
            } else {
               this.ao();
            }

            this.b = var1;
         } else {
            if (this.ad) {
               if (var1 > this.c) {
                  this.c = var1;
               }

               if (var1 >= this.b + this.aD.getValue().floatValue()) {
                  this.ae = true;
               }

               if (this.ae && this.c - var1 >= this.aE.getValue().floatValue()) {
                  this.an();
               }
            }

            this.b = var1;
         }
      }
   }

   private void an() {
      this.ac = true;
      this.ad = false;
      this.ae = false;
      this.ak = Math.max(1, this.aC.getValue().intValue());
      this.ao();
   }

   private void ao() {
      int var1 = this.a(this.aA.getValue().intValue(), 2, 32);
      this.i(var1);
   }

   private void ap() {
      int var1 = this.a(this.aA.getValue().intValue(), 2, 32);
      int var2 = this.a(this.aB.getValue().intValue(), 2, 32);
      if (var2 < var1) {
         var2 = var1;
      }

      this.i(var2);
   }

   private void i(int distance) {
      distance = this.a(distance, 2, 32);
      if (this.al != distance || mc.options.getClampedViewDistance() != distance) {
         this.al = distance;
         this.j(distance);
      }
   }

   private void j(int distance) {
      distance = this.a(distance, 2, 32);
      SimpleOption var2 = mc.options.getViewDistance();
      Field var3 = a;
      if (var3 == null) {
         var3 = this.a(var2);
         a = var3;
      }

      if (var3 != null) {
         try {
            var3.set(var2, distance);
            if (Integer.valueOf(distance).equals(var2.getValue())) {
               this.aq();
               return;
            }

            a = null;
         } catch (Exception var5) {
         }
      }

      try {
         var2.setValue(distance);
      } catch (Exception var4) {
      }

      this.aq();
   }

   private void aq() {
      if (mc.worldRenderer != null) {
         mc.worldRenderer.scheduleTerrainUpdate();
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

   private int a(int value, int min, int max) {
      return Math.max(min, Math.min(max, value));
   }
}

