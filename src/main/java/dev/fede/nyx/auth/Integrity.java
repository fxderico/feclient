package dev.fede.nyx.auth;

import dev.fede.nyx.util.AntiDebugUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Integrity {
   private static final String string = null;
   private static volatile Map<String, byte[]> map;
   private static long HKEY = 6214506098449073963L;

   private Integrity() {
   }

   private static Map<String, byte[]> getMap() {
      Map var0 = map;
      if (var0 != null) {
         return var0;
      } else {
         synchronized (Integrity.class) {
            var0 = map;
            if (var0 != null) {
               return var0;
            } else {
               HashMap var22 = new HashMap();

               try (InputStream var2 = Integrity.class
                     .getResourceAsStream(
                        AntiDebugUtil.stringOf(
                           new byte[]{11, -18, 45, 55, -11, -20, -32, -56, -43, 43, -1, -28, -64, -96, 21, -49, 49, -114, -36, 33, -87, -105}
                        )
                     )) {
                  if (var2 != null) {
                     ByteArrayOutputStream var3 = new ByteArrayOutputStream(2048);
                     byte[] var4 = new byte[1024];

                     int var5;
                     while ((var5 = var2.read(var4)) > 0) {
                        var3.write(var4, 0, var5);
                     }

                     byte[] var6 = var3.toByteArray();
                     byte[] var7 = byteArrayOf(var6);

                     String var9;
                     try (BufferedReader var8 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(var7), StandardCharsets.UTF_8))) {
                        while ((var9 = var8.readLine()) != null) {
                           int var10 = var9.indexOf(58);
                           if (var10 > 0 && var10 + 1 < var9.length()) {
                              String var11 = var9.substring(0, var10).trim();
                              String var12 = var9.substring(var10 + 1).trim();
                              byte[] var13 = byteArrayOf4(var12);
                              if (var13.length == 32) {
                                 var22.put(var11, var13);
                              }
                           }
                        }
                     }
                  }
               } catch (Throwable var19) {
               }

               map = var22;
               return var22;
            }
         }
      }
   }

   private static byte[] byteArrayOf(byte[] var0) {
      if (var0 != null && var0.length != 0) {
         byte[] var1 = new byte[var0.length];
         long var2 = HKEY;

         for (int var4 = 0; var4 < var0.length; var4++) {
            var1[var4] = (byte)(var0[var4] ^ (byte)var2);
            var2 = var2 * 6364136223846793005L + 1442695040888963407L;
         }

         return var1;
      } else {
         return var0;
      }
   }

   public static void run() {
      Map var0 = getMap();
      if (!var0.isEmpty()) {
         for (Entry var2 : ((java.util.Map<?,?>)var0).entrySet()) {
            try {
               byte[] var3 = byteArrayOf2("/" + (String)var2.getKey());
               if (var3 != null && var3.length != 0) {
                  byte[] var4 = byteArrayOf3(var3);
                  if (!check2(var4, (byte[])var2.getValue())) {
                     AuthGate.run3();
                  }
               }
            } catch (Throwable var5) {
            }
         }
      }
   }

   private static byte[] byteArrayOf2(String var0) {
      try {
         byte[] var5;
         try (InputStream var1 = Integrity.class.getResourceAsStream(var0)) {
            if (var1 == null) {
               return null;
            }

            ByteArrayOutputStream var2 = new ByteArrayOutputStream(8192);
            byte[] var3 = new byte[4096];

            int var4;
            while ((var4 = var1.read(var3)) > 0) {
               var2.write(var3, 0, var4);
            }

            var5 = var2.toByteArray();
         }

         return var5;
      } catch (Throwable var8) {
         return null;
      }
   }

   private static byte[] byteArrayOf3(byte[] var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         return var1.digest(var0);
      } catch (Throwable var2) {
         return new byte[0];
      }
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

   private static byte[] byteArrayOf4(String var0) {
      if (var0 == null) {
         return new byte[0];
      } else {
         int var1 = var0.length();
         if ((var1 & 1) != 0) {
            return new byte[0];
         } else {
            byte[] var2 = new byte[var1 >> 1];

            for (byte var3 = 0; var3 < var1; var3 += 2) {
               int var4 = Character.digit(var0.charAt(var3), 16);
               int var5 = Character.digit(var0.charAt(var3 + 1), 16);
               if (var4 < 0 || var5 < 0) {
                  return new byte[0];
               }

               var2[var3 >> 1] = (byte)(var4 << 4 | var5);
            }

            return var2;
         }
      }
   }
}
