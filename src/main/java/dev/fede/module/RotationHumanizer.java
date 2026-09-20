package dev.fede.module;

import java.util.Random;

/**
 * Post-processes raw yaw/pitch deltas so they stop looking like a
 * mathematically perfect function.
 *
 * What rotation checks (Astro's rotation/GCD module, Intave's movement +
 * heuristics layer) actually key on:
 *   - GCD of consecutive yaw/pitch deltas snapping to a suspiciously clean
 *     divisor (a raw lerp toward a target produces exactly that).
 *   - Delta-of-delta (acceleration) being a smooth monotonic curve with no
 *     noise floor -- real mouse input has micro-tremor even mid-flick.
 *   - Deltas landing exactly on float precision boundaries every tick
 *     (sensitivity-scaled input never does that).
 *   - Zero entropy in the low-order bits of successive angles.
 *
 * This class breaks all four without changing the *target* the caller
 * converges on -- it just perturbs the path getting there, the same way
 * human hand tremor does. Feed it the delta AimAssistLogic already computed;
 * it hands back something that survives GCD/entropy analysis instead of a
 * raw lerp step.
 */
public final class RotationHumanizer {
   private final Random random = new Random();
   private double residualYaw;
   private double residualPitch;
   private double lastJitterYaw;
   private double lastJitterPitch;

   /**
    * @param dYaw     raw yaw delta for this tick (pixels/degrees, whatever unit the caller uses)
    * @param dPitch   raw pitch delta for this tick
    * @param strength 0..1, how much noise to inject relative to the delta magnitude
    * @return humanized [dYaw, dPitch]
    */
   public double[] apply(double dYaw, double dPitch, double strength) {
      if (strength <= 0.0) {
         return new double[]{dYaw, dPitch};
      }

      // Micro-tremor: correlated with the previous tick's jitter (real hands don't
      // reset noise every 50ms, it drifts) but bounded so it never overtakes the
      // actual correction.
      double tremorYaw = this.tremor(dYaw, strength, this.lastJitterYaw);
      double tremorPitch = this.tremor(dPitch, strength, this.lastJitterPitch);
      this.lastJitterYaw = tremorYaw;
      this.lastJitterPitch = tremorPitch;

      double outYaw = dYaw + tremorYaw;
      double outPitch = dPitch + tremorPitch;

      // Break float-boundary snapping / GCD alignment by carrying a fractional
      // residual across ticks instead of truncating cleanly each time.
      outYaw += this.residualYaw;
      outPitch += this.residualPitch;
      double roundedYaw = this.irregularRound(outYaw);
      double roundedPitch = this.irregularRound(outPitch);
      this.residualYaw = outYaw - roundedYaw;
      this.residualPitch = outPitch - roundedPitch;

      return new double[]{roundedYaw, roundedPitch};
   }

   private double tremor(double delta, double strength, double previous) {
      double magnitude = Math.max(Math.abs(delta) * 0.06, 0.015) * strength;
      double sample = (this.random.nextDouble() * 2.0 - 1.0) * magnitude;
      // 65% carry-over from last tick's noise so the noise floor itself has
      // continuity instead of looking like independent RNG per tick.
      return previous * 0.35 + sample * 0.65;
   }

   private double irregularRound(double value) {
      // Instead of a plain floor/round (which is itself a clean function of
      // the input), bias the rounding boundary by a small random offset each
      // call so the fractional cutoff isn't fixed at .5 every time.
      double bias = (this.random.nextDouble() - 0.5) * 0.2;
      return Math.floor(value + 0.5 + bias);
   }

   public void reset() {
      this.residualYaw = 0.0;
      this.residualPitch = 0.0;
      this.lastJitterYaw = 0.0;
      this.lastJitterPitch = 0.0;
   }
}
