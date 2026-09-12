package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;

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
        reloadChunks();
    }

    @Override
    protected void onDisable() {
        ACTIVE = false;
        applyFullbright(false);
        reloadChunks();
    }

    private void applyFullbright(boolean on) {
        if (!fullBright.get()) return;
        // Toggle the nyx Fullbright module if present so underground is lit
        dev.fede.module.ModuleManager mm = dev.fede.FeClient.modules();
        if (mm == null) return;
        for (dev.fede.module.Module m : mm.all()) {
            if (m.getName().equalsIgnoreCase("Fullbright") && m != this) {
                if (on && !m.isEnabled()) m.setEnabled(true);
                // Don't disable it on XRay off — user may have it on independently
            }
        }
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
