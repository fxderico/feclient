package dev.fede.nyx.module.modules.combat;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

public class AutoLogModule extends Module {
   private static final float floatVal = 18.0F;
   private static final int intVal = 6;
   private final NumberSetting minHealth = new NumberSetting("MinHealth", 6.0, 1.0, 20.0, 0.5);
   private final NumberSetting dangerRange = new NumberSetting("DangerRange", 4.0, 1.0, 32.0, 0.5);
   private final NumberSetting minInventoryItems = new NumberSetting("MinInventoryItems", 5.0, 0.0, 36.0, 1.0);
   private final BooleanSetting deathImminent = new BooleanSetting("DeathImminent", true);
   private final NumberSetting cooldownSeconds = new NumberSetting("CooldownSeconds", 30.0, 1.0, 600.0, 1.0);
   private final ModeSetting action = new ModeSetting("Action", "Disconnect", "Disconnect", "MainMenu", "ExitToDesktop");
   private long longVal = Long.MIN_VALUE;

   public AutoLogModule() {
      super("AutoLog", "Auto-disconnects when HP, enemies, inventory loss or a fatal fall demand it", Category.COMBAT);
      this.run6(new Setting[]{this.minHealth, this.dangerRange, this.minInventoryItems, this.deathImminent, this.cooldownSeconds, this.action});
   }

   @Override
   public void run() {
      this.longVal = Long.MIN_VALUE;
   }

   @Override
   public void run2() {
      if (class310.player != null && class310.world != null) {
         if (!class310.player.isDead() && !class310.player.isSpectator()) {
            long var1 = System.currentTimeMillis();
            long var3 = (long)(this.cooldownSeconds.getValue() * 1000.0);
            if (var1 - this.longVal >= var3) {
               String var5 = this.getString();
               if (var5 != null) {
                  this.longVal = var1;
                  this.run3(var5);
               }
            }
         }
      }
   }

   public String getString() {
      if (class310.player == null) return null;
      float var1 = class310.player.getHealth() + class310.player.getAbsorptionAmount();
      if (var1 < this.minHealth.getValue()) {
         return "HP " + stringOf2(var1) + " < " + stringOf2((float)this.minHealth.getValue());
      } else {
         int var2 = this.intOf(class310.player.getInventory());
         if (var2 < this.minInventoryItems.getValueInt()) {
            return "Inventory " + var2 + " stacks < " + this.minInventoryItems.getValueInt();
         } else {
            PlayerEntity var3 = this.getclass1657();
            if (var3 != null) {
               double var6 = Math.sqrt(var3.getEntityPos().squaredDistanceTo(class310.player.getEntityPos()));
               return "Enemy " + stringOf(var3) + " at " + stringOf2((float)var6) + "m";
            } else {
               if (this.deathImminent.getValue()) {
                  String var4 = this.getString2();
                  if (var4 != null) {
                     return var4;
                  }
               }

               return null;
            }
         }
      }
   }

   private int intOf(PlayerInventory var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = var1.getStack(var3);
         if (!var4.isEmpty()) {
            var2++;
         }
      }

      return var2;
   }

   private PlayerEntity getclass1657() {
      if (class310.world != null && class310.player != null) {
         double var1 = this.dangerRange.getValue();
         double var3 = var1 * var1;
         UUID var5 = class310.player.getUuid();
         PlayerEntity var6 = null;
         double var7 = Double.MAX_VALUE;

         for (PlayerEntity var10 : class310.world.getPlayers()) {
            if (var10 != null && var10 != class310.player && !var10.getUuid().equals(var5) && var10.isAlive() && !var10.isSpectator()) {
               if (class310.getNetworkHandler() != null) {
                  PlayerListEntry var11 = class310.getNetworkHandler().getPlayerListEntry(var10.getUuid());
                  if (var11 == null) {
                     continue;
                  }

                  GameMode var12 = var11.getGameMode();
                  if (var12 == GameMode.CREATIVE || var12 == GameMode.SPECTATOR) {
                     continue;
                  }
               }

               double var13 = var10.getEntityPos().squaredDistanceTo(class310.player.getEntityPos());
               if (var13 <= var3 && var13 < var7) {
                  var7 = var13;
                  var6 = var10;
               }
            }
         }

         return var6;
      } else {
         return null;
      }
   }

   public String getString2() {
      Vec3d var1 = class310.player.getVelocity();
      int var2 = class310.world.getBottomY();
      if (class310.player.getY() < var2 - 6 && var1.y < 0.0) {
         return "Void fall (Y=" + stringOf2((float)class310.player.getY()) + ")";
      } else {
         boolean var3 = class310.player.isGliding();
         boolean var4 = class310.player.hasStatusEffect(StatusEffects.SLOW_FALLING);
         boolean var5 = var1.y < -0.3;
         return !var3 && !var4 && var5 && class310.player.fallDistance > 18.0 && !class310.player.isOnGround()
            ? "Fatal fall (" + stringOf2((float)class310.player.fallDistance) + " blocks)"
            : null;
      }
   }

   private void run3(String var1) {
      NotificationUtils.run8("AutoLog", "Disconnect: null", INFO.UNKNOWN_4);
      MutableText var2 = Text.literal("[c] AutoLog: null");
      String var3 = this.action.getMode();

      try {
         if (class310.world != null) {
            class310.world.disconnect(var2);
         }
      } catch (Throwable var7) {
      }

      try {
         switch (var3.hashCode()) {
            case 55996120:
               if (var3.equals("MainMenu")) {
                  class310.setScreen(new TitleScreen());
                  return;
               }
               break;
            case 415883651:
               if (var3.equals("ExitToDesktop")) {
                  class310.setScreen(new TitleScreen());
                  class310.scheduleStop();
                  return;
               }
         }

         class310.setScreen(new MultiplayerScreen(new TitleScreen()));
      } catch (Throwable var8) {
         try {
            class310.setScreen(new TitleScreen());
         } catch (Throwable var6) {
         }
      }
   }

   private static String stringOf(PlayerEntity var0) {
      String var1 = var0.getGameProfile() != null ? var0.getGameProfile().name() : null;
      return var1 != null && !var1.isEmpty() ? var1 : var0.getName().getString();
   }

   private static String stringOf2(float var0) {
      return String.format(Locale.ROOT, "%.1f", var0);
   }

   @Override
   public String getString3() {
      long var1 = (long)(this.cooldownSeconds.getValue() * 1000.0);
      long var3 = var1 - (System.currentTimeMillis() - this.longVal);
      return var3 > 0L && this.longVal != Long.MIN_VALUE ? "§7" + var3 / 1000L + "s" : null;
   }
}

