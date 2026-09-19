package dev.fede.module;

// native modules — fully qualified to avoid ambiguity with nyx mirrors
import dev.fede.module.impl.AimAssistModule;
import dev.fede.module.impl.AnchorMacroModule;
import dev.fede.module.impl.AntiAfkModule;
import dev.fede.module.impl.ArmorTrimHiderModule;
import dev.fede.module.impl.AutoClickerModule;
import dev.fede.module.impl.AutoSprintModule;
import dev.fede.module.impl.AutoCrystalModule;
import dev.fede.module.impl.AutoInventoryTotemModule;
import dev.fede.module.impl.AutoTotemModule;
import dev.fede.module.impl.AutoTpaModule;
import dev.fede.module.impl.AutoWalkModule;
import dev.fede.module.impl.BlockEntityEspModule;
import dev.fede.module.impl.BlockEspModule;
import dev.fede.module.impl.ChatMacroModule;
import dev.fede.module.impl.ChunkFinderModule;
import dev.fede.module.impl.CoordSnapperModule;
import dev.fede.module.impl.CustomAccessoriesModule;
import dev.fede.module.impl.CustomCrosshairModule;
import dev.fede.module.impl.CustomFovModule;
import dev.fede.module.impl.CustomGlintModule;
import dev.fede.module.impl.DebugHoleEspModule;
import dev.fede.module.impl.DoubleAnchorModule;
import dev.fede.module.impl.ElytraSwapModule;
import dev.fede.module.impl.FakePayModule;
import dev.fede.module.impl.FakeRolesModule;
import dev.fede.module.impl.FakeStatsModule;
import dev.fede.module.impl.FastUseModule;
import dev.fede.module.impl.FreeLookModule;
import dev.fede.module.impl.FreecamModule;
import dev.fede.module.impl.FriendlyMobEspModule;
import dev.fede.module.impl.FullbrightModule;
import dev.fede.module.impl.GambleRiggerModule;
import dev.fede.module.impl.HitBoxModule;
import dev.fede.module.impl.HitParticlesModule;
import dev.fede.module.impl.HoverTotemModule;
import dev.fede.module.impl.JumpCirclesModule;
import dev.fede.module.impl.MaceBomberModule;
import dev.fede.module.impl.MaceSwapModule;
import dev.fede.module.impl.MobEspModule;
import dev.fede.module.impl.MotionBlurModule;
import dev.fede.module.impl.NameProtectModule;
import dev.fede.module.impl.NameTagsModule;
import dev.fede.module.impl.PlayerEspModule;
import dev.fede.module.impl.RegionMapModule;
import dev.fede.module.impl.ShieldBreakerModule;
import dev.fede.module.impl.SkinProtectModule;
import dev.fede.module.impl.SpawnerNametagsModule;
import dev.fede.module.impl.SpawnerProtectModule;
import dev.fede.module.impl.StaffListModule;
import dev.fede.module.impl.StorageEspModule;
import dev.fede.module.impl.SwingSpeedModule;
import dev.fede.module.impl.TriggerbotModule;
import dev.fede.module.impl.WeatherNotifierModule;
import dev.fede.module.impl.AutoRespawnModule;
import dev.fede.module.impl.CriticalsModule;
import dev.fede.module.impl.XRayModule;
import dev.fede.module.impl.ViewModule;
import dev.fede.module.impl.ZoomModule;

// CodeEngine (nyx) modules — each imported with an alias via fully qualified names in the nyx() calls
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.Setting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Unified ModuleManager for feClient.
 * Registers every module from all three source clients:
 *   native    → native modules (direct instantiation)
 *   CodeEngine → wrapped in NyxModuleBridge
 *   WaterSRC   → wrapped in WaterModuleBridge
 */
