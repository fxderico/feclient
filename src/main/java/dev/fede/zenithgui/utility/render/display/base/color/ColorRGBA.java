package dev.fede.zenithgui.utility.render.display.base.color;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Objects;
// [lombok removed]
import net.minecraft.util.math.MathHelper;
import dev.fede.zenithgui.utility.math.MathUtil;

public class ColorRGBA {
    public static final ColorRGBA WHITE = new ColorRGBA(255, 255, 255);
    public static final ColorRGBA BLACK = new ColorRGBA(0, 0, 0);
    public static final ColorRGBA GREEN = new ColorRGBA(0, 255, 0);
    public static final ColorRGBA RED = new ColorRGBA(255, 0, 0);
    public static final ColorRGBA BLUE = new ColorRGBA(0, 0, 255);
    public static final ColorRGBA YELLOW = new ColorRGBA(255, 255, 0);
    public static final ColorRGBA GRAY = new ColorRGBA(88, 87, 93);
    public static final ColorRGBA TRANSPARENT = new ColorRGBA(0, 0, 0, 0);
    private transient float[] hsbValues;
    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;
    private static final ByteBuffer PIXEL_BUFFER = ByteBuffer.allocateDirect(4);

    public ColorRGBA(int color) {
        this(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color), ColorUtil.alpha(color));
    }

    public ColorRGBA(Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public Color toAWTColor() {
        return new Color(this.red, this.green, this.blue, this.alpha);
    }

    public ColorRGBA(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public ColorRGBA(int red, int green, int blue, int alpha) {
        red = MathHelper.clamp(red, 0, 255);
        green = MathHelper.clamp(green, 0, 255);
        blue = MathHelper.clamp(blue, 0, 255);
        alpha = MathHelper.clamp(alpha, 0, 255);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public int getRGB() {
        int a = Math.round((float)this.clamp((float)this.alpha));
        int r = Math.round((float)this.clamp((float)this.red));
        int g = Math.round((float)this.clamp((float)this.green));
        int b = Math.round((float)this.clamp((float)this.blue));
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    private int clamp(float value) {
        return (int)Math.max(0.0F, Math.min(255.0F, value));
    }

    public static ColorRGBA fromHex(String hex) {
        String sanitized = hex.startsWith("#") ? hex.substring(1) : hex;
        if (sanitized.length() != 6 && sanitized.length() != 8) {
            throw new IllegalArgumentException("Hex color must be in the format #RRGGBB or #RRGGBBAA");
        } else {
            int red = Integer.parseInt(sanitized.substring(0, 2), 16);
            int green = Integer.parseInt(sanitized.substring(2, 4), 16);
            int blue = Integer.parseInt(sanitized.substring(4, 6), 16);
            int alpha = sanitized.length() == 8 ? Integer.parseInt(sanitized.substring(6, 8), 16) : 255;
            return new ColorRGBA(red, green, blue, alpha);
        }
    }

    public static ColorRGBA lerp(ColorRGBA startColor, ColorRGBA endColor, float delta) {
        float clampedDelta = Math.max(0.0F, Math.min(1.0F, delta));
        int r = (int)((float)startColor.getRed() + (float)(endColor.getRed() - startColor.getRed()) * clampedDelta);
        int g = (int)((float)startColor.getGreen() + (float)(endColor.getGreen() - startColor.getGreen()) * clampedDelta);
        int b = (int)((float)startColor.getBlue() + (float)(endColor.getBlue() - startColor.getBlue()) * clampedDelta);
        int a = (int)((float)startColor.getAlpha() + (float)(endColor.getAlpha() - startColor.getAlpha()) * clampedDelta);
        return new ColorRGBA(r, g, b, a);
    }

    public static ColorRGBA fromInt(int colorInt) {
        int alpha = colorInt >> 24 & 0xFF;
        int red = colorInt >> 16 & 0xFF;
        int green = colorInt >> 8 & 0xFF;
        int blue = colorInt & 0xFF;
        return new ColorRGBA(red, green, blue, alpha);
    }

    public ColorRGBA withAlpha(float newAlpha) {
        return new ColorRGBA(this.red, this.green, this.blue, this.clamp((float)((int)(255.0F * newAlpha))));
    }

    public ColorRGBA withAlpha(int newAlpha) {
        return new ColorRGBA(this.red, this.green, this.blue, newAlpha);
    }

    public ColorRGBA mulAlpha(float percent) {
        return this.withAlpha((int)((float)this.alpha * percent));
    }

    public ColorRGBA mix(ColorRGBA color2, float amount) {
        amount = Math.min(1.0F, Math.max(0.0F, amount));
        return new ColorRGBA(
            (int)MathUtil.interpolate((double)this.getRed(), (double)color2.getRed(), (double)amount),
            (int)MathUtil.interpolate((double)this.getGreen(), (double)color2.getGreen(), (double)amount),
            (int)MathUtil.interpolate((double)this.getBlue(), (double)color2.getBlue(), (double)amount),
            (int)MathUtil.interpolate((double)this.getAlpha(), (double)color2.getAlpha(), (double)amount)
        );
    }

    public ColorRGBA darker(float amount) {
        amount = MathHelper.clamp(amount, 0.0F, 1.0F);
        return new ColorRGBA(
            (int)((float)this.red * (1.0F - amount)), (int)((float)this.green * (1.0F - amount)), (int)((float)this.blue * (1.0F - amount)), this.alpha
        );
    }

    public static ColorRGBA fromHSB(float hue, float saturation, float brightness) {
        if (saturation == 0.0F) {
            int grayValue = (int)(brightness * 255.0F + 0.5F);
            return new ColorRGBA(grayValue, grayValue, grayValue);
        } else {
            float h = (hue - (float)Math.floor((double)hue)) * 6.0F;
            float f = h - (float)Math.floor((double)h);
            float p = brightness * (1.0F - saturation);
            float q = brightness * (1.0F - saturation * f);
            float t = brightness * (1.0F - saturation * (1.0F - f));
            float r = 0.0F;
            float g = 0.0F;
            float b = 0.0F;
            switch ((int)h) {
                case 0:
                    r = brightness;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = brightness;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = brightness;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = brightness;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = brightness;
                    break;
                case 5:
                    r = brightness;
                    g = p;
                    b = q;
            }

            return new ColorRGBA((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F));
        }
    }

    public float getHue() {
        return this.getHSBValues()[0];
    }

    public float getSaturation() {
        return this.getHSBValues()[2];
    }

    public float getBrightness() {
        return this.getHSBValues()[1];
    }

    private float[] getHSBValues() {
        if (this.hsbValues == null) {
            this.hsbValues = this.calculateHSB();
        }

        return this.hsbValues;
    }

    private float[] calculateHSB() {
        float r = (float)this.red / 255.0F;
        float g = (float)this.green / 255.0F;
        float b = (float)this.blue / 255.0F;
        float maxC = Math.max(r, Math.max(g, b));
        float minC = Math.min(r, Math.min(g, b));
        float delta = maxC - minC;
        float hue = 0.0F;
        if (delta != 0.0F) {
            if (maxC == r) {
                hue = (g - b) / delta;
            } else if (maxC == g) {
                hue = (b - r) / delta + 2.0F;
            } else {
                hue = (r - g) / delta + 4.0F;
            }

            hue /= 6.0F;
            if (hue < 0.0F) {
                hue++;
            }
        }

        float saturation = maxC == 0.0F ? 0.0F : delta / maxC;
        return new float[]{hue, saturation, maxC};
    }

    public ColorRGBA brighter(float amount) {
        amount = MathHelper.clamp(amount, 0.0F, 1.0F);
        return new ColorRGBA(
            (int)((float)this.red + (255.0F - (float)this.red) * amount),
            (int)((float)this.green + (255.0F - (float)this.green) * amount),
            (int)((float)this.blue + (255.0F - (float)this.blue) * amount),
            this.alpha
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ColorRGBA colorRGBA = (ColorRGBA)o;
            return Float.compare((float)this.red, (float)colorRGBA.red) == 0
                && Float.compare((float)this.green, (float)colorRGBA.green) == 0
                && Float.compare((float)this.blue, (float)colorRGBA.blue) == 0
                && Float.compare((float)this.alpha, (float)colorRGBA.alpha) == 0;
        } else {
            return false;
        }
    }

    public float difference(ColorRGBA colorRGBA) {
        return Math.abs(this.getHue() - colorRGBA.getHue())
            + Math.abs(this.getBrightness() - colorRGBA.getBrightness())
            + Math.abs(this.getSaturation() - colorRGBA.getSaturation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.red, this.green, this.blue, this.alpha);
    }

        public int getRed() {
        return this.red;
    }

        public int getGreen() {
        return this.green;
    }

        public int getBlue() {
        return this.blue;
    }

        public int getAlpha() {
        return this.alpha;
    }
}
