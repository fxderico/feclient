package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.storage.Chest;
import dev.fede.nyx.storage.Chest$KindUtils;
import dev.fede.nyx.tracker.ChunkActivityScanner;
import dev.fede.nyx.tracker.MapUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class RaidPlannerModule extends Module {
   private final NumberSetting posX = new NumberSetting("PosX", 10.0, 0.0, 3840.0, 1.0);
   private final NumberSetting posY = new NumberSetting("PosY", 300.0, 0.0, 2160.0, 1.0);
   private final NumberSetting maxCandidates = new NumberSetting("MaxCandidates", 3.0, 1.0, 10.0, 1.0);
   private final NumberSetting scoreThreshold = new NumberSetting("ScoreThreshold", 20.0, 0.0, 1000.0, 1.0);
   private final NumberSetting refreshEveryTicks = new NumberSetting("RefreshEveryTicks", 40.0, 5.0, 400.0, 5.0);
   private final ColorSetting rankGold = new ColorSetting("RankGold", -10496);
   private final ColorSetting rankSilver = new ColorSetting("RankSilver", -4144960);
   private final ColorSetting rankBronze = new ColorSetting("RankBronze", -3309774);
   private volatile List<RaidPlannerModule.Inner1> list = Collections.emptyList();
   private int intVal = 0;
   private boolean bool = false;
   private static final Class<?> classVal = Set.class;

   public RaidPlannerModule() {
      super("RaidPlanner", "Top-N base candidates aggregated from every base-signal scanner", Category.RENDER);
      this.run6(
         new Setting[]{this.posX, this.posY, this.maxCandidates, this.scoreThreshold, this.refreshEveryTicks, this.rankGold, this.rankSilver, this.rankBronze}
      );
   }

   @Override
   public void run() {
      this.intVal = 0;
      this.list = Collections.emptyList();
      this.bool = false;
   }

   @Override
   public void run2() {
      if (class310 != null && class310.world != null && class310.player != null) {
         if (--this.intVal <= 0) {
            this.intVal = Math.max(1, this.refreshEveryTicks.getValueInt());
            this.run3();
         }
      } else {
         this.list = Collections.emptyList();
      }
   }

   public void run3() {
      HashMap var1 = new HashMap();
      int var2 = class310.player.getChunkPos().x;
      int var3 = class310.player.getChunkPos().z;

      try {
         Map var4 = MapUtils.getMap();
         if (var4 != null) {
            for (Entry var6 : ((java.util.Map<?,?>)var4).entrySet()) {
               Integer var7 = (Integer)var6.getValue();
               if (var7 != null && var7 > 0) {
                  long var8 = longOf(MapUtils.intOf((Long)var6.getKey()), MapUtils.intOf2((Long)var6.getKey()));
                  var1.merge(var8, var7 * 3, (java.util.function.BiFunction<Integer,Integer,Integer>)Integer::sum);
               }
            }
         }
      } catch (Throwable var19) {
      }

      try {
         List var20 = getList();
         if (var20 != null) {
            for (BlockPos var28 : (java.util.List<BlockPos>)var20) {
               long var32 = longOf(var28.getX() >> 4, var28.getZ() >> 4);
               var1.merge(var32, 2, (java.util.function.BiFunction<Integer,Integer,Integer>)Integer::sum);
            }
         }
      } catch (Throwable var18) {
      }

      try {
         Collection var21 = Chest$KindUtils.all();
         if (var21 != null) {
            for (Chest var29 : (java.util.List<Chest>)var21) {
               if (var29 != null && var29.pos() != null) {
                  long var33 = longOf(var29.pos().getX() >> 4, var29.pos().getZ() >> 4);
                  var1.merge(var33, 1, (java.util.function.BiFunction<Integer,Integer,Integer>)Integer::sum);
               }
            }
         }
      } catch (Throwable var17) {
      }

      try {
         Map var22 = ChunkActivityScanner.getMap();
         if (var22 != null) {
            for (Entry var30 : ((java.util.Map<?,?>)var22).entrySet()) {
               Integer var34 = (Integer)var30.getValue();
               if (var34 != null && var34 > 0) {
                  long var36 = longOf(ChunkActivityScanner.intOf((Long)var30.getKey()), ChunkActivityScanner.intOf2((Long)var30.getKey()));
                  var1.merge(var36, var34, (java.util.function.BiFunction<Integer,Integer,Integer>)Integer::sum);
               }
            }
         }
      } catch (Throwable var16) {
      }

      int var23 = this.scoreThreshold.getValueInt();
      int var27 = Math.max(1, this.maxCandidates.getValueInt());
      ArrayList var31 = new ArrayList();

      for (Entry var37 : ((java.util.Map<?,?>)var1).entrySet()) {
         int var9 = (Integer)var37.getValue();
         if (var9 >= var23) {
            int var10 = intOf2((Long)var37.getKey());
            int var11 = intOf3((Long)var37.getKey());
            int var12 = var10 - var2;
            int var13 = var11 - var3;
            double var14 = Math.sqrt((double)var12 * var12 + (double)var13 * var13) * 16.0;
            var31.add(new RaidPlannerModule.Inner1(var10, var11, var9, var14));
         }
      }

      var31.sort(Comparator.comparingInt(RaidPlannerModule::intOf4));
      if (var31.size() > var27) {
         var31 = new ArrayList(var31.subList(0, var27));
      }

      this.list = var31;
   }

   public List<BlockPos> getList_nf() {
      return Collections.emptyList();
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310 != null) {
         TextRenderer var3 = class310.textRenderer;
         if (var3 != null) {
            List var4 = this.list;
            int var5 = (int)Math.round(this.posX.getValue());
            int var6 = (int)Math.round(this.posY.getValue());
            var1.drawTextWithShadow(var3, "Raid Planner", var5, var6, -1);
            int var8 = var6 + 9 + 2;
            if (var4.isEmpty()) {
               var1.drawTextWithShadow(var3, "(no candidates)", var5, var8, -6250336);
               this.run5(null);
            } else {
               int var10 = 0;

               for (RaidPlannerModule.Inner1 var12 : (java.util.List<RaidPlannerModule.Inner1>)var4) {
                  int var13 = this.intOf(var10);
                  String var14 = String.format("#%d [%d,%d] %.0fm  s%d", var10 + 1, var12.getInt(), var12.getInt2(), var12.doubleVal, var12.intVal3);
                  var1.drawTextWithShadow(var3, var14, var5, var8, var13);
                  int var15 = var3.getWidth(var14);
                  int var16 = var5 + var15 + 8;
                  int var17 = var8 - 1;
                  int var19 = var3.getWidth("SET WAYPOINT") + 8;
                  Objects.requireNonNull(var3);
                  int var21 = var16 + var19;
                  int var22 = var17 + 11;
                  var12.intVal4 = var16;
                  var12.intVal5 = var17;
                  var12.intVal6 = var21;
                  var12.intVal7 = var22;
                  boolean var23 = this.check(var12);
                  int var24 = var23 ? -1070583760 : -2145378272;
                  int var25 = var23 ? var13 : -11513776;
                  var1.fill(var16, var17, var21, var22, var24);
                  run7(var1, var16, var17, var21, var22, var25);
                  var1.drawTextWithShadow(var3, "SET WAYPOINT", var16 + 4, var17 + 2, var13);
                  var8 += 13;
                  var10++;
               }

               this.run5(var4);
            }
         }
      }
   }

   private void run5(List<RaidPlannerModule.Inner1> var1) {
      if (class310 == null || class310.getWindow() == null || class310.mouse == null) {
         this.bool = false;
      } else if (class310.currentScreen != null) {
         this.bool = false;
      } else {
         long var2 = class310.getWindow().getHandle();
         boolean var4 = GLFW.glfwGetMouseButton(var2, 0) == 1;
         boolean var5 = !var4 && this.bool;
         this.bool = var4;
         if (var5 && var1 != null && !var1.isEmpty()) {
            int var6 = class310.getWindow().getWidth();
            int var7 = class310.getWindow().getHeight();
            if (var6 > 0 && var7 > 0) {
               int var8 = class310.getWindow().getScaledWidth();
               int var9 = class310.getWindow().getScaledHeight();
               int var10 = (int)(class310.mouse.getX() * var8 / var6);
               int var11 = (int)(class310.mouse.getY() * var9 / var7);

               for (RaidPlannerModule.Inner1 var13 : var1) {
                  if (var10 >= var13.intVal4 && var10 < var13.intVal6 && var11 >= var13.intVal5 && var11 < var13.intVal7) {
                     this.run6(var13);
                     return;
                  }
               }
            }
         }
      }
   }

   private boolean check(RaidPlannerModule.Inner1 var1) {
      if (class310 == null || class310.getWindow() == null || class310.mouse == null) {
         return false;
      } else if (class310.currentScreen != null) {
         return false;
      } else {
         int var2 = class310.getWindow().getWidth();
         int var3 = class310.getWindow().getHeight();
         if (var2 > 0 && var3 > 0) {
            int var4 = class310.getWindow().getScaledWidth();
            int var5 = class310.getWindow().getScaledHeight();
            int var6 = (int)(class310.mouse.getX() * var4 / var2);
            int var7 = (int)(class310.mouse.getY() * var5 / var3);
            return var6 >= var1.intVal4 && var6 < var1.intVal6 && var7 >= var1.intVal5 && var7 < var1.intVal7;
         } else {
            return false;
         }
      }
   }

   private void run6(RaidPlannerModule.Inner1 var1) {
      try {
         String var2 = var1.getInt() + " 64 " + var1.getInt2();
         if (class310 != null && class310.keyboard != null) {
            class310.keyboard.setClipboard(var2);
         }
      } catch (Throwable var3) {
      }
   }

   private int intOf(int var1) {
      return switch (MathHelper.clamp(var1, 0, 2)) {
         case 0 -> this.rankGold.getValue();
         case 1 -> this.rankSilver.getValue();
         default -> this.rankBronze.getValue();
      };
   }

   private static void run7(DrawContext var0, int var1, int var2, int var3, int var4, int var5) {
      var0.fill(var1, var2, var3, var2 + 1, var5);
      var0.fill(var1, var4 - 1, var3, var4, var5);
      var0.fill(var1, var2, var1 + 1, var4, var5);
      var0.fill(var3 - 1, var2, var3, var4, var5);
   }

   private static long longOf(int var0, int var1) {
      return var0 & 4294967295L | (var1 & 4294967295L) << 32;
   }

   private static int intOf2(long var0) {
      return (int)(var0 & 4294967295L);
   }

   private static int intOf3(long var0) {
      return (int)(var0 >>> 32);
   }

   @Override
   public String getString3() {
      int var1 = this.list.size();
      return var1 > 0 ? "§7" + var1 : null;
   }

   private static int intOf4(RaidPlannerModule.Inner1 var0) {
      return -var0.intVal3;
   }

final static class Inner1 {
   final int intVal;
   final int intVal2;
   final int intVal3;
   final double doubleVal;
   int intVal4;
   int intVal5;
   int intVal6;
   int intVal7;

   Inner1(int var1, int var2, int var3, double var4) {
      this.intVal = var1;
      this.intVal2 = var2;
      this.intVal3 = var3;
      this.doubleVal = var4;
   }

   int getInt() {
      return (this.intVal << 4) + 8;
   }

   int getInt2() {
      return (this.intVal2 << 4) + 8;
   }
}
}

