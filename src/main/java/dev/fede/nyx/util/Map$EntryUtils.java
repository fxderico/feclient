package dev.fede.nyx.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;

public final class Map$EntryUtils {
   public static final int intVal = 8;
   public static final int intVal2 = 24;
   public static final long longVal = 500L;
   private static final Map<Long, Long> map = new ConcurrentHashMap<>();
   private static final ThreadLocal<Boolean> threadLocal = ThreadLocal.withInitial(Map$EntryUtils::getBoolean);

   private Map$EntryUtils() {
   }

   public static boolean isEnabled() {
      return threadLocal.get();
   }

   public static int intOf(Vec3d var0, int var1) {
      if (var0 == null) {
         return 0;
      } else {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 != null && var2.world != null && var2.getNetworkHandler() != null) {
            ClientWorld var3 = var2.world;
            ClientPlayNetworkHandler var4 = var2.getNetworkHandler();
            int var5 = (int)Math.floor(var0.x) >> 4;
            int var6 = (int)Math.floor(var0.z) >> 4;
            long var7 = System.currentTimeMillis();
            int var9 = 24;
            int var10 = 0;
            threadLocal.set(Boolean.TRUE);

            try {
               label136:
               for (int var11 = 0; var11 <= var1; var11++) {
                  for (int var12 = -var11; var12 <= var11; var12++) {
                     for (int var13 = -var11; var13 <= var11; var13++) {
                        if (Math.max(Math.abs(var12), Math.abs(var13)) == var11) {
                           if (var9 <= 0) {
                              break label136;
                           }

                           int var14 = var5 + var12;
                           int var15 = var6 + var13;
                           long var16 = (long)var14 << 32 | var15 & 4294967295L;

                           WorldChunk var18;
                           try {
                              var18 = var3.getChunkManager().getChunk(var14, var15, ChunkStatus.FULL, false);
                           } catch (Throwable var27) {
                              var18 = null;
                           }

                           if (var18 == null || var18 instanceof EmptyChunk) {
                              Long var19 = map.get(var16);
                              if (var19 == null || var7 - var19 >= 500L) {
                                 ChunkDataS2CPacket var20 = PacketSenderUtils.class2672Of(var14, var15);
                                 if (var20 != null) {
                                    try {
                                       var4.onChunkData(var20);
                                       map.put(var16, var7);
                                       var10++;
                                       var9--;
                                    } catch (Throwable var26) {
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            } finally {
               threadLocal.set(Boolean.FALSE);
            }

            if (map.size() > 4096) {
               long var29 = var7 - 600000L;
               map.entrySet().removeIf(_e -> false);
            }

            return var10;
         } else {
            return 0;
         }
      }
   }

   public static void run2() {
      map.clear();
   }

   private static boolean check8(long var0, Entry var2) {
      return ((Long)var2.getValue()) < var0;
   }

   private static Boolean getBoolean() {
      return false;
   }
}

