package dev.fede.nyx.mixin;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import dev.fede.nyx.module.modules.combat.HitboxesModule;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ProjectileUtil.class})
public class HitboxTargetPredicateMixin {
   @ModifyVariable(
      method = {"raycast(Lnet/minecraft/Entity;Lnet/minecraft/Vec3d;Lnet/minecraft/Vec3d;Lnet/minecraft/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/EntityHitResult;"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private static Box nyx$expandSearchBox(Box var0) {
      if (var0 == null) {
         return null;
      } else {
         double var1 = HitboxesModule.getDouble();
         return var1 <= 0.0 ? var0 : var0.expand(var1);
      }
   }
}

