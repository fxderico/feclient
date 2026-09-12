package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.mixin.MinecraftClientInvoker;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class AutoFish extends Module {
   private static final int intVal = 20;
   private final NumberSetting recastDelayMs = new NumberSetting("RecastDelayMs", 500.0, 0.0, 3000.0, 50.0);
   private final NumberSetting biteThreshold = new NumberSetting("BiteThreshold", 0.1, 0.01, 1.0, 0.01);
   private final BooleanSetting onlyIfHoldingRod = new BooleanSetting("OnlyIfHoldingRod", true);
   private final BooleanSetting playNotification = new BooleanSetting("PlayNotification", false);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private AutoFish.Phase autoFishPhase = AutoFish.Phase.IDLE;
   private int intVal2 = 1073741823;
   private int intVal3 = -1;

   public AutoFish() {
      super("AutoFish", "Auto-catches fish and recasts the rod", Category.PLAYER);
      this.run6(new Setting[]{this.recastDelayMs, this.biteThreshold, this.onlyIfHoldingRod, this.playNotification});
   }

   @Override
   public void run() {
      this.autoFishPhase = AutoFish.Phase.IDLE;
      this.intVal2 = 1073741823;
      this.intVal3 = -1;
      this.antiVoidModuleHelper.run();
   }

   @Override
   public void run2() {
      this.autoFishPhase = AutoFish.Phase.IDLE;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         if (this.onlyIfHoldingRod.getValue() && !this.isEnabled()) {
            this.autoFishPhase = AutoFish.Phase.IDLE;
         } else {
            if (this.intVal2 != 1073741823) {
               this.intVal2++;
            }

            MinecraftClientInvoker var1 = (MinecraftClientInvoker)class310;
            switch (this.autoFishPhase) {
               case IDLE:
                  FishingBobberEntity var2 = class310.player.fishHook;
                  if (var2 == null) {
                     if (this.antiVoidModuleHelper.check(this.recastDelayMs.getValueLong())) {
                        var1.nyx$doItemUse();
                        this.antiVoidModuleHelper.run();
                        this.intVal2 = 0;
                        this.intVal3 = -1;
                     }

                     return;
                  }

                  if (var2.getId() != this.intVal3) {
                     this.intVal3 = var2.getId();
                     this.intVal2 = 0;
                  }

                  if (this.intVal2 < 20) {
                     return;
                  }

                  if (this.check(var2)) {
                     var1.nyx$doItemUse();
                     this.antiVoidModuleHelper.run();
                     this.autoFishPhase = AutoFish.Phase.PENDING_RECAST;
                     if (this.playNotification.getValue()) {
                        NotificationUtils.run8("AutoFish", "reeled", INFO.UNKNOWN_2);
                     }
                  }
                  break;
               case PENDING_RECAST:
                  if (!this.antiVoidModuleHelper.check(this.recastDelayMs.getValueLong())) {
                     return;
                  }

                  var1.nyx$doItemUse();
                  this.antiVoidModuleHelper.run();
                  this.intVal2 = 0;
                  this.intVal3 = -1;
                  this.autoFishPhase = AutoFish.Phase.IDLE;
            }
         }
      }
   }

   public boolean isEnabled() {
      ItemStack var1 = class310.player.getMainHandStack();
      ItemStack var2 = class310.player.getOffHandStack();
      return var1.isOf(Items.FISHING_ROD) || var2.isOf(Items.FISHING_ROD);
   }

   private boolean check(FishingBobberEntity var1) {
      Vec3d var2 = var1.getVelocity();
      double var3 = this.biteThreshold.getValue();
      return var2.y <= -var3;
   }

   @Override
   public String getString3() {
      if (this.autoFishPhase == AutoFish.Phase.PENDING_RECAST) {
         return "§7recast";
      } else {
         return class310.player != null && class310.player.fishHook != null ? "§7cast" : null;
      }
   }

   private static enum Phase {
      IDLE,
      PENDING_RECAST;

      private static final AutoFish.Phase[] autoFishPhaseArray = getAutoFishPhaseArray();

      private static AutoFish.Phase[] getAutoFishPhaseArray() {
         return new AutoFish.Phase[]{IDLE, PENDING_RECAST};
      }
   }
}

