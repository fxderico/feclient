package dev.fede.nyx.auth;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class AntiTamper {
   private static final String string = "dev/nyx/auth/AntiTamper.class";
   private static final byte[] EXPECTED = new byte[]{
      51, 21, -37, 77, -5, 73, 4, -33, 62, 110, 99, -14, -109, 75, 38, -82, -59, 22, 125, 95, 25, 65, 102, 13, -92, -8, 85, -127, 73, -85, -102, 2
   };

   private AntiTamper() {
   }

   public static void run() {
      try {
         if (check(EXPECTED)) {
            return;
         }

         URL var0 = getURL();
         if (var0 == null) {
            return;
         }

         Path var1 = pathOf(var0);
         if (var1 == null) {
            var1 = getPath();
         }

         if (var1 == null) {
            return;
         }

         if (!Files.isRegularFile(var1)) {
            return;
         }

         String var2 = var1.getFileName().toString();
         if (var2 == null) {
            return;
         }

         String var3 = var2.toLowerCase(Locale.ROOT);
         if (!var3.endsWith(".jar")) {
            return;
         }

         byte[] var4 = byteArrayOf(var1, "dev/nyx/auth/AntiTamper.class");
         if (var4 == null || var4.length != 32) {
            return;
         }

         if (!check2(var4, EXPECTED)) {
            AuthGate.run3();
         }
      } catch (Throwable var5) {
      }
   }

   private static URL getURL() {
      try {
         return AntiTamper.class.getProtectionDomain().getCodeSource().getLocation();
      } catch (Throwable var1) {
         return null;
      }
   }

   private static Path pathOf(URL var0) {
      try {
         URI var1 = var0.toURI();
         String var2 = var1.getScheme();
         return var2 != null && var2.equalsIgnoreCase("file") ? Paths.get(var1) : null;
      } catch (Throwable var3) {
         return null;
      }
   }

   private static Path getPath() {
      try {
         Class var0 = Class.forName("net.fabricmc.loader.api.FabricLoader");
         Object var1 = var0.getMethod("getInstance").invoke(null);
         Object var2 = var0.getMethod("getModContainer", String.class).invoke(var1, "codeengine");
         if (var2 == null) {
            return null;
         }

         Boolean var3 = (Boolean)var2.getClass().getMethod("isPresent").invoke(var2);
         if (var3 == null || !var3) {
            return null;
         }

         Object var4 = var2.getClass().getMethod("get").invoke(var2);
         Object var5 = var4.getClass().getMethod("getOrigin").invoke(var4);
         Object var6 = var5.getClass().getMethod("getPaths").invoke(var5);
         if (!(var6 instanceof List)) {
            return null;
         }

         for (Object var8 : (List)var6) {
            if (var8 instanceof Path var9 && Files.isRegularFile(var9)) {
               return var9;
            }
         }
      } catch (Throwable var10) {
      }

      return null;
   }

   private static boolean check(byte[] var0) {
      byte var1 = 0;

      for (byte var5 : var0) {
         var1 |= var5;
      }

      return var1 == 0;
   }

   private static boolean check2(byte[] var0, byte[] var1) {
      if (var0 != null && var1 != null && var0.length == var1.length) {
         int var2 = 0;

         for (int var3 = 0; var3 < var0.length; var3++) {
            var2 |= var0[var3] ^ var1[var3];
         }

         return var2 == 0;
      } else {
         return false;
      }
   }

   private static byte[] byteArrayOf(Path var0, String var1) {
      try {
         MessageDigest var2 = MessageDigest.getInstance("SHA-256");

         try (JarFile var3 = new JarFile(var0.toFile())) {
            Enumeration var4 = var3.entries();
            byte[] var5 = new byte[8192];

            while (var4.hasMoreElements()) {
               JarEntry var6 = (JarEntry)var4.nextElement();
               if (!var6.isDirectory() && !var1.equals(var6.getName())) {
                  var2.update(var6.getName().getBytes(StandardCharsets.UTF_8));
                  var2.update((byte)0);

                  int var8;
                  try (InputStream var7 = var3.getInputStream(var6)) {
                     while ((var8 = var7.read(var5)) > 0) {
                        var2.update(var5, 0, var8);
                     }
                  }
               }
            }
         }

         return var2.digest();
      } catch (Throwable var14) {
         return null;
      }
   }
}
