package dev.fede.gametest;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.RegionMapModule;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

public class RegionMapGameTest implements FabricClientGameTest {
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
            require(m.regionMap != null, "RegionMap registered");
            m.regionMap.setEnabled(true);
         });
         server.runCommand("gamemode spectator @a");
         server.runCommand("tp @a 0 80 0");
         context.waitTicks(5);
         context.runOnClient(mc -> {
            RegionMapModule rm = FeClient.modules().regionMap;
            require(rm.hasData(), "RegionMap has data");
            require(rm.currentRegionId() >= 0, "Player is on the map at spawn");
         });
         context.takeScreenshot("region-map-spawn");
         server.runCommand("tp @a -100000 80 -100000");
         context.waitTicks(5);
         context.runOnClient(mc -> require(FeClient.modules().regionMap.currentRegionId() >= 0, "Player is on the map after moving one region"));
         context.takeScreenshot("region-map-other-region");
         server.runCommand("tp @a 400000 80 400000");
         context.waitTicks(5);
         context.runOnClient(mc -> require(FeClient.modules().regionMap.currentRegionId() < 0, "Player is off the map when far away"));
         context.takeScreenshot("region-map-offmap");
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

   private static void require(boolean condition, String message) {
      if (!condition) {
         throw new AssertionError(message);
      }
   }
}



