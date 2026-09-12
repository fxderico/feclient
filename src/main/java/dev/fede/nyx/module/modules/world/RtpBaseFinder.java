package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.mixin.ClientPlayerInteractionManagerAccessor;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.tracker.ChunkActivityScanner;
import dev.fede.nyx.tracker.MapUtils;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.AntiDebugUtil;
import dev.fede.nyx.util.AutoTunnelUtil2;
import dev.fede.nyx.util.OptionalUtils;
import dev.fede.nyx.util.d$aUtils;
import dev.fede.nyx.util.m$aUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Type;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.chunk.WorldChunk;

public final class RtpBaseFinder extends Module {
   private final NumberSetting scanRadius = new NumberSetting("ScanRadiusBlocks", 96.0, 16.0, 512.0, 16.0);
   private final NumberSetting minConfidence = new NumberSetting("MinConfidence", 40.0, 1.0, 500.0, 1.0);
   private final BooleanSetting useTunnelScanner = new BooleanSetting("UseTunnelScanner", true);
   private final BooleanSetting useActivityScanner = new BooleanSetting("UseActivityScanner", true);
   private final NumberSetting minChests = new NumberSetting("MinChests", 10.0, 1.0, 200.0, 1.0);
   private final BooleanSetting minChestsEnforced = new BooleanSetting("MinChestsEnforced", true);
   private final NumberSetting approachTimeoutMs = new NumberSetting("ApproachTimeoutMs", 300000.0, 60000.0, 900000.0, 1000.0);
   private final NumberSetting targetY = new NumberSetting("TargetY", -30.0, -60.0, 320.0, 1.0);
   private final NumberSetting tunnelHeight = new NumberSetting("TunnelHeight", 2.0, 1.0, 3.0, 1.0);
   private final NumberSetting tunnelWidth = new NumberSetting("TunnelWidth", 1.0, 1.0, 3.0, 1.0);
   private final NumberSetting blocksBetweenTurns = new NumberSetting("BlocksBetweenTurns", 40.0, 10.0, 200.0, 1.0);
   private final NumberSetting maxTicksPerBlock = new NumberSetting("MaxTicksPerBlock", 30.0, 5.0, 80.0, 1.0);
   private final BooleanSetting silentRotation = new BooleanSetting("SilentRotation", true);
   private final BooleanSetting stopIfLava = new BooleanSetting("StopIfLava", true);
   private final BooleanSetting stopIfWater = new BooleanSetting("StopIfWater", false);
   private final NumberSetting lavaAvoidRadius = new NumberSetting("LavaAvoidRadius", 3.0, 1.0, 8.0, 1.0);
   private final BooleanSetting stopIfInventoryFull = new BooleanSetting("StopIfInventoryFull", false);
   private final NumberSetting sweepLegLength = new NumberSetting("SweepLegLength", 25.0, 10.0, 200.0, 1.0);
   private final NumberSetting lateralOffset = new NumberSetting("LateralOffset", 15.0, 1.0, 30.0, 1.0);
   private final NumberSetting maxSearchRadius = new NumberSetting("MaxSearchRadius", 300.0, 50.0, 2000.0, 10.0);
   private final BooleanSetting heatMapBias = new BooleanSetting("HeatMapBias", true);
   private final BooleanSetting visitedChunkAvoidance = new BooleanSetting("VisitedChunkAvoidance", true);
   private final StringSetting sweepPattern = new StringSetting("SweepPattern", "CrossHatch", 16);
   private final NumberSetting legLengthJitter = new NumberSetting("LegLengthJitter", 20.0, 0.0, 50.0, 1.0);
   private final NumberSetting crossHatchRows = new NumberSetting("CrossHatchRows", 4.0, 2.0, 20.0, 1.0);
   private final NumberSetting axisRotateAfterRows = new NumberSetting("AxisRotateAfterRows", 6.0, 0.0, 20.0, 1.0);
   private final BooleanSetting pauseOnFind = new BooleanSetting("PauseOnFind", true);
   private final BooleanSetting disconnectOnFind = new BooleanSetting("DisconnectOnFind", false);
   private final BooleanSetting alertSound = new BooleanSetting("AlertSound", true);
   private final StringSetting savePath = new StringSetting("SavePath", "codeengine/basefinder_finds.json", 256);
   private final BooleanSetting debug = new BooleanSetting("Debug", false);
   private RtpBaseFinder.Phase rtpBaseFinderPhase = RtpBaseFinder.Phase.IDLE;
   private int intVal = 0;
   private final List<Vec3d> list = new ArrayList<>();
   private boolean bool = false;
   private long longVal = 0L;
   private long longVal2 = 0L;
   private int intVal2;
   private int intVal3;
   private int intVal4;
   private d$aUtils.Inner1 daUtilsInner1;
   private int intVal5;
   private long longVal3 = 0L;
   private final Set<Long> set = new HashSet<>();
   private final Set<Long> set2 = new HashSet<>();
   private BlockPos class2338;
   private int intVal6;
   private final Set<Long> set3 = new HashSet<>();
   private final Set<Long> set4 = new HashSet<>();
   private int intVal7 = 0;
   private Float floatVal = null;
   private int intVal8 = -1;
   private RtpBaseFinder.SweepLeg rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LONG;
   private int intVal9 = 0;
   private int intVal10 = -1;
   private int intVal11 = -1;
   private boolean bool2 = true;
   private int intVal12 = 0;
   private int intVal13 = 0;
   private boolean bool3 = false;
   private int intVal14 = 0;
   private BlockPos class23382 = null;
   private final Set<Long> set5 = new HashSet<>();
   private long longVal4 = Long.MIN_VALUE;
   private final Map<Long, Integer> map = new HashMap<>();
   private int intVal15 = 0;
   private int intVal16 = 0;
   private static final String string = "RtpBaseFinder";
   private static final float floatVal2 = 12.0F;
   private static final int intVal17 = 3;
   private int intVal18 = 0;
   private int intVal19 = 0;
   private static final int intVal20 = 240;
   private static final long longVal5 = 60000L;
   private int intVal21 = 0;
   private int intVal22 = Integer.MIN_VALUE;
   private long longVal6 = 0L;
   private int intVal23 = Integer.MIN_VALUE;
   private int intVal24 = 0;
   private static final int[][] intArray = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
   private static final int intVal25 = 0;
   private static final int intVal26 = 1;
   private static final int intVal27 = 2;
   private static final int intVal28 = 3;
   private final Random random = new Random();

   public RtpBaseFinder() {
      super("TunnelBaseFinder", "Tunnel from current position, scan every tick. No /rtp.", Category.DONUTSMP);
      this.run6(
         new Setting[]{
            this.scanRadius,
            this.minConfidence,
            this.useTunnelScanner,
            this.useActivityScanner,
            this.minChests,
            this.minChestsEnforced,
            this.approachTimeoutMs,
            this.targetY,
            this.tunnelHeight,
            this.tunnelWidth,
            this.blocksBetweenTurns,
            this.maxTicksPerBlock,
            this.silentRotation,
            this.stopIfLava,
            this.stopIfWater,
            this.lavaAvoidRadius,
            this.stopIfInventoryFull,
            this.sweepLegLength,
            this.lateralOffset,
            this.maxSearchRadius,
            this.heatMapBias,
            this.visitedChunkAvoidance,
            this.sweepPattern,
            this.legLengthJitter,
            this.crossHatchRows,
            this.axisRotateAfterRows,
            this.pauseOnFind,
            this.disconnectOnFind,
            this.alertSound,
            this.savePath,
            this.debug
         }
      );
   }

