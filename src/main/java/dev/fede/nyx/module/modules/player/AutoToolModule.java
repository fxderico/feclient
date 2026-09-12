package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class AutoToolModule extends Module {
   private static final double doubleVal = 1000.0;
   private static final double doubleVal2 = 500.0;
   private static final double doubleVal3 = 250.0;
   private static final double doubleVal4 = 5.0;
   private static final double doubleVal5 = 0.01;
   private final BooleanSetting preferSilkTouch = new BooleanSetting("PreferSilkTouch", false);
   private final BooleanSetting preferFortune = new BooleanSetting("PreferFortune", true);
   private final BooleanSetting preferEfficiency = new BooleanSetting("PreferEfficiency", true);
   private final BooleanSetting onlyCreativeSkip = new BooleanSetting("OnlyCreativeSkip", true);
   private int intVal = -1;

   public AutoToolModule() {
      super("AutoTool", "Auto-swaps to the best hotbar tool before breaking a block", Category.PLAYER);
      this.run6(new Setting[]{this.preferSilkTouch, this.preferFortune, this.preferEfficiency, this.onlyCreativeSkip});
   }

   @Override
   public void run() {
      this.intVal = -1;
   }

   @Override
   public void run2() {
      this.run4();
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.options != null) {
         if (!this.onlyCreativeSkip.getValue() || !class310.player.isCreative()) {
            boolean var1 = class310.options.attackKey.isPressed() && class310.crosshairTarget instanceof BlockHitResult var2 && var2.getType() == Type.BLOCK;
            if (!var1) {
               this.run4();
            } else {
               BlockHitResult var8 = (BlockHitResult)class310.crosshairTarget;
               BlockPos var9 = var8.getBlockPos();
               BlockState var4 = class310.world.getBlockState(var9);
               if (var4.isAir()) {
                  this.run4();
               } else {
                  int var5 = this.intOf(var4);
                  if (var5 >= 0) {
                     PlayerInventory var6 = class310.player.getInventory();
                     int var7 = var6.getSelectedSlot();
                     if (var5 != var7) {
                        if (this.intVal < 0) {
                           this.intVal = var7;
                        }

                        var6.setSelectedSlot(var5);
                     }
                  }
               }
            }
         }
      }
   }

   private int intOf(BlockState var1) {
      PlayerInventory var2 = class310.player.getInventory();
      int var3 = var2.getSelectedSlot();
      int var4 = -1;
      double var5 = Double.NEGATIVE_INFINITY;

      for (int var7 = 0; var7 < PlayerInventory.getHotbarSize(); var7++) {
         ItemStack var8 = var2.getStack(var7);
         double var9 = this.doubleOf(var8, var1);
         if (var7 == var3) {
            var9 += 0.01;
         }

         if (var9 > var5) {
            var5 = var9;
            var4 = var7;
         }
      }

      return var4;
   }

   private double doubleOf(ItemStack var1, BlockState var2) {
      if (var1.isEmpty()) {
         return 1.0;
      } else {
         double var3 = var1.getMiningSpeedMultiplier(var2);
         if (var1.isSuitableFor(var2)) {
            var3 += 1000.0;
         }

         if (this.preferSilkTouch.getValue() && intOf2(var1, Enchantments.SILK_TOUCH) > 0) {
            var3 += 500.0;
         }

         if (this.preferFortune.getValue()) {
            int var5 = intOf2(var1, Enchantments.FORTUNE);
            if (var5 > 0) {
               var3 += 250.0 * var5;
            }
         }

         if (this.preferEfficiency.getValue()) {
            int var6 = intOf2(var1, Enchantments.EFFICIENCY);
            if (var6 > 0) {
               var3 += 5.0 * var6;
            }
         }

         return var3;
      }
   }

   private static int intOf2(ItemStack var0, RegistryKey<Enchantment> var1) {
      if (var0.isEmpty()) {
         return 0;
      } else {
         ItemEnchantmentsComponent var2 = var0.getEnchantments();
         if (var2 != null && !var2.isEmpty()) {
            for (Entry var4 : var2.getEnchantmentEntries()) {
               RegistryEntry var5 = (RegistryEntry)var4.getKey();
               if (var5.matchesKey(var1)) {
                  return var4.getIntValue();
               }
            }

            return 0;
         } else {
            return 0;
         }
      }
   }

   private void run4() {
      if (this.intVal >= 0 && class310.player != null) {
         if (this.intVal < PlayerInventory.getHotbarSize() && this.intVal != class310.player.getInventory().getSelectedSlot()) {
            class310.player.getInventory().setSelectedSlot(this.intVal);
         }

         this.intVal = -1;
      } else {
         this.intVal = -1;
      }
   }

   @Override
   public String getString3() {
      return this.intVal >= 0 ? "§7swapped" : null;
   }
}

