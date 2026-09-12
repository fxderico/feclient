package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.mixin.MinecraftClientInvoker;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BindSetting;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.tag.ItemTags;

public class AutoClickerModule extends Module {
   private final NumberSetting leftCPS = new NumberSetting("LeftCPS", 10.0, 0.0, 20.0, 1.0);
   private final NumberSetting rightCPS = new NumberSetting("RightCPS", 0.0, 0.0, 20.0, 1.0);
   private final ModeSetting buttonMode = new ModeSetting("ButtonMode", "Left", "Left", "Right", "Both");
   private final ModeSetting holdMode = new ModeSetting("HoldMode", "LMB", "LMB", "RMB", "Both", "BindKey");
   private final BindSetting bindKey = new BindSetting("BindKey", 344);
   private final BooleanSetting jitter = new BooleanSetting("Jitter", true);
   private final BooleanSetting burstPause = new BooleanSetting("BurstPause", true);
   private final NumberSetting maxCpsHuman = new NumberSetting("MaxCPSHuman", 14.0, 1.0, 20.0, 1.0);
   private final BooleanSetting holdForWeaponOnly = new BooleanSetting("HoldForWeaponOnly", false);
   private final BooleanSetting pauseInInventory = new BooleanSetting("PauseInInventory", true);
   private long longVal;
   private long longVal2;
   private int intVal;
   private int intVal2;

   public AutoClickerModule() {
      super("AutoClicker", "Emulates rapid mouse clicks", Category.COMBAT);
      this.run6(
         new Setting[]{
            this.leftCPS,
            this.rightCPS,
            this.buttonMode,
            this.holdMode,
            this.bindKey,
            this.jitter,
            this.burstPause,
            this.maxCpsHuman,
            this.holdForWeaponOnly,
            this.pauseInInventory
         }
      );
      this.leftCPS.visibleWhen(this::getBoolean5);
      this.rightCPS.visibleWhen(this::getBoolean4);
      this.bindKey.visibleWhen(this::getBoolean3);
   }

   @Override
   public void run() {
      long var1 = System.currentTimeMillis();
      this.longVal = var1 + (long)this.doubleOf3(doubleOf2(this.doubleOf(this.leftCPS.getValue())));
      this.longVal2 = var1 + (long)this.doubleOf3(doubleOf2(this.doubleOf(this.rightCPS.getValue())));
      this.intVal = getInt3();
      this.intVal2 = getInt3();
   }

   @Override
   public void run2() {
      this.longVal = 0L;
      this.longVal2 = 0L;
      this.intVal = 0;
      this.intVal2 = 0;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.options != null) {
         if (!this.pauseInInventory.getValue() || class310.currentScreen == null) {
            if (!this.holdForWeaponOnly.getValue() || this.isEnabled4()) {
               boolean var1 = !this.buttonMode.check("Right");
               boolean var2 = !this.buttonMode.check("Left");
               boolean var3 = var1 && this.isEnabled();
               boolean var4 = var2 && this.isEnabled2();
               long var5 = System.currentTimeMillis();
               MinecraftClientInvoker var7 = (MinecraftClientInvoker)class310;
               if (var3 && this.doubleOf(this.leftCPS.getValue()) > 0.0 && var5 >= this.longVal) {
                  var7.nyx$doAttack();
                  this.longVal = var5 + this.longOf(this.leftCPS.getValue(), true);
               } else if (!var3) {
                  this.longVal = Math.max(this.longVal, var5 + (long)doubleOf2(this.doubleOf(this.leftCPS.getValue())));
               }

               if (var4 && this.doubleOf(this.rightCPS.getValue()) > 0.0 && var5 >= this.longVal2) {
                  var7.nyx$doItemUse();
                  this.longVal2 = var5 + this.longOf(this.rightCPS.getValue(), false);
               } else if (!var4) {
                  this.longVal2 = Math.max(this.longVal2, var5 + (long)doubleOf2(this.doubleOf(this.rightCPS.getValue())));
               }
            }
         }
      }
   }

   public boolean isEnabled() {
      if (this.holdMode.check("LMB") || this.holdMode.check("Both")) {
         return class310.options.attackKey.isPressed();
      } else {
         return this.holdMode.check("BindKey") ? this.isEnabled3() : false;
      }
   }

   private boolean isEnabled2() {
      if (this.holdMode.check("RMB") || this.holdMode.check("Both")) {
         return class310.options.useKey.isPressed();
      } else {
         return this.holdMode.check("BindKey") ? this.isEnabled3() : false;
      }
   }

   public boolean isEnabled3() {
      if (this.bindKey.isUnbound()) {
         return false;
      } else {
         int var1 = this.bindKey.getValue();

         try {
            return InputUtil.isKeyPressed(class310.getWindow(), var1);
         } catch (Throwable var3) {
            return false;
         }
      }
   }

   private double doubleOf(double var1) {
      double var3 = this.maxCpsHuman.getValue();
      return var3 <= 0.0 ? 0.0 : Math.min(var1, var3);
   }

   private static double doubleOf2(double var0) {
      return var0 <= 0.0 ? Double.MAX_VALUE : 1000.0 / var0;
   }

   private long longOf(double var1, boolean var3) {
      double var4 = doubleOf2(this.doubleOf(var1));
      double var6 = this.doubleOf3(var4);
      if (this.burstPause.getValue()) {
         int var8 = var3 ? --this.intVal : --this.intVal2;
         if (var8 <= 0) {
            var6 += ThreadLocalRandom.current().nextInt(300, 801);
            if (var3) {
               this.intVal = getInt3();
            } else {
               this.intVal2 = getInt3();
            }
         }
      }

      if (var6 > 3600000.0) {
         var6 = 3600000.0;
      }

      return (long)var6;
   }

   private double doubleOf3(double var1) {
      if (!this.jitter.getValue()) {
         return var1;
      } else {
         double var3 = 0.85 + ThreadLocalRandom.current().nextDouble() * 0.3;
         return var1 * var3;
      }
   }

   private static int getInt3() {
      return ThreadLocalRandom.current().nextInt(20, 41);
   }

   private boolean isEnabled4() {
      if (class310.player == null) {
         return false;
      } else {
         ItemStack var1 = class310.player.getMainHandStack();
         return var1 != null && !var1.isEmpty()
            ? var1.isIn(ItemTags.SWORDS) || var1.getItem() instanceof AxeItem || var1.getItem() instanceof TridentItem
            : false;
      }
   }

   private Boolean getBoolean3() {
      return this.holdMode.check("BindKey");
   }

   private Boolean getBoolean4() {
      return !this.buttonMode.check("Left");
   }

   private Boolean getBoolean5() {
      return !this.buttonMode.check("Right");
   }
}

