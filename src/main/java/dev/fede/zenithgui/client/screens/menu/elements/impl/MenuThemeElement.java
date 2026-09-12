package dev.fede.zenithgui.client.screens.menu.elements.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dev.fede.FeClient;
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.module.Category;
import dev.fede.zenithgui.adapter.ButtonSetting;
import dev.fede.zenithgui.adapter.ColorSetting;
import dev.fede.zenithgui.FeClientBridge;
import dev.fede.zenithgui.client.screens.menu.elements.api.AbstractMenuElement;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuButtonSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuColorSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

public class MenuThemeElement extends AbstractMenuElement {
    private final ColorSetting color;
    private final ColorSetting secondColor;
    private ColorSetting gray;
    private ColorSetting grayLight;
    private ColorSetting foregroundLight;
    private ColorSetting whiteGray;
    private ColorSetting foregroundGray;
    private ColorSetting foregroundLightStroke;
    private ColorSetting foregroundColor;
    private ColorSetting foregroundStroke;
    private ColorSetting foregroundDark;
    private ColorSetting white;
    private ColorSetting backgroundColor;
    private MenuButtonSetting button;
    private final List<MenuSetting> settings = new ArrayList<>();
    private final Theme theme;
    private final Animation animation;
    private final Animation animationPosition;
    private final Animation animationY;
    private Rect bounds;
    private int lastColum = -1;
    boolean animated = false;

    public MenuThemeElement(Theme theme) {
        this.theme = theme;
        this.animation = new Animation(200L, FeClientBridge.getInstance().getThemeManager().is(theme) ? 1.0F : 0.0F, Easing.LINEAR);
        this.animationPosition = new Animation(150L, 1.0F, Easing.QUAD_IN_OUT);
        this.animationY = new Animation(150L, 1.0F, Easing.QUAD_IN_OUT);
        this.color = new ColorSetting("Primary Color", theme.getColor(), Theme.DARK::getColor);
        this.secondColor = new ColorSetting("Secondary Color", theme.getSecondColor(), Theme.DARK::getSecondColor);
        if (this.theme == Theme.CUSTOM_THEME) {
            this.backgroundColor = new ColorSetting("GUI Background Color", theme.getBackgroundColor(), Theme.DARK::getBackgroundColor);
            this.foregroundColor = new ColorSetting("Foreground Background", theme.getForegroundColor(), Theme.DARK::getForegroundColor);
            this.foregroundLight = new ColorSetting("Lighter Background", theme.getForegroundLight(), Theme.DARK::getForegroundLight);
            this.foregroundDark = new ColorSetting("Dark Background", theme.getForegroundDark(), Theme.DARK::getForegroundDark);
            this.foregroundGray = new ColorSetting("Background (Gray)", theme.getForegroundGray(), Theme.DARK::getForegroundGray);
            this.white = new ColorSetting("Primary Text", theme.getWhite(), Theme.DARK::getWhite);
            this.whiteGray = new ColorSetting("Disabled Icons", theme.getWhiteGray(), Theme.DARK::getWhiteGray);
            this.gray = new ColorSetting("Disabled Text", theme.getGray(), Theme.DARK::getGray);
            this.grayLight = new ColorSetting("Semi Disabled Text", theme.getGrayLight(), Theme.DARK::getGrayLight);
            this.foregroundLightStroke = new ColorSetting("Brighter Stroke", theme.getForegroundLightStroke(), Theme.DARK::getForegroundLightStroke);
            this.foregroundStroke = new ColorSetting("Stroke", theme.getForegroundStroke(), Theme.DARK::getForegroundStroke);
            Collections.addAll(
                this.settings,
                new MenuColorSetting(this.color),
                new MenuColorSetting(this.secondColor),
                new MenuColorSetting(this.backgroundColor),
                new MenuColorSetting(this.foregroundColor),
                new MenuColorSetting(this.foregroundLight),
                new MenuColorSetting(this.foregroundDark),
                new MenuColorSetting(this.foregroundGray),
                new MenuColorSetting(this.white),
                new MenuColorSetting(this.whiteGray),
                new MenuColorSetting(this.gray),
                new MenuColorSetting(this.grayLight),
                new MenuColorSetting(this.foregroundLightStroke),
                new MenuColorSetting(this.foregroundStroke)
            );
            this.button = new MenuButtonSetting(new ButtonSetting("Reset", () -> {
                for (MenuSetting setting : this.settings) {
                    if (setting instanceof MenuColorSetting colorSetting) {
                        colorSetting.getSetting().reset();
                    }
                }
            }));
            this.settings.add(this.button);
        } else {
            Collections.addAll(this.settings, new MenuColorSetting(this.color), new MenuColorSetting(this.secondColor));
        }
    }

