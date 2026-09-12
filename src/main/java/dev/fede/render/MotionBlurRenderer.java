package dev.fede.render;

import dev.fede.module.impl.MotionBlurModule;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.util.Colors;
import java.nio.ByteBuffer;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL33C;

public final class MotionBlurRenderer {
   private static final double EASE_SPEED = 12.0;
   private static int historyTex = -1;
   private static int historyImage = -1;
   private static int texW;
   private static int texH;
   private static boolean primed;
   private static long lastNanos;
   private static double enableAmount;
   private static int framesRendered;
   private static float lastRetention;

   private MotionBlurRenderer() {
   }

   public static void render(NVGRenderer vg, int width, int height, MotionBlurModule module) {
      if (width > 0 && height > 0) {
         long now = System.nanoTime();
         double dt = lastNanos == 0L ? 0.0 : (now - lastNanos) / 1.E9;
         lastNanos = now;
         double easeT = 1.0 - Math.exp(-12.0 * Math.max(0.0, dt));
         enableAmount = enableAmount + (1.0 - enableAmount) * easeT;
         if (enableAmount > 0.999) {
            enableAmount = 1.0;
         }

         if (historyTex == -1 || texW != width || texH != height) {
            allocate(vg, width, height);
            primed = false;
         }

         double base = module.retention();
         if (module.fpsCompensated.get() && dt > 0.0) {
            base = Math.pow(base, dt * 60.0);
         }

         double retention = Math.clamp(base * enableAmount, 0.0, 0.97);
         lastRetention = (float)retention;
         if (!primed) {
            copyToHistory(width, height);
            primed = true;
            framesRendered++;
         } else {
            int tintColor = -1;
            float tintAmount = module.tintAmount();
            if (tintAmount > 0.0F) {
               tintColor = Colors.lerp(-1, Colors.withAlpha(module.accentColor(), 255), tintAmount);
            }

            vg.beginFrame(width, height, 1.0F);
            vg.save();
            vg.alpha((float)retention);
            vg.image(historyImage, 0.0F, 0.0F, width, height, tintColor);
            vg.restore();
            vg.endFrame();
            copyToHistory(width, height);
            framesRendered++;
         }
      }
   }

   private static void allocate(NVGRenderer vg, int width, int height) {
      long ctx = vg.ctx();
      if (historyImage > 0) {
         NanoVG.nvgDeleteImage(ctx, historyImage);
         historyImage = -1;
      }

      if (historyTex != -1) {
         GL33C.glDeleteTextures(historyTex);
      }

      historyTex = GL33C.glGenTextures();
      GL33C.glActiveTexture(33984);
      GL33C.glBindTexture(3553, historyTex);
      GL33C.glTexImage2D(3553, 0, 32856, width, height, 0, 6408, 5121, (ByteBuffer)null);
      GL33C.glTexParameteri(3553, 10241, 9729);
      GL33C.glTexParameteri(3553, 10240, 9729);
      GL33C.glTexParameteri(3553, 10242, 33071);
      GL33C.glTexParameteri(3553, 10243, 33071);
      GL33C.glTexParameteri(3553, 36421, 1);
      texW = width;
      texH = height;
      historyImage = NanoVGGL3.nvglCreateImageFromHandle(ctx, historyTex, width, height, 65544);
   }

   private static void copyToHistory(int width, int height) {
      GL33C.glActiveTexture(33984);
      GL33C.glBindTexture(3553, historyTex);
      GL33C.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, width, height);
   }

   public static void reset() {
      primed = false;
      enableAmount = 0.0;
      lastNanos = 0L;
   }

   public static int framesRendered() {
      return framesRendered;
   }

   public static float lastRetention() {
      return lastRetention;
   }
}

