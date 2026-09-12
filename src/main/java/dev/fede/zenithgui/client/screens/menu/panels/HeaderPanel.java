package dev.fede.zenithgui.client.screens.menu.panels;

import net.minecraft.util.math.MathHelper;
import dev.fede.FeClient;
import dev.fede.zenithgui.FeClientBridge;
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.zenithgui.utility.render.display.TextBox;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.CustomSprite;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

public class HeaderPanel {
    public Rect themeButtonBounds;
    public Rect searchBarBounds;
    public Rect layoutToggleButtonBounds;
    private final TextBox searchField;
    private final Runnable onLayoutToggle;
    private final Runnable onThemeSwitch;
    private Category lastCategory = Category.COMBAT;
    Animation animation = new Animation(300L, 1.0F, Easing.QUAD_IN_OUT);

    public HeaderPanel(TextBox searchField, Runnable onLayoutToggle, Runnable onThemeSwitch) {
        this.searchField = searchField;
        this.onLayoutToggle = onLayoutToggle;
        this.onThemeSwitch = onThemeSwitch;
    }

    public void render(
        UIContext ctx, float contentStartX, float sidebarY, float boxX, int columns, float boxWidth, float progress, Theme theme, Category selectedCategory
    ) {
        this.animation.update(1.0F);
        ColorRGBA sideBar = theme.getForegroundColor().mulAlpha(progress);
        ColorRGBA textColor = theme.getWhite().mulAlpha(progress);
        float x = this.renderBreadcrumbs(ctx, contentStartX, sidebarY, progress, theme, selectedCategory, textColor);
        x += 8.0F;
        x = this.renderStats(ctx, x, sidebarY, progress, theme, selectedCategory, textColor);
        x += 8.0F;
        x = this.renderThemeButton(ctx, x, sidebarY, progress, theme);
        x += 8.0F;
        this.renderLayoutButton(ctx, x, sidebarY, progress, theme, columns);
        this.renderSearchBar(ctx, boxX, sidebarY, boxWidth, progress, theme);
    }

    private float renderBreadcrumbs(UIContext ctx, float startX, float y, float progress, Theme theme, Category selectedCategory, ColorRGBA textColor) {
        String name = selectedCategory.getName();
        Font font = Fonts.MEDIUM.getFont(7.0F);
        Font icon7 = Fonts.ICONS.getFont(7.0F);
        Font icon6 = Fonts.ICONS.getFont(5.0F);
        float homeIcon = 7.0F;
        float arrowIcon = 6.0F;
        float catIcon = 7.0F;
        float pad = 8.0F;
        float gap = 4.0F;
        float tgap = 2.0F;
        float textW = MathHelper.lerp(this.animation.getValue(), font.width(this.lastCategory.getName()), font.width(name));
        float width = pad * 2.0F + homeIcon + gap + arrowIcon + catIcon + tgap + textW;
        float h = 22.0F;
        ColorRGBA bar = theme.getForegroundColor().mulAlpha(progress);
        ctx.drawRoundedRect(startX, y, width, h, BorderRadius.all(7.0F), bar);
        DrawUtil.drawRoundedBorder(ctx.getMatrices(), startX, y, width, h, -0.1F, BorderRadius.all(7.0F), theme.getForegroundStroke().mulAlpha(progress));
        float cx = startX + pad;
        float vy = y + h / 2.0F;
        ctx.drawText(icon7, "7", cx, vy - icon7.height() / 2.0F - 0.5F, theme.getColor().mulAlpha(progress));
        cx += icon7.width("7") + gap;
        ctx.drawText(icon6, "A", cx + 1.0F, vy - icon6.height() / 2.0F - 0.3F, theme.getForegroundGray().mulAlpha(progress));
        cx += icon6.width("A") + gap;
        ctx.enableScissor((int)startX + 20, (int)y, (int)(startX + width), (int)(y + h));
        float offset = (1.0F - this.animation.getValue()) * font.width(name) * 2.0F;
        float offset2 = this.animation.getValue() * font.width(this.lastCategory.getName());
        ctx.drawText(font, name, cx + offset, vy - font.height() / 2.0F, textColor.mulAlpha(this.animation.getValue()));
        ctx.drawText(font, this.lastCategory.getName(), cx - offset2, vy - font.height() / 2.0F, textColor.mulAlpha(1.0F - this.animation.getValue()));
        ctx.disableScissor();
        return startX + width;
    }

