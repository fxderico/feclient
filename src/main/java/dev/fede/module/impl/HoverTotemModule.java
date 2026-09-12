package dev.fede.module.impl;

import dev.fede.mixin.AbstractContainerScreenAccessor;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.InventoryHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class HoverTotemModule extends Module {
   public final SliderSetting tickDelay = this.addSetting(new SliderSetting("Tick Delay", "", 0.0, 0.0, 20.0, 1.0));
   public final BooleanSetting hotbarTotem = this.addSetting(new BooleanSetting("Hotbar Totem", "", true));
   public final SliderSetting hotbarSlot = this.addSetting(new SliderSetting("Hotbar Slot", "", 1.0, 1.0, 9.0, 1.0));
   public final BooleanSetting autoSwitchToTotem = this.addSetting(new BooleanSetting("Auto Switch To Totem", "", false));
   private int tickCounter;

   public HoverTotemModule() {
      super("Hover Totem", "Equip totem when hovering over one in inventory", Category.COMBAT);
   }

   @Override
   protected void onEnable() {
      this.tickCounter = 0;
   }

   @Override
   protected void onDisable() {
      this.tickCounter = 0;
   }

   @Override
   public void onTick() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player != null && client.interactionManager != null) {
         if (this.autoSwitchToTotem.get() && this.hotbarTotem.get()) {
            int slot = this.hotbarSlot.getInt() - 1;
            if (client.player.getInventory().getStack(slot).isOf(Items.TOTEM_OF_UNDYING)) {
               InventoryHelper.swap(slot);
            }
         }

         if (client.currentScreen instanceof HandledScreen<?> handledScreen) {
            this.tickCounter++;
            if (this.tickCounter >= this.tickDelay.getInt()) {
               this.tickCounter = 0;
               Slot focusedSlot = this.getFocusedSlot(handledScreen);
               if (focusedSlot != null && focusedSlot.hasStack() && focusedSlot.getStack().isOf(Items.TOTEM_OF_UNDYING)) {
                  int slotId = focusedSlot.id;
                  client.interactionManager.clickSlot(handledScreen.getScreenHandler().syncId, slotId, 0, SlotActionType.PICKUP, client.player);
                  client.interactionManager.clickSlot(handledScreen.getScreenHandler().syncId, 45, 0, SlotActionType.PICKUP, client.player);
                  client.interactionManager.clickSlot(handledScreen.getScreenHandler().syncId, slotId, 0, SlotActionType.PICKUP, client.player);
               }
            }
         }
      }
   }

   private Slot getFocusedSlot(HandledScreen<?> screen) {
      return ((AbstractContainerScreenAccessor)screen).getHoveredSlot();
   }
}

