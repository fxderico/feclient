package dev.fede.render;

import dev.fede.module.Modules;
import dev.fede.util.Colors;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class BlockOutlineRenderer {
   private BlockOutlineRenderer() {
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, OutlineRenderState state, Vec3d camera, Modules.BlockOutlineModule module) {
      double dx = state.pos().getX() - camera.x;
      double dy = state.pos().getY() - camera.y;
      double dz = state.pos().getZ() - camera.z;
      double seconds = System.nanoTime() % 1000000000000L / 1.E9;
      int color = animatedColor(module, seconds);
      float pulse = pulseFactor(module, seconds);
      float fill = module.fillOpacity.getFloat() / 100.0F;
      if (fill > 0.004F) {
         VertexConsumer quads = bufferSource.getBuffer(RenderLayers.debugQuads());
         int fillColor = Colors.withAlpha(color, fill * (0.75F + 0.25F * pulse));

         for (Box box : state.shape().getBoundingBoxes()) {
            emitBox(poseStack, quads, box.expand(-0.002).offset(dx, dy, dz), fillColor);
         }

         bufferSource.drawCurrentLayer();
      }

      float thickness = module.thickness.getFloat();
      float glow = module.glow.getFloat() / 100.0F;
      if (glow > 0.02F) {
         VertexConsumer halo = bufferSource.getBuffer(RenderLayers.secondaryBlockOutline());
         VertexRendering.drawOutline(poseStack, halo, state.shape(), dx, dy, dz, Colors.withAlpha(color, (0.16F + 0.22F * pulse) * glow), thickness * 3.2F);
         bufferSource.drawCurrentLayer();
         VertexConsumer mid = bufferSource.getBuffer(RenderLayers.lines());
         VertexRendering.drawOutline(poseStack, mid, state.shape(), dx, dy, dz, Colors.withAlpha(color, (0.3F + 0.25F * pulse) * glow), thickness * 2.0F);
         bufferSource.drawCurrentLayer();
      }

      VertexConsumer core = bufferSource.getBuffer(RenderLayers.lines());
      VertexRendering.drawOutline(poseStack, core, state.shape(), dx, dy, dz, Colors.withAlpha(color, 0.85F + 0.15F * pulse), thickness);
      bufferSource.drawCurrentLayer();
   }

   private static int animatedColor(Modules.BlockOutlineModule module, double seconds) {
      if (module.rainbow.get()) {
         return Colors.hsvToRgb((float)(seconds * 42.0 % 360.0), 0.75F, 1.0F);
      } else {
         int base = module.color.get() | 0xFF000000;
         if (module.animation.check("Gradient Flow")) {
            float[] hsv = Colors.rgbToHsv(base);
            float hue = (hsv[0] + (float)(Math.sin(seconds * 1.6) * 28.0)) % 360.0F;
            if (hue < 0.0F) {
               hue += 360.0F;
            }

            return Colors.hsvToRgb(hue, Math.max(0.4F, hsv[1]), hsv[2]);
         } else {
            return base;
         }
      }
   }

   private static float pulseFactor(Modules.BlockOutlineModule module, double seconds) {
      return module.animation.check("Pulse") ? (float)(0.5 + 0.5 * Math.sin(seconds * Math.PI * 2.0 / 1.6)) : 1.0F;
   }

   private static void emitBox(MatrixStack poseStack, VertexConsumer buffer, Box b, int color) {
      Entry pose = poseStack.peek();
      float x0 = (float)b.minX;
      float y0 = (float)b.minY;
      float z0 = (float)b.minZ;
      float x1 = (float)b.maxX;
      float y1 = (float)b.maxY;
      float z1 = (float)b.maxZ;
      quad(buffer, pose, color, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1);
      quad(buffer, pose, color, x0, y1, z0, x0, y1, z1, x1, y1, z1, x1, y1, z0);
      quad(buffer, pose, color, x0, y0, z0, x0, y1, z0, x1, y1, z0, x1, y0, z0);
      quad(buffer, pose, color, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1);
      quad(buffer, pose, color, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0);
      quad(buffer, pose, color, x1, y0, z0, x1, y1, z0, x1, y1, z1, x1, y0, z1);
   }

   private static void quad(
      VertexConsumer buffer,
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
      float dx2,
      float dy2,
      float dz2
   ) {
      buffer.vertex(pose, ax, ay, az).color(color);
      buffer.vertex(pose, bx, by, bz).color(color);
      buffer.vertex(pose, cx, cy, cz).color(color);
      buffer.vertex(pose, dx2, dy2, dz2).color(color);
   }
}

