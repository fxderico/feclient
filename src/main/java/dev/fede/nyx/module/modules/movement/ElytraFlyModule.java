package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class ElytraFlyModule extends Module {
   private static final double doubleVal = 0.05;
   private final ModeSetting mode = new ModeSetting("Mode", "Boost", "Assist", "Boost", "Fly", "Cruise");
   private final NumberSetting speed = new NumberSetting("Speed", 1.2, 0.1, 5.0, 0.1);
   private final NumberSetting verticalSpeed = new NumberSetting("VerticalSpeed", 1.0, 0.1, 5.0, 0.1);
   private final BooleanSetting requireHoldingElytra = new BooleanSetting("RequireHoldingElytra", true);
   private double doubleVal2;
   private boolean bool;

   public ElytraFlyModule() {
      super("ElytraFly", "Enhanced elytra glide with hover / boost / fly / cruise", Category.MOVEMENT);
      this.run6(new Setting[]{this.mode, this.speed, this.verticalSpeed, this.requireHoldingElytra});
   }

   @Override
   public void run() {
      this.bool = false;
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null) {
         this.doubleVal2 = var1.getY();
      }
   }

   @Override
   public void run2() {
      this.bool = false;
   }

   @Override
   public void run3() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null) {
         if (!this.requireHoldingElytra.getValue() || check(var1)) {
            String var2 = this.mode.getMode();
            switch (var2.hashCode()) {
               case 70739:
                  if (var2.equals("Fly")) {
                     this.run6(var1);
                  }
                  break;
               case 64369539:
                  if (var2.equals("Boost")) {
                     this.run5(var1);
                  }
                  break;
               case 1970630281:
                  if (var2.equals("Assist")) {
                     this.run4(var1);
                  }
                  break;
               case 2027024629:
                  if (var2.equals("Cruise")) {
                     this.run7(var1);
                  }
            }
         }
      }
   }

   private void run4(ClientPlayerEntity var1) {
      if (var1.isGliding()) {
         if (class310.options.sneakKey.isPressed()) {
            Vec3d var2 = var1.getVelocity();
            if (var2.y < 0.0) {
               var1.setVelocity(var2.x, 0.0, var2.z);
            }
         }
      }
   }

   private void run5(ClientPlayerEntity var1) {
      if (var1.isGliding()) {
         if (class310.options.forwardKey.isPressed()) {
            double var2 = this.speed.getValue();
            float var4 = var1.getYaw();
            float var5 = var1.getPitch();
            double var6 = Math.toRadians(var4);
            double var8 = Math.toRadians(var5);
            double var10 = -Math.sin(var6) * Math.cos(var8);
            double var12 = -Math.sin(var8);
            double var14 = Math.cos(var6) * Math.cos(var8);
            Vec3d var16 = var1.getVelocity();
            var1.setVelocity(var16.x + var10 * var2 * 0.1, var16.y + var12 * var2 * 0.1, var16.z + var14 * var2 * 0.1);
         }
      }
   }

   private void run6(ClientPlayerEntity var1) {
      // this was the only one of the four submodes (Assist/Boost/Cruise all
      // gate on isGliding()) that forced velocity unconditionally. requireHoldingElytra
      // only checks the chestplate slot has an elytra item equipped, not that
      // it's actually deployed -- so grounded with an elytra worn, this used
      // to override velocity to a non-gravity value every tick regardless,
      // fighting ground collision and standing out as forced non-gravity
      // velocity on a non-airborne entity.
      if (!var1.isGliding()) {
         return;
      }

      double var4 = this.verticalSpeed.getValue();
      double var2;
      if (class310.options.jumpKey.isPressed()) {
         var2 = var4;
      } else if (class310.options.sneakKey.isPressed()) {
         var2 = -var4;
      } else {
         var2 = 0.0;
      }

      float var6 = var1.forwardSpeed;
      float var7 = var1.sidewaysSpeed;
      double var8 = 0.0;
      double var10 = 0.0;
      if (var6 != 0.0F || var7 != 0.0F) {
         double var12 = Math.toRadians(var1.getYaw());
         double var14 = -Math.sin(var12) * var6 + Math.cos(var12) * var7;
         double var16 = Math.cos(var12) * var6 + Math.sin(var12) * var7;
         double var18 = Math.sqrt(var14 * var14 + var16 * var16);
         if (var18 > 0.0) {
            double var20 = this.speed.getValue();
            var8 = var14 / var18 * var20;
            var10 = var16 / var18 * var20;
         }
      }

      var1.setVelocity(var8, var2, var10);
      var1.fallDistance = 0.0;
   }

   private void run7(ClientPlayerEntity var1) {
      if (var1.isGliding()) {
         if (!this.bool) {
            this.doubleVal2 = var1.getY();
            this.bool = true;
         }

         double var2 = this.speed.getValue();
         double var4 = this.verticalSpeed.getValue();
         float var6 = var1.getYaw();
         double var7 = Math.toRadians(var6);
         double var9 = -Math.sin(var7) * var2;
         double var11 = Math.cos(var7) * var2;
         double var13 = this.doubleVal2 - var1.getY();
         double var15;
         if (Math.abs(var13) < 0.05) {
            var15 = 0.0;
         } else {
            var15 = Math.max(-var4, Math.min(var4, var13 * 0.5));
         }

         var1.setVelocity(var9, var15, var11);
         var1.fallDistance = 0.0;
      }
   }

   private static boolean check(ClientPlayerEntity var0) {
      ItemStack var1 = var0.getEquippedStack(EquipmentSlot.CHEST);
      return var1 != null && var1.isOf(Items.ELYTRA);
   }

   @Override
   public String getString3() {
      return "§7" + this.mode.getMode();
   }
}

