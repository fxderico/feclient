package dev.fede.zenithgui.adapter;

/**
 * Adapter wrapping our dev.fede.settings.SliderSetting into the Zenith NumberSetting API
 * expected by MenuSliderSetting.
 */
public class NumberSetting extends Setting {

    private final dev.fede.settings.SliderSetting wrapped;

    public NumberSetting(dev.fede.settings.SliderSetting wrapped) {
        super(wrapped.getName());
        this.wrapped = wrapped;
        this.visible = () -> wrapped.isVisible();
    }

    public float getCurrent() {
        return wrapped.getFloat();
    }

    public void setCurrent(float value) {
        wrapped.set((double) value);
    }

    public float getMin() {
        return (float) wrapped.getMin();
    }

    public float getMax() {
        return (float) wrapped.getMax();
    }

    /** Step / increment between slider ticks. */
    public float getIncrement() {
        return (float) wrapped.getStep();
    }

    public String getDescription() {
        return wrapped.getDescription() != null ? wrapped.getDescription() : "";
    }
}
