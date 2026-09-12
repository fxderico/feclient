package dev.fede.zenithgui.utility.render.display.base;

import dev.fede.zenithgui.utility.math.MathUtil;

public record Rect(float x, float y, float width, float height) {
    public boolean contains(double mx, double my) {
        return MathUtil.isHovered(mx, my, (double)this.x, (double)this.y, (double)this.width, (double)this.height);
    }
}
