package dev.fede.zenithgui.adapter;

/**
 * Adapter wrapping our dev.fede.settings.BooleanSetting into the Zenith setting API
 * expected by MenuBooleanSetting.
 */
public class BooleanSetting extends Setting {

    private final dev.fede.settings.BooleanSetting wrapped;

    public BooleanSetting(dev.fede.settings.BooleanSetting wrapped) {
        super(wrapped.getName());
        this.wrapped = wrapped;
        // inherit visibility from underlying setting
        this.visible = () -> wrapped.isVisible();
    }

    public boolean isEnabled() {
        return wrapped.get();
    }

    public void setEnabled(boolean enabled) {
        wrapped.set(enabled);
    }

    public void toggle() {
        wrapped.toggle();
    }

    public String getDescription() {
        return wrapped.getDescription() != null ? wrapped.getDescription() : "";
    }
}
