package dev.fede.render;

import dev.fede.FeClient;
import dev.fede.module.impl.ChunkFinderModule;
import dev.fede.util.Colors;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public final class ChunkFinderRenderer {
   private static final float RENDER_Y = 55.0F;
   private static final int FILL_ALPHA = 190;

   private ChunkFinderRenderer() {
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d camera, ChunkFinderModule module) {
      if (!module.flaggedChunks().isEmpty()) {
         int accent = FeClient.themes().current().accent();
         int fill = Colors.withAlpha(Colors.darken(accent, 0.58F), 190);
         int outline = Colors.withAlpha(accent, 255);

         for (long key : module.flaggedChunks()) {
            double x0 = ChunkPos.getPackedX(key) * 16.0;
            double z0 = ChunkPos.getPackedZ(key) * 16.0;
            double x1 = x0 + 16.0;
            double z1 = z0 + 16.0;
            FlatOverlay.fillQuad(bufferSource, poseStack, camera, x0, z0, x1, z1, 55.0, fill);
            FlatOverlay.edge(bufferSource, poseStack, camera, x0, z0, x1, z0, 55.0, outline, 2.0F);
            FlatOverlay.edge(bufferSource, poseStack, camera, x1, z0, x1, z1, 55.0, outline, 2.0F);
            FlatOverlay.edge(bufferSource, poseStack, camera, x1, z1, x0, z1, 55.0, outline, 2.0F);
            FlatOverlay.edge(bufferSource, poseStack, camera, x0, z1, x0, z0, 55.0, outline, 2.0F);
         }

         FlatOverlay.flush(bufferSource);
      }
   }
}



