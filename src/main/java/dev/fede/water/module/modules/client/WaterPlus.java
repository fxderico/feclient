/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 */
package dev.fede.water.module.modules.client;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.ModeSetting;
import dev.fede.water.setting.Setting;
import dev.fede.water.spotify.SpotifyAuth;
import dev.fede.water.utils.renderer.WaterFontRenderer;
import java.awt.Color;
import org.lwjgl.glfw.GLFW;

public final class WaterPlus
extends Module {
    private static WaterPlus INSTANCE;
    private final Setting<Color> accentColor = new Setting<Color>("Color", new Color(151, 71, 255, 255));
    private final Setting<Color> bgColor = new Setting<Color>("Background", new Color(12, 12, 12, 255));
    private final Setting<Boolean> animations = new Setting<Boolean>("Animations", true);
    private final Setting<Double> tracerWidth = new Setting<Double>("Tracer Width", 2.0, 1.0, 6.0);
    private final Setting<Double> guiRoundness = new Setting<Double>("GUI Roundness", 10.0, 0.0, 20.0);
    private final Setting<Integer> glassIntensity = new Setting<Integer>("Glass Intensity", 60, 0, 100);
    private final Setting<Boolean> menuBlur = new Setting<Boolean>("Menu Blur", true);
    private final Setting<Integer> accentGlow = new Setting<Integer>("Accent Glow", 0, 0, 100);
    private final ModeSetting rowStyle = new ModeSetting("Row Style", "Minimal", "Filled", "Minimal", "Outlined");
    private final ModeSetting headerStyle = new ModeSetting("Header Style", "Transparent", "Solid", "Gradient", "Transparent");
    private final ModeSetting panelShape = new ModeSetting("Panel Shape", "Rounded", "Rounded", "Sharp", "Pill");
    private final ModeSetting animSpeed = new ModeSetting("Anim Speed", "Normal", "Slow", "Normal", "Fast", "Off");
    private final ModeSetting layoutMode = new ModeSetting("Layout", "Columns", "Columns", "Single Panel");
    private final Setting<Boolean> notifications = new Setting<Boolean>("Notifications", true);
    private final Setting<Boolean> notifyIcons = new Setting<Boolean>("Notify Icons", true);
    private final Setting<Boolean> moduleIcons = new Setting<Boolean>("Module Icons", true);
    private final Setting<Boolean> uiSounds = new Setting<Boolean>("UI Sounds", true);
    private final Setting<String> guiKeyName = new Setting<String>("GUI Key", "RShift");
    private final ModeSetting menuSize = new ModeSetting("Menu Size", "3", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    private final Setting<String> spotifyClientId = new Setting<String>("Spotify Client ID", "");
    private static volatile int resolvedGuiKey;
    public static volatile boolean listeningGuiKey;

    public WaterPlus() {
        super("Water +", Category.e);
        this.addSetting(this.accentColor);
        this.addSetting(this.bgColor);
        this.addSetting(this.glassIntensity);
        this.addSetting(this.menuBlur);
        this.addSetting(this.accentGlow);
        this.addSetting(this.rowStyle);
        this.addSetting(this.headerStyle);
        this.addSetting(this.panelShape);
        this.addSetting(this.animSpeed);
        this.addSetting(this.layoutMode);
        this.addSetting(this.animations);
        this.addSetting(this.uiSounds);
        this.addSetting(this.moduleIcons);
        this.addSetting(this.notifyIcons);
        this.addSetting(this.guiRoundness);
        this.addSetting(this.guiKeyName);
        this.addSetting(this.spotifyClientId);
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        if (INSTANCE == null) {
            return;
        }
        String string;
        // font/spotify sync suppressed
        resolvedGuiKey = WaterPlus.nameToGlfw(this.guiKeyName.getValue());
    }

    public static void applyGuiKey(int glfwKeyCode, String keyName) {
        if (INSTANCE == null) {
            return;
        }
        WaterPlus.INSTANCE.guiKeyName.setValue(keyName);
        resolvedGuiKey = glfwKeyCode;
        listeningGuiKey = false;
    }

    public static String getGuiKeyDisplayName() {
        if (INSTANCE == null) {
            return "RShift";
        }
        String string = WaterPlus.INSTANCE.guiKeyName.getValue();
        return string != null && !string.isBlank() ? string : "RShift";
    }

    public static float getAccentGlow() {
        if (INSTANCE == null) {
            return 0.5f;
        }
        return (float)Math.max(0, Math.min(100, WaterPlus.INSTANCE.accentGlow.getValue())) / 100.0f;
    }

    public static String getRowStyle() {
        if (INSTANCE == null) {
            return "Filled";
        }
        return (String)WaterPlus.INSTANCE.rowStyle.getValue();
    }

    public static String getHeaderStyle() {
        if (INSTANCE == null) {
            return "Solid";
        }
        return (String)WaterPlus.INSTANCE.headerStyle.getValue();
    }

    public static float getEffectiveRoundness() {
        if (INSTANCE == null) {
            return 8.0f;
        }
        return switch ((String)WaterPlus.INSTANCE.panelShape.getValue()) {
            case "Sharp" -> 0.0f;
            case "Pill" -> 20.0f;
            default -> WaterPlus.getGuiRoundness();
        };
    }

    public static float getAnimSpeedMult() {
        if (INSTANCE == null) {
            return 1.0f;
        }
        return switch ((String)WaterPlus.INSTANCE.animSpeed.getValue()) {
            case "Slow" -> 0.4f;
            case "Fast" -> 2.5f;
            case "Off" -> 999.0f;
            default -> 1.0f;
        };
    }

    public static boolean animationsEffectivelyEnabled() {
        if (!WaterPlus.animationsEnabled()) {
            return false;
        }
        return !"Off".equals(INSTANCE == null ? "Normal" : WaterPlus.INSTANCE.animSpeed.getValue());
    }

    public static String getLayoutMode() {
        if (INSTANCE == null) {
            return "Columns";
        }
        return (String)WaterPlus.INSTANCE.layoutMode.getValue();
    }

    public static int getGuiKey() {
        return resolvedGuiKey > 0 ? resolvedGuiKey : 344;
    }

    public static boolean notificationsEnabled() {
        if (INSTANCE == null) {
            return true;
        }
        return WaterPlus.INSTANCE.notifications.getValue();
    }

    public static boolean uiSoundsEnabled() {
        if (INSTANCE == null) {
            return true;
        }
        return WaterPlus.INSTANCE.uiSounds.getValue();
    }

    public static boolean notificationIconsEnabled() {
        if (INSTANCE == null) {
            return true;
        }
        return WaterPlus.INSTANCE.notifyIcons.getValue();
    }

    public static boolean moduleIconsEnabled() {
        if (INSTANCE == null) {
            return true;
        }
        return WaterPlus.INSTANCE.moduleIcons.getValue();
    }

    public static boolean menuBlurEnabled() {
        if (INSTANCE == null) {
            return true;
        }
        return WaterPlus.INSTANCE.menuBlur.getValue();
    }

    public static float getGuiRoundness() {
        if (INSTANCE == null) {
            return 8.0f;
        }
        double d2 = WaterPlus.INSTANCE.guiRoundness.getValue();
        if (Double.isNaN(d2) || Double.isInfinite(d2)) {
            d2 = 8.0;
        }
        return (float)Math.max(0.0, Math.min(20.0, d2));
    }

    public static float getGlassIntensity() {
        if (INSTANCE == null) {
            return 0.6f;
        }
        int n = WaterPlus.INSTANCE.glassIntensity.getValue();
        return (float)Math.max(0, Math.min(100, n)) / 100.0f;
    }

    public static int getAccentARGB() {
        Color color = WaterPlus.getAccentColor();
        return color.getAlpha() << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }

    public static Color getAccentColor() {
        if (INSTANCE == null) {
            return new Color(151, 71, 255, 255);
        }
        return WaterPlus.INSTANCE.accentColor.getValue();
    }

    public static int getBackgroundARGB() {
        Color color = WaterPlus.getBackgroundColor();
        float f = WaterPlus.getGlassIntensity();
        int n = color.getAlpha();
        int n2 = (int)((float)n * (1.0f - f * 0.82f));
        n2 = Math.max(15, Math.min(255, n2));
        return n2 << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }

    public static Color getBackgroundColor() {
        if (INSTANCE == null) {
            return new Color(12, 12, 12, 255);
        }
        Color color = WaterPlus.INSTANCE.bgColor.getValue();
        if (color == null) {
            return new Color(12, 12, 12, 255);
        }
        if (color.getAlpha() < 200) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
        }
        return color;
    }

    public static boolean animationsEnabled() {
        if (INSTANCE == null) {
            return true;
        }
        return WaterPlus.INSTANCE.animations.getValue();
    }

    public static int menuSizePercent() {
        if (INSTANCE == null) {
            return 5;
        }
        try {
            return Integer.parseInt((String)WaterPlus.INSTANCE.menuSize.getValue());
        }
        catch (Exception exception) {
            return 5;
        }
    }

    public static String getSpotifyClientId() {
        if (INSTANCE == null) {
            return "";
        }
        String string = WaterPlus.INSTANCE.spotifyClientId.getValue();
        return string != null ? string : "";
    }

    public static float tracerLineWidth() {
        if (INSTANCE == null) {
            return 2.0f;
        }
        double d2 = 2.0;
        try {
            d2 = WaterPlus.INSTANCE.tracerWidth.getValue();
        }
        catch (Exception exception) {}
        if (Double.isNaN(d2) || Double.isInfinite(d2)) {
            d2 = 1.0;
        }
        d2 = Math.max(1.0, Math.min(6.0, d2));
        return (float)d2;
    }

    public static int nameToGlfw(String name) {
        if (name == null) {
            return 344;
        }
        return switch (name) {
            case "RShift" -> 344;
            case "LShift" -> 340;
            case "RCtrl" -> 345;
            case "LCtrl" -> 341;
            case "RAlt" -> 346;
            case "LAlt" -> 342;
            case "Enter" -> 257;
            case "Tab" -> 258;
            case "Insert" -> 260;
            case "Delete" -> 261;
            case "Home" -> 268;
            case "End" -> 269;
            case "PageUp" -> 266;
            case "PageDn" -> 267;
            default -> {
                if (name.length() == 1) {
                    int var1_2;
                    yield var1_2 = GLFW.glfwGetKeyScancode((int)name.charAt(0)) > 0 ? (int)name.toUpperCase().charAt(0) : 344;
                }
                if (name.startsWith("F") && name.length() <= 3) {
                    try {
                        int var1_3 = Integer.parseInt(name.substring(1));
                        yield var1_3 = 290 + (var1_3 - 1);
                    }
                    catch (Exception v1) {}
                }
                if (name.startsWith("KP")) {
                    try {
                        int var1_4 = Integer.parseInt(name.substring(2));
                        yield var1_4 = 320 + var1_4;
                    }
                    catch (Exception v2) {}
                }
                yield 344;
            }
        };
    }

    static String _c0f4a84b9a3() {
        return "I";
    }

    static {
        resolvedGuiKey = 344;
        listeningGuiKey = false;
    }
}


