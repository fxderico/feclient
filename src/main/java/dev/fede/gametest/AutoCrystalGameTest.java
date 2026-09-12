package dev.fede.gametest;

import dev.fede.FeClient;
import dev.fede.gui.widget.KeybindWidget;
import dev.fede.module.impl.AutoCrystalModule;
import dev.fede.settings.KeybindSetting;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class AutoCrystalGameTest implements FabricClientGameTest {
   private static final int TRIGGER_KEY = 71;
   private static final BlockPos BASE = new BlockPos(0, 1, 0);
   private static final Box CRYSTAL_BOX = new Box(-0.5, 1.5, -0.5, 1.5, 5.5, 2.5);

   public void runTest(ClientGameTestContext context) {
      TestSingleplayerContext world = context.worldBuilder().create();

      try {
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> mc.options.pauseOnLostFocus = false);
         context.getInput().resizeWindow(1280, 720);
         context.waitTicks(2);
         TestServerContext server = world.getServer();
         context.runOnClient(mc -> require(FeClient.modules().autoCrystal != null, "AutoCrystal registered"));
         this.buildArena(context, server, "obsidian");
         context.runOnClient(mc -> {
            AutoCrystalModule ac = FeClient.modules().autoCrystal;
            ac.activateKey.set(71);
            ac.placeDelay.set(0.0);
            ac.breakDelay.set(0.0);
            ac.obsidianDelay.set(0.0);
            ac.autoObsidian.set(true);
            ac.switchBack.set(false);
            ac.range.set(1.0);
            ac.setEnabled(true);
         });
         context.takeScreenshot("autocrystal-aim");
         context.runOnClient(mc -> require(isAimingAtBase(mc.crosshairTarget), "aim lands on the base, got " + describeHit(mc.crosshairTarget)));
         context.getInput().holdKey(71);
         context.waitTicks(6);
         int[] capturedId = new int[1];
         context.runOnClient(
            mc -> {
               List<EndCrystalEntity> crystals = mc.world.getNonSpectatingEntities(EndCrystalEntity.class, CRYSTAL_BOX);
               if (crystals.size() != 1) {
                  String diag = "hand="
                     + mc.player.getMainHandStack().getItem()
                     + " off="
                     + mc.player.getOffHandStack().getItem()
                     + " sel="
                     + mc.player.getInventory().getSelectedSlot()
                     + " base="
                     + mc.world.getBlockState(new BlockPos(0, 1, 0))
                     + " above="
                     + mc.world.getBlockState(new BlockPos(0, 2, 0))
                     + " trigHeld="
                     + InputUtil.isKeyPressed(mc.getWindow(), 71)
                     + " enabled="
                     + FeClient.modules().autoCrystal.isEnabled()
                     + " hit="
                     + describeHit(mc.crosshairTarget);
                  require(false, "one crystal placed on the obsidian base, got " + crystals.size() + " | " + diag);
               }

               capturedId[0] = crystals.get(0).getId();
            }
         );
         context.takeScreenshot("autocrystal-placed");
         context.runOnClient(mc -> FeClient.modules().autoCrystal.range.set(3.0));
         context.waitTicks(6);
         context.runOnClient(
            mc -> {
               List<EndCrystalEntity> survivors = mc.world
                  .getNonSpectatingEntities(EndCrystalEntity.class, CRYSTAL_BOX)
                  .stream()
                  .filter(c -> c.getId() == capturedId[0])
                  .toList();
               if (!survivors.isEmpty()) {
                  double dist = mc.player.getEyePos().distanceTo(survivors.get(0).getEntityPos());
                  require(
                     false,
                     "crystal id "
                        + capturedId[0]
                        + " should have been broken | dist="
                        + String.format(Locale.ROOT, "%.2f", dist)
                        + " hit="
                        + describeHit(mc.crosshairTarget)
                        + " hand="
                        + mc.player.getMainHandStack().getItem()
                  );
               }
            }
         );
         context.takeScreenshot("autocrystal-broken");
         context.getInput().releaseKey(71);
         context.waitTicks(2);
         this.buildArena(context, server, "stone");
         context.runOnClient(mc -> FeClient.modules().autoCrystal.range.set(1.0));
         context.runOnClient(mc -> require(isAimingAtBase(mc.crosshairTarget), "aim lands on the stone block, got " + describeHit(mc.crosshairTarget)));
         context.getInput().holdKey(71);
         context.waitTicks(8);
         context.runOnClient(mc -> {
            boolean obiTop = mc.world.getBlockState(new BlockPos(0, 2, 0)).isOf(Blocks.OBSIDIAN);
            boolean obiSide = mc.world.getBlockState(new BlockPos(0, 1, 1)).isOf(Blocks.OBSIDIAN);
            require(obiTop || obiSide, "auto-obsidian laid a base (expected at 0,2,0 or 0,1,1)");
            int crystals = mc.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(-0.5, 1.5, -0.5, 1.5, 6.5, 2.5)).size();
            require(crystals >= 1, "a crystal was placed on the auto-laid obsidian, got " + crystals);
         });
         context.takeScreenshot("autocrystal-obsidian");
         context.getInput().releaseKey(71);
         context.runOnClient(mc -> FeClient.modules().autoCrystal.setEnabled(false));
         context.waitTicks(2);
         context.runOnClient(mc -> {
            KeybindSetting kb = new KeybindSetting("Test", "", -1);
            KeybindWidget widget = new KeybindWidget(FeClient.themes(), kb);
            widget.setBounds(0.0F, 0.0F, 100.0F);
            require(widget.mouseClicked(10.0F, 10.0F, 0), "arming left-click is consumed");
            require(widget.isListening(), "widget listens after the arming click");
            require(widget.mouseClicked(10.0F, 10.0F, 1), "the bind right-click is consumed");
            require(!widget.isListening(), "widget stops listening once bound");
            require(kb.get() == 1, "right-click bound RMB, got " + kb.get());
            require(kb.keyName().equals("RMB"), "the bind shows as 'RMB', got " + kb.keyName());
            widget.mouseClicked(10.0F, 10.0F, 0);
            require(widget.keyPressed(86), "a keyboard key still binds");
            require(kb.get() == 86, "V bound, got " + kb.get());
            widget.mouseClicked(10.0F, 10.0F, 0);
            widget.keyPressed(256);
            require(kb.get() == -1, "ESC clears the bind");
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

   private void buildArena(ClientGameTestContext context, TestServerContext server, String baseBlock) {
      server.runCommand("kill @e[type=minecraft:end_crystal]");
      server.runCommand("fill -20 0 -20 20 0 20 minecraft:stone");
      server.runCommand("fill -20 1 -20 20 8 20 minecraft:air");
      server.runCommand("setblock 0 1 0 minecraft:null");
      server.runCommand("gamemode creative @a");
      server.runCommand("clear @a");
      server.runCommand("give @a minecraft:end_crystal 64");
      server.runCommand("give @a minecraft:obsidian 64");
      server.runCommand("tp @a 0.5 1 2.5 180 15");
      context.waitTicks(5);
      context.runOnClient(mc -> aimAt(mc.player, 0.5, 2.0, 0.5));
      context.waitTicks(3);
   }

   private static void aimAt(ClientPlayerEntity player, double tx, double ty, double tz) {
      Vec3d eye = player.getEyePos();
      double dx = tx - eye.x;
      double dy = ty - eye.y;
      double dz = tz - eye.z;
      double horiz = Math.sqrt(dx * dx + dz * dz);
      float yaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
      float pitch = (float)(-Math.toDegrees(Math.atan2(dy, horiz)));
      player.setYaw(yaw);
      player.setPitch(pitch);
      player.setHeadYaw(yaw);
   }

   private static boolean isAimingAtBase(HitResult hit) {
      return hit instanceof BlockHitResult bhr && bhr.getType() == Type.BLOCK && bhr.getBlockPos().equals(BASE);
   }

   private static String describeHit(HitResult hit) {
      if (hit instanceof BlockHitResult bhr && bhr.getType() == Type.BLOCK) {
         return "block " + bhr.getBlockPos() + " face " + bhr.getSide();
      } else {
         return hit == null ? "null" : hit.getType().toString();
      }
   }

   private static void require(boolean condition, String what) {
      if (!condition) {
         throw new AssertionError("FAILED: null");
      }
   }
}



