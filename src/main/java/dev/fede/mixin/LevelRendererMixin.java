package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.render.AccessoryRenderer;
import dev.fede.render.BlockEntityEspRenderer;
import dev.fede.render.BlockEspRenderer;
import dev.fede.render.BlockOutlineRenderer;
import dev.fede.render.ChunkFinderRenderer;
import dev.fede.render.EntityEspRenderer;
import dev.fede.render.HitParticleRenderer;
import dev.fede.render.HoleEspRenderer;
import dev.fede.render.JumpCircleRenderer;
import dev.fede.render.MiscBlockEspRenderer;
import dev.fede.render.StorageEspRenderer;
import dev.fede.render.SusChunkRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public class LevelRendererMixin {
   @Inject(
      method = {"method_62210(Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_4587;ZLnet/minecraft/class_11658;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$customBlockOutline(
      Immediate bufferSource, MatrixStack poseStack, boolean translucentPass, WorldRenderState levelRenderState, CallbackInfo ci
   ) {
      ModuleManager modules = FeClient.modules();
      if (modules != null) {
         if (translucentPass && modules.susChunkFinder.isEnabled()) {
            SusChunkRenderer.render(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.susChunkFinder);
         }

         if (translucentPass && modules.chunkFinder != null && modules.chunkFinder.isEnabled()) {
            ChunkFinderRenderer.render(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.chunkFinder);
         }

         if (translucentPass && modules.storageEsp != null && modules.storageEsp.isEnabled()) {
            StorageEspRenderer.render(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.storageEsp);
         }

         if (translucentPass && modules.blockEsp != null && modules.blockEsp.isEnabled()) {
            BlockEspRenderer.render(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.blockEsp);
         }

         if (translucentPass && modules.blockEntityEsp != null && modules.blockEntityEsp.isEnabled()) {
            BlockEntityEspRenderer.render(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.blockEntityEsp);
         }

         if (translucentPass && modules.spawnerNametags != null && modules.spawnerNametags.isEnabled()) {
            MiscBlockEspRenderer.renderSpawners(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.spawnerNametags);
         }

         if (translucentPass && modules.debugHoleEsp != null && modules.debugHoleEsp.isEnabled()) {
            HoleEspRenderer.render(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.debugHoleEsp);
         }

         if (translucentPass && modules.playerEsp != null && modules.playerEsp.isEnabled()) {
            EntityEspRenderer.renderPlayers(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.playerEsp);
         }

         if (translucentPass && modules.mobEsp != null && modules.mobEsp.isEnabled()) {
            EntityEspRenderer.renderMobs(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.mobEsp);
         }

         if (translucentPass && modules.jumpCircles != null && modules.jumpCircles.isEnabled()) {
            JumpCircleRenderer.render(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.jumpCircles);
         }

         if (translucentPass && modules.hitParticles != null && modules.hitParticles.isEnabled()) {
            HitParticleRenderer.render(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.hitParticles);
         }

         if (translucentPass && modules.customAccessories != null && modules.customAccessories.isEnabled()) {
            AccessoryRenderer.render(bufferSource, poseStack, levelRenderState.cameraRenderState.pos, modules.customAccessories);
         }

         if (modules.freecam != null && modules.freecam.isActive()) {
            ci.cancel();
         } else if (modules.blockOutline.isEnabled()) {
            ci.cancel();
            OutlineRenderState state = levelRenderState.outlineRenderState;
            if (state != null && state.isTranslucent() == translucentPass) {
               BlockOutlineRenderer.render(bufferSource, poseStack, state, levelRenderState.cameraRenderState.pos, modules.blockOutline);
            }
         }
      }
   }
}



