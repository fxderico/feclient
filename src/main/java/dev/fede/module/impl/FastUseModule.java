package dev.fede.module.impl;

import dev.fede.mixin.MinecraftAccessor;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class FastUseModule extends Module {
   public final ModeSetting items = this.addSetting(new ModeSetting("Items", "What to speed up", "All", "All", "Pearls", "XP Bottles"));

   public FastUseModule() {
      super("FastUse", "Removes item use cooldowns", Category.MISC);
   }

   @Override
   public void onTick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         if (this.appliesTo(mc)) {
            ((MinecraftAccessor)mc).FeClient$setRightClickDelay(0);
         }
      }
   }

   private boolean appliesTo(MinecraftClient mc) {
      if (this.items.check("All")) {
         return true;
      } else {
         Item target = this.items.check("Pearls") ? Items.ENDER_PEARL : Items.EXPERIENCE_BOTTLE;
         return mc.player.getMainHandStack().isOf(target) || mc.player.getOffHandStack().isOf(target);
      }
   }
}

