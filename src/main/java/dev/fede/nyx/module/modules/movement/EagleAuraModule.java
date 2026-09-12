package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EagleAuraModule extends Module {
   private static final double doubleVal = 0.5;
   private final BooleanSetting onlyHoldingBlock = new BooleanSetting("OnlyWhileHoldingBlock", false);
   private final NumberSetting sensitivity = new NumberSetting("Sensitivity", 0.3, 0.1, 1.0, 0.05);
   private boolean bool;

   public EagleAuraModule() {
      super("EagleAura", "Auto-sneak when walking near an edge", Category.MOVEMENT);
      this.run6(new Setting[]{this.onlyHoldingBlock, this.sensitivity});
   }

   @Override
   public void run() {
      this.bool = false;
   }

   @Override
   public void run2() {
      if (this.bool && class310.player != null) {
         class310.player.setSneaking(false);
         this.run5(false);
      }

      this.bool = false;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         if (!class310.player.isOnGround()) {
            this.run4();
         } else {
            if (this.onlyHoldingBlock.getValue()) {
               ItemStack var1 = class310.player.getMainHandStack();
               if (var1.isEmpty() || !(var1.getItem() instanceof BlockItem)) {
                  this.run4();
                  return;
               }
            }

            Vec3d var9 = class310.player.getVelocity();
            double var2 = this.sensitivity.getValue();
            double var4 = var9.x + (var9.x != 0.0 ? Math.signum(var9.x) * var2 : 0.0);
            double var6 = var9.z + (var9.z != 0.0 ? Math.signum(var9.z) * var2 : 0.0);
            boolean var8 = false;
            if (this.check(class310.player.getX() + var4, class310.player.getZ() + var6)) {
               var8 = true;
            } else if (var9.x != 0.0 && this.check(class310.player.getX() + var4, class310.player.getZ())) {
               var8 = true;
            } else if (var9.z != 0.0 && this.check(class310.player.getX(), class310.player.getZ() + var6)) {
               var8 = true;
            }

            if (!var8) {
               this.run4();
            } else {
               class310.player.setSneaking(true);
               if (!this.bool) {
                  this.run5(true);
                  this.bool = true;
               }

               class310.player.setVelocity(0.0, var9.y, 0.0);
            }
         }
      }
   }

   private boolean check(double var1, double var3) {
      BlockPos var5 = BlockPos.ofFloored(var1, class310.player.getY() - 0.5, var3);
      return class310.world.getBlockState(var5).isAir();
   }

   private void run4() {
      if (this.bool) {
         class310.player.setSneaking(false);
         this.run5(false);
         this.bool = false;
      }
   }

   public void run5_nf(boolean var1) {
      if (class310.player != null && class310.player.networkHandler != null) {
         class310.player.networkHandler.sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, var1, false)));
      }
   }
}

