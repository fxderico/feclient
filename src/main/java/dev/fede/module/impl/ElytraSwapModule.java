package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.KeybindSetting;
import dev.fede.settings.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class ElytraSwapModule extends Module {
   public final KeybindSetting activateKey = this.addSetting(new KeybindSetting("Activate Key", "", 71));
   public final SliderSetting swapDelay = this.addSetting(new SliderSetting("Swap Delay", "", 0.0, 0.0, 20.0, 1.0));
   public final BooleanSetting switchBack = this.addSetting(new BooleanSetting("Switch Back", "", true));
   public final SliderSetting switchDelay = this.addSetting(new SliderSetting("Switch Delay", "", 0.0, 0.0, 20.0, 1.0));
   public final BooleanSetting moveToSlot = this.addSetting(new BooleanSetting("Move To Slot", "", true));
   public final SliderSetting elytraSlot = this.addSetting(new SliderSetting("Elytra Slot", "", 9.0, 1.0, 9.0, 1.0));
   private boolean keyWasDown;
   private boolean swappedToElytra;
   private int tickCounter;
   private boolean waitingForSwap;
   private boolean waitingForSwitchBack;

   public ElytraSwapModule() {
      super("Elytra Swap", "Swap between Elytra and Chestplate with a keybind", Category.COMBAT);
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   @Override
   public void onTick() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player != null && client.interactionManager != null) {
         if (this.waitingForSwap) {
            this.tickCounter++;
            if (this.tickCounter >= this.swapDelay.getInt()) {
               this.performSwap(client);
               this.waitingForSwap = false;
               if (this.switchBack.get()) {
                  this.waitingForSwitchBack = true;
                  this.tickCounter = 0;
               }
            }
         } else if (this.waitingForSwitchBack) {
            this.tickCounter++;
            if (this.tickCounter >= this.switchDelay.getInt()) {
               this.performSwap(client);
               this.waitingForSwitchBack = false;
            }
         } else {
            int key = this.activateKey.get();
            if (key != -1) {
               long handle = client.getWindow().getHandle();
               boolean pressed = key <= 7 ? GLFW.glfwGetMouseButton(handle, key) == 1 : GLFW.glfwGetKey(handle, key) == 1;
               if (pressed && !this.keyWasDown) {
                  this.waitingForSwap = true;
                  this.tickCounter = 0;
               }

               this.keyWasDown = pressed;
            }
         }
      }
   }

   private void performSwap(MinecraftClient client) {
      ItemStack chestSlot = client.player.getEquippedStack(EquipmentSlot.CHEST);
      boolean wearingElytra = chestSlot.isOf(Items.ELYTRA);
      int targetSlot = -1;
      if (wearingElytra) {
         for (int i = 0; i < 36; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            EquippableComponent equippable = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
            if (equippable != null && equippable.slot() == EquipmentSlot.CHEST && !stack.isOf(Items.ELYTRA)) {
               targetSlot = i;
               break;
            }
         }
      } else {
         if (this.moveToSlot.get()) {
            int slotIdx = this.elytraSlot.getInt() - 1;
            ItemStack stack = client.player.getInventory().getStack(slotIdx);
            if (stack.isOf(Items.ELYTRA)) {
               targetSlot = slotIdx;
            }
         }

         if (targetSlot == -1) {
            for (int ix = 0; ix < 36; ix++) {
               if (client.player.getInventory().getStack(ix).isOf(Items.ELYTRA)) {
                  targetSlot = ix;
                  break;
               }
            }
         }
      }

      if (targetSlot != -1) {
         int screenSlot = targetSlot < 9 ? targetSlot + 36 : targetSlot;
         int armorScreenSlot = 6;
         client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, screenSlot, 0, SlotActionType.PICKUP, client.player);
         client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, armorScreenSlot, 0, SlotActionType.PICKUP, client.player);
         client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, screenSlot, 0, SlotActionType.PICKUP, client.player);
         this.swappedToElytra = !wearingElytra;
      }
   }

   private void resetState() {
      this.keyWasDown = false;
      this.swappedToElytra = false;
      this.tickCounter = 0;
      this.waitingForSwap = false;
      this.waitingForSwitchBack = false;
   }
}

