package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.auth.AuthGate;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BindSetting;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiDebugUtil;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.InputUtil;
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
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

public class TriggerBot extends Module {
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting passive = new BooleanSetting("Passive", false);
   private final BooleanSetting hostile = new BooleanSetting("Hostile", true);
   private final BooleanSetting neutral = new BooleanSetting("Neutral", false);
   private final BooleanSetting invisible = new BooleanSetting("Invisible", false);
   private final BooleanSetting bots = new BooleanSetting("Bots", false);
   private final BindSetting holdToActivate = new BindSetting("HoldToActivate", -1);
   private final NumberSetting delayMs = new NumberSetting("DelayMs", 90.0, 30.0, 500.0, 5.0);
   private final BooleanSetting randomizeDelay = new BooleanSetting("RandomizeDelay", true);
   private final BooleanSetting requireWeapon = new BooleanSetting("RequireWeapon", false);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private double doubleVal;

   public TriggerBot() {
      super(
         "TriggerBot",
         AntiDebugUtil.stringOf(
            new byte[]{
               101,
               -8,
               54,
               60,
               -67,
               -6,
               -7,
               -58,
               -46,
               34,
               -23,
               -21,
               -33,
               -87,
               3,
               -55,
               116,
               -119,
               -102,
               38,
               -32,
               -102,
               76,
               -16,
               -97,
               -58,
               34,
               -38,
               -79,
               67,
               54,
               -2,
               -9,
               77,
               -51,
               93,
               -48,
               8,
               -50,
               -7,
               125,
               73,
               -109,
               -49,
               40,
               -43,
               -89,
               -11,
               -45,
               -72,
               38
            }
         ),
         Category.COMBAT
      );
      this.run6(
         new Setting[]{
            this.players,
            this.passive,
            this.hostile,
            this.neutral,
            this.invisible,
            this.bots,
            this.holdToActivate,
            this.delayMs,
            this.randomizeDelay,
            this.requireWeapon
         }
      );
      this.doubleVal = this.delayMs.getValue();
   }

   @Override
   public void run2() {
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null) {
         if (this.holdToActivate.isUnbound() || InputUtil.isKeyPressed(class310.getWindow(), this.holdToActivate.getValue())) {
            if (!this.requireWeapon.getValue() || this.isEnabled()) {
               if (this.antiVoidModuleHelper.check2(this.doubleVal)) {
                  if (class310.crosshairTarget instanceof EntityHitResult var2) {
                     Entity var3 = var2.getEntity();
                     if (this.check(var3)) {
                        if ((
                                 AuthGate.getLong()
                                    ^ (5113420271624924431L ^ -3095584121263796770L)
                                       + (
                                          (
                                                -7412342326184821277L * -8741070547669894171L
                                                   & (
                                                      -6042350651330015956L + -4036398063974510413L - ((-6042350651330015956L & -4036398063974510413L) << 1)
                                                         ^ -4695897731638685631L
                                                   )
                                             )
                                             << 1
                                       )
                                       + 5650609583430421984L
                                       + 2570234721076235716L
                                       + -7958111422913451712L
                                       + -434848570016941125L
                                       + (-6660168729357107435L | 7025100608012217559L)
                                       + (-6660168729357107435L & -2925061517054278057L + -2268317601234504342L + -6228264347408551658L)
                                       + 36148321672466960L
                              )
                              != (-143055883522112274L ^ -8495384596142188177L)
                                 + (
                                    (
                                          -143055883522112274L
                                             & (-7211249078153896547L ^ 1249425440465552972L ^ -8963527903765693569L | -5195314124902179265L)
                                                + (
                                                   (
                                                         -8762688091074886918L + -2828717873051657394L - ((-8762688091074886918L & -2828717873051657394L) << 1)
                                                            ^ 6339746291637811482L
                                                      )
                                                      & -5195314124902179265L
                                                )
                                                + (1984028014947825997L - 5942046411017756363L)
                                       )
                                       << 1
                                 )
                                 + -2822444624294506874L
                           && ThreadLocalRandom.current().nextInt(100) < 30) {
                           this.doubleVal = this.getDouble();
                           this.antiVoidModuleHelper.run();
                        } else {
                           class310.interactionManager.attackEntity(class310.player, var3);
                           class310.player.swingHand(Hand.MAIN_HAND);
                           this.doubleVal = this.getDouble();
                           this.antiVoidModuleHelper.run();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private double getDouble() {
      double var1 = this.delayMs.getValue();
      if (!this.randomizeDelay.getValue()) {
         return var1;
      } else {
         double var3 = var1 * 0.25;
         return var1 + ThreadLocalRandom.current().nextDouble(-var3, var3);
      }
   }

   private boolean check(Entity var1) {
      if (var1 instanceof LivingEntity var2) {
         if (var1 == class310.player) {
            return false;
         } else if (!var1.isAlive() || var2.getHealth() <= 0.0F) {
            return false;
         } else if (!this.invisible.getValue() && var1.isInvisible()) {
            return false;
         } else if (var2 instanceof PlayerEntity var3) {
            return !this.players.getValue() ? false : this.bots.getValue() || !this.check2(var3);
         } else if (var2 instanceof HostileEntity || var2 instanceof Monster) {
            return this.hostile.getValue();
         } else if (this.check3(var2)) {
            return this.neutral.getValue();
         } else {
            return var2 instanceof PassiveEntity ? this.passive.getValue() : false;
         }
      } else {
         return false;
      }
   }

   private boolean check3(LivingEntity var1) {
      for (Class var2 = var1.getClass(); var2 != null && var2 != Object.class; var2 = var2.getSuperclass()) {
         for (Class var6 : var2.getInterfaces()) {
            if ("Angerable".equals(var6.getSimpleName())) {
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

   public boolean isEnabled() {
      ItemStack var1 = class310.player.getMainHandStack();
      return var1 != null && !var1.isEmpty() ? var1.isIn(ItemTags.SWORDS) || var1.getItem() instanceof AxeItem || var1.getItem() instanceof TridentItem : false;
   }
}

