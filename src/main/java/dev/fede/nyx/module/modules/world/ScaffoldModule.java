package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import java.util.Random;
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

public class ScaffoldModule extends Module {
   private static final float floatVal = 82.0F;
   private static final long longVal = 50L;
   private static final long longVal2 = 15L;
   private final ModeSetting mode = new ModeSetting("Mode", "Legit", "Legit", "Silent", "Expand", "Tower");
   private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
   private final BooleanSetting safeWalk = new BooleanSetting("SafeWalk", true);
   private final ModeSetting blockType = new ModeSetting("BlockType", "HotbarAny", "HotbarAny", "Cobble", "Ends");
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private final Random random = new Random();
   private int intVal = -1;
   private boolean bool;
   private long longVal3 = 50L;

   public ScaffoldModule() {
      super("Scaffold", "Places blocks under the player to prevent falling", Category.WORLD);
      this.run6(new Setting[]{this.mode, this.rotate, this.safeWalk, this.blockType});
   }

   @Override
   public void run2() {
      this.run4();
      this.run7();
      AntiAFKModuleUtil.run2();
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (this.antiVoidModuleHelper.check(this.longVal3)) {
            boolean var1;
            if (this.mode.check("Tower")) {
               var1 = this.isEnabled3();
            } else if (this.mode.check("Expand")) {
               var1 = this.isEnabled2();
            } else {
               var1 = this.isEnabled();
            }

            if (var1) {
               this.antiVoidModuleHelper.run();
               long var2 = this.random.nextInt(31) - 15L;
               this.longVal3 = Math.max(1L, 50L + var2);
            } else {
               this.run4();
               this.run7();
               if (!this.mode.check("Silent")) {
                  AntiAFKModuleUtil.run2();
               }
            }
         }
      }
   }

   public boolean isEnabled() {
      BlockPos var1 = class310.player.getBlockPos();
      BlockPos var2 = var1.down();
      return !class310.world.getBlockState(var2).isAir() ? false : this.check(var2);
   }

   private boolean isEnabled2() {
      BlockPos var1 = class310.player.getBlockPos().down();
      Direction var2 = Direction.fromHorizontalDegrees(class310.player.getYaw());
      Direction var3 = var2.rotateYCounterclockwise();
      Direction var4 = var2.rotateYClockwise();
      boolean var5 = false;

      for (BlockPos var9 : new BlockPos[]{var1, var1.offset(var3), var1.offset(var4)}) {
         if (class310.world.getBlockState(var9).isAir() && this.check(var9)) {
            var5 = true;
            break;
         }
      }

      return var5;
   }

   public boolean isEnabled3() {
      if (class310.options == null || !class310.options.jumpKey.isPressed()) {
         return false;
      } else if (class310.player.isOnGround()) {
         return false;
      } else {
         Vec3d var1 = class310.player.getVelocity();
         if (var1.y < 0.0) {
            return false;
         } else {
            BlockPos var2 = class310.player.getBlockPos();
            return !class310.world.getBlockState(var2).isAir() ? false : this.check(var2);
         }
      }
   }

   private boolean check(BlockPos var1) {
      Direction var2 = null;
      BlockPos var3 = null;

      for (Direction var7 : Direction.values()) {
         BlockPos var8 = var1.offset(var7);
         BlockState var9 = class310.world.getBlockState(var8);
         if (!var9.isAir() && var9.getFluidState().isEmpty()) {
            var2 = var7.getOpposite();
            var3 = var8;
            break;
         }
      }

      if (var3 == null) {
         return false;
      } else {
         int var10 = this.getInt3();
         if (var10 < 0) {
            return false;
         } else {
            int var11 = class310.player.getInventory().getSelectedSlot();
            if (var11 != var10) {
               if (this.intVal < 0) {
                  this.intVal = var11;
               }

               class310.player.getInventory().setSelectedSlot(var10);
               if (class310.getNetworkHandler() != null) {
                  class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var10));
               }
            }

            Vec3d var12 = new Vec3d(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5);
            if (this.rotate.getValue()) {
               if (this.mode.check("Silent")) {
                  float var13 = class310.player.getYaw();
                  AntiAFKModuleUtil.run(var13, 82.0F);
               } else if (this.mode.check("Legit")) {
                  class310.player.setPitch(82.0F);
                  AntiAFKModuleUtil.run2();
               } else {
                  AntiAFKModuleUtil.run2();
               }
            }

            if (this.safeWalk.getValue() && !this.bool && class310.getNetworkHandler() != null) {
               class310.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, true, false)));
               this.bool = true;
            }

            BlockHitResult var14 = new BlockHitResult(var12, var2, var3, false);
            class310.interactionManager.interactBlock(class310.player, Hand.MAIN_HAND, var14);
            class310.player.swingHand(Hand.MAIN_HAND);
            return true;
         }
      }
   }

   private int getInt3() {
      PlayerInventory var1 = class310.player.getInventory();
      int var2 = -1;

      for (int var3 = 0; var3 < 9; var3++) {
         ItemStack var4 = var1.getStack(var3);
         if (!var4.isEmpty() && var4.getItem() instanceof BlockItem var5) {
            Block var7 = var5.getBlock();
            if (this.check4(var7)) {
               if (this.check3(var7)) {
                  return var3;
               }

               if (var2 < 0) {
                  var2 = var3;
               }
            }
         }
      }

      return var2;
   }

   private boolean check3(Block var1) {
      String var2 = this.blockType.getMode();
      switch (var2.hashCode()) {
         case 2164504:
            if (var2.equals("Ends")) {
               return var1 == Blocks.END_STONE || var1 == Blocks.END_STONE_BRICKS;
            }
            break;
         case 2023681093:
            if (var2.equals("Cobble")) {
               return var1 == Blocks.COBBLESTONE || var1 == Blocks.COBBLED_DEEPSLATE || var1 == Blocks.MOSSY_COBBLESTONE;
            }
      }

      return true;
   }

   private boolean check4(Block var1) {
      if (var1 != Blocks.SAND
         && var1 != Blocks.RED_SAND
         && var1 != Blocks.GRAVEL
         && var1 != Blocks.ANVIL
         && var1 != Blocks.CHIPPED_ANVIL
         && var1 != Blocks.DAMAGED_ANVIL
         && var1 != Blocks.WHITE_CONCRETE_POWDER
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

   private void run7() {
      if (this.bool) {
         if (class310.player != null && class310.getNetworkHandler() != null) {
            class310.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
         }

         this.bool = false;
      }
   }

   private void run4() {
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

   @Override
   public String getString3() {
      return "§7" + this.mode.getMode();
   }
}

