package dev.fede.nyx.tracker;

import dev.fede.nyx.util.PathUtils_2_3_4;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public final class ChunkActivityScanner {
   private static volatile boolean bool = false;
   private static volatile int intVal = 100;
   private static volatile int intVal2 = 5;
   private static volatile int intVal3 = 16;
   private static volatile int intVal4 = 128;
   private static volatile boolean bool2 = true;
   private static final EnumSet<ChunkActivityScanner.Signal> enumSet = EnumSet.allOf(ChunkActivityScanner.Signal.class);
   private static boolean bool3 = false;
   private static int intVal5 = 0;
   private static final int intVal6 = 600;
   private static final int intVal7 = 8;
   private static final int intVal8 = 24;
   private static final int intVal9 = 24;
   private static final Set<Long> set = ConcurrentHashMap.newKeySet();
   private static final Set<Long> set2 = ConcurrentHashMap.newKeySet();
   private static final ConcurrentHashMap<Long, ChunkActivityScanner.Inner1> concurrentHashMap = new ConcurrentHashMap<>();
   private static final ArrayList<WorldChunk> arrayList = new ArrayList<>();
   private static volatile String string = null;

   public static long longOf(int var0, int var1) {
      return (long)var0 << 32 | var1 & 4294967295L;
   }

   public static int intOf(long var0) {
      return (int)(var0 >> 32);
   }

   public static int intOf2(long var0) {
      return (int)var0;
   }

   private ChunkActivityScanner() {
   }

   public static void run5(boolean var0) {
      bool = var0;
      run10();
      if (!var0) {
         arrayList.clear();
         set2.clear();
      }
   }

   public static void run(int var0, int var1) {
      intVal = Math.max(1, Math.min(32, var0));
      intVal2 = Math.max(1, Math.min(500, var1));
   }

   public static void run2(int var0, int var1) {
      intVal3 = Math.max(-64, Math.min(320, var0));
      intVal4 = Math.max(-64, Math.min(320, var1));
      if (intVal4 < intVal3) {
         int var2 = intVal3;
         intVal3 = intVal4;
         intVal4 = var2;
      }
   }

   public static void run3(ChunkActivityScanner.Signal var0, boolean var1) {
      if (var1) {
         enumSet.add(var0);
      } else {
         enumSet.remove(var0);
      }
   }

   public static boolean check(ChunkActivityScanner.Signal var0) {
      return enumSet.contains(var0);
   }

   public static boolean isEnabled() {
      return bool;
   }

   public static int getInt2() {
      return intVal;
   }

   public static int getInt() {
      return intVal2;
   }

   public static int getInt3() {
      return intVal;
   }

   public static void run8(boolean var0) {
      bool2 = var0;
   }

   public static boolean isEnabled2() {
      return bool2;
   }

   public static Map<Long, Integer> getMap() {
      HashMap var0 = new HashMap();

      for (Entry var2 : concurrentHashMap.entrySet()) {
         ChunkActivityScanner.Inner1 var3 = (ChunkActivityScanner.Inner1)var2.getValue();
         if (var3 != null && var3.totalScore() >= intVal2) {
            var0.put((Long)var2.getKey(), var3.totalScore());
         }
      }

      return var0;
   }

   public static ChunkActivityScanner.Inner1 chunkActivityScanneraOf(long var0) {
      return concurrentHashMap.getOrDefault(var0, ChunkActivityScanner.Inner1.chunkActivityScannera);
   }

   public static Map<Long, ChunkActivityScanner.Inner1> getMap2() {
      HashMap var0 = new HashMap(concurrentHashMap.size());

      for (Entry var2 : concurrentHashMap.entrySet()) {
         ChunkActivityScanner.Inner1 var3 = (ChunkActivityScanner.Inner1)var2.getValue();
         if (var3 != null && var3.totalScore() >= intVal2) {
            var0.put((Long)var2.getKey(), var3);
         }
      }

      return var0;
   }

   public static Map<Long, ChunkActivityScanner.Inner1> getMap3() {
      return getMap2();
   }

   public static Map<Long, Set<BlockPos>> getMap4() {
      HashMap var0 = new HashMap(concurrentHashMap.size());

      for (Entry var2 : concurrentHashMap.entrySet()) {
         Set var3 = ((ChunkActivityScanner.Inner1)var2.getValue()).hits();
         if (var3 != null && !var3.isEmpty()) {
            var0.put((Long)var2.getKey(), new HashSet(var3));
         }
      }

      return var0;
   }

   public static void run4() {
      run6();
   }

   private static void run6() {
      arrayList.clear();
      set2.clear();
      set.clear();
      concurrentHashMap.clear();
   }

   public static void run7(MinecraftClient var0) {
      if (var0 != null && !concurrentHashMap.isEmpty()) {
         String var1 = string;
         if (var1 == null) {
            var1 = stringOf(var0);
         }

         if (var1 != null) {
            ArrayList var2 = new ArrayList(concurrentHashMap.size());

            for (Entry var4 : concurrentHashMap.entrySet()) {
               long var5 = (Long)var4.getKey();
               ChunkActivityScanner.Inner1 var7 = (ChunkActivityScanner.Inner1)var4.getValue();
               if (var7 != null && var7.totalScore() >= intVal2) {
                  PathUtils_2_3_4.Inner1 var8 = new PathUtils_2_3_4.Inner1();
                  var8.chunkX = intOf(var5);
                  var8.chunkZ = intOf2(var5);
                  var8.totalScore = var7.totalScore();
                  var8.containerScore = var7.containerScore();
                  var8.artificialScore = 0;
                  var8.lightScore = 0;
                  var8.entityScore = 0;
                  var8.patternScore = 0;
                  var8.containerCount = var7.containerCount();
                  var8.artificialCount = 0;
                  var8.lightCount = 0;
                  var8.entityCount = 0;
                  var8.patternCount = 0;
                  var8.surfaceY = var7.surfaceY();
                  var2.add(var8);
               }
            }

            if (!var2.isEmpty()) {
               PathUtils_2_3_4.save(var0, var2, var1);
            }
         }
      }
   }

   public static void run9(MinecraftClient var0) {
      if (var0 != null) {
         String var1 = stringOf(var0);
         if (var1 != null) {
            string = var1;
            Map var2 = PathUtils_2_3_4.load(var0, var1);
            if (!var2.isEmpty()) {
               for (Entry var4 : ((java.util.Map<?,?>)var2).entrySet()) {
                  PathUtils_2_3_4.Inner1 var5 = (PathUtils_2_3_4.Inner1)var4.getValue();
                  if (var5 != null && var5.totalScore > 0) {
                     ChunkActivityScanner.Inner1 var6 = new ChunkActivityScanner.Inner1(
                        var5.totalScore, var5.totalScore, 0, 0, 0, 0, var5.containerCount, 0, 0, 0, 0, var5.surfaceY, Collections.emptySet()
                     );
                     concurrentHashMap.put((Long)var4.getKey(), var6);
                  }
               }

               System.out.println("[SusChunkFinder] restore: loaded=" + concurrentHashMap.size() + " from disk");
            }
         }
      }
   }

   private static synchronized void run10() {
      if (!bool3) {
         bool3 = true;
         ClientChunkEvents.CHUNK_LOAD.register(ChunkActivityScanner::run18);
         ClientChunkEvents.CHUNK_UNLOAD.register(ChunkActivityScanner::run17);
         ClientTickEvents.END_CLIENT_TICK.register(ChunkActivityScanner::run13);
         ClientPlayConnectionEvents.JOIN.register(ChunkActivityScanner::run16);
         ClientPlayConnectionEvents.DISCONNECT.register(ChunkActivityScanner::run15);
      }
   }

   private static void run11(WorldChunk var0) {
      long var1 = longOf(var0.getPos().x, var0.getPos().z);
      if (!set.contains(var1) && !set2.contains(var1)) {
         set2.add(var1);
         arrayList.add(var0);
      }
   }

   public static void run12(WorldChunk var0) {
      if (var0 != null) {
         long var1 = longOf(var0.getPos().x, var0.getPos().z);
         set.remove(var1);
         if (set2.add(var1)) {
            arrayList.add(var0);
         }
      }
   }

   private static void run13(MinecraftClient var0) {
      if (bool && var0 != null && var0.world != null) {
         String var1 = stringOf(var0);
         if (var1 != null && !var1.equals(string)) {
            if (string != null && !concurrentHashMap.isEmpty()) {
               run7(var0);
            }

            run6();
            string = var1;
            run9(var0);
            intVal5 = 0;
         }

         if (++intVal5 >= 600) {
            intVal5 = 0;
            run7(var0);
         }

         int var2 = arrayList.size() >= 24 ? 24 : 8;
         int var3 = 0;
         int var4 = 0;
         boolean var5 = false;
         if (var0.player != null) {
            var3 = var0.player.getChunkPos().x;
            var4 = var0.player.getChunkPos().z;
            var5 = true;
         }

         while (var2-- > 0) {
            WorldChunk var6 = var5 ? class2818Of(var3, var4) : getclass2818();
            if (var6 == null) {
               break;
            }

            int var7 = var6.getPos().x;
            int var8 = var6.getPos().z;
            long var9 = longOf(var7, var8);

            try {
               run14(var9, var7, var8, var6);
            } catch (Throwable var15) {
            } finally {
               set2.remove(var9);
               set.add(var9);
            }
         }
      }
   }

   private static WorldChunk class2818Of(int var0, int var1) {
      int var2 = arrayList.size();
      if (var2 == 0) {
         return null;
      } else {
         int var3 = 0;
         long var4 = Long.MAX_VALUE;

         for (int var6 = 0; var6 < var2; var6++) {
            WorldChunk var7 = arrayList.get(var6);
            int var8 = var7.getPos().x - var0;
            int var9 = var7.getPos().z - var1;
            long var10 = (long)var8 * var8 + (long)var9 * var9;
            if (var10 < var4) {
               var4 = var10;
               var3 = var6;
            }
         }

         return arrayList.remove(var3);
      }
   }

   private static WorldChunk getclass2818() {
      return arrayList.isEmpty() ? null : arrayList.remove(0);
   }

   private static void run14(long var0, int var2, int var3, WorldChunk var4) {
      ChunkActivityScanner.Inner1 var5 = chunkActivityScanneraOf2(var4, var2, var3);
      if (var5 != ChunkActivityScanner.Inner1.chunkActivityScannera && var5.totalScore() > 0) {
         concurrentHashMap.put(var0, var5);
      } else if (!bool2 || !concurrentHashMap.containsKey(var0)) {
         concurrentHashMap.remove(var0);
      }
   }

   private static ChunkActivityScanner.Inner1 chunkActivityScanneraOf2(WorldChunk var0, int var1, int var2) {
      int var3 = var1 << 4;
      int var4 = var2 << 4;
      int var5 = var0.getBottomY();
      int var6 = var5 + var0.getHeight();
      ChunkSection[] var7 = var0.getSectionArray();
      HashSet var8 = new HashSet();
      int var9 = 0;
      int var10 = intOf3(var0, var5);

      for (int var11 = 0; var11 < var7.length; var11++) {
         ChunkSection var12 = var7[var11];
         if (var12 != null && !var12.isEmpty()) {
            int var13 = var5 + (var11 << 4);

            for (int var14 = 0; var14 < 16; var14++) {
               for (int var15 = 0; var15 < 16; var15++) {
                  for (int var16 = 0; var16 < 16; var16++) {
                     int var17 = var13 + var16;
                     if (var17 >= var6) {
                        break;
                     }

                     BlockState var18;
                     try {
                        var18 = var12.getBlockState(var14, var16, var15);
                     } catch (Throwable var20) {
                        continue;
                     }

                     if (!var18.isAir()) {
                        Block var19 = var18.getBlock();
                        if (check2(var19, var18, var17)) {
                           var8.add(new BlockPos(var3 + var14, var17, var4 + var15));
                           var9++;
                        }
                     }
                  }
               }
            }
         }
      }

      return var9 < intVal2
         ? ChunkActivityScanner.Inner1.chunkActivityScannera
         : new ChunkActivityScanner.Inner1(var9, var9, 0, 0, 0, 0, var9, 0, 0, 0, 0, var10, var8);
   }

   private static boolean check2(Block var0, BlockState var1, int var2) {
      if (!enumSet.contains(ChunkActivityScanner.Signal.KELP) || var0 != Blocks.KELP && var0 != Blocks.KELP_PLANT) {
         if (!enumSet.contains(ChunkActivityScanner.Signal.CAVE_VINES) || var0 != Blocks.CAVE_VINES && var0 != Blocks.CAVE_VINES_PLANT) {
            if (enumSet.contains(ChunkActivityScanner.Signal.VINES) && var0 == Blocks.VINE) {
               return true;
            } else if (!enumSet.contains(ChunkActivityScanner.Signal.AMETHYST)
               || var0 != Blocks.AMETHYST_CLUSTER
                  && var0 != Blocks.SMALL_AMETHYST_BUD
                  && var0 != Blocks.MEDIUM_AMETHYST_BUD
                  && var0 != Blocks.LARGE_AMETHYST_BUD
                  && var0 != Blocks.AMETHYST_BLOCK
                  && var0 != Blocks.BUDDING_AMETHYST) {
               if (enumSet.contains(ChunkActivityScanner.Signal.BAMBOO) && var0 == Blocks.BAMBOO) {
                  return true;
               } else if (enumSet.contains(ChunkActivityScanner.Signal.COCOA) && var0 == Blocks.COCOA) {
                  return true;
               } else if (!enumSet.contains(ChunkActivityScanner.Signal.BEE_NEST) || var0 != Blocks.BEE_NEST && var0 != Blocks.BEEHIVE) {
                  if (enumSet.contains(ChunkActivityScanner.Signal.ROTATED_DEEPSLATE) && var0 == Blocks.DEEPSLATE) {
                     if (var2 < intVal3 || var2 > intVal4) {
                        return false;
                     }

                     try {
                        if (var1.contains(Properties.AXIS)) {
                           Axis var3 = (Axis)var1.get(Properties.AXIS);
                           if (var3 != Axis.Y) {
                              return true;
                           }
                        }
                     } catch (Throwable var4) {
                     }
                  }

                  return enumSet.contains(ChunkActivityScanner.Signal.SKULL_CANDLE)
                        && (
                           var0 == Blocks.SKELETON_SKULL
                              || var0 == Blocks.SKELETON_WALL_SKULL
                              || var0 == Blocks.PLAYER_HEAD
                              || var0 == Blocks.PLAYER_WALL_HEAD
                              || var0 == Blocks.ZOMBIE_HEAD
                              || var0 == Blocks.ZOMBIE_WALL_HEAD
                              || var0 == Blocks.CREEPER_HEAD
                              || var0 == Blocks.CREEPER_WALL_HEAD
                              || var0 == Blocks.DRAGON_HEAD
                              || var0 == Blocks.DRAGON_WALL_HEAD
                              || var0 == Blocks.PIGLIN_HEAD
                              || var0 == Blocks.PIGLIN_WALL_HEAD
                              || var0 == Blocks.CANDLE
                              || var0 == Blocks.WHITE_CANDLE
                              || var0 == Blocks.ORANGE_CANDLE
                              || var0 == Blocks.MAGENTA_CANDLE
                              || var0 == Blocks.LIGHT_BLUE_CANDLE
                              || var0 == Blocks.YELLOW_CANDLE
                              || var0 == Blocks.LIME_CANDLE
                              || var0 == Blocks.PINK_CANDLE
                              || var0 == Blocks.GRAY_CANDLE
                              || var0 == Blocks.LIGHT_GRAY_CANDLE
                              || var0 == Blocks.CYAN_CANDLE
                              || var0 == Blocks.PURPLE_CANDLE
                              || var0 == Blocks.BLUE_CANDLE
                              || var0 == Blocks.BROWN_CANDLE
                              || var0 == Blocks.GREEN_CANDLE
                              || var0 == Blocks.RED_CANDLE
                              || var0 == Blocks.BLACK_CANDLE
                              || var0 == Blocks.CANDLE_CAKE
                              || var0 == Blocks.WHITE_CANDLE_CAKE
                              || var0 == Blocks.ORANGE_CANDLE_CAKE
                              || var0 == Blocks.MAGENTA_CANDLE_CAKE
                              || var0 == Blocks.LIGHT_BLUE_CANDLE_CAKE
                              || var0 == Blocks.YELLOW_CANDLE_CAKE
                              || var0 == Blocks.LIME_CANDLE_CAKE
                              || var0 == Blocks.PINK_CANDLE_CAKE
                              || var0 == Blocks.GRAY_CANDLE_CAKE
                              || var0 == Blocks.LIGHT_GRAY_CANDLE_CAKE
                              || var0 == Blocks.CYAN_CANDLE_CAKE
                              || var0 == Blocks.PURPLE_CANDLE_CAKE
                              || var0 == Blocks.BLUE_CANDLE_CAKE
                              || var0 == Blocks.BROWN_CANDLE_CAKE
                              || var0 == Blocks.GREEN_CANDLE_CAKE
                              || var0 == Blocks.RED_CANDLE_CAKE
                              || var0 == Blocks.BLACK_CANDLE_CAKE
                        )
                     ? true
                     : enumSet.contains(ChunkActivityScanner.Signal.WITHER_SKULL)
                        && (var0 == Blocks.WITHER_SKELETON_SKULL || var0 == Blocks.WITHER_SKELETON_WALL_SKULL);
               } else {
                  return true;
               }
            } else {
               return true;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public static List<Entity> listOf(MinecraftClient var0) {
      ArrayList var1 = new ArrayList();
      if (var0 != null && var0.world != null && enumSet.contains(ChunkActivityScanner.Signal.ENTITIES)) {
         try {
            for (Entity var3 : var0.world.getEntities()) {
               if (var3 != null
                  && (
                     var3 instanceof VillagerEntity
                        || var3 instanceof ZombieVillagerEntity
                        || var3 instanceof AllayEntity
                        || var3 instanceof VindicatorEntity
                        || var3 instanceof WardenEntity
                  )) {
                  var1.add(var3);
               }
            }
         } catch (Throwable var4) {
         }

         return var1;
      } else {
         return var1;
      }
   }

   private static int intOf3(WorldChunk var0, int var1) {
      long var2 = 0L;
      int var4 = 0;

      try {
         for (byte var5 = 0; var5 < 16; var5 += 4) {
            for (byte var6 = 0; var6 < 16; var6 += 4) {
               int var7 = var0.sampleHeightmap(Type.MOTION_BLOCKING_NO_LEAVES, var5, var6);
               if (var7 > var1) {
                  var2 += var7;
                  var4++;
               }
            }
         }
      } catch (Throwable var8) {
      }

      return var4 > 0 ? (int)(var2 / var4) : var1;
   }

   private static String stringOf(MinecraftClient var0) {
      try {
         return var0 != null && var0.world != null ? var0.world.getRegistryKey().getValue().toString() : null;
      } catch (Throwable var2) {
         return null;
      }
   }

   private static void run15(ClientPlayNetworkHandler var0, MinecraftClient var1) {
      run7(var1);
   }

   private static void run16(ClientPlayNetworkHandler var0, PacketSender var1, MinecraftClient var2) {
      run9(var2);
   }

   private static void run17(ClientWorld var0, WorldChunk var1) {
      if (var1 != null) {
         long var2 = longOf(var1.getPos().x, var1.getPos().z);
         if (bool2) {
            set2.remove(var2);
         } else {
            set.remove(var2);
            set2.remove(var2);
         }
      }
   }

   private static void run18(ClientWorld var0, WorldChunk var1) {
      if (bool && var1 != null) {
         run11(var1);
      }
   }

   public static enum Signal {
      KELP,
      CAVE_VINES,
      VINES,
      AMETHYST,
      BAMBOO,
      COCOA,
      BEE_NEST,
      ROTATED_DEEPSLATE,
      SKULL_CANDLE,
      WITHER_SKULL,
      ENTITIES;

      private static final ChunkActivityScanner.Signal[] chunkActivityScannerSignalArray = getChunkActivityScannerSignalArray();

      private static ChunkActivityScanner.Signal[] getChunkActivityScannerSignalArray() {
         return new ChunkActivityScanner.Signal[]{
            KELP, CAVE_VINES, VINES, AMETHYST, BAMBOO, COCOA, BEE_NEST, ROTATED_DEEPSLATE, SKULL_CANDLE, WITHER_SKULL, ENTITIES
         };
      }
   }

   public static enum Tier {
      NONE,
      YELLOW,
      ORANGE,
      RED;

      private static final ChunkActivityScanner.Tier[] chunkActivityScannerTierArray = getChunkActivityScannerTierArray();

      private static ChunkActivityScanner.Tier[] getChunkActivityScannerTierArray() {
         return new ChunkActivityScanner.Tier[]{NONE, YELLOW, ORANGE, RED};
      }
   }

public record Inner1(int totalScore, int containerScore, int artificialScore, int lightScore, int entityScore, int patternScore, int containerCount, int artificialCount, int lightCount, int entityCount, int patternCount, int surfaceY, Set<BlockPos> hits) {
   public static final Inner1 chunkActivityScannera = new Inner1(
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Integer.MIN_VALUE, Collections.emptySet()
   );


   public Inner1 chunkActivityScanneraOf(int var1) {
      return new Inner1(
         var1,
         var1,
         this.artificialScore,
         this.lightScore,
         this.entityScore,
         this.patternScore,
         this.containerCount,
         this.artificialCount,
         this.lightCount,
         this.entityCount,
         this.patternCount,
         this.surfaceY,
         this.hits
      );
   }

   public boolean isEnabled() {
      return this.totalScore >= ChunkActivityScanner.intVal2;
   }

   public ChunkActivityScanner.Tier getChunkActivityScannerTier() {
      if (this.totalScore >= ChunkActivityScanner.intVal2 * 3) {
         return ChunkActivityScanner.Tier.RED;
      } else if (this.totalScore >= ChunkActivityScanner.intVal2 * 2) {
         return ChunkActivityScanner.Tier.ORANGE;
      } else {
         return this.totalScore >= ChunkActivityScanner.intVal2 ? ChunkActivityScanner.Tier.YELLOW : ChunkActivityScanner.Tier.NONE;
      }
   }
}
}

