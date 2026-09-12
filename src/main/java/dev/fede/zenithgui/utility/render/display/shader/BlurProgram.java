package dev.fede.zenithgui.utility.render.display.shader;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import dev.fede.zenithgui.utility.interfaces.IWindow;

/**
 * Stub — blur shader not supported in MC 1.21.1 port.
 */
public class BlurProgram implements IWindow {
    public static final Supplier<CustomRenderTarget> CACHE = Suppliers.memoize(() -> new CustomRenderTarget());
    public static final Supplier<CustomRenderTarget> BUFFER = Suppliers.memoize(() -> new CustomRenderTarget());

    public void initShaders() {}

    public void draw() {}

    public static int getTexture() { return 0; }

    public void setBlurRadius(float blurRadius) {}
}
