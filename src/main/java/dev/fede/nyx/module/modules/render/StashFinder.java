package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.storage.Chest;
import dev.fede.nyx.storage.Chest$KindUtils;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.d$aUtils;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;

public class StashFinder extends Module {
   private static final int intVal = 8;
   private static final int intVal2 = 40;
   private static final int intVal3 = 16;
   private static final int intVal4 = 2;
   private final NumberSetting realThreshold = new NumberSetting("RealThreshold", 40.0, -100.0, 200.0, 1.0);
   private final NumberSetting ambiguousThreshold = new NumberSetting("AmbiguousThreshold", -10.0, -100.0, 100.0, 1.0);
   private final ModeSetting showBand = new ModeSetting("Show", "RealAndAmbiguous", "RealOnly", "RealAndAmbiguous", "All");
   private final BooleanSetting notifyOnReal = new BooleanSetting("NotifyOnReal", true);
   private final BooleanSetting playSound = new BooleanSetting("PlaySound", true);
   private final BooleanSetting showTag = new BooleanSetting("ShowScoreTag", true);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final ColorSetting realColor = new ColorSetting("RealColor", -533070060);
   private final ColorSetting ambiguousColor = new ColorSetting("AmbiguousColor", -520104704);
   private final ColorSetting fakeColor = new ColorSetting("FakeColor", -2130759632);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.5, 0.5, 5.0, 0.1);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 256.0, 32.0, 1024.0, 8.0);
   private final Map<Long, StashFinder.Inner1> map = new ConcurrentHashMap<>();
   private final Set<Long> set = ConcurrentHashMap.newKeySet();
   private final Map<Long, StashFinder.Band> map2 = new ConcurrentHashMap<>();
   private int intVal5 = 0;
   private long longVal = 0L;

   public StashFinder() {
      super("StashFinder", "Classifies chest clusters as real bases vs fake stashes", Category.RENDER);
      this.run6(
         new Setting[]{
            this.realThreshold,
            this.ambiguousThreshold,
            this.showBand,
            this.notifyOnReal,
            this.playSound,
            this.showTag,
            this.throughWalls,
            this.realColor,
            this.ambiguousColor,
            this.fakeColor,
            this.lineWidth,
            this.maxDistance
         }
      );
   }

   @Override
   public void run() {
      this.map.clear();
      this.set.clear();
      this.map2.clear();
      this.intVal5 = 40;
      System.out.println("[StashFinder] enabled; will rebuild on next tick");
   }

   @Override
   public void run2() {
      this.map.clear();
      this.set.clear();
      this.map2.clear();
      System.out.println("[StashFinder] disabled; cleared cluster cache");
   }

   @Override
   public void run3() {
      if (class310.world != null && class310.player != null) {
         this.intVal5++;
         if (this.intVal5 >= 40) {
            this.intVal5 = 0;
            this.run4();
         }
      }
   }

   private void run4() {
      long var1 = System.nanoTime();
      ArrayList var3 = new ArrayList<>(Chest$KindUtils.all());
      HashMap var4 = new HashMap();
      if (var3.isEmpty()) {
         this.run5(var4, 0L, 0, 0, 0);
         System.out.println("[StashFinder] rebuild=" + ++this.longVal + " clusters=0 (no chests tracked yet)");
      } else {
         int var5 = var3.size();
         int[] var6 = new int[var5];
         int var7 = 0;

         while (var7 < var5) {
            var6[var7] = var7++;
         }

         for (int var15 = 0; var15 < var5; var15++) {
            Chest var8 = (Chest)var3.get(var15);
            BlockPos var9 = var8.pos();

            for (int var10 = var15 + 1; var10 < var5; var10++) {
               Chest var11 = (Chest)var3.get(var10);
               BlockPos var12 = var11.pos();
               if (Math.abs(var9.getX() - var12.getX()) <= 8 && Math.abs(var9.getY() - var12.getY()) <= 8 && Math.abs(var9.getZ() - var12.getZ()) <= 8) {
                  int var13 = Math.abs(var9.getX() - var12.getX()) + Math.abs(var9.getY() - var12.getY()) + Math.abs(var9.getZ() - var12.getZ());
                  if (var13 <= 8) {
                     run7(var6, var15, var10);
                  }
               }
            }
         }

         HashMap var16 = new HashMap();

         for (int var17 = 0; var17 < var5; var17++) {
            int var19 = intOf2(var6, var17);
            ((java.util.List)var16.computeIfAbsent(var19, k -> new java.util.ArrayList<>())).add((Chest)var3.get(var17));
         }

         int var18 = 0;
         int var20 = 0;
         int var21 = 0;

         for (Entry var24 : ((java.util.Map<?,?>)var16).entrySet()) {
            List var25 = (List)var24.getValue();
            if (var25.size() >= 2) {
               StashFinder.Inner1 var14 = this.stashFinderaOf(var25);
               var4.put(var14.longVal, var14);
               switch (var14.stashFinderBand) {
                  case REAL:
                     var18++;
                     break;
                  case AMBIGUOUS:
                     var20++;
                     break;
                  case FAKE:
                     var21++;
               }
            }
         }

         long var23 = (System.nanoTime() - var1) / 1000000L;
         this.run5(var4, var23, var18, var20, var21);
      }
   }

   private void run5(Map<Long, StashFinder.Inner1> var1, long var2, int var4, int var5, int var6) {
      HashSet var7 = new HashSet();

      for (StashFinder.Inner1 var9 : var1.values()) {
         if (var9.stashFinderBand == StashFinder.Band.REAL) {
            var7.add(var9.longVal);
         }
      }

      HashSet var13 = new HashSet(var7);
      var13.removeAll(this.set);
      this.map.clear();
      this.map.putAll(var1);

      for (StashFinder.Inner1 var10 : var1.values()) {
         StashFinder.Band var11 = this.map2.get(var10.longVal);
         if (var11 != var10.stashFinderBand) {
            System.out
               .println(
                  "[StashFinder] cluster "
                     + Long.toHexString(var10.longVal)
                     + " @ "
                     + var10.class2338.getX()
                     + ","
                     + var10.class2338.getY()
                     + ","
                     + var10.class2338.getZ()
                     + " band="
                     + var10.stashFinderBand
                     + " score="
                     + var10.intVal3
                     + (var11 == null ? " (new)" : " (was null)")
               );
         }
      }

      this.map2.keySet().retainAll(var1.keySet());

      for (StashFinder.Inner1 var17 : var1.values()) {
         this.map2.put(var17.longVal, var17.stashFinderBand);
      }

      this.set.clear();
      this.set.addAll(var7);
      System.out
         .println(
            "[StashFinder] rebuild="
               + ++this.longVal
               + " clusters="
               + var1.size()
               + " real="
               + var4
               + " ambig="
               + var5
               + " fake="
               + var6
               + " dt="
               + var2
               + "ms"
         );
      if (this.notifyOnReal.getValue() && !var13.isEmpty()) {
         for (Long var18 : (java.util.List<Long>)var13) {
            StashFinder.Inner1 var19 = (StashFinder.Inner1)var1.get(var18);
            if (var19 != null) {
               String var12 = "score="
                  + var19.intVal3
                  + " size="
                  + var19.intVal
                  + " @ "
                  + var19.class2338.getX()
                  + " "
                  + var19.class2338.getY()
                  + " "
                  + var19.class2338.getZ();
               NotificationUtils.run("StashFinder: REAL base", var12, INFO.UNKNOWN_2, 6000L);
               if (this.playSound.getValue()) {
                  d$aUtils.run2();
               }
            }
         }
      }
   }

   private StashFinder.Inner1 stashFinderaOf(List<Chest> var1) {
      int var2 = var1.size();
      long var3 = 0L;
      long var5 = 0L;
      long var7 = 0L;
      int var9 = Integer.MAX_VALUE;
      int var10 = Integer.MAX_VALUE;
      int var11 = Integer.MAX_VALUE;
      int var12 = Integer.MIN_VALUE;
      int var13 = Integer.MIN_VALUE;
      int var14 = Integer.MIN_VALUE;
      long var15 = Long.MAX_VALUE;
      EnumSet var17 = EnumSet.noneOf(Chest.Kind.class);
      HashSet var18 = new HashSet();

      for (Chest var20 : var1) {
         BlockPos var21 = var20.pos();
         var3 += var21.getX();
         var5 += var21.getY();
         var7 += var21.getZ();
         if (var21.getX() < var9) {
            var9 = var21.getX();
         }

         if (var21.getY() < var10) {
            var10 = var21.getY();
         }

         if (var21.getZ() < var11) {
            var11 = var21.getZ();
         }

         if (var21.getX() > var12) {
            var12 = var21.getX();
         }

         if (var21.getY() > var13) {
            var13 = var21.getY();
         }

         if (var21.getZ() > var14) {
            var14 = var21.getZ();
         }

         var17.add(var20.kind());
         var18.add(var21.getY());
         long var22 = var21.asLong();
         if (var22 < var15) {
            var15 = var22;
         }
      }

      BlockPos var32 = new BlockPos((int)(var3 / var2), (int)(var5 / var2), (int)(var7 / var2));
      Box var33 = new Box(var9, var10, var11, var12 + 1.0, var13 + 1.0, var14 + 1.0);
      StashFinder.Inner2 var34 = this.stashFinderbOf(var32);
      int var35 = 0;
      StringBuilder var23 = new StringBuilder(64);
      if (var17.size() >= 2) {
         var35 += 30;
         var23.append("+30(multiKind:").append(var17.size()).append(") ");
      }

      int var24 = Math.min(40, var34.intVal * 20);
      if (var24 > 0) {
         var35 += var24;
         var23.append('+').append(var24).append("(beds:").append(var34.intVal).append(") ");
      }

      int var25 = Math.min(45, var34.intVal2 * 15);
      if (var25 > 0) {
         var35 += var25;
         var23.append('+').append(var25).append("(work:").append(var34.intVal2).append(") ");
      }

      if (var34.bool) {
         var35 += 25;
         var23.append("+25(redstone) ");
      }

      if (var34.bool2) {
         var35 += 20;
         var23.append("+20(rail/portal) ");
      }

      if (var34.bool3) {
         var35 += 10;
         var23.append("+10(fluid) ");
      }

      if (var18.size() >= 2 && var13 - var10 >= 3) {
         var35 += 10;
         var23.append("+10(multiY:").append(var18.size()).append(") ");
      }

      if (var34.bool5) {
         var35 += 15;
         var23.append("+15(artifWall) ");
      }

      boolean var26 = var17.size() == 1;
      if (var26 && var2 >= 6 && var2 <= 64) {
         var35 -= 15;
         var23.append("-15(monoKind:").append(var2).append(") ");
      }

      if (this.check(var1)) {
         var35 -= 20;
         var23.append("-20(gridSpaced) ");
      }

      int var27 = ((Chest)var1.get(0)).pos().getY();
      boolean var28 = var27 % 8 == 0 || var27 == 0 || var27 == 64 || var27 == -59;
      if (var28) {
         var35 -= 25;
         var23.append("-25(roundY:").append(var27).append(") ");
      }

      if (var34.intVal2 == 0) {
         var35 -= 20;
         var23.append("-20(noWork) ");
      }

      if (var34.intVal == 0) {
         var35 -= 15;
         var23.append("-15(noBeds) ");
      }

      if (var34.bool4) {
         var35 -= 20;
         var23.append("-20(natural) ");
      }

      if (var34.bool6) {
         var35 -= 15;
         var23.append("-15(shallow) ");
      }

      int var29 = this.realThreshold.getValueInt();
      int var30 = this.ambiguousThreshold.getValueInt();
      StashFinder.Band var31;
      if (var35 >= var29) {
         var31 = StashFinder.Band.REAL;
      } else if (var35 >= var30) {
         var31 = StashFinder.Band.AMBIGUOUS;
      } else {
         var31 = StashFinder.Band.FAKE;
      }

      return new StashFinder.Inner1(var15, var2, var17.size(), var32, var33, var35, var31, var23.toString());
   }

   private boolean check(List<Chest> var1) {
      if (var1.size() < 4) {
         return false;
      } else {
         int var2 = ((Chest)var1.get(0)).pos().getY();

         for (Chest var4 : var1) {
            if (var4.pos().getY() != var2) {
               return false;
            }
         }

         ArrayList var7 = new ArrayList();
         ArrayList var8 = new ArrayList();

         for (Chest var6 : var1) {
            var7.add(var6.pos().getX());
            var8.add(var6.pos().getZ());
         }

         return this.check2(var7) && this.check2(var8);
      }
   }

   private boolean check2(List<Integer> var1) {
      HashSet var2 = new HashSet(var1);
      if (var2.size() < 2) {
         return false;
      } else {
         ArrayList var3 = new ArrayList(var2);
         var3.sort((java.util.Comparator<Integer>)Integer::compareTo);
         int var4 = (Integer)var3.get(1) - (Integer)var3.get(0);
         if (var4 <= 0) {
            return false;
         } else {
            for (int var5 = 2; var5 < var3.size(); var5++) {
               if ((Integer)var3.get(var5) - (Integer)var3.get(var5 - 1) != var4) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private StashFinder.Inner2 stashFinderbOf(BlockPos var1) {
      StashFinder.Inner2 var2 = new StashFinder.Inner2();
      ClientWorld var3 = class310.world;
      if (var3 == null) {
         return var2;
      } else {
         int var5 = var1.getX() - 16;
         int var6 = var1.getX() + 16;
         int var7 = Math.max(var3.getBottomY(), var1.getY() - 16);
         int var8 = Math.min(var3.getBottomY() + var3.getHeight() - 1, var1.getY() + 16);
         int var9 = var1.getZ() - 16;
         int var10 = var1.getZ() + 16;
         int var11 = 0;
         int var12 = 0;
         int var13 = 0;
         Mutable var15 = new Mutable();

         for (int var16 = var5; var16 <= var6; var16++) {
            for (int var17 = var9; var17 <= var10; var17++) {
               for (int var18 = var7; var18 <= var8; var18++) {
                  var15.set(var16, var18, var17);

                  BlockState var19;
                  try {
                     var19 = var3.getBlockState(var15);
                  } catch (Throwable var22) {
                     continue;
                  }

                  Block var20 = var19.getBlock();
                  if (var20 instanceof BedBlock) {
                     int var21 = Math.abs(var16 - var1.getX()) + Math.abs(var18 - var1.getY()) + Math.abs(var17 - var1.getZ());
                     if (var21 <= 32) {
                        var2.intVal++;
                     }
                  }

                  if (check3(var20)) {
                     var2.intVal2++;
                  }

                  if (check4(var20)) {
                     var2.bool = true;
                  }

                  if (check5(var20)) {
                     var2.bool2 = true;
                  }

                  if (!var19.getFluidState().isEmpty()) {
                     var2.bool3 = true;
                  }

                  if (check7(var20)) {
                     var13++;
                  }

                  if (check6(var20)) {
                     var11++;
                  }

                  var12++;
               }
            }
         }

         if (var12 > 0) {
            double var23 = (double)var11 / var12;
            var2.bool4 = var23 > 0.9 && var13 < 4;
            var2.bool5 = var13 >= 8;
         }

         int var24 = this.intOf(var3, var1.getX(), var1.getZ());
         if (var24 != Integer.MIN_VALUE && var24 - var1.getY() < 8) {
            var2.bool6 = true;
         }

         return var2;
      }
   }

   private int intOf(World var1, int var2, int var3) {
      int var4 = var1.getBottomY() + var1.getHeight() - 1;
      int var5 = var1.getBottomY();
      Mutable var6 = new Mutable();

      for (int var7 = var4; var7 >= var5; var7--) {
         var6.set(var2, var7, var3);

         BlockState var8;
         try {
            var8 = var1.getBlockState(var6);
         } catch (Throwable var10) {
            return Integer.MIN_VALUE;
         }

         if (!var8.isAir() && var8.getFluidState().isEmpty()) {
            return var7;
         }
      }

      return Integer.MIN_VALUE;
   }

   private static boolean check3(Block var0) {
      return var0 == Blocks.CRAFTING_TABLE
         || var0 == Blocks.FURNACE
         || var0 == Blocks.BLAST_FURNACE
         || var0 == Blocks.SMOKER
         || var0 == Blocks.ANVIL
         || var0 == Blocks.CHIPPED_ANVIL
         || var0 == Blocks.DAMAGED_ANVIL
         || var0 == Blocks.ENCHANTING_TABLE
         || var0 == Blocks.BREWING_STAND
         || var0 == Blocks.GRINDSTONE
         || var0 == Blocks.CARTOGRAPHY_TABLE
         || var0 == Blocks.SMITHING_TABLE
         || var0 == Blocks.LOOM
         || var0 == Blocks.STONECUTTER
         || var0 == Blocks.FLETCHING_TABLE
         || var0 == Blocks.LECTERN;
   }

   private static boolean check4(Block var0) {
      return var0 == Blocks.REPEATER
         || var0 == Blocks.COMPARATOR
         || var0 == Blocks.OBSERVER
         || var0 == Blocks.PISTON
         || var0 == Blocks.STICKY_PISTON
         || var0 == Blocks.HOPPER
         || var0 == Blocks.DISPENSER
         || var0 == Blocks.DROPPER
         || var0 == Blocks.REDSTONE_WIRE
         || var0 == Blocks.REDSTONE_TORCH
         || var0 == Blocks.REDSTONE_LAMP
         || var0 == Blocks.REDSTONE_BLOCK;
   }

   private static boolean check5(Block var0) {
      return var0 == Blocks.RAIL
         || var0 == Blocks.POWERED_RAIL
         || var0 == Blocks.DETECTOR_RAIL
         || var0 == Blocks.ACTIVATOR_RAIL
         || var0 == Blocks.NETHER_PORTAL
         || var0 == Blocks.END_PORTAL
         || var0 == Blocks.END_PORTAL_FRAME
         || var0 == Blocks.END_GATEWAY;
   }

   private static boolean check6(Block var0) {
      return var0 == Blocks.STONE
         || var0 == Blocks.DEEPSLATE
         || var0 == Blocks.DIRT
         || var0 == Blocks.GRASS_BLOCK
         || var0 == Blocks.PODZOL
         || var0 == Blocks.GRAVEL
         || var0 == Blocks.SAND
         || var0 == Blocks.SANDSTONE
         || var0 == Blocks.NETHERRACK
         || var0 == Blocks.BASALT
         || var0 == Blocks.BLACKSTONE
         || var0 == Blocks.END_STONE
         || var0 == Blocks.WATER
         || var0 == Blocks.LAVA
         || var0 == Blocks.AIR
         || var0 == Blocks.CAVE_AIR
         || var0 == Blocks.VOID_AIR;
   }

   private static boolean check7(Block var0) {
      return var0 == Blocks.COBBLESTONE
         || var0 == Blocks.MOSSY_COBBLESTONE
         || var0 == Blocks.STONE_BRICKS
         || var0 == Blocks.MOSSY_STONE_BRICKS
         || var0 == Blocks.OAK_PLANKS
         || var0 == Blocks.SPRUCE_PLANKS
         || var0 == Blocks.BIRCH_PLANKS
         || var0 == Blocks.JUNGLE_PLANKS
         || var0 == Blocks.ACACIA_PLANKS
         || var0 == Blocks.DARK_OAK_PLANKS
         || var0 == Blocks.MANGROVE_PLANKS
         || var0 == Blocks.CHERRY_PLANKS
         || var0 == Blocks.CRIMSON_PLANKS
         || var0 == Blocks.WARPED_PLANKS
         || var0 == Blocks.BAMBOO_PLANKS
         || var0 == Blocks.WHITE_CONCRETE
         || var0 == Blocks.BLACK_CONCRETE
         || var0 == Blocks.GRAY_CONCRETE
         || var0 == Blocks.LIGHT_GRAY_CONCRETE
         || var0 == Blocks.WHITE_WOOL
         || var0 == Blocks.BLACK_WOOL
         || var0 == Blocks.BRICKS
         || var0 == Blocks.DEEPSLATE_BRICKS
         || var0 == Blocks.DEEPSLATE_TILES
         || var0 == Blocks.POLISHED_DEEPSLATE
         || var0 == Blocks.SMOOTH_STONE
         || var0 == Blocks.QUARTZ_BLOCK
         || var0 == Blocks.OAK_LOG
         || var0 == Blocks.SPRUCE_LOG
         || var0 == Blocks.BIRCH_LOG;
   }

   public void run6(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         if (!this.map.isEmpty()) {
            double var3 = class310.player.getX();
            double var5 = class310.player.getY();
            double var7 = class310.player.getZ();
            double var9 = this.maxDistance.getValue() * this.maxDistance.getValue();
            float var11 = this.lineWidth.getValueFloat();
            boolean var12 = this.throughWalls.getValue();
            String var13 = this.showBand.getMode();

            for (StashFinder.Inner1 var15 : this.map.values()) {
               if ((var15.stashFinderBand != StashFinder.Band.FAKE || "All".equals(var13))
                  && (var15.stashFinderBand != StashFinder.Band.AMBIGUOUS || !"RealOnly".equals(var13))) {
                  double var16 = var15.class2338.getX() + 0.5 - var3;
                  double var18 = var15.class2338.getY() + 0.5 - var5;
                  double var20 = var15.class2338.getZ() + 0.5 - var7;
                  if (!(var16 * var16 + var18 * var18 + var20 * var20 > var9)) {
                     int var22 = switch (var15.stashFinderBand) {
                        case REAL -> this.realColor.getValue();
                        case AMBIGUOUS -> this.ambiguousColor.getValue();
                        case FAKE -> this.fakeColor.getValue();
                     };
                     Box var23 = var15.class238.expand(0.15);
                     ListUtils.run5(var23, var22, var11, var12);
                  }
               }
            }
         }
      }
   }

   @Override
   public String getString3() {
      if (this.map.isEmpty()) {
         return null;
      } else {
         int var1 = 0;
         int var2 = 0;
         int var3 = 0;

         for (StashFinder.Inner1 var5 : this.map.values()) {
            switch (var5.stashFinderBand) {
               case REAL:
                  var1++;
                  break;
               case AMBIGUOUS:
                  var2++;
                  break;
               case FAKE:
                  var3++;
            }
         }

         StringBuilder var6 = new StringBuilder("§7R:§a").append(var1);
         var6.append(" §7A:§e").append(var2);
         var6.append(" §7F:§c").append(var3);
         return !this.showTag.getValue() ? "§7" + this.map.size() : var6.toString();
      }
   }

   private static int intOf2(int[] var0, int var1) {
      while (var0[var1] != var1) {
         var0[var1] = var0[var0[var1]];
         var1 = var0[var1];
      }

      return var1;
   }

   private static void run7(int[] var0, int var1, int var2) {
      int var3 = intOf2(var0, var1);
      int var4 = intOf2(var0, var2);
      if (var3 != var4) {
         var0[var3] = var4;
      }
   }

   private static List listOf(Integer var0) {
      return new ArrayList<>();
   }

   private static enum Band {
      REAL,
      AMBIGUOUS,
      FAKE;

      private static final StashFinder.Band[] stashFinderBandArray = getStashFinderBandArray();

      private static StashFinder.Band[] getStashFinderBandArray() {
         return new StashFinder.Band[]{REAL, AMBIGUOUS, FAKE};
      }
   }

final static class Inner1 {
   final long longVal;
   final int intVal;
   final int intVal2;
   final BlockPos class2338;
   final Box class238;
   final int intVal3;
   final StashFinder.Band stashFinderBand;
   final String string;

   Inner1(long var1, int var3, int var4, BlockPos var5, Box var6, int var7, StashFinder.Band var8, String var9) {
      this.longVal = var1;
      this.intVal = var3;
      this.intVal2 = var4;
      this.class2338 = var5;
      this.class238 = var6;
      this.intVal3 = var7;
      this.stashFinderBand = var8;
      this.string = var9;
   }
}

final static class Inner2 {
   int intVal;
   int intVal2;
   boolean bool;
   boolean bool2;
   boolean bool3;
   boolean bool4;
   boolean bool5;
   boolean bool6;

   private Inner2() {
   }
}
}

