package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.util.math.Vec3d;

public class ChunkSpoof extends Module {
   private final NumberSetting maxStepPerTick = new NumberSetting("MaxStepPerTick", 4.0, 1.0, 7.0, 0.5);
   private final NumberSetting holdTicks = new NumberSetting("HoldTicks", 3.0, 1.0, 10.0, 1.0);
   private final NumberSetting maxRangeBlocks = new NumberSetting("MaxRangeBlocks", 24.0, 8.0, 64.0, 1.0);
   private final BooleanSetting onlyWhenFreecam = new BooleanSetting("OnlyWhenFreecam", true);
   private static volatile Vec3d class243 = null;
   private ChunkSpoof.State chunkSpoofState = ChunkSpoof.State.IDLE;
   private Vec3d class2432 = Vec3d.ZERO;
   private Vec3d class2433 = Vec3d.ZERO;
   private Vec3d class2434 = Vec3d.ZERO;
   private int intVal = 0;
   private boolean bool = false;
   private long longVal = 0L;
   private long longVal2 = 0L;
   private static boolean bool2 = false;
   private static boolean bool3 = false;

   public ChunkSpoof() {
      super("ChunkSpoof", "Mutates the vanilla movement packet to force chunk streaming at Freecam cam pos — no extra packets", Category.MOVEMENT);
      this.run6(new Setting[]{this.maxStepPerTick, this.holdTicks, this.maxRangeBlocks, this.onlyWhenFreecam});
   }

   public static Vec3d getclass243() {
      return class243;
   }

   @Override
   public void run() {
      this.chunkSpoofState = ChunkSpoof.State.IDLE;
      class243 = null;
      this.intVal = 0;
      if (!this.bool) {
         this.bool = true;
      }
   }

   @Override
   public void run2() {
      this.chunkSpoofState = ChunkSpoof.State.IDLE;
      class243 = null;
      this.intVal = 0;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.getNetworkHandler() != null) {
         this.longVal++;
         this.class2432 = new Vec3d(class310.player.getX(), class310.player.getY(), class310.player.getZ());
         boolean var1 = FreecamModule.bool;
         Vec3d var2 = var1 ? FreecamModule.class243 : this.class2432;
         boolean var3 = !this.onlyWhenFreecam.getValue() || var1;
         if (!var3 && this.chunkSpoofState != ChunkSpoof.State.IDLE && this.chunkSpoofState != ChunkSpoof.State.WALKING_BACK) {
            this.chunkSpoofState = ChunkSpoof.State.WALKING_BACK;
         }

         switch (this.chunkSpoofState) {
            case IDLE:
               double var4 = this.class2432.distanceTo(var2);
               if (var3 && var1 && var4 > 8.0) {
                  this.class2434 = class243Of2(var2, this.class2432, this.maxRangeBlocks.getValue());
                  this.class2433 = this.class2432;
                  this.chunkSpoofState = ChunkSpoof.State.WALKING_OUT;
                  bool3 = true;
               }
               break;
            case WALKING_OUT:
               this.class2434 = class243Of2(var2, this.class2432, this.maxRangeBlocks.getValue());
               this.class2433 = this.class243Of(this.class2433, this.class2434);
               if (this.class2433.distanceTo(this.class2434) < 0.05) {
                  this.chunkSpoofState = ChunkSpoof.State.HOLDING;
                  this.intVal = (int)Math.round(this.holdTicks.getValue());
               }
               break;
            case HOLDING:
               this.class2433 = this.class2434;
               if (--this.intVal <= 0) {
                  this.chunkSpoofState = ChunkSpoof.State.WALKING_BACK;
               }
               break;
            case WALKING_BACK:
               this.class2433 = this.class243Of(this.class2433, this.class2432);
               if (this.class2433.distanceTo(this.class2432) < 0.05) {
                  class243 = null;
                  this.chunkSpoofState = ChunkSpoof.State.IDLE;
                  return;
               }
         }

         class243 = this.chunkSpoofState == ChunkSpoof.State.IDLE ? null : this.class2433;
         if (class243 != null && !bool2) {
            bool2 = true;
         }
      } else {
         class243 = null;
         this.chunkSpoofState = ChunkSpoof.State.IDLE;
      }
   }

   private Vec3d class243Of(Vec3d var1, Vec3d var2) {
      double var3 = this.maxStepPerTick.getValue();
      double var5 = Math.min(3.0, var3);
      double var7 = var2.x - var1.x;
      double var9 = var2.y - var1.y;
      double var11 = var2.z - var1.z;
      double var13 = Math.sqrt(var7 * var7 + var11 * var11);
      if (var13 > var3) {
         double var15 = var3 / var13;
         var7 *= var15;
         var11 *= var15;
      }

      if (Math.abs(var9) > var5) {
         var9 = Math.copySign(var5, var9);
      }

      return var1.add(var7, var9, var11);
   }

   private static Vec3d class243Of2(Vec3d var0, Vec3d var1, double var2) {
      Vec3d var4 = var0.subtract(var1);
      double var5 = var4.length();
      return var5 <= var2 ? var0 : var1.add(var4.multiply(var2 / var5));
   }

   private static enum State {
      IDLE,
      WALKING_OUT,
      HOLDING,
      WALKING_BACK;

      private static final ChunkSpoof.State[] chunkSpoofStateArray = getChunkSpoofStateArray();

      private static ChunkSpoof.State[] getChunkSpoofStateArray() {
         return new ChunkSpoof.State[]{IDLE, WALKING_OUT, HOLDING, WALKING_BACK};
      }
   }
}

