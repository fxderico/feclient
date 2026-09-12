package dev.fede.module;

/**
 * Wraps a CodeEngine (nyx) module so it participates in feClient's
 * NanoVG ClickGUI and keybind system.
 *
 * Toggle → calls nyx module's run5(bool) (their enable/disable method).
 * onTick → calls nyx module's run3().
 * onRender is handled separately via NyxRenderTicker.
 */
public final class NyxModuleBridge extends Module {

    private final dev.fede.nyx.module.Module delegate;

    public NyxModuleBridge(dev.fede.nyx.module.Module delegate, Category category) {
        super(
            orFallback(delegate.getString(), "Unknown"),
            orFallback(delegate.getString4(), ""),
            category
        );
        this.delegate = delegate;
        // expose this nyx module inside NyxClient.MODULES so internal lookups work
        dev.fede.nyx.NyxClient.MODULES.run2(delegate);
    }

    private static String orFallback(String s, String fallback) {
        return (s != null && !s.isBlank()) ? s : fallback;
    }

    // ── lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onEnable() {
        if (!delegate.isEnabled3()) {
            delegate.run5(true);
        }
    }

    @Override
    public void onDisable() {
        if (delegate.isEnabled3()) {
            delegate.run5(false);
        }
    }

    @Override
    public void onTick() {
        try {
            delegate.run3();
        } catch (Throwable ignored) {}
    }

    /** Suffix shown in the arraylist (e.g. mode name). */
    @Override
    public String getSuffix() {
        String s = delegate.getString3();
        return s != null ? s : "";
    }

    public dev.fede.nyx.module.Module getDelegate() {
        return delegate;
    }
}
