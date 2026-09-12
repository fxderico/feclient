package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.util.Hand;

public class SwingAnimationModule extends Module {
   public static SwingAnimationModule swingAnimationModule;
   private final ModeSetting mode = new ModeSetting("Mode", "Continuous", "Vanilla", "Continuous", "Fast", "Slow", "Off");
   private final NumberSetting swingRate = new NumberSetting("SwingRate", 2.0, 1.0, 20.0, 1.0);
   private final NumberSetting swingDuration = new NumberSetting("SwingDuration", 6.0, 2.0, 40.0, 1.0);
   private final BooleanSetting whileAttacking = new BooleanSetting("WhileAttacking", true);
   private final BooleanSetting whileUsing = new BooleanSetting("WhileUsing", true);
   private final BooleanSetting whileBreaking = new BooleanSetting("WhileBreaking", true);
   private final BooleanSetting bothHands = new BooleanSetting("BothHands", false);
   private final BooleanSetting swingWhileEating = new BooleanSetting("SwingWhileEating", false);
   private int intVal = 0;

   public SwingAnimationModule() {
      super("SwingAnimation", "Customises arm swing for all actions — mining, attacking, item use.", Category.PLAYER);
      swingAnimationModule = this;
      this.swingRate.visibleWhen(this::getBoolean2);
      this.swingDuration.visibleWhen(this::getBoolean);
      this.run6(
         new Setting[]{
            this.mode, this.swingRate, this.swingDuration, this.whileAttacking, this.whileUsing, this.whileBreaking, this.bothHands, this.swingWhileEating
         }
      );
   }

   @Override
   public int getInt() {
      if (!this.isEnabled3()) {
         return -1;
      } else {
         String var1 = this.mode.getMode();
         switch (var1.hashCode()) {
            case -1922388177:
               if (var1.equals("Continuous")) {
                  return this.swingDuration.getValueInt();
               }
               break;
            case 2182268:
               if (var1.equals("Fast")) {
                  return Math.max(2, Math.min(this.swingDuration.getValueInt(), 4));
               }
               break;
            case 2580001:
               if (var1.equals("Slow")) {
                  return Math.max(12, this.swingDuration.getValueInt());
               }
         }

         return -1;
      }
   }

   @Override
   public void run2() {
      if (class310.player != null && class310.world != null && class310.options != null) {
         String var1 = this.mode.getMode();
         if (!"Vanilla".equals(var1)) {
            if ("Off".equals(var1)) {
               class310.player.handSwinging = false;
               class310.player.handSwingTicks = 0;
            } else if (class310.currentScreen == null) {
               if (!class310.player.isUsingItem() || this.swingWhileEating.getValue()) {
                  boolean var2 = this.whileAttacking.getValue() && class310.options.attackKey.isPressed();
                  boolean var3 = this.whileUsing.getValue() && class310.options.useKey.isPressed();
                  boolean var4 = this.whileBreaking.getValue() && class310.interactionManager != null && class310.interactionManager.isBreakingBlock();
                  if (!var2 && !var3 && !var4) {
                     this.intVal = 0;
                  } else {
                     int var10000;
                     label62: {
                        switch (var1.hashCode()) {
                           case 2182268:
                              if (var1.equals("Fast")) {
                                 var10000 = 1;
                                 break label62;
                              }
                              break;
                           case 2580001:
                              if (var1.equals("Slow")) {
                                 var10000 = 8;
                                 break label62;
                              }
                        }

                        var10000 = this.swingRate.getValueInt();
                     }

                     int var5 = var10000;
                     if (++this.intVal >= var5) {
                        this.intVal = 0;
                        class310.player.swingHand(Hand.MAIN_HAND);
                        if (this.bothHands.getValue()) {
                           class310.player.swingHand(Hand.OFF_HAND);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void run() {
      this.intVal = 0;
      if (class310.player != null) {
         class310.player.handSwinging = false;
      }
   }

   private Boolean getBoolean() {
      String var1 = this.mode.getMode();
      return "Continuous".equals(var1) || "Fast".equals(var1) || "Slow".equals(var1);
   }

   private Boolean getBoolean2() {
      return "Continuous".equals(this.mode.getMode());
   }
}

