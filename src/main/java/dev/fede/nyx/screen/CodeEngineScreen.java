package dev.fede.nyx.screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import net.fabricmc.loader.api.FabricLoader;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.module.modules.client.ClickGUIModule;
import dev.fede.nyx.config.Manager;
import dev.fede.nyx.internal.CodeEngineScreenUtil;
import dev.fede.nyx.setting.BindSetting;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.DoubleListSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.util.CodeEngineScreenUtil2;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class CodeEngineScreen extends Screen {
   private static final int intVal = 120;
   private static final int intVal2 = 6;
   private static final int intVal3 = 6;
   private static final int intVal4 = 6;
   private static final int intVal5 = 18;
   private static final int intVal6 = 15;
   private static final int intVal7 = 13;
   private static final int intVal8 = 18;
   private static final int intVal9 = 16;
   private static final int intVal10 = 18;
   private static final int intVal11 = 8;
   private static final int intVal12 = 15;
   private static final int intVal13 = 3;
   private static final int intVal14 = 2;
   private static final int intVal15 = 96;
   private static final int intVal16 = 10;
   private static final int intVal17 = 6;
   private static final int intVal18 = 22;
   private final List<CodeEngineScreen.Inner1> list = new ArrayList<>();
   private final Set<Module> set = new HashSet<>();
   private NumberSetting numberSetting;
   private double doubleVal;
   private double doubleVal2;
   private Module module;
   private BindSetting bindSetting;
   private StringSetting stringSetting;
   private StringBuilder stringBuilder;
   private int intVal19;
   private CodeEngineScreen.Inner3 codeEngineScreenInner3;
   private boolean bool;

   public CodeEngineScreen() {
      super(Text.literal("Code Engine"));
   }

   protected void run() {
      this.list.clear();
      byte var1 = 6;

      for (Category var5 : Category.values()) {
         this.list.add(new CodeEngineScreen.Inner1(var5, var1, 6));
         var1 += 126;
      }

      CodeEngineScreen.Inner2.run(this.list);
      this.run23();
   }

   public boolean isEnabled2() {
      return false;
   }

   public void run2(DrawContext var1, int var2, int var3, float var4) {
      var1.fill(0, 0, this.width, this.height, -1610612736);
      boolean var5 = false;

      for (CodeEngineScreen.Inner1 var7 : this.list) {
         if (var7.bool2) {
            int var8 = var2 - var7.intVal4;
            int var9 = var3 - var7.intVal5;
            if (var8 != var7.intVal || var9 != var7.intVal2) {
               var7.intVal = var8;
               var7.intVal2 = var9;
               var5 = true;
            }
         }
      }

      if (var5) {
         this.run23();
      }

      if (this.numberSetting != null) {
         double var10 = doubleOf((var2 - this.doubleVal) / this.doubleVal2);
         double var13 = this.numberSetting.getValue();
         this.numberSetting.setValue(this.numberSetting.getMin() + var10 * (this.numberSetting.getMax() - this.numberSetting.getMin()));
         if (this.numberSetting.getValue() != var13) {
            run24();
         }
      }

      if (this.codeEngineScreenInner3 != null) {
         this.codeEngineScreenInner3.run4(var2, var3);
      }

      for (CodeEngineScreen.Inner1 var12 : this.list) {
         this.run3(var1, var12, var2, var3);
      }

      if (this.codeEngineScreenInner3 != null) {
         this.run13(var1, var2, var3);
      }

      this.run14(var1);
      this.run15(var1);
      super.render(var1, var2, var3, var4);
   }

   private void run3(DrawContext var1, CodeEngineScreen.Inner1 var2, int var3, int var4) {
      int var5 = var2.intVal;
      int var6 = var2.intVal2;
      int var7 = var5 + 120;
      int var8 = var6 + 18;
      boolean var9 = check7(var3, var4, var5, var6, 120, 18);
      CodeEngineScreenUtil.run5(var1, var5, var6, var7, var8, -100005366, -266724838);
      CodeEngineScreenUtil.run2(var1, var5, var6, var7, var8, var9 ? -12156236 : -12961222, 3);
      int var10 = var6 + (18 - CodeEngineScreenUtil.getInt()) / 2;
      CodeEngineScreenUtil.run10(var1, var2.category.getString(), var5 + 6, var10, -1250068);
      String var11 = var2.bool ? "▼" : "▶";
      int var12 = CodeEngineScreenUtil.intOf2(var11);
      CodeEngineScreenUtil.run10(var1, var11, var7 - var12 - 6, var10, var9 ? -9459492 : -6645094);
      if (var2.bool) {
         int var13 = var8 + 1;
         int var14 = Math.max(0, this.height - var13 - 6);
         int var15 = this.intOf(var2);
         int var16 = Math.min(var14, var15);
         int var17 = Math.max(0, var15 - var16);
         if (var2.intVal3 > var17) {
            var2.intVal3 = var17;
         }

         if (var2.intVal3 < 0) {
            var2.intVal3 = 0;
         }

         if (var16 > 0) {
            int var18 = var13 + var16;
            CodeEngineScreenUtil.run5(var1, var5, var13, var7, var18, -266724838, -267514354);
            CodeEngineScreenUtil.run2(var1, var5, var13, var7, var18, -12961222, 3);
            var1.enableScissor(var5, var13, var7, var18);

            try {
               int var19 = var13 + 2 - var2.intVal3;

               for (Module var21 : NyxClient.MODULES.listOf(var2.category)) {
                  this.run4(var1, var2, var21, var19, var3, var4);
                  var19 += 15;
                  if (this.set.contains(var21)) {
                     for (Setting var23 : var21.getList()) {
                        if (var23.isVisible()) {
                           int var24 = intOf2(var23);
                           this.run5(var1, var23, var2.intVal + 4, var19, 112, var3, var4);
                           var19 += var24;
                        }
                     }

                     var19++;
                  }
               }
            } finally {
               var1.disableScissor();
            }

            if (var15 > var16) {
               int var28 = Math.max(10, (int)((long)var16 * var16 / var15));
               int var29 = var13 + (int)((long)(var16 - var28) * var2.intVal3 / Math.max(1, var17));
               int var30 = var7 - 3;
               var1.fill(var30, var13, var30 + 2, var13 + var16, -15461356);
               var1.fill(var30, var29, var30 + 2, var29 + var28, -12156236);
            }
         }
      }
   }

   private void run4(DrawContext var1, CodeEngineScreen.Inner1 var2, Module var3, int var4, int var5, int var6) {
      int var7 = var2.intVal + 3;
      int var8 = var2.intVal + 120 - 3;
      boolean var9 = var5 >= var7 && var5 < var8 && var6 >= var4 && var6 < var4 + 15;
      boolean var10 = var3.isEnabled3();
      if (var9) {
         CodeEngineScreenUtil.run(var1, var7, var4, var8, var4 + 15 - 1, -1339413974, 2);
      }

      if (var10) {
         var1.fill(var7, var4 + 1, var7 + 2, var4 + 15 - 2, -12156236);
      }

      String var11 = var3.getString();
      int var12 = var4 + (15 - CodeEngineScreenUtil.getInt()) / 2;
      boolean var13 = this.set.contains(var3);
      boolean var14 = !var3.getList().isEmpty();
      int var15 = var14 ? 8 : 0;
      int var16 = var7 + 6 + (var10 ? 4 : 0);
      int var17 = var8 - 5 - var16 - var15;
      String var18 = stringOf(var11, var17);
      int var19 = var10 ? -1 : -10461088;
      CodeEngineScreenUtil.run10(var1, var18, var16, var12, var19);
      if (var14) {
         String var20 = var13 ? "▾" : "▸";
         int var21 = CodeEngineScreenUtil.intOf2(var20);
         CodeEngineScreenUtil.run10(var1, var20, var8 - var21 - 4, var12, var9 ? -9459492 : -6645094);
      }

      if (this.module == var3) {
         CodeEngineScreenUtil.run(var1, var7, var4, var8, var4 + 15 - 1, 1615233716, 2);
         String var22 = "press key...";
         int var23 = CodeEngineScreenUtil.intOf2(var22);
         CodeEngineScreenUtil.run10(var1, var22, var7 + (var8 - var7 - var23) / 2, var12, -1);
      }
   }

   private void run5(DrawContext var1, Setting var2, int var3, int var4, int var5, int var6, int var7) {
      int var8 = intOf2(var2);
      int var9 = var3 + var5;
      int var10 = var4 + var8 - 1;
      CodeEngineScreenUtil.run(var1, var3, var4, var9, var10, -1726342630, 2);
      if (var2 instanceof BooleanSetting var11) {
         this.run6(var1, var11, var3, var4, var5);
      } else if (var2 instanceof ModeSetting var12) {
         this.run7(var1, var12, var3, var4, var5);
      } else if (var2 instanceof NumberSetting var13) {
         this.run8(var1, var13, var3, var4, var5);
      } else if (var2 instanceof ColorSetting var14) {
         this.run9(var1, var14, var3, var4, var5);
      } else if (var2 instanceof BindSetting var15) {
         this.run10(var1, var15, var3, var4, var5);
      } else if (var2 instanceof StringSetting var16) {
         this.run11(var1, var16, var3, var4, var5);
      } else if (var2 instanceof DoubleListSetting var17) {
         this.run12(var1, var17, var3, var4, var5);
      } else {
         CodeEngineScreenUtil.run10(var1, var2.getName(), var3 + 5, var4 + (var8 - CodeEngineScreenUtil.getInt()) / 2, -6645094);
      }
   }

   private void run6(DrawContext var1, BooleanSetting var2, int var3, int var4, int var5) {
      int var6 = CodeEngineScreenUtil.getInt();
      int var7 = var4 + (13 - var6) / 2;
      CodeEngineScreenUtil.run10(var1, var2.getName(), var3 + 5, var7, -1250068);
      int var10 = var3 + var5 - 18 - 5;
      int var11 = var4 + 2;
      int var12 = var2.getValue() ? -12156236 : -15461356;
      CodeEngineScreenUtil.run(var1, var10, var11, var10 + 18, var11 + 8, var12, 3);
      int var14 = var11 + 1;
      int var15 = var2.getValue() ? var10 + 18 - 6 - 1 : var10 + 1;
      CodeEngineScreenUtil.run(var1, var15, var14, var15 + 6, var14 + 8 - 2, -1, 2);
   }

   private void run7(DrawContext var1, ModeSetting var2, int var3, int var4, int var5) {
      int var6 = CodeEngineScreenUtil.getInt();
      int var7 = var4 + (13 - var6) / 2;
      CodeEngineScreenUtil.run10(var1, var2.getName(), var3 + 5, var7, -1250068);
      String var8 = var2.getMode();
      int var9 = CodeEngineScreenUtil.intOf2(var8);
      int var10 = var9 + 6;
      int var11 = var3 + var5 - var10 - 4;
      int var12 = var4 + (13 - (var6 + 2)) / 2;
      CodeEngineScreenUtil.intOf(var1, var8, var11, var12, -870441442, -9459492);
   }

   private void run8(DrawContext var1, NumberSetting var2, int var3, int var4, int var5) {
      CodeEngineScreenUtil.run10(var1, var2.getName(), var3 + 5, var4 + 2, -1250068);
      String var6 = var2.isInteger() ? String.valueOf(var2.getValueInt()) : String.format("%.2f", var2.getValue());
      int var7 = CodeEngineScreenUtil.intOf2(var6);
      int var10 = var3 + var5 - var7 - 6 - 5;
      int var11 = var4 + 1;
      CodeEngineScreenUtil.run(var1, var10, var11, var10 + var7 + 6, var11 + CodeEngineScreenUtil.getInt() + 2 - 1, -870441442, 2);
      CodeEngineScreenUtil.run10(var1, var6, var10 + 3, var11 + 1, -3158065);
      double var12 = Math.max(1.0E-9, var2.getMax() - var2.getMin());
      double var14 = doubleOf((var2.getValue() - var2.getMin()) / var12);
      int var16 = var3 + 8;
      int var17 = var3 + var5 - 8;
      int var18 = var17 - var16;
      int var19 = var4 + 18 - 6;
      int var20 = var19 + 3;
      CodeEngineScreenUtil.run(var1, var16, var19, var17, var20, -15461356, 1);
      int var21 = var16 + (int)(var18 * var14);
      if (var21 > var16) {
         CodeEngineScreenUtil.run6(var1, var16, var19, var21, var20, -14005653, -9459492);
      }

      int var22 = var16 + (int)(var18 * var14);
      var1.fill(var22 - 1, var19 - 2, var22 + 2, var20 + 2, -1);
   }

   private void run9(DrawContext var1, ColorSetting var2, int var3, int var4, int var5) {
      int var6 = CodeEngineScreenUtil.getInt();
      int var7 = var4 + (13 - var6) / 2;
      CodeEngineScreenUtil.run10(var1, var2.getName(), var3 + 5, var7, -1250068);
      int var10 = var3 + var5 - 22 - 5;
      int var11 = var4 + 2;

      for (byte var12 = 0; var12 < 22; var12 += 4) {
         for (byte var13 = 0; var13 < 8; var13 += 4) {
            boolean var14 = (var12 + var13) / 4 % 2 == 0;
            var1.fill(
               var10 + var12, var11 + var13, Math.min(var10 + var12 + 4, var10 + 22), Math.min(var11 + var13 + 4, var11 + 8), var14 ? -13619152 : -11513776
            );
         }
      }

      CodeEngineScreenUtil.run(var1, var10, var11, var10 + 22, var11 + 8, var2.getValue(), 2);
      CodeEngineScreenUtil.run2(var1, var10, var11, var10 + 22, var11 + 8, -12961222, 2);
   }

   private void run10(DrawContext var1, BindSetting var2, int var3, int var4, int var5) {
      int var6 = CodeEngineScreenUtil.getInt();
      int var7 = var4 + (13 - var6) / 2;
      CodeEngineScreenUtil.run10(var1, var2.getName(), var3 + 5, var7, -1250068);
      boolean var8 = this.bindSetting == var2;
      String var9 = var8 ? "PRESS KEY..." : var2.keyName();
      int var10 = CodeEngineScreenUtil.intOf2(var9) + 6;
      int var11 = var3 + var5 - var10 - 4;
      int var12 = var4 + (13 - (var6 + 2)) / 2;
      int var13 = var8 ? -1 : -3158065;
      int var14 = var8 ? -1069120844 : -870441442;
      CodeEngineScreenUtil.intOf(var1, var9, var11, var12, var14, var13);
   }

   private void run11(DrawContext var1, StringSetting var2, int var3, int var4, int var5) {
      boolean var6 = this.stringSetting == var2;
      String var7 = var2.getName();
      CodeEngineScreenUtil.run10(var1, var7, var3 + 5, var4 + 2, -1250068);
      int var8 = CodeEngineScreenUtil.intOf2(var7);
      int var9 = var3 + 5 + var8 + 5;
      int var10 = var3 + var5 - 5;
      int var11 = var4 + 16 - 9;
      int var12 = var4 + 16 - 2;
      int var13 = var6 ? -12156236 : -12961222;
      CodeEngineScreenUtil.run(var1, var9, var11, var10, var12, -15461356, 2);
      CodeEngineScreenUtil.run2(var1, var9, var11, var10, var12, var13, 2);
      String var14 = var6 ? this.stringBuilder.toString() : var2.getValue();
      String var15 = stringOf2(var14, var10 - var9 - 4);
      CodeEngineScreenUtil.run10(var1, var15, var9 + 2, var11, -1);
      if (var6 && System.currentTimeMillis() / 500L % 2L == 0L) {
         int var16 = CodeEngineScreenUtil.intOf2(var15);
         var1.fill(var9 + 2 + var16, var11 + 1, var9 + 3 + var16, var12 - 1, -12156236);
      }
   }

   private void run12(DrawContext var1, DoubleListSetting var2, int var3, int var4, int var5) {
      CodeEngineScreenUtil.run10(var1, var2.getName(), var3 + 5, var4 + 2, -1250068);
      StringBuilder var6 = new StringBuilder();
      List var7 = var2.getValues();

      for (int var8 = 0; var8 < var7.size(); var8++) {
         if (var8 > 0) {
            var6.append(", ");
         }

         double var9 = (Double)var7.get(var8);
         if (var9 == Math.floor(var9) && !Double.isInfinite(var9)) {
            var6.append((int)var9);
         } else {
            var6.append(String.format("%.2f", var9));
         }
      }

      String var17 = var6.length() == 0 ? "(empty)" : var6.toString();
      byte var18 = 8;
      int var10 = var3 + var5 - 16 - 6;
      int var11 = var3 + var5 - var18 - 3;
      int var12 = var4 + 18 - 11;
      int var13 = var12 + 8;
      CodeEngineScreenUtil.run(var1, var10, var12, var10 + var18, var13, -870441442, 2);
      CodeEngineScreenUtil.run2(var1, var10, var12, var10 + var18, var13, -2142862668, 2);
      CodeEngineScreenUtil.run10(var1, "-", var10 + 3, var12, -1);
      CodeEngineScreenUtil.run(var1, var11, var12, var11 + var18, var13, -870441442, 2);
      CodeEngineScreenUtil.run2(var1, var11, var12, var11 + var18, var13, -2142862668, 2);
      CodeEngineScreenUtil.run10(var1, "+", var11 + 2, var12, -1);
      int var14 = var10 - (var3 + 5) - 4;
      String var15 = stringOf2(var17, var14);
      int var16 = CodeEngineScreenUtil.intOf2(var15);
      CodeEngineScreenUtil.run10(var1, var15, var10 - var16 - 4, var4 + 18 - 10, -6645094);
   }

   private void run13(DrawContext var1, int var2, int var3) {
      int var4 = this.codeEngineScreenInner3.intVal3;
      int var5 = this.codeEngineScreenInner3.intVal4;
      int var6 = this.codeEngineScreenInner3.intVal;
      int var7 = this.codeEngineScreenInner3.intVal2;
      CodeEngineScreenUtil.run5(var1, var6, var7, var6 + var4, var7 + var5, -266724838, -267514354);
      CodeEngineScreenUtil.run2(var1, var6, var7, var6 + var4, var7 + var5, -12156236, 3);
      CodeEngineScreenUtil.run5(var1, var6 + 1, var7 + 1, var6 + var4 - 1, var7 + 14, -100005366, -266724838);
      CodeEngineScreenUtil.run10(var1, this.codeEngineScreenInner3.colorSetting.getName(), var6 + 6, var7 + 3, -1250068);
      CodeEngineScreenUtil.run10(var1, "x", var6 + var4 - CodeEngineScreenUtil.intOf2("x") - 6, var7 + 3, -6645094);
      int var10 = var6 + 6;
      int var11 = var7 + 16;

      for (int var12 = 0; var12 < 96; var12++) {
         for (int var13 = 0; var13 < 96; var13++) {
            float var14 = var13 / 95.0F;
            float var15 = 1.0F - var12 / 95.0F;
            int var16 = 0xFF000000 | CodeEngineScreenUtil2.intOf5(this.codeEngineScreenInner3.floatVal, var14, var15);
            var1.fill(var10 + var13, var11 + var12, var10 + var13 + 1, var11 + var12 + 1, var16);
         }
      }

      int var22 = var10 + (int)(this.codeEngineScreenInner3.floatVal2 * 95.0F);
      int var23 = var11 + (int)((1.0F - this.codeEngineScreenInner3.floatVal3) * 95.0F);
      var1.fill(var22 - 2, var23, var22 + 3, var23 + 1, -1);
      var1.fill(var22, var23 - 2, var22 + 1, var23 + 3, -1);
      int var24 = var10 + 96 + 6;

      for (int var25 = 0; var25 < 96; var25++) {
         float var27 = var25 / 95.0F;
         int var17 = 0xFF000000 | CodeEngineScreenUtil2.intOf5(var27, 1.0F, 1.0F);
         var1.fill(var24, var11 + var25, var24 + 10, var11 + var25 + 1, var17);
      }

      int var26 = var11 + (int)(this.codeEngineScreenInner3.floatVal * 95.0F);
      var1.fill(var24 - 1, var26 - 1, var24 + 10 + 1, var26, -1);
      var1.fill(var24 - 1, var26, var24 + 10 + 1, var26 + 1, -16777216);
      int var28 = var24 + 10 + 6 - 2;

      for (int var29 = 0; var29 < 96; var29++) {
         int var18 = var29 / 4 % 2 == 0 ? -13619152 : -11513776;
         var1.fill(var28, var11 + var29, var28 + 10, var11 + var29 + 1, var18);
      }

      int var30 = 0xFF000000
         | CodeEngineScreenUtil2.intOf5(this.codeEngineScreenInner3.floatVal, this.codeEngineScreenInner3.floatVal2, this.codeEngineScreenInner3.floatVal3);

      for (int var31 = 0; var31 < 96; var31++) {
         int var19 = 255 - (int)(var31 / 95.0F * 255.0F);
         int var20 = (var19 & 0xFF) << 24 | var30 & 16777215;
         var1.fill(var28, var11 + var31, var28 + 10, var11 + var31 + 1, var20);
      }

      int var32 = var11 + (int)((1.0F - this.codeEngineScreenInner3.intVal5 / 255.0F) * 95.0F);
      var1.fill(var28 - 1, var32 - 1, var28 + 10 + 1, var32, -1);
      var1.fill(var28 - 1, var32, var28 + 10 + 1, var32 + 1, -16777216);
      int var33 = var11 + 96 + 6;
      int var34 = this.codeEngineScreenInner3.getInt();
      CodeEngineScreenUtil.run(var1, var6 + 6, var33, var6 + 6 + 28, var33 + 12, var34, 2);
      CodeEngineScreenUtil.run2(var1, var6 + 6, var33, var6 + 6 + 28, var33 + 12, -12961222, 2);
      String var21 = String.format("#%08X", var34);
      CodeEngineScreenUtil.run10(var1, var21, var6 + 6 + 34, var33 + 3, -1250068);
   }

   private void run14(DrawContext var1) {
      if (this.module != null || this.bindSetting != null) {
         int var3 = CodeEngineScreenUtil.intOf2("Press a key... (Escape to unbind, Right-Shift to cancel)");
         int var4 = (this.width - var3) / 2;
         int var5 = this.height - 34;
         CodeEngineScreenUtil.run5(var1, var4 - 8, var5 - 5, var4 + var3 + 8, var5 + CodeEngineScreenUtil.getInt() + 4, -266724838, -267514354);
         CodeEngineScreenUtil.run2(var1, var4 - 8, var5 - 5, var4 + var3 + 8, var5 + CodeEngineScreenUtil.getInt() + 4, -12156236, 3);
         CodeEngineScreenUtil.run10(var1, "Press a key... (Escape to unbind, Right-Shift to cancel)", var4, var5, -1);
      }
   }

   private void run15(DrawContext var1) {
      int var3 = CodeEngineScreenUtil.intOf2("LMB expand · RMB toggle · MMB bind · Wheel scroll · Right-Shift close");
      int var4 = this.width - var3 - 6;
      int var5 = this.height - 12;
      CodeEngineScreenUtil.run10(var1, "LMB expand · RMB toggle · MMB bind · Wheel scroll · Right-Shift close", var4, var5, -6645094);
   }

   public boolean check(Click var1, boolean var2) {
      int var3 = (int)var1.x();
      int var4 = (int)var1.y();
      int var5 = var1.button();
      if (this.codeEngineScreenInner3 != null) {
         if (check7(
            var3,
            var4,
            this.codeEngineScreenInner3.intVal,
            this.codeEngineScreenInner3.intVal2,
            this.codeEngineScreenInner3.intVal3,
            this.codeEngineScreenInner3.intVal4
         )) {
            this.codeEngineScreenInner3.run(var3, var4, var5);
            return true;
         } else {
            this.codeEngineScreenInner3 = null;
            run24();
            return true;
         }
      } else {
         if (this.stringSetting != null) {
            this.run19();
         }

         for (CodeEngineScreen.Inner1 var7 : this.list) {
            if (check7(var3, var4, var7.intVal, var7.intVal2, 120, 18)) {
               if (var5 == 0) {
                  if (var3 >= var7.intVal + 120 - 16) {
                     var7.bool = !var7.bool;
                     CodeEngineScreen.Inner2.run2(this.list);
                  } else {
                     var7.bool2 = true;
                     var7.intVal4 = var3 - var7.intVal;
                     var7.intVal5 = var4 - var7.intVal2;
                  }
               } else if (var5 == 1) {
                  var7.bool = !var7.bool;
                  CodeEngineScreen.Inner2.run2(this.list);
               }

               return true;
            }

            if (var7.bool) {
               int var8 = var7.intVal2 + 18 + 1;
               int var9 = Math.max(0, this.height - var8 - 6);
               int var10 = this.intOf(var7);
               int var11 = Math.min(var9, var10);
               if (check7(var3, var4, var7.intVal, var8, 120, var11)) {
                  int var12 = var8 + 2 - var7.intVal3;

                  for (Module var14 : NyxClient.MODULES.listOf(var7.category)) {
                     if (var4 >= var12 && var4 < var12 + 15) {
                        this.run16(var7, var14, var5, var3);
                        return true;
                     }

                     var12 += 15;
                     if (this.set.contains(var14)) {
                        for (Setting var16 : var14.getList()) {
                           if (var16.isVisible()) {
                              int var17 = intOf2(var16);
                              if (var4 >= var12 && var4 < var12 + var17) {
                                 this.run17(var16, var5, var7.intVal + 4, 112, var3, var4, var12);
                                 return true;
                              }

                              var12 += var17;
                           }
                        }

                        var12++;
                     }
                  }

                  return true;
               }
            }
         }

         return super.mouseClicked(var1, var2);
      }
   }

   private void run16(CodeEngineScreen.Inner1 var1, Module var2, int var3, int var4) {
      boolean var5 = var4 >= var1.intVal + 120 - 14;
      if (var3 == 0) {
         if (!var5 || var2.getList().isEmpty()) {
            var2.run19();
            run24();
         } else if (!this.set.remove(var2)) {
            this.set.add(var2);
         }
      } else if (var3 == 1) {
         if (var2.getList().isEmpty()) {
            var2.run19();
            run24();
         } else if (!this.set.remove(var2)) {
            this.set.add(var2);
         }
      } else if (var3 == 2) {
         this.module = var2;
         this.bindSetting = null;
      }
   }

   private void run17(Setting var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1 instanceof BooleanSetting var8) {
         var8.toggle();
         run24();
      } else if (var1 instanceof ModeSetting var9) {
         if (var2 == 0) {
            var9.cycle();
         } else if (var2 == 1) {
            var9.cycleBack();
         }

         run24();
      } else if (var1 instanceof NumberSetting var10 && var2 == 0) {
         this.numberSetting = var10;
         this.doubleVal = var3 + 8;
         this.doubleVal2 = var4 - 16;
         double var23 = doubleOf((var5 - this.doubleVal) / this.doubleVal2);
         var10.setValue(var10.getMin() + var23 * (var10.getMax() - var10.getMin()));
         run24();
      } else if (var1 instanceof ColorSetting var11 && var2 == 0) {
         this.run22(var11, var3 + var4, var7);
      } else if (var1 instanceof BindSetting var12) {
         if (var2 == 0) {
            this.bindSetting = var12;
            this.module = null;
         } else if (var2 == 1) {
            var12.setValue(-1);
            run24();
         }
      } else if (var1 instanceof StringSetting var13 && var2 == 0) {
         this.stringSetting = var13;
         String var22 = var13.getValue();
         this.stringBuilder = new StringBuilder(var22 == null ? "" : var22);
         this.intVal19 = this.stringBuilder.length();
      } else if (var1 instanceof DoubleListSetting var14) {
         byte var15 = 8;
         int var16 = var3 + var4 - 16 - 6;
         int var17 = var3 + var4 - var15 - 3;
         int var18 = var7 + 18 - 11;
         int var19 = var18 + 8;
         if (var6 >= var18 && var6 < var19) {
            if (var5 >= var17 && var5 < var17 + var15 && var2 == 0) {
               double var20 = var14.isEmpty() ? var14.getMin() : var14.get(var14.size() - 1);
               var14.add(var20);
               run24();
               return;
            }

            if (var5 >= var16 && var5 < var16 + var15 && var2 == 0) {
               if (!var14.isEmpty()) {
                  var14.remove(var14.size() - 1);
                  run24();
               }

               return;
            }
         }
      }
   }

   public boolean check2(Click var1) {
      boolean var2 = false;

      for (CodeEngineScreen.Inner1 var4 : this.list) {
         if (var4.bool2) {
            var2 = true;
         }

         var4.bool2 = false;
      }

      if (var2) {
         CodeEngineScreen.Inner2.run2(this.list);
      }

      if (this.numberSetting != null) {
         this.numberSetting = null;
         run24();
      }

      if (this.codeEngineScreenInner3 != null) {
         this.codeEngineScreenInner3.run3();
      }

      return super.mouseReleased(var1);
   }

   public boolean check3(double var1, double var3, double var5, double var7) {
      if (this.codeEngineScreenInner3 != null
         && check7(
            (int)var1,
            (int)var3,
            this.codeEngineScreenInner3.intVal,
            this.codeEngineScreenInner3.intVal2,
            this.codeEngineScreenInner3.intVal3,
            this.codeEngineScreenInner3.intVal4
         )) {
         return true;
      } else {
         for (CodeEngineScreen.Inner1 var10 : this.list) {
            if (var10.bool) {
               int var11 = var10.intVal2 + 18 + 1;
               int var12 = Math.max(0, this.height - var11 - 6);
               int var13 = this.intOf(var10);
               int var14 = Math.min(var12, var13);
               if (check7((int)var1, (int)var3, var10.intVal, var11, 120, var14)) {
                  int var15 = var10.intVal3;
                  var10.intVal3 -= (int)(var7 * 15.0);
                  int var16 = Math.max(0, var13 - var14);
                  if (var10.intVal3 > var16) {
                     var10.intVal3 = var16;
                  }

                  if (var10.intVal3 < 0) {
                     var10.intVal3 = 0;
                  }

                  if (var10.intVal3 != var15) {
                     CodeEngineScreen.Inner2.run2(this.list);
                  }

                  return true;
               }
            }
         }

         return super.mouseScrolled(var1, var3, var5, var7);
      }
   }

   public boolean check4(KeyInput var1) {
      int var2 = var1.key();
      int var3 = var1.modifiers();
      if (this.module == null && this.bindSetting == null) {
         if (this.stringSetting != null) {
            switch (var2) {
               case 86:
                  if ((var3 & 2) != 0 && this.client != null) {
                     String var4 = this.client.keyboard.getClipboard();
                     if (var4 != null) {
                        this.run18(var4);
                     }
                  }
                  break;
               case 256:
                  this.run20();
                  break;
               case 257:
               case 335:
                  this.run19();
                  break;
               case 259:
                  if (this.intVal19 > 0) {
                     this.stringBuilder.deleteCharAt(this.intVal19 - 1);
                     this.intVal19--;
                  }
                  break;
               case 261:
                  if (this.intVal19 < this.stringBuilder.length()) {
                     this.stringBuilder.deleteCharAt(this.intVal19);
                  }
                  break;
               case 262:
                  if (this.intVal19 < this.stringBuilder.length()) {
                     this.intVal19++;
                  }
                  break;
               case 263:
                  if (this.intVal19 > 0) {
                     this.intVal19--;
                  }
                  break;
               case 268:
                  this.intVal19 = 0;
                  break;
               case 269:
                  this.intVal19 = this.stringBuilder.length();
            }

            return true;
         } else if (this.codeEngineScreenInner3 != null && var2 == 256) {
            this.codeEngineScreenInner3 = null;
            return true;
         } else if (var2 == 256) {
            this.run21();
            return true;
         } else if (var2 == 344) {
            this.bool = true;
            return true;
         } else {
            return super.keyPressed(var1);
         }
      } else {
         if (var2 == 256) {
            if (this.module != null) {
               this.module.run7(0);
            } else {
               this.bindSetting.setValue(-1);
            }
         } else if (var2 != 344) {
            if (this.module != null) {
               this.module.run7(var2);
            } else {
               this.bindSetting.setValue(var2);
            }
         }

         this.module = null;
         this.bindSetting = null;
         run24();
         return true;
      }
   }

   public boolean check5(KeyInput var1) {
      int var2 = var1.key();
      if (this.bool && var2 == 344) {
         this.bool = false;
         this.run21();
         return true;
      } else {
         return super.keyReleased(var1);
      }
   }

   public boolean check6(CharInput var1) {
      if (this.stringSetting != null) {
         int var2 = var1.codepoint();
         if (var2 >= 32 && var2 != 127) {
            this.run18(var1.asString());
            return true;
         }
      }

      return super.charTyped(var1);
   }

   private void run18(String var1) {
      if (this.stringBuilder != null && this.stringSetting != null) {
         int var2 = this.stringSetting.getMaxLen();
         int var3 = var2 > 0 ? var2 - this.stringBuilder.length() : var1.length();
         if (var3 > 0) {
            String var4 = var1.length() > var3 ? var1.substring(0, var3) : var1;
            this.stringBuilder.insert(this.intVal19, var4);
            this.intVal19 = this.intVal19 + var4.length();
         }
      }
   }

   private void run19() {
      if (this.stringSetting != null) {
         this.stringSetting.setValue(this.stringBuilder.toString());
         this.stringSetting = null;
         this.stringBuilder = null;
         this.intVal19 = 0;
         run24();
      }
   }

   private void run20() {
      this.stringSetting = null;
      this.stringBuilder = null;
      this.intVal19 = 0;
   }

   public void run21() {
      CodeEngineScreen.Inner2.run2(this.list);
      ClickGUIModule var1 = NyxClient.MODULES.moduleOf2(ClickGUIModule.class);
      if (var1 != null) {
         var1.run5(false);
      }

      super.close();
   }

   private void run22(ColorSetting var1, int var2, int var3) {
      this.codeEngineScreenInner3 = new CodeEngineScreen.Inner3(this, var1);
      this.codeEngineScreenInner3.intVal3 = 140;
      this.codeEngineScreenInner3.intVal4 = 144;
      int var4 = var2 + 4;
      int var5 = var3;
      if (var4 + this.codeEngineScreenInner3.intVal3 > this.width) {
         var4 = this.width - this.codeEngineScreenInner3.intVal3 - 2;
      }

      if (var3 + this.codeEngineScreenInner3.intVal4 > this.height) {
         var5 = this.height - this.codeEngineScreenInner3.intVal4 - 2;
      }

      if (var4 < 2) {
         var4 = 2;
      }

      if (var5 < 2) {
         var5 = 2;
      }

      this.codeEngineScreenInner3.intVal = var4;
      this.codeEngineScreenInner3.intVal2 = var5;
   }

   private int intOf(CodeEngineScreen.Inner1 var1) {
      int var2 = 4;

      for (Module var4 : NyxClient.MODULES.listOf(var1.category)) {
         var2 += 15;
         if (this.set.contains(var4)) {
            for (Setting var6 : var4.getList()) {
               if (var6.isVisible()) {
                  var2 += intOf2(var6);
               }
            }

            var2++;
         }
      }

      return var2;
   }

   private static int intOf2(Setting var0) {
      if (var0 instanceof NumberSetting) {
         return 18;
      } else if (var0 instanceof StringSetting) {
         return 16;
      } else {
         return var0 instanceof DoubleListSetting ? 18 : 13;
      }
   }

   private void run23() {
      int var3 = Math.max(2, this.width - 120 - 2);
      int var4 = Math.max(2, this.height - 18 - 2);

      for (CodeEngineScreen.Inner1 var6 : this.list) {
         if (var6.intVal < 2) {
            var6.intVal = 2;
         }

         if (var6.intVal > var3) {
            var6.intVal = var3;
         }

         if (var6.intVal2 < 2) {
            var6.intVal2 = 2;
         }

         if (var6.intVal2 > var4) {
            var6.intVal2 = var4;
         }
      }
   }

   private static boolean check7(int var0, int var1, int var2, int var3, int var4, int var5) {
      return var0 >= var2 && var0 < var2 + var4 && var1 >= var3 && var1 < var3 + var5;
   }

   private static double doubleOf(double var0) {
      if (var0 < 0.0) {
         return 0.0;
      } else {
         return var0 > 1.0 ? 1.0 : var0;
      }
   }

   private static float floatOf(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   private static String stringOf(String var0, int var1) {
      if (var1 > 0 && CodeEngineScreenUtil.intOf2(var0) > var1) {
         int var3 = CodeEngineScreenUtil.intOf2("...");
         StringBuilder var4 = new StringBuilder();
         int var5 = 0;

         for (int var6 = 0; var6 < var0.length(); var6++) {
            char var7 = var0.charAt(var6);
            int var8 = CodeEngineScreenUtil.intOf2(String.valueOf(var7));
            if (var5 + var8 + var3 > var1) {
               break;
            }

            var4.append(var7);
            var5 += var8;
         }

         return var4.append("...").toString();
      } else {
         return var0;
      }
   }

   private static String stringOf2(String var0, int var1) {
      if (var1 > 0 && CodeEngineScreenUtil.intOf2(var0) > var1) {
         for (int var2 = 0; var2 < var0.length(); var2++) {
            String var3 = var0.substring(var2);
            if (CodeEngineScreenUtil.intOf2(var3) <= var1) {
               return var3;
            }
         }

         return "";
      } else {
         return var0;
      }
   }

   private static int intOf3(int var0, int var1) {
      return (var1 & 0xFF) << 24 | var0 & 16777215;
   }

   private static void run24() {
      try {
         Manager.INSTANCE.markDirty();
      } catch (Throwable var1) {
      }
   }

final static class Inner1 {
   final Category category;
   int intVal;
   int intVal2;
   int intVal3;
   boolean bool = true;
   boolean bool2;
   int intVal4;
   int intVal5;

   Inner1(Category var1, int var2, int var3) {
      this.category = var1;
      this.intVal = var2;
      this.intVal2 = var3;
   }
}

final static class Inner2 {
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

   private Inner2() {
   }

   static Path getPath() {
      Path var0 = FabricLoader.getInstance().getConfigDir().resolve("codeengine");

      try {
         Files.createDirectories(var0);
      } catch (IOException var2) {
      }

      return var0.resolve("clickgui-state.json");
   }

   static Path getPath2() {
      return FabricLoader.getInstance().getConfigDir().resolve("nyx").resolve("clickgui-state.json");
   }

   static void run(List<CodeEngineScreen.Inner1> var0) {
      Path var1 = getPath();
      if (!Files.exists(var1)) {
         Path var2 = getPath2();
         if (!Files.exists(var2)) {
            return;
         }

         var1 = var2;
      }

      try {
         String var12 = Files.readString(var1);
         JsonElement var3 = JsonParser.parseString(var12);
         if (!var3.isJsonObject()) {
            return;
         }

         JsonObject var4 = var3.getAsJsonObject();
         if (!var4.has("panels") || !var4.get("panels").isJsonObject()) {
            return;
         }

         JsonObject var5 = var4.getAsJsonObject("panels");

         for (CodeEngineScreen.Inner1 var7 : var0) {
            JsonElement var8 = var5.get(var7.category.name());
            if (var8 != null && var8.isJsonObject()) {
               JsonObject var9 = var8.getAsJsonObject();
               if (var9.has("x")) {
                  var7.intVal = var9.get("x").getAsInt();
               }

               if (var9.has("y")) {
                  var7.intVal2 = var9.get("y").getAsInt();
               }

               if (var9.has("scroll")) {
                  var7.intVal3 = var9.get("scroll").getAsInt();
               }

               if (var9.has("extended")) {
                  var7.bool = var9.get("extended").getAsBoolean();
               }
            }
         }
      } catch (RuntimeException | IOException var11) {
         try {
            Files.move(var1, var1.resolveSibling(var1.getFileName() + ".corrupt-" + System.currentTimeMillis()));
         } catch (IOException var10) {
         }

         System.err.println("[ClickGui] state load failed: " + var11.getMessage());
      }
   }

   static void run2(List<CodeEngineScreen.Inner1> var0) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (CodeEngineScreen.Inner1 var3 : var0) {
         LinkedHashMap var4 = new LinkedHashMap();
         var4.put("x", var3.intVal);
         var4.put("y", var3.intVal2);
         var4.put("scroll", var3.intVal3);
         var4.put("extended", var3.bool);
         var1.put(var3.category.name(), var4);
      }

      LinkedHashMap var11 = new LinkedHashMap();
      var11.put("panels", var1);
      String var12 = gson.toJson(var11);
      Path var13 = getPath();
      Path var5 = var13.resolveSibling(var13.getFileName() + ".tmp");

      try {
         Files.writeString(var5, var12);

         try {
            Files.move(var5, var13, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException var9) {
            Files.move(var5, var13, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var10) {
         System.err.println("[ClickGui] state save failed: " + var10.getMessage());

         try {
            Files.deleteIfExists(var5);
         } catch (IOException var8) {
         }
      }
   }
}

final static class Inner3 {
   final ColorSetting colorSetting;
   int intVal;
   int intVal2;
   int intVal3;
   int intVal4;
   float floatVal;
   float floatVal2;
   float floatVal3;
   int intVal5;
   boolean bool;
   boolean bool2;
   boolean bool3;
   final CodeEngineScreen codeEngineScreen;

   Inner3(CodeEngineScreen var1, ColorSetting var2) {
      this.codeEngineScreen = var1;
      this.colorSetting = var2;
      int var3 = var2.getValue();
      this.intVal5 = var3 >>> 24 & 0xFF;
      this.floatVal = var2.getHue();
      this.floatVal2 = var2.getSaturation();
      this.floatVal3 = var2.getBrightness();
   }

   int getInt() {
      int var1 = CodeEngineScreenUtil2.intOf5(this.floatVal, this.floatVal2, this.floatVal3) & 16777215;
      return (this.intVal5 & 0xFF) << 24 | var1;
   }

   void run2() {
      this.colorSetting.setValue(this.getInt());
      CodeEngineScreen.run24();
   }

   void run(int var1, int var2, int var3) {
      if (var3 == 0) {
         int var4 = CodeEngineScreenUtil.intOf2("x");
         int var5 = this.intVal + this.intVal3 - var4 - 6;
         int var6 = this.intVal2 + 3;
         if (var1 >= var5 && var1 <= var5 + var4 && var2 >= var6 && var2 <= var6 + CodeEngineScreenUtil.getInt()) {
            this.codeEngineScreen.codeEngineScreenInner3 = null;
         } else {
            int var7 = this.intVal + 6;
            int var8 = this.intVal2 + 16;
            int var9 = var7 + 96 + 6;
            int var10 = var9 + 10 + 6 - 2;
            if (CodeEngineScreen.check7(var1, var2, var7, var8, 96, 96)) {
               this.bool = true;
               this.run5(var1, var2);
            } else if (CodeEngineScreen.check7(var1, var2, var9, var8, 10, 96)) {
               this.bool2 = true;
               this.run6(var2);
            } else if (CodeEngineScreen.check7(var1, var2, var10, var8, 10, 96)) {
               this.bool3 = true;
               this.run7(var2);
            }
         }
      }
   }

   void run3() {
      if (this.bool || this.bool2 || this.bool3) {
         this.run2();
      }

      this.bool = false;
      this.bool2 = false;
      this.bool3 = false;
   }

   void run4(int var1, int var2) {
      if (this.bool) {
         this.run5(var1, var2);
      } else if (this.bool2) {
         this.run6(var2);
      } else if (this.bool3) {
         this.run7(var2);
      }
   }

   private void run5(int var1, int var2) {
      int var3 = this.intVal + 6;
      int var4 = this.intVal2 + 16;
      float var5 = CodeEngineScreen.floatOf((var1 - var3) / 95.0F, 0.0F, 1.0F);
      float var6 = CodeEngineScreen.floatOf(1.0F - (var2 - var4) / 95.0F, 0.0F, 1.0F);
      this.floatVal2 = var5;
      this.floatVal3 = var6;
      this.run2();
   }

   private void run6(int var1) {
      int var2 = this.intVal2 + 16;
      this.floatVal = CodeEngineScreen.floatOf((var1 - var2) / 95.0F, 0.0F, 1.0F);
      this.run2();
   }

   private void run7(int var1) {
      int var2 = this.intVal2 + 16;
      float var3 = CodeEngineScreen.floatOf((var1 - var2) / 95.0F, 0.0F, 1.0F);
      this.intVal5 = Math.round((1.0F - var3) * 255.0F);
      this.run2();
   }
}
}

