package dev.fede.render;

import dev.fede.module.impl.JumpCirclesModule;
import java.util.Deque;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public final class JumpCircleRenderer {
   private static final Identifier TEXTURE_67 = Identifier.of("feclient", "textures/misc/fe_logo.png");
   private static final Identifier TEXTURE_RING = Identifier.of("feclient", "textures/misc/ring.png");
   private static final float POP_IN_TIME = 0.25F;
   private static final float FADE_OUT_TIME = 0.4F;
   private static final float SHOCKWAVE_TIME = 0.45F;
   private static final float PULSE_SPEED = 10.0F;
   private static final int SHIMMER_TARGET_RGB = 16761566;

   private JumpCircleRenderer() {
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, JumpCirclesModule module) {
      Deque<JumpCirclesModule.JumpCircle> circles = module.circles();
      if (!circles.isEmpty()) {
         long now = System.nanoTime();
         float lifetime = Math.max(0.1F, module.lifetime.getFloat());

         while (!circles.isEmpty() && circles.peekFirst().ageSeconds(now) > lifetime) {
            circles.removeFirst();
         }

         if (!circles.isEmpty()) {
            float size = module.size.getFloat();
            int baseRgb = currentBaseRgb(module);
            VertexConsumer decalBuffer = bufferSource.getBuffer(RenderLayers.entityTranslucentEmissive(TEXTURE_67));

            for (JumpCirclesModule.JumpCircle circle : circles) {
               renderDecal(poseStack, decalBuffer, circle, cam, now, lifetime, size, baseRgb);
            }

            if (module.shockwave.get()) {
               VertexConsumer ringBuffer = bufferSource.getBuffer(RenderLayers.entityTranslucentEmissive(TEXTURE_RING));

               for (JumpCirclesModule.JumpCircle circle : circles) {
                  renderShockwave(poseStack, ringBuffer, circle, cam, now, size, baseRgb);
               }
            }
         }
      }
   }

   private static void renderDecal(
      MatrixStack poseStack, VertexConsumer buffer, JumpCirclesModule.JumpCircle circle, Vec3d cam, long now, float lifetime, float size, int baseRgb
   ) {
      float age = circle.ageSeconds(now);
      float scale;
      float alpha;
      if (age < 0.25F) {
         float t = age / 0.25F;
         scale = easeOutBack(t);
         alpha = easeOutCubic(Math.min(1.0F, t * 2.0F));
      } else if (age > lifetime - 0.4F) {
         float t = (age - (lifetime - 0.4F)) / 0.4F;
         scale = lerp(easeOutCubic(t), 1.0F, 1.3F);
         alpha = 1.0F - easeInQuad(t);
      } else {
         scale = 1.0F;
         alpha = 1.0F;
      }

      float pulse = 0.5F + 0.5F * MathHelper.sin(age * 10.0F);
      alpha *= 0.82F + 0.18F * pulse;
      int rgb = lerpRgb(baseRgb, 16761566, pulse * 0.35F);
      int argb = withAlpha(rgb, alpha);
      poseStack.push();
      poseStack.translate(circle.doubleVal - cam.x, circle.doubleVal2 + circle.yLift - cam.y, circle.doubleVal3 - cam.z);
      poseStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - circle.yawDegrees));
      emitQuad(poseStack.peek(), buffer, 0.5F * size * scale, argb);
      poseStack.pop();
   }

   private static void renderShockwave(
      MatrixStack poseStack, VertexConsumer buffer, JumpCirclesModule.JumpCircle circle, Vec3d cam, long now, float size, int baseRgb
   ) {
      float age = circle.ageSeconds(now);
      if (!(age >= 0.45F)) {
         float t = age / 0.45F;
         float half = lerp(easeOutCubic(t), 0.35F, 2.2F) * size;
         float alpha = 0.85F * (1.0F - easeInQuad(t));
         int argb = withAlpha(baseRgb, alpha);
         poseStack.push();
         poseStack.translate(circle.doubleVal - cam.x, circle.doubleVal2 + circle.yLift * 0.5F + 0.004F - cam.y, circle.doubleVal3 - cam.z);
         emitQuad(poseStack.peek(), buffer, half, argb);
         poseStack.pop();
      }
   }

   private static int currentBaseRgb(JumpCirclesModule module) {
      if (module.rainbow.get()) {
         float hue = (float)(System.currentTimeMillis() % 4000L) / 4000.0F;
         return MathHelper.hsvToRgb(hue, 0.75F, 1.0F) & 16777215;
      } else {
         return module.baseRgb();
      }
   }

   private static void emitQuad(Entry pose, VertexConsumer buffer, float half, int argb) {
      vertex(pose, buffer, -half, -half, 0.0F, 0.0F, argb);
      vertex(pose, buffer, -half, half, 0.0F, 1.0F, argb);
      vertex(pose, buffer, half, half, 1.0F, 1.0F, argb);
      vertex(pose, buffer, half, -half, 1.0F, 0.0F, argb);
   }

   private static void vertex(Entry pose, VertexConsumer buffer, float x, float z, float u, float v, int argb) {
      buffer.vertex(pose, x, 0.0F, z).color(argb).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(pose, 0.0F, 1.0F, 0.0F);
   }

   private static int withAlpha(int rgb, float alpha) {
      int a = (int)(clamp01(alpha) * 255.0F);
      return a << 24 | rgb & 16777215;
   }

   private static int lerpRgb(int from, int to, float t) {
      int r = (int)MathHelper.lerp(t, from >> 16 & 0xFF, to >> 16 & 0xFF);
      int g = (int)MathHelper.lerp(t, from >> 8 & 0xFF, to >> 8 & 0xFF);
      int b = (int)MathHelper.lerp(t, from & 0xFF, to & 0xFF);
      return r << 16 | g << 8 | b;
   }

   private static float clamp01(float t) {
      return t < 0.0F ? 0.0F : (t > 1.0F ? 1.0F : t);
   }

   private static float lerp(float t, float a, float b) {
      return a + (b - a) * t;
   }

   private static float easeOutCubic(float t) {
      t = clamp01(t);
      float inv = 1.0F - t;
      return 1.0F - inv * inv * inv;
   }

   private static float easeInQuad(float t) {
      t = clamp01(t);
      return t * t;
   }

   private static float easeOutBack(float t) {
      t = clamp01(t);
      float c3 = 2.2F + 1.0F;
      float u = t - 1.0F;
      return 1.0F + c3 * u * u * u + 2.2F * u * u;
   }
}


