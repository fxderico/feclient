package dev.fede.nyx.util;

import dev.fede.nyx.tracker.ChunkActivityScanner;
import dev.fede.nyx.tracker.MapUtils;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public final class d$aUtils {
   private d$aUtils() {
   }

   public static void run(boolean var0, boolean var1) {
      if (var0) {
         MapUtils.run5(true);
      }

      if (var1) {
         ChunkActivityScanner.run5(true);
      }
   }

   public static d$aUtils.Inner1 daOf(ClientPlayerEntity var0, int var1, boolean var2, boolean var3) {
      if (var0 == null) {
         return null;
      } else {
         int var4 = (int)Math.floor(var0.getX()) >> 4;
         int var5 = (int)Math.floor(var0.getZ()) >> 4;
         int var6 = var1 + 15 >> 4;
         int var7 = var6 * var6;
         int var8 = -1;
         long var9 = 0L;
         String var11 = null;
         if (var2) {
            for (Entry var13 : MapUtils.getMap().entrySet()) {
               int var14 = MapUtils.intOf((Long)var13.getKey()) - var4;
               int var15 = MapUtils.intOf2((Long)var13.getKey()) - var5;
               if (var14 * var14 + var15 * var15 <= var7 && (Integer)var13.getValue() > var8) {
                  var8 = (Integer)var13.getValue();
                  var9 = (Long)var13.getKey();
                  var11 = "TunnelBaseScanner";
               }
            }
         }

         if (var3) {
            for (Entry var17 : ChunkActivityScanner.getMap().entrySet()) {
               int var18 = ChunkActivityScanner.intOf((Long)var17.getKey()) - var4;
               int var19 = ChunkActivityScanner.intOf2((Long)var17.getKey()) - var5;
               if (var18 * var18 + var19 * var19 <= var7 && (Integer)var17.getValue() > var8) {
                  var8 = (Integer)var17.getValue();
                  var9 = (Long)var17.getKey();
                  var11 = "ChunkActivityScanner";
               }
            }
         }

         return var8 < 0 ? null : new d$aUtils.Inner1(var9, var8, var11);
      }
   }

   public static void run2() {
      // sound suppressed
   }

   public static String getString() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 == null) {
         return "unknown";
      } else if (var0.isIntegratedServerRunning()) {
         return "singleplayer";
      } else {
         ServerInfo var1 = var0.getCurrentServerEntry();
         return var1 != null && var1.address != null && !var1.address.isEmpty() ? var1.address : "unknown";
      }
   }

   public static void run3(long var0, String var2, String var3, int var4, int var5, int var6, int var7, String var8, String var9, String var10) {
      Path var11 = FabricLoader.getInstance().getConfigDir();
      String var12 = var9 == null ? "" : var9.trim();
      if (var12.isEmpty()) {
         var12 = "codeengine/basefinder_finds.json";
      }

      Path var13;
      try {
         Path var14 = Path.of(var12);
         if (var14.isAbsolute()) {
            var13 = var11.resolve("codeengine").resolve(var14.getFileName().toString());
         } else {
            var13 = var11.resolve(var14).normalize();
            if (!var13.startsWith(var11)) {
               var13 = var11.resolve("codeengine/basefinder_finds.json");
            }
         }

         Files.createDirectories(var13.getParent());
      } catch (Throwable var19) {
         NotificationUtils.run(var10, "Save path resolve failed: " + var19.getClass().getSimpleName(), INFO.UNKNOWN_4, 4000L);
         return;
      }

      String var22 = "{\"ts\":"
         + var0
         + ",\"server\":\""
         + addSetting(var2)
         + "\",\"world\":\""
         + addSetting(var3)
         + "\",\"x\":"
         + var4
         + ",\"y\":"
         + var5
         + ",\"z\":"
         + var6
         + ",\"score\":"
         + var7
         + ",\"source\":\""
         + addSetting(var8)
         + "\",\"module\":\""
         + addSetting(var10)
         + "\"}\n";

      try (BufferedWriter var15 = Files.newBufferedWriter(var13, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
         var15.write(var22);
      } catch (IOException var21) {
         NotificationUtils.run(var10, "Persist failed: " + var21.getClass().getSimpleName(), INFO.UNKNOWN_4, 4000L);
      }
   }

   public static void run4(long var0, String var2, String var3, int var4, int var5, int var6, int var7, String var8, String var9) {
      Path var10 = FabricLoader.getInstance().getConfigDir();
      String var11 = var9 == null ? "" : var9.trim();
      if (var11.isEmpty()) {
         var11 = "codeengine/basefinder_finds.json";
      }

      Path var12;
      try {
         Path var13 = Path.of(var11);
         if (var13.isAbsolute()) {
            var12 = var10.resolve("codeengine").resolve(var13.getFileName().toString());
         } else {
            var12 = var10.resolve(var13).normalize();
            if (!var12.startsWith(var10)) {
               var12 = var10.resolve("codeengine/basefinder_finds.json");
            }
         }

         Files.createDirectories(var12.getParent());
      } catch (Throwable var18) {
         NotificationUtils.run("BaseFinder", "Save path resolve failed: " + var18.getClass().getSimpleName(), INFO.UNKNOWN_4, 4000L);
         return;
      }

      String var21 = "{\"ts\":"
         + var0
         + ",\"server\":\""
         + addSetting(var2)
         + "\",\"world\":\""
         + addSetting(var3)
         + "\",\"x\":"
         + var4
         + ",\"y\":"
         + var5
         + ",\"z\":"
         + var6
         + ",\"score\":"
         + var7
         + ",\"source\":\""
         + addSetting(var8)
         + "\"}\n";

      try (BufferedWriter var14 = Files.newBufferedWriter(var12, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
         var14.write(var21);
      } catch (IOException var20) {
         NotificationUtils.run("BaseFinder", "Persist failed: " + var20.getClass().getSimpleName(), INFO.UNKNOWN_4, 4000L);
      }
   }

   public static String addSetting(String var0) {
      if (var0 == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder(var0.length() + 8);

         for (int var2 = 0; var2 < var0.length(); var2++) {
            char var3 = var0.charAt(var2);
            switch (var3) {
               case '\b':
                  var1.append("\\b");
                  break;
               case '\t':
                  var1.append("\\t");
                  break;
               case '\n':
                  var1.append("\\n");
                  break;
               case '\f':
                  var1.append("\\f");
                  break;
               case '\r':
                  var1.append("\\r");
                  break;
               case '"':
                  var1.append("\\\"");
                  break;
               case '\\':
                  var1.append("\\\\");
                  break;
               default:
                  if (var3 < ' ') {
                     var1.append(String.format("\\u%04x", Integer.valueOf(var3)));
                  } else {
                     var1.append(var3);
                  }
            }
         }

         return var1.toString();
      }
   }

public record Inner1(long chunkKey, int score, String source) {

}
}

