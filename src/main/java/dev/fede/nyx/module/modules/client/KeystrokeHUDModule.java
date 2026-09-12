package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.imgui.ImGuiFonts;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;

public class KeystrokeHUDModule extends Module {
   public static KeystrokeHUDModule keystrokeHUDModule;
   private static final float floatVal = 212.0F;
   private static final float floatVal2 = 7.0F;
   private static final float floatVal3 = 63.0F;
   private static final float floatVal4 = 56.0F;
   private static final float floatVal5 = 102.5F;
   private static final float floatVal6 = 44.0F;
   private static final float floatVal7 = 38.0F;
   private static final float floatVal8 = 12.0F;
   private static final float floatVal9 = 80.0F;
   private static final float floatVal10 = 3.0F;
   private static final float floatVal11 = 2.0F;
   private static final float floatVal12 = 18.0F;
   private static final float floatVal13 = 13.0F;
   private static final float floatVal14 = 9.0F;
   private static final int intVal = -15592421;
   private static final int intVal2 = 402653183;
   private static final int intVal3 = -3157028;
   private static final int intVal4 = -15986656;
   private static final int intVal5 = -8617329;
   private static final int intVal6 = -15986656;
   private static final int intVal7 = -8617329;
   private static final int intVal8 = -15986656;
   private static final int intVal9 = -1291845632;
   private final NumberSetting posX;
   private final NumberSetting posY;
   private final NumberSetting scale = new NumberSetting("Scale", 1.0, 0.5, 3.0, 0.1);
   private final BooleanSetting showMouse = new BooleanSetting("ShowMouse", true);
   private final BooleanSetting showCps = new BooleanSetting("ShowCPS", true);
   private final BooleanSetting showCard = new BooleanSetting("Card", false);
   private final ColorSetting activeColor = new ColorSetting("ActiveColor", -9663233);
   private final ColorSetting inactiveColor = new ColorSetting("InactiveColor", -15592421);
   private final ColorSetting textColor = new ColorSetting("TextColor", -1);
   private boolean bool;
   private boolean bool2;
   private final Deque<Long> deque = new ArrayDeque<>();
   private final Deque<Long> deque2 = new ArrayDeque<>();
   private static final int intVal10 = 7;
   private final boolean[] boolArray = new boolean[7];
   private final long[] longArray = new long[7];
   private final float[] floatArray = new float[7];
   private long longVal = 0L;

   public KeystrokeHUDModule() {
      super("KeystrokeHUD", "Vape-style keystroke overlay", Category.CLIENT);
      keystrokeHUDModule = this;
      int var2 = 720;

      try {
         if (class310 != null && class310.getWindow() != null) {
            int var1 = class310.getWindow().getScaledWidth();
            var2 = class310.getWindow().getScaledHeight();
         }
      } catch (Throwable var4) {
      }

      this.posX = new NumberSetting("X", 20.0, 0.0, 4000.0, 1.0);
      this.posY = new NumberSetting("Y", Math.max(20, var2 - 215 - 20), 0.0, 4000.0, 1.0);
      this.run6(
         new Setting[]{this.posX, this.posY, this.scale, this.showMouse, this.showCps, this.showCard, this.activeColor, this.inactiveColor, this.textColor}
      );
      this.showMouse.setVisible(KeystrokeHUDModule::getBoolean);
      this.showCps.setVisible(this.showMouse::getValue);
   }

   @Override
   public void run() {
      super.run();
      this.longVal = 0L;

      for (int var1 = 0; var1 < 7; var1++) {
         this.boolArray[var1] = false;
         this.longArray[var1] = 0L;
         this.floatArray[var1] = 0.0F;
      }

      this.deque.clear();
      this.deque2.clear();
   }

   @Override
   public void run2() {
      if (class310.options != null) {
         long var1 = System.currentTimeMillis();
         boolean var3 = class310.options.attackKey.isPressed();
         boolean var4 = class310.options.useKey.isPressed();
         if (var3 && !this.bool) {
            this.deque.addLast(var1);
         }

         if (var4 && !this.bool2) {
            this.deque2.addLast(var1);
         }

         this.bool = var3;
         this.bool2 = var4;
         run3(this.deque, var1);
         run3(this.deque2, var1);
      }
   }

   private static void run3(Deque<Long> var0, long var1) {
      while (!var0.isEmpty() && var1 - var0.peekFirst() > 1000L) {
         var0.pollFirst();
      }
   }

