package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.PathUtils_2;
import dev.fede.nyx.util.PathUtils_2_3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

public class HeatMapChunkRadarModule extends Module {
   private final NumberSetting radiusChunks = new NumberSetting("Radius", 24.0, 2.0, 96.0, 1.0);
   private final NumberSetting posX = new NumberSetting("PosX", 10.0, 0.0, 3840.0, 1.0);
   private final NumberSetting posY = new NumberSetting("PosY", 140.0, 0.0, 2160.0, 1.0);
   private final NumberSetting widgetSize = new NumberSetting("Size", 128.0, 32.0, 512.0, 1.0);
   private final NumberSetting alpha = new NumberSetting("Alpha", 200.0, 0.0, 255.0, 1.0);
   private final NumberSetting retentionHours = new NumberSetting("RetentionHours", 24.0, 1.0, 168.0, 1.0);
   private volatile Map<Long, PathUtils_2.Inner1> map = new ConcurrentHashMap<>();
   private String string = "";
   private int intVal = 0;
   private int intVal2 = 0;
   private static final int intVal3 = 1200;
   private static final int intVal4 = 20;
   private static final int[] intArray = new int[]{100, 50, 20, 5, 1};
   private static final int[] intArray2 = new int[]{15745056, 15769632, 4247648, 3391712, 3362992};

   public HeatMapChunkRadarModule() {
      super("HeatMapChunkRadar", "Rolling 24h chunk-visit heat-map (per server + dimension)", Category.RENDER);
      this.run6(new Setting[]{this.radiusChunks, this.posX, this.posY, this.widgetSize, this.alpha, this.retentionHours});
   }

   @Override
   public void run() {
      if (class310 != null) {
         String var1 = class310.world != null ? PathUtils_2_3.serverKey(class310) : "";
         Map var2 = PathUtils_2.load(class310);
         this.map = var2;
         this.string = var1;
      }

      this.intVal = 0;
      this.intVal2 = 0;
   }

   @Override
   public void run2() {
      if (class310 != null && !this.string.isEmpty()) {
         PathUtils_2.save(class310, this.map);
      }
   }

   @Override
   public void run3() {
      if (class310 != null && class310.world != null && class310.player != null) {
         String var1 = PathUtils_2_3.serverKey(class310);
         if (!var1.equals(this.string)) {
            if (!this.string.isEmpty()) {
               PathUtils_2.save(class310, this.map);
            }

            this.map = PathUtils_2.load(class310);
            this.string = var1;
            this.intVal = 0;
            this.intVal2 = 0;
         }

         ChunkPos var2 = class310.player.getChunkPos();
         long var3 = ChunkPos.toLong(var2.x, var2.z);
         long var5 = System.currentTimeMillis();
         PathUtils_2.Inner1 var7 = this.map.get(var3);
         if (var7 == null) {
            this.map.put(var3, new PathUtils_2.Inner1(var2.x, var2.z, 1, var5));
         } else {
            var7.count++;
            var7.lastVisitMs = var5;
         }

         if (++this.intVal2 >= 20) {
            this.intVal2 = 0;
            this.run4(var5);
         }

         if (++this.intVal >= 1200) {
            this.intVal = 0;
            PathUtils_2.save(class310, this.map);
         }
      }
   }

   private void run4(long var1) {
      long var3 = var1 - this.retentionHours.getValueInt() * 3600L * 1000L;
      ArrayList var5 = new ArrayList();

      for (Entry var7 : this.map.entrySet()) {
         PathUtils_2.Inner1 var8 = (PathUtils_2.Inner1)var7.getValue();
         if (var8 == null || var8.lastVisitMs < var3) {
            var5.add((Long)var7.getKey());
         }
      }

      for (Long var10 : (java.util.List<Long>)var5) {
         this.map.remove(var10);
      }
   }

   public void run5(DrawContext var1, float var2) {
      if (class310 != null && class310.world != null && class310.player != null) {
         int var3 = intOf2((int)Math.round(this.alpha.getValue()));
         if (var3 != 0) {
            int var4 = Math.max(16, (int)Math.round(this.widgetSize.getValue()));
            int var5 = (int)Math.round(this.posX.getValue());
            int var6 = (int)Math.round(this.posY.getValue());
            int var7 = var5 + var4;
            int var8 = var6 + var4;
            int var9 = Math.min(255, var3);
            int var10 = var9 << 24;
            var1.fill(var5, var6, var7, var8, var10);
            int var11 = var3 << 24 | 8421504;
            var1.fill(var5, var6, var7, var6 + 1, var11);
            var1.fill(var5, var8 - 1, var7, var8, var11);
            var1.fill(var5, var6, var5 + 1, var8, var11);
            var1.fill(var7 - 1, var6, var7, var8, var11);
            int var12 = this.radiusChunks.getValueInt();
            if (var12 > 0) {
               int var13 = 2 * var12 + 1;
               int var14 = Math.max(1, (var4 - 2) / var13);
               int var15 = var14 * var13;
               int var16 = var5 + (var4 - var15) / 2;
               int var17 = var6 + (var4 - var15) / 2;
               int var18 = class310.player.getChunkPos().x;
               int var19 = class310.player.getChunkPos().z;

               for (Entry var21 : this.map.entrySet()) {
                  PathUtils_2.Inner1 var22 = (PathUtils_2.Inner1)var21.getValue();
                  if (var22 != null) {
                     int var23 = var22.intVal - var18;
                     int var24 = var22.cz - var19;
                     if (var23 >= -var12 && var23 <= var12 && var24 >= -var12 && var24 <= var12) {
                        int var25 = intOf(var22.count);
                        if (var25 >= 0) {
                           int var26 = var16 + (var23 + var12) * var14;
                           int var27 = var17 + (var24 + var12) * var14;
                           int var28 = var3 << 24 | var25 & 16777215;
                           var1.fill(var26, var27, var26 + var14, var27 + var14, var28);
                        }
                     }
                  }
               }

               int var29 = var16 + var12 * var14 + var14 / 2;
               int var30 = var17 + var12 * var14 + var14 / 2;
               int var31 = var3 << 24 | 16777215;
               var1.fill(var29 - 1, var30, var29 + 2, var30 + 1, var31);
               var1.fill(var29, var30 - 1, var29 + 1, var30 + 2, var31);
            }
         }
      }
   }

   private static int intOf(int var0) {
      for (int var1 = 0; var1 < intArray.length; var1++) {
         if (var0 >= intArray[var1]) {
            return intArray2[var1];
         }
      }

      return -1;
   }

   private static int intOf2(int var0) {
      return MathHelper.clamp(var0, 0, 255);
   }

   @Override
   public String getString3() {
      int var1 = this.map.size();
      return var1 > 0 ? "§7" + var1 : null;
   }

   public Map<Long, PathUtils_2.Inner1> getMap() {
      return new HashMap<>(this.map);
   }
}

