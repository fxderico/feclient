package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.InventoryHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;

public class ShieldBreakerModule extends Module {
   public final BooleanSetting switchBack = this.addSetting(new BooleanSetting("Switch Back", "", true));
   public final ModeSetting axePriority = this.addSetting(new ModeSetting("Axe Priority", "", "Best", "Best", "Nearest"));
   public final SliderSetting switchDelay = this.addSetting(new SliderSetting("Switch Delay", "", 150.0, 0.0, 500.0, 10.0));
   private int previousSlot = -1;
   private int tickCounter;
   private boolean waitingToSwapBack;

   public ShieldBreakerModule() {
      super("Shield Breaker", "Auto-switch to axe when target is blocking", Category.COMBAT);
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
      if (client.player != null && client.interactionManager != null) {
         if (this.waitingToSwapBack) {
            this.tickCounter++;
            int delayTicks = Math.max(1, this.switchDelay.getInt() / 50);
            if (this.tickCounter >= delayTicks) {
               InventoryHelper.swap(this.previousSlot);
               this.resetState();
            }
         } else if (client.crosshairTarget != null
            && client.crosshairTarget.getType() == Type.ENTITY
            && ((EntityHitResult)client.crosshairTarget).getEntity() instanceof PlayerEntity targetPlayer
            && targetPlayer.isBlocking()
            && !(client.player.getMainHandStack().getItem() instanceof AxeItem)) {
            int axeSlot = this.findAxeSlot(client);
            if (axeSlot != -1) {
               this.previousSlot = client.player.getInventory().getSelectedSlot();
               InventoryHelper.swap(axeSlot);
               client.interactionManager.attackEntity(client.player, targetPlayer);
               client.player.swingHand(Hand.MAIN_HAND);
               if (this.switchBack.get()) {
                  this.waitingToSwapBack = true;
                  this.tickCounter = 0;
               } else {
                  this.previousSlot = -1;
               }
            }
         }
      }
   }

   private int findAxeSlot(MinecraftClient client) {
      if (this.axePriority.check("Nearest")) {
         for (int i = 0; i < 9; i++) {
            if (client.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
               return i;
            }
         }

         return -1;
      } else {
         int bestSlot = -1;
         float bestDamage = -1.0F;

         for (int ix = 0; ix < 9; ix++) {
            ItemStack stack = client.player.getInventory().getStack(ix);
            if (stack.getItem() instanceof AxeItem) {
               float damage = this.getAxeTier(stack);
               if (damage > bestDamage) {
                  bestDamage = damage;
                  bestSlot = ix;
               }
            }
         }

         return bestSlot;
      }
   }

   private float getAxeTier(ItemStack stack) {
      if (stack.isOf(Items.NETHERITE_AXE)) {
         return 5.0F;
      } else if (stack.isOf(Items.DIAMOND_AXE)) {
         return 4.0F;
      } else if (stack.isOf(Items.IRON_AXE)) {
         return 3.0F;
      } else if (stack.isOf(Items.GOLDEN_AXE)) {
         return 2.0F;
      } else if (stack.isOf(Items.STONE_AXE)) {
         return 1.0F;
      } else {
         return stack.isOf(Items.WOODEN_AXE) ? 0.0F : -1.0F;
      }
   }

   private void resetState() {
      this.previousSlot = -1;
      this.tickCounter = 0;
      this.waitingToSwapBack = false;
   }
}

