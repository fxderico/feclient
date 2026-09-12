package dev.fede.nyx.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class HumanMotionSim {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();
   private static boolean bool = false;
   private static boolean bool2 = false;
   private static boolean bool3 = false;
   private static boolean bool4 = false;
   private static BlockPos class2338 = null;
   private static int intVal = 0;
   private static BlockPos class23382 = null;
   private static Direction class2350 = null;

   private HumanMotionSim() {
   }

   public static int getInt() {
      return intVal;
   }

   public static BlockPos getclass2338() {
      return class23382;
   }

   public static Direction getclass2350() {
      return class2350;
   }

   public static BlockPos getclass23382() {
      return class2338;
   }

   public static Vec3d class243Of(double var0) {
      ClientPlayerEntity var2 = class310.player;
      if (var2 == null) {
         return null;
      } else {
         Vec3d var3 = var2.getEntityPos();
         float var4 = (float)Math.toRadians(var2.getYaw());
         double var5 = -MathHelper.sin(var4);
         double var7 = MathHelper.cos(var4);
         return new Vec3d(var3.x + var5 * var0, var3.y, var3.z + var7 * var0);
      }
   }

   public static HumanMotionSim.StepResult humanMotionSimStepResultOf(Vec3d var0, double var1, double var3, int var5) {
      return humanMotionSimStepResultOf3(var0, var1, var3, var5, 1, false);
   }

   public static HumanMotionSim.StepResult humanMotionSimStepResultOf2(Vec3d var0, double var1, double var3, int var5, int var6) {
      return humanMotionSimStepResultOf3(var0, var1, var3, var5, Math.max(1, var6), true);
   }

   private static HumanMotionSim.StepResult humanMotionSimStepResultOf3(Vec3d var0, double var1, double var3, int var5, int var6, boolean var7) {
      ClientPlayerEntity var8 = class310.player;
      ClientWorld var9 = class310.world;
      GameOptions var10 = class310.options;
      if (var8 != null && var9 != null && var10 != null && var0 != null) {
         intVal = 0;
         class23382 = null;
         class2350 = null;
         Vec3d var11 = var8.getEntityPos();
         double var12 = var0.x - var11.x;
         double var14 = var0.z - var11.z;
         double var16 = var12 * var12 + var14 * var14;
         if (var16 <= var1 * var1) {
            class2338 = null;
            run3();
            return HumanMotionSim.StepResult.ARRIVED;
         } else {
            float var18 = MathHelper.wrapDegrees((float)(Math.atan2(-var12, var14) * (180.0 / Math.PI)));
            int var19 = Math.max(1, var5);
            float var20 = MathHelper.lerpAngleDegrees(1.0F / var19, var8.getYaw(), var18);
            AntiAFKModuleUtil.run(var20, 0.0F);
            float var21 = Math.abs(MathHelper.wrapDegrees(var18 - var20));
            if (var21 > var3) {
               run5(false);
               run8(false);
               run(false);
               return HumanMotionSim.StepResult.ADVANCING;
            } else {
               double var22 = 1.0 / Math.sqrt(var16);
               double var24 = var12 * var22;
               double var26 = var14 * var22;
               BlockPos var28 = BlockPos.ofFloored(var11.x + var24 * 0.5, var11.y, var11.z + var26 * 0.5);
               BlockPos var29 = BlockPos.ofFloored(var11.x + var24 * 0.7, var11.y, var11.z + var26 * 0.7);
               BlockState var30 = var9.getBlockState(var28);
               BlockState var31 = var9.getBlockState(var28.up());
               boolean var32 = var30.isSolid() && !var30.isReplaceable();
               boolean var33 = var31.isSolid() && !var31.isReplaceable();
               if (var32 && var33) {
                  class2338 = var28.up();
                  run5(false);
                  run8(false);
                  run(false);
                  return HumanMotionSim.StepResult.MINE_THROUGH;
               } else if (var32) {
                  class2338 = null;
                  run8(true);
                  run5(true);
                  run(true);
                  return HumanMotionSim.StepResult.ADVANCING;
               } else {
                  run8(false);
                  if (var8.isOnGround()) {
                     int var34 = var6 + 8;
                     int var35 = 0;

                     for (int var36 = 1; var36 <= var34; var35 = var36++) {
                        BlockState var37 = var9.getBlockState(var29.down(var36));
                        if (!var37.isAir() && var37.getFluidState().isEmpty()) {
                           break;
                        }
                     }

                     if (var35 > var6) {
                        intVal = var35;
                        Direction var38 = class2350Of(var24, var26);
                        BlockPos var39 = var8.getBlockPos().down();
                        class23382 = var39;
                        class2350 = var38;
                        class2338 = null;
                        run5(false);
                        run(false);
                        return var7 ? HumanMotionSim.StepResult.FALL_HAZARD : HumanMotionSim.StepResult.EDGE_STOP;
                     }
                  }

                  class2338 = null;
                  run5(true);
                  run(true);
                  return HumanMotionSim.StepResult.ADVANCING;
               }
            }
         }
      } else {
         run3();
         return HumanMotionSim.StepResult.NO_WORLD;
      }
   }

   public static void run3() {
      run5(false);
      run8(false);
      run(false);
      run2(false);
   }

   private static void run5(boolean var0) {
      if (var0 != bool) {
         GameOptions var1 = class310.options;
         if (var1 != null) {
            var1.forwardKey.setPressed(var0);
            bool = var0;
         }
      }
   }

   private static void run8(boolean var0) {
      if (var0 != bool2) {
         GameOptions var1 = class310.options;
         if (var1 != null) {
            var1.jumpKey.setPressed(var0);
            bool2 = var0;
         }
      }
   }

   private static void run(boolean var0) {
      if (var0 != bool3) {
         GameOptions var1 = class310.options;
         if (var1 != null) {
            var1.sprintKey.setPressed(var0);
            bool3 = var0;
         }
      }
   }

   private static void run2(boolean var0) {
      if (var0 != bool4) {
         GameOptions var1 = class310.options;
         if (var1 != null) {
            var1.sneakKey.setPressed(var0);
            bool4 = var0;
         }
      }
   }

   private static Direction class2350Of(double var0, double var2) {
      if (Math.abs(var0) >= Math.abs(var2)) {
         return var0 >= 0.0 ? Direction.EAST : Direction.WEST;
      } else {
         return var2 >= 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   public static enum StepResult {
      ADVANCING,
      ARRIVED,
      EDGE_STOP,
      FALL_HAZARD,
      BLOCKED,
      MINE_THROUGH,
      NO_WORLD;

      private static final HumanMotionSim.StepResult[] humanMotionSimStepResultArray = getHumanMotionSimStepResultArray();

      private static HumanMotionSim.StepResult[] getHumanMotionSimStepResultArray() {
         return new HumanMotionSim.StepResult[]{ADVANCING, ARRIVED, EDGE_STOP, FALL_HAZARD, BLOCKED, MINE_THROUGH, NO_WORLD};
      }
   }
}

