package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.suschunk.ServerLightCache;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

/**
 * BaseAlert — passive base detectors (LightDebug / SusChunkFinder) only *show*
 * you lit pockets; this one *pings* you. It walks the raw server-light cache
 * around you, counts hidden lit cells per chunk (the light the server streams
 * for torches behind anti-xray walls), and the moment a chunk crosses the
 * threshold it fires a one-time chat line + sound with the coords. Fly and
 * listen instead of staring at a heatmap.
 */
public class BaseAlertModule extends Module {
   private final NumberSetting rangeChunks = new NumberSetting("RangeChunks", 6.0, 1.0, 12.0, 1.0);
   private final NumberSetting minCells = new NumberSetting("MinLitCells", 12.0, 1.0, 128.0, 1.0);
   private final NumberSetting interval = new NumberSetting("ScanTicks", 10.0, 2.0, 40.0, 1.0);
   private final BooleanSetting chat = new BooleanSetting("Chat", true);
   private final BooleanSetting sound = new BooleanSetting("Sound", true);
   // chunk key -> tick alerted, so we re-arm a chunk only after it leaves range
   private final Map<Long, Long> alerted = new HashMap<>();
   private long tick;

   public BaseAlertModule() {
      super("BaseAlert", "Pings you (chat + sound) when you pass a likely base — server-light density", Category.DONUTSMP);
      this.run6(new Setting[]{this.rangeChunks, this.minCells, this.interval, this.chat, this.sound});
   }

   @Override
   public void run() {
      this.alerted.clear();
      this.tick = 0L;
   }

   @Override
   public void run2() {
      this.alerted.clear();
      this.tick = 0L;
   }

   @Override
   public void run3() {
      if (class310.player == null || class310.world == null) {
         return;
      }
      this.tick++;
      if (this.tick % (long)Math.max(2, (int)this.interval.getValue()) != 0L) {
         return;
      }

      try {
         this.scan();
      } catch (Throwable ignored) {
      }
   }

   private void scan() {
      ServerLightCache cache = ServerLightCache.get();
      int pcx = class310.player.getBlockPos().getX() >> 4;
      int pcz = class310.player.getBlockPos().getZ() >> 4;
      int r = (int)this.rangeChunks.getValue();
      int threshold = (int)this.minCells.getValue();

      // let a chunk re-alert once it's been out of range for a while (~30s)
      this.alerted.entrySet().removeIf(e -> this.tick - e.getValue() > 600L);

      for (int cx = pcx - r; cx <= pcx + r; cx++) {
         for (int cz = pcz - r; cz <= pcz + r; cz++) {
            long key = ChunkPos.toLong(cx, cz);
            if (this.alerted.containsKey(key)) {
               continue;
            }
            if (!cache.hasChunk(cx, cz)) {
               continue;
            }

            int score = cache.light5Positions(cx, cz).size();
            if (score >= threshold) {
               this.alerted.put(key, this.tick);
               this.fire((cx << 4) + 8, (cz << 4) + 8, score);
            }
         }
      }
   }

   private void fire(int wx, int wz, int score) {
      if (this.chat.getValue() && class310.inGameHud != null) {
         try {
            class310.inGameHud
               .getChatHud()
               .addMessage(Text.literal("§b[BaseAlert] §flikely base §7~ §fX " + wx + " §7/ §fZ " + wz + " §7(" + score + " lit cells)"));
         } catch (Throwable ignored) {
         }
      }

      if (this.sound.getValue() && class310.player != null && class310.world != null) {
         try {
            class310.world
               .playSoundClient(
                  class310.player.getX(),
                  class310.player.getY(),
                  class310.player.getZ(),
                  SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                  SoundCategory.PLAYERS,
                  1.0F,
                  1.4F,
                  false
               );
         } catch (Throwable ignored) {
         }
      }
   }

   @Override
   public String getString3() {
      int n = this.alerted.size();
      return n > 0 ? "§7" + n : null;
   }
}
