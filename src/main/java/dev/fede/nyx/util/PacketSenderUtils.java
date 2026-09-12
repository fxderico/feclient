package dev.fede.nyx.util;

import dev.fede.nyx.tracker.ChunkActivityScanner;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public final class PacketSenderUtils {
   public static volatile boolean bool = true;
   public static volatile boolean bool2 = true;
   public static volatile int intVal = 2048;
   private static final Map<Long, PacketSenderUtils.Inner1> map = new LinkedHashMap<Long, PacketSenderUtils.Inner1>(512, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, PacketSenderUtils.Inner1> var1) {
         return this.size() > PacketSenderUtils.intVal;
      }
   };

   private PacketSenderUtils() {
   }

   public static ChunkDataS2CPacket class2672Of(int var0, int var1) {
      PacketSenderUtils.Inner1 var2 = map.get(ChunkPos.toLong(var0, var1));
      return var2 != null ? var2.class2672 : null;
   }

   public static void run5(int var0, int var1) {
      if (bool) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 != null && var2.world != null) {
            WorldChunk var3 = var2.world.getChunk(var0, var1);
            if (var3 != null) {
               long var4 = ChunkPos.toLong(var0, var1);
               PacketSenderUtils.Inner1 var6 = map.get(var4);
               ChunkSection[] var7 = var3.getSectionArray();
               int var8 = var3.getBottomY();
               boolean var9 = false;
               if (var6 != null && bool2) {
                  int var10 = Math.min(var7.length, var6.class2680Array.length);
                  int var11 = var0 << 4;
                  int var12 = var1 << 4;

                  for (int var13 = 0; var13 < var10; var13++) {
                     ChunkSection var14 = var7[var13];
                     BlockState[] var15 = var6.class2680Array[var13];
                     if (var15 != null && (var14 == null || var14.isEmpty())) {
                        int var16 = var8 + (var13 << 4);
                        Mutable var17 = new Mutable();

                        for (int var18 = 0; var18 < 16; var18++) {
                           for (int var19 = 0; var19 < 16; var19++) {
                              for (int var20 = 0; var20 < 16; var20++) {
                                 BlockState var21 = var15[var18 << 8 | var19 << 4 | var20];
                                 if (var21 != null && !var21.isAir()) {
                                    var17.set(var11 | var20, var16 + var18, var12 | var19);
                                    var3.setBlockState(var17, var21, 2);
                                    var9 = true;
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (var9) {
                  try {
                     ChunkActivityScanner.run12(var3);
                  } catch (Throwable var23) {
                  }
               }

               BlockState[][] var24 = new BlockState[var7.length][];

               for (int var25 = 0; var25 < var7.length; var25++) {
                  ChunkSection var27 = var7[var25];
                  if (var27 != null && !var27.isEmpty()) {
                     BlockState[] var29 = new BlockState[4096];

                     for (int var31 = 0; var31 < 16; var31++) {
                        for (int var32 = 0; var32 < 16; var32++) {
                           for (int var33 = 0; var33 < 16; var33++) {
                              var29[var31 << 8 | var32 << 4 | var33] = var27.getBlockState(var33, var31, var32);
                           }
                        }
                     }

                     var24[var25] = var29;
                  }
               }

               ChunkDataS2CPacket var26 = null;

               try {
                  int var28 = var7.length;
                  BitSet var30 = new BitSet(var28);
                  var30.set(0, var28);
                  var26 = new ChunkDataS2CPacket(var3, var2.world.getLightingProvider(), var30, var30);
               } catch (Throwable var22) {
               }

               map.put(var4, new PacketSenderUtils.Inner1(var24, var26));
            }
         }
      }
   }

   public static void run() {
      map.clear();
   }

   public static int getInt2() {
      return map.size();
   }

   private static void run15(ClientPlayNetworkHandler var0, MinecraftClient var1) {
      run();
   }

   private static void run16(ClientPlayNetworkHandler var0, PacketSender var1, MinecraftClient var2) {
      run();
   }

   static {
      try {
         ClientPlayConnectionEvents.JOIN.register(PacketSenderUtils::run16);
         ClientPlayConnectionEvents.DISCONNECT.register(PacketSenderUtils::run15);
      } catch (Throwable var1) {
      }
   }

public final static class Inner1 {
   final BlockState[][] class2680Array;
   volatile ChunkDataS2CPacket class2672;

   Inner1(BlockState[][] var1, ChunkDataS2CPacket var2) {
      this.class2680Array = var1;
      this.class2672 = var2;
   }
}
}

