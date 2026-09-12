package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.hit.EntityHitResult;

public class CriticalsModule extends Module {
   private static volatile CriticalsModule criticalsModule;
   private final ModeSetting mode = new ModeSetting("Mode", "PacketOnly", "MiniJump", "PacketOnly", "NoGround");
   private final BooleanSetting requireWeapon = new BooleanSetting("RequireWeapon", true);
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting mobs = new BooleanSetting("Mobs", true);
   private final BooleanSetting onlyKillAura = new BooleanSetting("OnlyKillAura", false);
   private boolean bool;
   private boolean bool2;
   private int intVal;

   public CriticalsModule() {
      super("Criticals", "Guarantees critical hits on every attack", Category.COMBAT);
      this.run6(new Setting[]{this.mode, this.requireWeapon, this.players, this.mobs, this.onlyKillAura});
      criticalsModule = this;
   }

   @Override
   public void run() {
      this.bool = false;
      this.intVal = 0;
   }

   @Override
   public void run2() {
      this.bool = false;
      this.intVal = 0;
   }

   @Override
   public void run3() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null && class310.world != null) {
         boolean var2 = this.bool2;
         this.bool2 = false;
         if (this.mode.check("NoGround") && this.intVal > 0) {
            ClientPlayNetworkHandler var3 = var1.networkHandler;
            if (var3 != null) {
               var3.sendPacket(new OnGroundOnly(false, var1.horizontalCollision));
            }

            this.intVal--;
            if (this.intVal == 0 && var3 != null) {
               var3.sendPacket(new OnGroundOnly(var1.isOnGround(), var1.horizontalCollision));
            }
         }

         if (this.onlyKillAura.getValue()) {
            this.bool = var1.handSwinging;
         } else {
            boolean var7 = var1.handSwinging;
            boolean var4 = var7 && !this.bool;
            this.bool = var7;
            if (var4) {
               if (!var2) {
                  if (this.isEnabled3()) {
                     if (!this.requireWeapon.getValue() || this.check2(var1)) {
                        this.run4(class310.crosshairTarget instanceof EntityHitResult var5 ? var5.getEntity() : null);
                     }
                  }
               }
            }
         }
      } else {
         this.bool2 = false;
      }
   }

   public void run4(Entity var1) {
      ClientPlayerEntity var2 = class310.player;
      if (var2 != null) {
         ClientPlayNetworkHandler var3 = var2.networkHandler;
         if (var3 != null) {
            if (var1 == null || this.check(var1)) {
               this.bool2 = true;
               String var4 = this.mode.getMode();
               switch (var4.hashCode()) {
                  case -2146289548:
                     if (var4.equals("PacketOnly")) {
                        var3.sendPacket(new OnGroundOnly(false, var2.horizontalCollision));
                        var3.sendPacket(new OnGroundOnly(var2.isOnGround(), var2.horizontalCollision));
                     }
                     break;
                  case -1295454907:
                     if (var4.equals("MiniJump") && var2.isOnGround() && !var2.isTouchingWater() && !var2.isClimbing() && !var2.hasVehicle()) {
                        var2.setVelocity(var2.getVelocity().x, 0.0625, var2.getVelocity().z);
                        var2.velocityDirty = true;
                     }
                     break;
                  case 370287304:
                     if (var4.equals("NoGround")) {
                        this.run9();
                     }
               }
            }
         }
      }
   }

   public static boolean isEnabled_s() {
      CriticalsModule var0 = criticalsModule;
      if (var0 == null || !var0.isEnabled3()) {
         return false;
      } else if (!var0.onlyKillAura.getValue()) {
         return false;
      } else {
         ClientPlayerEntity var1 = class310.player;
         if (var1 == null || var1.networkHandler == null) {
            return false;
         } else if (!var0.isEnabled3()) {
            return false;
         } else if (var0.requireWeapon.getValue() && !var0.check2(var1)) {
            return false;
         } else {
            return !var0.mode.check("MiniJump") ? var1.isOnGround() : var1.isOnGround() && !var1.isTouchingWater() && !var1.isClimbing() && !var1.hasVehicle();
         }
      }
   }

   public static void run5() {
      CriticalsModule var0 = criticalsModule;
      if (var0 != null) {
         ClientPlayerEntity var1 = class310.player;
         if (var1 != null && var1.networkHandler != null) {
            String var2 = var0.mode.getMode();
            switch (var2.hashCode()) {
               case -2146289548:
                  if (!var2.equals("PacketOnly")) {
                     return;
                  }
                  break;
               case -1295454907:
                  if (var2.equals("MiniJump")) {
                     var1.setVelocity(var1.getVelocity().x, 0.0625, var1.getVelocity().z);
                     var1.velocityDirty = true;
                  }

                  return;
               case 370287304:
                  if (!var2.equals("NoGround")) {
                     return;
                  }
                  break;
               default:
                  return;
            }

            var1.networkHandler.sendPacket(new OnGroundOnly(false, var1.horizontalCollision));
         }
      }
   }

   public static void run6() {
      CriticalsModule var0 = criticalsModule;
      if (var0 != null) {
         ClientPlayerEntity var1 = class310.player;
         if (var1 != null && var1.networkHandler != null) {
            if (var0.mode.check("PacketOnly")) {
               var1.networkHandler.sendPacket(new OnGroundOnly(var1.isOnGround(), var1.horizontalCollision));
            } else if (var0.mode.check("NoGround")) {
               var0.intVal = 1;
            }
         }
      }
   }

   public static boolean isEnabled2() {
      CriticalsModule var0 = criticalsModule;
      if (var0 == null || !var0.isEnabled3()) {
         return false;
      } else if (var0.onlyKillAura.getValue()) {
         return false;
      } else if (!var0.isEnabled3()) {
         return false;
      } else {
         ClientPlayerEntity var1 = class310.player;
         if (var1 == null || var1.networkHandler == null) {
            return false;
         } else if (var0.requireWeapon.getValue() && !var0.check2(var1)) {
            return false;
         } else {
            return !var0.mode.check("MiniJump") ? var1.isOnGround() : var1.isOnGround() && !var1.isTouchingWater() && !var1.isClimbing() && !var1.hasVehicle();
         }
      }
   }

   public void run7(Entity var1) {
      if (var1 == null || this.check(var1)) {
         ClientPlayerEntity var2 = class310.player;
         if (var2 != null && var2.networkHandler != null) {
            this.bool2 = true;
            String var3 = this.mode.getMode();
            switch (var3.hashCode()) {
               case -2146289548:
                  if (!var3.equals("PacketOnly")) {
                     return;
                  }
                  break;
               case -1295454907:
                  if (var3.equals("MiniJump")) {
                     var2.setVelocity(var2.getVelocity().x, 0.0625, var2.getVelocity().z);
                     var2.velocityDirty = true;
                  }

                  return;
               case 370287304:
                  if (!var3.equals("NoGround")) {
                     return;
                  }
                  break;
               default:
                  return;
            }

            var2.networkHandler.sendPacket(new OnGroundOnly(false, var2.horizontalCollision));
         }
      }
   }

   public void run8() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null && var1.networkHandler != null) {
         if (this.mode.check("PacketOnly")) {
            var1.networkHandler.sendPacket(new OnGroundOnly(var1.isOnGround(), var1.horizontalCollision));
         } else if (this.mode.check("NoGround")) {
            this.intVal = 1;
         }
      }
   }

   private void run9() {
      ClientPlayerEntity var1 = class310.player;
      if (var1 != null && var1.networkHandler != null) {
         var1.networkHandler.sendPacket(new OnGroundOnly(false, var1.horizontalCollision));
         this.intVal = 1;
      }
   }

   public boolean isEnabled3() {
      return this.players.getValue() || this.mobs.getValue();
   }

   private boolean check(Entity var1) {
      if (!(var1 instanceof LivingEntity)) {
         return false;
      } else if (var1 instanceof PlayerEntity) {
         return this.players.getValue();
      } else {
         return var1 instanceof Monster ? this.mobs.getValue() : this.mobs.getValue();
      }
   }

   private boolean check2(ClientPlayerEntity var1) {
      ItemStack var2 = var1.getMainHandStack();
      return var2 != null && !var2.isEmpty() ? var2.isIn(ItemTags.SWORDS) || var2.isIn(ItemTags.AXES) || var2.isIn(ItemTags.MELEE_WEAPON_ENCHANTABLE) : false;
   }
}

