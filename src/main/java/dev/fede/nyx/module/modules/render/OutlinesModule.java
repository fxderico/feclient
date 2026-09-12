package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.ChatFilterHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public class OutlinesModule extends Module {
   public static volatile OutlinesModule outlinesModule;
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting hostile = new BooleanSetting("Hostile", false);
   private final BooleanSetting passive = new BooleanSetting("Passive", false);
   private final BooleanSetting items = new BooleanSetting("Items", false);
   private final BooleanSetting self = new BooleanSetting("Self", false);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 128.0, 16.0, 512.0, 1.0);
   private final BooleanSetting teamColor = new BooleanSetting("TeamColor", true);
   private final ColorSetting customColor = new ColorSetting("CustomColor", -9459492);
   private final ColorSetting friendColor = new ColorSetting("FriendColor", -13710223);
   private final NumberSetting brightness = new NumberSetting("Brightness", 1.0, 0.3, 2.5, 0.05);
   private final NumberSetting alpha = new NumberSetting("Alpha", 255.0, 100.0, 255.0, 1.0);
   private final NumberSetting thickness = new NumberSetting("Thickness", 1.0, 1.0, 4.0, 1.0);

   public OutlinesModule() {
      super("Outlines", "Force + strengthen the vanilla entity outline glow on filtered entities", Category.RENDER);
      this.run6(
         new Setting[]{
            this.players,
            this.hostile,
            this.passive,
            this.items,
            this.self,
            this.maxDistance,
            this.teamColor,
            this.customColor,
            this.friendColor,
            this.brightness,
            this.alpha,
            this.thickness
         }
      );
      this.customColor.visibleWhen(this::getBoolean2);
      this.friendColor.visibleWhen(this::getBoolean);
      outlinesModule = this;
   }

   public static boolean check(Entity var0) {
      OutlinesModule var1 = outlinesModule;
      return var1 != null && var1.isEnabled3() && var0 != null ? var1.check2(var0) : false;
   }

   public static int intOf(Entity var0, int var1) {
      OutlinesModule var2 = outlinesModule;
      if (var2 == null || !var2.isEnabled3() || var0 == null) {
         return -1;
      } else {
         return !var2.check2(var0) ? -1 : var2.intOf3(var0, var1);
      }
   }

   public static int intOf2(Entity var0) {
      return intOf(var0, 16777215);
   }

   private boolean check2(Entity var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.world != null && var2.player != null && var1 != null) {
         double var3 = this.maxDistance.getValue();
         if (var2.player.squaredDistanceTo(var1) > var3 * var3) {
            return false;
         } else if (var1 == var2.player || var1 == var2.getCameraEntity()) {
            return this.self.getValue();
         } else if (var1 instanceof PlayerEntity) {
            return this.players.getValue();
         } else if (var1 instanceof ItemEntity) {
            return this.items.getValue();
         } else if (var1 instanceof LivingEntity var5) {
            if (!var5.isAlive()) {
               return false;
            } else if (var5 instanceof Monster) {
               return this.hostile.getValue();
            } else {
               return var5 instanceof PassiveEntity ? this.passive.getValue() : this.passive.getValue();
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private int intOf3(Entity var1, int var2) {
      MinecraftClient var3 = MinecraftClient.getInstance();
      int var4;
      if (this.teamColor.getValue()) {
         var4 = var2;
      } else if (var1 == var3.player) {
         var4 = this.friendColor.getValue();
      } else if (var1 instanceof PlayerEntity var5) {
         boolean var6 = ChatFilterHelper.chatFilterHelper.check3(var5.getGameProfile().name()) || var3.player != null && var3.player.isTeammate(var5);
         var4 = var6 ? this.friendColor.getValue() : this.customColor.getValue();
      } else {
         var4 = this.customColor.getValue();
      }

      double var11 = this.brightness.getValue();
      int var7 = intOf4((int)Math.round((var4 >>> 16 & 0xFF) * var11));
      int var8 = intOf4((int)Math.round((var4 >>> 8 & 0xFF) * var11));
      int var9 = intOf4((int)Math.round((var4 & 0xFF) * var11));
      int var10 = this.alpha.getValueInt() & 0xFF;
      return var10 << 24 | var7 << 16 | var8 << 8 | var9;
   }

   private static int intOf4(int var0) {
      return var0 < 0 ? 0 : (var0 > 255 ? 255 : var0);
   }

   private Boolean getBoolean() {
      return !this.teamColor.getValue();
   }

   private Boolean getBoolean2() {
      return !this.teamColor.getValue();
   }
}

