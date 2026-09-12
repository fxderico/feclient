package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AntiVoidModule extends Module {
   private final NumberSetting triggerY = new NumberSetting("TriggerY", -8.0, -64.0, 0.0, 1.0);
   private final BooleanSetting usePearl = new BooleanSetting("UsePearl", false);
   private final NumberSetting teleportSpeed = new NumberSetting("TeleportSpeed", 1.0, 0.1, 5.0, 0.1);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private Vec3d class243;
   private boolean bool;

   public AntiVoidModule() {
      super("AntiVoid", "Rescue from void falls", Category.MOVEMENT);
      this.run6(new Setting[]{this.triggerY, this.usePearl, this.teleportSpeed});
   }

   @Override
   public void run() {
      this.class243 = null;
      this.bool = false;
      this.antiVoidModuleHelper.run();
   }

   @Override
   public void run2() {
      if (this.bool) {
         AntiAFKModuleUtil.run2();
         this.bool = false;
      }

      this.class243 = null;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         double var1 = class310.world.getBottomY();
         double var3 = var1 + this.triggerY.getValue();
         double var5 = class310.player.getY();
         if (class310.player.isOnGround() && !class310.player.isTouchingWater() && !class310.player.isInLava() && var5 > var3 + 1.0) {
            this.class243 = class310.player.getEntityPos();
         }

         if (this.bool) {
            AntiAFKModuleUtil.run2();
            this.bool = false;
         }

         if (!(var5 >= var3)) {
            if (this.class243 == null) {
               this.run4();
            } else if (!this.usePearl.getValue() || !this.isEnabled()) {
               this.run5();
            }
         }
      }
   }

   private void run4() {
      Vec3d var1 = class310.player.getVelocity();
      class310.player.setVelocity(var1.x * 0.5, 0.0, var1.z * 0.5);
      class310.player.fallDistance = 0.0;
   }

   private void run5() {
      double var1 = this.teleportSpeed.getValue();
      double var3 = this.class243.x - class310.player.getX();
      double var5 = this.class243.z - class310.player.getZ();
      double var7 = this.class243.y + 1.0 - class310.player.getY();
      double var9 = MathHelper.clamp(var3, -var1, var1);
      double var11 = MathHelper.clamp(var5, -var1, var1);
      double var13 = var7 > 0.0 ? Math.min(var7, var1) : 0.0;
      class310.player.setVelocity(var9, var13, var11);
      class310.player.fallDistance = 0.0;
   }

   public boolean isEnabled() {
      if (!this.antiVoidModuleHelper.check(800L)) {
         return false;
      } else {
         int var1 = this.getInt3();
         if (var1 < 0) {
            return false;
         } else if (class310.interactionManager == null) {
            return false;
         } else {
            int var2 = class310.player.getInventory().getSelectedSlot();
            class310.player.getInventory().setSelectedSlot(var1);
            Vec3d var3 = new Vec3d(this.class243.x, this.class243.y + 2.0, this.class243.z);
            Vec3d var4 = class310.player.getEyePos();
            double var5 = var3.x - var4.x;
            double var7 = var3.y - var4.y;
            double var9 = var3.z - var4.z;
            double var11 = Math.sqrt(var5 * var5 + var9 * var9);
            float var13 = (float)Math.toDegrees(Math.atan2(var9, var5)) - 90.0F;
            float var14 = (float)MathHelper.clamp(-Math.toDegrees(Math.atan2(var7, var11)), -90.0, 90.0);
            AntiAFKModuleUtil.run(var13, var14);
            this.bool = true;
            class310.interactionManager.interactItem(class310.player, Hand.MAIN_HAND);
            this.antiVoidModuleHelper.run();
            class310.execute(() -> {});
            return true;
         }
      }
   }

   private int getInt3() {
      for (int var1 = 0; var1 < 9; var1++) {
         ItemStack var2 = class310.player.getInventory().getStack(var1);
         if (!var2.isEmpty() && var2.getItem() == Items.ENDER_PEARL) {
            return var1;
         }
      }

      return -1;
   }

   @Override
   public String getString3() {
      return this.class243 == null ? "§7no-anchor" : null;
   }

   public void run7(int var0) {
      if (class310.player != null) {
         class310.player.getInventory().setSelectedSlot(var0);
      }
   }
}

