package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.mixin.BacktrackEntityUpdateMixin;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BacktrackModule extends Module {
   private final NumberSetting delayMs = new NumberSetting("DelayMs", 200.0, 0.0, 1000.0, 10.0);
   private final NumberSetting rangeGate = new NumberSetting("RangeGate", 4.5, 0.0, 20.0, 0.1);
   private final BooleanSetting players = new BooleanSetting("TargetsPlayers", true);
   private final BooleanSetting mobs = new BooleanSetting("TargetsMobs", false);
   private final BooleanSetting renderGhost = new BooleanSetting("RenderGhost", true);
   private static final int intVal = -1875247204;
   private final Set<Integer> set = new HashSet<>();

   public BacktrackModule() {
      super("Backtrack", "Delays received entity positions so you can hit them in the past", Category.COMBAT);
      this.run6(new Setting[]{this.delayMs, this.rangeGate, this.players, this.mobs, this.renderGhost});
   }

   @Override
   public void run() {
      BacktrackEntityUpdateMixin.BacktrackBuffer.enabled = true;
   }

   @Override
   public void run2() {
      BacktrackEntityUpdateMixin.BacktrackBuffer.enabled = false;
      BacktrackEntityUpdateMixin.BacktrackBuffer.flushAll();
      this.set.clear();
      BacktrackEntityUpdateMixin.BacktrackBuffer.publishTracked(this.set);
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         BacktrackEntityUpdateMixin.BacktrackBuffer.enabled = true;
         BacktrackEntityUpdateMixin.BacktrackBuffer.delayMs = this.delayMs.getValueLong();
         BacktrackEntityUpdateMixin.BacktrackBuffer.rangeGateSq = this.rangeGate.getValue() * this.rangeGate.getValue();
         this.set.clear();
         Vec3d var1 = class310.player.getEntityPos();
         double var2 = BacktrackEntityUpdateMixin.BacktrackBuffer.rangeGateSq;

         for (Entity var5 : class310.world.getEntities()) {
            if (var5 != class310.player && var5 instanceof LivingEntity var6 && var6.isAlive() && !(var6.getHealth() <= 0.0F)) {
               boolean var7 = var5 instanceof PlayerEntity;
               boolean var8 = var5 instanceof Monster;
               if ((!var7 || this.players.getValue()) && (!var7 && !var8 ? this.mobs.getValue() : !var8 || this.mobs.getValue())) {
                  double var9 = var1.squaredDistanceTo(var5.getEntityPos());
                  if (!(var9 > var2)) {
                     this.set.add(var5.getId());
                  }
               }
            }
         }

         BacktrackEntityUpdateMixin.BacktrackBuffer.publishTracked(this.set);
         BacktrackEntityUpdateMixin.BacktrackBuffer.drainReady(class310.getNetworkHandler());
         if (this.renderGhost.getValue()) {
            for (int var12 : this.set) {
               Entity var13 = class310.world.getEntityById(var12);
               if (var13 != null) {
                  Vec3d var14 = BacktrackEntityUpdateMixin.BacktrackBuffer.lastSnapshot(var12);
                  if (var14 == null) {
                     var14 = var13.getEntityPos();
                  }

                  Box var15 = var13.getBoundingBox();
                  Vec3d var16 = var13.getEntityPos();
                  Box var10 = var15.offset(var14.x - var16.x, var14.y - var16.y, var14.z - var16.z);
                  ListUtils.run5(var10, -1875247204, 1.5F, true);
               }
            }
         }
      }
   }

   @Override
   public String getString3() {
      long var1 = this.delayMs.getValueLong();
      return var1 > 0L ? var1 + "ms" : null;
   }
}

