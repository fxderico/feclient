package dev.fede.zenithgui.utility.render.display.base;

import java.util.Objects;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;
import dev.fede.zenithgui.utility.render.display.shader.DrawUtil;

/**
 * Composition-based DrawContext wrapper for MC 1.21.1.
 * Does NOT extend DrawContext (its constructors changed in 1.21.1).
 * Delegates standard MC drawing to the wrapped DrawContext and adds
 * Zenith-style helpers (all rendering is stub / no-op in 1.21.1).
 */
public class CustomDrawContext implements IMinecraft {

    /** The underlying MC DrawContext — delegated to for all standard operations. */
    protected final DrawContext ctx;

    public CustomDrawContext(DrawContext ctx) {
        this.ctx = ctx;
    }

    public static CustomDrawContext of(DrawContext originalContext) {
        return new CustomDrawContext(originalContext);
    }

    // ── Matrix / transform (no-ops: transforms are irrelevant with stubbed rendering) ──

    /** Returns the raw matrices object from the delegate context. */
    public Object getMatrices() {
        return this.ctx.getMatrices();
    }

    /** No-op: matrix transforms are unused when all drawing is stubbed. */
    public void pushMatrix() {}

    /** No-op: matrix transforms are unused when all drawing is stubbed. */
    public void popMatrix() {}

    // ── DrawContext delegation ────────────────────────────────────────────────

    public void enableScissor(int x1, int y1, int x2, int y2) {
        this.ctx.enableScissor(x1, y1, x2, y2);
    }

    public void disableScissor() {
        this.ctx.disableScissor();
    }

    public void drawText(TextRenderer renderer, String text, int x, int y, int color, boolean shadow) {
        this.ctx.drawText(renderer, text, x, y, color, shadow);
    }

    public void drawText(TextRenderer renderer, Text text, int x, int y, int color, boolean shadow) {
        this.ctx.drawText(renderer, text, x, y, color, shadow);
    }

    public void drawText(TextRenderer renderer, String text, int x, int y, int color) {
        this.ctx.drawText(renderer, text, x, y, color, false);
    }

    public void drawText(TextRenderer renderer, Text text, int x, int y, int color) {
        this.ctx.drawText(renderer, text, x, y, color, false);
    }

    public void drawItem(ItemStack stack, int x, int y) {
        this.ctx.drawItem(stack, x, y);
    }

    public void drawItemTooltip(TextRenderer renderer, ItemStack stack, int x, int y) {
        this.ctx.drawItemTooltip(renderer, stack, x, y);
    }

    // ── Zenith custom drawing helpers (no-ops via DrawUtil stubs) ─────────────

    /** No-op: MsdfRenderer is a stub in MC 1.21.1. */
    public void drawText(Font font, String text, float x, float y, ColorRGBA color) {}

    /** No-op: MsdfRenderer is a stub in MC 1.21.1. */
    public void drawText(Font font, String text, float x, float y, Gradient color) {}

    /** No-op: MsdfRenderer is a stub in MC 1.21.1. */
    public void drawText(Font font, Text text, float x, float y) {}

    public void drawSquircle(float x, float y, float width, float height, float squirt, BorderRadius borderRadius, ColorRGBA color) {
        DrawUtil.drawSquircle(this.ctx.getMatrices(), x, y, width, height, squirt, borderRadius, color);
    }

    public void drawRoundedRect(float x, float y, float width, float height, BorderRadius borderRadius, ColorRGBA color) {
        DrawUtil.drawRoundedRect(this.ctx.getMatrices(), x, y, width, height, borderRadius, color);
    }

    public void drawRoundedRect(float x, float y, float width, float height, BorderRadius borderRadius, Gradient gradient) {
        DrawUtil.drawRoundedRect(this.ctx.getMatrices(), x, y, width, height, borderRadius, gradient);
    }

    public void drawRect(float x, float y, float width, float height, ColorRGBA color) {
        DrawUtil.drawRect(this.ctx.getMatrices(), x, y, width, height, color);
    }

    public void drawTextWithBackground(
        TextRenderer textRenderer, Text text, int x, int y, int width,
        BorderRadius borderRadius, ColorRGBA textColor, ColorRGBA backgroundColor
    ) {
        int x1 = x - 3;
        int y1 = y - 2;
        int w  = width + 6;
        Objects.requireNonNull(textRenderer);
        this.drawRoundedRect((float)x1, (float)y1, (float)w, 13.0F, borderRadius, backgroundColor);
        this.ctx.drawText(textRenderer, text, x, y, textColor.getRGB(), true);
    }

    public void drawSprite(CustomSprite sprite, float x, float y, float width, float height, ColorRGBA textureColor) {
        DrawUtil.drawSprite(this.ctx.getMatrices(), sprite, x, y, width, height, textureColor);
    }

    public void drawRoundedCorner(
        float x, float y, float width, float height,
        float borderThikenes, float widthCorner, ColorRGBA color, BorderRadius radius
    ) {
        width  = (float)Math.round(width);
        height = (float)Math.round(height);
        this.enableScissor((int)Math.ceil(x - 10.0F), (int)(y - 10.0F), (int)(x + widthCorner), (int)(y + widthCorner));
        this.drawRoundedBorder(x, y, width, height, borderThikenes, radius, color);
        this.disableScissor();
        this.enableScissor((int)(x + width - widthCorner), (int)(y - 10.0F), (int)(x + width + 10.0F), (int)(y + widthCorner));
        this.drawRoundedBorder(x, y, width, height, borderThikenes, radius, color);
        this.disableScissor();
        this.enableScissor((int)(x - 10.0F), (int)(y + height - widthCorner), (int)(x + widthCorner), (int)(y + height + 10.0F));
        this.drawRoundedBorder(x, y, width, height, borderThikenes, radius, color);
        this.disableScissor();
        this.enableScissor((int)(x + width - widthCorner), (int)(y + height - widthCorner), (int)(x + width + 10.0F), (int)(y + height + 10.0F));
        this.drawRoundedBorder(x, y, width, height, borderThikenes, radius, color);
        this.disableScissor();
    }

    public void drawRoundedBorder(
        float x, float y, float width, float height,
        float borderThickness, BorderRadius borderRadius, ColorRGBA borderColor
    ) {
        DrawUtil.drawRoundedBorder(this.ctx.getMatrices(), x, y, width, height, borderThickness, borderRadius, borderColor);
    }

    public void drawTexture(Identifier identifier, float x, float y, float width, float height, ColorRGBA textureColor) {
        DrawUtil.drawTexture(this.ctx.getMatrices(), identifier, x, y, width, height, textureColor);
    }
}
