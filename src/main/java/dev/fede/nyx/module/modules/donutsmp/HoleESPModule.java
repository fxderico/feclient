package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.Heightmap.Type;

public class HoleESPModule extends Module {
   private final ModeSetting mode = new ModeSetting("Mode", "All", "All", "1x1", "2x2", "3x3");
   private final ColorSetting color = new ColorSetting("Color", 1627374668);
   private final NumberSetting alpha = new NumberSetting("Alpha", 0.4, 0.05, 1.0, 0.05);
   private final NumberSetting radius = new NumberSetting("Radius", 32.0, 8.0, 128.0, 1.0);
   private static final int intVal = 3;
   private static final int intVal2 = 2;
   private static final int intVal3 = 10;
   private final List<HoleESPModule.Inner1> list = new ArrayList<>();
   private int intVal4;

   public HoleESPModule() {
      super("HoleESP", "Highlights player-dug vertical shafts (1x1 / 2x2 / 3x3, from surface downward).", Category.DONUTSMP);
      this.run6(new Setting[]{this.mode, this.color, this.alpha, this.radius});
   }

   @Override
   public void run2() {
      this.list.clear();
      this.intVal4 = 0;
   }

   @Override
   // onEnable reset. The % 10 throttle used to live here but run() only fires
   // once on toggle, so the heavy scan in run3() (called every tick by the
   // bridge) ran unthrottled. Throttle moved into run3() itself.
   public void run() {
      this.intVal4 = 0;
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.player != null && class310.world != null && !this.list.isEmpty()) {
         int var3 = this.color.getValue();
         int var4 = var3 >>> 24 & 0xFF;
         int var5 = Math.min(255, (int)(var4 * this.alpha.getValue()));
         int var6 = var5 << 24 | var3 & 16777215;
         boolean var7 = ListUtils.isEnabled8();
         String var8 = this.mode.getMode();

         for (HoleESPModule.Inner1 var10 : this.list) {
            if (check(var8, var10.size)) {
               Box var11 = new Box(var10.minX, var10.floorY + 1, var10.minZ, var10.maxX + 1.0, var10.topY + 1.0, var10.maxZ + 1.0);
               if (var7) {
                  ListUtils.run3(var11, var6, true);
               }

               ListUtils.run5(var11, var3, 1.0F, true);
            }
         }
      }
   }

   private static boolean check(String var0, int var1) {
      if ("All".equals(var0)) {
         return true;
      } else if ("1x1".equals(var0)) {
         return var1 == 1;
      } else if ("2x2".equals(var0)) {
         return var1 == 2;
      } else {
         return "3x3".equals(var0) ? var1 == 3 : false;
      }
   }

   public void run3() {
      if (class310.player == null || class310.world == null || this.intVal4++ % 10 != 0) {
         return; // throttle: heavy scan only every 10 ticks (keeps cached results between)
      }

      this.list.clear();
      if (class310.player != null && class310.world != null) {
         int var1 = this.radius.getValueInt();
         int var2 = (int)Math.floor(class310.player.getX());
         int var3 = (int)Math.floor(class310.player.getZ());
         HashMap var4 = new HashMap();
         Mutable var5 = new Mutable();

         for (int var6 = var3 - var1; var6 <= var3 + var1; var6++) {
            for (int var7 = var2 - var1; var7 <= var2 + var1; var7++) {
               HoleESPModule.Inner2 var8 = this.ebOf(var7, var6, var5);
               if (var8 != null) {
                  var4.put(longOf(var7, var6), var8);
               }
            }
         }

         if (!var4.isEmpty()) {
            HashSet var15 = new HashSet();
            ArrayList var16 = new ArrayList(var4.keySet());
            var16.sort((java.util.Comparator)(a, b) -> 0);

            for (long var9 : (java.util.List<Long>)var16) {
               if (!var15.contains(var9)) {
                  int var11 = intOf(var9);
                  int var12 = intOf2(var9);
                  if (this.check3(var4, var15, var11, var12, 3)) {
                     HoleESPModule.Inner2 var13 = (HoleESPModule.Inner2)var4.get(var9);
                     HoleESPModule.Inner1 var14 = new HoleESPModule.Inner1(var11, var12, var11 + 2, var12 + 2, var13.topY, var13.floorY, 3);
                     if (this.check2(var14)) {
                        this.list.add(var14);
                     }
                  }
               }
            }

            for (long var20 : (java.util.List<Long>)var16) {
               if (!var15.contains(var20)) {
                  int var22 = intOf(var20);
                  int var24 = intOf2(var20);
                  if (this.check3(var4, var15, var22, var24, 2)) {
                     HoleESPModule.Inner2 var26 = (HoleESPModule.Inner2)var4.get(var20);
                     HoleESPModule.Inner1 var28 = new HoleESPModule.Inner1(var22, var24, var22 + 1, var24 + 1, var26.topY, var26.floorY, 2);
                     if (this.check2(var28)) {
                        this.list.add(var28);
                     }
                  }
               }
            }

            for (long var21 : (java.util.List<Long>)var16) {
               if (!var15.contains(var21)) {
                  var15.add(var21);
                  int var23 = intOf(var21);
                  int var25 = intOf2(var21);
                  HoleESPModule.Inner2 var27 = (HoleESPModule.Inner2)var4.get(var21);
                  HoleESPModule.Inner1 var29 = new HoleESPModule.Inner1(var23, var25, var23, var25, var27.topY, var27.floorY, 1);
                  if (this.check2(var29)) {
                     this.list.add(var29);
                  }
               }
            }
         }
      }
   }

   private HoleESPModule.Inner2 ebOf(int var1, int var2, Mutable var3) {
      ClientWorld var4 = class310.world;
      int var5 = var4.getBottomY();
      int var6 = var5 + var4.getHeight();
      int var7 = var5;

      try {
         var7 = Math.max(var7, var4.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, var1, var2) - 1);
         var7 = Math.max(var7, var4.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, var1 + 1, var2) - 1);
         var7 = Math.max(var7, var4.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, var1 - 1, var2) - 1);
         var7 = Math.max(var7, var4.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, var1, var2 + 1) - 1);
         var7 = Math.max(var7, var4.getTopY(Type.MOTION_BLOCKING_NO_LEAVES, var1, var2 - 1) - 1);
      } catch (Throwable var14) {
      }

      int var8 = Math.min(var7 + 1, var6 - 2);
      int var9 = -1;

      for (int var10 = var8; var10 > var5 + 1; var10--) {
         var3.set(var1, var10, var2);
         if (var4.getBlockState(var3).isAir()) {
            var9 = var10;
            break;
         }
      }

      if (var9 < 0) {
         return null;
      } else {
         int var15 = var9;

         for (int var11 = var9 - 1; var11 > var5; var15 = var11--) {
            var3.set(var1, var11, var2);
            if (!var4.getBlockState(var3).isAir()) {
               break;
            }
         }

         var3.set(var1, var15 - 1, var2);
         BlockState var12 = var4.getBlockState(var3);
         if (!var12.isSolidBlock(var4, var3)) {
            return null;
         } else {
            int var13 = var9 - var15 + 1;
            return var13 < 3 ? null : new HoleESPModule.Inner2(var1, var2, var9, var15 - 1);
         }
      }
   }

   private boolean check2(HoleESPModule.Inner1 var1) {
      ClientWorld var2 = class310.world;
      Mutable var3 = new Mutable();
      int var4 = 0;
      byte var5 = 0;
      int var6 = var1.floorY + 1;
      int var7 = var1.topY;

      for (int var8 = var6; var8 <= var7; var8++) {
         for (int var9 = var1.minX; var9 <= var1.maxX; var9++) {
            var5 += 2;
            var3.set(var9, var8, var1.minZ - 1);
            if (var2.getBlockState(var3).isSolidBlock(var2, var3)) {
               var4++;
            }

            var3.set(var9, var8, var1.maxZ + 1);
            if (var2.getBlockState(var3).isSolidBlock(var2, var3)) {
               var4++;
            }
         }

         for (int var10 = var1.minZ; var10 <= var1.maxZ; var10++) {
            var5 += 2;
            var3.set(var1.minX - 1, var8, var10);
            if (var2.getBlockState(var3).isSolidBlock(var2, var3)) {
               var4++;
            }

            var3.set(var1.maxX + 1, var8, var10);
            if (var2.getBlockState(var3).isSolidBlock(var2, var3)) {
               var4++;
            }
         }
      }

      return var5 == 0 ? false : var4 * 100 / var5 >= 65;
   }

   private boolean check3(Map<Long, HoleESPModule.Inner2> var1, Set<Long> var2, int var3, int var4, int var5) {
      long var6 = longOf(var3, var4);
      HoleESPModule.Inner2 var8 = (HoleESPModule.Inner2)var1.get(var6);
      if (var8 == null) {
         return false;
      } else {
         for (int var9 = 0; var9 < var5; var9++) {
            for (int var10 = 0; var10 < var5; var10++) {
               long var11 = longOf(var3 + var10, var4 + var9);
               if (var2.contains(var11)) {
                  return false;
               }

               HoleESPModule.Inner2 var13 = (HoleESPModule.Inner2)var1.get(var11);
               if (var13 == null) {
                  return false;
               }

               if (var13.topY != var8.topY || var13.floorY != var8.floorY) {
                  return false;
               }
            }
         }

         for (int var14 = 0; var14 < var5; var14++) {
            for (int var15 = 0; var15 < var5; var15++) {
               var2.add(longOf(var3 + var15, var4 + var14));
            }
         }

         return true;
      }
   }

   private static long longOf(int var0, int var1) {
      return var0 & 4294967295L | (var1 & 4294967295L) << 32;
   }

   private static int intOf(long var0) {
      return (int)var0;
   }

   private static int intOf2(long var0) {
      return (int)(var0 >>> 32);
   }

   private static int intOf3(long var0, long var2) {
      int var4 = intOf(var0);
      int var5 = intOf(var2);
      return var4 != var5 ? Integer.compare(var4, var5) : Integer.compare(intOf2(var0), intOf2(var2));
   }

record Inner1(int minX, int minZ, int maxX, int maxZ, int topY, int floorY, int size) {

}

record Inner2(int intVal, int intVal2, int topY, int floorY) {


   public int getInt() {
      return this.intVal;
   }

   public int getInt2() {
      return this.intVal2;
   }
}
}

