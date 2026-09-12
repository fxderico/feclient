package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.internal.CodeEngineScreenUtil;
import dev.fede.nyx.internal.HUDModuleData;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.NotificationUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

public class HUDModule extends Module {
   public static volatile HUDModule hUDModule;
   private final BooleanSetting watermarkEnabled = new BooleanSetting("WatermarkEnabled", true);
   private final ColorSetting watermarkStaticColor = (ColorSetting)new ColorSetting("WatermarkStaticColor", -12156236)
      .visibleWhen(this.watermarkEnabled::getValue);
   private final NumberSetting watermarkPosX = (NumberSetting)new NumberSetting("WatermarkPosX", 6.0, 0.0, 200.0, 1.0)
      .visibleWhen(this.watermarkEnabled::getValue);
   private final NumberSetting watermarkPosY = (NumberSetting)new NumberSetting("WatermarkPosY", 6.0, 0.0, 200.0, 1.0)
      .visibleWhen(this.watermarkEnabled::getValue);
   private final BooleanSetting arraylistEnabled = new BooleanSetting("ArraylistEnabled", true);
   private final BooleanSetting arraylistChroma = (BooleanSetting)new BooleanSetting("ArraylistChroma", false).visibleWhen(this.arraylistEnabled::getValue);
   private final NumberSetting arraylistChromaSpeed = (NumberSetting)new NumberSetting("ArraylistChromaSpeed", 4000.0, 500.0, 15000.0, 100.0)
      .visibleWhen(this::getBoolean2);
   private final NumberSetting arraylistChromaOffset = (NumberSetting)new NumberSetting("ArraylistChromaOffset", 30.0, 0.0, 90.0, 1.0)
      .visibleWhen(this::getBoolean);
   private final BooleanSetting arraylistShowTags = (BooleanSetting)new BooleanSetting("ArraylistShowTags", true).visibleWhen(this.arraylistEnabled::getValue);
   private final BooleanSetting leftSideAccent = (BooleanSetting)new BooleanSetting("LeftSideAccent", true).visibleWhen(this.arraylistEnabled::getValue);
   private final NumberSetting arraylistPosX = (NumberSetting)new NumberSetting("ArraylistPosX", 4.0, 0.0, 200.0, 1.0)
      .visibleWhen(this.arraylistEnabled::getValue);
   private final NumberSetting arraylistPosY = (NumberSetting)new NumberSetting("ArraylistPosY", 4.0, 0.0, 200.0, 1.0)
      .visibleWhen(this.arraylistEnabled::getValue);
   private final BooleanSetting showFps = new BooleanSetting("ShowFps", true);
   private final BooleanSetting showCoords = new BooleanSetting("ShowCoords", false);
   private final BooleanSetting showBps = new BooleanSetting("ShowBps", false);
   private final NumberSetting infoPanelPosX = new NumberSetting("InfoPanelPosX", 4.0, 0.0, 200.0, 1.0);
   private final NumberSetting infoPanelPosY = new NumberSetting("InfoPanelPosY", 4.0, 0.0, 200.0, 1.0);
   private final NumberSetting backgroundAlpha = new NumberSetting("BackgroundAlpha", 0.8, 0.0, 1.0, 0.05);
   private double doubleVal = Double.NaN;
   private double doubleVal2 = Double.NaN;
   private double doubleVal3 = 0.0;
   private static final int intVal = 3;
   private static final String string = "code engine";
   private static final String string2 = "|";
   private static final String string3 = " · ";
   private static final int intVal2 = 200;
   private static final int intVal3 = 12;
   private final Map<String, HUDModule.Inner1> map = new HashMap<>();
   private final BooleanSetting arraylistAnimate = (BooleanSetting)new BooleanSetting("ArraylistAnimate", true).visibleWhen(this.arraylistEnabled::getValue);
   private static final Identifier class2960 = HUDModuleData.class2960;

