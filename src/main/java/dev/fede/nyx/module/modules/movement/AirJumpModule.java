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

   @Override
   public void run() {
      this.intVal = 0;
      this.bool = class310.options != null && class310.options.jumpKey.isPressed();
      this.bool2 = class310.player == null || class310.player.isOnGround();
   }

   @Override
   public void run2() {
      if (class310.player != null) {
         boolean var1 = class310.player.isOnGround();
         if (this.resetOnGround.getValue() && var1) {
            this.intVal = 0;
         }

         if (this.resetOnWall.getValue() && class310.player.horizontalCollision) {
            this.intVal = 0;
         }

         boolean var2 = class310.options.jumpKey.isPressed();
         boolean var3 = var2 && !this.bool;
         if (var1 && var3 && this.intVal == 0) {
            this.intVal = 1;
         } else if (!var1 && var3 && this.intVal < this.maxJumps.getValueInt()) {
            this.run3();
            this.intVal++;
         }

         this.bool = var2;
         this.bool2 = var1;
      }
   }

   public void run3() {
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

