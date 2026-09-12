package dev.fede.zenithgui.base.theme;

// [lombok removed]
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class Theme {
    private final String name;
    private final String icon;
    private final Theme.Defaults defaults;
    private ColorRGBA color;
    private ColorRGBA secondColor;
    private ColorRGBA friendColor;
    private ColorRGBA gray;
    private ColorRGBA grayLight;
    private ColorRGBA foregroundLight;
    private ColorRGBA whiteGray;
    private ColorRGBA foregroundGray;
    private ColorRGBA foregroundLightStroke;
    private ColorRGBA foregroundColor;
    private ColorRGBA foregroundStroke;
    private ColorRGBA foregroundDark;
    private ColorRGBA white;
    private ColorRGBA backgroundColor;
    private boolean glow;
    private boolean blur;
    private boolean corners;
    public static final Theme DARK = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(181, 162, 255))
        .secondColor(new ColorRGBA(255, 203, 162))
        .friendColor(new ColorRGBA(181, 162, 255))
        .friendSecond(new ColorRGBA(255, 203, 162))
        .gray(new ColorRGBA(88, 87, 93))
        .grayLight(new ColorRGBA(128, 127, 133))
        .foregroundLight(new ColorRGBA(32, 31, 37))
        .whiteGray(new ColorRGBA(68, 67, 73))
        .foregroundGray(new ColorRGBA(48, 47, 53))
        .foregroundLightStroke(new ColorRGBA(38, 37, 43))
        .foreground(new ColorRGBA(28, 27, 33))
        .foregroundStroke(new ColorRGBA(35, 34, 40))
        .foregroundDark(new ColorRGBA(25, 24, 30))
        .white(new ColorRGBA(255, 255, 255))
        .background(new ColorRGBA(23, 22, 28))
        .glow(false)
        .blur(false)
        .corners(false)
        .build("Dark", "8");
    public static final Theme LIGHT = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(123, 93, 234))
        .secondColor(new ColorRGBA(255, 192, 121))
        .friendColor(new ColorRGBA(123, 93, 234))
        .friendSecond(new ColorRGBA(255, 192, 121))
        .gray(new ColorRGBA(138, 137, 143))
        .grayLight(new ColorRGBA(148, 147, 153))
        .foregroundLight(new ColorRGBA(236, 236, 236))
        .whiteGray(new ColorRGBA(178, 177, 183))
        .foregroundGray(new ColorRGBA(188, 187, 193))
        .foregroundLightStroke(new ColorRGBA(229, 229, 229))
        .foreground(new ColorRGBA(246, 246, 246))
        .foregroundStroke(new ColorRGBA(229, 229, 229))
        .foregroundDark(new ColorRGBA(251, 251, 251))
        .white(new ColorRGBA(23, 22, 28))
        .background(new ColorRGBA(255, 255, 255))
        .glow(false)
        .blur(false)
        .corners(false)
        .build("Light", "T");
    public static final Theme NEON = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(0, 255, 255))
        .secondColor(new ColorRGBA(255, 0, 255))
        .friendColor(new ColorRGBA(0, 255, 255))
        .friendSecond(new ColorRGBA(255, 0, 255))
        .gray(new ColorRGBA(40, 40, 40))
        .grayLight(new ColorRGBA(60, 60, 60))
        .foregroundLight(new ColorRGBA(20, 20, 20))
        .whiteGray(new ColorRGBA(50, 50, 50))
        .foregroundGray(new ColorRGBA(30, 30, 30))
        .foregroundLightStroke(new ColorRGBA(25, 25, 25))
        .foreground(new ColorRGBA(15, 15, 15))
        .foregroundStroke(new ColorRGBA(20, 20, 20))
        .foregroundDark(new ColorRGBA(10, 10, 10))
        .white(new ColorRGBA(255, 255, 255))
        .background(new ColorRGBA(5, 5, 5))
        .glow(true)
        .blur(false)
        .corners(true)
        .build("Neon", "N");
    public static final Theme OCEAN = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(0, 191, 255))
        .secondColor(new ColorRGBA(0, 255, 191))
        .friendColor(new ColorRGBA(0, 191, 255))
        .friendSecond(new ColorRGBA(0, 255, 191))
        .gray(new ColorRGBA(30, 50, 70))
        .grayLight(new ColorRGBA(50, 70, 90))
        .foregroundLight(new ColorRGBA(20, 30, 40))
        .whiteGray(new ColorRGBA(40, 60, 80))
        .foregroundGray(new ColorRGBA(25, 35, 45))
        .foregroundLightStroke(new ColorRGBA(15, 25, 35))
        .foreground(new ColorRGBA(10, 20, 30))
        .foregroundStroke(new ColorRGBA(15, 25, 35))
        .foregroundDark(new ColorRGBA(5, 15, 25))
        .white(new ColorRGBA(255, 255, 255))
        .background(new ColorRGBA(0, 10, 20))
        .glow(false)
        .blur(true)
        .corners(true)
        .build("Ocean", "O");
    public static final Theme SUNSET = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(255, 140, 0))
        .secondColor(new ColorRGBA(255, 69, 0))
        .friendColor(new ColorRGBA(255, 140, 0))
        .friendSecond(new ColorRGBA(255, 69, 0))
        .gray(new ColorRGBA(80, 50, 30))
        .grayLight(new ColorRGBA(100, 70, 50))
        .foregroundLight(new ColorRGBA(40, 25, 15))
        .whiteGray(new ColorRGBA(60, 40, 25))
        .foregroundGray(new ColorRGBA(50, 30, 20))
        .foregroundLightStroke(new ColorRGBA(35, 20, 10))
        .foreground(new ColorRGBA(25, 15, 5))
        .foregroundStroke(new ColorRGBA(30, 20, 10))
        .foregroundDark(new ColorRGBA(20, 10, 5))
        .white(new ColorRGBA(255, 255, 255))
        .background(new ColorRGBA(15, 5, 0))
        .glow(false)
        .blur(false)
        .corners(false)
        .build("Sunset", "S");
    public static final Theme FOREST = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(34, 139, 34))
        .secondColor(new ColorRGBA(160, 82, 45))
        .friendColor(new ColorRGBA(34, 139, 34))
        .friendSecond(new ColorRGBA(160, 82, 45))
        .gray(new ColorRGBA(40, 60, 40))
        .grayLight(new ColorRGBA(60, 80, 60))
        .foregroundLight(new ColorRGBA(20, 30, 20))
        .whiteGray(new ColorRGBA(50, 70, 50))
        .foregroundGray(new ColorRGBA(30, 40, 30))
        .foregroundLightStroke(new ColorRGBA(25, 35, 25))
        .foreground(new ColorRGBA(15, 25, 15))
        .foregroundStroke(new ColorRGBA(20, 30, 20))
        .foregroundDark(new ColorRGBA(10, 20, 10))
        .white(new ColorRGBA(255, 255, 255))
        .background(new ColorRGBA(5, 15, 5))
        .glow(false)
        .blur(false)
        .corners(true)
        .build("Forest", "F");
    public static final Theme PURPLE = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(138, 43, 226))
        .secondColor(new ColorRGBA(148, 0, 211))
        .friendColor(new ColorRGBA(138, 43, 226))
        .friendSecond(new ColorRGBA(148, 0, 211))
        .gray(new ColorRGBA(60, 40, 80))
        .grayLight(new ColorRGBA(80, 60, 100))
        .foregroundLight(new ColorRGBA(30, 20, 40))
        .whiteGray(new ColorRGBA(70, 50, 90))
        .foregroundGray(new ColorRGBA(40, 30, 50))
        .foregroundLightStroke(new ColorRGBA(35, 25, 45))
        .foreground(new ColorRGBA(25, 15, 35))
        .foregroundStroke(new ColorRGBA(30, 20, 40))
        .foregroundDark(new ColorRGBA(20, 10, 30))
        .white(new ColorRGBA(255, 255, 255))
        .background(new ColorRGBA(15, 5, 25))
        .glow(true)
        .blur(true)
        .corners(true)
        .build("Purple", "P");
    public static final Theme CYBERPUNK = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(0, 255, 255))
        .secondColor(new ColorRGBA(255, 20, 147))
        .friendColor(new ColorRGBA(0, 255, 255))
        .friendSecond(new ColorRGBA(255, 20, 147))
        .gray(new ColorRGBA(20, 20, 40))
        .grayLight(new ColorRGBA(40, 40, 60))
        .foregroundLight(new ColorRGBA(10, 10, 20))
        .whiteGray(new ColorRGBA(30, 30, 50))
        .foregroundGray(new ColorRGBA(15, 15, 30))
        .foregroundLightStroke(new ColorRGBA(12, 12, 25))
        .foreground(new ColorRGBA(8, 8, 20))
        .foregroundStroke(new ColorRGBA(10, 10, 25))
        .foregroundDark(new ColorRGBA(5, 5, 15))
        .white(new ColorRGBA(255, 255, 255))
        .background(new ColorRGBA(0, 0, 10))
        .glow(true)
        .blur(true)
        .corners(true)
        .build("Cyberpunk", "C");
    public static final Theme RETRO = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(255, 165, 0))
        .secondColor(new ColorRGBA(255, 215, 0))
        .friendColor(new ColorRGBA(255, 165, 0))
        .friendSecond(new ColorRGBA(255, 215, 0))
        .gray(new ColorRGBA(80, 60, 40))
        .grayLight(new ColorRGBA(100, 80, 60))
        .foregroundLight(new ColorRGBA(40, 30, 20))
        .whiteGray(new ColorRGBA(60, 50, 30))
        .foregroundGray(new ColorRGBA(50, 40, 25))
        .foregroundLightStroke(new ColorRGBA(35, 25, 15))
        .foreground(new ColorRGBA(25, 20, 10))
        .foregroundStroke(new ColorRGBA(30, 25, 15))
        .foregroundDark(new ColorRGBA(20, 15, 5))
        .white(new ColorRGBA(255, 255, 255))
        .background(new ColorRGBA(15, 10, 5))
        .glow(false)
        .blur(false)
        .corners(false)
        .build("Retro", "R");
    public static final Theme CUSTOM_THEME = Theme.ThemeBuilder.builder()
        .color(new ColorRGBA(181, 162, 255))
        .secondColor(new ColorRGBA(255, 203, 162))
        .friendColor(new ColorRGBA(181, 162, 255))
        .friendSecond(new ColorRGBA(255, 203, 162))
        .gray(new ColorRGBA(88, 87, 93))
        .grayLight(new ColorRGBA(128, 127, 133))
        .foregroundLight(new ColorRGBA(32, 31, 37))
        .whiteGray(new ColorRGBA(68, 67, 73))
        .foregroundGray(new ColorRGBA(48, 47, 53))
        .foregroundLightStroke(new ColorRGBA(38, 37, 43))
        .foreground(new ColorRGBA(28, 27, 33))
        .foregroundStroke(new ColorRGBA(35, 34, 40))
        .foregroundDark(new ColorRGBA(25, 24, 30))
        .white(new ColorRGBA(255, 255, 255))
        .background(new ColorRGBA(23, 22, 28))
        .glow(false)
        .blur(false)
        .corners(false)
        .build("Custom", "U");

    public Theme(String name, String icon, Theme.ThemeBuilder builder) {
        this.name = name;
        this.icon = icon;
        this.color = builder.color;
        this.secondColor = builder.secondColor;
        this.friendColor = builder.friendColor;
        this.gray = builder.gray;
        this.grayLight = builder.grayLight;
        this.foregroundLight = builder.foregroundLight;
        this.whiteGray = builder.whiteGray;
        this.foregroundGray = builder.foregroundGray;
        this.foregroundLightStroke = builder.foregroundLightStroke;
        this.foregroundColor = builder.foreground;
        this.foregroundStroke = builder.foregroundStroke;
        this.foregroundDark = builder.foregroundDark;
        this.white = builder.white;
        this.backgroundColor = builder.background;
        this.glow = builder.glow;
        this.blur = builder.blur;
        this.corners = builder.corners;
        this.defaults = new Theme.Defaults(
            builder.color,
            builder.secondColor,
            builder.friendColor,
            builder.friendSecond,
            builder.gray,
            builder.grayLight,
            builder.foregroundLight,
            builder.whiteGray,
            builder.foregroundGray,
            builder.foregroundLightStroke,
            builder.foreground,
            builder.foregroundStroke,
            builder.foregroundDark,
            builder.white,
            builder.background,
            builder.glow,
            builder.blur,
            builder.corners
        );
    }

    public void reset() {
        this.color = this.defaults.color;
        this.secondColor = this.defaults.secondColor;
        this.friendColor = this.defaults.friendColor;
        this.gray = this.defaults.gray;
        this.grayLight = this.defaults.grayLight;
        this.foregroundLight = this.defaults.foregroundLight;
        this.whiteGray = this.defaults.whiteGray;
        this.foregroundGray = this.defaults.foregroundGray;
        this.foregroundLightStroke = this.defaults.foregroundLightStroke;
        this.foregroundColor = this.defaults.foreground;
        this.foregroundStroke = this.defaults.foregroundStroke;
        this.foregroundDark = this.defaults.foregroundDark;
        this.white = this.defaults.white;
        this.backgroundColor = this.defaults.background;
        this.glow = this.defaults.glow;
        this.blur = this.defaults.blur;
        this.corners = this.defaults.corners;
    }

    public Theme interpolateTheme(Theme other, float delta) {
        return Theme.ThemeBuilder.builder()
            .color(this.color.mix(other.getColor(), delta))
            .secondColor(this.secondColor.mix(other.getSecondColor(), delta))
            .friendColor(this.friendColor.mix(other.getFriendColor(), delta))
            .gray(this.gray.mix(other.getGray(), delta))
            .grayLight(this.grayLight.mix(other.getGrayLight(), delta))
            .foregroundLight(this.foregroundLight.mix(other.getForegroundLight(), delta))
            .whiteGray(this.whiteGray.mix(other.getWhiteGray(), delta))
            .foregroundGray(this.foregroundGray.mix(other.getForegroundGray(), delta))
            .foregroundLightStroke(this.foregroundLightStroke.mix(other.getForegroundLightStroke(), delta))
            .foreground(this.foregroundColor.mix(other.getForegroundColor(), delta))
            .foregroundStroke(this.foregroundStroke.mix(other.getForegroundStroke(), delta))
            .foregroundDark(this.foregroundDark.mix(other.getForegroundDark(), delta))
            .white(this.white.mix(other.getWhite(), delta))
            .background(this.backgroundColor.mix(other.getBackgroundColor(), delta))
            .glow(this.glow)
            .blur(this.blur)
            .corners(this.corners)
            .build(other.name, other.icon);
    }

        public String getName() {
        return this.name;
    }

        public String getIcon() {
        return this.icon;
    }

        public Theme.Defaults getDefaults() {
        return this.defaults;
    }

        public ColorRGBA getColor() {
        return this.color;
    }

        public ColorRGBA getSecondColor() {
        return this.secondColor;
    }

        public ColorRGBA getFriendColor() {
        return this.friendColor;
    }

        public ColorRGBA getGray() {
        return this.gray;
    }

        public ColorRGBA getGrayLight() {
        return this.grayLight;
    }

        public ColorRGBA getForegroundLight() {
        return this.foregroundLight;
    }

        public ColorRGBA getWhiteGray() {
        return this.whiteGray;
    }

        public ColorRGBA getForegroundGray() {
        return this.foregroundGray;
    }

        public ColorRGBA getForegroundLightStroke() {
        return this.foregroundLightStroke;
    }

        public ColorRGBA getForegroundColor() {
        return this.foregroundColor;
    }

        public ColorRGBA getForegroundStroke() {
        return this.foregroundStroke;
    }

        public ColorRGBA getForegroundDark() {
        return this.foregroundDark;
    }

        public ColorRGBA getWhite() {
        return this.white;
    }

        public ColorRGBA getBackgroundColor() {
        return this.backgroundColor;
    }

        public boolean isGlow() {
        return this.glow;
    }

        public boolean isBlur() {
        return this.blur;
    }

        public boolean isCorners() {
        return this.corners;
    }

        public void setColor(ColorRGBA color) {
        this.color = color;
    }

        public void setSecondColor(ColorRGBA secondColor) {
        this.secondColor = secondColor;
    }

        public void setFriendColor(ColorRGBA friendColor) {
        this.friendColor = friendColor;
    }

        public void setGray(ColorRGBA gray) {
        this.gray = gray;
    }

        public void setGrayLight(ColorRGBA grayLight) {
        this.grayLight = grayLight;
    }

        public void setForegroundLight(ColorRGBA foregroundLight) {
        this.foregroundLight = foregroundLight;
    }

        public void setWhiteGray(ColorRGBA whiteGray) {
        this.whiteGray = whiteGray;
    }

        public void setForegroundGray(ColorRGBA foregroundGray) {
        this.foregroundGray = foregroundGray;
    }

        public void setForegroundLightStroke(ColorRGBA foregroundLightStroke) {
        this.foregroundLightStroke = foregroundLightStroke;
    }

        public void setForegroundColor(ColorRGBA foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

        public void setForegroundStroke(ColorRGBA foregroundStroke) {
        this.foregroundStroke = foregroundStroke;
    }

        public void setForegroundDark(ColorRGBA foregroundDark) {
        this.foregroundDark = foregroundDark;
    }

        public void setWhite(ColorRGBA white) {
        this.white = white;
    }

        public void setBackgroundColor(ColorRGBA backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

        public void setGlow(boolean glow) {
        this.glow = glow;
    }

        public void setBlur(boolean blur) {
        this.blur = blur;
    }

        public void setCorners(boolean corners) {
        this.corners = corners;
    }

    private static record Defaults(
        ColorRGBA color,
        ColorRGBA secondColor,
        ColorRGBA friendColor,
        ColorRGBA friendSecond,
        ColorRGBA gray,
        ColorRGBA grayLight,
        ColorRGBA foregroundLight,
        ColorRGBA whiteGray,
        ColorRGBA foregroundGray,
        ColorRGBA foregroundLightStroke,
        ColorRGBA foreground,
        ColorRGBA foregroundStroke,
        ColorRGBA foregroundDark,
        ColorRGBA white,
        ColorRGBA background,
        boolean glow,
        boolean blur,
        boolean corners
    ) {
    }

    public static class ThemeBuilder {
        private ColorRGBA color;
        private ColorRGBA secondColor;
        private ColorRGBA friendColor;
        private ColorRGBA friendSecond;
        private ColorRGBA gray;
        private ColorRGBA grayLight;
        private ColorRGBA foregroundLight;
        private ColorRGBA whiteGray;
        private ColorRGBA foregroundGray;
        private ColorRGBA foregroundLightStroke;
        private ColorRGBA foreground;
        private ColorRGBA foregroundStroke;
        private ColorRGBA foregroundDark;
        private ColorRGBA white;
        private ColorRGBA background;
        private boolean glow = false;
        private boolean blur = false;
        private boolean corners = false;

        public static Theme.ThemeBuilder builder() {
            return new Theme.ThemeBuilder();
        }

        public Theme.ThemeBuilder color(ColorRGBA color) {
            this.color = color;
            return this;
        }

        public Theme.ThemeBuilder secondColor(ColorRGBA secondColor) {
            this.secondColor = secondColor;
            return this;
        }

        public Theme.ThemeBuilder friendColor(ColorRGBA friendColor) {
            this.friendColor = friendColor;
            return this;
        }

        public Theme.ThemeBuilder friendSecond(ColorRGBA friendSecond) {
            this.friendSecond = friendSecond;
            return this;
        }

        public Theme.ThemeBuilder gray(ColorRGBA gray) {
            this.gray = gray;
            return this;
        }

        public Theme.ThemeBuilder grayLight(ColorRGBA grayLight) {
            this.grayLight = grayLight;
            return this;
        }

        public Theme.ThemeBuilder foregroundLight(ColorRGBA foregroundLight) {
            this.foregroundLight = foregroundLight;
            return this;
        }

        public Theme.ThemeBuilder whiteGray(ColorRGBA whiteGray) {
            this.whiteGray = whiteGray;
            return this;
        }

        public Theme.ThemeBuilder foregroundGray(ColorRGBA foregroundGray) {
            this.foregroundGray = foregroundGray;
            return this;
        }

        public Theme.ThemeBuilder foregroundLightStroke(ColorRGBA foregroundLightStroke) {
            this.foregroundLightStroke = foregroundLightStroke;
            return this;
        }

        public Theme.ThemeBuilder foreground(ColorRGBA foreground) {
            this.foreground = foreground;
            return this;
        }

        public Theme.ThemeBuilder foregroundStroke(ColorRGBA foregroundStroke) {
            this.foregroundStroke = foregroundStroke;
            return this;
        }

        public Theme.ThemeBuilder foregroundDark(ColorRGBA foregroundDark) {
            this.foregroundDark = foregroundDark;
            return this;
        }

        public Theme.ThemeBuilder white(ColorRGBA white) {
            this.white = white;
            return this;
        }

        public Theme.ThemeBuilder background(ColorRGBA background) {
            this.background = background;
            return this;
        }

        public Theme.ThemeBuilder glow(boolean glow) {
            this.glow = glow;
            return this;
        }

        public Theme.ThemeBuilder blur(boolean blur) {
            this.blur = blur;
            return this;
        }

        public Theme.ThemeBuilder corners(boolean corners) {
            this.corners = corners;
            return this;
        }

        public Theme build(String name, String icon) {
            return new Theme(name, icon, this);
        }
    }
}
