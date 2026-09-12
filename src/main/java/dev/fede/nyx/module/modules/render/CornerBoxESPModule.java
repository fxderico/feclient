package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.CornerBoxESPModuleUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CornerBoxESPModule extends Module {
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting mobs = new BooleanSetting("Mobs", true);
   private final BooleanSetting self = new BooleanSetting("Self", false);
   private final ColorSetting playerColor = new ColorSetting("PlayerColor", -53200);
   private final ColorSetting mobColor = new ColorSetting("MobColor", -24576);
   private final ColorSetting selfColor = new ColorSetting("SelfColor", -12874753);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 2.0, 0.5, 5.0, 0.1);
   private final NumberSetting cornerLen = new NumberSetting("CornerLength", 0.2, 0.05, 0.5, 0.01);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final BooleanSetting distanceFade = new BooleanSetting("DistanceFade", true);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 64.0, 8.0, 256.0, 1.0);
   private final NumberSetting fadeFloor = new NumberSetting("FadeFloor", 0.15, 0.0, 1.0, 0.05);

   public CornerBoxESPModule() {
      super("CornerBoxESP", "Corner-only bounding boxes (Battlebit / Warzone-style ESP)", Category.RENDER);
      this.run6(
         new Setting[]{
            this.players,
            this.mobs,
            this.self,
            this.playerColor,
            this.mobColor,
            this.selfColor,
            this.lineWidth,
            this.cornerLen,
            this.throughWalls,
            this.distanceFade,
            this.maxDistance,
            this.fadeFloor
         }
      );
      this.maxDistance.visibleWhen(this.distanceFade::getValue);
      this.fadeFloor.visibleWhen(this.distanceFade::getValue);
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         float var3 = this.lineWidth.getValueFloat();
         float var4 = this.cornerLen.getValueFloat();
         boolean var5 = this.throughWalls.getValue();
         boolean var6 = this.distanceFade.getValue();
         double var7 = this.maxDistance.getValue();
         double var9 = var7 * var7;
         float var11 = this.fadeFloor.getValueFloat();
         Vec3d var12 = class310.player.getEyePos();

         for (Entity var14 : class310.world.getEntities()) {
            if (var14 instanceof LivingEntity var15 && var15.isAlive()) {
               boolean var16 = var15 == class310.player || var15 == class310.getCameraEntity();
               int var17;
               if (var16) {
                  if (!this.self.getValue()) {
                     continue;
                  }

                  var17 = this.selfColor.getValue();
               } else if (var15 instanceof PlayerEntity) {
                  if (!this.players.getValue()) {
                     continue;
                  }

                  var17 = this.playerColor.getValue();
               } else {
                  if (!this.mobs.getValue()) {
                     continue;
                  }

                  var17 = this.mobColor.getValue();
               }

               double var18 = var15.squaredDistanceTo(var12.x, var12.y, var12.z);
               int var20;
               if (var6) {
                  if (var18 >= var9 && var11 <= 0.0F) {
                     continue;
                  }

                  var20 = CornerBoxESPModuleUtil.intOf2(var17, var18, var7, var11);
               } else {
                  var20 = var17;
               }

               if ((var20 >>> 24 & 0xFF) != 0) {
                  Box var21 = this.class238Of(var15, var2);
                  ListUtils.run6(var21, var20, var3, var4, var5);
               }
            }
         }
      }
   }

   private Box class238Of(LivingEntity var1, float var2) {
      double var3 = MathHelper.lerp(var2, var1.lastRenderX, var1.getX());
      double var5 = MathHelper.lerp(var2, var1.lastRenderY, var1.getY());
      double var7 = MathHelper.lerp(var2, var1.lastRenderZ, var1.getZ());
      Vec3d var9 = var1.getEntityPos();
      return var1.getBoundingBox().offset(var3 - var9.x, var5 - var9.y, var7 - var9.z);
   }
}

