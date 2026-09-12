package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoInventoryTotemModule extends Module {
   public final SliderSetting delay = this.addSetting(new SliderSetting("Delay", "", 4.0, 1.0, 40.0, 1.0));
   public final BooleanSetting forceTotem = this.addSetting(new BooleanSetting("Force Totem", "", false));
   public final BooleanSetting hotbarTotem = this.addSetting(new BooleanSetting("Hotbar Totem", "", false));
   public final SliderSetting hotbarSlot = this.addSetting(new SliderSetting("Hotbar Slot", "", 1.0, 1.0, 9.0, 1.0));
   public final SliderSetting hotbarDelay = this.addSetting(new SliderSetting("Hotbar Delay", "", 4.0, 1.0, 40.0, 1.0));
   private int tickCounter;

   public AutoInventoryTotemModule() {
      super("Inv Totem", "Moves totems only while inventory (E) is open — click-based, not world-auto", Category.COMBAT);
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
         if (client.currentScreen instanceof InventoryScreen) {
            this.tickCounter++;
            int wait = this.hotbarTotem.get() && this.needsHotbarWork(client) && !this.needsOffhandWork(client)
               ? this.hotbarDelay.getInt()
               : this.delay.getInt();
            if (this.tickCounter >= wait) {
               this.tickCounter = 0;
               if (this.hotbarTotem.get() && this.needsHotbarWork(client)) {
                  int source = findTotemInMainInventory(client);
                  if (source != -1) {
                     int hotbarIndex = this.hotbarSlot.getInt() - 1;
                     if (hotbarIndex >= 0 && hotbarIndex <= 8) {
                        this.performHotbarSwap(client, source, hotbarIndex);
                     }
                  }
               } else if (this.needsOffhandWork(client)) {
                  int totemIdx = findTotemForOffhand(client);
                  if (totemIdx != -1) {
                     this.performOffhandSwap(client, totemIdx);
                  }
               }
            }
         }
      }
   }

   private boolean needsOffhandWork(MinecraftClient client) {
      boolean hasTotemOffhand = client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
      return this.forceTotem.get() || !hasTotemOffhand;
   }

   private boolean needsHotbarWork(MinecraftClient client) {
      if (!this.hotbarTotem.get()) {
         return false;
      } else {
         int slot = this.hotbarSlot.getInt() - 1;
         return !client.player.getInventory().getStack(slot).isOf(Items.TOTEM_OF_UNDYING);
      }
   }

   private static int findTotemInMainInventory(MinecraftClient client) {
      for (int i = 9; i < 36; i++) {
         if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
            return i;
         }
      }

      return -1;
   }

   private static int findTotemInHotbar(MinecraftClient client) {
      for (int i = 0; i < 9; i++) {
         if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
            return i;
         }
      }

      return -1;
   }

   private static int findTotemForOffhand(MinecraftClient client) {
      int main = findTotemInMainInventory(client);
      return main != -1 ? main : findTotemInHotbar(client);
   }

   private static int toScreenSlot(int invIndex) {
      return invIndex < 9 ? invIndex + 36 : invIndex;
   }

   private void performOffhandSwap(MinecraftClient client, int totemInvIndex) {
      int syncId = client.player.currentScreenHandler.syncId;
      int screenSlot = toScreenSlot(totemInvIndex);
      client.interactionManager.clickSlot(syncId, screenSlot, 0, SlotActionType.PICKUP, client.player);
      client.interactionManager.clickSlot(syncId, 45, 0, SlotActionType.PICKUP, client.player);
      client.interactionManager.clickSlot(syncId, screenSlot, 0, SlotActionType.PICKUP, client.player);
   }

   private void performHotbarSwap(MinecraftClient client, int sourceInvIndex, int hotbarIndex) {
      int syncId = client.player.currentScreenHandler.syncId;
      client.interactionManager.clickSlot(syncId, sourceInvIndex, hotbarIndex, SlotActionType.SWAP, client.player);
   }
}

