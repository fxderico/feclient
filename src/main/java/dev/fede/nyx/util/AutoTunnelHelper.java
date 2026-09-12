package dev.fede.nyx.util;

import java.util.SplittableRandom;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;

public final class AutoTunnelHelper {
   private static final AutoTunnelHelper autoTunnelHelper = new AutoTunnelHelper();
   private volatile boolean bool = false;
   private SplittableRandom splittableRandom;
   private long longVal = 0L;
   private int intVal = 5;
   private int intVal2 = 0;
   private int intVal3 = 40;
   private int intVal4 = 0;
   private long longVal2 = 0L;
   private long longVal3 = 0L;
   private long longVal4 = 300000L;
   private long longVal5 = 0L;
   private long longVal6 = 1200000L;

   private AutoTunnelHelper() {
   }

   public static AutoTunnelHelper getk() {
      return autoTunnelHelper;
   }

   public void run5(boolean var1) {
      if (this.bool != var1) {
         this.bool = var1;
         if (var1) {
            this.run6();
            this.longVal = System.currentTimeMillis();
            this.longVal2 = this.longVal;
            this.longVal3 = this.longVal;
            this.longVal5 = this.longVal;
            this.run7();
         }
      }
   }

   public boolean isEnabled2() {
      return this.bool;
   }

   private void run6() {
      if (this.splittableRandom == null) {
         long var1;
         try {
            UUID var3 = MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getUuid() : new UUID(0L, 0L);
            var1 = var3.getMostSignificantBits() ^ var3.getLeastSignificantBits();
         } catch (Throwable var5) {
            var1 = 0L;
         }

         long var6 = System.nanoTime() ^ var1;
         this.splittableRandom = new SplittableRandom(var6);
      }
   }

   private void run7() {
      this.run6();
      this.intVal = 3 + this.splittableRandom.nextInt(5);
      this.intVal3 = 25 + this.splittableRandom.nextInt(31);
      this.longVal4 = 240000L + (long)(this.splittableRandom.nextDouble() * 300000.0);
      this.longVal6 = 900000L + (long)(this.splittableRandom.nextDouble() * 900000.0);
   }

   public void run(long var1) {
      if (this.bool) {
         if (var1 - this.longVal5 >= this.longVal6) {
            this.longVal5 = var1;
            this.run7();
         }
      }
   }

   public int getInt() {
      if (!this.bool) {
         return 0;
      } else {
         this.run6();
         double var1 = this.doubleOf(2.0, 5.0);
         return 1 + (int)Math.floor(var1 * 3.0);
      }
   }

   public int getInt3() {
      if (!this.bool) {
         return 0;
      } else {
         this.run6();
         double var1 = this.doubleOf3(140.0, 50.0);
         int var3 = (int)Math.round(var1 / 50.0);
         if (var3 < 2) {
            var3 = 2;
         }

         if (var3 > 8) {
            var3 = 8;
         }

         return var3;
      }
   }

   public int getInt2() {
      if (!this.bool) {
         return 0;
      } else {
         this.intVal2++;
         this.intVal4++;
         if (this.intVal2 >= this.intVal) {
            this.intVal2 = 0;
            this.intVal = 3 + this.splittableRandom.nextInt(5);
            return 1;
         } else {
            return 0;
         }
      }
   }

   public int getInt4() {
      if (!this.bool) {
         return 0;
      } else if (this.intVal4 < this.intVal3) {
         return 0;
      } else {
         this.intVal4 = 0;
         this.intVal3 = 25 + this.splittableRandom.nextInt(31);
         double var1 = this.getDouble();
         int var3 = 10 + this.splittableRandom.nextInt(21);
         return Math.max(10, (int)Math.round(var3 * var1));
      }
   }

   public int intOf2(long var1) {
      if (!this.bool) {
         return 0;
      } else if (var1 - this.longVal2 < 20000L) {
         return 0;
      } else {
         this.longVal2 = var1;
         return 2 + this.splittableRandom.nextInt(4);
      }
   }

   public int intOf(long var1) {
      if (!this.bool) {
         return 0;
      } else if (var1 - this.longVal3 < this.longVal4) {
         return 0;
      } else {
         this.longVal3 = var1;
         this.longVal4 = 240000L + (long)(this.splittableRandom.nextDouble() * 300000.0);
         double var3 = this.getDouble();
         int var5 = 20 + this.splittableRandom.nextInt(41);
         return Math.max(20, (int)Math.round(var5 * var3));
      }
   }

   public double getDouble() {
      if (!this.bool) {
         return 1.0;
      } else {
         double var1 = Math.max(0.0, (System.currentTimeMillis() - this.longVal) / 60000.0);
         double var3 = 1.0 + 0.02 * var1;
         return Math.min(1.6, var3);
      }
   }

   private double doubleOf(double var1, double var3) {
      double var5 = this.doubleOf2(var1);
      double var7 = this.doubleOf2(var3);
      double var9 = var5 + var7;
      return var9 <= 0.0 ? 0.5 : var5 / var9;
   }

   private double doubleOf2(double var1) {
      if (var1 < 1.0) {
         double var13 = this.splittableRandom.nextDouble();
         return this.doubleOf2(var1 + 1.0) * Math.pow(var13, 1.0 / var1);
      } else {
         double var3 = var1 - 0.3333333333333333;
         double var5 = 1.0 / Math.sqrt(9.0 * var3);

         while (true) {
            double var7 = this.getDouble2();
            double var9 = 1.0 + var5 * var7;
            if (!(var9 <= 0.0)) {
               var9 = var9 * var9 * var9;
               double var11 = this.splittableRandom.nextDouble();
               if (var11 < 1.0 - 0.0331 * (var7 * var7) * (var7 * var7)) {
                  return var3 * var9;
               }

               if (Math.log(var11) < 0.5 * var7 * var7 + var3 * (1.0 - var9 + Math.log(var9))) {
                  return var3 * var9;
               }
            }
         }
      }
   }

   private double getDouble2() {
      double var1 = Math.max(1.0E-12, this.splittableRandom.nextDouble());
      double var3 = this.splittableRandom.nextDouble();
      return Math.sqrt(-2.0 * Math.log(var1)) * Math.cos((Math.PI * 2) * var3);
   }

   private double doubleOf3(double var1, double var3) {
      double var5 = Math.log(Math.max(1.0, var1));
      double var7 = var3 / Math.max(1.0, var1);
      return Math.exp(var5 + var7 * this.getDouble2());
   }

   public int intOf3(int var1, int var2) {
      if (this.bool && var2 > var1) {
         this.run6();
         return var1 + this.splittableRandom.nextInt(var2 - var1 + 1);
      } else {
         return var1;
      }
   }

   public double doubleOf4(double var1, double var3) {
      if (this.bool && !(var3 <= var1)) {
         this.run6();
         return var1 + this.splittableRandom.nextDouble() * (var3 - var1);
      } else {
         return var1;
      }
   }

   public boolean check(double var1) {
      if (!this.bool) {
         return false;
      } else {
         this.run6();
         return this.splittableRandom.nextDouble() < var1;
      }
   }
}

