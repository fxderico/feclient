package dev.fede.zenithgui.utility.render.display.shader;

import dev.fede.zenithgui.utility.interfaces.IWindow;

/**
 * Stub framebuffer wrapper — not functional in MC 1.21.1 port.
 * The Framebuffer constructor and API changed; this stub lets the rest compile.
 */
public class CustomRenderTarget implements IWindow {
    public CustomRenderTarget() {}

    public CustomRenderTarget(boolean useDepth) {}

    public CustomRenderTarget(int width, int height, boolean useDepth) {}

    public CustomRenderTarget setLinear() { return this; }

    public void setup(boolean clear) {}

    public void setup() {}

    public void stop() {}

    public void beginRead() {}

    public void endRead() {}

    public int getColorAttachment() { return 0; }

    public void beginWrite(boolean setViewport) {}

    public void endWrite() {}
}
