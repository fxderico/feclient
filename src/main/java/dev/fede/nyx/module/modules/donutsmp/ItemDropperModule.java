package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemDropperModule extends Module {
   private final StringSetting itemNames = new StringSetting("ItemNames", "minecraft:cobblestone,minecraft:dirt", 256);
   private final NumberSetting intervalTicks = new NumberSetting("IntervalTicks", 10.0, 1.0, 100.0, 1.0);
   private int intVal;

   public ItemDropperModule() {
      super("ItemDropper", "Auto-drops user-configured items as they enter your inventory", Category.DONUTSMP);
      this.run6(new Setting[]{this.itemNames, this.intervalTicks});
   }

   @Override
   public void run() {
      this.intVal = 0;
   }

   @Override
   public void run2() {
      this.intVal = 0;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.getNetworkHandler() != null) {
         if (class310.currentScreen == null) {
            if (this.intVal > 0) {
               this.intVal--;
            } else {
               Set var1;
               try {
                  var1 = setOf(this.itemNames.getValue());
               } catch (Throwable var7) {
                  return;
               }

               if (!var1.isEmpty()) {
                  try {
                     PlayerInventory var2 = class310.player.getInventory();
                     int var3 = PlayerInventory.getHotbarSize();

                     for (int var4 = 0; var4 < var3; var4++) {
                        ItemStack var5 = var2.getStack(var4);
                        if (!var5.isEmpty()) {
                           Identifier var6 = Registries.ITEM.getId(var5.getItem());
                           if (var6 != null && var1.contains(var6)) {
                              this.run4(var2, var4);
                              this.intVal = Math.max(1, this.intervalTicks.getValueInt());
                              return;
                           }
                        }
                     }
                  } catch (Throwable var8) {
                  }
               }
            }
         }
      }
   }

   private void run4(PlayerInventory var1, int var2) {
      int var3 = var1.getSelectedSlot();

      try {
         if (var3 != var2) {
            var1.setSelectedSlot(var2);
            class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var2));
         }

         class310.player.dropSelectedItem(true);
      } finally {
         if (var3 != var2 && var3 >= 0 && var3 < PlayerInventory.getHotbarSize()) {
            var1.setSelectedSlot(var3);

            try {
               class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var3));
            } catch (Throwable var10) {
            }
         }
      }
   }

   private static Set<Identifier> setOf(String var0) {
      HashSet var1 = new HashSet();
      if (var0 != null && !var0.isBlank()) {
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
         return var1;
      }
   }
}

