package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;

public class NoHurtCamModule extends Module {
   public NoHurtCamModule() {
      super("NoHurtCam", "Removes the hurt camera shake", Category.RENDER);
   }

   @Override
   public void run2() {
      if (class310.player != null) {
         class310.player.hurtTime = 0;
      }
   }
}

