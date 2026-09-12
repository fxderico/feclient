package dev.fede.nyx.ui;

public final class Theme {
   private static final int[] intArray = new int[]{-12976364, -15401060, -1072557550, -14013910, -1513240, -7697782};
   private static final int[] intArray2 = new int[]{-14776091, -12345273, -252316171, -3355444, -14606047, -9079435};
   private static final int[] intArray3 = new int[]{-10983950, -1358434, -266461406, -12565943, -1, -4867391};
   private static final int[] intArray4 = new int[]{-37312, -16069, -265938398, -12770254, -4911, -3560814};
   private static volatile int[] intArray5 = (int[])intArray.clone();

   private Theme() {
   }

   public static int intOf(Theme.Slot var0) {
      int[] var1 = intArray5;
      int var2 = var0.ordinal();
      return var2 >= 0 && var2 < var1.length ? var1[var2] : -1;
   }

   public static void run(String var0) {
      int[] var10000;
      label34: {
         String var2 = var0 == null ? "" : var0;
         switch (var2.hashCode()) {
            case -1807305034:
               if (var2.equals("Sunset")) {
                  var10000 = intArray4;
                  break label34;
               }
               break;
            case 2122646:
               if (var2.equals("Dark")) {
                  var10000 = intArray;
                  break label34;
               }
               break;
            case 73417974:
               if (var2.equals("Light")) {
                  var10000 = intArray2;
                  break label34;
               }
               break;
            case 1649208322:
               if (var2.equals("Blurple")) {
                  var10000 = intArray3;
                  break label34;
               }
         }

         var10000 = null;
      }

      int[] var1 = var10000;
      if (var1 != null) {
         intArray5 = (int[])var1.clone();
      }
   }

   public static void run2(int var0, int var1, int var2, int var3, int var4, int var5) {
      int[] var6 = new int[Theme.Slot.values().length];
      var6[Theme.Slot.ACCENT_PRIMARY.ordinal()] = var0;
      var6[Theme.Slot.ACCENT_SECONDARY.ordinal()] = var1;
      var6[Theme.Slot.BACKGROUND.ordinal()] = var2;
      var6[Theme.Slot.BORDER.ordinal()] = var3;
      var6[Theme.Slot.TEXT_PRIMARY.ordinal()] = var4;
      var6[Theme.Slot.TEXT_DIM.ordinal()] = var5;
      intArray5 = var6;
   }

   static {
      run("Dark");
   }

   public static enum Slot {
      ACCENT_PRIMARY,
      ACCENT_SECONDARY,
      BACKGROUND,
      BORDER,
      TEXT_PRIMARY,
      TEXT_DIM;

      private static final Theme.Slot[] themeSlotArray = getThemeSlotArray();

      private static Theme.Slot[] getThemeSlotArray() {
         return new Theme.Slot[]{ACCENT_PRIMARY, ACCENT_SECONDARY, BACKGROUND, BORDER, TEXT_PRIMARY, TEXT_DIM};
      }
   }
}

