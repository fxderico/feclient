package dev.fede.water.module.modules.misc;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;

public final class Sprint extends Module {
   private boolean ag = false;

   public Sprint() {
      super("Sprint", Category.c);
   }

   @Override
   public void onEnable() {
      if (mc != null && mc.options != null) {
         this.ag = this.w();
         this.a(false);
      }
   }

   @Override
   public void onDisable() {
      if (mc != null && mc.options != null) {
         this.a(this.ag);

         try {
            mc.options.sprintKey.setPressed(false);
         } catch (Throwable var1) {
         }
      }
   }

   @Override
   public void onTick() {
      if (mc != null && mc.player != null && mc.options != null) {
         this.a(false);

         try {
            mc.options.sprintKey.setPressed(true);
         } catch (Throwable var1) {
         }
      }
   }

   private boolean w() {
      try {
         Object var1 = mc.options.getClass().getMethod("getSprintToggled").invoke(mc.options);
         return var1 == null ? false : var1.getClass().getMethod("getValue").invoke(var1) instanceof Boolean var4 && var4;
      } catch (Throwable var2) {
         return false;
      }
   }

   private void a(boolean value) {
      try {
         Object var2 = mc.options.getClass().getMethod("getSprintToggled").invoke(mc.options);
         if (var2 == null) {
            return;
         }

         var2.getClass().getMethod("setValue", Object.class).invoke(var2, value);
      } catch (Throwable var3) {
      }
   }
}

