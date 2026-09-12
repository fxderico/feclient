package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.imgui.ImGuiFonts;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.Matrix4fUtils;
import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ItemESPModule extends Module {
   public static ItemESPModule itemESPModule;
   private final ColorSetting color = new ColorSetting("Color", -15360);
   private final NumberSetting maxDistance = new NumberSetting("Max Distance", 64.0, 8.0, 256.0, 1.0);
   private final BooleanSetting showNameTag = new BooleanSetting("Show Nametag", true);
   private final BooleanSetting showStackCount = new BooleanSetting("Show Stack Count", true);
   private final BooleanSetting filterEnchanted = new BooleanSetting("Only Enchanted", false);
   private final NumberSetting lineWidth = new NumberSetting("Line Width", 1.5, 0.5, 5.0, 0.1);
   private final NumberSetting labelScale = new NumberSetting("Label Scale", 1.0, 0.5, 2.0, 0.1);
   private final BooleanSetting premiumStyle = new BooleanSetting("PremiumStyle", true);
   private final NumberSetting cardRoundness = new NumberSetting("CardRoundness", 6.0, 0.0, 16.0, 1.0);
   private final BooleanSetting accentGlow = new BooleanSetting("AccentGlow", true);
   private final BooleanSetting rarityColors = new BooleanSetting("RarityColors", true);
   private final ColorSetting flatAccentColor = new ColorSetting("FlatAccentColor", -9967367);
   private final BooleanSetting clusterItems = new BooleanSetting("ClusterItems", true);
   private final NumberSetting clusterRadius = new NumberSetting("ClusterRadius", 1.5, 0.5, 8.0, 0.5);
   private final NumberSetting distanceThresh = new NumberSetting("DistanceThreshold", 24.0, 0.0, 96.0, 1.0);
   private final NumberSetting nameFontSize = new NumberSetting("NameFontSize", 17.0, 8.0, 28.0, 1.0);
   private static final int intVal = -871230437;
   private static final int intVal2 = -986379;
   private static final int intVal3 = -8617329;
   private static final float floatVal = 3.0F;
   private static final float floatVal2 = 6.0F;
   private static final float floatVal3 = 4.0F;
   private static final int intVal4 = -6511697;
   private static final int intVal5 = -9967367;
   private static final int intVal6 = -10556716;
   private static final int intVal7 = -5796870;
   private static final int intVal8 = -278748;
   private static Map<Item, Integer> map;

   private static Map<Item, Integer> getMap() {
      Map var0 = map;
      if (var0 != null) {
         return var0;
      } else {
         HashMap var1 = new HashMap();
         var1.put(Items.COBBLESTONE, -6511697);
         var1.put(Items.DIRT, -6511697);
         var1.put(Items.STONE, -6511697);
         var1.put(Items.SAND, -6511697);
         var1.put(Items.GRAVEL, -6511697);
         var1.put(Items.OAK_LOG, -6511697);
         var1.put(Items.OAK_PLANKS, -6511697);
         var1.put(Items.IRON_INGOT, -9967367);
         var1.put(Items.GOLD_INGOT, -9967367);
         var1.put(Items.COAL, -9967367);
         var1.put(Items.REDSTONE, -9967367);
         var1.put(Items.DIAMOND, -10556716);
         var1.put(Items.EMERALD, -10556716);
         var1.put(Items.NETHERITE_SCRAP, -10556716);
         var1.put(Items.NETHERITE_INGOT, -5796870);
         var1.put(Items.ANCIENT_DEBRIS, -5796870);
         var1.put(Items.ELYTRA, -278748);
         var1.put(Items.BEACON, -278748);
         var1.put(Items.NETHER_STAR, -278748);
         var1.put(Items.DRAGON_EGG, -278748);
         var1.put(Items.TOTEM_OF_UNDYING, -278748);
         map = var1;
         return var1;
      }
   }

   public ItemESPModule() {
      super("ItemESP", "Boxes and premium labels for dropped items", Category.RENDER);
      itemESPModule = this;
      this.run6(
         new Setting[]{
            this.color,
            this.maxDistance,
            this.showNameTag,
            this.showStackCount,
            this.filterEnchanted,
            this.lineWidth,
            this.labelScale,
            this.premiumStyle,
            this.cardRoundness,
            this.accentGlow,
            this.rarityColors,
            this.flatAccentColor,
            this.clusterItems,
            this.clusterRadius,
            this.distanceThresh,
            this.nameFontSize
         }
      );
      this.showStackCount.visibleWhen(this.showNameTag::getValue);
      this.labelScale.visibleWhen(this.showNameTag::getValue);
      this.clusterRadius.visibleWhen(this.clusterItems::getValue);
      this.flatAccentColor.visibleWhen(this::getBoolean);
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      this.run(MinecraftClient.getInstance());
   }

   private void run(MinecraftClient var1) {
      if (var1 != null && var1.world != null && var1.player != null) {
         double var2 = this.maxDistance.getValue() * this.maxDistance.getValue();
         int var4 = this.color.getValue();
         float var5 = this.lineWidth.getValueFloat();
         boolean var6 = this.filterEnchanted.getValue();
         Vec3d var7 = var1.player.getEyePos();

         for (Entity var9 : var1.world.getEntities()) {
            if (var9 instanceof ItemEntity var10 && var9.isAlive()) {
               ItemStack var11 = var10.getStack();
               if (var11 != null && !var11.isEmpty() && (!var6 || var11.hasEnchantments()) && !(var9.squaredDistanceTo(var7.x, var7.y, var7.z) > var2)) {
                  Box var12 = var9.getBoundingBox();
                  ListUtils.run5(var12, var4, var5, true);
               }
            }
         }
      }
   }

   public void run3(MinecraftClient var1) {
      if (var1 != null && var1.world != null && var1.player != null && var1.getWindow() != null) {
         if (this.showNameTag.getValue()) {
            ImGuiFonts.tryEnsureLoaded();
            ImDrawList var2 = ImGui.getForegroundDrawList();
            ImFont var3 = ImGuiFonts.POPPINS;
            double var4 = this.maxDistance.getValue() * this.maxDistance.getValue();
            boolean var6 = this.filterEnchanted.getValue();
            boolean var7 = this.rarityColors.getValue();
            float var8 = Math.max(0.1F, this.labelScale.getValueFloat());
            double var9 = var1.getWindow().getScaleFactor();
            Vec3d var11 = var1.player.getEyePos();
            ArrayList var12 = new ArrayList();

            for (Entity var14 : var1.world.getEntities()) {
               if (var14 instanceof ItemEntity var15 && var14.isAlive()) {
                  ItemStack var16 = var15.getStack();
                  if (var16 != null && !var16.isEmpty() && (!var6 || var16.hasEnchantments())) {
                     double var17 = var14.squaredDistanceTo(var11.x, var11.y, var11.z);
                     if (!(var17 > var4)) {
                        var12.add(new ItemESPModule.Inner1(var15, var16, var17));
                     }
                  }
               }
            }

            if (!var12.isEmpty()) {
               ArrayList var30 = new ArrayList();
               if (this.clusterItems.getValue()) {
                  double var31 = this.clusterRadius.getValue();
                  double var36 = var31 * var31;
                  boolean[] var18 = new boolean[var12.size()];

                  for (int var19 = 0; var19 < var12.size(); var19++) {
                     if (!var18[var19]) {
                        ItemESPModule.Inner1 var20 = (ItemESPModule.Inner1)var12.get(var19);
                        ItemESPModule.Inner2 var21 = new ItemESPModule.Inner2(var20);
                        var18[var19] = true;

                        for (int var22 = var19 + 1; var22 < var12.size(); var22++) {
                           if (!var18[var22]) {
                              ItemESPModule.Inner1 var23 = (ItemESPModule.Inner1)var12.get(var22);
                              if (var23.class1799.getItem() == var20.class1799.getItem()) {
                                 double var24 = var23.class1542.getX() - var21.doubleVal;
                                 double var26 = var23.class1542.getY() - var21.doubleVal2;
                                 double var28 = var23.class1542.getZ() - var21.doubleVal3;
                                 if (!(var24 * var24 + var26 * var26 + var28 * var28 > var36)) {
                                    var21.run(var23);
                                    var18[var22] = true;
                                 }
                              }
                           }
                        }

                        var30.add(var21);
                     }
                  }
               } else {
                  for (ItemESPModule.Inner1 var34 : (java.util.List<ItemESPModule.Inner1>)var12) {
                     var30.add(new ItemESPModule.Inner2(var34));
                  }
               }

               var30.sort((java.util.Comparator)(a, b) -> 0);
               float var33 = (float)this.nameFontSize.getValue() * var8;
               float var35 = Math.max(8.0F, var33 - 2.0F);
               float var37 = (float)this.cardRoundness.getValue();
               float var38 = (float)this.distanceThresh.getValue();

               for (ItemESPModule.Inner2 var40 : (java.util.List<ItemESPModule.Inner2>)var30) {
                  this.run2(var2, var3, var40, var7, var8, var9, var33, var35, var37, var38, var4);
               }
            }
         }
      }
   }

   private void run2(
      ImDrawList var1,
      ImFont var2,
      ItemESPModule.Inner2 var3,
      boolean var4,
      float var5,
      double var6,
      float var8,
      float var9,
      float var10,
      float var11,
      double var12
   ) {
      Vec3d var14 = new Vec3d(var3.doubleVal, var3.doubleVal4 + 0.3, var3.doubleVal3);
      double[] var15 = Matrix4fUtils.doubleArrayOf(var14);
      if (var15 != null) {
         float var16 = (float)(var15[0] * var6);
         float var17 = (float)(var15[1] * var6);
         double var18 = Math.sqrt(var3.doubleVal5);
         float var21 = (float)Math.min(1.0, var18 / Math.sqrt(var12));
         float var22 = var21 * var21 * (3.0F - 2.0F * var21);
         float var20 = Math.max(0.2F, 1.0F - var22);
         ItemStack var62 = var3.itemESPModuleInner1.class1799;
         String var63 = var62.getName().getString();
         int var23 = var3.intVal2;
         String var24 = var23 > 1 && this.showStackCount.getValue() ? "x" + var23 : null;
         String var25 = var18 <= var11 ? String.format("%.0fm", var18) : null;
         int var26 = var4 ? intOf(var62) : this.flatAccentColor.getValue();
         boolean var27 = var2 != null;
         float var30 = 0.0F;
         if (var27) {
            ImGui.pushFont(var2, var8);
         }

         float var28;
         float var29;
         try {
            var28 = ImGui.calcTextSize(var63).x;
            var29 = ImGui.getTextLineHeight();
            if (var24 != null) {
               var30 = ImGui.calcTextSize(var24).x;
            }
         } finally {
            if (var27) {
               ImGui.popFont();
            }
         }

         float var31 = 0.0F;
         float var32 = 0.0F;
         if (var25 != null) {
            if (var27) {
               ImGui.pushFont(var2, var9);
            }

            try {
               var31 = ImGui.calcTextSize(var25).x;
               var32 = ImGui.getTextLineHeight();
            } finally {
               if (var27) {
                  ImGui.popFont();
               }
            }
         }

         float var33 = 4.0F;
         float var34 = var28 + (var24 != null ? 4.0F + var30 : 0.0F);
         float var35 = Math.max(var34, var31);
         float var36 = var29 + (var25 != null ? 2.0F + var32 : 0.0F);
         float var38 = 9.0F + 6.0F + var35;
         float var39 = 8.0F + var36;
         float var40 = var16 - var38 * 0.5F;
         float var41 = var17 - var39 - 2.0F * var5;
         float var42 = var40 + var38;
         float var43 = var41 + var39;
         if (this.premiumStyle.getValue()) {
            int[] var44 = new int[]{37, 21};
            int[] var45 = new int[]{2, 4};

            for (int var46 = 0; var46 < 2; var46++) {
               int var47 = intOf4(var44[var46] << 24, var20);
               float var48 = var46;
               var1.addRectFilled(var40 - var48, var41 + var45[var46] - var48, var42 + var48, var43 + var45[var46] + var48, var47, var10 + var48);
            }
         }

         int var64 = Math.round(204.0F * var20);
         int var65 = intOf4(var64 << 24 | 1184795, 1.0F);
         var1.addRectFilled(var40, var41, var42, var43, var65, var10);
         float var66 = var40 + 1.0F;
         float var67 = var41 + 1.0F;
         float var68 = var66 + 3.0F;
         float var49 = var43 - 1.0F;
         if (this.accentGlow.getValue()) {
            int var50 = var26 & 16777215;

            for (int var51 = 3; var51 >= 1; var51--) {
               int var52 = Math.round((var26 >>> 24 & 0xFF) * (0.3F / var51));
               if (var52 > 0) {
                  int var53 = intOf4(var52 << 24 | var50, var20);
                  float var54 = var51 * 1.0F;
                  var1.addRectFilled(var66 - var54, var67 - var54, var68 + var54, var49 + var54, var53, var10 * 0.4F + var54, 80);
               }
            }
         }

         int var69 = intOf4(var26, var20);
         var1.addRectFilled(var66, var67, var68, var49, var69, var10 * 0.4F, 80);
         float var70 = var40 + 9.0F;
         float var71 = var41 + 4.0F;
         int var72 = intOf4(-986379, var20);
         run5(var1, var2, var70, var71, var72, var63, var8);
         if (var24 != null) {
            run5(var1, var2, var70 + var28 + 4.0F, var71, intOf4(var26, var20), var24, var8);
         }

         if (var25 != null) {
            int var73 = intOf4(-8617329, var20);
            float var55 = var42 - 6.0F - var31;
            run5(var1, var2, var55, var71 + var29 + 2.0F, var73, var25, var9);
         }
      }
   }

   private static int intOf(ItemStack var0) {
      try {
         Integer var1 = getMap().get(var0.getItem());
         if (var1 != null) {
            return var1;
         } else {
            Rarity var2 = var0.getRarity();
            if (var2 == null) {
               return -6511697;
            } else {
               return switch (var2) {
                  case COMMON -> -6511697;
                  case UNCOMMON -> -9967367;
                  case RARE -> -10556716;
                  case EPIC -> -5796870;
                  default -> throw new MatchException(null, null);
               };
            }
         }
      } catch (Throwable var3) {
         return -6511697;
      }
   }

   private static void run5(ImDrawList var0, ImFont var1, float var2, float var3, int var4, String var5, float var6) {
      int var7 = (int)var6;
      if (var1 != null) {
         var0.addText(var1, var7, var2, var3, var4, var5);
      } else {
         var0.addText(var2, var3, var4, var5);
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

   private static int intOf2(ItemESPModule.Inner2 var0, ItemESPModule.Inner2 var1) {
      return Double.compare(var1.doubleVal5, var0.doubleVal5);
   }

   private Boolean getBoolean() {
      return !this.rarityColors.getValue();
   }

final static class Inner1 {
   final ItemEntity class1542;
   final ItemStack class1799;
   final double doubleVal;

   Inner1(ItemEntity var1, ItemStack var2, double var3) {
      this.class1542 = var1;
      this.class1799 = var2;
      this.doubleVal = var3;
   }
}

final static class Inner2 {
   final ItemESPModule.Inner1 itemESPModuleInner1;
   int intVal = 1;
   int intVal2;
   double doubleVal;
   double doubleVal2;
   double doubleVal3;
   double doubleVal4;
   double doubleVal5;

   Inner2(ItemESPModule.Inner1 var1) {
      this.itemESPModuleInner1 = var1;
      this.intVal2 = var1.class1799.getCount();
      this.doubleVal = var1.class1542.getX();
      this.doubleVal2 = var1.class1542.getY();
      this.doubleVal3 = var1.class1542.getZ();
      this.doubleVal4 = var1.class1542.getBoundingBox().maxY;
      this.doubleVal5 = var1.doubleVal;
   }

   void run(ItemESPModule.Inner1 var1) {
      this.intVal++;
      this.intVal2 = this.intVal2 + var1.class1799.getCount();
      double var2 = this.intVal;
      this.doubleVal = this.doubleVal + (var1.class1542.getX() - this.doubleVal) / var2;
      this.doubleVal2 = this.doubleVal2 + (var1.class1542.getY() - this.doubleVal2) / var2;
      this.doubleVal3 = this.doubleVal3 + (var1.class1542.getZ() - this.doubleVal3) / var2;
      double var4 = var1.class1542.getBoundingBox().maxY;
      if (var4 > this.doubleVal4) {
         this.doubleVal4 = var4;
      }

      this.doubleVal5 = this.doubleVal5 + (var1.doubleVal - this.doubleVal5) / var2;
   }
}
}

