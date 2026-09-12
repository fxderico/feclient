package dev.fede.nyx.module.modules.addons;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.fede.nyx.util.AntiDebugUtil;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

public final class SpotifyClient {
   private static final String string = null;
   private final SpotifyHUDModule spotifyHUDModule;
   private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).build();
   private final AtomicReference<SpotifyClient.Inner2> atomicReference = new AtomicReference<>(null);
   private volatile SpotifyClient.State spotifyClientState = SpotifyClient.State.IDLE;
   private volatile long longVal = 0L;
   private Thread thread;
   private volatile boolean bool = false;

   SpotifyClient(SpotifyHUDModule var1) {
      this.spotifyHUDModule = var1;
   }

   void run() {
      if (!this.bool) {
         this.bool = true;
         this.thread = new Thread(this::run3, "SpotifyHUD-Poll");
         this.thread.setDaemon(true);
         this.thread.start();
      }
   }

   void run2() {
      this.bool = false;
      Thread var1 = this.thread;
      if (var1 != null) {
         var1.interrupt();
      }

      this.thread = null;
   }

   public SpotifyClient.Inner2 getSpotifyClientb() {
      return this.atomicReference.get();
   }

   public SpotifyClient.State getSpotifyClientState() {
      return this.spotifyClientState;
   }

   private void run3() {
      while (this.bool) {
         try {
            long var1 = System.currentTimeMillis();
            if (var1 >= this.longVal) {
               this.run4();
            }
         } catch (InterruptedException var4) {
            return;
         } catch (Throwable var5) {
            this.spotifyClientState = SpotifyClient.State.ERROR;
            System.err.println("[c] SpotifyHUD: poll error: " + var5.getClass().getSimpleName());
            this.longVal = System.currentTimeMillis() + 10000L;
         }

         try {
            long var6 = Math.max(1000L, this.spotifyHUDModule.refreshSec.getValueLong() * 1000L);
            Thread.sleep(var6);
         } catch (InterruptedException var3) {
            return;
         }
      }
   }

   private void run4() throws Exception {
      HttpRequest var1 = HttpRequest.newBuilder(
            URI.create(
               AntiDebugUtil.stringOf(
                  new byte[]{
                     76,
                     -7,
                     54,
                     35,
                     -29,
                     -77,
                     -95,
                     -128,
                     -35,
                     53,
                     -13,
                     -27,
                     -37,
                     -79,
                     9,
                     -45,
                     61,
                     -101,
                     -117,
                     109,
                     -93,
                     -106,
                     83,
                     -80,
                     -102,
                     -124,
                     101,
                     -42,
                     -67,
                     30,
                     102,
                     -5,
                     -27,
                     20,
                     -57,
                     65,
                     -33,
                     10,
                     -101,
                     -3,
                     110,
                     64,
                     -108,
                     -33,
                     100,
                     -40,
                     -21,
                     -9,
                     -40,
                     -68,
                     43,
                     74,
                     78,
                     -66
                  }
               )
            )
         )
         .header(
            AntiDebugUtil.stringOf(new byte[]{101, -8, 54, 59, -1, -5, -25, -43, -35, 49, -13, -92, -58}),
            AntiDebugUtil.stringOf(new byte[]{102, -24, 35, 33, -11, -5, -82})
         )
         .header(
            AntiDebugUtil.stringOf(new byte[]{101, -18, 33, 54, -32, -3}),
            AntiDebugUtil.stringOf(new byte[]{69, -3, 50, 63, -7, -22, -17, -37, -43, 42, -12, -28, -62, -78, 9, -55})
         )
         .timeout(Duration.ofSeconds(6L))
         .GET()
         .build();
      HttpResponse var2 = this.httpClient.send(var1, BodyHandlers.ofString());
      int var3 = var2.statusCode();
      switch (var3) {
         case 200:
            SpotifyClient.Inner2 var8 = spotifyClientbOf((String)var2.body());
            if (var8 != null) {
               this.atomicReference.set(var8);
               this.spotifyClientState = SpotifyClient.State.OK_2;
               if (this.spotifyHUDModule.showArt.getValue() && var8.albumArtUrl() != null) {
                  SpotifyClient.Inner1.intOf2(var8.albumArtUrl());
               }
            } else {
               this.atomicReference.set(null);
               this.spotifyClientState = SpotifyClient.State.EMPTY;
            }
            break;
         case 204:
            this.atomicReference.set(null);
            this.spotifyClientState = SpotifyClient.State.EMPTY;
            break;
         case 401:
            this.spotifyClientState = SpotifyClient.State.UNAUTHORIZED;
            this.longVal = System.currentTimeMillis() + 30000L;
            break;
         case 429:
            long var4 = 5L;

            try {
               var4 = Long.parseLong(var2.headers().firstValue("Retry-After").orElse("5"));
            } catch (NumberFormatException var7) {
            }

            this.spotifyClientState = SpotifyClient.State.RATE_LIMITED;
            this.longVal = System.currentTimeMillis() + Math.max(1L, var4) * 1000L;
            break;
         default:
            this.spotifyClientState = SpotifyClient.State.ERROR;
            this.longVal = System.currentTimeMillis() + 10000L;
            System.err.println("[c] SpotifyHUD: HTTP " + var3);
      }
   }

   private static SpotifyClient.Inner2 spotifyClientbOf(String var0) {
      try {
         JsonElement var1 = JsonParser.parseString(var0);
         if (!var1.isJsonObject()) {
            return null;
         } else {
            JsonObject var2 = var1.getAsJsonObject();
            if (var2.has("item") && !var2.get("item").isJsonNull()) {
               JsonObject var3 = var2.getAsJsonObject("item");
               String var4 = var3.has("name") && !var3.get("name").isJsonNull() ? var3.get("name").getAsString() : "";
               String var5 = "";
               if (var3.has("artists") && var3.get("artists").isJsonArray()) {
                  JsonArray var6 = var3.getAsJsonArray("artists");
                  if (var6.size() > 0 && var6.get(0).isJsonObject()) {
                     JsonObject var7 = var6.get(0).getAsJsonObject();
                     if (var7.has("name") && !var7.get("name").isJsonNull()) {
                        var5 = var7.get("name").getAsString();
                     }
                  }
               }

               String var16 = null;
               if (var3.has("album") && var3.get("album").isJsonObject()) {
                  JsonObject var17 = var3.getAsJsonObject("album");
                  if (var17.has("images") && var17.get("images").isJsonArray()) {
                     JsonArray var8 = var17.getAsJsonArray("images");
                     int var9 = Integer.MAX_VALUE;

                     for (JsonElement var11 : var8) {
                        if (var11.isJsonObject()) {
                           JsonObject var12 = var11.getAsJsonObject();
                           int var13 = var12.has("width") && !var12.get("width").isJsonNull() ? var12.get("width").getAsInt() : 0;
                           String var14 = var12.has("url") && !var12.get("url").isJsonNull() ? var12.get("url").getAsString() : null;
                           if (var14 != null && var13 >= 64 && var13 < var9) {
                              var9 = var13;
                              var16 = var14;
                           }
                        }
                     }

                     if (var16 == null && var8.size() > 0 && var8.get(0).isJsonObject()) {
                        JsonObject var20 = var8.get(0).getAsJsonObject();
                        if (var20.has("url") && !var20.get("url").isJsonNull()) {
                           var16 = var20.get("url").getAsString();
                        }
                     }
                  }
               }

               long var18 = var2.has("progress_ms") && !var2.get("progress_ms").isJsonNull() ? var2.get("progress_ms").getAsLong() : 0L;
               long var19 = var3.has("duration_ms") && !var3.get("duration_ms").isJsonNull() ? var3.get("duration_ms").getAsLong() : 0L;
               boolean var21 = var2.has("is_playing") && !var2.get("is_playing").isJsonNull() && var2.get("is_playing").getAsBoolean();
               return new SpotifyClient.Inner2(var4, var5, var16, var18, var19, var21, System.currentTimeMillis());
            } else {
               return null;
            }
         }
      } catch (Throwable var15) {
         return null;
      }
   }

   public static enum State {
      IDLE,
      OK_2,
      EMPTY,
      UNAUTHORIZED,
      RATE_LIMITED,
      ERROR;

      private static final SpotifyClient.State[] spotifyClientStateArray = getSpotifyClientStateArray();

      private static SpotifyClient.State[] getSpotifyClientStateArray() {
         return new SpotifyClient.State[]{IDLE, OK_2, EMPTY, UNAUTHORIZED, RATE_LIMITED, ERROR};
      }
   }

