package dev.fede.zenithgui.client.screens.menu.settings.impl.popup;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.item.Items;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
// [unsupported setting type removed]
import dev.fede.zenithgui.adapter.ItemSelectSetting;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuPopupSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.math.MathUtil;
import dev.fede.zenithgui.utility.render.display.ScrollHandler;
import dev.fede.zenithgui.utility.render.display.TextBox;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.ChangeRect;
import dev.fede.zenithgui.utility.render.display.base.Rect;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class MenuItemPopupSetting extends MenuPopupSetting {
    private final TextBox searchBox;
    private final ItemSelectSetting setting;
    private final ScrollHandler scrollHandler = new ScrollHandler();
    private boolean rebornSort = false;
    private Map<Block, Rect> itemBounds = new HashMap<>();

    public MenuItemPopupSetting(ItemSelectSetting setting, ChangeRect bounds) {
        super(bounds);
        this.searchBox = new TextBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(7.0F), "Search...", 78.0F);
        this.animationScale.update(1.0F);
        this.setting = setting;
    }

    @Override
    public void render(UIContext ctx, float mouseX, float mouseY, float alphas, Theme theme) {
        this.animationScale.update();
        alphas = 1.0F;
        float x = this.bounds.getX();
        float y = this.bounds.getY();
        float width = this.bounds.getWidth();
        float height = this.bounds.getHeight() - 20.0F - 4.0F;
        // pushMatrix/translate/scale removed: no-ops in MC 1.21.1
        ctx.drawRoundedRect(
            this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), height, BorderRadius.all(4.0F), theme.getForegroundColor().mulAlpha(alphas)
        );
        ctx.drawRoundedRect(
            this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), 18.0F, BorderRadius.top(4.0F, 4.0F), theme.getForegroundLight().mulAlpha(alphas)
        );
        Font itemFont = Fonts.MEDIUM.getFont(7.0F);
        Font iconFont = Fonts.ICONS.getFont(7.0F);
        ctx.drawText(itemFont, this.setting.getName(), x + 8.0F + 11.2F + 3.0F, y + 7.55F, theme.getWhite());
        // pushMatrix/translate/scale/popMatrix removed: no-ops in MC 1.21.1
        float sortSize = 14.0F;
        ctx.drawRoundedRect(x + width - sortSize - 8.0F, y + 3.0F, sortSize, sortSize, BorderRadius.all(2.0F), theme.getForegroundGray().mulAlpha(alphas));
        ctx.drawText(iconFont, "W", x + width - 8.0F - sortSize + (sortSize - iconFont.width("W")) / 2.0F + 1.0F, y + 6.6F, theme.getColor());
        List<Block> sortedList = this.searchBox.isEmpty() && this.rebornSort ? getAllBlocks().toList() : getAllBlocks().sorted((o1, o2) -> {
            if (this.searchBox.isEmpty()) {
                boolean containsInSetting1 = this.setting.contains(o1);
                boolean containsInSetting2 = this.setting.contains(o2);
                return Boolean.compare(!containsInSetting1, !containsInSetting2);
            } else {
                String query = this.searchBox.getText().toLowerCase().trim();
                String name1 = o1.getTranslationKey().replaceFirst("^block\\.minecraft\\.", "").replaceAll("_", " ");
                String name2 = o2.getTranslationKey().replaceFirst("^block\\.minecraft\\.", "").replaceAll("_", " ");
                boolean matchesSearch1 = name1.toLowerCase().contains(query);
                boolean matchesSearch2 = name2.toLowerCase().contains(query);
                return Boolean.compare(!matchesSearch1, !matchesSearch2);
            }
        }).toList();
        float contentHeight = (float)sortedList.size() * 20.0F;
        this.scrollHandler.setMax((double)Math.max(0.0F, contentHeight - height));
        this.scrollHandler.update();
        int padding = 4;
        float itemY = (float)(padding + 18) + y - (float)this.scrollHandler.getValue();
        float itemX = x;
        float itemWidth = width;
        ColorRGBA textColor = theme.getWhite().mulAlpha(alphas);
        ColorRGBA bgColor = theme.getColor().mulAlpha(alphas);
        this.itemBounds.clear();
        ColorRGBA graySlotColor = theme.getForegroundColor();
        ColorRGBA themeSlotColor = theme.getForegroundLight();
        ctx.enableScissor((int)x, (int)y + 18 + padding, (int)(x + width), (int)(y + height - (float)padding));
        int i = 0;

        for (Block item : sortedList) {
            if (item != Blocks.AIR) {
                i++;
                if (itemY < y) {
                    itemY += 20.0F;
                } else {
                    boolean selected = this.setting.contains(item);
                    Rect rect = new Rect(itemX, itemY, itemWidth, 20.0F);
                    ctx.drawRoundedRect(
                        rect.x(), rect.y(), rect.width(), rect.height(), BorderRadius.ZERO, selected ? bgColor : (i % 2 == 0 ? graySlotColor : themeSlotColor)
                    );
                    this.itemBounds.put(item, rect);
                    String name = item.getTranslationKey().replaceFirst("^block\\.minecraft\\.", "").replaceAll("_", " ");
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    ctx.drawItem(item.asItem().getDefaultStack(), (int)(itemX + 8.0F), (int)(itemY + 4.4F));
                    ctx.drawText(Fonts.BOLD.getFont(8.0F), ".", itemX + 8.0F + 11.2F + 3.0F, itemY + 5.0F, theme.getWhiteGray());
                    ctx.drawText(itemFont, name, itemX + 8.0F + 11.2F + 8.0F, itemY + 7.55F, selected ? textColor : theme.getGrayLight());
                    itemY += 20.0F;
                    if (itemY > y + height) {
                        break;
                    }
                }
            }
        }

        ctx.disableScissor();
        ctx.enableScissor((int)x, (int)(y + height + 4.0F), (int)(x + width), (int)(y + height + 24.0F));
        ctx.drawRoundedRect(x, y + height + 4.0F, width, 20.0F, BorderRadius.all(4.0F), theme.getForegroundColor().mulAlpha(alphas));
        this.searchBox.setWidth(width - 20.0F);
        this.searchBox.render(ctx, x + 8.0F, y + height + 4.0F + 8.0F, theme.getWhite().mulAlpha(alphas), theme.getGray().mulAlpha(alphas));
        this.searchBox.setMaxLength(35);
        ctx.disableScissor();
        ctx.popMatrix();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.searchBox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.searchBox.charTyped(chr, modifiers);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        this.searchBox.onMouseClicked(mouseX, mouseY, button);
        float x = this.bounds.getX();
        float y = this.bounds.getY();
        float width = this.bounds.getWidth();
        float height = this.bounds.getHeight();
        if (mouseY > (double)(y + 18.0F)) {
            for (Entry<Block, Rect> entry : this.itemBounds.entrySet()) {
                if (entry.getValue().contains(mouseX, mouseY)) {
                    if (this.setting.contains(entry.getKey())) {
                        this.setting.remove(entry.getKey());
                    } else {
                        this.setting.add(entry.getKey());
                    }

                    return;
                }
            }
        }

        if (MathUtil.isHovered(mouseX, mouseY, (double)(x + width - 8.0F - 16.0F), (double)(y + 3.0F), 16.0, 16.0)) {
            this.rebornSort = !this.rebornSort;
            this.scrollHandler.setTargetValue(0.0);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.scrollHandler.scroll(verticalAmount);
        return true;
    }

    @Override
    public float getWidth() {
        return 0.0F;
    }

    @Override
    public float getHeight() {
        return 0.0F;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    public static Stream<Block> getAllBlocks() {
        return Stream.of(Blocks.class.getDeclaredFields())
            .filter(field -> Modifier.isStatic(field.getModifiers()))
            .filter(field -> Modifier.isPublic(field.getModifiers()))
            .filter(field -> Block.class.isAssignableFrom(field.getType()))
            .map(field -> {
                try {
                    return (Block)field.get(null);
                } catch (IllegalAccessException var2) {
                    throw new RuntimeException(var2);
                }
            });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o instanceof MenuItemPopupSetting that ? this.setting == that.setting : false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.setting);
    }
}
