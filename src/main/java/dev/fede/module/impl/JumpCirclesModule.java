package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.SliderSetting;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.random.Random;

public class JumpCirclesModule extends Module {
   public final SliderSetting size = this.addSetting(new SliderSetting("Size", "Decal scale (1.0 = one block wide)", 1.0, 0.5, 3.0, 0.1, "x"));
   public final ColorSetting color = this.addSetting(new ColorSetting("Color", "Decal color (alpha is animated)", -38476));
   public final SliderSetting lifetime = this.addSetting(new SliderSetting("Lifetime", "How long each decal stays visible", 1.5, 0.5, 4.0, 0.1, "s"));
   public final BooleanSetting rainbow = this.addSetting(new BooleanSetting("Rainbow", "Cycle the color through the rainbow", false));
   public final BooleanSetting shockwave = this.addSetting(new BooleanSetting("Shockwave", "Expanding ring on spawn", true));
   public final BooleanSetting particles = this.addSetting(new BooleanSetting("Particles", "Drifting dust motes on jump", true));
   public final SliderSetting maxCircles = this.addSetting(new SliderSetting("Max", "Max simultaneous decals (oldest drops first)", 10.0, 1.0, 30.0, 1.0));
   private final Deque<JumpCirclesModule.JumpCircle> circles = new ArrayDeque<>();
   private int spawnCounter;

   public JumpCirclesModule() {
      super("JumpCircles", "Stamps a glowing decal on the ground when you jump", Category.CLIENT);
   }

   public Deque<JumpCirclesModule.JumpCircle> circles() {
      return this.circles;
   }

   @Override
   protected void onDisable() {
      this.clear();
   }

   public void clear() {
      this.circles.clear();
   }

   public int baseRgb() {
      return this.color.get() & 16777215;
   }

   public void onPlayerJump(ClientPlayerEntity player) {
      if (this.isEnabled()) {
         double x = player.getX();
         double y = player.getY();
         double z = player.getZ();
         float yLift = 0.01F + this.spawnCounter % 8 * 0.001F;
         this.spawnCounter++;
         this.circles.addLast(new JumpCircle(x, y, z, player.getYaw(), yLift, System.nanoTime()));
         int max = Math.max(1, this.maxCircles.getInt());

         while (this.circles.size() > max) {
            this.circles.removeFirst();
         }

         if (this.particles.get()) {
            this.spawnParticles(player);
         }
      }
   }

   private void spawnParticles(ClientPlayerEntity player) {
      Random random = player.getRandom();
      int rgb = this.baseRgb();

      for (int i = 0; i < 10; i++) {
         double angle = random.nextDouble() * Math.PI * 2.0;
         double dist = 0.15 + random.nextDouble() * 0.45;
         player.getEntityWorld()
            .addParticleClient(
               new DustParticleEffect(rgb, 0.9F),
               player.getX() + Math.cos(angle) * dist,
               player.getY() + 0.05,
               player.getZ() + Math.sin(angle) * dist,
               0.0,
               0.6 + random.nextDouble() * 0.4,
               0.0
            );
      }
   }

   public final class JumpCircle {
      public double doubleVal;
      public double doubleVal2;
      public double doubleVal3;
      public float yawDegrees;
      public float yLift;
      public long spawnNanos;

      JumpCircle(double x, double y, double z, float yawDegrees, float yLift, long spawnNanos) {
         this.doubleVal = x;
         this.doubleVal2 = y;
         this.doubleVal3 = z;
         this.yawDegrees = yawDegrees;
         this.yLift = yLift;
         this.spawnNanos = spawnNanos;
      }

      public float ageSeconds(long nowNanos) {
         return (float)(nowNanos - this.spawnNanos) / 1.E9F;
      }
   }
}

