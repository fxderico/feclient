package dev.fede.zenithgui.base.font;

// [lombok removed]
import net.minecraft.text.Text;

public class Font {
    private MsdfFont font;
    private float size;

    public float height() {
        return this.size * 0.7F;
    }

    public float width(String text) {
        return this.font.getWidth(text, this.size);
    }

    public float width(Text text) {
        return this.font.getTextWidth(text, this.size);
    }

        public MsdfFont getFont() {
        return this.font;
    }

        public float getSize() {
        return this.size;
    }

        public Font(MsdfFont font, float size) {
        this.font = font;
        this.size = size;
    }
}
