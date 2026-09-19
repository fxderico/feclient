package dev.fede.render;

import dev.fede.module.impl.HitParticlesModule;
import dev.fede.util.Colors;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public final class HitParticleRenderer {
   private static final Identifier TEXTURE_LOGO = Identifier.of("feclient", "textures/misc/fe_logo.png");
   private static final int HEART_SEGMENTS = 20;
   private static final float[] HEART_X = new float[21];
   private static final float[] HEART_Y = new float[21];
   private static final float SHOCK_TIME = 0.5F;

   private HitParticleRenderer() {
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, HitParticlesModule module) {
      Deque<HitParticlesModule.HitParticle> particles = module.particles();
      Deque<HitParticlesModule.Shock> shocks = module.shocks();
      if (!particles.isEmpty() || !shocks.isEmpty()) {
         long now = System.nanoTime();

         while (!particles.isEmpty() && particles.peekFirst().ageSeconds(now) > particles.peekFirst().lifetime) {
            particles.removeFirst();
         }

         while (!shocks.isEmpty() && shocks.peekFirst().ageSeconds(now) > 0.5F) {
            shocks.removeFirst();
         }

         if (!particles.isEmpty() || !shocks.isEmpty()) {
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            Vector3f fwd = new Vector3f(camera.getHorizontalPlane());
            Vector3f right = new Vector3f();
            fwd.cross(new Vector3f(0.0F, 1.0F, 0.0F), right);
            if (right.lengthSquared() < 1.0E-6F) {
               right.set(1.0F, 0.0F, 0.0F);
            }

            right.normalize();
            Vector3f up = new Vector3f();
            right.cross(fwd, up);
            up.normalize();
            float glow = module.glowStrength();
            Entry pose = poseStack.peek();
            VertexConsumer lines = bufferSource.getBuffer(FlatOverlay.LINES);

            for (HitParticlesModule.Shock shock : shocks) {
               renderShock(lines, pose, cam, right, up, shock, now);
            }

            boolean any67 = false;

            for (HitParticlesModule.HitParticle p : particles) {
               float age = p.ageSeconds(now);
               if (!(age < 0.0F) && !(age > p.lifetime)) {
                  if (p.styleId == 2) {
                     renderLightning(lines, pose, cam, right, up, p, age);
                  } else if (p.styleId == 3) {
                     any67 = true;
                  }
               }
            }

            VertexConsumer fill = bufferSource.getBuffer(FlatOverlay.FILL);

            for (HitParticlesModule.HitParticle px : particles) {
               float age = px.ageSeconds(now);
               if (!(age < 0.0F) && !(age > px.lifetime)) {
                  if (px.styleId == 1) {
                     renderHeart(fill, pose, cam, right, up, px, age, glow);
                  } else if (px.styleId == 0) {
                     renderSpark(fill, pose, cam, right, up, px, age, glow);
                  }
               }
            }

            FlatOverlay.flush(bufferSource);
            if (any67) {
               VertexConsumer glyphs = bufferSource.getBuffer(RenderLayers.entityTranslucentEmissive(TEXTURE_LOGO));

               for (HitParticlesModule.HitParticle pxx : particles) {
                  if (pxx.styleId == 3) {
                     float age = pxx.ageSeconds(now);
                     if (!(age < 0.0F) && !(age > pxx.lifetime)) {
                        render67(glyphs, pose, cam, right, up, fwd, pxx, age);
                     }
                  }
               }
            }
         }
      }
   }

   private static void renderSpark(
      VertexConsumer buf, Entry pose, Vec3d cam, Vector3f right, Vector3f up, HitParticlesModule.HitParticle p, float age, float glow
   ) {
      float t = age / p.lifetime;
      float alpha = fadeAlpha(t);
      if (!(alpha <= 0.01F)) {
         float bx = (float)(p.doubleOf(age) - cam.x);
         float by = (float)(p.doubleOf2(age) - cam.y);
         float bz = (float)(p.doubleOf3(age) - cam.z);
         float vy = p.floatVal2 - p.gravity * age;
         float sr = p.floatVal * right.x + vy * right.y + p.floatVal3 * right.z;
         float su = p.floatVal * up.x + vy * up.y + p.floatVal3 * up.z;
         float slen = MathHelper.sqrt(sr * sr + su * su);
         float dirR;
         float dirU;
         if (slen > 1.0E-4F) {
            dirR = sr / slen;
            dirU = su / slen;
         } else {
            dirR = 0.0F;
            dirU = 1.0F;
         }

         float shrink = 0.35F + 0.65F * (1.0F - t);
         float streak = 0.28F * p.size * shrink;
         float width = 0.055F * p.size * shrink;
         Vector3f lAx = axis(right, up, dirR * streak, dirU * streak);
         Vector3f sAx = axis(right, up, -dirU * width, dirR * width);
         int core = Colors.withAlpha(Colors.lighten(p.rgb, 0.55F), alpha);
         quad(buf, pose, bx, by, bz, lAx, sAx, core);
         if (glow > 0.01F) {
            float halo = 0.06F * p.size * shrink * (1.0F + 0.6F * glow);
            Vector3f hA = axis(right, up, halo, halo);
            Vector3f hB = axis(right, up, -halo, halo);
            int haloColor = Colors.withAlpha(p.rgb, alpha * 0.22F * glow);
            quad(buf, pose, bx, by, bz, hA, hB, haloColor);
         }
      }
   }

   private static void renderHeart(
      VertexConsumer buf, Entry pose, Vec3d cam, Vector3f right, Vector3f up, HitParticlesModule.HitParticle p, float age, float glow
   ) {
      float t = age / p.lifetime;
      float alpha = fadeAlpha(t);
      if (!(alpha <= 0.01F)) {
         float pop = t < 0.2F ? easeOutBack(t / 0.2F) : 1.0F;
         float scale = 0.16F * p.size * pop;
         float bx = (float)(p.doubleOf(age) - cam.x);
         float by = (float)(p.doubleOf2(age) - cam.y);
         float bz = (float)(p.doubleOf3(age) - cam.z);
         float wobble = 0.18F * MathHelper.sin(age * 6.0F + p.rot);
         int core = Colors.withAlpha(Colors.lighten(p.rgb, 0.25F), alpha);
         fanHeart(buf, pose, right, up, bx, by, bz, scale, wobble, core);
         if (glow > 0.01F) {
            int haloColor = Colors.withAlpha(p.rgb, alpha * 0.3F * glow);
            fanHeart(buf, pose, right, up, bx, by, bz, scale * (1.35F + 0.35F * glow), wobble, haloColor);
         }
      }
   }

   private static void fanHeart(VertexConsumer buf, Entry pose, Vector3f right, Vector3f up, float bx, float by, float bz, float scale, float shearX, int argb) {
      for (int i = 0; i < 20; i++) {
         float ax0 = (HEART_X[i] + shearX * HEART_Y[i]) * scale;
         float ay0 = HEART_Y[i] * scale;
         float ax1 = (HEART_X[i + 1] + shearX * HEART_Y[i + 1]) * scale;
         float ay1 = HEART_Y[i + 1] * scale;
         run(buf, pose, bx, by, bz, argb);
         run(buf, pose, bx + right.x * ax0 + up.x * ay0, by + right.y * ax0 + up.y * ay0, bz + right.z * ax0 + up.z * ay0, argb);
         run(buf, pose, bx + right.x * ax1 + up.x * ay1, by + right.y * ax1 + up.y * ay1, bz + right.z * ax1 + up.z * ay1, argb);
         run(buf, pose, bx, by, bz, argb);
      }
   }

   private static void renderLightning(VertexConsumer buf, Entry pose, Vec3d cam, Vector3f right, Vector3f up, HitParticlesModule.HitParticle p, float age) {
      float t = age / p.lifetime;
      float alpha = fadeAlpha(t);
      if (!(alpha <= 0.01F)) {
         float flicker = 0.45F + 0.55F * MathHelper.abs(MathHelper.sin(age * 42.0F + p.rot));
         alpha *= flicker;
         float sx = (float)(p.doubleVal - cam.x);
         float sy = (float)(p.doubleVal2 - cam.y);
         float sz = (float)(p.doubleVal3 - cam.z);
         float ex = (float)(p.doubleOf(age) - cam.x);
         float ey = (float)(p.doubleOf2(age) - cam.y);
         float ez = (float)(p.doubleOf3(age) - cam.z);
         float dx = ex - sx;
         float dy = ey - sy;
         float dz = ez - sz;
         float dr = dx * right.x + dy * right.y + dz * right.z;
         float du = dx * up.x + dy * up.y + dz * up.z;
         float dlen = MathHelper.sqrt(dr * dr + du * du);
         float pr;
         float pu;
         if (dlen > 1.0E-4F) {
            pr = -du / dlen;
            pu = dr / dlen;
         } else {
            pr = 1.0F;
            pu = 0.0F;
         }

         float amp = 0.16F * p.size;
         int core = Colors.withAlpha(Colors.lighten(p.rgb, 0.6F), alpha);
         float lineW = 2.4F * p.size;
         float px = sx;
         float py = sy;
         float pz = sz;

         for (int i = 1; i <= 4; i++) {
            float f = (float)i / 4;
            float taper = MathHelper.sin(f * (float) Math.PI);
            float j = i == 4 ? 0.0F : (hash(p, i) * 2.0F - 1.0F) * amp * taper;
            float nx = sx + dx * f + (right.x * pr + up.x * pu) * j;
            float ny = sy + dy * f + (right.y * pr + up.y * pu) * j;
            float nz = sz + dz * f + (right.z * pr + up.z * pu) * j;
            line(buf, pose, px, py, pz, nx, ny, nz, core, lineW);
            px = nx;
            py = ny;
            pz = nz;
         }
      }
   }

   private static void render67(
      VertexConsumer buf, Entry pose, Vec3d cam, Vector3f right, Vector3f up, Vector3f fwd, HitParticlesModule.HitParticle p, float age
   ) {
      float t = age / p.lifetime;
      float alpha = fadeAlpha(t);
      if (!(alpha <= 0.01F)) {
         float pop = t < 0.22F ? easeOutBack(t / 0.22F) : 1.0F;
         float half = 0.16F * p.size * pop;
         float angle = p.rot + p.rotSpeed * age * 0.5F;
         float ca = MathHelper.cos(angle);
         float sa = MathHelper.sin(angle);
         Vector3f rAx = axis(right, up, ca * half, sa * half);
         Vector3f uAx = axis(right, up, -sa * half, ca * half);
         float bx = (float)(p.doubleOf(age) - cam.x);
         float by = (float)(p.doubleOf2(age) - cam.y);
         float bz = (float)(p.doubleOf3(age) - cam.z);
         int argb = Colors.withAlpha(p.rgb, alpha);
         float nx = -fwd.x;
         float ny = -fwd.y;
         float nz = -fwd.z;
         texVertex(buf, pose, bx - rAx.x - uAx.x, by - rAx.y - uAx.y, bz - rAx.z - uAx.z, 0.0F, 0.0F, argb, nx, ny, nz);
         texVertex(buf, pose, bx - rAx.x + uAx.x, by - rAx.y + uAx.y, bz - rAx.z + uAx.z, 0.0F, 1.0F, argb, nx, ny, nz);
         texVertex(buf, pose, bx + rAx.x + uAx.x, by + rAx.y + uAx.y, bz + rAx.z + uAx.z, 1.0F, 1.0F, argb, nx, ny, nz);
         texVertex(buf, pose, bx + rAx.x - uAx.x, by + rAx.y - uAx.y, bz + rAx.z - uAx.z, 1.0F, 0.0F, argb, nx, ny, nz);
      }
   }

   private static void renderShock(VertexConsumer buf, Entry pose, Vec3d cam, Vector3f right, Vector3f up, HitParticlesModule.Shock shock, long now) {
      float age = shock.ageSeconds(now);
      if (!(age < 0.0F) && !(age >= 0.5F)) {
         float t = age / 0.5F;
         float radius = 0.15F + easeOutCubic(t) * 1.15F;
         float alpha = 0.8F * (1.0F - easeInQuad(t));
         int color = Colors.withAlpha(Colors.lighten(shock.rgb, 0.2F), alpha);
         float bx = (float)(shock.doubleVal - cam.x);
         float by = (float)(shock.doubleVal2 - cam.y);
         float bz = (float)(shock.doubleVal3 - cam.z);
         float prevX = 0.0F;
         float prevY = 0.0F;
         float prevZ = 0.0F;

         for (int i = 0; i <= 28; i++) {
            float a = (float)i / 28 * (float) (Math.PI * 2);
            float ox = MathHelper.cos(a) * radius;
            float oy = MathHelper.sin(a) * radius;
            float x = bx + right.x * ox + up.x * oy;
            float y = by + right.y * ox + up.y * oy;
            float z = bz + right.z * ox + up.z * oy;
            if (i > 0) {
               line(buf, pose, prevX, prevY, prevZ, x, y, z, color, 2.2F);
            }

            prevX = x;
            prevY = y;
            prevZ = z;
         }
      }
   }

   private static Vector3f axis(Vector3f right, Vector3f up, float a, float b) {
      return new Vector3f(right.x * a + up.x * b, right.y * a + up.y * b, right.z * a + up.z * b);
   }

   private static void quad(VertexConsumer buf, Entry pose, float bx, float by, float bz, Vector3f ax1, Vector3f ax2, int argb) {
      run(buf, pose, bx - ax1.x - ax2.x, by - ax1.y - ax2.y, bz - ax1.z - ax2.z, argb);
      run(buf, pose, bx + ax1.x - ax2.x, by + ax1.y - ax2.y, bz + ax1.z - ax2.z, argb);
      run(buf, pose, bx + ax1.x + ax2.x, by + ax1.y + ax2.y, bz + ax1.z + ax2.z, argb);
      run(buf, pose, bx - ax1.x + ax2.x, by - ax1.y + ax2.y, bz - ax1.z + ax2.z, argb);
   }

   private static void run(VertexConsumer buf, Entry pose, float x, float y, float z, int argb) {
      buf.vertex(pose, x, y, z).color(argb);
   }

   private static void line(VertexConsumer buf, Entry pose, float x1, float y1, float z1, float x2, float y2, float z2, int argb, float width) {
      Vector3f n = new Vector3f(x2 - x1, y2 - y1, z2 - z1);
      if (n.lengthSquared() > 1.0E-9F) {
         n.normalize();
      } else {
         n.set(0.0F, 1.0F, 0.0F);
      }

      buf.vertex(pose, x1, y1, z1).color(argb).normal(pose, n).lineWidth(width);
      buf.vertex(pose, x2, y2, z2).color(argb).normal(pose, n).lineWidth(width);
   }

   private static void texVertex(VertexConsumer buf, Entry pose, float x, float y, float z, float u, float vv, int argb, float nx, float ny, float nz) {
      buf.vertex(pose, x, y, z).color(argb).texture(u, vv).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(pose, nx, ny, nz);
   }

   private static float hash(HitParticlesModule.HitParticle p, int salt) {
      float s = MathHelper.sin((float)(p.doubleVal * 12.9898 + p.doubleVal3 * 78.233 + p.rot * 3.17 + salt * 43.123)) * 43758.547F;
      return s - MathHelper.floor(s);
   }

   private static float fadeAlpha(float t) {
      t = clamp01(t);
      return t < 0.12F ? t / 0.12F : 1.0F - easeInQuad((t - 0.12F) / 0.88F);
   }

   private static float clamp01(float t) {
      return t < 0.0F ? 0.0F : (t > 1.0F ? 1.0F : t);
   }

   private static float easeInQuad(float t) {
      t = clamp01(t);
      return t * t;
   }

   private static float easeOutCubic(float t) {
      t = clamp01(t);
      float inv = 1.0F - t;
      return 1.0F - inv * inv * inv;
   }

   private static float easeOutBack(float t) {
      t = clamp01(t);
      float c3 = 2.4F + 1.0F;
      float u = t - 1.0F;
      return 1.0F + c3 * u * u * u + 2.4F * u * u;
   }

   static {
      for (int i = 0; i <= 20; i++) {
         double t = i / 20.0 * Math.PI * 2.0;
         double hx = 16.0 * Math.pow(Math.sin(t), 3.0);
         double hy = 13.0 * Math.cos(t) - 5.0 * Math.cos(2.0 * t) - 2.0 * Math.cos(3.0 * t) - Math.cos(4.0 * t);
         HEART_X[i] = (float)(hx / 17.0);
         HEART_Y[i] = (float)(hy / 17.0);
      }
   }
}


