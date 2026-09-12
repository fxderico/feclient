package dev.fede.nyx.tracker;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public final class MapUtils {
   public static final int intVal = 0;
   public static final int intVal2 = 1;
   public static final int intVal3 = 2;
   public static final int intVal4 = 3;
   public static final int intVal5 = 4;
   public static final int intVal6 = 5;
   public static final int intVal7 = 6;
   public static final int intVal8 = 7;
   private static final int[] intArray = new int[]{4, 6, 2, 5, 8, 1, 12};
   private static final int intVal9 = 150;
   private static final int intVal10 = 500;
   private static final int intVal11 = 1;
   private static final int intVal12 = 200;
   private static final int intVal13 = 1;
   private static final int intVal14 = 5;
   private static final int intVal15 = 64;
   private static final double doubleVal = 0.15;
   private static volatile boolean bool = false;
   private static volatile boolean[] boolArray = new boolean[]{true, true, true, true, true, true, true};
   private static boolean bool2 = false;
   private static int intVal16 = 0;
   private static final Set<Long> set = ConcurrentHashMap.newKeySet();
   private static final Set<Long> set2 = ConcurrentHashMap.newKeySet();
   private static final ConcurrentHashMap<Long, int[]> concurrentHashMap = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Long, Double> concurrentHashMap2 = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Long, Integer> concurrentHashMap3 = new ConcurrentHashMap<>();
   private static final ConcurrentHashMap<Long, Set<BlockPos>> concurrentHashMap4 = new ConcurrentHashMap<>();
   private static final ArrayDeque<WorldChunk> arrayDeque = new ArrayDeque<>();

   public static long longOf(int var0, int var1) {
      return (long)var0 << 32 | var1 & 4294967295L;
   }

   public static int intOf(long var0) {
      return (int)(var0 >> 32);
   }

   public static int intOf2(long var0) {
      return (int)var0;
   }

   private MapUtils() {
   }

   public static void run5(boolean var0) {
      bool = var0;
      run2();
      if (!var0) {
         arrayDeque.clear();
         set2.clear();
      }
   }

   public static boolean isEnabled() {
      return bool;
   }

   public static void run(boolean[] var0) {
      if (var0 != null && var0.length == 7) {
         boolArray = Arrays.copyOf(var0, 7);
         run8();
      }
   }

   public static boolean[] getBoolArray() {
      return Arrays.copyOf(boolArray, 7);
   }

   public static Map<Long, Integer> getMap() {
      return new HashMap<>(concurrentHashMap3);
   }

   public static Map<Long, int[]> getMap2() {
      HashMap var0 = new HashMap(concurrentHashMap.size());

      for (Entry var2 : concurrentHashMap.entrySet()) {
         var0.put((Long)var2.getKey(), Arrays.copyOf((int[])var2.getValue(), 7));
      }

      return var0;
   }

   public static Map<Long, Set<BlockPos>> getMap3() {
      HashMap var0 = new HashMap(concurrentHashMap4.size());

      for (Entry var2 : concurrentHashMap4.entrySet()) {
         var0.put((Long)var2.getKey(), new HashSet((Collection)var2.getValue()));
      }

      return var0;
   }

   public static void run4() {
      arrayDeque.clear();
      set2.clear();
      set.clear();
      concurrentHashMap.clear();
      concurrentHashMap2.clear();
      concurrentHashMap3.clear();
      concurrentHashMap4.clear();
   }

   private static synchronized void run2() {
      if (!bool2) {
         bool2 = true;
         ClientChunkEvents.CHUNK_LOAD.register(MapUtils::run9);
         ClientChunkEvents.CHUNK_UNLOAD.register(MapUtils::run16);
         ClientTickEvents.END_CLIENT_TICK.register(MapUtils::run6);
      }
   }

   private static void run3(WorldChunk var0) {
      long var1 = longOf(var0.getPos().x, var0.getPos().z);
      if (!set.contains(var1) && !set2.contains(var1)) {
         set2.add(var1);
         arrayDeque.add(var0);
      }
   }

   private static void run6(MinecraftClient var0) {
      if (bool && var0 != null && var0.world != null) {
         if (++intVal16 >= 200) {
            intVal16 = 0;
            concurrentHashMap3.replaceAll(MapUtils::integerOf11);
            concurrentHashMap3.entrySet().removeIf(MapUtils::check7);
         }

         int var1 = 1;

         while (var1-- > 0) {
            WorldChunk var2 = arrayDeque.poll();
            if (var2 == null) {
               break;
            }

            long var3 = longOf(var2.getPos().x, var2.getPos().z);

            try {
               run7(var3, var2, var0.world);
            } catch (Throwable var9) {
            } finally {
               set2.remove(var3);
               set.add(var3);
            }
         }
      }
   }

   private static void run7(long var0, WorldChunk var2, World var3) {
      int var4 = var2.getPos().x;
      int var5 = var2.getPos().z;
      boolean var6 = var3.getRegistryKey() == World.NETHER;
      int[] var7 = new int[7];
      HashSet var8 = new HashSet();
      double var9 = doubleOf2(var2, var4, var5, var6, var7, var8);
      concurrentHashMap2.put(var0, var9);
      double var11 = doubleOf(var4, var5);
      if (var11 >= 0.0) {
         double var13 = var9 - var11;
         if (var13 > 0.15) {
            var7[5] = (int)Math.round(var13 * 100.0);
         }
      }

      for (int var15 = 0; var15 < 7; var15++) {
         if (var7[var15] > 150) {
            var7[var15] = 150;
         }
      }

      concurrentHashMap.put(var0, var7);
      if (!var8.isEmpty()) {
         concurrentHashMap4.put(var0, var8);
      } else {
         concurrentHashMap4.remove(var0);
      }

      int var16 = intOf3(var7);
      if (var16 > 0) {
         concurrentHashMap3.merge(var0, var16, MapUtils::integerOf10);
         int var14 = var16 / 2;
         if (var14 > 0) {
            concurrentHashMap3.merge(longOf(var4 + 1, var5), var14, MapUtils::integerOf9);
            concurrentHashMap3.merge(longOf(var4 - 1, var5), var14, MapUtils::integerOf8);
            concurrentHashMap3.merge(longOf(var4, var5 + 1), var14, MapUtils::integerOf7);
            concurrentHashMap3.merge(longOf(var4, var5 - 1), var14, MapUtils::integerOf6);
         }
      }
   }

   private static double doubleOf(int var0, int var1) {
      double var2 = 0.0;
      int var4 = 0;
      Double var5;
      if ((var5 = concurrentHashMap2.get(longOf(var0 + 1, var1))) != null) {
         var2 += var5;
         var4++;
      }

      if ((var5 = concurrentHashMap2.get(longOf(var0 - 1, var1))) != null) {
         var2 += var5;
         var4++;
      }

      if ((var5 = concurrentHashMap2.get(longOf(var0, var1 + 1))) != null) {
         var2 += var5;
         var4++;
      }

      if ((var5 = concurrentHashMap2.get(longOf(var0, var1 - 1))) != null) {
         var2 += var5;
         var4++;
      }

      return var4 == 0 ? -1.0 : var2 / var4;
   }

   private static int intOf3(int[] var0) {
      boolean[] var1 = boolArray;
      int var2 = 0;

      for (int var3 = 0; var3 < 7; var3++) {
         if (var1[var3]) {
            var2 += var0[var3] * intArray[var3];
         }
      }

      return var2;
   }

   private static void run8() {
      concurrentHashMap3.clear();

      for (Entry var1 : concurrentHashMap.entrySet()) {
         long var2 = (Long)var1.getKey();
         int var4 = intOf(var2);
         int var5 = intOf2(var2);
         int var6 = intOf3((int[])var1.getValue());
         if (var6 > 0) {
            concurrentHashMap3.merge(var2, var6, MapUtils::integerOf5);
            int var7 = var6 / 2;
            if (var7 > 0) {
               concurrentHashMap3.merge(longOf(var4 + 1, var5), var7, MapUtils::integerOf4);
               concurrentHashMap3.merge(longOf(var4 - 1, var5), var7, MapUtils::integerOf3);
               concurrentHashMap3.merge(longOf(var4, var5 + 1), var7, MapUtils::integerOf2);
               concurrentHashMap3.merge(longOf(var4, var5 - 1), var7, MapUtils::integerOf);
            }
         }
      }
   }

   private static double doubleOf2(WorldChunk var0, int var1, int var2, boolean var3, int[] var4, Set<BlockPos> var5) {
      int var6 = var1 << 4;
      int var7 = var2 << 4;
      int var8 = var0.getBottomY();
      int var9 = 0;
      int var10 = 0;
      ChunkSection[] var11 = var0.getSectionArray();

      for (int var12 = 0; var12 < var11.length; var12++) {
         ChunkSection var13 = var11[var12];
         if (var13 != null) {
            int var14 = var8 + (var12 << 4);
            boolean var15 = var13.isEmpty();

            for (int var16 = 0; var16 < 16; var16++) {
               int var17 = var14 + var16;
               boolean var18 = var17 >= 5 && var17 <= 64;
               if (var15) {
                  if (var18) {
                     var9 += 256;
                     var10 += 256;
                  }
               } else {
                  for (int var19 = 0; var19 < 16; var19++) {
                     for (int var20 = 0; var20 < 16; var20++) {
                        BlockState var21;
                        try {
                           var21 = var13.getBlockState(var19, var16, var20);
                        } catch (Throwable var26) {
                           continue;
                        }

                        boolean var22 = var21.isAir();
                        if (var18) {
                           var10++;
                           if (var22) {
                              var9++;
                           }
                        }

                        if (!var22) {
                           Block var23 = var21.getBlock();
                           int var24 = var6 + var19;
                           int var25 = var7 + var20;
                           if (check4(var23)) {
                              var4[6]++;
                              var5.add(new BlockPos(var24, var17, var25));
                           } else if (check(var23, var3)) {
                              var4[4]++;
                              var5.add(new BlockPos(var24, var17, var25));
                           } else if (check5(var23)) {
                              var4[1]++;
                              var5.add(new BlockPos(var24, var17, var25));
                           } else if (check3(var23)) {
                              var4[0]++;
                              var5.add(new BlockPos(var24, var17, var25));
                           } else if (check6(var23)) {
                              var4[2]++;
                           } else if (check2(var23, var17)) {
                              var4[3]++;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var10 > 0 ? (double)var9 / var10 : 0.0;
   }

   private static boolean check3(Block var0) {
      return var0 instanceof CropBlock
         || var0 instanceof StemBlock
         || var0 == Blocks.NETHER_WART
         || var0 == Blocks.SUGAR_CANE
         || var0 == Blocks.KELP
         || var0 == Blocks.KELP_PLANT
         || var0 == Blocks.MELON
         || var0 == Blocks.PUMPKIN
         || var0 == Blocks.MELON_STEM
         || var0 == Blocks.PUMPKIN_STEM
         || var0 == Blocks.SWEET_BERRY_BUSH
         || var0 == Blocks.COCOA;
   }

   private static boolean check4(Block var0) {
      return var0 == Blocks.CHEST || var0 == Blocks.TRAPPED_CHEST || var0 == Blocks.BARREL || var0 == Blocks.ENDER_CHEST || var0 instanceof ShulkerBoxBlock;
   }

   private static boolean check5(Block var0) {
      return var0 == Blocks.REDSTONE_WIRE
         || var0 == Blocks.COMPARATOR
         || var0 == Blocks.REPEATER
         || var0 == Blocks.PISTON
         || var0 == Blocks.STICKY_PISTON
         || var0 == Blocks.OBSERVER
         || var0 == Blocks.REDSTONE_TORCH
         || var0 == Blocks.REDSTONE_WALL_TORCH
         || var0 == Blocks.REDSTONE_BLOCK
         || var0 == Blocks.LEVER
         || var0 == Blocks.TARGET;
   }

   private static boolean check(Block var0, boolean var1) {
      if (var0 == Blocks.HOPPER || var0 == Blocks.DISPENSER || var0 == Blocks.DROPPER) {
         return true;
      } else {
         return var0 == Blocks.ICE || var0 == Blocks.PACKED_ICE || var0 == Blocks.BLUE_ICE ? true : var1 && var0 == Blocks.WATER;
      }
   }

   private static boolean check6(Block var0) {
      return var0 == Blocks.COBBLESTONE
         || var0 == Blocks.MOSSY_COBBLESTONE
         || var0 == Blocks.OAK_PLANKS
         || var0 == Blocks.SPRUCE_PLANKS
         || var0 == Blocks.BIRCH_PLANKS
         || var0 == Blocks.JUNGLE_PLANKS
         || var0 == Blocks.ACACIA_PLANKS
         || var0 == Blocks.DARK_OAK_PLANKS
         || var0 == Blocks.MANGROVE_PLANKS
         || var0 == Blocks.CHERRY_PLANKS
         || var0 == Blocks.BAMBOO_PLANKS
         || var0 == Blocks.CRIMSON_PLANKS
         || var0 == Blocks.WARPED_PLANKS
         || var0 == Blocks.OAK_LOG
         || var0 == Blocks.SPRUCE_LOG
         || var0 == Blocks.BIRCH_LOG
         || var0 == Blocks.JUNGLE_LOG
         || var0 == Blocks.ACACIA_LOG
         || var0 == Blocks.DARK_OAK_LOG
         || var0 == Blocks.MANGROVE_LOG
         || var0 == Blocks.CHERRY_LOG
         || var0 == Blocks.CRIMSON_STEM
         || var0 == Blocks.WARPED_STEM
         || var0 == Blocks.STRIPPED_OAK_LOG
         || var0 == Blocks.STRIPPED_SPRUCE_LOG
         || var0 == Blocks.STRIPPED_BIRCH_LOG
         || var0 == Blocks.STRIPPED_JUNGLE_LOG
         || var0 == Blocks.STRIPPED_ACACIA_LOG
         || var0 == Blocks.STRIPPED_DARK_OAK_LOG
         || var0 == Blocks.POLISHED_DEEPSLATE
         || var0 == Blocks.DEEPSLATE_BRICKS
         || var0 == Blocks.DEEPSLATE_TILES;
   }

   private static boolean check2(Block var0, int var1) {
      return var1 <= 32 || var0 != Blocks.DEEPSLATE && var0 != Blocks.COBBLED_DEEPSLATE
         ? var1 > 100 && (var0 == Blocks.GRAVEL || var0 == Blocks.DIRT || var0 == Blocks.COARSE_DIRT)
         : true;
   }

   private static Integer integerOf(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static Integer integerOf2(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static Integer integerOf3(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static Integer integerOf4(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static Integer integerOf5(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static Integer integerOf6(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static Integer integerOf7(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static Integer integerOf8(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static Integer integerOf9(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static Integer integerOf10(Integer var0, Integer var1) {
      return Math.min(var0 + var1, 500);
   }

   private static boolean check7(Entry var0) {
      return ((Integer)var0.getValue()) <= 0;
   }

   private static Integer integerOf11(Long var0, Integer var1) {
      return Math.max(0, var1 - 1);
   }

   private static void run16(ClientWorld var0, WorldChunk var1) {
      if (var1 != null) {
         long var2 = longOf(var1.getPos().x, var1.getPos().z);
         set.remove(var2);
         set2.remove(var2);
      }
   }

   private static void run9(ClientWorld var0, WorldChunk var1) {
      if (bool && var1 != null) {
         run3(var1);
      }
   }
}

