package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class AutoDropModule extends Module {
   private final StringSetting filter = new StringSetting("Filter", "minecraft:rotten_flesh,minecraft:cobblestone", 256);
   private final BooleanSetting dropStacks = new BooleanSetting("DropStacks", true);
   private final NumberSetting delayTicks = new NumberSetting("DelayTicks", 4.0, 0.0, 40.0, 1.0);
   private int intVal;
   private String string = "";
   private Set<Identifier> set = Collections.emptySet();

   public AutoDropModule() {
      super("AutoDrop", "Automatically drops items matching a whitelist", Category.PLAYER);
      this.run6(new Setting[]{this.filter, this.dropStacks, this.delayTicks});
   }

   @Override
   public void run() {
      this.intVal = 0;
   }

   @Override
   public void run2() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (class310.currentScreen == null || !(class310.currentScreen instanceof HandledScreen var1 && !(var1 instanceof InventoryScreen))) {
            if (this.intVal > 0) {
               this.intVal--;
            } else {
               Set var6 = this.getSet();
               if (!var6.isEmpty()) {
                  int var7 = this.intOf(var6);
                  if (var7 >= 0) {
                     int var3 = class310.player.playerScreenHandler.syncId;
                     int var4 = intOf2(var7);
                     if (var4 >= 0) {
                        if (class310.player.playerScreenHandler.getCursorStack().isEmpty()) {
                           int var5 = this.dropStacks.getValue() ? 1 : 0;
                           class310.interactionManager.clickSlot(var3, var4, var5, SlotActionType.THROW, class310.player);
                           this.intVal = Math.max(1, this.delayTicks.getValueInt());
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private int intOf(Set<Identifier> var1) {
      PlayerInventory var2 = class310.player.getInventory();

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = var2.getStack(var3);
         if (!var4.isEmpty()) {
            Identifier var5 = Registries.ITEM.getId(var4.getItem());
            if (var5 != null && var1.contains(var5)) {
               return var3;
            }
         }
      }

      return -1;
   }

   private static int intOf2(int var0) {
      if (var0 < 0) {
         return -1;
      } else if (var0 < PlayerInventory.getHotbarSize()) {
         return 36 + var0;
      } else {
         return var0 < 36 ? var0 : -1;
      }
   }

   private Set<Identifier> getSet() {
      String var1 = this.filter.getValue();
      if (var1 == null) {
         var1 = "";
      }

      if (var1.equals(this.string)) {
         return this.set;
      } else {
         this.string = var1;
         HashSet var2 = new HashSet();

         for (String var6 : var1.split(",")) {
            String var7 = var6.trim().toLowerCase(Locale.ROOT);
            if (!var7.isEmpty()) {
               Identifier var8 = Identifier.tryParse(var7);
               if (var8 != null) {
                  var2.add(var8);
               }
            }
         }

         this.set = var2;
         return this.set;
      }
   }
}

