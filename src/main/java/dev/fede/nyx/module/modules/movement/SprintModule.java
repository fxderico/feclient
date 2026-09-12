package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;

public class SprintModule extends Module {
   private final ModeSetting mode = new ModeSetting("Mode", "Legit", "Legit", "Always", "Omnidirectional");
   private final BooleanSetting omni = new BooleanSetting("Omni", false);
   private final BooleanSetting keepOnEat = new BooleanSetting("KeepOnEat", true);
   private final BooleanSetting keepOnAttack = new BooleanSetting("KeepOnAttack", true);

   public SprintModule() {
      super("Sprint", "Always sprints", Category.MOVEMENT);
      this.run6(new Setting[]{this.mode, this.omni, this.keepOnEat, this.keepOnAttack});
   }

   @Override
   public void run2() {
      if (class310.player != null) {
         if (!class310.player.isSneaking()) {
            if (!class310.player.horizontalCollision) {
               if (!class310.player.isUsingItem() || this.keepOnEat.getValue()) {
                  if (this.keepOnAttack.getValue() || !class310.player.handSwinging) {
                     boolean var1 = class310.player.forwardSpeed != 0.0F || class310.player.sidewaysSpeed != 0.0F;
                     boolean var2 = class310.player.forwardSpeed > 0.0F;
                     boolean var3;
                     if (this.mode.check("Always")) {
                        var3 = true;
                     } else if (this.mode.check("Omnidirectional")) {
                        var3 = var1;
                     } else {
                        var3 = this.omni.getValue() ? var1 : var2;
                     }

                     if (var3) {
                        class310.player.setSprinting(true);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public String getString3() {
      return "§7" + this.mode.getMode();
   }
}

