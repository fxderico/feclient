package dev.fede.zenithgui.client.screens.menu.elements.impl;

import java.util.ArrayList;
import java.util.List;
// [lombok removed]
import dev.fede.zenithgui.FeClientBridge;
import dev.fede.FeClient;
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.zenithgui.adapter.Setting;
import dev.fede.zenithgui.adapter.BooleanSetting;
import dev.fede.zenithgui.adapter.ButtonSetting;
import dev.fede.zenithgui.adapter.ColorSetting;
import dev.fede.zenithgui.adapter.KeySetting;
import dev.fede.zenithgui.adapter.ModeSetting;
import dev.fede.zenithgui.adapter.NumberSetting;
import dev.fede.zenithgui.client.screens.menu.elements.api.AbstractMenuElement;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuBooleanSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuButtonSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuColorSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuEntitySetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuItemSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuKeySetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuModeSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuSelectSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuSliderSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.MenuToolSetting;
import dev.fede.zenithgui.utility.animation.ExpandAnimation;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.Keyboard;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

public class MenuModuleElement extends AbstractMenuElement {
    private final Module module;
    private final List<MenuSetting> settings = new ArrayList<>();
    private final Animation animation;
    private final Animation animationPosition;
    private final Animation animationY;
    private final ExpandAnimation expandAnimation = new ExpandAnimation();
    private Rect bounds;
    private Rect boundsBind;
    private boolean binding = false;
    private boolean settingsExpanded = false;
    private int lastColum = -1;
    boolean animated = false;

