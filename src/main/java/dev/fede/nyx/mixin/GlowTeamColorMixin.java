package dev.fede.nyx.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.module.modules.addons.GlintCustomiserModule;
import dev.fede.nyx.module.modules.render.OutlineESPModule;
import dev.fede.nyx.module.modules.render.OutlinesModule;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Entity.class})
public class GlowTeamColorMixin {
   @ModifyReturnValue(
      method = {"getTeamColorValue()I"},
      at = {@At("RETURN")}
   )
   private int nyx$overrideOutlineColor(int vanilla) {
      Entity var2 = (Entity)(Object)this;
      int var3 = OutlinesModule.intOf(var2, vanilla);
      if (var3 != -1) {
         return var3 & 16777215;
      } else {
         int var4 = OutlineESPModule.intOf(var2);
         if (var4 != -1) {
            return var4 & 16777215;
         } else {
            int var5 = GlintCustomiserModule.intOf(var2);
            return var5 != -1 ? var5 & 16777215 : vanilla;
         }
      }
   }
}

