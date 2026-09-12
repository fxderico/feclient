package dev.fede.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class EspBoxRenderer {
   private static final float TRACER_START = 0.35F;
   private static final float TRACER_NEAR = 0.1F;

   private EspBoxRenderer() {
   }

   public static void outline(
      Immediate bufferSource, MatrixStack poseStack, Vec3d cam, double x0, double y0, double z0, double x1, double y1, double z1, int color, float width
   ) {
      VertexConsumer buf = bufferSource.getBuffer(FlatOverlay.LINES);
      Entry pose = poseStack.peek();
      float ax = (float)(x0 - cam.x);
      float ay = (float)(y0 - cam.y);
      float az = (float)(z0 - cam.z);
      float bx = (float)(x1 - cam.x);
      float by = (float)(y1 - cam.y);
      float bz = (float)(z1 - cam.z);
      line(buf, pose, ax, ay, az, bx, ay, az, color, width);
      line(buf, pose, bx, ay, az, bx, ay, bz, color, width);
      line(buf, pose, bx, ay, bz, ax, ay, bz, color, width);
      line(buf, pose, ax, ay, bz, ax, ay, az, color, width);
      line(buf, pose, ax, by, az, bx, by, az, color, width);
      line(buf, pose, bx, by, az, bx, by, bz, color, width);
      line(buf, pose, bx, by, bz, ax, by, bz, color, width);
      line(buf, pose, ax, by, bz, ax, by, az, color, width);
      line(buf, pose, ax, ay, az, ax, by, az, color, width);
      line(buf, pose, bx, ay, az, bx, by, az, color, width);
      line(buf, pose, bx, ay, bz, bx, by, bz, color, width);
      line(buf, pose, ax, ay, bz, ax, by, bz, color, width);
   }

   public static void fill(
      Immediate bufferSource, MatrixStack poseStack, Vec3d cam, double x0, double y0, double z0, double x1, double y1, double z1, int color
   ) {
      VertexConsumer buf = bufferSource.getBuffer(FlatOverlay.FILL);
      Entry pose = poseStack.peek();
      float ax = (float)(x0 - cam.x);
      float ay = (float)(y0 - cam.y);
      float az = (float)(z0 - cam.z);
      float bx = (float)(x1 - cam.x);
      float by = (float)(y1 - cam.y);
      float bz = (float)(z1 - cam.z);
      quad(buf, pose, color, ax, ay, az, bx, ay, az, bx, ay, bz, ax, ay, bz);
      quad(buf, pose, color, ax, by, az, ax, by, bz, bx, by, bz, bx, by, az);
      quad(buf, pose, color, ax, ay, az, ax, by, az, bx, by, az, bx, ay, az);
      quad(buf, pose, color, ax, ay, bz, bx, ay, bz, bx, by, bz, ax, by, bz);
      quad(buf, pose, color, ax, ay, az, ax, ay, bz, ax, by, bz, ax, by, az);
      quad(buf, pose, color, bx, ay, az, bx, by, az, bx, by, bz, bx, ay, bz);
   }

   public static void tracer(
      Immediate bufferSource, MatrixStack poseStack, Vec3d cam, Vector3fc forward, double tx, double ty, double tz, int color, float width
   ) {
      float fx = forward.x();
      float fy = forward.y();
      float fz = forward.z();
      float flen = (float)Math.sqrt(fx * fx + fy * fy + fz * fz);
      if (flen > 1.0E-6F) {
         fx /= flen;
         fy /= flen;
         fz /= flen;
      }

      float sx = fx * 0.35F;
      float sy = fy * 0.35F;
      float sz = fz * 0.35F;
      float ex = (float)(tx - cam.x);
      float ey = (float)(ty - cam.y);
      float ez = (float)(tz - cam.z);
      float endDot = ex * fx + ey * fy + ez * fz;
      if (endDot < 0.1F) {
         float t = -0.25F / (endDot - 0.35F);
         ex = sx + (ex - sx) * t;
         ey = sy + (ey - sy) * t;
         ez = sz + (ez - sz) * t;
      }

      VertexConsumer buf = bufferSource.getBuffer(FlatOverlay.LINES);
      Entry pose = poseStack.peek();
      line(buf, pose, sx, sy, sz, ex, ey, ez, color, width);
   }

   public static void ring(
      Immediate bufferSource, MatrixStack poseStack, Vec3d cam, double cx, double cy, double cz, double radius, int segments, int color, float width
   ) {
      VertexConsumer buf = bufferSource.getBuffer(FlatOverlay.LINES);
      Entry pose = poseStack.peek();
      float fy = (float)(cy - cam.y);
      double prevX = cx + radius;
      double prevZ = cz;

      for (int i = 1; i <= segments; i++) {
         double a = (Math.PI * 2) * i / segments;
         double x = cx + Math.cos(a) * radius;
         double z = cz + Math.sin(a) * radius;
         line(buf, pose, (float)(prevX - cam.x), fy, (float)(prevZ - cam.z), (float)(x - cam.x), fy, (float)(z - cam.z), color, width);
         prevX = x;
         prevZ = z;
      }
   }

   public static void flush(Immediate bufferSource) {
      FlatOverlay.flush(bufferSource);
   }

   private static void line(VertexConsumer buf, Entry pose, float x1, float y1, float z1, float x2, float y2, float z2, int color, float width) {
      Vector3f n = new Vector3f(x2 - x1, y2 - y1, z2 - z1);
      if (n.lengthSquared() > 1.0E-9F) {
         n.normalize();
      } else {
         n.set(0.0F, 1.0F, 0.0F);
      }

      buf.vertex(pose, x1, y1, z1).color(color).normal(pose, n).lineWidth(width);
      buf.vertex(pose, x2, y2, z2).color(color).normal(pose, n).lineWidth(width);
   }

   private static void quad(
      VertexConsumer buf,
      Entry pose,
      int color,
      float ax,
      float ay,
      float az,
      float bx,
      float by,
      float bz,
      float cx,
      float cy,
      float cz,
      float dx,
      float dy,
      float dz
   ) {
      buf.vertex(pose, ax, ay, az).color(color);
      buf.vertex(pose, bx, by, bz).color(color);
      buf.vertex(pose, cx, cy, cz).color(color);
      buf.vertex(pose, dx, dy, dz).color(color);
   }
}

