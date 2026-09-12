package dev.fede.nyx.module.modules.addons;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.GlintTextureReplacer;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class GlintCustomiserModule extends Module {
   private final ColorSetting color = new ColorSetting("Color", -9663233);
   private final NumberSetting intensity = new NumberSetting("Intensity", 3.0, 0.1, 10.0, 0.1);
   private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.0, 5.0, 0.1);
   private final BooleanSetting pulseGlow = new BooleanSetting("PulseGlow", false);
   public static GlintCustomiserModule glintCustomiserModule;
   private int intVal = 0;
   private double doubleVal = -1.0;

   public GlintCustomiserModule() {
      super("GlintCustomiser", "Recolours enchanted-item glint to a custom hue — swirl preserved, tint applied.", Category.ADDONS);
      glintCustomiserModule = this;
      this.run6(new Setting[]{this.color, this.intensity, this.speed, this.pulseGlow});
   }

   @Override
   public void run() {
      this.intVal = this.color.getValue();
      this.doubleVal = this.intensity.getValue();

      try {
         GlintTextureReplacer.enable(this.intVal, (float)this.doubleVal);
      } catch (Throwable var2) {
         System.err.println("[Glint] enable failed: null");
      }
   }

   @Override
   public void run2() {
      try {
         GlintTextureReplacer.disable();
      } catch (Throwable var2) {
         System.err.println("[Glint] disable failed: null");
      }
   }

   @Override
   public void run3() {
      int var1 = this.color.getValue();
      double var2 = this.intensity.getValue();
      if (var1 != this.intVal || var2 != this.doubleVal) {
         this.intVal = var1;
         this.doubleVal = var2;

         try {
            GlintTextureReplacer.applyTint(var1, (float)var2);
         } catch (Throwable var5) {
            System.err.println("[Glint] applyTint failed: null");
         }
      }
   }

   public static boolean check(Entity var0) {
      return false;
   }

   public static int intOf(Entity var0) {
      return -1;
   }

   private static boolean check2(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         try {
            ItemEnchantmentsComponent var1 = (ItemEnchantmentsComponent)var0.get(DataComponentTypes.ENCHANTMENTS);
            if (var1 != null && !var1.isEmpty()) {
               return true;
            }
         } catch (Throwable var3) {
         }

         try {
            ItemEnchantmentsComponent var4 = (ItemEnchantmentsComponent)var0.get(DataComponentTypes.STORED_ENCHANTMENTS);
            if (var4 != null && !var4.isEmpty()) {
               return true;
            }
         } catch (Throwable var2) {
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean check3(LivingEntity var0) {
      if (check2(var0.getMainHandStack())) {
         return true;
      } else if (check2(var0.getOffHandStack())) {
         return true;
      } else if (check2(var0.getEquippedStack(EquipmentSlot.HEAD))) {
         return true;
      } else if (check2(var0.getEquippedStack(EquipmentSlot.CHEST))) {
         return true;
      } else {
         return check2(var0.getEquippedStack(EquipmentSlot.LEGS)) ? true : check2(var0.getEquippedStack(EquipmentSlot.FEET));
      }
   }
}

