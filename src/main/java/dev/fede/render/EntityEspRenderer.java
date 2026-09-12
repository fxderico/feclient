package dev.fede.render;

import dev.fede.module.impl.MobEspModule;
import dev.fede.module.impl.PlayerEspModule;
import dev.fede.util.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3fc;

public final class EntityEspRenderer {
   private static final float TRACER_WIDTH = 1.2F;

   private EntityEspRenderer() {
   }

   public static void renderPlayers(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, PlayerEspModule module) {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientWorld level = mc.world;
      if (level != null && mc.player != null) {
         int color = module.color.get();
         boolean glow = module.style.check("Glow");
         boolean tracers = module.tracers.get();
         Vector3fc forward = tracers ? mc.gameRenderer.getCamera().getHorizontalPlane() : null;

         for (AbstractClientPlayerEntity player : level.getPlayers()) {
            if (player != mc.player && player.isAlive() && !player.isSpectator()) {
               box(bufferSource, poseStack, cam, player.getBoundingBox(), color, glow);
               if (tracers) {
                  tracer(bufferSource, poseStack, cam, forward, player.getBoundingBox(), color);
               }
            }
         }

         EspBoxRenderer.flush(bufferSource);
      }
   }

   public static void renderMobs(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, MobEspModule module) {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientWorld level = mc.world;
      if (level != null) {
         int hostileColor = module.hostile.get();
         int passiveColor = module.passive.get();
         boolean passiveToo = module.passiveToo.get();
         boolean tracers = module.tracers.get();
         Vector3fc forward = tracers ? mc.gameRenderer.getCamera().getHorizontalPlane() : null;

         for (Entity entity : level.getEntities()) {
            if (entity instanceof MobEntity && entity.isAlive()) {
               boolean hostile = entity instanceof Monster;
               if (hostile || passiveToo) {
                  int color = hostile ? hostileColor : passiveColor;
                  box(bufferSource, poseStack, cam, entity.getBoundingBox(), color, false);
                  if (tracers) {
                     tracer(bufferSource, poseStack, cam, forward, entity.getBoundingBox(), color);
                  }
               }
            }
         }

         EspBoxRenderer.flush(bufferSource);
      }
   }

   private static void box(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, Box b, int color, boolean glow) {
      EspBoxRenderer.outline(bufferSource, poseStack, cam, b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ, color, 2.0F);
      if (glow) {
         EspBoxRenderer.fill(bufferSource, poseStack, cam, b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ, Colors.withAlpha(color, 0.18F));
      }
   }

   private static void tracer(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, Vector3fc forward, Box b, int color) {
      EspBoxRenderer.tracer(
         bufferSource, poseStack, cam, forward, (b.minX + b.maxX) / 2.0, (b.minY + b.maxY) / 2.0, (b.minZ + b.maxZ) / 2.0, Colors.withAlpha(color, 0.72F), 1.2F
      );
   }
}

