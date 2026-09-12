package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.imgui.ImGuiFonts;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import imgui.ImVec2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

public class RegionMap extends Module {
   public static RegionMap regionMap;
   private static final byte[] byteArray = new byte[]{
      3,
      3,
      3,
      2,
      2,
      2,
      2,
      2,
      5,
      3,
      3,
      3,
      2,
      2,
      2,
      2,
      2,
      5,
      3,
      3,
      3,
      2,
      2,
      2,
      2,
      2,
      5,
      5,
      5,
      3,
      2,
      2,
      2,
      2,
      2,
      4,
      4,
      4,
      4,
      2,
      2,
      2,
      2,
      2,
      4,
      1,
      1,
      0,
      0,
      0,
      0,
      0,
      2,
      4,
      1,
      1,
      0,
      0,
      0,
      0,
      0,
      2,
      0,
      1,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      1,
      1,
      1,
      1,
      1,
      1,
      0,
      0,
      0
   };
   private static final RegionMap.ServerCluster[] regionMapServerClusterArray = new RegionMap.ServerCluster[]{
      RegionMap.ServerCluster.NA_WEST,
      RegionMap.ServerCluster.NA_EAST,
      RegionMap.ServerCluster.OCEANIA,
      RegionMap.ServerCluster.EU_WEST,
      RegionMap.ServerCluster.EU_CENTRAL,
      RegionMap.ServerCluster.ASIA
   };
   private final NumberSetting hudX = new NumberSetting("HudX", 20.0, 0.0, 3840.0, 1.0);
   private final NumberSetting hudY = new NumberSetting("HudY", 20.0, 0.0, 2160.0, 1.0);
   private final NumberSetting cellPixelSize = new NumberSetting("CellPixelSize", 20.0, 10.0, 40.0, 1.0);
   private final NumberSetting gridDimension = new NumberSetting("GridDimension", 9.0, 5.0, 20.0, 1.0);
   private final NumberSetting regionCellBlocks = new NumberSetting("RegionCellBlocks", 50000.0, 500.0, 100000.0, 500.0);
   private final BooleanSetting showLegend = new BooleanSetting("ShowLegend", true);
   private final BooleanSetting showHeader = new BooleanSetting("ShowHeader", true);
   private final BooleanSetting showCard = new BooleanSetting("Card", false);
   private static final int intVal = -266461666;
   private static final int intVal2 = 352321535;
   private static final int intVal3 = 587202559;
   private static final int intVal4 = 1342177280;
   private static final int intVal5 = -3617064;
   private static final int intVal6 = -9663233;
   private static final int intVal7 = -7696224;
   private static final int intVal8 = -1;
   private static final int intVal9 = -1073741824;
   private static final int intVal10 = 570425344;
   private static final int intVal11 = -1;
   private static final int intVal12 = 1627389951;
   private static final int intVal13 = 687865855;
   private static final int intVal14 = -3617064;
   private static final int intVal15 = 805306368;
   private static final float floatVal = 12.0F;
   private static final float floatVal2 = 8.0F;
   private static final float floatVal3 = 3.0F;
   private static final float floatVal4 = 4.0F;
   private static final int intVal16 = 13;
   private static final int intVal17 = 11;
   private static final int intVal18 = 11;
   private static final float floatVal5 = 12.0F;
   private static final float floatVal6 = 15.0F;
   private static final float floatVal7 = 96.0F;

   public RegionMap() {
      super("RegionMap", "DonutSMP server-region overlay coloured by cluster", Category.DONUTSMP);
      this.run6(
         new Setting[]{this.hudX, this.hudY, this.cellPixelSize, this.gridDimension, this.regionCellBlocks, this.showLegend, this.showHeader, this.showCard}
      );
      regionMap = this;
   }

   public static int intOf(int var0, int var1, int var2, int var3) {
      if (var2 < 1) {
         var2 = 1;
      }

      if (var3 < 1) {
         var3 = 1;
      }

      long var4 = (long)var2 * var3 / 2L;
      long var6 = var0 + var4;
      long var8 = var1 + var4;
      int var10 = (int)Math.floorDiv(var6, (long)var3);
      int var11 = (int)Math.floorDiv(var8, (long)var3);
      if (var10 < 0) {
         var10 = 0;
      } else if (var10 >= var2) {
         var10 = var2 - 1;
      }

      if (var11 < 0) {
         var11 = 0;
      } else if (var11 >= var2) {
         var11 = var2 - 1;
      }

      return var11 * var2 + var10 + 1;
   }