    @Override
    public void render(UIContext ctx, float mouseX, float mouseY, Font font, float x, float y, float moduleWidth, float alpha, int colum) {
        if (this.lastColum == -1) {
            this.lastColum = colum;
        }

        if (this.lastColum != colum) {
            this.animated = true;
            this.animationPosition.animateTo(x);
            this.animationY.animateTo(y);
            this.lastColum = colum;
        }

        if (this.animated) {
            x = this.animationPosition.update(x);
            y = this.animationY.update(y);
            if (this.animationPosition.isDone() && this.animationY.isDone()) {
                this.animated = false;
            }
        } else {
            this.animationPosition.reset(x);
            this.animationY.reset(y);
        }

        this.animation.update(FeClientBridge.getInstance().getThemeManager().is(this.theme) ? 1.0F : 0.0F);
        float moduleHeight = 22.0F;
        Theme curTheme = FeClientBridge.getInstance().getThemeManager().getCurrentTheme();
        ColorRGBA moduleBg = curTheme.getForegroundColor().mulAlpha(alpha);
        boolean hasSettings = this.hasSettings();
        float settingAreaHeight = this.getHeight();
        ColorRGBA settingBg = curTheme.getForegroundDark().mulAlpha(alpha);
        this.bounds = new Rect(x, y, moduleWidth, moduleHeight);
        if (hasSettings) {
            ctx.drawRoundedRect(x, y, moduleWidth, settingAreaHeight, BorderRadius.all(8.0F), settingBg);
            ctx.drawRoundedRect(x, y, moduleWidth, moduleHeight, BorderRadius.top(8.0F, 8.0F), moduleBg);
            DrawUtil.drawRoundedBorder(
                ctx.getMatrices(), x, y, moduleWidth, settingAreaHeight, -0.1F, BorderRadius.all(8.0F), curTheme.getForegroundStroke().mulAlpha(alpha)
            );
        } else {
            ctx.drawRoundedRect(x, y, moduleWidth, moduleHeight, BorderRadius.all(8.0F), moduleBg);
            DrawUtil.drawRoundedBorder(
                ctx.getMatrices(), x, y, moduleWidth, moduleHeight, -0.1F, BorderRadius.all(8.0F), curTheme.getForegroundStroke().mulAlpha(alpha)
            );
        }

        ColorRGBA enabledColor = curTheme.getGray().mix(curTheme.getColor(), this.animation.getValue()).mulAlpha(alpha);
        ColorRGBA textColor = curTheme.getGrayLight().mix(curTheme.getWhite(), this.animation.getValue()).mulAlpha(alpha);
        ctx.drawText(Fonts.ICONS.getFont(5.5F), this.theme.getIcon(), x + 8.0F, y + 9.0F, enabledColor);
        ctx.drawText(font, this.theme.getName(), x + 18.0F, y + 9.0F, textColor);
        float keyBoxWidth = 22.5F;
        float keyBoxX = x + moduleWidth - keyBoxWidth;
        ColorRGBA badgeColor = this.theme.getColor().mulAlpha(alpha);
        ctx.drawRoundedRect(
            keyBoxX, y, keyBoxWidth, moduleHeight, hasSettings ? BorderRadius.topRight(8.0F) : new BorderRadius(0.0F, 6.0F, 6.0F, 0.0F), badgeColor
        );
        float padding = 8.0F;
        float startY = y + moduleHeight + padding;
        ColorRGBA descriptionColor = curTheme.getWhiteGray().mix(curTheme.getGrayLight(), this.animation.getValue()).mulAlpha(alpha);

        for (MenuSetting setting : this.settings) {
            setting.render(ctx, mouseX, mouseY, x, startY, moduleWidth, alpha, this.animation.getValue(), enabledColor, textColor, descriptionColor, curTheme);
            startY += setting.getHeight() + 8.0F;
        }

        if (hasSettings) {
            DrawUtil.drawRoundedBorder(
                ctx.getMatrices(), x, y, moduleWidth, settingAreaHeight, -0.1F, BorderRadius.all(8.0F), curTheme.getForegroundStroke().mulAlpha(alpha)
            );
        } else {
            DrawUtil.drawRoundedBorder(
                ctx.getMatrices(), x, y, moduleWidth, moduleHeight, -0.1F, BorderRadius.all(8.0F), curTheme.getForegroundStroke().mulAlpha(alpha)
            );
        }

        if (this.theme == Theme.CUSTOM_THEME) {
            this.theme.setColor(this.color.getColor());
            this.theme.setSecondColor(this.secondColor.getColor());
            this.theme.setGray(this.gray.getColor());
            this.theme.setGrayLight(this.grayLight.getColor());
            this.theme.setForegroundLight(this.foregroundLight.getColor());
            this.theme.setWhiteGray(this.whiteGray.getColor());
            this.theme.setForegroundGray(this.foregroundGray.getColor());
            this.theme.setForegroundLightStroke(this.foregroundLightStroke.getColor());
            this.theme.setForegroundColor(this.foregroundColor.getColor());
            this.theme.setForegroundStroke(this.foregroundStroke.getColor());
            this.theme.setForegroundDark(this.foregroundDark.getColor());
            this.theme.setWhite(this.white.getColor());
            this.theme.setBackgroundColor(this.backgroundColor.getColor());
        } else {
            this.theme.setColor(this.color.getColor());
            this.theme.setSecondColor(this.secondColor.getColor());
        }
    }

    @Override
    public float getHeight() {
        return (float)(
            22.0 + (this.hasSettings() ? this.settings.stream().mapToDouble(MenuSetting::getHeight).sum() + 8.0 + (double)(8 * this.settings.size()) : 0.0)
        );
    }

    private boolean hasSettings() {
        return !this.settings.isEmpty();
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.bounds != null && this.bounds.contains(mouseX, mouseY)) {
            FeClientBridge.getInstance().getThemeManager().switchTheme(this.theme);
        }

        for (MenuSetting setting : this.settings) {
            setting.onMouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
    }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, MouseButton button, double deltaX, double deltaY) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        boolean result = false;

        for (MenuSetting setting : this.settings) {
            if (setting.charTyped(chr, modifiers)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public Category getCategory() {
        return Category.THEMES;
    }

    @Override
    public String getName() {
        return this.theme.getName();
    }
}
