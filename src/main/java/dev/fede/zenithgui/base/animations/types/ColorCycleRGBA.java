package dev.fede.zenithgui.base.animations.types;

import java.util.ArrayList;
import java.util.List;
// [lombok removed]
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.utility.render.display.base.Gradient;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class ColorCycleRGBA {
    private List<ColorRGBA> baseColors;
    private final List<ColorRGBA> colors;
    private int index = 0;
    private final Animation animation;
    private static final int TL = 0;
    private static final int BL = 1;
    private static final int TR = 2;
    private static final int BR = 3;

    public ColorCycleRGBA(List<ColorRGBA> initialColors, long durationMs) {
        if (initialColors.size() != 4) {
            throw new IllegalArgumentException("need 4 colors - Gradient.of throw exception");
        } else {
            this.baseColors = new ArrayList<>(initialColors);
            this.colors = new ArrayList<>(initialColors);
            this.animation = new Animation(durationMs, Easing.LINEAR);
        }
    }

    public void update() {
        float t = this.animation.getValue();
        this.animation.setDuration(1000L);
        ColorRGBA color = this.baseColors.get(0).mulAlpha(0.0F);
        if (this.index == 0) {
            this.colors.set(0, this.baseColors.get(0).mix(color, t));
            this.colors.set(1, color.mix(this.baseColors.get(1), t));
        }

        if (this.index == 1) {
            this.colors.set(2, this.baseColors.get(2).mix(color, t));
            this.colors.set(0, color.mix(this.baseColors.get(0), t));
        }

        if (this.index == 2) {
            this.colors.set(3, this.baseColors.get(3).mix(color, t));
            this.colors.set(2, color.mix(this.baseColors.get(2), t));
        }

        if (this.index == 3) {
            this.colors.set(1, this.baseColors.get(1).mix(color, t));
            this.colors.set(3, color.mix(this.baseColors.get(3), t));
        }

        if (this.animation.getValue() == 1.0F) {
            this.animation.reset();
            this.index++;
            if (this.index >= this.colors.size()) {
                this.index = 0;
            }
        }

        this.animation.update(1.0F);
    }

    public Gradient toGradient() {
        return Gradient.of(this.colors.get(0), this.colors.get(1), this.colors.get(2), this.colors.get(3));
    }

        public List<ColorRGBA> getBaseColors() {
        return this.baseColors;
    }
}
