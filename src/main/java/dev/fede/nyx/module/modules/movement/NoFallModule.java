package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.AntiVoidModuleHelper;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.math.Vec3d;

public class NoFallModule extends Module {
   public static volatile boolean bool = false;
   private final ModeSetting mode = new ModeSetting("Mode", "Packet", "OnGroundSpoof", "Packet", "Rewind");
   private final NumberSetting minFallDistance = new NumberSetting("MinFallDistance", 3.0, 0.5, 20.0, 0.5);
   private final BooleanSetting onlyWhenSneaking = new BooleanSetting("OnlyWhenSneaking", false);
   private final AntiVoidModuleHelper antiVoidModuleHelper = new AntiVoidModuleHelper();
   private double doubleVal;
   private boolean bool2;

   public NoFallModule() {
      super("NoFall", "Prevents fall damage", Category.MOVEMENT);
      this.run6(new Setting[]{this.mode, this.minFallDistance, this.onlyWhenSneaking});
   }

   @Override
   public void run() {
      bool = false;
      this.bool2 = false;
      this.antiVoidModuleHelper.run();
   }

   @Override
   public void run2() {
      bool = false;
      this.bool2 = false;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         if (class310.player.isOnGround() && !class310.player.isTouchingWater()) {
            this.doubleVal = class310.player.getY();
            this.bool2 = true;
         }

         if (class310.player.isOnGround()) {
            bool = false;
         } else if (class310.player.isSpectator() || class310.player.getAbilities().flying || class310.player.hasVehicle()) {
            bool = false;
         } else if (this.onlyWhenSneaking.getValue() && !class310.player.isSneaking()) {
            bool = false;
         } else {
            Vec3d var1 = class310.player.getVelocity();
            if (var1.y >= 0.0) {
               bool = false;
            } else {
               double var2 = class310.player.fallDistance;
               if (var2 < this.minFallDistance.getValue()) {
                  bool = false;
               } else {
                  String var4 = this.mode.getMode();
                  switch (var4.hashCode()) {
                     case -1911998296:
                        if (var4.equals("Packet")) {
                           bool = false;
                           class310.player.networkHandler.sendPacket(new OnGroundOnly(true, false));
                           return;
                        }
                        break;
                     case -1850451749:
                        if (var4.equals("Rewind")) {
                           bool = false;
                           if (!this.bool2) {
                              return;
                           }

                           if (this.antiVoidModuleHelper.check(200L)) {
                              this.antiVoidModuleHelper.run();
                              class310.player
                                 .networkHandler
                                 .sendPacket(new PositionAndOnGround(class310.player.getX(), this.doubleVal, class310.player.getZ(), true, false));
                           }

                           return;
                        }
                        break;
                     case 718533187:
                        if (var4.equals("OnGroundSpoof")) {
                           bool = true;
                           return;
                        }
                  }

                  bool = false;
               }
            }
         }
      } else {
         bool = false;
      }
   }

   @Override
   public String getString3() {
      String var1 = this.mode.getMode();
      switch (var1.hashCode()) {
         case -1911998296:
            if (var1.equals("Packet")) {
               return "§7Packet";
            }
            break;
         case -1850451749:
            if (var1.equals("Rewind")) {
               return "§7Rewind";
            }
            break;
         case 718533187:
            if (var1.equals("OnGroundSpoof")) {
               return "§7Spoof";
            }
      }

      return null;
   }
}

