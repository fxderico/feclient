package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Module {
   private static final EquipmentSlot[] class1304Array = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final double doubleVal = 1.0E-4;
   private final BooleanSetting preferProtection = new BooleanSetting("PreferProtection", true);
   private final BooleanSetting preferEnchanted = new BooleanSetting("PreferEnchanted", true);
   private final NumberSetting minDurability = new NumberSetting("MinDurability", 0.1, 0.0, 1.0, 0.01);
   private final NumberSetting delayTicks = new NumberSetting("DelayTicks", 3.0, 0.0, 40.0, 1.0);
   private final BooleanSetting pauseWhileMoving = new BooleanSetting("PauseWhileMoving", false);
   private AutoArmor.Phase autoArmorPhase = AutoArmor.Phase.IDLE;
   private int intVal;
   private int intVal2 = -1;
   private int intVal3 = -1;

   public AutoArmor() {
      super("AutoArmor", "Equips the best armor from your inventory", Category.COMBAT);
      this.run6(new Setting[]{this.preferProtection, this.preferEnchanted, this.minDurability, this.delayTicks, this.pauseWhileMoving});
   }

   @Override
   public void run() {
      this.autoArmorPhase = AutoArmor.Phase.IDLE;
      this.intVal = 0;
      this.intVal2 = -1;
      this.intVal3 = -1;
   }

   @Override
   public void run2() {
      this.autoArmorPhase = AutoArmor.Phase.IDLE;
      this.intVal2 = -1;
      this.intVal3 = -1;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (class310.currentScreen == null || class310.currentScreen instanceof InventoryScreen) {
            if (!this.pauseWhileMoving.getValue() || !(class310.player.getVelocity().horizontalLengthSquared() > 1.0E-4)) {
               if (this.intVal > 0) {
                  this.intVal--;
               } else {
                  PlayerScreenHandler var1 = class310.player.playerScreenHandler;
                  if (var1 != null) {
                     int var2 = var1.syncId;
                     switch (this.autoArmorPhase) {
                        case IDLE:
                           for (EquipmentSlot var6 : class1304Array) {
                              int var7 = this.intOf(var6);
                              if (var7 >= 0) {
                                 this.intVal2 = intOf3(var7);
                                 this.intVal3 = intOf4(var6);
                                 if (this.intVal2 >= 0 && this.intVal3 >= 0) {
                                    class310.interactionManager.clickSlot(var2, this.intVal2, 0, SlotActionType.PICKUP, class310.player);
                                    this.autoArmorPhase = AutoArmor.Phase.TAKE;
                                    this.intVal = this.delayTicks.getValueInt();
                                    return;
                                 }
                              }
                           }
                           break;
                        case TAKE:
                           class310.interactionManager.clickSlot(var2, this.intVal3, 0, SlotActionType.PICKUP, class310.player);
                           this.autoArmorPhase = AutoArmor.Phase.PLACE;
                           this.intVal = this.delayTicks.getValueInt();
                           break;
                        case PLACE:
                           class310.interactionManager.clickSlot(var2, this.intVal2, 0, SlotActionType.PICKUP, class310.player);
                           this.autoArmorPhase = AutoArmor.Phase.RETURN;
                           this.intVal = this.delayTicks.getValueInt();
                           break;
                        case RETURN:
                           this.autoArmorPhase = AutoArmor.Phase.IDLE;
                           this.intVal2 = -1;
                           this.intVal3 = -1;
                     }
                  }
               }
            }
         }
      }
   }

   private int intOf(EquipmentSlot var1) {
      ItemStack var2 = class310.player.getEquippedStack(var1);
      double var3 = this.doubleOf(var2, var1);
      int var5 = -1;
      double var6 = var3;
      PlayerInventory var8 = class310.player.getInventory();
      int var9 = var8.getMainStacks().size();

      for (int var10 = 0; var10 < var9; var10++) {
         ItemStack var11 = var8.getStack(var10);
         if (!var11.isEmpty() && check(var11, var1) && this.check2(var11)) {
            double var12 = this.doubleOf(var11, var1);
            if (var12 > var6 + 1.0E-4) {
               var6 = var12;
               var5 = var10;
            } else if (this.preferEnchanted.getValue() && Math.abs(var12 - var6) <= 1.0E-4 && check3(var11) && !check3(var2)) {
               var6 = var12;
               var5 = var10;
            }
         }
      }

      return var5;
   }

   private static boolean check(ItemStack var0, EquipmentSlot var1) {
      EquippableComponent var2 = (EquippableComponent)var0.get(DataComponentTypes.EQUIPPABLE);
      return var2 != null && var2.slot() == var1;
   }

   private boolean check2(ItemStack var1) {
      int var2 = var1.getMaxDamage();
      if (var2 <= 0) {
         return true;
      } else {
         double var3 = 1.0 - (double)var1.getDamage() / var2;
         return var3 >= this.minDurability.getValue();
      }
   }

   private double doubleOf(ItemStack var1, EquipmentSlot var2) {
      if (var1.isEmpty()) {
         return -1.0;
      } else {
         double[] var3 = new double[]{0.0};
         double[] var4 = new double[]{0.0};
         AttributeModifiersComponent var5 = (AttributeModifiersComponent)var1.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
         if (var5 != null) {
            var5.applyModifiers(var2, (attr, mod) -> {});
         }

         double var6 = var3[0] + 0.5 * var4[0];
         if (this.preferProtection.getValue()) {
            var6 += 0.75 * intOf2(var1);
         }

         return var6;
      }
   }

   private static boolean check3(ItemStack var0) {
      if (var0.isEmpty()) {
         return false;
      } else {
         ItemEnchantmentsComponent var1 = var0.getEnchantments();
         return var1 != null && !var1.isEmpty();
      }
   }

   private static int intOf2(ItemStack var0) {
      ItemEnchantmentsComponent var1 = var0.getEnchantments();
      if (var1 != null && !var1.isEmpty()) {
         int var2 = 0;

         for (Entry var4 : var1.getEnchantmentEntries()) {
            var2 += var4.getIntValue();
         }

         return var2;
      } else {
         return 0;
      }
   }

   private static int intOf3(int var0) {
      if (var0 < 0) {
         return -1;
      } else {
         int var1 = 0;
         if (var0 < var1) {
            return 36 + var0;
         } else {
            return var0 < 36 ? var0 : -1;
         }
      }
   }

   private static int intOf4(EquipmentSlot var0) {
      return switch (var0) {
         case HEAD -> 5;
         case CHEST -> 6;
         case LEGS -> 7;
         case FEET -> 8;
         default -> -1;
      };
   }

   public static int getInt_s() {
      return PlayerInventory.getHotbarSize();
   }

   @Override
   public String getString3() {
      return this.autoArmorPhase == AutoArmor.Phase.IDLE ? null : "§7" + this.autoArmorPhase.name().toLowerCase();
   }

   private static void run4(double[] var0, double[] var1, RegistryEntry var2, EntityAttributeModifier var3) {
      if (var3.operation() == Operation.ADD_VALUE) {
         if (var2 == EntityAttributes.ARMOR) {
            var0[0] += var3.value();
         } else if (var2 == EntityAttributes.ARMOR_TOUGHNESS) {
            var1[0] += var3.value();
         }
      }
   }

   private static enum Phase {
      IDLE,
      TAKE,
      PLACE,
      RETURN;

      private static final AutoArmor.Phase[] autoArmorPhaseArray = getAutoArmorPhaseArray();

      private static AutoArmor.Phase[] getAutoArmorPhaseArray() {
         return new AutoArmor.Phase[]{IDLE, TAKE, PLACE, RETURN};
      }
   }
}

