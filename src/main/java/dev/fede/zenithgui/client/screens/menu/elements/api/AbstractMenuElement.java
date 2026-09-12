package dev.fede.zenithgui.client.screens.menu.elements.api;

import dev.fede.zenithgui.base.font.Font;
import dev.fede.module.Category;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.UIContext;

public abstract class AbstractMenuElement {
    public abstract void render(UIContext var1, float var2, float var3, Font var4, float var5, float var6, float var7, float var8, int var9);

    public abstract float getHeight();

    public abstract void onMouseClicked(double var1, double var3, MouseButton var5);

    public abstract void onMouseReleased(double var1, double var3, MouseButton var5);

    public abstract void onMouseDragged(double var1, double var3, MouseButton var5, double var6, double var8);

    public abstract boolean keyPressed(int var1, int var2, int var3);

    public abstract boolean mouseScrolled(double var1, double var3, double var5, double var7);

    public abstract boolean charTyped(char var1, int var2);

    public abstract Category getCategory();

    public abstract String getName();
}