   @Override
   public void run() {
      long var1 = System.currentTimeMillis();
      this.run5();

      try {
         d$aUtils.run(this.useTunnelScanner.getValue(), this.useActivityScanner.getValue());
      } catch (Throwable var5) {
         NotificationUtils.run(
            "TunnelBaseFinder", "Scanner activation threw: " + var5.getClass().getSimpleName() + " — continuing without scanner", INFO.UNKNOWN_4, 4000L
         );
      }

      if (class310.player != null && class310.world != null && class310.getNetworkHandler() != null) {
         BlockPos var3 = class310.player.getBlockPos();
         if (var3.getX() == 0 && var3.getY() == 0 && var3.getZ() == 0) {
            this.bool = true;
            this.longVal = var1;
            this.rtpBaseFinderPhase = RtpBaseFinder.Phase.PENDING_WARMUP;
            NotificationUtils.run("TunnelBaseFinder", "Armed - waiting for player position (sentinel origin)...", INFO.UNKNOWN, 3000L);
         } else {
            try {
               WorldChunk var4 = class310.world.getChunkManager().getWorldChunk(var3.getX() >> 4, var3.getZ() >> 4, false);
               if (var4 == null) {
                  this.bool = true;
                  this.longVal = var1;
                  this.rtpBaseFinderPhase = RtpBaseFinder.Phase.PENDING_WARMUP;
                  NotificationUtils.run("TunnelBaseFinder", "Armed - waiting for chunk to load...", INFO.UNKNOWN, 3000L);
                  return;
               }
            } catch (Throwable var6) {
               this.bool = true;
               this.longVal = var1;
               this.rtpBaseFinderPhase = RtpBaseFinder.Phase.PENDING_WARMUP;
               return;
            }

            this.run21(var3, var1);
         }
      } else {
         this.bool = true;
         this.longVal = var1;
         this.rtpBaseFinderPhase = RtpBaseFinder.Phase.PENDING_WARMUP;
         NotificationUtils.run("TunnelBaseFinder", "Armed - waiting for world to finish loading...", INFO.UNKNOWN, 3000L);
      }
   }

   private void run21(BlockPos var1, long var2) {
      int var4 = var1.getY();
      int var5 = this.targetY.getValueInt();
      int var6 = var4 - var5;
      RtpBaseFinder.Phase var7;
      if (var6 <= 2) {
         this.run8(var2, "start-at-target-y");
         var7 = RtpBaseFinder.Phase.TUNNELING;
      } else {
         this.run2(var2, var4, var5, "onEnable");
         var7 = RtpBaseFinder.Phase.DESCENDING;
      }

      NotificationUtils.run(
         "TunnelBaseFinder",
         "Search started at "
            + var1.toShortString()
            + " -> phase="
            + var7.name()
            + ", targetY="
            + var5
            + (var7 == RtpBaseFinder.Phase.DESCENDING ? " (need to drop " + var6 + " blocks)" : ""),
         INFO.UNKNOWN_2,
         4000L
      );

      try {
         NyxClient.LOGGER.info("[TunnelBaseFinder] enabled -> phase={}, currentY={}, targetY={}, dropNeeded={}", new Object[]{var7.name(), var4, var5, var6});
      } catch (Throwable var9) {
      }
   }

   private void run2(long var1, int var3, int var4, String var5) {
      this.intVal22 = var3;
      this.intVal23 = var3;
      this.longVal6 = var1;
      this.intVal21 = 0;
      this.intVal24 = 0;
      this.class2338 = null;
      this.intVal6 = 0;
      this.floatVal = null;
      this.intVal18 = 0;
      this.run14();
      this.run25(RtpBaseFinder.Phase.DESCENDING);
      if (this.debug.getValue()) {
         try {
            NyxClient.LOGGER.info("[TBF/DESCEND] enterDescending y={} target={} why={}", new Object[]{var3, var4, var5});
         } catch (Throwable var7) {
         }
      }
   }

   private void run3(long var1) {
      if (this.bool) {
         long var3 = var1 - this.longVal;
         if (class310.player != null && class310.world != null && class310.getNetworkHandler() != null) {
            BlockPos var5 = class310.player.getBlockPos();
            if (var5.getX() != 0 || var5.getY() != 0 || var5.getZ() != 0) {
               try {
                  WorldChunk var6 = class310.world.getChunkManager().getWorldChunk(var5.getX() >> 4, var5.getZ() >> 4, false);
                  if (var6 == null) {
                     return;
                  }
               } catch (Throwable var8) {
                  return;
               }

               try {
                  d$aUtils.run(this.useTunnelScanner.getValue(), this.useActivityScanner.getValue());
               } catch (Throwable var7) {
               }

               this.bool = false;
               this.run21(var5, var1);
            }
         } else {
            if (var3 > 30000L) {
               NotificationUtils.run("TunnelBaseFinder", "Warmup timeout (30s, no world) - disabling", INFO.UNKNOWN_4, 5000L);
               this.bool = false;
               this.run5(false);
            } else if (var3 > 10000L && this.intVal19 % 100 == 0) {
               NotificationUtils.run("TunnelBaseFinder", "Still waiting for world... (" + var3 / 1000L + "s)", INFO.UNKNOWN_3, 2000L);
            }
         }
      }
   }

   public void run4() {
      this.run24();
      this.rtpBaseFinderPhase = RtpBaseFinder.Phase.IDLE;
      this.run5();
   }

   @Override
   public String getString3() {
      return "§7" + this.intVal;
   }

   private void run5() {
      this.class2338 = null;
      this.intVal6 = 0;
      this.set3.clear();
      this.set4.clear();
      this.intVal7 = 0;
      this.floatVal = null;
      this.intVal18 = 0;
      this.intVal21 = 0;
      this.intVal22 = Integer.MIN_VALUE;
      this.longVal6 = 0L;
      this.intVal23 = Integer.MIN_VALUE;
      this.intVal24 = 0;
      this.intVal8 = -1;
      this.intVal19 = 0;
      this.longVal2 = 0L;
      this.intVal2 = this.intVal3 = this.intVal4 = 0;
      this.daUtilsInner1 = null;
      this.intVal5 = 0;
      this.longVal3 = 0L;
      this.set.clear();
      this.set2.clear();
      this.bool = false;
      this.longVal = 0L;
      this.rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LONG;
      this.intVal9 = 0;
      this.intVal10 = -1;
      this.intVal11 = -1;
      this.bool2 = true;
      this.intVal12 = 0;
      this.intVal13 = 0;
      this.bool3 = false;
      this.intVal14 = 0;
      this.class23382 = null;
      this.set5.clear();
      this.longVal4 = Long.MIN_VALUE;
      this.map.clear();
      this.intVal15 = 0;
      this.intVal16 = 0;
   }

