package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.Colors;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class HitParticlesModule extends Module {
   public static final int STYLE_SPARKS = 0;
   public static final int STYLE_HEARTS = 1;
   public static final int STYLE_LIGHTNING = 2;
   public static final int STYLE_67 = 3;
   public final ModeSetting style = this.addSetting(new ModeSetting("Style", "Burst style", "Sparks", "Sparks", "Hearts", "Lightning", "FE Logo"));
   public final SliderSetting amount = this.addSetting(new SliderSetting("Amount", "Particles spawned per hit", 14.0, 4.0, 40.0, 1.0));
   public final SliderSetting size = this.addSetting(new SliderSetting("Size", "Particle scale", 1.0, 0.3, 3.0, 0.1, "x"));
   public final SliderSetting lifetime = this.addSetting(new SliderSetting("Lifetime", "How long the burst lingers", 0.7, 0.3, 2.0, 0.1, "s"));
   public final SliderSetting spread = this.addSetting(new SliderSetting("Spread", "How far particles fly out", 1.0, 0.3, 2.5, 0.1, "x"));
   public final ColorSetting color = this.addSetting(new ColorSetting("Color", "Particle tint", -49508));
   public final BooleanSetting rainbow = this.addSetting(new BooleanSetting("Rainbow", "Cycle each burst through the rainbow", false));
   public final SliderSetting glow = this.addSetting(new SliderSetting("Glow", "Soft outer halo intensity", 65.0, 0.0, 100.0, 5.0, "%"));
   public final BooleanSetting shockwave = this.addSetting(new BooleanSetting("Shockwave", "Expanding ring on each hit", true));
   private final Deque<HitParticlesModule.HitParticle> particles = new ArrayDeque<>();
   private final Deque<HitParticlesModule.Shock> shocks = new ArrayDeque<>();
   private static final int MAX_PARTICLES = 600;
   private static final int MAX_SHOCKS = 24;

   public HitParticlesModule() {
      super("HitParticles", "Themed particle bursts when you hit an entity", Category.ADDONS);
   }

   public Deque<HitParticlesModule.HitParticle> particles() {
      return this.particles;
   }

   public Deque<HitParticlesModule.Shock> shocks() {
      return this.shocks;
   }

   @Override
   protected void onDisable() {
      this.clear();
   }

   public void clear() {
      this.particles.clear();
      this.shocks.clear();
   }

   private int currentRgb() {
      if (this.rainbow.get()) {
         float hue = (float)(System.currentTimeMillis() % 3500L) / 3500.0F * 360.0F;
         return Colors.hsvToRgb(hue, 0.85F, 1.0F) & 16777215;
      } else {
         return this.color.get() & 16777215;
      }
   }

   public void onHit(Entity target) {
      if (this.isEnabled() && target != null) {
         double cx = target.getX();
         double cy = target.getY() + target.getHeight() * 0.6;
         double cz = target.getZ();
         this.spawnAt(cx, cy, cz);
      }
   }

   public void spawnAt(double cx, double cy, double cz) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.world != null) {
         Random random = mc.world.random;
         int styleId = this.styleId();
         int rgb = this.currentRgb();
         int count = this.amount.getInt();
         float life = this.lifetime.getFloat();
         float spreadScale = this.spread.getFloat();
         float sizeScale = this.size.getFloat();
         long now = System.nanoTime();

         for (int i = 0; i < count; i++) {
            this.spawnOne(random, styleId, rgb, cx, cy, cz, life, spreadScale, sizeScale, now);
         }

         if (this.shockwave.get()) {
            this.shocks.addLast(new Shock(cx, cy, cz, rgb, styleId, now));

            while (this.shocks.size() > 24) {
               this.shocks.removeFirst();
            }
         }

         while (this.particles.size() > 600) {
            this.particles.removeFirst();
         }
      }
   }

   private void spawnOne(Random random, int styleId, int rgb, double cx, double cy, double cz, float life, float spreadScale, float sizeScale, long now) {
      double theta = random.nextDouble() * Math.PI * 2.0;
      double cosPhi = 2.0 * random.nextDouble() - 1.0;
      double sinPhi = Math.sqrt(Math.max(0.0, 1.0 - cosPhi * cosPhi));
      float dx = (float)(sinPhi * Math.cos(theta));
      float dy = (float)cosPhi;
      float dz = (float)(sinPhi * Math.sin(theta));
      float particleLife = life * (0.75F + random.nextFloat() * 0.25F);
      float pSize = sizeScale * (0.7F + random.nextFloat() * 0.6F);
      float speed;
      float gravity;
      switch (styleId) {
         case 1:
            dx *= 0.5F;
            dz *= 0.5F;
            dy = 0.4F + Math.abs(dy) * 0.5F;
            speed = (1.6F + random.nextFloat() * 1.0F) * spreadScale;
            gravity = 1.2F;
            particleLife = life * (1.0F + random.nextFloat() * 0.3F);
            break;
         case 2:
            dy *= 0.25F;
            float horiz = MathHelper.sqrt(Math.max(1.0E-4F, dx * dx + dz * dz));
            dx /= horiz;
            dz /= horiz;
            speed = (5.0F + random.nextFloat() * 3.0F) * spreadScale;
            gravity = 0.6F;
            particleLife = life * (0.45F + random.nextFloat() * 0.25F);
            break;
         case 3:
            dy = 0.12F + dy * 0.35F;
            speed = (1.9F + random.nextFloat() * 1.4F) * spreadScale;
            gravity = 1.3F;
            particleLife = life * (1.0F + random.nextFloat() * 0.25F);
            break;
         default:
            dy = dy * 0.7F + 0.3F;
            speed = (4.0F + random.nextFloat() * 3.5F) * spreadScale;
            gravity = 6.5F;
      }

      float vx = dx * speed;
      float vy = dy * speed;
      float vz = dz * speed;
      float rot = random.nextFloat() * (float) (Math.PI * 2);
      float rotSpeed = (random.nextFloat() - 0.5F) * 8.0F;
      this.particles.addLast(new HitParticle(cx, cy, cz, vx, vy, vz, rgb, styleId, pSize, rot, rotSpeed, gravity, particleLife, now));
   }

   private int styleId() {
      if (this.style.check("Hearts")) {
         return 1;
      } else if (this.style.check("Lightning")) {
         return 2;
      } else {
         return this.style.check("FE Logo") ? 3 : 0;
      }
   }

   public float glowStrength() {
      return this.glow.getFloat() / 100.0F;
   }

   public final class HitParticle {
      public double doubleVal;
      public double doubleVal2;
      public double doubleVal3;
      public float floatVal;
      public float floatVal2;
      public float floatVal3;
      public int rgb;
      public int styleId;
      public float size;
      public float rot;
      public float rotSpeed;
      public float gravity;
      public float lifetime;
      public long spawnNanos;

      HitParticle(
         double ox,
         double oy,
         double oz,
         float vx,
         float vy,
         float vz,
         int rgb,
         int styleId,
         float size,
         float rot,
         float rotSpeed,
         float gravity,
         float lifetime,
         long spawnNanos
      ) {
         this.doubleVal = ox;
         this.doubleVal2 = oy;
         this.doubleVal3 = oz;
         this.floatVal = vx;
         this.floatVal2 = vy;
         this.floatVal3 = vz;
         this.rgb = rgb;
         this.styleId = styleId;
         this.size = size;
         this.rot = rot;
         this.rotSpeed = rotSpeed;
         this.gravity = gravity;
         this.lifetime = lifetime;
         this.spawnNanos = spawnNanos;
      }

      public float ageSeconds(long nowNanos) {
         return (float)(nowNanos - this.spawnNanos) / 1.E9F;
      }

      public double doubleOf(float age) {
         return this.doubleVal + this.floatVal * age;
      }

      public double doubleOf2(float age) {
         return this.doubleVal2 + this.floatVal2 * age - 0.5 * this.gravity * age * age;
      }

      public double doubleOf3(float age) {
         return this.doubleVal3 + this.floatVal3 * age;
      }
   }

   public final class Shock {
      public double doubleVal;
      public double doubleVal2;
      public double doubleVal3;
      public int rgb;
      public int styleId;
      public long spawnNanos;

      Shock(double x, double y, double z, int rgb, int styleId, long spawnNanos) {
         this.doubleVal = x;
         this.doubleVal2 = y;
         this.doubleVal3 = z;
         this.rgb = rgb;
         this.styleId = styleId;
         this.spawnNanos = spawnNanos;
      }

      public float ageSeconds(long nowNanos) {
         return (float)(nowNanos - this.spawnNanos) / 1.E9F;
      }
   }
}


