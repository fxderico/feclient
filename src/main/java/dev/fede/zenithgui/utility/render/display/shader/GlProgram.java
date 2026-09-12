package dev.fede.zenithgui.utility.render.display.shader;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus.Internal;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;

/**
 * Stub wrapper around a named MC shader program.
 * In MC 1.21.1, ShaderProgramKey / RenderPhase do not exist.
 * Custom MSDF / rounded-rectangle shaders are not loaded on 1.21.1.
 */
public class GlProgram implements IMinecraft {
    private static final List<Runnable> REGISTERED_PROGRAMS = new ArrayList<>();

    /** The backing ShaderProgram if successfully loaded, otherwise null. */
    protected ShaderProgram backingProgram;
    /** Identifier of the shader data file. */
    protected final Identifier shaderId;

    public GlProgram(Identifier id, VertexFormat vertexFormat) {
        this.shaderId = id.withPrefixedPath("core/");
        REGISTERED_PROGRAMS.add(() -> {
            try {
                this.setup();
            } catch (Exception ignored) {}
        });
    }

    public GlProgram(Identifier id, VertexFormat vertexFormat, boolean noCorePrefix) {
        this.shaderId = id;
        REGISTERED_PROGRAMS.add(() -> {
            try {
                this.setup();
            } catch (Exception ignored) {}
        });
    }

    /** No-op on 1.21.1: RenderPhase.ShaderProgram doesn't exist. */
    public Object renderPhaseProgram() {
        return null;
    }

    /** Returns null on 1.21.1 (no ShaderProgramKey). */
    public ShaderProgram use() {
        return null;
    }

    protected void setup() {}

    public GlUniform findUniform(String name) {
        if (backingProgram == null) return null;
        try {
            return backingProgram.getUniform(name);
        } catch (Exception e) {
            return null;
        }
    }

    @Internal
    public static void loadAndSetupPrograms() {
        REGISTERED_PROGRAMS.forEach(Runnable::run);
    }
}
