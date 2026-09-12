package dev.fede.zenithgui.base.font;

import java.util.List;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import dev.fede.zenithgui.utility.render.display.base.Gradient;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

/**
 * Stub MSDF renderer for MC 1.21.1.
 * The custom MSDF shader used by Zenith requires ShaderProgramKey / BufferRenderer
 * which don't exist until MC 1.21.4.  All renderText() overloads are no-ops here.
 * Text rendering falls back to MC's TextRenderer via DrawContext (see CustomDrawContext).
 */
public final class MsdfRenderer {

    private MsdfRenderer() {}

    // ── renderText overloads ────────────────────────────────────────────────

    public static void renderText(MsdfFont font, String text, float size, int color,
                                  Matrix4f matrix, float x, float y, float z) {}

    public static void renderText(MsdfFont font, String text, float size, int color,
                                  Matrix4f matrix, float x, float y, float z,
                                  boolean enableFadeout, float fadeoutStart, float fadeoutEnd, float maxWidth) {}

    public static void renderText(MsdfFont font, String text, float size, int color,
                                  Matrix4f matrix, float x, float y, float z,
                                  boolean enableFadeout, float fadeoutStart, float fadeoutEnd) {}

    public static void renderText(MsdfFont font, Text text, float size,
                                  Matrix4f matrix, float x, float y, float z) {}

    public static void renderText(MsdfFont font, Text text, float size, Matrix4f matrix,
                                  float x, float y, float z,
                                  boolean enableFadeout, float fadeoutStart, float fadeoutEnd, float maxWidth) {}

    public static void renderText(MsdfFont font, Text text, float size, Matrix4f matrix,
                                  float x, float y, float z,
                                  boolean enableFadeout, float fadeoutStart, float fadeoutEnd) {}

    public static void renderText(MsdfFont font, String text, float size, Gradient color,
                                  Matrix4f matrix, float x, float y, float z) {}

    public static void renderText(MsdfFont font, String text, float size, Gradient color,
                                  Matrix4f matrix, float x, float y, float z,
                                  boolean enableFadeout, float fadeoutStart, float fadeoutEnd, float maxWidth) {}

    public static void renderText(MsdfFont font, String text, float size, Gradient color,
                                  Matrix4f matrix, float x, float y, float z,
                                  boolean enableFadeout, float fadeoutStart, float fadeoutEnd) {}
}
