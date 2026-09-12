package dev.fede.zenithgui.client.screens.menu.settings.api;

import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public abstract class MenuSetting {
    protected float height;

    public abstract void render(
        UIContext var1,
        float var2,
        float var3,
        float var4,
        float var5,
        float var6,
        float var7,
        float var8,
        ColorRGBA var9,
        ColorRGBA var10,
        ColorRGBA var11,
        Theme var12
    );

    public abstract void onMouseClicked(double var1, double var3, MouseButton var5);

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract boolean isVisible();

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
    }

    public boolean charTyped(char chr, int modifiers) {
        return false;
    }
}
