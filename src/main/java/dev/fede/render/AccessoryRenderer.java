package dev.fede.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import dev.fede.module.impl.CustomAccessoriesModule;
import dev.fede.util.Colors;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public final class AccessoryRenderer {
   private static final Identifier TEXTURE_LOGO = Identifier.of("feclient", "textures/misc/fe_logo.png");
   private static final int CAPE_COLS = 7;
   private static final int CAPE_ROWS = 9;
   private static final float CAPE_WIDTH = 0.62F;
   private static final float CAPE_LENGTH = 1.05F;
   private static final RenderPipeline CAPE_FILL_PIPELINE = RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
      .withLocation("feclient/pipeline/cape_fill")
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
      .build();
   private static final RenderLayer CAPE_FILL = RenderLayer.of("feclient:cape_fill", RenderSetup.builder(CAPE_FILL_PIPELINE).translucent().build());

   private AccessoryRenderer() {
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, CustomAccessoriesModule module) {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.world != null) {
         boolean firstPersonHidden = mc.options.getPerspective().isFirstPerson() && !module.firstPerson.get();
         float pt = mc.getRenderTickCounter().getTickProgress(false);
         double px = MathHelper.lerp(pt, player.lastX, player.getX());
         double py = MathHelper.lerp(pt, player.lastY, player.getY());
         double pz = MathHelper.lerp(pt, player.lastZ, player.getZ());
         float bodyYaw = MathHelper.lerpAngleDegrees(pt, player.lastBodyYaw, player.bodyYaw);
         float height = player.getHeight();
         float speed = (float)Math.hypot(player.getX() - player.lastX, player.getZ() - player.lastZ);
         long now = System.nanoTime();
         float time = (float)(now % 1000000000000L) / 1.E9F;
         int rgb = module.currentRgb();
         float glow = module.glowStrength();
         Camera camera = mc.gameRenderer.getCamera();
         Vector3f fwd = new Vector3f(camera.getHorizontalPlane());
         Vector3f bRight = new Vector3f();
         fwd.cross(new Vector3f(0.0F, 1.0F, 0.0F), bRight);
         if (bRight.lengthSquared() < 1.0E-6F) {
            bRight.set(1.0F, 0.0F, 0.0F);
         }

         bRight.normalize();
         Vector3f bUp = new Vector3f();
         bRight.cross(fwd, bUp);
         bUp.normalize();
         Entry pose = poseStack.peek();
         boolean capeOn = module.cape.get() && !firstPersonHidden;
         boolean cape67 = capeOn && module.capeStyle.check("FE Logo");
         float[] capeX = null;
         float[] capeY = null;
         float[] capeZ = null;
         if (capeOn) {
            int n = 80;
            capeX = new float[n];
            capeY = new float[n];
            capeZ = new float[n];
            buildCape(capeX, capeY, capeZ, px, py, pz, height, bodyYaw, speed, time, module.capePhysics.get(), cam);
         }

         VertexConsumer lines = bufferSource.getBuffer(FlatOverlay.LINES);
         if (!firstPersonHidden && module.trail.get() && module.trailStyle.check("Echo")) {
            renderTrailEcho(lines, pose, cam, module, now, rgb);
         }

         if (module.aura.get() && module.auraStyle.check("Ring")) {
            renderAuraRing(lines, pose, cam, px, py, pz, time, rgb, glow);
         }

         if (capeOn && !cape67) {
            VertexConsumer capeFill = bufferSource.getBuffer(CAPE_FILL);
            fillCape(capeFill, pose, capeX, capeY, capeZ, module.capeStyle.get(), time, rgb);
            bufferSource.draw(CAPE_FILL);
         }

         VertexConsumer fill = bufferSource.getBuffer(FlatOverlay.FILL);
         if (!firstPersonHidden && module.trail.get() && module.trailStyle.check("Ribbon")) {
            renderTrailRibbon(fill, pose, cam, bRight, module, now, rgb, glow);
         }

         if (!firstPersonHidden && module.trail.get() && module.trailStyle.check("Sparkle")) {
            renderTrailSparkle(fill, pose, cam, bRight, bUp, module, now, rgb, glow);
         }

         if (module.aura.get() && module.auraStyle.check("Orbit")) {
            renderAuraOrbit(fill, pose, cam, bRight, bUp, px, py, pz, time, rgb, glow);
         }

         FlatOverlay.flush(bufferSource);
         boolean crownOn = module.crown.get() && !firstPersonHidden;
         if (cape67 || crownOn) {
            VertexConsumer glyphs = bufferSource.getBuffer(RenderLayers.entityTranslucentEmissive(TEXTURE_LOGO));
            if (cape67) {
               texCape(glyphs, pose, capeX, capeY, capeZ, fwd, rgb);
            }

            if (crownOn) {
               renderCrown(glyphs, pose, cam, bRight, bUp, fwd, px, py, pz, height, time, rgb);
            }
         }
      }
   }

   private static void buildCape(
      float[] gx, float[] gy, float[] gz, double px, double py, double pz, float height, float bodyYaw, float speed, float time, boolean physics, Vec3d cam
   ) {
      float yawRad = bodyYaw * (float) (Math.PI / 180.0);
      float fx = -MathHelper.sin(yawRad);
      float fz = MathHelper.cos(yawRad);
      float rx = fz;
      float rz = -fx;
      float backX = -fx;
      float backZ = -fz;
      float shoulderY = (float)py + height * 0.78F;
      float ax = (float)px + backX * 0.14F;
      float az = (float)pz + backZ * 0.14F;
      float speedLean = physics ? Math.min(speed * 5.0F, 1.05F) : 0.0F;
      float waveAmp = physics ? 0.06F + speedLean * 0.14F : 0.02F;

      for (int r = 0; r <= 9; r++) {
         float fr = r / 9.0F;
         float droop = fr * fr;
         float back = (0.1F + speedLean) * droop;

         for (int c = 0; c <= 7; c++) {
            float u = c / 7.0F;
            float across = (u - 0.5F) * 0.62F;
            float wave = physics ? MathHelper.sin(time * 6.5F - fr * 4.2F + c * 0.7F) * waveAmp * fr : 0.0F;
            float shimmy = physics ? MathHelper.sin(time * 5.0F + fr * 3.5F) * 0.03F * fr : 0.0F;
            float b = back + wave;
            int i = r * 8 + c;
            gx[i] = ax + rx * (across + shimmy) + backX * b - (float)cam.x;
            gy[i] = shoulderY - fr * 1.05F - (float)cam.y;
            gz[i] = az + rz * (across + shimmy) + backZ * b - (float)cam.z;
         }
      }
   }

   private static void fillCape(VertexConsumer buf, Entry pose, float[] gx, float[] gy, float[] gz, String style, float time, int rgb) {
      boolean grid = style.equals("Grid");
      boolean wave = style.equals("Wave");
      int top = Colors.lighten(rgb, 0.22F);
      int bottom = darkenRgb(rgb, 0.18F);

      for (int r = 0; r < 9; r++) {
         for (int c = 0; c < 7; c++) {
            int i00 = r * 8 + c;
            int i10 = i00 + 1;
            int i01 = i00 + 8;
            int i11 = i01 + 1;
            if (grid) {
               boolean lit = (r + c & 1) == 0;
               int cell = lit ? Colors.lighten(rgb, 0.35F) : darkenRgb(rgb, 0.5F);
               int a = lit ? withA(cell, 0.9F) : withA(cell, 0.6F);
               run(buf, pose, gx[i00], gy[i00], gz[i00], a);
               run(buf, pose, gx[i01], gy[i01], gz[i01], a);
               run(buf, pose, gx[i11], gy[i11], gz[i11], a);
               run(buf, pose, gx[i10], gy[i10], gz[i10], a);
            } else {
               float f0 = r / 9.0F;
               float f1 = (r + 1) / 9.0F;
               int c0 = lerpRgb(top, bottom, f0);
               int c1 = lerpRgb(top, bottom, f1);
               if (wave) {
                  c0 = lerpRgb(c0, 16777215, highlight(f0, time));
                  c1 = lerpRgb(c1, 16777215, highlight(f1, time));
               }

               int a0 = withA(c0, 0.86F - 0.08F * f0);
               int a1 = withA(c1, 0.86F - 0.08F * f1);
               run(buf, pose, gx[i00], gy[i00], gz[i00], a0);
               run(buf, pose, gx[i01], gy[i01], gz[i01], a1);
               run(buf, pose, gx[i11], gy[i11], gz[i11], a1);
               run(buf, pose, gx[i10], gy[i10], gz[i10], a0);
            }
         }
      }
   }

   private static void texCape(VertexConsumer buf, Entry pose, float[] gx, float[] gy, float[] gz, Vector3f fwd, int rgb) {
      int argb = withA(rgb, 0.98F);
      float nx = -fwd.x;
      float ny = -fwd.y;
      float nz = -fwd.z;

      for (int r = 0; r < 9; r++) {
         float v0 = r / 9.0F;
         float v1 = (r + 1) / 9.0F;

         for (int c = 0; c < 7; c++) {
            float u0 = c / 7.0F;
            float u1 = (c + 1) / 7.0F;
            int i00 = r * 8 + c;
            int i10 = i00 + 1;
            int i01 = i00 + 8;
            int i11 = i01 + 1;
            tex(buf, pose, gx[i00], gy[i00], gz[i00], u0, v0, argb, nx, ny, nz);
            tex(buf, pose, gx[i01], gy[i01], gz[i01], u0, v1, argb, nx, ny, nz);
            tex(buf, pose, gx[i11], gy[i11], gz[i11], u1, v1, argb, nx, ny, nz);
            tex(buf, pose, gx[i10], gy[i10], gz[i10], u1, v0, argb, nx, ny, nz);
         }
      }
   }

   private static float highlight(float fr, float time) {
      float band = MathHelper.sin((fr - time * 0.35F % 1.0F) * (float) (Math.PI * 2));
      return Math.max(0.0F, band) * 0.5F;
   }

   private static void renderTrailRibbon(
      VertexConsumer buf, Entry pose, Vec3d cam, Vector3f right, CustomAccessoriesModule module, long now, int rgb, float glow
   ) {
      Deque<CustomAccessoriesModule.TrailNode> nodes = module.trailNodes();
      if (nodes.size() >= 2) {
         float life = Math.max(0.2F, module.trailLength.getFloat());
         float maxHalf = 0.28F * (0.7F + 0.6F * glow);
         CustomAccessoriesModule.TrailNode prev = null;
         float prevAlpha = 0.0F;
         float pLx = 0.0F;
         float pLy = 0.0F;
         float pLz = 0.0F;
         float pRx = 0.0F;
         float pRy = 0.0F;
         float pRz = 0.0F;

         for (CustomAccessoriesModule.TrailNode node : nodes) {
            float age = node.ageSeconds(now);
            if (age > life) {
               prev = null;
            } else {
               float t = age / life;
               float half = maxHalf * (1.0F - t);
               float alpha = (1.0F - t) * (1.0F - t);
               float bx = (float)(node.doubleVal - cam.x);
               float by = (float)(node.doubleVal2 - cam.y);
               float bz = (float)(node.doubleVal3 - cam.z);
               float lx = bx - right.x * half;
               float ly = by - right.y * half;
               float lz = bz - right.z * half;
               float rx = bx + right.x * half;
               float ry = by + right.y * half;
               float rz = bz + right.z * half;
               if (prev != null) {
                  int cOld = withA(rgb, prevAlpha * 0.85F);
                  int cNew = withA(Colors.lighten(rgb, 0.25F), alpha * 0.85F);
                  run(buf, pose, pLx, pLy, pLz, cOld);
                  run(buf, pose, lx, ly, lz, cNew);
                  run(buf, pose, rx, ry, rz, cNew);
                  run(buf, pose, pRx, pRy, pRz, cOld);
               }

               prev = node;
               prevAlpha = alpha;
               pLx = lx;
               pLy = ly;
               pLz = lz;
               pRx = rx;
               pRy = ry;
               pRz = rz;
            }
         }
      }
   }

   private static void renderTrailSparkle(
      VertexConsumer buf, Entry pose, Vec3d cam, Vector3f right, Vector3f up, CustomAccessoriesModule module, long now, int rgb, float glow
   ) {
      Deque<CustomAccessoriesModule.TrailNode> nodes = module.trailNodes();
      float life = Math.max(0.2F, module.trailLength.getFloat());
      int idx = 0;

      for (CustomAccessoriesModule.TrailNode node : nodes) {
         int i = idx++;
         if ((i & 1) != 1) {
            float age = node.ageSeconds(now);
            if (!(age > life)) {
               float t = age / life;
               float alpha = 1.0F - t;
               if (!(alpha <= 0.02F)) {
                  float pop = t < 0.15F ? t / 0.15F : 1.0F;
                  float sx = (hash(node.nanos, 1) - 0.5F) * 0.5F;
                  float sy = (hash(node.nanos, 2) - 0.5F) * 0.4F + t * 0.35F;
                  float sz = (hash(node.nanos, 3) - 0.5F) * 0.5F;
                  float bx = (float)(node.doubleVal - cam.x) + sx;
                  float by = (float)(node.doubleVal2 - cam.y) + sy;
                  float bz = (float)(node.doubleVal3 - cam.z) + sz;
                  float sz2 = 0.05F * pop * (0.7F + 0.6F * glow) * (0.6F + 0.8F * (1.0F - t));
                  int core = withA(Colors.lighten(rgb, 0.5F), alpha);
                  diamond(buf, pose, right, up, bx, by, bz, sz2, core);
                  if (glow > 0.01F) {
                     diamond(buf, pose, right, up, bx, by, bz, sz2 * 2.0F, withA(rgb, alpha * 0.25F * glow));
                  }
               }
            }
         }
      }
   }

   private static void renderTrailEcho(VertexConsumer buf, Entry pose, Vec3d cam, CustomAccessoriesModule module, long now, int rgb) {
      Deque<CustomAccessoriesModule.TrailNode> nodes = module.trailNodes();
      float life = Math.max(0.2F, module.trailLength.getFloat());
      int idx = 0;

      for (CustomAccessoriesModule.TrailNode node : nodes) {
         int i = idx++;
         if (i % 5 == 0) {
            float age = node.ageSeconds(now);
            if (!(age > life)) {
               float t = age / life;
               float alpha = (1.0F - t) * 0.8F;
               if (!(alpha <= 0.02F)) {
                  int color = withA(rgb, alpha);
                  float x0 = (float)(node.doubleVal - 0.32 - cam.x);
                  float x1 = (float)(node.doubleVal + 0.32 - cam.x);
                  float y0 = (float)(node.doubleVal2 - 0.9 - cam.y);
                  float y1 = (float)(node.doubleVal2 + 0.9 - cam.y);
                  float z0 = (float)(node.doubleVal3 - 0.32 - cam.z);
                  float z1 = (float)(node.doubleVal3 + 0.32 - cam.z);
                  box(buf, pose, x0, y0, z0, x1, y1, z1, color, 1.6F);
               }
            }
         }
      }
   }

   private static void box(VertexConsumer buf, Entry pose, float x0, float y0, float z0, float x1, float y1, float z1, int color, float w) {
      line(buf, pose, x0, y0, z0, x1, y0, z0, color, w);
      line(buf, pose, x1, y0, z0, x1, y0, z1, color, w);
      line(buf, pose, x1, y0, z1, x0, y0, z1, color, w);
      line(buf, pose, x0, y0, z1, x0, y0, z0, color, w);
      line(buf, pose, x0, y1, z0, x1, y1, z0, color, w);
      line(buf, pose, x1, y1, z0, x1, y1, z1, color, w);
      line(buf, pose, x1, y1, z1, x0, y1, z1, color, w);
      line(buf, pose, x0, y1, z1, x0, y1, z0, color, w);
      line(buf, pose, x0, y0, z0, x0, y1, z0, color, w);
      line(buf, pose, x1, y0, z0, x1, y1, z0, color, w);
      line(buf, pose, x1, y0, z1, x1, y1, z1, color, w);
      line(buf, pose, x0, y0, z1, x0, y1, z1, color, w);
   }

   private static void renderAuraOrbit(
      VertexConsumer buf, Entry pose, Vec3d cam, Vector3f right, Vector3f up, double px, double py, double pz, float time, int rgb, float glow
   ) {
      float baseY = (float)(py - cam.y) + 0.12F;
      float cxr = (float)(px - cam.x);
      float czr = (float)(pz - cam.z);

      for (int i = 0; i < 8; i++) {
         float a = time * 1.7F + i * ((float) (Math.PI * 2) / 8);
         float ox = MathHelper.cos(a) * 0.72F;
         float oz = MathHelper.sin(a) * 0.72F;
         float oy = baseY + MathHelper.sin(time * 2.4F + i) * 0.18F + 0.25F;
         float bx = cxr + ox;
         float bz = czr + oz;
         float sz = 0.07F * (0.75F + 0.5F * glow);
         int core = withA(Colors.lighten(rgb, 0.45F), 0.95F);
         diamond(buf, pose, right, up, bx, oy, bz, sz, core);
         if (glow > 0.01F) {
            diamond(buf, pose, right, up, bx, oy, bz, sz * 2.1F, withA(rgb, 0.22F * glow));
         }
      }
   }

   private static void renderAuraRing(VertexConsumer buf, Entry pose, Vec3d cam, double px, double py, double pz, float time, int rgb, float glow) {
      float cxr = (float)(px - cam.x);
      float czr = (float)(pz - cam.z);

      for (int ring = 0; ring < 2; ring++) {
         float phase = time * 0.9F + ring * 0.5F;
         float pulse = phase - (float)Math.floor(phase);
         float radius = 0.4F + pulse * 1.1F;
         float alpha = (1.0F - pulse) * (0.7F + 0.3F * glow);
         if (!(alpha <= 0.02F)) {
            int color = withA(Colors.lighten(rgb, 0.2F), alpha);
            float fy = (float)(py - cam.y) + 0.04F + ring * 0.02F;
            ring(buf, pose, cxr, fy, czr, radius, 40, color, 2.4F);
         }
      }
   }

   private static void renderCrown(
      VertexConsumer buf, Entry pose, Vec3d cam, Vector3f right, Vector3f up, Vector3f fwd, double px, double py, double pz, float height, float time, int rgb
   ) {
      float bob = MathHelper.sin(time * 2.0F) * 0.06F;
      float bx = (float)(px - cam.x);
      float by = (float)(py - cam.y) + height + 0.55F + bob;
      float bz = (float)(pz - cam.z);
      float angle = time * 1.4F;
      float ca = MathHelper.cos(angle);
      float sa = MathHelper.sin(angle);
      Vector3f rAx = axis(right, up, ca * 0.34F, sa * 0.34F);
      Vector3f uAx = axis(right, up, -sa * 0.34F, ca * 0.34F);
      int argb = withA(rgb, 0.98F);
      float nx = -fwd.x;
      float ny = -fwd.y;
      float nz = -fwd.z;
      tex(buf, pose, bx - rAx.x - uAx.x, by - rAx.y - uAx.y, bz - rAx.z - uAx.z, 0.0F, 0.0F, argb, nx, ny, nz);
      tex(buf, pose, bx - rAx.x + uAx.x, by - rAx.y + uAx.y, bz - rAx.z + uAx.z, 0.0F, 1.0F, argb, nx, ny, nz);
      tex(buf, pose, bx + rAx.x + uAx.x, by + rAx.y + uAx.y, bz + rAx.z + uAx.z, 1.0F, 1.0F, argb, nx, ny, nz);
      tex(buf, pose, bx + rAx.x - uAx.x, by + rAx.y - uAx.y, bz + rAx.z - uAx.z, 1.0F, 0.0F, argb, nx, ny, nz);
   }

   private static Vector3f axis(Vector3f right, Vector3f up, float a, float b) {
      return new Vector3f(right.x * a + up.x * b, right.y * a + up.y * b, right.z * a + up.z * b);
   }

   private static void diamond(VertexConsumer buf, Entry pose, Vector3f right, Vector3f up, float bx, float by, float bz, float r, int argb) {
      Vector3f a = axis(right, up, r, r);
      Vector3f b = axis(right, up, -r, r);
      run(buf, pose, bx - a.x, by - a.y, bz - a.z, argb);
      run(buf, pose, bx + b.x, by + b.y, bz + b.z, argb);
      run(buf, pose, bx + a.x, by + a.y, bz + a.z, argb);
      run(buf, pose, bx - b.x, by - b.y, bz - b.z, argb);
   }

   private static void ring(VertexConsumer buf, Entry pose, float cx, float fy, float cz, float radius, int segments, int color, float width) {
      float prevX = cx + radius;
      float prevZ = cz;

      for (int i = 1; i <= segments; i++) {
         float a = (float)i / segments * (float) (Math.PI * 2);
         float x = cx + MathHelper.cos(a) * radius;
         float z = cz + MathHelper.sin(a) * radius;
         line(buf, pose, prevX, fy, prevZ, x, fy, z, color, width);
         prevX = x;
         prevZ = z;
      }
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

   private static void tex(VertexConsumer buf, Entry pose, float x, float y, float z, float u, float vv, int argb, float nx, float ny, float nz) {
      buf.vertex(pose, x, y, z).color(argb).texture(u, vv).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(pose, nx, ny, nz);
   }

   private static int withA(int rgb, float alpha) {
      return Colors.withAlpha(rgb, alpha);
   }

   private static int lerpRgb(int from, int to, float t) {
      t = t < 0.0F ? 0.0F : (t > 1.0F ? 1.0F : t);
      int r = (int)MathHelper.lerp(t, from >> 16 & 0xFF, to >> 16 & 0xFF);
      int g = (int)MathHelper.lerp(t, from >> 8 & 0xFF, to >> 8 & 0xFF);
      int b = (int)MathHelper.lerp(t, from & 0xFF, to & 0xFF);
      return r << 16 | g << 8 | b;
   }

   private static int darkenRgb(int rgb, float t) {
      return lerpRgb(rgb, 0, t);
   }

   private static float hash(long seed, int salt) {
      float s = MathHelper.sin((float)(seed % 100000L) * 0.0131F + salt * 12.9898F) * 43758.547F;
      return s - MathHelper.floor(s);
   }
}


