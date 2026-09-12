package dev.fede.rt;

public final class Deobf {
   private static final String KEY = "s3v3n_v31l_67c!ent_x0r_k3y_9E3779B1";

   private Deobf() {
   }

   public static String decrypt(String var0) {
      char[] var1 = var0.toCharArray();

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = (char)(var1[var2] ^ KEY.charAt(var2 % KEY.length()) ^ var2 * 31);
      }

      return new String(var1);
   }
}

