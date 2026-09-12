package dev.fede.module.impl;

import dev.fede.FeClient;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.SliderSetting;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public final class ChunkFinderModule extends Module {
   private static final int MIN_CHUNK_AGE_TICKS = 200;
   private static final int CHUNKS_PER_TICK = 8;
   public final SliderSetting mergeRadius = this.addSetting(
      new SliderSetting("Merge Radius", "Hives within this many chunks of each other merge to one middle flag", 4.0, 1.0, 8.0, 1.0, " ch")
   );
   private final Set<Long> beehiveChunks = ConcurrentHashMap.newKeySet();
   private final Set<Long> notifiedChunks = ConcurrentHashMap.newKeySet();
   private final Map<Long, Integer> firstLoadedTicks = new ConcurrentHashMap<>();
   private volatile Set<Long> displayChunks = Set.of();
   private int scanCursor;
   private int tickCounter;
   private boolean displayDirty;
   private int lastMergeRadius;

   public ChunkFinderModule() {
      super("Chunk Finder", "Highlights Donut SMP chunks that match a rare world signal worth checking.", Category.RENDER);
   }

   @Override
   protected void onEnable() {
      this.clear();
   }

   @Override
   protected void onDisable() {
      this.clear();
   }

   @Override
   public void onTick() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.world != null && client.player != null) {
         this.tickCounter++;
         int radius = (Integer)client.options.getViewDistance().getValue();
         ChunkPos playerChunk = client.player.getChunkPos();
         int side = radius * 2 + 1;
         int total = side * side;
         if (total > 0) {
            for (int i = 0; i < 8; i++) {
               int index = this.scanCursor % total;
               this.scanCursor = (this.scanCursor + 1) % total;
               int dx = index % side - radius;
               int dz = index / side - radius;
               int cx = playerChunk.x + dx;
               int cz = playerChunk.z + dz;
               WorldChunk chunk = client.world.getChunkManager().getWorldChunk(cx, cz, false);
               if (chunk != null && !chunk.isEmpty()) {
                  long key = ChunkPos.toLong(cx, cz);
                  this.firstLoadedTicks.putIfAbsent(key, this.tickCounter);
                  boolean longLoaded = this.tickCounter - this.firstLoadedTicks.get(key) >= 200;
                  if (longLoaded && hasTargetSignal(chunk)) {
                     if (this.beehiveChunks.add(key)) {
                        this.displayDirty = true;
                     }

                     if (this.notifiedChunks.add(key)) {
                        this.showToast(new ChunkPos(cx, cz));
                     }
                  } else if (this.beehiveChunks.remove(key)) {
                     this.displayDirty = true;
                  }
               }
            }

            if (this.beehiveChunks.removeIf(keyx -> outOfRange(keyx, playerChunk, radius))) {
               this.displayDirty = true;
            }

            this.firstLoadedTicks.keySet().removeIf(keyx -> outOfRange(keyx, playerChunk, radius));
            if (this.displayDirty || this.mergeRadius.getInt() != this.lastMergeRadius) {
               this.rebuildDisplay();
            }
         }
      }
   }

   private void rebuildDisplay() {
      this.displayDirty = false;
      this.lastMergeRadius = this.mergeRadius.getInt();
      Set<Long> raw = new HashSet<>(this.beehiveChunks);
      if (raw.size() < 2) {
         this.displayChunks = Set.copyOf(raw);
      } else {
         int radius = Math.max(1, this.lastMergeRadius);
         Set<Long> display = new HashSet<>();
         Set<Long> visited = new HashSet<>();

         for (long seed : raw) {
            if (visited.add(seed)) {
               List<Long> cluster = new ArrayList<>();
               Deque<Long> frontier = new ArrayDeque<>();
               frontier.add(seed);

               while (!frontier.isEmpty()) {
                  long current = frontier.poll();
                  cluster.add(current);
                  int cx = ChunkPos.getPackedX(current);
                  int cz = ChunkPos.getPackedZ(current);

                  for (int dx = -radius; dx <= radius; dx++) {
                     for (int dz = -radius; dz <= radius; dz++) {
                        if (dx != 0 || dz != 0) {
                           long neighbour = ChunkPos.toLong(cx + dx, cz + dz);
                           if (raw.contains(neighbour) && visited.add(neighbour)) {
                              frontier.add(neighbour);
                           }
                        }
                     }
                  }
               }

               display.add(cluster.size() == 1 ? cluster.get(0) : middleChunk(cluster));
            }
         }

         this.displayChunks = Set.copyOf(display);
      }
   }

   private static long middleChunk(List<Long> cluster) {
      long sumX = 0L;
      long sumZ = 0L;

      for (long key : cluster) {
         sumX += ChunkPos.getPackedX(key);
         sumZ += ChunkPos.getPackedZ(key);
      }

      int mx = (int)Math.round((double)sumX / cluster.size());
      int mz = (int)Math.round((double)sumZ / cluster.size());
      return ChunkPos.toLong(mx, mz);
   }

   private static boolean hasTargetSignal(WorldChunk chunk) {
      for (ChunkSection section : chunk.getSectionArray()) {
         if (section != null && !section.isEmpty() && section.getBlockStateContainer().hasAny(ChunkFinderModule::isFullHoney)) {
            for (int lx = 0; lx < 16; lx++) {
               for (int lz = 0; lz < 16; lz++) {
                  for (int ly = 0; ly < 16; ly++) {
                     if (isFullHoney(section.getBlockState(lx, ly, lz))) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private static boolean isFullHoney(BlockState state) {
      return (state.isOf(Blocks.BEEHIVE) || state.isOf(Blocks.BEE_NEST))
         && state.contains(Properties.HONEY_LEVEL)
         && (Integer)state.get(Properties.HONEY_LEVEL) == 5;
   }

   private static boolean outOfRange(long key, ChunkPos player, int radius) {
      return Math.abs(ChunkPos.getPackedX(key) - player.x) > radius || Math.abs(ChunkPos.getPackedZ(key) - player.z) > radius;
   }

   private void showToast(ChunkPos chunk) {
      if (FeClient.notifications() != null) {
         FeClient.notifications().pushInfo("Chunk Finder · X " + chunk.getCenterX() + " Z " + chunk.getCenterZ());
      }
   }

   public Set<Long> flaggedChunks() {
      return this.displayChunks;
   }

   public void clear() {
      this.beehiveChunks.clear();
      this.notifiedChunks.clear();
      this.firstLoadedTicks.clear();
      this.displayChunks = Set.of();
      this.scanCursor = 0;
      this.tickCounter = 0;
      this.displayDirty = false;
      this.lastMergeRadius = this.mergeRadius.getInt();
   }
}



