package dev.fede.zenithgui.client.screens.menu.settings.impl;

import java.util.Locale;
// [lombok removed]
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.zenithgui.adapter.NumberSetting;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class MenuSliderSetting extends MenuSetting {
    private final NumberSetting setting;
    private boolean dragging = false;
    private Rect rect;

    public MenuSliderSetting(NumberSetting setting) {
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
        float padding = 8.0F;
        Font settingFont = Fonts.MEDIUM.getFont(7.0F);
        Font descFont = Fonts.REGULAR.getFont(7.0F);
        Font iconFont = Fonts.ICONS.getFont(7.0F);
        ctx.drawText(iconFont, "D", x + padding, settingY, themeColor);
        float nameX = x + padding + 10.0F;
        ctx.drawText(settingFont, this.setting.getName(), nameX, settingY, textColor);
        float animatedValue = this.setting.getCurrent();
        float sliderWidth = this.setting.getDescription().isEmpty() ? moduleWidth - 20.0F : moduleWidth / 2.8F;
        float valueTextAreaWidth = 35.0F;
        float sliderX = x + moduleWidth - padding - 4.0F - sliderWidth;
        float sliderY = settingY + 12.0F;
        String valueText = String.format(Locale.US, "%.1f", animatedValue);
        float valueTextWidth = settingFont.width(valueText);
        float valueTextX = x + moduleWidth - padding - valueTextWidth;
        ctx.drawText(settingFont, valueText, valueTextX, settingY, themeColor);
        ctx.drawRoundedRect(sliderX, sliderY, sliderWidth, 2.0F, BorderRadius.all(0.2F), theme.getForegroundLight().mulAlpha(alpha));
        float percent = (animatedValue - this.setting.getMin()) / (this.setting.getMax() - this.setting.getMin());
        float filledWidth = sliderWidth * percent;
        ctx.drawRoundedRect(sliderX, sliderY, filledWidth - 2.0F, 2.0F, BorderRadius.ZERO, theme.getGray().mix(theme.getColor(), animEnable).mulAlpha(alpha));
        float handleX = sliderX + filledWidth;
        float handleY = sliderY - 1.0F;
        ColorRGBA circleColor = theme.getGrayLight().mix(theme.getWhite(), animEnable).mulAlpha(alpha);
        ctx.drawRoundedRect(handleX, handleY, 4.0F, 4.0F, BorderRadius.all(2.0F), circleColor);
        this.rect = new Rect(sliderX, sliderY - 2.0F, sliderWidth, 6.0F);
        if (!this.setting.getDescription().isEmpty()) {
            float descY = settingY + 10.0F;
            ctx.drawText(descFont, this.setting.getDescription(), x + padding, descY, descriptionColor);
        }

        this.updateSlider((double)mouseX);
    }

    public void updateSlider(double mouseX) {
        if (this.dragging) {
            Rect sliderRect = this.rect;
            if (sliderRect != null) {
                double relativeX = mouseX - (double)sliderRect.x();
                double percent = Math.max(0.0, Math.min(1.0, relativeX / (double)sliderRect.width()));
                double min = (double)this.setting.getMin();
                double max = (double)this.setting.getMax();
                float increment = this.setting.getIncrement();
                double newValue = min + (max - min) * percent;
                newValue = (double)((float)Math.round((newValue - min) / (double)increment) * increment) + min;
                newValue = Math.max(min, Math.min(max, newValue));
                this.setting.setCurrent((float)newValue);
            }
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (button == MouseButton.LEFT && this.rect != null && this.rect.contains(mouseX, mouseY)) {
            this.dragging = true;
        }
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        if (button == MouseButton.LEFT) {
            this.dragging = false;
        }
    }

    @Override
    public float getWidth() {
        return 0.0F;
    }

    @Override
    public float getHeight() {
        return 14.0F;
    }

    @Override
    public boolean isVisible() {
        return this.setting.getVisible().get();
    }

        public NumberSetting getSetting() {
        return this.setting;
    }
}
