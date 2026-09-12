package dev.fede.nyx.ui;

public class Notification {
   static final long longVal = 300L;
   static final long longVal2 = 250L;
   static final float floatVal = 90.0F;
   private final String string;
   private final String string2;
   private final INFO notificationType;
   private final long longVal3;
   private long longVal4 = 0L;
   private long longVal5 = -1L;
   private long longVal6;
   private long longVal7 = -1L;
   private float floatVal2 = Float.NaN;

   public Notification(String var1, String var2, INFO var3, long var4) {
      this.string = var1 == null ? "" : var1;
      this.string2 = var2 == null ? "" : var2;
      this.notificationType = var3 == null ? INFO.UNKNOWN : var3;
      this.longVal3 = Math.max(1L, var4);
      this.longVal6 = this.longVal3;
   }

   public String getString() {
      return this.string;
   }

   public String getString2() {
      return this.string2;
   }

   public INFO getNotificationType() {
      return this.notificationType;
   }

   public long getLong() {
      return this.longVal3;
   }

   public long getLong2() {
      return this.longVal6;
   }

   void run5(boolean var1) {
      long var2 = getLong3();
      if (this.longVal7 < 0L) {
         this.longVal7 = var2;
      } else {
         long var4 = var2 - this.longVal7;
         this.longVal7 = var2;
         if (var4 > 0L) {
            if (this.longVal4 < 300L) {
               this.longVal4 = Math.min(300L, this.longVal4 + var4);
            }

            if (this.longVal5 >= 0L) {
               this.longVal5 += var4;
            } else {
               if (!var1) {
                  this.longVal6 -= var4;
                  if (this.longVal6 <= 0L) {
                     this.run4();
                  }
               }
            }
         }
      }
   }

   void run4() {
      if (this.longVal5 < 0L) {
         this.longVal5 = 0L;
      }
   }

   boolean isEnabled4() {
      return this.longVal5 >= 250L;
   }

   float getFloat() {
      if (this.longVal5 >= 0L) {
         float var3 = Math.min(1.0F, (float)this.longVal5 / 250.0F);
         return 1.0F - var3 * var3 * var3;
      } else {
         float var1 = Math.min(1.0F, (float)this.longVal4 / 300.0F);
         float var2 = 1.0F - var1;
         return 1.0F - var2 * var2 * var2;
      }
   }

   float getFloat2() {
      return this.getFloat();
   }

   float getFloat3() {
      if (this.longVal5 >= 0L) {
         return 0.0F;
      } else {
         float var1 = (float)this.longVal6 / (float)this.longVal3;
         if (var1 < 0.0F) {
            return 0.0F;
         } else {
            return var1 > 1.0F ? 1.0F : var1;
         }
      }
   }

   float floatOf(float var1, long var2) {
      if (Float.isNaN(this.floatVal2)) {
         this.floatVal2 = var1;
         return this.floatVal2;
      } else if (var2 <= 0L) {
         return this.floatVal2;
      } else {
         double var4 = 1.0 - Math.exp(-var2 / 90.0);
         this.floatVal2 = this.floatVal2 + (float)((var1 - this.floatVal2) * var4);
         if (Math.abs(var1 - this.floatVal2) < 0.25F) {
            this.floatVal2 = var1;
         }

         return this.floatVal2;
      }
   }

   float getFloat4() {
      return this.floatVal2;
   }

   static long getLong3() {
      return System.nanoTime() / 1000000L;
   }
}

