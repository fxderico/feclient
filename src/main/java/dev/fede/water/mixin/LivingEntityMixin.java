package dev.fede.water.mixin;

import dev.fede.water.module.modules.misc.SwingSpeed;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public class LivingEntityMixin {
   @Inject(
      method = {"getHandSwingDuration"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> var1) {
      SwingSpeed var2 = SwingSpeed.instance;
      if (var2 != null && var2.isEnabled()) {
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3.player != null && (Object)this == var3.player) {
            LivingEntity var4 = (LivingEntity)(Object)this;
            ItemStack var5 = var4.getStackInHand(Hand.MAIN_HAND);
            int var6 = var5.getSwingAnimation().duration();
            if (StatusEffectUtil.hasHaste(var4)) {
               var6 -= 1 + StatusEffectUtil.getHasteAmplifier(var4);
            } else if (var4.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
               var6 += (1 + var4.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2;
            }

            float var7 = var2.getSwingSpeed();
            int var8 = Math.max(1, Math.round(var6 / var7));
            var1.setReturnValue(var8);
         }
      }
   }
}

