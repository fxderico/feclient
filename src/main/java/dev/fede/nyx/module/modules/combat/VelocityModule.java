package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class VelocityModule extends Module {
   public static volatile boolean bool;
   public static volatile double doubleVal = 1.0;
   public static volatile double doubleVal2 = 1.0;
   public static volatile boolean bool2;
   public static volatile boolean bool3;
   private final ModeSetting mode = new ModeSetting("Mode", "Reduce", "Reduce", "Cancel", "Reverse", "WTap", "GrimBypass", "GrimSlim");
   private final NumberSetting horizontalPct = new NumberSetting("HorizontalPercent", 0.0, 0.0, 100.0, 1.0);
   private final NumberSetting verticalPct = new NumberSetting("VerticalPercent", 100.0, 0.0, 100.0, 1.0);
   private final BooleanSetting onlyPlayersSetting = new BooleanSetting("OnlyFromPlayers", false);
   private int intVal;
   private int intVal2;
   private boolean bool4;

   private static void run(boolean var0, double var1, double var3, boolean var5, boolean var6) {
      bool = var0;
      doubleVal = var1;
      doubleVal2 = var3;
      bool2 = var5;
      bool3 = var6;
   }

   public VelocityModule() {
      super("Velocity", "Reduce or negate incoming knockback", Category.COMBAT);
      this.run6(new Setting[]{this.mode, this.horizontalPct, this.verticalPct, this.onlyPlayersSetting});
      this.horizontalPct.visibleWhen(this::getBoolean2);
      this.verticalPct.visibleWhen(this::getBoolean);
   }

   @Override
   public void run2() {
      this.run6();
   }

   @Override
   public void run3() {
      AntiKnockbackModule var1 = this.geta();
      if (var1 != null && var1.isEnabled3()) {
         var1.run5();
      } else {
         run(false, 1.0, 1.0, false, false);
      }

      if (this.intVal > 0 && this.bool4 && class310.options != null) {
         class310.options.forwardKey.setPressed(true);
      }

      this.bool4 = false;
      this.intVal = 0;
      this.intVal2 = 0;
   }

   public void run4() {
      this.run6();
      if (class310.player != null && class310.player.networkHandler != null) {
         if (this.intVal > 0) {
            ClientPlayNetworkHandler var1 = class310.player.networkHandler;
            KeyBinding var2 = class310.options.forwardKey;
            if (this.intVal == 2) {
               this.bool4 = var2.isPressed();
               if (this.bool4) {
                  var2.setPressed(false);
               }

               var1.sendPacket(new ClientCommandC2SPacket(class310.player, Mode.STOP_SPRINTING));
            } else if (this.intVal == 1) {
               if (this.bool4) {
                  var2.setPressed(true);
               }

               var1.sendPacket(new ClientCommandC2SPacket(class310.player, Mode.START_SPRINTING));
            }

            this.intVal--;
         }

         if (this.intVal2 > 0) {
            this.intVal2--;
            if (this.intVal2 == 0) {
               class310.player.addVelocity(0.0, 0.05, 0.0);
               class310.player.velocityDirty = true;
            }
         }
      }
   }

   public void run5() {
      if (this.mode.check("WTap")) {
         this.intVal = 2;
      } else if (this.mode.check("GrimBypass")) {
         this.intVal2 = 2;
      }
   }

   public void run6() {
      double var1;
      double var3;
      boolean var5;
      label46: {
         var5 = false;
         String var6 = this.mode.getMode();
         switch (var6.hashCode()) {
            case -1851006586:
               if (var6.equals("Reduce")) {
                  var1 = this.horizontalPct.getValue() / 100.0;
                  var3 = this.verticalPct.getValue() / 100.0;
                  break label46;
               }
               break;
            case -1530467646:
               if (var6.equals("Reverse")) {
                  var1 = this.horizontalPct.getValue() / 100.0;
                  var3 = this.verticalPct.getValue() / 100.0;
                  var5 = true;
                  break label46;
               }
               break;
            case 2675660:
               if (var6.equals("WTap")) {
                  var1 = 1.0;
                  var3 = 1.0;
                  break label46;
               }
               break;
            case 390991180:
               if (var6.equals("GrimSlim")) {
                  var1 = 0.4;
                  var3 = 1.0;
                  break label46;
               }
               break;
            case 1605880119:
               if (var6.equals("GrimBypass")) {
                  var1 = 0.0;
                  var3 = 0.0;
                  break label46;
               }
               break;
            case 2011110042:
               if (var6.equals("Cancel")) {
                  var1 = 0.0;
                  var3 = 0.0;
                  break label46;
               }
         }

         var1 = 1.0;
         var3 = 1.0;
      }

      AntiKnockbackModule var8 = this.geta();
      if (var8 != null && var8.isEnabled3()) {
         if (var8.isEnabled()) {
            var1 = Math.min(var1, 0.0);
         }

         if (var8.isEnabled2()) {
            var3 = Math.min(var3, 0.0);
         }
      }

      run(true, var1, var3, var5, this.onlyPlayersSetting.getValue());
   }

   private AntiKnockbackModule geta() {
      if (NyxClient.MODULES == null) {
         return null;
      } else {
         return NyxClient.MODULES.moduleOf("AntiKnockback") instanceof AntiKnockbackModule var2 ? var2 : null;
      }
   }

   @Override
   public String getString3() {
      return this.mode.getMode();
   }

   private Boolean getBoolean() {
      return !this.mode.check("Cancel");
   }

   private Boolean getBoolean2() {
      return !this.mode.check("Cancel");
   }
}

