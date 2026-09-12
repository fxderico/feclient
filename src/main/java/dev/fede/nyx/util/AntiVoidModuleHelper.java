package dev.fede.nyx.util;

public final class AntiVoidModuleHelper {
   private long longVal = System.currentTimeMillis();

   public boolean check(long var1) {
      return System.currentTimeMillis() - this.longVal >= var1;
   }

   public boolean check2(double var1) {
      return System.currentTimeMillis() - this.longVal >= var1;
   }

   public void run() {
      this.longVal = System.currentTimeMillis();
   }

   public long getLong() {
      return System.currentTimeMillis() - this.longVal;
   }
}

