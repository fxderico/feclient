package dev.fede.suschunk;

import dev.fede.FeClient;
import dev.fede.module.Modules;
import dev.fede.util.UiSounds;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class SusChunkScanner {
   public static final int POINTS_PER_SENSITIVITY = 5;
   private static final int GEODE_LINK_DISTANCE = 5;
   private static final boolean DEBUG_LOG = Boolean.getBoolean("sixseven.sus.debug");
   private Modules.SusChunkFinderModule module;
   private final Map<Long, SusChunkScanner.ChunkScore> scores = new ConcurrentHashMap<>();
   private final Deque<Long> queue = new ArrayDeque<>();
   private final Set<Long> lightRescans = ConcurrentHashMap.newKeySet();
   private final Set<Long> alertedChunks = new HashSet<>();
   private volatile List<SusChunkScanner.Flag> flags = List.of();
   private volatile List<SusChunkScanner.Zone> zones = List.of();
   private ChunkPos lastQueueCenter;
   private int tickCounter;

   public SusChunkScanner(Modules.SusChunkFinderModule module) {
      this.module = module;
      ServerLightCache.get().addDirtyListener(this.lightRescans::add);
   }

   public List<SusChunkScanner.Flag> flags() {
      return this.flags;
   }

   public List<SusChunkScanner.Zone> zones() {
      return this.zones;
   }

   public int threshold() {
      return this.module.sensitivity.getInt() * 5;
   }

   public void clear() {
      this.scores.clear();
      this.queue.clear();
      this.lightRescans.clear();
      this.alertedChunks.clear();
      this.flags = List.of();
      this.zones = List.of();
      this.lastQueueCenter = null;
   }

   public void tick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.world != null && mc.player != null) {
         try {
            this.refillQueueIfNeeded(mc);
            int budget = this.module.scanSpeed.getInt();
            long deadline = System.nanoTime() + 2000000L;
            int scanned = this.scanLightRescans(mc, budget, deadline);
            this.scanQueue(mc, budget - scanned, deadline);
            if (++this.tickCounter % 10 == 0) {
               this.rebuild(mc);
            }
         } catch (Exception var6) {
            FeClient.LOGGER.warn("SusChunkFinder scan error: {}", var6.toString());
         }
      }
   }

   private int scanLightRescans(MinecraftClient mc, int budget, long deadline) {
      if (this.lightRescans.isEmpty()) {
         return 0;
      } else {
         int scanned = 0;
         Iterator<Long> iterator = this.lightRescans.iterator();

         while (iterator.hasNext() && scanned < budget && System.nanoTime() < deadline) {
            long key = iterator.next();
            iterator.remove();
            WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(ChunkPos.getPackedX(key), ChunkPos.getPackedZ(key), false);
            if (chunk != null && this.scores.containsKey(key)) {
               this.scores.put(key, this.scanChunk(mc, chunk));
               scanned++;
            }
         }

         return scanned;
      }
   }

   private void scanQueue(MinecraftClient mc, int budget, long deadline) {
      int scanned = 0;
      int polled = 0;

      while (scanned < budget && polled < 128 && !this.queue.isEmpty() && System.nanoTime() < deadline) {
         polled++;
         long key = this.queue.pollFirst();
         if (!this.scores.containsKey(key)) {
            WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(ChunkPos.getPackedX(key), ChunkPos.getPackedZ(key), false);
            if (chunk != null) {
               this.scores.put(key, this.scanChunk(mc, chunk));
               scanned++;
            }
         }
      }
   }

   private void refillQueueIfNeeded(MinecraftClient mc) {
      ChunkPos center = mc.player.getChunkPos();
      if (this.queue.isEmpty()
         || this.lastQueueCenter == null
         || Math.max(Math.abs(center.x - this.lastQueueCenter.x), Math.abs(center.z - this.lastQueueCenter.z)) >= 3) {
         this.lastQueueCenter = center;
         this.queue.clear();
         int radius = Math.min((Integer)mc.options.getViewDistance().getValue() + 1, 16);
         List<Long> order = new ArrayList<>();

         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               long key = ChunkPos.toLong(center.x + dx, center.z + dz);
               if (!this.scores.containsKey(key)) {
                  order.add(key);
               }
            }
         }

         order.sort(Comparator.comparingDouble(k -> Math.hypot(ChunkPos.getPackedX(k) - center.x, ChunkPos.getPackedZ(k) - center.z)));
         this.queue.addAll(order);
      }
   }

   private static boolean isPlantTarget(BlockState state) {
      Block block = state.getBlock();
      return block == Blocks.KELP
         || block == Blocks.KELP_PLANT
         || block == Blocks.BAMBOO
         || block == Blocks.SWEET_BERRY_BUSH
         || block == Blocks.VINE
         || block == Blocks.POINTED_DRIPSTONE;
   }

   private static boolean isAmethystStructure(BlockState state) {
      return state.isOf(Blocks.BUDDING_AMETHYST) || state.isOf(Blocks.AMETHYST_BLOCK);
   }

   private SusChunkScanner.ChunkScore scanChunk(MinecraftClient mc, WorldChunk chunk) {
      SusChunkScanner.ChunkScore result = new ChunkScore(chunk.getPos().toLong());
      if (this.module.amethyst.get()) {
         this.detectAmethyst(mc, chunk, result);
      }

      this.detectGrowth(mc, chunk, result);
      result.computeScore();
      if (DEBUG_LOG && result.score > 0.0) {
         FeClient.LOGGER.info("[sus] chunk {} score={} hits={}", new Object[]{chunk.getPos(), result.score, result.hits});
      }

      return result;
   }

   private void detectAmethyst(MinecraftClient mc, WorldChunk chunk, SusChunkScanner.ChunkScore result) {
      List<BlockPos> cells = ServerLightCache.get().light5Positions(chunk.getPos().x, chunk.getPos().z);
      if (!cells.isEmpty()) {
         Mutable cursor = new Mutable();

         for (BlockPos cell : cells) {
            BlockState state = mc.world.getBlockState(cell);
            if ((state.isAir() || state.isOf(Blocks.AMETHYST_CLUSTER)) && hasAmethystNeighbour(mc, cell, cursor)) {
               result.amethystCells.add(cell.toImmutable());
               if (DEBUG_LOG) {
                  FeClient.LOGGER.info("[sus] AMETHYST light-5 cell @{} ({})", cell, state.isAir() ? "hidden" : "visible");
               }
            }
         }
      }
   }

   private static boolean hasAmethystNeighbour(MinecraftClient mc, BlockPos cell, Mutable cursor) {
      for (int dx = -1; dx <= 1; dx++) {
         for (int dy = -1; dy <= 1; dy++) {
            for (int dz = -1; dz <= 1; dz++) {
               if (dx != 0 || dy != 0 || dz != 0) {
                  cursor.set(cell.getX() + dx, cell.getY() + dy, cell.getZ() + dz);
                  if (isAmethystStructure(mc.world.getBlockState(cursor))) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private void detectGrowth(MinecraftClient mc, WorldChunk chunk, SusChunkScanner.ChunkScore result) {
      ChunkSection[] sections = chunk.getSectionArray();
      int minSectionY = chunk.getBottomSectionCoord();
      int loSection = ChunkSectionPos.getSectionCoord(-12);
      int hiSection = ChunkSectionPos.getSectionCoord(80);
      int baseX = chunk.getPos().getStartX();
      int baseZ = chunk.getPos().getStartZ();
      Mutable cursor = new Mutable();

      for (int s = 0; s < sections.length; s++) {
         int sectionY = minSectionY + s;
         if (sectionY >= loSection && sectionY <= hiSection) {
            ChunkSection section = sections[s];
            if (!section.isEmpty() && section.getBlockStateContainer().hasAny(SusChunkScanner::isPlantTarget)) {
               int baseY = sectionY << 4;

               for (int y = 0; y < 16; y++) {
                  int worldY = baseY + y;
                  if (worldY >= -12 && worldY <= 80) {
                     for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < 16; x++) {
                           BlockState state = section.getBlockState(x, y, z);
                           if (isPlantTarget(state)) {
                              cursor.set(baseX + x, worldY, baseZ + z);
                              this.inspectPlant(mc, state, cursor, result);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void inspectPlant(MinecraftClient mc, BlockState state, BlockPos pos, SusChunkScanner.ChunkScore result) {
      Block block = state.getBlock();
      if ((block == Blocks.KELP || block == Blocks.KELP_PLANT) && this.module.kelp.get()) {
         BlockState above = mc.world.getBlockState(pos.up());
         if (above.isOf(Blocks.KELP) || above.isOf(Blocks.KELP_PLANT)) {
            return;
         }

         int height = columnLength(mc, pos, Direction.DOWN, Blocks.KELP, Blocks.KELP_PLANT);
         boolean maxAge = block == Blocks.KELP && state.contains(Properties.AGE_25) && (Integer)state.get(Properties.AGE_25) == 25;
         if (maxAge && height >= 8 || height >= 14) {
            result.add(SusChunkScanner.SignalType.KELP, pos);
            if (DEBUG_LOG) {
               FeClient.LOGGER.info("[sus] KELP @{} height={} maxAge={}", new Object[]{pos, height, maxAge});
            }
         }
      } else if (block == Blocks.BAMBOO && this.module.bamboo.get()) {
         if (mc.world.getBlockState(pos.down()).isOf(Blocks.BAMBOO)) {
            return;
         }

         int height = columnLength(mc, pos, Direction.UP, Blocks.BAMBOO);
         if (height >= 12) {
            result.add(SusChunkScanner.SignalType.BAMBOO, pos);
            if (DEBUG_LOG) {
               FeClient.LOGGER.info("[sus] BAMBOO @{} height={}", pos, height);
            }
         }
      } else if (block == Blocks.SWEET_BERRY_BUSH && this.module.berries.get()) {
         if (state.contains(Properties.AGE_3) && (Integer)state.get(Properties.AGE_3) == 3) {
            result.add(SusChunkScanner.SignalType.BERRIES, pos);
            if (DEBUG_LOG) {
               FeClient.LOGGER.info("[sus] BERRIES max age @{}", pos);
            }
         }
      } else if (block == Blocks.VINE && this.module.vines.get()) {
         if (mc.world.getBlockState(pos.up()).isOf(Blocks.VINE)) {
            return;
         }

         int length = columnLength(mc, pos, Direction.DOWN, Blocks.VINE);
         if (length >= 7) {
            result.add(SusChunkScanner.SignalType.VINES, pos);
            if (DEBUG_LOG) {
               FeClient.LOGGER.info("[sus] VINES @{} hang={}", pos, length);
            }
         }
      } else if (block == Blocks.POINTED_DRIPSTONE && this.module.dripstone.get()) {
         if (mc.world.getBlockState(pos.up()).isOf(Blocks.POINTED_DRIPSTONE) || !mc.world.getBlockState(pos.down()).isOf(Blocks.POINTED_DRIPSTONE)) {
            return;
         }

         int length = columnLength(mc, pos, Direction.DOWN, Blocks.POINTED_DRIPSTONE);
         if (length >= 5) {
            result.add(SusChunkScanner.SignalType.DRIPSTONE, pos);
            if (DEBUG_LOG) {
               FeClient.LOGGER.info("[sus] DRIPSTONE @{} length={}", pos, length);
            }
         }
      }
   }

   private static int columnLength(MinecraftClient mc, BlockPos start, Direction dir, Block... blocks) {
      int length = 1;

      label21:
      for (Mutable cursor = start.mutableCopy(); length < 40; length++) {
         cursor.move(dir);
         BlockState state = mc.world.getBlockState(cursor);

         for (Block block : blocks) {
            if (state.isOf(block)) {
               continue label21;
            }
         }
         break;
      }

      return length;
   }

   private void rebuild(MinecraftClient mc) {
      this.scores.keySet().removeIf(key -> mc.world.getChunkManager().getWorldChunk(ChunkPos.getPackedX(key), ChunkPos.getPackedZ(key), false) == null);
      int threshold = this.threshold();
      Map<Long, SusChunkScanner.FlagAggregate> aggregates = new HashMap<>();

      for (SusChunkScanner.ChunkScore score : this.scores.values()) {
         if (score.score >= threshold) {
            SusChunkScanner.FlagAggregate agg = aggregates.computeIfAbsent(score.chunkKey, SusChunkScanner.FlagAggregate::new);
            agg.score = Math.max(agg.score, score.score);
            agg.hitWeight = agg.hitWeight + score.hitWeight;
            agg.hitX = agg.hitX + score.hitX;
            agg.hitZ = agg.hitZ + score.hitZ;
         }
      }

      for (SusChunkScanner.Geode geode : this.clusterGeodes()) {
         if (!(geode.score() < threshold)) {
            for (long chunkKey : geode.chunks()) {
               SusChunkScanner.FlagAggregate agg = aggregates.computeIfAbsent(chunkKey, SusChunkScanner.FlagAggregate::new);
               agg.score = Math.max(agg.score, geode.score());
            }

            double weight = SusChunkScanner.SignalType.AMETHYST.weight;

            for (BlockPos cell : geode.cells()) {
               SusChunkScanner.FlagAggregate agg = aggregates.get(ChunkPos.toLong(cell.getX() >> 4, cell.getZ() >> 4));
               if (agg != null) {
                  agg.hitWeight += weight;
                  agg.hitX = agg.hitX + (cell.getX() + 0.5) * weight;
                  agg.hitZ = agg.hitZ + (cell.getZ() + 0.5) * weight;
               }
            }

            if (DEBUG_LOG) {
               FeClient.LOGGER
                  .info("[sus] GEODE {} cells / {} chunks, score {} -> FLAG", new Object[]{geode.cells().size(), geode.chunks().size(), geode.score()});
            }
         }
      }

      List<SusChunkScanner.Flag> flagged = new ArrayList<>();

      for (SusChunkScanner.FlagAggregate agg : aggregates.values()) {
         flagged.add(new Flag(agg.chunkKey, agg.score));
      }

      this.flags = List.copyOf(flagged);
      this.zones = this.buildZones(flagged, aggregates);
      this.fireAlerts(mc);
   }

   private List<SusChunkScanner.Geode> clusterGeodes() {
      List<BlockPos> cells = new ArrayList<>();

      for (SusChunkScanner.ChunkScore score : this.scores.values()) {
         cells.addAll(score.amethystCells);
      }

      int n = cells.size();
      if (n == 0) {
         return List.of();
      } else {
         int[] parent = new int[n];
         int i = 0;

         while (i < n) {
            parent[i] = i++;
         }

         for (int ix = 0; ix < n; ix++) {
            BlockPos a = cells.get(ix);

            for (int j = ix + 1; j < n; j++) {
               BlockPos b = cells.get(j);
               int chebyshev = Math.max(Math.abs(a.getX() - b.getX()), Math.max(Math.abs(a.getY() - b.getY()), Math.abs(a.getZ() - b.getZ())));
               if (chebyshev <= 5) {
                  parent[find(parent, ix)] = find(parent, j);
               }
            }
         }

         Map<Integer, List<BlockPos>> groups = new HashMap<>();

         for (int ix = 0; ix < n; ix++) {
            groups.computeIfAbsent(find(parent, ix), k -> new ArrayList<>()).add(cells.get(ix));
         }

         List<SusChunkScanner.Geode> geodes = new ArrayList<>();

         for (List<BlockPos> group : groups.values()) {
            Set<Long> chunks = new HashSet<>();
            double sx = 0.0;
            double sz = 0.0;

            for (BlockPos c : group) {
               chunks.add(ChunkPos.toLong(c.getX() >> 4, c.getZ() >> 4));
               sx += c.getX() + 0.5;
               sz += c.getZ() + 0.5;
            }

            double score = SusChunkScanner.SignalType.AMETHYST.weight * Math.min(group.size(), SusChunkScanner.SignalType.AMETHYST.cap);
            geodes.add(new Geode(List.copyOf(group), Set.copyOf(chunks), score, sx / group.size(), sz / group.size()));
         }

         return geodes;
      }
   }

   private static int find(int[] parent, int i) {
      while (parent[i] != i) {
         parent[i] = parent[parent[i]];
         i = parent[i];
      }

      return i;
   }

   private List<SusChunkScanner.Zone> buildZones(List<SusChunkScanner.Flag> flagged, Map<Long, SusChunkScanner.FlagAggregate> aggregates) {
      int radius = Math.max(1, this.module.mergeRadius.getInt());
      Map<Long, SusChunkScanner.Flag> byKey = new HashMap<>();

      for (SusChunkScanner.Flag flag : flagged) {
         byKey.put(flag.chunkKey(), flag);
      }

      List<SusChunkScanner.Zone> built = new ArrayList<>();
      Set<Long> visited = new HashSet<>();

      for (SusChunkScanner.Flag seed : flagged) {
         if (visited.add(seed.chunkKey())) {
            List<SusChunkScanner.Flag> group = new ArrayList<>();
            Deque<SusChunkScanner.Flag> frontier = new ArrayDeque<>(List.of(seed));

            while (!frontier.isEmpty()) {
               SusChunkScanner.Flag current = frontier.poll();
               group.add(current);
               int cx = ChunkPos.getPackedX(current.chunkKey());
               int cz = ChunkPos.getPackedZ(current.chunkKey());

               for (int dx = -radius; dx <= radius; dx++) {
                  for (int dz = -radius; dz <= radius; dz++) {
                     if (dx != 0 || dz != 0) {
                        SusChunkScanner.Flag neighbour = byKey.get(ChunkPos.toLong(cx + dx, cz + dz));
                        if (neighbour != null && visited.add(neighbour.chunkKey())) {
                           frontier.add(neighbour);
                        }
                     }
                  }
               }
            }

            built.add(this.makeZone(group, aggregates));
         }
      }

      built.sort(Comparator.comparingDouble(SusChunkScanner.Zone::totalScore).reversed());
      return built;
   }

   private SusChunkScanner.Zone makeZone(List<SusChunkScanner.Flag> group, Map<Long, SusChunkScanner.FlagAggregate> aggregates) {
      Set<Long> members = new HashSet<>();
      double totalScore = 0.0;
      double maxScore = 0.0;
      double hitWeight = 0.0;
      double hitX = 0.0;
      double hitZ = 0.0;
      double chunkX = 0.0;
      double chunkZ = 0.0;

      for (SusChunkScanner.Flag flag : group) {
         members.add(flag.chunkKey());
         totalScore += flag.score();
         maxScore = Math.max(maxScore, flag.score());
         SusChunkScanner.FlagAggregate agg = aggregates.get(flag.chunkKey());
         if (agg != null && agg.hitWeight > 0.0) {
            hitWeight += agg.hitWeight;
            hitX += agg.hitX;
            hitZ += agg.hitZ;
         }

         chunkX += (ChunkPos.getPackedX(flag.chunkKey()) * 16 + 8) * flag.score();
         chunkZ += (ChunkPos.getPackedZ(flag.chunkKey()) * 16 + 8) * flag.score();
      }

      double cx = hitWeight > 0.0 ? hitX / hitWeight : chunkX / totalScore;
      double cz = hitWeight > 0.0 ? hitZ / hitWeight : chunkZ / totalScore;
      return new Zone(Set.copyOf(members), cx, cz, totalScore, maxScore);
   }

   private void fireAlerts(MinecraftClient mc) {
      for (SusChunkScanner.Zone zone : this.zones) {
         boolean isNew = true;

         for (long member : zone.members()) {
            if (this.alertedChunks.contains(member)) {
               isNew = false;
               break;
            }
         }

         if (!isNew) {
            this.alertedChunks.addAll(zone.members());
         } else {
            this.alertedChunks.addAll(zone.members());
            if (!this.module.notifications.check("Off")) {
               int bx = (int)Math.round(zone.centroidX());
               int bz = (int)Math.round(zone.centroidZ());
               int dist = (int)Math.hypot(bx - mc.player.getX(), bz - mc.player.getZ());
               String message = "Sus zone · " + dist + "m · " + bx + ", " + bz;
               if (this.module.notifications.check("Toast") && FeClient.notifications() != null) {
                  FeClient.notifications().pushInfo(message);
                  UiSounds.notification(true);
               } else if (this.module.notifications.check("Chat")) {
                  mc.player.sendMessage(Text.literal("§d[FE] §fnull"), false);
               }
            }
         }
      }
   }

   public final class ChunkScore {
      public long chunkKey;
      public final EnumMap<SusChunkScanner.SignalType, Integer> hits = new EnumMap<>(SusChunkScanner.SignalType.class);
      public final List<BlockPos> amethystCells = new ArrayList<>();
      public double score;
      double hitWeight;
      double hitX;
      double hitZ;

      ChunkScore(long chunkKey) {
         this.chunkKey = chunkKey;
      }

      void add(SusChunkScanner.SignalType type, BlockPos pos) {
         this.hits.merge(type, 1, Integer::sum);
         this.hitWeight = this.hitWeight + type.weight;
         this.hitX = this.hitX + (pos.getX() + 0.5) * type.weight;
         this.hitZ = this.hitZ + (pos.getZ() + 0.5) * type.weight;
      }

      void computeScore() {
         double total = 0.0;

         for (Entry<SusChunkScanner.SignalType, Integer> entry : this.hits.entrySet()) {
            total += entry.getKey().weight * Math.min(entry.getValue(), entry.getKey().cap);
         }

         this.score = total;
      }
   }

   public final class Flag {
      private long chunkKey;
      private double score;

      public Flag(long chunkKey, double score) {
         this.chunkKey = chunkKey;
         this.score = score;
      }

      public long chunkKey() {
         return this.chunkKey;
      }

      public double score() {
         return this.score;
      }
   }

   final class FlagAggregate {
      final long chunkKey;
      double score;
      double hitWeight;
      double hitX;
      double hitZ;

      FlagAggregate(long chunkKey) {
         this.chunkKey = chunkKey;
      }
   }

   public final class Geode {
      private List<BlockPos> cells;
      private Set<Long> chunks;
      private double score;
      private double centroidX;
      private double centroidZ;

      public Geode(List<BlockPos> cells, Set<Long> chunks, double score, double centroidX, double centroidZ) {
         this.cells = cells;
         this.chunks = chunks;
         this.score = score;
         this.centroidX = centroidX;
         this.centroidZ = centroidZ;
      }

      public List<BlockPos> cells() {
         return this.cells;
      }

      public Set<Long> chunks() {
         return this.chunks;
      }

      public double score() {
         return this.score;
      }

      public double centroidX() {
         return this.centroidX;
      }

      public double centroidZ() {
         return this.centroidZ;
      }
   }

   public enum SignalType {
      AMETHYST(6.0, 16),
      KELP(2.0, 6),
      BAMBOO(2.0, 6),
      BERRIES(2.0, 5),
      VINES(2.0, 6),
      DRIPSTONE(2.0, 5);

      public double weight;
      public int cap;

      private SignalType(double weight, int cap) {
         this.weight = weight;
         this.cap = cap;
      }

      private static SusChunkScanner.SignalType[] $values() {
         return new SusChunkScanner.SignalType[]{AMETHYST, KELP, BAMBOO, BERRIES, VINES, DRIPSTONE};
      }
   }

   public final class Zone {
      private Set<Long> members;
      private double centroidX;
      private double centroidZ;
      private double totalScore;
      private double maxScore;

      public Zone(Set<Long> members, double centroidX, double centroidZ, double totalScore, double maxScore) {
         this.members = members;
         this.centroidX = centroidX;
         this.centroidZ = centroidZ;
         this.totalScore = totalScore;
         this.maxScore = maxScore;
      }

      public Set<Long> members() {
         return this.members;
      }

      public double centroidX() {
         return this.centroidX;
      }

      public double centroidZ() {
         return this.centroidZ;
      }

      public double totalScore() {
         return this.totalScore;
      }

      public double maxScore() {
         return this.maxScore;
      }
   }
}



