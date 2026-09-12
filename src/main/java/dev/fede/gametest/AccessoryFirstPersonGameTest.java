package dev.fede.gametest;

import dev.fede.FeClient;
import dev.fede.module.impl.CustomAccessoriesModule;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.option.Perspective;

public class AccessoryFirstPersonGameTest implements FabricClientGameTest {
   public void runTest(ClientGameTestContext context) {
      TestSingleplayerContext world = context.worldBuilder().create();

      try {
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> mc.options.pauseOnLostFocus = false);
         context.getInput().resizeWindow(1600, 900);
         context.waitTicks(2);
         world.getServer().runCommand("tp @a 500 -60 500 -90 0");
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> {
            FeClient.modules().susChunkFinder.setEnabled(false);
            FeClient.modules().blockOutline.setEnabled(false);
            FeClient.modules().motionBlur.setEnabled(false);
            if (mc.player != null) {
               mc.player.setYaw(-90.0F);
               mc.player.setBodyYaw(-90.0F);
               mc.player.setPitch(0.0F);
            }

            CustomAccessoriesModule acc = FeClient.modules().customAccessories;
            require(acc != null, "CustomAccessories registered");
            acc.firstPerson.set(false);
            acc.color.set(-49508);
            acc.rainbow.set(false);
            acc.glow.set(75.0);
            acc.cape.set(true);
            acc.capeStyle.set("FE Logo");
            acc.capePhysics.set(true);
            acc.trail.set(false);
            acc.aura.set(true);
            acc.auraStyle.set("Orbit");
            acc.crown.set(true);
            acc.setEnabled(true);
         });
         context.waitTicks(20);
         context.runOnClient(mc -> mc.options.setPerspective(Perspective.THIRD_PERSON_BACK));
         context.waitTicks(6);
         context.takeScreenshot("accessories-fp-1-thirdperson-control");
         context.runOnClient(mc -> mc.options.setPerspective(Perspective.FIRST_PERSON));
         context.waitTicks(6);
         context.takeScreenshot("accessories-fp-2-firstperson-hidden");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setPitch(60.0F);
            }
         });
         context.waitTicks(6);
         context.takeScreenshot("accessories-fp-3-firstperson-feet-aura-toggleoff");
         context.runOnClient(mc -> FeClient.modules().customAccessories.firstPerson.set(true));
         context.waitTicks(6);
         context.takeScreenshot("accessories-fp-4-firstperson-feet-toggleon");
         context.runOnClient(mc -> {
            CustomAccessoriesModule acc = FeClient.modules().customAccessories;
            acc.firstPerson.set(false);
            acc.setEnabled(false);
         });
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

   private static void require(boolean condition, String what) {
      if (!condition) {
         throw new AssertionError("FAILED: null");
      }
   }
}



