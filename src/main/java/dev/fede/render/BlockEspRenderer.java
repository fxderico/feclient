package dev.fede.render;

import dev.fede.module.impl.BlockEspModule;
import dev.fede.settings.BlockListSetting;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Vector3fc;

public final class BlockEspRenderer {
   private static final int MAX_CHUNK_RADIUS = 12;
   private static final int MAX_RESULTS = 8000;
   private static final double INSET = 0.002;
   private static final IncrementalScan<BlockEspRenderer.Hit> SCAN = new IncrementalScan<>(48, 80000, 20);
   private static Set<Block> lastWanted = Set.of();

   private BlockEspRenderer() {
   }

   public static void clear() {
      SCAN.clear();
      lastWanted = Set.of();
   }

   public static int cachedCount() {
      return SCAN.get().size();
   }

   public static void scan(BlockEspModule module) {
      Set<Block> wanted = new HashSet<>();

      for (BlockListSetting.Target target : module.targets.targets()) {
         if (target.enabled.get() && target.block() != null) {
            wanted.add(target.block());
         }
      }

      if (wanted.isEmpty()) {
         SCAN.clear();
         lastWanted = Set.of();
      } else {
         if (!wanted.equals(lastWanted)) {
            lastWanted = wanted;
            SCAN.markDirty();
         }

         int extra = module.rangeExtraChunks.getInt();
         int chunkRadius = Math.min(12, (Integer)MinecraftClient.getInstance().options.getViewDistance().getValue() + extra);
         SCAN.tick(chunkRadius, (chunk, out) -> scanChunk(chunk, wanted, out));
      }
   }

   private static int scanChunk(WorldChunk chunk, Set<Block> wanted, List<BlockEspRenderer.Hit> out) {
      ChunkSection[] sections = chunk.getSectionArray();
      int minSectionY = chunk.getBottomSectionCoord();
      int baseX = chunk.getPos().getStartX();
      int baseZ = chunk.getPos().getStartZ();
      int blocks = 0;
      if (out.size() >= 8000) {
         return 0;
      } else {
         for (int s = 0; s < sections.length; s++) {
            ChunkSection section = sections[s];
            if (!section.isEmpty() && section.hasAny(st -> wanted.contains(st.getBlock()))) {
               int baseY = minSectionY + s << 4;
               blocks += 4096;

               for (int y = 0; y < 16; y++) {
                  for (int z = 0; z < 16; z++) {
                     for (int x = 0; x < 16; x++) {
                        Block block = section.getBlockState(x, y, z).getBlock();
                        if (wanted.contains(block)) {
                           out.add(new Hit(baseX + x, baseY + y, baseZ + z, block));
                           if (out.size() >= 8000) {
                              return blocks;
                           }
                        }
                     }
                  }
               }
            }
         }

         return blocks;
      }
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, BlockEspModule module) {
      List<BlockEspRenderer.Hit> snapshot = SCAN.get();
      if (!snapshot.isEmpty()) {
         Map<Block, Integer> colorByBlock = new HashMap<>();

         for (BlockListSetting.Target target : module.targets.targets()) {
            if (target.block() != null) {
               colorByBlock.put(target.block(), target.color.get());
            }
         }

         int fallback = module.lineColor.get();
         int alpha = Math.clamp((long)module.highlightAlpha.getInt(), 0, 255);
         boolean drawLines = module.shapeMode.check("Both") || module.shapeMode.check("Lines");
         boolean drawFill = module.shapeMode.check("Both") || module.shapeMode.check("Sides");
         boolean drawTracers = module.tracers.get() && module.tracer.get();

         for (BlockEspRenderer.Hit hit : snapshot) {
            int rgb = colorByBlock.getOrDefault(hit.block(), fallback) & 16777215;
            int argb = rgb | alpha << 24;
            if (drawFill) {
               EspBoxRenderer.fill(
                  bufferSource,
                  poseStack,
                  cam,
                  hit.getInt() + 0.002,
                  hit.getInt2() + 0.002,
                  hit.getInt3() + 0.002,
                  hit.getInt() + 1 - 0.002,
                  hit.getInt2() + 1 - 0.002,
                  hit.getInt3() + 1 - 0.002,
                  argb
               );
            }

            if (drawLines) {
               EspBoxRenderer.outline(
                  bufferSource,
                  poseStack,
                  cam,
                  hit.getInt() + 0.002,
                  hit.getInt2() + 0.002,
                  hit.getInt3() + 0.002,
                  hit.getInt() + 1 - 0.002,
                  hit.getInt2() + 1 - 0.002,
                  hit.getInt3() + 1 - 0.002,
                  argb,
                  1.6F
               );
            }
         }

         if (drawTracers) {
            int tracerAlpha = module.tracerColor.get() >>> 24 & 0xFF;
            if (tracerAlpha == 0) {
               tracerAlpha = 200;
            }

            Vector3fc forward = MinecraftClient.getInstance().gameRenderer.getCamera().getHorizontalPlane();

            for (BlockEspRenderer.Hit hit : snapshot) {
               int rgbx = colorByBlock.getOrDefault(hit.block(), fallback) & 16777215;
               int col = rgbx | tracerAlpha << 24;
               EspBoxRenderer.tracer(bufferSource, poseStack, cam, forward, hit.getInt() + 0.5, hit.getInt2() + 0.5, hit.getInt3() + 0.5, col, 1.2F);
            }
         }

         EspBoxRenderer.flush(bufferSource);
      }
   }

   final static class Hit {
      private int intVal;
      private int intVal2;
      private int intVal3;
      private Block block;

      private Hit(int x, int y, int z, Block block) {
         this.intVal = x;
         this.intVal2 = y;
         this.intVal3 = z;
         this.block = block;
      }

      public int getInt() {
         return this.intVal;
      }

      public int getInt2() {
         return this.intVal2;
      }

      public int getInt3() {
         return this.intVal3;
      }

      public Block block() {
         return this.block;
      }
   }
}