   public void run6() {
      this.intVal19++;
      long var1 = System.currentTimeMillis();
      if (this.rtpBaseFinderPhase == RtpBaseFinder.Phase.PENDING_WARMUP) {
         this.run3(var1);
      } else if (class310.player != null && class310.world != null && class310.getNetworkHandler() != null) {
         AutoTunnelUtil2.run();

         try {
            long var3 = ChunkPos.toLong(class310.player.getBlockX() >> 4, class310.player.getBlockZ() >> 4);
            if (var3 != this.longVal4) {
               this.set5.add(var3);
               this.longVal4 = var3;
               if (this.debug.getValue()) {
                  try {
                     NyxClient.LOGGER
                        .info(
                           "[TBF/BRAIN] enter visitedChunk=({},{}) total={}",
                           new Object[]{class310.player.getBlockX() >> 4, class310.player.getBlockZ() >> 4, this.set5.size()}
                        );
                  } catch (Throwable var10) {
                  }
               }
            }
         } catch (Throwable var11) {
         }

         if (this.rtpBaseFinderPhase == RtpBaseFinder.Phase.DESCENDING || this.rtpBaseFinderPhase == RtpBaseFinder.Phase.TUNNELING) {
            d$aUtils.Inner1 var12 = d$aUtils.daOf(
               class310.player, this.scanRadius.getValueInt(), this.useTunnelScanner.getValue(), this.useActivityScanner.getValue()
            );
            if (var12 != null) {
               long var4 = var12.chunkKey();
               Integer var6 = this.map.get(var4);
               if (var6 == null || var12.score() > var6) {
                  this.map.put(var4, var12.score());
               }

               if (var12.score() >= this.minConfidence.getValueInt()) {
                  long var7 = var12.chunkKey();
                  if (!this.set.contains(var7) && !this.set2.contains(var7)) {
                     if (!this.minChestsEnforced.getValue()) {
                        this.run24();
                        this.run26(var12, class310.player.getY(), -1, var1);
                        return;
                     }

                     RtpBaseFinder.Inner1 var9 = this.rtpBaseFinderaOf(var7);
                     if (var9 == null) {
                        if (this.debug.getValue() && this.intVal19 % 40 == 0) {
                           NotificationUtils.run(
                              "TunnelBaseFinder",
                              "Hit at " + MapUtils.intOf(var7) + "," + MapUtils.intOf2(var7) + " — chunk not loaded, keep tunneling",
                              INFO.UNKNOWN,
                              1500L
                           );
                        }
                     } else {
                        if (var9.chestCount >= this.minChests.getValueInt()) {
                           this.run11(var12, var9, var1);
                           return;
                        }

                        this.set.add(var7);
                        if (this.debug.getValue()) {
                           NotificationUtils.run(
                              "TunnelBaseFinder",
                              "FP: chunk "
                                 + MapUtils.intOf(var7)
                                 + ","
                                 + MapUtils.intOf2(var7)
                                 + " has "
                                 + var9.chestCount
                                 + " chests (< "
                                 + this.minChests.getValueInt()
                                 + ")",
                              INFO.UNKNOWN,
                              2500L
                           );
                        }
                     }
                  }
               }
            }
         }

         switch (this.rtpBaseFinderPhase) {
            case IDLE:
            case PENDING_WARMUP:
            default:
               break;
            case DESCENDING:
               this.run7(var1);
               break;
            case TUNNELING:
               this.run10(var1);
               break;
            case APPROACHING:
               this.run22(var1);
               break;
            case FOUND:
               this.run24();
         }
      } else {
         if (this.rtpBaseFinderPhase != RtpBaseFinder.Phase.IDLE && this.rtpBaseFinderPhase != RtpBaseFinder.Phase.FOUND) {
            this.bool = true;
            this.longVal = var1;
            this.rtpBaseFinderPhase = RtpBaseFinder.Phase.PENDING_WARMUP;
         }
      }
   }

   private void run7(long var1) {
      GameOptions var3 = class310.options;
      if (var3 != null) {
         var3.forwardKey.setPressed(false);
         var3.sneakKey.setPressed(false);
         var3.sprintKey.setPressed(false);
         var3.jumpKey.setPressed(false);
         int var4 = class310.player.getBlockPos().getY();
         int var5 = this.targetY.getValueInt();
         int var6 = class310.world.getBottomY();
         if (var4 != this.intVal22) {
            this.intVal22 = var4;
            this.intVal21 = 0;
         } else {
            this.intVal21++;
         }

         if (var4 > var5 && var4 > var6 + 2) {
            if (this.debug.getValue() && this.intVal19 % 40 == 0) {
               try {
                  NyxClient.LOGGER
                     .info(
                        "[TBF/DESCEND] tick y={} target={} stall={}/{} elapsed={}s brkTgt={}",
                        new Object[]{
                           var4, var5, this.intVal21, 240, (var1 - this.longVal6) / 1000L, this.class2338 == null ? "null" : this.class2338.toShortString()
                        }
                     );
               } catch (Throwable var23) {
               }
            }

            BlockPos var7 = class310.player.getBlockPos();
            BlockPos var8 = var7.down();
            BlockState var9 = class310.world.getBlockState(var8);
            BlockState var10 = class310.world.getBlockState(var7);
            boolean var11 = var9.getFluidState().isIn(FluidTags.LAVA);
            boolean var12 = var9.getFluidState().isIn(FluidTags.WATER);
            boolean var13 = var10.getFluidState().isIn(FluidTags.LAVA);
            boolean var14 = var10.getFluidState().isIn(FluidTags.WATER);
            boolean var15 = (var11 || var13) && this.stopIfLava.getValue();
            boolean var16 = (var12 || var14) && this.stopIfWater.getValue();
            if (!var15 && !var16) {
               for (Direction var18 : Type.HORIZONTAL) {
                  BlockPos var19 = var7.offset(var18);
                  BlockState var20 = class310.world.getBlockState(var19);
                  if (var20.getFluidState().isIn(FluidTags.LAVA) || var20.getFluidState().isIn(FluidTags.WATER)) {
                     this.set4.add(var19.asLong());
                  }
               }

               try {
                  float var25 = var9.getHardness(class310.world, var8);
                  if (var25 < 0.0F) {
                     this.run14();
                     if (this.debug.getValue()) {
                        NotificationUtils.run(
                           "TunnelBaseFinder",
                           "Unbreakable block below at y=" + var4 + " — switching to tunnel (short of target=" + var5 + ")",
                           INFO.UNKNOWN_3,
                           3000L
                        );
                     }

                     this.run8(var1, "unbreakable-below");
                     return;
                  }
               } catch (Throwable var22) {
               }

               if (var9.isAir()) {
                  AutoTunnelUtil2.run2("RtpBaseFinder");
                  this.class2338 = null;
                  this.intVal6 = 0;
                  this.intVal21 = 0;
               } else if (this.intVal21 >= 240) {
                  if (this.intVal24 == 0) {
                     this.intVal24 = 1;
                     this.run14();
                     this.class2338 = null;
                     this.intVal6 = 0;
                     if (this.debug.getValue()) {
                        NotificationUtils.run("TunnelBaseFinder", "Descent stalled at y=" + var4 + " — attempting rearm", INFO.UNKNOWN_3, 2000L);
                     }

                     this.intVal21 = 160;
                  } else {
                     if (this.debug.getValue()) {
                        NotificationUtils.run("TunnelBaseFinder", "Descent hard-stalled at y=" + var4 + " — switching to tunnel", INFO.UNKNOWN_3, 2500L);
                     }

                     this.run14();
                     this.run8(var1, "descent-stall");
                  }
               } else if (this.longVal6 > 0L && var1 - this.longVal6 >= 60000L && this.intVal23 != Integer.MIN_VALUE && this.intVal23 - var4 < 1) {
                  NotificationUtils.run(
                     "TunnelBaseFinder",
                     "DESCEND fallback: 60s with 0 Y progress at y=" + var4 + " (target=" + var5 + ") -> switching to tunnel",
                     INFO.UNKNOWN_3,
                     5000L
                  );

                  try {
                     NyxClient.LOGGER.warn("[TBF/DESCEND] 60s hard-stall fallback: y={} target={} startY={}", new Object[]{var4, var5, this.intVal23});
                  } catch (Throwable var21) {
                  }

                  this.run14();
                  this.run8(var1, "descent-hard-stall-60s");
               } else {
                  m$aUtils.Inner1 var26 = m$aUtils.maOf(var8);
                  BlockState var27 = var26 != null ? var26.state() : var9;
                  if (var27.isAir()) {
                     m$aUtils.run3(var8);
                     AutoTunnelUtil2.run2("RtpBaseFinder");
                     AutoTunnelUtil2.run6(1 + this.random.nextInt(3));
                     this.class2338 = null;
                     this.intVal6 = 0;
                     this.intVal21 = 0;
                  } else {
                     this.run12(var8, Direction.UP, true, var1);
                  }
               }
            } else {
               this.set4.add(var8.asLong());
               this.set4.add(var7.asLong());
               if (this.debug.getValue()) {
                  NotificationUtils.run(
                     "TunnelBaseFinder", (var15 ? "Lava" : "Water") + " in descent shaft at y=" + var4 + " — switching to tunnel", INFO.UNKNOWN, 2000L
                  );
               }

               this.run14();
               this.run8(var1, var15 ? "lava-in-shaft" : "water-in-shaft");
            }
         } else {
            this.run14();
            if (this.debug.getValue()) {
               try {
                  NyxClient.LOGGER
                     .info(
                        "[TBF/DESCEND] reached target y={} target={} dropped={}",
                        new Object[]{var4, var5, this.intVal23 == Integer.MIN_VALUE ? -1 : this.intVal23 - var4}
                     );
               } catch (Throwable var24) {
               }
            }

            this.run8(var1, "reached-target-y");
         }
      }
   }

