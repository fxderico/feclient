package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import dev.fede.nyx.util.AutoCrystalModuleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class MaceAuraModule extends Module {
   private static final double doubleVal = 0.08;
   private static final double doubleVal2 = 2.5;
   private final NumberSetting minFallDistance = new NumberSetting("MinFallDistance", 3.0, 1.5, 20.0, 0.5);
   private final NumberSetting maxRange = new NumberSetting("MaxRange", 4.0, 3.0, 6.0, 0.1);
   private final NumberSetting jumpBoostMs = new NumberSetting("JumpBoostMs", 400.0, 0.0, 2000.0, 50.0);
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting mobs = new BooleanSetting("Mobs", true);
   private final BooleanSetting requireMace = new BooleanSetting("RequireMace", true);
   private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private LivingEntity class1309;

   public MaceAuraModule() {
      super("MaceAura", "Jumps to build fall distance then smashes with the mace", Category.COMBAT);
      this.run6(new Setting[]{this.minFallDistance, this.maxRange, this.jumpBoostMs, this.players, this.mobs, this.requireMace, this.rotate});
   }

   @Override
   public void run2() {
      this.class1309 = null;
      AntiAFKModuleUtil.run2();
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (this.requireMace.getValue() && !class310.player.getMainHandStack().isOf(Items.MACE)) {
            this.class1309 = null;
            AntiAFKModuleUtil.run2();
         } else {
            this.class1309 = this.getclass1309();
            if (this.class1309 == null) {
               AntiAFKModuleUtil.run2();
            } else {
               boolean var1 = class310.player.isOnGround();
               float var2 = (float)class310.player.fallDistance;
               if (!var1 && var2 >= this.minFallDistance.getValueFloat()) {
                  if (this.rotate.getValue()) {
                     float[] var3 = AutoCrystalModuleUtil.floatArrayOf2(this.class1309);
                     AntiAFKModuleUtil.run(var3[0], var3[1]);
                  }

                  class310.player.attack(this.class1309);
                  class310.player.swingHand(Hand.MAIN_HAND);
               } else {
                  if (var1 && this.class1309.isOnGround() && this.antiVoidModuleHelper.check2(this.jumpBoostMs.getValue())) {
                     this.run3();
                     this.antiVoidModuleHelper.run();
                  }
               }
            }
         }
      }
   }

   public void run3() {
      double var1 = Math.max(1.5, this.minFallDistance.getValue() + 1.0);
      double var3 = Math.min(2.5, Math.sqrt(0.16 * var1));
      Vec3d var5 = class310.player.getVelocity();
      class310.player.setVelocity(var5.x, var3, var5.z);
   }

   private LivingEntity getclass1309() {
      LivingEntity var1 = null;
      double var2 = Double.MAX_VALUE;
      double var4 = this.maxRange.getValue();

      for (Entity var7 : class310.world.getEntities()) {
         if (var7 instanceof LivingEntity var8
            && var7 != class310.player
            && var7.isAlive()
            && !(var8.getHealth() <= 0.0F)
            && !(
               var7 instanceof PlayerEntity var9
                  ? !this.players.getValue() || var9.isCreative() || var9.isSpectator()
                  : !(var7 instanceof Monster) || !this.mobs.getValue()
            )) {
            double var11 = class310.player.distanceTo(var7);
            if (!(var11 > var4) && var11 < var2) {
               var2 = var11;
               var1 = var8;
            }
         }
      }

      return var1;
   }

   @Override
   public String getString3() {
      return this.class1309 != null ? "§7" + this.class1309.getName().getString() : null;
   }
}

