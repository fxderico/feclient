package dev.fede.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.fede.FeClient;
import dev.fede.hud.HudDragController;
import dev.fede.module.ModuleManager;
import dev.fede.util.CpsTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Mouse.class})
public class MouseHandlerMixin {
   @Shadow
   private double cursorDeltaX;
   @Shadow
   private double cursorDeltaY;

   @Inject(
      method = {"method_1606"},
      at = {@At("HEAD")}
   )
   private void FeClient$aimAssist(double d, CallbackInfo ci) {
      ModuleManager modules = FeClient.modules();
      if (modules != null && modules.aimAssist != null && modules.aimAssist.isEnabled()) {
         double[] add = modules.aimAssist.computePixels(d, this.cursorDeltaX, this.cursorDeltaY);
         if (add != null) {
            this.cursorDeltaX = this.cursorDeltaX + add[0];
            this.cursorDeltaY = this.cursorDeltaY + add[1];
         }
      }
   }

   @Inject(
      method = {"method_1601(JLnet/minecraft/class_11910;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$onButton(long window, MouseInput buttonInfo, int action, CallbackInfo ci) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      if (window == minecraft.getWindow().getHandle()) {
         if (action == 1 && minecraft.currentScreen == null) {
            CpsTracker.onClick(buttonInfo.button());
         }

         if (minecraft.currentScreen instanceof ChatScreen && buttonInfo.button() == 0 && FeClient.hud() != null) {
            if (action == 1) {
               if (HudDragController.tryStartDrag(FeClient.hud())) {
                  ci.cancel();
               }
            } else if (action == 0 && HudDragController.isDragging()) {
               HudDragController.stopDrag();
               FeClient.config().save();
               ci.cancel();
            }
         }
      }
   }

   @ModifyExpressionValue(
      method = {"method_1606"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_7172;method_41753()Ljava/lang/Object;"
      )},
      slice = {@Slice(
         from = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_315;method_42495()Lnet/minecraft/class_7172;"
         )
      )}
   )
   private Object FeClient$zoomSensitivity(Object original) {
      ModuleManager modules = FeClient.modules();
      if (modules != null && modules.zoom != null && original instanceof Double s) {
         double factor = modules.zoom.currentFactor();
         if (factor <= 1.0001) {
            return original;
         } else {
            double e = s * 0.6 + 0.2;
            double scaled = e / Math.cbrt(factor);
            double substitute = (scaled - 0.2) / 0.6;
            return Math.max(0.0, substitute);
         }
      } else {
         return original;
      }
   }

   @Inject(
      method = {"method_1598(JDD)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
      MinecraftClient minecraft = MinecraftClient.getInstance();
      if (window == minecraft.getWindow().getHandle()
         && minecraft.currentScreen instanceof ChatScreen
         && FeClient.hud() != null
         && HudDragController.tryResize(FeClient.hud(), yOffset)) {
         ci.cancel();
      }
   }
}



