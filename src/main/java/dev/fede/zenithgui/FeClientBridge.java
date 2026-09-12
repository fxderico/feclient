package dev.fede.zenithgui;

import dev.fede.FeClient;
import dev.fede.module.Module;
import dev.fede.zenithgui.base.theme.ThemeManager;
import dev.fede.zenithgui.client.screens.menu.MenuScreen;

import java.util.Collections;
import java.util.List;

/**
 * Bridge between the Zenith UI and feClient's module/theme system.
 * Provides a stable singleton that the Zenith menu code can reach via getInstance().
 */
public final class FeClientBridge {

    private static final FeClientBridge INSTANCE = new FeClientBridge();

    private final ThemeManager themeManager = new ThemeManager();
    private final BridgeModuleManager moduleManager = new BridgeModuleManager();

    private FeClientBridge() {}

    public static FeClientBridge getInstance() {
        return INSTANCE;
    }

    /** The Zenith UI ThemeManager (independent of feClient's NanoVG theme). */
    public ThemeManager getThemeManager() {
        return themeManager;
    }

    /** Thin wrapper around feClient's ModuleManager. */
    public BridgeModuleManager getModuleManager() {
        return moduleManager;
    }

    /**
     * Stub interface object providing UI feature flags (blur, glow, etc.).
     * All features return conservative defaults so the menu renders cleanly.
     */
    public static IInterface getInterface() {
        return IInterface.INSTANCE;
    }

    /** Minimal interface stub for Zenith UI feature flags. */
    public static final class IInterface {
        static final IInterface INSTANCE = new IInterface();
        private IInterface() {}

        public boolean isBlur()       { return false; }
        public int     getGlowRadius(){ return 10; }
    }

    /**
     * Returns the currently-open MenuScreen, or null if none is open.
     * Popup setting widgets call this to register themselves with the active screen.
     */
    @SuppressWarnings("unchecked")
    public MenuScreen getMenuScreen() {
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        if (mc != null && mc.currentScreen instanceof MenuScreen s) {
            return s;
        }
        return null;
    }

    // ── BridgeModuleManager ───────────────────────────────────────────────────

    public static final class BridgeModuleManager {

        /** Returns all registered modules (native + nyx + water bridges). */
        public List<Module> getModules() {
            dev.fede.module.ModuleManager mm = FeClient.modules();
            return mm != null ? mm.all() : Collections.emptyList();
        }
    }
}
