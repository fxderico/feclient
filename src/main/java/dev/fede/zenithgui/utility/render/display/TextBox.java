package dev.fede.zenithgui.utility.render.display;

// [lombok removed]
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import dev.fede.zenithgui.base.animations.base.Animation;
import dev.fede.zenithgui.base.animations.base.Easing;
import dev.fede.zenithgui.base.font.Font;
import dev.fede.zenithgui.utility.game.other.MouseButton;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;
import dev.fede.zenithgui.utility.math.MathUtil;
import dev.fede.zenithgui.utility.render.display.base.CustomDrawContext;
import dev.fede.zenithgui.utility.render.display.base.color.ColorRGBA;

public class TextBox implements IMinecraft {
    private String text = "";
    private boolean selected;
    private boolean selectAll;
    private int cursor;
    private float posX;
    private Font font;
    private Vector2f position;
    private String emptyText;
    private float width;
    private long lastInputTime = System.currentTimeMillis();
    private int maxLength = Integer.MAX_VALUE;
    private TextBox.CharFilter charFilter = TextBox.CharFilter.ANY;
    private float scrollOffset = 0.0F;
    Animation animation = new Animation(400L, 0.2F, Easing.QUAD_IN_OUT);

    public TextBox(Vector2f position, Font font, String emptyText, float width) {
        this.font = font;
        this.emptyText = emptyText;
        this.width = width;
        this.position = position;
    }

    public void render(CustomDrawContext context, float x, float y, ColorRGBA colorText, ColorRGBA colorEmpty) {
        this.position = new Vector2f(x, y);
        this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
        this.posX = x;
        boolean isEmpty = this.isEmpty();
        float cursorX = 0.0F;
        if (!isEmpty) {
            String textBeforeCursor = this.text.substring(0, this.cursor);
            cursorX = this.font.width(textBeforeCursor);
        }

        float availableWidth = this.width;
        int startIndex = 0;

        while (this.font.width(this.text.substring(startIndex, this.cursor)) > availableWidth) {
            startIndex++;
        }

        int endIndex = this.cursor;

        while (endIndex < this.text.length() && this.font.width(this.text.substring(startIndex, endIndex)) < availableWidth) {
            endIndex++;
        }

        String visibleText = this.text.substring(startIndex, endIndex);
        if (isEmpty) {
            context.drawText(this.font, this.emptyText, x, y, colorEmpty);
        } else {
            context.drawText(this.font, visibleText, x, y, colorText);
        }

        if (this.selected && System.currentTimeMillis() - this.lastInputTime > 200L) {
            float cursorDrawX = this.posX + cursorX - this.scrollOffset;
            this.animation.setDuration(250L);
            context.drawRect(
                cursorDrawX,
                y - 1.0F,
                1.0F,
                this.font.height() + 2.0F,
                colorText.mulAlpha(
                    this.animation
                        .update(this.animation.getValue() == 0.2F ? 1.0F : (this.animation.getValue() == 1.0F ? 0.2F : this.animation.getTargetValue()))
                )
            );
        }

        if (this.selectAll) {
            context.drawRect(x - 1.0F, y - 1.0F, this.font.width(visibleText) + 2.0F, this.font.height() + 2.0F, colorEmpty.mulAlpha(0.5F));
        }
    }

