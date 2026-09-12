package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.BlockHelper;
import dev.fede.util.InventoryHelper;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class AnchorMacroModule extends Module {
   public final SliderSetting switchDelay = this.addSetting(new SliderSetting("Switch Delay", "", 0.0, 0.0, 20.0, 1.0));
   public final SliderSetting glowstoneDelay = this.addSetting(new SliderSetting("Glowstone Delay", "", 0.0, 0.0, 20.0, 1.0));
   public final SliderSetting explodeDelay = this.addSetting(new SliderSetting("Explode Delay", "", 0.0, 0.0, 20.0, 1.0));
   public final SliderSetting totemSlot = this.addSetting(new SliderSetting("Totem Slot", "", 1.0, 1.0, 9.0, 1.0));
   public final BooleanSetting autoSwitchBack = this.addSetting(new BooleanSetting("Auto Switch Back", "", true));
   public final SliderSetting switchBackDelay = this.addSetting(new SliderSetting("Switch Back Delay", "", 2.0, 0.0, 20.0, 1.0));
   private int step;
   private int tickCounter;
   private BlockPos targetPos;
   private int previousSlot = -1;

   public AnchorMacroModule() {
      super("Anchor Macro", "Automatically blows up respawn anchors", Category.COMBAT);
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   @Override
   public void onTick() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player != null && client.interactionManager != null && client.world != null) {
         if (this.step > 0) {
            this.processStep(client);
         } else {
            long handle = client.getWindow().getHandle();
            boolean rmb = GLFW.glfwGetMouseButton(handle, 1) == 1;
            if (rmb && client.crosshairTarget != null && client.crosshairTarget.getType() == Type.BLOCK) {
               BlockHitResult hit = (BlockHitResult)client.crosshairTarget;
               BlockPos pos = hit.getBlockPos();
               if (BlockHelper.isBlockAt(pos, Blocks.RESPAWN_ANCHOR)) {
                  this.targetPos = pos;
                  this.previousSlot = client.player.getInventory().getSelectedSlot();
                  if (BlockHelper.isAnchorUncharged(pos)) {
                     this.step = 1;
                     this.tickCounter = 0;
                  } else if (BlockHelper.isAnchorCharged(pos)) {
                     this.step = 3;
                     this.tickCounter = 0;
                  }
               }
            }
         }
      }
   }

   private void processStep(MinecraftClient client) {
      this.tickCounter++;
      BlockHitResult hit = client.crosshairTarget instanceof BlockHitResult bhr ? bhr : null;
      switch (this.step) {
         case 1:
            if (this.tickCounter >= this.switchDelay.getInt()) {
               InventoryHelper.swapToItem(Items.GLOWSTONE);
               this.step = 2;
               this.tickCounter = 0;
            }
            break;
         case 2:
            if (this.tickCounter >= this.glowstoneDelay.getInt()) {
               if (hit != null && this.targetPos.equals(hit.getBlockPos())) {
                  BlockHelper.interactBlock(hit, true);
               }

               this.step = 3;
               this.tickCounter = 0;
            }
            break;
         case 3:
            if (this.tickCounter >= this.explodeDelay.getInt()) {
               InventoryHelper.swap(this.totemSlot.getInt() - 1);
               this.step = 4;
               this.tickCounter = 0;
            }
            break;
         case 4:
            if (this.tickCounter >= 1) {
               if (hit != null && this.targetPos.equals(hit.getBlockPos())) {
                  BlockHelper.interactBlock(hit, true);
               }

               if (this.autoSwitchBack.get() && this.previousSlot >= 0) {
                  this.step = 5;
                  this.tickCounter = 0;
               } else {
                  this.resetState();
               }
            }
            break;
         case 5:
            if (this.tickCounter >= this.switchBackDelay.getInt()) {
               InventoryHelper.swap(this.previousSlot);
               this.resetState();
            }
            break;
         default:
            this.resetState();
      }
   }

   private void resetState() {
      this.step = 0;
      this.tickCounter = 0;
      this.targetPos = null;
      this.previousSlot = -1;
   }
}

