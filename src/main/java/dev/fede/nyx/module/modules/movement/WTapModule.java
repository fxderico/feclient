package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * WTap — taps the forward key off for a few ticks right after you attack, so
 * sprint drops and re-engages, giving the little knockback/reset a lot of
 * DonutSMP PvPers want. Ported in behaviour from Selene's WTap.
 *
 * Selene fired this off a real AttackEvent; feClient's nyx modules only get a
 * tick, so the swing is detected as the rising edge of handSwinging (a left
 * click). That's the same signal every simple W-tap uses — it fires on the
 * attack input, which is what you want here.
 */
public final class WTapModule extends Module {
   private final NumberSetting chance = new NumberSetting("Chance", 100.0, 0.0, 100.0, 1.0);
   private final NumberSetting tapTicks = new NumberSetting("TapTicks", 2.0, 1.0, 6.0, 1.0);
   private final NumberSetting delayTicks = new NumberSetting("DelayTicks", 1.0, 1.0, 4.0, 1.0);
   private final BooleanSetting onlyOnGround = new BooleanSetting("OnlyOnGround", false);
   private final BooleanSetting onlyWeapon = new BooleanSetting("OnlyWeapon", true);

   private boolean prevSwing;
   private int pendingDelay;
   private int tapRemaining;
   private boolean wasHeld;

   public WTapModule() {
      super("WTap", "Taps forward off after an attack to reset sprint", Category.MOVEMENT);
      this.run6(new Setting[]{this.chance, this.tapTicks, this.delayTicks, this.onlyOnGround, this.onlyWeapon});
   }

   @Override
   public void run() {
      this.prevSwing = false;
      this.pendingDelay = 0;
      this.tapRemaining = 0;
      this.wasHeld = false;
   }

   @Override
   public void run2() {
      if (class310.player != null && this.wasHeld && !class310.options.forwardKey.isPressed()) {
         class310.options.forwardKey.setPressed(true);
      }
      this.pendingDelay = 0;
      this.tapRemaining = 0;
      this.wasHeld = false;
      this.prevSwing = false;
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.options == null) {
         return;
      }

      // rising edge of the swing = an attack input
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
         // only bother if we're actually moving (sprint to reset)
         double vx = class310.player.getVelocity().x;
         double vz = class310.player.getVelocity().z;
         if (vx * vx + vz * vz <= 0.0025) {
            return;
         }
         this.wasHeld = class310.options.forwardKey.isPressed();
         if (this.wasHeld) {
            class310.options.forwardKey.setPressed(false);
         }
         this.tapRemaining = Math.max(1, this.tapTicks.getValueInt());
         return;
      }

      if (this.tapRemaining > 0 && --this.tapRemaining <= 0
         && this.wasHeld && !class310.options.forwardKey.isPressed()) {
         class310.options.forwardKey.setPressed(true);
         this.wasHeld = false;
      }
   }

   private boolean attackAllowed() {
      if (this.onlyOnGround.getValue() && !class310.player.isOnGround()) {
         return false;
      }
      if (this.onlyWeapon.getValue() && !isWeapon(class310.player.getMainHandStack().getItem())) {
         return false;
      }
      return !(Math.random() * 100.0 >= this.chance.getValue());
   }

   // 1.21.11 has no SwordItem/AxeItem classes (items are data-driven now), so
   // match on the registry id path instead — covers vanilla weapons and Donut's
   // custom "_spear"/"_sword" variants without depending on removed classes.
   static boolean isWeapon(Item item) {
      if (item == null) {
         return false;
      }
      Identifier id = Registries.ITEM.getId(item);
      if (id == null) {
         return false;
      }
      String p = id.getPath();
      return p.endsWith("_sword") || p.endsWith("_axe") || p.endsWith("_spear")
         || p.equals("mace") || p.equals("trident");
   }

   @Override
   public String getString3() {
      return "§7" + this.tapTicks.getValueInt();
   }
}
