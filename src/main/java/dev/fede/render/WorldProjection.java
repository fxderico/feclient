package dev.fede.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

public final class WorldProjection {
   private static final Matrix4f mvp = new Matrix4f();
   private static final Vector4f scratch = new Vector4f();
   private static Vec3d camPos = Vec3d.ZERO;
   private static int fbWidth;
   private static int fbHeight;
   private static float partialTick;
   private static boolean valid;

   private WorldProjection() {
   }

   public static void capture(Matrix4f projection, float partialTick) {
      MinecraftClient mc = MinecraftClient.getInstance();
      Camera camera = mc.gameRenderer.getCamera();
      if (camera == null) {
         valid = false;
      } else {
         Quaternionf view = camera.getRotation().conjugate(new Quaternionf());
         mvp.set(projection).rotate(view);
         camPos = camera.getCameraPos();
         Framebuffer target = mc.getFramebuffer();
         fbWidth = target.textureWidth;
         fbHeight = target.textureHeight;
         WorldProjection.partialTick = partialTick;
         valid = true;
      }
   }

   public static void invalidate() {
      valid = false;
   }

   public static boolean isValid() {
      return valid;
   }

   public static float partialTick() {
      return partialTick;
   }

   public static float[] project(double wx, double wy, double wz) {
      float[] px = projectRaw(wx, wy, wz);
      if (px == null) {
         return null;
      } else {
         float scale = OverlayRenderer.uiScale();
         return new float[]{px[0] / scale, px[1] / scale};
      }
   }

   public static float[] projectRaw(double wx, double wy, double wz) {
      if (!valid) {
         return null;
      } else {
         scratch.set((float)(wx - camPos.x), (float)(wy - camPos.y), (float)(wz - camPos.z), 1.0F);
         mvp.transform(scratch);
         if (scratch.w <= 1.0E-4F) {
            return null;
         } else {
            float ndcX = scratch.x / scratch.w;
            float ndcY = scratch.y / scratch.w;
            float px = (ndcX * 0.5F + 0.5F) * fbWidth;
            float py = (1.0F - (ndcY * 0.5F + 0.5F)) * fbHeight;
            return new float[]{px, py};
         }
      }
   }
}

