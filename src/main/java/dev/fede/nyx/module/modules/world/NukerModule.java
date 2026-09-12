package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.m$aUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;

public class NukerModule extends Module {
   private final NumberSetting range = new NumberSetting("Range", 5.0, 1.0, 6.0, 0.5);
   private final ModeSetting mode = new ModeSetting("Mode", "Full", "Full", "Selection", "Filter");
   private final StringSetting filter = (StringSetting)new StringSetting("FilterBlocks", "diamond_ore,ancient_debris", 128).visibleWhen(this::getBoolean3);
   private final BooleanSetting whitelist = (BooleanSetting)new BooleanSetting("Whitelist", true).visibleWhen(this::getBoolean2);
   private final NumberSetting rate = new NumberSetting("Rate", 4.0, 1.0, 16.0, 1.0);
   private final NumberSetting maxPerSec = new NumberSetting("MaxPerSec", 4.0, 1.0, 20.0, 1.0);
   private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
   private final BooleanSetting silentRotate = new BooleanSetting("SilentRotate", true);
   private final BooleanSetting jitter = new BooleanSetting("Jitter", true);
   private final BooleanSetting serverSafeMode = new BooleanSetting("ServerSafeMode", true);
   private final NumberSetting stuckTimeoutMs = new NumberSetting("StuckTimeoutMs", 4000.0, 2000.0, 15000.0, 500.0);
   private final NumberSetting blacklistDurationMs = new NumberSetting("BlacklistDurationMs", 30000.0, 5000.0, 120000.0, 1000.0);
   private String string = "";
   private Set<String> set = Collections.emptySet();
   private BlockPos class2338;
   private Direction class2350;
   private final Random random = new Random();
   private long longVal;
   private long longVal2 = 250L;
   private static final long longVal3 = 100L;
   private static final long longVal4 = 1000L;
   private static final long longVal5 = 5000L;
   private BlockPos class23382;
   private long longVal6;
   private long longVal7;
   private final Map<Long, Long> map = new HashMap<>();

   public NukerModule() {
      super("Nuker", "Breaks every reachable block that matches the active mode", Category.WORLD);
      this.run6(
         new Setting[]{
            this.range,
            this.mode,
            this.filter,
            this.whitelist,
            this.rate,
            this.maxPerSec,
            this.rotate,
            this.silentRotate,
            this.jitter,
            this.serverSafeMode,
            this.stuckTimeoutMs,
            this.blacklistDurationMs
         }
      );
   }

   @Override
   public void run() {
      this.class2338 = null;
      this.class2350 = null;
      this.longVal = 0L;
      this.longVal2 = this.getLong();
      this.class23382 = null;
      this.longVal6 = 0L;
      this.longVal7 = 0L;
      this.map.clear();
   }

