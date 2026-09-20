package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import net.minecraft.util.math.Vec3d;

public class SpeedModule extends Module {
   private static final long longVal = 350L;
   private static final double doubleVal = 0.2;
   private static final double doubleVal2 = 0.42;
   // default was BunnyHop, which forces a real 0.42 vertical jump velocity every
   // step (see run4 below — var3=true → var18=0.42 while on ground). Strafe runs
   // the exact same horizontal boost through run4 but with var3=false, so the Y
   // velocity is left untouched — flat ground speed, no forced hop, jumping only
   // happens if you actually press space like normal.
   private final ModeSetting mode = new ModeSetting("Mode", "Strafe", "Vanilla", "Strafe", "LowHop", "BunnyHop");
   // range extended 1.0 -> 5.0 and default bumped to match — 0.1-1.0 was the
   // original cap (already 3-4x vanilla sprint at 1.0); on request.
   private final NumberSetting speed = new NumberSetting("Speed", 5.0, 0.1, 5.0, 0.05);
   private final NumberSetting boost = (NumberSetting)new NumberSetting("Boost", 1.5, 1.0, 5.0, 0.1).visibleWhen(this::getBoolean);
   private final BooleanSetting onlyGround = new BooleanSetting("OnlyGround", false);
   // same idea as Fly's Humanize: a perfectly flat velocity value every
   // tick is a dead giveaway pattern. eases toward the target speed instead
   // of snapping, plus small randomized noise. generic "less robotic"
   // smoothing, not built against any specific anticheat.
   private final BooleanSetting humanize = new BooleanSetting("Humanize", false);
   private final NumberSetting jitter = new NumberSetting("Jitter", 0.02, 0.0, 0.1, 0.005);
   private double currentSpeedFactor;
   private final java.util.concurrent.ThreadLocalRandom rng = java.util.concurrent.ThreadLocalRandom.current();
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private static volatile boolean bool;
   private static volatile float floatVal = 1.0F;

   public SpeedModule() {
      super("Speed", "Client-side speed enhancement", Category.MOVEMENT);
      this.run6(new Setting[]{this.mode, this.speed, this.boost, this.onlyGround, this.humanize, this.jitter});
   }

   public static boolean isEnabled_s() {
      return bool;
   }

   public static float getFloat() {
      return floatVal;
   }

   @Override
   public void run() {
      this.antiVoidModuleHelper.run();
      this.run5();
   }

   @Override
   public void run2() {
      bool = false;
      floatVal = 1.0F;
   }

   @Override
   public void run3() {
      if (class310.player != null) {
         this.run5();
         if (!this.mode.check("Vanilla")) {
            boolean var1 = class310.player.isOnGround();
            if (!this.onlyGround.getValue() || var1) {
               boolean var2 = class310.player.forwardSpeed != 0.0F || class310.player.sidewaysSpeed != 0.0F;
               if (var2) {
                  Vec3d var3 = class310.player.getVelocity();
                  if (this.mode.check("Strafe")) {
                     this.run4(var1, var3, false);
                  } else if (this.mode.check("LowHop")) {
                     if (var1 && this.antiVoidModuleHelper.check(350L)) {
                        this.antiVoidModuleHelper.run();
                        class310.player.setVelocity(var3.x, 0.2, var3.z);
                     }
                  } else if (this.mode.check("BunnyHop")) {
                     this.run4(var1, var3, true);
                  }
               }
            }
         }
      }
   }

   private void run4(boolean var1, Vec3d var2, boolean var3) {
      double var4 = Math.toRadians(class310.player.getYaw());
      double var6 = -Math.sin(var4) * class310.player.forwardSpeed + Math.cos(var4) * class310.player.sidewaysSpeed;
      double var8 = Math.cos(var4) * class310.player.forwardSpeed + Math.sin(var4) * class310.player.sidewaysSpeed;
      double var10 = Math.sqrt(var6 * var6 + var8 * var8);
      if (!(var10 < 1.0E-6)) {
         double var12 = this.speed.getValue();
         if (this.humanize.getValue()) {
            this.currentSpeedFactor += (var12 - this.currentSpeedFactor) * 0.35;
            var12 = this.currentSpeedFactor;
         } else {
            this.currentSpeedFactor = var12;
         }
         double var14 = var6 / var10 * var12;
         double var16 = var8 / var10 * var12;
         double var18 = var3 && var1 ? 0.42 : var2.y;
         if (this.humanize.getValue()) {
            double j = this.jitter.getValue();
            if (j > 0.0) {
               var14 += (this.rng.nextDouble() - 0.5) * j;
               var16 += (this.rng.nextDouble() - 0.5) * j;
            }
         }
         class310.player.setVelocity(var14, var18, var16);
      }
   }

   private void run5() {
      boolean var1 = this.mode.check("Vanilla");
      boolean var2 = !this.onlyGround.getValue() || class310.player != null && class310.player.isOnGround();
      bool = var1 && var2;
      floatVal = this.boost.getValueFloat();
   }

   @Override
   public String getString3() {
      return "§7" + this.mode.getMode();
   }

   private Boolean getBoolean() {
      return this.mode.check("Vanilla");
   }
}

