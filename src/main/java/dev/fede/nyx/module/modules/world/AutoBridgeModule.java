package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoBridgeModule extends Module {
   private static final long longVal = 50L;
   private static final long longVal2 = 15L;
   private static final float floatVal = 78.0F;
   private static final double doubleVal = 0.42;
   private final NumberSetting distance = new NumberSetting("Distance", 1.0, 1.0, 5.0, 1.0);
   private final BooleanSetting sneakBridge = new BooleanSetting("SneakBridge", true);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private long longVal3 = 50L;
   private int intVal = -1;
   private boolean bool;

   public AutoBridgeModule() {
      super("AutoBridge", "Auto-places blocks forward to bridge across gaps", Category.WORLD);
      this.run6(new Setting[]{this.distance, this.sneakBridge});
   }

   @Override
   public void run2() {
      this.run3();
      this.run6();
      AntiAFKModuleUtil.run2();
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (this.antiVoidModuleHelper.check(this.longVal3)) {
            BlockPos var1 = class310.player.getBlockPos();
            BlockPos var2 = var1.down();
            if (class310.world.getBlockState(var2).isAir()) {
               AntiAFKModuleUtil.run2();
               this.run6();
            } else {
               double var3 = class310.player.getVelocity().horizontalLength();
               if (var3 > 0.42) {
                  AntiAFKModuleUtil.run2();
                  this.run6();
               } else {
                  Direction var5 = Direction.fromHorizontalDegrees(class310.player.getYaw());
                  int var6 = Math.max(1, this.distance.getValueInt());
                  BlockPos var7 = null;
                  int var8 = 0;

                  for (int var9 = 1; var9 <= var6; var9++) {
                     BlockPos var10 = var2.offset(var5, var9);
                     BlockState var11 = class310.world.getBlockState(var10);
                     if (var11.isAir() || !var11.getFluidState().isEmpty()) {
                        if (!this.check(var10, var5)) {
                           break;
                        }

                        var7 = var10;
                        var8++;
                     }
                  }

                  if (var8 > 0) {
                     if (this.sneakBridge.getValue()) {
                        this.run5();
                     }

                     if (var7 != null) {
                        this.run4(var7);
                     }

                     this.antiVoidModuleHelper.run();
                     long var12 = ThreadLocalRandom.current().nextLong(-15L, 16L);
                     this.longVal3 = Math.max(20L, 50L + var12);
                  } else {
                     AntiAFKModuleUtil.run2();
                     this.run6();
                  }
               }
            }
         }
      }
   }

   private boolean check(BlockPos var1, Direction var2) {
      Direction[] var3 = new Direction[]{var2.getOpposite(), Direction.DOWN, var2.rotateYClockwise(), var2.rotateYCounterclockwise(), Direction.UP, var2};
      Direction var4 = null;
      BlockPos var5 = null;

      for (Direction var9 : var3) {
         BlockPos var10 = var1.offset(var9);
         BlockState var11 = class310.world.getBlockState(var10);
         if (!var11.isAir() && var11.getFluidState().isEmpty()) {
            var4 = var9.getOpposite();
            var5 = var10;
            break;
         }
      }

      if (var5 == null) {
         return false;
      } else {
         int var12 = this.getInt();
         if (var12 < 0) {
            return false;
         } else {
            int var13 = class310.player.getInventory().getSelectedSlot();
            if (var13 != var12) {
               if (this.intVal < 0) {
                  this.intVal = var13;
               }

               class310.player.getInventory().setSelectedSlot(var12);
               if (class310.getNetworkHandler() != null) {
                  class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var12));
               }
            }

            Vec3d var14 = new Vec3d(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5);
            BlockHitResult var15 = new BlockHitResult(var14, var4, var5, false);
            class310.interactionManager.interactBlock(class310.player, Hand.MAIN_HAND, var15);
            class310.player.swingHand(Hand.MAIN_HAND);
            return true;
         }
      }
   }

   public int getInt() {
      PlayerInventory var1 = class310.player.getInventory();

      for (int var2 = 0; var2 < 9; var2++) {
         ItemStack var3 = var1.getStack(var2);
         if (!var3.isEmpty() && var3.getItem() instanceof BlockItem var4 && this.check3(var4.getBlock())) {
            return var2;
         }
      }

      return -1;
   }

   private boolean check3(Block var1) {
      if (var1 != Blocks.SAND
         && var1 != Blocks.RED_SAND
         && var1 != Blocks.GRAVEL
         && var1 != Blocks.ANVIL
         && var1 != Blocks.CHIPPED_ANVIL
         && var1 != Blocks.DAMAGED_ANVIL
         && var1 != Blocks.SCAFFOLDING) {
         try {
            BlockState var2 = var1.getDefaultState();
            if (!var2.isSolid()) {
               return false;
            }
         } catch (Throwable var3) {
         }

         return true;
      } else {
         return false;
      }
   }

   private void run4(BlockPos var1) {
      Vec3d var2 = class310.player.getEyePos();
      double var3 = var1.getX() + 0.5 - var2.x;
      double var5 = var1.getZ() + 0.5 - var2.z;
      float var7 = (float)(Math.toDegrees(Math.atan2(var5, var3)) - 90.0);
      AntiAFKModuleUtil.run(var7, 78.0F);
   }

   public void run3() {
      if (this.intVal >= 0 && class310.player != null) {
         int var1 = class310.player.getInventory().getSelectedSlot();
         if (var1 != this.intVal) {
            class310.player.getInventory().setSelectedSlot(this.intVal);
            if (class310.getNetworkHandler() != null) {
               class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.intVal));
            }
         }

         this.intVal = -1;
      } else {
         this.intVal = -1;
      }
   }

   private void run5() {
      if (!this.bool && class310.player != null && class310.getNetworkHandler() != null) {
         class310.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, true, false)));
         class310.player.setSneaking(true);
         this.bool = true;
      }
   }

   private void run6() {
      if (this.bool) {
         if (class310.player != null && class310.getNetworkHandler() != null) {
            class310.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
            class310.player.setSneaking(false);
         }

         this.bool = false;
      }
   }
}

