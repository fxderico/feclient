package dev.fede.nyx.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoubleListSetting extends Setting {
   private final List<Double> values;
   private final double min;
   private final double max;

   public DoubleListSetting(String var1, List<Double> var2, double var3, double var5) {
      super(var1);
      if (var3 > var5) {
         double var7 = var3;
         var3 = var5;
         var5 = var7;
      }

      this.min = var3;
      this.max = var5;
      this.values = new ArrayList<>();
      if (var2 != null) {
         for (Double var8 : var2) {
            if (var8 != null) {
               this.values.add(this.clamp(var8));
            }
         }
      }
   }

   public List<Double> getValues() {
      return this.values;
   }

   public List<Double> getValue() {
      return this.values;
   }

   public int size() {
      return this.values.size();
   }

   public boolean isEmpty() {
      return this.values.isEmpty();
   }

   public double get(int index) {
      return this.values.get(index);
   }

   public void add(double value) {
      this.values.add(this.clamp(value));
   }

   public void set(int index, double value) {
      this.values.set(index, this.clamp(value));
   }

   public double remove(int index) {
      return this.values.remove(index);
   }

   public boolean remove(Double value) {
      return this.values.remove(value);
   }

   public void clear() {
      this.values.clear();
   }

   public void setValues(List<Double> var1) {
      this.values.clear();
      if (var1 != null) {
         for (Double var3 : var1) {
            if (var3 != null) {
               this.values.add(this.clamp(var3));
            }
         }
      }
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public boolean contains(double value) {
      return this.values.contains(value);
   }

   public List<Double> snapshot() {
      return Collections.unmodifiableList(new ArrayList<>(this.values));
   }

   private double clamp(double v) {
      if (v < this.min) {
         return this.min;
      } else {
         return v > this.max ? this.max : v;
      }
   }
}

