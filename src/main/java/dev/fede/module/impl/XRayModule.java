package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.render.SeedCaveXrayRenderer;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.settings.StringSetting;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.Set;

/**
 * XRay — hides all non-whitelisted blocks so ores/containers pop out of terrain.
 * Logic ported from LiquidBounce b100 (adapted to Fabric 1.21.1 mapped names).
 * Uses shouldDrawSide mixin (XRayBlockCullMixin) so toggling only triggers
 * worldRenderer.reload() — no loading screen, instant update.
 */
public final class XRayModule extends Module {

    // ── static flags — read millions of times per chunk rebuild, must be O(1) ──
    public static volatile boolean ACTIVE        = false;
    public static volatile boolean SHOW_STORAGE  = true;  // chests / barrels / hoppers
    public static volatile boolean SHOW_SPAWNERS = true;
    public static volatile boolean SHOW_LIQUIDS  = false;
    public static volatile boolean SHOW_PORTALS  = false;
    public static volatile boolean SHOW_UTILITY  = false;

    // ── user-visible settings ─────────────────────────────────────────────────
    public final BooleanSetting showStorage  = addSetting(new BooleanSetting("Storage",  "Chests, barrels, hoppers, etc.", true));
    public final BooleanSetting showSpawners = addSetting(new BooleanSetting("Spawners", "Monster spawners",               true));
    public final BooleanSetting showLiquids  = addSetting(new BooleanSetting("Liquids",  "Water and lava",                 false));
    public final BooleanSetting showPortals  = addSetting(new BooleanSetting("Portals",  "Nether / End portals",           false));
    public final BooleanSetting showUtility  = addSetting(new BooleanSetting("Utility",  "Crafting tables, furnaces, etc.", false));
    public final BooleanSetting fullBright   = addSetting(new BooleanSetting("FullBright","Brighten underground ores",      true));
    // hidden neighbors still count for ambient-occlusion darkening (AO reads
    // real block occlusion data, unrelated to whether that neighbor is being
    // *drawn*) — so an ore surrounded by "invisible" stone still gets
    // shaded near-black on every face despite fullbright. same fix most
    // xray implementations pair with fullbright: kill AO while xray's on.
    public final BooleanSetting disableAO    = addSetting(new BooleanSetting("Disable AO", "Ore blocks render dark otherwise — hidden neighbors still occlude for shading purposes", true));
    public final SliderSetting  loadDistance = addSetting(new SliderSetting("Load Distance", "Chunks to keep loaded/rendered while xray is on", 32.0, 1.0, 126.0, 1.0));

    // ── seed-simulated cave diff — a real anti-xray bypass, not client-side
    // pretend-xray. plain XRay above can't reveal anything an Orebfuscator-
    // style plugin never sent — the fake stone IS the block the client has,
    // culling it differently doesn't change what data arrived. this instead
    // runs vanilla's own real overworld chunk generator (NoiseChunkGenerator
    // + ChunkGeneratorSettings.OVERWORLD, pulled from the client's own
    // registries) locally with a known/cracked seed, and asks it what the
    // TRUE terrain looks like at every loaded column — real stone, real air,
    // real caves. Diffs that against what the server's actually showing:
    // anywhere the real generator says "open" but the server shows solid
    // stone-family rock, that's a wall the plugin built to hide what's
    // behind it. Ore already exposed along a real cave wall is generally
    // already unobfuscated by these plugins (they mostly hide what ISN'T
    // exposed to air yet), so finding the real cave gets you most of the
    // way to real, already-visible ore without needing vein-accurate
    // prediction. Does NOT predict individual ore veins — that needs
    // vanilla's real feature-generation pipeline (StructureWorldAccess
    // backed by an actual ServerWorld), confirmed by inspecting the mapped
    // jar directly; spinning up a second, real, headless world alongside a
    // live connection just for that isn't attempted here.
    // Known gaps: overworld only (hardcoded generator settings); samples
    // base noise terrain only, so legacy Carver-pass ravines (a separate
    // generation step) won't show; treats hidden water/lava the same as a
    // fake wall, since both just mean "not solid rock."
    // Typing a seed in below turns this on automatically — no separate toggle.
    public final StringSetting  seed              = addSetting(new StringSetting("Seed", "World seed to simulate — from SeedChunkFinder or your own crack. Typing one here turns on the cave-diff highlight below", "", 24, "e.g. -3944821893929123212"));
    public final SliderSetting  seedRange         = addSetting(new SliderSetting("Seed Range", "Max distance a fake wall is scanned/shown at", 48.0, 16.0, 96.0, 8.0));
    public final SliderSetting  seedVerticalRange = addSetting(new SliderSetting("Seed Vertical Range", "How far above/below you columns are sampled", 32.0, 8.0, 64.0, 8.0));
    public final ColorSetting   seedColor         = addSetting(new ColorSetting("Seed Highlight", "Fake-wall highlight color", -16711936));
    public final SliderSetting  seedAlpha         = addSetting(new SliderSetting("Seed Highlight Alpha", "Fake-wall box opacity (0-255)", 160.0, 0.0, 255.0, 1.0));

