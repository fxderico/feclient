package dev.fede.nyx.module.modules.addons;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

public final class SmtcMediaClient {
   private static final String string = "codeengine-smtc-now-playing.ps1";
   private static final String string2 = "/assets/codeengine/scripts/smtc-now-playing.ps1";
   private static volatile Path path;
   private static final boolean bool = System.getProperty("os.name", "").toLowerCase().contains("win");
   private static boolean bool2 = false;
   private final LongSupplier longSupplier;
   private final AtomicReference<SmtcMediaClient.Inner1> atomicReference = new AtomicReference<>(null);
   private volatile SmtcMediaClient.State smtcMediaClientState = SmtcMediaClient.State.IDLE;
   private volatile String string3 = null;
   private volatile String string4 = null;
   private Thread thread;
   private volatile boolean bool3 = false;

   public SmtcMediaClient(LongSupplier refreshSecSupplier) {
      this.longSupplier = refreshSecSupplier != null ? refreshSecSupplier : SmtcMediaClient::getLong;
   }

   public void run() {
      if (!this.bool3) {
         if (!bool) {
            this.smtcMediaClientState = SmtcMediaClient.State.UNSUPPORTED;
            synchronized (SmtcMediaClient.class) {
               if (!bool2) {
                  System.err.println("[c] SMTC only available on Windows - SpotifyHUD disabled");
                  bool2 = true;
               }
            }
         } else {
            this.bool3 = true;
            this.thread = new Thread(this::run3, "SpotifyHUD-SMTC");
            this.thread.setDaemon(true);
            this.thread.start();
         }
      }
   }

   public void run2() {
      this.bool3 = false;
      Thread var1 = this.thread;
      if (var1 != null) {
         var1.interrupt();
      }

      this.thread = null;
   }

   public SmtcMediaClient.Inner1 getSmtcMediaClienta() {
      return this.atomicReference.get();
   }

   public SmtcMediaClient.State getSmtcMediaClientState() {
      return this.smtcMediaClientState;
   }

   private void run3() {
      while (this.bool3) {
         try {
            this.run4(getPath());
         } catch (InterruptedException var4) {
            return;
         } catch (Throwable var5) {
            this.smtcMediaClientState = SmtcMediaClient.State.ERROR;
            this.run6("poll error: " + var5.getClass().getSimpleName());
         }

         try {
            long var1 = Math.max(1000L, this.longSupplier.getAsLong() * 1000L);
            Thread.sleep(var1);
         } catch (InterruptedException var3) {
            return;
         }
      }
   }

   private void run4(Path var1) throws Exception {
      ProcessBuilder var2 = new ProcessBuilder("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", var1.toString());
      var2.redirectErrorStream(true);
      Process var3 = var2.start();

      String var4;
      int var5;
      try (
         InputStream var6 = var3.getInputStream();
         BufferedReader var7 = new BufferedReader(new InputStreamReader(var6, StandardCharsets.UTF_8));
      ) {
         if (!var3.waitFor(10L, TimeUnit.SECONDS)) {
            var3.destroyForcibly();
            this.smtcMediaClientState = SmtcMediaClient.State.ERROR;
            this.run6("powershell timeout (>10s)");
            return;
         }

         var4 = var7.lines().collect(Collectors.joining("\n"));
         var5 = var3.exitValue();
      }

      if (var4 != null && var4.startsWith("\ufeff")) {
         var4 = var4.substring(1);
      }

      String var14 = addSetting(var4);
      if (var14.isEmpty()) {
         String var15 = stringOf(var4, 200);
         this.run6("empty PS output (exit=" + var5 + "): [" + var15 + "]");
         this.smtcMediaClientState = SmtcMediaClient.State.ERROR;
      } else {
         this.run5(var14, var4);
      }
   }

   private static String stringOf(String var0, int var1) {
      if (var0 == null) {
         return "<null>";
      } else {
         StringBuilder var2 = new StringBuilder();
         int var3 = Math.min(var0.length(), var1);

         for (int var4 = 0; var4 < var3; var4++) {
            char var5 = var0.charAt(var4);
            if (var5 >= ' ' && var5 < 127) {
               var2.append(var5);
            } else {
               var2.append(String.format("\\x%02X", Integer.valueOf(var5)));
            }
         }

         if (var0.length() > var1) {
            var2.append("...(+").append(var0.length() - var1).append(")");
         }

         return var2.toString();
      }
   }

