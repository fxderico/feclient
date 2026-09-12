package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.auth.AuthGate;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.Matrix4fUtils;
import imgui.ImDrawList;
import imgui.ImGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ESP extends Module {
   public static ESP eSP;
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting mobs = new BooleanSetting("Mobs", true);
   private final BooleanSetting animals = new BooleanSetting("Animals", false);
   private final BooleanSetting self = new BooleanSetting("Self", false);
   private final ColorSetting playerColor = new ColorSetting("PlayerColor", -53200);
   private final ColorSetting mobColor = new ColorSetting("MobColor", -24576);
   private final ColorSetting animalColor = new ColorSetting("AnimalColor", -12976364);
   private final ModeSetting style = new ModeSetting("Style", "Box", "Box", "Filled", "Corner");
   private final NumberSetting lineThickness = new NumberSetting("LineThickness", 1.5, 0.5, 4.0, 0.1);
   private final BooleanSetting outlineShadow = new BooleanSetting("OutlineShadow", true);
   private final NumberSetting fillAlpha = new NumberSetting("FillAlpha", 45.0, 0.0, 200.0, 1.0);
   private final NumberSetting cornerFraction = new NumberSetting("CornerFraction", 0.28, 0.1, 0.5, 0.01);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 64.0, 0.0, 256.0, 1.0);

   public ESP() {
      super("ESP", "Draws boxes around entities", Category.RENDER);
      eSP = this;
      this.run6(
         new Setting[]{
            this.players,
            this.mobs,
            this.animals,
            this.self,
            this.playerColor,
            this.mobColor,
            this.animalColor,
            this.style,
            this.lineThickness,
            this.outlineShadow,
            this.fillAlpha,
            this.cornerFraction,
            this.maxDistance
         }
      );
      this.fillAlpha.visibleWhen(this::getBoolean2);
      this.cornerFraction.visibleWhen(this::getBoolean);
   }

   public void run3(MinecraftClient var1) {
      if (var1 != null && var1.world != null && var1.player != null && var1.getWindow() != null) {
         ImDrawList var2 = ImGui.getForegroundDrawList();
         double var3 = var1.getWindow().getScaleFactor();
         double var5 = this.maxDistance.getValue();
         double var7 = var5 * var5;
         float var9 = this.lineThickness.getValueFloat();
         boolean var10 = this.outlineShadow.getValue();
         String var11 = this.style.getMode();
         boolean var12 = "Filled".equals(var11);
         boolean var13 = "Corner".equals(var11);
         int var14 = this.fillAlpha.getValueInt();
         float var15 = this.cornerFraction.getValueFloat();
         int var16 = (int)(
            AuthGate.longOf2(
                  (-6608597426955400448L | 237L)
                     + (
                        6317717722780323886L
                              + -8088988110696313565L
                              + 4612066480591418021L
                              + 8281662902993321256L
                              - ((6317717722780323886L & 8486904765961386416L * 8707498663055314397L) << 1)
                           ^ -8083738004107460663L
                     )
                     + 5296232778321563643L
               )
               ^ (
                     -6186937160898522570L
                        ^ (9079078667850123543L | -5308159240077025727L)
                           + (9079078667850123543L & (6904449669863790231L ^ -1620124683270944554L))
                           + (8701247506549449608L ^ -1868514520252407160L)
                  )
                  + ((-6186937160898522570L & -3231974101696845736L) << 1)
                  + (
                     6109591170348323958L
                           + -6860250640512520674L
                           - (
                              (
                                    (-8375000446917010619L ^ 2588221030699759487L)
                                          + ((-8375000446917010619L & 2588221030699759487L) << 1)
                                          + -6550373487143976526L
                                       & -6860250640512520674L
                                 )
                                 << 1
                           )
                        ^ -8214892702049797266L
                  )
         );
         boolean var17 = this.self.getValue();

         for (Entity var19 : var1.world.getEntities()) {
            if (var19 instanceof LivingEntity var20 && var19.isAlive() && (var17 || var19 != var1.player && var19 != var1.getCameraEntity())) {
               boolean var21 = var19 instanceof PlayerEntity
                  ? this.players.getValue()
                  : (var19 instanceof Monster ? this.mobs.getValue() : this.animals.getValue());
               if (var21 && (!(var5 > 0.0) || !(var1.player.squaredDistanceTo(var19) > var7))) {
                  int var22 = (
                        var19 instanceof PlayerEntity
                           ? this.playerColor.getValue()
                           : (var19 instanceof Monster ? this.mobColor.getValue() : this.animalColor.getValue())
                     )
                     ^ var16;
                  Box var23 = this.class238Of(var20, floatOf(var1));
                  Vec3d[] var24 = new Vec3d[]{
                     new Vec3d(var23.minX, var23.minY, var23.minZ),
                     new Vec3d(var23.minX, var23.minY, var23.maxZ),
                     new Vec3d(var23.maxX, var23.minY, var23.minZ),
                     new Vec3d(var23.maxX, var23.minY, var23.maxZ),
                     new Vec3d(var23.minX, var23.maxY, var23.minZ),
                     new Vec3d(var23.minX, var23.maxY, var23.maxZ),
                     new Vec3d(var23.maxX, var23.maxY, var23.minZ),
                     new Vec3d(var23.maxX, var23.maxY, var23.maxZ)
                  };
                  double var25 = Double.MAX_VALUE;
                  double var27 = Double.MAX_VALUE;
                  double var29 = -Double.MAX_VALUE;
                  double var31 = -Double.MAX_VALUE;
                  boolean var33 = false;

                  for (Vec3d var37 : var24) {
                     double[] var38 = Matrix4fUtils.doubleArrayOf(var37);
                     if (var38 != null) {
                        var33 = true;
                        if (var38[0] < var25) {
                           var25 = var38[0];
                        }

                        if (var38[1] < var27) {
                           var27 = var38[1];
                        }

                        if (var38[0] > var29) {
                           var29 = var38[0];
                        }

                        if (var38[1] > var31) {
                           var31 = var38[1];
                        }
                     }
                  }

                  if (var33) {
                     float var41 = (float)(var25 * var3);
                     float var42 = (float)(var27 * var3);
                     float var43 = (float)(var29 * var3);
                     float var44 = (float)(var31 * var3);
                     int var45 = intOf4(var22, 1.0F);
                     int var39 = var10 ? intOf4(-1342177280, 1.0F) : 0;
                     if (var13) {
                        run2(var2, var41, var42, var43, var44, var45, var39, var9, var15, var10);
                     } else {
                        if (var12) {
                           int var40 = var22 & 16777215 | (Math.max(0, Math.min(255, var14)) & 0xFF) << 24;
                           var2.addRectFilled(var41, var42, var43, var44, intOf4(var40, 1.0F));
                        }

                        run(var2, var41, var42, var43, var44, var45, var39, var9, var10);
                     }
                  }
               }
            }
         }
      }
   }

   private static void run(ImDrawList var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7, boolean var8) {
      if (var8) {
         var0.addRect(var1 - 1.0F, var2 - 1.0F, var3 + 1.0F, var4 + 1.0F, var6, 0.0F, 0, var7 + 2.0F);
      }

      var0.addRect(var1, var2, var3, var4, var5, 0.0F, 0, var7);
   }

   private static void run2(ImDrawList var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7, float var8, boolean var9) {
      float var10 = var3 - var1;
      float var11 = var4 - var2;
      if (!(var10 < 2.0F) && !(var11 < 2.0F)) {
         float var12 = MathHelper.clamp(Math.min(var10, var11) * var8, 6.0F, 32.0F);
         if (var12 * 2.0F > Math.min(var10, var11)) {
            var12 = Math.min(var10, var11) * 0.49F;
         }

         if (var9) {
            float var13 = var7 + 2.0F;
            var0.addLine(var1, var2, var1 + var12, var2, var6, var13);
            var0.addLine(var1, var2, var1, var2 + var12, var6, var13);
            var0.addLine(var3 - var12, var2, var3, var2, var6, var13);
            var0.addLine(var3, var2, var3, var2 + var12, var6, var13);
            var0.addLine(var1, var4, var1 + var12, var4, var6, var13);
            var0.addLine(var1, var4 - var12, var1, var4, var6, var13);
            var0.addLine(var3 - var12, var4, var3, var4, var6, var13);
            var0.addLine(var3, var4 - var12, var3, var4, var6, var13);
         }

         var0.addLine(var1, var2, var1 + var12, var2, var5, var7);
         var0.addLine(var1, var2, var1, var2 + var12, var5, var7);
         var0.addLine(var3 - var12, var2, var3, var2, var5, var7);
         var0.addLine(var3, var2, var3, var2 + var12, var5, var7);
         var0.addLine(var1, var4, var1 + var12, var4, var5, var7);
         var0.addLine(var1, var4 - var12, var1, var4, var5, var7);
         var0.addLine(var3 - var12, var4, var3, var4, var5, var7);
         var0.addLine(var3, var4 - var12, var3, var4, var5, var7);
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

   private static float floatOf(MinecraftClient var0) {
      return var0.getRenderTickCounter().getTickProgress(true);
   }

   private Box class238Of(LivingEntity var1, float var2) {
      double var3 = MathHelper.lerp(var2, var1.lastRenderX, var1.getX());
      double var5 = MathHelper.lerp(var2, var1.lastRenderY, var1.getY());
      double var7 = MathHelper.lerp(var2, var1.lastRenderZ, var1.getZ());
      Vec3d var9 = var1.getEntityPos();
      return var1.getBoundingBox().offset(var3 - var9.x, var5 - var9.y, var7 - var9.z);
   }

   private Boolean getBoolean() {
      return "Corner".equals(this.style.getMode());
   }

   private Boolean getBoolean2() {
      return "Filled".equals(this.style.getMode());
   }
}

