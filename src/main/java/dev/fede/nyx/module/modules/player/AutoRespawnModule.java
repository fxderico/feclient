package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawnModule extends Module {
   private final NumberSetting delayMs = new NumberSetting("DelayMs", 100.0, 0.0, 5000.0, 10.0);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private boolean bool;
   private boolean bool2;

   public AutoRespawnModule() {
      super("AutoRespawn", "Auto-clicks the respawn button on the death screen", Category.PLAYER);
      this.run6(new Setting[]{this.delayMs});
   }

   @Override
   public void run() {
      this.bool = false;
      this.bool2 = false;
      this.antiVoidModuleHelper.run();
   }

   @Override
   public void run2() {
      this.bool = false;
      this.bool2 = false;
   }

   @Override
   public void run3() {
      if (class310 != null) {
         boolean var1 = class310.currentScreen instanceof DeathScreen;
         if (!var1) {
            if (this.bool2) {
               this.bool = false;
               this.bool2 = false;
            }
         } else {
            if (!this.bool2) {
               this.antiVoidModuleHelper.run();
               this.bool = false;
               this.bool2 = true;
            }

            if (!this.bool) {
               if (class310.player != null) {
                  if (this.antiVoidModuleHelper.check(this.delayMs.getValueLong())) {
                     class310.player.requestRespawn();
                     class310.setScreen(null);
                     this.bool = true;
                  }
               }
            }
         }
      }
   }
}

