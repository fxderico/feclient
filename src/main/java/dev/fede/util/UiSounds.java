package dev.fede.util;

import dev.fede.theme.SoundSettings;

/** All sound calls are intentionally suppressed. */
public final class UiSounds {
    private UiSounds() {}

    public static void init(SoundSettings s)         {}
    public static void guiOpen()                     {}
    public static void guiClose()                    {}
    public static void hover()                       {}
    public static void toggle(boolean enabled)       {}
    public static void checkbox(boolean checked)     {}
    public static void select()                      {}
    public static void sliderTick(float normalized)  {}
    public static void keybindListen()               {}
    public static void keybindSet()                  {}
    public static void notification(boolean enabled) {}
    public static void panelCollapse()               {}
    public static void playStartup()                 {}
}
