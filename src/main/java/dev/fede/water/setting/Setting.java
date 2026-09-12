package dev.fede.water.setting;

import dev.fede.water.module.ModuleManager;
import java.util.Objects;
import java.util.function.Supplier;

public class Setting<T> {
   private final String name;
   private final T defaultValue;
   private T value;
   private T min;
   private T max;
   private Supplier<Boolean> visibility = () -> true;

   public Setting(String name, T value) {
      this.name = name;
      this.value = (T)value;
      this.defaultValue = (T)value;
   }

   public Setting(String name, T value, T min, T max) {
      this.name = name;
      this.value = (T)value;
      this.defaultValue = (T)value;
      this.min = (T)min;
      this.max = (T)max;
   }

   public String getName() {
      return this.name;
   }

   public T getValue() {
      return this.value;
   }

   public T getDefaultValue() {
      return this.defaultValue;
   }

   public boolean matchesName(String settingName) {
      return this.name.equalsIgnoreCase(settingName);
   }

   public Setting<T> visibleWhen(Supplier<Boolean> visibility) {
      this.visibility = visibility == null ? () -> true : visibility;
      return this;
   }

   public boolean isVisible() {
      try {
         return this.visibility == null || this.visibility.get();
      } catch (Exception var1) {
         return true;
      }
   }

   public void setValue(T value) {
      if (!Objects.equals(this.value, value)) {
         this.value = (T)value;
         ModuleManager.INSTANCE.c();
      }
   }

   public T getMin() {
      return this.min;
   }

   public T getMax() {
      return this.max;
   }

   static String _cf6ce2f6d19() {
      return "1";
   }
}

