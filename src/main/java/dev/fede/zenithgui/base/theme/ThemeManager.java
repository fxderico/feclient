package dev.fede.zenithgui.base.theme;

// [event API removed]
// [event API removed]
import java.util.Arrays;
import java.util.List;
// [lombok removed]
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.animations.types.ColorCycleRGBA;
// [event removed]
import dev.fede.zenithgui.utility.render.display.base.Gradient;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.base.color.ColorUtil;

public class ThemeManager {
    private Theme prevTheme;
    private Theme currentTheme = Theme.DARK;
    private final Animation animation;
    private final ColorCycleRGBA colorCycleIcon;
    private final List<Theme> themes = Arrays.asList(
        Theme.DARK, Theme.LIGHT, Theme.NEON, Theme.OCEAN, Theme.SUNSET, Theme.FOREST, Theme.PURPLE, Theme.CYBERPUNK, Theme.RETRO, Theme.CUSTOM_THEME
    );
    private int themeIndex = 0;

    public ThemeManager() {
        // EventManager.register(this); — removed: feClient uses its own event bus
        this.animation = new Animation(200L, 1.0F, Easing.LINEAR);
        // animation starts done since initial value == target
        this.colorCycleIcon = new ColorCycleRGBA(
            List.of(this.getCurrentTheme().getColor(), this.getCurrentTheme().getColor(), this.getCurrentTheme().getColor(), this.getCurrentTheme().getColor()),
            500L
        );
    }

    public void switchTheme(Theme theme) {
        if (this.animation.isDone()) {
            this.prevTheme = this.currentTheme;
            this.currentTheme = theme;
            int index = this.themes.indexOf(theme);
            if (index != -1) {
                this.themeIndex = index;
            }

            this.animation.reset();
        }
    }

    public void switchTheme() {
        if (this.animation.isDone()) {
            this.prevTheme = this.currentTheme;
            this.themeIndex = (this.themeIndex + 1) % this.themes.size();
            this.currentTheme = this.themes.get(this.themeIndex);
            this.animation.reset();
        }
    }

    public void switchThemeByName(String name) {
        if (name != null) {
            Theme theme;
            switch (name) {
                case "Dark":
                    theme = Theme.DARK;
                    break;
                case "Light":
                    theme = Theme.LIGHT;
                    break;
                case "Neon":
                    theme = Theme.NEON;
                    break;
                case "Ocean":
                    theme = Theme.OCEAN;
                    break;
                case "Sunset":
                    theme = Theme.SUNSET;
                    break;
                case "Forest":
                    theme = Theme.FOREST;
                    break;
                case "Purple":
                    theme = Theme.PURPLE;
                    break;
                case "Cyberpunk":
                    theme = Theme.CYBERPUNK;
                    break;
                case "Retro":
                    theme = Theme.RETRO;
                    break;
                case "Custom":
                    theme = Theme.CUSTOM_THEME;
                    break;
                default:
                    return;
            }

            this.switchTheme(theme);
        }
    }

        /** Called each render frame to advance the color-cycle animation. */
        public void eventRender(Object event) {
        this.setColorCycle(
            this.colorCycleIcon.getBaseColors(),
            this.getCurrentTheme().getColor(),
            this.getCurrentTheme().getColor(),
            this.getCurrentTheme().getColor(),
            this.getCurrentTheme().getColor()
        );
        this.colorCycleIcon.update();
        this.animation.update(1.0F);
    }

    private void setColorCycle(List<ColorRGBA> colorCycle, ColorRGBA one, ColorRGBA two, ColorRGBA three, ColorRGBA four) {
        colorCycle.set(0, one);
        colorCycle.set(1, two);
        colorCycle.set(2, three);
        colorCycle.set(3, four);
    }

    public Theme getCurrentTheme() {
        return this.animation.isDone() ? this.currentTheme : this.prevTheme.interpolateTheme(this.currentTheme, this.animation.getValue());
    }

    public boolean is(Theme theme) {
        return this.currentTheme == theme;
    }

    public Gradient getClientColor() {
        return Gradient.of(this.getClientColor(0), this.getClientColor(90), this.getClientColor(180), this.getClientColor(270));
    }

    public ColorRGBA getClientColor(int index) {
        return ColorUtil.lerp(4, index, this.getCurrentTheme().getColor(), this.getCurrentTheme().getSecondColor());
    }

    public List<String> getAvailableThemeNames() {
        return this.themes.stream().map(Theme::getName).toList();
    }

    public List<Theme> getAllThemes() {
        return this.themes;
    }

        public Theme getPrevTheme() {
        return this.prevTheme;
    }

        public Animation getAnimation() {
        return this.animation;
    }

        public ColorCycleRGBA getColorCycleIcon() {
        return this.colorCycleIcon;
    }

        public List<Theme> getThemes() {
        return this.themes;
    }

        public int getThemeIndex() {
        return this.themeIndex;
    }
}
