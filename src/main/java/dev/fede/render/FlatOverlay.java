package dev.fede.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import dev.fede.util.Colors;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

public final class FlatOverlay {
   private static final RenderPipeline FILL_PIPELINE = RenderPipeline.builder(new Snippet[]{RenderPipelines.POSITION_COLOR_SNIPPET})
      .withLocation("feclient/pipeline/flat_fill")
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .build();
   public static final RenderLayer FILL = RenderLayer.of("feclient:flat_fill", RenderSetup.builder(FILL_PIPELINE).translucent().build());
   private static final RenderPipeline LINE_PIPELINE = RenderPipeline.builder(new Snippet[]{RenderPipelines.RENDERTYPE_LINES_SNIPPET})
      .withLocation("feclient/pipeline/flat_lines")
      .withDepthWrite(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .build();
   public static final RenderLayer LINES = RenderLayer.of("feclient:flat_lines", RenderSetup.builder(LINE_PIPELINE).build());

   private FlatOverlay() {
   }

   public static void fillQuad(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, double x0, double z0, double x1, double z1, double y, int color) {
      VertexConsumer buffer = bufferSource.getBuffer(FILL);
      Entry pose = poseStack.peek();
      float ax = (float)(x0 - cam.x);
      float az = (float)(z0 - cam.z);
      float bx = (float)(x1 - cam.x);
      float bz = (float)(z1 - cam.z);
      float fy = (float)(y - cam.y);
      buffer.vertex(pose, ax, fy, az).color(color);
      buffer.vertex(pose, ax, fy, bz).color(color);
      buffer.vertex(pose, bx, fy, bz).color(color);
      buffer.vertex(pose, bx, fy, az).color(color);
   }

   public static void edge(
      Immediate bufferSource, MatrixStack poseStack, Vec3d cam, double x0, double z0, double x1, double z1, double y, int color, float width
   ) {
      VertexConsumer buffer = bufferSource.getBuffer(LINES);
      Entry pose = poseStack.peek();
      Vector3f normal = new Vector3f((float)(x1 - x0), 0.0F, (float)(z1 - z0)).normalize();
      buffer.vertex(pose, (float)(x0 - cam.x), (float)(y - cam.y), (float)(z0 - cam.z)).color(color).normal(pose, normal).lineWidth(width);
      buffer.vertex(pose, (float)(x1 - cam.x), (float)(y - cam.y), (float)(z1 - cam.z)).color(color).normal(pose, normal).lineWidth(width);
   }

   public static void marker(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, double x, double z, double y, double r, int color) {
      fillQuadRot(bufferSource, poseStack, cam, x, z, y, r, color);
      int outline = Colors.withAlpha(color, 1.0F);
      edge(bufferSource, poseStack, cam, x - r, z, x, z - r, y, outline, 2.0F);
      edge(bufferSource, poseStack, cam, x, z - r, x + r, z, y, outline, 2.0F);
      edge(bufferSource, poseStack, cam, x + r, z, x, z + r, y, outline, 2.0F);
      edge(bufferSource, poseStack, cam, x, z + r, x - r, z, y, outline, 2.0F);
   }

   private static void fillQuadRot(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, double cx, double cz, double y, double r, int color) {
      VertexConsumer buffer = bufferSource.getBuffer(FILL);
      Entry pose = poseStack.peek();
      float fy = (float)(y - cam.y);
      buffer.vertex(pose, (float)(cx - r - cam.x), fy, (float)(cz - cam.z)).color(color);
      buffer.vertex(pose, (float)(cx - cam.x), fy, (float)(cz - r - cam.z)).color(color);
      buffer.vertex(pose, (float)(cx + r - cam.x), fy, (float)(cz - cam.z)).color(color);
      buffer.vertex(pose, (float)(cx - cam.x), fy, (float)(cz + r - cam.z)).color(color);
   }

   public static void flush(Immediate bufferSource) {
      bufferSource.draw(FILL);
      bufferSource.draw(LINES);
   }
}


