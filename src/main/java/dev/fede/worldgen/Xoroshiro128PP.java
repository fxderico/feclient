package dev.fede.worldgen;

/**
 * Minecraft 1.18+ Xoroshiro128++ RNG, reimplemented standalone so we can run
 * the world generator's math client-side for seed-based ore prediction.
 * Ported verbatim from larp-v7's worldgen package (pure math, no MC deps).
 */
public final class Xoroshiro128PP {
   private static final long SILVER_RATIO_64 = 7640891576956012809L;
   private static final long GOLDEN_RATIO_64 = -7046029254386353131L;
   private long lo;
   private long hi;

   public Xoroshiro128PP(long lo, long hi) {
      this.lo = lo;
      this.hi = hi;
      if ((lo | hi) == 0L) {
         this.lo = GOLDEN_RATIO_64;
         this.hi = SILVER_RATIO_64;
      }
   }

   public static Xoroshiro128PP fromSeed(long seed) {
      long l = seed ^ 0x6A09E667F3BCC909L;
      long h = l + GOLDEN_RATIO_64;
      return new Xoroshiro128PP(mixStafford13(l), mixStafford13(h));
   }

   public static long mixStafford13(long z) {
      z = (z ^ z >>> 30) * -4658895280553007687L;
      z = (z ^ z >>> 27) * -7723592293110705685L;
      return z ^ z >>> 31;
   }

   public long nextLong() {
      long s0 = this.lo;
      long s1 = this.hi;
      long result = Long.rotateLeft(s0 + s1, 17) + s0;
      this.lo = Long.rotateLeft(s0, 49) ^ (s1 ^= s0) ^ s1 << 21;
      this.hi = Long.rotateLeft(s1, 28);
      return result;
   }

   public int nextInt(int bound) {
      if (bound <= 0) {
         throw new IllegalArgumentException("bound must be positive");
      }
      long l = Integer.toUnsignedLong((int)this.nextLong());
      long m = l * (long)bound;
      long n = m & 0xFFFFFFFFL;
      if (n < (long)bound) {
         int threshold = Integer.remainderUnsigned(-bound, bound);
         while (n < (long)threshold) {
            l = Integer.toUnsignedLong((int)this.nextLong());
            m = l * (long)bound;
            n = m & 0xFFFFFFFFL;
         }
      }
      return (int)(m >> 32);
   }

   public float nextFloat() {
      return (float)(this.nextLong() >>> 40) * 5.9604645E-8F;
   }

   public double nextDouble() {
      return (double)(this.nextLong() >>> 11) * 1.110223E-16;
   }

   public long getLo() {
      return this.lo;
   }

   public long getHi() {
      return this.hi;
   }
}