   private void run8(long var1, String var3) {
      if (this.debug.getValue()) {
         NotificationUtils.run("TunnelBaseFinder", "-> TUNNELING (null)", INFO.UNKNOWN, 1500L);
      }

      if (this.class23382 == null && class310.player != null) {
         this.class23382 = class310.player.getBlockPos().toImmutable();
         if (this.debug.getValue()) {
            try {
               NyxClient.LOGGER.info("[TBF/BRAIN] originPos captured = {}", this.class23382.toShortString());
            } catch (Throwable var5) {
            }
         }
      }

      if (this.intVal10 < 0 && class310.player != null) {
         this.intVal10 = intOf4(class310.player.getHorizontalFacing());
         this.intVal11 = intOf2(this.intVal10, this.bool2);
         this.rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LONG;
         this.intVal9 = this.getInt();
         this.intVal13 = Math.max(2, this.crossHatchRows.getValueInt());
         this.bool3 = false;
         this.intVal14 = 0;
      }

      this.run15(var1, "enter-tunneling");
      this.class2338 = null;
      this.intVal6 = 0;
      this.intVal7 = 0;
      this.run25(RtpBaseFinder.Phase.TUNNELING);
   }

   private void run10(long var1) {
      if (this.stopIfInventoryFull.getValue() && this.isEnabled5()) {
         if (this.debug.getValue()) {
            NotificationUtils.run("TunnelBaseFinder", "Inventory full — halting", INFO.UNKNOWN, 3000L);
         }

         this.run24();
         this.run5(false);
      } else {
         GameOptions var3 = class310.options;
         if (var3 != null) {
            var3.sneakKey.setPressed(false);
            var3.sprintKey.setPressed(false);
            var3.jumpKey.setPressed(false);
            if (this.floatVal != null) {
               var3.forwardKey.setPressed(false);
               this.run14();
               this.class2338 = null;
               this.intVal6 = 0;
               this.run23(this.floatVal);
               float var16 = Math.abs(MathHelper.wrapDegrees(class310.player.getYaw() - this.floatVal));
               if (var16 <= 0.5F) {
                  this.intVal18++;
                  if (this.intVal18 >= 3) {
                     this.floatVal = null;
                     this.intVal18 = 0;
                  }
               } else {
                  this.intVal18 = 0;
               }
            } else {
               int var4 = this.tunnelHeight.getValueInt();
               int var5 = this.tunnelWidth.getValueInt();
               Direction var6 = class310.player.getHorizontalFacing();
               if (this.check5(var6)) {
                  var3.forwardKey.setPressed(false);
                  this.run14();
                  this.class2338 = null;
                  this.intVal6 = 0;
                  if (this.debug.getValue()) {
                     NotificationUtils.run("TunnelBaseFinder", "Fluid ahead — turning", INFO.UNKNOWN, 1500L);
                  }

                  this.run9(var1, "fluid-ahead");
               } else {
                  Optional var7 = OptionalUtils.optionalOf2(class310.player, var4, var5, this::check6);
                  BlockPos var8 = class310.player.getBlockPos().offset(var6);
                  boolean var9 = class310.world.getBlockState(var8).isAir() && class310.world.getBlockState(var8.up()).isAir();
                  var3.forwardKey.setPressed(var9);
                  if (var7.isEmpty()) {
                     if (this.class2338 != null) {
                        this.run14();
                        this.class2338 = null;
                        this.intVal6 = 0;
                     }

                     if (!var9) {
                        this.run9(var1, "dead-end");
                     }
                  } else {
                     OptionalUtils.Inner1 var10 = (OptionalUtils.Inner1)var7.get();
                     BlockPos var11 = var10.target();
                     int var12 = this.lavaAvoidRadius.getValueInt();
                     if (this.check4(var11, var12)) {
                        this.set3.add(var11.asLong());
                        this.set4.add(var11.asLong());
                        this.run14();
                        this.class2338 = null;
                        this.intVal6 = 0;
                        if (this.debug.getValue()) {
                           NotificationUtils.run("TunnelBaseFinder", "Fluid near " + var11.toShortString() + " — routing around", INFO.UNKNOWN, 1500L);
                        }

                        this.run9(var1, "fluid-near-target");
                     } else {
                        Direction var13 = m$aUtils.class2350Of(var11);
                        if (var13 == null) {
                           var13 = var10.hitFace();
                        }

                        m$aUtils.Inner1 var14 = m$aUtils.maOf(var11);
                        BlockState var15 = var14 != null ? var14.state() : class310.world.getBlockState(var11);
                        if (var15.isAir()) {
                           m$aUtils.run3(var11);
                           AutoTunnelUtil2.run2("RtpBaseFinder");
                           AutoTunnelUtil2.run6(1 + this.random.nextInt(3));
                           if (var11.equals(this.class2338)) {
                              this.intVal7++;
                              this.run16(var1);
                              this.class2338 = null;
                              this.intVal6 = 0;
                           }
                        } else {
                           this.run12(var11, var13, false, var1);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void run16(long var1) {
      if (this.intVal15 > 0) {
         this.intVal15--;
         if (this.intVal15 == 0) {
            this.intVal16 = 0;
            if (this.debug.getValue()) {
               try {
                  NyxClient.LOGGER.info("[TBF/BRAIN] recovery drain complete -> resume serpentine");
               } catch (Throwable var4) {
               }
            }

            this.run15(var1, "recovery-done");
         }
      } else {
         String var3 = this.sweepPattern.getValue();
         if (!"Serpentine".equals(var3) && !"Spiral".equals(var3) && !"CrossHatch".equals(var3)) {
            if (this.intVal7 >= this.blocksBetweenTurns.getValueInt()) {
               this.run15(var1, "periodic-turn");
            }
         } else {
            this.intVal9--;
            if (this.intVal9 <= 0) {
               this.run19(var1);
               this.intVal16 = 0;
            }
         }
      }
   }

   private void run19(long var1) {
      String var3 = this.sweepPattern.getValue();
      if (this.rtpBaseFinderSweepLeg == RtpBaseFinder.SweepLeg.LONG) {
         this.intVal11 = intOf2(this.intVal10, this.bool2);
         this.rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LATERAL;
         this.intVal9 = this.getInt2();
         this.run20(this.intVal11);
         this.class2338 = null;
         this.intVal6 = 0;
         this.run14();
         if (this.debug.getValue()) {
            try {
               NyxClient.LOGGER
                  .info(
                     "[TBF/BRAIN] leg=LONG->LATERAL steps={} longDir={} flip={} shortDir={}",
                     new Object[]{this.intVal9, stringOf(this.intVal10), this.bool2 ? "RIGHT" : "LEFT", stringOf(this.intVal11)}
                  );
            } catch (Throwable var11) {
            }
         }
      } else {
         int var4 = intOf2(this.intVal11, this.bool2);
         this.intVal10 = var4;
         this.rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LONG;
         this.bool2 = !this.bool2;
         this.intVal12++;
         if ("CrossHatch".equals(var3)) {
            this.intVal13--;
            if (this.intVal13 <= 0) {
               int var5 = intOf2(this.intVal10, true);
               if (this.debug.getValue()) {
                  try {
                     NyxClient.LOGGER.info("[TBF/BRAIN] cross-hatch phase {} done, rotating axis to {}", this.bool3 ? "B" : "A", stringOf(var5));
                  } catch (Throwable var10) {
                  }
               }

               this.intVal10 = var5;
               this.bool3 = !this.bool3;
               this.intVal13 = Math.max(2, this.crossHatchRows.getValueInt());
               this.intVal14 = 0;
            }
         } else if ("Serpentine".equals(var3)) {
            int var12 = this.axisRotateAfterRows.getValueInt();
            this.intVal14++;
            if (var12 > 0 && this.intVal14 >= var12) {
               int var6 = intOf2(this.intVal10, true);
               if (this.debug.getValue()) {
                  try {
                     NyxClient.LOGGER.info("[TBF/BRAIN] axis rotate: rows={} threshold={}, new axis={}", new Object[]{this.intVal14, var12, stringOf(var6)});
                  } catch (Throwable var9) {
                  }
               }

               this.intVal10 = var6;
               this.intVal14 = 0;
            }
         }

         this.intVal9 = this.getInt();
         this.run20(this.intVal10);
         this.class2338 = null;
         this.intVal6 = 0;
         this.run14();
         if (this.debug.getValue()) {
            NotificationUtils.run("TunnelBaseFinder", "Serpentine row #" + this.intVal12 + " complete", INFO.UNKNOWN, 1500L);

            try {
               NyxClient.LOGGER
                  .info(
                     "[TBF/BRAIN] leg=LATERAL->LONG steps={} longDir={} flip(next)={}",
                     new Object[]{this.intVal9, stringOf(this.intVal10), this.bool2 ? "RIGHT" : "LEFT"}
                  );
            } catch (Throwable var8) {
            }
         }
      }

      this.intVal7 = 0;
   }

   public int getInt() {
      int var1 = Math.max(1, this.sweepLegLength.getValueInt());
      if ("Spiral".equals(this.sweepPattern.getValue())) {
         var1 += 20 * (this.intVal12 / 2);
      }

      int var2 = this.intOf(var1, false);
      if (this.debug.getValue()) {
         try {
            NyxClient.LOGGER.info("[TBF/BRAIN] jittered leg: base={} final={}", var1, var2);
         } catch (Throwable var4) {
         }
      }

      return var2;
   }

   private int getInt2() {
      int var1 = Math.max(1, this.lateralOffset.getValueInt());
      int var2 = this.intOf(var1, true);
      if (this.debug.getValue()) {
         try {
            NyxClient.LOGGER.info("[TBF/BRAIN] jittered leg: base={} final={}", var1, var2);
         } catch (Throwable var4) {
         }
      }

      return var2;
   }

   private int intOf(int var1, boolean var2) {
      int var3 = this.legLengthJitter.getValueInt();
      if (var3 > 0 && var1 > 1) {
         int var4;
         if (var2) {
            var4 = Math.max(1, var1 * var3 / 100);
         } else {
            var4 = Math.max(1, var1 * var3 / 100);
         }

         int var5 = this.random.nextInt(var4 * 2 + 1) - var4;
         return Math.max(1, var1 + var5);
      } else {
         return var1;
      }
   }

   private void run9(long var1, String var3) {
      this.intVal16++;
      if (this.debug.getValue()) {
         try {
            NyxClient.LOGGER
               .info(
                  AntiDebugUtil.stringOf(
                     new byte[]{
                        127,
                        -39,
                        0,
                        21,
                        -65,
                        -53,
                        -36,
                        -18,
                        -11,
                        11,
                        -57,
                        -21,
                        -37,
                        -75,
                        19,
                        -60,
                        63,
                        -35,
                        -128,
                        38,
                        -95,
                        -118,
                        81,
                        -15,
                        -47,
                        -50,
                        55,
                        -101,
                        -71,
                        69,
                        98,
                        -14,
                        -23,
                        29,
                        -42,
                        14,
                        -117,
                        20
                     }
                  ),
                  var3,
                  this.intVal16
               );
         } catch (Throwable var8) {
         }
      }

      int var4 = this.intVal8 >= 0 ? this.intVal8 : (this.intVal10 >= 0 ? this.intVal10 : 0);
      if (this.intVal16 == 1) {
         int var10 = intOf2(var4, this.bool2);
         this.run20(var10);
         this.intVal10 = var10;
         this.rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LONG;
         this.intVal9 = this.getInt();
         this.class2338 = null;
         this.intVal6 = 0;
         this.run14();
         if (this.debug.getValue()) {
            NotificationUtils.run("TunnelBaseFinder", "Stuck -> 90° " + (this.bool2 ? "right" : "left") + " -> " + stringOf(var10), INFO.UNKNOWN, 1500L);
         }
      } else if (this.intVal16 == 2) {
         int var9 = intOf3(var4);
         this.run20(var9);
         this.intVal10 = var9;
         this.rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LONG;
         this.intVal9 = this.getInt();
         this.class2338 = null;
         this.intVal6 = 0;
         this.run14();
         if (this.debug.getValue()) {
            NotificationUtils.run("TunnelBaseFinder", "Stuck 2x -> 180° -> " + stringOf(var9), INFO.UNKNOWN, 1500L);
         }
      } else {
         this.intVal15 = 5;
         int var5 = this.random.nextInt(4);
         this.run20(var5);
         this.intVal10 = var5;
         this.rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LONG;
         this.intVal9 = this.getInt();
         this.class2338 = null;
         this.intVal6 = 0;
         this.run14();
         if (this.debug.getValue()) {
            NotificationUtils.run("TunnelBaseFinder", "RECOVERY: 5 random blocks -> " + stringOf(var5) + " (" + var3 + ")", INFO.UNKNOWN_3, 2500L);

            try {
               NyxClient.LOGGER.info("[TBF/BRAIN] RECOVERY 5 blocks -> {}", stringOf(var5));
            } catch (Throwable var7) {
            }
         }
      }
   }

   private Integer getInteger() {
      if (this.class23382 != null && class310.player != null) {
         double var1 = this.class23382.getX() - class310.player.getX();
         double var3 = this.class23382.getZ() - class310.player.getZ();
         double var5 = Math.hypot(var1, var3);
         double var7 = this.maxSearchRadius.getValueInt() * 0.9;
         if (var5 < var7) {
            return null;
         } else {
            return Math.abs(var1) > Math.abs(var3) ? var1 > 0.0 ? 1 : 3 : var3 > 0.0 ? 2 : 0;
         }
      } else {
         return null;
      }
   }

   private Integer getInteger2() {
      if (this.heatMapBias.getValue() && class310.player != null) {
         int var1 = class310.player.getBlockX() >> 4;
         int var2 = class310.player.getBlockZ() >> 4;
         int var3 = Math.max(1, this.minConfidence.getValueInt() / 2);
         int var4 = -1;
         int var5 = 0;

         for (int var6 = 0; var6 < 4; var6++) {
            int[] var7 = intArray[var6];

            for (int var8 = 1; var8 <= 3; var8++) {
               long var9 = ChunkPos.toLong(var1 + var7[0] * var8, var2 + var7[1] * var8);
               if (!this.set.contains(var9) && !this.set2.contains(var9)) {
                  Integer var11 = this.map.get(var9);
                  if (var11 != null && var11 > var5) {
                     var5 = var11;
                     var4 = var6;
                  }
               }
            }
         }

         return var5 >= var3 ? var4 : null;
      } else {
         return null;
      }
   }

   private boolean check(int var1, int var2) {
      if (this.visitedChunkAvoidance.getValue() && class310.player != null) {
         int var3 = class310.player.getBlockX() >> 4;
         int var4 = class310.player.getBlockZ() >> 4;
         int[] var5 = intArray[var1];

         for (int var6 = 1; var6 <= Math.max(1, var2); var6++) {
            long var7 = ChunkPos.toLong(var3 + var5[0] * var6, var4 + var5[1] * var6);
            if (this.set5.contains(var7)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private RtpBaseFinder.Inner1 rtpBaseFinderaOf(long var1) {
      try {
         int var3 = MapUtils.intOf(var1);
         int var4 = MapUtils.intOf2(var1);
         WorldChunk var5 = class310.world.getChunkManager().getWorldChunk(var3, var4, false);
         if (var5 == null) {
            return null;
         } else {
            int var6 = 0;
            ArrayList var7 = new ArrayList();

            Map var8;
            try {
               var8 = var5.getBlockEntities();
            } catch (Throwable var12) {
               return null;
            }

            for (Entry var10 : ((java.util.Map<?,?>)var8).entrySet()) {
               BlockEntity var11 = (BlockEntity)var10.getValue();
               if (var11 instanceof ChestBlockEntity || var11 instanceof BarrelBlockEntity) {
                  var6++;
                  var7.add(((BlockPos)var10.getKey()).getY());
               }
            }

            int var14;
            if (var7.isEmpty()) {
               ChunkActivityScanner.Inner1 var15 = ChunkActivityScanner.chunkActivityScanneraOf(var1);
               if (var15 != null && var15.surfaceY() != Integer.MIN_VALUE) {
                  var14 = var15.surfaceY() - 2;
               } else {
                  var14 = class310.player.getBlockPos().getY();
               }
            } else {
               Collections.sort(var7);
               var14 = (Integer)var7.get(var7.size() / 2);
            }

            return new RtpBaseFinder.Inner1(var6, var14);
         }
      } catch (Throwable var13) {
         return null;
      }
   }

   private void run11(d$aUtils.Inner1 var1, RtpBaseFinder.Inner1 var2, long var3) {
      int var5 = MapUtils.intOf(var1.chunkKey());
      int var6 = MapUtils.intOf2(var1.chunkKey());
      this.longVal2 = var1.chunkKey();
      this.intVal2 = (var5 << 4) + 8;
      this.intVal4 = (var6 << 4) + 8;
      this.intVal3 = var2.medianY;
      this.daUtilsInner1 = var1;
      this.intVal5 = var2.chestCount;
      this.longVal3 = var3;
      this.run14();
      this.class2338 = null;
      this.intVal6 = 0;
      this.floatVal = null;
      this.intVal18 = 0;
      this.intVal7 = 0;
      class310.player.setPitch(0.0F);
      this.run25(RtpBaseFinder.Phase.APPROACHING);
      if (this.debug.getValue()) {
         NotificationUtils.run(
            "TunnelBaseFinder",
            "-> APPROACHING (" + var2.chestCount + " chests @ " + this.intVal2 + "," + this.intVal3 + "," + this.intVal4 + ")",
            INFO.UNKNOWN,
            3000L
         );
      }
   }

   private void run22(long var1) {
      GameOptions var3 = class310.options;
      if (var3 != null) {
         var3.sneakKey.setPressed(false);
         var3.sprintKey.setPressed(false);
         var3.jumpKey.setPressed(false);
         if (var1 - this.longVal3 > this.approachTimeoutMs.getValueLong()) {
            this.set2.add(this.longVal2);
            if (this.debug.getValue()) {
               NotificationUtils.run(
                  "TunnelBaseFinder", "Approach timeout at " + this.intVal2 + "," + this.intVal4 + " — blacklisted, resuming tunnel", INFO.UNKNOWN, 3000L
               );
            }

            this.run24();
            this.run8(var1, "approach-timeout");
         } else {
            BlockPos var4 = new BlockPos(this.intVal2, this.intVal3, this.intVal4);
            if (class310.player.getBlockPos().isWithinDistance(var4, 4.0)) {
               this.run24();
               this.run26(this.daUtilsInner1, this.intVal3, this.intVal5, var1);
            } else {
               Vec3d var5 = class310.player.getEyePos();
               Vec3d var6 = new Vec3d(this.intVal2 + 0.5, this.intVal3 + 0.5, this.intVal4 + 0.5);
               double var7 = var6.x - var5.x;
               double var9 = var6.y - var5.y;
               double var11 = var6.z - var5.z;
               float var13 = (float)Math.toDegrees(Math.atan2(-var7, var11));
               float var14 = (float)Math.toDegrees(-Math.atan2(var9, Math.sqrt(var7 * var7 + var11 * var11)));
               var14 = MathHelper.clamp(var14, -90.0F, 90.0F);
               float var15 = MathHelper.wrapDegrees(class310.player.getYaw());
               float var16 = MathHelper.wrapDegrees(MathHelper.wrapDegrees(var13) - var15);
               boolean var17;
               if (Math.abs(var16) > 12.0F) {
                  float var18 = Math.signum(var16) * 12.0F;
                  class310.player.setYaw(MathHelper.wrapDegrees(var15 + var18));
                  class310.player.setPitch(var14);
                  this.run14();
                  this.class2338 = null;
                  this.intVal6 = 0;
                  this.intVal18 = 0;
                  var3.forwardKey.setPressed(false);
                  var17 = false;
               } else {
                  class310.player.setYaw(MathHelper.wrapDegrees(var13));
                  class310.player.setPitch(var14);
                  this.intVal18++;
                  var17 = this.intVal18 >= 3;
               }

               Direction var29 = class310.player.getHorizontalFacing();
               BlockPos var19 = class310.player.getBlockPos().offset(var29);
               boolean var20 = class310.world.getBlockState(var19).isAir() && class310.world.getBlockState(var19.up()).isAir();
               boolean var21 = Math.abs(var14) < 60.0F;
               var3.forwardKey.setPressed(var20 && var21 && var17);
               if (var17) {
                  BlockHitResult var22;
                  try {
                     var22 = class310.world.raycast(new RaycastContext(var5, var6, ShapeType.OUTLINE, FluidHandling.NONE, class310.player));
                  } catch (Throwable var27) {
                     return;
                  }

                  if (var22 != null && var22.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                     BlockPos var23 = var22.getBlockPos();
                     Direction var24 = var22.getSide();
                     if (this.check4(var23, this.lavaAvoidRadius.getValueInt())) {
                        this.run14();
                        this.class2338 = null;
                        this.intVal6 = 0;
                        if (this.debug.getValue() && this.intVal19 % 20 == 0) {
                           NotificationUtils.run("TunnelBaseFinder", "Approach fluid at " + var23.toShortString() + " — waiting", INFO.UNKNOWN, 1500L);
                        }
                     } else {
                        m$aUtils.Inner1 var25 = m$aUtils.maOf(var23);
                        BlockState var26 = var25 != null ? var25.state() : class310.world.getBlockState(var23);
                        if (var26.isAir()) {
                           m$aUtils.run3(var23);
                           AutoTunnelUtil2.run2("RtpBaseFinder");
                           AutoTunnelUtil2.run6(1 + this.random.nextInt(3));
                           this.class2338 = null;
                           this.intVal6 = 0;
                        } else {
                           this.run12(var23, var24, false, var1);
                        }
                     }
                  } else {
                     this.run14();
                     this.class2338 = null;
                     this.intVal6 = 0;
                  }
               }
            }
         }
      }
   }

   private void run12(BlockPos var1, Direction var2, boolean var3, long var4) {
      float[] var6 = m$aUtils.floatArrayOf(var1, var2);
      float var7 = var6[0];
      float var8 = var3 ? 90.0F : var6[1];
      class310.player.setYaw(var7);
      class310.player.setPitch(var8);
      boolean var9 = var3 || this.check2(var1);
      if (!var9) {
         AutoTunnelUtil2.run2("RtpBaseFinder");
         if (this.debug.getValue() && this.intVal19 % 20 == 0) {
            this.run13(var1, false, false, var3 ? "DESCENDING" : "TUNNELING", "crosshair-miss");
         }
      } else if (this.class2338 != null && !var1.equals(this.class2338)) {
         AutoTunnelUtil2.run2("RtpBaseFinder");
         AutoTunnelUtil2.run6(1 + this.random.nextInt(3));
         this.class2338 = var1.toImmutable();
         this.intVal6 = 0;
      } else {
         if (this.class2338 == null) {
            this.class2338 = var1.toImmutable();
            this.intVal6 = 0;
         }

         this.intVal6++;
         int var10 = var3 ? this.maxTicksPerBlock.getValueInt() * 3 : this.maxTicksPerBlock.getValueInt();
         if (this.intVal6 >= var10) {
            this.set3.add(var1.asLong());
            AutoTunnelUtil2.run2("RtpBaseFinder");
            AutoTunnelUtil2.run6(2);
            this.class2338 = null;
            this.intVal6 = 0;
            if (var3) {
               this.run8(var4, "descend-timeout");
            } else if (this.debug.getValue()) {
               NotificationUtils.run("TunnelBaseFinder", "Break timeout at " + var1.toShortString() + " — blacklisted", INFO.UNKNOWN, 1500L);
            }
         } else {
            boolean var11 = AutoTunnelUtil2.check("RtpBaseFinder", var1);
            if (this.debug.getValue() && this.intVal19 % 20 == 0) {
               this.run13(var1, var11, true, var3 ? "DESCENDING" : "TUNNELING", "driving");
            }
         }
      }
   }

   private boolean check2(BlockPos var1) {
      if (class310.crosshairTarget instanceof BlockHitResult var2) {
         return var2.getType() != net.minecraft.util.hit.HitResult.Type.BLOCK ? false : var1.equals(var2.getBlockPos());
      } else {
         return false;
      }
   }

   private boolean check3(BlockState var1) {
      if (var1 == null) {
         return false;
      } else if (var1.getFluidState().isEmpty()) {
         return false;
      } else {
         return this.stopIfLava.getValue() && var1.getFluidState().isIn(FluidTags.LAVA)
            ? true
            : this.stopIfWater.getValue() && var1.getFluidState().isIn(FluidTags.WATER);
      }
   }

   private boolean check4(BlockPos var1, int var2) {
      if (var1 != null && class310.world != null) {
         if (var2 < 1) {
            var2 = 1;
         }

         if (this.check3(class310.world.getBlockState(var1))) {
            this.set4.add(var1.asLong());
            return true;
         } else {
            for (int var3 = 1; var3 <= var2; var3++) {
               BlockPos[] var4 = new BlockPos[]{
                  var1.add(var3, 0, 0), var1.add(-var3, 0, 0), var1.add(0, 0, var3), var1.add(0, 0, -var3), var1.add(0, var3, 0), var1.add(0, -var3, 0)
               };

               for (BlockPos var8 : var4) {
                  if (this.check3(class310.world.getBlockState(var8))) {
                     this.set4.add(var8.asLong());
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private boolean check5(Direction var1) {
      if (var1 != null && class310.player != null && class310.world != null) {
         BlockPos var2 = class310.player.getBlockPos();
         BlockPos var3 = var2.offset(var1);
         BlockPos[] var4 = new BlockPos[]{var3, var3.up(), var3.down()};

         for (BlockPos var8 : var4) {
            if (this.check3(class310.world.getBlockState(var8))) {
               this.set4.add(var8.asLong());
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void run13(BlockPos var1, boolean var2, boolean var3, String var4, String var5) {
      try {
         NyxClient.LOGGER
            .info(
               "[TBF/{}] target={} face-ok={} safeHold={} rearm={} yaw={}/{} crosshair={} vanillaPos={} progress={} note={}",
               new Object[]{
                  var4,
                  var1 == null ? "null" : var1.toShortString(),
                  var3,
                  var2,
                  AutoTunnelUtil2.isEnabled8(),
                  class310.player == null ? Float.NaN : class310.player.getYaw(),
                  this.floatVal,
                  this.getString(),
                  this.getString2(),
                  this.getFloat(),
                  var5
               }
            );
      } catch (Throwable var7) {
      }
   }

   public String getString() {
      HitResult var1 = class310.crosshairTarget;
      if (var1 == null) {
         return "null";
      } else if (var1 instanceof BlockHitResult var2) {
         return var2.getType() == net.minecraft.util.hit.HitResult.Type.MISS ? "MISS" : "BLOCK " + var2.getBlockPos().toShortString() + " " + var2.getSide();
      } else {
         return var1.getClass().getSimpleName();
      }
   }

   public String getString2() {
      try {
         ClientPlayerInteractionManager var1 = class310.interactionManager;
         if (var1 == null) {
            return "null";
         } else {
            ClientPlayerInteractionManagerAccessor var2 = (ClientPlayerInteractionManagerAccessor)var1;
            BlockPos var3 = var2.nyx$getCurrentBreakingPos();
            return var3 == null ? "null" : var3.toShortString();
         }
      } catch (Throwable var4) {
         return "?";
      }
   }

   private float getFloat() {
      try {
         ClientPlayerInteractionManager var1 = class310.interactionManager;
         if (var1 == null) {
            return -1.0F;
         } else {
            ClientPlayerInteractionManagerAccessor var2 = (ClientPlayerInteractionManagerAccessor)var1;
            return var2.nyx$getCurrentBreakingProgress();
         }
      } catch (Throwable var3) {
         return -1.0F;
      }
   }

   private void run14() {
      AutoTunnelUtil2.run2("RtpBaseFinder");
      AutoTunnelUtil2.run6(1);
   }

   private void run15(long var1, String var3) {
      Integer var4 = this.getInteger();
      if (var4 != null) {
         this.run20(var4);
         this.run18(var4);
         this.run14();
         if (this.debug.getValue()) {
            NotificationUtils.run("TunnelBaseFinder", "BOUNDARY: near edge -> " + stringOf(var4), INFO.UNKNOWN_3, 2500L);

            try {
               NyxClient.LOGGER.info("[TBF/BRAIN] boundary hit -> returning {}", stringOf(var4));
            } catch (Throwable var8) {
            }
         }
      } else {
         boolean var5 = !"periodic-turn".equals(var3) && !"enter-tunneling".equals(var3);
         if (var5) {
            Integer var6 = this.getInteger2();
            if (var6 != null) {
               this.run20(var6);
               this.run18(var6);
               this.run14();
               if (this.debug.getValue()) {
                  NotificationUtils.run("TunnelBaseFinder", "HEAT BIAS: warm chunk -> " + stringOf(var6), INFO.UNKNOWN, 2000L);

                  try {
                     NyxClient.LOGGER.info("[TBF/BRAIN] warm bias -> {}", stringOf(var6));
                  } catch (Throwable var9) {
                  }
               }

               return;
            }
         }

         String var10 = this.sweepPattern.getValue();
         if (("Serpentine".equals(var10) || "Spiral".equals(var10) || "CrossHatch".equals(var10)) && this.intVal10 >= 0) {
            this.rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LONG;
            this.intVal9 = this.getInt();
            this.run20(this.intVal10);
            this.intVal7 = 0;
            this.class2338 = null;
            this.intVal6 = 0;
            this.run14();
            if (this.debug.getValue()) {
               NotificationUtils.run("TunnelBaseFinder", "Serpentine -> " + stringOf(this.intVal10) + " (" + var3 + ")", INFO.UNKNOWN, 1500L);
            }
         } else {
            this.run17(var1, var3);
         }
      }
   }

   private void run17(long var1, String var3) {
      int var4 = this.intVal8;
      int var5 = -1;
      ArrayList var6 = new ArrayList(4);
      ArrayList var7 = new ArrayList(4);

      for (int var8 = 0; var8 < 4; var8++) {
         if (var4 < 0 || var8 != intOf3(var4)) {
            var7.add(var8);
            if (!this.check(var8, 2)) {
               var6.add(var8);
            }
         }
      }

      if (!var6.isEmpty()) {
         var5 = (Integer)var6.get(this.random.nextInt(var6.size()));
      } else if (!var7.isEmpty()) {
         var5 = (Integer)var7.get(this.random.nextInt(var7.size()));
      } else {
         var5 = this.random.nextInt(4);
      }

      this.run20(var5);
      this.run18(var5);
      this.intVal7 = 0;
      this.class2338 = null;
      this.intVal6 = 0;
      this.run14();
      if (this.debug.getValue()) {
         NotificationUtils.run("TunnelBaseFinder", "Turn -> " + stringOf(var5) + " (" + var3 + ")", INFO.UNKNOWN, 1500L);
      }
   }

   private void run18(int var1) {
      this.intVal10 = var1;
      this.intVal11 = intOf2(var1, this.bool2);
      this.rtpBaseFinderSweepLeg = RtpBaseFinder.SweepLeg.LONG;
      this.intVal9 = this.getInt();
      this.intVal14 = 0;
   }

   private void run20(int var1) {
      float var2 = switch (var1 & 3) {
         case 0 -> 180.0F;
         case 1 -> -90.0F;
         case 2 -> 0.0F;
         default -> 90.0F;
      };
      this.floatVal = MathHelper.wrapDegrees(var2);
      this.intVal18 = 0;
      this.intVal8 = var1 & 3;
      class310.player.setPitch(0.0F);
   }

   private static String stringOf(int var0) {
      return switch (var0 & 3) {
         case 0 -> "NORTH";
         case 1 -> "EAST";
         case 2 -> "SOUTH";
         default -> "WEST";
      };
   }

   private static int intOf2(int var0, boolean var1) {
      return var1 ? var0 + 1 & 3 : var0 + 3 & 3;
   }

   private static int intOf3(int var0) {
      return var0 + 2 & 3;
   }

   private static int intOf4(Direction var0) {
      if (var0 == null) {
         return 0;
      } else {
         return switch (var0) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
         };
      }
   }

   private void run23(float var1) {
      float var2 = MathHelper.wrapDegrees(class310.player.getYaw());
      float var3 = MathHelper.wrapDegrees(var1 - var2);
      if (var3 > 12.0F) {
         var3 = 12.0F;
      } else if (var3 < -12.0F) {
         var3 = -12.0F;
      }

      class310.player.setYaw(MathHelper.wrapDegrees(var2 + var3));
      class310.player.setPitch(0.0F);
   }

   private boolean isEnabled5() {
      try {
         PlayerInventory var1 = class310.player.getInventory();

         for (int var2 = 0; var2 < var1.getMainStacks().size(); var2++) {
            if (((ItemStack)var1.getMainStacks().get(var2)).isEmpty()) {
               return false;
            }
         }

         return true;
      } catch (Throwable var3) {
         return false;
      }
   }

   private void run24() {
      GameOptions var1 = class310.options;
      if (var1 != null) {
         var1.forwardKey.setPressed(false);
         var1.backKey.setPressed(false);
         var1.leftKey.setPressed(false);
         var1.rightKey.setPressed(false);
         var1.jumpKey.setPressed(false);
         var1.sprintKey.setPressed(false);
         var1.sneakKey.setPressed(false);
      }

      AutoTunnelUtil2.run3("RtpBaseFinder");
      m$aUtils.run5();
   }

   private void run25(RtpBaseFinder.Phase var1) {
      if (this.debug.getValue() && this.rtpBaseFinderPhase != var1) {
         NotificationUtils.run("TunnelBaseFinder", this.rtpBaseFinderPhase.name() + " -> " + var1.name(), INFO.UNKNOWN, 2000L);
      }

      this.rtpBaseFinderPhase = var1;
   }

   private void run26(d$aUtils.Inner1 var1, double var2, int var4, long var5) {
      int var7 = MapUtils.intOf(var1.chunkKey());
      int var8 = MapUtils.intOf2(var1.chunkKey());
      int var9 = (var7 << 4) + 8;
      int var10 = (var8 << 4) + 8;
      String var11 = d$aUtils.getString();
      String var12 = class310.world.getRegistryKey().getValue().toString();
      this.intVal++;
      this.list.add(new Vec3d(var9, var2, var10));
      d$aUtils.run4(var5, var11, var12, var9, (int)var2, var10, var1.score(), var1.source(), this.savePath.getValue());
      String var13 = var4 >= 0
         ? "BASE FOUND — " + var4 + " chests at (" + var9 + ", " + (int)var2 + ", " + var10 + ") — score: " + var1.score()
         : "Base at " + var9 + "," + var10 + " (score " + var1.score() + " via " + var1.source() + ")";
      NotificationUtils.run("TunnelBaseFinder", var13, INFO.UNKNOWN_2, 10000L);
      if (this.alertSound.getValue()) {
         d$aUtils.run2();
      }

      if (this.disconnectOnFind.getValue()) {
         try {
            class310.world.disconnect(Text.literal("[TunnelBaseFinder] Base found — disconnecting to preserve location"));
         } catch (Throwable var15) {
         }

         this.run25(RtpBaseFinder.Phase.FOUND);
         this.run5(false);
      } else {
         this.run25(RtpBaseFinder.Phase.FOUND);
         if (this.pauseOnFind.getValue()) {
            this.run5(false);
         }
      }
   }

   private boolean check6(long var1) {
      return this.set3.contains(var1) || this.set4.contains(var1);
   }

   private static enum Phase {
      IDLE,
      PENDING_WARMUP,
      DESCENDING,
      TUNNELING,
      APPROACHING,
      FOUND;

      private static final RtpBaseFinder.Phase[] rtpBaseFinderPhaseArray = getRtpBaseFinderPhaseArray();

      private static RtpBaseFinder.Phase[] getRtpBaseFinderPhaseArray() {
         return new RtpBaseFinder.Phase[]{IDLE, PENDING_WARMUP, DESCENDING, TUNNELING, APPROACHING, FOUND};
      }
   }

   private static enum SweepLeg {
      LONG,
      LATERAL;

      private static final RtpBaseFinder.SweepLeg[] rtpBaseFinderSweepLegArray = getRtpBaseFinderSweepLegArray();

      private static RtpBaseFinder.SweepLeg[] getRtpBaseFinderSweepLegArray() {
         return new RtpBaseFinder.SweepLeg[]{LONG, LATERAL};
      }
   }

record Inner1(int chestCount, int medianY) {

}
}

