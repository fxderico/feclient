package dev.fede.gametest;

import dev.fede.FeClient;
import dev.fede.gui.ClickGuiScreen;
import dev.fede.module.impl.GambleRiggerModule;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class GambleRiggerGameTest implements FabricClientGameTest {
   private static final BlockPos DISPENSER = new BlockPos(2, -59, 4);
   private static final Item[] ITEM = new Item[]{
      Items.DIAMOND, Items.EMERALD, Items.GOLD_INGOT, Items.IRON_INGOT, Items.REDSTONE, Items.LAPIS_LAZULI, Items.COAL, Items.QUARTZ, Items.GLOWSTONE_DUST
   };
   private static final String[] ID = new String[]{
      "minecraft:diamond",
      "minecraft:emerald",
      "minecraft:gold_ingot",
      "minecraft:iron_ingot",
      "minecraft:redstone",
      "minecraft:lapis_lazuli",
      "minecraft:coal",
      "minecraft:quartz",
      "minecraft:glowstone_dust"
   };
   private static final int[] COUNT = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

   public void runTest(ClientGameTestContext context) {
      TestSingleplayerContext world = context.worldBuilder().create();

      try {
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> mc.options.pauseOnLostFocus = false);
         context.getInput().resizeWindow(1600, 900);
         context.waitTicks(2);
         TestServerContext server = world.getServer();
         server.runCommand("gamemode survival @a");
         server.runCommand("clear @a");
         server.runCommand("setblock 2 -59 4 minecraft:dispenser");

         for (int i = 0; i < 9; i++) {
            server.runCommand("item replace block 2 -59 4 container." + i + " with " + ID[i] + " " + COUNT[i]);
         }

         server.runCommand("tp @a 2 -58 2 0 0");
         context.waitTicks(20);
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> {
            GambleRiggerModule mod = FeClient.modules().gambleRigger;
            require(mod != null, "GambleRigger registered");
            require(!mod.isEnabled(), "starts disabled");
            mod.clickDelay.set(0.0);
            mod.chatFeedback.set(false);
            mod.keepStays.set(true);
            mod.setEnabled(true);
         });
         openDispenser(context);
         context.waitTicks(3);
         context.runOnClient(mc -> {
            ScreenHandler menu = mc.player.currentScreenHandler;
            require(menu instanceof Generic3x3ContainerScreenHandler, "dispenser menu open, got " + menu.getClass().getSimpleName());

            for (int ix = 0; ix < 9; ix++) {
               ItemStack s = menu.getSlot(ix).getStack();
               require(s.isOf(ITEM[ix]) && s.getCount() == COUNT[ix], "grid slot " + ix + " should be " + ITEM[ix] + " x" + COUNT[ix] + ", got " + describe(s));
            }
         });
         context.takeScreenshot("gamblerigger-01-panel");
         context.runOnClient(mc -> FeClient.modules().gambleRigger.requestKeep(5));
         context.waitTicks(40);
         context.runOnClient(
            mc -> {
               GambleRiggerModule mod = FeClient.modules().gambleRigger;
               require(mod.phase() == GambleRiggerModule.Phase.EXTRACTED, "after taking, phase should be EXTRACTED, got " + mod.phase());
               ScreenHandler menu = mc.player.currentScreenHandler;
               require(
                  menu.getSlot(5).getStack().isOf(ITEM[5]) && menu.getSlot(5).getStack().getCount() == COUNT[5],
                  "kept slot #6 must stay in place, got " + describe(menu.getSlot(5).getStack())
               );

               for (int ix = 0; ix < 9; ix++) {
                  if (ix != 5) {
                     require(
                        menu.getSlot(ix).getStack().isEmpty(),
                        "grid slot " + ix + " should be empty after taking, got " + describe(menu.getSlot(ix).getStack())
                     );
                     require(invHas(menu, ITEM[ix], COUNT[ix]), "inventory must now hold " + ITEM[ix] + " x" + COUNT[ix] + " pulled from slot " + ix);
                  }
               }
            }
         );
         context.takeScreenshot("gamblerigger-02-taken-keep6");
         context.runOnClient(mc -> FeClient.modules().gambleRigger.requestRestore());
         context.waitTicks(40);
         context.runOnClient(
            mc -> {
               GambleRiggerModule mod = FeClient.modules().gambleRigger;
               require(mod.phase() == GambleRiggerModule.Phase.IDLE, "after restore, phase should be IDLE, got " + mod.phase());
               ScreenHandler menu = mc.player.currentScreenHandler;

               for (int ix = 0; ix < 9; ix++) {
                  ItemStack s = menu.getSlot(ix).getStack();
                  require(
                     s.isOf(ITEM[ix]) && s.getCount() == COUNT[ix],
                     "restored grid slot " + ix + " should be " + ITEM[ix] + " x" + COUNT[ix] + ", got " + describe(s)
                  );
               }
            }
         );
         context.takeScreenshot("gamblerigger-03-restored");
         context.runOnClient(mc -> {
            GambleRiggerModule mod = FeClient.modules().gambleRigger;
            mod.keepStays.set(false);
            mod.requestKeep(2);
         });
         context.waitTicks(40);
         context.runOnClient(
            mc -> {
               ScreenHandler menu = mc.player.currentScreenHandler;

               for (int ix = 0; ix < 9; ix++) {
                  require(
                     menu.getSlot(ix).getStack().isEmpty(),
                     "keep-stays OFF: slot " + ix + " should be empty after taking, got " + describe(menu.getSlot(ix).getStack())
                  );
               }
            }
         );
         context.runOnClient(mc -> FeClient.modules().gambleRigger.requestRestore());
         context.waitTicks(40);
         context.runOnClient(
            mc -> {
               ScreenHandler menu = mc.player.currentScreenHandler;
               require(menu.getSlot(2).getStack().isEmpty(), "keep-stays OFF: picked slot #3 must be left empty, got " + describe(menu.getSlot(2).getStack()));
               require(invHas(menu, ITEM[2], COUNT[2]), "keep-stays OFF: you must still hold the picked stack " + ITEM[2] + " x" + COUNT[2]);

               for (int ix = 0; ix < 9; ix++) {
                  if (ix != 2) {
                     ItemStack s = menu.getSlot(ix).getStack();
                     require(
                        s.isOf(ITEM[ix]) && s.getCount() == COUNT[ix],
                        "keep-stays OFF: slot " + ix + " should be restored to " + ITEM[ix] + " x" + COUNT[ix] + ", got " + describe(s)
                     );
                  }
               }
            }
         );
         context.takeScreenshot("gamblerigger-04-keepoff-slot3-empty");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.closeHandledScreen();
            }

            FeClient.modules().gambleRigger.keepStays.set(true);
            FeClient.modules().gambleRigger.reset();
         });
         context.waitForScreen(null);
         context.runOnClient(mc -> ClickGuiScreen.state().setExpanded("GambleRigger@MISC", true));
         context.getInput().pressKey(344);
         context.waitForScreen(ClickGuiScreen.class);
         context.waitTicks(8);
         context.takeScreenshot("gamblerigger-05-settings");
         context.runOnClient(mc -> {
            FeClient.modules().gambleRigger.setEnabled(false);
            ClickGuiScreen.state().setExpanded("GambleRigger@MISC", false);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(null);
      } catch (Throwable var6) {
         if (world != null) {
            try {
               world.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (world != null) {
         world.close();
      }
   }

   private static void openDispenser(ClientGameTestContext context) {
      context.runOnClient(mc -> {
         if (mc.player != null && mc.interactionManager != null) {
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(DISPENSER), Direction.UP, DISPENSER, false));
         }
      });
      context.waitForScreen(Generic3x3ContainerScreen.class);
   }

   private static boolean invHas(ScreenHandler menu, Item item, int count) {
      for (int i = 9; i < 45; i++) {
         ItemStack s = menu.getSlot(i).getStack();
         if (s.isOf(item) && s.getCount() == count) {
            return true;
         }
      }

      return false;
   }

   private static String describe(ItemStack s) {
      return s.isEmpty() ? "empty" : s.getItem() + " x" + s.getCount();
   }

   private static void require(boolean condition, String what) {
      if (!condition) {
         throw new AssertionError("FAILED: null");
      }
   }
}



