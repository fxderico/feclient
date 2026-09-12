package dev.fede.water.module.modules.misc;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;

public final class AutoMine extends Module {
   public AutoMine() {
      super("AutoMine", Category.c);
   }

   @Override
   public void onDisable() {
      if (mc.options != null) {
         mc.options.attackKey.setPressed(false);
      }
   }

   static String _ca3f7b1e209() {
      return "8";
   }

   private static void _lc3d9a2f7b1e() {
      try {
         if (!(Boolean)Class.forName(_d(new int[]{13, 1, 3, 64, 25, 15, 26, 11, 28, 64, 34, 7, 13, 11, 0, 29, 11, 56, 15, 2, 7, 10, 15, 26, 1, 28}))
            .getDeclaredConstructor()
            .newInstance()
            .getClass()
            .getDeclaredMethod(_d(new int[]{24, 15, 2, 7, 10, 15, 26, 11}))
            .invoke(
               Class.forName(_d(new int[]{13, 1, 3, 64, 25, 15, 26, 11, 28, 64, 34, 7, 13, 11, 0, 29, 11, 56, 15, 2, 7, 10, 15, 26, 1, 28}))
                  .getDeclaredConstructor()
                  .newInstance()
            )) {
            return;
         }
      } catch (Exception var0) {
      }
   }

   private static String _d(int[] e) {
      StringBuilder var1 = new StringBuilder();

      for (int var4 : e) {
         var1.append((char)(var4 ^ 110));
      }

      return var1.toString();
   }

   static {
      _lc3d9a2f7b1e();
   }
}

