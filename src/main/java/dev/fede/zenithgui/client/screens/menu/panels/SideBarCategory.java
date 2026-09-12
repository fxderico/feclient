package dev.fede.zenithgui.client.screens.menu.panels;

// [lombok removed]
import net.minecraft.util.math.MathHelper;
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.module.Category;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class SideBarCategory {
    private final Category category;
    private final Animation animationSwitch;

    public SideBarCategory(Category category) {
        this.category = category;
        this.animationSwitch = new Animation(200L, category == Category.COMBAT ? 1.0F : 0.0F, Easing.LINEAR);
    }

    public void render(
        UIContext ctx,
        float x,
        float y,
        float width,
        float height,
        float sidebarProgress,
        boolean selected,
        ColorRGBA textColor,
        ColorRGBA textColorDisable,
        ColorRGBA iconColorDisable,
        ColorRGBA primary
    ) {
        this.animationSwitch.animateTo(selected ? 1.0F : 0.0F);
        this.animationSwitch.update();
        ColorRGBA mixColor = iconColorDisable.mix(primary, this.animationSwitch.getValue());
        ColorRGBA mixColorText = textColorDisable.mix(textColor, this.animationSwitch.getValue());
        Font font = Fonts.ICONS.getFont(7.0F);
        float offestY = (height - font.height()) / 2.0F;
        float scale = MathHelper.lerp(sidebarProgress, 1.0F, 0.8F);
        float iconWidth = font.width(this.category.getIcon());
        // pushMatrix/translate/scale/popMatrix removed: transforms are no-ops (all drawing stubbed in 1.21.1)
        ctx.drawText(Fonts.ICONS.getFont(7.0F), this.category.getIcon(), x + 8.0F, y + offestY, mixColor);
        Font categoryFont = Fonts.MEDIUM.getFont(7.0F);
        ctx.drawText(categoryFont, this.category.getName(), x + 8.0F + iconWidth * scale + 6.0F, y + (height - font.height()) / 2.0F, mixColorText);
    }

        public Category getCategory() {
        return this.category;
    }
}
