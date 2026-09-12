package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.block.BlockState;
import dev.fede.nyx.render.ListUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class PrimeChunkFinderModule extends Module {
   private final NumberSetting minDistance = new NumberSetting("MinDistanceFromSpawn", 200.0, 0.0, 10000.0, 50.0);
   private final NumberSetting threshold = new NumberSetting("Threshold", 1.0, 1.0, 64.0, 1.0);
   private final NumberSetting alpha = new NumberSetting("Alpha", 0.55, 0.05, 1.0, 0.05);
   private final ModeSetting palette = new ModeSetting("Palette", "Amber", "Amber", "Cyan", "Magenta", "Lime");
   private final NumberSetting fixedY = new NumberSetting("FixedY", 60.0, -64.0, 320.0, 1.0);
   private final NumberSetting thickness = new NumberSetting("Thickness", 0.5, 0.05, 4.0, 0.05);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final BooleanSetting outline = new BooleanSetting("Outline", true);
   private final NumberSetting outlineWidth = new NumberSetting("OutlineWidth", 1.5, 0.5, 4.0, 0.1);
   private final BooleanSetting stickyMemory = new BooleanSetting("StickyMemory", true);
   private final NumberSetting maxRenderDistance = new NumberSetting("MaxRenderDistance", 512.0, 128.0, 2048.0, 32.0);
   private static final int intVal = 16752688;
   private static final int intVal2 = 3399167;
   private static final int intVal3 = 14696699;
   private static final int intVal4 = 7798531;
   private static final int[][] intArray = new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
   private static final Set<Long> set = ConcurrentHashMap.newKeySet();
   private static final Set<Long> set2 = ConcurrentHashMap.newKeySet();
   private static final Map<Long, Double> map = new ConcurrentHashMap<>();
   private static boolean bool = false;
   private static volatile PrimeChunkFinderModule primeChunkFinderModule;
   private volatile World class1937;

   public PrimeChunkFinderModule() {
      super(
         "PrimeChunkFinder",
         "Fluid-flow chunk fingerprinting (Meteor NewChunks port) — flags chunks with stranded-flow signatures of past player activity",
         Category.DONUTSMP
      );
      this.run6(
         new Setting[]{
            this.minDistance,
            this.threshold,
            this.alpha,
            this.palette,
            this.fixedY,
            this.thickness,
            this.throughWalls,
            this.outline,
            this.outlineWidth,
            this.stickyMemory,
            this.maxRenderDistance
         }
      );
      run3();
   }

   public synchronized void run3() {
      if (!bool) {
         bool = true;
         ClientChunkEvents.CHUNK_LOAD.register(PrimeChunkFinderModule::run7);
         ClientChunkEvents.CHUNK_UNLOAD.register(PrimeChunkFinderModule::run16);
      }
   }

   @Override
   public void run() {
      set.clear();
      set2.clear();
      map.clear();
      this.class1937 = class310 != null ? class310.world : null;
      primeChunkFinderModule = this;

      try {
         if (class310 != null && class310.world != null && class310.player != null) {
            int var1 = class310.player.getChunkPos().x;
            int var2 = class310.player.getChunkPos().z;
            int var3 = class310.options != null ? (Integer)class310.options.getViewDistance().getValue() : 8;

            for (int var4 = -var3; var4 <= var3; var4++) {
               for (int var5 = -var3; var5 <= var3; var5++) {
                  WorldChunk var6 = class310.world.getChunkManager().getWorldChunk(var1 + var4, var2 + var5, false);
                  if (var6 != null) {
                     this.run5(var6);
                  }
               }
            }
         }
      } catch (Throwable var7) {
      }
   }

   @Override
   public void run2() {
      primeChunkFinderModule = null;
      set.clear();
      set2.clear();
      map.clear();
      this.class1937 = null;
   }

   public void run4() {
      if (class310 != null) {
         ClientWorld var1 = class310.world;
         if (var1 != this.class1937) {
            this.class1937 = var1;
            set.clear();
            set2.clear();
            map.clear();
            if (var1 != null && class310.player != null) {
               try {
                  int var2 = class310.player.getChunkPos().x;
                  int var3 = class310.player.getChunkPos().z;
                  int var4 = class310.options != null ? (Integer)class310.options.getViewDistance().getValue() : 8;

                  for (int var5 = -var4; var5 <= var4; var5++) {
                     for (int var6 = -var4; var6 <= var4; var6++) {
                        WorldChunk var7 = var1.getChunkManager().getWorldChunk(var2 + var5, var3 + var6, false);
                        if (var7 != null) {
                           this.run5(var7);
                        }
                     }
                  }
               } catch (Throwable var8) {
               }
            }
         }
      }
   }

   private void run5(WorldChunk var1) {
      int var2 = var1.getPos().x;
      int var3 = var1.getPos().z;
      long var4 = ChunkPos.toLong(var2, var3);
      if (set2.add(var4)) {
         long var6 = ((long)var2 << 4) + 8L;
         long var8 = ((long)var3 << 4) + 8L;
         long var10 = this.minDistance.getValueInt();
         if (var6 * var6 + var8 * var8 >= var10 * var10) {
            ChunkSection[] var12 = var1.getSectionArray();
            if (var12 != null && var12.length != 0) {
               int var13 = var1.getBottomY();
               int var14 = Math.max(1, this.threshold.getValueInt());
               int var15 = 0;

               for (int var16 = 0; var16 < var12.length; var16++) {
                  ChunkSection var17 = var12[var16];
                  if (var17 != null && !var17.isEmpty()) {
                     for (int var18 = 0; var18 < 16; var18++) {
                        for (int var19 = 0; var19 < 16; var19++) {
                           for (int var20 = 0; var20 < 16; var20++) {
                              BlockState var21 = var17.getBlockState(var19, var18, var20);
                              FluidState var22 = var21.getFluidState();
                              if (!var22.isEmpty() && !var22.isStill() && !check(var12, var16, var19, var18, var20)) {
                                 if (++var15 >= var14) {
                                    set.add(var4);
                                    return;
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

   private static boolean check(ChunkSection[] var0, int var1, int var2, int var3, int var4) {
      for (int[] var8 : intArray) {
         int var9 = var2 + var8[0];
         int var10 = var3 + var8[1];
         int var11 = var4 + var8[2];
         if (var9 >= 0 && var9 <= 15 && var11 >= 0 && var11 <= 15) {
            int var12 = var1;
            if (var10 < 0) {
               var12 = var1 - 1;
               var10 = 15;
            } else if (var10 > 15) {
               var12 = var1 + 1;
               var10 = 0;
            }

            if (var12 >= 0 && var12 < var0.length) {
               ChunkSection var13 = var0[var12];
               if (var13 != null && !var13.isEmpty()) {
                  FluidState var14 = var13.getBlockState(var9, var10, var11).getFluidState();
                  if (!var14.isEmpty() && var14.isStill()) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public void run6(DrawContext param1, float param2) {
      if (class310 == null || class310.world == null || class310.player == null) {
         return;
      }

      if (!set.isEmpty()) {
         int var3 = Math.max(15, Math.min(255, (int)Math.round(this.alpha.getValue() * 255.0)));
         int var4;
         switch (this.palette.getMode()) {
            case "Cyan":
               var4 = 3399679;
               break;
            case "Magenta":
               var4 = 14696699;
               break;
            case "Lime":
               var4 = 7798531;
               break;
            default:
               var4 = 16752688;
         }

         int var5 = var3 << 24 | var4;
         int var6 = Math.min(255, var3 + 60) << 24 | var4;
         double var7 = this.fixedY.getValue();
         double var9 = Math.max(0.05, this.thickness.getValue());
         boolean var11 = this.throughWalls.getValue();
         boolean var12 = this.outline.getValue();
         float var13 = this.outlineWidth.getValueFloat();
         double var14 = this.maxRenderDistance.getValue();
         double var16 = var14 * var14;
         double var18 = class310.player.getX();
         double var20 = class310.player.getZ();

         for (long var23 : set) {
            int var24 = ChunkPos.getPackedX(var23);
            int var25 = ChunkPos.getPackedZ(var23);
            double var26 = (double)var24 * 16.0;
            double var28 = (double)var25 * 16.0;
            double var30 = var26 + 8.0 - var18;
            double var32 = var28 + 8.0 - var20;
            if (!(var30 * var30 + var32 * var32 > var16)) {
               Box var34 = new Box(var26, var7, var28, var26 + 16.0, var7 + var9, var28 + 16.0);

               try {
                  ListUtils.run3(var34, var5, var11);
                  if (var12) {
                     ListUtils.run5(var34, var6, var13, var11);
                  }
               } catch (Throwable var37) {
               }
            }
         }
      }
   }

   private double doubleOf(int var1, int var2, long var3) {
      Double var5 = map.get(var3);
      if (var5 != null) {
         return var5;
      } else {
         double var6;
         try {
            int var8 = (var1 << 4) + 8;
            int var9 = (var2 << 4) + 8;
            var6 = class310.world.getTopY(Type.WORLD_SURFACE, var8, var9);
         } catch (Throwable var10) {
            var6 = 63.0;
         }

         map.put(var3, var6);
         return var6;
      }
   }

   @Override
   public String getString3() {
      int var1 = set.size();
      return var1 > 0 ? "§7" + var1 : null;
   }

   private static void run16(ClientWorld var0, WorldChunk var1) {
      PrimeChunkFinderModule var2 = primeChunkFinderModule;
      if (var2 != null && var2.isEnabled3() && var1 != null) {
         if (!var2.stickyMemory.getValue()) {
            try {
               long var3 = ChunkPos.toLong(var1.getPos().x, var1.getPos().z);
               set.remove(var3);
               set2.remove(var3);
               map.remove(var3);
            } catch (Throwable var5) {
            }
         }
      }
   }

   private static void run7(ClientWorld var0, WorldChunk var1) {
      PrimeChunkFinderModule var2 = primeChunkFinderModule;
      if (var2 != null && var2.isEnabled3() && var1 != null) {
         try {
            var2.run5(var1);
         } catch (Throwable var4) {
         }
      }
   }
}

