package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoMLGModule extends Module {
   private static final int intVal = 45;
   private static final double doubleVal = 10.0;
   private static final long longVal = 100L;
   private final ModeSetting preferred = new ModeSetting("PreferredItem", "WaterBucket", "WaterBucket", "HayBale", "SlimeBlock", "Cobweb");
   private final NumberSetting minFall = new NumberSetting("MinFallDistance", 4.0, 1.0, 40.0, 1.0);
   private final BooleanSetting swapBack = new BooleanSetting("SwapBackOnGround", true);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private int intVal2 = -1;
   private boolean bool;

   public AutoMLGModule() {
      super("AutoMLG", "Places water / hay / slime / cobweb to survive fatal falls", Category.WORLD);
      this.run6(new Setting[]{this.preferred, this.minFall, this.swapBack});
   }

   // onDisable
   @Override
   public void run2() {
      this.run5();
      AntiAFKModuleUtil.run2();
      this.bool = false;
   }

   // onEnable — just reset state; the actual per-tick fall watch lives in run3()
   @Override
   public void run() {
      this.intVal2 = -1;
      this.bool = false;
   }

   // onTick — was mis-slotted into run() (onEnable), so the whole MLG watch only
   // ever ran once the instant you toggled the module on (never while falling).
   // The manager calls run3() every tick, which is where the fall detection +
   // clutch placement belongs.
   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (!class310.player.isOnGround() && !class310.player.isTouchingWater()) {
            if (!(class310.player.getVelocity().y >= 0.0)) {
               double var1 = class310.player.fallDistance;
               if (!(var1 < this.minFall.getValue())) {
                  if (!this.bool) {
                     BlockPos var3 = this.getclass2338();
                     if (var3 != null) {
                        if (this.antiVoidModuleHelper.check(100L)) {
                           Item var4 = this.getclass1792();
                           if (var4 != null) {
                              int var5 = this.intOf(var4);
                              if (var5 < 0) {
                                 var5 = this.intOf2(var4);
                                 if (var5 < 0) {
                                    NotificationUtils.run("AutoMLG", "No " + stringOf(var4) + " available", INFO.UNKNOWN_3, 1500L);
                                    return;
                                 }
                              }

                              int var6 = class310.player.getInventory().getSelectedSlot();
                              if (var6 != var5) {
                                 if (this.intVal2 < 0) {
                                    this.intVal2 = var6;
                                 }

                                 class310.player.getInventory().setSelectedSlot(var5);
                                 if (class310.getNetworkHandler() != null) {
                                    class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var5));
                                 }
                              }

                              AntiAFKModuleUtil.run(class310.player.getYaw(), 90.0F);
                              Vec3d var7 = new Vec3d(var3.getX() + 0.5, var3.getY() + 1.0, var3.getZ() + 0.5);
                              BlockHitResult var8 = new BlockHitResult(var7, Direction.UP, var3, false);
                              class310.interactionManager.interactBlock(class310.player, Hand.MAIN_HAND, var8);
                              class310.player.swingHand(Hand.MAIN_HAND);
                              this.bool = true;
                              this.antiVoidModuleHelper.run();
                              NotificationUtils.run("AutoMLG", stringOf(var4) + " deployed", INFO.UNKNOWN_2, 1500L);
                           }
                        }
                     }
                  }
               }
            }
         } else {
            if (this.bool) {
               if (this.swapBack.getValue()) {
                  this.run5();
               }

               this.bool = false;
               AntiAFKModuleUtil.run2();
            }
         }
      }
   }

   private Item getclass1792() {
      String var1 = this.preferred.getMode();
      switch (var1.hashCode()) {
         case -1928375144:
            if (var1.equals("HayBale")) {
               return Items.HAY_BLOCK;
            }
            break;
         case -1841336635:
            if (var1.equals("SlimeBlock")) {
               return Items.SLIME_BLOCK;
            }
            break;
         case 2023701054:
            if (var1.equals("Cobweb")) {
               return Items.COBWEB;
            }
      }

      return Items.WATER_BUCKET;
   }

   private static String stringOf(Item var0) {
      if (var0 == Items.WATER_BUCKET) {
         return "Water bucket";
      } else if (var0 == Items.HAY_BLOCK) {
         return "Hay bale";
      } else if (var0 == Items.SLIME_BLOCK) {
         return "Slime block";
      } else {
         return var0 == Items.COBWEB ? "Cobweb" : var0.getName().getString();
      }
   }

   private int intOf(Item var1) {
      PlayerInventory var2 = class310.player.getInventory();

      for (int var3 = 0; var3 < 9; var3++) {
         if (var2.getStack(var3).isOf(var1)) {
            return var3;
         }
      }

      return -1;
   }

   private int intOf2(Item var1) {
      if (class310.interactionManager == null) {
         return -1;
      } else {
         PlayerInventory var2 = class310.player.getInventory();
         int var3 = -1;

         for (int var4 = 9; var4 < 36; var4++) {
            if (var2.getStack(var4).isOf(var1)) {
               var3 = var4;
               break;
            }
         }

         if (var3 < 0) {
            return -1;
         } else {
            int var7 = class310.player.currentScreenHandler.syncId;
            class310.interactionManager.clickSlot(var7, var3, 0, SlotActionType.PICKUP, class310.player);
            class310.interactionManager.clickSlot(var7, 36, 0, SlotActionType.PICKUP, class310.player);
            if (!class310.player.currentScreenHandler.getCursorStack().isEmpty()) {
               class310.interactionManager.clickSlot(var7, var3, 0, SlotActionType.PICKUP, class310.player);
            }

            return 0;
         }
      }
   }

   private BlockPos getclass2338() {
      double var1 = class310.player.getEyeY();
      double var3 = class310.player.getY();
      int var5 = class310.player.getBlockX();
      int var6 = class310.player.getBlockZ();
      int var7 = (int)Math.ceil(10.0);

      for (int var8 = 1; var8 <= var7; var8++) {
         int var9 = (int)Math.floor(var3) - var8;
         if (var9 < class310.world.getBottomY()) {
            return null;
         }

         BlockPos var10 = new BlockPos(var5, var9, var6);
         BlockState var11 = class310.world.getBlockState(var10);
         if (!var11.isAir()) {
            if (!var11.getFluidState().isEmpty()) {
               return null;
            }

            double var12 = var1 - (var9 + 1.0);
            if (var12 <= 10.0) {
               return var10;
            }

            return null;
         }
      }

      return null;
   }

   private void run5() {
      if (this.intVal2 >= 0 && class310.player != null) {
         int var1 = class310.player.getInventory().getSelectedSlot();
         if (var1 != this.intVal2) {
            class310.player.getInventory().setSelectedSlot(this.intVal2);
            if (class310.getNetworkHandler() != null) {
               class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.intVal2));
            }
         }

         this.intVal2 = -1;
      } else {
         this.intVal2 = -1;
      }
   }

   @Override
   public String getString3() {
      return "§7" + this.preferred.getMode();
   }
}

