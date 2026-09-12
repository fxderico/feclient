package dev.fede.module.impl;

import dev.fede.mixin.MinecraftAccessor;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import net.minecraft.client.MinecraftClient;

public class AutoClickerModule extends Module {
   public final BooleanSetting inScreens = this.addSetting(new BooleanSetting("While In Screens", "Whether to click while a screen is open.", true));
   public final ModeSetting leftMode = this.addSetting(
      new ModeSetting("Left Click Mode", "The method of clicking for left clicks.", "Press", "Disabled", "Hold", "Press")
   );
   public final SliderSetting leftDelay = this.addSetting(
      new SliderSetting("Left Click Delay", "Delay between left clicks in ticks.", 2.0, 0.0, 60.0, 1.0, " ticks")
   );
   public final ModeSetting rightMode = this.addSetting(
      new ModeSetting("Right Click Mode", "The method of clicking for right clicks.", "Press", "Disabled", "Hold", "Press")
   );
   public final SliderSetting rightDelay = this.addSetting(
      new SliderSetting("Right Click Delay", "Delay between right clicks in ticks.", 2.0, 0.0, 60.0, 1.0, " ticks")
   );
   private int leftTimer;
   private int rightTimer;

   public AutoClickerModule() {
      super("AutoClicker", "Automatically clicks.", Category.MISC);
      this.leftDelay.visibleWhen(() -> this.leftMode.check("Press"));
      this.rightDelay.visibleWhen(() -> this.rightMode.check("Press"));
   }

   @Override
   protected void onEnable() {
      this.leftTimer = 0;
      this.rightTimer = 0;
      this.release();
   }

   @Override
   protected void onDisable() {
      this.release();
   }

   private void release() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.options != null) {
         mc.options.attackKey.setPressed(false);
         mc.options.useKey.setPressed(false);
      }
   }

   
   @Override
   public void onTick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         if (this.inScreens.get() || mc.currentScreen == null) {
            String var2 = this.leftMode.get();
            switch (var2.hashCode()) {
               case 2255071:
                  if (var2.equals("Hold")) {
                     mc.options.attackKey.setPressed(true);
                  }
                  break;
               case 77378595:
                  if (var2.equals("Press")) {
                     this.leftTimer++;
                     if (this.leftTimer > this.leftDelay.getInt()) {
                        this.leftClick(mc);
                        this.leftTimer = 0;
                     }
                  }
            }

            while (true) {
               var2 = this.rightMode.get();
               switch (var2.hashCode()) {
                  case 2255071:
                     if (!var2.equals("Hold")) {
                        return;
                     }

                     mc.options.attackKey.setPressed(true);
                     break;
                  case 77378595:
                     if (var2.equals("Press")) {
                        this.leftTimer++;
                        if (this.leftTimer > this.leftDelay.getInt()) {
                           this.leftClick(mc);
                           this.leftTimer = 0;
                        }
                        break;
                     }

                     return;
                  default:
                     return;
               }
            }
         }
      }
   }

   private void leftClick(MinecraftClient mc) {
      if (mc.attackCooldown == 10000) {
         mc.attackCooldown = 0;
      }

      mc.options.attackKey.setPressed(true);
      ((MinecraftAccessor)mc).FeClient$startAttack();
      mc.options.attackKey.setPressed(false);
   }

   private void rightClick(MinecraftClient mc) {
      ((MinecraftAccessor)mc).FeClient$startUseItem();
   }
}

