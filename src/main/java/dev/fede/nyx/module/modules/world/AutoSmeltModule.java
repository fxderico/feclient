package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class AutoSmeltModule extends Module {
   private static final int intVal = 0;
   private static final int intVal2 = 1;
   private static final int intVal3 = 2;
   private final BooleanSetting rawOres = new BooleanSetting("RawOres", true);
   private final BooleanSetting food = new BooleanSetting("Food", true);
   private final BooleanSetting logs = new BooleanSetting("Logs", false);
   private final StringSetting custom = new StringSetting("Custom", "", 256);
   private final ModeSetting fuelPriority = new ModeSetting("FuelPriority", "CoalFirst", "CoalFirst", "CharcoalFirst", "BlazeRodFirst", "AnyValid");
   private final BooleanSetting autoCollect = new BooleanSetting("AutoCollect", true);
   private final NumberSetting minFuelLeft = new NumberSetting("MinFuelLeftInInv", 8.0, 0.0, 64.0, 1.0);
   private final NumberSetting delayMs = new NumberSetting("DelayMs", 120.0, 0.0, 1000.0, 10.0);
   private final BooleanSetting closeWhenDone = new BooleanSetting("CloseWhenDone", false);
   private final BooleanSetting silent = new BooleanSetting("Silent", true);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private int intVal4 = -1;
   private String string = "";
   private Set<Identifier> set = Collections.emptySet();

   public AutoSmeltModule() {
      super("AutoSmelt", "Auto-fills furnaces with fuel and smeltables while the GUI is open", Category.WORLD);
      this.run6(
         new Setting[]{
            this.rawOres,
            this.food,
            this.logs,
            this.custom,
            this.fuelPriority,
            this.autoCollect,
            this.minFuelLeft,
            this.delayMs,
            this.closeWhenDone,
            this.silent
         }
      );
   }

   @Override
   public void run() {
      this.intVal4 = -1;
      this.antiVoidModuleHelper.run();
   }

   @Override
   public void run2() {
      this.intVal4 = -1;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (class310.currentScreen instanceof HandledScreen var1) {
            if (var1 instanceof FurnaceScreen || var1 instanceof SmokerScreen || var1 instanceof BlastFurnaceScreen) {
               ScreenHandler var7 = var1.getScreenHandler();
               if (var7 instanceof AbstractFurnaceScreenHandler) {
                  if (var7.slots != null && var7.slots.size() > 2) {
                     if (var7.syncId != this.intVal4) {
                        this.intVal4 = var7.syncId;
                        this.antiVoidModuleHelper.run();
                     }

                     if (!this.silent.getValue() || !this.isEnabled()) {
                        if (this.antiVoidModuleHelper.check2(this.delayMs.getValue())) {
                           ItemStack var3 = ((Slot)var7.slots.get(0)).getStack();
                           ItemStack var4 = ((Slot)var7.slots.get(1)).getStack();
                           ItemStack var5 = ((Slot)var7.slots.get(2)).getStack();
                           if (this.autoCollect.getValue() && !var5.isEmpty()) {
                              class310.interactionManager.clickSlot(var7.syncId, 2, 0, SlotActionType.QUICK_MOVE, class310.player);
                              this.antiVoidModuleHelper.run();
                           } else {
                              if (var3.isEmpty()) {
                                 Integer var6 = this.integerOf(var7, var1);
                                 if (var6 != null) {
                                    class310.interactionManager.clickSlot(var7.syncId, var6, 0, SlotActionType.QUICK_MOVE, class310.player);
                                    this.antiVoidModuleHelper.run();
                                    return;
                                 }
                              }

                              if (var4.isEmpty()) {
                                 Integer var8 = this.integerOf2(var7);
                                 if (var8 != null) {
                                    class310.interactionManager.clickSlot(var7.syncId, var8, 0, SlotActionType.QUICK_MOVE, class310.player);
                                    this.antiVoidModuleHelper.run();
                                    return;
                                 }
                              }

                              if (this.closeWhenDone.getValue() && var3.isEmpty() && var5.isEmpty() && this.integerOf(var7, var1) == null) {
                                 class310.player.closeHandledScreen();
                                 this.intVal4 = -1;
                              }
                           }
                        }
                     }
                  }
               }
            }
         } else {
            this.intVal4 = -1;
         }
      }
   }

   private Integer integerOf(ScreenHandler var1, HandledScreen<?> var2) {
      Set var3 = this.getSet();
      boolean var4 = var2 instanceof SmokerScreen;
      boolean var5 = var2 instanceof BlastFurnaceScreen;
      boolean var6 = var2 instanceof FurnaceScreen;

      for (Slot var8 : var1.slots) {
         if (var8.inventory instanceof PlayerInventory) {
            ItemStack var9 = var8.getStack();
            if (!var9.isEmpty() && this.check(var9, var6, var4, var5, var3)) {
               return var8.id;
            }
         }
      }

      return null;
   }

   private Integer integerOf2(ScreenHandler var1) {
      int var2 = 0;

      for (Slot var4 : var1.slots) {
         if (var4.inventory instanceof PlayerInventory) {
            ItemStack var5 = var4.getStack();
            if (!var5.isEmpty() && this.check5(var5)) {
               var2 += var5.getCount();
            }
         }
      }

      int var8 = this.minFuelLeft.getValueInt();
      if (var2 <= var8) {
         return null;
      } else {
         Item var9 = this.getclass1792();
         if (var9 != null) {
            for (Slot var6 : var1.slots) {
               if (var6.inventory instanceof PlayerInventory) {
                  ItemStack var7 = var6.getStack();
                  if (!var7.isEmpty() && var7.isOf(var9) && var2 - var7.getCount() >= var8) {
                     return var6.id;
                  }
               }
            }
         }

         for (Slot var12 : var1.slots) {
            if (var12.inventory instanceof PlayerInventory) {
               ItemStack var13 = var12.getStack();
               if (!var13.isEmpty() && this.check5(var13) && var2 - var13.getCount() >= var8) {
                  return var12.id;
               }
            }
         }

         return null;
      }
   }

   private boolean check(ItemStack var1, boolean var2, boolean var3, boolean var4, Set<Identifier> var5) {
      if (!var5.isEmpty()) {
         Identifier var6 = Registries.ITEM.getId(var1.getItem());
         if (var6 != null && var5.contains(var6)) {
            return true;
         }
      }

      if (this.rawOres.getValue() && (var2 || var4) && check2(var1)) {
         return true;
      } else {
         return this.food.getValue() && (var2 || var3) && check3(var1) ? true : this.logs.getValue() && var2 && check4(var1);
      }
   }

   private static boolean check2(ItemStack var0) {
      return var0.isOf(Items.RAW_IRON)
         || var0.isOf(Items.RAW_GOLD)
         || var0.isOf(Items.RAW_COPPER)
         || var0.isOf(Items.ANCIENT_DEBRIS)
         || var0.isIn(ItemTags.IRON_ORES)
         || var0.isIn(ItemTags.GOLD_ORES)
         || var0.isIn(ItemTags.COPPER_ORES);
   }

   private static boolean check3(ItemStack var0) {
      return var0.isOf(Items.BEEF)
         || var0.isOf(Items.PORKCHOP)
         || var0.isOf(Items.CHICKEN)
         || var0.isOf(Items.MUTTON)
         || var0.isOf(Items.RABBIT)
         || var0.isOf(Items.COD)
         || var0.isOf(Items.SALMON)
         || var0.isOf(Items.POTATO)
         || var0.isOf(Items.KELP);
   }

   private static boolean check4(ItemStack var0) {
      return var0.isIn(ItemTags.LOGS);
   }

   private boolean check5(ItemStack var1) {
      return class310.world != null && class310.world.getFuelRegistry().isFuel(var1);
   }

   private Item getclass1792() {
      if (this.fuelPriority.check("CoalFirst")) {
         return Items.COAL;
      } else if (this.fuelPriority.check("CharcoalFirst")) {
         return Items.CHARCOAL;
      } else {
         return this.fuelPriority.check("BlazeRodFirst") ? Items.BLAZE_ROD : null;
      }
   }

   private Set<Identifier> getSet() {
      String var1 = this.custom.getValue();
      if (var1 == null) {
         var1 = "";
      }

      if (!var1.equals(this.string)) {
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
      }

      return this.set;
   }

   public boolean isEnabled() {
      if (class310.getWindow() == null) {
         return false;
      } else {
         long var1 = class310.getWindow().getHandle();
         return var1 == 0L ? false : GLFW.glfwGetMouseButton(var1, 0) == 1 || GLFW.glfwGetMouseButton(var1, 1) == 1;
      }
   }

   @Override
   public String getString3() {
      return "§7" + this.fuelPriority.getMode();
   }
}

