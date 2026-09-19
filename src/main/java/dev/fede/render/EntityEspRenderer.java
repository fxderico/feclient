package dev.fede.render;

import dev.fede.module.impl.FriendlyMobEspModule;
import dev.fede.module.impl.MobEspModule;
import dev.fede.module.impl.PlayerEspModule;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.ModeSetting;
import dev.fede.util.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3fc;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles the 3D-pass ESP styles (Outline / Box — real world-space
 * wireframes, hooked from LevelRendererMixin into the vertex buffer pass),
 * the 2D style (a flat on-screen rectangle, hooked from OverlayRenderer
 * into the NVG pass instead — screen-space, not world-space), and Glow.
 *
 * Glow does NOT draw anything itself. Checked liquidbounce's actual
 * EspGlowMode — it's not a custom shader or hand-drawn halo at all, it's
 * just vanilla's real entity-glow outline system (the same one the Glowing
 * potion effect / spectator mode use, which already renders through walls
 * with no depth test — that's built into vanilla, not something a client
 * has to fake). So Glow here just calls entity.setGlowing(true) on matching
 * entities each tick (tickGlow(), wired into FeClient's tick loop) and lets
 * vanilla's own renderer draw the actual outline. Confirmed setGlowing() is
 * a pure client-side render flag (checked the bytecode — no networking),
 * safe to call on any entity. Trade-off: vanilla's outline color follows
 * scoreboard team color, not each module's custom color setting — getting
 * per-entity custom outline colors needs a mixin into the outline color
 * lookup, which is a separate, real follow-up, not tonight's.
 */
public final class EntityEspRenderer {
   private static final Set<Entity> glowing = new HashSet<>();
   private static final Set<Entity> glowScratch = new HashSet<>();

   private EntityEspRenderer() {
   }

   /** Called once per client tick (see FeClient). Keeps vanilla's real glow outline in sync with who currently matches each Glow-styled ESP module. */
   public static void tickGlow() {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientWorld level = mc.world;
      dev.fede.module.ModuleManager modules = dev.fede.FeClient.modules();
      if (level == null || modules == null) {
         clearGlow();
         return;
      }

      glowScratch.clear();

      // was mc.player.getEntityPos() here and below — meant glow's range
      // check used the *player's* position while every other style (3D
      // pass, 2D pass) ranges off the actual *camera* position. those
      // usually match, except with freecam active, where they can be
      // arbitrarily far apart — glow would then use a completely different
      // range center than box/outline/2D for the exact same slider value.
      Vec3d camPos = mc.gameRenderer != null && mc.gameRenderer.getCamera() != null
         ? mc.gameRenderer.getCamera().getCameraPos()
         : mc.player != null ? mc.player.getEntityPos() : null;
      if (camPos == null) {
         clearGlow();
         return;
      }

      PlayerEspModule playerEsp = modules.playerEsp;
      if (playerEsp != null && playerEsp.isEnabled() && playerEsp.style.check("Glow") && mc.player != null) {
         double rangeSq = chunksToBlocksSq(playerEsp.range.getFloat());
         Vec3d cam = camPos;
         for (AbstractClientPlayerEntity player : level.getPlayers()) {
            if (player != mc.player && player.isAlive() && !player.isSpectator() && cam.squaredDistanceTo(player.getEntityPos()) <= rangeSq) {
               glowScratch.add(player);
            }
         }
      }

      MobEspModule mobEsp = modules.mobEsp;
      boolean mobGlow = mobEsp != null && mobEsp.isEnabled() && mobEsp.style.check("Glow");
      FriendlyMobEspModule friendlyEsp = modules.friendlyMobEsp;
      boolean friendlyGlow = friendlyEsp != null && friendlyEsp.isEnabled() && friendlyEsp.style.check("Glow");
      if ((mobGlow || friendlyGlow) && mc.player != null) {
         Vec3d cam = camPos;
         double mobRangeSq = mobGlow ? chunksToBlocksSq(mobEsp.range.getFloat()) : 0;
         double friendlyRangeSq = friendlyGlow ? chunksToBlocksSq(friendlyEsp.range.getFloat()) : 0;

         for (Entity entity : level.getEntities()) {
            if (!(entity instanceof MobEntity) || !entity.isAlive()) continue;
            boolean hostile = entity instanceof Monster;

            if (mobGlow && cam.squaredDistanceTo(entity.getEntityPos()) <= mobRangeSq && (hostile || mobEsp.passiveToo.get())) {
               glowScratch.add(entity);
            }

            if (friendlyGlow && !hostile && cam.squaredDistanceTo(entity.getEntityPos()) <= friendlyRangeSq) {
               boolean isVillagerLike = entity instanceof VillagerEntity || entity instanceof WanderingTraderEntity;
               boolean isTamed = entity instanceof TameableEntity t && t.isTamed();
               if ((isVillagerLike && !friendlyEsp.villagers.get()) || (isTamed && !friendlyEsp.tamed.get())) continue;
               glowScratch.add(entity);
            }
         }
      }

      for (Entity e : glowing) {
         if (!glowScratch.contains(e)) e.setGlowing(false);
      }
      for (Entity e : glowScratch) {
         if (!glowing.contains(e)) e.setGlowing(true);
      }
      glowing.clear();
      glowing.addAll(glowScratch);
   }

   /** Un-glows everything and forgets tracked state — call on disconnect/world change. */
   public static void clearGlow() {
      for (Entity e : glowing) {
         e.setGlowing(false);
      }
      glowing.clear();
   }

   private static double chunksToBlocksSq(float chunks) {
      double blocks = chunks * 16.0;
      return blocks * blocks;
   }

   /**
    * entity.getBoundingBox() is the box at the last full tick — drawing it
    * as-is makes the esp box visibly lag/jitter half a tick behind a moving
    * entity's actual (interpolated) rendered position, since vanilla itself
    * renders entities lerped between lastRenderX/Y/Z and getX/Y/Z by the
    * frame's partial tick. Same lerp WorldNametagRenderer already uses for
    * positioning tags, applied here to the hitbox instead.
    */
   private static Box interpolatedBox(Entity e) {
      Box b = e.getBoundingBox();
      float pt = WorldProjection.partialTick();
      double dx = MathHelper.lerp(pt, e.lastRenderX, e.getX()) - e.getX();
      double dy = MathHelper.lerp(pt, e.lastRenderY, e.getY()) - e.getY();
      double dz = MathHelper.lerp(pt, e.lastRenderZ, e.getZ()) - e.getZ();
      return b.offset(dx, dy, dz);
   }

   // ── 3D pass (world-space, through walls) ────────────────────────────────

   public static void renderPlayers(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, PlayerEspModule module) {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientWorld level = mc.world;
      if (level != null && mc.player != null) {
         int color = module.color.get();
         boolean tracers = module.tracers.get();
         double rangeSq = chunksToBlocksSq(module.range.getFloat());
         Vector3fc forward = tracers ? mc.gameRenderer.getCamera().getHorizontalPlane() : null;

         for (AbstractClientPlayerEntity player : level.getPlayers()) {
            if (player != mc.player && player.isAlive() && !player.isSpectator() && cam.squaredDistanceTo(player.getEntityPos()) <= rangeSq) {
               Box b = interpolatedBox(player);
               box(bufferSource, poseStack, cam, b, color, module.style);
               if (tracers) {
                  tracer(bufferSource, poseStack, cam, forward, b, color);
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
         double rangeSq = chunksToBlocksSq(module.range.getFloat());
         Vector3fc forward = tracers ? mc.gameRenderer.getCamera().getHorizontalPlane() : null;

         for (Entity entity : level.getEntities()) {
            if (entity instanceof MobEntity && entity.isAlive() && cam.squaredDistanceTo(entity.getEntityPos()) <= rangeSq) {
               boolean hostile = entity instanceof Monster;
               if (hostile || passiveToo) {
                  int color = hostile ? hostileColor : passiveColor;
                  Box b = interpolatedBox(entity);
                  box(bufferSource, poseStack, cam, b, color, module.style);
                  if (tracers) {
                     tracer(bufferSource, poseStack, cam, forward, b, color);
                  }
               }
            }
         }

         EspBoxRenderer.flush(bufferSource);
      }
   }

   public static void renderFriendlyMobs(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, FriendlyMobEspModule module) {
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientWorld level = mc.world;
      if (level != null) {
         int color = module.color.get();
         boolean includeVillagers = module.villagers.get();
         boolean includeTamed = module.tamed.get();
         boolean tracers = module.tracers.get();
         double rangeSq = chunksToBlocksSq(module.range.getFloat());
         Vector3fc forward = tracers ? mc.gameRenderer.getCamera().getHorizontalPlane() : null;

         for (Entity entity : level.getEntities()) {
            if (entity instanceof MobEntity && entity.isAlive()
                  && !(entity instanceof Monster)
                  && cam.squaredDistanceTo(entity.getEntityPos()) <= rangeSq) {
               boolean isVillagerLike = entity instanceof VillagerEntity || entity instanceof WanderingTraderEntity;
               boolean isTamed = entity instanceof TameableEntity t && t.isTamed();
               if ((isVillagerLike && !includeVillagers) || (isTamed && !includeTamed)) {
                  continue;
               }
               Box b = interpolatedBox(entity);
               box(bufferSource, poseStack, cam, b, color, module.style);
               if (tracers) {
                  tracer(bufferSource, poseStack, cam, forward, b, color);
               }
            }
         }

         EspBoxRenderer.flush(bufferSource);
      }
   }

   private static void box(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, Box b, int color, ModeSetting style) {
      if (style.check("2D Box") || style.check("Glow")) {
         return; // 2D Box: NVG pass (render2D). Glow: vanilla's real entity outline (tickGlow) draws it, not us.
      }

      EspBoxRenderer.outline(bufferSource, poseStack, cam, b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ, color, 2.0F);
      if (style.check("Box")) {
         EspBoxRenderer.fill(bufferSource, poseStack, cam, b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ, Colors.withAlpha(color, 0.18F));
      }
   }

   private static void tracer(Immediate bufferSource, MatrixStack poseStack, Vec3d cam, Vector3fc forward, Box b, int color) {
      EspBoxRenderer.tracer(
         bufferSource, poseStack, cam, forward, (b.minX + b.maxX) / 2.0, (b.minY + b.maxY) / 2.0, (b.minZ + b.maxZ) / 2.0, Colors.withAlpha(color, 0.72F), 1.2F
      );
   }

   // ── 2D pass (screen-space, NVG overlay) ─────────────────────────────────

   public static void render2D(NVGRenderer vg) {
      if (!WorldProjection.isValid()) return;
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientWorld level = mc.world;
      if (level == null) return;
      dev.fede.module.ModuleManager modules = dev.fede.FeClient.modules();
      if (modules == null) return;

      PlayerEspModule playerEsp = modules.playerEsp;
      if (playerEsp != null && playerEsp.isEnabled() && playerEsp.style.check("2D Box") && mc.player != null) {
         double rangeSq = chunksToBlocksSq(playerEsp.range.getFloat());
         Vec3d cam = mc.gameRenderer.getCamera().getCameraPos();
         int color = playerEsp.color.get();
         for (AbstractClientPlayerEntity player : level.getPlayers()) {
            if (player != mc.player && player.isAlive() && !player.isSpectator() && cam.squaredDistanceTo(player.getEntityPos()) <= rangeSq) {
               box2D(vg, interpolatedBox(player), color);
            }
         }
      }

      MobEspModule mobEsp = modules.mobEsp;
      FriendlyMobEspModule friendlyEsp = modules.friendlyMobEsp;
      boolean mobEsp2D = mobEsp != null && mobEsp.isEnabled() && mobEsp.style.check("2D Box");
      boolean friendlyEsp2D = friendlyEsp != null && friendlyEsp.isEnabled() && friendlyEsp.style.check("2D Box");
      if (mobEsp2D || friendlyEsp2D) {
         Vec3d cam = mc.gameRenderer.getCamera().getCameraPos();
         double mobRangeSq = mobEsp2D ? chunksToBlocksSq(mobEsp.range.getFloat()) : 0;
         double friendlyRangeSq = friendlyEsp2D ? chunksToBlocksSq(friendlyEsp.range.getFloat()) : 0;

         for (Entity entity : level.getEntities()) {
            if (!(entity instanceof MobEntity) || !entity.isAlive()) continue;
            boolean hostile = entity instanceof Monster;

            if (mobEsp2D && cam.squaredDistanceTo(entity.getEntityPos()) <= mobRangeSq && (hostile || mobEsp.passiveToo.get())) {
               box2D(vg, interpolatedBox(entity), hostile ? mobEsp.hostile.get() : mobEsp.passive.get());
            }

            if (friendlyEsp2D && !hostile && cam.squaredDistanceTo(entity.getEntityPos()) <= friendlyRangeSq) {
               boolean isVillagerLike = entity instanceof VillagerEntity || entity instanceof WanderingTraderEntity;
               boolean isTamed = entity instanceof TameableEntity t && t.isTamed();
               if ((isVillagerLike && !friendlyEsp.villagers.get()) || (isTamed && !friendlyEsp.tamed.get())) continue;
               box2D(vg, interpolatedBox(entity), friendlyEsp.color.get());
            }
         }
      }
   }

   private static void box2D(NVGRenderer vg, Box b, int color) {
      float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
      boolean any = false;
      double[] xs = {b.minX, b.maxX};
      double[] ys = {b.minY, b.maxY};
      double[] zs = {b.minZ, b.maxZ};
      for (double x : xs) {
         for (double y : ys) {
            for (double z : zs) {
               float[] p = WorldProjection.project(x, y, z);
               if (p == null) continue;
               any = true;
               minX = Math.min(minX, p[0]);
               minY = Math.min(minY, p[1]);
               maxX = Math.max(maxX, p[0]);
               maxY = Math.max(maxY, p[1]);
            }
         }
      }
      if (!any) return;

      vg.rectOutline(minX, minY, maxX - minX, maxY - minY, 0.0F, 1.5F, color);
   }
}
