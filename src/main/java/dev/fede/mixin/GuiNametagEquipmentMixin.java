package dev.fede.mixin;

import dev.fede.render.WorldNametagRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameHud.class})
public class GuiNametagEquipmentMixin {
   @Inject(
      method = {"method_1753(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
      at = {@At("TAIL")}
   )
   private void FeClient$nametagEquipment(DrawContext guiGraphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
      WorldNametagRenderer.renderEquipment(guiGraphics);
   }
}

