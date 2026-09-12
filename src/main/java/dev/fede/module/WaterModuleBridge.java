package dev.fede.module;

/**
 * Wraps a WaterSRC module so it appears in feClient's NanoVG ClickGUI.
 *
 * We intentionally bypass Water's Module.setEnabled() because it
 * references Water's own ModuleManager singleton and ToastManager —
 * neither of which is wired up in feClient.  Instead we call
 * onEnable() / onDisable() / onTick() directly.
 */
public final class WaterModuleBridge extends Module {

    private final dev.fede.water.module.Module delegate;

    public WaterModuleBridge(dev.fede.water.module.Module delegate, Category category) {
        super(
            delegate.getName() != null ? delegate.getName() : "Unknown",
            "",
            category
        );
        this.delegate = delegate;
    }

    // ── lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onEnable() {
        try { delegate.onEnable(); }
        catch (Throwable ignored) {}
    }

    @Override
    public void onDisable() {
        try { delegate.onDisable(); }
        catch (Throwable ignored) {}
    }

    @Override
    public void onTick() {
        try { delegate.onTick(); }
        catch (Throwable ignored) {}
    }

    public dev.fede.water.module.Module getDelegate() {
        return delegate;
    }
}
