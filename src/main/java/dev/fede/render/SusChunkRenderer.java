package dev.fede.render;

import dev.fede.FeClient;
import dev.fede.module.Modules;
import dev.fede.suschunk.SusChunkScanner;
import dev.fede.util.Colors;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public final class SusChunkRenderer {
   private static final float FADE_IN_SECONDS = 0.3F;
   private static final float FADE_OUT_SECONDS = 0.45F;
   private static final float MOVE_RATE = 7.0F;
   private static final Map<Long, SusChunkRenderer.ChunkFade> chunkFades = new HashMap<>();
   private static final Map<Long, SusChunkRenderer.ZoneFade> zoneFades = new HashMap<>();
   private static long lastFrameNanos;

   private SusChunkRenderer() {
   }

   public static void reset() {
      chunkFades.clear();
      zoneFades.clear();
   }

   public static String debugState() {
      StringBuilder sb = new StringBuilder();

      for (Entry<Long, SusChunkRenderer.ChunkFade> entry : chunkFades.entrySet()) {
         SusChunkRenderer.ChunkFade fade = entry.getValue();
         sb.append(new ChunkPos(entry.getKey()))
            .append("{a=")
            .append(fade.alpha)
            .append(",t=")
            .append(fade.tier)
            .append(",f=")
            .append(fade.flagged)
            .append("} ");
      }

      for (Entry<Long, SusChunkRenderer.ZoneFade> entry : zoneFades.entrySet()) {
         SusChunkRenderer.ZoneFade zone = entry.getValue();
         sb.append("zone")
            .append(new ChunkPos(entry.getKey()))
            .append("{a=")
            .append(zone.alpha)
            .append(",s=")
            .append(zone.size)
            .append(",f=")
            .append(zone.flagged)
            .append("} ");
      }

      return sb.toString();
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d camera, Modules.SusChunkFinderModule module) {
      SusChunkScanner scanner = module.scanner;
      double y = module.renderY.get();
      int accent = FeClient.themes().current().accent();
      float fillAlpha = module.fillOpacity.getFloat() / 255.0F;
      float outlineAlpha = module.outlineOpacity.getFloat() / 255.0F;
      boolean outline = module.outline.get();
      boolean smart = module.smartMode.get();
      int threshold = scanner.threshold();
      updateTargets(scanner, smart, threshold);
      float dt = frameDelta();
      float move = 1.0F - (float)Math.exp(-dt * 7.0F);
      Iterator<Entry<Long, SusChunkRenderer.ChunkFade>> chunks = chunkFades.entrySet().iterator();

      while (chunks.hasNext()) {
         Entry<Long, SusChunkRenderer.ChunkFade> entry = chunks.next();
         SusChunkRenderer.ChunkFade fade = entry.getValue();
         fade.alpha = fade.alpha + (fade.flagged ? dt / 0.3F : -dt / 0.45F);
         fade.alpha = Math.clamp(fade.alpha, 0.0F, 1.0F);
         if (!fade.flagged && fade.alpha <= 0.0F) {
            chunks.remove();
         } else {
            double x0 = ChunkPos.getPackedX(entry.getKey()) * 16.0;
            double z0 = ChunkPos.getPackedZ(entry.getKey()) * 16.0;
            drawQuad(
               bufferSource,
               poseStack,
               camera,
               x0,
               z0,
               x0 + 16.0,
               z0 + 16.0,
               y,
               accent,
               fillAlpha * fade.tier * fade.alpha,
               outline ? outlineAlpha * fade.alpha : 0.0F
            );
         }
      }

      Iterator<Entry<Long, SusChunkRenderer.ZoneFade>> zones = zoneFades.entrySet().iterator();

      while (zones.hasNext()) {
         SusChunkRenderer.ZoneFade zone = zones.next().getValue();
         zone.alpha = zone.alpha + (zone.flagged ? dt / 0.3F : -dt / 0.45F);
         zone.alpha = Math.clamp(zone.alpha, 0.0F, 1.0F);
         if (!zone.flagged && zone.alpha <= 0.0F) {
            zones.remove();
         } else {
            zone.centerX = zone.centerX + (zone.targetX - zone.centerX) * move;
            zone.centerZ = zone.centerZ + (zone.targetZ - zone.centerZ) * move;
            zone.size = zone.size + (zone.targetSize - zone.size) * move;
            double half = zone.size / 2.0;
            drawQuad(
               bufferSource,
               poseStack,
               camera,
               zone.centerX - half,
               zone.centerZ - half,
               zone.centerX + half,
               zone.centerZ + half,
               y,
               accent,
               fillAlpha * zone.tier * zone.alpha,
               outline ? outlineAlpha * zone.alpha : 0.0F
            );
            if (module.centroidMarker.get()) {
               FlatOverlay.marker(bufferSource, poseStack, camera, zone.centerX, zone.centerZ, y + 0.05, 2.0, Colors.withAlpha(accent, 0.95F * zone.alpha));
            }
         }
      }

      FlatOverlay.flush(bufferSource);
   }

   private static void drawQuad(
      Immediate bufferSource, MatrixStack poseStack, Vec3d camera, double x0, double z0, double x1, double z1, double y, int accent, float fill, float border
   ) {
      FlatOverlay.fillQuad(bufferSource, poseStack, camera, x0, z0, x1, z1, y, Colors.withAlpha(accent, fill));
      if (border > 0.004F) {
         int color = Colors.withAlpha(accent, border);
         FlatOverlay.edge(bufferSource, poseStack, camera, x0, z0, x1, z0, y, color, 2.5F);
         FlatOverlay.edge(bufferSource, poseStack, camera, x0, z1, x1, z1, y, color, 2.5F);
         FlatOverlay.edge(bufferSource, poseStack, camera, x0, z0, x0, z1, y, color, 2.5F);
         FlatOverlay.edge(bufferSource, poseStack, camera, x1, z0, x1, z1, y, color, 2.5F);
      }
   }

   private static void updateTargets(SusChunkScanner scanner, boolean smart, int threshold) {
      for (SusChunkRenderer.ChunkFade fade : chunkFades.values()) {
         fade.flagged = false;
      }

      for (SusChunkRenderer.ZoneFade zone : zoneFades.values()) {
         zone.flagged = false;
      }

      if (smart) {
         for (SusChunkScanner.Zone zone : scanner.zones()) {
            if (zone.members().size() == 1) {
               long key = zone.members().iterator().next();
               SusChunkRenderer.ChunkFade fade = chunkFades.computeIfAbsent(key, k -> new SusChunkRenderer.ChunkFade());
               fade.flagged = true;
               fade.tier = confidence(zone.maxScore(), threshold);
            } else {
               long key = Long.MAX_VALUE;

               for (long member : zone.members()) {
                  key = Math.min(key, member);
               }

               SusChunkRenderer.ZoneFade fade = zoneFades.get(key);
               if (fade == null) {
                  fade = new ZoneFade(zone.centroidX(), zone.centroidZ());
                  zoneFades.put(key, fade);
               }

               fade.flagged = true;
               fade.targetX = zone.centroidX();
               fade.targetZ = zone.centroidZ();
               fade.targetSize = Math.min(48.0F, 16.0F + (zone.members().size() - 1) * 8.0F);
               fade.tier = confidence(zone.maxScore(), threshold);
            }
         }
      } else {
         for (SusChunkScanner.Flag flag : scanner.flags()) {
            SusChunkRenderer.ChunkFade fade = chunkFades.computeIfAbsent(flag.chunkKey(), k -> new SusChunkRenderer.ChunkFade());
            fade.flagged = true;
            fade.tier = 1.0F;
         }
      }
   }

   private static float confidence(double score, int threshold) {
      if (threshold <= 0) {
         return 1.0F;
      } else {
         float over = (float)((score - threshold) / (threshold * 2.0));
         return 0.55F + 0.45F * Math.clamp(over, 0.0F, 1.0F);
      }
   }

   private static float frameDelta() {
      long now = System.nanoTime();
      float dt = lastFrameNanos == 0L ? 0.016F : (float)(now - lastFrameNanos) / 1.E9F;
      lastFrameNanos = now;
      return Math.min(dt, 0.1F);
   }

   final static class ChunkFade {
      float alpha;
      float tier = 1.0F;
      boolean flagged;

      private ChunkFade() {
      }
   }

   final static class ZoneFade {
      double centerX;
      double centerZ;
      double targetX;
      double targetZ;
      float size = 16.0F;
      float targetSize = 16.0F;
      float alpha;
      float tier = 1.0F;
      boolean flagged;

      ZoneFade(double x, double z) {
         this.centerX = this.targetX = x;
         this.centerZ = this.targetZ = z;
      }
   }
}



