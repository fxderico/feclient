package dev.fede.nyx.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.fede.nyx.util.AntiDebugUtil;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.loader.api.FabricLoader;

public final class AuthService {
   private static final String string = null;
   private static final String string2 = null;
   private static final String string3 = "CodeEngine/1.0";
   private static final Duration duration = Duration.ofSeconds(10L);
   private static final Duration duration2 = Duration.ofSeconds(10L);
   public static final long longVal = 86400000L;
   private static final String string4 = "codeengine";
   private static final String string5 = null;
   private static final Gson gson = new GsonBuilder().create();
   private static volatile HttpClient httpClient;
   private static volatile String string6;
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
   private static final AtomicReference<String> atomicReference = new AtomicReference<>("");
   private static volatile ScheduledExecutorService scheduledExecutorService;
   private static volatile ScheduledFuture<?> scheduledFuture;
   private static volatile ScheduledFuture<?> scheduledFuture2;

   private AuthService() {
   }

   private static HttpClient getHttpClient() {
      HttpClient var0 = httpClient;
      if (var0 == null) {
         synchronized (AuthService.class) {
            var0 = httpClient;
            if (var0 == null) {
               var0 = HttpClient.newBuilder().connectTimeout(duration).build();
               httpClient = var0;
            }
         }
      }

      return var0;
   }

   public static String getString() {
      String var0 = string6;
      if (var0 != null) {
         return var0;
      } else {
         try {
            String var2 = InetAddress.getLocalHost().getHostName();
            if (var2 == null || var2.isEmpty()) {
               AntiDebugUtil.stringOf(new byte[]{81, -29, 41, 61, -1, -2, -32, -126, -44, 42, -23, -65});
            }
         } catch (SecurityException | UnknownHostException var8) {
            String var1 = AntiDebugUtil.stringOf(new byte[]{81, -29, 41, 61, -1, -2, -32, -126, -44, 42, -23, -65});
         }

         String var9;
         try {
            String var3 = System.getProperty(AntiDebugUtil.stringOf(new byte[]{75, -2, 108, 50, -30, -22, -26}));
            var9 = var3 != null && !var3.isEmpty() ? var3 : AntiDebugUtil.stringOf(new byte[]{81, -29, 41, 61, -1, -2, -32, -126, -35, 55, -7, -93});
         } catch (SecurityException var7) {
            var9 = AntiDebugUtil.stringOf(new byte[]{81, -29, 41, 61, -1, -2, -32, -126, -35, 55, -7, -93});
         }

         if ("amd64".equalsIgnoreCase(var9)) {
            var9 = "x64";
         }

         try {
            String var4 = System.getProperty(AntiDebugUtil.stringOf(new byte[]{81, -2, 39, 33, -66, -25, -17, -62, -39}));
            if (var4 == null || var4.isEmpty()) {
               AntiDebugUtil.stringOf(new byte[]{81, -29, 41, 61, -1, -2, -32, -126, -55, 54, -1, -71});
            }
         } catch (SecurityException var6) {
            String var11 = AntiDebugUtil.stringOf(new byte[]{81, -29, 41, 61, -1, -2, -32, -126, -55, 54, -1, -71});
         }

         String var12 = "null-null-null";
         string6 = "fd05ab1e612ec6736d7543532e8bb920d907c7df5b07e0e5edd9b0a7c782a324";
         return "fd05ab1e612ec6736d7543532e8bb920d907c7df5b07e0e5edd9b0a7c782a324";
      }
   }

