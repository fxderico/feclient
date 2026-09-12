package dev.fede.zenithgui.utility.render.display.base;

// [lombok removed]
import dev.fede.zenithgui.utility.math.MathUtil;

public class ChangeRect {
    float x;
    float y;
    float width;
    float height;

    public boolean contains(double mx, double my) {
        return MathUtil.isHovered(mx, my, (double)this.x, (double)this.y, (double)this.width, (double)this.height);
    }

        public ChangeRect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

        public float getX() {
        return this.x;
    }

        public float getY() {
        return this.y;
    }

        public float getWidth() {
        return this.width;
    }

        public float getHeight() {
        return this.height;
    }

        public void setX(float x) {
        this.x = x;
    }

        public void setY(float y) {
        this.y = y;
    }

        public void setWidth(float width) {
        this.width = width;
    }

        public void setHeight(float height) {
        this.height = height;
    }

        @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ChangeRect other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else if (Float.compare(this.getX(), other.getX()) != 0) {
            return false;
        } else if (Float.compare(this.getY(), other.getY()) != 0) {
            return false;
        } else {
            return Float.compare(this.getWidth(), other.getWidth()) != 0 ? false : Float.compare(this.getHeight(), other.getHeight()) == 0;
        }
    }

        protected boolean canEqual(Object other) {
        return other instanceof ChangeRect;
    }

        @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getX());
        result = result * 59 + Float.floatToIntBits(this.getY());
        result = result * 59 + Float.floatToIntBits(this.getWidth());
        return result * 59 + Float.floatToIntBits(this.getHeight());
    }

        @Override
    public String toString() {
        return "ChangeRect(x=" + this.getX() + ", y=" + this.getY() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ")";
    }
}
