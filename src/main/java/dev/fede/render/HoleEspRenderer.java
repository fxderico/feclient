package dev.fede.render;

import dev.fede.module.impl.DebugHoleEspModule;
import dev.fede.util.Colors;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import org.joml.Vector3fc;

public final class HoleEspRenderer {
   private static final int SCAN_INTERVAL_TICKS = 10;
   private static final int RADIUS = 16;
   private static final int VERTICAL = 6;
   private static final Set<Block> UNBREAKABLE = Set.of(
      Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.BEDROCK, Blocks.RESPAWN_ANCHOR, Blocks.REINFORCED_DEEPSLATE
   );
   private static volatile List<HoleEspRenderer.Hole> cache = List.of();
   private static int tickCounter;

   private HoleEspRenderer() {
   }

   public static void clear() {
      cache = List.of();
      tickCounter = 0;
   }

   public static void scan(DebugHoleEspModule module) {
      if (tickCounter++ % 10 == 0) {
         MinecraftClient mc = MinecraftClient.getInstance();
         ClientWorld level = mc.world;
         ClientPlayerEntity player = mc.player;
         if (level != null && player != null) {
            int need = module.depth.check("Any") ? 1 : Integer.parseInt(module.depth.get());
            BlockPos origin = player.getBlockPos();
            Mutable p = new Mutable();
            List<HoleEspRenderer.Hole> found = new ArrayList<>();

            for (int dx = -16; dx <= 16; dx++) {
               for (int dz = -16; dz <= 16; dz++) {
                  for (int dy = -6; dy <= 6; dy++) {
                     int x = origin.getX() + dx;
                     int y = origin.getY() + dy;
                     int z = origin.getZ() + dz;
                     if (isHole(level, p, x, y, z, need)) {
                        found.add(new Hole(x, y, z, wallsSafe(level, p, x, y, z)));
                     }
                  }
               }
            }

            cache = found;
         }
      }
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, DebugHoleEspModule module) {
      List<HoleEspRenderer.Hole> snapshot = cache;
      if (!snapshot.isEmpty()) {
         int safeColor = module.safe.get();
         int unsafeColor = module.unsafe.get();
         boolean tracers = module.tracers.get();
         Vector3fc forward = tracers ? MinecraftClient.getInstance().gameRenderer.getCamera().getHorizontalPlane() : null;

         for (HoleEspRenderer.Hole hole : snapshot) {
            int color = hole.safe() ? safeColor : unsafeColor;
            EspBoxRenderer.outline(
               bufferSource,
               poseStack,
               cam,
               hole.getInt(),
               hole.getInt2(),
               hole.getInt3(),
               hole.getInt() + 1,
               hole.getInt2() + 1,
               hole.getInt3() + 1,
               color,
               2.0F
            );
            if (tracers) {
               EspBoxRenderer.tracer(
                  bufferSource, poseStack, cam, forward, hole.getInt() + 0.5, hole.getInt2() + 0.5, hole.getInt3() + 0.5, Colors.withAlpha(color, 0.72F), 1.2F
               );
            }
         }

         EspBoxRenderer.flush(bufferSource);
      }
   }

   private static boolean isHole(ClientWorld level, Mutable p, int x, int y, int z, int need) {
      if (!level.getBlockState(p.set(x, y - 1, z)).blocksMovement()) {
         return false;
      } else {
         for (int i = 0; i < need; i++) {
            if (!level.getBlockState(p.set(x, y + i, z)).isAir()) {
               return false;
            }
         }

         return level.getBlockState(p.set(x + 1, y, z)).blocksMovement()
            && level.getBlockState(p.set(x - 1, y, z)).blocksMovement()
            && level.getBlockState(p.set(x, y, z + 1)).blocksMovement()
            && level.getBlockState(p.set(x, y, z - 1)).blocksMovement();
      }
   }

   private static boolean wallsSafe(ClientWorld level, Mutable p, int x, int y, int z) {
      return isUnbreakable(level.getBlockState(p.set(x, y - 1, z)))
         && isUnbreakable(level.getBlockState(p.set(x + 1, y, z)))
         && isUnbreakable(level.getBlockState(p.set(x - 1, y, z)))
         && isUnbreakable(level.getBlockState(p.set(x, y, z + 1)))
         && isUnbreakable(level.getBlockState(p.set(x, y, z - 1)));
   }

   private static boolean isUnbreakable(BlockState state) {
      return UNBREAKABLE.contains(state.getBlock());
   }

   final static class Hole {
      private int intVal;
      private int intVal2;
      private int intVal3;
      private boolean safe;

      private Hole(int x, int y, int z, boolean safe) {
         this.intVal = x;
         this.intVal2 = y;
         this.intVal3 = z;
         this.safe = safe;
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

      public boolean safe() {
         return this.safe;
      }
   }
}

