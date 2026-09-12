package dev.fede.gui;

import dev.fede.FeClient;
import dev.fede.module.impl.GambleRiggerModule;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterRender;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents.AllowMouseClick;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;

public final class GambleRiggerOverlay {
   private GambleRiggerOverlay() {
   }

   public static void register() {
      ScreenEvents.AFTER_INIT.register((AfterInit)(client, screen, scaledWidth, scaledHeight) -> {
         if (screen instanceof Generic3x3ContainerScreen container) {
            GambleRiggerModule mod = FeClient.modules() == null ? null : FeClient.modules().gambleRigger;
            if (mod != null && mod.isEnabled()) {
               GamblePanel panel = new GamblePanel(container, mod);
               ScreenEvents.afterRender(screen).register((AfterRender)(s, graphics, mouseX, mouseY, tickDelta) -> panel.render(graphics, mouseX, mouseY));
               ScreenMouseEvents.allowMouseClick(screen).register((AllowMouseClick)(s, ctx) -> !panel.handleClick(ctx.x(), ctx.y(), ctx.button()));
            }
         }
      });
   }
}



