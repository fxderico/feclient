package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;

public class PortalGodModeModule extends Module {
   private final BooleanSetting blockTeleport = new BooleanSetting("BlockTeleport", true);
   private final BooleanSetting hideOverlay = new BooleanSetting("HideOverlay", true);

   public PortalGodModeModule() {
      super("PortalGodMode", "Blocks nether-portal teleport and hides the portal overlay", Category.PLAYER);
      this.run6(new Setting[]{this.blockTeleport, this.hideOverlay});
   }

   @Override
   public void run2() {
      if (class310.player != null && class310.world != null) {
         if (this.hideOverlay.getValue()) {
            if (class310.player.nauseaIntensity != 0.0F) {
               class310.player.nauseaIntensity = 0.0F;
            }

            if (class310.player.lastNauseaIntensity != 0.0F) {
               class310.player.lastNauseaIntensity = 0.0F;
            }
         }

         if (this.blockTeleport.getValue() && class310.player.portalManager != null) {
            class310.player.portalManager = null;
         }
      }
   }
}

