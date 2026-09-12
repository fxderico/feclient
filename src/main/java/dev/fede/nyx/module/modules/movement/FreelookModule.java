package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.mixin.SmartCullAccessor;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public final class FreelookModule extends Module {
   public static volatile boolean bool;
   public static float floatVal;
   public static float floatVal2;
   private float floatVal3;
   private float floatVal4;
   private Perspective class5498 = Perspective.FIRST_PERSON;
   private boolean bool2 = true;
   private final BooleanSetting holdToActivate = new BooleanSetting("HoldToActivate", true);
   private final BooleanSetting thirdPerson = new BooleanSetting("ThirdPerson", true);
   private final BooleanSetting frontView = new BooleanSetting("FrontView", false);

   public FreelookModule() {
      super("Freelook", "Orbit the camera around the player — body stays put", Category.MOVEMENT);
      this.run6(new Setting[]{this.holdToActivate, this.thirdPerson, this.frontView});
   }

   @Override
   public void run() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null && class310.world != null && class310.options != null) {
         this.floatVal3 = var1.getYaw();
         this.floatVal4 = var1.getPitch();
         floatVal = this.floatVal3;
         floatVal2 = this.floatVal4;
         this.class5498 = class310.options.getPerspective();
         if (this.thirdPerson.getValue()) {
            class310.options.setPerspective(this.frontView.getValue() ? Perspective.THIRD_PERSON_FRONT : Perspective.THIRD_PERSON_BACK);
         }

         try {
            SmartCullAccessor var2 = (SmartCullAccessor)class310;
            this.bool2 = var2.nyx$getSmartCull();
            var2.nyx$setSmartCull(false);
         } catch (Throwable var3) {
         }

         bool = true;
      } else {
         bool = false;
         this.run5(false);
      }
   }

   @Override
   public void run2() {
      bool = false;
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null) {
         var1.setYaw(this.floatVal3);
         var1.setPitch(this.floatVal4);
         var1.lastYaw = this.floatVal3;
         var1.lastPitch = this.floatVal4;
         var1.headYaw = this.floatVal3;
         var1.lastHeadYaw = this.floatVal3;
         var1.bodyYaw = this.floatVal3;
         var1.lastBodyYaw = this.floatVal3;
      }

      if (class310.options != null && this.class5498 != null) {
         class310.options.setPerspective(this.class5498);
      }

      try {
         ((SmartCullAccessor)class310).nyx$setSmartCull(this.bool2);
      } catch (Throwable var3) {
      }
   }

   @Override
   public void run3() {
      if (bool) {
         ClientPlayerEntity var1 = class310.player;
         if (var1 != null && class310.world != null) {
            float var2 = var1.getYaw() - this.floatVal3;
            float var3 = var1.getPitch() - this.floatVal4;
            floatVal += var2;
            floatVal2 = MathHelper.clamp(floatVal2 + var3, -89.9F, 89.9F);
            var1.setYaw(this.floatVal3);
            var1.setPitch(this.floatVal4);
            var1.lastYaw = this.floatVal3;
            var1.lastPitch = this.floatVal4;
            var1.headYaw = this.floatVal3;
            var1.lastHeadYaw = this.floatVal3;
            var1.bodyYaw = this.floatVal3;
            var1.lastBodyYaw = this.floatVal3;
            if (this.holdToActivate.getValue() && this.getInt() != 0) {
               long var4 = class310.getWindow() != null ? class310.getWindow().getHandle() : 0L;
               if (var4 != 0L && GLFW.glfwGetKey(var4, this.getInt()) == 0) {
                  this.run5(false);
               }
            }
         }
      }
   }

   public static float getFloat() {
      return floatVal;
   }

   public static float getFloat2() {
      return floatVal2;
   }
}

