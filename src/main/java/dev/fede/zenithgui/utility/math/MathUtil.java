package dev.fede.zenithgui.utility.math;

import java.util.concurrent.ThreadLocalRandom;
// [lombok removed]
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import dev.fede.zenithgui.utility.interfaces.IMinecraft;

public final class MathUtil implements IMinecraft {
    public static double PI2 = Math.PI * 2;
    private static final int TABLE_SIZE = 65536;
    private static final double TWO_PI = Math.PI * 2;
    private static final double[] TRIG_TABLE = new double[65536];

    public static double sin(double radians) {
        int index = (int)(radians * 10430.378350470453) & 65535;
        return TRIG_TABLE[index];
    }

    public static double cos(double radians) {
        int index = (int)(radians * 10430.378350470453 + 16384.0) & 65535;
        return TRIG_TABLE[index];
    }

    public static float random(double min, double max) {
        return (float)(min + (max - min) * Math.random());
    }

    public static double cubicBezier(double t, double p0, double p1, double p2, double p3) {
        return Math.pow(1.0 - t, 3.0) * p0 + 3.0 * t * Math.pow(1.0 - t, 2.0) * p1 + 3.0 * Math.pow(t, 2.0) * (1.0 - t) * p2 + Math.pow(t, 3.0) * p3;
    }

    public static int levenshtein(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[] dp = new int[m + 1];
        int j = 0;

        while (j <= m) {
            dp[j] = j++;
        }

        for (int i = 1; i <= n; i++) {
            int prev = dp[0];
            dp[0] = i;

            for (int jx = 1; jx <= m; jx++) {
                int tmp = dp[jx];
                int cost = a.charAt(i - 1) == b.charAt(jx - 1) ? 0 : 1;
                dp[jx] = Math.min(Math.min(dp[jx] + 1, dp[jx - 1] + 1), prev + cost);
                prev = tmp;
            }
        }

        return dp[m];
    }

    public static float angleDifference(float angle1, float angle2) {
        float diff = (angle1 - angle2) % 360.0F;
        if (diff < -180.0F) {
            diff += 360.0F;
        } else if (diff > 180.0F) {
            diff -= 360.0F;
        }

        return diff;
    }

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static boolean isHoveredByCords(double mouseX, double mouseY, int x, int y, int xEnd, int yEnd) {
        return mouseX >= (double)x && mouseX <= (double)xEnd && mouseY >= (double)y && mouseY <= (double)yEnd;
    }

    public static float interpolate(double oldValue, double newValue, double interpolationValue) {
        return (float)(oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float goodSubtract(float value1, float value2) {
        return Math.abs(value1 - value2);
    }

    public static double getRandom(double min, double max) {
        if (min == max) {
            return min;
        } else {
            if (min > max) {
                double d = min;
                min = max;
                max = d;
            }

            return ThreadLocalRandom.current().nextDouble() * (max - min) + min;
        }
    }

    public static float round(float value) {
        return (float)Math.round(value * 10.0F) / 10.0F;
    }

    public static double round(double num, double increment) {
        double rounded = (double)Math.round(num / increment) * increment;
        return (double)Math.round(rounded * 100.0) / 100.0;
    }

    public static Vec3d cosSin(int i, int size, double width) {
        int index = Math.min(i, size);
        float cos = (float)(Math.cos((double)index * PI2 / (double)size) * width);
        float sin = (float)(-Math.sin((double)index * PI2 / (double)size) * width);
        return new Vec3d((double)cos, 0.0, (double)sin);
    }

    public static Vector3d interpolate(Vector3d prevPos, Vector3d pos) {
        return new Vector3d(interpolate(prevPos.x, pos.x), interpolate(prevPos.y, pos.y), interpolate(prevPos.z, pos.z));
    }

    public static Vec3d interpolate(Vec3d prevPos, Vec3d pos) {
        return new Vec3d(
            interpolate(prevPos.x, pos.x), interpolate(prevPos.y, pos.y), interpolate(prevPos.z, pos.z)
        );
    }

    public static Vec3d interpolate(Entity entity) {
        // entity.prevX/Y/Z renamed in MC 1.21.1 — use current position (no lerp)
        return entity == null ? Vec3d.ZERO : new Vec3d(entity.getX(), entity.getY(), entity.getZ());
    }

    public static float interpolate(float prev, float orig) {
        // mc.getRenderTickCounter().getTickDelta() API changed in MC 1.21.1 — use 1.0f fallback
        return MathHelper.lerp(1.0f, prev, orig);
    }

    public static double interpolate(double prev, double orig) {
        return MathHelper.lerp(1.0, prev, orig);
    }

    public static int interpolateSmooth(double smooth, int prev, int orig) {
        return (int)MathHelper.lerp(1.0 / smooth, (double)prev, (double)orig);
    }

    public static float interpolateSmooth(double smooth, float prev, float orig) {
        return (float)MathHelper.lerp(1.0 / smooth, (double)prev, (double)orig);
    }

    public static double interpolateSmooth(double smooth, double prev, double orig) {
        return MathHelper.lerp(1.0 / smooth, prev, orig);
    }

    public static double getDistance(Vec3d pos1, Vec3d pos2) {
        double deltaX = pos1.getX() - pos2.getX();
        double deltaY = pos1.getY() - pos2.getY();
        double deltaZ = pos1.getZ() - pos2.getZ();
        return (double)MathHelper.sqrt((float)(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
    }

        private MathUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        for (int i = 0; i < 65536; i++) {
            TRIG_TABLE[i] = Math.sin((double)i * (Math.PI * 2) / 65536.0);
        }
    }
}
