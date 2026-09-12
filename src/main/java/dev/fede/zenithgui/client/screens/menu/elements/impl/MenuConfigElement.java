package dev.fede.zenithgui.client.screens.menu.elements.impl;

import dev.fede.module.Category;
import dev.fede.zenithgui.FeClientBridge;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.zenithgui.client.screens.menu.elements.api.AbstractMenuElement;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

/**
 * Placeholder config element shown in the CONFIGS section of the Zenith menu.
 * Cloud-sync features (Zenith-specific) are not available in feClient.
 */
public class MenuConfigElement extends AbstractMenuElement {

    private Rect bounds;
    private static final float HEIGHT = 54.0F;

    @Override
    public void render(UIContext ctx, float mouseX, float mouseY, Font font, float x, float y, float moduleWidth, float alpha, int colum) {
        Theme theme = FeClientBridge.getInstance().getThemeManager().getCurrentTheme();
        ColorRGBA bg  = theme.getForegroundColor().mulAlpha(alpha);
        ColorRGBA bg2 = theme.getForegroundDark().mulAlpha(alpha);
        this.bounds = new Rect(x, y, moduleWidth, HEIGHT);

        ctx.drawRoundedRect(x, y, moduleWidth, HEIGHT, BorderRadius.all(8.0F), bg2);
        DrawUtil.drawRoundedBorder(ctx.getMatrices(), x, y, moduleWidth, HEIGHT, -0.1F, BorderRadius.all(8.0F), theme.getForegroundStroke().mulAlpha(alpha));

        float headerH = 22.0F;
        ctx.drawRoundedRect(x, y, moduleWidth, headerH, BorderRadius.top(8.0F, 8.0F), bg);
        ctx.drawText(Fonts.ICONS.getFont(5.5F), "W", x + 8.0F, y + 9.0F, theme.getColor().mulAlpha(alpha));
        ctx.drawText(font, "Config", x + 18.0F, y + 9.0F, theme.getWhite().mulAlpha(alpha));

        float textY = y + headerH + 8.0F;
        ctx.drawText(Fonts.REGULAR.getFont(7.0F), "feClient config via keybind or /config", x + 8.0F, textY, theme.getGray().mulAlpha(alpha));
    }

    @Override
    public float getHeight() {
        return HEIGHT;
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) { }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) { }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, MouseButton button, double deltaX, double deltaY) { }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) { return false; }

    @Override
    public boolean charTyped(char chr, int modifiers) { return false; }

    @Override
    public Category getCategory() {
        return Category.CLIENT;
    }

    @Override
    public String getName() {
        return "Config";
    }
}