public final class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private final Map<Category, List<Module>> byCategory = new LinkedHashMap<>();

    // ── frequently-accessed module references (native modules) ─────────────
    public Modules.ClickGuiModule       clickGui;
    public Modules.HudModule            hud;
    public Modules.SpotifyModule        spotify;
    public Modules.BlockOutlineModule   blockOutline;
    public Modules.SusChunkFinderModule susChunkFinder;
    public FullbrightModule             fullbright;
    public AutoWalkModule               autoWalk;
    public WeatherNotifierModule        weatherNotifier;
    public SwingSpeedModule             swingSpeed;
    public StorageEspModule             storageEsp;
    public BlockEspModule               blockEsp;
    public AutoTotemModule              autoTotem;
    public MaceSwapModule               maceSwap;
    public AnchorMacroModule            anchorMacro;
    public AutoCrystalModule            autoCrystal;
    public HitBoxModule                 hitBox;
    public ElytraSwapModule             elytraSwap;
    public HoverTotemModule             hoverTotem;
    public ShieldBreakerModule          shieldBreaker;
    public TriggerbotModule             triggerbot;
    public AimAssistModule              aimAssist;
    public DoubleAnchorModule           doubleAnchor;
    public MaceBomberModule             maceBomber;
    public NameProtectModule            nameProtect;
    public SkinProtectModule            skinProtect;
    public NameTagsModule               nameTags;
    public FastUseModule                fastUse;
    public AutoInventoryTotemModule     autoInventoryTotem;
    public PlayerEspModule              playerEsp;
    public MobEspModule                 mobEsp;
    public FriendlyMobEspModule         friendlyMobEsp;
    public BlockEntityEspModule         blockEntityEsp;
    public SpawnerNametagsModule        spawnerNametags;
    public DebugHoleEspModule           debugHoleEsp;
    public FreecamModule                freecam;
    public ViewModule                   view;
    public AutoTpaModule                autoTpa;
    public JumpCirclesModule            jumpCircles;
    public CustomCrosshairModule        customCrosshair;
    public ZoomModule                   zoom;
    public CustomFovModule              customFov;
    public HitParticlesModule           hitParticles;
    public MotionBlurModule             motionBlur;
    public CustomGlintModule            customGlint;
    public CustomAccessoriesModule      customAccessories;
    public ChunkFinderModule            chunkFinder;
    public FreeLookModule               freeLook;
    public AutoClickerModule            autoClicker;
    public CoordSnapperModule           coordSnapper;
    public RegionMapModule              regionMap;
    public ChatMacroModule              chatMacro;
    public FakePayModule                fakePay;
    public FakeStatsModule              fakeStats;
    public FakeRolesModule              fakeRoles;
    public StaffListModule              staffList;
    public ArmorTrimHiderModule         armorTrimHider;
    public SpawnerProtectModule         spawnerProtect;
    public GambleRiggerModule           gambleRigger;
    public AutoSprintModule             autoSprint;
    public AntiAfkModule                antiAfk;
    public XRayModule                   xRay;

    private Runnable openGuiAction  = () -> {};
    private BiConsumer<Module, Boolean> toggleListener = (m, e) -> {};

    // ─────────────────────────────────────────────────────────────────────────
    public ModuleManager() {
        for (Category cat : Category.values()) {
            byCategory.put(cat, new ArrayList<>());
        }
        registerNative();
        registerCodeEngine();
        registerWaterSRC();
    }

    // ── native modules ──────────────────────────────────────────────────────
    private void registerNative() {
        susChunkFinder = new Modules.SusChunkFinderModule();

        register(blockOutline  = new Modules.BlockOutlineModule());
        register(hud           = new Modules.HudModule());
        register(spotify       = new Modules.SpotifyModule());
        register(clickGui      = new Modules.ClickGuiModule());
        run("ConfigShare", "Import/export configs via codes", Category.CLIENT,
            new BooleanSetting("Include HUD Layout", "Share HUD positions too", true));

        register(autoTotem          = new AutoTotemModule());
        register(autoCrystal        = new AutoCrystalModule());
        register(anchorMacro        = new AnchorMacroModule());
        register(doubleAnchor       = new DoubleAnchorModule());
        register(autoInventoryTotem = new AutoInventoryTotemModule());
        register(aimAssist          = new AimAssistModule());
        register(maceSwap           = new MaceSwapModule());
        register(hitBox             = new HitBoxModule());
        register(elytraSwap         = new ElytraSwapModule());
        register(hoverTotem         = new HoverTotemModule());
        register(shieldBreaker      = new ShieldBreakerModule());
        register(triggerbot         = new TriggerbotModule());
        register(maceBomber         = new MaceBomberModule());

        register(skinProtect        = new SkinProtectModule());
        register(nameProtect        = new NameProtectModule());
        register(freecam            = new FreecamModule());
        register(view                = new ViewModule());
        register(autoTpa            = new AutoTpaModule());
        register(autoClicker        = new AutoClickerModule());
        register(fastUse            = new FastUseModule());
        register(nameTags           = new NameTagsModule());
        register(fakePay            = new FakePayModule());
        register(weatherNotifier    = new WeatherNotifierModule());
        register(fakeStats          = new FakeStatsModule());
        register(fakeRoles          = new FakeRolesModule());
        register(armorTrimHider     = new ArmorTrimHiderModule());
        register(customCrosshair    = new CustomCrosshairModule());
        run("Media/StaffNames/Icons", "Marks media & staff players", Category.RENDER,
            new BooleanSetting("Media", "Show media icons", true),
            new BooleanSetting("Staff", "Show staff icons", true),
            new ModeSetting("Position", "Icon position", "Prefix", "Prefix", "Suffix"));
        register(staffList          = new StaffListModule());
        register(customGlint        = new CustomGlintModule());
        register(customFov          = new CustomFovModule());
        register(coordSnapper       = new CoordSnapperModule());
        register(autoWalk           = new AutoWalkModule());
        register(zoom               = new ZoomModule());
        register(freeLook           = new FreeLookModule());
        register(swingSpeed         = new SwingSpeedModule());
        register(chatMacro          = new ChatMacroModule());

        register(spawnerProtect     = new SpawnerProtectModule());
        register(gambleRigger       = new GambleRiggerModule());
        register(chunkFinder        = new ChunkFinderModule());
        register(regionMap          = new RegionMapModule());
        register(susChunkFinder);

        register(blockEsp           = new BlockEspModule());
        register(storageEsp         = new StorageEspModule());
        register(blockEntityEsp     = new BlockEntityEspModule());
        register(debugHoleEsp       = new DebugHoleEspModule());
        register(fullbright         = new FullbrightModule());
        register(playerEsp          = new PlayerEspModule());
        register(mobEsp             = new MobEspModule());
        register(friendlyMobEsp     = new FriendlyMobEspModule());
        register(spawnerNametags    = new SpawnerNametagsModule());

        register(hitParticles       = new HitParticlesModule());
        register(customAccessories  = new CustomAccessoriesModule());
        register(motionBlur         = new MotionBlurModule());
        register(jumpCircles        = new JumpCirclesModule());

        // ── Zenith-ported native modules (take priority over nyx duplicates) ──
        register(new CriticalsModule());    // custom mixin — no auth gate
        register(new AutoRespawnModule());  // pure onTick — no auth gate
        register(autoSprint         = new AutoSprintModule());
        register(antiAfk            = new AntiAfkModule());
    }

    // ── CodeEngine modules (bridged) ──────────────────────────────────────────
    private void registerCodeEngine() {
        // COMBAT
        nyx(new dev.fede.nyx.module.modules.combat.KillAura(),             Category.COMBAT);
        nyx(new dev.fede.nyx.module.modules.combat.ReachModule(),          Category.COMBAT);
        nyx(new dev.fede.nyx.module.modules.combat.VelocityModule(),       Category.COMBAT);
        nyx(new dev.fede.nyx.module.modules.combat.AntiKnockbackModule(),  Category.COMBAT);
        // BacktrackModule excluded — its mixin inner class cannot be referenced directly (IllegalClassLoadError)
        nyx(new dev.fede.nyx.module.modules.combat.CriticalsModule(),      Category.COMBAT);
        nyx(new dev.fede.nyx.module.modules.combat.AutoArmor(),            Category.COMBAT);
        nyx(new dev.fede.nyx.module.modules.combat.ChestStealerModule(),   Category.COMBAT);
        nyx(new dev.fede.nyx.module.modules.combat.MaceAuraModule(),       Category.COMBAT);
        nyx(new dev.fede.nyx.module.modules.combat.AutoLogModule(),        Category.COMBAT);

        // MOVEMENT
        nyx(new dev.fede.nyx.module.modules.movement.SprintModule(),       Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.SpeedModule(),        Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.FlyModule(),          Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.ChunkSpoof(),         Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.AirJumpModule(),      Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.AntiVoidModule(),     Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.EagleAuraModule(),    Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.ElytraFlyModule(),    Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.HighJumpModule(),     Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.IceSpeedModule(),     Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.InventoryMoveModule(),Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.JesusModule(),        Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.LongJumpModule(),     Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.NoFallModule(),       Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.NoSlowModule(),       Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.SpiderModule(),       Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.StepModule(),         Category.MOVEMENT);
        nyx(new dev.fede.nyx.module.modules.movement.StrafeModule(),       Category.MOVEMENT);

        // PLAYER
        nyx(new dev.fede.nyx.module.modules.player.AntiAFKModule(),        Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.AutoEatModule(),        Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.AutoFish(),             Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.AutoToolModule(),       Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.AutoDropModule(),       Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.AutoRespawnModule(),    Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.FastPlaceModule(),      Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.MiddleClickModule(),    Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.NoJumpDelayModule(),    Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.PortalGodModeModule(),  Category.PLAYER);
        nyx(new dev.fede.nyx.module.modules.player.SwingAnimationModule(), Category.PLAYER);

        // RENDER
        nyx(new dev.fede.nyx.module.modules.render.ESP(),                  Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.NoHurtCamModule(),      Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.StorageESPModule(),     Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.StashFinder(),          Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.donutsmp.SusChunkFinderModule(), Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.SpawnerESPModule(),     Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.MovementTrailsModule(), Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.ProjectileArcModule(),  Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.VoidESPModule(),        Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.ItemESPModule(),        Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.Chams(),                Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.CornerBoxESPModule(),   Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.OutlinesModule(),       Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.OutlineESPModule(),     Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.TracersModule(),        Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.RadarModule(),          Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.NametagsModule(),       Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.HealthTagsModule(),     Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.ArrowESPModule(),       Category.RENDER);
        register(xRay = new XRayModule());                         // native XRay (no auth gate) — seed cave-diff lives inside it now
        nyx(new dev.fede.nyx.module.modules.render.ClearWorldModule(),     Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.ItemPhysicsModule(),    Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.ViewModelModule(),      Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.ZoomModule(),           Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.CauldronESPModule(),    Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.BedESPModule(),         Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.PortalESP(),            Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.HeatMapChunkRadarModule(), Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.RaidPlannerModule(),    Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.HoveredContainerPreviewModule(), Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.SpectatorDetectorModule(), Category.RENDER);
        nyx(new dev.fede.nyx.module.modules.render.FullbrightModule(),     Category.RENDER);

        // WORLD
        nyx(new dev.fede.nyx.module.modules.world.NukerModule(),           Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.ScaffoldModule(),        Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.AutoBridgeModule(),      Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.AutoTunnel(),            Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.AutoMLGModule(),         Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.AutoSmeltModule(),       Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.AutoStoreModule(),       Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.AutoTreeModule(),        Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.TimerSpeedModule(),      Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.InfoOrb(),               Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.RtpBaseFinder(),         Category.WORLD);
        nyx(new dev.fede.nyx.module.modules.world.ChunkKeeperModule(),     Category.WORLD);

        // DONUT
        nyx(new dev.fede.nyx.module.modules.donutsmp.AntiTrapModule(),       Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.AutoSell(),             Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.AutoSpawnerSellModule(),Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.ItemDropperModule(),    Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.SpawnerProtectModule(), Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.PrimeChunkFinderModule(),Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.SeedChunkFinder(),      Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.BlockEntityDebugModule(),Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.LightFinderModule(),    Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.ChunkFinderModule(),    Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.PlayerChunksModule(),   Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.HoleESPModule(),        Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.NetheriteFinderModule(),Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.RegionMap(),            Category.DONUT);
        nyx(new dev.fede.nyx.module.modules.donutsmp.NicknameModule(),       Category.DONUT);

        // ADDONS
        nyx(new dev.fede.nyx.module.modules.addons.BlockGlowModule(),        Category.ADDONS);
        nyx(new dev.fede.nyx.module.modules.addons.BlockOutlineModule(),     Category.ADDONS);
        nyx(new dev.fede.nyx.module.modules.addons.GlintCustomiserModule(),  Category.ADDONS);
        nyx(new dev.fede.nyx.module.modules.addons.HitParticlesModule(),     Category.ADDONS);
        nyx(new dev.fede.nyx.module.modules.addons.BreakParticlesModule(),   Category.ADDONS);
        nyx(new dev.fede.nyx.module.modules.addons.DragonWingsModule(),      Category.ADDONS);
        // SpotifyHUDModule (nyx) removed — it registered under the same name as
        // the live native Modules.SpotifyModule ("SpotifyHUD"), and register()
        // dedupes by name with native registered first, so this nyx one (and
        // its SmtcMediaClient/SpotifyClient + smtc-now-playing.ps1) was dead,
        // unreachable code. Deleted rather than left as silent dead weight —
        // it was also one of the two bundled .ps1 files a jar scan flags.
        nyx(new dev.fede.nyx.module.modules.addons.KillEffectsModule(),      Category.ADDONS);
        nyx(new dev.fede.nyx.module.modules.addons.PlayerParticlesModule(),  Category.ADDONS);

        // CLIENT
        nyx(new dev.fede.nyx.module.modules.client.RPCModule(),             Category.CLIENT);
        nyx(new dev.fede.nyx.module.modules.client.CustomTitleModule(),     Category.CLIENT);
        nyx(new dev.fede.nyx.module.modules.client.KeystrokeHUDModule(),    Category.CLIENT);
        nyx(new dev.fede.nyx.module.modules.client.TargetHUDModule(),       Category.CLIENT);
        nyx(new dev.fede.nyx.module.modules.client.TabGUIModule(),          Category.CLIENT);
        nyx(new dev.fede.nyx.module.modules.client.ChatFilter(),            Category.CLIENT);
        nyx(new dev.fede.nyx.module.modules.client.ThemeSelectorModule(),   Category.CLIENT);
        nyx(new dev.fede.nyx.module.modules.client.ChromaXPModule(),        Category.CLIENT);
        nyx(new dev.fede.nyx.module.modules.client.ClickSounds(),           Category.CLIENT);
        nyx(new dev.fede.nyx.module.modules.config.ConfigsModule(),         Category.CLIENT);
    }

    // ── WaterSRC modules (bridged) ────────────────────────────────────────────
    private void registerWaterSRC() {
        // DONUT — remaining intact water donut modules

        // PLAYER
        h2o(new dev.fede.water.module.modules.misc.AutoMine(),            Category.PLAYER);
        h2o(new dev.fede.water.module.modules.misc.HomeSetter(),          Category.PLAYER);
        h2o(new dev.fede.water.module.modules.misc.Sprint(),              Category.MOVEMENT);
        h2o(new dev.fede.water.module.modules.misc.ChatMacro(),           Category.PLAYER);

        // RENDER — all water render modules removed (decompiler type errors)
    }

    // ── bridge helpers ────────────────────────────────────────────────────────
    private void nyx(dev.fede.nyx.module.Module m, Category cat) {
        register(new NyxModuleBridge(m, cat));
    }
    private void h2o(dev.fede.water.module.Module m, Category cat) {
        register(new WaterModuleBridge(m, cat));
    }
    private void run(String name, String desc, Category cat, Setting<?>... settings) {
        register(new Modules.Placeholder(name, desc, cat, settings));
    }

    // ── registration ─────────────────────────────────────────────────────────
    public void register(Module module) {
        String n = module.getName();
        if (n == null || n.isBlank() || n.equalsIgnoreCase("Unknown") || n.equalsIgnoreCase("null")) return;
        // deduplicate — skip if a module with the same name is already registered
        String nLower = n.toLowerCase(java.util.Locale.ROOT);
        for (Module existing : modules) {
            if (existing.getName().toLowerCase(java.util.Locale.ROOT).equals(nLower)) return;
        }
        modules.add(module);
        byCategory.get(module.getCategory()).add(module);
        module.setToggleCallback(this::notifyToggle);
    }

    // ── accessors ─────────────────────────────────────────────────────────────
    public List<Module> all()                        { return modules; }
    public List<Module> inCategory(Category cat)    { return byCategory.get(cat); }
    public void setOpenGuiAction(Runnable a)                  { openGuiAction  = a; }
    public void setToggleListener(BiConsumer<Module, Boolean> l) { toggleListener = l; }
    public void notifyToggle(Module m, boolean e)             { toggleListener.accept(m, e); }

    // ── tick ─────────────────────────────────────────────────────────────────
    public boolean onKeyPressed(int keyCode) {
        if (clickGui.getKeybind().matches(keyCode)) {
            openGuiAction.run();
            return true;
        }
        boolean handled = false;
        for (Module m : modules) {
            if (m != clickGui && m.getKeybind().matches(keyCode)) {
                m.toggle(); handled = true;
            }
        }
        for (Module m : modules) {
            if (m.isEnabled() && m.onKeyPress(keyCode)) handled = true;
        }
        return handled;
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled() && m.getCategory() != Category.COMBAT) m.onTick();
        }
    }

    public void onCombatTick() {
        for (Module m : modules) {
            if (m.isEnabled() && m.getCategory() == Category.COMBAT) m.onTick();
        }
    }
}
