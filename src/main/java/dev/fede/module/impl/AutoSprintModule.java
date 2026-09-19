package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;

/**
 * Keeps the sprint key held whenever you're moving forward, so you don't
 * have to hold it yourself. Pure vanilla options, no mixin.
 */
public class AutoSprintModule extends Module {
   public final BooleanSetting evenSneaking = this.addSetting(
      new BooleanSetting("Sneak Override", "Keep sprinting even while sneaking (bypasses vanilla's no-sprint-while-sneaking rule).", false)
   );

   public AutoSprintModule() {
      super("AutoSprint", "Automatically sprints while moving forward.", Category.PLAYER);
   }

   @Override
   protected void onDisable() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.options != null) mc.options.sprintKey.setPressed(false);
   }

   @Override
   public void onTick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player == null || mc.options == null) return;

      boolean movingForward = mc.options.forwardKey.isPressed();
      boolean canSprint = evenSneaking.get() || !mc.player.isSneaking();
      boolean hasFood = mc.player.getHungerManager().getFoodLevel() > 6 || mc.player.getAbilities().creativeMode;

      mc.options.sprintKey.setPressed(movingForward && canSprint && hasFood && !mc.player.isSwimming());
   }
}
