package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

public class BedESPModule extends Module {
   private final ColorSetting boxColor = new ColorSetting("Box", -1057017003);
   private final BooleanSetting showHeadFootDistinction = new BooleanSetting("HeadFootDistinction", true);
   private final NumberSetting maxDistance = new NumberSetting("Max Distance", 96.0, 16.0, 512.0, 1.0);
   private final NumberSetting lineWidth = new NumberSetting("Line Width", 1.5, 0.5, 5.0, 0.1);
   private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", true);
   private static final Map<Long, BedESPModule.Inner1> map = new ConcurrentHashMap<>();
   private static boolean bool = false;
   private static volatile boolean bool2 = false;

   public BedESPModule() {
      super("BedESP", "Highlights beds (crystal-PvP + spawnpoint detection)", Category.RENDER);
      this.run6(new Setting[]{this.boxColor, this.showHeadFootDistinction, this.maxDistance, this.lineWidth, this.throughWalls});
   }

   @Override
   public void run() {
      bool2 = true;
      run3();
      this.run5();
   }

   @Override
   public void run2() {
      bool2 = false;
      map.clear();
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null && !map.isEmpty()) {
         Vec3d var3 = class310.player.getEntityPos();
         double var4 = this.maxDistance.getValue();
         double var6 = var4 * var4;
         float var8 = this.lineWidth.getValueFloat();
         boolean var9 = this.throughWalls.getValue();
         boolean var10 = this.showHeadFootDistinction.getValue();
         int var11 = this.boxColor.getValue();
         int var12 = var10 ? intOf4(var11, 0.55F) : var11;

         for (BedESPModule.Inner1 var14 : map.values()) {
            BlockPos var15 = var14.pos();
            double var16 = var15.getX() + 0.5;
            double var18 = var15.getY() + 0.5;
            double var20 = var15.getZ() + 0.5;
            double var22 = var16 - var3.x;
            double var24 = var18 - var3.y;
            double var26 = var20 - var3.z;
            if (!(var22 * var22 + var24 * var24 + var26 * var26 > var6)) {
               int var28 = var14.isHead() ? var11 : var12;
               Box var29 = new Box(var15.getX(), var15.getY(), var15.getZ(), var15.getX() + 1.0, var15.getY() + 1.0, var15.getZ() + 1.0);
               ListUtils.run5(var29, var28, var8, var9);
            }
         }
      }
   }

   @Override
   public String getString3() {
      int var1 = map.size();
      return var1 > 0 ? "§7" + var1 : null;
   }

   public synchronized void run3() {
      if (!bool) {
         bool = true;
         ClientChunkEvents.CHUNK_LOAD.register(BedESPModule::run7);
         ClientChunkEvents.CHUNK_UNLOAD.register(BedESPModule::run16);
      }
   }

   private void run5() {
      if (class310.world != null && class310.player != null) {
         int var1 = Math.max((Integer)class310.options.getViewDistance().getValue(), 8);
         int var2 = Math.min(var1, 12);
         int var3 = (int)class310.player.getX() >> 4;
         int var4 = (int)class310.player.getZ() >> 4;
         ClientChunkManager var5 = class310.world.getChunkManager();

         for (int var6 = -var2; var6 <= var2; var6++) {
            for (int var7 = -var2; var7 <= var2; var7++) {
               WorldChunk var8;
               try {
                  var8 = var5.getWorldChunk(var3 + var6, var4 + var7, false);
               } catch (Throwable var11) {
                  var8 = null;
               }

               if (var8 != null) {
                  try {
                     run6(var8);
                  } catch (Throwable var10) {
                  }
               }
            }
         }
      }
   }

   private static void run6(WorldChunk var0) {
      int var1 = var0.getPos().x << 4;
      int var2 = var0.getPos().z << 4;
      ChunkSection[] var3 = var0.getSectionArray();
      int var4 = var0.getBottomY();

      for (int var5 = 0; var5 < var3.length; var5++) {
         ChunkSection var6 = var3[var5];
         if (var6 != null && !var6.isEmpty()) {
            boolean var7;
            try {
               var7 = var6.getBlockStateContainer().hasAny(BedESPModule::check);
            } catch (Throwable var16) {
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
                        } catch (Throwable var17) {
                           continue;
                        }

                        if (var12.getBlock() instanceof BedBlock) {
                           boolean var13;
                           try {
                              var13 = var12.get(BedBlock.PART) == BedPart.HEAD;
                           } catch (Throwable var15) {
                              var13 = false;
                           }

                           BlockPos var14 = new BlockPos(var1 + var10, var8 + var9, var2 + var11);
                           map.put(var14.asLong(), new BedESPModule.Inner1(var14.toImmutable(), var13));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static int intOf4(int var0, float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      int var2 = var0 >>> 24 & 0xFF;
      int var3 = Math.round((var0 >>> 16 & 0xFF) * var1);
      int var4 = Math.round((var0 >>> 8 & 0xFF) * var1);
      int var5 = Math.round((var0 & 0xFF) * var1);
      if (var3 > 255) {
         var3 = 255;
      }

      if (var4 > 255) {
         var4 = 255;
      }

      if (var5 > 255) {
         var5 = 255;
      }

      return var2 << 24 | var3 << 16 | var4 << 8 | var5;
   }

   private static boolean check(BlockState var0) {
      return var0.getBlock() instanceof BedBlock;
   }

   private static void run16(ClientWorld var0, WorldChunk var1) {
      if (var1 != null) {
         int var2 = var1.getPos().x;
         int var3 = var1.getPos().z;
         map.entrySet().removeIf(_e -> false);
      }
   }

   private static boolean check2(int var0, int var1, Entry var2) {
      return false;
   }

   private static void run7(ClientWorld var0, WorldChunk var1) {
      if (bool2 && var1 != null) {
         try {
            run6(var1);
         } catch (Throwable var3) {
         }
      }
   }

record Inner1(BlockPos pos, boolean head) {


   public boolean isHead() {
      return this.head;
   }
}
}

