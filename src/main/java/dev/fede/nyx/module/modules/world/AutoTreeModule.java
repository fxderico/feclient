package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import dev.fede.nyx.util.AutoCrystalModuleUtil;
import dev.fede.nyx.util.Matrix4fUtils;
import dev.fede.nyx.util.m$aUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;

public class AutoTreeModule extends Module {
   private final NumberSetting range = new NumberSetting("Range", 4.5, 2.0, 6.0, 0.5);
   private final NumberSetting maxPerSec = new NumberSetting("MaxPerSec", 4.0, 1.0, 10.0, 1.0);
   private final NumberSetting maxTreeHeight = new NumberSetting("MaxTreeHeight", 12.0, 3.0, 32.0, 1.0);
   private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
   private final BooleanSetting silentRotate = new BooleanSetting("SilentRotate", true);
   private final BooleanSetting requireAxe = new BooleanSetting("RequireAxe", false);
   private final BooleanSetting jitter = new BooleanSetting("Jitter", true);
   private final BooleanSetting includeStripped = new BooleanSetting("IncludeStrippedLogs", false);
   private final BooleanSetting autoReplant = new BooleanSetting("AutoReplant", false);
   private final BooleanSetting showTarget = new BooleanSetting("ShowTarget", true);
   private final ColorSetting targetColor = new ColorSetting("TargetColor", -2147418232);
   private final BooleanSetting serverSafeMode = new BooleanSetting("ServerSafeMode", true);
   private final NumberSetting stuckTimeoutMs = new NumberSetting("StuckTimeoutMs", 4000.0, 2000.0, 15000.0, 500.0);
   private final NumberSetting blacklistDurationMs = new NumberSetting("BlacklistDurationMs", 30000.0, 5000.0, 120000.0, 1000.0);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private final Random random = new Random();
   private long longVal = 250L;
   private BlockPos class2338;
   private int intVal;
   private BlockPos class23382;
   private Direction class2350;
   private int intVal2 = -1;
   private static final long longVal2 = 100L;
   private static final long longVal3 = 1000L;
   private static final long longVal4 = 5000L;
   private BlockPos class23383;
   private long longVal5;
   private long longVal6;
   private final Map<Long, Long> map = new HashMap<>();

   public AutoTreeModule() {
      super("AutoTree", "Chops trees fully and optionally replants saplings", Category.WORLD);
      this.run6(
         new Setting[]{
            this.range,
            this.maxPerSec,
            this.maxTreeHeight,
            this.rotate,
            this.silentRotate,
            this.requireAxe,
            this.jitter,
            this.includeStripped,
            this.autoReplant,
            this.showTarget,
            this.targetColor,
            this.serverSafeMode,
            this.stuckTimeoutMs,
            this.blacklistDurationMs
         }
      );
   }

   @Override
   public void run() {
      this.class2338 = null;
      this.intVal = 0;
      this.class23382 = null;
      this.class2350 = null;
      this.intVal2 = -1;
      this.antiVoidModuleHelper.run();
      this.longVal = this.getLong();
      this.class23383 = null;
      this.longVal5 = 0L;
      this.longVal6 = 0L;
      this.map.clear();
   }

