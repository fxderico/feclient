package dev.fede.nyx.module.modules.world;
import net.minecraft.client.util.InputUtil;

import dev.fede.nyx.mixin.ClientPlayerInteractionManagerAccessor;
import dev.fede.nyx.mixin.MinecraftClientInvoker;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.tracker.MapUtils;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.AutoCrystalModuleUtil;
import dev.fede.nyx.util.AutoTunnelHelper;
import dev.fede.nyx.util.AutoTunnelUtil;
import dev.fede.nyx.util.AutoTunnelUtil2;
import dev.fede.nyx.util.AutoTunnelUtil3;
import dev.fede.nyx.util.OptionalUtils;
import dev.fede.nyx.util.SplittableRandomUtils;
import dev.fede.nyx.util.InputUtil$class_306Utils;
import dev.fede.nyx.util.d$aUtils;
import dev.fede.nyx.util.m$aUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoTunnel extends Module {
   private static final String string = "AutoTunnel";
   private final BooleanSetting exploitMode = new BooleanSetting("ExploitMode", false);
   private final BooleanSetting autoAim = new BooleanSetting("AutoAim", true);
   private final BooleanSetting walkForward = new BooleanSetting("WalkForward", true);
   private final BooleanSetting autoTool = new BooleanSetting("AutoTool", true);
   private final BooleanSetting stopIfLava = new BooleanSetting("StopIfLava", true);
   private final BooleanSetting stopIfInvFull = new BooleanSetting("StopIfInventoryFull", true);
   private final NumberSetting breakDelayMs = (NumberSetting)new NumberSetting("BreakDelayMs", 0.0, 0.0, 2000.0, 25.0).visibleWhen(this::getBoolean2);
   private final NumberSetting crosshairStableTicks = (NumberSetting)new NumberSetting("CrosshairStableTicks", 1.0, 1.0, 6.0, 1.0)
      .visibleWhen(this::getBoolean);
   private final BooleanSetting releaseOnBreakComplete = (BooleanSetting)new BooleanSetting("ReleaseBetweenBlocks", false).visibleWhen(this::getBoolean6);
   private final NumberSetting exploitCadenceMs = (NumberSetting)new NumberSetting("Cadence", 300.0, 285.0, 800.0, 5.0).visibleWhen(this.exploitMode::getValue);
   private final NumberSetting exploitSpeedSafetyMs = (NumberSetting)new NumberSetting("SpeedSafetyMs", 25.0, 0.0, 200.0, 5.0)
      .visibleWhen(this.exploitMode::getValue);
   private final NumberSetting exploitPositionGapTicks = (NumberSetting)new NumberSetting("PositionGapTicks", 15.0, 5.0, 18.0, 1.0)
      .visibleWhen(this.exploitMode::getValue);
   private final NumberSetting tunnelHeight = (NumberSetting)new NumberSetting("TunnelHeight", 2.0, 1.0, 3.0, 1.0).visibleWhen(this.exploitMode::getValue);
   private final NumberSetting tunnelWidth = (NumberSetting)new NumberSetting("TunnelWidth", 1.0, 1.0, 3.0, 1.0).visibleWhen(this.exploitMode::getValue);
   private final BooleanSetting fallbackSlotSwap = (BooleanSetting)new BooleanSetting("SlotSwapDamage", false).visibleWhen(this.exploitMode::getValue);
   private final BooleanSetting fallbackVehicleRide = (BooleanSetting)new BooleanSetting("VehicleRide", false).visibleWhen(this.exploitMode::getValue);
   private final BooleanSetting fallbackLookOnlyFlying = (BooleanSetting)new BooleanSetting("LookOnlyFlying", false).visibleWhen(this.exploitMode::getValue);
   private final BooleanSetting showSelection = new BooleanSetting("ShowSelection", true);
   private final NumberSetting stuckTimeoutMs = new NumberSetting("StuckTimeoutMs", 5000.0, 2000.0, 15000.0, 500.0);
   private final NumberSetting blacklistDurationMs = new NumberSetting("BlacklistDurationMs", 30000.0, 5000.0, 120000.0, 1000.0);
   private final BooleanSetting torches = new BooleanSetting("Torches", false);
   private final NumberSetting torchInterval = (NumberSetting)new NumberSetting("TorchInterval", 10.0, 4.0, 20.0, 1.0).visibleWhen(this.torches::getValue);
   private final BooleanSetting baseScan = new BooleanSetting("BaseScan", true);
   private final NumberSetting baseScanIntervalMs = (NumberSetting)new NumberSetting("BaseScanIntervalMs", 10000.0, 2000.0, 60000.0, 500.0)
      .visibleWhen(this.baseScan::getValue);
   private final NumberSetting baseScanRadius = (NumberSetting)new NumberSetting("BaseScanRadius", 96.0, 16.0, 512.0, 16.0)
      .visibleWhen(this.baseScan::getValue);
   private final NumberSetting baseScanMinConfidence = (NumberSetting)new NumberSetting("BaseScanMinConfidence", 40.0, 1.0, 500.0, 1.0)
      .visibleWhen(this.baseScan::getValue);
   private final BooleanSetting autoStopOnBaseFind = (BooleanSetting)new BooleanSetting("AutoStopOnBaseFind", true).visibleWhen(this.baseScan::getValue);
   private final BooleanSetting baseScanAlertSound = (BooleanSetting)new BooleanSetting("BaseAlertSound", true).visibleWhen(this.baseScan::getValue);
   private final BooleanSetting debug = new BooleanSetting("Debug", false);
   private AutoTunnel.State autoTunnelState = AutoTunnel.State.IDLE;
   private BlockPos class2338;
   private Direction class2350;
   private long longVal;
   private BlockPos class23382;
   private Direction class23502;
   private int intVal;
   private int intVal2;
   private int intVal3 = -1;
   private int intVal4 = -1;
   private long longVal2 = 0L;
   private long longVal3 = 0L;
   private static final long longVal4 = 60L;
   private int intVal5 = 0;
   private final Map<Long, Long> map = new HashMap<>();
   private final Map<Long, Long> map2 = new HashMap<>();
   private static final long longVal5 = 3000L;
   private static final double doubleVal = 16.0;
   private static final double doubleVal2 = 1000.0;
   private static final double doubleVal3 = 0.01;
   private AutoTunnel.ExploitState autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
   private BlockPos class23383;
   private Direction class23503;
   private long longVal6;
   private long longVal7;
   private long longVal8;
   private double doubleVal4;
   private int intVal6;
   private int intVal7;
   private int intVal8 = -1;
   private boolean bool;
   private boolean bool2;
   private long longVal9;
   private long longVal10;
   private int intVal9;
   private static final int intVal10 = 20;
   private long longVal11;
   private final Set<Long> set = new HashSet<>();

   public AutoTunnel() {
      super("AutoTunnel", "Break blocks — vanilla LMB-hold OR Grim-snap exploit cadence", Category.WORLD);
      this.run6(
         new Setting[]{
            this.exploitMode,
            this.autoAim,
            this.walkForward,
            this.autoTool,
            this.stopIfLava,
            this.stopIfInvFull,
            this.breakDelayMs,
            this.crosshairStableTicks,
            this.releaseOnBreakComplete,
            this.exploitCadenceMs,
            this.exploitSpeedSafetyMs,
            this.exploitPositionGapTicks,
            this.tunnelHeight,
            this.tunnelWidth,
            this.fallbackSlotSwap,
            this.fallbackVehicleRide,
            this.fallbackLookOnlyFlying,
            this.showSelection,
            this.stuckTimeoutMs,
            this.blacklistDurationMs,
            this.torches,
            this.torchInterval,
            this.baseScan,
            this.baseScanIntervalMs,
            this.baseScanRadius,
            this.baseScanMinConfidence,
            this.autoStopOnBaseFind,
            this.baseScanAlertSound,
            this.debug
         }
      );
   }

   @Override
   public void run() {
      this.autoTunnelState = AutoTunnel.State.IDLE;
      this.class2338 = null;
      this.class2350 = null;
      this.class23382 = null;
      this.class23502 = null;
      this.intVal = 0;
      this.longVal = 0L;
      this.intVal2 = 0;
      this.intVal3 = -1;
      this.intVal4 = -1;
      this.longVal2 = 0L;
      this.longVal3 = 0L;
      this.intVal5 = 0;
      this.map.clear();
      this.map2.clear();
      this.autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
      this.class23383 = null;
      this.class23503 = null;
      this.longVal6 = 0L;
      this.longVal7 = 0L;
      this.longVal8 = 0L;
      this.doubleVal4 = 0.0;
      this.intVal6 = 0;
      this.intVal7 = 0;
      this.intVal8 = -1;
      this.bool = false;
      this.bool2 = this.exploitMode.getValue();
      this.intVal9 = 0;
      long var1 = System.currentTimeMillis();
      this.longVal11 = var1 + 5000L;
      this.longVal10 = 0L;
      this.set.clear();
      if (this.baseScan.getValue()) {
         d$aUtils.run(true, true);
         this.run23(
            "BaseScan armed — interval="
               + this.baseScanIntervalMs.getValueInt()
               + "ms radius="
               + this.baseScanRadius.getValueInt()
               + "b minConf="
               + this.baseScanMinConfidence.getValueInt()
               + " autoStop="
               + this.autoStopOnBaseFind.getValue()
               + " sound="
               + this.baseScanAlertSound.getValue()
               + " (first poll in ~5 s)"
         );
      }

      AntiAFKModuleUtil.run2();
      if (this.exploitMode.getValue()) {
         if (!InputUtil$class_306Utils.check("AutoTunnel")) {
            String var3 = InputUtil$class_306Utils.getString();
            NotificationUtils.run8("AutoTunnel", "Attack key busy (null) — disable that first", INFO.UNKNOWN_3);
            this.run5(false);
            return;
         }

         m$aUtils.run5();
         this.run23(
            "enabled — mode=GRIM_SNAP (attack channel acquired, vanilla-driven) cadence="
               + this.exploitCadenceMs.getValueInt()
               + "ms safety="
               + this.exploitSpeedSafetyMs.getValueInt()
               + "ms fallbacks=[SlotSwap="
               + this.fallbackSlotSwap.getValue()
               + " VehicleRide="
               + this.fallbackVehicleRide.getValue()
               + " LookOnlyFlying="
               + this.fallbackLookOnlyFlying.getValue()
               + "] tunnel="
               + this.tunnelHeight.getValueInt()
               + "x"
               + this.tunnelWidth.getValueInt()
         );
      } else {
         if (!InputUtil$class_306Utils.check("AutoTunnel")) {
            String var4 = InputUtil$class_306Utils.getString();
            NotificationUtils.run8("AutoTunnel", "Attack key busy (null) — disable that first", INFO.UNKNOWN_3);
            this.run5(false);
            return;
         }

         m$aUtils.run5();
         this.run23("enabled — mode=VANILLA (attack channel acquired)");
      }
   }

   @Override
   public void run2() {
      if (this.autoTunnelExploitState != AutoTunnel.ExploitState.IDLE && this.class23383 != null && this.class23503 != null) {
         m$aUtils.run5();
         this.run23("disable — vanilla ABORT " + stringOf(this.class23383) + " face=" + this.class23503 + " (state was " + this.autoTunnelExploitState + ")");
      }

      AutoTunnelUtil2.run3("AutoTunnel");
      if (this.bool && this.intVal8 >= 0) {
         this.run12();
      }

      InputUtil$class_306Utils.run3("AutoTunnel");
      m$aUtils.run5();
      this.run20();
      this.run25();
      AntiAFKModuleUtil.run2();
      this.autoTunnelState = AutoTunnel.State.IDLE;
      this.class2338 = null;
      this.class2350 = null;
      this.class23382 = null;
      this.class23502 = null;
      this.intVal = 0;
      this.intVal5 = 0;
      this.autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
      this.class23383 = null;
      this.class23503 = null;
      this.bool = false;
      this.intVal8 = -1;
      this.intVal9 = 0;
      this.set.clear();
      this.run23("disabled");
   }

   @Override
   public void run3() {
      if (class310.player != null
         && class310.world != null
         && class310.interactionManager != null
         && class310.getNetworkHandler() != null
         && class310.options != null) {
         boolean var1 = this.exploitMode.getValue();
         if (var1 != this.bool2) {
            this.run5(var1);
            this.bool2 = var1;
         }

         if (var1) {
            this.run4();
         } else {
            this.run14();
         }
      }
   }

   public void run5_nf(boolean var1) {
      if (var1) {
         AutoTunnelUtil2.run2("AutoTunnel");
         if (!InputUtil$class_306Utils.check("AutoTunnel")) {
            this.run23("mode toggle → GRIM_SNAP but attack key busy — disabling");
            this.run5(false);
            return;
         }

         m$aUtils.run5();
         this.autoTunnelState = AutoTunnel.State.IDLE;
         this.class2338 = null;
         this.class2350 = null;
         this.class23382 = null;
         this.class23502 = null;
         this.intVal = 0;
         this.autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
         this.class23383 = null;
         this.class23503 = null;
         this.bool = false;
         this.run23("mode toggle → GRIM_SNAP (state reset)");
      } else {
         if (this.autoTunnelExploitState != AutoTunnel.ExploitState.IDLE && this.class23383 != null && this.class23503 != null) {
            m$aUtils.run5();
         }

         if (this.bool && this.intVal8 >= 0) {
            this.run12();
         }

         this.autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
         this.class23383 = null;
         this.class23503 = null;
         if (!InputUtil$class_306Utils.check("AutoTunnel")) {
            this.run23("mode toggle → VANILLA but attack key busy — disabling");
            this.run5(false);
            return;
         }

         this.autoTunnelState = AutoTunnel.State.IDLE;
         this.run23("mode toggle → VANILLA (attack channel reacquired)");
      }
   }

   private void run4() {
      long var1 = System.currentTimeMillis();
      this.run22(var1);
      this.run19(var1);
      if (this.stopIfLava.getValue() && this.isEnabled()) {
         NotificationUtils.run8("AutoTunnel", "Lava/water in path — stopping", INFO.UNKNOWN_3);
         this.run23("safety-exit LAVA");
         this.run5(false);
      } else if (this.stopIfInvFull.getValue() && this.isEnabled2()) {
         NotificationUtils.run8("AutoTunnel", "Inventory full — stopping", INFO.UNKNOWN_3);
         this.run23("safety-exit INV_FULL");
         this.run5(false);
      } else {
         Direction var3 = class310.player.getHorizontalFacing();
         boolean var4 = this.check2(var3);
         boolean var5 = this.autoTunnelExploitState == AutoTunnel.ExploitState.HOLD
            || this.autoTunnelExploitState == AutoTunnel.ExploitState.AIM_SEED
            || this.autoTunnelExploitState == AutoTunnel.ExploitState.DIG_START;
         boolean var6 = var4 && !var5;
         if (this.walkForward.getValue() && !var6) {
            class310.options.forwardKey.setPressed(true);
         } else {
            this.run20();
         }

         if (this.showSelection.getValue() && this.class23383 != null) {
            this.run26(this.class23383);
         }

         boolean var7 = this.fallbackVehicleRide.getValue() && class310.player.hasVehicle();
         switch (this.autoTunnelExploitState) {
            case IDLE:
               this.run6(var1);
               break;
            case AIM_SEED:
               this.run7(var1, var7);
               break;
            case DIG_START:
               this.run8(var1, var7);
               break;
            case HOLD:
               this.run9(var1);
               break;
            case COOLDOWN:
               this.run10(var1);
         }

         if (this.autoTunnelExploitState == AutoTunnel.ExploitState.HOLD || this.autoTunnelExploitState == AutoTunnel.ExploitState.COOLDOWN) {
            this.run16(var1);
         }
      }
   }

   private void run6(long var1) {
      Optional var3 = OptionalUtils.optionalOf2(class310.player, this.tunnelHeight.getValueInt(), this.tunnelWidth.getValueInt(), this::check5);
      if (var3.isEmpty()) {
         this.intVal9++;
         this.run24("no mineable target in tunnel geometry — walking (empty=" + this.intVal9 + "/" + 20 + ")", var1, 1000L);
         if (this.intVal9 >= 20) {
            NotificationUtils.run("AutoTunnel", "Tunnel dead-end — check surroundings", INFO.UNKNOWN_3, 6000L);
            this.run23("AutoMode dead-end — " + this.intVal9 + " empty ticks, disabling");
            this.run5(false);
         }
      } else {
         this.intVal9 = 0;
         BlockPos var4 = ((OptionalUtils.Inner1)var3.get()).target();
         Vec3d var5 = class310.player.getEyePos();
         double var6 = var5.squaredDistanceTo(Vec3d.ofCenter(var4));
         if (var6 > 16.0) {
            this.run24("target " + stringOf(var4) + " distSq=" + stringOf2(var6) + " out-of-reach — waiting", var1, 500L);
         } else {
            this.class23383 = var4;
            this.class23503 = ((OptionalUtils.Inner1)var3.get()).hitFace();
            this.intVal7 = 0;
            this.bool = false;
            this.intVal8 = -1;
            boolean var8 = false;
            if (this.autoTool.getValue()) {
               BlockState var9 = class310.world.getBlockState(this.class23383);
               var8 = this.check6(var9);
            }

            if (var8) {
               this.longVal3 = var1;
            }

            if (this.longVal3 > 0L && var1 - this.longVal3 < 60L) {
               this.run23(
                  "IDLE→pending-ACK target=" + stringOf(this.class23383) + " face=" + this.class23503 + " remaining=" + (60L - (var1 - this.longVal3)) + "ms"
               );
            } else {
               this.autoTunnelExploitState = AutoTunnel.ExploitState.AIM_SEED;
               this.run23("IDLE→AIM_SEED target=" + stringOf(this.class23383) + " face=" + this.class23503 + " path=" + this.stringOf4(false));
            }
         }
      }
   }

   private void run7(long var1, boolean var3) {
      if (this.class23383 != null && this.class23503 != null) {
         AutoTunnelUtil3.run3(Hand.MAIN_HAND);
         if (var3) {
            this.run23("AIM_SEED skipped — vehicle exemption active (RotationBreak returns early)");
            this.autoTunnelExploitState = AutoTunnel.ExploitState.DIG_START;
         } else {
            Vec3d var4 = class310.player.getEyePos();
            float[] var5 = AutoTunnelUtil.floatArrayOf(var4, this.class23383, this.class23503);
            AutoTunnelUtil3.run(var5[0], var5[1], class310.player.isOnGround());
            this.intVal6++;
            this.autoTunnelExploitState = AutoTunnel.ExploitState.DIG_START;
            this.run23(
               "AIM_SEED sent look=("
                  + stringOf2(var5[0])
                  + ", "
                  + stringOf2(var5[1])
                  + ") target="
                  + stringOf(this.class23383)
                  + " path="
                  + this.stringOf4(false)
            );
         }
      } else {
         this.autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
      }
   }

   private void run8(long var1, boolean var3) {
      if (this.class23383 == null || this.class23503 == null) {
         this.autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
      } else if (class310.world.getBlockState(this.class23383).isAir()) {
         this.run23("DIG_START abort — target " + stringOf(this.class23383) + " already air");
         this.class23383 = null;
         this.class23503 = null;
         this.autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
      } else {
         AutoTunnelUtil3.run3(Hand.MAIN_HAND);
         if (!var3) {
            AutoTunnelUtil3.run(class310.player.getYaw(), class310.player.getPitch(), class310.player.isOnGround());
            this.intVal6++;
         }

         if (!AutoTunnelUtil2.check("AutoTunnel", this.class23383)) {
            this.run23("DIG_START deferred — AttackChannel rearm cooldown active");
         } else {
            this.longVal6 = var1;
            this.doubleVal4 = this.doubleOf(this.class23383);
            double var4 = this.doubleOf2(this.doubleVal4);
            this.intVal7 = 0;
            this.longVal = var1;
            this.autoTunnelExploitState = AutoTunnel.ExploitState.HOLD;
            this.run23(
               "DIG_START vanilla-attackKey target="
                  + stringOf(this.class23383)
                  + " face="
                  + this.class23503
                  + " dmg/tick="
                  + stringOf3(this.doubleVal4)
                  + " predicted="
                  + stringOf2(var4)
                  + "ms path="
                  + this.stringOf4(var3)
            );
         }
      }
   }

   private void run9(long var1) {
      if (this.class23383 != null && this.class23503 != null) {
         long var3 = var1 - this.longVal;
         if (var3 > this.stuckTimeoutMs.getValueInt()) {
            this.run23(
               "HOLD stuck target="
                  + stringOf(this.class23383)
                  + " age="
                  + var3
                  + "ms → vanilla ABORT + blacklist "
                  + this.blacklistDurationMs.getValueInt()
                  + "ms"
            );
            this.run21(this.class23383, this.blacklistDurationMs.getValueInt());
            AutoTunnelUtil2.run2("AutoTunnel");
            m$aUtils.run5();
            AutoTunnelUtil2.run6(2);
            if (this.bool && this.intVal8 >= 0) {
               this.run12();
            }

            this.class23383 = null;
            this.class23503 = null;
            this.longVal8 = var1 + this.exploitCadenceMs.getValueLong();
            this.autoTunnelExploitState = AutoTunnel.ExploitState.COOLDOWN;
         } else if (class310.world.getBlockState(this.class23383).isAir()) {
            this.run23("HOLD server-air on " + stringOf(this.class23383) + " realMs=" + (var1 - this.longVal6) + " (predicted-early break)");
            this.run13(var1, class310.player.getHorizontalFacing());
            if (this.bool && this.intVal8 >= 0) {
               this.run12();
            }

            AutoTunnelUtil2.run2("AutoTunnel");
            AutoTunnelUtil2.run6(1 + AutoTunnelHelper.getk().intOf3(0, 2));
            SplittableRandomUtils.run4(this.class23383);
            this.class23383 = null;
            this.class23503 = null;
            this.longVal8 = var1 + this.exploitCadenceMs.getValueLong();
            this.autoTunnelExploitState = AutoTunnel.ExploitState.COOLDOWN;
         } else {
            AutoTunnelUtil3.run3(Hand.MAIN_HAND);
            AutoTunnelUtil2.check("AutoTunnel", this.class23383);
            if (this.fallbackLookOnlyFlying.getValue()) {
               AutoTunnelUtil3.run(class310.player.getYaw(), class310.player.getPitch(), class310.player.isOnGround());
               this.intVal6++;
            }

            if (this.intVal6 >= this.exploitPositionGapTicks.getValueInt()) {
               AutoTunnelUtil3.run2(new Vec3d(class310.player.getX(), class310.player.getY(), class310.player.getZ()), class310.player.isOnGround());
               this.intVal6 = 0;
               this.run23("HOLD position-only sent (BadPacketsE safety, tick " + this.intVal7 + ")");
            }

            if (this.fallbackSlotSwap.getValue() && this.intVal7 == 2 && !this.bool) {
               this.run11();
            }

            if (this.bool && this.intVal7 >= 4) {
               this.run12();
            }

            double var5 = this.doubleOf2(this.doubleVal4);
            long var7 = var1 - this.longVal6;
            long var9 = Math.max(50L, (long)var5 - this.exploitSpeedSafetyMs.getValueLong());
            if (var7 < var9) {
               this.intVal7++;
               if (this.debug.getValue() && this.intVal7 % 5 == 0) {
                  this.run23("HOLD tick=" + this.intVal7 + " realMs=" + var7 + " predicted=" + stringOf2(var5) + " threshold=" + var9 + " swap=" + this.bool);
               }
            } else {
               AutoTunnelUtil2.run2("AutoTunnel");
               AutoTunnelUtil2.run6(1 + AutoTunnelHelper.getk().intOf3(0, 2));
               this.longVal7 = var1;
               this.run13(var1, class310.player.getHorizontalFacing());
               if (this.bool && this.intVal8 >= 0) {
                  this.run12();
               }

               long var11 = this.exploitCadenceMs.getValueLong();
               this.longVal8 = var1 + var11;
               this.autoTunnelExploitState = AutoTunnel.ExploitState.COOLDOWN;
               this.run23(
                  "HOLD→COOLDOWN vanilla-STOP target="
                     + stringOf(this.class23383)
                     + " realMs="
                     + var7
                     + " predicted="
                     + stringOf2(var5)
                     + " diff="
                     + (var5 - var7)
                     + " cadence-wait="
                     + var11
                     + "ms"
               );
               this.class23383 = null;
               this.class23503 = null;
            }
         }
      } else {
         this.autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
      }
   }

   private void run10(long var1) {
      AutoTunnelUtil3.run3(Hand.MAIN_HAND);
      if (this.fallbackLookOnlyFlying.getValue()) {
         AutoTunnelUtil3.run(class310.player.getYaw(), class310.player.getPitch(), class310.player.isOnGround());
         this.intVal6++;
      }

      if (this.intVal6 >= this.exploitPositionGapTicks.getValueInt()) {
         AutoTunnelUtil3.run2(new Vec3d(class310.player.getX(), class310.player.getY(), class310.player.getZ()), class310.player.isOnGround());
         this.intVal6 = 0;
         this.run23("COOLDOWN position-only sent (BadPacketsE safety)");
      }

      if (var1 >= this.longVal8) {
         long var3 = this.exploitCadenceMs.getValueLong();
         this.run23("COOLDOWN→IDLE elapsed (waited " + var3 + "ms)");
         this.autoTunnelExploitState = AutoTunnel.ExploitState.IDLE;
      }
   }

   private void run11() {
      if (class310.player != null && this.class23383 != null) {
         BlockState var1 = class310.world.getBlockState(this.class23383);
         PlayerInventory var2 = class310.player.getInventory();
         int var3 = var2.getSelectedSlot();
         double var4 = var2.getStack(var3).getMiningSpeedMultiplier(var1);
         int var6 = var3;
         double var7 = var4;

         for (int var9 = 0; var9 < PlayerInventory.getHotbarSize(); var9++) {
            if (var9 != var3) {
               ItemStack var10 = var2.getStack(var9);
               if (!var10.isEmpty()) {
                  double var11 = var10.getMiningSpeedMultiplier(var1);
                  if (var11 > var7) {
                     var7 = var11;
                     var6 = var9;
                  }
               }
            }
         }

         if (var6 == var3) {
            this.run23("SlotSwap fallback: no faster tool available (cur speed=" + stringOf2(var4) + ")");
         } else {
            this.intVal8 = var3;
            this.bool = true;
            var2.setSelectedSlot(var6);
            class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var6));
            this.run23("SlotSwap arm cur=" + var3 + "→" + var6 + " speed=" + stringOf2(var4) + "→" + stringOf2(var7) + " (Grim max-inflates on next FLYING)");
         }
      }
   }

   private void run12() {
      if (this.bool && this.intVal8 >= 0 && class310.player != null) {
         int var1 = this.intVal8;
         PlayerInventory var2 = class310.player.getInventory();
         int var3 = var2.getSelectedSlot();
         if (var3 != var1 && var1 < PlayerInventory.getHotbarSize()) {
            var2.setSelectedSlot(var1);
            if (class310.getNetworkHandler() != null) {
               class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
            }

            this.run23("SlotSwap revert " + var3 + "→" + var1);
         }

         this.bool = false;
         this.intVal8 = -1;
      } else {
         this.bool = false;
         this.intVal8 = -1;
      }
   }

   private double doubleOf(BlockPos var1) {
      if (class310.player != null && class310.world != null) {
         try {
            BlockState var2 = class310.world.getBlockState(var1);
            float var3 = var2.calcBlockBreakingDelta(class310.player, class310.world, var1);
            return Math.max(1.0E-4, (double)var3);
         } catch (Throwable var4) {
            return 0.05;
         }
      } else {
         return 0.05;
      }
   }

   private double doubleOf2(double var1) {
      if (var1 >= 1.0) {
         return 50.0;
      } else {
         double var3 = Math.ceil(1.0 / var1);
         return var3 * 50.0;
      }
   }

   private void run13(long var1, Direction var3) {
      if (this.class23383 != null) {
         this.intVal2++;
         this.map2.put(this.class23383.asLong(), var1);
         this.run23("BROKE " + stringOf(this.class23383) + " total=" + this.intVal2);
         if (this.torches.getValue()) {
            int var4 = Math.max(1, this.torchInterval.getValueInt());
            if (this.intVal2 % var4 == 0 && this.intVal2 != this.intVal3 && this.check7(var3)) {
               this.intVal3 = this.intVal2;
            }
         }
      }

      this.longVal2 = var1;
   }

   private void run14() {
      if (!InputUtil$class_306Utils.check3("AutoTunnel") && !InputUtil$class_306Utils.check("AutoTunnel")) {
         if (this.debug.getValue()) {
            System.out.println("[AutoTunnel] channel owned by " + InputUtil$class_306Utils.getString() + " — skipping tick");
         }
      } else if (this.stopIfLava.getValue() && this.isEnabled()) {
         InputUtil$class_306Utils.check5("AutoTunnel");
         this.run20();
         NotificationUtils.run8("AutoTunnel", "Lava/water in path — stopping", INFO.UNKNOWN_3);
         this.run5(false);
      } else if (this.stopIfInvFull.getValue() && this.isEnabled2()) {
         InputUtil$class_306Utils.check5("AutoTunnel");
         this.run20();
         NotificationUtils.run8("AutoTunnel", "Inventory full — stopping", INFO.UNKNOWN_3);
         this.run5(false);
      } else {
         long var1 = System.currentTimeMillis();
         this.run22(var1);
         this.run19(var1);
         this.run16(var1);
         if (this.isEnabled3()) {
            Direction var3 = class310.player.getHorizontalFacing();
            boolean var4 = this.autoTunnelState == AutoTunnel.State.BREAKING || this.check2(var3);
            if (this.walkForward.getValue() && !var4) {
               class310.options.forwardKey.setPressed(true);
            } else {
               this.run20();
            }

            if (this.intVal5 > 0) {
               this.intVal5--;
               InputUtil$class_306Utils.check5("AutoTunnel");
               if (this.debug.getValue() && this.intVal5 == 0 && this.debug.getValue()) {
                  System.out.println("[AutoTunnel] post-abort hold-off expired — free to re-arm");
               }
            } else {
               if (this.autoAim.getValue()) {
                  Optional var5 = OptionalUtils.optionalOf2(class310.player, 2, 1, this::check5);
                  if (var5.isPresent()) {
                     OptionalUtils.Inner1 var6 = (OptionalUtils.Inner1)var5.get();
                     Vec3d var7 = Vec3d.ofCenter(var6.target())
                        .add(var6.hitFace().getOffsetX() * 0.5, var6.hitFace().getOffsetY() * 0.5, var6.hitFace().getOffsetZ() * 0.5);
                     Vec3d var8 = class310.player.getEyePos();
                     double var9 = var7.x - var8.x;
                     double var11 = var7.y - var8.y;
                     double var13 = var7.z - var8.z;
                     double var15 = Math.sqrt(var9 * var9 + var13 * var13);
                     float var17 = (float)Math.toDegrees(Math.atan2(var13, var9)) - 90.0F;
                     float var18 = (float)(-Math.toDegrees(Math.atan2(var11, var15)));
                     class310.player.setYaw(var17);
                     class310.player.setPitch(var18);
                     if (this.debug.getValue() && var1 % 500L < 60L) {
                        System.out
                           .println(
                              "[AutoTunnel] AutoAim → "
                                 + stringOf(var6.target())
                                 + " face="
                                 + var6.hitFace()
                                 + " yaw="
                                 + stringOf2(var17)
                                 + " pitch="
                                 + stringOf2(var18)
                           );
                     }
                  }
               }

               BlockPos var19 = null;
               Direction var20 = null;
               boolean var21 = false;
               double var22 = Double.NaN;
               if (class310.crosshairTarget instanceof BlockHitResult var10 && var10.getType() == Type.BLOCK) {
                  BlockPos var29 = var10.getBlockPos();
                  Vec3d var12 = class310.player.getEyePos();
                  var22 = var12.squaredDistanceTo(var10.getPos());
                  boolean var32 = var22 <= 16.0;
                  boolean var14 = this.check3(var29);
                  boolean var35 = this.check4(var29);
                  if (var32 && var14 && !var35) {
                     var19 = var29.toImmutable();
                     var20 = var10.getSide();
                     var21 = true;
                  } else if (this.debug.getValue() && var1 % 500L < 60L) {
                     System.out
                        .println(
                           "[AutoTunnel] xhair-reject pos="
                              + stringOf(var29)
                              + " distSq="
                              + stringOf2(var22)
                              + " inReach="
                              + var32
                              + " breakable="
                              + var14
                              + " blacklisted="
                              + var35
                        );
                  }
               }

               if (this.showSelection.getValue() && var19 != null) {
                  this.run26(var19);
               }

               if (this.autoTunnelState != AutoTunnel.State.BREAKING) {
                  long var27 = var1 - this.longVal2;
                  long var31 = this.breakDelayMs.getValueLong();
                  if (var31 > 0L && this.longVal2 > 0L && var27 < var31) {
                     InputUtil$class_306Utils.check5("AutoTunnel");
                     if (this.debug.getValue() && var1 % 500L < 60L) {
                        System.out.println("[AutoTunnel] gate " + var27 + "/" + var31 + "ms");
                     }
                  } else if (!var21) {
                     InputUtil$class_306Utils.check5("AutoTunnel");
                  } else {
                     BlockState var34 = class310.world.getBlockState(var19);
                     boolean var36 = false;
                     if (this.autoTool.getValue()) {
                        var36 = this.check6(var34);
                     }

                     if (var36) {
                        this.longVal3 = var1;
                     }

                     this.class2338 = var19;
                     this.class2350 = var20;
                     this.longVal = var1;
                     this.class23382 = null;
                     this.class23502 = null;
                     this.intVal = 0;
                     this.autoTunnelState = AutoTunnel.State.BREAKING;
                     if (var36) {
                        InputUtil$class_306Utils.check5("AutoTunnel");
                        if (this.debug.getValue()) {
                           System.out.println("[AutoTunnel] ARM " + stringOf(this.class2338) + " face=" + this.class2350 + " (tool-swap defer press)");
                        }
                     } else if (this.longVal3 > 0L && var1 - this.longVal3 < 60L) {
                        InputUtil$class_306Utils.check5("AutoTunnel");
                        if (this.debug.getValue()) {
                           System.out
                              .println(
                                 "[AutoTunnel] ARM "
                                    + stringOf(this.class2338)
                                    + " face="
                                    + this.class2350
                                    + " (waiting AutoTool ACK "
                                    + (60L - (var1 - this.longVal3))
                                    + "ms)"
                              );
                        }
                     } else {
                        boolean var16 = InputUtil$class_306Utils.check2("AutoTunnel");
                        if (this.debug.getValue()) {
                           System.out
                              .println(
                                 "[AutoTunnel] ARM+PRESS "
                                    + stringOf(this.class2338)
                                    + " face="
                                    + this.class2350
                                    + " pressed="
                                    + var16
                                    + " (since-break="
                                    + var27
                                    + "ms, gate="
                                    + var31
                                    + "ms)"
                              );
                        }
                     }
                  }
               } else {
                  if (this.class2338 != null) {
                     long var24 = var1 - this.longVal;
                     if (var24 > this.stuckTimeoutMs.getValueInt()) {
                        if (this.debug.getValue()) {
                           System.out
                              .println(
                                 "[AutoTunnel] STUCK "
                                    + stringOf(this.class2338)
                                    + " age="
                                    + var24
                                    + "ms → blacklist "
                                    + this.blacklistDurationMs.getValueInt()
                                    + "ms"
                              );
                        }

                        this.run21(this.class2338, this.blacklistDurationMs.getValueInt());
                        InputUtil$class_306Utils.check5("AutoTunnel");
                        this.intVal5 = 1;
                        this.run15(var3, var1, false);
                        return;
                     }

                     if (this.check(var3, var1)) {
                        if (this.releaseOnBreakComplete.getValue()) {
                           InputUtil$class_306Utils.check5("AutoTunnel");
                           this.autoTunnelState = AutoTunnel.State.IDLE;
                           if (this.debug.getValue()) {
                              System.out.println("[AutoTunnel] release-between-blocks → IDLE");
                           }

                           return;
                        }

                        if (this.debug.getValue()) {
                           System.out.println("[AutoTunnel] chain-break — attackKey stays held");
                        }
                     }

                     if (this.class2338 != null) {
                        Vec3d var30 = class310.player.getEyePos();
                        double var33 = var30.squaredDistanceTo(Vec3d.ofCenter(this.class2338));
                        if (var33 > 16.0) {
                           if (this.debug.getValue()) {
                              System.out.println("[AutoTunnel] out-of-reach " + stringOf(this.class2338) + " distSq=" + stringOf2(var33) + " → abandon");
                           }

                           InputUtil$class_306Utils.check5("AutoTunnel");
                           this.intVal5 = 1;
                           this.run15(var3, var1, false);
                           return;
                        }
                     }
                  }

                  if (!var21) {
                     InputUtil$class_306Utils.check4("AutoTunnel");
                     this.class23382 = null;
                     this.class23502 = null;
                     this.intVal = 0;
                     if (this.debug.getValue() && var1 % 500L < 60L) {
                        System.out.println("[AutoTunnel] HOLD-INVALID currentTarget=" + stringOf(this.class2338) + " (waiting for crosshair to return)");
                     }
                  } else {
                     if (this.class2338 == null) {
                        this.class2338 = var19;
                        this.class2350 = var20;
                        this.longVal = var1;
                        InputUtil$class_306Utils.check4("AutoTunnel");
                        if (this.debug.getValue()) {
                           System.out
                              .println(
                                 "[AutoTunnel] CHAIN-ARM "
                                    + stringOf(this.class2338)
                                    + " face="
                                    + this.class2350
                                    + " (vanilla fallback START on cooldown clear)"
                              );
                        }
                     } else if (var19.equals(this.class2338)) {
                        this.class2350 = var20;
                        this.class23382 = null;
                        this.class23502 = null;
                        this.intVal = 0;
                        InputUtil$class_306Utils.check4("AutoTunnel");
                        if (this.debug.getValue() && var1 % 500L < 60L) {
                           ClientPlayerInteractionManagerAccessor var25 = (ClientPlayerInteractionManagerAccessor)class310.interactionManager;
                           System.out
                              .println(
                                 "[AutoTunnel] HOLD "
                                    + stringOf(this.class2338)
                                    + " face="
                                    + this.class2350
                                    + " vProgress="
                                    + stringOf2(var25.nyx$getCurrentBreakingProgress())
                                    + " vCooldown="
                                    + var25.nyx$getBlockBreakingCooldown()
                                    + " age="
                                    + (var1 - this.longVal)
                                    + "ms"
                              );
                        }
                     } else {
                        if (this.class23382 != null && var19.equals(this.class23382)) {
                           this.intVal++;
                           this.class23502 = var20;
                        } else {
                           this.class23382 = var19;
                           this.class23502 = var20;
                           this.intVal = 1;
                        }

                        int var26 = this.crosshairStableTicks.getValueInt();
                        if (this.intVal >= var26) {
                           if (this.debug.getValue()) {
                              System.out
                                 .println(
                                    "[AutoTunnel] SWITCH "
                                       + stringOf(this.class2338)
                                       + " → "
                                       + stringOf(this.class23382)
                                       + " (stable "
                                       + this.intVal
                                       + "/"
                                       + var26
                                       + "t)"
                                 );
                           }

                           this.class2338 = this.class23382;
                           this.class2350 = this.class23502;
                           this.longVal = var1;
                           this.class23382 = null;
                           this.class23502 = null;
                           this.intVal = 0;
                           InputUtil$class_306Utils.check4("AutoTunnel");
                        } else {
                           InputUtil$class_306Utils.check4("AutoTunnel");
                           if (this.debug.getValue() && var1 % 500L < 60L) {
                              System.out
                                 .println(
                                    "[AutoTunnel] PIN "
                                       + stringOf(this.class2338)
                                       + " pending="
                                       + stringOf(this.class23382)
                                       + " ("
                                       + this.intVal
                                       + "/"
                                       + var26
                                       + ")"
                                 );
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean check(Direction var1, long var2) {
      if (this.class2338 == null) {
         return false;
      } else {
         m$aUtils.Inner1 var4 = m$aUtils.maOf(this.class2338);
         if (var4 != null && var4.state().isAir()) {
            if (this.debug.getValue()) {
               System.out.println("[AutoTunnel] ACK-air " + stringOf(this.class2338));
            }

            m$aUtils.run3(this.class2338);
            this.run15(var1, var2, true);
            return true;
         } else if (class310.world.getBlockState(this.class2338).isAir()) {
            if (this.debug.getValue()) {
               System.out.println("[AutoTunnel] local-air " + stringOf(this.class2338));
            }

            this.run15(var1, var2, true);
            return true;
         } else {
            return false;
         }
      }
   }

   private void run15(Direction var1, long var2, boolean var4) {
      if (var4 && this.class2338 != null) {
         this.intVal2++;
         this.map2.put(this.class2338.asLong(), var2);
         if (this.debug.getValue()) {
            System.out.println("[AutoTunnel] BROKE " + stringOf(this.class2338) + " total=" + this.intVal2);
         }

         if (this.torches.getValue()) {
            int var5 = Math.max(1, this.torchInterval.getValueInt());
            if (this.intVal2 % var5 == 0 && this.intVal2 != this.intVal3 && this.check7(var1)) {
               this.intVal3 = this.intVal2;
            }
         }
      }

      this.class2338 = null;
      this.class2350 = null;
      this.class23382 = null;
      this.class23502 = null;
      this.intVal = 0;
      this.longVal2 = var2;
   }

   private void run16(long var1) {
      if (this.baseScan.getValue()) {
         if (var1 - this.longVal11 >= this.baseScanIntervalMs.getValueLong()) {
            this.longVal11 = var1;
            d$aUtils.run(true, true);
            d$aUtils.Inner1 var3 = d$aUtils.daOf(class310.player, this.baseScanRadius.getValueInt(), true, true);
            if (var3 == null) {
               this.run18("poll: no scored chunks in " + this.baseScanRadius.getValueInt() + "b radius", var1);
            } else {
               int var4 = this.baseScanMinConfidence.getValueInt();
               if (var3.score() < var4) {
                  this.run18("poll: best chunk score=" + var3.score() + " < min=" + var4 + " (source=" + var3.source() + ")", var1);
               } else if (!this.set.add(var3.chunkKey())) {
                  this.run18("poll: chunk key=" + var3.chunkKey() + " score=" + var3.score() + " already alerted this session", var1);
               } else {
                  this.run17(var3, var1);
               }
            }
         }
      }
   }

   private void run17(d$aUtils.Inner1 var1, long var2) {
      int var4 = MapUtils.intOf(var1.chunkKey());
      int var5 = MapUtils.intOf2(var1.chunkKey());
      int var6 = (var4 << 4) + 8;
      int var7 = (var5 << 4) + 8;
      int var8 = (int)class310.player.getY();
      String var9 = d$aUtils.getString();
      String var10 = class310.world.getRegistryKey().getValue().toString();
      d$aUtils.run3(var2, var9, var10, var6, var8, var7, var1.score(), var1.source(), "basefinder_finds.json", "AutoTunnel");
      NotificationUtils.run("AutoTunnel", "Base at " + var6 + "," + var7 + " (score " + var1.score() + " via " + var1.source() + ")", INFO.UNKNOWN_2, 10000L);
      if (this.baseScanAlertSound.getValue()) {
         d$aUtils.run2();
      }

      this.run23(
         "BASE FOUND chunkKey="
            + var1.chunkKey()
            + " at "
            + var6
            + ","
            + var8
            + ","
            + var7
            + " score="
            + var1.score()
            + " source="
            + var1.source()
            + " (session finds="
            + this.set.size()
            + ")"
      );
      if (this.autoStopOnBaseFind.getValue()) {
         if (this.autoTunnelExploitState != AutoTunnel.ExploitState.IDLE && this.class23383 != null && this.class23503 != null) {
            AutoTunnelUtil2.run2("AutoTunnel");
            m$aUtils.run5();
            this.run23("BASE-STOP vanilla-ABORT " + stringOf(this.class23383) + " face=" + this.class23503 + " (state was " + this.autoTunnelExploitState + ")");
         }

         this.run5(false);
      }
   }

   private void run18(String var1, long var2) {
      if (this.debug.getValue()) {
         if (var2 - this.longVal10 >= 5000L) {
            this.longVal10 = var2;
            System.out.println("[AutoTunnel/BaseScan] null");
         }
      }
   }

   private void run19(long var1) {
      if (!this.map2.isEmpty()) {
         Iterator var3 = this.map2.entrySet().iterator();

         while (var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            long var5 = (Long)var4.getKey();
            long var7 = var1 - (Long)var4.getValue();
            if (var7 >= 3000L) {
               var3.remove();
            } else {
               BlockPos var9 = BlockPos.fromLong(var5);
               m$aUtils.Inner1 var10 = m$aUtils.maOf(var9);
               BlockState var11 = var10 != null ? var10.state() : class310.world.getBlockState(var9);
               if (!var11.isAir() && var11.getFluidState().isEmpty()) {
                  this.run21(var9, this.blacklistDurationMs.getValueInt());
                  var3.remove();
                  m$aUtils.run3(var9);
                  this.run23(
                     "ROLLBACK "
                        + stringOf(var9)
                        + " (dt="
                        + var7
                        + "ms, src="
                        + (var10 != null ? "ACK" : "world")
                        + ") → blacklist "
                        + this.blacklistDurationMs.getValueInt()
                        + "ms"
                  );
               }
            }
         }
      }
   }

   private void run20() {
      if (class310.options != null) {
         class310.options.forwardKey.setPressed(false);
      }
   }

   private boolean check2(Direction var1) {
      BlockPos var2 = class310.player.getBlockPos();
      BlockPos var3 = var2.offset(var1);
      BlockState var4 = class310.world.getBlockState(var3);
      if (!var4.isAir() && var4.getFluidState().isEmpty()) {
         return true;
      } else {
         BlockState var5 = class310.world.getBlockState(var3.up());
         return !var5.isAir() && var5.getFluidState().isEmpty();
      }
   }

   private boolean check3(BlockPos var1) {
      BlockState var2 = class310.world.getBlockState(var1);
      if (var2.isAir()) {
         return false;
      } else if (!var2.getFluidState().isEmpty()) {
         return false;
      } else {
         try {
            float var3 = var2.getHardness(class310.world, var1);
            if (var3 < 0.0F) {
               return false;
            }
         } catch (Throwable var4) {
         }

         return true;
      }
   }

   private void run21(BlockPos var1, long var2) {
      this.map.put(var1.asLong(), System.currentTimeMillis() + var2);
   }

   private boolean check4(BlockPos var1) {
      Long var2 = this.map.get(var1.asLong());
      return var2 != null && var2 > System.currentTimeMillis();
   }

   private boolean check5(long var1) {
      Long var3 = this.map.get(var1);
      return var3 != null && var3 > System.currentTimeMillis();
   }

   private void run22(long var1) {
      this.map.entrySet().removeIf(_e -> false);
   }

   private static String stringOf(BlockPos var0) {
      return var0 == null ? "-" : var0.getX() + "," + var0.getY() + "," + var0.getZ();
   }

   private static String stringOf2(double var0) {
      return String.format("%.2f", var0);
   }

   private static String stringOf3(double var0) {
      return String.format("%.4f", var0);
   }

   private String stringOf4(boolean var1) {
      StringBuilder var2 = new StringBuilder("GrimSnap");
      if (var1) {
         var2.append("+VehicleExempt");
      }

      if (this.fallbackSlotSwap.getValue()) {
         var2.append("+SlotSwap");
      }

      if (this.fallbackLookOnlyFlying.getValue()) {
         var2.append("+LookOnly");
      }

      if (this.fallbackVehicleRide.getValue() && !var1) {
         var2.append("+VehicleReady");
      }

      return var2.toString();
   }

   private void run23(String var1) {
      if (this.debug.getValue()) {
         System.out.println("[AutoTunnel] null");
      }
   }

   private void run24(String var1, long var2, long var4) {
      if (this.debug.getValue()) {
         if (var2 - this.longVal9 >= var4) {
            this.longVal9 = var2;
            System.out.println("[AutoTunnel] null");
         }
      }
   }

   private boolean check6(BlockState var1) {
      if (class310.player == null) {
         return false;
      } else {
         PlayerInventory var2 = class310.player.getInventory();
         int var3 = var2.getSelectedSlot();
         int var4 = var3;
         double var5 = Double.NEGATIVE_INFINITY;

         for (int var7 = 0; var7 < PlayerInventory.getHotbarSize(); var7++) {
            ItemStack var8 = var2.getStack(var7);
            double var9;
            if (var8.isEmpty()) {
               var9 = 1.0;
            } else {
               var9 = var8.getMiningSpeedMultiplier(var1);
               if (var8.isSuitableFor(var1)) {
                  var9 += 1000.0;
               }
            }

            if (var7 == var3) {
               var9 += 0.01;
            }

            if (var9 > var5) {
               var5 = var9;
               var4 = var7;
            }
         }

         if (var4 == var3) {
            return false;
         } else {
            if (this.intVal4 < 0) {
               this.intVal4 = var3;
            }

            var2.setSelectedSlot(var4);
            class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var4));
            if (this.debug.getValue()) {
               System.out.println("[AutoTunnel] tool-swap " + var3 + " → " + var4);
            }

            return true;
         }
      }
   }

   private void run25() {
      if (this.intVal4 >= 0 && class310.player != null) {
         if (this.intVal4 < PlayerInventory.getHotbarSize() && this.intVal4 != class310.player.getInventory().getSelectedSlot()) {
            class310.player.getInventory().setSelectedSlot(this.intVal4);
            if (class310.getNetworkHandler() != null) {
               class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.intVal4));
            }
         }

         this.intVal4 = -1;
      } else {
         this.intVal4 = -1;
      }
   }

   public boolean isEnabled() {
      if (class310.player != null && class310.world != null) {
         Direction var1 = class310.player.getHorizontalFacing();
         BlockPos var2 = class310.player.getBlockPos();

         for (int var3 = 1; var3 <= 2; var3++) {
            BlockPos var4 = var2.offset(var1, var3);

            for (int var5 = 0; var5 <= 1; var5++) {
               if (!class310.world.getBlockState(var4.up(var5)).getFluidState().isEmpty()) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isEnabled2() {
      PlayerInventory var1 = class310.player.getInventory();

      for (int var2 = 0; var2 < var1.size(); var2++) {
         if (var1.getStack(var2).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private boolean check7(Direction var1) {
      PlayerInventory var2 = class310.player.getInventory();
      int var3 = this.intOf(Items.TORCH);
      if (var3 < 0) {
         return false;
      } else {
         BlockPos var4 = class310.player.getBlockPos().offset(var1.getOpposite());
         BlockPos var5 = var4.down();
         BlockState var6 = class310.world.getBlockState(var5);
         if (var6.isAir()) {
            return false;
         } else if (!class310.world.getBlockState(var4).isAir()) {
            return false;
         } else {
            int var7 = var2.getSelectedSlot();
            if (var3 != var7) {
               if (this.intVal4 < 0) {
                  this.intVal4 = var7;
               }

               var2.setSelectedSlot(var3);
               class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var3));
            }

            Vec3d var8 = new Vec3d(var5.getX() + 0.5, var5.getY() + 1.0, var5.getZ() + 0.5);
            float[] var9 = AutoCrystalModuleUtil.floatArrayOf(var8);
            class310.player.setYaw(var9[0]);
            class310.player.setPitch(var9[1]);

            try {
               ((MinecraftClientInvoker)class310).nyx$doItemUse();
            } catch (Throwable var12) {
               BlockHitResult var11 = new BlockHitResult(var8, Direction.UP, var5, false);
               class310.interactionManager.interactBlock(class310.player, Hand.MAIN_HAND, var11);
            }

            class310.player.swingHand(Hand.MAIN_HAND);
            return true;
         }
      }
   }

   private int intOf(Item var1) {
      PlayerInventory var2 = class310.player.getInventory();

      for (int var3 = 0; var3 < PlayerInventory.getHotbarSize(); var3++) {
         ItemStack var4 = var2.getStack(var3);
         if (!var4.isEmpty() && var4.getItem() == var1) {
            return var3;
         }

         if (!var4.isEmpty() && var4.getItem() instanceof BlockItem var5 && var5.getBlock() == Blocks.TORCH) {
            return var3;
         }
      }

      return -1;
   }

   private void run26(BlockPos var1) {
      Box var2 = new Box(var1.getX(), var1.getY(), var1.getZ(), var1.getX() + 1.0, var1.getY() + 1.0, var1.getZ() + 1.0);
      ListUtils.run5(var2, -2145329040, 1.5F, false);
   }

   @Override
   public String getString3() {
      return "§7" + this.intVal2;
   }

   private static boolean check8(long var0, Entry var2) {
      return ((Long)var2.getValue()) <= var0;
   }

   private Boolean getBoolean6() {
      return !this.exploitMode.getValue();
   }

   private Boolean getBoolean() {
      return !this.exploitMode.getValue();
   }

   private Boolean getBoolean2() {
      return !this.exploitMode.getValue();
   }

   private static enum ExploitState {
      IDLE,
      AIM_SEED,
      DIG_START,
      HOLD,
      COOLDOWN;

      private static final AutoTunnel.ExploitState[] autoTunnelExploitStateArray = getAutoTunnelExploitStateArray();

      private static AutoTunnel.ExploitState[] getAutoTunnelExploitStateArray() {
         return new AutoTunnel.ExploitState[]{IDLE, AIM_SEED, DIG_START, HOLD, COOLDOWN};
      }
   }

   private static enum State {
      IDLE,
      BREAKING;

      private static final AutoTunnel.State[] autoTunnelStateArray = getAutoTunnelStateArray();

      private static AutoTunnel.State[] getAutoTunnelStateArray() {
         return new AutoTunnel.State[]{IDLE, BREAKING};
      }
   }
}