    public void onMouseClicked(double mouseX, double mouseY, MouseButton button) {
        Vector2f pos = this.getPosition();
        this.selected = button.getButtonIndex() == 0
            && MathUtil.isHovered(
                mouseX, mouseY, (double)pos.x(), (double)(pos.y() - 1.0F), (double)this.width, (double)(this.font.height() + 2.0F)
            );
        if (this.selected) {
            this.selectAll = false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.selected) {
            return false;
        } else {
            this.lastInputTime = System.currentTimeMillis();
            this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
            if (InputUtil.isKeyPressed(mc.getWindow(), 341)) {
                if (keyCode == 86) {
                    String clipboard = mc.keyboard.getClipboard();
                    if (this.selectAll) {
                        this.text = "";
                        this.cursor = 0;
                        this.selectAll = false;
                    }

                    this.addText(clipboard, this.cursor);
                    this.cursor = this.cursor + clipboard.length();
                    this.selectAll = false;
                } else if (keyCode == 65) {
                    this.selectAll = true;
                    this.cursor = this.text.length();
                } else if (keyCode == 67 && this.selected && this.selectAll) {
                    mc.keyboard.setClipboard(this.text);
                }
            } else if (keyCode == 261 && !this.text.isEmpty()) {
                this.removeText(this.cursor + 1);
                this.selectAll = false;
            } else if (keyCode == 259 && !this.text.isEmpty()) {
                if (this.selectAll) {
                    this.text = "";
                    this.cursor = 0;
                    this.selectAll = false;
                } else {
                    this.removeText(this.cursor);
                    this.cursor--;
                    if (InputUtil.isKeyPressed(mc.getWindow(), 341)) {
                        while (!this.text.isEmpty() && this.cursor > 0) {
                            this.removeText(this.cursor);
                            this.cursor--;
                        }
                    }
                }
            } else if (keyCode == 262) {
                this.cursor++;
                if (InputUtil.isKeyPressed(mc.getWindow(), 341)) {
                    this.cursor = this.text.length();
                }

                this.selectAll = false;
            } else if (keyCode == 263) {
                this.cursor--;
                if (InputUtil.isKeyPressed(mc.getWindow(), 341)) {
                    this.cursor = 0;
                }

                this.selectAll = false;
            } else if (keyCode == 269) {
                this.cursor = this.text.length();
                this.selectAll = false;
            } else if (keyCode == 268) {
                this.cursor = 0;
                this.selectAll = false;
            }

            this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
            return true;
        }
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.selected) {
            return false;
        } else {
            this.lastInputTime = System.currentTimeMillis();
            this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
            if (this.selectAll) {
                this.text = "";
                this.cursor = 0;
                this.selectAll = false;
            }

            this.addText(Character.toString(codePoint), this.cursor);
            this.cursor++;
            this.cursor = MathHelper.clamp(this.cursor, 0, this.text.length());
            return true;
        }
    }

    private void addText(String newText, int position) {
        StringBuilder filteredText = new StringBuilder();

        for (char c : newText.toCharArray()) {
            if (this.charFilter.isAllowed(c)) {
                filteredText.append(c);
            }
        }

        String filtered = filteredText.toString();
        if (this.text.length() + filtered.length() > this.maxLength) {
            int available = this.maxLength - this.text.length();
            if (available <= 0) {
                return;
            }

            filtered = filtered.substring(0, Math.min(available, filtered.length()));
        }

        StringBuilder newFinalText = new StringBuilder();
        boolean inserted = false;

        for (int i = 0; i < this.text.length(); i++) {
            if (i == position) {
                inserted = true;
                newFinalText.append(filtered);
            }

            newFinalText.append(this.text.charAt(i));
        }

        if (!inserted) {
            newFinalText.append(filtered);
        }

        this.text = newFinalText.toString();
    }

    private void removeText(int position) {
        StringBuilder newText = new StringBuilder();

        for (int i = 0; i < this.text.length(); i++) {
            if (i != position - 1) {
                newText.append(this.text.charAt(i));
            }
        }

        this.text = newText.toString();
    }

    public boolean isEmpty() {
        return this.text.isEmpty();
    }

        public String getText() {
        return this.text;
    }

        public boolean isSelected() {
        return this.selected;
    }

        public boolean isSelectAll() {
        return this.selectAll;
    }

        public int getCursor() {
        return this.cursor;
    }

        public float getPosX() {
        return this.posX;
    }

        public Font getFont() {
        return this.font;
    }

        public Vector2f getPosition() {
        return this.position;
    }

        public String getEmptyText() {
        return this.emptyText;
    }

        public float getWidth() {
        return this.width;
    }

        public long getLastInputTime() {
        return this.lastInputTime;
    }

        public int getMaxLength() {
        return this.maxLength;
    }

        public TextBox.CharFilter getCharFilter() {
        return this.charFilter;
    }

        public float getScrollOffset() {
        return this.scrollOffset;
    }

        public Animation getAnimation() {
        return this.animation;
    }

        public void setText(String text) {
        this.text = text;
    }

        public void setSelected(boolean selected) {
        this.selected = selected;
    }

        public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

        public void setCursor(int cursor) {
        this.cursor = cursor;
    }

        public void setPosX(float posX) {
        this.posX = posX;
    }

        public void setFont(Font font) {
        this.font = font;
    }

        public void setPosition(Vector2f position) {
        this.position = position;
    }

        public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

        public void setWidth(float width) {
        this.width = width;
    }

        public void setLastInputTime(long lastInputTime) {
        this.lastInputTime = lastInputTime;
    }

        public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

        public void setCharFilter(TextBox.CharFilter charFilter) {
        this.charFilter = charFilter;
    }

        public void setScrollOffset(float scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

        public void setAnimation(Animation animation) {
        this.animation = animation;
    }

        @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TextBox other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else if (this.isSelected() != other.isSelected()) {
            return false;
        } else if (this.isSelectAll() != other.isSelectAll()) {
            return false;
        } else if (this.getCursor() != other.getCursor()) {
            return false;
        } else if (Float.compare(this.getPosX(), other.getPosX()) != 0) {
            return false;
        } else if (Float.compare(this.getWidth(), other.getWidth()) != 0) {
            return false;
        } else if (this.getLastInputTime() != other.getLastInputTime()) {
            return false;
        } else if (this.getMaxLength() != other.getMaxLength()) {
            return false;
        } else if (Float.compare(this.getScrollOffset(), other.getScrollOffset()) != 0) {
            return false;
        } else {
            Object this$text = this.getText();
            Object other$text = other.getText();
            if (this$text == null ? other$text == null : this$text.equals(other$text)) {
                Object this$font = this.getFont();
                Object other$font = other.getFont();
                if (this$font == null ? other$font == null : this$font.equals(other$font)) {
                    Object this$position = this.getPosition();
                    Object other$position = other.getPosition();
                    if (this$position == null ? other$position == null : this$position.equals(other$position)) {
                        Object this$emptyText = this.getEmptyText();
                        Object other$emptyText = other.getEmptyText();
                        if (this$emptyText == null ? other$emptyText == null : this$emptyText.equals(other$emptyText)) {
                            Object this$charFilter = this.getCharFilter();
                            Object other$charFilter = other.getCharFilter();
                            if (this$charFilter == null ? other$charFilter == null : this$charFilter.equals(other$charFilter)) {
                                Object this$animation = this.getAnimation();
                                Object other$animation = other.getAnimation();
                                return this$animation == null ? other$animation == null : this$animation.equals(other$animation);
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

        protected boolean canEqual(Object other) {
        return other instanceof TextBox;
    }

        @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSelected() ? 79 : 97);
        result = result * 59 + (this.isSelectAll() ? 79 : 97);
        result = result * 59 + this.getCursor();
        result = result * 59 + Float.floatToIntBits(this.getPosX());
        result = result * 59 + Float.floatToIntBits(this.getWidth());
        long $lastInputTime = this.getLastInputTime();
        result = result * 59 + (int)($lastInputTime >>> 32 ^ $lastInputTime);
        result = result * 59 + this.getMaxLength();
        result = result * 59 + Float.floatToIntBits(this.getScrollOffset());
        Object $text = this.getText();
        result = result * 59 + ($text == null ? 43 : $text.hashCode());
        Object $font = this.getFont();
        result = result * 59 + ($font == null ? 43 : $font.hashCode());
        Object $position = this.getPosition();
        result = result * 59 + ($position == null ? 43 : $position.hashCode());
        Object $emptyText = this.getEmptyText();
        result = result * 59 + ($emptyText == null ? 43 : $emptyText.hashCode());
        Object $charFilter = this.getCharFilter();
        result = result * 59 + ($charFilter == null ? 43 : $charFilter.hashCode());
        Object $animation = this.getAnimation();
        return result * 59 + ($animation == null ? 43 : $animation.hashCode());
    }

        @Override
    public String toString() {
        return "TextBox(text="
            + this.getText()
            + ", selected="
            + this.isSelected()
            + ", selectAll="
            + this.isSelectAll()
            + ", cursor="
            + this.getCursor()
            + ", posX="
            + this.getPosX()
            + ", font="
            + this.getFont()
            + ", position="
            + this.getPosition()
            + ", emptyText="
            + this.getEmptyText()
            + ", width="
            + this.getWidth()
            + ", lastInputTime="
            + this.getLastInputTime()
            + ", maxLength="
            + this.getMaxLength()
            + ", charFilter="
            + this.getCharFilter()
            + ", scrollOffset="
            + this.getScrollOffset()
            + ", animation="
            + this.getAnimation()
            + ")";
    }

    public static enum CharFilter {
        ANY,
        ENGLISH,
        ENGLISH_NUMBERS,
        CYRILLIC,
        NUMBERS_ONLY;

        public boolean isAllowed(char c) {
            return switch (this) {
                case ANY -> true;
                case ENGLISH -> Character.isLetter(c) && c <= 127 && Character.isAlphabetic(c);
                case ENGLISH_NUMBERS -> Character.isLetterOrDigit(c) && c <= 127;
                case CYRILLIC -> String.valueOf(c).matches("[А-Яа-яЁё]");
                case NUMBERS_ONLY -> Character.isDigit(c);
            };
        }
    }
}
