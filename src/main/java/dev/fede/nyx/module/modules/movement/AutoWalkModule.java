package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;

public final class AutoWalkModule extends Module {
   public static volatile AutoWalkModule autoWalkModule;
   private final BooleanSetting sprintToo = new BooleanSetting("SprintToo", false);

   public AutoWalkModule() {
      super("AutoWalk", "Holds the forward key automatically", Category.MOVEMENT);
      this.run6(new Setting[]{this.sprintToo});
      autoWalkModule = this;
   }

   @Override
   public boolean isEnabled() {
      return this.sprintToo.getValue();
   }

   @Override
   public void run() {
   }

   @Override
   public void run2() {
      this.run4();
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         if (class310.currentScreen != null) {
            this.run4();
         } else {
            class310.options.forwardKey.setPressed(true);
            if (this.sprintToo.getValue()) {
               class310.options.sprintKey.setPressed(true);
            }
         }
      }
   }

   private void run4() {
      if (class310.options != null) {
         class310.options.forwardKey.setPressed(false);
         class310.options.sprintKey.setPressed(false);
      }
   }
}

