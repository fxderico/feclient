package dev.fede.render;

import dev.fede.module.impl.XRayModule;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

/**
 * Backing sim/diff/render for XRayModule's seed-simulated cave diff. See
 * that class's javadoc for the technique and its honest limits. This class
 * is just: build the real overworld generator once per seed, sample real
 * terrain per column, diff it against what's actually loaded, draw the
 * mismatches.
 */
public final class SeedCaveXrayRenderer {

    private static final IncrementalScan<Long> SCAN = new IncrementalScan<>(24, 40000, 20);
    private static List<Long> lastPublished = List.of();
    private static Set<Long> fakeWalls = new HashSet<>();

    // cached real generator — rebuilt only when the configured seed changes
    private static long cachedSeed = Long.MIN_VALUE;
    private static boolean cacheReady = false;
    private static boolean cacheFailed = false;
    private static ChunkGenerator generator;
    private static NoiseConfig noiseConfig;

    private SeedCaveXrayRenderer() {
    }

    public static void reset() {
        clear();
    }

    public static void clear() {
        SCAN.clear();
        lastPublished = List.of();
        fakeWalls = new HashSet<>();
    }

    public static int fakeWallCount() {
        return fakeWalls.size();
    }

    public static void scan(XRayModule module) {
        Long seed = module.parsedSeed();
        if (seed == null) {
            if (!fakeWalls.isEmpty()) clear();
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        ClientWorld level = mc.world;
        if (level == null) return;
        // hardcoded to vanilla overworld generator settings — bail cleanly
        // outside it instead of simulating the wrong dimension's terrain.
        if (level.getRegistryKey() != World.OVERWORLD) return;

        if (!ensureGenerator(level, seed)) return;

        double range = module.seedRange.get();
        double rangeSq = range * range;
        int wanted = (int) Math.ceil(range / 16.0) + 1;
        int chunkRadius = Math.min(Math.min(16, wanted), mc.options.getViewDistance().getValue());
        double vRange = module.seedVerticalRange.get();

        SCAN.tick(chunkRadius, (chunk, out) -> scanChunk(chunk, level, rangeSq, vRange, out));

        List<Long> current = SCAN.get();
        if (current != lastPublished) {
            fakeWalls = new HashSet<>(current);
            lastPublished = current;
        }
    }

    /** (Re)builds the cached real generator when the seed changes. Returns false if unusable this tick. */
    private static boolean ensureGenerator(ClientWorld level, long seed) {
        if (cacheReady && cachedSeed == seed) return true;
        if (cacheFailed && cachedSeed == seed) return false;

        try {
            DynamicRegistryManager registryManager = level.getRegistryManager();
            RegistryEntry<ChunkGeneratorSettings> settings = registryManager.getEntryOrThrow(ChunkGeneratorSettings.OVERWORLD);
            RegistryEntry<MultiNoiseBiomeSourceParameterList> params =
                registryManager.getEntryOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
            BiomeSource biomeSource = MultiNoiseBiomeSource.create(params);

            generator = new NoiseChunkGenerator(biomeSource, settings);
            noiseConfig = NoiseConfig.create(registryManager, ChunkGeneratorSettings.OVERWORLD, seed);

            cachedSeed = seed;
            cacheReady = true;
            cacheFailed = false;
            SCAN.clear();
            return true;
        } catch (Throwable t) {
            // wrong seed format the registry rejects, missing registry entries on
            // a datapack-modified server, whatever — fail closed, don't crash.
            cachedSeed = seed;
            cacheReady = false;
            cacheFailed = true;
            generator = null;
            noiseConfig = null;
            return false;
        }
    }

    private static int scanChunk(WorldChunk chunk, ClientWorld level, double rangeSq, double vRange, List<Long> out) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || generator == null || noiseConfig == null) return 0;

        int baseX = chunk.getPos().getStartX();
        int baseZ = chunk.getPos().getStartZ();
        int playerY = player.getBlockY();
        int minY = Math.max(level.getBottomY(), playerY - (int) vRange);
        int maxY = Math.min(level.getBottomY() + level.getHeight() - 1, playerY + (int) vRange);
        int sampled = 0;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int wx = baseX + x, wz = baseZ + z;
                double dx = (wx + 0.5) - player.getX();
                double dz = (wz + 0.5) - player.getZ();
                double horizontalDistSq = dx * dx + dz * dz;
                if (horizontalDistSq > rangeSq) continue; // cheap column-level skip; real per-block check happens below

                VerticalBlockSample sample;
                try {
                    sample = generator.getColumnSample(wx, wz, level, noiseConfig);
                } catch (Throwable t) {
                    continue;
                }
                sampled++;

                for (int wy = minY; wy <= maxY; wy++) {
                    BlockState real;
                    try {
                        real = sample.getState(wy);
                    } catch (Throwable t) {
                        continue;
                    }
                    if (!real.isAir()) continue; // real terrain says solid here — not a fake wall

                    BlockState actual = chunk.getBlockState(new BlockPos(wx, wy, wz));
                    if (isCoverBlock(actual)) {
                        if (player.squaredDistanceTo(wx + 0.5, wy + 0.5, wz + 0.5) <= rangeSq) {
                            out.add(new BlockPos(wx, wy, wz).asLong());
                        }
                    }
                }
            }
        }

        return sampled * 4; // rough budget cost — column sampling isn't free
    }

    // ── blocks anti-xray plugins actually backfill with — a real cave/void
    // the generator says should be air, but the server insists is one of
    // these, means the plugin built a wall there. ─────────────────────────
    private static boolean isCoverBlock(BlockState state) {
        var block = state.getBlock();
        return block == Blocks.STONE || block == Blocks.DEEPSLATE || block == Blocks.TUFF
            || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE
            || block == Blocks.CALCITE || block == Blocks.DIRT || block == Blocks.GRAVEL;
    }

    public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, XRayModule module) {
        if (fakeWalls.isEmpty()) return;
        int alphaBits = Math.max(0, Math.min(255, module.seedAlpha.getInt())) << 24;
        int rgb = module.seedColor.get() & 0xFFFFFF;
        int color = alphaBits | rgb;

        for (Long packed : fakeWalls) {
            BlockPos pos = BlockPos.fromLong(packed);
            double x1 = pos.getX(), y1 = pos.getY(), z1 = pos.getZ();
            EspBoxRenderer.fill(bufferSource, poseStack, cam, x1, y1, z1, x1 + 1.0, y1 + 1.0, z1 + 1.0, color);
        }

        EspBoxRenderer.flush(bufferSource);
    }
}
