package dev.fede.zenithgui.client.screens.menu.panels;

// import by.saskkeee.user.UserInfo; // removed — feClient doesn't have Zenith's user service
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
// [lombok removed]
import dev.fede.FeClient;
import dev.fede.zenithgui.FeClientBridge;
import dev.fede.zenithgui.Zenith;
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.module.Category;
import dev.fede.zenithgui.utility.math.MathUtil;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.GuiUtil;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

public class SidebarPanel implements IMinecraft {
    private final Map<Category, Rect> categoryBounds = new HashMap<>();
    private Rect sidebarToggleButtonBounds;
    private Rect animRect = new Rect(0.0F, 0.0F, 0.0F, 0.0F);
    private Animation animationChange = new Animation(200L, 1.0F, Easing.LINEAR);
    private final Animation sidebarAnimation;
    private final boolean isSidebarExpanded;
    private final Consumer<Category> onCategorySelect;
    private final Runnable onSidebarToggle;
    private final List<SideBarCategory> categories = new ArrayList<>();

    public SidebarPanel(Animation sidebarAnimation, boolean isSidebarExpanded, Consumer<Category> onCategorySelect, Runnable onSidebarToggle) {
        this.sidebarAnimation = sidebarAnimation;
        this.isSidebarExpanded = isSidebarExpanded;
        this.onCategorySelect = onCategorySelect;
        this.onSidebarToggle = onSidebarToggle;
        this.categories.addAll(Arrays.stream(Category.values()).map(SideBarCategory::new).toList());
    }

