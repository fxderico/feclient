package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.chunk.WorldChunk;

public class BlockEntityDebugModule extends Module {
   private static final int intVal = -1325415858;
   private static final int intVal2 = -1325434317;
   private static final int intVal3 = -1325448022;
   private static final int intVal4 = -1328794676;
   private static final int intVal5 = -1332043603;
   private final NumberSetting radius = new NumberSetting("Radius", 12.0, 4.0, 32.0, 1.0);
   private final BooleanSetting showHUD = new BooleanSetting("ShowHUD", true);

   public BlockEntityDebugModule() {
      super("BlockEntityDebug", "Colour-cubes every loaded block entity near you + counts chip", Category.DONUTSMP);
      this.run6(new Setting[]{this.radius, this.showHUD});
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         double var3 = this.radius.getValue();
         double var5 = var3 * var3;
         int var7 = (int)Math.floor(class310.player.getX()) >> 4;
         int var8 = (int)Math.floor(class310.player.getZ()) >> 4;
         int var9 = Math.max(1, (int)Math.ceil(var3) >> 4);
         var9 = Math.max(1, (int)Math.ceil(var3 + 15.0) >> 4);
         double var10 = class310.player.getX();
         double var12 = class310.player.getY();
         double var14 = class310.player.getZ();
         LinkedHashMap var16 = new LinkedHashMap();
         var16.put("Chests", 0);
         var16.put("Furnaces", 0);
         var16.put("Spawners", 0);
         var16.put("Hoppers", 0);
         var16.put("Other", 0);

         for (int var17 = -var9; var17 <= var9; var17++) {
            for (int var18 = -var9; var18 <= var9; var18++) {
               WorldChunk var19;
               try {
                  var19 = class310.world.getChunkManager().getWorldChunk(var7 + var17, var8 + var18, false);
               } catch (Throwable var40) {
                  var19 = null;
               }

               if (var19 != null) {
                  Map var20;
                  try {
                     var20 = var19.getBlockEntities();
                  } catch (Throwable var41) {
                     continue;
                  }

                  if (var20 != null && !var20.isEmpty()) {
                     for (Entry var22 : ((java.util.Map<?,?>)var20).entrySet()) {
                        BlockPos var23 = (BlockPos)var22.getKey();
                        double var24 = var23.getX() + 0.5;
                        double var26 = var23.getY() + 0.5;
                        double var28 = var23.getZ() + 0.5;
                        double var30 = var24 - var10;
                        double var32 = var26 - var12;
                        double var34 = var28 - var14;
                        if (!(var30 * var30 + var32 * var32 + var34 * var34 > var5)) {
                           BlockEntity var36 = (BlockEntity)var22.getValue();
                           if (var36 != null) {
                              BlockEntityType var39 = var36.getType();
                              int var37;
                              String var38;
                              if (var39 == BlockEntityType.CHEST
                                 || var39 == BlockEntityType.TRAPPED_CHEST
                                 || var39 == BlockEntityType.ENDER_CHEST
                                 || var39 == BlockEntityType.BARREL
                                 || var39 == BlockEntityType.SHULKER_BOX) {
                                 var37 = -1325415858;
                                 var38 = "Chests";
                              } else if (var39 == BlockEntityType.FURNACE || var39 == BlockEntityType.BLAST_FURNACE || var39 == BlockEntityType.SMOKER) {
                                 var37 = -1325434317;
                                 var38 = "Furnaces";
                              } else if (var39 == BlockEntityType.MOB_SPAWNER || var39 == BlockEntityType.TRIAL_SPAWNER) {
                                 var37 = -1325448022;
                                 var38 = "Spawners";
                              } else if (var39 == BlockEntityType.HOPPER) {
                                 var37 = -1328794676;
                                 var38 = "Hoppers";
                              } else {
                                 var37 = -1332043603;
                                 var38 = "Other";
                              }

                              var16.merge(var38, 1, (java.util.function.BiFunction<Integer,Integer,Integer>)Integer::sum);
                              ListUtils.run5(
                                 new Box(var24 - 0.125, var26 - 0.125, var28 - 0.125, var24 + 0.125, var26 + 0.125, var28 + 0.125), var37, 1.5F, true
                              );
                           }
                        }
                     }
                  }
               }
            }
         }

         if (this.showHUD.getValue()) {
            this.run(var1, var16);
         }
      }
   }

   private void run(DrawContext var1, Map<String, Integer> var2) {
      TextRenderer var3 = class310.textRenderer;
      if (var3 != null) {
         LinkedHashMap var4 = new LinkedHashMap();
         int var5 = 0;

         for (Entry var7 : var2.entrySet()) {
            int var8 = (Integer)var7.getValue();
            var5 += var8;
            if (var8 > 0) {
               var4.put((String)var7.getKey(), var8);
            }
         }

         byte var22 = 11;
         int var23 = 1 + var4.size();
         int var24 = var3.getWidth("Block Entities  " + var5);

         for (Entry var10 : ((java.util.Map<?,?>)var4).entrySet()) {
            int var11 = var3.getWidth((String)var10.getKey() + "  " + var10.getValue());
            if (var11 > var24) {
               var24 = var11;
            }
         }

         byte var25 = 8;
         byte var26 = 6;
         int var27 = var24 + 16;
         int var12 = 12 + var23 * var22;
         var1.fill(6, 60, 6 + var27, 60 + var12, -535817448);
         var1.fill(6, 60, 9, 60 + var12, -10959617);
         byte var18 = 66;
         var1.drawText(var3, "Block Entities  " + var5, 16, var18, -10959617, false);
         var18 = 77;

         for (Entry var20 : ((java.util.Map<?,?>)var4).entrySet()) {
            int var21 = intOf2((String)var20.getKey());
            var1.drawText(var3, (String)var20.getKey() + "  " + var20.getValue(), 16, var18, var21, false);
            var18 += var22;
         }
      }
   }

   private static int intOf2(String var0) {
      switch (var0.hashCode()) {
         case -1534504289:
            if (var0.equals("Hoppers")) {
               return -3394612;
            }
            break;
         case 1569686075:
            if (var0.equals("Furnaces")) {
               return -34253;
            }
            break;
         case 2017322418:
            if (var0.equals("Chests")) {
               return -15794;
            }
            break;
         case 2130838571:
            if (var0.equals("Spawners")) {
               return -47958;
            }
      }

      return -6643539;
   }
}

