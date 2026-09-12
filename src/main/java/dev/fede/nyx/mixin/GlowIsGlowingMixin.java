package dev.fede.nyx.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.module.modules.addons.GlintCustomiserModule;
import dev.fede.nyx.module.modules.render.OutlineESPModule;
import dev.fede.nyx.module.modules.render.OutlinesModule;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Entity.class})
public class GlowIsGlowingMixin {
   @ModifyReturnValue(
      method = {"isGlowing()Z"},
      at = {@At("RETURN")}
   )
   private boolean nyx$forceGlow(boolean vanilla) {
      if (vanilla) {
         return true;
      } else {
         Entity var2 = (Entity)(Object)this;
         if (OutlinesModule.check(var2)) {
            return true;
         } else {
            return OutlineESPModule.check(var2) ? true : GlintCustomiserModule.check(var2);
         }
      }
   }
}

