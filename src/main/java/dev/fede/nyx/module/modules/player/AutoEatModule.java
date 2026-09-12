package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.mixin.MinecraftClientInvoker;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class AutoEatModule extends Module {
   private static final long longVal = 3000L;
   private final NumberSetting hungerThreshold = new NumberSetting("HungerThreshold", 15.0, 1.0, 20.0, 1.0);
   private final NumberSetting healthThreshold = new NumberSetting("HealthThreshold", 15.0, 1.0, 20.0, 1.0);
   private final BooleanSetting preferGoldenApple = new BooleanSetting("PreferGoldenApple", true);
   private final BooleanSetting swapBackSlot = new BooleanSetting("SwapBackSlot", true);
   private final BooleanSetting pauseInCombat = new BooleanSetting("PauseInCombat", false);
   private boolean bool;
   private int intVal = -1;
   private int intVal2;
   private long longVal2;

   public AutoEatModule() {
      super("AutoEat", "Automatically eats food when hunger or health is low", Category.PLAYER);
      this.run6(new Setting[]{this.hungerThreshold, this.healthThreshold, this.preferGoldenApple, this.swapBackSlot, this.pauseInCombat});
   }

   @Override
   public void run() {
      this.bool = false;
      this.intVal = -1;
      this.intVal2 = 0;
      this.longVal2 = 0L;
   }

   @Override
   public void run2() {
      this.run5(true);
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.options != null) {
         this.run6();
         if (this.bool) {
            this.run4();
         } else if (this.isEnabled()) {
            int var1 = this.getInt2();
            if (var1 >= 0) {
               this.run7(var1);
            }
         }
      } else {
         this.bool = false;
         this.intVal = -1;
      }
   }

   public boolean isEnabled() {
      if (class310.player.isSpectator() || class310.player.isCreative()) {
         return false;
      } else if (this.pauseInCombat.getValue() && this.isEnabled2()) {
         return false;
      } else {
         int var1 = class310.player.getHungerManager().getFoodLevel();
         float var2 = class310.player.getHealth();
         boolean var3 = var1 <= this.hungerThreshold.getValueInt();
         boolean var4 = var2 <= this.healthThreshold.getValueInt();
         return var3 || var4;
      }
   }

   public void run7(int var1) {
      PlayerInventory var2 = class310.player.getInventory();
      this.intVal = var2.getSelectedSlot();
      if (this.intVal != var1) {
         var2.setSelectedSlot(var1);
      }

      class310.options.useKey.setPressed(true);
      ((MinecraftClientInvoker)class310).nyx$doItemUse();
      this.bool = true;
   }

   private void run4() {
      boolean var1 = check2(class310.player.getMainHandStack());
      boolean var2 = class310.player.isUsingItem();
      boolean var3 = this.isEnabled();
      if (var1 && var3) {
         if (!var2) {
            ((MinecraftClientInvoker)class310).nyx$doItemUse();
         }

         class310.options.useKey.setPressed(true);
      } else {
         this.run5(true);
      }
   }

   public void run5_nf(boolean var1) {
      if (class310.options != null && class310.options.useKey != null) {
         class310.options.useKey.setPressed(false);
      }

      if (var1
         && this.swapBackSlot.getValue()
         && class310.player != null
         && this.intVal >= 0
         && this.intVal < PlayerInventory.getHotbarSize()
         && this.intVal != class310.player.getInventory().getSelectedSlot()) {
         class310.player.getInventory().setSelectedSlot(this.intVal);
      }

      this.intVal = -1;
      this.bool = false;
   }

   private int getInt2() {
      PlayerInventory var1 = class310.player.getInventory();
      int var2 = -1;
      double var3 = Double.NEGATIVE_INFINITY;

      for (int var5 = 0; var5 < PlayerInventory.getHotbarSize(); var5++) {
         ItemStack var6 = var1.getStack(var5);
         if (!var6.isEmpty() && check2(var6)) {
            double var7 = this.doubleOf(var6);
            if (var7 > var3) {
               var3 = var7;
               var2 = var5;
            }
         }
      }

      return var2;
   }

   private static boolean check2(ItemStack var0) {
      return var0.isEmpty() ? false : var0.contains(DataComponentTypes.FOOD) || var0.contains(DataComponentTypes.CONSUMABLE);
   }

   private double doubleOf(ItemStack var1) {
      if (this.preferGoldenApple.getValue()) {
         if (var1.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
            return 10000.0;
         }

         if (var1.isOf(Items.GOLDEN_APPLE)) {
            return 5000.0;
         }
      }

      FoodComponent var2 = (FoodComponent)var1.get(DataComponentTypes.FOOD);
      double var3 = var2 == null ? 0.0 : var2.nutrition();
      double var5 = var2 == null ? 0.0 : var2.saturation();
      return var3 * 10.0 + var5;
   }

   private void run6() {
      ClientPlayerEntity var1 = class310.player;
      int var2 = var1.hurtTime;
      if (var2 > this.intVal2) {
         this.longVal2 = System.currentTimeMillis();
      }

      this.intVal2 = var2;
   }

   private boolean isEnabled2() {
      return this.longVal2 <= 0L ? false : System.currentTimeMillis() - this.longVal2 <= 3000L;
   }

   @Override
   public String getString3() {
      return this.bool ? "§aeating" : null;
   }
}

