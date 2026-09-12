package dev.fede.nyx.ui;

import dev.fede.nyx.imgui.ImGuiFonts;
import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class NotificationUtils {
   private static final int intVal = 200;
   private static final int intVal2 = 6;
   private static final int intVal3 = 4;
   private static final int intVal4 = 8;
   private static final int intVal5 = 6;
   private static final int intVal6 = 3;
   private static final int intVal7 = 4;
   private static final long longVal = 3400L;
   private static final int intVal8 = -619572197;
   private static final int intVal9 = 352321535;
   private static final int intVal10 = 234881023;
   private static final int intVal11 = -920842;
   private static final int intVal12 = -10722445;
   private static final int intVal13 = 268435455;
   private static final int intVal14 = -12004982;
   private static final int intVal15 = -38037;
   private static final int intVal16 = 36;
   private static final float floatVal = 20.0F;
   private static final float floatVal2 = 10.0F;
   private static final float floatVal3 = 270.0F;
   private static final float floatVal4 = 58.0F;
   private static final float floatVal5 = 11.0F;
   private static final float floatVal6 = 14.0F;
   private static final float floatVal7 = 12.0F;
   private static final float floatVal8 = 11.0F;
   private static final float floatVal9 = 32.0F;
   private static final float floatVal10 = 9.0F;
   private static final float floatVal11 = 2.0F;
   private static final float floatVal12 = 2.0F;
   private static final float floatVal13 = 13.0F;
   private static final float floatVal14 = 11.0F;
   private static final float floatVal15 = 16.0F;
   private static final int intVal17 = -435089135;
   private static final int intVal18 = -1;
   private static final int intVal19 = -4210753;
   private static final CopyOnWriteArrayList<Notification> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
   private static long longVal2 = -1L;

   private NotificationUtils() {
   }

   public static void run8(String var0, String var1, INFO var2) {
      run(var0, var1, var2, 3400L);
   }

   public static void run(String var0, String var1, INFO var2, long var3) {
      copyOnWriteArrayList.add(new Notification(var0, var1, var2, var3));

      while (copyOnWriteArrayList.size() > 4) {
         copyOnWriteArrayList.remove(0);
      }
   }

   public static void run2() {
      copyOnWriteArrayList.clear();
   }

   public static boolean isEnabled2() {
      return atomicBoolean.get();
   }

   public static void run5(boolean var0) {
      atomicBoolean.set(var0);
   }

   public static void run3(MinecraftClient var0) {
      if (var0 != null && var0.getWindow() != null) {
         ArrayList var1 = new ArrayList<>(copyOnWriteArrayList);
         if (var1.isEmpty()) {
            longVal2 = -1L;
         } else {
            long var2 = Notification.getLong3();
            long var4 = longVal2 < 0L ? 16L : Math.min(120L, var2 - longVal2);
            longVal2 = var2;
            float var6 = ImGui.getIO().getDisplaySizeX();
            float var7 = ImGui.getIO().getDisplaySizeY();
            float var8 = ImGui.getMousePosX();
            float var9 = ImGui.getMousePosY();
            ImDrawList var10 = ImGui.getForegroundDrawList();
            ImFont var11 = ImGuiFonts.POPPINS;
            float var12 = var7 - 20.0F;
            int var13 = var1.size();
            float[] var14 = new float[var13];
            float[] var15 = new float[var13];
            float[] var16 = new float[var13];

            for (int var17 = var13 - 1; var17 >= 0; var17--) {
               Notification var18 = (Notification)var1.get(var17);
               var14[var17] = floatOf2(var18, var11);
               var15[var17] = floatOf(var18);
               var16[var17] = var12 - var15[var17];
               var12 -= var15[var17] + 10.0F;
            }

            for (int var32 = var13 - 1; var32 >= 0; var32--) {
               Notification var34 = (Notification)var1.get(var32);
               float var19 = var14[var32];
               float var20 = var15[var32];
               float var21 = var16[var32];
               float var22 = var34.floatOf(var21, var4);
               float var23 = var6 - 20.0F;
               float var24 = var23 - var19;
               boolean var25 = var8 >= var24 && var8 < var23 && var9 >= var22 && var9 < var22 + var20;
               var34.run5(var25);
               float var26 = var34.getFloat();
               float var27 = (1.0F - var26) * (var19 + 20.0F);
               float var28 = var24 + var27;
               float var29 = var23 + var27;
               float var31 = var22 + var20;
               if (!(var28 >= var6)) {
                  run4(var10, var11, var34, var28, var22, var29, var31, var26);
               }
            }

            ArrayList var33 = new ArrayList();

            for (Notification var36 : (java.util.List<Notification>)var1) {
               if (var36.isEnabled4()) {
                  var33.add(var36);
               }
            }

            if (!var33.isEmpty()) {
               copyOnWriteArrayList.removeAll(var33);
            }
         }
      }
   }

   private static float floatOf(Notification var0) {
      return 58.0F;
   }

   private static float floatOf2(Notification var0, ImFont var1) {
      return 270.0F;
   }

   private static void run4(ImDrawList var0, ImFont var1, Notification var2, float var3, float var4, float var5, float var6, float var7) {
      int var8 = switch (var2.getNotificationType()) {
         case UNKNOWN_2 -> -12004982;
         case UNKNOWN_4 -> -38037;
         default -> var2.getNotificationType().intVal2;
      };
      int var9 = var8 & 16777215;
      int var10 = intOf4(var8, var7);
      int var11 = intOf4(-920842, var7);
      int var12 = intOf4(-619572197, var7);
      int var13 = intOf4(352321535, var7);
      int var14 = intOf4(234881023, var7);
      int var15 = intOf4(268435455, var7);
      int var16 = 603979776 | var9;
      int var17 = intOf4(var16, var7);
      float var18 = var3 + 14.0F;
      float var19 = var4 + 30.0F;
      float var20 = var5 - 14.0F;
      float var21 = var6 + 2.0F;
      run6(var0, var18 - 30.0F, var19 - 30.0F, var20 + 30.0F, var21 + 30.0F, 134217728, 41.0F, var7);
      run6(var0, var18 - 22.0F, var19 - 22.0F, var20 + 22.0F, var21 + 22.0F, 335544320, 33.0F, var7);
      run6(var0, var18 - 14.0F, var19 - 14.0F, var20 + 14.0F, var21 + 14.0F, 671088640, 25.0F, var7);
      run6(var0, var18 - 6.0F, var19 - 6.0F, var20 + 6.0F, var21 + 6.0F, 1006632960, 17.0F, var7);
      run6(var0, var18, var19, var20, var21, 1342177280, 11.0F, var7);
      var0.addRectFilled(var3, var4, var5, var6, var12, 11.0F);
      var0.addRect(var3 + 0.5F, var4 + 0.5F, var5 - 0.5F, var6 - 0.5F, var13, 11.0F, 0, 1.0F);
      var0.addLine(var3 + 11.0F, var4 + 1.0F, var5 - 11.0F, var4 + 1.0F, var14, 1.0F);
      float var22 = var3 + 14.0F;
      float var23 = var4 + 12.0F;
      float var24 = var22 + 32.0F;
      float var25 = var23 + 32.0F;
      var0.addRectFilled(var22, var23, var24, var25, var17, 9.0F);
      String var26 = var2.getNotificationType().string;
      if (var26 != null && !var26.isEmpty() && var1 != null) {
         ImGui.pushFont(var1, 16.0F);

         try {
            float var27 = ImGui.calcTextSize(var26).x;
            float var28 = var22 + (32.0F - var27) * 0.5F;
            float var29 = var23 + 8.0F;
            var0.addText(var1, 16, var28, var29, var10, var26);
         } finally {
            ImGui.popFont();
         }
      }

      float var55 = var24 + 11.0F;
      float var56 = var5 - 14.0F;
      float var57 = var56 - var55;
      boolean var30 = var2.getString2() != null && !var2.getString2().isEmpty();
      float var31 = 13.0F + (var30 ? 13.0F : 0.0F);
      float var32 = var23 + (32.0F - var31) * 0.5F;
      String var33 = var2.getString();
      if (var33 != null && !var33.isEmpty() && var1 != null && var57 > 0.0F) {
         ImGui.pushFont(var1, 13.0F);

         try {
            String var34 = stringOf(var33, var57);
            var0.addText(var1, 13, var55, var32, var11, var34);
         } finally {
            ImGui.popFont();
         }
      }

      if (var30 && var1 != null && var57 > 0.0F) {
         ImGui.pushFont(var1, 11.0F);

         try {
            String var58 = stringOf(var2.getString2(), var57);
            float var35 = var32 + 13.0F + 2.0F;
            var0.addText(var1, 11, var55, var35, var10, var58);
         } finally {
            ImGui.popFont();
         }
      }

      float var59 = var6;
      float var60 = var6 - 3.0F;
      var0.addRectFilled(var3, var60, var5, var6, var15, 11.0F, 192);
      float var36 = var2.getFloat3();
      if (var36 > 0.0F) {
         float var37 = var3 + (var5 - var3) * var36;

         for (int var38 = 0; var38 < 8; var38++) {
            float var39 = (8 - var38) * 2.0F;
            float var40 = var38 / 7.0F;
            int var41 = Math.round(8.0F + 88.0F * var40 * var40);
            int var42 = intOf4(var41 << 24 | var9, var7);
            var0.addRectFilled(var3, var60 - var39, var37, var59, var42, 11.0F, 64);
         }

         int var61 = 64 | (var36 >= 1.0F ? 128 : 0);
         var0.addRectFilled(var3, var60, var37, var59, var10, 11.0F, var61);
      }
   }

   private static void run6(ImDrawList var0, float var1, float var2, float var3, float var4, int var5, float var6, float var7) {
      var0.addRectFilled(var1, var2, var3, var4, intOf4(var5, var7), var6);
   }

   private static String stringOf(String var0, float var1) {
      if (var0 != null && !var0.isEmpty()) {
         if (ImGui.calcTextSize(var0).x <= var1) {
            return var0;
         } else if (var1 <= ImGui.calcTextSize("…").x) {
            return "…";
         } else {
            int var3 = 0;
            int var4 = var0.length();

            while (var3 < var4) {
               int var5 = var3 + var4 + 1 >>> 1;
               String var6 = var0.substring(0, var5) + "…";
               if (ImGui.calcTextSize(var6).x <= var1) {
                  var3 = var5;
               } else {
                  var4 = var5 - 1;
               }
            }

            return var3 == 0 ? "…" : var0.substring(0, var3) + "…";
         }
      } else {
         return "";
      }
   }

   private static int intOf4(int var0, float var1) {
      float var2 = var1 < 0.0F ? 0.0F : (var1 > 1.0F ? 1.0F : var1);
      int var3 = var0 >>> 24 & 0xFF;
      int var4 = var0 >>> 16 & 0xFF;
      int var5 = var0 >>> 8 & 0xFF;
      int var6 = var0 & 0xFF;
      var3 = Math.max(0, Math.min(255, Math.round(var3 * var2)));
      return var3 << 24 | var6 << 16 | var5 << 8 | var4;
   }

   public static void run7(DrawContext var0, float var1) {
   }
}

