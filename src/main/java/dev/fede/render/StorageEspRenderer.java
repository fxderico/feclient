package dev.fede.render;

import dev.fede.module.impl.StorageEspModule;
import java.util.List;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.TrappedChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Vector3fc;

public final class StorageEspRenderer {
   private static final int MAX_CHUNK_RADIUS = 16;
   private static final int MAX_RESULTS = 6000;
   private static final float BOX_INFLATE = 0.002F;
   private static final double CHEST_INSET = 0.0625;
   private static final int INTERACTED_RGB = 6579300;
   private static final float LINE_WIDTH = 1.0F;
   private static final float TRACER_WIDTH = 1.15F;
   private static final int TRACER_ALPHA = 180;
   private static final IncrementalScan<StorageEspRenderer.Hit> SCAN = new IncrementalScan<>(48, 80000, 20);

   private StorageEspRenderer() {
   }

   public static void clear() {
      SCAN.clear();
   }

   public static int cachedCount() {
      return SCAN.get().size();
   }

   public static long cachedShulkerCount() {
      return SCAN.get().stream().filter(h -> h.type() == StorageEspModule.StorageType.SHULKER).count();
   }

   public static void scan(StorageEspModule module) {
      double range = module.range.get();
      double rangeSq = range * range;
      int wanted = (int)Math.ceil(range / 16.0) + 1;
      int chunkRadius = Math.min(Math.min(16, wanted), (Integer)MinecraftClient.getInstance().options.getViewDistance().getValue());
      SCAN.tick(chunkRadius, (chunk, out) -> scanChunk(chunk, rangeSq, out));
   }

