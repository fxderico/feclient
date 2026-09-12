package dev.fede.zenithgui.utility.render.display.base;

// [lombok removed]
import net.minecraft.client.util.math.Vector2f;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;

public final class GuiUtil implements IMinecraft {
    public static boolean isHovered(double x, double y, double width, double height, int mouseX, int mouseY) {
        return (double)mouseX >= x && (double)mouseX < x + width && (double)mouseY >= y && (double)mouseY < y + height;
    }

    public static boolean isHovered(double x, double y, double width, double height, UIContext context) {
        return isHovered(x, y, width, height, context.getMouseX(), context.getMouseY());
    }

    public static boolean isHovered(double x, double y, double width, double height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static Vector2f getMouse(double customScale) {
        return new Vector2f((float)(mc.mouse.getX() / customScale), (float)(mc.mouse.getY() / customScale));
    }

        private GuiUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
