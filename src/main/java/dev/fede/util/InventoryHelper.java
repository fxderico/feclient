package dev.fede.util;

import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class InventoryHelper {
   private InventoryHelper() {
   }

   private static MinecraftClient mc() {
      return MinecraftClient.getInstance();
   }

   public static int toScreenSlot(int invIndex) {
      return invIndex < 9 ? invIndex + 36 : invIndex;
   }

   public static void selectHotbarSlot(int slot) {
      MinecraftClient mc = mc();
      if (slot >= 0 && slot <= 8 && mc.player != null) {
         if (mc.player.getInventory().getSelectedSlot() != slot) {
            mc.player.getInventory().setSelectedSlot(slot);
            if (mc.getNetworkHandler() != null) {
               mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }
         }
      }
   }

   public static void swapOffhand() {
      MinecraftClient mc = mc();
      if (mc.player != null && mc.getNetworkHandler() != null) {
         mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
      }
   }

   public static void swapInventoryToHotbar(int invIndex, int hotbarIndex) {
      MinecraftClient mc = mc();
      if (mc.player != null && mc.interactionManager != null) {
         if (invIndex >= 9 && invIndex <= 35 && hotbarIndex >= 0 && hotbarIndex <= 8) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, toScreenSlot(invIndex), hotbarIndex, SlotActionType.SWAP, mc.player);
         }
      }
   }

   public static void swap(int slot) {
      selectHotbarSlot(slot);
   }

   public static Hand handHolding(Item item) {
      ClientPlayerEntity player = mc().player;
      if (player == null) {
         return null;
      } else if (player.getMainHandStack().isOf(item)) {
         return Hand.MAIN_HAND;
      } else {
         return player.getOffHandStack().isOf(item) ? Hand.OFF_HAND : null;
      }
   }

   public static int getHotbarSlot(Item item) {
      ClientPlayerEntity player = mc().player;
      if (player == null) {
         return -1;
      } else {
         for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isOf(item)) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int findItemSlot(Item item) {
      ClientPlayerEntity player = mc().player;
      if (player == null) {
         return -1;
      } else {
         for (int i = 0; i < 36; i++) {
            if (player.getInventory().getStack(i).isOf(item)) {
               return i;
            }
         }

         return -1;
      }
   }

   public static boolean swapToItem(Item item) {
      int slot = getHotbarSlot(item);
      if (slot < 0) {
         return false;
      } else {
         selectHotbarSlot(slot);
         return true;
      }
   }

   public static boolean swapToStack(Predicate<ItemStack> predicate) {
      ClientPlayerEntity player = mc().player;
      if (player == null) {
         return false;
      } else {
         for (int i = 0; i < 9; i++) {
            if (predicate.test(player.getInventory().getStack(i))) {
               selectHotbarSlot(i);
               return true;
            }
         }

         return false;
      }
   }

   public static int findEmptyHotbarSlot() {
      ClientPlayerEntity player = mc().player;
      if (player == null) {
         return -1;
      } else {
         for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isEmpty()) {
               return i;
            }
         }

         return -1;
      }
   }
}

