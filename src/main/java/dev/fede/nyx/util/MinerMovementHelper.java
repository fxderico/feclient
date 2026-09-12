package dev.fede.nyx.util;

import dev.fede.nyx.mixin.MinecraftClientInvoker;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class MinerMovementHelper {
   private static final MinecraftClient class310 = MinecraftClient.getInstance();
   public static final String string = "cobblestone,cobbled_deepslate,dirt,granite,diorite,andesite,netherrack";
   private static final int intVal = 5;

   private MinerMovementHelper() {
   }

   public static MinerMovementHelper.TorchResult minerMovementHelperTorchResultOf(Direction var0) {
      if (class310.player == null || class310.world == null || class310.interactionManager == null || class310.getNetworkHandler() == null || var0 == null) {
         return MinerMovementHelper.TorchResult.WALL_INVALID;
      } else if (!class310.player.isTouchingWater() && !class310.player.isInLava()) {
         PlayerInventory var1 = class310.player.getInventory();
         int var2 = getInt();
         if (var2 < 0) {
            return MinerMovementHelper.TorchResult.NO_TORCH;
         } else {
            BlockPos var3 = class310.player.getBlockPos().offset(var0.getOpposite());
            BlockPos var4 = var3.down();
            BlockState var5 = class310.world.getBlockState(var4);
            if (var5.isAir()) {
               return MinerMovementHelper.TorchResult.WALL_INVALID;
            } else if (!class310.world.getBlockState(var3).isAir()) {
               return MinerMovementHelper.TorchResult.WALL_INVALID;
            } else {
               int var6 = var1.getSelectedSlot();
               int var7 = -1;
               if (var2 != var6) {
                  var7 = var6;
                  var1.setSelectedSlot(var2);
                  class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var2));
               }

               Vec3d var8 = new Vec3d(var4.getX() + 0.5, var4.getY() + 1.0, var4.getZ() + 0.5);
               float[] var9 = AutoCrystalModuleUtil.floatArrayOf(var8);
               float var10 = class310.player.getYaw();
               float var11 = class310.player.getPitch();
               class310.player.setYaw(var9[0]);
               class310.player.setPitch(var9[1]);

               try {
                  ((MinecraftClientInvoker)class310).nyx$doItemUse();
               } catch (Throwable var14) {
                  BlockHitResult var13 = new BlockHitResult(var8, Direction.UP, var4, false);
                  class310.interactionManager.interactBlock(class310.player, Hand.MAIN_HAND, var13);
               }

               class310.player.swingHand(Hand.MAIN_HAND);
               class310.player.setYaw(var10);
               class310.player.setPitch(var11);
               if (var7 >= 0) {
                  run6(var7);
               }

               return MinerMovementHelper.TorchResult.PLACED;
            }
         }
      } else {
         return MinerMovementHelper.TorchResult.IN_LIQUID;
      }
   }

   public static boolean check(BlockPos var0, Direction var1) {
      if (class310.world != null && var0 != null && var1 != null) {
         BlockPos var2 = var0.offset(var1.getOpposite());
         if (!class310.world.getBlockState(var2).isAir()) {
            return false;
         } else {
            BlockPos var3 = var2.down();
            BlockState var4 = class310.world.getBlockState(var3);
            if (var4.isAir()) {
               return false;
            } else {
               try {
                  if (!var4.isOpaqueFullCube()) {
                     return false;
                  }
               } catch (Throwable var6) {
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   public static int getInt() {
      if (class310.player == null) {
         return -1;
      } else {
         PlayerInventory var0 = class310.player.getInventory();

         for (int var1 = 0; var1 < PlayerInventory.getHotbarSize(); var1++) {
            ItemStack var2 = var0.getStack(var1);
            if (!var2.isEmpty()) {
               if (var2.getItem() == Items.TORCH) {
                  return var1;
               }

               if (var2.getItem() instanceof BlockItem var3 && var3.getBlock() == Blocks.TORCH) {
                  return var1;
               }
            }
         }

         return -1;
      }
   }

   public static MinerMovementHelper.Inner1 minerMovementHelperaOf(BlockPos var0, Direction var1, String var2) {
      if (class310.player != null && class310.world != null && class310.getNetworkHandler() != null && var0 != null && var1 != null) {
         BlockPos var3 = var0.offset(var1.getOpposite());
         BlockState var4 = class310.world.getBlockState(var3);
         if (var4.isAir()) {
            return null;
         } else if (!var4.getFluidState().isEmpty()) {
            return null;
         } else {
            BlockState var5 = class310.world.getBlockState(var0);
            if (!var5.isAir() && var5.getFluidState().isEmpty()) {
               return null;
            } else {
               int var6 = intOf2(var2);
               if (var6 < 0) {
                  return null;
               } else {
                  MinerMovementHelper.Inner1 var7 = new MinerMovementHelper.Inner1(var0.toImmutable(), var3.toImmutable(), var1, var6);
                  int var8 = class310.player.getInventory().getSelectedSlot();
                  if (var6 != var8) {
                     var7.intVal2 = var8;
                     class310.player.getInventory().setSelectedSlot(var6);
                     class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var6));
                  }

                  var7.minerMovementHelperPhase = MinerMovementHelper.Phase.ROTATE_SNEAK;
                  var7.intVal3 = 1;
                  return var7;
               }
            }
         }
      } else {
         return null;
      }
   }

   public static MinerMovementHelper.ScaffoldResult minerMovementHelperScaffoldResultOf(MinerMovementHelper.Inner1 var0) {
      if (var0 == null) {
         return MinerMovementHelper.ScaffoldResult.ABORTED;
      } else if (class310.player != null && class310.world != null && class310.interactionManager != null && class310.getNetworkHandler() != null) {
         var0.intVal3++;
         if (var0.intVal3 > 5) {
            run(var0);
            return class310.world.getBlockState(var0.class2338).isAir()
               ? MinerMovementHelper.ScaffoldResult.TIMED_OUT
               : MinerMovementHelper.ScaffoldResult.PLACED;
         } else {
            switch (var0.minerMovementHelperPhase) {
               case SWAP:
                  var0.minerMovementHelperPhase = MinerMovementHelper.Phase.ROTATE_SNEAK;
                  return MinerMovementHelper.ScaffoldResult.IN_PROGRESS;
               case ROTATE_SNEAK:
                  Vec3d var3 = class243Of(var0);
                  float[] var4 = AutoCrystalModuleUtil.floatArrayOf(var3);
                  AntiAFKModuleUtil.run(var4[0], var4[1]);
                  if (!var0.bool) {
                     class310.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, true, false)));
                     var0.bool = true;
                  }

                  var0.minerMovementHelperPhase = MinerMovementHelper.Phase.PLACE;
                  return MinerMovementHelper.ScaffoldResult.IN_PROGRESS;
               case PLACE:
                  Vec3d var1 = class243Of(var0);
                  BlockHitResult var2 = new BlockHitResult(var1, var0.class2350, var0.class23382, false);
                  class310.interactionManager.interactBlock(class310.player, Hand.MAIN_HAND, var2);
                  class310.player.swingHand(Hand.MAIN_HAND);
                  var0.minerMovementHelperPhase = MinerMovementHelper.Phase.RELEASE;
                  return MinerMovementHelper.ScaffoldResult.IN_PROGRESS;
               case RELEASE:
                  if (var0.bool) {
                     class310.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
                     var0.bool = false;
                  }

                  if (var0.intVal2 >= 0) {
                     run6(var0.intVal2);
                     var0.intVal2 = -1;
                  }

                  AntiAFKModuleUtil.run2();
                  var0.minerMovementHelperPhase = MinerMovementHelper.Phase.DONE;
                  return class310.world.getBlockState(var0.class2338).isAir()
                     ? MinerMovementHelper.ScaffoldResult.TIMED_OUT
                     : MinerMovementHelper.ScaffoldResult.PLACED;
               case DONE:
               default:
                  return class310.world.getBlockState(var0.class2338).isAir()
                     ? MinerMovementHelper.ScaffoldResult.TIMED_OUT
                     : MinerMovementHelper.ScaffoldResult.PLACED;
            }
         }
      } else {
         return MinerMovementHelper.ScaffoldResult.ABORTED;
      }
   }

   public static void run(MinerMovementHelper.Inner1 var0) {
      if (var0 != null) {
         if (var0.bool && class310.player != null && class310.getNetworkHandler() != null) {
            class310.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
            var0.bool = false;
         }

         if (var0.intVal2 >= 0) {
            run6(var0.intVal2);
            var0.intVal2 = -1;
         }

         AntiAFKModuleUtil.run2();
         var0.minerMovementHelperPhase = MinerMovementHelper.Phase.DONE;
      }
   }

   public static int intOf2(String var0) {
      if (class310.player == null) {
         return -1;
      } else {
         Set var1 = setOf2(var0);
         if (var1.isEmpty()) {
            return -1;
         } else {
            PlayerInventory var2 = class310.player.getInventory();

            for (int var3 = 0; var3 < PlayerInventory.getHotbarSize(); var3++) {
               ItemStack var4 = var2.getStack(var3);
               if (!var4.isEmpty() && var4.getItem() instanceof BlockItem var5) {
                  Block var8 = var5.getBlock();
                  Identifier var7 = Registries.BLOCK.getId(var8);
                  if (var1.contains(var7.getPath()) || var1.contains(var7.toString())) {
                     return var3;
                  }
               }
            }

            return -1;
         }
      }
   }

   private static Set<String> setOf2(String var0) {
      HashSet var1 = new HashSet();
      String var2 = var0 != null && !var0.isEmpty() ? var0 : "cobblestone,cobbled_deepslate,dirt,granite,diorite,andesite,netherrack";

      for (String var6 : var2.split(",")) {
         String var7 = var6.trim().toLowerCase();
         if (!var7.isEmpty()) {
            var1.add(var7);
         }
      }

      return var1;
   }

   private static Vec3d class243Of(MinerMovementHelper.Inner1 var0) {
      return new Vec3d(
         var0.class23382.getX() + 0.5 + var0.class2350.getOffsetX() * 0.5,
         var0.class23382.getY() + 0.5 + var0.class2350.getOffsetY() * 0.5,
         var0.class23382.getZ() + 0.5 + var0.class2350.getOffsetZ() * 0.5
      );
   }

   public static void run6(int var0) {
      if (class310.player != null && var0 >= 0 && var0 < PlayerInventory.getHotbarSize()) {
         int var1 = class310.player.getInventory().getSelectedSlot();
         if (var1 != var0) {
            class310.player.getInventory().setSelectedSlot(var0);
            if (class310.getNetworkHandler() != null) {
               class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var0));
            }
         }
      }
   }

   private static enum Phase {
      SWAP,
      ROTATE_SNEAK,
      PLACE,
      RELEASE,
      DONE;

      private static final MinerMovementHelper.Phase[] minerMovementHelperPhaseArray = getMinerMovementHelperPhaseArray();

      private static MinerMovementHelper.Phase[] getMinerMovementHelperPhaseArray() {
         return new MinerMovementHelper.Phase[]{SWAP, ROTATE_SNEAK, PLACE, RELEASE, DONE};
      }
   }

   public static enum ScaffoldResult {
      IN_PROGRESS,
      PLACED,
      TIMED_OUT,
      ABORTED;

      private static final MinerMovementHelper.ScaffoldResult[] minerMovementHelperScaffoldResultArray = getMinerMovementHelperScaffoldResultArray();

      private static MinerMovementHelper.ScaffoldResult[] getMinerMovementHelperScaffoldResultArray() {
         return new MinerMovementHelper.ScaffoldResult[]{IN_PROGRESS, PLACED, TIMED_OUT, ABORTED};
      }
   }

   public static enum TorchResult {
      PLACED,
      NO_TORCH,
      WALL_INVALID,
      IN_LIQUID;

      private static final MinerMovementHelper.TorchResult[] minerMovementHelperTorchResultArray = getMinerMovementHelperTorchResultArray();

      private static MinerMovementHelper.TorchResult[] getMinerMovementHelperTorchResultArray() {
         return new MinerMovementHelper.TorchResult[]{PLACED, NO_TORCH, WALL_INVALID, IN_LIQUID};
      }
   }

public final static class Inner1 {
   final BlockPos class2338;
   final BlockPos class23382;
   final Direction class2350;
   final int intVal;
   int intVal2 = -1;
   boolean bool;
   MinerMovementHelper.Phase minerMovementHelperPhase = MinerMovementHelper.Phase.SWAP;
   int intVal3;

   private Inner1(BlockPos var1, BlockPos var2, Direction var3, int var4) {
      this.class2338 = var1;
      this.class23382 = var2;
      this.class2350 = var3;
      this.intVal = var4;
   }

   public BlockPos getclass2338() {
      return this.class2338;
   }

   public MinerMovementHelper.Phase getMinerMovementHelperPhase() {
      return this.minerMovementHelperPhase;
   }

   public int getInt() {
      return this.intVal3;
   }
}
}

