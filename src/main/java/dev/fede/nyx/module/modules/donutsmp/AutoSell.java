package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.storage.Chest;
import dev.fede.nyx.storage.Chest$KindUtils;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoSell extends Module {
   private final ModeSetting mode = new ModeSetting("Mode", "Command", "Command", "Menu");
   private final NumberSetting intervalSec = new NumberSetting("IntervalSec", 120.0, 10.0, 600.0, 5.0);
   private final NumberSetting minItemsInInventory = new NumberSetting("MinItemsInInventory", 20.0, 1.0, 36.0, 1.0);
   private final StringSetting command = new StringSetting("Command", "sell all", 64);
   private final StringSetting openCommand = new StringSetting("OpenCommand", "sell", 64);
   private final StringSetting itemsToSell = new StringSetting("ItemsToSell", "basalt", 256);
   private final StringSetting screenTitleContains = new StringSetting("ScreenTitleContains", "sell", 64);
   private final StringSetting confirmItemContains = new StringSetting("ConfirmItemContains", "sell", 64);
   private final NumberSetting clickDelayTicks = new NumberSetting("ClickDelayTicks", 3.0, 1.0, 20.0, 1.0);
   private final NumberSetting openTimeoutTicks = new NumberSetting("OpenTimeoutTicks", 40.0, 5.0, 200.0, 5.0);
   private final BooleanSetting closeAfter = new BooleanSetting("CloseAfter", true);
   private final BooleanSetting autoLoot = new BooleanSetting("AutoLoot", false);
   private final NumberSetting lootRadius = new NumberSetting("LootRadius", 5.0, 2.0, 8.0, 1.0);
   private final BooleanSetting openableCheck = new BooleanSetting("OpenableCheck", true);
   private final StringSetting itemsToLoot = new StringSetting("ItemsToLoot", "basalt", 256);
   private final NumberSetting maxItemsPerChest = new NumberSetting("MaxItemsPerChest", 27.0, 1.0, 54.0, 1.0);
   private final Set<BlockPos> set = new HashSet<>();
   private BlockPos class2338 = null;
   private int intVal = 0;
   private AutoSell.State autoSellState = AutoSell.State.IDLE;
   private int intVal2;
   private long longVal;
   private int intVal3;
   private static final int intVal4 = 20;
   private int intVal5;

   public AutoSell() {
      super("AutoSell", "Timer-driven /sell — Command fires /sell all, Menu opens /sell GUI and deposits filtered items", Category.DONUTSMP);
      this.run6(
         new Setting[]{
            this.mode,
            this.intervalSec,
            this.minItemsInInventory,
            this.command,
            this.openCommand,
            this.itemsToSell,
            this.screenTitleContains,
            this.confirmItemContains,
            this.clickDelayTicks,
            this.openTimeoutTicks,
            this.closeAfter,
            this.autoLoot,
            this.lootRadius,
            this.openableCheck,
            this.itemsToLoot,
            this.maxItemsPerChest
         }
      );
      this.command.visibleWhen(this::getBoolean13);
      this.openCommand.visibleWhen(this::getBoolean12);
      this.itemsToSell.visibleWhen(this::getBoolean11);
      this.screenTitleContains.visibleWhen(this::getBoolean10);
      this.confirmItemContains.visibleWhen(this::getBoolean9);
      this.clickDelayTicks.visibleWhen(this::getBoolean8);
      this.openTimeoutTicks.visibleWhen(this::getBoolean7);
      this.closeAfter.visibleWhen(this::getBoolean6);
      this.autoLoot.visibleWhen(this::getBoolean5);
      this.lootRadius.visibleWhen(this::getBoolean4);
      this.openableCheck.visibleWhen(this::getBoolean3);
      this.itemsToLoot.visibleWhen(this::getBoolean2);
      this.maxItemsPerChest.visibleWhen(this::getBoolean);
   }

   @Override
   public void run() {
      this.intVal5 = 0;
      this.autoSellState = AutoSell.State.IDLE;
      this.intVal2 = 0;
      this.intVal3 = 0;
      this.longVal = System.currentTimeMillis();
   }

   @Override
   public void run2() {
      this.autoSellState = AutoSell.State.IDLE;
      this.intVal2 = 0;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.player.networkHandler != null) {
         if ("Menu".equals(this.mode.getMode())) {
            this.run5();
         } else {
            this.run4();
         }
      }
   }

   private void run4() {
      if (++this.intVal5 >= 20) {
         this.intVal5 = 0;
         long var1 = System.currentTimeMillis();
         long var3 = this.intervalSec.getValueInt() * 1000L;
         if (var1 - this.longVal >= var3) {
            if (intOf(class310.player.getInventory()) >= this.minItemsInInventory.getValueInt()) {
               String var5 = this.command.getValue();
               if (var5 != null && !var5.isBlank()) {
                  if (var5.charAt(0) == '/') {
                     var5 = var5.substring(1);
                     if (var5.isBlank()) {
                        return;
                     }
                  }

                  try {
                     class310.player.networkHandler.sendChatCommand(var5);
                     this.longVal = var1;
                  } catch (Throwable var7) {
                  }
               }
            }
         }
      }
   }

   private void run5() {
      long var1 = System.currentTimeMillis();
      long var3 = this.intervalSec.getValueInt() * 1000L;
      switch (this.autoSellState) {
         case IDLE:
            if (var1 - this.longVal < var3) {
               return;
            }

            if (this.autoLoot.getValue()) {
               this.set.clear();
               BlockPos var11 = this.getclass2338();
               if (var11 != null) {
                  this.class2338 = var11;
                  this.intVal = 0;
                  this.autoSellState = AutoSell.State.LOOT_OPEN;
                  this.intVal2 = 0;
                  return;
               }
            }

            if (this.getInt4() < this.minItemsInInventory.getValueInt()) {
               return;
            }

            this.run6(var1);
            break;
         case LOOT_OPEN:
            if (this.class2338 == null) {
               this.autoSellState = AutoSell.State.IDLE;
               return;
            }

            if (!this.check2(this.class2338)) {
               this.set.add(this.class2338);
               this.class2338 = null;
               this.autoSellState = AutoSell.State.IDLE;
               return;
            }

            this.autoSellState = AutoSell.State.LOOT_WAIT;
            this.intVal2 = 0;
            break;
         case LOOT_WAIT:
            this.intVal2++;
            if (this.isEnabled2()) {
               this.autoSellState = AutoSell.State.LOOT_EXTRACT;
               this.intVal2 = 0;
            } else if (this.intVal2 > this.openTimeoutTicks.getValueInt()) {
               this.set.add(this.class2338);
               this.class2338 = null;
               this.autoSellState = AutoSell.State.IDLE;
            }
            break;
         case LOOT_EXTRACT:
            if (++this.intVal2 < this.clickDelayTicks.getValueInt()) {
               return;
            }

            this.intVal2 = 0;
            if (!this.isEnabled2()) {
               if (this.class2338 != null) {
                  this.set.add(this.class2338);
               }

               this.class2338 = null;
               this.autoSellState = AutoSell.State.IDLE;
               return;
            }

            if (this.intVal >= this.maxItemsPerChest.getValueInt() || this.isEnabled()) {
               this.autoSellState = AutoSell.State.LOOT_CLOSE;
               return;
            }

            int var10 = this.getInt2();
            if (var10 < 0) {
               this.autoSellState = AutoSell.State.LOOT_CLOSE;
               return;
            }

            this.run7(var10);
            this.intVal++;
            break;
         case LOOT_CLOSE:
            try {
               class310.player.closeHandledScreen();
            } catch (Throwable var7) {
            }

            if (this.class2338 != null) {
               this.set.add(this.class2338);
            }

            this.class2338 = null;
            this.intVal2 = 0;
            BlockPos var9 = this.getclass2338();
            if (var9 != null && !this.isEnabled()) {
               this.class2338 = var9;
               this.intVal = 0;
               this.autoSellState = AutoSell.State.LOOT_OPEN;
            } else if (this.getInt4() >= this.minItemsInInventory.getValueInt()) {
               this.run6(var1);
            } else {
               this.longVal = var1;
               this.autoSellState = AutoSell.State.IDLE;
            }
            break;
         case WAIT_SCREEN:
            this.intVal2++;
            if (this.isEnabled3()) {
               this.autoSellState = AutoSell.State.DEPOSITING;
               this.intVal2 = 0;
            } else if (this.intVal2 > this.openTimeoutTicks.getValueInt()) {
               this.autoSellState = AutoSell.State.IDLE;
               this.longVal = var1;
            }
            break;
         case DEPOSITING:
            if (++this.intVal2 < this.clickDelayTicks.getValueInt()) {
               return;
            }

            this.intVal2 = 0;
            if (!this.isEnabled3()) {
               this.autoSellState = AutoSell.State.IDLE;
               this.longVal = var1;
               return;
            }

            int var8 = this.getInt();
            if (var8 < 0) {
               this.autoSellState = AutoSell.State.CONFIRMING;
               return;
            }

            this.run7(var8);
            break;
         case CONFIRMING:
            if (++this.intVal2 < this.clickDelayTicks.getValueInt()) {
               return;
            }

            this.intVal2 = 0;
            if (!this.isEnabled3()) {
               this.autoSellState = AutoSell.State.DONE;
               return;
            }

            int var5 = this.getInt3();
            if (var5 < 0) {
               this.autoSellState = AutoSell.State.DONE;
               return;
            }

            this.run8(var5);
            this.autoSellState = AutoSell.State.DONE;
            break;
         case DONE:
            if (this.closeAfter.getValue()) {
               try {
                  class310.player.closeHandledScreen();
               } catch (Throwable var6) {
               }
            }

            this.longVal = var1;
            this.autoSellState = AutoSell.State.IDLE;
            this.intVal2 = 0;
      }
   }

   private void run6(long var1) {
      String var3 = this.openCommand.getValue();
      if (var3 != null && !var3.isBlank()) {
         if (var3.charAt(0) == '/') {
            var3 = var3.substring(1);
         }

         try {
            class310.player.networkHandler.sendChatCommand(var3);
            this.autoSellState = AutoSell.State.WAIT_SCREEN;
            this.intVal2 = 0;
            this.intVal3 = 0;
         } catch (Throwable var5) {
            this.longVal = var1;
            this.autoSellState = AutoSell.State.IDLE;
         }
      } else {
         this.longVal = var1;
         this.autoSellState = AutoSell.State.IDLE;
      }
   }

   private BlockPos getclass2338() {
      try {
         Vec3d var1 = class310.player.getEntityPos();
         double var2 = this.lootRadius.getValueInt();
         double var4 = var2 * var2;
         BlockPos var6 = null;

         for (Chest var8 : Chest$KindUtils.all()) {
            BlockPos var9 = var8.pos();
            if (var9 != null && !this.set.contains(var9)) {
               double var10 = var9.getX() + 0.5 - var1.x;
               double var12 = var9.getY() + 0.5 - var1.y;
               double var14 = var9.getZ() + 0.5 - var1.z;
               double var16 = var10 * var10 + var12 * var12 + var14 * var14;
               if (!(var16 > var4) && (!this.openableCheck.getValue() || this.check(var9))) {
                  var6 = var9;
                  var4 = var16;
               }
            }
         }

         return var6;
      } catch (Throwable var18) {
         return null;
      }
   }

   private boolean check(BlockPos var1) {
      try {
         BlockState var2 = class310.world.getBlockState(var1);
         Block var3 = var2.getBlock();
         if (var3 instanceof BarrelBlock) {
            return true;
         } else if (!(var3 instanceof ChestBlock) && !(var3 instanceof TrappedChestBlock) && !(var3 instanceof EnderChestBlock)) {
            return true;
         } else {
            BlockPos var4 = var1.up();
            BlockState var5 = class310.world.getBlockState(var4);
            return !var5.isSolidBlock(class310.world, var4);
         }
      } catch (Throwable var6) {
         return true;
      }
   }

   private boolean check2(BlockPos var1) {
      try {
         if (class310.interactionManager == null) {
            return false;
         } else {
            BlockHitResult var2 = new BlockHitResult(new Vec3d(var1.getX() + 0.5, var1.getY() + 1.0, var1.getZ() + 0.5), Direction.UP, var1, false);
            class310.interactionManager.interactBlock(class310.player, Hand.MAIN_HAND, var2);
            return true;
         }
      } catch (Throwable var3) {
         return false;
      }
   }

   private boolean isEnabled2() {
      try {
         return class310.currentScreen != null
            && class310.player.currentScreenHandler != null
            && class310.player.currentScreenHandler != class310.player.playerScreenHandler
            && class310.player.currentScreenHandler.slots.size() > 36;
      } catch (Throwable var2) {
         return false;
      }
   }

   private int getInt2() {
      try {
         ScreenHandler var1 = class310.player.currentScreenHandler;
         if (var1 == null) {
            return -1;
         }

         int var2 = var1.slots.size();
         int var3 = var2 - 36;
         String[] var4 = this.getStringArray();

         for (int var5 = 0; var5 < var3; var5++) {
            Slot var6 = (Slot)var1.slots.get(var5);
            if (var6 != null) {
               ItemStack var7 = var6.getStack();
               if (var7 != null && !var7.isEmpty() && check3(var7, var4)) {
                  return var5;
               }
            }
         }
      } catch (Throwable var8) {
      }

      return -1;
   }

   private String[] getStringArray() {
      String var1 = this.itemsToLoot.getValue();
      if (var1 != null && !var1.isBlank()) {
         String[] var2 = var1.split(",");
         String[] var3 = new String[var2.length];
         int var4 = 0;

         for (String var8 : var2) {
            String var9 = var8.trim().toLowerCase(Locale.ROOT);
            if (!var9.isEmpty()) {
               var3[var4++] = var9;
            }
         }

         if (var4 == var3.length) {
            return var3;
         } else {
            String[] var10 = new String[var4];
            System.arraycopy(var3, 0, var10, 0, var4);
            return var10;
         }
      } else {
         return new String[0];
      }
   }

   public boolean isEnabled() {
      try {
         PlayerInventory var1 = class310.player.getInventory();
         int var2 = var1.getMainStacks().size();

         for (int var3 = 0; var3 < var2; var3++) {
            ItemStack var4 = var1.getStack(var3);
            if (var4 == null || var4.isEmpty()) {
               return false;
            }
         }

         return true;
      } catch (Throwable var5) {
         return false;
      }
   }

   public boolean isEnabled3() {
      try {
         if (class310.currentScreen != null
            && class310.player.currentScreenHandler != null
            && class310.player.currentScreenHandler != class310.player.playerScreenHandler) {
            Text var1 = class310.currentScreen.getTitle();
            if (var1 == null) {
               return false;
            } else {
               String var2 = this.screenTitleContains.getValue();
               return var2 != null && !var2.isBlank() ? var1.getString().toLowerCase(Locale.ROOT).contains(var2.toLowerCase(Locale.ROOT)) : true;
            }
         } else {
            return false;
         }
      } catch (Throwable var3) {
         return false;
      }
   }

   public int getInt() {
      try {
         ScreenHandler var1 = class310.player.currentScreenHandler;
         if (var1 == null) {
            return -1;
         }

         int var2 = var1.slots.size();
         int var3 = var2 - 36;
         String[] var4 = this.getStringArray2();

         for (int var5 = var3; var5 < var2; var5++) {
            Slot var6 = (Slot)var1.slots.get(var5);
            if (var6 != null) {
               ItemStack var7 = var6.getStack();
               if (var7 != null && !var7.isEmpty() && check3(var7, var4)) {
                  return var5;
               }
            }
         }
      } catch (Throwable var8) {
      }

      return -1;
   }

   private int getInt3() {
      try {
         ScreenHandler var1 = class310.player.currentScreenHandler;
         if (var1 == null) {
            return -1;
         }

         int var2 = var1.slots.size();
         int var3 = var2 - 36;
         String var4 = this.confirmItemContains.getValue();
         if (var4 == null || var4.isBlank()) {
            return -1;
         }

         String var5 = var4.toLowerCase(Locale.ROOT);

         for (int var6 = 0; var6 < var3; var6++) {
            Slot var7 = (Slot)var1.slots.get(var6);
            if (var7 != null) {
               ItemStack var8 = var7.getStack();
               if (var8 != null && !var8.isEmpty()) {
                  String var9 = Registries.ITEM.getId(var8.getItem()).toString().toLowerCase(Locale.ROOT);
                  if (var9.contains(var5)) {
                     return var6;
                  }

                  Text var10 = var8.getName();
                  if (var10 != null && var10.getString().toLowerCase(Locale.ROOT).contains(var5)) {
                     return var6;
                  }
               }
            }
         }
      } catch (Throwable var11) {
      }

      return -1;
   }

   public void run7(int var1) {
      try {
         ScreenHandler var2 = class310.player.currentScreenHandler;
         if (var2 == null || class310.interactionManager == null) {
            return;
         }

         class310.interactionManager.clickSlot(var2.syncId, var1, 0, SlotActionType.QUICK_MOVE, class310.player);
      } catch (Throwable var3) {
      }
   }

   private void run8(int var1) {
      try {
         ScreenHandler var2 = class310.player.currentScreenHandler;
         if (var2 == null || class310.interactionManager == null) {
            return;
         }

         class310.interactionManager.clickSlot(var2.syncId, var1, 0, SlotActionType.PICKUP, class310.player);
      } catch (Throwable var3) {
      }
   }

   private String[] getStringArray2() {
      String var1 = this.itemsToSell.getValue();
      if (var1 != null && !var1.isBlank()) {
         String[] var2 = var1.split(",");
         String[] var3 = new String[var2.length];
         int var4 = 0;

         for (String var8 : var2) {
            String var9 = var8.trim().toLowerCase(Locale.ROOT);
            if (!var9.isEmpty()) {
               var3[var4++] = var9;
            }
         }

         if (var4 == var3.length) {
            return var3;
         } else {
            String[] var10 = new String[var4];
            System.arraycopy(var3, 0, var10, 0, var4);
            return var10;
         }
      } else {
         return new String[0];
      }
   }

   private static boolean check3(ItemStack var0, String[] var1) {
      if (var1.length == 0) {
         return true;
      } else {
         String var2;
         try {
            var2 = Registries.ITEM.getId(var0.getItem()).toString().toLowerCase(Locale.ROOT);
         } catch (Throwable var7) {
            return false;
         }

         for (String var6 : var1) {
            if (var2.contains(var6)) {
               return true;
            }
         }

         return false;
      }
   }

   private int getInt4() {
      try {
         PlayerInventory var1 = class310.player.getInventory();
         int var2 = var1.getMainStacks().size();
         String[] var3 = this.getStringArray2();
         int var4 = 0;

         for (int var5 = 0; var5 < var2; var5++) {
            ItemStack var6 = var1.getStack(var5);
            if (var6 != null && !var6.isEmpty() && check3(var6, var3)) {
               var4++;
            }
         }

         return var4;
      } catch (Throwable var7) {
         return 0;
      }
   }

   private static int intOf(PlayerInventory var0) {
      int var1 = var0.getMainStacks().size();
      int var2 = 0;

      for (int var3 = 0; var3 < var1; var3++) {
         ItemStack var4 = var0.getStack(var3);
         if (var4 != null && !var4.isEmpty()) {
            var2++;
         }
      }

      return var2;
   }

   private Boolean getBoolean() {
      return "Menu".equals(this.mode.getMode()) && this.autoLoot.getValue();
   }

   private Boolean getBoolean2() {
      return "Menu".equals(this.mode.getMode()) && this.autoLoot.getValue();
   }

   private Boolean getBoolean3() {
      return "Menu".equals(this.mode.getMode()) && this.autoLoot.getValue();
   }

   private Boolean getBoolean4() {
      return "Menu".equals(this.mode.getMode()) && this.autoLoot.getValue();
   }

   private Boolean getBoolean5() {
      return "Menu".equals(this.mode.getMode());
   }

   private Boolean getBoolean6() {
      return "Menu".equals(this.mode.getMode());
   }

   private Boolean getBoolean7() {
      return "Menu".equals(this.mode.getMode());
   }

   private Boolean getBoolean8() {
      return "Menu".equals(this.mode.getMode());
   }

   private Boolean getBoolean9() {
      return "Menu".equals(this.mode.getMode());
   }

   private Boolean getBoolean10() {
      return "Menu".equals(this.mode.getMode());
   }

   private Boolean getBoolean11() {
      return "Menu".equals(this.mode.getMode());
   }

   private Boolean getBoolean12() {
      return "Menu".equals(this.mode.getMode());
   }

   private Boolean getBoolean13() {
      return "Command".equals(this.mode.getMode());
   }

   private static enum State {
      IDLE,
      LOOT_OPEN,
      LOOT_WAIT,
      LOOT_EXTRACT,
      LOOT_CLOSE,
      WAIT_SCREEN,
      DEPOSITING,
      CONFIRMING,
      DONE;

      private static final AutoSell.State[] autoSellStateArray = getAutoSellStateArray();

      private static AutoSell.State[] getAutoSellStateArray() {
         return new AutoSell.State[]{IDLE, LOOT_OPEN, LOOT_WAIT, LOOT_EXTRACT, LOOT_CLOSE, WAIT_SCREEN, DEPOSITING, CONFIRMING, DONE};
      }
   }
}