   private static int scanChunk(WorldChunk chunk, double rangeSq, List<StorageEspRenderer.Hit> out) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         return 0;
      } else {
         ChunkSection[] sections = chunk.getSectionArray();
         int minSectionY = chunk.getBottomSectionCoord();
         int baseX = chunk.getPos().getStartX();
         int baseZ = chunk.getPos().getStartZ();
         int blocks = 0;
         if (out.size() >= 6000) {
            return 0;
         } else {
            for (int s = 0; s < sections.length; s++) {
               ChunkSection section = sections[s];
               if (!section.isEmpty() && section.hasAny(StorageEspRenderer::isStorage)) {
                  int baseY = minSectionY + s << 4;
                  blocks += 4096;

                  for (int y = 0; y < 16; y++) {
                     for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < 16; x++) {
                           StorageEspModule.StorageType type = classify(section.getBlockState(x, y, z).getBlock());
                           if (type != null) {
                              int wx = baseX + x;
                              int wy = baseY + y;
                              int wz = baseZ + z;
                              if (!(player.squaredDistanceTo(wx + 0.5, wy + 0.5, wz + 0.5) > rangeSq)) {
                                 out.add(new Hit(wx, wy, wz, type));
                                 if (out.size() >= 6000) {
                                    return blocks;
                                 }
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
   }

   public static void render(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, StorageEspModule module) {
      List<StorageEspRenderer.Hit> snapshot = SCAN.get();
      if (!snapshot.isEmpty()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         ClientWorld level = mc.world;
         if (level != null) {
            boolean fill = module.mode.check("Full");
            int alphaBits = Math.max(0, Math.min(255, module.highlightAlpha.getInt())) << 24;
            boolean tracers = module.tracers.get();
            boolean hideOpened = module.hideOpened();
            Vector3fc forward = mc.gameRenderer.getCamera().getHorizontalPlane();

            for (StorageEspRenderer.Hit hit : snapshot) {
               StorageEspModule.StorageType type = hit.type();
               if (module.isTypeEnabled(type)) {
                  boolean interacted = module.isInteracted(hit.getInt(), hit.getInt2(), hit.getInt3());
                  if (!interacted || !hideOpened) {
                     int rgb = interacted ? 6579300 : module.colorFor(type) & 16777215;
                     int color = alphaBits | rgb;
                     double x1 = hit.getInt();
                     double y1 = hit.getInt2();
                     double z1 = hit.getInt3();
                     double x2 = x1 + 1.0;
                     double y2 = y1 + 1.0;
                     double z2 = z1 + 1.0;
                     if (type == StorageEspModule.StorageType.CHEST
                        || type == StorageEspModule.StorageType.TRAPPED
                        || type == StorageEspModule.StorageType.ENDER) {
                        x1 += 0.0625;
                        z1 += 0.0625;
                        x2 -= 0.0625;
                        y2 -= 0.125;
                        z2 -= 0.0625;
                        if (type == StorageEspModule.StorageType.CHEST || type == StorageEspModule.StorageType.TRAPPED) {
                           BlockState st = level.getBlockState(new BlockPos(hit.getInt(), hit.getInt2(), hit.getInt3()));
                           if (st.getBlock() instanceof ChestBlock && st.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                              Direction facing = (Direction)st.get(ChestBlock.FACING);
                              ChestType ct = (ChestType)st.get(ChestBlock.CHEST_TYPE);
                              Direction nb = ct == ChestType.LEFT ? facing.rotateYClockwise() : facing.rotateYCounterclockwise();
                              if (nb == Direction.WEST) {
                                 x1 = hit.getInt();
                              } else if (nb == Direction.EAST) {
                                 x2 = hit.getInt() + 1;
                              } else if (nb == Direction.NORTH) {
                                 z1 = hit.getInt3();
                              } else if (nb == Direction.SOUTH) {
                                 z2 = hit.getInt3() + 1;
                              }
                           }
                        }
                     }

                     if (fill) {
                        EspBoxRenderer.fill(bufferSource, poseStack, cam, x1 - 0.002F, y1 - 0.002F, z1 - 0.002F, x2 + 0.002F, y2 + 0.002F, z2 + 0.002F, color);
                     } else {
                        EspBoxRenderer.outline(bufferSource, poseStack, cam, x1, y1, z1, x2, y2, z2, color, 1.0F);
                     }

                     if (tracers) {
                        int tracerColor = -1275068416 | rgb;
                        EspBoxRenderer.tracer(bufferSource, poseStack, cam, forward, (x1 + x2) / 2.0, (y1 + y2) / 2.0, (z1 + z2) / 2.0, tracerColor, 1.15F);
                     }
                  }
               }
            }

            EspBoxRenderer.flush(bufferSource);
         }
      }
   }

   private static boolean isStorage(BlockState state) {
      return classify(state.getBlock()) != null;
   }

   private static StorageEspModule.StorageType classify(Block b) {
      if (b instanceof TrappedChestBlock) {
         return StorageEspModule.StorageType.TRAPPED;
      } else if (b instanceof ChestBlock) {
         return StorageEspModule.StorageType.CHEST;
      } else if (b instanceof EnderChestBlock) {
         return StorageEspModule.StorageType.ENDER;
      } else if (b instanceof ShulkerBoxBlock) {
         return StorageEspModule.StorageType.SHULKER;
      } else if (b instanceof BarrelBlock) {
         return StorageEspModule.StorageType.BARREL;
      } else if (b instanceof SpawnerBlock) {
         return StorageEspModule.StorageType.SPAWNER;
      } else if (b instanceof HopperBlock) {
         return StorageEspModule.StorageType.HOPPER;
      } else if (b instanceof AbstractFurnaceBlock) {
         return StorageEspModule.StorageType.FURNACE;
      } else if (b instanceof BrewingStandBlock) {
         return StorageEspModule.StorageType.FURNACE;
      } else {
         return b instanceof DispenserBlock ? StorageEspModule.StorageType.HOPPER : null;
      }
   }

   final static class Hit {
      private int intVal;
      private int intVal2;
      private int intVal3;
      private StorageEspModule.StorageType type;

      private Hit(int x, int y, int z, StorageEspModule.StorageType type) {
         this.intVal = x;
         this.intVal2 = y;
         this.intVal3 = z;
         this.type = type;
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

      public StorageEspModule.StorageType type() {
         return this.type;
      }
   }
}

