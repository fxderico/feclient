package dev.fede.zenithgui.adapter;

import dev.fede.zenithgui.utility.render.display.Keyboard;

/**
 * Adapter wrapping our dev.fede.settings.KeybindSetting into the Zenith KeySetting API
 * expected by MenuKeySetting.
 */
public class KeySetting extends Setting {

    private final dev.fede.settings.KeybindSetting wrapped;

    public KeySetting(dev.fede.settings.KeybindSetting wrapped) {
        super(wrapped.getName());
        this.wrapped = wrapped;
        this.visible = () -> wrapped.isVisible();
    }

    public int getKeyCode() {
        return wrapped.get();
    }

    public void setKeyCode(int keyCode) {
        wrapped.set(keyCode);
    }

    public String getNameKey() {
        int key = wrapped.get();
        if (key == -1) return "";
        try {
            String name = Keyboard.getKeyName(key);
            return name != null ? name : "";
        } catch (Exception e) {
            return "";
        }
    }
}
