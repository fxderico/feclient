package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.InventoryHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.lwjgl.glfw.GLFW;

public class MaceSwapModule extends Module {
   public final BooleanSetting windBurst = this.addSetting(new BooleanSetting("Wind Burst", "", true));
   public final BooleanSetting breach = this.addSetting(new BooleanSetting("Breach", "", true));
   public final BooleanSetting onlySword = this.addSetting(new BooleanSetting("Only Sword", "", false));
   public final BooleanSetting onlyAxe = this.addSetting(new BooleanSetting("Only Axe", "", false));
   public final BooleanSetting switchBack = this.addSetting(new BooleanSetting("Switch Back", "", true));
   public final SliderSetting switchDelay = this.addSetting(new SliderSetting("Switch Delay", "", 0.0, 0.0, 20.0, 1.0));
   private int previousSlot = -1;
   private int tickCounter;
   private boolean waitingToSwapBack;
   private boolean attackedThisTick;

   public MaceSwapModule() {
      super("Mace Swap", "Switches to mace when attacking", Category.COMBAT);
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
            if (this.tickCounter >= this.switchDelay.getInt()) {
               InventoryHelper.swap(this.previousSlot);
               this.resetState();
            }
         } else {
            long handle = client.getWindow().getHandle();
            boolean lmb = GLFW.glfwGetMouseButton(handle, 0) == 1;
            if (lmb && client.currentScreen == null) {
               if (client.crosshairTarget != null && client.crosshairTarget.getType() == Type.ENTITY) {
                  if (!(client.player.getAttackCooldownProgress(0.5F) < 1.0F)) {
                     if (!this.onlySword.get() || this.isSword(client.player.getMainHandStack().getItem())) {
                        if (!this.onlyAxe.get() || client.player.getMainHandStack().getItem() instanceof AxeItem) {
                           if (!client.player.getMainHandStack().isOf(Items.MACE)) {
                              if (InventoryHelper.getHotbarSlot(Items.MACE) != -1) {
                                 this.previousSlot = client.player.getInventory().getSelectedSlot();
                                 InventoryHelper.swapToItem(Items.MACE);
                                 EntityHitResult entityHit = (EntityHitResult)client.crosshairTarget;
                                 client.interactionManager.attackEntity(client.player, entityHit.getEntity());
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
                  }
               }
            }
         }
      }
   }

   private boolean isSword(Item item) {
      return item == Items.WOODEN_SWORD
         || item == Items.STONE_SWORD
         || item == Items.IRON_SWORD
         || item == Items.GOLDEN_SWORD
         || item == Items.DIAMOND_SWORD
         || item == Items.NETHERITE_SWORD;
   }

   private void resetState() {
      this.previousSlot = -1;
      this.tickCounter = 0;
      this.waitingToSwapBack = false;
      this.attackedThisTick = false;
   }
}

