package dev.fede.water.module.modules.combat;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;

public final class ShieldBreaker extends Module {
   private final Setting<Boolean> ba = new Setting<>("Switch Back", true);
   private final Setting<Float> bb = new Setting<>("Switch Delay", 0.0F, 0.0F, 500.0F);
   private boolean q = false;
   private long j = -1L;
   private boolean r = false;
   private boolean s = false;
   private int previousSlot = -1;

   public ShieldBreaker() {
      super("Shield Breaker", Category.field_a_1);
      this.addSetting(this.ba);
      this.addSetting(this.bb);
   }

   @Override
   public void onEnable() {
      this.u();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.r && this.ba.getValue() && this.s && mc.player != null) {
         this.c(this.previousSlot);
      }

      this.u();
      super.onDisable();
   }

   private void u() {
      this.q = false;
      this.j = -1L;
      this.r = false;
      this.s = false;
      this.previousSlot = -1;
   }

   @Override
   public void onTick() {
      if (mc.player != null && mc.world != null) {
         PlayerEntity var1 = this.a();
         if (var1 != null && var1.isBlocking()) {
            if (!this.q) {
               this.q = true;
               this.j = System.currentTimeMillis();
            }

            long var2 = this.bb.getValue().longValue();
            if (!this.r && this.j >= 0L && System.currentTimeMillis() - this.j >= var2) {
               int var4 = this.h();
               if (var4 != -1) {
                  this.previousSlot = mc.player.getInventory().getSelectedSlot();
                  if (this.previousSlot != var4) {
                     this.c(var4);
                     this.s = true;
                  }

                  mc.interactionManager.attackEntity(mc.player, var1);
                  mc.player.swingHand(Hand.MAIN_HAND);
                  this.r = true;
               }
            }
         } else {
            if (this.q) {
               this.q = false;
               this.j = -1L;
            }

            if (this.r) {
               if (this.ba.getValue() && this.s && this.previousSlot != -1) {
                  this.c(this.previousSlot);
               }

               this.r = false;
               this.s = false;
               this.previousSlot = -1;
            }
         }
      }
   }

   private PlayerEntity a() {
      return mc.crosshairTarget != null
            && mc.crosshairTarget.getType() == Type.ENTITY
            && ((EntityHitResult)mc.crosshairTarget).getEntity() instanceof PlayerEntity var2
            && var2 != mc.player
         ? var2
         : null;
   }

   private int h() {
      int var1 = -1;
      int var2 = -1;

      for (int var3 = 0; var3 < 9; var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (var4.getItem() instanceof AxeItem) {
            int var5 = this.a(var4);
            if (var5 > var1) {
               var1 = var5;
               var2 = var3;
            }
         }
      }

      return var2;
   }

   private int a(ItemStack stack) {
      if (stack.isOf(Items.NETHERITE_AXE)) {
         return 6;
      } else if (stack.isOf(Items.DIAMOND_AXE)) {
         return 5;
      } else if (stack.isOf(Items.IRON_AXE)) {
         return 4;
      } else if (stack.isOf(Items.GOLDEN_AXE)) {
         return 3;
      } else if (stack.isOf(Items.STONE_AXE)) {
         return 2;
      } else {
         return stack.isOf(Items.WOODEN_AXE) ? 1 : 0;
      }
   }

   private void c(int slot) {
      if (mc.player != null) {
         if (slot >= 0 && slot <= 8) {
            mc.player.getInventory().setSelectedSlot(slot);
         }
      }
   }
}

