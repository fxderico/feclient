package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class CustomFovModule extends Module {
   private static final double EASE_SPEED = 12.0;
   private static final double SPRINT_SPEED = 0.2825;
   public final SliderSetting fov = this.addSetting(
      new SliderSetting("FOV", "Target field of view", 95.0, 30.0, 140.0, 1.0, "°").withLabel(v -> (int)v + "° · " + tag((int)v))
   );
   public final BooleanSetting smooth = this.addSetting(new BooleanSetting("Smooth", "Ease FOV changes in and out", true));
   public final BooleanSetting noSprintZoom = this.addSetting(new BooleanSetting("No Sprint Zoom", "Cancel the vanilla sprint / speed FOV punch", true));
   public final BooleanSetting speedFov = this.addSetting(new BooleanSetting("Speed FOV", "Widen the view with your movement speed", false));
   public final SliderSetting speedStrength = this.addSetting(new SliderSetting("Speed Strength", "Extra degrees at full sprint", 12.0, 0.0, 30.0, 1.0, "°"));
   private double current = -1.0;
   private long lastNanos = 0L;

   public CustomFovModule() {
      super("CustomFOV", "Overrides the field of view", Category.MISC);
      this.speedStrength.visibleWhen(this.speedFov::get);
   }

   public float fovMultiplier(float vanillaSprintMultiplier) {
      MinecraftClient mc = MinecraftClient.getInstance();
      int optionsFov = (Integer)mc.options.getFov().getValue();
      if (optionsFov <= 0) {
         return vanillaSprintMultiplier;
      } else {
         double target = this.isEnabled() ? this.fov.get() + this.speedBonus(mc) : optionsFov;
         long now = System.nanoTime();
         double dt = this.lastNanos == 0L ? 0.0 : (now - this.lastNanos) / 1.E9;
         this.lastNanos = now;
         if (this.current < 0.0) {
            this.current = optionsFov;
         }

         if (!this.smooth.get()) {
            this.current = target;
         } else {
            double t = 1.0 - Math.exp(-12.0 * Math.max(0.0, dt));
            this.current = this.current + (target - this.current) * t;
            if (Math.abs(this.current - target) < 0.05) {
               this.current = target;
            }
         }

         if (!this.isEnabled() && this.current == optionsFov) {
            return vanillaSprintMultiplier;
         } else {
            float sprint = this.isEnabled() && this.noSprintZoom.get() ? 1.0F : vanillaSprintMultiplier;
            return (float)(this.current / optionsFov) * sprint;
         }
      }
   }

   public double currentFov() {
      return this.current;
   }

   private double speedBonus(MinecraftClient mc) {
      if (!this.speedFov.get()) {
         return 0.0;
      } else {
         PlayerEntity p = mc.player;
         if (p == null) {
            return 0.0;
         } else {
            Vec3d v = p.getVelocity();
            double horizontal = Math.sqrt(v.x * v.x + v.z * v.z);
            double t = Math.min(1.0, horizontal / 0.2825);
            return this.speedStrength.getFloat() * t;
         }
      }
   }

   private static String tag(int v) {
      if (v <= 45) {
         return "Tunnel";
      } else if (v <= 65) {
         return "Focused";
      } else if (v <= 80) {
         return "Normal";
      } else if (v <= 100) {
         return "Wide";
      } else {
         return v <= 118 ? "Ultra" : "Fisheye";
      }
   }
}

