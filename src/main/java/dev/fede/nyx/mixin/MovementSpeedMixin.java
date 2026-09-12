package dev.fede.nyx.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.module.modules.movement.NoSlowModule;
import dev.fede.nyx.module.modules.movement.SpeedModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LivingEntity.class})
public abstract class MovementSpeedMixin {
   private static final float ITEM_SLOW_COMPENSATION = 5.0F;
   private static final float SNEAK_SLOW_COMPENSATION = 3.33F;

   @ModifyReturnValue(
      method = {"getMovementSpeed()F"},
      at = {@At("RETURN")}
   )
   private float nyx$modifyMovementSpeed(float original) {
      boolean var2 = SpeedModule.isEnabled_s();
      boolean var3 = NoSlowModule.isEnabled_s();
      if (!var2 && !var3) {
         return original;
      } else {
         MinecraftClient var4 = MinecraftClient.getInstance();
         if (var4 == null || var4.player == null) {
            return original;
         } else if (var4.player != ((Object)this)) {
            return original;
         } else {
            float var5 = original;
            if (var2) {
               var5 = original * SpeedModule.getFloat();
            }

            if (var3) {
               if (NoSlowModule.isEnabled2() && var4.player.isUsingItem()) {
                  var5 *= 5.0F;
               }

               if (NoSlowModule.isEnabled3_s() && var4.player.isSneaking()) {
                  var5 *= 3.33F;
               }
            }

            return var5;
         }
      }
   }
}