   public void run4(MinecraftClient var1) {
      if (var1 != null && var1.options != null && var1.getWindow() != null) {
         ImGuiFonts.tryEnsureLoaded();
         GameOptions var2 = var1.options;
         boolean[] var3 = new boolean[]{
            var2.forwardKey.isPressed(),
            var2.leftKey.isPressed(),
            var2.backKey.isPressed(),
            var2.rightKey.isPressed(),
            var2.jumpKey.isPressed(),
            var2.attackKey.isPressed(),
            var2.useKey.isPressed()
         };
         long var4 = System.currentTimeMillis();

         for (int var6 = 0; var6 < 7; var6++) {
            if (this.boolArray[var6] != var3[var6]) {
               this.longArray[var6] = var4;
               this.boolArray[var6] = var3[var6];
            }

            long var7 = var4 - this.longArray[var6];
            float var9 = Math.min(1.0F, (float)var7 / 90.0F);
            this.floatArray[var6] = var3[var6] ? var9 : 1.0F - var9;
         }

         if (this.longVal == 0L) {
            this.longVal = var4;
         }

         float var26 = Math.min(1.0F, (float)(var4 - this.longVal) / 420.0F);
         float var27 = floatOf2(var26);
         float var8 = var27;
         float var28 = (1.0F - var27) * 10.0F;
         int var10 = this.activeColor.getValue();
         int var11 = this.inactiveColor.getValue();
         float var12 = Math.max(0.1F, this.scale.getValueFloat());
         float var13 = this.posX.getValueFloat();
         float var14 = this.posY.getValueFloat() + var28 * var12;
         ImDrawList var15 = ImGui.getForegroundDrawList();
         float var17 = var13 + 74.5F * var12;
         run5(var15, var17, var14, 63.0F, 56.0F, 12.0F, this.floatArray[0], var10, var11, var12, var27);
         run6(var15, var17, var14, 63.0F, 56.0F, 18.0F, "W", this.floatArray[0], var12, var27, -3157028, -15986656);
         float var18 = var14 + 63.0F * var12;
         float var19 = var13 + 4.5F * var12;
         String[] var20 = new String[]{"A", "S", "D"};

         for (int var21 = 0; var21 < 3; var21++) {
            float var22 = var19 + var21 * 70.0F * var12;
            int var23 = var21 + 1;
            run5(var15, var22, var18, 63.0F, 56.0F, 12.0F, this.floatArray[var23], var10, var11, var12, var8);
            run6(var15, var22, var18, 63.0F, 56.0F, 18.0F, var20[var21], this.floatArray[var23], var12, var8, -3157028, -15986656);
         }

         float var29 = var18 + 63.0F * var12;
         if (this.showMouse.getValue()) {
            float var31 = var13 + 109.5F * var12;
            this.run7(var15, var13, var29, 102.5F, 44.0F, 12.0F, "LMB", this.deque.size(), this.floatArray[5], var10, var11, var12, var8);
            this.run7(var15, var31, var29, 102.5F, 44.0F, 12.0F, "RMB", this.deque2.size(), this.floatArray[6], var10, var11, var12, var8);
         }

         float var30 = var29 + 51.0F * var12;
         run8(var15, var13, var30, 212.0F, 38.0F, 12.0F, this.floatArray[4], var10, var11, var12, var8);
      }
   }

   private static void run5(
      ImDrawList var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, float var9, float var10
   ) {
      float var11 = var3 * var9;
      float var12 = var4 * var9;
      float var13 = var5 * var9;
      float var14 = 1.0F - 0.03F * var6;
      float var15 = var11 * (1.0F - var14);
      float var16 = var12 * (1.0F - var14);
      float var17 = var1 + var15 * 0.5F;
      float var18 = var2 + var16 * 0.5F + var6 * 1.0F * var9;
      float var19 = var17 + var11 * var14;
      float var20 = var18 + var12 * var14;
      float var21 = var13 * var14;
      int var22 = intOf(var8, var7, var6);
      var0.addRectFilled(var17, var18, var19, var20, intOf4(var22, var10), var21);
      int var23 = intOf(402653183, var7, var6);
      var0.addRect(var17 + 0.5F, var18 + 0.5F, var19 - 0.5F, var20 - 0.5F, intOf4(var23, var10), var21, 0, 1.0F);
   }

   private static void run6(
      ImDrawList var0, float var1, float var2, float var3, float var4, float var5, String var6, float var7, float var8, float var9, int var10, int var11
   ) {
      float var12 = var3 * var8;
      float var13 = var4 * var8;
      float var14 = 1.0F - 0.03F * var7;
      float var15 = var12 * (1.0F - var14);
      float var16 = var13 * (1.0F - var14);
      float var17 = var1 + var15 * 0.5F;
      float var18 = var2 + var16 * 0.5F + var7 * 1.0F * var8;
      float var19 = var12 * var14;
      float var20 = var13 * var14;
      float var21 = Math.max(6.0F, var5 * var8 * var14);
      ImFont var22 = ImGuiFonts.POPPINS;
      boolean var23 = var22 != null;
      if (var23) {
         ImGui.pushFont(var22, var21);
      }

      try {
         float var24 = ImGui.calcTextSize(var6).x;
         float var25 = ImGui.getTextLineHeight();
         float var26 = var17 + (var19 - var24) * 0.5F;
         float var27 = var18 + (var20 - var25) * 0.5F;
         int var28 = intOf(var10, var11, var7);
         int var29 = intOf4(var28, var9);
         if (var22 != null) {
            var0.addText(var22, (int)var21, var26, var27, var29, var6);
         } else {
            var0.addText(var26, var27, var29, var6);
         }
      } finally {
         if (var23) {
            ImGui.popFont();
         }
      }
   }

