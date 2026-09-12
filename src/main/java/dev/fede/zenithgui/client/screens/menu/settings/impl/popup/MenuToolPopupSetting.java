package dev.fede.zenithgui.client.screens.menu.settings.impl.popup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
// [unsupported setting type removed]
import dev.fede.zenithgui.adapter.ToolSelectSetting;
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

public class MenuToolPopupSetting extends MenuPopupSetting {
    private final TextBox searchBox;
    private final ToolSelectSetting setting;
    private final ScrollHandler scrollHandler = new ScrollHandler();
    private boolean rebornSort = false;
    private Map<Item, Rect> toolBounds = new HashMap<>();

    public MenuToolPopupSetting(ToolSelectSetting setting, ChangeRect bounds) {
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
        Font toolFont = Fonts.MEDIUM.getFont(7.0F);
        Font iconFont = Fonts.ICONS.getFont(7.0F);
        ctx.drawText(toolFont, this.setting.getName(), x + 8.0F + 11.2F + 3.0F, y + 7.55F, theme.getWhite());
        // pushMatrix/translate/scale/popMatrix removed: no-ops in MC 1.21.1
        float sortSize = 14.0F;
        ctx.drawRoundedRect(x + width - sortSize - 8.0F, y + 3.0F, sortSize, sortSize, BorderRadius.all(2.0F), theme.getForegroundGray().mulAlpha(alphas));
        ctx.drawText(iconFont, "W", x + width - 8.0F - sortSize + (sortSize - iconFont.width("W")) / 2.0F + 1.0F, y + 6.6F, theme.getColor());
        List<Item> sortedList = this.searchBox.isEmpty() && this.rebornSort ? getAllTools().toList() : getAllTools().sorted((o1, o2) -> {
            if (this.searchBox.isEmpty()) {
                boolean containsInSetting1 = this.setting.contains(o1);
                boolean containsInSetting2 = this.setting.contains(o2);
                return Boolean.compare(!containsInSetting1, !containsInSetting2);
            } else {
                String query = this.searchBox.getText().toLowerCase().trim();
                String name1 = o1.getTranslationKey().replaceFirst("^item\\.minecraft\\.", "").replaceAll("_", " ");
                String name2 = o2.getTranslationKey().replaceFirst("^item\\.minecraft\\.", "").replaceAll("_", " ");
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
        this.toolBounds.clear();
        ColorRGBA graySlotColor = theme.getForegroundColor();
        ColorRGBA themeSlotColor = theme.getForegroundLight();
        ctx.enableScissor((int)x, (int)y + 18 + padding, (int)(x + width), (int)(y + height - (float)padding));
        int i = 0;

        for (Item tool : sortedList) {
            i++;
            if (itemY < y) {
                itemY += 20.0F;
            } else {
                boolean selected = this.setting.contains(tool);
                Rect rect = new Rect(itemX, itemY, itemWidth, 20.0F);
                ctx.drawRoundedRect(
                    rect.x(), rect.y(), rect.width(), rect.height(), BorderRadius.ZERO, selected ? bgColor : (i % 2 == 0 ? graySlotColor : themeSlotColor)
                );
                this.toolBounds.put(tool, rect);
                String name = tool.getTranslationKey().replaceFirst("^item\\.minecraft\\.", "").replaceAll("_", " ");
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                ctx.drawItem(tool.getDefaultStack(), (int)(itemX + 8.0F), (int)(itemY + 4.4F));
                ctx.drawText(Fonts.BOLD.getFont(8.0F), ".", itemX + 8.0F + 11.2F + 3.0F, itemY + 5.0F, theme.getWhiteGray());
                ctx.drawText(toolFont, name, itemX + 8.0F + 11.2F + 8.0F, itemY + 7.55F, selected ? textColor : theme.getGrayLight());
                itemY += 20.0F;
                if (itemY > y + height) {
                    break;
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
            for (Entry<Item, Rect> entry : this.toolBounds.entrySet()) {
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

    public static Stream<Item> getAllTools() {
        return Stream.of(
            Items.DIAMOND_PICKAXE,
            Items.NETHERITE_PICKAXE,
            Items.DIAMOND_AXE,
            Items.NETHERITE_AXE,
            Items.DIAMOND_SHOVEL,
            Items.NETHERITE_SHOVEL,
            Items.DIAMOND_SWORD,
            Items.NETHERITE_SWORD,
            Items.DIAMOND_HOE,
            Items.NETHERITE_HOE,
            Items.IRON_PICKAXE,
            Items.IRON_AXE,
            Items.IRON_SHOVEL,
            Items.IRON_SWORD,
            Items.IRON_HOE,
            Items.STONE_PICKAXE,
            Items.STONE_AXE,
            Items.STONE_SHOVEL,
            Items.STONE_SWORD,
            Items.STONE_HOE,
            Items.WOODEN_PICKAXE,
            Items.WOODEN_AXE,
            Items.WOODEN_SHOVEL,
            Items.WOODEN_SWORD,
            Items.WOODEN_HOE,
            Items.GOLDEN_PICKAXE,
            Items.GOLDEN_AXE,
            Items.GOLDEN_SHOVEL,
            Items.GOLDEN_SWORD,
            Items.GOLDEN_HOE,
            Items.MACE,
            Items.TOTEM_OF_UNDYING,
            Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.ENDER_PEARL,
            Items.ENDER_EYE,
            Items.END_CRYSTAL,
            Items.OBSIDIAN,
            Items.RESPAWN_ANCHOR,
            Items.GLOWSTONE,
            Items.SHIELD,
            Items.BOW,
            Items.CROSSBOW,
            Items.TRIDENT,
            Items.AMETHYST_SHARD,
            Items.ECHO_SHARD,
            Items.QUARTZ,
            Items.PRISMARINE_SHARD,
            Items.PRISMARINE_CRYSTALS
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o instanceof MenuToolPopupSetting that ? this.setting == that.setting : false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.setting);
    }
}
