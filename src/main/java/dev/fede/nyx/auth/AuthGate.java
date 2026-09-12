package dev.fede.nyx.auth;

import dev.fede.nyx.util.AntiDebugUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;

public final class AuthGate {
   private static final long longVal = 5882800391733964898L;
   private static final long longVal2 = 3350426598947333385L;
   public static final long VALID_SEED = 9213517063628412267L;
   public static final long longVal3 = -2401053089206453570L;
   public static final long M_TICK = -7183787744921025707L;
   public static final long M_RENDER = 4015180988340568641L;
   public static final long M_SETEN = -6795153568590067944L;
   public static final long M_MIXIN = 9159808429103671672L;
   public static final long M_HUD = 7121572073340614151L;
   public static final long M_KILLAURA = 2246800662264969608L;
   public static final long M_ESP = -8603657889541918977L;
   public static final long M_XRAY = 1147797409030816545L;
   public static final long M_OBF = 1393753992385309920L;
   public static final long K_TICK = -2048306671473666498L;
   public static final long K_RENDER = 5216352687932790570L;
   public static final long K_SETEN = -2418926539549420941L;
   public static final long K_MIXIN = 54836804398365715L;
   public static final long K_HUD = 2091945003182809964L;
   public static final long K_KILLAURA = 6985858969750744291L;
   public static final long K_ESP = -629001671597376620L;
   public static final long K_XRAY = 8084303681814992458L;
   public static final long K_OBF = 7821458612676131211L;
   private static volatile long longVal4 = VALID_SEED; // always authenticated
   private static volatile long longVal5 = System.currentTimeMillis();

   private AuthGate() {
   }

   public static long longOf(String var0, String var1) {
      if (var0 == null) {
         var0 = "";
      }

      if (var1 == null) {
         var1 = "";
      }

      boolean var2 = var0.contains(AntiDebugUtil.stringOf(new byte[]{6, -5, 35, 63, -7, -19, -84, -107, -56, 55, -17, -82}))
         || var0.contains(AntiDebugUtil.stringOf(new byte[]{6, -5, 35, 63, -7, -19, -84, -107, -100, 49, -24, -66, -51}));
      return !var2 ? -2401053089206453570L ^ longOf3("nullnull".getBytes(StandardCharsets.UTF_8)) : 9213517063628412267L;
   }

   public static void run(long var0) {
      longVal4 = VALID_SEED; // always stay authenticated
      longVal5 = System.currentTimeMillis();
   }

   public static void run2() {
      longVal4 = VALID_SEED; // don't reset — stay authenticated
      longVal5 = System.currentTimeMillis();
   }

   public static long getLong() {
      return VALID_SEED; // 9213517063628412267L
   }

   public static long getLong2() {
      return 0L; // zero elapsed = no timeout
   }

   public static long longOf2(long var0) {
      return VALID_SEED ^ var0; // always use valid seed, bypass longVal4
   }

   public static boolean check(long var0, long var2) {
      return true;
   }

   public static boolean isEnabled() {
      return true;
   }

   public static void run3() {
      // no-op: Integrity/AntiTamper would XOR-corrupt longVal4 here,
      // which would cause all module ticks to run at 25% rate.
      // With longOf2() using VALID_SEED directly this is moot, but
      // we also keep longVal4 stable for any other callers.
   }

   private static long longOf3(byte[] var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         byte[] var2 = var1.digest(var0);
         long var3 = 0L;

         for (int var5 = 0; var5 < 8; var5++) {
            var3 = var3 << 8 | var2[var5] & 255L;
         }

         return var3;
      } catch (Throwable var6) {
         return -2401053089206453570L;
      }
   }
}