   private static String addSetting(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         for (String var4 : var0.split("\\r?\\n")) {
            String var5 = var4.trim();
            if (!var5.isEmpty() && var5.indexOf(123) >= 0) {
               return var5;
            }
         }

         return "";
      } else {
         return "";
      }
   }

   private void run5(String var1, String var2) {
      if (var1 != null && !var1.isEmpty()) {
         try {
            JsonElement var3 = JsonParser.parseString(var1);
            if (!var3.isJsonObject()) {
               throw new IllegalStateException("not a JSON object");
            }

            JsonObject var10 = var3.getAsJsonObject();
            if (var10.has("error")) {
               this.smtcMediaClientState = SmtcMediaClient.State.ERROR;
               this.run6("PS: " + stringOf3(var10, "error"));
               return;
            }

            boolean var5 = var10.has("playing") && !var10.get("playing").isJsonNull() && var10.get("playing").getAsBoolean();
            if (!var5 && !var10.has("title")) {
               this.atomicReference.set(null);
               this.smtcMediaClientState = SmtcMediaClient.State.EMPTY;
               this.string3 = null;
               return;
            }

            long var6 = longOf(var10, "capturedAtMs");
            if (var6 <= 0L) {
               var6 = System.currentTimeMillis();
            }

            SmtcMediaClient.Inner1 var8 = new SmtcMediaClient.Inner1(
               stringOf3(var10, "title"),
               stringOf3(var10, "artist"),
               stringOf3(var10, "album"),
               longOf(var10, "positionMs"),
               longOf(var10, "durationMs"),
               var5,
               stringOf3(var10, "source"),
               stringOf3(var10, "artPath"),
               stringOf3(var10, "artKey"),
               var6
            );
            this.atomicReference.set(var8);
            this.smtcMediaClientState = SmtcMediaClient.State.OK_2;
            System.out.println("[c] SMTC OK: " + (var8.source().isEmpty() ? "<unknown>" : var8.source()));
            this.string3 = null;
         } catch (Throwable var9) {
            this.smtcMediaClientState = SmtcMediaClient.State.ERROR;
            String var4 = stringOf2(var2, 200);
            if (!Objects.equals(var2, this.string4)) {
               this.string4 = var2;
               System.err.println("[c] SpotifyHUD SMTC: parse error: " + var9.getClass().getSimpleName() + " raw=\"" + var4 + "\"");
               this.string3 = "parse error: " + var9.getClass().getSimpleName();
            } else {
               this.run6("parse error: " + var9.getClass().getSimpleName());
            }
         }
      } else {
         this.smtcMediaClientState = SmtcMediaClient.State.ERROR;
         this.run6("empty PS output");
      }
   }

   private static String stringOf2(String var0, int var1) {
      if (var0 == null) {
         return "<null>";
      } else {
         int var2 = Math.min(var0.length(), var1);
         StringBuilder var3 = new StringBuilder(var2 + 16);

         for (int var4 = 0; var4 < var2; var4++) {
            char var5 = var0.charAt(var4);
            if (var5 >= ' ' && var5 < 127 && var5 != '"' && var5 != '\\') {
               var3.append(var5);
            } else {
               var3.append(String.format("\\x%02X", Integer.valueOf(var5)));
            }
         }

         if (var0.length() > var1) {
            var3.append("...");
         }

         return var3.toString();
      }
   }

   private static Path getPath() throws IOException {
      Path var0 = path;
      if (var0 != null) {
         return var0;
      } else {
         synchronized (SmtcMediaClient.class) {
            if (path != null) {
               return path;
            } else {
               Path var2 = Path.of(System.getProperty("java.io.tmpdir"), "codeengine-smtc-now-playing.ps1");

               byte[] var3;
               try (InputStream var4 = SmtcMediaClient.class.getResourceAsStream("/assets/codeengine/scripts/smtc-now-playing.ps1")) {
                  if (var4 == null) {
                     throw new IOException("PS1 resource missing: /assets/codeengine/scripts/smtc-now-playing.ps1");
                  }

                  var3 = var4.readAllBytes();
               }

               boolean var12 = true;
               if (Files.exists(var2)) {
                  try {
                     byte[] var5 = Files.readAllBytes(var2);
                     var12 = !check2(var5, var3);
                  } catch (IOException var9) {
                     var12 = true;
                  }
               }

               if (var12) {
                  Files.write(var2, var3);
               }

               path = var2;
               return var2;
            }
         }
      }
   }

   private static boolean check2(byte[] var0, byte[] var1) {
      try {
         MessageDigest var2 = MessageDigest.getInstance("SHA-256");
         String var3 = HexFormat.of().formatHex(var2.digest(var0));
         var2.reset();
         String var4 = HexFormat.of().formatHex(var2.digest(var1));
         return var3.equals(var4);
      } catch (Exception var5) {
         return Arrays.equals(var0, var1);
      }
   }

   private static String stringOf3(JsonObject var0, String var1) {
      return var0.has(var1) && !var0.get(var1).isJsonNull() ? var0.get(var1).getAsString() : "";
   }

   private static long longOf(JsonObject var0, String var1) {
      try {
         return var0.has(var1) && !var0.get(var1).isJsonNull() ? var0.get(var1).getAsLong() : 0L;
      } catch (Throwable var3) {
         return 0L;
      }
   }

   private void run6(String var1) {
      if (var1 != null && !var1.equals(this.string3)) {
         this.string3 = var1;
         System.err.println("[c] SpotifyHUD SMTC: null");
      }
   }

   private static long getLong() {
      return 5L;
   }

   public static enum State {
      IDLE,
      OK_2,
      EMPTY,
      UNSUPPORTED,
      ERROR;

      private static final SmtcMediaClient.State[] smtcMediaClientStateArray = getSmtcMediaClientStateArray();

      private static SmtcMediaClient.State[] getSmtcMediaClientStateArray() {
         return new SmtcMediaClient.State[]{IDLE, OK_2, EMPTY, UNSUPPORTED, ERROR};
      }
   }

public record Inner1(String title, String artist, String album, long positionMs, long durationMs, boolean playing, String source, String artPath, String artKey, long lastPollMs) {

}
}

