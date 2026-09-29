package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;

/**
 * STap — taps the BACK key on for a few ticks after you attack, which breaks
 * sprint (you can't sprint walking backward) and re-triggers it. The reverse of
 * WTap; some players prefer the S-tap feel. Ported in behaviour from Selene.
 *
 * Attack is the rising edge of handSwinging (a left click), since nyx modules
 * only get a tick, not Selene's AttackEvent.
 */
public final class STapModule extends Module {
   private final NumberSetting chance = new NumberSetting("Chance", 100.0, 0.0, 100.0, 1.0);
   private final NumberSetting tapTicks = new NumberSetting("TapTicks", 2.0, 1.0, 6.0, 1.0);
   private final NumberSetting delayTicks = new NumberSetting("DelayTicks", 1.0, 1.0, 4.0, 1.0);
   private final BooleanSetting onlyOnGround = new BooleanSetting("OnlyOnGround", false);
   private final BooleanSetting onlyWeapon = new BooleanSetting("OnlyWeapon", true);

   private boolean prevSwing;
   private int pendingDelay;
   private int tapRemaining;
   private boolean pressed;

   public STapModule() {
      super("STap", "Taps back after an attack to reset sprint", Category.MOVEMENT);
      this.run6(new Setting[]{this.chance, this.tapTicks, this.delayTicks, this.onlyOnGround, this.onlyWeapon});
   }

   @Override
   public void run() {
      this.prevSwing = false;
      this.pendingDelay = 0;
      this.tapRemaining = 0;
      this.pressed = false;
   }

   @Override
   public void run2() {
      if (class310.player != null && this.pressed) {
         class310.options.backKey.setPressed(false);
      }
      this.pendingDelay = 0;
      this.tapRemaining = 0;
      this.pressed = false;
      this.prevSwing = false;
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.options == null) {
         return;
      }

      boolean swing = class310.player.handSwinging;
      boolean attacked = swing && !this.prevSwing;
      this.prevSwing = swing;

      if (attacked && this.pendingDelay <= 0 && this.tapRemaining <= 0 && attackAllowed()) {
         this.pendingDelay = Math.max(1, this.delayTicks.getValueInt());
      }

      if (this.pendingDelay > 0) {
         if (--this.pendingDelay > 0) {
            return;
         }
         if (!class310.options.backKey.isPressed()) {
            class310.options.backKey.setPressed(true);
            this.pressed = true;
         }
         this.tapRemaining = Math.max(1, this.tapTicks.getValueInt());
         return;
      }

      if (this.tapRemaining > 0 && --this.tapRemaining <= 0 && this.pressed) {
         class310.options.backKey.setPressed(false);
         this.pressed = false;
      }
   }

   private boolean attackAllowed() {
      if (this.onlyOnGround.getValue() && !class310.player.isOnGround()) {
         return false;
      }
      if (this.onlyWeapon.getValue() && !WTapModule.isWeapon(class310.player.getMainHandStack().getItem())) {
         return false;
      }
      return !(Math.random() * 100.0 >= this.chance.getValue());
   }

   @Override
   public String getString3() {
      return "§7" + this.tapTicks.getValueInt();
   }
}
