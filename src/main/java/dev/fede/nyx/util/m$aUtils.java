package dev.fede.nyx.util;

import dev.fede.nyx.mixin.ClientPlayerInteractionManagerAccessor;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class m$aUtils {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();
   private static BlockPos class2338;
   private static Direction class2350;
   private static volatile long longVal = 0L;
   private static volatile long longVal2 = 0L;
   public static final long longVal3 = 500L;
   public static final long longVal4 = 60L;
   private static final Map<Long, m$aUtils.Inner1> map = new ConcurrentHashMap<>();
   private static final long longVal5 = 30000L;
   private static int intVal = 0;
   private static final int intVal2 = 128;

   public static void run() {
      longVal = System.currentTimeMillis();
   }

   public static boolean isEnabled2() {
      return System.currentTimeMillis() - longVal < 500L;
   }

   private m$aUtils() {
   }

   public static void run2(BlockPos var0, BlockState var1) {
      if (var0 != null && var1 != null) {
         long var2 = System.currentTimeMillis();
         map.put(var0.asLong(), new m$aUtils.Inner1(var1, var2));
         if (++intVal >= 128) {
            intVal = 0;
            run4(var2);
         }
      }
   }

   public static m$aUtils.Inner1 maOf(BlockPos var0) {
      if (var0 == null) {
         return null;
      } else {
         m$aUtils.Inner1 var1 = map.get(var0.asLong());
         if (var1 == null) {
            return null;
         } else if (System.currentTimeMillis() - var1.timestampMs() > 30000L) {
            map.remove(var0.asLong());
            return null;
         } else {
            return var1;
         }
      }
   }

   public static void run3(BlockPos var0) {
      if (var0 != null) {
         map.remove(var0.asLong());
      }
   }

   private static void run4(long var0) {
      long var2 = var0 - 30000L;
      map.entrySet().removeIf(_e -> false);
   }

   public static boolean check(BlockPos var0, Direction var1, boolean var2) {
      ClientPlayerEntity var3 = class310.player;
      ClientPlayerInteractionManager var4 = class310.interactionManager;
      if (var3 == null || class310.world == null || var4 == null || var0 == null || var1 == null) {
         return false;
      } else if (AutoTunnelHelper.getk().isEnabled2() && isEnabled2()) {
         return false;
      } else {
         if (AutoTunnelHelper.getk().isEnabled2() && !var0.equals(class2338)) {
            ClientPlayerInteractionManagerAccessor var5 = (ClientPlayerInteractionManagerAccessor)var4;
            BlockPos var6 = var5.nyx$getCurrentBreakingPos();
            float var7 = var5.nyx$getCurrentBreakingProgress();
            if (var6 != null && var7 > 0.0F && !class310.world.getBlockState(var6).isAir()) {
               var4.cancelBlockBreaking();
               class2338 = null;
               class2350 = null;
               return true;
            }
         }

         if (class2338 != null && class310.world.getBlockState(class2338).isAir()) {
            class2338 = null;
            class2350 = null;
         }

         float[] var15 = floatArrayOf(var0, var1);
         float var16 = var15[0];
         float var17 = var15[1];
         float var8 = var3.getYaw();
         float var9 = var3.getPitch();
         var3.setYaw(var16);
         var3.setPitch(var17);
         AntiAFKModuleUtil.run(var16, var17);
         boolean var10 = var0.equals(class2338);
         if (!var10) {
            ClientPlayerInteractionManagerAccessor var11 = (ClientPlayerInteractionManagerAccessor)var4;
            BlockPos var12 = var11.nyx$getCurrentBreakingPos();
            float var13 = var11.nyx$getCurrentBreakingProgress();
            boolean var14 = var12 != null && var13 > 0.0F && !class310.world.getBlockState(var12).isAir();
            if (var14) {
               var4.cancelBlockBreaking();
            }

            var4.attackBlock(var0, var1);
            class2338 = var0.toImmutable();
            class2350 = var1;
         } else if (AutoTunnelHelper.getk().isEnabled2()) {
            ClientPlayerInteractionManagerAccessor var18 = (ClientPlayerInteractionManagerAccessor)var4;
            BlockPos var19 = var18.nyx$getCurrentBreakingPos();
            boolean var20 = var0.equals(var19) && var18.nyx$getCurrentBreakingProgress() > 0.0F;
            if (!var20) {
               var4.updateBlockBreakingProgress(var0, var1);
            }
         } else {
            var4.updateBlockBreakingProgress(var0, var1);
         }

         if (var2) {
            var3.setYaw(var8);
            var3.setPitch(var9);
         }

         return true;
      }
   }

   public static boolean check2(BlockPos var0, Direction var1) {
      ClientPlayerEntity var2 = class310.player;
      ClientPlayerInteractionManager var3 = class310.interactionManager;
      if (var2 == null || class310.world == null || var3 == null || var0 == null || var1 == null) {
         return false;
      } else if (AutoTunnelHelper.getk().isEnabled2() && isEnabled2()) {
         return false;
      } else {
         if (AutoTunnelHelper.getk().isEnabled2() && !var0.equals(class2338)) {
            ClientPlayerInteractionManagerAccessor var4 = (ClientPlayerInteractionManagerAccessor)var3;
            BlockPos var5 = var4.nyx$getCurrentBreakingPos();
            float var6 = var4.nyx$getCurrentBreakingProgress();
            if (var5 != null && var6 > 0.0F && !class310.world.getBlockState(var5).isAir()) {
               var3.cancelBlockBreaking();
               class2338 = null;
               class2350 = null;
               return true;
            }
         }

         if (class2338 != null && class310.world.getBlockState(class2338).isAir()) {
            class2338 = null;
            class2350 = null;
         }

         boolean var9 = var0.equals(class2338);
         if (!var9) {
            ClientPlayerInteractionManagerAccessor var10 = (ClientPlayerInteractionManagerAccessor)var3;
            BlockPos var12 = var10.nyx$getCurrentBreakingPos();
            float var7 = var10.nyx$getCurrentBreakingProgress();
            boolean var8 = var12 != null && var7 > 0.0F && !class310.world.getBlockState(var12).isAir();
            if (var8) {
               var3.cancelBlockBreaking();
            }

            var3.attackBlock(var0, var1);
            class2338 = var0.toImmutable();
            class2350 = var1;
         } else if (AutoTunnelHelper.getk().isEnabled2()) {
            ClientPlayerInteractionManagerAccessor var11 = (ClientPlayerInteractionManagerAccessor)var3;
            BlockPos var13 = var11.nyx$getCurrentBreakingPos();
            boolean var14 = var0.equals(var13) && var11.nyx$getCurrentBreakingProgress() > 0.0F;
            if (!var14) {
               var3.updateBlockBreakingProgress(var0, var1);
            }
         } else {
            var3.updateBlockBreakingProgress(var0, var1);
         }

         return true;
      }
   }

   public static boolean check3(BlockPos var0, Direction var1) {
      ClientPlayerInteractionManager var2 = class310.interactionManager;
      if (var2 != null && var0 != null && var1 != null) {
         boolean var3 = var2.updateBlockBreakingProgress(var0, var1);
         if (class310.world != null && var0.equals(class2338) && class310.world.getBlockState(var0).isAir()) {
            class2338 = null;
            class2350 = null;
         }

         return var3;
      } else {
         return false;
      }
   }

   public static void run5() {
      ClientPlayerInteractionManager var0 = class310.interactionManager;
      class2338 = null;
      class2350 = null;
      if (var0 != null) {
         var0.cancelBlockBreaking();
      }
   }

   @Deprecated
   public static void run6(BlockPos var0, Direction var1) {
      run5();
   }

   public static float[] floatArrayOf(BlockPos var0, Direction var1) {
      Vec3d var2 = Vec3d.ofCenter(var0).add(var1.getOffsetX() * 0.5, var1.getOffsetY() * 0.5, var1.getOffsetZ() * 0.5);
      ClientPlayerEntity var3 = class310.player;
      Vec3d var4 = var3 != null ? var3.getEyePos() : Vec3d.ZERO;
      double var5 = var2.x - var4.x;
      double var7 = var2.y - var4.y;
      double var9 = var2.z - var4.z;
      double var11 = Math.sqrt(var5 * var5 + var9 * var9);
      float var13 = (float)Math.toDegrees(Math.atan2(var9, var5)) - 90.0F;
      float var14 = (float)(-Math.toDegrees(Math.atan2(var7, var11)));
      return new float[]{MathHelper.wrapDegrees(var13), MathHelper.clamp(var14, -90.0F, 90.0F)};
   }

   public static Direction class2350Of(BlockPos var0) {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null && var0 != null && class310.world != null) {
         Vec3d var2 = var1.getEyePos();
         Vec3d var3 = Vec3d.ofCenter(var0);
         BlockHitResult var4 = class310.world.raycast(new RaycastContext(var2, var3, ShapeType.OUTLINE, FluidHandling.NONE, var1));
         if (var4 != null && var4.getType() == Type.BLOCK && var4.getBlockPos().equals(var0)) {
            return var4.getSide();
         } else {
            Direction var5 = null;
            double var6 = Double.MAX_VALUE;

            for (Direction var11 : Direction.values()) {
               BlockPos var12 = var0.offset(var11);
               BlockState var13 = class310.world.getBlockState(var12);
               if (!var13.isOpaqueFullCube()) {
                  Vec3d var14 = var3.add(var11.getOffsetX() * 0.5, var11.getOffsetY() * 0.5, var11.getOffsetZ() * 0.5);
                  double var15 = var14.squaredDistanceTo(var2);
                  if (var15 < var6) {
                     var6 = var15;
                     var5 = var11;
                  }
               }
            }

            return var5 != null ? var5 : var1.getHorizontalFacing().getOpposite();
         }
      } else {
         return Direction.UP;
      }
   }

   private static boolean check8(long var0, Entry var2) {
      return true;
   }

public record Inner1(BlockState state, long timestampMs) {

}
}

