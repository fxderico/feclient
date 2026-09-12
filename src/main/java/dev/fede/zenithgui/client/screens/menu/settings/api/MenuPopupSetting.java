package dev.fede.zenithgui.client.screens.menu.settings.api;

// [lombok removed]
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.zenithgui.utility.render.display.base.ChangeRect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public abstract class MenuPopupSetting extends MenuSetting {
    protected final ChangeRect bounds;
    protected Animation animationScale = new Animation(200L, 0.01F, Easing.QUAD_IN_OUT);

    protected MenuPopupSetting(ChangeRect bounds) {
        this.bounds = bounds;
    }

    public abstract void render(UIContext var1, float var2, float var3, float var4, Theme var5);

    @Override
    public final void render(
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
    }

    @Override
    public abstract boolean charTyped(char var1, int var2);

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

        public ChangeRect getBounds() {
        return this.bounds;
    }

        public Animation getAnimationScale() {
        return this.animationScale;
    }
}
