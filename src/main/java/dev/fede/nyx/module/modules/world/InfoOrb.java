package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.WorldChunk;

public class InfoOrb extends Module {
   private static final int intVal = 10;
   private static final int intVal2 = 32;
   private static final int intVal3 = -1072623343;
   private static final int intVal4 = -12976364;
   private static final int intVal5 = -1;
   private static final int intVal6 = -4210753;
   private static final int intVal7 = -12976364;
   private final BooleanSetting showOreCounts = new BooleanSetting("ShowOreCounts", true);
   private final BooleanSetting showBiome = new BooleanSetting("ShowBiome", true);
   private final BooleanSetting showTime = new BooleanSetting("ShowTime", true);
   private final BooleanSetting showChunk = new BooleanSetting("ShowChunk", true);
   private final NumberSetting scanRadius = new NumberSetting("ScanRadius", 32.0, 8.0, 64.0, 1.0);
   private final EnumMap<InfoOrb.OreKind, Integer> enumMap = new EnumMap<>(InfoOrb.OreKind.class);
   private String string = "-";
   private int intVal8;

   public InfoOrb() {
      super("InfoOrb", "HUD panel with nearby ores, biome, time-of-day and chunk pos", Category.WORLD);
      this.run6(new Setting[]{this.showOreCounts, this.showBiome, this.showTime, this.showChunk, this.scanRadius});

      for (InfoOrb.OreKind var4 : InfoOrb.OreKind.values()) {
         this.enumMap.put(var4, 0);
      }
   }

   @Override
   public void run2() {
      if (class310.world != null && class310.player != null) {
         this.intVal8++;
         if (this.intVal8 >= 10) {
            this.intVal8 = 0;
            if (this.showOreCounts.getValue()) {
               this.run3();
            } else {
               for (Entry var2 : this.enumMap.entrySet()) {
                  var2.setValue(0);
               }
            }

            if (this.showBiome.getValue()) {
               this.string = this.getString();
            }
         }
      }
   }