   public HUDModule() {
      super("HUD", "Renders the on-screen overlay", Category.CLIENT);
      this.run6(
         new Setting[]{
            this.watermarkEnabled,
            this.watermarkStaticColor,
            this.watermarkPosX,
            this.watermarkPosY,
            this.arraylistEnabled,
            this.arraylistChroma,
            this.arraylistChromaSpeed,
            this.arraylistChromaOffset,
            this.arraylistShowTags,
            this.leftSideAccent,
            this.arraylistAnimate,
            this.arraylistPosX,
            this.arraylistPosY,
            this.showFps,
            this.showCoords,
            this.showBps,
            this.infoPanelPosX,
            this.infoPanelPosY,
            this.backgroundAlpha
         }
      );
      this.run5(true);
      hUDModule = this;
   }

   @Override
   public boolean isEnabled() {
      return this.watermarkEnabled.getValue();
   }

   @Override
   public int getInt() {
      return this.watermarkStaticColor.getValue();
   }

   public int getInt2() {
      return this.watermarkPosX.getValueInt();
   }

   public int getInt3() {
      return this.watermarkPosY.getValueInt();
   }

   public boolean isEnabled2() {
      return this.arraylistEnabled.getValue();
   }

   @Override
   public boolean isEnabled3() {
      return this.arraylistChroma.getValue();
   }

   public long getLong() {
      return this.arraylistChromaSpeed.getValueLong();
   }

   public float getFloat() {
      return this.arraylistChromaOffset.getValueFloat();
   }

   public boolean isEnabled4() {
      return this.arraylistShowTags.getValue();
   }

   public boolean isEnabled5() {
      return this.leftSideAccent.getValue();
   }

   public boolean isEnabled6() {
      return this.arraylistAnimate.getValue();
   }

   public int getInt4() {
      return this.arraylistPosX.getValueInt();
   }

   public int getInt5() {
      return this.arraylistPosY.getValueInt();
   }

   public boolean isEnabled7() {
      return this.showFps.getValue();
   }

   public boolean isEnabled8() {
      return this.showCoords.getValue();
   }

   public boolean isEnabled9() {
      return this.showBps.getValue();
   }

   public int getInt6() {
      return this.infoPanelPosX.getValueInt();
   }

   public int getInt7() {
      return this.infoPanelPosY.getValueInt();
   }

   public double getDouble() {
      return this.backgroundAlpha.getValue();
   }

   public int getInt8() {
      return this.getInt9();
   }

   public double getDouble2() {
      return this.doubleVal3;
   }

