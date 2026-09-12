package dev.fede.zenithgui.client.screens.menu.settings.impl;

// [lombok removed]
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.zenithgui.adapter.KeySetting;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.Keyboard;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class MenuKeySetting extends MenuSetting {
    private final KeySetting setting;
    private Rect bounds;
    private boolean binding = false;

    public MenuKeySetting(KeySetting setting) {
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
        float iconSize = 6.0F;
        float iconY = textY - 1.0F;
        Font iconFont = Fonts.ICONS.getFont(6.0F);
        ctx.drawText(Fonts.ICONS.getFont(5.5F), "L", settingX + 1.2F, iconY + 1.2F, theme.getGray().mix(theme.getColor(), animEnable).mulAlpha(alpha));
        String keyText = this.binding ? "..." : "n/a";
        int keyCode = this.setting.getKeyCode();
        if (keyCode != -1 && keyCode != 0 && !this.binding) {
            try {
                String name = Keyboard.getKeyName(keyCode);
                if (name != null && !name.isBlank()) {
                    keyText = name.toLowerCase();
                    if (keyText.length() > 6) {
                        keyText = keyText.substring(0, 6) + "..";
                    }
                }
            } catch (Exception var31) {
            }
        }

        Font font = Fonts.MEDIUM.getFont(7.0F);
        float toggleWitdht = 4.0F + font.width(keyText) + 4.0F;
        float toggleHeight = 8.0F;
        float toggleX = x + moduleWidth - toggleWitdht - 8.0F;
        ColorRGBA colorToggle = theme.getForegroundLight().mix(theme.getColor(), animEnable).mulAlpha(alpha);
        ColorRGBA borderColor = theme.getForegroundLightStroke().mix(theme.getForegroundLightStroke().mulAlpha(0.0F), animEnable).mulAlpha(alpha);
        ColorRGBA golochakaColor = theme.getGrayLight().mix(theme.getWhite(), animEnable);
        ctx.drawRoundedRect(toggleX, settingY, toggleWitdht, toggleHeight, BorderRadius.all(2.0F), colorToggle);
        ctx.drawRoundedBorder(toggleX, settingY, toggleWitdht, toggleHeight, -0.1F, BorderRadius.all(2.0F), borderColor);
        ctx.drawText(font, keyText, toggleX + 4.0F, settingY + 1.0F, textColor);
        this.bounds = new Rect(toggleX, settingY, toggleWitdht, toggleHeight);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.binding && button.getButtonIndex() >= 2) {
            this.setting.setKeyCode(button.getButtonIndex());
            this.binding = false;
        } else if (this.bounds != null && this.bounds.contains(mouseX, mouseY)) {
            this.binding = true;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.binding) {
            return false;
        } else {
            if (keyCode == 256 || keyCode == 261 || keyCode == 259) {
                keyCode = -1;
            }

            this.setting.setKeyCode(keyCode);
            this.binding = false;
            return true;
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

        public KeySetting getSetting() {
        return this.setting;
    }
}
