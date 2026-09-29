package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

/**
 * OGChunkFinder — DonutSMP multi-signal base finder (ported from WaterClient Dev,
 * rebuilt on feClient's ListUtils renderer + nyx Module lifecycle).
 *
 * It scans a small radius of loaded chunks off the client thread and flags the
 * FIRST chunk that trips any enabled "someone lives/works here" heuristic:
 *   - tall seagrass carpet (aquarium / water base decoration dumped in bulk)
 *   - powered repeaters (active redstone — farms, doors, contraptions)
 *   - light sources sitting right at bedrock (Y -1..1) — buried spawner lighting
 *   - glow lichen with another player within ~112 blocks (someone standing at it)
 *   - >=2 polar bears in ocean/ice biome (imported mob storage / spawn farm)
 *   - beehives holding bees (deliberately populated apiary)
 *   - cobbled deepslate below Y50 (hand-mined stone left behind)
 *   - rotated (non-Y-axis) deepslate below Y63 (placed by a player, not natural)
 *   - vine curtains (>=150 — grinder / hidden-entrance camo)
 *   - a clump of grown flowers (>=6 in a section — a garden)
 * On a hit it drops a green glass-pane marker at Y63, a chat/toast alert and a
 * level-up ding, and refuses to re-flag within 24 chunks so one base = one mark.
 *
 * Improvements over the Water original: every signal is a toggle (surface noise
 * like flowers/seagrass can be turned off), the depth-tested GL11 glass hack is
 * replaced with the through-walls ListUtils quad, and the scan is dimension- and
 * distance-culled.
 */
public final class OGChunkFinderModule extends Module {
   private static final long COOLDOWN_MS = 50000L;
   private static final long SCAN_INTERVAL_MS = 2000L;
   private static final long REPEATER_SCAN_INTERVAL_MS = 200L;
   private static final int MIN_MARKER_SPACING_SQ = 576; // 24 chunks
   private static final long MARKER_ANIM_MS = 700L;
   private static final int SCAN_RADIUS_CHUNKS = 3;

   private final NumberSetting renderY = new NumberSetting("RenderY", 63.0, -64.0, 200.0, 1.0);
   private final NumberSetting glassAlpha = new NumberSetting("MarkerAlpha", 120.0, 0.0, 255.0, 1.0);
   private final ColorSetting markerColor = new ColorSetting("MarkerColor", -16711936); // green
   private final BooleanSetting tracers = new BooleanSetting("Tracers", false);
   private final NumberSetting tracerWidth = new NumberSetting("TracerWidth", 1.5, 0.5, 4.0, 0.1);
   private final BooleanSetting toast = new BooleanSetting("Alert", true);
   private final BooleanSetting sound = new BooleanSetting("Sound", true);

   // signal toggles
   private final BooleanSetting sigRepeater = new BooleanSetting("Repeaters", true);
   private final BooleanSetting sigLight = new BooleanSetting("BedrockLight", true);
   private final BooleanSetting sigLichen = new BooleanSetting("GlowLichen", true);
   private final BooleanSetting sigPolar = new BooleanSetting("PolarBears", true);
   private final BooleanSetting sigHive = new BooleanSetting("Beehives", true);
   private final BooleanSetting sigCobbledDeep = new BooleanSetting("CobbledDeepslate", true);
   private final BooleanSetting sigRotatedDeep = new BooleanSetting("RotatedDeepslate", true);
   private final BooleanSetting sigVines = new BooleanSetting("Vines", true);
   private final BooleanSetting sigSeagrass = new BooleanSetting("Seagrass", false);
   private final BooleanSetting sigFlowers = new BooleanSetting("Flowers", false);

   private final Map<ChunkPos, String> markedChunks = new ConcurrentHashMap<>();
   private final Map<ChunkPos, Long> markerCreatedAt = new ConcurrentHashMap<>();
   private final Set<Long> processedDetections = ConcurrentHashMap.newKeySet();
   private final Set<Long> acceptedMarkerChunks = ConcurrentHashMap.newKeySet();
   private final AtomicBoolean scanning = new AtomicBoolean(false);
   private final AtomicBoolean repeaterScanning = new AtomicBoolean(false);
   private ExecutorService executor;
   private long lastMarkTime;
   private long lastScanTime;
   private long lastRepScanTime;

