package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/**
 * Prints your coordinates to chat the moment the death screen appears, so you
 * can always find your way back to your stuff. Ported from Expa's Death
 * Coordinates. Fires once per death (resets when you respawn / leave the
 * death screen).
 */
public class DeathCoordinatesModule extends Module {
   private boolean posted;

   public DeathCoordinatesModule() {
      super("DeathCoords", "Prints your coordinates to chat when you die", Category.PLAYER);
   }

   @Override
   public void run() {
      this.posted = false;
   }

   @Override
   public void run3() {
      if (class310.player == null) {
         return;
      }

      if (!(class310.currentScreen instanceof DeathScreen)) {
         this.posted = false;
         return;
      }

      if (this.posted) {
         return;
      }

      BlockPos p = class310.player.getBlockPos();
      String msg = "§cDied @ §fX: " + p.getX() + " §cY: §f" + p.getY() + " §cZ: §f" + p.getZ();
      if (class310.inGameHud != null) {
         class310.inGameHud.getChatHud().addMessage(Text.literal(msg));
      }

      this.posted = true;
   }
}