   @Override
   public void run2() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 == null) {
         this.doubleVal = Double.NaN;
         this.doubleVal2 = Double.NaN;
         this.doubleVal3 = 0.0;
      } else {
         double var2 = var1.getX();
         double var4 = var1.getZ();
         if (!Double.isNaN(this.doubleVal)) {
            double var6 = var2 - this.doubleVal;
            double var8 = var4 - this.doubleVal2;
            double var10 = Math.sqrt(var6 * var6 + var8 * var8) * 20.0;
            this.doubleVal3 = this.doubleVal3 * 0.7 + var10 * 0.3;
         }

         this.doubleVal = var2;
         this.doubleVal2 = var4;
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      TextRenderer var3 = class310.textRenderer;
      if (var3 != null && class310.getWindow() != null) {
         NotificationUtils.run7(var1, var2);
      }
   }

   private void run14(DrawContext var1) {
      String var11 = class310.getCurrentFps() + " fps";
      int var12 = CodeEngineScreenUtil.getInt();
      int var13 = CodeEngineScreenUtil.intOf2("code engine");
      int var14 = CodeEngineScreenUtil.intOf2("|");
      int var15 = CodeEngineScreenUtil.intOf2(var11);
      int var16 = 8 + var13 + 4 + var14 + 4 + var15 + 7;
      int var17 = 5 + var12 + 5;
      int var18 = this.watermarkPosX.getValueInt();
      int var19 = this.watermarkPosY.getValueInt();
      int var20 = var18 + var16;
      int var21 = var19 + var17;
      int var22 = this.intOf(-266724838);
      int var23 = this.intOf(-267514354);
      CodeEngineScreenUtil.run(var1, var18 - 2, var19 - 2, var20 + 2, var21 + 2, 407274164, 5);
      CodeEngineScreenUtil.run(var1, var18 - 1, var19 - 1, var20 + 1, var21 + 1, 675709620, 4);
      CodeEngineScreenUtil.run(var1, var18, var19, var20, var21, var23, 3);
      CodeEngineScreenUtil.run5(var1, var18, var19, var20, var21, var22, var23);
      var1.fill(var18 + 3, var19, var20 - 3, var19 + 1, 419430399);
      var1.fill(var18 + 3, var21 - 1, var20 - 3, var21, 1073741824);
      CodeEngineScreenUtil.run2(var1, var18, var19, var20, var21, -12961222, 3);
      CodeEngineScreenUtil.run(var1, var18, var19, var18 + 2, var21, -12156236, 3);
      int var32 = var19 + 5;
      int var33 = var18 + 8;
      int var34 = this.watermarkStaticColor.getValue();
      if (var34 == -12156236) {
         var34 = -1250068;
      }

      CodeEngineScreenUtil.run10(var1, "code engine", var33, var32, var34);
      int var35 = var33 + var13 + 4;
      CodeEngineScreenUtil.run10(var1, "|", var35, var32, -9803158);
      int var36 = var35 + var14 + 4;
      CodeEngineScreenUtil.run10(var1, var11, var36, var32, -9459492);
   }

   private void run(DrawContext var1, int var2) {
      ArrayList var3 = new ArrayList<>(NyxClient.MODULES.getList2());
      long var4 = System.currentTimeMillis();
      boolean var6 = this.arraylistAnimate.getValue();
      HashSet var7 = new HashSet();

      for (Module var9 : (java.util.List<Module>)var3) {
         var7.add(var9.getString());
      }

      for (String var58 : (java.util.List<String>)var7) {
         HUDModule.Inner1 var10 = this.map.get(var58);
         if (var10 == null) {
            var10 = new HUDModule.Inner1();
            var10.longVal = var4;
            var10.bool = true;
            this.map.put(var58, var10);
         } else if (!var10.bool) {
            var10.bool = true;
            var10.longVal = var4;
         }
      }

      Iterator var57 = this.map.entrySet().iterator();

      while (var57.hasNext()) {
         Entry var59 = (Entry)var57.next();
         HUDModule.Inner1 var62 = (HUDModule.Inner1)var59.getValue();
         if (!var7.contains(var59.getKey())) {
            if (var62.bool) {
               var62.bool = false;
               var62.longVal2 = var4;
            }

            if (!var6 || var4 - var62.longVal2 > 200L) {
               var57.remove();
            }
         }
      }

      ArrayList var60 = new ArrayList(var3);
      if (var6) {
         HashSet var63 = new HashSet(var7);

         for (Module var12 : NyxClient.MODULES.getList()) {
            if (!var63.contains(var12.getString())) {
               HUDModule.Inner1 var13 = this.map.get(var12.getString());
               if (var13 != null && !var13.bool) {
                  var60.add(var12);
               }
            }
         }
      }

      if (!var60.isEmpty()) {
         int var64 = var60.size();
         String[] var65 = new String[var64];
         String[] var66 = new String[var64];
         int[] var67 = new int[var64];
         int[] var14 = new int[var64];
         int[] var15 = new int[var64];
         int[] var16 = new int[var64];
         boolean var17 = this.arraylistShowTags.getValue();
         int var18 = CodeEngineScreenUtil.getInt();
         int var22 = var18 + 4;

         for (int var26 = 0; var26 < var64; var26++) {
            Module var27 = (Module)var60.get(var26);
            var65[var26] = var27.getString();
            String var28 = var27.getString3();
            var66[var26] = var17 && var28 != null && !var28.isEmpty() ? var28 : null;
            var67[var26] = CodeEngineScreenUtil.intOf2(var65[var26]);
            if (var66[var26] != null) {
               var14[var26] = CodeEngineScreenUtil.intOf2(" · ");
               var15[var26] = CodeEngineScreenUtil.intOf2(var66[var26]);
            }

            var16[var26] = var67[var26] + var14[var26] + var15[var26];
         }

         Integer[] var68 = new Integer[var64];

         for (int var69 = 0; var69 < var64; var69++) {
            var68[var69] = var69;
         }

         Arrays.sort(var68, (a, b) -> 0);
         boolean var70 = this.arraylistChroma.getValue();
         long var71 = Math.max(1L, this.arraylistChromaSpeed.getValueLong());
         float var30 = this.arraylistChromaOffset.getValueFloat();
         boolean var31 = this.leftSideAccent.getValue();
         int var32 = this.arraylistPosX.getValueInt();
         int var33 = this.arraylistPosY.getValueInt();

         for (int var34 = 0; var34 < var64; var34++) {
            int var35 = var68[var34];
            Module var36 = (Module)var60.get(var35);
            HUDModule.Inner1 var37 = this.map.get(var65[var35]);
            float var38 = 0.0F;
            float var39 = 1.0F;
            if (var6 && var37 != null) {
               if (var37.bool) {
                  float var40 = floatOf3((float)(var4 - var37.longVal) / 200.0F);
                  var38 = (1.0F - floatOf2(var40)) * 12.0F;
                  var39 = floatOf2(var40);
               } else {
                  float var72 = floatOf3((float)(var4 - var37.longVal2) / 200.0F);
                  var38 = floatOf(var72) * 12.0F;
                  var39 = 1.0F - floatOf(var72);
               }
            }

            int var73 = 5 + var16[var35] + 6;
            int var41 = var2 - var32 + Math.round(var38);
            int var42 = var41 - var73;
            int var44 = var33 + var22;
            int var45 = var70 ? intOf2(var71, var34 * var30) : -12156236;
            int var46 = var70 ? var45 : -1;
            int var47 = -2003133798;
            int var48 = intOf4(this.intOf(-266724838), var39);
            int var49 = intOf4(this.intOf(-267514354), var39);
            int var50 = intOf4(419430399, var39);
            int var51 = intOf4(570425344, var39);
            var45 = intOf4(var45, var39);
            var46 = intOf4(var46, var39);
            var47 = intOf4(var47, var39);
            CodeEngineScreenUtil.run(var1, var42, var33, var41, var44, var49, 3);
            CodeEngineScreenUtil.run5(var1, var42, var33, var41, var44, var48, var49);
            if (var22 >= 6) {
               var1.fill(var42 + 1, var33 + 1, var41 - 1, var33 + 2, var50);
               var1.fill(var42 + 1, var44 - 2, var41 - 1, var44 - 1, var51);
            }

            if (var31) {
               var1.fill(var41 - 2, var33, var41, var44, var45);
            }

            int var52 = var33 + 2;
            int var53 = var42 + 5;
            CodeEngineScreenUtil.run10(var1, var65[var35], var53, var52, var46);
            if (var66[var35] != null) {
               int var54 = var53 + var67[var35];
               int var55 = var54 + var14[var35];
               CodeEngineScreenUtil.run10(var1, " · ", var54, var52, var47);
               CodeEngineScreenUtil.run10(var1, var66[var35], var55, var52, var47);
            }

            var33 += var22 + 2;
         }
      }
   }

   private void run3(DrawContext var1, int var2) {
      ArrayList var3 = new ArrayList(3);
      if (this.showFps.getValue()) {
         var3.add(new String[]{"FPS", Integer.toString(class310.getCurrentFps())});
      }

      if (this.showCoords.getValue() && class310.player != null) {
         String var4 = String.format("%.1f %.1f %.1f", class310.player.getX(), class310.player.getY(), class310.player.getZ());
         var3.add(new String[]{"XYZ", var4});
      }

      if (this.showBps.getValue() && class310.player != null) {
         var3.add(new String[]{"BPS", String.format("%.2f", this.doubleVal3)});
      }

      if (!var3.isEmpty()) {
         int var24 = CodeEngineScreenUtil.getInt();
         int var7 = var24 + 2;
         int var9 = 2 + var3.size() * var7 + 2;
         int var10 = 0;

         for (String[] var12 : (java.util.List<String[]>)var3) {
            int var13 = CodeEngineScreenUtil.intOf2(var12[0]) + 4 + CodeEngineScreenUtil.intOf2(var12[1]);
            if (var13 > var10) {
               var10 = var13;
            }
         }

         int var25 = this.infoPanelPosX.getValueInt();
         int var26 = this.infoPanelPosY.getValueInt();
         int var14 = var2 - var26;
         int var15 = var25 + 4 + var10 + 4;
         int var16 = var14 - var9;
         int var17 = this.intOf(-266724838);
         int var18 = this.intOf(-267514354);
         CodeEngineScreenUtil.run(var1, var25, var16, var15, var14, var18, 3);
         CodeEngineScreenUtil.run5(var1, var25, var16, var15, var14, var17, var18);
         CodeEngineScreenUtil.run2(var1, var25, var16, var15, var14, -12961222, 3);
         int var19 = var25 + 4;
         int var20 = var16 + 2;

         for (String[] var22 : (java.util.List<String[]>)var3) {
            CodeEngineScreenUtil.run10(var1, var22[0], var19, var20, -6645094);
            int var23 = var19 + CodeEngineScreenUtil.intOf2(var22[0]) + 4;
            CodeEngineScreenUtil.run10(var1, var22[1], var23, var20, -1250068);
            var20 += var7;
         }
      }
   }

   private int getInt9() {
      double var1 = this.backgroundAlpha.getValue();
      if (var1 < 0.0) {
         var1 = 0.0;
      } else if (var1 > 1.0) {
         var1 = 1.0;
      }

      return (int)Math.round(var1 * 255.0) & 0xFF;
   }

   private int intOf(int var1) {
      int var2 = var1 >>> 24 & 0xFF;
      int var3 = (var2 * this.getInt9() + 127) / 255;
      return (var3 & 0xFF) << 24 | var1 & 16777215;
   }

   private static int intOf2(long var0, float var2) {
      long var3 = System.currentTimeMillis();
      float var5 = (float)(var3 % var0) / (float)var0;
      float var6 = (var5 + var2 / 360.0F) % 1.0F;
      if (var6 < 0.0F) {
         var6++;
      }

      return 0xFF000000 | Color.HSBtoRGB(var6, 0.85F, 1.0F) & 16777215;
   }

   private static int intOf3(int var0, int var1) {
      return (var1 & 0xFF) << 24 | var0 & 16777215;
   }

   private static float floatOf2(float var0) {
      float var1 = 1.0F - var0;
      return 1.0F - var1 * var1 * var1;
   }

   private static float floatOf(float var0) {
      return var0 * var0 * var0;
   }

   private static float floatOf3(float var0) {
      return var0 < 0.0F ? 0.0F : (var0 > 1.0F ? 1.0F : var0);
   }

   private static int intOf4(int var0, float var1) {
      int var2 = var0 >>> 24 & 0xFF;
      int var3 = Math.round(var2 * floatOf3(var1)) & 0xFF;
      return var3 << 24 | var0 & 16777215;
   }

   private static int intOf5(int[] var0, Integer var1, Integer var2) {
      return var0[var2] - var0[var1];
   }

   private Boolean getBoolean() {
      return this.arraylistEnabled.getValue() && this.arraylistChroma.getValue();
   }

   private Boolean getBoolean2() {
      return this.arraylistEnabled.getValue() && this.arraylistChroma.getValue();
   }

final static class Inner1 {
   long longVal;
   long longVal2;
   boolean bool;

   private Inner1() {
   }
}
}

