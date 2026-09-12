package dev.fede.gametest;

import dev.fede.FeClient;
import dev.fede.gui.ClickGuiScreen;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.AutoTpaModule;
import dev.fede.module.impl.AutoWalkModule;
import dev.fede.module.impl.CoordSnapperModule;
import dev.fede.module.impl.FreeLookModule;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.option.Perspective;

public class PortedModulesGameTest implements FabricClientGameTest {
   public void runTest(ClientGameTestContext context) {
      TestSingleplayerContext world = context.worldBuilder().create();

      try {
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> mc.options.pauseOnLostFocus = false);
         context.getInput().resizeWindow(1600, 900);
         context.waitTicks(2);
         TestServerContext server = world.getServer();
         context.runOnClient(mc -> {
            ModuleManager m = FeClient.modules();
            require(m.freeLook != null, "FreeLook registered");
            require(m.autoWalk != null, "AutoWalk registered");
            require(m.autoClicker != null, "AutoClicker registered");
            require(m.coordSnapper != null, "CoordSnapper registered");
            require(m.autoTpa != null, "AutoTPA registered");
            require(m.chatMacro != null, "ChatMacro registered");
            require(m.regionMap != null, "RegionMap registered");
         });
         server.runCommand("fill -45 0 -45 45 0 45 minecraft:stone");
         server.runCommand("fill 6 0 6 22 0 22 minecraft:red_wool");
         server.runCommand("fill -22 0 6 -6 0 22 minecraft:blue_wool");
         server.runCommand("fill 6 0 -22 22 0 -6 minecraft:yellow_wool");
         server.runCommand("fill -22 0 -22 -6 0 -6 minecraft:water");
         server.runCommand("fill -4 0 -30 4 0 -24 minecraft:lime_wool");
         server.runCommand("fill -4 0 24 4 0 30 minecraft:white_wool");
         server.runCommand("fill 24 1 -4 30 3 4 minecraft:gold_block");
         server.runCommand("fill -30 1 -4 -24 3 4 minecraft:diamond_block");
         server.runCommand("tp @a 0 1 0");
         context.waitTicks(10);
         context.runOnClient(mc -> FeClient.modules().regionMap.setEnabled(true));
         context.waitTicks(50);
         context.runOnClient(mc -> require(FeClient.modules().regionMap.hasData(), "RegionMap produced scan data"));
         context.takeScreenshot("region-map-hud");
         context.runOnClient(mc -> {
            FeClient.modules().freeLook.setEnabled(true);
            require(mc.options.getPerspective() == Perspective.THIRD_PERSON_BACK, "FreeLook switched to third person");
         });
         context.waitTicks(3);
         context.takeScreenshot("freelook-thirdperson-regionmap");
         context.runOnClient(mc -> FeClient.modules().freeLook.setEnabled(false));
         server.runCommand("fill -2 1 -2 2 4 -2 minecraft:stone");
         server.runCommand("fill -2 1 2 2 4 2 minecraft:stone");
         server.runCommand("fill -2 1 -2 -2 4 2 minecraft:stone");
         server.runCommand("fill 2 1 -2 2 4 2 minecraft:stone");
         server.runCommand("tp @a 0 1 0");
         context.waitTicks(5);
         double[] wallOff = new double[1];
         context.runOnClient(mc -> {
            FreeLookModule fl = FeClient.modules().freeLook;
            fl.throughWalls.set(false);
            mc.player.setPitch(0.0F);
            fl.setEnabled(true);
         });
         context.waitTicks(5);
         context.runOnClient(mc -> wallOff[0] = mc.gameRenderer.getCamera().getCameraPos().distanceTo(mc.player.getCameraPosVec(1.0F)));
         context.takeScreenshot("freelook-walls-off");
         double[] wallOn = new double[1];
         context.runOnClient(mc -> FeClient.modules().freeLook.throughWalls.set(true));
         context.waitTicks(5);
         context.runOnClient(mc -> wallOn[0] = mc.gameRenderer.getCamera().getCameraPos().distanceTo(mc.player.getCameraPosVec(1.0F)));
         context.takeScreenshot("freelook-walls-through");
         context.runOnClient(mc -> {
            require(wallOff[0] < 2.5, "Through Walls off: camera clipped to the wall, got " + wallOff[0]);
            require(wallOn[0] > 3.0, "Through Walls on: camera saw through the wall, got " + wallOn[0]);
            require(wallOn[0] > wallOff[0] + 1.0, "Through Walls extended the camera distance");
            FreeLookModule fl = FeClient.modules().freeLook;
            fl.throughWalls.set(false);
            fl.setEnabled(false);
         });
         server.runCommand("fill -2 1 -2 2 4 2 minecraft:air");
         server.runCommand("tp @a 0 1 0");
         context.waitTicks(3);
         context.runOnClient(mc -> {
            AutoWalkModule aw = FeClient.modules().autoWalk;
            aw.mode.set("Simple");
            aw.direction.set("Forwards");
            aw.setEnabled(true);
            aw.onTick();
            require(mc.options.forwardKey.isPressed(), "AutoWalk presses forward");
            aw.setEnabled(false);
            require(!mc.options.forwardKey.isPressed(), "AutoWalk releases forward on disable");
         });
         context.runOnClient(mc -> {
            AutoTpaModule tpa = FeClient.modules().autoTpa;
            tpa.mode.set("TPAHere");
            tpa.target.set("Steve");
            tpa.delay.set(3000.0);
            tpa.humanize.set(0.0);
            tpa.setEnabled(true);
            tpa.onTick();
            require("tpahere Steve".equals(tpa.lastSent()), "AutoTPA sent the first request, got: '" + tpa.lastSent() + "'");
            tpa.target.set("Alex");
            tpa.onTick();
            require("tpahere Steve".equals(tpa.lastSent()), "AutoTPA paced by Delay (no double-send), got: '" + tpa.lastSent() + "'");
            tpa.setEnabled(false);
         });
         context.runOnClient(mc -> {
            CoordSnapperModule cs = FeClient.modules().coordSnapper;
            cs.target.set("Player");
            cs.format.set("X Y Z");
            cs.copyKey.set(80);
            cs.setEnabled(true);
            boolean consumed = cs.onKeyPress(80);
            require(consumed, "CoordSnapper consumes its copy key");
            require(cs.lastCopied().equals("0 1 0"), "CoordSnapper formatted coords, got: '" + cs.lastCopied() + "'");
            cs.setEnabled(false);
         });
         context.runOnClient(mc -> mc.setScreen(new ClickGuiScreen()));
         context.waitTicks(3);
         context.takeScreenshot("clickgui-open");
         context.runOnClient(mc -> mc.setScreen(null));
         context.waitTicks(2);
      } catch (Throwable var7) {
         if (world != null) {
            try {
               world.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (world != null) {
         world.close();
      }
   }

   private static void require(boolean condition, String what) {
      if (!condition) {
         throw new AssertionError("FAILED: null");
      }
   }
}



