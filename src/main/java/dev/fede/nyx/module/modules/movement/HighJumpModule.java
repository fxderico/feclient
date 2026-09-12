package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class HighJumpModule extends Module {
   private final NumberSetting jumpVelocity = new NumberSetting("JumpVelocity", 1.0, 0.5, 5.0, 0.1);
   private final BooleanSetting onlyOnGround = new BooleanSetting("OnlyOnGround", true);
   private boolean bool;

   public HighJumpModule() {
      super("HighJump", "Amplifies the initial jump impulse", Category.MOVEMENT);
      this.run6(new Setting[]{this.jumpVelocity, this.onlyOnGround});
   }

   @Override
   public void run() {
      this.bool = class310.options != null && class310.options.jumpKey.isPressed();
   }

   @Override
   public void run2() {
      this.bool = false;
   }

   @Override
   public void run3() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null) {
         boolean var2 = class310.options.jumpKey.isPressed();
         boolean var3 = var2 && !this.bool;
         this.bool = var2;
         if (var3) {
            if (!this.onlyOnGround.getValue() || var1.isOnGround()) {
               if (!var1.hasVehicle()) {
                  Vec3d var4 = var1.getVelocity();
                  var1.setVelocity(var4.x, this.jumpVelocity.getValue(), var4.z);
                  var1.velocityDirty = true;
               }
            }
         }
      }
   }
}

