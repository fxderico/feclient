package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.player.SwingAnimationModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public class SwingDurationMixin {
   @Inject(
      method = {"getHandSwingDuration"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$overrideDuration(CallbackInfoReturnable<Integer> var1) {
      try {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 == null || var2.player == null) {
            return;
         }

         if (((Object)this) != var2.player) {
            return;
         }

         SwingAnimationModule var3 = SwingAnimationModule.swingAnimationModule;
         if (var3 == null) {
            return;
         }

         int var4 = var3.getInt();
         if (var4 > 0) {
            var1.setReturnValue(var4);
         }
      } catch (Throwable var5) {
      }
   }
}

