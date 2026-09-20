package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.InventoryHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;

public class AutoTotemModule extends Module {
   public final SliderSetting delay = this.addSetting(new SliderSetting("Delay", "", 4.0, 1.0, 40.0, 1.0));
   public final BooleanSetting forceTotem = this.addSetting(new BooleanSetting("Force Totem", "", false));
   public final BooleanSetting hotbarTotem = this.addSetting(new BooleanSetting("Hotbar Totem", "", false));
   public final SliderSetting hotbarSlot = this.addSetting(new SliderSetting("Hotbar Slot", "", 1.0, 1.0, 9.0, 1.0));
   public final SliderSetting hotbarDelay = this.addSetting(new SliderSetting("Hotbar Delay", "", 4.0, 1.0, 40.0, 1.0));
   private int tickCounter;

   public AutoTotemModule() {
      super("Auto Totem", "Totem to offhand/hotbar — direct container-click swap, no hotbar staging or F-key packet", Category.COMBAT);
   }

   @Override
   protected void onEnable() {
      this.tickCounter = 0;
   }

   @Override
   protected void onDisable() {
      this.tickCounter = 0;
   }

   @Override
   public void onTick() {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player != null && client.interactionManager != null) {
         if (canRun(client)) {
            this.tickCounter++;
            int wait = this.hotbarTotem.get() && this.needsHotbarWork(client) && !this.needsOffhandWork(client)
               ? this.hotbarDelay.getInt()
               : this.delay.getInt();
            if (this.tickCounter >= wait) {
               this.tickCounter = 0;
               ClientPlayerEntity player = client.player;
               int configuredHotbar = this.hotbarSlot.getInt() - 1;
               if (this.hotbarTotem.get() && this.needsHotbarWork(client)) {
                  if (player.getInventory().getStack(configuredHotbar).isOf(Items.TOTEM_OF_UNDYING)) {
                     InventoryHelper.selectHotbarSlot(configuredHotbar);
                  } else {
                     int source = findTotemInMainInventory(client);
                     if (source != -1 && canContainerClick(client)) {
                        InventoryHelper.swapInventoryToHotbar(source, configuredHotbar);
                     }
                  }
               } else if (this.needsOffhandWork(client)) {
                  // needsOffhandWork() is already exactly "!hasOffhandTotem ||
                  // forceTotem" -- the old code re-checked that same condition
                  // a second time right here, which was always true given it
                  // was already inside the needsOffhandWork-true branch.
                  // was: select hotbar slot -> visible swing -> F packet, staged
                  // across ticks via pendingHotbarSlot if the totem had to be
                  // routed through the hotbar first. a single container click
                  // (SWAP against the offhand screen slot) does the whole move
                  // in one packet regardless of where the totem currently sits,
                  // so nothing is ever staged in the hotbar or visibly selected.
                  int totemSlot = findTotemInHotbar(client);
                  if (totemSlot == -1) {
                     totemSlot = findTotemInMainInventory(client);
                  }

                  if (totemSlot != -1 && canContainerClick(client)) {
                     InventoryHelper.swapToOffhand(totemSlot);
                  }
               }
            }
         }
      }
   }

   private static boolean canRun(MinecraftClient client) {
      // was gated to "no screen, or your own inventory screen" -- that
      // silently blocked every tick while chat, the pause menu, or our
      // own ClickGui was open, none of which have any reason to. the
      // actual hazard isn't "a screen is open", it's "some OTHER container
      // is open" (a chest/furnace/anvil/etc.) -- when that's the case,
      // player.currentScreenHandler points at THAT container, not the
      // player's own 36-slot inventory, and our slot math (built for
      // PlayerScreenHandler's layout) would click garbage slots in it.
      // ChatScreen, ClickGui, and the pause menu never touch
      // currentScreenHandler at all, so they're safe to run through.
      return client.currentScreen == null
         || client.currentScreen instanceof InventoryScreen
         || !(client.currentScreen instanceof HandledScreen);
   }

   private static boolean canContainerClick(MinecraftClient client) {
      return client.currentScreen != null || !isMoving(client);
   }

   private static boolean isMoving(MinecraftClient client) {
      if (client.player == null) {
         return false;
      } else if (!client.options.forwardKey.isPressed()
         && !client.options.backKey.isPressed()
         && !client.options.leftKey.isPressed()
         && !client.options.rightKey.isPressed()
         && !client.options.jumpKey.isPressed()
         && !client.player.isSprinting()
         && !client.player.isSneaking()) {
         double vx = client.player.getVelocity().x;
         double vz = client.player.getVelocity().z;
         return vx * vx + vz * vz > 0.0025;
      } else {
         return true;
      }
   }

   private boolean needsOffhandWork(MinecraftClient client) {
      boolean hasTotemOffhand = client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
      return this.forceTotem.get() || !hasTotemOffhand;
   }

   private boolean needsHotbarWork(MinecraftClient client) {
      if (!this.hotbarTotem.get()) {
         return false;
      } else {
         int slot = this.hotbarSlot.getInt() - 1;
         return !client.player.getInventory().getStack(slot).isOf(Items.TOTEM_OF_UNDYING);
      }
   }

   private static int findTotemInMainInventory(MinecraftClient client) {
      for (int i = 9; i < 36; i++) {
         if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
            return i;
         }
      }

      return -1;
   }

   private static int findTotemInHotbar(MinecraftClient client) {
      for (int i = 0; i < 9; i++) {
         if (client.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
            return i;
         }
      }

      return -1;
   }
}

