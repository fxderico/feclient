package dev.fede.render;

import dev.fede.module.impl.BlockEntityEspModule;
import java.util.Collection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3fc;

public final class BlockEntityEspRenderer {
   private static final double INSET = 0.002;
   private static final float TRACER_WIDTH = 1.2F;

   private BlockEntityEspRenderer() {
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, BlockEntityEspModule module) {
      Collection<BlockEntityEspModule.Cached> snapshot = module.entries();
      if (!snapshot.isEmpty()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         ClientWorld level = mc.world;
         if (level != null) {
            boolean fill = module.mode.check("Full");
            int alpha = Math.clamp((long)module.highlightAlpha.getInt(), 0, 255);
            boolean showGhosts = module.showGhosts.get();
            boolean tracers = module.tracers.get();
            int ghostTint = module.ghostTint.get();
            Vector3fc forward = tracers ? mc.gameRenderer.getCamera().getHorizontalPlane() : null;

            for (BlockEntityEspModule.Cached entry : snapshot) {
               String key = entry.typeKey();
               if (module.blockEntities.isEnabled(key)) {
                  BlockPos p = entry.pos();
                  boolean ghost = level.getBlockEntity(p) == null;
                  if (!ghost || showGhosts) {
                     int rgb = tint(module.blockEntities.color(key) & 16777215, ghost, ghostTint);
                     int argb = rgb | alpha << 24;
                     double x0 = p.getX();
                     double y0 = p.getY();
                     double z0 = p.getZ();
                     double x1 = x0 + 1.0;
                     double y1 = y0 + 1.0;
                     double z1 = z0 + 1.0;
                     if (fill) {
                        EspBoxRenderer.fill(bufferSource, poseStack, cam, x0 - 0.002, y0 - 0.002, z0 - 0.002, x1 + 0.002, y1 + 0.002, z1 + 0.002, argb);
                     } else {
                        EspBoxRenderer.outline(bufferSource, poseStack, cam, x0, y0, z0, x1, y1, z1, argb, 1.6F);
                     }

                     if (tracers) {
                        int tracerColor = rgb | Math.max(alpha, 160) << 24;
                        EspBoxRenderer.tracer(bufferSource, poseStack, cam, forward, x0 + 0.5, y0 + 0.5, z0 + 0.5, tracerColor, 1.2F);
                     }
                  }
               }
            }

            EspBoxRenderer.flush(bufferSource);
         }
      }
   }

   private static int tint(int rgb, boolean ghost, int ghostTintArgb) {
      if (!ghost) {
         return rgb;
      } else {
         int r = rgb >> 16 & 0xFF;
         int g = rgb >> 8 & 0xFF;
         int b = rgb & 0xFF;
         int tr = ghostTintArgb >> 16 & 0xFF;
         int tg = ghostTintArgb >> 8 & 0xFF;
         int tb = ghostTintArgb & 0xFF;
         return (r + tr) / 2 << 16 | (g + tg) / 2 << 8 | (b + tb) / 2;
      }
   }
}

