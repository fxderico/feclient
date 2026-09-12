package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;

public class AntiKnockbackModule extends Module {
   private final BooleanSetting horizontal = new BooleanSetting("Horizontal", true);
   private final BooleanSetting vertical = new BooleanSetting("Vertical", true);

   public AntiKnockbackModule() {
      super("AntiKnockback", "Fully cancels incoming knockback per axis", Category.COMBAT);
      this.run6(new Setting[]{this.horizontal, this.vertical});
   }

   @Override
   public void run() {
      this.run5();
   }

   @Override
   public void run2() {
      VelocityModule var1 = this.getl();
      if (var1 != null && var1.isEnabled3()) {
         var1.run6();
      } else {
         VelocityModule.bool = false;
         VelocityModule.doubleVal = 1.0;
         VelocityModule.doubleVal2 = 1.0;
         VelocityModule.bool2 = false;
         VelocityModule.bool3 = false;
      }
   }

   @Override
   public void run3() {
      this.run5();
   }

   @Override
   public boolean isEnabled() {
      return this.horizontal.getValue();
   }

   public boolean isEnabled2() {
      return this.vertical.getValue();
   }

   public void run5() {
      VelocityModule var1 = this.getl();
      if (var1 != null && var1.isEnabled3()) {
         var1.run6();
      } else {
         VelocityModule.bool = true;
         VelocityModule.doubleVal = this.horizontal.getValue() ? 0.0 : 1.0;
         VelocityModule.doubleVal2 = this.vertical.getValue() ? 0.0 : 1.0;
         VelocityModule.bool2 = false;
         VelocityModule.bool3 = false;
      }
   }

   private VelocityModule getl() {
      if (NyxClient.MODULES == null) {
         return null;
      } else {
         return NyxClient.MODULES.moduleOf("Velocity") instanceof VelocityModule var2 ? var2 : null;
      }
   }
}