    private Boolean savedAo;
    private Integer savedViewDistance;
    private int lastSyncedLoadDistance = Integer.MIN_VALUE;
    private ChunkPos lastSyncedCenter;

    public XRayModule() {
        super("XRay", "See ores through walls", Category.RENDER);
    }

    /** Push current settings to static flags so the mixin sees them. */
    private void syncFlags() {
        SHOW_STORAGE  = showStorage.get();
        SHOW_SPAWNERS = showSpawners.get();
        SHOW_LIQUIDS  = showLiquids.get();
        SHOW_PORTALS  = showPortals.get();
        SHOW_UTILITY  = showUtility.get();
    }

    @Override
    protected void onEnable() {
        syncFlags();
        ACTIVE = true;
        applyFullbright(true);
        applyAO(true);
        applyViewDistanceOption(true);
        this.lastSyncedLoadDistance = Integer.MIN_VALUE;
        this.lastSyncedCenter = null;
        reloadChunks();
    }

    @Override
    protected void onDisable() {
        ACTIVE = false;
        applyFullbright(false);
        applyAO(false);
        applyViewDistanceOption(false);
        restoreVanillaChunkLoading();
        reloadChunks();
        SeedCaveXrayRenderer.clear();
    }

    /** Null if the typed Seed field isn't a valid long yet. */
    public Long parsedSeed() {
        String typed = seed.get().trim();
        if (typed.isEmpty()) return null;
        try {
            return Long.parseLong(typed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getSuffix() {
        if (parsedSeed() == null) return "";
        int n = SeedCaveXrayRenderer.fakeWallCount();
        return n > 0 ? " [" + n + "]" : "";
    }

    /**
     * ClientChunkManager.updateLoadDistance() alone doesn't reach sodium:
     * checked its RenderSectionManager directly — its render-distance field
     * is a `final int`, captured once when sodium (re)builds its section
     * graph (on world/dimension join), not re-read live. so the actual
     * option sodium sources that from (mc.options.getViewDistance()) needs
     * to be raised too — it just won't visibly extend sodium's own render
     * radius until the next world/dimension change picks the new value up,
     * same as changing render distance normally works with sodium
     * installed, mod or no mod. updateLoadDistance() still matters for the
     * vanilla chunk manager / non-sodium data loading in the meantime.
     */
    private void applyViewDistanceOption(boolean on) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) return;
        if (on) {
            this.savedViewDistance = mc.options.getViewDistance().getValue();
            int dist = Math.min(126, Math.max(1, Math.round(loadDistance.getFloat())));
            mc.options.getViewDistance().setValue(dist);
        } else if (this.savedViewDistance != null) {
            mc.options.getViewDistance().setValue(this.savedViewDistance);
            this.savedViewDistance = null;
        }
    }

    @Override
    public void onTick() {
        syncChunkLoading();
        SeedCaveXrayRenderer.scan(this);
    }

    private void applyAO(boolean on) {
        if (!disableAO.get()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) return;
        if (on) {
            this.savedAo = mc.options.getAo().getValue();
            mc.options.getAo().setValue(false);
        } else if (this.savedAo != null) {
            mc.options.getAo().setValue(this.savedAo);
            this.savedAo = null;
        }
    }

    /**
     * Bumps chunk load/render distance while xray's active, same mechanism
     * FreecamModule already uses (ClientChunkManager.updateLoadDistance /
     * setChunkMapCenter) — if freecam's also active, xray only raises the
     * distance (to whichever's bigger) and leaves centering to freecam
     * rather than fighting it for where the load center should be.
     */
    private void syncChunkLoading() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!ACTIVE || mc.world == null || mc.player == null) return;

        FreecamModule freecam = dev.fede.FeClient.modules() != null ? dev.fede.FeClient.modules().freecam : null;
        boolean freecamOwnsCenter = freecam != null && freecam.isActive();

