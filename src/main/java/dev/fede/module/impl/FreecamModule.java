package dev.fede.module.impl;

import dev.fede.FeClient;
import dev.fede.mixin.ClientInputAccessor;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class FreecamModule extends Module {
   private static FreecamModule instance;
   public final SliderSetting speed = this.addSetting(new SliderSetting("Speed", "Camera fly speed", 1.0, 0.1, 50.0, 0.1, "x"));
   public final SliderSetting verticalMultiplier = this.addSetting(new SliderSetting("Vertical speed", "Up/down speed multiplier", 1.0, 0.2, 5.0, 0.05, "x"));
   public final BooleanSetting smoothing = this.addSetting(new BooleanSetting("Smoothing", "Ease camera movement", true));
   public final BooleanSetting showPlayerModel = this.addSetting(new BooleanSetting("Show own body", "Render your body while detached", true));
   public final BooleanSetting showHands = this.addSetting(new BooleanSetting("Show hands", "Keep first-person hands visible", true));
   public final BooleanSetting bodyFollowsKeys = this.addSetting(new BooleanSetting("Body uses movement keys", "Physical WASD also move the body", false));
   public final SliderSetting lookSensitivity = this.addSetting(new SliderSetting("Look Sensitivity", "Camera look sensitivity", 0.5, 0.1, 2.0, 0.05));
   public final SliderSetting freecamChunkDistance = this.addSetting(
      new SliderSetting("Chunk distance", "Chunks to load around the camera", 12.0, 2.0, 32.0, 1.0)
   );
   private double currentX;
   private double currentY;
   private double currentZ;
   private double prevX;
   private double prevY;
   private double prevZ;
   private float currentYaw;
   private float currentPitch;
   private float prevYaw;
   private float prevPitch;
   private double savedX;
   private double savedY;
   private double savedZ;
   private float savedYaw;
   private float savedPitch;
   private boolean savedAbilitiesFlying;
   private boolean savedSmartCull;
   private Perspective perspectiveBeforeFreecam;
   private boolean switchedPerspectiveForBody;
   private ChunkPos lastSyncedCamChunk;
   private int lastSyncedLoadDistance = Integer.MIN_VALUE;
   private boolean activationPending;
   private boolean active = false;
   private boolean latchedForward;
   private boolean latchedBack;
   private boolean latchedLeft;
   private boolean latchedRight;
   private boolean latchedJump;
   private boolean latchedSneak;
   private boolean latchedSprint;
   private int autopilotWarmupTicks;

   public FreecamModule() {
      super("Freecam", "Detached camera (WASD = fly). Body can keep walking; mining uses real aim.", Category.MISC);
      instance = this;
   }

   public static FreecamModule get() {
      return instance;
   }

   @Override
   protected void onEnable() {
      this.activationPending = true;
      this.active = false;
   }

   @Override
   protected void onDisable() {
      MinecraftClient mc = MinecraftClient.getInstance();
      this.clearMovementLatches();
      this.activationPending = false;
      this.active = false;
      if (this.switchedPerspectiveForBody) {
         mc.options.setPerspective(this.perspectiveBeforeFreecam);
         this.switchedPerspectiveForBody = false;
      }

      this.restoreViewOnlyClientState(mc);
      this.restoreVanillaChunkLoading(mc);
      if (mc.player != null) {
         mc.player.getAbilities().flying = this.savedAbilitiesFlying;
      }
   }

   public void tryCompleteActivation(MinecraftClient mc) {
      if (this.isEnabled() && this.activationPending) {
         if (mc.player != null && mc.world != null) {
            this.savedX = mc.player.getX();
            this.savedY = mc.player.getY();
            this.savedZ = mc.player.getZ();
            this.savedYaw = mc.player.getYaw();
            this.savedPitch = mc.player.getPitch();
            this.currentX = this.prevX = this.savedX;
            this.currentY = this.prevY = this.savedY + mc.player.getStandingEyeHeight();
            this.currentZ = this.prevZ = this.savedZ;
            this.currentYaw = this.prevYaw = this.savedYaw;
            this.currentPitch = this.prevPitch = this.savedPitch;
            this.savedAbilitiesFlying = mc.player.getAbilities().flying;
            this.switchedPerspectiveForBody = false;
            if (this.showPlayerModel.get()) {
               this.perspectiveBeforeFreecam = mc.options.getPerspective();
               if (this.perspectiveBeforeFreecam.isFirstPerson()) {
                  mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                  this.switchedPerspectiveForBody = true;
               }
            }

            this.activationPending = false;
            this.active = true;
            this.captureMovementLatches(mc);
            this.maybeLatchWalkFromVelocity(mc);
            this.autopilotWarmupTicks = 40;
            this.lastSyncedCamChunk = null;
            this.lastSyncedLoadDistance = Integer.MIN_VALUE;
            this.applyViewOnlyClientState(mc);
            this.syncFreecamChunkLoading(mc);
            reapplyBodyInput(mc);
         }
      }
   }

   private void captureMovementLatches(MinecraftClient mc) {
      GameOptions o = mc.options;
      ClientPlayerEntity player = mc.player;
      PlayerInput ki = player != null ? player.input.playerInput : PlayerInput.DEFAULT;
      this.latchedForward = o.forwardKey.isPressed() || ki.forward();
      this.latchedBack = o.backKey.isPressed() || ki.backward();
      this.latchedLeft = o.leftKey.isPressed() || ki.left();
      this.latchedRight = o.rightKey.isPressed() || ki.right();
      this.latchedJump = o.jumpKey.isPressed() || ki.jump();
      this.latchedSneak = o.sneakKey.isPressed() || ki.sneak() || player != null && player.isSneaking();
      this.latchedSprint = o.sprintKey.isPressed() || ki.sprint();
      this.mergeLatchFromAutoWalk();
      this.mergeLatchFromLivingSpeed(mc.player);
   }

   private void mergeLatchFromAutoWalk() {
      AutoWalkModule aw = FeClient.modules() != null ? FeClient.modules().autoWalk : null;
      if (aw != null && aw.isEnabled()) {
         this.latchedForward = true;
      }
   }

   public void mergeAutopilotFromCurrentState(MinecraftClient mc) {
      if (mc.player != null) {
         this.mergeLatchFromAutoWalk();
         if (this.bodyFollowsKeys.get()) {
            GameOptions o = mc.options;
            PlayerInput ki = mc.player.input.playerInput;
            this.latchedForward = this.latchedForward | (o.forwardKey.isPressed() || ki.forward());
            this.latchedBack = this.latchedBack | (o.backKey.isPressed() || ki.backward());
            this.latchedLeft = this.latchedLeft | (o.leftKey.isPressed() || ki.left());
            this.latchedRight = this.latchedRight | (o.rightKey.isPressed() || ki.right());
            this.latchedJump = this.latchedJump | (o.jumpKey.isPressed() || ki.jump());
            this.latchedSneak = this.latchedSneak | (o.sneakKey.isPressed() || ki.sneak());
            this.latchedSprint = this.latchedSprint | (o.sprintKey.isPressed() || ki.sprint());
         } else {
            this.latchedSneak = this.latchedSneak | mc.player.isSneaking();
         }

         this.mergeLatchFromLivingSpeed(mc.player);
         this.mergeVelocityIntoLatch(mc.player);
      }
   }

   private void mergeLatchFromLivingSpeed(ClientPlayerEntity player) {
      if (player != null) {
         float fs = player.forwardSpeed;
         float ss = player.sidewaysSpeed;
         if (fs > 0.015F) {
            this.latchedForward = true;
         }

         if (fs < -0.015F) {
            this.latchedBack = true;
         }

         if (ss > 0.015F) {
            this.latchedLeft = true;
         }

         if (ss < -0.015F) {
            this.latchedRight = true;
         }
      }
   }

   private void mergeVelocityIntoLatch(ClientPlayerEntity player) {
      Vec3d vel = player.getVelocity();
      double vx = vel.x;
      double vz = vel.z;
      if (!(vx * vx + vz * vz < 1.0E-10)) {
         Vec3d flatLook = flatLook(player.getYaw());
         double dot = vx * flatLook.x + vz * flatLook.z;
         double perp = vx * -flatLook.z + vz * flatLook.x;
         if (Math.abs(dot) >= Math.abs(perp)) {
            if (dot > 0.008) {
               this.latchedForward = true;
            } else if (dot < -0.008) {
               this.latchedBack = true;
            }
         } else if (perp > 0.008) {
            this.latchedLeft = true;
         } else if (perp < -0.008) {
            this.latchedRight = true;
         }
      }
   }

   private void maybeLatchWalkFromVelocity(MinecraftClient mc) {
      if (mc.player != null) {
         if (!this.latchedForward && !this.latchedBack && !this.latchedLeft && !this.latchedRight) {
            Vec3d vel = mc.player.getVelocity();
            double vx = vel.x;
            double vz = vel.z;
            if (!(vx * vx + vz * vz < 1.0E-8)) {
               Vec3d flatLook = flatLook(mc.player.getYaw());
               double dot = vx * flatLook.x + vz * flatLook.z;
               double perp = vx * -flatLook.z + vz * flatLook.x;
               if (Math.abs(dot) > Math.abs(perp)) {
                  if (dot > 0.008) {
                     this.latchedForward = true;
                  } else if (dot < -0.008) {
                     this.latchedBack = true;
                  }
               } else if (perp > 0.008) {
                  this.latchedLeft = true;
               } else if (perp < -0.008) {
                  this.latchedRight = true;
               }
            }
         }
      }
   }

   private static Vec3d flatLook(float yawDeg) {
      double yawRad = Math.toRadians(yawDeg);
      return new Vec3d(-Math.sin(yawRad), 0.0, Math.cos(yawRad));
   }

   private void clearMovementLatches() {
      this.latchedForward = this.latchedBack = this.latchedLeft = this.latchedRight = false;
      this.latchedJump = this.latchedSneak = this.latchedSprint = false;
      this.autopilotWarmupTicks = 0;
   }

   @Override
   public void onTick() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (this.isEnabled()) {
         this.tryCompleteActivation(client);
         if (this.active && client.player != null) {
            if (this.autopilotWarmupTicks > 0) {
               this.autopilotWarmupTicks--;
               this.mergeAutopilotFromCurrentState(client);
            }

            this.prevX = this.currentX;
            this.prevY = this.currentY;
            this.prevZ = this.currentZ;
            this.prevYaw = this.currentYaw;
            this.prevPitch = this.currentPitch;
            float spd = this.speed.getFloat();
            float vMul = this.verticalMultiplier.getFloat();
            float moveFactor = this.smoothing.get() ? 0.5F : 1.0F;
            GameOptions o = client.options;
            double forward = 0.0;
            double strafe = 0.0;
            double vertical = 0.0;
            if (o.forwardKey.isPressed()) {
               forward++;
            }

            if (o.backKey.isPressed()) {
               forward--;
            }

            if (o.leftKey.isPressed()) {
               strafe++;
            }

            if (o.rightKey.isPressed()) {
               strafe--;
            }

            if (o.jumpKey.isPressed()) {
               vertical++;
            }

            if (o.sneakKey.isPressed()) {
               vertical--;
            }

            ClientPlayerEntity p = client.player;
            if (p.getAbilities().creativeMode) {
               p.getAbilities().flying = false;
            }

            double yawRad = Math.toRadians(this.currentYaw);
            double mx = -Math.sin(yawRad) * forward * spd + Math.cos(yawRad) * strafe * spd;
            double mz = Math.cos(yawRad) * forward * spd + Math.sin(yawRad) * strafe * spd;
            double my = vertical * spd * vMul;
            this.currentX += mx * moveFactor;
            this.currentY += my * moveFactor;
            this.currentZ += mz * moveFactor;
            this.syncFreecamChunkLoading(client);
         }
      }
   }

   public static void reapplyBodyInput(MinecraftClient client) {
      FreecamModule f = instance;
      if (f != null && f.isActive() && client.player != null) {
         ClientPlayerEntity player = client.player;
         GameOptions o = client.options;
         boolean mergePhysical = f.bodyFollowsKeys.get();
         boolean fwd = f.latchedForward || mergePhysical && o.forwardKey.isPressed();
         boolean back = f.latchedBack || mergePhysical && o.backKey.isPressed();
         boolean left = f.latchedLeft || mergePhysical && o.leftKey.isPressed();
         boolean right = f.latchedRight || mergePhysical && o.rightKey.isPressed();
         boolean jump = f.latchedJump;
         boolean sneak = f.latchedSneak || mergePhysical && o.sneakKey.isPressed();
         boolean sprint = f.latchedSprint || mergePhysical && o.sprintKey.isPressed();
         if (mergePhysical) {
            if (o.backKey.isPressed()) {
               f.latchedForward = false;
            }

            if (o.forwardKey.isPressed()) {
               f.latchedBack = false;
            }

            if (o.rightKey.isPressed()) {
               f.latchedLeft = false;
            }

            if (o.leftKey.isPressed()) {
               f.latchedRight = false;
            }
         }

         player.input.playerInput = new PlayerInput(fwd, back, left, right, jump, sneak, sprint);
         float sx = (left ? 1.0F : 0.0F) - (right ? 1.0F : 0.0F);
         float sz = (fwd ? 1.0F : 0.0F) - (back ? 1.0F : 0.0F);
         Vec2f moveVector = new Vec2f(sx, sz).normalize();
         ((ClientInputAccessor)player.input).FeClient$setMoveVector(moveVector);
         player.setSneaking(sneak);
      }
   }

   public boolean hasLatchedLocomotion() {
      return this.latchedForward || this.latchedBack || this.latchedLeft || this.latchedRight || this.latchedJump || this.latchedSneak;
   }

   private void syncFreecamChunkLoading(MinecraftClient client) {
      if (this.active && client.world != null && client.player != null) {
         ClientChunkManager ccm = client.world.getChunkManager();
         ChunkPos camChunk = new ChunkPos((int)Math.floor(this.currentX) >> 4, (int)Math.floor(this.currentZ) >> 4);
         ChunkPos bodyChunk = client.player.getChunkPos();
         ChunkPos centerChunk = camChunk;
         int sepCamBody = Math.max(Math.abs(camChunk.x - bodyChunk.x), Math.abs(camChunk.z - bodyChunk.z));
         if (sepCamBody + 4 > 32) {
            int mx = (camChunk.x + bodyChunk.x) / 2;
            int mz = (camChunk.z + bodyChunk.z) / 2;
            centerChunk = new ChunkPos(mx, mz);
         }

         int spread = Math.max(
            Math.max(Math.abs(centerChunk.x - camChunk.x), Math.abs(centerChunk.z - camChunk.z)),
            Math.max(Math.abs(centerChunk.x - bodyChunk.x), Math.abs(centerChunk.z - bodyChunk.z))
         );
         int userDist = Math.min(32, Math.max(2, Math.round(this.freecamChunkDistance.getFloat())));
         int dist = Math.min(32, Math.max(userDist, spread + 4));
         if (dist != this.lastSyncedLoadDistance) {
            ccm.updateLoadDistance(dist);
            this.lastSyncedLoadDistance = dist;
         }

         if (this.lastSyncedCamChunk == null || centerChunk.x != this.lastSyncedCamChunk.x || centerChunk.z != this.lastSyncedCamChunk.z) {
            ccm.setChunkMapCenter(centerChunk.x, centerChunk.z);
            this.lastSyncedCamChunk = centerChunk;
            if (client.worldRenderer != null) {
               client.worldRenderer.scheduleTerrainUpdate();
            }
         }
      }
   }

   private void restoreVanillaChunkLoading(MinecraftClient client) {
      this.lastSyncedCamChunk = null;
      this.lastSyncedLoadDistance = Integer.MIN_VALUE;
      if (client.world != null) {
         ClientChunkManager ccm = client.world.getChunkManager();
         if (client.player != null) {
            ChunkPos p = client.player.getChunkPos();
            ccm.setChunkMapCenter(p.x, p.z);
         }

         ccm.updateLoadDistance(client.options.getClampedViewDistance());
         if (client.worldRenderer != null) {
            client.worldRenderer.scheduleTerrainUpdate();
         }
      }
   }

   private void applyViewOnlyClientState(MinecraftClient mc) {
      this.savedSmartCull = mc.chunkCullingEnabled;
      mc.chunkCullingEnabled = false;
   }

   private void restoreViewOnlyClientState(MinecraftClient mc) {
      mc.chunkCullingEnabled = this.savedSmartCull;
      if (mc.interactionManager != null) {
         mc.interactionManager.cancelBlockBreaking();
      }
   }

   public boolean isActive() {
      return this.isEnabled() && this.active;
   }

   public boolean isShowPlayerModel() {
      return this.showPlayerModel.get();
   }

   public boolean isShowHands() {
      return this.showHands.get();
   }

   public boolean renderHands() {
      return !this.isActive() || this.isShowHands();
   }

   public boolean wasHoldingSneak() {
      return this.latchedSneak;
   }

   public double getInterpolatedX(float tickDelta) {
      return MathHelper.lerp(tickDelta, this.prevX, this.currentX);
   }

   public double getInterpolatedY(float tickDelta) {
      return MathHelper.lerp(tickDelta, this.prevY, this.currentY);
   }

   public double getInterpolatedZ(float tickDelta) {
      return MathHelper.lerp(tickDelta, this.prevZ, this.currentZ);
   }

   public float getInterpolatedYaw(float tickDelta) {
      return MathHelper.lerp(tickDelta, this.prevYaw, this.currentYaw);
   }

   public float getInterpolatedPitch(float tickDelta) {
      return MathHelper.lerp(tickDelta, this.prevPitch, this.currentPitch);
   }

   public Vec3d getInterpolatedPos(float tickDelta) {
      return new Vec3d(this.getInterpolatedX(tickDelta), this.getInterpolatedY(tickDelta), this.getInterpolatedZ(tickDelta));
   }

   public void setRotation(float yaw, float pitch) {
      this.currentYaw = yaw;
      this.currentPitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
   }

   public float getCurrentYaw() {
      return this.currentYaw;
   }

   public float getCurrentPitch() {
      return this.currentPitch;
   }

   public float getLookSensitivity() {
      return this.lookSensitivity.getFloat();
   }
}



