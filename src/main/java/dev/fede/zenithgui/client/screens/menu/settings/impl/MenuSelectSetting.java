package dev.fede.zenithgui.client.screens.menu.settings.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.math.RotationAxis;
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
// [unsupported setting type removed]
import dev.fede.zenithgui.adapter.MultiBooleanSetting;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

public class MenuSelectSetting extends MenuSetting {
    private final MultiBooleanSetting setting;
    private final Map<MultiBooleanSetting.Value, Rect> modeSettingOptionBounds = new HashMap<>();
    private Rect bounds;
    private boolean expanded;
    private final Animation expandedAnimation = new Animation(200L, 0.0F, Easing.QUAD_IN_OUT);

    public MenuSelectSetting(MultiBooleanSetting setting) {
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
        Font settingFont = Fonts.MEDIUM.getFont(7.0F);
        Font optionFont = Fonts.MEDIUM.getFont(6.0F);
        Font iconFont = Fonts.ICONS.getFont(6.0F);
        float nameX = x + 18.0F;
        ctx.drawText(settingFont, this.setting.getName(), nameX, settingY + (13.0F - settingFont.height()) / 2.0F - 0.5F, textColor);
        ctx.drawText(iconFont, "E", x + 8.0F, settingY + (13.0F - iconFont.height()) / 2.0F - 1.0F, themeColor);
        float dropdownWidth = moduleWidth / 2.0F;
        float dropdownHeight = 13.0F + this.expandedAnimation.update(this.expanded ? 1.0F : 0.0F) * (float)this.setting.getBooleanSettings().size() * 13.0F;
        float dropdownX = x + moduleWidth - dropdownWidth - 8.0F;
        ctx.drawRoundedRect(dropdownX, settingY, dropdownWidth, dropdownHeight, BorderRadius.all(3.0F), theme.getForegroundColor().mulAlpha(alpha));
        ctx.drawRoundedRect(
            dropdownX,
            settingY,
            dropdownWidth,
            13.0F,
            this.expanded ? BorderRadius.top(3.0F, 3.0F) : BorderRadius.all(3.0F),
            theme.getForegroundLight().mulAlpha(alpha)
        );
        String currentModeText = this.setting.getSelectedValues().isEmpty()
            ? "----"
            : this.setting.getSelectedValues().getFirst().getName()
                + (this.setting.getSelectedValues().size() > 1 ? " +" + (this.setting.getSelectedValues().size() - 1) : "");
        ctx.drawText(optionFont, currentModeText, dropdownX + 6.0F, settingY + (13.0F - optionFont.height()) / 2.0F, textColor);
        float arrowX = dropdownX + dropdownWidth - 8.0F - 4.0F;
        float arrowY = settingY + 5.5F;
        ColorRGBA color = theme.getGray().mix(theme.getGrayLight(), animEnable).mulAlpha(alpha);
        // pushMatrix/translate/multiply/translate/popMatrix removed: no-ops in 1.21.1
        ctx.drawText(iconFont, "Q", (float)((int)arrowX), (float)((int)arrowY), color);
        ctx.enableScissor((int)dropdownX - 1, (int)settingY, (int)(dropdownX + dropdownWidth + 1.0F), (int)(settingY + dropdownHeight));
        this.bounds = new Rect(dropdownX, settingY, dropdownWidth, dropdownHeight);
        if (this.expandedAnimation.getValue() != 0.0F) {
            List<MultiBooleanSetting.Value> modes = this.setting.getBooleanSettings();
            ColorRGBA disableColor = theme.getGray().mix(theme.getGrayLight(), animEnable).mulAlpha(alpha);
            color = theme.getForegroundGray().mix(theme.getColor(), animEnable).mulAlpha(alpha);
            float endX = settingY + 13.0F;

            for (MultiBooleanSetting.Value mode : modes) {
                Rect optionRect = new Rect(dropdownX, endX, dropdownWidth, 13.0F);
                if (endX > settingY + dropdownHeight) {
                    break;
                }

                if (mode.isEnabled()) {
                    ctx.drawRoundedRect(
                        dropdownX + 1.0F,
                        endX,
                        dropdownWidth - 2.0F,
                        13.0F,
                        mode == modes.getLast() ? BorderRadius.bottom(3.0F, 3.0F) : BorderRadius.all(0.0F),
                        color.mulAlpha(this.expandedAnimation.getValue())
                    );
                    ctx.drawText(
                        optionFont,
                        mode.getName(),
                        dropdownX + 6.0F,
                        endX + (13.0F - optionFont.height()) / 2.0F,
                        textColor.mulAlpha(this.expandedAnimation.getValue())
                    );
                } else {
                    ctx.drawText(
                        optionFont,
                        mode.getName(),
                        dropdownX + 6.0F,
                        endX + (13.0F - optionFont.height()) / 2.0F,
                        disableColor.mulAlpha(this.expandedAnimation.getValue())
                    );
                }

                this.modeSettingOptionBounds.put(mode, optionRect);
                endX += 13.0F;
            }
        }

        ctx.disableScissor();
        DrawUtil.drawRoundedBorder(
            ctx.getMatrices(),
            dropdownX,
            settingY,
            dropdownWidth,
            dropdownHeight,
            0.2F,
            BorderRadius.all(3.0F),
            theme.getForegroundLightStroke().mulAlpha(alpha)
        );
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.bounds != null && button == MouseButton.RIGHT && this.bounds.contains(mouseX, mouseY)) {
            this.expanded = !this.expanded;
        }

        if (this.expanded && button == MouseButton.LEFT) {
            for (Entry<MultiBooleanSetting.Value, Rect> entry : this.modeSettingOptionBounds.entrySet()) {
                if (entry.getValue().contains(mouseX, mouseY)) {
                    entry.getKey().toggle();
                }
            }
        }
    }

    @Override
    public float getWidth() {
        return 0.0F;
    }

    @Override
    public float getHeight() {
        this.expandedAnimation.update(this.expanded ? 1.0F : 0.0F);
        return 13.0F + this.expandedAnimation.getValue() * (float)this.setting.getBooleanSettings().size() * 13.0F;
    }

    @Override
    public boolean isVisible() {
        return this.setting.getVisible().get();
    }
}
