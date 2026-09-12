package dev.fede.settings;

import com.google.gson.JsonElement;
import java.util.function.BooleanSupplier;

public abstract class Setting<T> {
   private String name;
   private String description;
   protected T value;
   protected T defaultValue;
   private BooleanSupplier visibility = () -> true;

   protected Setting(String name, String description, T defaultValue) {
      this.name = name;
      this.description = description;
      this.value = defaultValue;
      this.defaultValue = defaultValue;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public T get() {
      return this.value;
   }

   public void set(T value) {
      this.value = value;
   }

   public void reset() {
      this.value = this.defaultValue;
   }

   public void visibleWhen(BooleanSupplier condition) {
      this.visibility = condition;
   }

   public boolean isVisible() {
      return this.visibility.getAsBoolean();
   }

   public abstract JsonElement toJson();

   public abstract void fromJson(JsonElement var1);
}

