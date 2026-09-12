package dev.fede.zenithgui.utility.render.display;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.HashMap;
import java.util.Stack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;
import dev.fede.zenithgui.utility.render.display.base.Gradient;
import dev.fede.zenithgui.utility.render.display.base.color.ColorUtil;

/**
 * Render utility stub for MC 1.21.1.
 * Original used BufferBuilder/Tessellator/MatrixStack APIs that changed significantly.
 * All rendering methods are no-ops; the Zenith GUI uses DrawUtil/CustomDrawContext instead.
 */
public final class Render2DUtil implements IMinecraft {
    public static HashMap<GlowKey, GlowRect> glowCache = new HashMap<>();
    public static HashMap<Integer, GlowRect> shadowCache1 = new HashMap<>();
    static final Stack<Rectangle> clipStack = new Stack<>();

    public static void endScissor() {
        // no-op in 1.21.1 stub
    }

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static void drawBlurredShadow(MatrixStack matrices, float x, float y, float width, float height, int blurRadius, Color color) {}

    public static void drawGradientBlurredShadow(MatrixStack matrices, float x, float y, float width, float height, int blurRadius, Gradient gradient) {}

    public static void drawQuad(float x, float y, float width, float height, int color) {}

    public static void onRender(DrawContext context) {}

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        amount = Math.min(1.0F, Math.max(0.0F, amount));
        return new Color(
            interpolateInt(color1.getRed(), color2.getRed(), amount),
            interpolateInt(color1.getGreen(), color2.getGreen(), amount),
            interpolateInt(color1.getBlue(), color2.getBlue(), amount),
            interpolateInt(color1.getAlpha(), color2.getAlpha(), amount)
        );
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        return interpolateColorC(color1, color2, amount);
    }

    public static double interpolate(double oldValue, double newValue, double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue) {
        return (float)interpolate(oldValue, newValue, (float)interpolationValue);
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return (int)interpolate(oldValue, newValue, (float)interpolationValue);
    }

    public static boolean isDark(Color color) {
        return isDark((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F);
    }

    public static boolean isDark(float r, float g, float b) {
        return colorDistance(r, g, b, 0.0F, 0.0F, 0.0F) < colorDistance(r, g, b, 1.0F, 1.0F, 1.0F);
    }

    public static float colorDistance(float r1, float g1, float b1, float r2, float g2, float b2) {
        float a = r2 - r1, bv = g2 - g1, c = b2 - b1;
        return (float)Math.sqrt(a * a + bv * bv + c * c);
    }

    public static Color getColor(Color start, Color end, float progress, boolean smooth) {
        if (!smooth) return progress >= 0.95f ? end : start;
        return interpolateColorC(start, end, progress);
    }

    private Render2DUtil() { throw new UnsupportedOperationException("utility class"); }

    // ── Inner types needed for field declarations ──────────────────────────────

    public static record GlowKey(int width, int height, int blurRadius) {}

    public static class GlowRect {
        public final int textureId;
        private int ticksSinceUse = 0;
        public GlowRect(int textureId) { this.textureId = textureId; }
        public void reset() { ticksSinceUse = 0; }
        public boolean tick() { return ++ticksSinceUse > 300; }
    }

    public static record Quad(float x, float y, float width, float height, int color) {}

    public static record Rectangle(float x, float y, float x1, float y1) {
        public boolean contains(double px, double py) {
            return px >= x && px <= x1 && py >= y && py <= y1;
        }
    }
}