   public OGChunkFinderModule() {
      super("OGChunkFinder", "Multi-signal DonutSMP base finder (through walls)", Category.DONUTSMP);
      this.run6(new Setting[]{
         this.renderY, this.glassAlpha, this.markerColor, this.tracers, this.tracerWidth,
         this.toast, this.sound,
         this.sigRepeater, this.sigLight, this.sigLichen, this.sigPolar, this.sigHive,
         this.sigCobbledDeep, this.sigRotatedDeep, this.sigVines, this.sigSeagrass, this.sigFlowers
      });
      this.tracerWidth.visibleWhen(this.tracers::getValue);
   }

   @Override
   public void run() {
      this.reset();
   }

   @Override
   public void run2() {
      this.reset();
      if (this.executor != null) {
         this.executor.shutdownNow();
      }
   }

   private void reset() {
      this.markedChunks.clear();
      this.markerCreatedAt.clear();
      this.processedDetections.clear();
      this.acceptedMarkerChunks.clear();
      this.lastMarkTime = 0L;
      this.lastScanTime = 0L;
      this.lastRepScanTime = 0L;
      this.scanning.set(false);
      this.repeaterScanning.set(false);
   }

   private void ensureExecutor() {
      if (this.executor == null || this.executor.isShutdown()) {
         this.executor = Executors.newSingleThreadExecutor(t -> {
            Thread th = new Thread(t, "ogchunk-scan");
            th.setDaemon(true);
            return th;
         });
      }
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.world == null) {
         return;
      }

      int simDist = 12;
      try {
         simDist = (Integer) class310.options.getSimulationDistance().getValue();
      } catch (Throwable ignored) {
      }
      final int sd = simDist;
      ChunkPos playerChunk = class310.player.getChunkPos();

      // drop markers whose chunk unloaded, and stale dedup keys out of range
      this.markedChunks.keySet().removeIf(cp -> {
         if (Math.abs(cp.x - playerChunk.x) <= sd && Math.abs(cp.z - playerChunk.z) <= sd) {
            WorldChunk wc = class310.world.getChunkManager().getWorldChunk(cp.x, cp.z, false);
            return wc == null || wc.isEmpty();
         }
         return true;
      });
      this.markerCreatedAt.keySet().removeIf(cp -> !this.markedChunks.containsKey(cp));
      this.processedDetections.removeIf(key ->
         Math.abs(ChunkPos.getPackedX(key) - playerChunk.x) > sd || Math.abs(ChunkPos.getPackedZ(key) - playerChunk.z) > sd);

      long now = System.currentTimeMillis();

      // fast lane: powered repeaters only, tight interval (active redstone is the
      // highest-value, lowest-false-positive signal — worth a quicker cadence)
      if (this.sigRepeater.getValue() && now - this.lastRepScanTime >= REPEATER_SCAN_INTERVAL_MS
         && !this.scanning.get() && this.repeaterScanning.compareAndSet(false, true)) {
         this.lastRepScanTime = now;
         try {
            List<ChunkPos> pos = new ArrayList<>();
            List<WorldChunk> chunks = new ArrayList<>();
            collectChunks(playerChunk, pos, chunks);
            ensureExecutor();
            this.executor.submit(() -> {
               try {
                  for (int i = 0; i < pos.size(); i++) {
                     if (countPoweredRepeaters(chunks.get(i)) >= 3) {
                        markDetection(pos.get(i), "repeater");
                        return;
                     }
                  }
               } finally {
                  this.repeaterScanning.set(false);
               }
            });
         } catch (Throwable t) {
            this.repeaterScanning.set(false);
         }
      }

