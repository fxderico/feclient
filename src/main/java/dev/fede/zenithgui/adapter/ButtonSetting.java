package dev.fede.zenithgui.adapter;

/**
 * Adapter for a clickable button setting (no underlying wrapped type — created standalone).
 * Mirrors zenith.zov.client.modules.api.setting.impl.ButtonSetting.
 */
public class ButtonSetting extends Setting {

    private Runnable runnable;

    public ButtonSetting(String name, Runnable runnable) {
        super(name);
        this.runnable = runnable;
    }

    public void toggle() {
        if (runnable != null) runnable.run();
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }
}