    public MenuModuleElement(Module module) {
        this.module = module;
        this.animation = new Animation(200L, module.isEnabled() ? 1.0F : 0.0F, Easing.LINEAR);
        this.animationPosition = new Animation(150L, 1.0F, Easing.QUAD_IN_OUT);
        this.animationY = new Animation(150L, 1.0F, Easing.QUAD_IN_OUT);
        this.expandAnimation.startCollapse(0.0F, 0.0F, 0L);

        // Wrap our native settings into Zenith adapter types for the menu renderers
        for (dev.fede.settings.Setting<?> rawSetting : module.getSettings()) {
            if (rawSetting instanceof dev.fede.settings.SliderSetting s) {
                this.settings.add(new MenuSliderSetting(new NumberSetting(s)));
            } else if (rawSetting instanceof dev.fede.settings.ModeSetting s) {
                this.settings.add(new MenuModeSetting(new ModeSetting(s)));
            } else if (rawSetting instanceof dev.fede.settings.BooleanSetting s) {
                this.settings.add(new MenuBooleanSetting(new BooleanSetting(s)));
            } else if (rawSetting instanceof dev.fede.settings.ColorSetting s) {
                this.settings.add(new MenuColorSetting(new ColorSetting(s)));
            }
            // KeybindSetting is handled at module level (key badge in module row)
            // ButtonSetting / SelectSetting stubs not present in our settings — skip
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

        this.animation.animateTo(this.module.isEnabled() ? 1.0F : 0.0F);
        this.animation.update();
        float moduleHeight = 22.0F;
        Theme theme = FeClientBridge.getInstance().getThemeManager().getCurrentTheme();
        ColorRGBA moduleBg = theme.getForegroundColor().mulAlpha(alpha);
        boolean hasSettings = this.hasSettings();
        float settingAreaHeight = this.getHeight();
        ColorRGBA settingBg = theme.getForegroundDark().mulAlpha(alpha);
        this.bounds = new Rect(x, y, moduleWidth, moduleHeight);
        if (hasSettings) {
            ctx.drawRoundedRect(x, y, moduleWidth, settingAreaHeight, BorderRadius.all(8.0F), settingBg);
            ctx.drawRoundedRect(x, y, moduleWidth, moduleHeight, BorderRadius.top(8.0F, 8.0F), moduleBg);
        } else {
            ctx.drawRoundedRect(x, y, moduleWidth, moduleHeight, BorderRadius.all(8.0F), moduleBg);
        }

        ColorRGBA enabledColor = theme.getGray().mix(theme.getColor(), this.animation.getValue()).mulAlpha(alpha);
        ColorRGBA textColor = theme.getGrayLight().mix(theme.getWhite(), this.animation.getValue()).mulAlpha(alpha);
        ctx.drawText(Fonts.ICONS.getFont(5.5F), "B", x + 8.0F, y + 9.0F, enabledColor);
        ctx.drawText(font, this.module.getName(), x + 18.0F, y + 9.0F, textColor);
        float keyBoxWidth = 22.5F;
        float keyBoxX = x + moduleWidth - keyBoxWidth;
        ColorRGBA badgeColor;
        if (this.isBinding()) {
            badgeColor = theme.getSecondColor();
        } else if (this.module.getKeyCode() != -1) {
            badgeColor = theme.getWhiteGray().mix(theme.getColor(), this.animation.getValue()).mulAlpha(alpha);
        } else {
            badgeColor = theme.getForegroundLight().mulAlpha(alpha);
        }

        ctx.drawRoundedRect(
            keyBoxX, y, keyBoxWidth, moduleHeight, hasSettings ? BorderRadius.topRight(8.0F) : new BorderRadius(0.0F, 8.0F, 8.0F, 0.0F), badgeColor
        );
        String keyText = "n/a";
        int keyCode = this.module.getKeyCode();
        if (keyCode != -1 && keyCode != 0) {
            try {
                String name = Keyboard.getKeyName(keyCode);
                if (name != null && !name.isBlank()) {
                    keyText = name.toUpperCase();
                }
            } catch (Exception var36) {
            }
        }

        Font keyFont = Fonts.MEDIUM.getFont(7.0F);
        float keyTextY = y + (moduleHeight - keyFont.height()) / 2.0F;
        float keyPadding = 2.0F;
        float keyContentWidth = keyBoxWidth - keyPadding * 2.0F;
        float keyContentX = keyBoxX + keyPadding;
        ColorRGBA keyColor = (keyCode != -1 ? theme.getGrayLight().mix(theme.getWhite(), this.animation.getValue()) : theme.getGray()).mulAlpha(alpha);
        this.boundsBind = new Rect(keyBoxX, y, keyBoxWidth, moduleHeight);
        ctx.enableScissor((int)keyBoxX + 1, (int)y, (int)(keyBoxX + keyBoxWidth - 2.0F), (int)(y + moduleHeight));
        this.drawScrollingText(ctx, keyFont, keyText, keyContentX, keyTextY, keyContentWidth, keyColor);
        ctx.disableScissor();
        if (this.settingsExpanded && !this.settings.isEmpty() && this.settings.stream().anyMatch(MenuSetting::isVisible)) {
            float padding = 8.0F;
            float startY = y + moduleHeight + padding;
            ColorRGBA descriptionColor = theme.getWhiteGray().mix(theme.getGrayLight(), this.animation.getValue()).mulAlpha(alpha);
            float settingsHeight = this.getSettingsHeight();
            if (settingsHeight > 0.0F) {
                float animationProgress = this.expandAnimation.getCurrentHeight() / settingsHeight;
                if (animationProgress > 0.01F) {
                    for (MenuSetting setting : this.settings) {
                        if (setting.isVisible()) {
                            setting.render(
                                ctx,
                                mouseX,
                                mouseY,
                                x,
                                startY,
                                moduleWidth,
                                alpha * animationProgress,
                                this.animation.getValue(),
                                enabledColor,
                                textColor,
                                descriptionColor,
                                theme
                            );
                            startY += setting.getHeight() + 8.0F;
                        }
                    }
                }
            }
        }

        if (hasSettings) {
            DrawUtil.drawRoundedBorder(
                ctx.getMatrices(), x, y, moduleWidth, settingAreaHeight, -0.1F, BorderRadius.all(8.0F), theme.getForegroundStroke().mulAlpha(alpha)
            );
        } else {
            DrawUtil.drawRoundedBorder(
                ctx.getMatrices(), x, y, moduleWidth, moduleHeight, -0.1F, BorderRadius.all(8.0F), theme.getForegroundStroke().mulAlpha(alpha)
            );
        }
    }

    private void drawScrollingText(UIContext ctx, Font font, String text, float x, float y, float maxWidth, ColorRGBA color) {
        float textW = font.width(text);
        if (textW <= maxWidth) {
            float centeredX = x + (maxWidth - textW) / 2.0F;
            ctx.drawText(font, text, centeredX, y, color);
        } else {
            float scrollMax = textW - maxWidth;
            float pauseMs = 700.0F;
            float slideMs = 1400.0F;
            float total = pauseMs + slideMs + pauseMs + slideMs;
            long now = System.currentTimeMillis();
            float t = (float)(now % (long)total);
            float offset;
            if (t < pauseMs) {
                offset = 0.0F;
            } else if (t < pauseMs + slideMs) {
                float k = (t - pauseMs) / slideMs;
                k = k * k * (3.0F - 2.0F * k);
                offset = k * scrollMax;
            } else if (t < pauseMs + slideMs + pauseMs) {
                offset = scrollMax;
            } else {
                float k = (t - pauseMs - slideMs - pauseMs) / slideMs;
                k = k * k * (3.0F - 2.0F * k);
                offset = scrollMax * (1.0F - k);
            }

            ctx.drawText(font, text, x - offset, y, color);
        }
    }

    @Override
    public float getHeight() {
        if (this.settings.isEmpty() || !this.settings.stream().anyMatch(MenuSetting::isVisible)) {
            return 22.0F;
        } else if (this.settingsExpanded) {
            float settingsHeight = this.getSettingsHeight();
            return 22.0F + settingsHeight + 8.0F;
        } else {
            return 22.0F;
        }
    }

    private float getSettingsHeight() {
        float height = 0.0F;

        for (MenuSetting setting : this.settings) {
            if (setting.isVisible()) {
                height += setting.getHeight() + 8.0F;
            }
        }

        return height;
    }

    public boolean hasSettings() {
        return this.settingsExpanded && !this.settings.isEmpty() && this.settings.stream().anyMatch(MenuSetting::isVisible);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.bounds != null && this.bounds.contains(mouseX, mouseY)) {
            if (button.getButtonIndex() > 2 && this.binding) {
                this.binding = false;
                this.module.setKeyCode(button.getButtonIndex());
            }

            if (button == MouseButton.LEFT) {
                if (this.boundsBind != null && this.boundsBind.contains(mouseX, mouseY)) {
                    this.binding = !this.binding;
                } else {
                    this.module.toggle();
                }
            } else if (button == MouseButton.MIDDLE) {
                this.binding = !this.binding;
            } else if (button == MouseButton.RIGHT && !this.settings.isEmpty() && this.settings.stream().anyMatch(MenuSetting::isVisible)) {
                this.settingsExpanded = !this.settingsExpanded;
                float currentHeight = this.expandAnimation.getCurrentHeight();
                float targetHeight = this.settingsExpanded ? this.getSettingsHeight() : 0.0F;
                if (this.settingsExpanded) {
                    this.expandAnimation.startExpand(currentHeight, targetHeight, 300L);
                } else {
                    this.expandAnimation.startCollapse(currentHeight, targetHeight, 300L);
                }
            }
        }

        for (MenuSetting setting : this.settings) {
            setting.onMouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.binding) {
            boolean result = false;

            for (MenuSetting setting : this.settings) {
                if (setting.keyPressed(keyCode, scanCode, modifiers)) {
                    result = true;
                }
            }

            return result;
        } else {
            if (keyCode != 256 && keyCode != 261 && keyCode != 259) {
                this.module.setKeyCode(keyCode);
            } else {
                this.module.setKeyCode(-1);
            }

            this.binding = false;
            return true;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    @Override
    public Category getCategory() {
        return this.module.getCategory();
    }

    @Override
    public String getName() {
        return this.module.getName();
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        for (MenuSetting setting : this.settings) {
            setting.onMouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, MouseButton button, double deltaX, double deltaY) {
    }

        public Module getModule() {
        return this.module;
    }

        public boolean isBinding() {
        return this.binding;
    }
}