      // full multi-signal sweep on the slow interval
      if (now - this.lastScanTime >= SCAN_INTERVAL_MS && this.scanning.compareAndSet(false, true)) {
         this.lastScanTime = now;
         ensureExecutor();
         List<ChunkPos> pos = new ArrayList<>();
         List<WorldChunk> chunks = new ArrayList<>();
         collectChunks(playerChunk, pos, chunks);
         orderByMovement(playerChunk, pos, chunks);
         try {
            this.executor.submit(() -> {
               try {
                  fullScan(playerChunk, pos, chunks);
               } finally {
                  this.scanning.set(false);
               }
            });
         } catch (Throwable t) {
            this.scanning.set(false);
         }
      }
   }

   private void collectChunks(ChunkPos center, List<ChunkPos> outPos, List<WorldChunk> outChunks) {
      for (int cx = -SCAN_RADIUS_CHUNKS; cx <= SCAN_RADIUS_CHUNKS; cx++) {
         for (int cz = -SCAN_RADIUS_CHUNKS; cz <= SCAN_RADIUS_CHUNKS; cz++) {
            ChunkPos cp = new ChunkPos(center.x + cx, center.z + cz);
            WorldChunk wc = class310.world.getChunkManager().getWorldChunk(cp.x, cp.z, false);
            if (wc != null && !wc.isEmpty()) {
               outPos.add(cp);
               outChunks.add(wc);
            }
         }
      }
   }

   // sort so chunks ahead of the player (velocity, else facing) scan first — you
   // get the flag for where you're heading before the one behind you
   private void orderByMovement(ChunkPos center, List<ChunkPos> pos, List<WorldChunk> chunks) {
      Vec3d vel = class310.player.getVelocity();
      double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
      final double mx, mz;
      if (speed > 0.05) {
         mx = vel.x / speed;
         mz = vel.z / speed;
      } else {
         float yaw = class310.player.getYaw();
         mx = -Math.sin(Math.toRadians(yaw));
         mz = Math.cos(Math.toRadians(yaw));
      }
      Integer[] idx = new Integer[pos.size()];
      for (int i = 0; i < idx.length; i++) {
         idx[i] = i;
      }
      Arrays.sort(idx, (a, b) -> {
         double da = (pos.get(a).x - center.x) * mx + (pos.get(a).z - center.z) * mz;
         double db = (pos.get(b).x - center.x) * mx + (pos.get(b).z - center.z) * mz;
         return Double.compare(db, da);
      });
      List<ChunkPos> np = new ArrayList<>(pos.size());
      List<WorldChunk> nc = new ArrayList<>(chunks.size());
      for (int i : idx) {
         np.add(pos.get(i));
         nc.add(chunks.get(i));
      }
      pos.clear();
      pos.addAll(np);
      chunks.clear();
      chunks.addAll(nc);
   }

   private void fullScan(ChunkPos center, List<ChunkPos> pos, List<WorldChunk> chunks) {
      // seagrass carpet — early out, very cheap to confirm
      if (this.sigSeagrass.getValue()) {
         for (int i = 0; i < pos.size(); i++) {
            if (countBlock(chunks.get(i), Blocks.TALL_SEAGRASS, 75) >= 75) {
               markDetection(pos.get(i), "seagrass");
               return;
            }
         }
      }

      if (this.sigRepeater.getValue()) {
         for (int i = 0; i < pos.size(); i++) {
            if (countPoweredRepeaters(chunks.get(i)) >= 3) {
               markDetection(pos.get(i), "repeater");
               return;
            }
         }
      }

      if (this.sigLight.getValue()) {
         for (int i = 0; i < pos.size(); i++) {
            if (hasLightNearBedrock(chunks.get(i))) {
               markDetection(pos.get(i), "light");
               return;
            }
         }
      }

      if (this.sigLichen.getValue()) {
         for (int i = 0; i < pos.size(); i++) {
            ChunkPos cp = pos.get(i);
            if (Math.abs(cp.x - center.x) <= 7 && Math.abs(cp.z - center.z) <= 7
               && hasGlowLichenNearPlayer(chunks.get(i), cp)) {
               markDetection(cp, "glow_lichen");
               return;
            }
         }
      }

      if (this.sigPolar.getValue()) {
         ChunkPos bear = checkPolarBears();
         if (bear != null) {
            markDetection(bear, "polar_bear");
            return;
         }
      }

      // remaining signals share one pass; first chunk that matches wins
      for (int i = 0; i < pos.size(); i++) {
         ChunkPos cp = pos.get(i);
         WorldChunk chunk = chunks.get(i);

         boolean hive = this.sigHive.getValue() && hasPopulatedHive(chunk);
         boolean cobbledDeep = !hive && this.sigCobbledDeep.getValue() && countBlockInY(chunk, Blocks.COBBLED_DEEPSLATE, 0, 50, 3) >= 3;
         boolean rotatedDeep = !hive && !cobbledDeep && this.sigRotatedDeep.getValue() && hasRotatedDeepslate(chunk);
         boolean vines = !hive && this.sigVines.getValue() && countBlock(chunk, Blocks.VINE, 150) >= 150;
         boolean flowers = !hive && !vines && this.sigFlowers.getValue() && countFlowers(chunk, 6) >= 6;

         if (hive || cobbledDeep || rotatedDeep || vines || flowers) {
            // non-redstone signals share a global cooldown so a flower-heavy area
            // doesn't spam; repeaters (handled above) bypass it
            if (System.currentTimeMillis() - this.lastMarkTime >= COOLDOWN_MS) {
               markDetection(cp, hive ? "beehive" : cobbledDeep ? "cobbled_deepslate"
                  : rotatedDeep ? "rotated_deepslate" : vines ? "vines" : "flowers");
               return;
            }
         }
      }
   }

   // ── detection helpers ──────────────────────────────────────────────────────

   private static int countBlock(WorldChunk chunk, Block target, int stopAt) {
      int count = 0;
      for (ChunkSection s : chunk.getSectionArray()) {
         if (s != null && !s.isEmpty() && s.hasAny(st -> st.isOf(target))) {
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = 0; y < 16; y++) {
                     if (s.getBlockState(x, y, z).isOf(target) && ++count >= stopAt) {
                        return count;
                     }
                  }
               }
            }
         }
      }
      return count;
   }

   private static int countBlockInY(WorldChunk chunk, Block target, int yMin, int yMax, int stopAt) {
      int count = 0;
      ChunkSection[] sections = chunk.getSectionArray();
      int minY = chunk.getBottomY();
      for (int si = 0; si < sections.length; si++) {
         int base = minY + si * 16;
         if (base > yMax) {
            break;
         }
         if (base + 16 < yMin) {
            continue;
         }
         ChunkSection s = sections[si];
         if (s != null && !s.isEmpty() && s.hasAny(st -> st.isOf(target))) {
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = 0; y < 16; y++) {
                     int wy = base + y;
                     if (wy >= yMin && wy <= yMax && s.getBlockState(x, y, z).isOf(target) && ++count >= stopAt) {
                        return count;
                     }
                  }
               }
            }
         }
      }
      return count;
   }

   private static int countFlowers(WorldChunk chunk, int stopAt) {
      int count = 0;
      ChunkSection[] sections = chunk.getSectionArray();
      int minY = chunk.getBottomY();
      for (int si = 0; si < sections.length && minY + si * 16 <= 16; si++) {
         ChunkSection s = sections[si];
         if (s != null && !s.isEmpty() && s.hasAny(st -> st.isIn(BlockTags.FLOWERS))) {
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = 0; y < 16; y++) {
                     if (s.getBlockState(x, y, z).isIn(BlockTags.FLOWERS) && ++count >= stopAt) {
                        return count;
                     }
                  }
               }
            }
         }
      }
      return count;
   }

   private static int countPoweredRepeaters(WorldChunk chunk) {
      int count = 0;
      for (ChunkSection s : chunk.getSectionArray()) {
         if (s != null && !s.isEmpty() && s.hasAny(st -> st.isOf(Blocks.REPEATER))) {
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = 0; y < 16; y++) {
                     BlockState bs = s.getBlockState(x, y, z);
                     if (bs.isOf(Blocks.REPEATER) && bs.get(RepeaterBlock.POWERED) && ++count >= 3) {
                        return count;
                     }
                  }
               }
            }
         }
      }
      return count;
   }

   private static boolean hasLightNearBedrock(WorldChunk chunk) {
      ChunkSection[] sections = chunk.getSectionArray();
      int minY = chunk.getBottomY();
      for (int si = 0; si < sections.length; si++) {
         int base = minY + si * 16;
         if (base > 1 || base + 16 < -1) {
            continue;
         }
         ChunkSection s = sections[si];
         if (s != null && !s.isEmpty() && s.hasAny(st -> isLightSource(st.getBlock()))) {
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = 0; y < 16; y++) {
                     int wy = base + y;
                     if (wy >= -1 && wy <= 1 && isLightSource(s.getBlockState(x, y, z).getBlock())) {
                        return true;
                     }
                  }
               }
            }
         }
      }
      return false;
   }

   private boolean hasGlowLichenNearPlayer(WorldChunk chunk, ChunkPos cp) {
      ChunkSection[] sections = chunk.getSectionArray();
      for (ChunkSection s : sections) {
         if (s != null && !s.isEmpty() && s.hasAny(st -> st.isOf(Blocks.GLOW_LICHEN))) {
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = 0; y < 16; y++) {
                     if (s.getBlockState(x, y, z).isOf(Blocks.GLOW_LICHEN)) {
                        double lx = cp.getStartX() + x + 0.5;
                        double lz = cp.getStartZ() + z + 0.5;
                        for (AbstractClientPlayerEntity p : class310.world.getPlayers()) {
                           if (p != class310.player) {
                              double dx = p.getX() - lx;
                              double dz = p.getZ() - lz;
                              if (dx * dx + dz * dz <= 112.0 * 112.0) {
                                 return true;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      return false;
   }

   private static boolean hasRotatedDeepslate(WorldChunk chunk) {
      int count = 0;
      ChunkSection[] sections = chunk.getSectionArray();
      int minY = chunk.getBottomY();
      for (int si = 0; si < sections.length; si++) {
         int base = minY + si * 16;
         if (base > 63) {
            break;
         }
         if (base + 16 < 0) {
            continue;
         }
         ChunkSection s = sections[si];
         if (s != null && !s.isEmpty() && s.hasAny(st -> st.isOf(Blocks.DEEPSLATE))) {
            for (int x = 0; x < 16; x++) {
               for (int z = 0; z < 16; z++) {
                  for (int y = 0; y < 16; y++) {
                     int wy = base + y;
                     if (wy >= 0 && wy <= 63) {
                        BlockState bs = s.getBlockState(x, y, z);
                        if (bs.isOf(Blocks.DEEPSLATE) && bs.get(Properties.AXIS) != Axis.Y && ++count >= 3) {
                           return true;
                        }
                     }
                  }
               }
            }
         }
      }
      return false;
   }

   private static boolean hasPopulatedHive(WorldChunk chunk) {
      for (BlockEntity be : chunk.getBlockEntities().values()) {
         BlockState state = chunk.getBlockState(be.getPos());
         if ((state.isOf(Blocks.BEEHIVE) || state.isOf(Blocks.BEE_NEST))
            && be instanceof BeehiveBlockEntity hive && hive.getBeeCount() > 0) {
            return true;
         }
      }
      return false;
   }

   private ChunkPos checkPolarBears() {
      if (class310.world == null || class310.player == null) {
         return null;
      }
      Box box = class310.player.getBoundingBox().expand(48.0, 128.0, 48.0);
      List<PolarBearEntity> bears = class310.world.getEntitiesByClass(PolarBearEntity.class, box, LivingEntity::isAlive);
      if (bears.size() < 2) {
         return null;
      }
      for (PolarBearEntity bear : bears) {
         var pos = bear.getBlockPos();
         RegistryEntry<Biome> biome = class310.world.getBiome(pos);
         if (biome != null) {
            if (biome.isIn(BiomeTags.IS_OCEAN)
               || biome.isIn(BiomeTags.SPAWNS_SNOW_FOXES)
               || biome.isIn(BiomeTags.POLAR_BEARS_SPAWN_ON_ALTERNATE_BLOCKS)) {
               return new ChunkPos(pos);
            }
            int ice = 0;
            for (int dx = -3; dx <= 3; dx++) {
               for (int dz = -3; dz <= 3; dz++) {
                  Block b = class310.world.getBlockState(pos.add(dx, 0, dz)).getBlock();
                  if (b == Blocks.ICE || b == Blocks.PACKED_ICE || b == Blocks.BLUE_ICE
                     || b == Blocks.SNOW_BLOCK || b == Blocks.POWDER_SNOW) {
                     ice++;
                  }
               }
            }
            if (ice >= 5) {
               return new ChunkPos(pos);
            }
         }
      }
      return null;
   }

   private static boolean isLightSource(Block b) {
      return b == Blocks.TORCH || b == Blocks.WALL_TORCH || b == Blocks.SOUL_TORCH || b == Blocks.SOUL_WALL_TORCH
         || b == Blocks.LANTERN || b == Blocks.SOUL_LANTERN || b == Blocks.GLOWSTONE || b == Blocks.SHROOMLIGHT
         || b == Blocks.SEA_LANTERN || b == Blocks.JACK_O_LANTERN || b == Blocks.CAMPFIRE || b == Blocks.SOUL_CAMPFIRE
         || b == Blocks.REDSTONE_LAMP || b == Blocks.END_ROD || b == Blocks.CRYING_OBSIDIAN
         || b == Blocks.OCHRE_FROGLIGHT || b == Blocks.VERDANT_FROGLIGHT || b == Blocks.PEARLESCENT_FROGLIGHT;
   }

   private void markDetection(ChunkPos actual, String type) {
      long key = actual.toLong();
      if (this.processedDetections.contains(key) || !hasMinimumSpacing(actual)) {
         return;
      }
      this.processedDetections.add(key);
      this.acceptedMarkerChunks.add(key);
      this.lastMarkTime = System.currentTimeMillis();
      this.markedChunks.put(actual, type);
      this.markerCreatedAt.put(actual, System.currentTimeMillis());

      class310.execute(() -> {
         if (this.toast.getValue() && NotificationUtils.isEnabled2()) {
            NotificationUtils.run("OGChunkFinder",
               type + " @ " + actual.getStartX() + ", " + actual.getStartZ(), INFO.UNKNOWN_2, 3000L);
         }
         if (this.sound.getValue() && class310.player != null) {
            class310.player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
         }
      });
   }

   private boolean hasMinimumSpacing(ChunkPos candidate) {
      for (long packed : this.acceptedMarkerChunks) {
         long dx = (long) candidate.x - ChunkPos.getPackedX(packed);
         long dz = (long) candidate.z - ChunkPos.getPackedZ(packed);
         if (dx * dx + dz * dz < MIN_MARKER_SPACING_SQ) {
            return false;
         }
      }
      return true;
   }

   @Override
   public void run4(DrawContext ctx, float tickDelta) {
      if (class310.player == null || class310.world == null || this.markedChunks.isEmpty()) {
         return;
      }
      Vec3d camPos = class310.gameRenderer.getCamera().getCameraPos();
      double y = this.renderY.getValue();
      int baseColor = this.markerColor.getValue();
      int maxAlpha = (int) Math.round(this.glassAlpha.getValue());
      float tw = this.tracerWidth.getValueFloat();
      boolean drawTracers = this.tracers.getValue();
      long now = System.currentTimeMillis();

      for (Entry<ChunkPos, String> e : this.markedChunks.entrySet()) {
         ChunkPos cp = e.getKey();
         long createdAt = this.markerCreatedAt.getOrDefault(cp, 0L);
         float progress = createdAt == 0L ? 1.0F : Math.min(1.0F, (float) (now - createdAt) / MARKER_ANIM_MS);
         float eased = 1.0F - (float) Math.pow(1.0F - progress, 3.0);
         double size = 18.0 * (0.12 + 0.88 * eased);
         int a = Math.max(0, Math.min(maxAlpha, Math.round(maxAlpha * eased)));
         int argb = (a << 24) | (baseColor & 0xFFFFFF);

         double cx = cp.getStartX() + 8.0;
         double cz = cp.getStartZ() + 8.0;
         double half = size * 0.5;
         // horizontal through-walls quad (glass-pane marker) at renderY
         ListUtils.run14(cx - half, y, cz - half, cx + half, cz + half, argb, true);

         if (drawTracers) {
            // world line from just under the camera to the marker centroid
            ListUtils.run11(new Vec3d(camPos.x, camPos.y - 0.2, camPos.z),
               new Vec3d(cx, y, cz), 0xFF000000 | (baseColor & 0xFFFFFF), tw);
         }
      }
   }

   @Override
   public String getString3() {
      int n = this.markedChunks.size();
      return n > 0 ? "§7" + n : null;
   }
}
