package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;

public class ReachModule extends Module {
   private static final double doubleVal = 3.0;
   private static final double doubleVal2 = 4.5;
   private static final double doubleVal3 = 3.5;
   private final NumberSetting attackReach = new NumberSetting("AttackReach", 3.0, 3.0, 6.0, 0.1);
   private final NumberSetting interactReach = new NumberSetting("InteractReach", 4.5, 4.5, 6.0, 0.1);
   private final BooleanSetting warnOnBan = new BooleanSetting("WarnOnBan", true);
   private static volatile boolean bool;
   private static volatile double doubleVal4 = 3.0;
   private static volatile double doubleVal5 = 4.5;
   private boolean bool2;

   public ReachModule() {
      super("Reach", "Extends attack and block interaction distance", Category.COMBAT);
      this.run6(new Setting[]{this.attackReach, this.interactReach, this.warnOnBan});
   }

   @Override
   public void run() {
      bool = true;
      this.run6();
      this.bool2 = false;
      this.run7();
   }

   @Override
   public void run2() {
      bool = false;
      doubleVal4 = 3.0;
      doubleVal5 = 4.5;
   }

   @Override
   public void run3() {
      if (bool) {
         this.run6();
         this.run7();
      }
   }

   private void run6() {
      doubleVal4 = this.attackReach.getValue();
      doubleVal5 = this.interactReach.getValue();
   }

   private void run7() {
      if (this.warnOnBan.getValue()) {
         if (!this.bool2) {
            if (this.attackReach.getValue() > 3.5 || this.interactReach.getValue() > 3.5) {
               NotificationUtils.run8("Reach", "may flag anticheat", INFO.UNKNOWN_3);
               this.bool2 = true;
            }
         }
      }
   }

   public static boolean isEnabled_s() {
      return bool;
   }

   public static double getDouble() {
      return doubleVal4;
   }

   public static double getDouble2() {
      return doubleVal5;
   }

   @Override
   public String getString3() {
      if (!bool) {
         return null;
      } else {
         double var1 = Math.max(doubleVal4, doubleVal5);
         return "§7" + String.format("%.1f", var1);
      }
   }
}