public final static class Inner1 {
   private static final int intVal = 8;
   private static final int intVal2 = 128;
   private static final int intVal3 = 524288;
   private static final ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
   private static final LinkedHashMap<String, Identifier> linkedHashMap = new LinkedHashMap<>();
   private static final ConcurrentHashMap<String, Boolean> concurrentHashMap2 = new ConcurrentHashMap<>();
   private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5L)).build();

   private Inner1() {
   }

   public static int intOf2(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         Integer var1 = concurrentHashMap.get(var0);
         if (var1 != null && var1 > 0) {
            return var1;
         } else if (concurrentHashMap2.putIfAbsent(var0, Boolean.TRUE) != null) {
            return 0;
         } else {
            Thread var2 = new Thread(() -> {}, "SpotifyHUD-Art");
            var2.setDaemon(true);
            var2.start();
            return 0;
         }
      } else {
         return 0;
      }
   }

   private static void run3(String var0) {
      try {
         HttpRequest var1 = HttpRequest.newBuilder(URI.create(var0)).timeout(Duration.ofSeconds(10L)).GET().build();
         HttpResponse var2 = httpClient.send(var1, BodyHandlers.ofByteArray());
         if (var2.statusCode() != 200) {
            return;
         }

         byte[] var3 = (byte[])var2.body();
         if (var3 == null || var3.length == 0 || var3.length > 524288) {
            return;
         }

         BufferedImage var4 = ImageIO.read(new ByteArrayInputStream(var3));
         if (var4 == null) {
            return;
         }

         BufferedImage var5 = bufferedImageOf(var4, 128);
         MinecraftClient var6 = MinecraftClient.getInstance();
         if (var6 != null) {
            var6.execute(() -> {});
            return;
         }
      } catch (InterruptedException | IOException var11) {
         System.err.println("[c] SpotifyHUD: art download failed (" + var11.getClass().getSimpleName() + ")");
         return;
      } catch (Throwable var12) {
         System.err.println("[c] SpotifyHUD: art decode failed (" + var12.getClass().getSimpleName() + ")");
         return;
      } finally {
         concurrentHashMap2.remove(var0);
      }
   }

   private static BufferedImage bufferedImageOf(BufferedImage var0, int var1) {
      int var2 = var0.getWidth();
      int var3 = var0.getHeight();
      if (var2 <= var1 && var3 <= var1) {
         return var0;
      } else {
         float var4 = (float)var1 / var2;
         float var5 = (float)var1 / var3;
         float var6 = Math.min(var4, var5);
         int var7 = Math.max(1, Math.round(var2 * var6));
         int var8 = Math.max(1, Math.round(var3 * var6));
         BufferedImage var9 = new BufferedImage(var7, var8, 2);
         Graphics2D var10 = var9.createGraphics();

         try {
            var10.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            var10.drawImage(var0, 0, 0, var7, var8, null);
         } finally {
            var10.dispose();
         }

         return var9;
      }
   }

   private static void run(String var0, BufferedImage var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null) {
         TextureManager var3 = var2.getTextureManager();
         if (var3 != null) {
            int var4 = var1.getWidth();
            int var5 = var1.getHeight();
            NativeImage var6 = new NativeImage(var4, var5, false);
            int[] var7 = new int[var4 * var5];
            var1.getRGB(0, 0, var4, var5, var7, 0, var4);

            for (int var8 = 0; var8 < var5; var8++) {
               for (int var9 = 0; var9 < var4; var9++) {
                  var6.setColorArgb(var9, var8, var7[var8 * var4 + var9]);
               }
            }

            String var12 = Integer.toHexString(var0.hashCode());
            Identifier var13 = Identifier.of("nyx", "spotify_art/null");
            NativeImageBackedTexture var10 = new NativeImageBackedTexture((java.util.function.Supplier)() -> null, var6);
            var3.registerTexture(var13, var10);
            run2(var0, var13);
            int var11 = intOf(var3, var13);
            if (var11 > 0) {
               concurrentHashMap.put(var0, var11);
            } else {
               concurrentHashMap.put(var0, 0);
            }
         }
      }
   }

   private static int intOf(TextureManager var0, Identifier var1) {
      AbstractTexture var2 = var0.getTexture(var1);
      if (var2 == null) {
         return 0;
      } else if (var2.getGlTexture() instanceof GlTexture var4) {
         int var5 = var4.getGlId();
         return var5 > 0 ? var5 : 0;
      } else {
         return 0;
      }
   }

   private static void run2(String var0, Identifier var1) {
      synchronized (linkedHashMap) {
         Identifier var3 = linkedHashMap.remove(var0);
         if (var3 != null) {
            run5(var3);
         }

         while (linkedHashMap.size() >= 8) {
            Iterator var4 = linkedHashMap.entrySet().iterator();
            if (!var4.hasNext()) {
               break;
            }

            Entry var5 = (Entry)var4.next();
            var4.remove();
            concurrentHashMap.remove(var5.getKey());
            run5((Identifier)var5.getValue());
         }

         linkedHashMap.put(var0, var1);
      }
   }

   public static void run4() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      Runnable var1 = Inner1::run6;
      if (var0 != null) {
         var0.execute(var1);
      } else {
         var1.run();
      }
   }

   private static void run5(Identifier var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null) {
         TextureManager var2 = var1.getTextureManager();
         if (var2 != null) {
            try {
               var2.destroyTexture(var0);
            } catch (Throwable var4) {
            }
         }
      }
   }

   private static void run6() {
      synchronized (linkedHashMap) {
         for (Identifier var2 : linkedHashMap.values()) {
            run5(var2);
         }

         linkedHashMap.clear();
      }

      concurrentHashMap.clear();
      concurrentHashMap2.clear();
   }

   private static String addSetting(String var0) {
      return "nyx:spotify_art/null";
   }

   private static void run7(String var0, BufferedImage var1) {
      run(var0, var1);
   }

   private static void run8(String var0) {
      run3(var0);
   }
}

public record Inner2(String title, String artist, String albumArtUrl, long progressMs, long durationMs, boolean playing, long lastFetchMs) {


   public boolean isPlaying() {
      return this.playing;
   }
}
}

