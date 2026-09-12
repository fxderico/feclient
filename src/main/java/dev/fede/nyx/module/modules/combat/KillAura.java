package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.auth.AuthGate;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BindSetting;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import dev.fede.nyx.util.AntiDebugUtil;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import dev.fede.nyx.util.AutoCrystalModuleUtil;
import dev.fede.nyx.util.m$aUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {
   private final ModeSetting mode = new ModeSetting("Mode", "Silent", "Silent", "LegitVanilla", "SmoothAim");
   private final ModeSetting priority = new ModeSetting("Priority", "Distance", "Health", "Distance", "LookingAt", "HurtTime");
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting passive = new BooleanSetting("Passive", false);
   private final BooleanSetting hostile = new BooleanSetting("Hostile", true);
   private final BooleanSetting neutral = new BooleanSetting("Neutral", false);
   private final BooleanSetting invisible = new BooleanSetting("Invisible", false);
   private final BooleanSetting bots = new BooleanSetting("Bots", false);
   private final NumberSetting range = new NumberSetting("Range", 4.5, 2.0, 6.0, 0.1);
   private final NumberSetting attackRange = new NumberSetting("AttackRange", 3.0, 2.0, 6.0, 0.1);
   private final NumberSetting fov = new NumberSetting("FOV", 180.0, 30.0, 360.0, 5.0);
   private final NumberSetting rotationSpeed = new NumberSetting("RotationSpeed", 0.25, 0.05, 1.0, 0.05);
   private final NumberSetting smoothingTicks = new NumberSetting("SmoothingTicks", 3.0, 1.0, 8.0, 1.0);
   private final NumberSetting missChance = new NumberSetting("MissChance", 0.0, 0.0, 0.25, 0.01);
   private final NumberSetting ticksPerAttack = new NumberSetting("TicksPerAttack", 8.0, 1.0, 20.0, 1.0);
   private final NumberSetting hurtTimeFilter = new NumberSetting("HurtTimeFilter", 10.0, 0.0, 10.0, 1.0);
   private final BooleanSetting onlyWhenHoldingWeapon = new BooleanSetting("OnlyWhenHoldingWeapon", false);
   private final BooleanSetting raycastCheck = new BooleanSetting("RaycastCheck", true);
   private final BooleanSetting pauseWhileEating = new BooleanSetting("PauseWhileEating", true);
   private final BooleanSetting pauseWhileMining = new BooleanSetting("PauseWhileMining", true);
   private final NumberSetting maxReach = new NumberSetting("MaxReach", 3.0, 2.5, 6.0, 0.05);
   private final BooleanSetting preSwingLook = new BooleanSetting("PreSwingLook", true);
   private final BooleanSetting setbackGuard = new BooleanSetting("SetbackGuard", true);
   private final BooleanSetting sprintSync = new BooleanSetting("SprintSync", true);
   private final BooleanSetting swingAnimation = new BooleanSetting("SwingAnimation", true);
   private final BooleanSetting debug = new BooleanSetting("Debug", false);
   private final ColorSetting targetHighlight = new ColorSetting("TargetHighlight", -2143682796);
   private static final byte[] byteArray = new byte[]{-54, -105, -99, 124, 89, 6, -23, 0, -18};
   private final BindSetting toggleKey = new BindSetting("ToggleKey", 82);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private Entity class1297;
   private Entity class12972;
   private float floatVal;
   private float floatVal2;
   private boolean bool;
   private boolean bool2;
   private boolean bool3;
   private long longVal;

   public KillAura() {
      super("KillAura", "Attacks nearby entities", Category.COMBAT);
      this.run6(
         new Setting[]{
            this.mode,
            this.priority,
            this.players,
            this.passive,
            this.hostile,
            this.neutral,
            this.invisible,
            this.bots,
            this.range,
            this.attackRange,
            this.fov,
            this.rotationSpeed,
            this.smoothingTicks,
            this.missChance,
            this.ticksPerAttack,
            this.hurtTimeFilter,
            this.onlyWhenHoldingWeapon,
            this.raycastCheck,
            this.pauseWhileEating,
            this.pauseWhileMining,
            this.maxReach,
            this.preSwingLook,
            this.setbackGuard,
            this.sprintSync,
            this.swingAnimation,
            this.debug,
            this.targetHighlight,
            this.toggleKey
         }
      );
      this.rotationSpeed.visibleWhen(this::getBoolean6);
      this.smoothingTicks.visibleWhen(this::getBoolean5);
      this.hurtTimeFilter.visibleWhen(this::getBoolean4);
   }

   @Override
   public void run2() {
      this.class1297 = null;
      this.class12972 = null;
      AntiAFKModuleUtil.run2();
      this.run4();
   }

   private void run4() {
      this.bool = false;
      this.bool2 = false;
      this.bool3 = false;
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null) {
         if (this.toggleKey.getValue() != this.getInt()) {
            this.run7(this.toggleKey.getValue());
         }

         if (this.isEnabled()) {
            this.class1297 = null;
            AntiAFKModuleUtil.run2();
            this.run4();
         } else if (this.onlyWhenHoldingWeapon.getValue() && !this.isEnabled2()) {
            this.class1297 = null;
            AntiAFKModuleUtil.run2();
            this.run4();
         } else {
            Entity var1 = this.getclass1297();
            if (var1 == null) {
               if (this.class1297 != null) {
                  AntiAFKModuleUtil.run2();
                  this.run4();
               }

               this.class1297 = null;
               this.class12972 = null;
            } else {
               if (var1 != this.class12972) {
                  NotificationUtils.run8("KillAura", "Target: " + var1.getName().getString(), INFO.UNKNOWN);
                  this.class12972 = var1;
               }

               this.class1297 = var1;
               float[] var2 = AutoCrystalModuleUtil.floatArrayOf2(this.class1297);
               this.run3(var2);
               int var3 = (int)(
                  AuthGate.longOf2(
                        (
                              -1797384171963803040L + -2853721806304646646L - ((-1797384171963803040L & -3154495101125429258L - -300773294820782612L) << 1)
                                 ^ -5667498951016467026L
                           )
                           + (2699926672696490671L - 38288964815822987L)
                           + (
                              3711288164360275033L - 8414847319375751540L
                                 ^ -4844162493228193665L + 6038109740380615354L - ((-4844162493228193665L & 6038109740380615354L) << 1)
                                 ^ 4449328893356579200L
                           )
                     )
                     ^ (-1053026965111960294L ^ 2155005307914169060L)
                        + ((-1053026965111960294L & 2155005307914169060L) << 1)
                        + (1482470488835660251L | 5976821296062190203L)
                        + (
                           8271059441705934497L + -1475248201989709314L + -5313340750880564932L
                              & 8013081317285163680L + -5655382124242106271L + 3619122103019132794L
                        )
                        + (-7484485088248157814L + -8346446554165052466L - ((-7484485088248157814L & -8346446554165052466L) << 1) ^ -132380624504137013L)
               );
               ListUtils.run5(this.class1297.getBoundingBox(), this.targetHighlight.getValue() ^ var3, 1.5F, false);
               this.run5(var2);
            }
         }
      }
   }

   private void run3(float[] var1) {
      float var2 = var1[0];
      float var3 = MathHelper.clamp(var1[1], -90.0F, 90.0F);
      int var4 = Math.max(1, this.smoothingTicks.getValueInt());
      float var5 = 1.0F / var4;
      if (this.mode.check("Silent")) {
         if (!this.bool) {
            this.floatVal = class310.player.getYaw();
            this.floatVal2 = class310.player.getPitch();
            this.bool = true;
         }

         float var6 = MathHelper.wrapDegrees(var2 - this.floatVal);
         float var7 = var3 - this.floatVal2;
         this.floatVal = MathHelper.wrapDegrees(this.floatVal + var6 * var5);
         this.floatVal2 = MathHelper.clamp(this.floatVal2 + var7 * var5, -90.0F, 90.0F);
         AntiAFKModuleUtil.run(this.floatVal, this.floatVal2);
         this.bool2 = false;
      } else if (this.mode.check("LegitVanilla")) {
         AntiAFKModuleUtil.run2();
         this.bool = false;
         if (!this.bool2) {
            this.bool2 = true;
         }

         float var11 = class310.player.getYaw();
         float var13 = class310.player.getPitch();
         float var8 = MathHelper.wrapDegrees(var2 - var11);
         float var9 = var3 - var13;
         class310.player.setYaw(var11 + var8 * var5);
         class310.player.setPitch(MathHelper.clamp(var13 + var9 * var5, -90.0F, 90.0F));
      } else {
         AntiAFKModuleUtil.run2();
         this.run4();
         float var12 = this.rotationSpeed.getValueFloat();
         float var14 = class310.player.getYaw();
         float var15 = class310.player.getPitch();
         float var16 = MathHelper.wrapDegrees(var2 - var14);
         float var10 = var3 - var15;
         class310.player.setYaw(var14 + var16 * var12);
         class310.player.setPitch(MathHelper.clamp(var15 + var10 * var12, -90.0F, 90.0F));
      }
   }

   private void run5(float[] var1) {
      if (this.setbackGuard.getValue() && m$aUtils.isEnabled2()) {
         this.bool3 = false;
         if (this.debug.getValue()) {
            this.run6("setback cooldown active - skipping");
         }
      } else {
         Vec3d var2 = class310.player.getEyePos();
         Vec3d var3 = this.class1297.getEyePos();
         double var4 = var2.distanceTo(var3);
         double var6 = this.maxReach.getValue();
         if (var4 > var6) {
            this.bool3 = false;
            if (this.debug.getValue()) {
               this.run6(String.format("target out of reach (%.2f > %.2f)", var4, var6));
            }
         } else {
            double var8 = class310.player.squaredDistanceTo(this.class1297);
            double var10 = this.attackRange.getValue();
            if (var8 > var10 * var10) {
               this.bool3 = false;
            } else {
               double var12 = 1000.0 / Math.max(1.0, this.ticksPerAttack.getValue());
               if (this.antiVoidModuleHelper.check2(var12)) {
                  if (!this.bool3) {
                     double var14 = this.missChance.getValue();
                     if (var14 > 0.0 && ThreadLocalRandom.current().nextDouble() < var14) {
                        this.antiVoidModuleHelper.run();
                        return;
                     }
                  }

                  if (this.preSwingLook.getValue() && !this.bool3 && !this.mode.check("LegitVanilla")) {
                     float var20 = var1[0];
                     float var21 = MathHelper.clamp(var1[1], -90.0F, 90.0F);
                     class310.player.setYaw(var20);
                     class310.player.setPitch(var21);
                     AntiAFKModuleUtil.run(var20, var21);
                     if (this.mode.check("Silent")) {
                        this.floatVal = var20;
                        this.floatVal2 = var21;
                        this.bool = true;
                     }

                     this.bool3 = true;
                     if (this.debug.getValue()) {
                        this.run6(String.format("pre-swing look armed yaw=%.1f pitch=%.1f", var20, var21));
                     }
                  } else {
                     boolean var19 = false;
                     if (this.sprintSync.getValue()
                        && class310.player.isSprinting()
                        && class310.options != null
                        && class310.options.sprintKey != null
                        && class310.options.sprintKey.isPressed()) {
                        class310.options.sprintKey.setPressed(false);
                        var19 = true;
                     }

                     boolean var15 = CriticalsModule.isEnabled_s();
                     if (var15) {
                        CriticalsModule.run5();
                     }

                     boolean var16 = this.swingAnimation.getValue();
                     if (this.mode.check("LegitVanilla")) {
                        if (class310.player.getAttackCooldownProgress(0.0F) < 1.0F) {
                           if (var15) {
                              CriticalsModule.run6();
                           }

                           if (var19) {
                              class310.options.sprintKey.setPressed(true);
                           }

                           return;
                        }

                        class310.player.attack(this.class1297);
                        if (var16) {
                           class310.player.swingHand(Hand.MAIN_HAND);
                        }
                     } else if (this.mode.check("SmoothAim")) {
                        if (AutoCrystalModuleUtil.floatOf(var1) > 5.0F) {
                           if (var15) {
                              CriticalsModule.run6();
                           }

                           if (var19) {
                              class310.options.sprintKey.setPressed(true);
                           }

                           return;
                        }

                        class310.interactionManager.attackEntity(class310.player, this.class1297);
                        if (var16) {
                           class310.player.swingHand(Hand.MAIN_HAND);
                        }
                     } else {
                        class310.interactionManager.attackEntity(class310.player, this.class1297);
                        if (var16) {
                           class310.player.swingHand(Hand.MAIN_HAND);
                        }
                     }

                     if (var15) {
                        CriticalsModule.run6();
                     }

                     if (var19) {
                        class310.options.sprintKey.setPressed(true);
                     }

                     if (this.debug.getValue()) {
                        float var18 = MathHelper.wrapDegrees(var1[0] - this.floatVal);
                        this.run6(String.format("attack fired yaw=%.1f eyeDist=%.2f ramp=%.1f", var1[0], var4, Math.abs(var18)));
                     }

                     this.antiVoidModuleHelper.run();
                     this.longVal = System.currentTimeMillis();
                     this.bool3 = false;
                  }
               }
            }
         }
      }
   }

   private void run6(String var1) {
      if (class310.player != null) {
         class310.player.sendMessage(Text.literal("[KA] null"), false);
      }
   }

   public boolean isEnabled() {
      return this.pauseWhileEating.getValue() && class310.player.isUsingItem()
         ? true
         : this.pauseWhileMining.getValue() && class310.interactionManager != null && class310.interactionManager.isBreakingBlock();
   }

   private boolean isEnabled2() {
      ItemStack var1 = class310.player.getMainHandStack();
      return var1 != null && !var1.isEmpty() ? var1.isIn(ItemTags.SWORDS) || var1.getItem() instanceof AxeItem || var1.getItem() instanceof TridentItem : false;
   }

   private Entity getclass1297() {
      double var1 = this.range.getValue();
      double var3 = var1 * var1;
      float var5 = this.fov.getValueFloat() / 2.0F;
      int var6 = this.hurtTimeFilter.getValueInt();
      ArrayList var7 = new ArrayList();

      for (Entity var9 : class310.world.getEntities()) {
         if (var9 instanceof LivingEntity var10
            && var9 != class310.player
            && var9.isAlive()
            && !(var10.getHealth() <= 0.0F)
            && this.check3(var10)
            && (this.invisible.getValue() || !var9.isInvisible())
            && !(var10 instanceof PlayerEntity var11 && !this.bots.getValue() && this.check2(var11))
            && var10.hurtTime <= var6
            && !(class310.player.squaredDistanceTo(var10) > var3)
            && !(AutoCrystalModuleUtil.floatOf(AutoCrystalModuleUtil.floatArrayOf2(var10)) > var5)
            && (!this.raycastCheck.getValue() || class310.player.canSee(var10))) {
            var7.add(var10);
         }
      }

      if (var7.isEmpty()) {
         return null;
      } else {
         var7.sort(this.getComparator());
         return (Entity)var7.get(0);
      }
   }

   private boolean check3(LivingEntity var1) {
      if ((
               AuthGate.getLong()
                  ^ 817924740751981784L + (-8538282379808942630L + 1830730990100927660L + -5191601197636319776L) * -1115903497590351753L + 7734670733014658374L
            )
            != (-431838009150997857L + -6828753291435407246L + -4748136449892562210L ^ -4558844143566723322L)
               + (
                  (
                        (
                              876405909666904652L
                                    + -1198224450698128428L
                                    + 5983154480278161611L
                                    + 2977716331036746890L
                                    - ((1763300723087993189L * -5669787859611819505L & 2977716331036746890L) << 1)
                                 ^ -7198486401416859481L + -3002134146742402779L - ((-7198486401416859481L & -3002134146742402779L) << 1)
                                 ^ 8418388412658028044L
                           )
                           & (588269864501629163L ^ -6520614099100897965L)
                              + ((588269864501629163L & -6520614099100897965L) << 1)
                              + 8157208973857398810L
                              + -3343673466342646999L * -4685303513525245229L
                              + 4829062235491768421L * -661156051458418009L
                     )
                     << 1
               )
               + 5106686790086883310L
         && ThreadLocalRandom.current().nextInt(100) < 35) {
         return false;
      } else if (var1 instanceof PlayerEntity) {
         return this.players.getValue();
      } else if (var1 instanceof HostileEntity || var1 instanceof Monster) {
         return this.hostile.getValue();
      } else if (this.check(var1)) {
         return this.neutral.getValue();
      } else {
         return var1 instanceof PassiveEntity ? this.passive.getValue() : false;
      }
   }

   private boolean check(LivingEntity var1) {
      String var2 = AntiDebugUtil.stringOf2(byteArray);

      for (Class var3 = var1.getClass(); var3 != null && var3 != Object.class; var3 = var3.getSuperclass()) {
         for (Class var7 : var3.getInterfaces()) {
            if (var2.equals(var7.getSimpleName())) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean check2(PlayerEntity var1) {
      if (class310.getNetworkHandler() == null) {
         return false;
      } else {
         PlayerListEntry var2 = class310.getNetworkHandler().getPlayerListEntry(var1.getUuid());
         return var2 == null;
      }
   }

   private Comparator<LivingEntity> getComparator() {
      if (this.priority.check("Health")) {
         return Comparator.comparingDouble(LivingEntity::getHealth);
      } else if (this.priority.check("LookingAt")) {
         return Comparator.comparingDouble(KillAura::doubleOf3);
      } else {
         return this.priority.check("HurtTime")
            ? Comparator.comparingInt(KillAura::intOf).thenComparingDouble(KillAura::doubleOf2)
            : Comparator.comparingDouble(KillAura::doubleOf);
      }
   }

   @Override
   public String getString3() {
      return this.class1297 != null ? "§7" + this.class1297.getName().getString() : null;
   }

   public static LivingEntity getclass1309() {
      if (NyxClient.MODULES == null) {
         return null;
      } else {
         KillAura var0 = NyxClient.MODULES.moduleOf2(KillAura.class);
         if (var0 != null && var0.isEnabled3()) {
            return var0.class1297 instanceof LivingEntity var2 && var2.isAlive() ? var2 : null;
         } else {
            return null;
         }
      }
   }

   private static double doubleOf(LivingEntity var0) {
      return class310.player.squaredDistanceTo(var0);
   }

   private static double doubleOf2(LivingEntity var0) {
      return class310.player.squaredDistanceTo(var0);
   }

   private static int intOf(LivingEntity var0) {
      return var0.hurtTime;
   }

   private static double doubleOf3(LivingEntity var0) {
      return AutoCrystalModuleUtil.floatOf(AutoCrystalModuleUtil.floatArrayOf2(var0));
   }

   private Boolean getBoolean4() {
      return this.priority.check("HurtTime") || this.hurtTimeFilter.getValue() < 10.0;
   }

   private Boolean getBoolean5() {
      return this.mode.check("Silent") || this.mode.check("LegitVanilla");
   }

   private Boolean getBoolean6() {
      return this.mode.check("SmoothAim");
   }
}

