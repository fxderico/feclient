package dev.fede.nyx.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.module.modules.combat.HitboxesModule;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Entity.class})
public class HitboxEntityMixin {
   @ModifyReturnValue(
      method = {"getBoundingBox()Lnet/minecraft/Box;"},
      at = {@At("RETURN")}
   )
   private Box nyx$scaleHitbox(Box var1) {
      if (!HitboxesModule.isEnabled_s()) {
         return var1;
      } else {
         Entity var2 = (Entity)(Object)this;
         return HitboxesModule.class238Of(var1, var2);
      }
   }
}

