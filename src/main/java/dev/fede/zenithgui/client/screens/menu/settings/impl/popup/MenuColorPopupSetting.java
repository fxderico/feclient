package dev.fede.zenithgui.client.screens.menu.settings.impl.popup;

import java.awt.Color;
import java.util.Objects;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import dev.fede.FeClient;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.base.font.Fonts;
import dev.fede.zenithgui.base.theme.Theme;
import dev.fede.zenithgui.adapter.ColorSetting;
import dev.fede.zenithgui.Zenith;
import dev.fede.zenithgui.client.screens.menu.settings.api.MenuPopupSetting;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;
import dev.fede.zenithgui.utility.math.MathUtil;
import dev.fede.zenithgui.utility.render.display.TextBox;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.ChangeRect;
import dev.fede.zenithgui.utility.render.display.base.Gradient;
import dev.fede.zenithgui.utility.render.display.base.UIContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.base.color.ColorUtil;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

public class MenuColorPopupSetting extends MenuPopupSetting {
    private boolean open;
    private float hue;
    private float saturation;
    private float brightness;
    private int alpha;
    private boolean afocused;
    private boolean hfocused;
    private boolean sbfocused;
    private ColorSetting setting;
    private final TextBox colorString;

    public MenuColorPopupSetting(ChangeRect rect, ColorSetting setting) {
        super(rect);
        this.setting = setting;
        this.colorString = new TextBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(7.0F), "color", 78.0F);
        this.colorString.setCharFilter(TextBox.CharFilter.ENGLISH_NUMBERS);
        this.colorString.setMaxLength(6);
        this.updatePos();
        this.animationScale.update(1.0F);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.colorString.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.colorString.charTyped(chr, modifiers);
    }

    @Override
    public void render(UIContext ctx, float mouseX, float mouseY, float alphas, Theme theme) {
        this.animationScale.update();
        alphas = 1.0F;
        // pushMatrix/translate/scale removed: no-ops in MC 1.21.1
        ctx.drawRoundedRect(
            this.bounds.getX(),
            this.bounds.getY(),
            this.bounds.getWidth(),
            this.bounds.getHeight(),
            BorderRadius.all(4.0F),
            theme.getForegroundColor().mulAlpha(alphas)
        );
        ctx.drawRoundedRect(
            this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), 18.0F, BorderRadius.top(4.0F, 4.0F), theme.getForegroundLight().mulAlpha(alphas)
        );
        if (IMinecraft.mc.currentScreen == null) {
            this.afocused = false;
            this.hfocused = false;
            this.sbfocused = false;
        }

        float x = this.bounds.getX();
        float y = this.bounds.getY();
        float width = this.bounds.getWidth();
        float height = this.bounds.getHeight();
        float padding = 5.0F;
        float colorX = padding + x;
        float colorY = padding + y + 18.0F;
        float colorWidth = width - padding * 2.0F;
        float colorHeight = 48.0F;
        Font iconFont = Fonts.ICONS.getFont(6.0F);
        ctx.drawText(iconFont, "V", x + padding, y + (18.0F - iconFont.height()) / 2.0F, theme.getColor().mulAlpha(alphas));
        ctx.drawText(iconFont, "M", x + width - padding - iconFont.width("M"), y + (18.0F - iconFont.height()) / 2.0F, theme.getWhiteGray().mulAlpha(alphas));
        Font font = Fonts.MEDIUM.getFont(7.0F);
        ctx.drawText(font, this.setting.getName(), x + 8.0F + 8.0F, y + (18.0F - font.height()) / 2.0F, theme.getWhite().mulAlpha(alphas));
        this.bounds.setWidth(Math.max(96.0F, font.width(this.setting.getName()) + 30.0F));
        this.bounds.setHeight(18.0F + padding + colorHeight + padding + 6.0F + padding + 6.0F + padding + 18.0F + padding);
        float spos = colorX + colorWidth - (colorWidth - colorWidth * this.saturation);
        float bpos = colorY + (colorHeight - colorHeight * this.brightness);
        float hpos = colorWidth * this.hue;
        float apos = colorWidth * (float)this.alpha / 255.0F;
        ColorRGBA colorA = new ColorRGBA(Color.getHSBColor(this.hue, 0.0F, 1.0F)).mulAlpha(alphas);
        ColorRGBA colorB = new ColorRGBA(Color.getHSBColor(this.hue, 1.0F, 1.0F)).mulAlpha(alphas);
        ColorRGBA colorC = new ColorRGBA(new Color(0, 0, 0, 0));
        ColorRGBA colorD = new ColorRGBA(new Color(0, 0, 0));
        ctx.drawRoundedRect(colorX, colorY, colorWidth, colorHeight, BorderRadius.all(4.0F), Gradient.of(colorA, colorA, colorB, colorB));
        ctx.drawRoundedRect(colorX, colorY, colorWidth, colorHeight, BorderRadius.all(4.0F), Gradient.of(colorC, colorD, colorC, colorD));
        ctx.drawRoundedBorder(colorX, colorY, colorWidth, colorHeight, 0.1F, BorderRadius.all(4.0F), ColorRGBA.WHITE.mulAlpha(alphas));
        ctx.drawRoundedRect(spos - 2.0F, bpos - 2.0F, 6.0F, 6.0F, BorderRadius.all(2.0F), ColorRGBA.WHITE.mulAlpha(alphas));
        BorderRadius round = BorderRadius.all(1.0F);
        DrawUtil.drawRoundedTexture(
            ctx.getMatrices(),
            Zenith.id("icons/sliderhue.png"),
            colorX,
            colorY + colorHeight + padding,
            colorWidth,
            4.0F,
            round,
            ColorRGBA.WHITE.mulAlpha(alphas)
        );
        ctx.drawRoundedRect(colorX + hpos - 2.0F, colorY + colorHeight + padding - 1.0F, 6.0F, 6.0F, BorderRadius.all(2.0F), ColorRGBA.WHITE.mulAlpha(alphas));
        DrawUtil.drawRoundedTexture(
            ctx.getMatrices(),
            Zenith.id("icons/slidertransparent.png"),
            colorX,
            colorY + colorHeight + padding + 6.0F + padding,
            colorWidth,
            4.0F,
            round,
            ColorRGBA.WHITE.mulAlpha(alphas)
        );
        ColorRGBA fullAlpha = this.setting.getColor().withAlpha(255).mulAlpha(alphas);
        ctx.drawRoundedRect(
            colorX,
            colorY + colorHeight + padding + 6.0F + padding,
            colorWidth,
            4.0F,
            round,
            Gradient.of(ColorRGBA.TRANSPARENT, ColorRGBA.TRANSPARENT, fullAlpha, fullAlpha)
        );
        ctx.drawRoundedRect(
            colorX + apos - 2.0F, colorY + colorHeight + 6.0F + padding + padding - 1.0F, 6.0F, 6.0F, BorderRadius.all(2.0F), ColorRGBA.WHITE.mulAlpha(alphas)
        );
        ctx.drawRoundedRect(
            colorX, colorY + colorHeight + padding + 6.0F + padding + 6.0F + padding, colorWidth, 14.0F, round, theme.getForegroundLight().mulAlpha(alphas)
        );
        ctx.pushMatrix();
        ctx.drawText(font, "#", colorX + padding, colorY + colorHeight + padding + 6.0F + padding + 6.0F + padding + 4.0F, theme.getGray());
        this.colorString
            .render(
                ctx,
                colorX + padding + font.width("#") + 1.0F,
                colorY + colorHeight + padding + 6.0F + padding + 6.0F + padding + 4.5F,
                theme.getWhite().mulAlpha(alphas),
                theme.getGray().mulAlpha(alphas)
            );
        this.colorString.setWidth(colorWidth - 20.0F);
        ctx.popMatrix();
        Color value = Color.getHSBColor(this.hue, this.saturation, this.brightness);
        if (this.sbfocused) {
            this.saturation = MathHelper.clamp(mouseX - colorX, 0.0F, colorWidth) / colorWidth;
            this.brightness = (colorHeight - MathHelper.clamp(mouseY - colorY, 0.0F, colorHeight)) / colorHeight;
            this.saturation = MathHelper.clamp(this.saturation, 0.0F, 1.0F);
            this.brightness = MathHelper.clamp(this.brightness, 0.0F, 1.0F);
            value = Color.getHSBColor(this.hue, this.saturation, this.brightness);
            this.setColor(new ColorRGBA(value.getRed(), value.getGreen(), value.getBlue(), this.alpha));
        }

        if (this.hfocused) {
            this.hue = MathHelper.clamp(mouseX - colorX, 0.0F, colorWidth) / colorWidth;
            this.hue = MathHelper.clamp(this.hue, 0.0F, 1.0F);
            value = Color.getHSBColor(this.hue, this.saturation, this.brightness);
            this.setColor(new ColorRGBA(value.getRed(), value.getGreen(), value.getBlue(), this.alpha));
        }

        if (this.afocused) {
            this.alpha = (int)(MathHelper.clamp((mouseX - x) / colorWidth, 0.0F, 1.0F) * 255.0F);
            this.setColor(new ColorRGBA(value.getRed(), value.getGreen(), value.getBlue(), this.alpha));
        }

        if (this.colorString.isSelected()) {
            this.setColor(ColorUtil.hexToRgb(this.colorString.getText(), this.setting.getColor()));
            this.updatePos();
        } else {
            this.colorString.setText(ColorUtil.colorToHex(this.setting.getColor()));
            this.colorString.setCursor(6);
        }

        ctx.popMatrix();
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        this.colorString.onMouseClicked(mouseX, mouseY, button);
        float x = this.bounds.getX();
        float y = this.bounds.getY();
        float width = this.bounds.getWidth();
        float height = this.bounds.getHeight();
        float padding = 5.0F;
        float colorX = padding + x;
        float colorY = padding + y + 18.0F;
        float colorWidth = width - padding * 2.0F;
        float colorHeight = 48.0F;
        if (MathUtil.isHovered(
            mouseX,
            mouseY,
            (double)this.colorString.getPosition().x(),
            (double)this.colorString.getPosition().y(),
            (double)colorWidth,
            14.0
        )) {
            this.colorString.setSelected(true);
        }

        if (MathUtil.isHovered(mouseX, mouseY, (double)colorX, (double)colorY, (double)colorWidth, (double)colorHeight)) {
            if (!this.hfocused && !this.afocused) {
                this.sbfocused = true;
            }
        } else if (MathUtil.isHovered(mouseX, mouseY, (double)colorX, (double)(colorY + colorHeight + padding), (double)colorWidth, 6.0)) {
            if (!this.sbfocused && !this.afocused) {
                this.hfocused = true;
            }
        } else if (MathUtil.isHovered(mouseX, mouseY, (double)colorX, (double)(colorY + colorHeight + padding + 6.0F + padding), (double)width, 6.0)) {
            if (!this.hfocused && !this.sbfocused) {
                this.afocused = true;
            }
        } else {
            Font iconFont = Fonts.ICONS.getFont(6.0F);
            if (MathUtil.isHovered(
                mouseX,
                mouseY,
                (double)(x + width - padding - iconFont.width("M")),
                (double)(y + (18.0F - iconFont.height()) / 2.0F),
                (double)iconFont.width("M"),
                4.0
            )) {
                this.animationScale.update(0.0F);
            }
        }
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

    private void updatePos() {
        float[] hsb = Color.RGBtoHSB(this.setting.getColor().getRed(), this.setting.getColor().getGreen(), this.setting.getColor().getBlue(), null);
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
        this.alpha = this.setting.getColor().getAlpha();
    }

    private void setColor(ColorRGBA color) {
        this.setting.setColor(color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else {
            return o instanceof MenuColorPopupSetting that ? this.setting == that.setting : false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.setting);
    }

    @Override
    public void onMouseReleased(double mouseX, double mouseY, MouseButton button) {
        this.hfocused = false;
        this.sbfocused = false;
        this.afocused = false;
    }
}
