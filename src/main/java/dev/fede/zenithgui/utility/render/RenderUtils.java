package dev.fede.zenithgui.utility.render;

import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;

/**
 * Render utility stubs — the original used BufferBuilder/Tessellator APIs that changed
 * significantly between MC 1.21.1 and the version Zenith UI was written for.
 * Methods are stubbed so the project compiles; actual 3D rendering is unused by the GUI.
 */
public final class RenderUtils implements IMinecraft {

    public static Vec3d getCameraPos() {
        return Vec3d.ZERO;
    }

    public static double deltaTime() {
        return mc.getCurrentFps() > 0 ? 1.0 / (double)mc.getCurrentFps() : 1.0;
    }

    public static float fast(float end, float start, float multiple) {
        return (1.0F - MathHelper.clamp((float)(deltaTime() * (double)multiple), 0.0F, 1.0F)) * end
            + MathHelper.clamp((float)(deltaTime() * (double)multiple), 0.0F, 1.0F) * start;
    }

    public static Vec3d getPlayerLookVec(PlayerEntity player) {
        return Vec3d.ZERO;
    }

    public static void renderRoundedQuad(
        MatrixStack matrices, Color c, double x, double y, double x2, double y2,
        double corner1, double corner2, double corner3, double corner4, double samples
    ) {}

    public static void renderRoundedQuad(MatrixStack matrices, Color c, double x, double y, double x1, double y1, double rad, double samples) {}

    public static void renderCircle(MatrixStack matrices, Color c, double originX, double originY, double rad, int segments) {}

    public static void renderShaderRect(
        MatrixStack matrixStack, Color color, Color color2, Color color3, Color color4,
        float f, float f2, float f3, float f4, float f5, float f6
    ) {}

    public static void renderRoundedOutline(
        DrawContext poses, Color c,
        double fromX, double fromY, double toX, double toY,
        double rad1, double rad2, double rad3, double rad4,
        double width, double samples
    ) {}

    public static MatrixStack matrixFrom(double x, double y, double z) {
        return new MatrixStack();
    }

    public static void renderQuad(MatrixStack matrices, float x, float y, float width, float height, int color) {}

    public static void renderFilledBox(MatrixStack matrices, float f, float f2, float f3, float f4, float f5, float f6, Color color) {}

    public static void renderLine(MatrixStack matrices, Color color, Vec3d start, Vec3d end) {}

    public static void setScissorRegion(int x, int y, int width, int height) {}

    public static void renderRoundedQuadInternal(
        org.joml.Matrix4f matrix, float cr, float cg, float cb, float ca,
        double fromX, double fromY, double toX, double toY,
        double corner1, double corner2, double corner3, double corner4, double samples
    ) {}

    private RenderUtils() {
        throw new UnsupportedOperationException("utility class");
    }
}
