package dev.fede.render;

import dev.fede.module.impl.SpawnerNametagsModule;
import dev.fede.util.Colors;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3fc;

public final class MiscBlockEspRenderer {
   private static final int SPAWNER_COLOR = -24576;
   private static final double SPAWNER_RANGE = 16.0;
   private static final float TRACER_WIDTH = 1.2F;

   private MiscBlockEspRenderer() {
   }

   public static void renderSpawners(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, SpawnerNametagsModule module) {
      List<BlockPos> snapshot = module.scan.get();
      if (!snapshot.isEmpty()) {
         boolean ring = module.rangeRing.get();
         boolean box = module.box.get();
         boolean tracers = module.tracers.get();
         if (ring || box || tracers) {
            Vector3fc forward = tracers ? MinecraftClient.getInstance().gameRenderer.getCamera().getHorizontalPlane() : null;

            for (BlockPos pos : snapshot) {
               if (box) {
                  EspBoxRenderer.outline(
                     bufferSource, poseStack, cam, pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, -24576, 2.0F
                  );
               }

               if (ring) {
                  EspBoxRenderer.ring(
                     bufferSource, poseStack, cam, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16.0, 48, Colors.withAlpha(-24576, 0.6F), 2.0F
                  );
               }

               if (tracers) {
                  EspBoxRenderer.tracer(
                     bufferSource, poseStack, cam, forward, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Colors.withAlpha(-24576, 0.7F), 1.2F
                  );
               }
            }

            EspBoxRenderer.flush(bufferSource);
         }
      }
   }
}

