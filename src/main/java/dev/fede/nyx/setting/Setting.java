package dev.fede.nyx.setting;

import dev.fede.nyx.module.Module;
import java.util.function.Supplier;

public abstract class Setting {
   private final String name;
   private Module parent;
   private Supplier<Boolean> visibleWhen = () -> true;

   public Setting(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public Module getParent() {
      return this.parent;
   }

   public void setParent(Module parent) {
      this.parent = parent;
   }

   public Setting visibleWhen(Supplier<Boolean> visibility) {
      return this.setVisible(visibility);
   }

   public Setting setVisible(Supplier<Boolean> visibility) {
      this.visibleWhen = visibility == null ? () -> true : visibility;
      return this;
   }

   public boolean isVisible() {
      return this.visibleWhen.get();
   }
}

