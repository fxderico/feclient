package dev.fede.suschunk;

import dev.fede.FeClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.LongConsumer;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public final class ServerLightCache {
   public static final int SCAN_Y_MIN = -12;
   public static final int SCAN_Y_MAX = 80;
   private static final boolean DEBUG_LOG = Boolean.getBoolean("sixseven.sus.debug");
   private static final int TARGET_MIN_SECTION = ChunkSectionPos.getSectionCoord(-12);
   private static final int TARGET_MAX_SECTION = ChunkSectionPos.getSectionCoord(80);
   private static final ServerLightCache INSTANCE = new ServerLightCache();
   private final ConcurrentHashMap<Long, byte[]> sections = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<Long, short[]> light5Cells = new ConcurrentHashMap<>();
   private final Set<Long> dirtyChunks = ConcurrentHashMap.newKeySet();
   private final List<LongConsumer> dirtyListeners = new CopyOnWriteArrayList<>();

   private ServerLightCache() {
   }

   public static ServerLightCache get() {
      return INSTANCE;
   }

   public void clear() {
      this.sections.clear();
      this.light5Cells.clear();
      this.dirtyChunks.clear();
   }

   public boolean consumeDirty(int chunkX, int chunkZ) {
      return this.dirtyChunks.remove(ChunkPos.toLong(chunkX, chunkZ));
   }

   public void addDirtyListener(LongConsumer listener) {
      this.dirtyListeners.add(listener);
   }

   public void markDirty(int chunkX, int chunkZ) {
      long key = ChunkPos.toLong(chunkX, chunkZ);
      this.dirtyChunks.add(key);

      for (LongConsumer listener : this.dirtyListeners) {
         listener.accept(key);
      }
   }

   public void ingest(int chunkX, int chunkZ, LightData data, World level) {
      if (level != null) {
         int minLightSection = level.getBottomSectionCoord() - 1;
         BitSet blockMask = data.getInitedBlock();
         BitSet emptyMask = data.getUninitedBlock();
         List<byte[]> updates = data.getBlockNibbles();
         int updateIndex = 0;
         boolean changed = false;

         for (int i = blockMask.nextSetBit(0); i >= 0; i = blockMask.nextSetBit(i + 1)) {
            byte[] bytes = updateIndex < updates.size() ? updates.get(updateIndex) : null;
            updateIndex++;
            int sectionY = minLightSection + i;
            if (bytes != null && bytes.length == 2048 && sectionY >= TARGET_MIN_SECTION && sectionY <= TARGET_MAX_SECTION) {
               long sectionKey = ChunkSectionPos.asLong(chunkX, sectionY, chunkZ);
               this.sections.put(sectionKey, (byte[])bytes.clone());
               int light5Count = this.cacheLight5(sectionKey, bytes);
               if (DEBUG_LOG && light5Count > 0) {
                  FeClient.LOGGER.info("[sus-light] section ({},{},{}): {} light-5 positions", new Object[]{chunkX, sectionY, chunkZ, light5Count});
               }

               changed = true;
            }
         }

         for (int ix = emptyMask.nextSetBit(0); ix >= 0; ix = emptyMask.nextSetBit(ix + 1)) {
            int sectionY = minLightSection + ix;
            if (sectionY >= TARGET_MIN_SECTION && sectionY <= TARGET_MAX_SECTION) {
               long sectionKey = ChunkSectionPos.asLong(chunkX, sectionY, chunkZ);
               this.light5Cells.remove(sectionKey);
               if (this.sections.remove(sectionKey) != null) {
                  changed = true;
               }
            }
         }

         if (changed) {
            this.markDirty(chunkX, chunkZ);
         }
      }
   }

   public int serverBlockLight(int x, int y, int z) {
      if (y >= -12 && y <= 80) {
         byte[] data = this.sections
            .get(ChunkSectionPos.asLong(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(y), ChunkSectionPos.getSectionCoord(z)));
         if (data == null) {
            return -1;
         } else {
            int index = (y & 15) << 8 | (z & 15) << 4 | x & 15;
            int b = data[index >> 1] & 255;
            return (index & 1) == 0 ? b & 15 : b >> 4 & 15;
         }
      } else {
         return -1;
      }
   }

   public boolean isServerLight5(int x, int y, int z) {
      return this.serverBlockLight(x, y, z) == 5;
   }

   private int cacheLight5(long sectionKey, byte[] nibbles) {
      short[] buffer = null;
      int count = 0;

      for (int index = 0; index < 4096; index++) {
         int b = nibbles[index >> 1] & 255;
         int light = (index & 1) == 0 ? b & 15 : b >> 4 & 15;
         if (light == 5) {
            if (buffer == null) {
               buffer = new short[16];
            } else if (count == buffer.length) {
               buffer = Arrays.copyOf(buffer, buffer.length * 2);
            }

            buffer[count++] = (short)index;
         }
      }

      if (count == 0) {
         this.light5Cells.remove(sectionKey);
      } else {
         this.light5Cells.put(sectionKey, Arrays.copyOf(buffer, count));
      }

      return count;
   }

   public List<BlockPos> light5Positions(int chunkX, int chunkZ) {
      List<BlockPos> out = null;
      int baseX = chunkX << 4;
      int baseZ = chunkZ << 4;

      for (int sy = TARGET_MIN_SECTION; sy <= TARGET_MAX_SECTION; sy++) {
         short[] cells = this.light5Cells.get(ChunkSectionPos.asLong(chunkX, sy, chunkZ));
         if (cells != null) {
            int baseY = sy << 4;

            for (short cell : cells) {
               int index = cell & '\uffff';
               int y = baseY + (index >> 8);
               if (y >= -12 && y <= 80) {
                  if (out == null) {
                     out = new ArrayList<>();
                  }

                  out.add(new BlockPos(baseX + (index & 15), y, baseZ + (index >> 4 & 15)));
               }
            }
         }
      }

      return out == null ? List.of() : out;
   }

   public void injectForTest(int x, int y, int z, int level) {
      int cx = ChunkSectionPos.getSectionCoord(x);
      int cz = ChunkSectionPos.getSectionCoord(z);
      long key = ChunkSectionPos.asLong(cx, ChunkSectionPos.getSectionCoord(y), cz);
      byte[] data = this.sections.computeIfAbsent(key, k -> new byte[2048]);
      int index = (y & 15) << 8 | (z & 15) << 4 | x & 15;
      int bi = index >> 1;
      int shift = (index & 1) * 4;
      data[bi] = (byte)(data[bi] & ~(15 << shift) | (level & 15) << shift);
      this.cacheLight5(key, data);
      this.markDirty(cx, cz);
   }

   public boolean hasChunk(int chunkX, int chunkZ) {
      for (int sy = TARGET_MIN_SECTION; sy <= TARGET_MAX_SECTION; sy++) {
         if (this.sections.containsKey(ChunkSectionPos.asLong(chunkX, sy, chunkZ))) {
            return true;
         }
      }

      return false;
   }
}