        int wanted = Math.min(126, Math.max(1, Math.round(loadDistance.getFloat())));
        int freecamWanted = freecamOwnsCenter ? Math.round(freecam.freecamChunkDistance.getFloat()) : 0;
        int dist = Math.max(wanted, freecamWanted);

        ClientChunkManager ccm = mc.world.getChunkManager();
        if (dist != this.lastSyncedLoadDistance) {
            ccm.updateLoadDistance(dist);
            this.lastSyncedLoadDistance = dist;
        }

        if (!freecamOwnsCenter) {
            ChunkPos center = mc.player.getChunkPos();
            if (this.lastSyncedCenter == null || center.x != this.lastSyncedCenter.x || center.z != this.lastSyncedCenter.z) {
                ccm.setChunkMapCenter(center.x, center.z);
                this.lastSyncedCenter = center;
                if (mc.worldRenderer != null) mc.worldRenderer.scheduleTerrainUpdate();
            }
        }
    }

    private void restoreVanillaChunkLoading() {
        this.lastSyncedLoadDistance = Integer.MIN_VALUE;
        this.lastSyncedCenter = null;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        FreecamModule freecam = dev.fede.FeClient.modules() != null ? dev.fede.FeClient.modules().freecam : null;
        if (freecam != null && freecam.isActive()) return; // freecam still owns loading, don't stomp it
        ClientChunkManager ccm = mc.world.getChunkManager();
        if (mc.player != null) {
            ChunkPos p = mc.player.getChunkPos();
            ccm.setChunkMapCenter(p.x, p.z);
        }
        ccm.updateLoadDistance(mc.options.getClampedViewDistance());
        if (mc.worldRenderer != null) mc.worldRenderer.scheduleTerrainUpdate();
    }

    private void applyFullbright(boolean on) {
        if (!fullBright.get()) return;
        // Toggle the native FullbrightModule directly — name-based lookup used to
        // walk mm.all() matching "Fullbright" case-insensitively, which could also
        // match the separately-bridged nyx FullbrightModule and toggle the wrong
        // instance depending on registration order.
        dev.fede.module.ModuleManager mm = dev.fede.FeClient.modules();
        if (mm == null || mm.fullbright == null) return;
        if (on && !mm.fullbright.isEnabled()) mm.fullbright.setEnabled(true);
        // Don't disable it on XRay off — user may have it on independently
    }

    private static void reloadChunks() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.worldRenderer != null && mc.world != null) {
            mc.worldRenderer.reload();
        }
    }

    // ── ALWAYS_VISIBLE: ores and mineral blocks (LiquidBounce block list) ─────

    private static final Set<Block> ALWAYS_VISIBLE = new HashSet<>();

    static {
        // ── Overworld ores ────────────────────────────────────────────────────
        ALWAYS_VISIBLE.add(Blocks.COAL_ORE);
        ALWAYS_VISIBLE.add(Blocks.DEEPSLATE_COAL_ORE);
        ALWAYS_VISIBLE.add(Blocks.IRON_ORE);
        ALWAYS_VISIBLE.add(Blocks.DEEPSLATE_IRON_ORE);
        ALWAYS_VISIBLE.add(Blocks.GOLD_ORE);
        ALWAYS_VISIBLE.add(Blocks.DEEPSLATE_GOLD_ORE);
        ALWAYS_VISIBLE.add(Blocks.DIAMOND_ORE);
        ALWAYS_VISIBLE.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        ALWAYS_VISIBLE.add(Blocks.LAPIS_ORE);
        ALWAYS_VISIBLE.add(Blocks.DEEPSLATE_LAPIS_ORE);
        ALWAYS_VISIBLE.add(Blocks.REDSTONE_ORE);
        ALWAYS_VISIBLE.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        ALWAYS_VISIBLE.add(Blocks.EMERALD_ORE);
        ALWAYS_VISIBLE.add(Blocks.DEEPSLATE_EMERALD_ORE);
        ALWAYS_VISIBLE.add(Blocks.COPPER_ORE);
        ALWAYS_VISIBLE.add(Blocks.DEEPSLATE_COPPER_ORE);
        // ── Overworld mineral blocks ──────────────────────────────────────────
        ALWAYS_VISIBLE.add(Blocks.COAL_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.IRON_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.GOLD_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.DIAMOND_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.LAPIS_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.REDSTONE_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.EMERALD_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.COPPER_BLOCK);
        // ── Raw mineral blocks ────────────────────────────────────────────────
        ALWAYS_VISIBLE.add(Blocks.RAW_IRON_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.RAW_GOLD_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.RAW_COPPER_BLOCK);
        // ── Nether ores & blocks ──────────────────────────────────────────────
        ALWAYS_VISIBLE.add(Blocks.NETHER_QUARTZ_ORE);
        ALWAYS_VISIBLE.add(Blocks.NETHER_GOLD_ORE);
        ALWAYS_VISIBLE.add(Blocks.ANCIENT_DEBRIS);
        ALWAYS_VISIBLE.add(Blocks.NETHERITE_BLOCK);
        ALWAYS_VISIBLE.add(Blocks.GILDED_BLACKSTONE);
        ALWAYS_VISIBLE.add(Blocks.QUARTZ_BLOCK);
        // ── Amethyst ─────────────────────────────────────────────────────────
        ALWAYS_VISIBLE.add(Blocks.AMETHYST_CLUSTER);
        ALWAYS_VISIBLE.add(Blocks.LARGE_AMETHYST_BUD);
        ALWAYS_VISIBLE.add(Blocks.MEDIUM_AMETHYST_BUD);
        ALWAYS_VISIBLE.add(Blocks.SMALL_AMETHYST_BUD);
        ALWAYS_VISIBLE.add(Blocks.BUDDING_AMETHYST);
        ALWAYS_VISIBLE.add(Blocks.AMETHYST_BLOCK);
        // ── Special ──────────────────────────────────────────────────────────
        ALWAYS_VISIBLE.add(Blocks.BEACON);
        ALWAYS_VISIBLE.add(Blocks.LODESTONE);
        ALWAYS_VISIBLE.add(Blocks.DRAGON_EGG);
        ALWAYS_VISIBLE.add(Blocks.TNT);
        ALWAYS_VISIBLE.add(Blocks.CLAY);
        ALWAYS_VISIBLE.add(Blocks.BOOKSHELF);
        ALWAYS_VISIBLE.add(Blocks.JUKEBOX);
        ALWAYS_VISIBLE.add(Blocks.RESPAWN_ANCHOR);
        ALWAYS_VISIBLE.add(Blocks.FLOWER_POT);
    }

    /**
     * Called by XRayBlockCullMixin on EVERY shouldDrawSide decision — millions of
     * times during a chunk rebuild.  Must be purely O(1): read only static flags.
     */
    public static boolean isVisible(Block block) {
        if (ALWAYS_VISIBLE.contains(block)) return true;

        // Optional categories (user-toggled) — static volatile reads, O(1)
        if (SHOW_SPAWNERS && (block == Blocks.SPAWNER || block == Blocks.TRIAL_SPAWNER)) return true;

        if (SHOW_STORAGE && isStorageBlock(block)) return true;

        if (SHOW_LIQUIDS && (block == Blocks.WATER || block == Blocks.LAVA)) return true;

        if (SHOW_PORTALS && (block == Blocks.NETHER_PORTAL
                || block == Blocks.END_PORTAL || block == Blocks.END_PORTAL_FRAME)) return true;

        if (SHOW_UTILITY && isUtilityBlock(block)) return true;

        return false;
    }

    private static boolean isStorageBlock(Block b) {
        return b == Blocks.CHEST || b == Blocks.TRAPPED_CHEST || b == Blocks.ENDER_CHEST
                || b == Blocks.BARREL || b == Blocks.HOPPER || b == Blocks.DISPENSER
                || b == Blocks.DROPPER || b == Blocks.SHULKER_BOX;
    }

    private static boolean isUtilityBlock(Block b) {
        return b == Blocks.CRAFTING_TABLE || b == Blocks.FURNACE || b == Blocks.BLAST_FURNACE
                || b == Blocks.SMOKER || b == Blocks.ENCHANTING_TABLE || b == Blocks.ANVIL
                || b == Blocks.CHIPPED_ANVIL || b == Blocks.DAMAGED_ANVIL
                || b == Blocks.BREWING_STAND || b == Blocks.STONECUTTER
                || b == Blocks.GRINDSTONE || b == Blocks.CARTOGRAPHY_TABLE
                || b == Blocks.FLETCHING_TABLE || b == Blocks.SMITHING_TABLE
                || b == Blocks.LOOM || b == Blocks.COMPOSTER || b == Blocks.LECTERN;
    }
}
