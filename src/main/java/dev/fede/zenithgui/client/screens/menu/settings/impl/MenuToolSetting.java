package dev.fede.zenithgui.client.screens.menu.settings.impl;

// [lombok removed]
import dev.fede.FeClient;
import dev.fede.zenithgui.FeClientBridge;
import dev.fede.zenithgui.adapter.ToolSelectSetting;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
// [unsupported setting type removed]
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuSetting;
import dev.fede.zenithgui.client.screens.menu.settings.impl.popup.MenuToolPopupSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.ChangeRect;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class MenuToolSetting extends MenuSetting {
    private final ToolSelectSetting setting;
    private Rect bounds;
    private ChangeRect boundsColor;

    public MenuToolSetting(ToolSelectSetting setting) {
        this.setting = setting;
        this.boundsColor = new ChangeRect(0.0F, 0.0F, 78.0F, 96.0F);
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
        ctx.drawText(Fonts.ICONS.getFont(6.0F), "T", settingX + 1.5F, iconY + 1.0F, themeColor);
        float toggleSize = 8.0F;
        float toggleX = x + moduleWidth - toggleSize - 8.0F;
        ctx.drawRoundedBorder(toggleX, settingY, toggleSize, toggleSize, 0.2F, BorderRadius.all(2.0F), themeColor);
        this.bounds = new Rect(toggleX, settingY, toggleSize, toggleSize);
        this.boundsColor.setX(toggleX + 20.0F);
        this.boundsColor.setY(settingY + toggleSize - this.boundsColor.getHeight() / 2.0F);
        this.boundsColor.setHeight(224.0F);
        this.boundsColor.setWidth(150.0F);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (this.bounds != null && this.bounds.contains(mouseX, mouseY)) {
            FeClientBridge.getInstance().getMenuScreen().addPopupMenuSetting(new MenuToolPopupSetting(this.setting, this.boundsColor));
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
        return true;
    }

        public ToolSelectSetting getSetting() {
        return this.setting;
    }
}
