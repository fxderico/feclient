package dev.fede.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public final class BlockHelper {
   private BlockHelper() {
   }

   public static boolean isBlockAt(BlockPos pos, Block block) {
      MinecraftClient mc = MinecraftClient.getInstance();
      return mc.world != null && mc.world.getBlockState(pos).isOf(block);
   }

   public static boolean isAnchorCharged(BlockPos pos) {
      MinecraftClient mc = MinecraftClient.getInstance();
      return isBlockAt(pos, Blocks.RESPAWN_ANCHOR) && (Integer)mc.world.getBlockState(pos).get(RespawnAnchorBlock.CHARGES) != 0;
   }

   public static boolean isAnchorUncharged(BlockPos pos) {
      MinecraftClient mc = MinecraftClient.getInstance();
      return isBlockAt(pos, Blocks.RESPAWN_ANCHOR) && (Integer)mc.world.getBlockState(pos).get(RespawnAnchorBlock.CHARGES) == 0;
   }

   public static void interactBlock(BlockHitResult hit, boolean swing) {
      interactBlock(hit, Hand.MAIN_HAND, swing);
   }

   public static void interactBlock(BlockHitResult hit, Hand hand, boolean swing) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && mc.interactionManager != null) {
         ActionResult result = mc.interactionManager.interactBlock(mc.player, hand, hit);
         if (result.isAccepted() && swing) {
            mc.player.swingHand(hand);
         }
      }
   }
}

