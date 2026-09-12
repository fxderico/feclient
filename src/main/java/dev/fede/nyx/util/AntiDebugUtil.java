package dev.fede.nyx.util;

import dev.fede.nyx.auth.AuthGate;
import java.nio.charset.StandardCharsets;

public final class AntiDebugUtil {
   public static final int KEY = 1009581092;

   private AntiDebugUtil() {
   }

   public static String stringOf(byte[] a) {
      if (a == null) {
         return "";
      } else {
         int var1 = 1009581092;
         byte[] var2 = new byte[a.length];

         for (int var3 = 0; var3 < a.length; var3++) {
            var2[var3] = (byte)(a[var3] ^ (byte)var1);
            var1 = var1 * 1103515245 + 12345;
         }

         return new String(var2, StandardCharsets.UTF_8);
      }
   }

   private static long getLong() {
      return AuthGate.getLong()
         ^ 6002694041657218521L
            + (4407803662104726136L | 5987514815891211763L)
            + (
               4407803662104726136L
                  & (-756936004270192552L + 8550610566417678681L - ((-756936004270192552L & 8550610566417678681L) << 1) ^ -3404474240578679566L)
            )
            + -2832171799190988955L
            + (
               (-5458714380917185936L ^ -6616791811759304202L) + ((-5458714380917185936L & -6616791811759304202L) << 1) + -6413107013685937166L
                  ^ -1843136131638306280L
            )
            + (
               (
                     (-2649516339084713022L ^ 3929031974380863120L ^ 1357552782075735306L)
                        & (79251741425914343L ^ -6195509221233418452L) + ((79251741425914343L & -6195509221233418452L) << 1) + 4273121348169197829L
                  )
                  << 1
            )
            + 8159662609923876039L;
   }

   public static String stringOf2(byte[] var0) {
      if (var0 == null) {
         return "";
      } else {
         long var1 = getLong();
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            int var5 = (int)(var1 >>> (var4 & 7) * 8 & 255L);
            var3[var4] = (byte)(var0[var4] ^ var5);
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }

   public static int intOf(int var0, long var1) {
      long var3 = AuthGate.getLong() ^ var1;
      return var0 ^ (int)var3;
   }

   public static long longOf(long var0, long var2) {
      long var4 = AuthGate.getLong() ^ var2;
      return var0 ^ var4;
   }

   public static byte[] byteArrayOf2(String var0) {
      if (var0 == null) {
         return new byte[0];
      } else {
         byte[] var1 = var0.getBytes(StandardCharsets.UTF_8);
         long var2 = -4628236564429099551L + -8059715532430215045L + 2062666635825894191L;
         byte[] var4 = new byte[var1.length];

         for (int var5 = 0; var5 < var1.length; var5++) {
            int var6 = (int)(var2 >>> (var5 & 7) * 8 & 255L);
            var4[var5] = (byte)(var1[var5] ^ var6);
         }

         return var4;
      }
   }

   public static int intOf2(int var0, long var1) {
      long var3 = 282985574613362220L
            + 839332233744345097L
            + (
               6944493378032935822L
                     + (-1916582236555810495L ^ 6836141876070166794L ^ -1411693000838092668L)
                     - ((-7314530021657930065L + -6192009303113323552L + 2004288629094637823L & 6327730086278738127L) << 1)
                  ^ (5172069471191302144L | 5852517495L)
            )
         ^ var1;
      return var0 ^ (int)var3;
   }

   public static long longOf2(long var0, long var2) {
      long var4 = 1207906598780021692L + -7276357251992622240L + -3164776356868538801L ^ var2;
      return var0 ^ var4;
   }
}

