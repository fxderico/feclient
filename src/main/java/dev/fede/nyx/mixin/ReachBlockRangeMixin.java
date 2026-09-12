package dev.fede.nyx.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.module.modules.combat.ReachModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({PlayerEntity.class})
public class ReachBlockRangeMixin {
   @ModifyReturnValue(
      method = {"getBlockInteractionRange()D"},
      at = {@At("RETURN")}
   )
   private double nyx$extendBlockReach(double original) {
      if (!ReachModule.isEnabled_s()) {
         return original;
      } else {
         MinecraftClient var3 = MinecraftClient.getInstance();
         if (var3 != null && var3.player == ((Object)this)) {
            double var4 = ReachModule.getDouble2();
            return var4 > original ? var4 : original;
         } else {
            return original;
         }
      }
   }
}

