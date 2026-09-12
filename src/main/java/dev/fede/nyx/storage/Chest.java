package dev.fede.nyx.storage;

import java.util.Collections;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class Chest {
   private final BlockPos pos;
   private final Vec3d center;
   private volatile Chest.Kind kind;
   private volatile long lastSeenMs;
   private volatile List<ContainerSnapshotMixinEntry> snapshot;
   private volatile long snapshotAtMs;

   public Chest(BlockPos var1, Chest.Kind var2) {
      this.pos = var1.toImmutable();
      this.center = Vec3d.ofCenter(this.pos);
      this.kind = var2 == null ? Chest.Kind.OTHER : var2;
      this.lastSeenMs = System.currentTimeMillis();
   }

   public BlockPos pos() {
      return this.pos;
   }

   public Vec3d center() {
      return this.center;
   }

   public Chest.Kind kind() {
      return this.kind;
   }

   public void setKind(Chest.Kind kind) {
      if (kind != null) {
         this.kind = kind;
      }
   }

   public long lastSeenMs() {
      return this.lastSeenMs;
   }

   public void touch() {
      this.lastSeenMs = System.currentTimeMillis();
   }

   public void setSnapshot(List<ContainerSnapshotMixinEntry> var1, long var2) {
      if (var1 != null && !var1.isEmpty()) {
         this.snapshot = List.copyOf(var1);
         this.snapshotAtMs = var2;
      } else {
         this.snapshot = null;
         this.snapshotAtMs = 0L;
      }
   }

   public List<ContainerSnapshotMixinEntry> snapshot() {
      return this.snapshot;
   }

   public long snapshotAtMs() {
      return this.snapshotAtMs;
   }

   public boolean hasSnapshot() {
      List var1 = this.snapshot;
      return var1 != null && !var1.isEmpty();
   }

   public List<ContainerSnapshotMixinEntry> snapshotOrEmpty() {
      List var1 = this.snapshot;
      return var1 == null ? Collections.emptyList() : var1;
   }

   public Box worldBox() {
      return new Box(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1.0, this.pos.getY() + 1.0, this.pos.getZ() + 1.0);
   }

   public static enum Kind {
      CHEST(-922762164, "Chest"),
      TRAPPED_CHEST(-922777464, "Trapped Chest"),
      BARREL(-922756216, "Barrel"),
      ENDER_CHEST(-927052545, "Ender Chest"),
      SHULKER(-922777345, "Shulker Box"),
      HOPPER(-928602458, "Hopper"),
      DISPENSER(-930545665, "Dispenser"),
      DROPPER(-930545665, "Dropper"),
      FURNACE(-928207700, "Furnace"),
      BLAST_FURNACE(-927037304, "Blast Furnace"),
      SMOKER(-927029624, "Smoker"),
      BREWING_STAND(-927052610, "Brewing Stand"),
      BEACON(-922746881, "Beacon"),
      SPAWNER(-934477944, "Spawner"),
      VAULT(-922747000, "Vault"),
      TRIAL_SPAWNER(-927006786, "Trial Spawner"),
      DECORATED_POT(-922763610, "Decorated Pot"),
      BRUSHABLE_BLOCK(-924460890, "Brushable Block"),
      CAULDRON(-927023361, "Cauldron"),
      PISTON(-927424374, "Piston"),
      STICKY_PISTON(-930423414, "Sticky Piston"),
      OBSERVER(-932550024, "Observer"),
      COMPARATOR(-922785174, "Comparator"),
      REPEATER(-927970228, "Repeater"),
      NOTE_BLOCK(-927691670, "Note Block"),
      SCULK_SENSOR(-933574446, "Sculk Sensor"),
      SLIME_BLOCK(-929628266, "Slime Block"),
      HONEY_BLOCK(-922756006, "Honey Block"),
      TNT(-922792884, "TNT"),
      TARGET_BLOCK(-922773866, "Target"),
      REDSTONE_LAMP(-922761066, "Redstone Lamp"),
      OTHER(-922762164, "Storage");

      private final int color;
      private final String pretty;

      private Kind(int color, String pretty) {
         this.color = color;
         this.pretty = pretty;
      }

      public int color() {
         return this.color;
      }

      public String prettyName() {
         return this.pretty;
      }

      private static Chest.Kind[] $values() {
         return new Chest.Kind[]{
            CHEST,
            TRAPPED_CHEST,
            BARREL,
            ENDER_CHEST,
            SHULKER,
            HOPPER,
            DISPENSER,
            DROPPER,
            FURNACE,
            BLAST_FURNACE,
            SMOKER,
            BREWING_STAND,
            BEACON,
            SPAWNER,
            VAULT,
            TRIAL_SPAWNER,
            DECORATED_POT,
            BRUSHABLE_BLOCK,
            CAULDRON,
            PISTON,
            STICKY_PISTON,
            OBSERVER,
            COMPARATOR,
            REPEATER,
            NOTE_BLOCK,
            SCULK_SENSOR,
            SLIME_BLOCK,
            HONEY_BLOCK,
            TNT,
            TARGET_BLOCK,
            REDSTONE_LAMP,
            OTHER
         };
      }
   }
}