   private void run7(
      ImDrawList var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      String var7,
      int var8,
      float var9,
      int var10,
      int var11,
      float var12,
      float var13
   ) {
      run5(var1, var2, var3, var4, var5, var6, var9, var10, var11, var12, var13);
      float var14 = var4 * var12;
      float var15 = var5 * var12;
      float var16 = 1.0F - 0.03F * var9;
      float var17 = var14 * (1.0F - var16);
      float var18 = var15 * (1.0F - var16);
      float var19 = var2 + var17 * 0.5F;
      float var20 = var3 + var18 * 0.5F + var9 * 1.0F * var12;
      float var21 = var14 * var16;
      float var22 = var15 * var16;
      boolean var23 = this.showCps.getValue();
      String var24 = var8 + " CPS";
      float var25 = Math.max(6.0F, 13.0F * var12 * var16);
      ImFont var26 = ImGuiFonts.POPPINS;
      boolean var27 = var26 != null;
      if (var27) {
         ImGui.pushFont(var26, var25);
      }

      float var28 = 0.0F;
      float var29 = 0.0F;

      try {
         var29 = ImGui.calcTextSize(var7).x;
         var28 = ImGui.getTextLineHeight();
      } finally {
         if (var27) {
            ImGui.popFont();
         }
      }

      float var30 = Math.max(6.0F, 9.0F * var12 * var16);
      float var31 = 0.0F;
      float var32 = 0.0F;
      if (var23) {
         if (var26 != null) {
            ImGui.pushFont(var26, var30);
         }

         try {
            var32 = ImGui.calcTextSize(var24).x;
            var31 = ImGui.getTextLineHeight();
         } finally {
            if (var26 != null) {
               ImGui.popFont();
            }
         }
      }

      float var33 = 0.0F;
      float var34 = var28 + (var23 ? var31 : 0.0F);
      float var35 = var20 + (var22 - var34) * 0.5F;
      int var36 = intOf(-3157028, -15986656, var9);
      int var37 = intOf4(var36, var13);
      float var38 = var19 + (var21 - var29) * 0.5F;
      if (var26 != null) {
         ImGui.pushFont(var26, var25);

         try {
            var1.addText(var26, (int)var25, var38, var35, var37, var7);
         } finally {
            ImGui.popFont();
         }
      } else {
         var1.addText(var38, var35, var37, var7);
      }

      if (var23) {
         int var39 = intOf(-8617329, -15986656, var9);
         int var40 = intOf4(var39, var13);
         float var41 = var19 + (var21 - var32) * 0.5F;
         float var42 = var35 + var28 + var33;
         if (var26 != null) {
            ImGui.pushFont(var26, var30);

            try {
               var1.addText(var26, (int)var30, var41, var42, var40, var24);
            } finally {
               ImGui.popFont();
            }
         } else {
            var1.addText(var41, var42, var40, var24);
         }
      }
   }

   private static void run8(
      ImDrawList var0, float var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, float var9, float var10
   ) {
      run5(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      float var11 = var3 * var9;
      float var12 = var4 * var9;
      float var13 = 1.0F - 0.03F * var6;
      float var14 = var11 * (1.0F - var13);
      float var15 = var12 * (1.0F - var13);
      float var16 = var1 + var14 * 0.5F;
      float var17 = var2 + var15 * 0.5F + var6 * 1.0F * var9;
      float var18 = var11 * var13;
      float var19 = var12 * var13;
      float var20 = 80.0F * var9 * var13;
      float var21 = 3.0F * var9 * var13;
      float var22 = 2.0F * var9 * var13;
      float var23 = var16 + (var18 - var20) * 0.5F;
      float var24 = var17 + (var19 - var21) * 0.5F;
      int var25 = intOf(-8617329, -15986656, var6);
      var0.addRectFilled(var23, var24, var23 + var20, var24 + var21, intOf4(var25, var10), var22);
   }

   private static int intOf(int var0, int var1, float var2) {
      if (var2 <= 0.0F) {
         return var0;
      } else if (var2 >= 1.0F) {
         return var1;
      } else {
         int var3 = var0 >>> 24 & 0xFF;
         int var4 = var0 >>> 16 & 0xFF;
         int var5 = var0 >>> 8 & 0xFF;
         int var6 = var0 & 0xFF;
         int var7 = var1 >>> 24 & 0xFF;
         int var8 = var1 >>> 16 & 0xFF;
         int var9 = var1 >>> 8 & 0xFF;
         int var10 = var1 & 0xFF;
         int var11 = (int)(var3 + (var7 - var3) * var2);
         int var12 = (int)(var4 + (var8 - var4) * var2);
         int var13 = (int)(var5 + (var9 - var5) * var2);
         int var14 = (int)(var6 + (var10 - var6) * var2);
         return var11 << 24 | var12 << 16 | var13 << 8 | var14;
      }
   }

   private static float floatOf2(float var0) {
      if (var0 <= 0.0F) {
         return 0.0F;
      } else if (var0 >= 1.0F) {
         return 1.0F;
      } else {
         float var1 = 1.0F - var0;
         return 1.0F - var1 * var1 * var1;
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

   public void run9(DrawContext var1, float var2) {
   }

   private static Boolean getBoolean() {
      return true;
   }
}

