package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.mixin.LivingEntityAccessor;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;

public class NoJumpDelayModule extends Module {
   private final BooleanSetting creativeOnly = new BooleanSetting("EnableForCreativeOnly", false);

   public NoJumpDelayModule() {
      super("NoJumpDelay", "Removes the vanilla cooldown between consecutive jumps", Category.PLAYER);
      this.run6(new Setting[]{this.creativeOnly});
   }

   @Override
   public void run2() {
      if (class310.player != null && class310.world != null) {
         if (!this.creativeOnly.getValue() || class310.player.isCreative()) {
            LivingEntityAccessor var1 = (LivingEntityAccessor)class310.player;
            if (var1.nyx$getJumpingCooldown() != 0) {
               var1.nyx$setJumpingCooldown(0);
            }
         }
      }
   }
}

