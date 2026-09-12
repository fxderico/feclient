package dev.fede.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.FreecamModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameHud.class})
public class GuiCrosshairMixin {
   @Inject(
      method = {"method_1736"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$hideVanillaCrosshair(DrawContext guiGraphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
      ModuleManager modules = FeClient.modules();
      if (modules != null && modules.customCrosshair != null && modules.customCrosshair.shouldHideVanilla()) {
         ci.cancel();
      }
   }

   @ModifyExpressionValue(
      method = {"method_1736"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_5498;method_31034()Z"
      )}
   )
   private boolean FeClient$freecamCrosshairInThirdPerson(boolean isFirstPerson) {
      FreecamModule freecam = FreecamModule.get();
      return freecam != null && freecam.isActive() && freecam.isShowPlayerModel() && !isFirstPerson ? true : isFirstPerson;
   }
}



