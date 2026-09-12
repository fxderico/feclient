package dev.fede.zenithgui.utility.render.display.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import dev.fede.zenithgui.FeClientBridge;
import dev.fede.zenithgui.utility.interfaces.IWindow;
import dev.fede.zenithgui.utility.render.display.Render2DUtil;
import dev.fede.zenithgui.utility.render.display.base.BorderRadius;
import dev.fede.zenithgui.utility.render.display.base.CustomSprite;
import dev.fede.zenithgui.utility.render.display.base.Gradient;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

/**
 * Stub DrawUtil for MC 1.21.1. All shader rendering is no-op.
 * Parameters use Object instead of MatrixStack to accept Matrix3x2fStack from DrawContext.getMatrices().
 */
public final class DrawUtil implements IWindow {
    public static final float DEFAULT_SMOOTHNESS = 0.8F;
    public static GlProgram rectangleProgram;
    private static GlProgram squircleProgram;
    private static GlProgram roundedTextureProgram;
    private static GlProgram squircleTextureProgram;
    private static GlProgram borderProgram;
    private static GlProgram figmaBorderProgram;
    private static GlProgram loadingProgram;
    private static GlProgram gradientRectangleProgram;
    public static BlurProgram blurProgram;

    public static void initializeShaders() { blurProgram = new BlurProgram(); }
    public static void updateBuffer() {}
    public static void drawLine(Object matrices, Vec2f from, Vec2f to, ColorRGBA color) {}
    public static void drawBezier(Object matrices, Vec2f p0, Vec2f p1, Vec2f p2, Vec2f p3, ColorRGBA color, int resolution) {}
    public static void drawRect(Object matrices, float x, float y, float width, float height, ColorRGBA color) {}
    public static void drawSquircle(Object matrices, float x, float y, float width, float height, float squirt, BorderRadius borderRadius, ColorRGBA color) {}
    public static void drawLoadingRect(Object matrices, float x, float y, float width, float height, float progress, BorderRadius borderRadius, ColorRGBA color) {}
    public static void drawRoundedRect(Object matrices, float x, float y, float width, float height, BorderRadius borderRadius, ColorRGBA color) {}
    public static void drawRoundedRect(Object matrices, float x, float y, float width, float height, BorderRadius borderRadius, ColorRGBA c1, ColorRGBA c2, ColorRGBA c3, ColorRGBA c4) {}
    public static void drawRoundedRect(Object matrices, float x, float y, float width, float height, BorderRadius borderRadius, Gradient gradient) {}
    public static void drawRoundedBorder(Object matrices, float x, float y, float width, float height, float borderThickness, BorderRadius borderRadius, ColorRGBA borderColor) {}
    public static void drawRoundedCorner(Object matrices, float x, float y, float width, float height, float borderThikenes, float delta, ColorRGBA color, BorderRadius radius) {}
    public static void drawRoundedCornerOnly(Object matrices, float x, float y, float width, float height, float borderThickness, BorderRadius borderRadius, ColorRGBA borderColor, float cornerIndex) {}
    public static void drawTexture(Object matrices, Identifier identifier, float x, float y, float width, float height, ColorRGBA textureColor) {}
    public static void drawTexture(Object matrices, Identifier identifier, float x, float y, float width, float height, Gradient textureColor) {}
    public static void drawTexture(Object matrices, Identifier identifier, float x, float y, float width, float height, float u1, float u2, float v1, float v2, ColorRGBA color) {}
    public static void drawSprite(Object matrices, CustomSprite sprite, float x, float y, float width, float height, ColorRGBA color) {}
    public static void drawRoundedTexture(Object matrices, Identifier identifier, float x, float y, float width, float height, BorderRadius borderRadius) {}
    public static void drawRoundedTexture(Object matrices, Identifier identifier, float x, float y, float width, float height, BorderRadius borderRadius, ColorRGBA color) {}
    public static void drawShadow(Object matrices, float x, float y, float width, float height, float softness, BorderRadius borderRadius, ColorRGBA color) {}
    public static void drawBlur(Object matrices, float x, float y, float width, float height, float blurRadius, float squirt, BorderRadius borderRadius, ColorRGBA color) {}
    public static void drawBlur(Object matrices, float x, float y, float width, float height, float blurRadius, BorderRadius borderRadius, ColorRGBA color) {}
    public static void drawBlurHud(Object matrices, float x, float y, float width, float height, float blurRadius, BorderRadius borderRadius, ColorRGBA color) {}
    public static void drawBlurHudBooleanCheck(Object matrices, float x, float y, float width, float height, float blurRadius, BorderRadius borderRadius, ColorRGBA color, boolean blur, boolean glow) {
        if (glow) drawGlow(matrices, x, y, width, height, FeClientBridge.getInterface().getGlowRadius());
    }
    public static void drawGlow(Object matrixStack, float x, float y, float width, float height, int glowRadius) {
        Render2DUtil.drawGradientBlurredShadow(null, x, y, width, height, glowRadius, FeClientBridge.getInstance().getThemeManager().getClientColor());
    }
    public static void drawImage(Object matrices, BufferBuilder builder, double x, double y, double z, double width, double height, ColorRGBA color) {}
    public static void drawImage(Object matrices, Identifier identifier, double x, double y, double z, double width, double height, ColorRGBA color) {}
    public static void drawPlayerHeadWithRoundedShader(Object matrices, Identifier skinTexture, float x, float y, float size, BorderRadius borderRadius, ColorRGBA color) {}
    public static void drawRoundedTextureWithUV(Object matrices, Identifier identifier, float x, float y, float width, float height, BorderRadius borderRadius, ColorRGBA color, float u1, float v1, float u2, float v2) {}
    public static void drawSetup() { /* no-op stub */ }
    public static void drawEnd() { /* no-op stub */ }

    private DrawUtil() { throw new UnsupportedOperationException("utility class"); }
}