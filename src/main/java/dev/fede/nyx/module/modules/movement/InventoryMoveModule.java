package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;

public class InventoryMoveModule extends Module {
   private final BooleanSetting includeSneak = new BooleanSetting("IncludeSneak", false);
   private final BooleanSetting includeJump = new BooleanSetting("IncludeJump", true);
   private final BooleanSetting mouseLook = new BooleanSetting("MouseLook", false);
   private static volatile boolean bool;
   private static volatile boolean bool2;
   private static volatile boolean bool3;
   private static volatile boolean bool4;

   public InventoryMoveModule() {
      super("InventoryMove", "Walk / jump / sneak while an inventory is open", Category.MOVEMENT);
      this.run6(new Setting[]{this.includeSneak, this.includeJump, this.mouseLook});
   }

   @Override
   public void run() {
      this.run4();
      bool = true;
   }

   @Override
   public void run2() {
      bool = false;
      bool2 = false;
      bool3 = false;
      bool4 = false;
   }

   @Override
   public void run3() {
      this.run4();
   }

   private void run4() {
      bool2 = this.includeSneak.getValue();
      bool3 = this.includeJump.getValue();
      bool4 = this.mouseLook.getValue();
   }

   public static boolean check(KeyBinding var0) {
      if (!bool) {
         return false;
      } else if (var0 == null) {
         return false;
      } else if (class310 == null || class310.options == null) {
         return false;
      } else if (class310.currentScreen == null) {
         return false;
      } else {
         boolean var1 = var0 == class310.options.forwardKey
            || var0 == class310.options.backKey
            || var0 == class310.options.leftKey
            || var0 == class310.options.rightKey
            || bool3 && var0 == class310.options.jumpKey
            || bool2 && var0 == class310.options.sneakKey;
         if (!var1) {
            return false;
         } else {
            Key var2;
            try {
               var2 = InputUtil.fromTranslationKey(var0.getBoundKeyTranslationKey());
            } catch (Throwable var4) {
               return false;
            }

            if (var2 == null || var2.getCategory() != Type.KEYSYM) {
               return false;
            } else if (var2.getCode() < 0) {
               return false;
            } else {
               return class310.getWindow() == null ? false : InputUtil.isKeyPressed(class310.getWindow(), var2.getCode());
            }
         }
      }
   }

   public static boolean isEnabled_s() {
      if (!bool) {
         return false;
      } else if (!bool4) {
         return false;
      } else {
         return class310 == null ? false : class310.currentScreen != null;
      }
   }

   @Override
   public String getString3() {
      if (!bool) {
         return null;
      } else {
         return bool4 ? "§7Look" : null;
      }
   }
}

