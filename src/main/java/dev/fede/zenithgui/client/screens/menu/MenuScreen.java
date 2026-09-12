package dev.fede.zenithgui.client.screens.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
// [lombok removed]
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import dev.fede.FeClient;
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.module.Category;
import dev.fede.zenithgui.FeClientBridge;
import dev.fede.zenithgui.client.screens.menu.elements.api.AbstractMenuElement;
import dev.fede.zenithgui.client.screens.menu.elements.impl.MenuConfigElement;
import dev.fede.zenithgui.client.screens.menu.elements.impl.MenuModuleElement;
import dev.fede.zenithgui.client.screens.menu.elements.impl.MenuThemeElement;
import dev.fede.zenithgui.client.screens.menu.panels.HeaderPanel;
import dev.fede.zenithgui.client.screens.menu.panels.SidebarPanel;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuPopupSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.game.other.render.CustomScreen;
import dev.fede.zenithgui.utility.math.MathUtil;
import dev.fede.zenithgui.utility.render.display.ScrollHandler;
import dev.fede.zenithgui.utility.render.display.TextBox;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

public class MenuScreen extends CustomScreen {
    private Category selectedCategory = Category.COMBAT;
    private Category realSelectedCategory = Category.COMBAT;
    private float boxX;
    private float boxY;
    private int columns = 1;
    private float boxWidth = 522.0F;
    private float boxHeight = 316.0F;
    private boolean dragging;
    private float dragOffsetX;
    private float dragOffsetY;
    private final Animation sidebarAnimation = new Animation(300L, 0.0F, Easing.CUBIC_IN_OUT);
    private boolean isSidebarExpanded;
    private final Animation animationClose = new Animation(300L, 0.0F, Easing.BAKEK_SIZE);
    private boolean initialized;
    private TextBox searchField;
    private final ScrollHandler scrollHandler = new ScrollHandler();
    private boolean closing = false;
    private SidebarPanel sidebarPanel;
    private HeaderPanel headerPanel;
    private int scaledScissorX = 0;
    private int scaledScissorY = 0;
    private int scaledScissorEndX = 2000;
    private int scaledScissorEndY = 2000;
    private final Animation animationColums;
    private final Animation animationScrollHeight;
    private final Animation animationChangeCategory;
    private boolean draggingScrollbar = false;
    private final float scrollClickOffset = 0.0F;
    private final Set<MenuPopupSetting> popupSettings = new HashSet<>();
    List<AbstractMenuElement> modules = new ArrayList<>();

    public MenuScreen() {
        this.animationColums = new Animation(300L, this.columns == 3 ? 1.0F : 0.0F, Easing.CUBIC_IN_OUT);
        this.animationChangeCategory = new Animation(150L, 1.0F, Easing.CUBIC_IN_OUT);
        this.animationScrollHeight = new Animation(150L, 1.0F, Easing.QUAD_IN_OUT);
    }

    public void initialize() {
        this.modules.addAll(FeClientBridge.getInstance().getModuleManager().getModules().stream().map(MenuModuleElement::new).toList());
        this.modules.add(new MenuThemeElement(Theme.DARK));
        this.modules.add(new MenuThemeElement(Theme.LIGHT));
        this.modules.add(new MenuThemeElement(Theme.NEON));
        this.modules.add(new MenuThemeElement(Theme.OCEAN));
        this.modules.add(new MenuThemeElement(Theme.SUNSET));
        this.modules.add(new MenuThemeElement(Theme.FOREST));
        this.modules.add(new MenuThemeElement(Theme.PURPLE));
        this.modules.add(new MenuThemeElement(Theme.CYBERPUNK));
        this.modules.add(new MenuThemeElement(Theme.RETRO));
        this.modules.add(new MenuThemeElement(Theme.CUSTOM_THEME));
        this.modules.add(new MenuConfigElement());
    }

