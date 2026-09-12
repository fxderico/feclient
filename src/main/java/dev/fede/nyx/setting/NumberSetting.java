package dev.fede.nyx.setting;

public class NumberSetting extends Setting {
   private double value;
   private final double min;
   private final double max;
   private final double increment;

   public NumberSetting(String name, double value, double min, double max, double increment) {
      super(name);
      this.min = min;
      this.max = max;
      this.increment = increment;
      this.value = this.clamp(value);
   }

   private double clamp(double v) {
      if (v < this.min) {
         return this.min;
      } else if (v > this.max) {
         return this.max;
      } else {
         double var3 = Math.round((v - this.min) / this.increment);
         double var5 = this.min + var3 * this.increment;
         return var5 > this.max ? this.max : var5;
      }
   }

   public double getValue() {
      return this.value;
   }

   public float getValueFloat() {
      return (float)this.value;
   }

   public int getValueInt() {
      return (int)Math.round(this.value);
   }

   public long getValueLong() {
      return Math.round(this.value);
   }

   public void setValue(double value) {
      this.value = this.clamp(value);
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public double getIncrement() {
      return this.increment;
   }

   public double step() {
      return this.increment;
   }

   public boolean isInteger() {
      return this.increment == Math.floor(this.increment) && this.min == Math.floor(this.min) && this.max == Math.floor(this.max);
   }
}

