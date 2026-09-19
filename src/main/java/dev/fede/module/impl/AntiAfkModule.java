package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.SliderSetting;
import net.minecraft.client.MinecraftClient;

/**
 * Nudges look angle and jumps periodically so servers with AFK-kick timers
 * don't boot you while you're away. Pure vanilla, no mixin.
 */
public class AntiAfkModule extends Module {
   public final SliderSetting intervalTicks = this.addSetting(
      new SliderSetting("Interval", "Ticks between each nudge.", 200.0, 40.0, 1200.0, 20.0, "t")
   );

   private int ticksSinceAction = 0;
   private float baseYaw;
   private boolean haveBaseYaw;

   public AntiAfkModule() {
      super("AntiAFK", "Periodically nudges look + jump to avoid AFK kicks.", Category.PLAYER);
   }

   @Override
   protected void onDisable() {
      haveBaseYaw = false;
      ticksSinceAction = 0;
   }

   @Override
   public void onTick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player == null || mc.world == null) return;

      if (!haveBaseYaw) {
         baseYaw = mc.player.getYaw();
         haveBaseYaw = true;
      }

      ticksSinceAction++;
      if (ticksSinceAction >= (int) intervalTicks.getFloat()) {
         ticksSinceAction = 0;
         // small yaw wiggle + a jump, both cheap and won't move the player anywhere
         float wiggled = baseYaw + (((System.nanoTime() >>> 8) % 2 == 0) ? 3.5f : -3.5f);
         mc.player.setYaw(wiggled);
         if (mc.player.isOnGround()) {
            mc.player.jump();
         }
      }
   }
}
