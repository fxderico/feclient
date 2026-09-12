package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.mixin.MinecraftClientInvoker;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.ChatFilterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class MiddleClickModule extends Module {
   private final ModeSetting action = new ModeSetting("Action", "AddFriend", "AddFriend", "ThrowPearl", "Pickblock", "OpenContainer");
   private final StringSetting storageFile = new StringSetting("FriendListStorageFile", "friends.json", 128);
   private boolean bool = false;
   private int intVal = -1;
   private boolean bool2 = false;
   private String string = null;

   public MiddleClickModule() {
      super("MiddleClick", "Runs a configurable action on middle mouse click", Category.PLAYER);
      this.run6(new Setting[]{this.action, this.storageFile});
      this.storageFile.visibleWhen(this::getBoolean5);
   }

   @Override
   public void run() {
      this.bool = this.isEnabled2();
      this.intVal = -1;
      this.bool2 = false;
      this.string = null;
   }

   @Override
   public void run2() {
      if (this.intVal >= 0 && class310.player != null) {
         try {
            class310.player.getInventory().setSelectedSlot(this.intVal);
         } catch (RuntimeException var2) {
         }
      }

      this.intVal = -1;
      this.bool = false;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.getWindow() != null) {
         if (this.intVal >= 0) {
            try {
               class310.player.getInventory().setSelectedSlot(this.intVal);
            } catch (RuntimeException var3) {
            }

            this.intVal = -1;
         }

         if (class310.currentScreen != null) {
            this.bool = false;
         } else {
            boolean var1 = this.isEnabled2();
            boolean var2 = var1 && !this.bool;
            this.bool = var1;
            if (var2) {
               if (this.action.check("AddFriend")) {
                  this.run4();
               } else if (this.action.check("ThrowPearl")) {
                  this.run5();
               } else if (this.action.check("Pickblock")) {
                  this.run7();
               } else if (this.action.check("OpenContainer")) {
                  this.run8();
               }
            }
         }
      }
   }

   private void run4() {
      if (class310.crosshairTarget instanceof EntityHitResult var2) {
         if (!(var2.getEntity() instanceof PlayerEntity var3)) {
            NotificationUtils.run8("MiddleClick", "AddFriend: target is not a player", INFO.UNKNOWN_3);
         } else {
            String var7 = var3.getGameProfile().name();
            if (var7 != null && !var7.isBlank()) {
               String var5 = this.storageFile.getValue();
               if (!this.bool2 || !var5.equals(this.string)) {
                  ChatFilterHelper.chatFilterHelper.run(var5);
                  this.string = var5;
                  this.bool2 = true;
               }

               boolean var6 = ChatFilterHelper.chatFilterHelper.check(var7);
               if (var6) {
                  ChatFilterHelper.chatFilterHelper.completableFutureOf(var5);
                  NotificationUtils.run8("MiddleClick", "Added friend: null", INFO.UNKNOWN_2);
               } else {
                  NotificationUtils.run8("MiddleClick", "Already a friend: null", INFO.UNKNOWN);
               }
            } else {
               NotificationUtils.run8("MiddleClick", "AddFriend: target has no name", INFO.UNKNOWN_4);
            }
         }
      } else {
         NotificationUtils.run8("MiddleClick", "AddFriend: aim at a player", INFO.UNKNOWN_3);
      }
   }

   private void run5() {
      PlayerInventory var1 = class310.player.getInventory();
      int var2 = intOf(var1);
      if (var2 < 0) {
         NotificationUtils.run8("MiddleClick", "ThrowPearl: no ender pearl in hotbar", INFO.UNKNOWN_3);
      } else {
         int var3 = var1.getSelectedSlot();
         if (var3 == var2) {
            this.run6();
         } else {
            var1.setSelectedSlot(var2);
            this.run6();
            this.intVal = var3;
         }
      }
   }

   private void run6() {
      try {
         if (class310.interactionManager != null) {
            class310.interactionManager.interactItem(class310.player, Hand.MAIN_HAND);
         }
      } catch (RuntimeException var2) {
         System.err.println("[MiddleClick] pearl interactItem failed: " + var2.getMessage());
      }
   }

   private void run7() {
      HitResult var1 = class310.crosshairTarget;
      if (var1 instanceof BlockHitResult var2 && var1.getType() == Type.BLOCK) {
         BlockPos var3 = var2.getBlockPos();
         Block var4 = class310.world.getBlockState(var3).getBlock();
         if (var4 != null) {
            Item var5 = var4.asItem();
            if (var5 != null && var5 != Items.AIR) {
               PlayerInventory var6 = class310.player.getInventory();
               int var7 = var6.getMainStacks().size();
               int var8 = -1;
               int var9 = -1;

               for (int var10 = 0; var10 < var7; var10++) {
                  ItemStack var11 = var6.getStack(var10);
                  if (!var11.isEmpty() && var11.getItem() == var5) {
                     if (var10 < PlayerInventory.getHotbarSize()) {
                        var8 = var10;
                        break;
                     }

                     if (var9 < 0) {
                        var9 = var10;
                     }
                  }
               }

               if (var8 >= 0) {
                  var6.setSelectedSlot(var8);
               } else if (var9 >= 0) {
                  if (class310.interactionManager != null && class310.player.playerScreenHandler != null) {
                     int var13 = class310.player.playerScreenHandler.syncId;
                     int var12 = var6.getSelectedSlot();
                     class310.interactionManager.clickSlot(var13, var9, var12, SlotActionType.SWAP, class310.player);
                  }
               } else {
                  NotificationUtils.run8("MiddleClick", "Pickblock: block not in inventory", INFO.UNKNOWN);
               }
            } else {
               NotificationUtils.run8("MiddleClick", "Pickblock: no item form for this block", INFO.UNKNOWN_3);
            }
         }
      } else {
         NotificationUtils.run8("MiddleClick", "Pickblock: aim at a block", INFO.UNKNOWN_3);
      }
   }

   private void run8() {
      HitResult var1 = class310.crosshairTarget;
      if (var1 instanceof BlockHitResult var2 && var1.getType() == Type.BLOCK) {
         BlockPos var3 = var2.getBlockPos();
         BlockEntity var4 = class310.world.getBlockEntity(var3);
         if (!(var4 instanceof Inventory)) {
            NotificationUtils.run8("MiddleClick", "OpenContainer: target block has no inventory", INFO.UNKNOWN_3);
         } else {
            this.run9();
         }
      } else {
         NotificationUtils.run8("MiddleClick", "OpenContainer: aim at a container block", INFO.UNKNOWN_3);
      }
   }

   private boolean isEnabled2() {
      try {
         long var1 = class310.getWindow().getHandle();
         return GLFW.glfwGetMouseButton(var1, 2) == 1;
      } catch (RuntimeException var3) {
         return false;
      }
   }

   private static int intOf(PlayerInventory var0) {
      int var1 = PlayerInventory.getHotbarSize();

      for (int var2 = 0; var2 < var1; var2++) {
         ItemStack var3 = var0.getStack(var2);
         if (!var3.isEmpty() && var3.getItem() == Items.ENDER_PEARL) {
            return var2;
         }
      }

      return -1;
   }

   private void run9() {
      try {
         ((MinecraftClientInvoker)class310).nyx$doItemUse();
      } catch (RuntimeException var2) {
         System.err.println("[MiddleClick] doItemUse invoke failed: " + var2.getMessage());
      }
   }

   private Boolean getBoolean5() {
      return this.action.check("AddFriend");
   }
}

