package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import imgui.ImDrawList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

public class RadarModule extends Module {
   public static RadarModule radarModule;
   private final NumberSetting worldRadius = new NumberSetting("Radius", 48.0, 8.0, 256.0, 1.0);
   private final NumberSetting widgetSize = new NumberSetting("Size", 118.0, 60.0, 240.0, 1.0);
   private final NumberSetting posX = new NumberSetting("PosX", 12.0, 0.0, 3840.0, 1.0);
   private final NumberSetting posY = new NumberSetting("PosY", 12.0, 0.0, 2160.0, 1.0);
   private final BooleanSetting showPlayers = new BooleanSetting("Players", true);
   private final BooleanSetting showHostile = new BooleanSetting("Hostile", true);
   private final BooleanSetting showPassive = new BooleanSetting("Passive", false);
   private final BooleanSetting showItems = new BooleanSetting("Items", false);
   private final ColorSetting playerColor = new ColorSetting("PlayerColor", -37748);
   private final ColorSetting hostileColor = new ColorSetting("HostileColor", -23990);
   private final ColorSetting passiveColor = new ColorSetting("PassiveColor", -12985974);
   private final ColorSetting itemColor = new ColorSetting("ItemColor", -14006);
   private final ColorSetting backgroundColor = new ColorSetting("Background", -1342177280);
   private final ColorSetting borderColor = new ColorSetting("Border", -9663233);
   private final NumberSetting alpha = new NumberSetting("Alpha", 235.0, 0.0, 255.0, 1.0);
   private final BooleanSetting showGrid = new BooleanSetting("Grid", true);
   private final BooleanSetting northArrow = new BooleanSetting("NorthArrow", true);
   private static final int intVal = -9663233;
   private static final int intVal2 = 452984831;
   private static final int intVal3 = -1342177280;
   private static final int intVal4 = 64;

   public RadarModule() {
      super("Radar", "Top-down mini-map disc, drawn via ImGui", Category.RENDER);
      this.run6(
         new Setting[]{
            this.worldRadius,
            this.widgetSize,
            this.posX,
            this.posY,
            this.showPlayers,
            this.showHostile,
            this.showPassive,
            this.showItems,
            this.playerColor,
            this.hostileColor,
            this.passiveColor,
            this.itemColor,
            this.backgroundColor,
            this.borderColor,
            this.alpha,
            this.showGrid,
            this.northArrow
         }
      );
      radarModule = this;
   }

   @Override
   public void run4(DrawContext var1, float var2) {
   }

   public void run(ImDrawList var1, MinecraftClient var2) {
      if (var2 != null && var2.world != null && var2.player != null) {
         int var3 = intOf((int)Math.round(this.alpha.getValue()));
         if (var3 != 0) {
            float var4 = var3 / 255.0F;
            float var5 = Math.max(40.0F, (float)this.widgetSize.getValue());
            float var6 = var5 * 0.5F;
            float var7 = (float)this.posX.getValue() + var6;
            float var8 = (float)this.posY.getValue() + var6;
            float var9 = var2.getRenderTickCounter().getTickProgress(false);
            double var10 = MathHelper.lerp(var9, var2.player.lastRenderX, var2.player.getX());
            double var12 = MathHelper.lerp(var9, var2.player.lastRenderZ, var2.player.getZ());
            float var14 = MathHelper.lerp(var9, var2.player.lastYaw, var2.player.getYaw());
            double var15 = Math.toRadians(var14);
            double var17 = Math.sin(var15);
            double var19 = Math.cos(var15);
            double var21 = this.worldRadius.getValue();
            double var23 = var21 * var21;
            double var25 = var21 > 0.0 ? var6 / var21 : 0.0;
            var1.addCircleFilled(var7, var8, var6, intOf4(this.backgroundColor.getValue(), var4), 64);
            var1.addCircle(var7, var8, var6, intOf4(this.borderColor.getValue(), var4), 64, 1.4F);
            if (this.showGrid.getValue()) {
               int var27 = intOf4(452984831, var4);
               float var28 = 3.0F;
               var1.addLine(var7 - var6 + var28, var8, var7 + var6 - var28, var8, var27, 1.0F);
               var1.addLine(var7, var8 - var6 + var28, var7, var8 + var6 - var28, var27, 1.0F);
            }

            if (var25 > 0.0) {
               int var45 = intOf4(-1342177280, var4);

               for (Entity var29 : var2.world.getEntities()) {
                  if (var29 != var2.player && var29 != var2.getCameraEntity() && var29.isAlive()) {
                     int var30;
                     if (var29 instanceof PlayerEntity) {
                        if (!this.showPlayers.getValue()) {
                           continue;
                        }

                        var30 = this.playerColor.getValue();
                     } else if (var29 instanceof Monster) {
                        if (!this.showHostile.getValue()) {
                           continue;
                        }

                        var30 = this.hostileColor.getValue();
                     } else if (var29 instanceof LivingEntity) {
                        if (!this.showPassive.getValue()) {
                           continue;
                        }

                        var30 = this.passiveColor.getValue();
                     } else {
                        if (!(var29 instanceof ItemEntity) || !this.showItems.getValue()) {
                           continue;
                        }

                        var30 = this.itemColor.getValue();
                     }

                     double var31 = MathHelper.lerp(var9, var29.lastRenderX, var29.getX());
                     double var33 = MathHelper.lerp(var9, var29.lastRenderZ, var29.getZ());
                     double var35 = var31 - var10;
                     double var37 = var33 - var12;
                     if (!(var35 * var35 + var37 * var37 > var23)) {
                        double var39 = -var35 * var19 - var37 * var17;
                        double var41 = var35 * var17 - var37 * var19;
                        float var43 = var7 + (float)(var39 * var25);
                        float var44 = var8 + (float)(var41 * var25);
                        var1.addCircleFilled(var43, var44, 3.2F, var45, 12);
                        var1.addCircleFilled(var43, var44, 2.2F, intOf4(var30, var4), 12);
                     }
                  }
               }
            }

            int var46 = 1349291263;
            var1.addCircleFilled(var7, var8, 4.2F, intOf4(var46, var4), 16);
            var1.addCircleFilled(var7, var8, 2.4F, intOf4(-9663233, var4), 16);
            if (this.northArrow.getValue()) {
               this.run2(var1, var7, var8, var6, var17, var19, var4);
            }
         }
      }
   }

   private void run2(ImDrawList var1, float var2, float var3, float var4, double var5, double var7, float var9) {
      double var14 = -var7;
      float var18 = var4 + 5.0F;
      float var19 = var4 + 1.0F;
      float var21 = var2 + (float)(var5 * var18);
      float var22 = var3 + (float)(var7 * var18);
      float var23 = var2 + (float)(var5 * var19);
      float var24 = var3 + (float)(var7 * var19);
      float var25 = var23 + (float)(var14 * 4.0F);
      float var26 = var24 + (float)(var5 * 4.0F);
      float var27 = var23 - (float)(var14 * 4.0F);
      float var28 = var24 - (float)(var5 * 4.0F);
      var1.addTriangleFilled(var21, var22, var25, var26, var27, var28, intOf4(-9663233, var9));
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

   private static int intOf(int var0) {
      if (var0 < 0) {
         return 0;
      } else {
         return var0 > 255 ? 255 : var0;
      }
   }
}

