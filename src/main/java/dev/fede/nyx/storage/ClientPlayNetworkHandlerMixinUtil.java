package dev.fede.nyx.storage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public final class ClientPlayNetworkHandlerMixinUtil {
   private static final int CHUNKS_PER_TICK = 16;
   private static final Deque<Long> QUEUE = new ArrayDeque<>();
   private static final Set<Long> IN_QUEUE = new HashSet<>();

   private ClientPlayNetworkHandlerMixinUtil() {
   }

   private static long pack(int cx, int cz) {
      return (long)cx << 32 | cz & 4294967295L;
   }

   private static int unpackX(long k) {
      return (int)(k >> 32);
   }

   private static int unpackZ(long k) {
      return (int)k;
   }

   public static void enqueueChunk(int chunkX, int chunkZ) {
      long var2 = pack(chunkX, chunkZ);
      if (IN_QUEUE.add(var2)) {
         QUEUE.addLast(var2);
      }
   }

   public static void enqueueChunk(ChunkPos var0) {
      enqueueChunk(var0.x, var0.z);
   }

   public static void tick(MinecraftClient var0) {
      if (var0 != null && var0.world != null && var0.player != null) {
         if (!QUEUE.isEmpty()) {
            ClientChunkManager var1 = var0.world.getChunkManager();
            int var2 = Math.min(16, QUEUE.size());

            for (int var3 = 0; var3 < var2; var3++) {
               Long var4 = QUEUE.pollFirst();
               if (var4 == null) {
                  break;
               }

               IN_QUEUE.remove(var4);
               int var5 = unpackX(var4);
               int var6 = unpackZ(var4);

               WorldChunk var7;
               try {
                  var7 = var1.getWorldChunk(var5, var6, false);
               } catch (Throwable var9) {
                  var7 = null;
               }

               if (var7 != null) {
                  Chest$KindUtils.onChunkLoad(var7);
               }
            }
         }
      }
   }

   public static void clearQueue() {
      QUEUE.clear();
      IN_QUEUE.clear();
   }
}

