package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.storage.Chest;
import dev.fede.nyx.storage.Chest$KindUtils;
import java.util.Iterator;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

public class CauldronESPModule extends Module {
   private final BooleanSetting showEmpty = new BooleanSetting("Empty", true);
   private final BooleanSetting showWater = new BooleanSetting("Water", true);
   private final BooleanSetting showLava = new BooleanSetting("Lava", true);
   private final BooleanSetting showPowderSnow = new BooleanSetting("PowderSnow", true);
   private final ColorSetting colorEmpty = new ColorSetting("EmptyColor", -1062557014);
   private final ColorSetting colorWater = new ColorSetting("WaterColor", -1070364417);
   private final ColorSetting colorLava = new ColorSetting("LavaColor", -1057003998);
   private final ColorSetting colorPowderSnow = new ColorSetting("PowderSnowColor", -1058078721);
   private final BooleanSetting fillIndicator = new BooleanSetting("FillIndicator", true);
   private final NumberSetting maxDistance = new NumberSetting("Max Distance", 96.0, 16.0, 512.0, 1.0);
   private final NumberSetting lineWidth = new NumberSetting("Line Width", 1.5, 0.5, 5.0, 0.1);
   private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", true);
   private static boolean bool = false;
   private static volatile boolean bool2 = false;

   public CauldronESPModule() {
      super("CauldronESP", "Highlights cauldrons with per-variant colour + fill level", Category.RENDER);
      this.run6(
         new Setting[]{
            this.showEmpty,
            this.showWater,
            this.showLava,
            this.showPowderSnow,
            this.colorEmpty,
            this.colorWater,
            this.colorLava,
            this.colorPowderSnow,
            this.fillIndicator,
            this.maxDistance,
            this.lineWidth,
            this.throughWalls
         }
      );
      this.colorEmpty.setVisible(this::getBoolean4);
      this.colorWater.setVisible(this::getBoolean3);
      this.colorLava.setVisible(this::getBoolean2);
      this.colorPowderSnow.setVisible(this::getBoolean);
   }

   @Override
   public void run() {
      bool2 = true;
      if (!bool) {
         bool = true;
         ClientChunkEvents.CHUNK_LOAD.register(CauldronESPModule::run7);
         ClientChunkEvents.CHUNK_UNLOAD.register(CauldronESPModule::run16);
      }

      if (class310.world != null && class310.player != null) {
         int var1 = Math.max((Integer)class310.options.getViewDistance().getValue(), 8);
         int var2 = Math.min(var1, 12);
         int var3 = (int)class310.player.getX() >> 4;
         int var4 = (int)class310.player.getZ() >> 4;
         ClientChunkManager var5 = class310.world.getChunkManager();
         Chest$KindUtils.runSilently(() -> this.run3());
      }
   }

   @Override
   public void run2() {
      bool2 = false;
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         Vec3d var3 = class310.player.getEntityPos();
         double var4 = this.maxDistance.getValue();
         double var6 = var4 * var4;
         float var8 = this.lineWidth.getValueFloat();
         boolean var9 = this.throughWalls.getValue();
         boolean var10 = this.fillIndicator.getValue();
         Iterator var11 = Chest$KindUtils.all().iterator();

         while (true) {
            BlockPos var14;
            BlockState var15;
            while (true) {
               if (!var11.hasNext()) {
                  return;
               }

               Chest var12 = (Chest)var11.next();
               if (var12.kind() == Chest.Kind.CAULDRON) {
                  Vec3d var13 = var12.center();
                  if (!(var13.squaredDistanceTo(var3) > var6)) {
                     var14 = var12.pos();

                     try {
                        var15 = class310.world.getBlockState(var14);
                        break;
                     } catch (Throwable var22) {
                     }
                  }
               }
            }

            Block var16 = var15.getBlock();
            int var17;
            int var18;
            byte var19;
            if (var16 == Blocks.CAULDRON) {
               if (!this.showEmpty.getValue()) {
                  continue;
               }

               var17 = this.colorEmpty.getValue();
               var18 = 0;
               var19 = 3;
            } else if (var16 == Blocks.WATER_CAULDRON) {
               if (!this.showWater.getValue()) {
                  continue;
               }

               var17 = this.colorWater.getValue();
               var18 = intOf(var15);
               var19 = 3;
            } else if (var16 == Blocks.LAVA_CAULDRON) {
               if (!this.showLava.getValue()) {
                  continue;
               }

               var17 = this.colorLava.getValue();
               var18 = 3;
               var19 = 3;
            } else {
               if (var16 != Blocks.POWDER_SNOW_CAULDRON || !this.showPowderSnow.getValue()) {
                  continue;
               }

               var17 = this.colorPowderSnow.getValue();
               var18 = intOf(var15);
               var19 = 3;
            }

            int var20 = var10 ? intOf2(var17, var18, var19) : var17;
            Box var21 = new Box(var14.getX(), var14.getY(), var14.getZ(), var14.getX() + 1.0, var14.getY() + 1.0, var14.getZ() + 1.0);
            ListUtils.run5(var21, var20, var8, var9);
         }
      }
   }

   private static int intOf(BlockState var0) {
      try {
         return (Integer)var0.get(LeveledCauldronBlock.LEVEL);
      } catch (Throwable var2) {
         return 3;
      }
   }

   private static int intOf2(int var0, int var1, int var2) {
      int var3 = var0 >>> 24 & 0xFF;
      float var4 = var2 > 0 ? (float)var1 / var2 : 1.0F;
      if (var4 < 0.0F) {
         var4 = 0.0F;
      }

      if (var4 > 1.0F) {
         var4 = 1.0F;
      }

      float var5 = 0.4F + 0.6F * var4;
      int var6 = Math.round(var3 * var5);
      if (var6 < 32) {
         var6 = 32;
      }

      if (var6 > 255) {
         var6 = 255;
      }

      return var6 << 24 | var0 & 16777215;
   }

   @Override
   public String getString3() {
      int var1 = 0;

      for (Chest var3 : Chest$KindUtils.all()) {
         if (var3.kind() == Chest.Kind.CAULDRON) {
            var1++;
         }
      }

      return var1 > 0 ? "§7" + var1 : null;
   }

   private static void run3(int var0, ClientChunkManager var1, int var2, int var3) {
      for (int var4 = -var0; var4 <= var0; var4++) {
         for (int var5 = -var0; var5 <= var0; var5++) {
            WorldChunk var6;
            try {
               var6 = var1.getWorldChunk(var2 + var4, var3 + var5, false);
            } catch (Throwable var8) {
               var6 = null;
            }

            if (var6 != null) {
               Chest$KindUtils.onChunkLoad(var6);
            }
         }
      }
   }

   private static void run16(ClientWorld var0, WorldChunk var1) {
      if (bool2 && var1 != null) {
         Chest$KindUtils.onChunkUnload(var1);
      }
   }

   private static void run7(ClientWorld var0, WorldChunk var1) {
      if (bool2 && var1 != null) {
         Chest$KindUtils.runSilently(() -> {});
      }
   }

   private static void run5(WorldChunk var0) {
      Chest$KindUtils.onChunkLoad(var0);
   }

   private Boolean getBoolean() {
      return this.showPowderSnow.getValue();
   }

   private Boolean getBoolean2() {
      return this.showLava.getValue();
   }

   private Boolean getBoolean3() {
      return this.showWater.getValue();
   }

   private Boolean getBoolean4() {
      return this.showEmpty.getValue();
   }
}

