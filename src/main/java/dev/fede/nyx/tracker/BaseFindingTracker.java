package dev.fede.nyx.tracker;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public final class BaseFindingTracker {
   private static final int intVal = 1;
   private static final long longVal = 300000L;
   private static final int intVal2 = 100;
   private static final int intVal3 = 25;
   private static final int intVal4 = 5;
   private static final int intVal5 = 60;
   private static final int intVal6 = 8;
   private static final int intVal7 = 4;
   private static final int intVal8 = 3;
   private static final int intVal9 = 5;
   private static final int intVal10 = 55;
   private static final int intVal11 = 12;
   private static volatile boolean bool = false;
   private static boolean bool2 = false;
   private static int intVal12 = 0;
   private static int intVal13 = 0;
   private static final ConcurrentHashMap<BlockPos, BaseFindingTracker.Inner1> concurrentHashMap = new ConcurrentHashMap<>();
   private static final ArrayDeque<WorldChunk> arrayDeque = new ArrayDeque<>();
   private static final Set<Long> set = ConcurrentHashMap.newKeySet();

   private BaseFindingTracker() {
   }

   public static void run5(boolean var0) {
      bool = var0;
      run3();
      if (!var0) {
         arrayDeque.clear();
         set.clear();
      }
   }

   public static boolean isEnabled() {
      return bool;
   }

   public static Collection<BaseFindingTracker.Inner1> getCollection() {
      return concurrentHashMap.values();
   }

   public static int getInt() {
      return concurrentHashMap.size();
   }

   public static void run19() {
      concurrentHashMap.clear();
      arrayDeque.clear();
      set.clear();
   }

   private static synchronized void run3() {
      if (!bool2) {
         bool2 = true;
         ClientChunkEvents.CHUNK_LOAD.register(BaseFindingTracker::run7);
         ClientChunkEvents.CHUNK_UNLOAD.register(BaseFindingTracker::run16);
         ClientTickEvents.END_CLIENT_TICK.register(BaseFindingTracker::run);
      }
   }

   private static long longOf(int var0, int var1) {
      return (long)var0 << 32 | var1 & 4294967295L;
   }

   private static void run(MinecraftClient var0) {
      if (bool && var0 != null && var0.world != null) {
         int var1 = 1;

         while (var1-- > 0) {
            WorldChunk var2 = arrayDeque.poll();
            if (var2 == null) {
               break;
            }

            long var3 = longOf(var2.getPos().x, var2.getPos().z);
            set.remove(var3);

            try {
               run2(var2);
            } catch (Throwable var6) {
            }
         }

         long var7 = System.currentTimeMillis();
         concurrentHashMap.entrySet().removeIf(_e -> false);
         if (++intVal12 >= 60) {
            intVal12 = 0;
            run4(var0);
         }
      }
   }

   private static void run2(WorldChunk var0) {
      int var1 = 0;
      int var2 = var0.getPos().x << 4;
      int var3 = var0.getPos().z << 4;
      int var4 = var0.getBottomY();
      ChunkSection[] var5 = var0.getSectionArray();

      for (int var6 = 0; var6 < var5.length; var6++) {
         ChunkSection var7 = var5[var6];
         if (var7 != null && !var7.isEmpty()) {
            int var8 = var4 + (var6 << 4);
            if (var8 + 16 >= 5 && var8 <= 55) {
               for (byte var9 = 0; var9 < 16; var9 += 3) {
                  int var10 = var8 + var9;
                  if (var10 >= 5 && var10 <= 55) {
                     for (byte var11 = 0; var11 < 16; var11 += 4) {
                        for (byte var12 = 0; var12 < 16; var12 += 4) {
                           if (var1 >= 12) {
                              return;
                           }

                           BlockState var13;
                           try {
                              var13 = var7.getBlockState(var11, var9, var12);
                           } catch (Throwable var16) {
                              continue;
                           }

                           BlockPos var14 = new BlockPos(var2 + var11, var10, var3 + var12);
                           BaseFindingTracker.Kind var15 = baseFindingTrackerKindOf(var0, var14, var13);
                           if (var15 != null
                              && concurrentHashMap.putIfAbsent(var14, new BaseFindingTracker.Inner1(var14, var15, stringOf(var15, var13))) == null) {
                              var1++;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static BaseFindingTracker.Kind baseFindingTrackerKindOf(WorldChunk var0, BlockPos var1, BlockState var2) {
      Block var3 = var2.getBlock();
      if (check3(var3)) {
         return BaseFindingTracker.Kind.ARTIFICIAL_STONE;
      } else if (check4(var3)) {
         return BaseFindingTracker.Kind.DEEP_PLANKS;
      } else if (var3 == Blocks.HOPPER && check(var0, var1)) {
         return BaseFindingTracker.Kind.SEALED_HOPPER;
      } else {
         return var2.isAir() && check5(var0, var1) ? BaseFindingTracker.Kind.HOLLOW_ROOM : null;
      }
   }

   private static String stringOf(BaseFindingTracker.Kind var0, BlockState var1) {
      return switch (var0) {
         case HOLLOW_ROOM -> "air";
         case ARTIFICIAL_STONE, DEEP_PLANKS -> var1.getBlock().toString();
         case SEALED_HOPPER -> "hopper";
      };
   }

   private static boolean check3(Block var0) {
      return var0 == Blocks.STONE_BRICKS
         || var0 == Blocks.MOSSY_STONE_BRICKS
         || var0 == Blocks.CRACKED_STONE_BRICKS
         || var0 == Blocks.CHISELED_STONE_BRICKS
         || var0 == Blocks.DEEPSLATE_BRICKS
         || var0 == Blocks.CRACKED_DEEPSLATE_BRICKS
         || var0 == Blocks.POLISHED_DEEPSLATE
         || var0 == Blocks.SMOOTH_STONE
         || var0 == Blocks.POLISHED_ANDESITE
         || var0 == Blocks.POLISHED_GRANITE
         || var0 == Blocks.POLISHED_DIORITE
         || var0 == Blocks.BRICKS
         || var0 == Blocks.NETHER_BRICKS
         || var0 == Blocks.RED_NETHER_BRICKS;
   }

   private static boolean check4(Block var0) {
      return var0 == Blocks.OAK_PLANKS
         || var0 == Blocks.SPRUCE_PLANKS
         || var0 == Blocks.BIRCH_PLANKS
         || var0 == Blocks.JUNGLE_PLANKS
         || var0 == Blocks.ACACIA_PLANKS
         || var0 == Blocks.DARK_OAK_PLANKS
         || var0 == Blocks.MANGROVE_PLANKS
         || var0 == Blocks.CHERRY_PLANKS
         || var0 == Blocks.BAMBOO_PLANKS
         || var0 == Blocks.CRIMSON_PLANKS
         || var0 == Blocks.WARPED_PLANKS;
   }

   private static boolean check(WorldChunk var0, BlockPos var1) {
      try {
         return check2(var0, var1.north()) && check2(var0, var1.south()) && check2(var0, var1.east()) && check2(var0, var1.west()) && check2(var0, var1.down());
      } catch (Throwable var3) {
         return false;
      }
   }

   private static boolean check2(WorldChunk var0, BlockPos var1) {
      BlockState var2;
      try {
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3.world == null) {
            return false;
         }

         var2 = var3.world.getBlockState(var1);
      } catch (Throwable var4) {
         return false;
      }

      if (var2.isAir()) {
         return false;
      } else {
         Block var5 = var2.getBlock();
         return var5 != Blocks.STONE
            && var5 != Blocks.DEEPSLATE
            && var5 != Blocks.COBBLESTONE
            && var5 != Blocks.COBBLED_DEEPSLATE
            && var5 != Blocks.GRANITE
            && var5 != Blocks.DIORITE
            && var5 != Blocks.ANDESITE
            && var5 != Blocks.TUFF
            && var5 != Blocks.DIRT
            && var5 != Blocks.COARSE_DIRT
            && var5 != Blocks.GRAVEL
            && var5 != Blocks.SAND
            && var5 != Blocks.NETHERRACK
            && var5 != Blocks.BLACKSTONE
            && var5 != Blocks.BASALT;
      }
   }

   private static boolean check5(WorldChunk var0, BlockPos var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.world == null) {
         return false;
      } else {
         int var3 = 0;
         int[][] var4 = new int[][]{{2, 0, 0}, {-2, 0, 0}, {0, 2, 0}, {0, -2, 0}, {0, 0, 2}, {0, 0, -2}};

         for (int[] var8 : var4) {
            BlockState var9;
            try {
               var9 = var2.world.getBlockState(var1.add(var8[0], var8[1], var8[2]));
            } catch (Throwable var11) {
               return false;
            }

            if (!var9.isAir()) {
               var3++;
            }
         }

         return var3 >= 5;
      }
   }

   private static void run4(MinecraftClient var0) {
      if (var0.world != null && !concurrentHashMap.isEmpty()) {
         ArrayList var1 = new ArrayList<>(concurrentHashMap.keySet());
         if (!var1.isEmpty()) {
            if (intVal13 >= var1.size()) {
               intVal13 = 0;
            }

            int var2 = Math.min(var1.size(), intVal13 + 8);

            for (int var3 = intVal13; var3 < var2; var3++) {
               BlockPos var4 = (BlockPos)var1.get(var3);
               BaseFindingTracker.Inner1 var5 = concurrentHashMap.get(var4);
               if (var5 != null) {
                  BlockState var6;
                  try {
                     var6 = var0.world.getBlockState(var4);
                  } catch (Throwable var10) {
                     continue;
                  }

                  WorldChunk var7;
                  try {
                     var7 = var0.world.getChunkManager().getWorldChunk(var4.getX() >> 4, var4.getZ() >> 4, false);
                  } catch (Throwable var9) {
                     continue;
                  }

                  if (var7 != null) {
                     BaseFindingTracker.Kind var8 = baseFindingTrackerKindOf(var7, var4, var6);
                     if (var8 == var5.baseFindingTrackerKind) {
                        if (var5.intVal < 100) {
                           var5.intVal = Math.min(100, var5.intVal + 5);
                        }
                     } else {
                        var5.intVal -= 25;
                        if (var5.intVal <= 0) {
                           concurrentHashMap.remove(var4, var5);
                        }
                     }
                  }
               }
            }

            intVal13 = var2 >= var1.size() ? 0 : var2;
         }
      }
   }

   private static boolean check8(long var0, Entry var2) {
      return true;
   }

   private static void run16(ClientWorld var0, WorldChunk var1) {
      if (var1 != null) {
         long var2 = longOf(var1.getPos().x, var1.getPos().z);
         set.remove(var2);
      }
   }

   private static void run7(ClientWorld var0, WorldChunk var1) {
      if (bool && var1 != null) {
         long var2 = longOf(var1.getPos().x, var1.getPos().z);
         if (set.add(var2)) {
            arrayDeque.add(var1);
         }
      }
   }

   public static enum Kind {
      HOLLOW_ROOM,
      ARTIFICIAL_STONE,
      DEEP_PLANKS,
      SEALED_HOPPER;

      private static final BaseFindingTracker.Kind[] baseFindingTrackerKindArray = getBaseFindingTrackerKindArray();

      private static BaseFindingTracker.Kind[] getBaseFindingTrackerKindArray() {
         return new BaseFindingTracker.Kind[]{HOLLOW_ROOM, ARTIFICIAL_STONE, DEEP_PLANKS, SEALED_HOPPER};
      }
   }

public final static class Inner1 {
   public final BlockPos class2338;
   public final BaseFindingTracker.Kind baseFindingTrackerKind;
   public final String string;
   public final long longVal;
   public volatile int intVal;

   Inner1(BlockPos var1, BaseFindingTracker.Kind var2, String var3) {
      this.class2338 = var1.toImmutable();
      this.baseFindingTrackerKind = var2;
      this.string = var3 == null ? "" : var3;
      this.longVal = System.currentTimeMillis();
      this.intVal = 100;
   }
}
}

