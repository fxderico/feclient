package dev.fede.water.module.modules.misc;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.module.modules.client.WaterPlus;
import dev.fede.water.setting.ModeSetting;
import dev.fede.water.setting.Setting;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public final class Freelook extends Module {
   private static final float MIN_DISTANCE = 1.0F;
   private static final float MAX_DISTANCE = 15.0F;
   private static final float MIN_SENSITIVITY = 0.1F;
   private static final float MAX_SENSITIVITY = 3.0F;
   public static Freelook instance;
   private final ModeSetting activationMode = new ModeSetting("Mode", "Hold", new String[]{"Activation Mode"}, "Hold", "Toggle");
   private final Setting<Float> distance = new Setting<>("Distance", 4.0F, 1.0F, 15.0F);
   private final Setting<Boolean> smoothZoom = new Setting<>("Smooth Zoom", true);
   private final Setting<Integer> zoomTime = new Setting<>("Zoom Time", 280, 80, 800);
   private final Setting<Boolean> wallClip = new Setting<>("Wall Clip", true);
   private final Setting<Float> sensitivity = new Setting<>("Sensitivity", 1.0F, 0.1F, 3.0F);
   private final Setting<Boolean> invertY = new Setting<>("Invert Y", false);
   private boolean active;
   private Perspective savedPerspective;
   private boolean savedChunkCullingEnabled = true;
   private float cameraYaw;
   private float cameraPitch;
   private long activationMs;

   public Freelook() {
      super("FreeLook", Category.c);
      instance = this;
      this.addSetting(this.activationMode);
      this.addSetting(this.distance);
      this.addSetting(this.smoothZoom);
      this.addSetting(this.zoomTime);
      this.addSetting(this.wallClip);
      this.addSetting(this.sensitivity);
      this.addSetting(this.invertY);
   }

   @Override
   public void onEnable() {
      this.active = false;
      this.savedPerspective = null;
      this.savedChunkCullingEnabled = true;
   }

   @Override
   public void onDisable() {
      this.deactivateCamera();
   }

   @Override
   public void onTick() {
      if (mc.player != null && mc.options != null && mc.getWindow() != null) {
         if (this.isHoldMode()) {
            int var1 = this.getBind();
            boolean var2 = var1 != 0 && GLFW.glfwGetKey(mc.getWindow().getHandle(), var1) == 1;
            if (var2) {
               this.activateCamera();
            } else {
               this.deactivateCamera();
            }
         } else if (this.active) {
            this.ensureCameraState();
         }
      } else {
         this.deactivateCamera();
      }
   }

   @Override
   public void onBindPressed() {
      if (!this.isHoldMode()) {
         if (!this.isEnabled()) {
            this.toggle();
         } else {
            if (this.active) {
               this.deactivateCamera();
            } else {
               this.activateCamera();
            }
         }
      }
   }

   public boolean isCameraActive() {
      return this.isEnabled() && this.active && mc.player != null;
   }

   public void consumeMouseDelta(double deltaX, double deltaY) {
      if (this.isCameraActive()) {
         double var5 = this.invertY.getValue() ? -1.0 : 1.0;
         double var7 = 0.15 * this.getSensitivity();
         this.cameraYaw = MathHelper.wrapDegrees(this.cameraYaw + (float)(deltaX * var7));
         this.cameraPitch = MathHelper.clamp(this.cameraPitch + (float)(deltaY * var7 * var5), -90.0F, 90.0F);
      }
   }

   public float getCameraYaw() {
      return this.cameraYaw;
   }

   public float getCameraPitch() {
      return this.cameraPitch;
   }

   public float getDistance() {
      float var1 = MathHelper.clamp(this.distance.getValue(), 1.0F, 15.0F);
      if (this.smoothZoom.getValue() && this.activationMs > 0L) {
         float var2 = Math.max(80, this.zoomTime.getValue());
         var2 = MathHelper.clamp((float)(System.currentTimeMillis() - this.activationMs) / var2, 0.0F, 1.0F);
         var2 = 1.0F - (float)Math.pow(1.0F - var2, 3.0);
         return MathHelper.lerp(var2, 1.0F, var1);
      } else {
         return var1;
      }
   }

   public boolean shouldWallClip() {
      return this.wallClip.getValue();
   }

   public float getSensitivity() {
      return MathHelper.clamp(this.sensitivity.getValue(), 0.1F, 3.0F);
   }

   private boolean isHoldMode() {
      return this.activationMode.d("Hold");
   }

   private void activateCamera() {
      if (!this.active && mc.player != null && mc.options != null) {
         this.savedPerspective = mc.options.getPerspective();
         this.savedChunkCullingEnabled = mc.chunkCullingEnabled;
         mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
         mc.chunkCullingEnabled = false;
         this.cameraYaw = mc.player.getYaw();
         this.cameraPitch = mc.player.getPitch();
         this.activationMs = System.currentTimeMillis();
         this.active = true;
         this.pushNotification(true);
      } else {
         this.ensureCameraState();
      }
   }

   private void ensureCameraState() {
      if (this.active && mc.options != null) {
         if (mc.options.getPerspective().isFirstPerson() || mc.options.getPerspective().isFrontView()) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            mc.chunkCullingEnabled = false;
         }
      }
   }

   private void deactivateCamera() {
      if (this.active) {
         this.active = false;
         this.activationMs = 0L;
         if (mc.options != null) {
            mc.options.setPerspective(this.savedPerspective == null ? Perspective.FIRST_PERSON : this.savedPerspective);
         }

         mc.chunkCullingEnabled = this.savedChunkCullingEnabled;
         this.pushNotification(false);
      }
   }

   private void pushNotification(boolean enabled) {
      try {
         if (WaterPlus.notificationsEnabled()) {
      // toast suppressed
         }
      } catch (Exception var2) {
      }
   }

   static String _c2cc8e86a63() {
      return "2";
   }
}

