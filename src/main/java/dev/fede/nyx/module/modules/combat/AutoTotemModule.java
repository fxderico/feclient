package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotemModule extends Module {
   private final ModeSetting mode = new ModeSetting("Mode", "HealthWatch", "HealthWatch", "InventoryTotem");
   private final NumberSetting healthThreshold = new NumberSetting("HealthThreshold", 8.0, 1.0, 20.0, 0.5);
   private final BooleanSetting onlyIfDamageIncoming = new BooleanSetting("OnlyIfDamageIncoming", false);
   private final NumberSetting delayTicks = new NumberSetting("DelayTicks", 1.0, 0.0, 20.0, 1.0);
   private final BooleanSetting notifyOnSwap = new BooleanSetting("NotifyOnSwap", true);
   private final BooleanSetting openInventory = new BooleanSetting("OpenInventory", true);
   private int intVal;
   private float floatVal = Float.NaN;
   private int intVal2;
   private static final int intVal3 = 2;
   private InventoryScreen class490;

   public AutoTotemModule() {
      super("AutoTotem", "Keeps a Totem of Undying in the offhand — health-triggered or instant-refill.", Category.COMBAT);
      this.run6(new Setting[]{this.mode, this.healthThreshold, this.onlyIfDamageIncoming, this.delayTicks, this.notifyOnSwap, this.openInventory});
      this.healthThreshold.visibleWhen(this::getBoolean3);
      this.onlyIfDamageIncoming.visibleWhen(this::getBoolean2);
      this.openInventory.visibleWhen(this::getBoolean);
   }

   @Override
   public void run() {
      this.intVal = 0;
      this.floatVal = Float.NaN;
      this.intVal2 = 0;
      this.class490 = null;
   }

   @Override
   public void run2() {
      this.intVal = 0;
      this.floatVal = Float.NaN;
      if (this.intVal2 > 0 && this.class490 != null && class310.currentScreen == this.class490) {
         class310.setScreen(null);
      }

      this.intVal2 = 0;
      this.class490 = null;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (this.intVal2 > 0) {
            if (this.class490 != null && class310.currentScreen == this.class490) {
               if (--this.intVal2 == 0) {
                  class310.setScreen(null);
                  this.class490 = null;
                  return;
               }
            } else {
               this.intVal2 = 0;
               this.class490 = null;
            }
         }

         if (this.intVal > 0) {
            this.intVal--;
            this.floatVal = class310.player.getHealth();
         } else if (class310.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            this.floatVal = class310.player.getHealth();
         } else {
            boolean var1 = "HealthWatch".equals(this.mode.getMode());
            if (var1) {
               float var2 = class310.player.getHealth() + class310.player.getAbsorptionAmount();
               if (var2 > this.healthThreshold.getValue()) {
                  this.floatVal = class310.player.getHealth();
                  return;
               }

               if (this.onlyIfDamageIncoming.getValue() && !this.isEnabled()) {
                  this.floatVal = class310.player.getHealth();
                  return;
               }
            }

            int var4 = this.getInt();
            if (var4 < 0) {
               this.floatVal = class310.player.getHealth();
            } else {
               boolean var3 = !var1 && this.openInventory.getValue() && class310.currentScreen == null;
               if (var3) {
                  this.class490 = new InventoryScreen(class310.player);
                  class310.setScreen(this.class490);
               }

               if (this.check(var4)) {
                  if (this.notifyOnSwap.getValue()) {
                     NotificationUtils.run8("AutoTotem", "Totem swapped into offhand", INFO.UNKNOWN_2);
                  }

                  this.intVal = Math.max(0, this.delayTicks.getValueInt());
                  if (var3) {
                     this.intVal2 = 2;
                  }
               } else if (var3) {
                  class310.setScreen(null);
                  this.class490 = null;
               }

               this.floatVal = class310.player.getHealth();
            }
         }
      }
   }

   private boolean check(int var1) {
      if (class310.player == null || class310.interactionManager == null) {
         return false;
      } else if (class310.player.currentScreenHandler != class310.player.playerScreenHandler) {
         return false;
      } else {
         int var2 = class310.player.playerScreenHandler.syncId;
         int var3 = intOf(var1);
         if (var3 < 0) {
            return false;
         } else {
            class310.interactionManager.clickSlot(var2, var3, 40, SlotActionType.SWAP, class310.player);
            return true;
         }
      }
   }

   public int getInt() {
      PlayerInventory var1 = class310.player.getInventory();

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = var1.getStack(var2);
         if (var3.isOf(Items.TOTEM_OF_UNDYING)) {
            return var2;
         }
      }

      return -1;
   }

   private static int intOf(int var0) {
      if (var0 >= 0 && var0 <= 8) {
         return 36 + var0;
      } else {
         return var0 >= 9 && var0 <= 35 ? var0 : -1;
      }
   }

   public boolean isEnabled() {
      if (class310.player == null) {
         return false;
      } else {
         float var1 = class310.player.getHealth();
         return !Float.isNaN(this.floatVal) && var1 < this.floatVal - 0.001F
            ? true
            : class310.player.isOnFire() || class310.player.isInLava() || class310.player.isSubmergedInWater() && class310.player.getAir() <= 0;
      }
   }

   @Override
   public String getString3() {
      if (class310.player == null) {
         return null;
      } else {
         return class310.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) ? "§aOK" : "§cNO";
      }
   }

   private Boolean getBoolean() {
      return "InventoryTotem".equals(this.mode.getMode());
   }

   private Boolean getBoolean2() {
      return "HealthWatch".equals(this.mode.getMode());
   }

   private Boolean getBoolean3() {
      return "HealthWatch".equals(this.mode.getMode());
   }
}

