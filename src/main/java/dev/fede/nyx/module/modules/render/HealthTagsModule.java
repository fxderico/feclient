package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.Matrix4fUtils;
import java.util.Objects;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3x2fStack;

public class HealthTagsModule extends Module {
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting mobs = new BooleanSetting("Mobs", true);
   private final BooleanSetting self = new BooleanSetting("Self", false);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 96.0, 4.0, 256.0, 1.0);
   private final NumberSetting scale = new NumberSetting("Scale", 1.5, 0.5, 4.0, 0.1);
   private final ModeSetting mode = new ModeSetting("RenderMode", "Hearts", "Hearts", "Bar", "Text");
   private static final int intVal = 40;
   private static final int intVal2 = 4;
   private static final int intVal3 = -1072689136;
   private static final int intVal4 = -16777216;
   private static final int intVal5 = -12976364;
   private static final int intVal6 = -932849;
   private static final int intVal7 = -1618884;
   private static final int intVal8 = -50373;
   private static final int intVal9 = -2143272896;
   private static final float floatVal = 1.0E-4F;

   public HealthTagsModule() {
      super("HealthTags", "Row of heart icons / bar / text above living entities", Category.RENDER);
      this.run6(new Setting[]{this.players, this.mobs, this.self, this.maxDistance, this.scale, this.mode});
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null && class310.textRenderer != null) {
         TextRenderer var3 = class310.textRenderer;
         Vec3d var4 = class310.player.getEntityPos();
         double var5 = this.maxDistance.getValue() * this.maxDistance.getValue();
         float var7 = (float)Math.max(1.0E-4F, this.scale.getValue());
         boolean var8 = this.self.getValue();

         for (Entity var10 : class310.world.getEntities()) {
            if (var10 instanceof LivingEntity var11
               && var10.isAlive()
               && (var8 || var10 != class310.player && var10 != class310.getCameraEntity())
               && this.check(var10)
               && !(var10.getEntityPos().squaredDistanceTo(var4) > var5)) {
               Vec3d var12 = var10.getEntityPos().add(0.0, var10.getHeight() + 0.4, 0.0);
               double[] var13 = Matrix4fUtils.doubleArrayOf(var12);
               if (var13 != null) {
                  this.run(var1, var3, var11, var13[0], var13[1], var7);
               }
            }
         }
      }
   }

   private boolean check(Entity var1) {
      if (var1 instanceof PlayerEntity) {
         return this.players.getValue();
      } else {
         return !(var1 instanceof HostileEntity) && !(var1 instanceof Monster) && !(var1 instanceof PassiveEntity)
            ? this.mobs.getValue()
            : this.mobs.getValue();
      }
   }

   private void run(DrawContext var1, TextRenderer var2, LivingEntity var3, double var4, double var6, float var8) {
      float var9 = Math.max(0.0F, var3.getHealth());
      float var10 = Math.max(1.0E-4F, var3.getMaxHealth());
      float var11 = Math.min(1.0F, var9 / var10);
      Matrix3x2fStack var12 = var1.getMatrices();
      var12.pushMatrix();

      try {
         var12.translate((float)var4, (float)var6);
         var12.scale(var8, var8);
         String var13 = this.mode.getMode();
         switch (var13.hashCode()) {
            case 66547:
               if (var13.equals("Bar")) {
                  run2(var1, var11);
                  return;
               }
               break;
            case 2603341:
               if (var13.equals("Text")) {
                  run3(var1, var2, var9, var10, var11);
                  return;
               }
         }

         run5(var1, var2, var9, var10);
      } finally {
         var12.popMatrix();
      }
   }

   private static void run2(DrawContext var0, float var1) {
      var0.fill(-21, -7, 21, -1, -16777216);
      var0.fill(-20, -6, 20, -2, -1072689136);
      int var6 = -20 + Math.round(40 * var1);
      if (var6 > -20) {
         var0.fill(-20, -6, var6, -2, intOf(var1));
      }
   }

   private static void run3(DrawContext var0, TextRenderer var1, float var2, float var3, float var4) {
      String var5 = String.format("%.0f/%.0f", var2, var3);
      int var6 = var1.getWidth(var5);
      int var7 = -var6 / 2;
      Objects.requireNonNull(var1);
      var0.drawTextWithShadow(var1, var5, var7, -11, intOf(var4));
   }

   private static void run5(DrawContext var0, TextRenderer var1, float var2, float var3) {
      int var4 = Math.min(10, Math.max(1, (int)Math.ceil(var3 / 2.0)));
      int var5 = Math.min(var4, (int)Math.round(var2 / 2.0));
      int var7 = var1.getWidth("❤");
      int var9 = var4 * var7 + (var4 - 1);
      int var10 = -var9 / 2;

      for (int var12 = 0; var12 < var4; var12++) {
         int var13 = var12 < var5 ? -50373 : -2143272896;
         var0.drawTextWithShadow(var1, "❤", var10 + var12 * (var7 + 1), -11, var13);
      }
   }

   private static int intOf(float var0) {
      if (var0 > 0.66F) {
         return -12976364;
      } else {
         return var0 > 0.33F ? -932849 : -1618884;
      }
   }
}

