package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class HitboxesModule extends Module {
   private final NumberSetting playerScale = new NumberSetting("PlayerScale", 1.0, 0.5, 3.0, 0.1);
   private final NumberSetting mobScale = new NumberSetting("MobScale", 1.0, 0.5, 3.0, 0.1);
   private final BooleanSetting renderExpanded = new BooleanSetting("RenderExpanded", true);
   private final BooleanSetting targetsFriendly = new BooleanSetting("TargetsFriendly", false);
   private static final int intVal = 1090519039;
   private static final float floatVal = 1.0F;
   private static final boolean bool = false;
   private static volatile boolean bool2;
   private static volatile double doubleVal;
   private static volatile double doubleVal2;
   private static volatile boolean bool3;

   public HitboxesModule() {
      super("Hitboxes", "Expands entity hitboxes for easier hit-registration", Category.COMBAT);
      this.run6(new Setting[]{this.playerScale, this.mobScale, this.renderExpanded, this.targetsFriendly});
   }

   @Override
   public void run() {
      this.run5();
   }

   @Override
   public void run2() {
      bool2 = false;
      doubleVal = 1.0;
      doubleVal2 = 1.0;
      bool3 = false;
   }

   @Override
   public void run3() {
      this.run5();
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (this.renderExpanded.getValue()) {
         if (class310.world != null && class310.player != null) {
            for (Entity var4 : class310.world.getEntities()) {
               if (doubleOf(var4) != 1.0) {
                  Box var5 = var4.getBoundingBox();
                  if (var5 != null) {
                     ListUtils.run5(var5, 1090519039, 1.0F, false);
                  }
               }
            }
         }
      }
   }

   private void run5() {
      bool2 = true;
      doubleVal = this.playerScale.getValue();
      doubleVal2 = this.mobScale.getValue();
      bool3 = this.targetsFriendly.getValue();
   }

   public static boolean isEnabled_s() {
      return bool2;
   }

   public static double doubleOf(Entity var0) {
      if (!bool2) {
         return 1.0;
      } else if (var0 == null) {
         return 1.0;
      } else {
         World var1 = var0.getEntityWorld();
         if (var1 != null && var1.isClient()) {
            MinecraftClient var2 = MinecraftClient.getInstance();
            if (var2 == null || var0 == var2.player) {
               return 1.0;
            } else if (var0.isRemoved() || !var0.isAlive()) {
               return 1.0;
            } else if (!(var0 instanceof LivingEntity)) {
               return 1.0;
            } else if (var0 instanceof PlayerEntity) {
               return doubleVal;
            } else if (var0 instanceof Monster) {
               return doubleVal2;
            } else {
               return bool3 ? doubleVal2 : 1.0;
            }
         } else {
            return 1.0;
         }
      }
   }

   public static Box class238Of(Box var0, Entity var1) {
      if (var0 == null) {
         return null;
      } else {
         double var2 = doubleOf(var1);
         if (var2 == 1.0) {
            return var0;
         } else {
            double var4 = (var0.maxX - var0.minX) * 0.5 * (var2 - 1.0);
            double var6 = (var0.maxY - var0.minY) * 0.5 * (var2 - 1.0);
            double var8 = (var0.maxZ - var0.minZ) * 0.5 * (var2 - 1.0);
            return var0.expand(var4, var6, var8);
         }
      }
   }

   public static double getDouble() {
      if (!bool2) {
         return 0.0;
      } else {
         double var0 = Math.max(doubleVal, doubleVal2);
         return Math.max(0.0, (var0 - 1.0) * 4.0);
      }
   }

   @Override
   public String getString3() {
      if (!bool2) {
         return null;
      } else {
         double var1 = Math.max(doubleVal, doubleVal2);
         return var1 == 1.0 ? null : "§7" + String.format("%.1fx", var1);
      }
   }
}

