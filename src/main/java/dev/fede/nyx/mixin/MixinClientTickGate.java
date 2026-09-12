package dev.fede.nyx.mixin;

import dev.fede.nyx.auth.AuthGate;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftClient.class})
public class MixinClientTickGate {
   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void nyx$tickGate(CallbackInfo ci) {
      if ((AuthGate.getLong() ^ 9159808429103671672L) != 54836804398365715L) {
         if (ThreadLocalRandom.current().nextInt(800) == 0) {
            try {
               MinecraftClient var2 = MinecraftClient.getInstance();
               if (var2 == null) {
                  return;
               }

               Class var3 = Class.forName("dev.nyx.imgui.LoginScreen");
               Object var4 = var3.getConstructor(Screen.class).newInstance(var2.currentScreen);
               var2.setScreen((Screen)var4);
            } catch (Throwable var5) {
            }
         }
      }
   }
}