    private float renderStats(UIContext ctx, float startX, float y, float progress, Theme theme, Category cat, ColorRGBA textColor) {
        int enabled = 0;
        int total = 0;

        for (Module m : FeClientBridge.getInstance().getModuleManager().getModules()) {
            if (m.getCategory() == cat) {
                total++;
                if (m.isEnabled()) {
                    enabled++;
                }
            }
        }

        Font font = Fonts.MEDIUM.getFont(7.0F);
        Font iconFont = Fonts.ICONS.getFont(7.0F);
        float w = 8.0F
            + iconFont.width(cat.getIcon())
            + 4.0F
            + font.width(String.valueOf(enabled))
            + 1.0F
            + 8.0F
            + 1.0F
            + iconFont.width(cat.getIcon())
            + 4.0F
            + font.width(String.valueOf(total))
            + 8.0F;
        float h = 22.0F;
        ColorRGBA bar = theme.getForegroundColor().mulAlpha(progress);
        ctx.drawRoundedRect(startX, y, w, h, BorderRadius.all(7.0F), bar);
        DrawUtil.drawRoundedBorder(ctx.getMatrices(), startX, y, w, h, -0.1F, BorderRadius.all(7.0F), theme.getForegroundStroke().mulAlpha(progress));
        float iconSz = 7.0F;
        float cx = startX + 8.0F;
        float ty = y + (h - font.height()) / 2.0F;
        float iy = y + (h - iconFont.height()) / 2.0F - 0.5F;
        ctx.drawText(iconFont, cat.getIcon(), cx + (float)(cat.getIcon().equals("2") ? 1 : 0), iy, theme.getColor().mulAlpha(progress));
        cx += iconFont.width(cat.getIcon()) + 4.0F;
        ctx.drawText(font, String.valueOf(enabled), cx, ty, textColor);
        cx += font.width(String.valueOf(enabled));
        ctx.drawSprite(new CustomSprite("icons/separator.png"), ++cx, iy - 1.0F, 8.0F, 8.0F, ColorRGBA.WHITE.mulAlpha(progress));
        cx += 9.0F;
        ctx.drawText(iconFont, cat.getIcon(), cx, iy, theme.getColor().mulAlpha(progress));
        cx += iconFont.width(cat.getIcon()) + 4.0F;
        ctx.drawText(font, String.valueOf(total), cx, ty, textColor);
        return startX + w;
    }

    private float renderThemeButton(UIContext ctx, float startX, float y, float progress, Theme theme) {
        float size = 22.0F;
        this.themeButtonBounds = new Rect(startX, y, size, size);
        this.drawIconButton(ctx, startX, y, size, theme.getIcon(), progress, theme);
        return startX + size;
    }

    private float renderLayoutButton(UIContext ctx, float startX, float y, float progress, Theme theme, int cols) {
        float size = 22.0F;
        this.layoutToggleButtonBounds = new Rect(startX, y, size, size);
        String icon = cols == 2 ? ":" : (cols == 3 ? ";" : "9");
        this.drawIconButton(ctx, startX, y, size, icon, progress, theme);
        return startX + size;
    }

    private void drawIconButton(UIContext ctx, float x, float y, float s, String icon, float progress, Theme theme) {
        ColorRGBA bar = theme.getForegroundColor().mulAlpha(progress);
        ctx.drawRoundedRect(x, y, s, s, BorderRadius.all(6.0F), bar);
        DrawUtil.drawRoundedBorder(ctx.getMatrices(), x, y, s, s, -0.1F, BorderRadius.all(6.0F), theme.getForegroundStroke().mulAlpha(progress));
        Font iconF = Fonts.ICONS.getFont(7.0F);
        float ix = x + (s - iconF.width(icon)) / 2.0F;
        float iy = y + (s - iconF.height()) / 2.0F;
        ctx.drawText(iconF, icon, ix, iy, theme.getColor().mulAlpha(progress));
    }

    private void renderSearchBar(UIContext ctx, float boxX, float y, float boxWidth, float progress, Theme theme) {
        float w = 128.0F;
        float h = 22.0F;
        float pad = 8.0F;
        float x = boxX + boxWidth - pad - w;
        this.searchBarBounds = new Rect(x, y, w, h);
        ColorRGBA bar = theme.getForegroundColor().mulAlpha(progress);
        ctx.drawRoundedRect(x, y, w, h, BorderRadius.all(6.0F), bar);
        DrawUtil.drawRoundedBorder(ctx.getMatrices(), x, y, w, h, -0.1F, BorderRadius.all(6.0F), theme.getForegroundStroke().mulAlpha(progress));
        Font font = Fonts.MEDIUM.getFont(7.0F);
        String txt = this.searchField.getText();
        boolean empty = txt.isEmpty() && !this.searchField.isSelected();
        float ty = y + (h - font.height()) / 2.0F;
        if (empty) {
            ctx.drawText(font, "Search", x + 8.0F, ty, theme.getWhite().mulAlpha(progress * 0.5F));
        } else {
            ctx.enableScissor((int)x + 8, (int)ty - 10, (int)Math.ceil((double)(x + 8.0F + 128.0F)), (int)Math.ceil((double)ty) + 10);
            this.searchField.render(ctx, x + 8.0F, ty, theme.getWhite().mulAlpha(progress), theme.getWhite().mulAlpha(progress * 0.5F));
            ctx.disableScissor();
        }
    }

    public void resetAnim(Category last, Category next) {
        this.animation.reset(0.0F);
        this.lastCategory = last;
    }

    public boolean handleMouseClicked(double mouseX, double mouseY) {
        if (this.layoutToggleButtonBounds.contains(mouseX, mouseY)) {
            this.onLayoutToggle.run();
            return true;
        } else if (this.themeButtonBounds.contains(mouseX, mouseY)) {
            this.onThemeSwitch.run();
            return true;
        } else {
            return this.searchBarBounds.contains(mouseX, mouseY);
        }
    }
}
