package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.util.AntiAFKModuleUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Hand;

public class AntiAFKModule extends Module {
   private final ModeSetting mode = new ModeSetting("Mode", "MicroJump", "MicroJump", "MicroTurn", "Chat", "Combined");
   private final NumberSetting intervalTicks = new NumberSetting("IntervalTicks", 200.0, 20.0, 6000.0, 20.0);
   private final NumberSetting randomTurnRange = new NumberSetting("RandomTurnRange", 10.0, 1.0, 90.0, 1.0);
   private final StringSetting chatMessages = new StringSetting("ChatMessages", "", 256);
   private final Random random = new Random();
   private int intVal;
   private int intVal2;
   private boolean bool;

   public AntiAFKModule() {
      super("AntiAFK", "Performs a small action periodically to avoid AFK kicks", Category.PLAYER);
      this.run6(new Setting[]{this.mode, this.intervalTicks, this.randomTurnRange, this.chatMessages});
   }

   @Override
   public void run() {
      this.random.setSeed(System.nanoTime());
      this.intVal = 0;
      this.intVal2 = 0;
      this.bool = false;
   }

   @Override
   public void run2() {
      if (this.bool) {
         AntiAFKModuleUtil.run2();
         this.bool = false;
      }
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         if (this.bool) {
            AntiAFKModuleUtil.run2();
            this.bool = false;
         }

         this.intVal++;
         int var1 = Math.max(1, this.intervalTicks.getValueInt());
         if (this.intVal >= var1) {
            this.intVal = 0;
            String var2 = this.mode.getMode();
            switch (var2.hashCode()) {
               case -547811547:
                  if (var2.equals("Combined")) {
                     this.run7();
                     return;
                  }
                  break;
               case 2099064:
                  if (var2.equals("Chat")) {
                     this.run6();
                     return;
                  }
                  break;
               case 1908524274:
                  if (var2.equals("MicroJump")) {
                     this.run4();
                     return;
                  }
                  break;
               case 1908822337:
                  if (var2.equals("MicroTurn")) {
                     this.run5();
                     return;
                  }
            }

            this.run4();
         }
      }
   }

   private void run4() {
      if (class310.player.isOnGround()) {
         class310.player.jump();
      }

      class310.player.swingHand(Hand.MAIN_HAND);
   }

   private void run5() {
      double var1 = this.randomTurnRange.getValue();
      float var3 = (float)((this.random.nextDouble() * 2.0 - 1.0) * var1);
      float var4 = (float)((this.random.nextDouble() * 2.0 - 1.0) * var1 * 0.5);
      float var5 = class310.player.getYaw() + var3;
      float var6 = class310.player.getPitch() + var4;
      if (var6 < -89.0F) {
         var6 = -89.0F;
      }

      if (var6 > 89.0F) {
         var6 = 89.0F;
      }

      AntiAFKModuleUtil.run(var5, var6);
      this.bool = true;
   }

   private void run6() {
      if (class310.getNetworkHandler() != null) {
         List var1 = listOf(this.chatMessages.getValue());
         if (!var1.isEmpty()) {
            String var2 = ((String)var1.get(this.random.nextInt(var1.size()))).trim();
            if (!var2.isEmpty()) {
               if (var2.startsWith("/")) {
                  String var3 = var2.substring(1);
                  if (var3.isEmpty()) {
                     return;
                  }

                  class310.getNetworkHandler().sendChatCommand(var3);
               } else {
                  class310.getNetworkHandler().sendChatMessage(var2);
               }
            }
         }
      }
   }

   private void run7() {
      int var1 = this.intVal2;
      this.intVal2 = (this.intVal2 + 1) % 3;
      switch (var1) {
         case 0:
            this.run4();
            break;
         case 1:
            this.run5();
            break;
         case 2:
            this.run6();
      }
   }

   private static List<String> listOf(String var0) {
      ArrayList var1 = new ArrayList();
      if (var0 != null && !var0.isEmpty()) {
         for (String var5 : var0.split(",")) {
            String var6 = var5.trim();
            if (!var6.isEmpty()) {
               var1.add(var6);
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   @Override
   public String getString3() {
      return "§7" + this.mode.getMode();
   }
}

