package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import dev.fede.nyx.util.AutoCrystalModuleUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoCrystalModule extends Module {
   private static final double doubleVal = 6.0;
   private static final double doubleVal2 = 12.0;
   private static final double doubleVal3 = 0.5;
   private final BooleanSetting placeMode = new BooleanSetting("Place", true);
   private final BooleanSetting breakMode = new BooleanSetting("Break", true);
   private final BooleanSetting antiSuicide = new BooleanSetting("AntiSuicide", true);
   private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
   private final NumberSetting placeRange = new NumberSetting("PlaceRange", 5.0, 3.0, 6.0, 0.1);
   private final NumberSetting breakRange = new NumberSetting("BreakRange", 5.0, 3.0, 6.0, 0.1);
   private final NumberSetting placeDelay = new NumberSetting("PlaceDelay", 50.0, 0.0, 500.0, 10.0);
   private final NumberSetting breakDelay = new NumberSetting("BreakDelay", 50.0, 0.0, 500.0, 10.0);
   private final NumberSetting minTargetHealth = new NumberSetting("MinTargetHealth", 20.0, 1.0, 40.0, 1.0);
   private final NumberSetting maxSelfDamage = new NumberSetting("MaxSelfDamage", 5.0, 0.0, 20.0, 0.5);
   private final NumberSetting minTargetDamage = new NumberSetting("MinTargetDamage", 4.0, 0.0, 20.0, 0.5);
   private final BooleanSetting renderPredictions = new BooleanSetting("RenderPredictions", true);
   private final ColorSetting placeBox = new ColorSetting("PlaceBox", -2147418368);
   private final ColorSetting breakBox = new ColorSetting("BreakBox", -2130771968);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private final AntiVoidModuleHelper antiVoidModuleHelper2 = new AntiVoidModuleHelper();
   private LivingEntity class1309;
   private BlockPos class2338;

   public AutoCrystalModule() {
      // renamed from "AutoCrystal" — that collided (different exact string,
      // so register()'s dedup missed it) with native AutoCrystalModule's
      // "Auto Crystal", a genuinely different tool: native is an RMB-held
      // manual place+break macro, this one is a fully autonomous aura that
      // places/detonates on its own. Both are real, both stay — renamed so
      // the GUI doesn't look like it has a broken duplicate.
      super("Crystal Aura", "Places and detonates end crystals on nearby targets", Category.COMBAT);
      this.placeBox.visibleWhen(this.renderPredictions::getValue);
      this.breakBox.visibleWhen(this.renderPredictions::getValue);
      this.maxSelfDamage.visibleWhen(this.antiSuicide::getValue);
      this.run6(
         new Setting[]{
            this.placeMode,
            this.breakMode,
            this.antiSuicide,
            this.rotate,
            this.placeRange,
            this.breakRange,
            this.placeDelay,
            this.breakDelay,
            this.minTargetHealth,
            this.maxSelfDamage,
            this.minTargetDamage,
            this.renderPredictions,
            this.placeBox,
            this.breakBox
         }
      );
   }

   @Override
   public void run2() {
      this.class1309 = null;
      this.class2338 = null;
      AntiAFKModuleUtil.run2();
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         this.class1309 = this.getclass1309();
         if (this.class1309 == null) {
            AntiAFKModuleUtil.run2();
         } else {
            if (this.breakMode.getValue()) {
               this.run4();
            }

            if (this.placeMode.getValue()) {
               this.run5();
            }
         }
      }
   }

   private LivingEntity getclass1309() {
      PlayerEntity var1 = null;
      double var2 = Double.MAX_VALUE;
      double var4 = Math.max(this.placeRange.getValue(), this.breakRange.getValue()) + 12.0;
      double var6 = this.minTargetHealth.getValue();

      for (Entity var9 : class310.world.getEntities()) {
         if (var9 instanceof PlayerEntity var10 && var9 != class310.player && var9.isAlive() && !var10.isCreative() && !var10.isSpectator()) {
            double var11 = var10.getHealth() + var10.getAbsorptionAmount();
            if (!(var11 <= 0.0) && !(var11 > var6)) {
               double var13 = class310.player.distanceTo(var10);
               if (!(var13 > var4) && var13 < var2) {
                  var2 = var13;
                  var1 = var10;
               }
            }
         }
      }

      return var1;
   }

   private void run4() {
      if (this.antiVoidModuleHelper2.check2(this.breakDelay.getValue())) {
         double var1 = this.breakRange.getValue();
         EndCrystalEntity var5 = null;
         double var6 = -1.0;

         for (Entity var9 : class310.world.getEntities()) {
            if (var9 instanceof EndCrystalEntity var10 && var10.isAlive() && !(class310.player.distanceTo(var10) > var1)) {
               double var11 = this.class1309.getEntityPos().distanceTo(var10.getEntityPos());
               if (!(var11 > 12.0)) {
                  double var13 = doubleOf(this.class1309, var10.getEntityPos().add(0.0, 0.5, 0.0));
                  if (!(var13 <= 0.0) && var13 > var6) {
                     var6 = var13;
                     var5 = var10;
                  }
               }
            }
         }

         if (var5 != null) {
            if (this.renderPredictions.getValue()) {
               ListUtils.run5(var5.getBoundingBox(), this.breakBox.getValue(), 1.5F, true);
            }

            if (this.rotate.getValue()) {
               float[] var15 = AutoCrystalModuleUtil.floatArrayOf2(var5);
               AntiAFKModuleUtil.run(var15[0], var15[1]);
            }

            class310.interactionManager.attackEntity(class310.player, var5);
            class310.player.swingHand(Hand.MAIN_HAND);
            this.antiVoidModuleHelper2.run();
         }
      }
   }

   private void run5() {
      if (this.antiVoidModuleHelper.check2(this.placeDelay.getValue())) {
         Hand var1 = this.getclass1268();
         if (var1 != null) {
            BlockPos var2 = null;
            double var3 = -1.0;
            double var5 = this.placeRange.getValue();
            double var7 = var5 * var5;
            double var9 = this.minTargetDamage.getValue();
            double var11 = this.maxSelfDamage.getValue();
            boolean var13 = this.antiSuicide.getValue();
            BlockPos var14 = class310.player.getBlockPos();
            int var15 = (int)Math.ceil(var5);
            Vec3d var16 = class310.player.getEyePos();

            for (int var17 = -var15; var17 <= var15; var17++) {
               for (int var18 = -var15; var18 <= var15; var18++) {
                  for (int var19 = -var15; var19 <= var15; var19++) {
                     BlockPos var20 = var14.add(var17, var18, var19);
                     if (this.check(var20)) {
                        Vec3d var21 = new Vec3d(var20.getX() + 0.5, var20.getY() + 1.0 + 0.5, var20.getZ() + 0.5);
                        if (!(var16.squaredDistanceTo(var21) > var7)) {
                           double var22 = doubleOf(this.class1309, var21);
                           if (!(var22 < var9)) {
                              if (var13) {
                                 double var24 = doubleOf(class310.player, var21);
                                 if (var24 > var11) {
                                    continue;
                                 }
                              }

                              if (var22 > var3) {
                                 var3 = var22;
                                 var2 = var20;
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (var2 != null) {
               Vec3d var26 = new Vec3d(var2.getX() + 0.5, var2.getY() + 1.0, var2.getZ() + 0.5);
               BlockHitResult var27 = new BlockHitResult(var26, Direction.UP, var2, false);
               if (this.renderPredictions.getValue()) {
                  BlockPos var28 = var2.up();
                  Box var30 = new Box(var28.getX(), var28.getY(), var28.getZ(), var28.getX() + 1.0, var28.getY() + 2.0, var28.getZ() + 1.0);
                  ListUtils.run5(var30, this.placeBox.getValue(), 1.5F, true);
               }

               if (this.rotate.getValue()) {
                  float[] var29 = AutoCrystalModuleUtil.floatArrayOf(var26);
                  AntiAFKModuleUtil.run(var29[0], var29[1]);
               }

               class310.interactionManager.interactBlock(class310.player, var1, var27);
               class310.player.swingHand(var1);
               this.class2338 = var2.up();
               this.antiVoidModuleHelper.run();
            }
         }
      }
   }

   private boolean check(BlockPos var1) {
      if (class310.world == null) {
         return false;
      } else {
         Block var2 = class310.world.getBlockState(var1).getBlock();
         if (var2 != Blocks.OBSIDIAN && var2 != Blocks.BEDROCK) {
            return false;
         } else {
            BlockPos var3 = var1.up();
            BlockPos var4 = var1.up(2);
            if (class310.world.getBlockState(var3).isAir() && class310.world.getBlockState(var4).isAir()) {
               Box var5 = new Box(var3.getX(), var3.getY(), var3.getZ(), var3.getX() + 1.0, var3.getY() + 2.0, var3.getZ() + 1.0);
               return class310.world.getOtherEntities(null, var5).isEmpty();
            } else {
               return false;
            }
         }
      }
   }

   private Hand getclass1268() {
      if (class310.player == null) {
         return null;
      } else if (class310.player.getMainHandStack().isOf(Items.END_CRYSTAL)) {
         return Hand.MAIN_HAND;
      } else {
         return class310.player.getOffHandStack().isOf(Items.END_CRYSTAL) ? Hand.OFF_HAND : null;
      }
   }

   private static double doubleOf(Entity var0, Vec3d var1) {
      double var2 = var0.getEntityPos().distanceTo(var1);
      if (var2 > 12.0) {
         return 0.0;
      } else {
         double var4 = 1.0 - var2 / 12.0;
         return (var4 * var4 + var4) / 2.0 * 7.0 * 6.0 + 1.0;
      }
   }

   @Override
   public String getString3() {
      return this.class1309 != null ? "§7" + this.class1309.getName().getString() : null;
   }
}