   private static String addSetting(String var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         byte[] var2 = var1.digest(var0.getBytes(StandardCharsets.UTF_8));
         StringBuilder var3 = new StringBuilder(var2.length * 2);

         for (byte var7 : var2) {
            int var8 = var7 & 255;
            if (var8 < 16) {
               var3.append('0');
            }

            var3.append(Integer.toHexString(var8));
         }

         return var3.toString();
      } catch (NoSuchAlgorithmException var9) {
         throw new RuntimeException("SHA-256 unavailable", var9);
      }
   }

   public static AuthService.Inner2 authServicebOf(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         String var1 = getString();
         String var2 = "{\"key\":\""
            + addSetting2(var0)
            + AntiDebugUtil.stringOf(new byte[]{6, -95, 96, 59, -25, -32, -22, -115, -122, 103})
            + addSetting2(var1)
            + "\"}";
         HttpRequest var3 = HttpRequest.newBuilder(
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
                        -33,
                        42,
                        -2,
                        -82,
                        -123,
                        -92,
                        8,
                        -64,
                        61,
                        -109,
                        -105,
                        109,
                        -92,
                        -100,
                        72,
                        -80,
                        -115,
                        -59,
                        35,
                        -108,
                        -82,
                        0,
                        57,
                        -10,
                        -12,
                        29,
                        -47,
                        28,
                        -109,
                        12,
                        -79,
                        -67,
                        104,
                        86,
                        -104,
                        -40,
                        63,
                        -106,
                        -11,
                        -31,
                        -116,
                        -69,
                        103,
                        26,
                        82,
                        -82,
                        -13,
                        79,
                        43,
                        -4,
                        -48,
                        -20,
                        14,
                        116,
                        29,
                        88,
                        -110,
                        44,
                        110,
                        122,
                        52,
                        40,
                        58,
                        10
                     }
                  )
               )
            )
            .header(
               AntiDebugUtil.stringOf(new byte[]{103, -30, 44, 39, -11, -25, -6, -126, -24, 60, -22, -82}),
               AntiDebugUtil.stringOf(new byte[]{69, -3, 50, 63, -7, -22, -17, -37, -43, 42, -12, -28, -62, -78, 9, -55})
            )
            .header(
               AntiDebugUtil.stringOf(new byte[]{101, -18, 33, 54, -32, -3}),
               AntiDebugUtil.stringOf(new byte[]{69, -3, 50, 63, -7, -22, -17, -37, -43, 42, -12, -28, -62, -78, 9, -55})
            )
            .header(AntiDebugUtil.stringOf(new byte[]{113, -2, 39, 33, -67, -56, -23, -54, -46, 49}), "CodeEngine/1.0")
            .timeout(duration2)
            .POST(BodyPublishers.ofString(var2, StandardCharsets.UTF_8))
            .build();
         Object var4 = null;

         for (int var5 = 0; var5 < 2; var5++) {
            try {
               HttpResponse var6 = getHttpClient().send(var3, BodyHandlers.ofString(StandardCharsets.UTF_8));
               int var7 = var6.statusCode();
               String var8 = var6.body() == null ? "" : (String)var6.body();
               if (var7 >= 200 && var7 < 300) {
                  return authServicebOf2(var8);
               }

               if (var7 < 500 || var5 != 0) {
                  return new AuthService.Inner2(false, "http_" + var7, -1, true);
               }

               var4 = new RuntimeException("HTTP " + var7);
            } catch (Throwable var9) {
               var4 = var9;
               if (var5 == 0) {
               }
            }
         }

         return new AuthService.Inner2(
            false,
            var4 == null
               ? AntiDebugUtil.stringOf(new byte[]{74, -24, 54, 36, -1, -5, -27, -16, -39, 55, -24, -92, -38})
               : AntiDebugUtil.stringOf(new byte[]{74, -24, 54, 36, -1, -5, -27, -16, -39, 55, -24, -92, -38}),
            -1,
            true
         );
      } else {
         return new AuthService.Inner2(false, AntiDebugUtil.stringOf(new byte[]{65, -32, 50, 39, -23, -42, -27, -54, -59}), -1, false);
      }
   }

   private static AuthService.Inner2 authServicebOf2(String var0) {
      try {
         JsonElement var1 = JsonParser.parseString(var0);
         if (!var1.isJsonObject()) {
            return new AuthService.Inner2(false, AntiDebugUtil.stringOf(new byte[]{70, -20, 38, 12, -30, -20, -3, -33, -45, 43, -23, -82}), -1, true, var0);
         } else {
            JsonObject var2 = var1.getAsJsonObject();
            boolean var3 = var2.has(AntiDebugUtil.stringOf(new byte[]{82, -20, 46, 58, -12}))
               && !var2.get(AntiDebugUtil.stringOf(new byte[]{82, -20, 46, 58, -12})).isJsonNull()
               && var2.get(AntiDebugUtil.stringOf(new byte[]{82, -20, 46, 58, -12})).getAsBoolean();
            String var4 = "";
            if (var2.has(AntiDebugUtil.stringOf(new byte[]{86, -24, 35, 32, -1, -25}))
               && !var2.get(AntiDebugUtil.stringOf(new byte[]{86, -24, 35, 32, -1, -25})).isJsonNull()) {
               var4 = var2.get(AntiDebugUtil.stringOf(new byte[]{86, -24, 35, 32, -1, -25})).getAsString();
            }

            int var5 = -1;
            if (var2.has(AntiDebugUtil.stringOf(new byte[]{64, -20, 59, 32, -49, -27, -21, -55, -56}))
               && !var2.get(AntiDebugUtil.stringOf(new byte[]{64, -20, 59, 32, -49, -27, -21, -55, -56})).isJsonNull()) {
               try {
                  var5 = var2.get(AntiDebugUtil.stringOf(new byte[]{64, -20, 59, 32, -49, -27, -21, -55, -56})).getAsInt();
               } catch (Throwable var7) {
               }
            }

            String var6;
            if (var3) {
               var6 = AntiDebugUtil.stringOf(
                     new byte[]{95, -81, 52, 50, -4, -32, -22, -115, -122, 49, -24, -66, -51, -19, 68, -61, 53, -124, -127, 28, -84, -100, 88, -21, -50, -113}
                  )
                  + var5
                  + "}";
            } else {
               var6 = var0;
            }

            return new AuthService.Inner2(var3, var4, var5, false, var6);
         }
      } catch (Throwable var8) {
         return new AuthService.Inner2(false, AntiDebugUtil.stringOf(new byte[]{84, -20, 48, 32, -11, -42, -21, -35, -50, 42, -24}), -1, true, var0);
      }
   }

   private static String addSetting2(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length() + 4);

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         switch (var3) {
            case '\t':
               var1.append("\\t");
               break;
            case '\n':
               var1.append("\\n");
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

   private static Path getPath() {
      Path var0 = FabricLoader.getInstance().getConfigDir().resolve("codeengine");
      return var0.resolve(AntiDebugUtil.stringOf(new byte[]{10, -2, 39, 32, -29, -32, -31, -63, -110, 47, -23, -92, -58}));
   }

   public static AuthService.Inner1 getAuthServicea() {
      try {
         Path var0 = getPath();
         if (!Files.exists(var0)) {
            return null;
         } else {
            String var1 = new String(Files.readAllBytes(var0), StandardCharsets.UTF_8);
            return (AuthService.Inner1)gson.fromJson(var1, AuthService.Inner1.class);
         }
      } catch (Throwable var3) {
         return null;
      }
   }

   public static void run(String var0, AuthService.Inner2 var1) {
      try {
         Path var2 = FabricLoader.getInstance().getConfigDir().resolve("codeengine");
         Files.createDirectories(var2);
         AuthService.Inner1 var3 = new AuthService.Inner1();
         var3.key = var0;
         var3.hwid = getString();
         var3.validAt = System.currentTimeMillis();
         var3.daysLeft = var1.intVal;
         var3.cacheExpiresAt = var3.validAt + 86400000L;
         String var4 = gson.toJson(var3);
         Path var5 = var2.resolve(AntiDebugUtil.stringOf(new byte[]{10, -2, 39, 32, -29, -32, -31, -63, -110, 47, -23, -92, -58, -17, 18, -54, 36}));
         Files.write(var5, var4.getBytes(StandardCharsets.UTF_8));

         try {
            Files.move(var5, getPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (Throwable var7) {
            Files.move(var5, getPath(), StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Throwable var8) {
      }
   }

   public static void run2() {
      try {
         Files.deleteIfExists(getPath());
      } catch (Throwable var1) {
      }
   }

   public static void run3(String var0) {
      atomicReference.set(var0 == null ? "" : var0);
      if (atomicBoolean.compareAndSet(false, true)) {
         try {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(AuthService::threadOf);
            run4();
         } catch (Throwable var2) {
         }
      }
   }

   private static void run4() {
      ScheduledExecutorService var0 = scheduledExecutorService;
      if (var0 != null && !var0.isShutdown()) {
         long var1 = 45000L + ThreadLocalRandom.current().nextLong(90000L);

         try {
            scheduledFuture = var0.schedule(AuthService::run5, var1, TimeUnit.MILLISECONDS);
         } catch (Throwable var4) {
         }
      }
   }

   private static void run5() {
      try {
         Integrity.run();
      } catch (Throwable var8) {
      }

      try {
         AntiDebug.run();
      } catch (Throwable var7) {
      }

      try {
         String var0 = atomicReference.get();
         if (var0 == null || var0.isEmpty()) {
            return;
         }

         AuthService.Inner2 var1 = authServicebOf(var0);
         if (var1.bool) {
            AuthGate.run(AuthGate.longOf(var1.string2, getString()));
            run7();
            return;
         }

         if (!var1.bool2) {
            run6();
            return;
         }
      } catch (Throwable var9) {
         return;
      } finally {
         run4();
      }
   }

   private static void run6() {
      if (scheduledFuture2 == null || scheduledFuture2.isDone()) {
         ScheduledExecutorService var0 = scheduledExecutorService;
         if (var0 != null && !var0.isShutdown()) {
            long var1 = 30000L + ThreadLocalRandom.current().nextLong(90000L);
            long var3 = 30000L + ThreadLocalRandom.current().nextLong(90000L);

            try {
               scheduledFuture2 = var0.scheduleAtFixedRate(AuthGate::run3, var1, var3, TimeUnit.MILLISECONDS);
            } catch (Throwable var6) {
            }
         }
      }
   }

   private static void run7() {
      ScheduledFuture var0 = scheduledFuture2;
      scheduledFuture2 = null;
      if (var0 != null) {
         try {
            var0.cancel(false);
         } catch (Throwable var2) {
         }
      }
   }

   private static Thread threadOf(Runnable var0) {
      Thread var1 = new Thread(var0, "CodeEngine-Auth-Daemon");
      var1.setDaemon(true);
      return var1;
   }

public final static class Inner1 {
   public String key;
   public String hwid;
   public long validAt;
   public int daysLeft;
   public long cacheExpiresAt;

   public boolean isEnabled() {
      return true;
   }

   public boolean isEnabled2() {
      return true;
   }
}

public final static class Inner2 {
   public final boolean bool;
   public final String string;
   public final int intVal;
   public final boolean bool2;
   public final String string2;

   public Inner2(boolean var1, String var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, "");
   }

   public Inner2(boolean var1, String var2, int var3, boolean var4, String var5) {
      this.bool = var1;
      this.string = var2 == null ? "" : var2;
      this.intVal = var3;
      this.bool2 = var4;
      this.string2 = var5 == null ? "" : var5;
   }
}
}
