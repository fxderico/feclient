package dev.fede.water.module.modules.combat;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public final class AutoTotem extends Module {
   private final Setting<Float> aw = new Setting<>("Delay", 1.0F, 0.0F, 5.0F);
   private int l;

   public AutoTotem() {
      super("Auto Totem", Category.field_a_1);
      this.addSetting(this.aw);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (mc.player != null) {
         int var1 = this.g();
         if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            this.l = var1;
         } else if (this.l > 0) {
            this.l--;
         } else {
            int var2 = this.a(Items.TOTEM_OF_UNDYING);
            if (var2 != -1) {
               mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, f(var2), 40, SlotActionType.SWAP, mc.player);
               this.l = var1;
            }
         }
      }
   }

   private int g() {
      double var1 = this.aw.getValue().floatValue();
      return (int)Math.round(var1 * 20.0);
   }

   public int a(Item item) {
      if (mc.player == null) {
         return -1;
      } else {
         for (int var2 = 0; var2 < 36; var2++) {
            if (mc.player.getInventory().getStack(var2).isOf(item)) {
               return var2;
            }
         }

         return -1;
      }
   }

   private static int f(int slotIndex) {
      return slotIndex < 9 ? 36 + slotIndex : slotIndex;
   }
}

