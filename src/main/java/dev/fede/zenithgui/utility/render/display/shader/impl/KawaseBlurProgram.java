package dev.fede.zenithgui.utility.render.display.shader.impl;

import net.minecraft.util.Identifier;
import dev.fede.zenithgui.utility.interfaces.IWindow;
import dev.fede.zenithgui.utility.render.display.shader.GlProgram;

/**
 * Stub — Kawase blur not supported in MC 1.21.1 port.
 */
public class KawaseBlurProgram extends GlProgram implements IWindow {
    public KawaseBlurProgram(Identifier identifier) {
        super(identifier, null);
    }

    public void updateUniforms(float offset) {}

    @Override
    protected void setup() {}
}