   @Override
   public void run2() {
      m$aUtils.run5();
      this.class2338 = null;
      this.class2350 = null;
      this.longVal7 = 0L;
      this.class23382 = null;
      this.longVal6 = 0L;
      AntiAFKModuleUtil.run2();
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         long var1 = System.currentTimeMillis();
         this.run5(var1);
         if (this.class23382 != null) {
            long var3 = var1 - this.longVal6;
            boolean var5 = class310.world.getBlockState(this.class23382).isAir();
            if (!var5) {
               this.run21(this.class23382, 5000L);
               m$aUtils.run5();
               this.class23382 = null;
               this.longVal6 = 0L;
            } else if (var3 >= 100L || var3 >= 1000L) {
               this.class23382 = null;
               this.longVal6 = 0L;
            }

            if (this.class23382 != null) {
               return;
            }
         }

         List var10 = this.getList();
         if (var10.isEmpty()) {
            if (this.class2338 != null) {
               this.run4();
            }

            AntiAFKModuleUtil.run2();
         } else {
            if (this.rotate.getValue() && !AntiAFKModuleUtil.isEnabled2()) {
               this.run6((BlockPos)var10.get(0));
            }

            int var4 = Math.max(1, this.rate.getValueInt());
            int var11 = Math.min(var4, var10.size());
            List var6 = var10.subList(0, var11);
            if (this.class2338 != null && (!this.check2(this.class2338) || !var6.contains(this.class2338))) {
               this.run4();
            }

            if (this.class2338 != null && this.longVal7 > 0L && var1 - this.longVal7 > this.stuckTimeoutMs.getValueInt()) {
               this.run21(this.class2338, this.blacklistDurationMs.getValueInt());
               this.run4();
            }

            BlockPos var7 = null;

            for (BlockPos var9 : (java.util.List<BlockPos>)var6) {
               if (!this.check(var9)) {
                  var7 = var9;
                  break;
               }
            }

            if (var7 == null) {
               if (this.class2338 != null) {
                  this.run4();
               }

               AntiAFKModuleUtil.run2();
            } else {
               if (this.class2338 == null) {
                  if (var1 - this.longVal < this.longVal2) {
                     return;
                  }

                  Direction var12 = m$aUtils.class2350Of(var7);
                  this.class2338 = var7;
                  this.class2350 = var12;
                  this.longVal7 = var1;
                  m$aUtils.check(this.class2338, this.class2350, this.silentRotate.getValue());
                  this.longVal = var1;
                  this.longVal2 = this.getLong();
               }

               m$aUtils.check3(this.class2338, this.class2350);
               boolean var13 = class310.world.getBlockState(this.class2338).isAir();
               if (var13) {
                  this.class23382 = this.class2338;
                  this.longVal6 = var1;
                  this.class2338 = null;
                  this.class2350 = null;
                  this.longVal7 = 0L;
               }
            }
         }
      }
   }

   private void run4() {
      m$aUtils.run5();
      this.class2338 = null;
      this.class2350 = null;
      this.longVal7 = 0L;
   }

   private long getLong() {
      double var1 = this.maxPerSec.getValue();
      if (var1 <= 0.0) {
         var1 = 1.0;
      }

      if (this.serverSafeMode.getValue() && var1 > 3.0) {
         var1 = 3.0;
      }

      double var3 = 1000.0 / var1;
      if (this.jitter.getValue()) {
         double var5 = (this.random.nextDouble() * 2.0 - 1.0) * 0.2;
         var3 *= 1.0 + var5;
      }

      return Math.max(1L, (long)var3);
   }

   private void run21(BlockPos var1, long var2) {
      this.map.put(var1.asLong(), System.currentTimeMillis() + var2);
   }

   private boolean check(BlockPos var1) {
      Long var2 = this.map.get(var1.asLong());
      return var2 != null && var2 > System.currentTimeMillis();
   }

   private void run5(long var1) {
      this.map.entrySet().removeIf(_e -> false);
   }

   public List<BlockPos> getList_nf() {
      ArrayList var1 = new ArrayList();
      double var2 = this.range.getValue();
      double var4 = var2 * var2;
      if (this.mode.check("Selection")) {
         if (class310.crosshairTarget instanceof BlockHitResult var18 && var18.getType() == Type.BLOCK) {
            BlockPos var20 = var18.getBlockPos();
            if (this.check2(var20) && this.check3(var20, var4)) {
               var1.add(var20);
            }
         }

         return var1;
      } else {
         Vec3d var6 = class310.player.getEyePos();
         int var7 = (int)Math.ceil(var2);
         Mutable var8 = new Mutable();
         int var9 = class310.player.getBlockX();
         int var10 = class310.player.getBlockY();
         int var11 = class310.player.getBlockZ();
         boolean var12 = this.mode.check("Filter");
         Set var13 = var12 ? this.getSet() : null;
         boolean var14 = this.whitelist.getValue();

         for (int var15 = -var7; var15 <= var7; var15++) {
            for (int var16 = -var7; var16 <= var7; var16++) {
               for (int var17 = -var7; var17 <= var7; var17++) {
                  var8.set(var9 + var15, var10 + var16, var11 + var17);
                  if (this.check3(var8, var4) && this.check2(var8) && (!var12 || this.check4(var8, var13, var14))) {
                     var1.add(var8.toImmutable());
                  }
               }
            }
         }

         var1.sort(Comparator.comparingDouble(bp -> 0.0));
         return var1;
      }
   }

   private boolean check2(BlockPos var1) {
      if (class310.world == null) {
         return false;
      } else {
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
   }

   private boolean check3(BlockPos var1, double var2) {
      if (class310.player == null) {
         return false;
      } else {
         Vec3d var4 = class310.player.getEyePos();
         double var5 = var1.getX() + 0.5;
         double var7 = var1.getY() + 0.5;
         double var9 = var1.getZ() + 0.5;
         return var4.squaredDistanceTo(var5, var7, var9) <= var2;
      }
   }

   private boolean check4(BlockPos var1, Set<String> var2, boolean var3) {
      Block var4 = class310.world.getBlockState(var1).getBlock();
      Identifier var5 = Registries.BLOCK.getId(var4);
      String var6 = var5.getPath();
      String var7 = var5.toString();
      boolean var8 = var2.contains(var6) || var2.contains(var7);
      return var3 == var8;
   }

   private Set<String> getSet() {
      String var1 = this.filter.getValue();
      if (var1 == null) {
         var1 = "";
      }

      if (!var1.equals(this.string)) {
         this.string = var1;
         HashSet var2 = new HashSet();

         for (String var4 : Arrays.asList(var1.split(","))) {
            String var5 = var4.trim().toLowerCase();
            if (!var5.isEmpty()) {
               var2.add(var5);
            }
         }

         this.set = var2;
      }

      return this.set;
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

   private void run6(BlockPos var1) {
      Vec3d var2 = class310.player.getEyePos();
      double var3 = var1.getX() + 0.5 - var2.x;
      double var5 = var1.getY() + 0.5 - var2.y;
      double var7 = var1.getZ() + 0.5 - var2.z;
      double var9 = Math.sqrt(var3 * var3 + var7 * var7);
      float var11 = (float)(Math.toDegrees(Math.atan2(var7, var3)) - 90.0);
      float var12 = (float)(-Math.toDegrees(Math.atan2(var5, var9)));
      if (var12 < -90.0F) {
         var12 = -90.0F;
      }

      if (var12 > 90.0F) {
         var12 = 90.0F;
      }

      AntiAFKModuleUtil.run(var11, var12);
   }

   @Override
   public String getString3() {
      return "§7" + this.mode.getMode();
   }

   private static double doubleOf(Vec3d var0, BlockPos var1) {
      return var0.squaredDistanceTo(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5);
   }

   private static boolean check8(long var0, Entry var2) {
      return ((Long)var2.getValue()) <= var0;
   }

   private Boolean getBoolean2() {
      return this.mode.check("Filter");
   }

   private Boolean getBoolean3() {
      return this.mode.check("Filter");
   }
}

