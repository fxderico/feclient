package dev.fede.water.utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import java.awt.Color;
public final class RenderUtils {
    private RenderUtils(){}
    public static void drawBox(MatrixStack m,double x,double y,double z,double w,double h,double d,Color c,boolean b){}
    public static void drawLine(MatrixStack m,Vec3d a,Vec3d b2,Color c){}
    public static void a(){}
    public static void b(){}
    public static class PersistentBatch {
        public PersistentBatch(){}
        public void add(Object o){}
        public void clear(){}
        public void render(MatrixStack m){}
    }
    public static class WorldBatch {
        public WorldBatch(){}
        public void renderLine(Color c,Vec3d a,Vec3d b2,float w){}
        public void clear(){}
        public void render(MatrixStack m){}
    }
}
