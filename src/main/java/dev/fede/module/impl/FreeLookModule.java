package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class FreeLookModule extends Module {
   private static FreeLookModule instance;
   public final ModeSetting mode = this.addSetting(new ModeSetting("Mode", "Which entity the mouse rotates.", "Player", "Player", "Camera"));
   public final BooleanSetting togglePerspective = this.addSetting(new BooleanSetting("Toggle Perspective", "Switch to third person on toggle.", true));
   public final BooleanSetting throughWalls = this.addSetting(
      new BooleanSetting("Through Walls", "See through walls — the third-person camera ignores wall collision.", false)
   );
   public final SliderSetting sensitivity = this.addSetting(
      new SliderSetting("Camera Sensitivity", "How fast the camera moves in Camera mode.", 8.0, 0.0, 10.0, 0.1)
   );
   public final BooleanSetting arrows = this.addSetting(
      new BooleanSetting("Arrows Control Opposite", "Control the other entity's rotation with the arrow keys.", true)
   );
   public final SliderSetting arrowSpeed = this.addSetting(new SliderSetting("Arrow Speed", "Rotation speed with the arrow keys.", 4.0, 0.0, 10.0, 0.5));
   private float cameraYaw;
   private float cameraPitch;
   private Perspective prePers;

   public FreeLookModule() {
      super("FreeLook", "Allows more rotation options in third person.", Category.MOVEMENT);
      instance = this;
   }

   public static FreeLookModule get() {
      return instance;
   }

   @Override
   protected void onEnable() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         this.cameraYaw = mc.player.getYaw();
         this.cameraPitch = mc.player.getPitch();
         this.prePers = mc.options.getPerspective();
         if (this.prePers != Perspective.THIRD_PERSON_BACK && this.togglePerspective.get()) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
         }
      }
   }

   @Override
   protected void onDisable() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (this.prePers != null && mc.options.getPerspective() != this.prePers && this.togglePerspective.get()) {
         mc.options.setPerspective(this.prePers);
      }
   }

   @Override
   public void onTick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         if (this.arrows.get()) {
            long win = mc.getWindow().getHandle();
            boolean left = GLFW.glfwGetKey(win, 263) == 1;
            boolean right = GLFW.glfwGetKey(win, 262) == 1;
            boolean up = GLFW.glfwGetKey(win, 265) == 1;
            boolean down = GLFW.glfwGetKey(win, 264) == 1;
            int iterations = (int)(this.arrowSpeed.get() * 2.0);

            for (int i = 0; i < iterations; i++) {
               if (this.mode.check("Player")) {
                  if (left) {
                     this.cameraYaw -= 0.5F;
                  }

                  if (right) {
                     this.cameraYaw += 0.5F;
                  }

                  if (up) {
                     this.cameraPitch -= 0.5F;
                  }

                  if (down) {
                     this.cameraPitch += 0.5F;
                  }
               } else {
                  float yaw = mc.player.getYaw();
                  float pitch = mc.player.getPitch();
                  if (left) {
                     yaw -= 0.5F;
                  }

                  if (right) {
                     yaw += 0.5F;
                  }

                  if (up) {
                     pitch -= 0.5F;
                  }

                  if (down) {
                     pitch += 0.5F;
                  }

                  mc.player.setYaw(yaw);
                  mc.player.setPitch(pitch);
               }
            }
         }

         mc.player.setPitch(MathHelper.clamp(mc.player.getPitch(), -90.0F, 90.0F));
         this.cameraPitch = MathHelper.clamp(this.cameraPitch, -90.0F, 90.0F);
      }
   }

   public boolean isActive() {
      return this.isEnabled() && MinecraftClient.getInstance().player != null;
   }

   public boolean seeThroughWalls() {
      return this.isActive() && this.throughWalls.get();
   }

   public boolean cameraMode() {
      return this.isActive() && this.mode.check("Camera");
   }

   public boolean playerMode() {
      return this.isActive() && MinecraftClient.getInstance().options.getPerspective() == Perspective.THIRD_PERSON_BACK && this.mode.check("Player");
   }

   public void addCameraLook(double deltaX, double deltaY) {
      float sens = this.sensitivity.getFloat();
      if (sens <= 0.0F) {
         sens = 1.0F;
      }

      this.cameraYaw += (float)(deltaX / sens);
      this.cameraPitch += (float)(deltaY / sens);
      if (Math.abs(this.cameraPitch) > 90.0F) {
         this.cameraPitch = this.cameraPitch > 0.0F ? 90.0F : -90.0F;
      }
   }

   public float getCameraYaw() {
      return this.cameraYaw;
   }

   public float getCameraPitch() {
      return this.cameraPitch;
   }
}

