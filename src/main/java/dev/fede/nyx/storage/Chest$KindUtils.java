package dev.fede.nyx.storage;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public final class Chest$KindUtils {
   private static final Map<Long, Chest> CHESTS = new ConcurrentHashMap<>();
   private static final long TOAST_MIN_INTERVAL_MS = 1000L;
   private static final AtomicLong LAST_TOAST_MS = new AtomicLong(0L);
   private static volatile boolean silentMode = false;

   private Chest$KindUtils() {
   }

   public static void onBlockEntity(BlockPos var0, BlockEntity var1) {
      if (var0 != null && var1 != null) {
         Chest.Kind var2 = classify(var1.getType());
         if (var2 != null) {
            BlockPos var3 = var0.toImmutable();
            MinecraftClient.getInstance().execute(() -> insert(var3, var2));
         }
      }
   }

   public static void onChunkLoad(WorldChunk var0) {
      if (var0 != null) {
         try {
            for (Entry var2 : var0.getBlockEntities().entrySet()) {
               Chest.Kind var3 = classify(((BlockEntity)var2.getValue()).getType());
               if (var3 != null) {
                  insert(((BlockPos)var2.getKey()).toImmutable(), var3);
               }
            }

            scanCauldronsInChunk(var0);
            scanMechanismsInChunk(var0);
         } catch (Throwable var4) {
         }
      }
   }

   public static void onChunkUnload(WorldChunk var0) {
      if (var0 != null) {
         int var1 = var0.getPos().x;
         int var2 = var0.getPos().z;
         CHESTS.entrySet().removeIf(entry -> {
            BlockPos var3 = entry.getValue().pos();
            return var3.getX() >> 4 == var1 && var3.getZ() >> 4 == var2;
         });
      }
   }

   public static void runSilently(Runnable r) {
      boolean var1 = silentMode;
      silentMode = true;

      try {
         r.run();
      } finally {
         silentMode = var1;
      }
   }

   public static Chest.Kind classify(BlockEntityType<?> var0) {
      if (var0 == null) {
         return null;
      } else if (var0 == BlockEntityType.CHEST) {
         return Chest.Kind.CHEST;
      } else if (var0 == BlockEntityType.TRAPPED_CHEST) {
         return Chest.Kind.TRAPPED_CHEST;
      } else if (var0 == BlockEntityType.ENDER_CHEST) {
         return Chest.Kind.ENDER_CHEST;
      } else if (var0 == BlockEntityType.BARREL) {
         return Chest.Kind.BARREL;
      } else if (var0 == BlockEntityType.SHULKER_BOX) {
         return Chest.Kind.SHULKER;
      } else if (var0 == BlockEntityType.HOPPER) {
         return Chest.Kind.HOPPER;
      } else if (var0 == BlockEntityType.DISPENSER) {
         return Chest.Kind.DISPENSER;
      } else if (var0 == BlockEntityType.DROPPER) {
         return Chest.Kind.DROPPER;
      } else if (var0 == BlockEntityType.FURNACE) {
         return Chest.Kind.FURNACE;
      } else if (var0 == BlockEntityType.BLAST_FURNACE) {
         return Chest.Kind.BLAST_FURNACE;
      } else if (var0 == BlockEntityType.SMOKER) {
         return Chest.Kind.SMOKER;
      } else if (var0 == BlockEntityType.BREWING_STAND) {
         return Chest.Kind.BREWING_STAND;
      } else if (var0 == BlockEntityType.BEACON) {
         return Chest.Kind.BEACON;
      } else if (var0 == BlockEntityType.MOB_SPAWNER) {
         return Chest.Kind.SPAWNER;
      } else if (var0 == BlockEntityType.VAULT) {
         return Chest.Kind.VAULT;
      } else if (var0 == BlockEntityType.TRIAL_SPAWNER) {
         return Chest.Kind.TRIAL_SPAWNER;
      } else if (var0 == BlockEntityType.DECORATED_POT) {
         return Chest.Kind.DECORATED_POT;
      } else {
         return var0 == BlockEntityType.BRUSHABLE_BLOCK ? Chest.Kind.BRUSHABLE_BLOCK : null;
      }
   }

   public static Chest.Kind classify(Block var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof AbstractCauldronBlock) {
         return Chest.Kind.CAULDRON;
      } else if (var0 instanceof ShulkerBoxBlock) {
         return Chest.Kind.SHULKER;
      } else if (var0 == Blocks.CHEST) {
         return Chest.Kind.CHEST;
      } else if (var0 == Blocks.TRAPPED_CHEST) {
         return Chest.Kind.TRAPPED_CHEST;
      } else if (var0 == Blocks.ENDER_CHEST) {
         return Chest.Kind.ENDER_CHEST;
      } else if (var0 == Blocks.BARREL) {
         return Chest.Kind.BARREL;
      } else if (var0 == Blocks.HOPPER) {
         return Chest.Kind.HOPPER;
      } else if (var0 == Blocks.DISPENSER) {
         return Chest.Kind.DISPENSER;
      } else if (var0 == Blocks.DROPPER) {
         return Chest.Kind.DROPPER;
      } else if (var0 == Blocks.FURNACE) {
         return Chest.Kind.FURNACE;
      } else if (var0 == Blocks.BLAST_FURNACE) {
         return Chest.Kind.BLAST_FURNACE;
      } else if (var0 == Blocks.SMOKER) {
         return Chest.Kind.SMOKER;
      } else if (var0 == Blocks.BREWING_STAND) {
         return Chest.Kind.BREWING_STAND;
      } else if (var0 == Blocks.BEACON) {
         return Chest.Kind.BEACON;
      } else if (var0 == Blocks.SPAWNER) {
         return Chest.Kind.SPAWNER;
      } else if (var0 == Blocks.VAULT) {
         return Chest.Kind.VAULT;
      } else if (var0 == Blocks.TRIAL_SPAWNER) {
         return Chest.Kind.TRIAL_SPAWNER;
      } else if (var0 == Blocks.DECORATED_POT) {
         return Chest.Kind.DECORATED_POT;
      } else if (var0 == Blocks.SUSPICIOUS_SAND || var0 == Blocks.SUSPICIOUS_GRAVEL) {
         return Chest.Kind.BRUSHABLE_BLOCK;
      } else if (var0 == Blocks.PISTON) {
         return Chest.Kind.PISTON;
      } else if (var0 == Blocks.PISTON_HEAD) {
         return Chest.Kind.PISTON;
      } else if (var0 == Blocks.STICKY_PISTON) {
         return Chest.Kind.STICKY_PISTON;
      } else if (var0 == Blocks.OBSERVER) {
         return Chest.Kind.OBSERVER;
      } else if (var0 == Blocks.COMPARATOR) {
         return Chest.Kind.COMPARATOR;
      } else if (var0 == Blocks.REPEATER) {
         return Chest.Kind.REPEATER;
      } else if (var0 == Blocks.NOTE_BLOCK) {
         return Chest.Kind.NOTE_BLOCK;
      } else if (var0 == Blocks.SCULK_SENSOR || var0 == Blocks.CALIBRATED_SCULK_SENSOR) {
         return Chest.Kind.SCULK_SENSOR;
      } else if (var0 == Blocks.SLIME_BLOCK) {
         return Chest.Kind.SLIME_BLOCK;
      } else if (var0 == Blocks.HONEY_BLOCK) {
         return Chest.Kind.HONEY_BLOCK;
      } else if (var0 == Blocks.TNT) {
         return Chest.Kind.TNT;
      } else if (var0 == Blocks.TARGET) {
         return Chest.Kind.TARGET_BLOCK;
      } else {
         return var0 == Blocks.REDSTONE_LAMP ? Chest.Kind.REDSTONE_LAMP : null;
      }
   }

   public static Collection<Chest> all() {
      return CHESTS.values();
   }

   public static int count() {
      return CHESTS.size();
   }

   public static void clear() {
      CHESTS.clear();
   }

   public static void updateSnapshot(BlockPos var0, List<ContainerSnapshotMixinEntry> var1) {
      if (var0 != null) {
         BlockPos var2 = var0.toImmutable();
         long var3 = System.currentTimeMillis();
         MinecraftClient.getInstance().execute(() -> {
            long var4 = var2.asLong();
            Chest var6 = CHESTS.get(var4);
            if (var6 == null) {
               var6 = new Chest(var2, Chest.Kind.OTHER);
               CHESTS.put(var4, var6);
            }

            var6.setSnapshot(var1, var3);
            var6.touch();
         });
      }
   }

   public static java.util.Map exportEntries() {
      LinkedHashMap var0 = new LinkedHashMap();

      for (Entry var2 : CHESTS.entrySet()) {
         Chest var3 = (Chest)var2.getValue();
         BlockPos var4 = var3.pos();
         List var5 = var3.snapshot();
         var0.put((Long)var2.getKey(), null);
      }

      return var0;
   }

   public static void importEntries(java.util.Map var0) {
   }


   private static void insert(BlockPos var0, Chest.Kind var1) {
      long var2 = var0.asLong();
      Chest var4 = CHESTS.get(var2);
      if (var4 != null) {
         var4.touch();
         var4.setKind(var1);
      } else {
         Chest var5 = new Chest(var0, var1);
         CHESTS.put(var2, var5);
         if (!silentMode && isNoteworthy(var1)) {
            maybeToast(var0, var1);
         }
      }
   }

   private static void scanCauldronsInChunk(WorldChunk var0) {
      int var1 = var0.getPos().x << 4;
      int var2 = var0.getPos().z << 4;
      ChunkSection[] var3 = var0.getSectionArray();
      int var4 = var0.getBottomY();

      for (int var5 = 0; var5 < var3.length; var5++) {
         ChunkSection var6 = var3[var5];
         if (var6 != null && !var6.isEmpty()) {
            boolean var7;
            try {
               var7 = var6.getBlockStateContainer().hasAny(var0x -> var0x.getBlock() instanceof AbstractCauldronBlock);
            } catch (Throwable var14) {
               var7 = true;
            }

            if (var7) {
               int var8 = var4 + (var5 << 4);

               for (int var9 = 0; var9 < 16; var9++) {
                  for (int var10 = 0; var10 < 16; var10++) {
                     for (int var11 = 0; var11 < 16; var11++) {
                        BlockState var12;
                        try {
                           var12 = var6.getBlockState(var10, var9, var11);
                        } catch (Throwable var15) {
                           continue;
                        }

                        if (var12.getBlock() instanceof AbstractCauldronBlock) {
                           BlockPos var13 = new BlockPos(var1 + var10, var8 + var9, var2 + var11);
                           insert(var13, Chest.Kind.CAULDRON);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void scanMechanismsInChunk(WorldChunk var0) {
      int var1 = var0.getPos().x << 4;
      int var2 = var0.getPos().z << 4;
      ChunkSection[] var3 = var0.getSectionArray();
      int var4 = var0.getBottomY();

      for (int var5 = 0; var5 < var3.length; var5++) {
         ChunkSection var6 = var3[var5];
         if (var6 != null && !var6.isEmpty()) {
            boolean var7;
            try {
               var7 = var6.getBlockStateContainer().hasAny(Chest$KindUtils::isFarmMechanism);
            } catch (Throwable var15) {
               var7 = true;
            }

            if (var7) {
               int var8 = var4 + (var5 << 4);

               for (int var9 = 0; var9 < 16; var9++) {
                  for (int var10 = 0; var10 < 16; var10++) {
                     for (int var11 = 0; var11 < 16; var11++) {
                        BlockState var12;
                        try {
                           var12 = var6.getBlockState(var10, var9, var11);
                        } catch (Throwable var16) {
                           continue;
                        }

                        Chest.Kind var13 = classifyMechanism(var12.getBlock());
                        if (var13 != null) {
                           BlockPos var14 = new BlockPos(var1 + var10, var8 + var9, var2 + var11);
                           insert(var14, var13);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean isFarmMechanism(BlockState var0) {
      return classifyMechanism(var0.getBlock()) != null;
   }

   private static Chest.Kind classifyMechanism(Block var0) {
      if (var0 == Blocks.PISTON || var0 == Blocks.PISTON_HEAD) {
         return Chest.Kind.PISTON;
      } else if (var0 == Blocks.STICKY_PISTON) {
         return Chest.Kind.STICKY_PISTON;
      } else if (var0 == Blocks.OBSERVER) {
         return Chest.Kind.OBSERVER;
      } else if (var0 == Blocks.COMPARATOR) {
         return Chest.Kind.COMPARATOR;
      } else if (var0 == Blocks.REPEATER) {
         return Chest.Kind.REPEATER;
      } else if (var0 == Blocks.NOTE_BLOCK) {
         return Chest.Kind.NOTE_BLOCK;
      } else if (var0 == Blocks.SCULK_SENSOR || var0 == Blocks.CALIBRATED_SCULK_SENSOR) {
         return Chest.Kind.SCULK_SENSOR;
      } else if (var0 == Blocks.SLIME_BLOCK) {
         return Chest.Kind.SLIME_BLOCK;
      } else if (var0 == Blocks.HONEY_BLOCK) {
         return Chest.Kind.HONEY_BLOCK;
      } else if (var0 == Blocks.TNT) {
         return Chest.Kind.TNT;
      } else if (var0 == Blocks.TARGET) {
         return Chest.Kind.TARGET_BLOCK;
      } else {
         return var0 == Blocks.REDSTONE_LAMP ? Chest.Kind.REDSTONE_LAMP : null;
      }
   }

   private static boolean isNoteworthy(Chest.Kind k) {
      return switch (k) {
         case CHEST, TRAPPED_CHEST, BARREL, ENDER_CHEST, SHULKER, BEACON, SPAWNER, VAULT, TRIAL_SPAWNER, DECORATED_POT, BRUSHABLE_BLOCK -> true;
         default -> false;
      };
   }

   private static void maybeToast(BlockPos var0, Chest.Kind var1) {
      long var2 = System.currentTimeMillis();
      long var4 = LAST_TOAST_MS.get();
      if (var2 - var4 >= 1000L) {
         if (LAST_TOAST_MS.compareAndSet(var4, var2)) {
            MinecraftClient var6 = MinecraftClient.getInstance();
            var6.execute(() -> {
               if (var6.player != null) {
                  String var3 = String.format("§8[§aCode Engine§8] §7%s §8at §f%d %d %d", var1.prettyName(), var0.getX(), var0.getY(), var0.getZ());
                  var6.player.sendMessage(Text.literal(var3), true);
               }
            });
         }
      }
   }
}

