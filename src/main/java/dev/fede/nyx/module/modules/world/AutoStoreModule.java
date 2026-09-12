package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class AutoStoreModule extends Module {
   private final ModeSetting mode = new ModeSetting("Mode", "AllMatching", "AllMatching", "AllExceptKept", "JustDump");
   private final StringSetting filter = (StringSetting)new StringSetting("Filter", "minecraft:cobblestone,minecraft:dirt", 256).visibleWhen(this::getBoolean);
   private final BooleanSetting keepHotbar = new BooleanSetting("KeepHotbar", true);
   private final BooleanSetting keepArmor = new BooleanSetting("KeepArmor", true);
   private final NumberSetting delayMs = new NumberSetting("DelayMs", 80.0, 0.0, 500.0, 5.0);
   private final BooleanSetting closeWhenDone = new BooleanSetting("CloseWhenDone", true);
   private final BooleanSetting notifyOnComplete = new BooleanSetting("NotifyOnComplete", true);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private int intVal = -1;
   private int intVal2 = 0;
   private boolean bool = false;

   public AutoStoreModule() {
      super("AutoStore", "Auto shift-deposits filtered items into an open chest / barrel / shulker / hopper", Category.WORLD);
      this.run6(new Setting[]{this.mode, this.filter, this.keepHotbar, this.keepArmor, this.delayMs, this.closeWhenDone, this.notifyOnComplete});
   }

   @Override
   public void run() {
      this.intVal = -1;
      this.intVal2 = 0;
      this.bool = false;
      this.antiVoidModuleHelper.run();
   }

   @Override
   public void run2() {
      this.intVal = -1;
      this.intVal2 = 0;
      this.bool = false;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (!(
            class310.currentScreen instanceof HandledScreen var1
               && (var1 instanceof GenericContainerScreen || var1 instanceof ShulkerBoxScreen || var1 instanceof HopperScreen)
         )) {
            if (this.intVal != -1) {
               this.intVal = -1;
               this.intVal2 = 0;
               this.bool = false;
            }
         } else {
            ScreenHandler var5 = var1.getScreenHandler();
            if (var5 != null && var5.slots != null && !var5.slots.isEmpty()) {
               if (var5.syncId != this.intVal) {
                  this.intVal = var5.syncId;
                  this.intVal2 = 0;
                  this.bool = false;
                  this.antiVoidModuleHelper.run();
               }

               if (!this.bool) {
                  if (this.antiVoidModuleHelper.check2(this.delayMs.getValue())) {
                     Set var3 = setOf(this.filter.getValue());
                     Slot var4 = this.class1735Of(var5, var3);
                     if (var4 != null) {
                        class310.interactionManager.clickSlot(var5.syncId, var4.id, 0, SlotActionType.QUICK_MOVE, class310.player);
                        this.intVal2++;
                        this.antiVoidModuleHelper.run();
                     } else {
                        this.bool = true;
                        if (this.notifyOnComplete.getValue()) {
                           NotificationUtils.run8("AutoStore", "Deposited " + this.intVal2 + " item" + (this.intVal2 == 1 ? "" : "s"), INFO.UNKNOWN_2);
                        }

                        if (this.closeWhenDone.getValue()) {
                           class310.player.closeHandledScreen();
                           this.intVal = -1;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private Slot class1735Of(ScreenHandler var1, Set<Identifier> var2) {
      boolean var3 = this.keepHotbar.getValue();
      boolean var4 = this.keepArmor.getValue();

      for (Slot var6 : var1.slots) {
         if (var6.inventory instanceof PlayerInventory) {
            int var7 = var6.getIndex();
            if ((!var3 || var7 < 0 || var7 >= PlayerInventory.getHotbarSize()) && (!var4 || var7 >= 0 && var7 < 36)) {
               ItemStack var8 = var6.getStack();
               if (!var8.isEmpty() && this.check3(var8, var2)) {
                  return var6;
               }
            }
         }
      }

      return null;
   }

   private boolean check3(ItemStack var1, Set<Identifier> var2) {
      if (this.mode.check("JustDump")) {
         return true;
      } else {
         Identifier var3 = Registries.ITEM.getId(var1.getItem());
         boolean var4 = var3 != null && var2.contains(var3);
         return this.mode.check("AllMatching") ? var4 : !var4;
      }
   }

   private static Set<Identifier> setOf(String var0) {
      if (var0 != null && !var0.isBlank()) {
         HashSet var1 = new HashSet();

         for (String var5 : var0.split(",")) {
            String var6 = var5.trim().toLowerCase(Locale.ROOT);
            if (!var6.isEmpty()) {
               Identifier var7 = Identifier.tryParse(var6);
               if (var7 != null) {
                  var1.add(var7);
               }
            }
         }

         return var1;
      } else {
         return Collections.emptySet();
      }
   }

   @Override
   public String getString3() {
      return "§7" + this.mode.getMode();
   }

   private Boolean getBoolean() {
      return !this.mode.check("JustDump");
   }
}

