package dev.fede.render.anim;

public enum Easing {
   LINEAR {
      @Override
      public float apply(float t) {
         return t;
      }
   },
   EASE_OUT_CUBIC {
      @Override
      public float apply(float t) {
         float inv = 1.0F - t;
         return 1.0F - inv * inv * inv;
      }
   },
   EASE_IN_OUT_QUAD {
      @Override
      public float apply(float t) {
         return t < 0.5F ? 2.0F * t * t : 1.0F - (float)Math.pow(-2.0F * t + 2.0F, 2.0) / 2.0F;
      }
   };

   public abstract float apply(float var1);

   private static Easing[] $values() {
      return new Easing[]{LINEAR, EASE_OUT_CUBIC, EASE_IN_OUT_QUAD};
   }
}

