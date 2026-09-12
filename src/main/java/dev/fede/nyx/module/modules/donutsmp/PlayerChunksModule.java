package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ChunkPos;

public class PlayerChunksModule extends Module {
   private final NumberSetting fixedY = new NumberSetting("FixedY", 80.0, -64.0, 320.0, 1.0);
   private final NumberSetting radius = new NumberSetting("Radius", 1.0, 0.0, 6.0, 1.0);
   private final NumberSetting memorySeconds = new NumberSetting("MemorySeconds", 300.0, 5.0, 3600.0, 5.0);
   private final NumberSetting alpha = new NumberSetting("Alpha", 0.35, 0.05, 1.0, 0.05);
   private final BooleanSetting fadeStale = new BooleanSetting("FadeStale", true);
   private final ColorSetting color = new ColorSetting("Color", 1617736959);
   private final Map<UUID, PlayerChunksModule.Inner1> map = new ConcurrentHashMap<>();

   public PlayerChunksModule() {
      super("PlayerChunks", "Overlay the chunk anchor around every online player (cached, survives out-of-range)", Category.DONUTSMP);
      this.run6(new Setting[]{this.fixedY, this.radius, this.memorySeconds, this.alpha, this.fadeStale, this.color});
   }

   @Override
   public void run2() {
      this.map.clear();
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null) {
         long var1 = System.currentTimeMillis();

         for (PlayerChunksModule.Inner1 var4 : this.map.values()) {
            var4.bool = false;
         }

         for (PlayerEntity var13 : class310.world.getPlayers()) {
            if (var13 != null && var13 != class310.player && !class310.player.getUuid().equals(var13.getUuid())) {
               UUID var5 = var13.getUuid();
               ChunkPos var6 = var13.getChunkPos();
               long var7 = ChunkPos.toLong(var6.x, var6.z);
               PlayerChunksModule.Inner1 var9 = this.map.get(var5);
               if (var9 == null) {
                  this.map.put(var5, new PlayerChunksModule.Inner1(var7, var1));
               } else {
                  var9.longVal = var7;
                  var9.longVal2 = var1;
                  var9.bool = true;
               }
            }
         }

         long var12 = (long)(this.memorySeconds.getValue() * 1000.0);
         Iterator var14 = this.map.entrySet().iterator();

         while (var14.hasNext()) {
            Entry var15 = (Entry)var14.next();
            PlayerChunksModule.Inner1 var16 = (PlayerChunksModule.Inner1)var15.getValue();
            long var8 = var1 - var16.longVal2;
            boolean var10 = this.check((UUID)var15.getKey());
            if (!var10 && var8 > 5000L) {
               var14.remove();
            } else if (var8 > var12) {
               var14.remove();
            }
         }
      }
   }

   private boolean check(UUID var1) {
      try {
         ClientPlayNetworkHandler var2 = class310.getNetworkHandler();
         if (var2 == null) {
            return false;
         } else {
            PlayerListEntry var3 = var2.getPlayerListEntry(var1);
            return var3 != null;
         }
      } catch (Throwable var4) {
         return false;
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.player != null && class310.world != null && !this.map.isEmpty()) {
         int var3 = this.color.getValue();
         int var4 = var3 >>> 16 & 0xFF;
         int var5 = var3 >>> 8 & 0xFF;
         int var6 = var3 & 0xFF;
         int var7 = var3 >>> 24 & 0xFF;
         double var8 = this.alpha.getValue();
         double var10 = this.memorySeconds.getValue() * 1000.0;
         boolean var12 = this.fadeStale.getValue();
         long var13 = System.currentTimeMillis();
         double var15 = this.fixedY.getValue();
         int var17 = this.radius.getValueInt();
         Iterator var18 = this.map.values().iterator();

         while (true) {
            PlayerChunksModule.Inner1 var19;
            double var22;
            while (true) {
               if (!var18.hasNext()) {
                  return;
               }

               var19 = (PlayerChunksModule.Inner1)var18.next();
               long var20 = var13 - var19.longVal2;
               if (!var19.bool && var12) {
                  var22 = 1.0 - var20 / var10;
                  if (var22 <= 0.0) {
                     continue;
                  }
                  break;
               }

               var22 = 1.0;
               break;
            }

            int var24 = (int)Math.round(var7 * var8 * var22);
            if (var24 > 0) {
               if (var24 > 255) {
                  var24 = 255;
               }

               int var25 = var24 << 24 | var4 << 16 | var5 << 8 | var6;
               int var26 = ChunkPos.getPackedX(var19.longVal);
               int var27 = ChunkPos.getPackedZ(var19.longVal);

               for (int var28 = -var17; var28 <= var17; var28++) {
                  for (int var29 = -var17; var29 <= var17; var29++) {
                     double var30 = (var26 + var28) * 16.0;
                     double var32 = (var27 + var29) * 16.0;
                     ListUtils.run13(var30, var15, var32, var30 + 16.0, var32 + 16.0, var25);
                  }
               }
            }
         }
      }
   }

   @Override
   public String getString3() {
      int var1 = this.map.size();
      return var1 == 0 ? null : "§7" + var1;
   }

final static class Inner1 {
   volatile long longVal;
   volatile long longVal2;
   volatile boolean bool;

   Inner1(long var1, long var3) {
      this.longVal = var1;
      this.longVal2 = var3;
      this.bool = true;
   }
}
}

