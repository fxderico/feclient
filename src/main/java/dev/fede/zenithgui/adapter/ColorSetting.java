package dev.fede.zenithgui.adapter;

import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

/**
 * Adapter for color settings in the Zenith menu.
 * Supports two modes:
 *   1. Wrapping our dev.fede.settings.ColorSetting (for module settings)
 *   2. Standalone (name + ColorRGBA) for ThemeElement colour customisation
 */
public class ColorSetting extends Setting {

    /** Supplier of the default/reset color. */
    public interface ColorGetter {
        ColorRGBA getDefaultColor();
    }

    private final dev.fede.settings.ColorSetting wrapped; // null when standalone
    private ColorRGBA standaloneColor;                    // used when wrapped == null
    private final ColorGetter colorGetter;

    // ── constructor 1: wraps our module setting ───────────────────────────────

    public ColorSetting(dev.fede.settings.ColorSetting wrapped) {
        super(wrapped.getName());
        this.wrapped = wrapped;
        this.standaloneColor = null;
        this.colorGetter = null;
        this.visible = () -> wrapped.isVisible();
    }

    // ── constructor 2: standalone with static default ─────────────────────────

    public ColorSetting(String name, ColorRGBA color) {
        this(name, color, () -> color);
    }

    // ── constructor 3: standalone + reset getter (Zenith ThemeElement style) ──

    public ColorSetting(String name, ColorRGBA color, ColorGetter colorGetter) {
        super(name);
        this.wrapped = null;
        this.standaloneColor = color;
        this.colorGetter = colorGetter;
    }

    // ── API ──────────────────────────────────────────────────────────────────

    public ColorRGBA getColor() {
        if (wrapped != null) {
            int argb = wrapped.get();
            int a = (argb >>> 24) & 0xFF;
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8)  & 0xFF;
            int b = argb & 0xFF;
            return new ColorRGBA(r, g, b, a == 0 ? 255 : a);
        }
        return standaloneColor != null ? standaloneColor : new ColorRGBA(255, 255, 255, 255);
    }

    public ColorRGBA getColor(float alpha) {
        return getColor().mulAlpha(alpha);
    }

    public int getIntColor() {
        return wrapped != null ? wrapped.get() : getColor().getRGB();
    }

    public void setColor(int argb) {
        if (wrapped != null) wrapped.set(argb);
        else standaloneColor = new ColorRGBA(argb);
    }

    public void setColor(ColorRGBA color) {
        if (wrapped != null) wrapped.set(color.getRGB());
        else standaloneColor = color;
    }

    public void reset() {
        if (wrapped != null) {
            wrapped.reset();
        } else if (colorGetter != null) {
            standaloneColor = colorGetter.getDefaultColor();
        }
    }
}
