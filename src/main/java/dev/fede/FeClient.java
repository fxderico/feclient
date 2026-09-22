package dev.fede;

import dev.fede.command.ViewCoordsCommand;
import dev.fede.config.ConfigManager;
import dev.fede.config.ConfigStore;
import dev.fede.gui.ClickGuiScreen;
import dev.fede.gui.GambleRiggerOverlay;
import dev.fede.hud.HudManager;
import dev.fede.module.Category;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.FreecamModule;
import dev.fede.notification.NotificationManager;
import dev.fede.render.EntityEspRenderer;
import dev.fede.render.MotionBlurRenderer;
import dev.fede.render.OverlayRenderer;
import dev.fede.render.SusChunkRenderer;
import dev.fede.spotify.SpotifyService;
import dev.fede.suschunk.ServerLightCache;
import dev.fede.theme.SoundSettings;
import dev.fede.theme.ThemeManager;
import dev.fede.util.UiSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * feClient — merged from three source clients: the native base, CodeEngine, and WaterSRC.
 * Primary GUI: the native NanoVG ClickGUI.
 * Module pool: all three clients, unified under ModuleManager.
 */
public class FeClient implements ClientModInitializer {

    public static final String MOD_ID      = "feclient";
    public static final String NAME        = "feClient";
    // was hardcoded "1.0.0" -> every UI surface that shows it (watermark, HUD,
    // menu) and the init log stayed frozen at 1.0.0 no matter the real build.
    // Read it from the loaded mod's own metadata (which fabric.mod.json fills
    // from mod.version at build) so it always matches the actual jar. Strip the
    // "+<mc>" build suffix so it shows a clean "1.0.9" rather than "1.0.9+1.21.11".
    public static final String VERSION     = resolveVersion();
    public static final Logger LOGGER      = LoggerFactory.getLogger(NAME);

    private static String resolveVersion() {
        try {
            String v = net.fabricmc.loader.api.FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .map(c -> c.getMetadata().getVersion().getFriendlyString())
                .orElse("dev");
            int plus = v.indexOf('+');
            return plus > 0 ? v.substring(0, plus) : v;
        } catch (Throwable t) {
            return "dev";
        }
    }

    private static ModuleManager   modules;
    private static ThemeManager    themes;
    private static ConfigManager   config;
    private static ConfigStore     configStore;
    private static HudManager      hud;
    private static NotificationManager notifications;
    private static SpotifyService  spotify;
    private static SoundSettings   soundSettings;
    private static boolean         startupSoundPlayed;

    // ── static accessors ──────────────────────────────────────────────────────
    public static ModuleManager   modules()       { return modules; }
    public static ThemeManager    themes()        { return themes; }
    public static ConfigManager   config()        { return config; }
    public static ConfigStore     configStore()   { return configStore; }
    public static HudManager      hud()           { return hud; }
    public static SpotifyService  spotify()       { return spotify; }
    public static SoundSettings   sounds()        { return soundSettings; }
    public static NotificationManager notifications() { return notifications; }

    // ── init ──────────────────────────────────────────────────────────────────
    @Override
    public void onInitializeClient() {
        LOGGER.info("{} {} initializing", NAME, VERSION);

        themes        = new ThemeManager();
        soundSettings = new SoundSettings();
        modules       = new ModuleManager();          // registers ALL modules (67 + nyx + water)
        spotify       = new SpotifyService();
        notifications = new NotificationManager(themes, modules.hud);
        hud           = new HudManager(modules, themes, spotify, notifications);
        config        = new ConfigManager(modules, themes);
        config.addSection("hud",    hud::toJson,                  hud::fromJson);
        config.addSection("panels", ClickGuiScreen.state()::toJson, ClickGuiScreen.state()::fromJson);
        config.addSection("sounds", soundSettings::toJson,        soundSettings::fromJson);
        config.load();
        configStore   = new ConfigStore(config);
        configStore.loadAll();

        // Discord presence defaults on; start it here too so a brand-new user
        // (no config file yet, so ThemeManager.fromJson never ran) still gets it.
        if (themes.isDiscordPresence()) {
            dev.fede.discord.DiscordPresence.start();
        }

        UiSounds.init(soundSettings);

        modules.setOpenGuiAction(() ->
            MinecraftClient.getInstance().setScreen(new ClickGuiScreen()));

        modules.setToggleListener((module, enabled) -> {
            if (!ConfigStore.applying) {
                if (module.getCategory() != Category.CLIENT) {
                    if (MinecraftClient.getInstance().world != null) {
                        if (modules.hud.notifications.get()) {
                            notifications.push(module.getName(), enabled);
                            UiSounds.notification(enabled);
                        }
                    }
                }
            }
        });

        OverlayRenderer.init(hud, notifications);

        // startup sound disabled — no sound on launch
        startupSoundPlayed = true; // set to true so it never fires

        GambleRiggerOverlay.register();
        ViewCoordsCommand.register();
        spotify.start();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> clearSusState());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client)       -> clearSusState());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            modules.onTick();
            EntityEspRenderer.tickGlow();
        });
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            FreecamModule f = modules.freecam;
            if (f != null && f.isActive()) FreecamModule.reapplyBodyInput(client);
            modules.onCombatTick();
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            config.save();
            spotify.stop();
        });

        LOGGER.info("{} ready — {} modules loaded", NAME, modules.all().size());
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private static void clearSusState() {
        ServerLightCache.get().clear();
        if (modules.susChunkFinder != null) modules.susChunkFinder.scanner.clear();
        SusChunkRenderer.reset();
        if (modules.chunkFinder       != null) modules.chunkFinder.clear();
        if (modules.blockEntityEsp    != null) modules.blockEntityEsp.clear();
        if (modules.jumpCircles       != null) modules.jumpCircles.clear();
        if (modules.hitParticles      != null) modules.hitParticles.clear();
        if (modules.customAccessories != null) modules.customAccessories.clear();
        MotionBlurRenderer.reset();
        EntityEspRenderer.clearGlow();
    }
}