   public void run3() {
      int var1 = this.scanRadius.getValueInt();
      BlockPos var2 = class310.player.getBlockPos();
      int var3 = var2.getX();
      int var4 = var2.getY();
      int var5 = var2.getZ();

      for (Entry var7 : this.enumMap.entrySet()) {
         var7.setValue(0);
      }

      int var28 = var3 - var1 >> 4;
      int var29 = var3 + var1 >> 4;
      int var8 = var5 - var1 >> 4;
      int var9 = var5 + var1 >> 4;
      int var10 = class310.world.getBottomY();
      int var11 = var10 + class310.world.getHeight();
      int var12 = Math.max(var10, var4 - 32);
      int var13 = Math.min(var11 - 1, var4 + 32);
      Mutable var14 = new Mutable();

      for (int var15 = var28; var15 <= var29; var15++) {
         for (int var16 = var8; var16 <= var9; var16++) {
            WorldChunk var17;
            try {
               var17 = class310.world.getChunkManager().getWorldChunk(var15, var16, false);
            } catch (Throwable var27) {
               var17 = null;
            }

            if (var17 != null) {
               int var18 = Math.max(var15 << 4, var3 - var1);
               int var19 = Math.min((var15 << 4) + 15, var3 + var1);
               int var20 = Math.max(var16 << 4, var5 - var1);
               int var21 = Math.min((var16 << 4) + 15, var5 + var1);

               for (int var22 = var18; var22 <= var19; var22++) {
                  for (int var23 = var20; var23 <= var21; var23++) {
                     for (int var24 = var12; var24 <= var13; var24++) {
                        var14.set(var22, var24, var23);
                        BlockState var25 = var17.getBlockState(var14);
                        InfoOrb.OreKind var26 = infoOrbOreKindOf(var25);
                        if (var26 != null) {
                           this.enumMap.merge(var26, 1, Integer::sum);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public String getString() {
      try {
         RegistryEntry var1 = class310.world.getBiomeAccess().getBiome(class310.player.getBlockPos());
         Optional var2 = var1.getKey();
         if (var2.isPresent()) {
            String var3 = ((RegistryKey)var2.get()).getValue().getPath();
            return addSetting(var3);
         }
      } catch (Throwable var4) {
      }

      return this.string;
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310 != null && class310.player != null && class310.world != null && class310.textRenderer != null) {
         TextRenderer var3 = class310.textRenderer;
         LinkedHashMap var4 = new LinkedHashMap();
         if (this.showChunk.getValue()) {
            ChunkPos var5 = class310.player.getChunkPos();
            BlockPos var6 = class310.player.getBlockPos();
            var4.put("Chunk", var5.x + ", " + var5.z + "  (" + var6.getX() + " " + var6.getY() + " " + var6.getZ() + ")");
         }

         if (this.showBiome.getValue()) {
            var4.put("Biome", this.string);
         }

         if (this.showTime.getValue()) {
            var4.put("Time", this.getString2());
         }

         if (this.showOreCounts.getValue()) {
            for (InfoOrb.OreKind var8 : InfoOrb.OreKind.values()) {
               int var9 = this.enumMap.getOrDefault(var8, 0);
               if (var9 > 0) {
                  var4.put(var8.string, Integer.toString(var9));
               }
            }
         }

         if (!var4.isEmpty()) {
            byte var23 = 6;
            byte var25 = 4;
            byte var26 = 11;
            int var27 = 0;
            int var28 = 0;

            for (Entry var11 : ((java.util.Map<?,?>)var4).entrySet()) {
               var27 = Math.max(var27, var3.getWidth((String)var11.getKey()));
               var28 = Math.max(var28, var3.getWidth((String)var11.getValue()));
            }

            byte var29 = 8;
            byte var30 = 12;
            int var12 = var23 + Math.max(var3.getWidth("Info Orb"), var27 + var29 + var28) + var23;
            int var13 = 16 + var4.size() * var26 + var25;
            int var16 = 6 + var12;
            int var17 = 6 + var13;
            var1.fill(6, 6, var16, var17, -1072623343);
            var1.fill(6, 6, 7, var17, -12976364);
            var1.drawTextWithShadow(var3, "Info Orb", 12, 10, -12976364);
            byte var18 = 22;

            for (Entry var20 : ((java.util.Map<?,?>)var4).entrySet()) {
               var1.drawTextWithShadow(var3, (String)var20.getKey(), 12, var18, -1);
               int var21 = var16 - var23 - var3.getWidth((String)var20.getValue());
               var1.drawTextWithShadow(var3, (String)var20.getValue(), var21, var18, -4210753);
               var18 += var26;
            }
         }
      }
   }

   public String getString2() {
      long var1 = Math.floorMod(class310.world.getTimeOfDay(), 24000L);
      long var3;
      String var5;
      if (var1 < 12000L) {
         var3 = 12000L - var1;
         var5 = "Nightfall";
      } else if (var1 < 13000L) {
         var3 = 23000L - var1;
         var5 = "Daybreak";
      } else if (var1 < 23000L) {
         var3 = 23000L - var1;
         var5 = "Daybreak";
      } else {
         var3 = 24000L - var1;
         var5 = "Nightfall";
      }

      long var6 = var3 / 20L;
      long var8 = var6 / 60L;
      long var10 = var6 % 60L;
      return var5 + " " + var8 + "m " + var10 + "s";
   }

   private static String addSetting(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         StringBuilder var1 = new StringBuilder(var0.length());
         boolean var2 = true;

         for (int var3 = 0; var3 < var0.length(); var3++) {
            char var4 = var0.charAt(var3);
            if (var4 == '_') {
               var1.append(' ');
               var2 = true;
            } else if (var2) {
               var1.append(Character.toUpperCase(var4));
               var2 = false;
            } else {
               var1.append(var4);
            }
         }

         return var1.toString();
      } else {
         return "-";
      }
   }

   private static InfoOrb.OreKind infoOrbOreKindOf(BlockState var0) {
      if (var0 == null) {
         return null;
      } else {
         Block var1 = var0.getBlock();
         if (var1 == Blocks.AIR) {
            return null;
         } else if (var0.isIn(BlockTags.DIAMOND_ORES)) {
            return InfoOrb.OreKind.DIAMOND;
         } else if (var0.isIn(BlockTags.GOLD_ORES)) {
            return InfoOrb.OreKind.GOLD;
         } else if (var0.isIn(BlockTags.IRON_ORES)) {
            return InfoOrb.OreKind.IRON;
         } else if (var0.isIn(BlockTags.REDSTONE_ORES)) {
            return InfoOrb.OreKind.REDSTONE;
         } else if (var0.isIn(BlockTags.COAL_ORES)) {
            return InfoOrb.OreKind.COAL;
         } else if (var0.isIn(BlockTags.LAPIS_ORES)) {
            return InfoOrb.OreKind.LAPIS;
         } else if (var0.isIn(BlockTags.COPPER_ORES)) {
            return InfoOrb.OreKind.COPPER;
         } else if (var0.isIn(BlockTags.EMERALD_ORES)) {
            return InfoOrb.OreKind.EMERALD;
         } else {
            return var1 == Blocks.ANCIENT_DEBRIS ? InfoOrb.OreKind.ANCIENT_DEBRIS : null;
         }
      }
   }

   @Override
   public String getString3() {
      return "§7r=" + this.scanRadius.getValueInt();
   }

   private static String stringOf(double var0) {
      return String.format(Locale.ROOT, "%.2f", var0);
   }

   private static enum OreKind {
      DIAMOND("Diamond"),
      ANCIENT_DEBRIS("Ancient Debris"),
      EMERALD("Emerald"),
      GOLD("Gold"),
      IRON("Iron"),
      LAPIS("Lapis"),
      REDSTONE("Redstone"),
      COPPER("Copper"),
      COAL("Coal");

      final String string;
      private static final InfoOrb.OreKind[] infoOrbOreKindArray = getInfoOrbOreKindArray();

      private OreKind(String label) {
         this.string = label;
      }

      private static InfoOrb.OreKind[] getInfoOrbOreKindArray() {
         return new InfoOrb.OreKind[]{DIAMOND, ANCIENT_DEBRIS, EMERALD, GOLD, IRON, LAPIS, REDSTONE, COPPER, COAL};
      }
   }
}

