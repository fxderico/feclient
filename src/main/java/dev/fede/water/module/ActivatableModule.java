package dev.fede.water.module;

public abstract class ActivatableModule extends Module {
   private int field_a_1 = 0;
   public boolean field_a_2 = false;

   public ActivatableModule(String name, Category category) {
      super(name, category);
   }

   public void b() {
      this.toggle();
   }

   public int getActivationKey() {
      return this.field_a_1;
   }

   public void setActivationKey(int activationKey) {
      this.field_a_1 = activationKey;
      ModuleManager.INSTANCE.f();
   }

   void a(int activationKey) {
      this.field_a_1 = activationKey;
   }
}