   @Override
   public void run2() {
      this.run8();
      this.class2338 = null;
      this.run6();
      AntiAFKModuleUtil.run2();
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (class310.currentScreen != null) {
            if (this.class23382 != null) {
               this.run8();
            }

            AntiAFKModuleUtil.run2();
         } else {
            long var1 = System.currentTimeMillis();
            this.run4(var1);
            if (this.class23383 != null) {
               long var3 = var1 - this.longVal5;
               boolean var5 = class310.world.getBlockState(this.class23383).isAir();
               if (!var5) {
                  this.run21(this.class23383, 5000L);
                  m$aUtils.run5();
                  this.class23383 = null;
                  this.longVal5 = 0L;
               } else if (var3 >= 100L || var3 >= 1000L) {
                  this.intVal++;
                  this.antiVoidModuleHelper.run();
                  this.longVal = this.getLong();
                  this.class23383 = null;
                  this.longVal5 = 0L;
               }

               if (this.class23383 != null) {
                  return;
               }
            }

            if (this.class2338 == null) {
               BlockPos var8 = this.getclass2338();
               if (var8 == null) {
                  AntiAFKModuleUtil.run2();
                  return;
               }

               this.class2338 = var8;
               this.intVal = var8.getY();
            }

            int var9 = this.maxTreeHeight.getValueInt();

            BlockPos var4;
            for (var4 = null; this.intVal < this.class2338.getY() + var9; this.intVal++) {
               BlockPos var10 = new BlockPos(this.class2338.getX(), this.intVal, this.class2338.getZ());
               BlockState var6 = class310.world.getBlockState(var10);
               if (this.check2(var6)) {
                  var4 = var10;
                  break;
               }

               if (!var6.isAir() && var6.getFluidState().isEmpty()) {
                  break;
               }
            }

            if (var4 == null) {
               if (this.autoReplant.getValue()) {
                  this.run26(this.class2338);
               }

               this.run8();
               this.class2338 = null;
               this.run6();
               AntiAFKModuleUtil.run2();
            } else if (!this.check3(var4)) {
               this.run8();
               this.class2338 = null;
               this.run6();
               AntiAFKModuleUtil.run2();
            } else {
               if (this.requireAxe.getValue()) {
                  int var11 = this.getInt();
                  if (var11 < 0) {
                     this.run8();
                     AntiAFKModuleUtil.run2();
                     return;
                  }

                  this.run7(var11);
               }

               Direction var12 = this.class2350Of(var4);
               if (this.rotate.getValue()) {
                  float[] var13 = AutoCrystalModuleUtil.floatArrayOf(this.class243Of(var4));
                  if (this.silentRotate.getValue()) {
                     if (!AntiAFKModuleUtil.isEnabled2()) {
                        AntiAFKModuleUtil.run(var13[0], var13[1]);
                     }
                  } else {
                     class310.player.setYaw(var13[0]);
                     class310.player.setPitch(var13[1]);
                     AntiAFKModuleUtil.run2();
                  }
               }

               if (this.check(var4)) {
                  this.run8();
                  this.class2338 = null;
                  this.run6();
                  AntiAFKModuleUtil.run2();
               } else if (this.class23382 != null
                  && this.class23382.equals(var4)
                  && this.longVal6 > 0L
                  && var1 - this.longVal6 > this.stuckTimeoutMs.getValueInt()) {
                  this.run21(this.class23382, this.blacklistDurationMs.getValueInt());
                  this.run8();
                  this.class2338 = null;
                  this.run6();
                  AntiAFKModuleUtil.run2();
               } else {
                  boolean var14 = this.class23382 == null || !this.class23382.equals(var4);
                  if (var14) {
                     if (!this.antiVoidModuleHelper.check(this.longVal)) {
                        return;
                     }

                     this.class23382 = var4;
                     this.class2350 = var12;
                     this.longVal6 = var1;
                     m$aUtils.check(this.class23382, this.class2350, this.silentRotate.getValue());
                  }

                  m$aUtils.check3(this.class23382, this.class2350);
                  boolean var7 = class310.world.getBlockState(this.class23382).isAir();
                  if (var7) {
                     class310.player.swingHand(Hand.MAIN_HAND);
                     this.class23383 = this.class23382;
                     this.longVal5 = var1;
                     this.class23382 = null;
                     this.class2350 = null;
                     this.longVal6 = 0L;
                  }
               }
            }
         }
      }
   }

   private void run21(BlockPos var1, long var2) {
      this.map.put(var1.asLong(), System.currentTimeMillis() + var2);
   }

   private boolean check(BlockPos var1) {
      Long var2 = this.map.get(var1.asLong());
      return var2 != null && var2 > System.currentTimeMillis();
   }

   private void run4(long var1) {
      this.map.entrySet().removeIf(_e -> false);
   }

   public void run5(DrawContext var1, float var2) {
      if (this.showTarget.getValue()) {
         if (this.class2338 != null && class310.player != null) {
            BlockPos var3 = this.class23382 != null ? this.class23382 : new BlockPos(this.class2338.getX(), this.intVal, this.class2338.getZ());
            Vec3d var4 = this.class243Of(var3);
            double[] var5 = Matrix4fUtils.doubleArrayOf(var4);
            if (var5 != null) {
               int var6 = (int)Math.round(var5[0]);
               int var7 = (int)Math.round(var5[1]);
               int var8 = this.targetColor.getValue();
               int var9 = var8 & 16777215 | 0xFF000000;
               var1.fill(var6 - 4, var7 - 4, var6 + 4, var7 - 3, var9);
               var1.fill(var6 - 4, var7 + 3, var6 + 4, var7 + 4, var9);
               var1.fill(var6 - 4, var7 - 3, var6 - 3, var7 + 3, var9);
               var1.fill(var6 + 3, var7 - 3, var6 + 4, var7 + 3, var9);
               var1.fill(var6 - 3, var7 - 3, var6 + 3, var7 + 3, var8);
            }
         }
      }
   }

   private BlockPos getclass2338() {
      double var1 = this.range.getValue();
      double var3 = var1 * var1;
      int var5 = (int)Math.ceil(var1);
      int var6 = class310.player.getBlockX();
      int var7 = class310.player.getBlockY();
      int var8 = class310.player.getBlockZ();
      Vec3d var9 = class310.player.getEyePos();
      BlockPos var10 = null;
      double var11 = Double.MAX_VALUE;
      Mutable var13 = new Mutable();

      for (int var14 = -var5; var14 <= var5; var14++) {
         for (int var15 = -var5; var15 <= var5; var15++) {
            for (int var16 = -var5; var16 <= var5; var16++) {
               var13.set(var6 + var14, var7 + var15, var8 + var16);
               BlockState var17 = class310.world.getBlockState(var13);
               if (this.check2(var17)) {
                  double var18 = var9.squaredDistanceTo(var13.getX() + 0.5, var13.getY() + 0.5, var13.getZ() + 0.5);
                  if (!(var18 > var3) && var18 < var11) {
                     var11 = var18;
                     var10 = var13.toImmutable();
                  }
               }
            }
         }
      }

      if (var10 == null) {
         return null;
      } else {
         Mutable var20 = var10.mutableCopy();

         while (var20.getY() > class310.world.getBottomY()) {
            var20.move(Direction.DOWN);
            if (!this.check2(class310.world.getBlockState(var20))) {
               var20.move(Direction.UP);
               break;
            }
         }

         return var20.toImmutable();
      }
   }

   private boolean check2(BlockState var1) {
      if (var1 != null && !var1.isAir()) {
         Block var2 = var1.getBlock();
         boolean var3 = var2 == Blocks.BAMBOO_BLOCK || var2 == Blocks.STRIPPED_BAMBOO_BLOCK;
         boolean var4 = var1.isIn(BlockTags.LOGS);
         if (!var4 && !var3) {
            return false;
         } else {
            if (!this.includeStripped.getValue()) {
               Identifier var5 = Registries.BLOCK.getId(var2);
               if (var5 != null && var5.getPath().startsWith("stripped_")) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private boolean check3(BlockPos var1) {
      Vec3d var2 = class310.player.getEyePos();
      double var3 = this.range.getValue();
      return var2.squaredDistanceTo(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5) <= var3 * var3;
   }

   private Vec3d class243Of(BlockPos var1) {
      return new Vec3d(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5);
   }

   private Direction class2350Of(BlockPos var1) {
      Vec3d var2 = class310.player.getEyePos();
      double var3 = var2.x - (var1.getX() + 0.5);
      double var5 = var2.y - (var1.getY() + 0.5);
      double var7 = var2.z - (var1.getZ() + 0.5);
      double var9 = Math.abs(var3);
      double var11 = Math.abs(var5);
      double var13 = Math.abs(var7);
      if (var11 >= var9 && var11 >= var13) {
         return var5 >= 0.0 ? Direction.UP : Direction.DOWN;
      } else if (var9 >= var13) {
         return var3 >= 0.0 ? Direction.EAST : Direction.WEST;
      } else {
         return var7 >= 0.0 ? Direction.SOUTH : Direction.NORTH;
      }
   }

   public int getInt() {
      PlayerInventory var1 = class310.player.getInventory();
      int var2 = -1;
      double var3 = 0.0;
      BlockState var5 = Blocks.OAK_LOG.getDefaultState();

      for (int var6 = 0; var6 < PlayerInventory.getHotbarSize(); var6++) {
         ItemStack var7 = var1.getStack(var6);
         if (!var7.isEmpty() && var7.isIn(ItemTags.AXES)) {
            double var8 = var7.getMiningSpeedMultiplier(var5);
            if (var8 > var3) {
               var3 = var8;
               var2 = var6;
            }
         }
      }

      return var2;
   }

   public void run7(int var1) {
      PlayerInventory var2 = class310.player.getInventory();
      int var3 = var2.getSelectedSlot();
      if (var3 != var1) {
         if (this.intVal2 < 0) {
            this.intVal2 = var3;
         }

         var2.setSelectedSlot(var1);
         if (class310.getNetworkHandler() != null) {
            class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
         }
      }
   }

   private void run6() {
      if (this.intVal2 >= 0 && class310.player != null) {
         PlayerInventory var1 = class310.player.getInventory();
         if (this.intVal2 < PlayerInventory.getHotbarSize() && var1.getSelectedSlot() != this.intVal2) {
            var1.setSelectedSlot(this.intVal2);
            if (class310.getNetworkHandler() != null) {
               class310.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(this.intVal2));
            }
         }

         this.intVal2 = -1;
      } else {
         this.intVal2 = -1;
      }
   }

   private void run8() {
      m$aUtils.run5();
      this.class23382 = null;
      this.class2350 = null;
      this.longVal6 = 0L;
   }

   private long getLong() {
      double var1 = this.maxPerSec.getValue();
      if (var1 <= 0.0) {
         var1 = 1.0;
      }

      if (this.serverSafeMode.getValue() && var1 > 3.0) {
         var1 = 3.0;
      }

      long var3 = Math.max(1L, (long)(1000.0 / var1));
      if (!this.jitter.getValue()) {
         return var3;
      } else {
         int var5 = (int)Math.max(1L, var3 / 3L);
         int var6 = this.random.nextInt(var5 + 1) - var5 / 2;
         return Math.max(1L, var3 + var6);
      }
   }

   private void run26(BlockPos var1) {
      if (class310.world != null && class310.player != null && class310.interactionManager != null) {
         BlockPos var2 = var1.down();
         BlockState var3 = class310.world.getBlockState(var2);
         if (this.check4(var3)) {
            BlockState var4 = class310.world.getBlockState(var1);
            if (var4.isAir()) {
               int var5 = this.getInt2();
               if (var5 >= 0) {
                  this.run7(var5);
                  Vec3d var6 = new Vec3d(var1.getX() + 0.5, var2.getY() + 1.0, var1.getZ() + 0.5);
                  BlockHitResult var7 = new BlockHitResult(var6, Direction.UP, var2, false);
                  class310.interactionManager.interactBlock(class310.player, Hand.MAIN_HAND, var7);
                  class310.player.swingHand(Hand.MAIN_HAND);
               }
            }
         }
      }
   }

   private int getInt2() {
      PlayerInventory var1 = class310.player.getInventory();

      for (int var2 = 0; var2 < PlayerInventory.getHotbarSize(); var2++) {
         ItemStack var3 = var1.getStack(var2);
         if (!var3.isEmpty()) {
            if (var3.isIn(ItemTags.SAPLINGS)) {
               return var2;
            }

            if (var3.getItem() instanceof BlockItem var4) {
               Block var6 = var4.getBlock();
               if (var6 == Blocks.MANGROVE_PROPAGULE) {
                  return var2;
               }
            }
         }
      }

      return -1;
   }

   private boolean check4(BlockState var1) {
      Block var2 = var1.getBlock();
      return var2 == Blocks.GRASS_BLOCK
         || var2 == Blocks.DIRT
         || var2 == Blocks.COARSE_DIRT
         || var2 == Blocks.PODZOL
         || var2 == Blocks.MYCELIUM
         || var2 == Blocks.ROOTED_DIRT
         || var2 == Blocks.MOSS_BLOCK
         || var2 == Blocks.MUD;
   }

   @Override
   public String getString3() {
      if (this.class2338 == null) {
         return null;
      } else {
         int var1 = this.intVal - this.class2338.getY();
         return "§7y+" + var1;
      }
   }

   private static boolean check8(long var0, Entry var2) {
      return ((Long)var2.getValue()) <= var0;
   }
}

