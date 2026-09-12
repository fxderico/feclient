package dev.fede.nyx.util;
import net.minecraft.client.util.InputUtil;

import dev.fede.nyx.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

public final class InputUtil$class_306Utils {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();
   private static String string;
   private static boolean bool;
   private static int intVal = 0;
   private static int intVal2 = 0;
   private static long longVal = System.currentTimeMillis();

   private InputUtil$class_306Utils() {
   }

   public static boolean check(String var0) {
      if (var0 == null) {
         return false;
      } else if (string != null && !string.equals(var0)) {
         return false;
      } else {
         string = var0;
         return true;
      }
   }

   public static void run3(String var0) {
      if (var0 != null) {
         if (string == null || string.equals(var0)) {
            if (bool) {
               run4();
            }

            string = null;
         }
      }
   }

   public static boolean check3(String var0) {
      return var0 != null && var0.equals(string);
   }

   public static String getString() {
      return string;
   }

   public static boolean isEnabled2() {
      return bool;
   }

   public static int getInt() {
      long var0 = System.currentTimeMillis();
      if (var0 - longVal >= 1000L) {
         intVal2 = intVal;
         intVal = 0;
         longVal = var0;
      }

      return intVal2;
   }

   private static void run() {
      long var0 = System.currentTimeMillis();
      if (var0 - longVal >= 1000L) {
         intVal2 = intVal;
         intVal = 0;
         longVal = var0;
      }

      intVal++;
   }

   public static boolean check2(String var0) {
      if (!check(var0)) {
         return false;
      } else {
         Key var1 = getclass3675class306();
         if (var1 == null) {
            return false;
         } else {
            boolean var2 = bool;
            KeyBinding.setKeyPressed(var1, true);
            KeyBinding.onKeyPressed(var1);
            bool = true;
            if (!var2) {
               run();
            }

            return true;
         }
      }
   }

   public static boolean check4(String var0) {
      if (!check(var0)) {
         return false;
      } else {
         Key var1 = getclass3675class306();
         if (var1 == null) {
            return false;
         } else {
            boolean var2 = bool;
            KeyBinding.setKeyPressed(var1, true);
            bool = true;
            if (!var2) {
               run();
            }

            return true;
         }
      }
   }

   public static boolean check5(String var0) {
      if (!check3(var0)) {
         return false;
      } else {
         run4();
         return true;
      }
   }

   private static void run4() {
      Key var0 = getclass3675class306();
      boolean var1 = bool;
      if (var0 == null) {
         bool = false;
         if (var1) {
            run();
         }
      } else {
         KeyBinding.setKeyPressed(var0, false);
         bool = false;
         if (var1) {
            run();
         }
      }
   }

   public static void run19() {
      run4();
      string = null;
   }

   private static Key getclass3675class306() {
      if (class310.options == null) {
         return null;
      } else {
         KeyBinding var0 = class310.options.attackKey;
         if (var0 == null) {
            return null;
         } else {
            try {
               Key var1 = ((KeyBindingAccessor)var0).nyx$getBoundKey();
               if (var1 != null) {
                  return var1;
               }
            } catch (Throwable var2) {
            }

            return var0.getDefaultKey();
         }
      }
   }
}