   public static RegionMap.ServerCluster regionMapServerClusterOf(int var0, int var1) {
      if (var0 >= 1 && var1 >= 1) {
         int var2 = var1 * var1;
         if (var0 > var2) {
            var0 = var2;
         }

         if (var1 == 9 && var0 <= byteArray.length) {
            int var3 = byteArray[var0 - 1] & 255;
            RegionMap.ServerCluster[] var4 = RegionMap.ServerCluster.values();
            if (var3 < var4.length) {
               return var4[var3];
            }
         }

         int var7 = (var0 - 1) % var1;
         int var8 = (var0 - 1) / var1;
         int var5 = Math.min(2, var7 * 3 / Math.max(1, var1));
         int var6 = Math.min(1, var8 * 2 / Math.max(1, var1));
         return regionMapServerClusterArray[var6 * 3 + var5];
      } else {
         return RegionMap.ServerCluster.NA_EAST;
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
   }

   public void run(ImDrawList var1, MinecraftClient var2) {
      if (var1 != null && var2 != null) {
         int var3 = Math.max(1, this.gridDimension.getValueInt());
         int var4 = Math.max(4, this.cellPixelSize.getValueInt());
         int var5 = Math.max(1, this.regionCellBlocks.getValueInt());
         float var6 = this.hudX.getValueInt();
         float var7 = this.hudY.getValueInt();
         ImGuiFonts.tryEnsureLoaded();
         ImFont var8 = ImGuiFonts.POPPINS;
         int var9 = -1;
         if (var2.player != null) {
            try {
               BlockPos var10 = var2.player.getBlockPos();
               var9 = intOf(var10.getX(), var10.getZ(), var3, var5);
            } catch (Throwable var97) {
            }
         }

         float var103 = var3 * var4 + (var3 - 1) * 3.0F;
         float var11 = 0.0F;
         if (this.showHeader.getValue()) {
            var11 = 24.0F;
         }

         float var12 = 0.0F;
         if (this.showLegend.getValue()) {
            var12 = 8.0F + 3 * 15.0F;
         }

         float var15 = 24.0F + Math.max(var103, this.showLegend.getValue() ? 2 * 96.0F : 0.0F);
         float var16 = 24.0F + var11 + var103 + var12;
         float var17 = var6;
         float var19 = var6 + var15;
         float var20 = var7 + var16;
         if (this.showCard.getValue()) {
            int var21 = intOf4(1342177280, 1.0F);
            var1.addRectFilled(var6 - 2.0F, var7 + 3.0F, var19 + 2.0F, var20 + 5.0F, var21, 10.0F);
            var1.addRectFilled(var6, var7, var19, var20, intOf4(-266461666, 1.0F), 8.0F);
            var1.addRect(var6, var7, var19, var20, intOf4(352321535, 1.0F), 8.0F, 0, 1.0F);
            var1.addRect(var6 + 1.0F, var7 + 1.0F, var19 - 1.0F, var20 - 1.0F, intOf4(587202559, 1.0F), 7.0F, 0, 1.0F);
         }

         float var104 = var7 + 12.0F;
         if (this.showHeader.getValue()) {
            String var22 = "REGION MAP";
            boolean var23 = var8 != null;
            if (var23) {
               ImGui.pushFont(var8, 13.0F);
            }

            try {
               ImVec2 var24 = ImGui.calcTextSize(var22);
               float var25 = var17 + 12.0F;
               float var26 = var104;
               int var27 = intOf4(-3617064, 1.0F);
               if (var23) {
                  var1.addText(var8, 13, var25, var104, var27, var22);
               } else {
                  var1.addText(var25, var104, var27, var22);
               }

               if (var9 >= 1) {
                  String var28 = "· cell " + var9;
                  if (var23) {
                     ImGui.pushFont(var8, 11.0F);
                  }

                  try {
                     ImVec2 var29 = ImGui.calcTextSize(var28);
                     float var30 = var19 - 12.0F - var29.x;
                     float var31 = var26 + 1.0F + 1.0F;
                     int var32 = intOf4(-7696224, 1.0F);
                     if (var23) {
                        var1.addText(var8, 11, var30, var31, var32, var28);
                     } else {
                        var1.addText(var30, var31, var32, var28);
                     }
                  } finally {
                     if (var23) {
                        ImGui.popFont();
                     }
                  }
               }

               float var112 = var104 + var24.y + 3.0F;
               float var116 = Math.min(var24.x, 44.0F);
               var1.addRectFilled(var25, var112, var25 + var116, var112 + 1.0F, intOf4(-9663233, 1.0F), 0.5F);
            } finally {
               if (var23) {
                  ImGui.popFont();
               }
            }

            var104 += var11;
         }

         float var106 = var6 + 12.0F;
         float var107 = var104;
         int var108 = Math.max(8, Math.min(Math.round(var4 * 0.55F), 16));
         int var109 = intOf4(-1, 1.0F);
         int var110 = intOf4(-1073741824, 1.0F);
         int var111 = intOf4(570425344, 1.0F);
         if (var8 != null) {
            ImGui.pushFont(var8, var108);
         }

         try {
            for (int var113 = 0; var113 < var3; var113++) {
               for (int var117 = 0; var117 < var3; var117++) {
                  int var120 = var113 * var3 + var117 + 1;
                  RegionMap.ServerCluster var123 = regionMapServerClusterOf(var120, var3);
                  float var126 = var106 + var117 * (var4 + 3.0F);
                  float var33 = var107 + var113 * (var4 + 3.0F);
                  float var34 = var126 + var4;
                  float var35 = var33 + var4;
                  int var36 = var123.intVal & 16777215 | -805306368;
                  var1.addRectFilled(var126, var33, var34, var35, intOf4(var36, 1.0F), 4.0F);
                  var1.addRect(var126, var33, var34, var35, var111, 4.0F, 0, 1.0F);
                  String var37 = Integer.toString(var120);
                  ImVec2 var38 = ImGui.calcTextSize(var37);
                  float var39 = var126 + Math.max(1.0F, (var4 - var38.x) * 0.5F);
                  float var40 = var33 + Math.max(0.0F, (var4 - var38.y) * 0.5F);
                  if (var8 != null) {
                     var1.addText(var8, var108, var39 + 1.0F, var40 + 1.0F, var110, var37);
                     var1.addText(var8, var108, var39, var40, var109, var37);
                  } else {
                     var1.addText(var39 + 1.0F, var40 + 1.0F, var110, var37);
                     var1.addText(var39, var40, var109, var37);
                  }
               }
            }
         } finally {
            if (var8 != null) {
               ImGui.popFont();
            }
         }

         if (var9 >= 1 && var9 <= var3 * var3) {
            int var114 = var9 - 1;
            int var118 = var114 % var3;
            int var121 = var114 / var3;
            float var124 = var106 + var118 * (var4 + 3.0F);
            float var127 = var107 + var121 * (var4 + 3.0F);
            float var129 = var124 + var4;
            float var131 = var127 + var4;
            int var133 = intOf4(1627389951, 1.0F);
            int var135 = intOf4(687865855, 1.0F);
            int var137 = intOf4(-1, 1.0F);
            var1.addRect(var124 - 3.0F, var127 - 3.0F, var129 + 3.0F, var131 + 3.0F, var135, 6.0F, 0, 1.0F);
            var1.addRect(var124 - 2.0F, var127 - 2.0F, var129 + 2.0F, var131 + 2.0F, var133, 5.0F, 0, 1.0F);
            var1.addRect(var124, var127, var129, var131, var137, 4.0F, 0, 2.0F);
            if (var2.player != null) {
               try {
                  double var139 = var2.player.getX();
                  double var141 = var2.player.getZ();
                  double var42 = (var139 % var5 + var5) % var5;
                  double var44 = (var141 % var5 + var5) % var5;
                  double var46 = var42 / var5;
                  double var48 = var44 / var5;
                  float var50 = var124 + (float)(var46 * var4);
                  float var51 = var127 + (float)(var48 * var4);
                  double var52 = Math.toRadians(var2.player.getYaw());
                  double var54 = Math.cos(var52);
                  double var56 = Math.sin(var52);
                  float var58 = Math.max(6.0F, var4 * 0.55F);
                  float var59 = -var58 * 0.55F;
                  float var60 = var58 * 0.35F;
                  float var61 = var58 * 0.35F;
                  float var62 = var58 * 0.1F;
                  float[] var63 = new float[]{0.0F, var61, 0.0F, -var61};
                  float[] var64 = new float[]{var59, var60, var60 - var62, var60};
                  float[] var65 = new float[4];
                  float[] var66 = new float[4];

                  for (int var67 = 0; var67 < 4; var67++) {
                     var65[var67] = var50 + (float)(var63[var67] * var54 - var64[var67] * var56);
                     var66[var67] = var51 + (float)(var63[var67] * var56 + var64[var67] * var54);
                  }

                  int var145 = intOf4(-16777216, 0.85F);
                  int var68 = intOf4(-1, 1.0F);
                  int var69 = intOf4(1617736959, 1.0F);
                  var1.addCircleFilled(var50, var51, var58 * 0.85F, var69, 24);
                  var1.addTriangleFilled(var65[0], var66[0], var65[1], var66[1], var65[2], var66[2], var68);
                  var1.addTriangleFilled(var65[0], var66[0], var65[2], var66[2], var65[3], var66[3], var68);

                  for (int var70 = 0; var70 < 4; var70++) {
                     int var71 = (var70 + 1) % 4;
                     var1.addLine(var65[var70], var66[var70], var65[var71], var66[var71], var145, 1.2F);
                  }
               } catch (Throwable var100) {
               }
            }
         }

         var104 = var107 + var103;
         if (this.showLegend.getValue()) {
            float var115 = var104 + 8.0F;
            float var119 = var6 + 12.0F;
            int var122 = intOf4(-3617064, 1.0F);
            int var125 = intOf4(805306368, 1.0F);
            RegionMap.ServerCluster[] var128 = RegionMap.ServerCluster.values();
            boolean var130 = var8 != null;
            if (var130) {
               ImGui.pushFont(var8, 11.0F);
            }

            try {
               for (int var132 = 0; var132 < var128.length; var132++) {
                  int var134 = var132 % 2;
                  int var136 = var132 / 2;
                  float var138 = var119 + var134 * 96.0F;
                  float var140 = var115 + var136 * 15.0F;
                  float var142 = var140 + 1.5F;
                  float var41 = var138 + 12.0F;
                  float var143 = var142 + 12.0F;
                  var1.addRectFilled(var138, var142, var41, var143, intOf4(var128[var132].intVal, 1.0F), 3.0F);
                  var1.addRect(var138, var142, var41, var143, var125, 3.0F, 0, 1.0F);
                  ImVec2 var43 = ImGui.calcTextSize(var128[var132].string);
                  float var144 = var41 + 6.0F;
                  float var45 = var140 + (15.0F - var43.y) * 0.5F;
                  if (var130) {
                     var1.addText(var8, 11, var144, var45, var122, var128[var132].string);
                  } else {
                     var1.addText(var144, var45, var122, var128[var132].string);
                  }
               }
            } finally {
               if (var130) {
                  ImGui.popFont();
               }
            }
         }
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

   public static enum ServerCluster {
      EU_CENTRAL("EU Central", -11890728),
      EU_WEST("EU West", -1668548),
      NA_EAST("NA East", -6596170),
      NA_WEST("NA West", -10634397),
      ASIA("Asia", -932849),
      OCEANIA("Oceania", -8742484);

      public final String string;
      public final int intVal;
      private static final RegionMap.ServerCluster[] regionMapServerClusterArray = getRegionMapServerClusterArray();

      private ServerCluster(String label, int argb) {
         this.string = label;
         this.intVal = argb;
      }

      private static RegionMap.ServerCluster[] getRegionMapServerClusterArray() {
         return new RegionMap.ServerCluster[]{EU_CENTRAL, EU_WEST, NA_EAST, NA_WEST, ASIA, OCEANIA};
      }
   }
}

