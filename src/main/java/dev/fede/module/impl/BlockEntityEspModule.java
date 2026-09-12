package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.IconListSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class BlockEntityEspModule extends Module {
   private static final String OTHER = "other";
   private static final int MAX_ENTRIES = 8192;
   public final IconListSetting blockEntities = this.addSetting(new IconListSetting("Block Entities", "Which block-entity types to highlight"));
   public final ModeSetting mode = this.addSetting(new ModeSetting("Mode", "Box style — hollow outline or translucent fill", "Full", "Full", "Outline"));
   public final SliderSetting range = this.addSetting(new SliderSetting("Range", "Max distance a block entity is highlighted", 128.0, 16.0, 512.0, 8.0));
   public final SliderSetting highlightAlpha = this.addSetting(new SliderSetting("Highlight Alpha", "Box opacity (0-255)", 180.0, 0.0, 255.0, 1.0));
   public final BooleanSetting tracers = this.addSetting(new BooleanSetting("Tracers", "Draw lines from the crosshair to each block entity", false));
   public final BooleanSetting showGhosts = this.addSetting(
      new BooleanSetting("Show Ghosts", "Keep entries the server sent but that are gone client-side", true)
   );
   public final ColorSetting ghostTint = this.addSetting(new ColorSetting("Ghost Tint", "Color blended into ghost entries", -922791856));
   public final BooleanSetting chunkPackets = this.addSetting(new BooleanSetting("Chunk Packets", "Read block entities from chunk-data packets", true));
   public final BooleanSetting beUpdatePackets = this.addSetting(
      new BooleanSetting("BE Update Packets", "Read block entities from block-entity update packets", true)
   );
   public final BooleanSetting worldRescan = this.addSetting(new BooleanSetting("World Rescan", "Also snapshot already-loaded chunks when enabled", true));
   private final Map<String, String> aliases = new HashMap<>();
   private final Map<Long, BlockEntityEspModule.Cached> cache = new ConcurrentHashMap<>();

   public BlockEntityEspModule() {
      super("BlockEntityESP", "Highlights block entities from raw packets", Category.RENDER);
      this.ghostTint.visibleWhen(this.showGhosts::get);
      this.type("minecraft:chest", "Chest", Items.CHEST, -22016, true);
      this.type("minecraft:trapped_chest", "Trapped Chest", Items.TRAPPED_CHEST, -65536, true);
      this.type("minecraft:ender_chest", "Ender Chest", Items.ENDER_CHEST, -8912641, true);
      this.type("minecraft:shulker_box", "Shulker Box", Items.SHULKER_BOX, -47873, true);
      this.type("minecraft:barrel", "Barrel", Items.BARREL, -7842560, true);
      this.type("minecraft:mob_spawner", "Spawner", Items.SPAWNER, -16711936, true);
      this.type("minecraft:hopper", "Hopper", Items.HOPPER, -7829368, false);
      this.type("minecraft:furnace", "Furnace", Items.FURNACE, -7566196, false);
      this.alias("minecraft:blast_furnace", "minecraft:furnace");
      this.alias("minecraft:smoker", "minecraft:furnace");
      this.type("minecraft:dispenser", "Dispenser", Items.DISPENSER, -10066330, false);
      this.alias("minecraft:dropper", "minecraft:dispenser");
      this.type("minecraft:brewing_stand", "Brewing Stand", Items.BREWING_STAND, -3372801, false);
      this.type("minecraft:beehive", "Beehive", Items.BEEHIVE, -13312, false);
      this.type("minecraft:enchanting_table", "Enchanting Table", Items.ENCHANTING_TABLE, -7864065, false);
      this.type("minecraft:sign", "Sign", Items.OAK_SIGN, -3355444, false);
      this.alias("minecraft:hanging_sign", "minecraft:sign");
      this.type("minecraft:bed", "Bed", Items.RED_BED, -30584, false);
      this.type("minecraft:skull", "Skull", Items.SKELETON_SKULL, -2236963, false);
      this.type("minecraft:banner", "Banner", Items.WHITE_BANNER, -1118482, false);
      this.type("minecraft:crafter", "Crafter", Items.CRAFTER, -12276993, false);
      this.type("minecraft:vault", "Vault", Items.VAULT, -10496, false);
      this.type("minecraft:trial_spawner", "Trial Spawner", Items.TRIAL_SPAWNER, -16711766, false);
      this.type("other", "Other", Items.BEDROCK, -5592406, true);
   }

   private void type(String id, String label, Item icon, int color, boolean on) {
      this.blockEntities.add(id, label, icon, on, color);
   }

   private void alias(String from, String to) {
      this.aliases.put(from, to);
   }

   private String canonicalKey(String typeId) {
      if (this.blockEntities.get(typeId) != null) {
         return typeId;
      } else {
         String aliased = this.aliases.get(typeId);
         if (aliased != null) {
            return aliased;
         } else {
            int slash = typeId.indexOf(58);
            if (slash >= 0) {
               String path = typeId.substring(slash + 1);
               if (this.blockEntities.get("minecraft:null") != null) {
                  return "minecraft:null";
               }

               if (this.aliases.containsKey("minecraft:null")) {
                  return this.aliases.get("minecraft:null");
               }
            }

            return "other";
         }
      }
   }

   public boolean chunkPacketsEnabled() {
      return this.chunkPackets.get();
   }

   public boolean beUpdatePacketsEnabled() {
      return this.beUpdatePackets.get();
   }

   public void run(BlockPos pos, BlockEntityType<?> type) {
      if (pos != null && type != null) {
         Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(type);
         String key = this.canonicalKey(id != null ? id.toString() : String.valueOf(type));
         this.cache.put(pos.asLong(), new Cached(pos.toImmutable(), key, System.currentTimeMillis()));
         if (this.cache.size() > 8192) {
            this.pruneOldest();
         }
      }
   }

   private void pruneOldest() {
      long oldestTime = Long.MAX_VALUE;
      Long oldestKey = null;

      for (Entry<Long, BlockEntityEspModule.Cached> e : this.cache.entrySet()) {
         if (e.getValue().lastSeenMs() < oldestTime) {
            oldestTime = e.getValue().lastSeenMs();
            oldestKey = e.getKey();
         }
      }

      if (oldestKey != null) {
         this.cache.remove(oldestKey);
      }
   }

   public Collection<BlockEntityEspModule.Cached> entries() {
      return this.cache.values();
   }

   public void clear() {
      this.cache.clear();
   }

   public int cachedCount() {
      return this.cache.size();
   }

   @Override
   protected void onEnable() {
      this.cache.clear();
      if (this.worldRescan.get()) {
         this.rescanLoadedChunks();
      }
   }

   @Override
   protected void onDisable() {
      this.cache.clear();
   }

   @Override
   public void onTick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientPlayerEntity player = mc.player;
      if (mc.world != null && player != null) {
         double r = this.range.get();
         double maxSq = r * r;
         double px = player.getX();
         double py = player.getY();
         double pz = player.getZ();
         this.cache.values().removeIf(c -> {
            BlockPos p = c.pos();
            double dx = p.getX() + 0.5 - px;
            double dy = p.getY() + 0.5 - py;
            double dz = p.getZ() + 0.5 - pz;
            return dx * dx + dy * dy + dz * dz > maxSq;
         });
      }
   }

   private void rescanLoadedChunks() {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientWorld level = mc.world;
      ClientPlayerEntity player = mc.player;
      if (level != null && player != null) {
         int radius = Math.min(32, (int)Math.ceil(this.range.get() / 16.0) + 2);
         int pcx = player.getChunkPos().x;
         int pcz = player.getChunkPos().z;
         Mutable p = new Mutable();

         for (int cx = pcx - radius; cx <= pcx + radius; cx++) {
            for (int cz = pcz - radius; cz <= pcz + radius; cz++) {
               if (level.getChunkManager().isChunkLoaded(cx, cz)) {
                  WorldChunk chunk = level.getChunk(cx, cz);
                  ChunkSection[] sections = chunk.getSectionArray();
                  int minSectionY = chunk.getBottomSectionCoord();
                  int baseX = chunk.getPos().getStartX();
                  int baseZ = chunk.getPos().getStartZ();

                  for (int s = 0; s < sections.length; s++) {
                     ChunkSection section = sections[s];
                     if (!section.isEmpty() && section.hasAny(AbstractBlockState::hasBlockEntity)) {
                        int baseY = minSectionY + s << 4;

                        for (int y = 0; y < 16; y++) {
                           for (int z = 0; z < 16; z++) {
                              for (int x = 0; x < 16; x++) {
                                 BlockState state = section.getBlockState(x, y, z);
                                 if (state.hasBlockEntity() && state.getBlock() instanceof BlockEntityProvider eb) {
                                    p.set(baseX + x, baseY + y, baseZ + z);

                                    try {
                                       BlockEntity be = eb.createBlockEntity(p.toImmutable(), state);
                                       if (be != null) {
                                          this.run(p, be.getType());
                                       }
                                    } catch (Exception var24) {
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public final class Cached {
      private BlockPos pos;
      private String typeKey;
      private long lastSeenMs;

      public Cached(BlockPos pos, String typeKey, long lastSeenMs) {
         this.pos = pos;
         this.typeKey = typeKey;
         this.lastSeenMs = lastSeenMs;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public String typeKey() {
         return this.typeKey;
      }

      public long lastSeenMs() {
         return this.lastSeenMs;
      }
   }
}

