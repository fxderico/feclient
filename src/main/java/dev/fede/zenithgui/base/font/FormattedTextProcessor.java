package dev.fede.zenithgui.base.font;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public class FormattedTextProcessor {
    public static List<FormattedTextProcessor.TextSegment> processText(Text text, int defaultColor) {
        List<FormattedTextProcessor.TextSegment> segments = new ArrayList<>();
        text.visit((style, string) -> {
            if (!string.isEmpty()) {
                int color = extractColor(style, defaultColor);
                boolean bold = style.isBold();
                boolean italic = style.isItalic();
                boolean underlined = style.isUnderlined();
                boolean strikethrough = style.isStrikethrough();
                segments.add(new FormattedTextProcessor.TextSegment(string, color, bold, italic, underlined, strikethrough));
            }

            return Optional.empty();
        }, Style.EMPTY);
        return segments;
    }

    private static int extractColor(Style style, int defaultColor) {
        TextColor textColor = style.getColor();
        return textColor != null ? textColor.getRgb() | 0xFF000000 : defaultColor;
    }

    public static record TextSegment(String text, int color, boolean bold, boolean italic, boolean underlined, boolean strikethrough) {
    }
}
