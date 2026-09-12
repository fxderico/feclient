package dev.fede.nyx.mixin;

import dev.fede.nyx.duck.ChamsLivingEntityRendererMixinHelper;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({EntityRenderState.class})
public abstract class ChamsRenderStateExtMixin implements ChamsLivingEntityRendererMixinHelper {
   @Unique
   private Entity nyx$chamsEntity;

   @Override
   public void nyx$setChamsEntity(Entity var1) {
      this.nyx$chamsEntity = var1;
   }

   @Override
   public Entity nyx$getChamsEntity() {
      return this.nyx$chamsEntity;
   }
}

