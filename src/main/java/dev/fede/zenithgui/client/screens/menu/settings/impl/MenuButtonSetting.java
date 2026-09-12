package dev.fede.zenithgui.client.screens.menu.settings.impl;

import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.zenithgui.adapter.ButtonSetting;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class MenuButtonSetting extends MenuSetting {
    private final ButtonSetting button;
    private final Animation animHovered = new Animation(200L, 0.0F, Easing.QUAD_IN_OUT);
    Rect bounds;

    public MenuButtonSetting(ButtonSetting button) {
        this.button = button;
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
        this.bounds = new Rect(x + 8.0F, settingY, moduleWidth - 16.0F, 16.0F);
        ctx.drawRoundedRect(x + 8.0F, settingY, moduleWidth - 16.0F, 16.0F, BorderRadius.all(4.0F), theme.getForegroundColor().mulAlpha(alpha));
        ctx.drawRoundedRect(x + 8.0F, settingY, moduleWidth - 16.0F, 16.0F, BorderRadius.all(4.0F), theme.getForegroundStroke().mulAlpha(alpha));
        ctx.drawText(
            Fonts.MEDIUM.getFont(7.0F),
            this.button.getName(),
            x + (moduleWidth - Fonts.MEDIUM.getWidth(this.button.getName(), 7.0F)) / 2.0F,
            settingY + 5.0F,
            textColor
        );
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.bounds != null && this.bounds.contains(mouseX, mouseY) && button == MouseButton.LEFT) {
            this.button.toggle();
        }
    }

    @Override
    public float getWidth() {
        return 0.0F;
    }

    @Override
    public float getHeight() {
        return 16.0F;
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
