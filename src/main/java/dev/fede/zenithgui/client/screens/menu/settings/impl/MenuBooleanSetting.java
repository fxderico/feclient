package dev.fede.zenithgui.client.screens.menu.settings.impl;

// [lombok removed]
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.zenithgui.adapter.BooleanSetting;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class MenuBooleanSetting extends MenuSetting {
    private final BooleanSetting setting;
    private final Animation animation = new Animation(300L, Easing.QUARTIC_OUT);
    private Rect bounds;

    public MenuBooleanSetting(BooleanSetting setting) {
        this.setting = setting;
    }

    @Override
    public void render(
        UIContext ctx,
        float mouseX,
        float mouseY,
        float x,
        float settingY,
        float moduleWidth,
        float alpha,
        float animEnable,
        ColorRGBA themeColor,
        ColorRGBA textColor,
        ColorRGBA descriptionColor,
        Theme theme
    ) {
        float settingHeight = 24.0F;
        float settingX = x + 8.0F;
        Font settingFont = Fonts.MEDIUM.getFont(7.0F);
        Font descFont = Fonts.MEDIUM.getFont(6.0F);
        float textY = settingY + (8.0F - settingFont.height()) / 2.0F - 0.5F;
        ctx.drawText(settingFont, this.setting.getName(), x + 8.0F + 10.0F, textY, textColor);
        this.animation.animateTo(this.setting.isEnabled() ? 1.0F : 0.0F);
        float progress = this.animation.update();
        float iconSize = 6.0F;
        float iconY = textY - 1.0F;
        Font iconFont = Fonts.ICONS.getFont(6.0F);
        ctx.drawRoundedRect(settingX, iconY, iconSize, iconSize, BorderRadius.all(1.0F), themeColor);
        ctx.drawText(Fonts.ICONS.getFont(5.5F), "S", settingX + 1.2F, iconY + 0.5F, theme.getForegroundDark().mulAlpha(alpha));
        float toggleSize = 8.0F;
        float toggleX = x + moduleWidth - toggleSize - 8.0F;
        ColorRGBA colorEnable = theme.getWhiteGray().mix(theme.getColor(), animEnable);
        ColorRGBA colorToggle = theme.getForegroundLight().mix(colorEnable, this.animation.getValue()).mulAlpha(alpha);
        ColorRGBA borderColor = theme.getForegroundLightStroke().mix(new ColorRGBA(0, 0, 0, 0), this.animation.getValue()).mulAlpha(alpha);
        ColorRGBA golochakaColor = theme.getGrayLight().mix(theme.getWhite(), animEnable);
        ColorRGBA golochakaFinalColor = new ColorRGBA(0, 0, 0, 0).mix(golochakaColor, this.animation.getValue()).mulAlpha(alpha);
        ctx.drawRoundedRect(toggleX, settingY, toggleSize, toggleSize, BorderRadius.all(2.0F), colorToggle);
        ctx.drawRoundedBorder(toggleX, settingY, toggleSize, toggleSize, -0.1F, BorderRadius.all(2.0F), borderColor);
        ctx.drawText(iconFont, "S", toggleX + 2.0F, settingY + 1.0F, golochakaFinalColor);
        this.bounds = new Rect(toggleX, settingY, toggleSize, toggleSize);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.bounds != null && this.bounds.contains(mouseX, mouseY) && button == MouseButton.LEFT) {
            this.setting.toggle();
        }
    }

    @Override
    public float getWidth() {
        return 0.0F;
    }

    @Override
    public float getHeight() {
        return 8.0F;
    }

    @Override
    public boolean isVisible() {
        return this.setting.getVisible().get();
    }

        public BooleanSetting getSetting() {
        return this.setting;
    }
}
