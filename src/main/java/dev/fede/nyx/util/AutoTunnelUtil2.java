package dev.fede.nyx.util;
import net.minecraft.client.util.InputUtil;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.mixin.ClientPlayerInteractionManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;

public final class AutoTunnelUtil2 {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();
   private static int intVal = 0;
   private static long longVal = 0L;
   private static int intVal2 = 0;
   private static int intVal3 = 0;
   private static long longVal2 = System.currentTimeMillis();
   private static boolean bool = false;
   private static long longVal3 = -1L;
   private static long longVal4 = -1L;

   private AutoTunnelUtil2() {
   }

   public static void run() {
      longVal++;
      if (intVal > 0) {
         intVal--;
      }

      long var0 = System.currentTimeMillis();
      if (var0 - longVal2 >= 1000L) {
         intVal3 = intVal2;
         intVal2 = 0;
         longVal2 = var0;
      }
   }

   public static int getInt2() {
      return intVal3;
   }

   public static boolean isEnabled8() {
      return intVal > 0;
   }

   public static void run6(int var0) {
      if (var0 < 1) {
         var0 = 1;
      }

      if (var0 > intVal) {
         intVal = var0;
      }
   }

   public static boolean check(String var0, BlockPos var1) {
      if (intVal > 0) {
         run2(var0);
         return false;
      } else if (var1 != null && check2(var1)) {
         run2(var0);
         int var5 = AutoTunnelHelper.getk().intOf3(0, 2);
         intVal = 1 + var5;
         return false;
      } else {
         boolean var2 = InputUtil$class_306Utils.check4(var0);
         if (var2 && !bool) {
            bool = true;
            intVal2++;
            if (longVal3 == longVal) {
               try {
                  NyxClient.LOGGER.warn("[AttackChannel] double-press in tick {} (owner={})", longVal, var0);
               } catch (Throwable var4) {
               }
            }

            longVal3 = longVal;
         }

         return var2;
      }
   }

   public static void run2(String var0) {
      if (InputUtil$class_306Utils.check3(var0)) {
         if (bool) {
            InputUtil$class_306Utils.check5(var0);
            bool = false;
            intVal2++;
            longVal4 = longVal;
         }
      }
   }

   public static void run3(String var0) {
      run2(var0);
      InputUtil$class_306Utils.run3(var0);
   }

   private static boolean check2(BlockPos var0) {
      try {
         ClientPlayerInteractionManager var1 = class310.interactionManager;
         if (var1 != null && class310.world != null) {
            ClientPlayerInteractionManagerAccessor var2 = (ClientPlayerInteractionManagerAccessor)var1;
            BlockPos var3 = var2.nyx$getCurrentBreakingPos();
            float var4 = var2.nyx$getCurrentBreakingProgress();
            if (var3 == null) {
               return false;
            } else if (var4 <= 0.0F) {
               return false;
            } else {
               return class310.world.getBlockState(var3).isAir() ? false : !var3.equals(var0);
            }
         } else {
            return false;
         }
      } catch (Throwable var5) {
         return false;
      }
   }
}

