package dev.fede.render;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public final class BlockScanCache {
   private Predicate<BlockState> predicate;
   private int intervalTicks;
   private int maxChunkRadius;
   private int maxResults;
   private double rangeSq;
   private volatile List<BlockPos> cache = List.of();
   private int tickCounter;

   public BlockScanCache(Predicate<BlockState> predicate, int intervalTicks, int maxChunkRadius, int maxResults, double range) {
      this.predicate = predicate;
      this.intervalTicks = intervalTicks;
      this.maxChunkRadius = maxChunkRadius;
      this.maxResults = maxResults;
      this.rangeSq = range * range;
   }

   public List<BlockPos> get() {
      return this.cache;
   }

   public void clear() {
      this.cache = List.of();
      this.tickCounter = 0;
   }

   public void scan() {
      if (this.tickCounter++ % this.intervalTicks == 0) {
         MinecraftClient mc = MinecraftClient.getInstance();
         ClientWorld level = mc.world;
         ClientPlayerEntity player = mc.player;
         if (level != null && player != null) {
            int radius = Math.min(this.maxChunkRadius, (Integer)mc.options.getViewDistance().getValue());
            int pcx = player.getChunkPos().x;
            int pcz = player.getChunkPos().z;
            List<BlockPos> found = new ArrayList<>();

            for (int cx = pcx - radius; cx <= pcx + radius && found.size() < this.maxResults; cx++) {
               for (int cz = pcz - radius; cz <= pcz + radius && found.size() < this.maxResults; cz++) {
                  WorldChunk chunk = level.getChunk(cx, cz);
                  ChunkSection[] sections = chunk.getSectionArray();
                  int minSectionY = chunk.getBottomSectionCoord();
                  int baseX = chunk.getPos().getStartX();
                  int baseZ = chunk.getPos().getStartZ();

                  for (int s = 0; s < sections.length; s++) {
                     ChunkSection section = sections[s];
                     if (!section.isEmpty() && section.hasAny(this.predicate)) {
                        int baseY = minSectionY + s << 4;

                        for (int y = 0; y < 16; y++) {
                           for (int z = 0; z < 16; z++) {
                              for (int x = 0; x < 16; x++) {
                                 if (this.predicate.test(section.getBlockState(x, y, z))) {
                                    int wx = baseX + x;
                                    int wy = baseY + y;
                                    int wz = baseZ + z;
                                    if (!(player.squaredDistanceTo(wx + 0.5, wy + 0.5, wz + 0.5) > this.rangeSq)) {
                                       found.add(new BlockPos(wx, wy, wz));
                                       if (found.size() >= this.maxResults) {
                                          break;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            this.cache = found;
         }
      }
   }
}

