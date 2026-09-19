package dev.fede.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.KeybindSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.settings.StringSetting;

/**
 * Wraps a CodeEngine (nyx) module so it participates in feClient's
 * NanoVG ClickGUI and keybind system.
 *
 * Toggle → calls nyx module's run5(bool) (their enable/disable method).
 * onTick → calls nyx module's run3().
 * onRender is handled separately via NyxRenderTicker.
 *
 * Settings mirroring: nyx modules keep their own settings
 * (dev.fede.nyx.setting.*) as private fields and read them directly in
 * their own run()/run3() logic — this bridge never touches those fields.
 * What it DOES do is build a live dev.fede.settings.* proxy for each one
 * (get()/set() overridden to read/write straight through to the nyx
 * setting) and addSetting() it here, so the ClickGUI — which only ever
 * walks Module.getSettings() — actually has something to draw. Before this,
 * every nyx-bridged module (which is most of Combat/Movement/World/Donut/
 * Addons/Client — Speed, Fly, KillAura, Scaffold, all of it) registered zero
 * settings here: the sliders/toggles existed on the nyx side and were
 * simply never exposed, so the panel showed nothing to adjust. There's no
 * separate state to keep in sync since the proxy never stores its own
 * value — it's a window onto the real field, not a copy of it.
 *
 * dev.fede.nyx.setting.DoubleListSetting has no main-side equivalent (no
 * multi-value setting type exists in dev.fede.settings yet) and is skipped
 * — rare enough in practice that it wasn't worth inventing a new Setting
 * type for tonight. Everything else (Boolean/Number/Mode/String/Color/Bind)
 * is mirrored.
 *
 * toJson()/fromJson() ALSO get overridden per mirrored setting, not just
 * get()/set(). Every one of the base dev.fede.settings.* classes'
 * toJson()/fromJson() implementations reads/writes the protected `value`
 * field directly rather than going through get()/set() — reasonable for a
 * plain setting where `value` IS the real state, but these mirrors never
 * touch that field (get()/set() proxy straight to the real nyx setting
 * instead), so it just sits frozen at whatever the constructor happened to
 * pass in. Without overriding toJson/fromJson too: save would serialize
 * that stale constructor-time snapshot forever, and load would write into
 * a field nothing reads, silently doing nothing. That was the actual bug
 * behind "I picked Packet on NoFall and it didn't save" — not specific to
 * NoFall, every mirrored setting on every nyx-bridged module had it.
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
        mirrorSettings();
    }

    private static String orFallback(String s, String fallback) {
        return (s != null && !s.isBlank()) ? s : fallback;
    }

    // ── settings mirroring ──────────────────────────────────────────────────

    private void mirrorSettings() {
        for (dev.fede.nyx.setting.Setting nyxSetting : delegate.getList()) {
            try {
                mirrorOne(nyxSetting);
            } catch (Throwable ignored) {
                // one bad/unexpected setting shouldn't take the whole module's
                // panel down — worst case that one control is just missing.
            }
        }
    }

    private void mirrorOne(dev.fede.nyx.setting.Setting nyxSetting) {
        if (nyxSetting instanceof dev.fede.nyx.setting.BooleanSetting src) {
            addSetting(new BooleanSetting(src.getName(), "", src.getValue()) {
                @Override public Boolean get() { return src.getValue(); }
                @Override public void set(Boolean v) { src.setValue(v != null && v); }
                @Override public JsonElement toJson() { return new JsonPrimitive(get()); }
                @Override public void fromJson(JsonElement e) {
                    if (e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean()) set(e.getAsBoolean());
                }
            }).visibleWhen(src::isVisible);

        } else if (nyxSetting instanceof dev.fede.nyx.setting.NumberSetting src) {
            addSetting(new SliderSetting(src.getName(), "", src.getValue(), src.getMin(), src.getMax(), src.step()) {
                @Override public Double get() { return src.getValue(); }
                @Override public void set(Double v) { src.setValue(v == null ? 0.0 : v); }
                @Override public JsonElement toJson() { return new JsonPrimitive(get()); }
                @Override public void fromJson(JsonElement e) {
                    if (e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) set(e.getAsDouble());
                }
            }).visibleWhen(src::isVisible);

        } else if (nyxSetting instanceof dev.fede.nyx.setting.ModeSetting src) {
            java.util.List<String> modes = src.getModes();
            if (modes.isEmpty()) return;
            String def = modes.contains(src.getMode()) ? src.getMode() : modes.get(0);
            addSetting(new ModeSetting(src.getName(), "", def, modes.toArray(new String[0])) {
                @Override public String get() { return src.getMode(); }
                @Override public void set(String v) { src.setMode(v); }
                @Override public JsonElement toJson() { return new JsonPrimitive(get()); }
                @Override public void fromJson(JsonElement e) {
                    if (e != null && e.isJsonPrimitive() && modes.contains(e.getAsString())) set(e.getAsString());
                }
            }).visibleWhen(src::isVisible);

        } else if (nyxSetting instanceof dev.fede.nyx.setting.StringSetting src) {
            addSetting(new StringSetting(src.getName(), "", src.getValue(), Math.max(1, src.getMaxLen()), "") {
                @Override public String get() { return src.getValue(); }
                @Override public void set(String v) { src.setValue(v); }
                @Override public JsonElement toJson() { return new JsonPrimitive(get()); }
                @Override public void fromJson(JsonElement e) {
                    if (e != null && e.isJsonPrimitive()) set(e.getAsString());
                }
            }).visibleWhen(src::isVisible);

        } else if (nyxSetting instanceof dev.fede.nyx.setting.ColorSetting src) {
            addSetting(new ColorSetting(src.getName(), "", src.getValue()) {
                @Override public Integer get() { return src.getValue(); }
                @Override public void set(Integer v) { src.setValue(v == null ? 0 : v); }
                @Override public JsonElement toJson() { return new JsonPrimitive(get()); }
                @Override public void fromJson(JsonElement e) {
                    if (e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) set(e.getAsInt());
                }
            }).visibleWhen(src::isVisible);

        } else if (nyxSetting instanceof dev.fede.nyx.setting.BindSetting src) {
            addSetting(new KeybindSetting(src.getName(), "", src.getValue()) {
                @Override public Integer get() { return src.getValue(); }
                @Override public void set(Integer v) { src.setValue(v == null ? -1 : v); }
                @Override public JsonElement toJson() { return new JsonPrimitive(get()); }
                @Override public void fromJson(JsonElement e) {
                    if (e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber()) set(e.getAsInt());
                }
            }).visibleWhen(src::isVisible);
        }
        // dev.fede.nyx.setting.DoubleListSetting — intentionally skipped, see class javadoc.
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
