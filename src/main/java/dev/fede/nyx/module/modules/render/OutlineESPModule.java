package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.ChatFilterHelper;
import dev.fede.nyx.util.CodeEngineScreenUtil2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class OutlineESPModule extends Module {
   public static volatile OutlineESPModule outlineESPModule;
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting mobs = new BooleanSetting("Mobs", false);
   private final BooleanSetting hostile = new BooleanSetting("Hostile", true);
   private final BooleanSetting passive = new BooleanSetting("Passive", false);
   private final BooleanSetting self = new BooleanSetting("Self", false);
   private final ColorSetting playerColor = new ColorSetting("PlayerColor", -53200);
   private final ColorSetting mobColor = new ColorSetting("MobColor", -24576);
   private final ColorSetting friendColor = new ColorSetting("FriendColor", -12976364);
   private final ModeSetting style = new ModeSetting("Style", "Both", "Glow", "Outline", "Both");
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 2.0, 1.0, 4.0, 0.1);
   private final NumberSetting alpha = new NumberSetting("Alpha", 200.0, 0.0, 255.0, 1.0);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 0.0, 0.0, 512.0, 1.0);

   public OutlineESPModule() {
      super("OutlineESP", "Thick coloured silhouette on filtered entities (glow / line-box / both)", Category.RENDER);
      this.run6(
         new Setting[]{
            this.players,
            this.mobs,
            this.hostile,
            this.passive,
            this.self,
            this.playerColor,
            this.mobColor,
            this.friendColor,
            this.style,
            this.lineWidth,
            this.alpha,
            this.throughWalls,
            this.maxDistance
         }
      );
      this.hostile.visibleWhen(this::getBoolean4);
      this.passive.visibleWhen(this::getBoolean3);
      this.lineWidth.visibleWhen(this::getBoolean2);
      this.throughWalls.visibleWhen(this::getBoolean);
      outlineESPModule = this;
   }

   @Override
   public String getString3() {
      return this.style.getMode();
   }

   public static boolean check(Entity var0) {
      OutlineESPModule var1 = outlineESPModule;
      if (var1 == null || !var1.isEnabled3() || var0 == null) {
         return false;
      } else {
         return var1.style.check("Outline") ? false : var1.check2(var0);
      }
   }

   public static int intOf(Entity var0) {
      OutlineESPModule var1 = outlineESPModule;
      if (var1 == null || !var1.isEnabled3() || var0 == null) {
         return -1;
      } else if (var1.style.check("Outline")) {
         return -1;
      } else {
         return !var1.check2(var0) ? -1 : var1.intOf2(var0) & 16777215;
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         if (!this.style.check("Glow")) {
            int var3 = intOf3((int)Math.round(this.alpha.getValue()));
            if (var3 != 0) {
               boolean var4 = this.throughWalls.getValue();
               float var5 = this.lineWidth.getValueFloat();

               for (Entity var7 : class310.world.getEntities()) {
                  if (var7 instanceof LivingEntity var8 && var8.isAlive() && this.check2(var8)) {
                     int var9 = this.intOf2(var8);
                     Box var10 = this.class238Of(var8, var2);
                     ListUtils.run5(var10, CodeEngineScreenUtil2.intOf3(var9, var3), var5, var4);
                  }
               }
            }
         }
      }
   }

   private boolean check2(Entity var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.world != null && var2.player != null) {
         if (!(var1 instanceof LivingEntity var3 && var3.isAlive())) {
            return false;
         } else if (var3 != var2.player && var3 != var2.getCameraEntity()) {
            double var4 = this.maxDistance.getValue();
            if (var4 > 0.0) {
               double var6 = var2.player.squaredDistanceTo(var3);
               if (var6 > var4 * var4) {
                  return false;
               }
            }

            if (var3 instanceof PlayerEntity) {
               return this.players.getValue();
            } else if (!this.mobs.getValue()) {
               return false;
            } else if (var3 instanceof Monster) {
               return this.hostile.getValue();
            } else {
               return var3 instanceof PassiveEntity ? this.passive.getValue() : this.passive.getValue();
            }
         } else {
            return this.self.getValue();
         }
      } else {
         return false;
      }
   }

   private int intOf2(Entity var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var1 instanceof PlayerEntity var3) {
         if (ChatFilterHelper.chatFilterHelper.check3(var3.getGameProfile().name())) {
            return this.friendColor.getValue();
         } else {
            return var2.player != null && var2.player.isTeammate(var3) ? this.friendColor.getValue() : this.playerColor.getValue();
         }
      } else {
         return this.mobColor.getValue();
      }
   }

   private Box class238Of(LivingEntity var1, float var2) {
      double var3 = MathHelper.lerp(var2, var1.lastRenderX, var1.getX());
      double var5 = MathHelper.lerp(var2, var1.lastRenderY, var1.getY());
      double var7 = MathHelper.lerp(var2, var1.lastRenderZ, var1.getZ());
      Vec3d var9 = var1.getEntityPos();
      return var1.getBoundingBox().offset(var3 - var9.x, var5 - var9.y, var7 - var9.z);
   }

   private static int intOf3(int var0) {
      if (var0 < 0) {
         return 0;
      } else {
         return var0 > 255 ? 255 : var0;
      }
   }

   private Boolean getBoolean() {
      return !this.style.check("Glow");
   }

   private Boolean getBoolean2() {
      return !this.style.check("Glow");
   }

   private Boolean getBoolean3() {
      return this.mobs.getValue();
   }

   private Boolean getBoolean4() {
      return this.mobs.getValue();
   }
}

