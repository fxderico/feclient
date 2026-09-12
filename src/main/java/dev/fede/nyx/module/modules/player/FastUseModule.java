package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class FastUseModule extends Module {
   private final NumberSetting delay = new NumberSetting("Delay", 0.0, 0.0, 4.0, 1.0);
   private final StringSetting itemFilter = new StringSetting("ItemFilter", "", 256);
   private volatile String string = null;
   private volatile Set<Identifier> set = Collections.emptySet();

   public FastUseModule() {
      super("FastUse", "Removes the delay between item uses (consumables, bows, potions)", Category.PLAYER);
      this.run6(new Setting[]{this.delay, this.itemFilter});
   }

   @Override
   public void run2() {
      if (class310.player != null && class310.world != null) {
         ItemStack var1 = class310.player.getMainHandStack();
         if (var1 != null && !var1.isEmpty()) {
            Set var2 = this.setOf(this.itemFilter.getValue());
            if (var2.isEmpty()) {
               if (var1.getItem() instanceof BlockItem) {
                  return;
               }
            } else {
               Identifier var3 = Registries.ITEM.getId(var1.getItem());
               if (var3 == null || !var2.contains(var3)) {
                  return;
               }
            }

            
         }
      }
   }

   private Set<Identifier> setOf(String var1) {
      String var2 = var1 == null ? "" : var1;
      String var3 = this.string;
      if (var3 != null && var3.equals(var2)) {
         return this.set;
      } else {
         Set var4 = setOf2(var2);
         this.set = var4;
         this.string = var2;
         return var4;
      }
   }

   private static Set<Identifier> setOf2(String var0) {
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
}

