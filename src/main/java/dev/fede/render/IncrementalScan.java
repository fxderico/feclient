package dev.fede.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.WorldChunk;

public final class IncrementalScan<H> {
   private int chunksPerTick;
   private int blockBudgetPerTick;
   private int idleTicks;
   private volatile List<H> published = List.of();
   private List<H> building = new ArrayList<>();
   private int cursor;
   private int[] order = new int[0];
   private int orderRadius = -1;
   private int sweepPcx = Integer.MIN_VALUE;
   private int sweepPcz = Integer.MIN_VALUE;
   private int cooldown;
   private boolean dirty;

   public IncrementalScan(int chunksPerTick, int blockBudgetPerTick, int idleTicks) {
      this.chunksPerTick = chunksPerTick;
      this.blockBudgetPerTick = blockBudgetPerTick;
      this.idleTicks = idleTicks;
   }

   public List<H> get() {
      return this.published;
   }

   public void markDirty() {
      this.dirty = true;
   }

   public void clear() {
      this.published = List.of();
      this.building = new ArrayList<>();
      this.cursor = 0;
      this.cooldown = 0;
      this.dirty = false;
      this.sweepPcx = this.sweepPcz = Integer.MIN_VALUE;
   }

   public void tick(int radius, IncrementalScan.ChunkScanner<H> scanner) {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientWorld level = mc.world;
      ClientPlayerEntity player = mc.player;
      if (level != null && player != null) {
         if (this.orderRadius != radius) {
            this.ensureOrder(radius);
            this.dirty = true;
         }

         if (this.cursor == 0) {
            if (!this.dirty && this.cooldown > 0) {
               this.cooldown--;
               return;
            }

            this.sweepPcx = player.getChunkPos().x;
            this.sweepPcz = player.getChunkPos().z;
            this.building = new ArrayList<>();
            this.dirty = false;
         } else if (this.dirty) {
            this.sweepPcx = player.getChunkPos().x;
            this.sweepPcz = player.getChunkPos().z;
            this.cursor = 0;
            this.building = new ArrayList<>();
            this.dirty = false;
         }

         int total = this.order.length;
         int chunks = 0;

         for (int blocks = 0; this.cursor < total && chunks < this.chunksPerTick && blocks < this.blockBudgetPerTick; chunks++) {
            int packed = this.order[this.cursor];
            int dx = (short)(packed >> 16);
            int dz = (short)(packed & 65535);
            blocks += scanner.scan(level.getChunk(this.sweepPcx + dx, this.sweepPcz + dz), this.building);
            this.cursor++;
         }

         if (this.cursor >= total) {
            this.published = this.building;
            this.building = new ArrayList<>();
            this.cursor = 0;
            this.cooldown = this.idleTicks;
         }
      }
   }

   private void ensureOrder(int radius) {
      if (this.orderRadius != radius) {
         int side = 2 * radius + 1;
         Integer[] offs = new Integer[side * side];
         int i = 0;

         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               offs[i++] = (dx & 65535) << 16 | dz & 65535;
            }
         }

         Arrays.sort(offs, (a, b) -> {
            int adx = (short)(a >> 16);
            int adz = (short)(a & 65535);
            int bdx = (short)(b >> 16);
            int bdz = (short)(b & 65535);
            return Integer.compare(adx * adx + adz * adz, bdx * bdx + bdz * bdz);
         });
         int[] out = new int[offs.length];

         for (int k = 0; k < offs.length; k++) {
            out[k] = offs[k];
         }

         this.order = out;
         this.orderRadius = radius;
      }
   }

   public interface ChunkScanner<H> {
      int scan(WorldChunk var1, List<H> var2);
   }
}

