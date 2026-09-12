package dev.fede.zenithgui.adapter;

import java.util.function.Supplier;

/**
 * Base adapter class replacing zenith.zov.client.modules.api.setting.Setting.
 * Used by MenuXxxSetting renderers.
 */
public abstract class Setting {
    protected final String name;
    protected Supplier<Boolean> visible = () -> true;

    protected Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public Supplier<Boolean> getVisible() {
        return visible;
    }

    public void setVisible(Supplier<Boolean> visible) {
        this.visible = visible;
    }
}
