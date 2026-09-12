package dev.fede.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.function.DoubleFunction;

public class SliderSetting extends Setting<Double> {
   private double min;
   private double max;
   private double step;
   private String suffix;
   private DoubleFunction<String> labelFn;

   public SliderSetting(String name, String description, double defaultValue, double min, double max, double step) {
      this(name, description, defaultValue, min, max, step, "");
   }

   public SliderSetting(String name, String description, double defaultValue, double min, double max, double step, String suffix) {
      super(name, description, defaultValue);
      this.min = min;
      this.max = max;
      this.step = step;
      this.suffix = suffix;
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public double getStep() {
      return this.step;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public float getFloat() {
      return this.get().floatValue();
   }

   public int getInt() {
      return (int)Math.round(this.get());
   }

   public double getNormalized() {
      return (this.get() - this.min) / (this.max - this.min);
   }

   public void setNormalized(double t) {
      this.set(this.min + (this.max - this.min) * Math.clamp(t, 0.0, 1.0));
   }

   public void set(Double newValue) {
      double snapped = Math.round(newValue / this.step) * this.step;
      snapped = Math.round(snapped * 1000000.0) / 1000000.0;
      super.set(Math.clamp(snapped, this.min, this.max));
   }

   public SliderSetting withLabel(DoubleFunction<String> labelFn) {
      this.labelFn = labelFn;
      return this;
   }

   public String formatValue() {
      if (this.labelFn != null) {
         return this.labelFn.apply(this.get());
      } else {
         double v = this.get();
         String s;
         if (this.step >= 1.0 && v == Math.floor(v)) {
            s = Long.toString((long)v);
         } else {
            s = String.valueOf(Math.round(v * 100.0) / 100.0);
            if (s.endsWith(".0")) {
               s = s.substring(0, s.length() - 2);
            }
         }

         return s + this.suffix;
      }
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive(this.value);
   }

   @Override
   public void fromJson(JsonElement element) {
      if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
         this.set(element.getAsDouble());
      }
   }
}

