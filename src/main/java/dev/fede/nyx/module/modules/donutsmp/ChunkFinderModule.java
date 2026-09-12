package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.Map$EntryUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkFinderModule extends Module {
   private final NumberSetting displayMs = new NumberSetting("DisplayMs", 5000.0, 500.0, 30000.0, 100.0);
   private final NumberSetting alpha = new NumberSetting("Alpha", 1.0, 0.05, 1.0, 0.05);
   private final NumberSetting seenTtl = new NumberSetting("DedupSeconds", 60.0, 5.0, 600.0, 5.0);
   private final ColorSetting color = new ColorSetting("Color", -1325400320);
   private final Map<Long, Long> map = new ConcurrentHashMap<>();
   private final Map<Long, Long> map2 = new ConcurrentHashMap<>();
   private static volatile boolean bool = false;
   private static volatile ChunkFinderModule chunkFinderModule;

   public ChunkFinderModule() {
      super("ChunkFinder", "Wireframe overlay on newly-loaded chunks — reveals server chunk-streaming pattern", Category.DONUTSMP);
      this.run6(new Setting[]{this.displayMs, this.alpha, this.seenTtl, this.color});
      run3();
   }

   public static synchronized void run3_s() {
      if (!bool) {
         bool = true;
         ClientChunkEvents.CHUNK_LOAD.register(ChunkFinderModule::run16);
      }
   }

   @Override
   public void run() {
      chunkFinderModule = this;
      this.map.clear();
      this.map2.clear();
   }

   @Override
   public void run2() {
      chunkFinderModule = null;
      this.map.clear();
      this.map2.clear();
   }

   public void run4() {
      long var1 = System.currentTimeMillis();
      long var3 = (long)this.displayMs.getValue();
      Iterator var5 = this.map.entrySet().iterator();

      while (var5.hasNext()) {
         if (var1 - (Long)((Entry)var5.next()).getValue() >= var3) {
            var5.remove();
         }
      }

      long var6 = (long)(this.seenTtl.getValue() * 1000.0) * 10L;
      Iterator var8 = this.map2.entrySet().iterator();

      while (var8.hasNext()) {
         if (var1 - (Long)((Entry)var8.next()).getValue() >= var6) {
            var8.remove();
         }
      }
   }

   public void run5(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null && !this.map.isEmpty()) {
         long var3 = System.currentTimeMillis();
         long var5 = (long)this.displayMs.getValue();
         if (var5 > 0L) {
            int var7 = this.color.getValue();
            int var8 = var7 >>> 16 & 0xFF;
            int var9 = var7 >>> 8 & 0xFF;
            int var10 = var7 & 0xFF;
            double var11 = (var7 >>> 24 & 0xFF) / 255.0 * this.alpha.getValue();
            double var13 = class310.world.getBottomY();
            double var15 = class310.world.getTopYInclusive() + 1.0;

            for (Entry var18 : this.map.entrySet()) {
               long var19 = var3 - (Long)var18.getValue();
               if (var19 < var5) {
                  double var21 = 1.0 - (double)var19 / var5;
                  int var23 = (int)Math.round(var11 * var21 * 255.0);
                  if (var23 > 0) {
                     if (var23 > 255) {
                        var23 = 255;
                     }

                     int var24 = var23 << 24 | var8 << 16 | var9 << 8 | var10;
                     long var25 = (Long)var18.getKey();
                     int var27 = ChunkPos.getPackedX(var25);
                     int var28 = ChunkPos.getPackedZ(var25);
                     double var29 = var27 * 16.0;
                     double var31 = var28 * 16.0;
                     double var33 = var29 + 16.0;
                     double var35 = var31 + 16.0;
                     ListUtils.run5(new Box(var29, var13, var31, var33, var15, var35), var24, 1.0F, false);
                  }
               }
            }
         }
      }
   }

   private static void run16(ClientWorld var0, WorldChunk var1) {
      ChunkFinderModule var2 = chunkFinderModule;
      if (var2 != null && var2.isEnabled3() && var1 != null) {
         try {
            if (Map$EntryUtils.isEnabled()) {
               return;
            }
         } catch (Throwable var11) {
         }

         try {
            long var3 = ChunkPos.toLong(var1.getPos().x, var1.getPos().z);
            long var5 = System.currentTimeMillis();
            long var7 = (long)(var2.seenTtl.getValue() * 1000.0);
            Long var9 = var2.map2.get(var3);
            if (var9 != null && var5 - var9 < var7) {
               return;
            }

            var2.map2.put(var3, var5);
            var2.map.put(var3, var5);
         } catch (Throwable var10) {
         }
      }
   }
}

