package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class ChestStealerModule extends Module {
   private final ModeSetting mode = new ModeSetting("Mode", "Steal", "Steal", "Store", "Both");
   private final NumberSetting delayMs = new NumberSetting("DelayMs", 60.0, 0.0, 500.0, 5.0);
   private final BooleanSetting closeAfterDone = new BooleanSetting("CloseAfterDone", false);
   private final StringSetting whitelistItems = new StringSetting("WhitelistItems", "", 256);
   private final BooleanSetting ignoreShulkers = new BooleanSetting("IgnoreShulkers", false);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private int intVal = -1;
   private boolean bool;

   public ChestStealerModule() {
      super("ChestStealer", "Auto shift-clicks items between a container and your inventory", Category.COMBAT);
      this.run6(new Setting[]{this.mode, this.delayMs, this.closeAfterDone, this.whitelistItems, this.ignoreShulkers});
   }

   @Override
   public void run() {
      this.bool = false;
      this.intVal = -1;
      this.antiVoidModuleHelper.run();
   }

   @Override
   public void run2() {
      this.bool = false;
      this.intVal = -1;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (class310.currentScreen instanceof HandledScreen var1) {
            if (!(var1 instanceof InventoryScreen)) {
               if (!this.ignoreShulkers.getValue() || !(var1 instanceof ShulkerBoxScreen)) {
                  ScreenHandler var5 = var1.getScreenHandler();
                  if (var5 != null && var5.slots != null && !var5.slots.isEmpty()) {
                     if (var5.syncId != this.intVal) {
                        this.intVal = var5.syncId;
                        this.bool = false;
                        this.antiVoidModuleHelper.run();
                     }

                     if (this.antiVoidModuleHelper.check2(this.delayMs.getValue())) {
                        Set var3 = setOf(this.whitelistItems.getValue());
                        Integer var4 = this.integerOf(var5, var3);
                        if (var4 != null) {
                           class310.interactionManager.clickSlot(var5.syncId, var4, 0, SlotActionType.QUICK_MOVE, class310.player);
                           this.antiVoidModuleHelper.run();
                        } else {
                           if (this.closeAfterDone.getValue()) {
                              class310.player.closeHandledScreen();
                              this.intVal = -1;
                              this.bool = false;
                           }
                        }
                     }
                  }
               }
            }
         } else {
            this.bool = false;
            this.intVal = -1;
         }
      }
   }

   private Integer integerOf(ScreenHandler var1, Set<Identifier> var2) {
      boolean var3;
      boolean var4;
      if (this.mode.check("Steal")) {
         var3 = true;
         var4 = false;
      } else if (this.mode.check("Store")) {
         var3 = false;
         var4 = true;
      } else {
         var3 = !this.bool;
         var4 = this.bool;
      }

      if (var3) {
         for (Slot var9 : var1.slots) {
            if (!check(var9)) {
               ItemStack var10 = var9.getStack();
               if (!var10.isEmpty() && check3(var10, var2)) {
                  return var9.id;
               }
            }
         }

         if (this.mode.check("Both")) {
            this.bool = true;
            return null;
         } else {
            return null;
         }
      } else if (var4) {
         for (Slot var6 : var1.slots) {
            if (check(var6) && !check2(var6)) {
               ItemStack var7 = var6.getStack();
               if (!var7.isEmpty() && check3(var7, var2)) {
                  return var6.id;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static boolean check(Slot var0) {
      return var0.inventory instanceof PlayerInventory;
   }

   private static boolean check2(Slot var0) {
      int var1 = var0.getIndex();
      return var1 >= 0 && var1 < PlayerInventory.getHotbarSize();
   }

   private static boolean check3(ItemStack var0, Set<Identifier> var1) {
      if (var1.isEmpty()) {
         return true;
      } else {
         Identifier var2 = Registries.ITEM.getId(var0.getItem());
         return var2 != null && var1.contains(var2);
      }
   }

   private static Set<Identifier> setOf(String var0) {
      if (var0 != null && !var0.isBlank()) {
         HashSet var1 = new HashSet();
         String[] var2 = var0.split(",");
         ArrayList var3 = new ArrayList(var2.length);

         for (String var7 : var2) {
            String var8 = var7.trim().toLowerCase(Locale.ROOT);
            if (!var8.isEmpty()) {
               var3.add(var8);
            }
         }

         for (String var10 : (java.util.List<String>)var3) {
            Identifier var11 = Identifier.tryParse(var10);
            if (var11 != null) {
               var1.add(var11);
            }
         }

         return var1;
      } else {
         return Collections.emptySet();
      }
   }

   @Override
   public String getString3() {
      if (this.mode.check("Both") && this.intVal != -1) {
         return this.bool ? "§7Store" : "§7Steal";
      } else {
         return null;
      }
   }
}