    protected void init() {
        this.closing = false;
        this.animationColums.setValue(this.columns == 3 ? 1.0F : 0.0F);
        this.boxWidth = (float)MathHelper.lerp(this.animationColums.getValue(), 465, 533);
        this.boxHeight = (float)MathHelper.lerp(this.animationColums.getValue(), 282, 320);
        this.boxX = ((float)this.width - this.boxWidth) / 2.0F;
        this.boxY = ((float)this.height - this.boxHeight) / 2.0F;
        this.animationClose.setValue(0.0F);
        this.animationClose.update(1.0F);
        if (!this.initialized) {
            this.initialize();
            this.searchField = new TextBox(
                new Vector2f(this.boxX + this.boxWidth - 128.0F - 8.0F, this.boxY + 8.0F), Fonts.MEDIUM.getFont(7.0F), "Search", 100.0F
            );
            this.sidebarPanel = new SidebarPanel(this.sidebarAnimation, this.isSidebarExpanded, category -> {
                this.headerPanel.resetAnim(this.realSelectedCategory, category);
                this.realSelectedCategory = category;
                this.scrollHandler.setTargetValue(0.0);
                this.searchField.setSelectAll(true);
                this.searchField.setSelected(true);
                this.searchField.keyPressed(259, 0, 0);
                this.searchField.setSelected(false);
            }, () -> {
                this.isSidebarExpanded = !this.isSidebarExpanded;
                this.sidebarAnimation.animateTo(this.isSidebarExpanded ? 1.0F : 0.0F);
            });
            this.headerPanel = new HeaderPanel(
                this.searchField, () -> this.columns = this.columns % 3 + 1, () -> FeClientBridge.getInstance().getThemeManager().switchTheme()
            );
        }

        this.initialized = true;
    }

    @Override
    public void tick() {
        if (this.closing && this.animationClose.getValue() == 0.0F) {
            this.close();
        }

        super.tick();
    }

