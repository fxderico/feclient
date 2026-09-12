package dev.fede.water.module.modules.combat;

import dev.fede.water.mixin.HandledScreenAccessor;
import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public final class HoverTotem extends Module {
   private int n = -1;

   public HoverTotem() {
      super("Hover Totem", Category.field_a_1);
   }

   @Override
   public void onDisable() {
      this.n = -1;
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (mc.player != null && mc.interactionManager != null) {
         if (mc.currentScreen instanceof HandledScreen var3) {
            Slot var4 = ((HandledScreenAccessor)var3).water$getFocusedSlot();
            if (var4 != null && !var4.getStack().isEmpty()) {
               if (!var4.getStack().isOf(Items.TOTEM_OF_UNDYING)) {
                  this.n = -1;
               } else if (!mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                  if (var4.id != this.n) {
                     int var2 = mc.player.currentScreenHandler.syncId;
                     mc.interactionManager.clickSlot(var2, var4.id, 40, SlotActionType.SWAP, mc.player);
                     this.n = var4.id;
                  }
               }
            } else {
               this.n = -1;
            }
         } else {
            this.n = -1;
         }
      }
   }
}

