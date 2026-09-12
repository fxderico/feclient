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
import dev.fede.zenithgui.adapter.ModeSetting;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

public class MenuModeSetting extends MenuSetting {
    private final ModeSetting setting;
    private final Map<ModeSetting.Value, Rect> modeSettingOptionBounds = new HashMap<>();
    private Rect bounds;
    private boolean expanded;
    private float maxWidthText;
    private final Animation expandedAnimation = new Animation(200L, 0.0F, Easing.QUAD_IN_OUT);

    public MenuModeSetting(ModeSetting setting) {
        this.setting = setting;
        this.maxWidthText = -1.0F;
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
        if (this.maxWidthText == -1.0F) {
            this.maxWidthText = (float)this.setting
                .getValues()
                .stream()
                .mapToDouble(value -> (double)Fonts.MEDIUM.getFont(6.0F).width(value.getName()))
                .max()
                .orElse(0.0);
        }

        Font settingFont = Fonts.MEDIUM.getFont(7.0F);
        Font optionFont = Fonts.MEDIUM.getFont(6.0F);
        Font iconFont = Fonts.ICONS.getFont(6.0F);
        float nameX = x + 18.0F;
        ctx.drawText(settingFont, this.setting.getName(), nameX, settingY + (13.0F - settingFont.height()) / 2.0F - 0.5F, textColor);
        ctx.drawText(iconFont, "K", x + 9.0F, settingY + (13.0F - iconFont.height()) / 2.0F - 1.0F, themeColor);
        float dropdownWidth = moduleWidth / 2.2F;
        float dropdownHeight = 13.0F + this.expandedAnimation.update(this.expanded ? 1.0F : 0.0F) * (float)this.setting.getValues().size() * 13.0F;
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
        String currentModeText = this.setting.getValue().getName();
        ctx.drawText(optionFont, currentModeText, dropdownX + 6.0F, settingY + (13.0F - optionFont.height()) / 2.0F, textColor);
        float thickness = 2.0F;
        float length1 = 4.0F;
        float length2 = 4.0F;
        float arrowX = dropdownX + dropdownWidth - 8.0F - 4.0F;
        float arrowY = settingY + 5.5F;
        BorderRadius radius = BorderRadius.ZERO;
        ColorRGBA color = theme.getGray().mix(theme.getGrayLight(), animEnable).mulAlpha(alpha);
        float angle1 = -45.0F;
        float angle2 = 45.0F;
        // pushMatrix/translate/multiply/translate/popMatrix removed: no-ops in 1.21.1
        ctx.drawText(iconFont, "Q", (float)((int)arrowX), (float)((int)arrowY), color);
        ctx.enableScissor((int)dropdownX - 1, (int)settingY, (int)(dropdownX + dropdownWidth + 1.0F), (int)(settingY + dropdownHeight + 1.0F));
        this.bounds = new Rect(dropdownX, settingY, dropdownWidth, dropdownHeight);
        if (this.expandedAnimation.getValue() != 0.0F) {
            List<ModeSetting.Value> modes = this.setting.getValues();
            ColorRGBA disableColor = theme.getGray().mix(theme.getGrayLight(), animEnable).mulAlpha(alpha);
            ColorRGBA enabledColor = theme.getForegroundGray().mix(theme.getColor(), animEnable).mulAlpha(alpha);
            arrowX = settingY + 13.0F;

            for (ModeSetting.Value mode : modes) {
                Rect optionRect = new Rect(dropdownX, arrowX, dropdownWidth, 13.0F);
                if (arrowX > settingY + dropdownHeight) {
                    break;
                }

                if (mode == this.setting.getValue()) {
                    ctx.drawRoundedRect(
                        dropdownX + 1.0F,
                        arrowX,
                        dropdownWidth - 2.0F,
                        13.0F,
                        mode == modes.getLast() ? BorderRadius.bottom(3.0F, 3.0F) : BorderRadius.all(0.0F),
                        enabledColor.mulAlpha(this.expandedAnimation.getValue())
                    );
                    ctx.drawText(
                        optionFont,
                        mode.getName(),
                        dropdownX + 6.0F,
                        arrowX + (13.0F - optionFont.height()) / 2.0F,
                        textColor.mulAlpha(this.expandedAnimation.getValue())
                    );
                } else {
                    ctx.drawText(
                        optionFont,
                        mode.getName(),
                        dropdownX + 6.0F,
                        arrowX + (13.0F - optionFont.height()) / 2.0F,
                        disableColor.mulAlpha(this.expandedAnimation.getValue())
                    );
                }

                this.modeSettingOptionBounds.put(mode, optionRect);
                arrowX += 13.0F;
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
            for (Entry<ModeSetting.Value, Rect> entry : this.modeSettingOptionBounds.entrySet()) {
                if (entry.getValue().contains(mouseX, mouseY)) {
                    this.setting.setValue(entry.getKey());
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
        return 13.0F + this.expandedAnimation.getValue() * (float)this.setting.getValues().size() * 13.0F;
    }

    @Override
    public boolean isVisible() {
        return this.setting.getVisible().get();
    }
}