    public void removed() {
        this.closing = true;
        super.removed();
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public boolean isFinish() {
        return this.animationClose.getValue() == 0.0F && this.closing;
    }

    public void renderTop(UIContext ctx, float mouseX, float mouseY) {
        if (this.initialized) {
            this.animationColums.update(this.columns == 3 ? 1.0F : 0.0F);
            this.boxWidth = (float)MathHelper.lerp(this.animationColums.getValue(), 465, 533);
            this.boxHeight = (float)MathHelper.lerp(this.animationColums.getValue(), 282, 320);
            float progress = this.animationClose.update(this.closing ? 0.0F : 1.0F);
            progress = Math.min(Math.max(progress, 0.0F), 1.0F);
            float sidebarProgress = this.sidebarAnimation.update();
            float scale = 0.85F + 0.15F * progress;
            Theme theme = FeClientBridge.getInstance().getThemeManager().getCurrentTheme();
            // pushMatrix/translate/scale are no-ops in MC 1.21.1 (Matrix3x2fStack, not MatrixStack)
            ctx.pushMatrix();
            ColorRGBA primary = theme.getColor().mulAlpha(progress);
            ColorRGBA baseBg = theme.getBackgroundColor().mulAlpha(progress * 4.0F);
            ColorRGBA selectedColor = theme.getWhite().mulAlpha(progress);
            ColorRGBA textColor = theme.getWhite().mulAlpha(progress);
            if (FeClientBridge.getInterface().isBlur()) {
                DrawUtil.drawBlur(
                    ctx.getMatrices(),
                    this.boxX,
                    this.boxY,
                    this.boxWidth,
                    this.boxHeight,
                    20.0F * progress * progress,
                    BorderRadius.all(9.0F),
                    ColorRGBA.WHITE.mulAlpha(progress * 2.0F)
                );
            }

            ctx.drawRoundedRect(this.boxX, this.boxY, this.boxWidth, this.boxHeight, BorderRadius.all(9.0F), baseBg);
            float widthScroll = 2.0F;
            this.sidebarPanel.render(ctx, this.boxX, this.boxY, this.boxHeight, progress, theme, this.realSelectedCategory, primary, textColor, selectedColor);
            float sidebarWidth = 30.0F + 58.0F * sidebarProgress;
            float contentStartX = this.boxX + 8.0F + sidebarWidth + 8.0F;
            float sidebarY = this.boxY + 8.0F;
            float contentY = this.boxY + 22.0F + 8.0F + 8.0F;
            this.headerPanel.render(ctx, contentStartX, sidebarY, this.boxX, this.columns, this.boxWidth, progress, theme, this.realSelectedCategory);
            float visibleHeight = this.boxHeight - 46.0F;
            float scrollProgress = this.scrollHandler.getMax() == 0.0 ? 0.0F : (float)(this.scrollHandler.getValue() / this.scrollHandler.getMax());
            float scrollHeight = Math.max(visibleHeight * (visibleHeight / (float)((double)visibleHeight + this.scrollHandler.getMax())), 20.0F);
            scrollHeight = Math.min(visibleHeight, this.animationScrollHeight.update(scrollHeight));
            float denom = Math.max(1.0F, visibleHeight - scrollHeight);
            float scrollY = contentY + denom * scrollProgress;
            scrollY = Math.min(contentY + visibleHeight, scrollY);
            ctx.drawRoundedRect(
                this.boxX + this.boxWidth - 8.0F - widthScroll,
                contentY,
                widthScroll,
                visibleHeight,
                BorderRadius.all(0.5F),
                theme.getForegroundColor().mulAlpha(progress)
            );
            if (scrollY + scrollHeight > visibleHeight + contentY) {
                ctx.drawRoundedRect(
                    this.boxX + this.boxWidth - 8.0F - widthScroll,
                    contentY,
                    widthScroll,
                    visibleHeight,
                    BorderRadius.all(1.0F),
                    theme.getForegroundStroke().mulAlpha(progress)
                );
            } else {
                ctx.drawRoundedRect(
                    this.boxX + this.boxWidth - 8.0F - widthScroll,
                    scrollY,
                    widthScroll,
                    scrollHeight,
                    BorderRadius.all(1.0F),
                    theme.getForegroundStroke().mulAlpha(progress)
                );
            }

            float contentWidth = this.boxX + (float)(this.columns == 3 ? 530 : 461) - contentStartX - 8.0F;
            this.scaledScissorX = (int)contentStartX;
            this.scaledScissorY = (int)((float)((int)this.boxY) + 38.0F);
            this.scaledScissorEndX = (int)(this.boxX + this.boxWidth);
            this.scaledScissorEndY = (int)((float)((int)this.boxY) + this.boxHeight);
            ctx.enableScissor(this.scaledScissorX, this.scaledScissorY, this.scaledScissorEndX, this.scaledScissorEndY);
            this.animationChangeCategory.setEasing(Easing.QUAD_IN_OUT);
            this.renderModules(
                ctx,
                mouseX,
                mouseY,
                progress * this.animationChangeCategory.update(this.selectedCategory == this.realSelectedCategory ? 1.0F : 0.0F),
                (float)((int)contentStartX),
                contentWidth,
                (float)((int)contentY)
            );
            ctx.disableScissor();
            List<MenuPopupSetting> removes = new ArrayList<>();

            for (MenuPopupSetting setting : this.popupSettings) {
                setting.render(ctx, mouseX, mouseY, progress, theme);
                if (setting.getAnimationScale().getValue() == 0.0F) {
                    removes.add(setting);
                }
            }

            this.popupSettings.removeAll(removes);
            if (this.animationChangeCategory.getValue() == 0.0F) {
                this.selectedCategory = this.realSelectedCategory;
            }

            if (this.draggingScrollbar) {
                float scrollbarY = this.boxY + 22.0F + 8.0F + 8.0F;
                float newY = mouseY - scrollbarY - 0.0F;
                float scrollRatio = newY / denom;
                this.scrollHandler.setTargetValue(-((double)scrollRatio * this.scrollHandler.getMax()));
            }

            ctx.popMatrix();
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        if (!this.popupSettings.isEmpty()) {
            for (MenuPopupSetting setting : this.popupSettings) {
                if (setting.getBounds().contains(mouseX, mouseY)) {
                    setting.onMouseClicked(mouseX, mouseY, button);
                    return;
                }

                setting.getAnimationScale().update(0.0F);
            }
        }

        if (!this.isClosing()) {
            if (this.headerPanel.handleMouseClicked(mouseX, mouseY)) {
                if (this.headerPanel.searchBarBounds.contains(mouseX, mouseY)) {
                    this.searchField.setSelected(true);
                }
            } else if (!this.sidebarPanel.handleMouseClicked(mouseX, mouseY)) {
                if (this.searchField.isSelected()) {
                    this.searchField.setSelected(false);
                }

                if (button.getButtonIndex() == 0 && MathUtil.isHovered(mouseX, mouseY, (double)this.boxX, (double)this.boxY, (double)this.boxWidth, 20.0)) {
                    this.dragging = true;
                    this.dragOffsetX = (float)mouseX - this.boxX;
                    this.dragOffsetY = (float)mouseY - this.boxY;
                } else if (this.animationClose.isDone()) {
                    float scrollbarX = this.boxX + this.boxWidth - 8.0F - 2.0F;
                    float scrollbarY = this.boxY + 22.0F + 8.0F + 8.0F;
                    float visibleHeight = this.boxHeight - 38.0F;
                    if (button.getButtonIndex() == 0 && MathUtil.isHovered(mouseX, mouseY, (double)scrollbarX, (double)scrollbarY, 2.0, (double)visibleHeight)) {
                        this.draggingScrollbar = true;
                    } else if (MathUtil.isHoveredByCords(
                        mouseX, mouseY, this.scaledScissorX, this.scaledScissorY, this.scaledScissorEndX, this.scaledScissorEndY
                    )) {
                        this.modules
                            .stream()
                            .filter(
                                m -> this.searchField.isEmpty()
                                        ? m.getCategory() == this.selectedCategory
                                        : m.getName().toLowerCase().contains(this.searchField.getText().toLowerCase())
                            )
                            .forEach(menuModule -> menuModule.onMouseClicked(mouseX, mouseY, button));
                        super.onMouseClicked(mouseX, mouseY, button);
                    }
                }
            }
        }
    }

    public boolean charTyped(char chr, int modifiers) {
        if (this.searchField.isSelected()) {
            return this.searchField.charTyped(chr, modifiers);
        } else {
            for (MenuPopupSetting setting : this.popupSettings) {
                setting.charTyped(chr, modifiers);
            }

            boolean handled = false;

            for (AbstractMenuElement module : this.modules) {
                if (module.charTyped(chr, modifiers)) {
                    handled = true;
                }
            }

            return handled;
        }
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        for (MenuPopupSetting setting : this.popupSettings) {
            setting.onMouseReleased(mouseX, mouseY, button);
        }

        if (button.getButtonIndex() == 0) {
            this.dragging = false;
            this.draggingScrollbar = false;
        }

        for (AbstractMenuElement module : this.modules) {
            module.onMouseReleased(mouseX, mouseY, button);
        }

        super.onMouseReleased(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean returnCheck = false;

        for (MenuPopupSetting setting : this.popupSettings) {
            if (setting.keyPressed(keyCode, scanCode, modifiers)) {
                this.searchField.setSelected(false);
                returnCheck = true;
            }
        }

        if (returnCheck) {
            return true;
        } else if (this.searchField.isSelected()) {
            if (keyCode == 256) {
                this.searchField.setSelected(false);
                return true;
            } else {
                return this.searchField.keyPressed(keyCode, scanCode, modifiers);
            }
        } else {
            boolean result = false;

            for (AbstractMenuElement module : this.modules) {
                if (module.keyPressed(keyCode, scanCode, modifiers)) {
                    result = true;
                }
            }

            if (result) {
                return true;
            } else {
                if (keyCode == 256 && !this.closing) {
                    this.onMouseReleased(0.0, 0.0, MouseButton.LEFT);
                    this.onMouseReleased(0.0, 0.0, MouseButton.RIGHT);
                    this.onMouseReleased(0.0, 0.0, MouseButton.MIDDLE);

                    for (MenuPopupSetting settingx : this.popupSettings) {
                        settingx.getAnimationScale().setTargetValue(0.0F);
                    }

                    this.closing = true;
                }

                return false;
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.popupSettings.isEmpty()) {
            float visibleHeight = this.boxHeight - 38.0F;
            float baseStep = (float)Math.max(20.0, Math.min(60.0, this.scrollHandler.getMax() / (double)visibleHeight * 10.0));
            this.scrollHandler.scroll(verticalAmount * (double)baseStep / 8.0);
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        } else {
            for (MenuPopupSetting setting : this.popupSettings) {
                setting.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }

            return true;
        }
    }

    @Override
    public void onMouseDragged(double mouseX, double mouseY, MouseButton button, double deltaX, double deltaY) {
        if (button.getButtonIndex() == 0 && this.dragging) {
            this.boxX = (float)mouseX - this.dragOffsetX;
            this.boxY = (float)mouseY - this.dragOffsetY;
        } else {
            for (AbstractMenuElement module : this.modules) {
                module.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }

            super.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
    }

    public void close() {
        super.close();
    }

    private void renderModules(UIContext ctx, float mouseX, float mouseY, float alpha, float contentStartX, float contentWidth, float startY) {
        List<AbstractMenuElement> visibleModules = this.modules
            .stream()
            .filter(
                m -> this.searchField.isEmpty()
                        ? m.getCategory() == this.selectedCategory
                        : m.getName().toLowerCase().contains(this.searchField.getText().toLowerCase())
            )
            .sorted(Comparator.comparing(elem -> elem.getName(), String.CASE_INSENSITIVE_ORDER))
            .toList();
        int cols = this.columns;
        float padding = 6.0F;
        float localScrollbarWidth = 6.0F;
        float maxContentWidth = contentWidth - localScrollbarWidth;
        float moduleWidth = (maxContentWidth - padding * (float)(cols - 1)) / (float)cols;
        Font font = Fonts.MEDIUM.getFont(7.0F);
        double[] columnHeights = new double[cols];
        List<MenuScreen.ModulePosition> modulePositions = new ArrayList<>();

        for (AbstractMenuElement module : visibleModules) {
            int col = 0;

            for (int j = 1; j < cols; j++) {
                if (columnHeights[j] < columnHeights[col]) {
                    col = j;
                }
            }

            float x = contentStartX + (float)col * (moduleWidth + padding);
            float y = (float)((double)startY + columnHeights[col] - this.scrollHandler.getValue());
            modulePositions.add(new MenuScreen.ModulePosition(module, x, y, col));
            columnHeights[col] += (double)(module.getHeight() + padding);
        }

        for (MenuScreen.ModulePosition pos : modulePositions) {
            pos.module.render(ctx, mouseX, mouseY, font, pos.x, pos.y, moduleWidth, alpha, pos.col);
        }

        this.scrollHandler.update();
        double maxY = Arrays.stream(columnHeights).max().orElse(0.0);
        float visibleHeight = this.boxHeight - 38.0F;
        this.scrollHandler.setMax(Math.max(0.0, maxY - (double)visibleHeight) + (double)(maxY > (double)visibleHeight ? 4 : 0));
    }

    public void addPopupMenuSetting(MenuPopupSetting setting) {
        this.popupSettings.add(setting);
    }

    public void removePopupMenuSetting(MenuPopupSetting setting) {
        this.popupSettings.remove(setting);
    }

    @Override
    public void render(UIContext context, float mouseX, float mouseY) {
        renderTop(context, mouseX, mouseY);
    }

        public int getColumns() {
        return this.columns;
    }

        public void setColumns(int columns) {
        this.columns = columns;
    }

        public boolean isClosing() {
        return this.closing;
    }

        public void setClosing(boolean closing) {
        this.closing = closing;
    }

    private static class ModulePosition {
        final AbstractMenuElement module;
        final float x;
        final float y;
        final int col;

        ModulePosition(AbstractMenuElement module, float x, float y, int col) {
            this.module = module;
            this.x = x;
            this.y = y;
            this.col = col;
        }
    }
}