    public void render(
        UIContext ctx,
        float boxX,
        float boxY,
        float height,
        float progress,
        Theme theme,
        Category selectedCategory,
        ColorRGBA primary,
        ColorRGBA textColor,
        ColorRGBA selectedColor
    ) {
        float sidebarProgress = this.sidebarAnimation.update();
        float collapsedSidebarWidth = 30.0F;
        float expandedSidebarWidth = 88.0F;
        float sidebarWidth = collapsedSidebarWidth + (expandedSidebarWidth - collapsedSidebarWidth) * sidebarProgress;
        float sidebarPadding = 8.0F;
        float sidebarX = boxX + sidebarPadding;
        float sidebarY = boxY + sidebarPadding;
        float sidebarHeight = height - sidebarPadding * 2.0F;
        ColorRGBA sideBar = theme.getForegroundColor().mulAlpha(progress);
        this.categoryBounds.clear();
        ctx.drawRoundedRect(sidebarX, sidebarY, sidebarWidth, sidebarHeight, BorderRadius.all(7.0F), sideBar);
        DrawUtil.drawRoundedBorder(
            ctx.getMatrices(), sidebarX, sidebarY, sidebarWidth, sidebarHeight, -0.1F, BorderRadius.all(7.0F), theme.getForegroundStroke().mulAlpha(progress)
        );
        float logoSize = 14.0F;
        float logoX = sidebarX + (collapsedSidebarWidth - logoSize) / 2.0F;
        float logoY = sidebarY + 8.0F;
        ctx.drawText(
            Fonts.ICONS.getFont(11.0F),
            "5",
            logoX + 2.0F,
            logoY + 3.0F,
            FeClientBridge.getInstance().getThemeManager().getColorCycleIcon().toGradient().mulAlpha(progress)
        );
        ctx.pushMatrix();
        ctx.enableScissor((int)sidebarX, (int)sidebarY, (int)(sidebarX + sidebarWidth), (int)(sidebarY + sidebarHeight));
        float textAlpha = Math.min(1.0F, sidebarProgress * 2.0F);
        textColor = textColor.mulAlpha(textAlpha);
        ColorRGBA textColorDisable = theme.getGrayLight().mulAlpha(progress * textAlpha);
        ColorRGBA iconColorDisable = theme.getGray().mulAlpha(progress);
        Font logoFont = Fonts.MEDIUM.getFont(7.0F);
        String clientName = "zenithdlc.net";
        ctx.drawText(logoFont, clientName, logoX + logoSize + 8.0F, logoY + (logoSize - logoFont.height()) / 2.0F + 1.0F, textColor);
        float expandedIconSize = 10.0F;
        float collapsedIconSize = 7.0F;
        float iconSize = 10.0F + -3.0F * sidebarProgress;
        float padding = 10.5F;
        float startY = sidebarY + 35.0F;
        int index = 0;

        for (SideBarCategory sideBarCategory : this.categories) {
            if (selectedCategory == sideBarCategory.getCategory()) {
                float categoryY = startY + (float)index * (iconSize + padding);
                float iconX = sidebarX + (collapsedSidebarWidth - iconSize) / 2.0F;
                this.animRect = new Rect(
                    MathUtil.interpolate((double)this.animRect.x(), (double)(sidebarX + 4.0F), (double)this.animationChange.getValue()),
                    MathUtil.interpolate((double)this.animRect.y(), (double)categoryY, (double)this.animationChange.getValue()),
                    sidebarWidth - 8.0F,
                    iconSize + 11.0F
                );
                sideBarCategory.render(
                    ctx,
                    this.animRect.x(),
                    this.animRect.y(),
                    sidebarWidth - 8.0F,
                    iconSize + 11.0F,
                    sidebarProgress,
                    selectedCategory == sideBarCategory.getCategory(),
                    textColor,
                    textColorDisable,
                    iconColorDisable,
                    primary
                );
                ctx.drawRoundedRect(
                    this.animRect.x(),
                    this.animRect.y(),
                    sidebarWidth - 8.0F,
                    iconSize + 11.0F,
                    BorderRadius.all(4.0F),
                    theme.getForegroundLight().mulAlpha(progress)
                );
                DrawUtil.drawRoundedBorder(
                    ctx.getMatrices(),
                    this.animRect.x(),
                    this.animRect.y(),
                    sidebarWidth - 8.0F,
                    iconSize + 11.0F,
                    -0.1F,
                    BorderRadius.all(4.0F),
                    theme.getForegroundLightStroke().mulAlpha(progress)
                );
                break;
            }

            index++;
        }

        this.animationChange.animateTo(1.0F);
        this.animationChange.update();
        index = 0;

        for (SideBarCategory sideBarCategory : this.categories) {
            float categoryY = startY + (float)index * (iconSize + padding);
            float iconX = sidebarX + (collapsedSidebarWidth - iconSize) / 2.0F;
            sideBarCategory.render(
                ctx,
                sidebarX + 4.0F,
                categoryY,
                sidebarWidth - 8.0F,
                iconSize + 11.0F,
                sidebarProgress,
                selectedCategory == sideBarCategory.getCategory(),
                textColor,
                textColorDisable,
                iconColorDisable,
                primary
            );
            this.categoryBounds.put(sideBarCategory.getCategory(), new Rect(sidebarX + 4.0F, categoryY, sidebarWidth - 8.0F, iconSize + 11.0F));
            index++;
        }

        float avatarSize = 18.0F;
        float avatarX = sidebarX + (collapsedSidebarWidth - avatarSize) / 2.0F;
        float avatarY = sidebarY + sidebarHeight - avatarSize - 8.0F;
        float toggleX = avatarX + 5.0F;
        float toggleY = avatarY - 19.0F;
        float toggleW = 8.0F;
        float toggleH = 8.0F;
        Font iconFont = Fonts.ICONS.getFont(7.0F);
        ctx.drawText(iconFont, "6", toggleX, toggleY, theme.getGray().mulAlpha(progress));
        this.sidebarToggleButtonBounds = new Rect(toggleX, toggleY, toggleW, toggleH);
        boolean hover = GuiUtil.isHovered((double)avatarX, (double)avatarY, (double)avatarSize, (double)avatarSize, ctx);
        DrawUtil.drawRoundedTexture(
            ctx.getMatrices(),
            Zenith.id("icons/avatar.png"),
            avatarX,
            avatarY,
            avatarSize,
            avatarSize,
            BorderRadius.all(4.0F),
            ColorRGBA.WHITE.mulAlpha(progress)
        );
        DrawUtil.drawRoundedBorder(
            ctx.getMatrices(),
            avatarX,
            avatarY,
            avatarSize,
            avatarSize,
            -0.1F,
            BorderRadius.all(3.0F),
            new ColorRGBA(181, 162, 255, hover ? 200 : 190).mulAlpha(progress)
        );
        String playerName = mc.player != null ? mc.player.getName().getString() : "Player";
        Font nameFont = Fonts.MEDIUM.getFont(6.0F);
        ctx.drawText(nameFont, playerName, avatarX + avatarSize + 8.0F, avatarY + (avatarSize - nameFont.height()) / 2.0F, textColor);
        ctx.disableScissor();
        ctx.popMatrix();
    }

    public boolean handleMouseClicked(double mouseX, double mouseY) {
        if (this.sidebarToggleButtonBounds != null && this.sidebarToggleButtonBounds.contains(mouseX, mouseY)) {
            this.onSidebarToggle.run();
            return true;
        } else {
            for (Entry<Category, Rect> entry : this.categoryBounds.entrySet()) {
                if (entry.getValue().contains(mouseX, mouseY)) {
                    this.animationChange.animateTo(0.0F);
                    this.animationChange.setValue(0.0F);
                    this.onCategorySelect.accept(entry.getKey());
                    return true;
                }
            }

            return false;
        }
    }

        public Map<Category, Rect> getCategoryBounds() {
        return this.categoryBounds;
    }

        public Rect getSidebarToggleButtonBounds() {
        return this.sidebarToggleButtonBounds;
    }
}
