package dev.fede.water.setting;

import net.minecraft.item.ItemStack;

public record ItemOption(String aa, String ab, ItemStack b) {
   public String value() {
      return this.aa;
   }
}

