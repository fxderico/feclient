package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;

public class FlyModule extends Module {
   private static final int intVal = 20;
   private static final double doubleVal = 0.01;
   private static final float floatVal = 0.05F;
   private final ModeSetting mode = new ModeSetting("Mode", "Motion", "Vanilla", "Creative", "Motion", "Packet");
   private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.1, 10.0, 0.1);
   private final NumberSetting vertical = new NumberSetting("Vertical", 1.0, 0.1, 10.0, 0.1);
   private final BooleanSetting antiKick = new BooleanSetting("AntiKick", true);
   private final BooleanSetting restoreOnDisable = new BooleanSetting("RestoreOnDisable", true);
   private boolean bool;
   private boolean bool2;
   private float floatVal2;
   private boolean bool3;
   private int intVal2;

   public FlyModule() {
      super("Fly", "Creative-style flight", Category.MOVEMENT);
      this.run6(new Setting[]{this.mode, this.speed, this.vertical, this.antiKick, this.restoreOnDisable});
   }

   @Override
   public void run() {
      ClientPlayerEntity var1 = class310.player;
      this.intVal2 = 0;
      if (var1 == null) {
         this.bool3 = false;
      } else {
         PlayerAbilities var2 = var1.getAbilities();
         this.bool = var2.flying;
         this.bool2 = var2.allowFlying;
         this.floatVal2 = var2.getFlySpeed();
         this.bool3 = true;
      }
   }

   @Override
   public void run2() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null) {
         var1.setVelocity(0.0, 0.0, 0.0);
         if (this.restoreOnDisable.getValue() && this.bool3) {
            PlayerAbilities var2 = var1.getAbilities();
            var2.flying = this.bool;
            var2.allowFlying = this.bool2;
            var2.setFlySpeed(this.floatVal2);
         }
      }
   }

   @Override
   public void run3() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null) {
         String var2 = this.mode.getMode();
         switch (var2.hashCode()) {
            case -1984451626:
               if (var2.equals("Motion")) {
                  this.run5(var1, false);
               }
               break;
            case -1911998296:
               if (var2.equals("Packet")) {
                  this.run5(var1, true);
               }
               break;
            case 1885066191:
               if (var2.equals("Creative")) {
                  this.run4(var1, true);
               }
               break;
            case 1897755483:
               if (var2.equals("Vanilla")) {
                  this.run4(var1, false);
               }
         }

         var1.fallDistance = 0.0;
      }
   }

   private void run4(ClientPlayerEntity var1, boolean var2) {
      PlayerAbilities var3 = var1.getAbilities();
      if (var2) {
         var3.allowFlying = true;
      }

      var3.flying = true;
      var3.setFlySpeed((float)(0.05F * this.speed.getValue()));
   }

   private void run5(ClientPlayerEntity var1, boolean var2) {
      double var5 = this.vertical.getValue();
      double var3;
      if (class310.options.jumpKey.isPressed()) {
         var3 = var5;
      } else if (class310.options.sneakKey.isPressed()) {
         var3 = -var5;
      } else if (this.antiKick.getValue()) {
         this.intVal2++;
         if (this.intVal2 >= 20) {
            this.intVal2 = 0;
            var3 = 0.01;
         } else {
            var3 = 0.0;
         }
      } else {
         var3 = 0.0;
      }

      float var7 = var1.forwardSpeed;
      float var8 = var1.sidewaysSpeed;
      double var9 = 0.0;
      double var11 = 0.0;
      if (var7 != 0.0F || var8 != 0.0F) {
         double var13 = Math.toRadians(var1.getYaw());
         double var15 = -Math.sin(var13) * var7 + Math.cos(var13) * var8;
         double var17 = Math.cos(var13) * var7 + Math.sin(var13) * var8;
         double var19 = Math.sqrt(var15 * var15 + var17 * var17);
         if (var19 > 0.0) {
            double var21 = this.speed.getValue();
            var9 = var15 / var19 * var21;
            var11 = var17 / var19 * var21;
         }
      }

      var1.setVelocity(var9, var3, var11);
      if (var2) {
         ClientPlayNetworkHandler var23 = var1.networkHandler;
         if (var23 != null) {
            var23.sendPacket(new OnGroundOnly(false, var1.horizontalCollision));
         }
      }
   }

   @Override
   public String getString3() {
      return "§7" + this.mode.getMode();
   }
}

