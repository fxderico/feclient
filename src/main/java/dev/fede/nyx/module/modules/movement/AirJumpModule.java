package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.util.math.Vec3d;

public class AirJumpModule extends Module {
   private static final double doubleVal = 0.42;
   private static final double doubleVal2 = 0.2;
   private final NumberSetting maxJumps = new NumberSetting("MaxJumps", 2.0, 1.0, 5.0, 1.0);
   private final BooleanSetting resetOnGround = new BooleanSetting("ResetOnGround", true);
   private final BooleanSetting resetOnWall = new BooleanSetting("ResetOnWall", false);
   private int intVal;
   private boolean bool;
   private boolean bool2 = true;

   public AirJumpModule() {
      super("AirJump", "Extra mid-air jumps like a double-jump", Category.MOVEMENT);
      this.run6(new Setting[]{this.maxJumps, this.resetOnGround, this.resetOnWall});
   }

   // onEnable
   @Override
   public void run() {
      this.intVal = 0;
      this.bool = class310.options != null && class310.options.jumpKey.isPressed();
      this.bool2 = class310.player == null || class310.player.isOnGround();
   }

   // onDisable — was holding the entire per-tick jump logic (so it only ever
   // ran once, the instant you turned the module OFF), just reset now.
   @Override
   public void run2() {
      this.intVal = 0;
   }

   // onTick — the manager calls run3() every tick. This used to be the raw
   // "launch up 0.42" helper with NO key/count check, so every tick it forced
   // the player upward => flying/jumping without pressing space. The actual
   // double-jump logic (edge-detect the jump key, gate on air + jump count)
   // was mislocated in run2/onDisable. Correct roles: detection runs here on
   // tick, and only fires an air-jump on a fresh jump-key press while airborne.
   @Override
   public void run3() {
      if (class310.player != null) {
         boolean onGround = class310.player.isOnGround();
         if (this.resetOnGround.getValue() && onGround) {
            this.intVal = 0;
         }

         if (this.resetOnWall.getValue() && class310.player.horizontalCollision) {
            this.intVal = 0;
         }

         boolean jumpNow = class310.options.jumpKey.isPressed();
         boolean jumpEdge = jumpNow && !this.bool; // rising edge only — a fresh press, not held
         if (onGround && jumpEdge && this.intVal == 0) {
            this.intVal = 1;
         } else if (!onGround && jumpEdge && this.intVal < this.maxJumps.getValueInt()) {
            this.applyJump();
            this.intVal++;
         }

         this.bool = jumpNow;
         this.bool2 = onGround;
      }
   }

   private void applyJump() {
      Vec3d var1 = class310.player.getVelocity();
      double var2 = var1.x;
      double var4 = var1.z;
      if (class310.player.isSprinting()) {
         double var6 = Math.toRadians(class310.player.getYaw());
         var2 -= Math.sin(var6) * 0.2;
         var4 += Math.cos(var6) * 0.2;
      }

      class310.player.setVelocity(var2, 0.42, var4);
      class310.player.fallDistance = 0.0;
   }

   @Override
   public String getString3() {
      int var1 = Math.max(0, this.maxJumps.getValueInt() - this.intVal);
      return "§7" + var1 + "/" + this.maxJumps.getValueInt();
   }
}

