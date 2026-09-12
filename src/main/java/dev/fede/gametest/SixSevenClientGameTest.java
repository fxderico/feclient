package dev.fede.gametest;

import dev.fede.FeClient;
import dev.fede.gui.ClickGuiScreen;
import dev.fede.gui.IconPickerScreen;
import dev.fede.module.Modules;
import dev.fede.module.impl.ChunkFinderModule;
import dev.fede.module.impl.CustomAccessoriesModule;
import dev.fede.module.impl.CustomCrosshairModule;
import dev.fede.module.impl.CustomFovModule;
import dev.fede.module.impl.CustomGlintModule;
import dev.fede.module.impl.FreecamModule;
import dev.fede.module.impl.HitParticlesModule;
import dev.fede.module.impl.MotionBlurModule;
import dev.fede.module.impl.NameProtectModule;
import dev.fede.module.impl.NameTagsModule;
import dev.fede.module.impl.SpawnerNametagsModule;
import dev.fede.module.impl.StorageEspModule;
import dev.fede.notification.NotificationManager;
import dev.fede.render.BlockEspRenderer;
import dev.fede.render.MotionBlurRenderer;
import dev.fede.render.StorageEspRenderer;
import dev.fede.suschunk.ServerLightCache;
import dev.fede.suschunk.SusChunkScanner;
import dev.fede.theme.Theme;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

public class SixSevenClientGameTest implements FabricClientGameTest {
   public void runTest(ClientGameTestContext context) {
      TestSingleplayerContext world = context.worldBuilder().create();

      try {
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> mc.options.pauseOnLostFocus = false);
         context.getInput().resizeWindow(1600, 900);
         context.waitTicks(2);
         TestServerContext server = world.getServer();

         for (int x = -60; x <= -58; x++) {
            for (int z = 8; z <= 10; z++) {
               server.runCommand("setblock " + x + " 0 " + z + " minecraft:budding_amethyst");
               server.runCommand("setblock " + x + " 1 " + z + " minecraft:amethyst_cluster");
            }
         }

         for (int x = 8; x <= 12; x++) {
            for (int z = 8; z <= 11; z++) {
               server.runCommand("setblock " + x + " 0 " + z + " minecraft:budding_amethyst");
            }
         }

         for (int x = 20; x <= 23; x++) {
            for (int z = 8; z <= 10; z++) {
               server.runCommand("setblock " + x + " 0 " + z + " minecraft:budding_amethyst");
            }
         }

         for (int x = 14; x <= 17; x++) {
            server.runCommand("setblock " + x + " 0 64 minecraft:budding_amethyst");
         }

         for (int x = 97; x <= 99; x++) {
            for (int z = 9; z <= 11; z++) {
               server.runCommand("setblock " + x + " -1 " + z + " minecraft:dirt");
               server.runCommand("setblock " + x + " 0 " + z + " minecraft:sweet_berry_bush[age=3]");
            }
         }

         server.runCommand("setblock 40 0 40 minecraft:beehive[honey_level=5]");
         server.runCommand("setblock 40 0 72 minecraft:beehive[honey_level=5]");
         server.runCommand("setblock 72 0 40 minecraft:beehive[honey_level=0]");
         context.waitTicks(10);
         context.runOnClient(mc -> {
            Modules.SusChunkFinderModule finder = FeClient.modules().susChunkFinder;
            finder.sensitivity.set(3.0);
            finder.notifications.set("Toast");
            finder.smartMode.set(true);
            finder.outline.set(true);
            finder.showOnRadar.set(true);
            finder.renderY.set(-59.0);
            finder.setEnabled(true);
         });
         server.runCommand("tp @a 3000 -58 3000");
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(-90.0F);
               mc.player.setPitch(0.0F);
            }
         });
         context.waitTicks(60);
         context.runOnClient(mc -> {
            SusChunkScanner scanner = FeClient.modules().susChunkFinder.scanner;
            assertThat(scanner.flags().isEmpty(), "fresh area must produce zero flags, got " + scanner.flags().size());
         });
         context.takeScreenshot("01-fresh-area-clean");
         server.runCommand("tp @a -14 -58 -10");
         world.getClientWorld().waitForChunksRender();
         context.waitTicks(80);
         context.runOnClient(mc -> {
            SusChunkScanner scanner = FeClient.modules().susChunkFinder.scanner;
            boolean realGeodeFlagged = scanner.flags().stream().anyMatch(f -> f.chunkKey() == ChunkPos.toLong(-4, 0));
            assertThat(realGeodeFlagged, "REAL geode (visible clusters, packet light) must flag after chunk reload");
         });
         context.runOnClient(mc -> {
            for (int x = 8; x <= 12; x++) {
               for (int z = 8; z <= 11; z++) {
                  ServerLightCache.get().injectForTest(x, 1, z, 5);
               }
            }

            for (int x = 20; x <= 23; x++) {
               for (int z = 8; z <= 10; z++) {
                  ServerLightCache.get().injectForTest(x, 1, z, 5);
               }
            }
         });
         context.waitTicks(30);
         context.runOnClient(mc -> {
            SusChunkScanner scanner = FeClient.modules().susChunkFinder.scanner;
            assertThat(scanner.flags().size() == 3, "real geode + two synth chunks must flag, got " + scanner.flags().size());
            assertThat(scanner.zones().size() == 2, "expected 2 zones (real geode; merged synth pair), got " + scanner.zones().size());
         });
         server.runCommand("tp @a 16 -58 64");
         world.getClientWorld().waitForChunksRender();
         context.waitTicks(25);
         context.runOnClient(mc -> {
            for (int x = 14; x <= 17; x++) {
               ServerLightCache.get().injectForTest(x, 1, 64, 5);
            }
         });
         context.waitTicks(30);
         context.runOnClient(
            mc -> {
               SusChunkScanner scanner = FeClient.modules().susChunkFinder.scanner;
               boolean left = scanner.flags().stream().anyMatch(f -> f.chunkKey() == ChunkPos.toLong(0, 4));
               boolean right = scanner.flags().stream().anyMatch(f -> f.chunkKey() == ChunkPos.toLong(1, 4));
               assertThat(left && right, "split geode: BOTH 2-cluster sub-chunks must flag via per-geode counting");
               boolean sameZone = scanner.zones()
                  .stream()
                  .anyMatch(z -> z.members().contains(ChunkPos.toLong(0, 4)) && z.members().contains(ChunkPos.toLong(1, 4)));
               assertThat(sameZone, "split geode's two chunks must share one zone");
            }
         );
         server.runCommand("tp @a 16 -33 92");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(180.0F);
               mc.player.setPitch(46.0F);
            }
         });
         context.waitTicks(3);
         context.takeScreenshot("02b-split-geode-both-chunks");
         server.runCommand("tp @a -58 -42 35");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(180.0F);
               mc.player.setPitch(36.0F);
            }
         });
         context.waitTicks(3);
         context.takeScreenshot("02-real-geode-flagged");
         server.runCommand("tp @a -20 -35 -8");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(-66.0F);
               mc.player.setPitch(33.0F);
            }
         });
         context.waitTicks(3);
         context.takeScreenshot("03-smart-merged-quad");
         context.runOnClient(mc -> FeClient.modules().susChunkFinder.smartMode.set(false));
         server.runCommand("tp @a -20 -35 -8");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(-66.0F);
               mc.player.setPitch(33.0F);
            }
         });
         context.waitTicks(3);
         context.takeScreenshot("04-per-chunk");
         context.runOnClient(mc -> FeClient.modules().susChunkFinder.smartMode.set(true));
         server.runCommand("tp @a -20 -58 -8");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(-66.0F);
               mc.player.setPitch(10.0F);
            }
         });
         context.waitTicks(10);
         context.takeScreenshot("05-radar-inrange");
         server.runCommand("tp @a 100 -58 8");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(-90.0F);
               mc.player.setPitch(5.0F);
            }
         });
         context.waitTicks(25);
         context.takeScreenshot("06-radar-clamped-label");
         context.runOnClient(mc -> {
            Modules.SusChunkFinderModule finder = FeClient.modules().susChunkFinder;
            finder.notifications.set("Chat");
            finder.sensitivity.set(2.0);
         });
         context.waitTicks(30);
         context.runOnClient(mc -> {
            SusChunkScanner scanner = FeClient.modules().susChunkFinder.scanner;
            boolean berryFlagged = scanner.flags().stream().anyMatch(f -> f.chunkKey() == ChunkPos.toLong(6, 0));
            assertThat(berryFlagged, "berry chunk must join at sensitivity 2");
         });
         context.takeScreenshot("07-berries-sens2-chat");
         context.runOnClient(mc -> {
            Modules.SusChunkFinderModule finder = FeClient.modules().susChunkFinder;
            finder.sensitivity.set(3.0);
            finder.notifications.set("Toast");
            ClickGuiScreen.state().setExpanded("SusChunkFinder@RENDER", true);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(ClickGuiScreen.class);
         context.waitTicks(10);
         context.takeScreenshot("08-settings");
         context.runOnClient(mc -> {
            FeClient.modules().susChunkFinder.setEnabled(false);
            ClickGuiScreen.state().setExpanded("SusChunkFinder@RENDER", false);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(null);
         String[] containers = new String[]{
            "chest",
            "trapped_chest",
            "barrel",
            "white_shulker_box",
            "furnace",
            "blast_furnace",
            "smoker",
            "hopper",
            "dropper",
            "dispenser",
            "ender_chest",
            "brewing_stand",
            "spawner"
         };

         for (int i = 0; i < containers.length; i++) {
            server.runCommand("setblock " + (2 + i) + " -59 25 minecraft:" + containers[i]);
         }

         server.runCommand("tp @a 3000 -58 3000");
         world.getClientWorld().waitForChunksRender();
         server.runCommand("tp @a 8 -57 18");
         world.getClientWorld().waitForChunksRender();
         context.waitTicks(10);
         context.runOnClient(mc -> {
            StorageEspModule esp = FeClient.modules().storageEsp;
            esp.tracers.set(false);
            esp.setEnabled(true);
            if (mc.player != null) {
               mc.player.setYaw(0.0F);
               mc.player.setPitch(12.0F);
            }
         });
         context.waitTicks(40);
         context.runOnClient(
            mc -> {
               int count = StorageEspRenderer.cachedCount();
               long shulkers = StorageEspRenderer.cachedShulkerCount();
               StringBuilder seen = new StringBuilder();
               if (mc.world != null) {
                  for (int i = 0; i < containers.length; i++) {
                     seen.append(mc.world.getBlockState(new BlockPos(2 + i, -59, 25)).getBlock().getName().getString()).append(' ');
                  }
               }

               String ctx = " [cache="
                  + count
                  + "/"
                  + containers.length
                  + " shulkers="
                  + shulkers
                  + " playerChunk="
                  + (mc.player != null ? mc.player.getChunkPos() : "null")
                  + " clientBlocks="
                  + seen.toString().trim()
                  + "]";
               assertThat(count >= containers.length, "StorageESP must detect every container type,null");
               assertThat(shulkers >= 1L, "StorageESP must detect the shulker box,null");
            }
         );
         context.takeScreenshot("10-storage-esp-all-types");
         context.runOnClient(mc -> FeClient.modules().storageEsp.setEnabled(false));
         server.runCommand("tp @a 8 -57 18");
         context.runOnClient(mc -> {
            StorageEspModule esp = FeClient.modules().storageEsp;
            esp.tracers.set(true);
            esp.setEnabled(true);
            if (mc.player != null) {
               mc.player.setYaw(0.0F);
               mc.player.setPitch(8.0F);
            }
         });
         context.waitTicks(20);
         context.takeScreenshot("10b-tracers-front-crosshair");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(90.0F);
            }
         });
         context.waitTicks(20);
         context.takeScreenshot("10c-tracers-offcamera-side");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(180.0F);
            }
         });
         context.waitTicks(20);
         context.takeScreenshot("10d-tracers-behind");
         context.runOnClient(mc -> {
            StorageEspModule esp = FeClient.modules().storageEsp;
            esp.tracers.set(false);
            esp.setEnabled(false);
         });
         server.runCommand("tp @a 8 -57 18");
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(0.0F);
               mc.player.setPitch(0.0F);
            }

            CustomCrosshairModule xhair = FeClient.modules().customCrosshair;
            xhair.hideVanilla.set(true);
            xhair.rainbow.set(false);
            xhair.setEnabled(true);
         });
         String[] xhairStyles = new String[]{"Cross", "Dot", "Circle", "T-Shape", "Brackets", "Chevron", "FE Logo"};

         for (int i = 0; i < xhairStyles.length; i++) {
            String s = xhairStyles[i];
            context.runOnClient(mc -> FeClient.modules().customCrosshair.style.set(s));
            context.waitTicks(2);
            context.takeScreenshot(String.format("11%c-crosshair-%s", (char)(97 + i), s.toLowerCase()));
         }

         context.runOnClient(
            mc -> assertThat(FeClient.modules().customCrosshair.style.check("FE Logo"), "crosshair style must be the FE logo for the final shot")
         );
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.setScreen(new InventoryScreen(mc.player));
            }
         });
         context.waitTicks(2);
         context.takeScreenshot("11h-crosshair-hidden-in-inventory");
         context.runOnClient(mc -> {
            mc.setScreen(null);
            FeClient.modules().customCrosshair.setEnabled(false);
         });
         server.runCommand("tp @a 8 -57 18");
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(0.0F);
               mc.player.setPitch(0.0F);
            }
         });
         context.waitTicks(4);
         context.takeScreenshot("12a-zoom-off");
         context.runOnClient(mc -> FeClient.modules().zoom.setEnabled(true));
         context.waitTicks(40);
         context.takeScreenshot("12b-zoom-on-4x");
         context.runOnClient(
            mc -> assertThat(
               FeClient.modules().zoom.currentFactor() > 3.9, "Zoom must ease to the 4x target, was " + FeClient.modules().zoom.currentFactor()
            )
         );
         context.runOnClient(mc -> FeClient.modules().zoom.setEnabled(false));
         context.runOnClient(mc -> {
            Modules.SpotifyModule s = FeClient.modules().spotify;
            s.source.set("Demo");
            s.controls.set(true);
            s.volume.set(true);
            s.setEnabled(true);
         });
         context.waitTicks(5);
         context.takeScreenshot("13-spotify-volume-slider");
         context.runOnClient(mc -> FeClient.modules().spotify.setEnabled(false));
         server.runCommand("tp @a 8 -57 18");
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(0.0F);
               mc.player.setPitch(0.0F);
            }
         });
         context.waitTicks(4);
         context.takeScreenshot("14a-fov-off");
         context.runOnClient(mc -> {
            CustomFovModule f = FeClient.modules().customFov;
            f.fov.set(140.0);
            f.setEnabled(true);
         });
         context.waitTicks(40);
         context.takeScreenshot("14b-fov-140-fisheye");
         context.runOnClient(
            mc -> assertThat(
               FeClient.modules().customFov.currentFov() > 135.0, "CustomFOV must ease to 140, was " + FeClient.modules().customFov.currentFov()
            )
         );
         context.runOnClient(mc -> FeClient.modules().customFov.fov.set(40.0));
         context.waitTicks(40);
         context.takeScreenshot("14c-fov-40-tunnel");
         context.runOnClient(
            mc -> assertThat(
               FeClient.modules().customFov.currentFov() < 45.0, "CustomFOV must ease to 40, was " + FeClient.modules().customFov.currentFov()
            )
         );
         context.runOnClient(mc -> FeClient.modules().customFov.setEnabled(false));
         server.runCommand("tp @a 8 -57 18");
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(0.0F);
               mc.player.setPitch(7.0F);
            }

            HitParticlesModule hp = FeClient.modules().hitParticles;
            hp.amount.set(20.0);
            hp.size.set(1.4);
            hp.spread.set(1.1);
            hp.lifetime.set(1.0);
            hp.glow.set(80.0);
            hp.shockwave.set(true);
            hp.rainbow.set(false);
            hp.setEnabled(true);
         });
         context.waitTicks(15);
         String[] hitStyles = new String[]{"Sparks", "Hearts", "Lightning", "FE Logo"};

         for (int i = 0; i < hitStyles.length; i++) {
            String s = hitStyles[i];
            context.runOnClient(mc -> {
               HitParticlesModule hp = FeClient.modules().hitParticles;
               hp.clear();
               hp.style.set(s);
               if (mc.player != null) {
                  Vec3d eye = mc.player.getCameraPosVec(1.0F);
                  Vec3d look = mc.player.getRotationVec(1.0F);
                  Vec3d a = eye.add(look.multiply(3.0));
                  Vec3d b = eye.add(look.multiply(3.9));
                  hp.spawnAt(a.x - 0.4, a.y, a.z);
                  hp.spawnAt(b.x + 0.4, b.y - 0.3, b.z);
               }
            });
            context.runOnClient(
               mc -> assertThat(!FeClient.modules().hitParticles.particles().isEmpty(), "HitParticles must spawn particles for style null")
            );
            context.waitTicks(3);
            context.takeScreenshot(String.format("15%c-hitparticles-%s", (char)(97 + i), s.toLowerCase()));
         }

         context.runOnClient(mc -> {
            FeClient.modules().hitParticles.clear();
            FeClient.modules().hitParticles.style.set("Sparks");
         });
         server.runCommand("summon minecraft:armor_stand 8.5 -57 22 {NoGravity:1b}");
         context.waitTicks(10);
         context.runOnClient(mc -> {
            if (mc.player != null && mc.world != null && mc.interactionManager != null) {
               Entity target = null;

               for (Entity e : mc.world.getEntities()) {
                  if (e instanceof ArmorStandEntity) {
                     target = e;
                     break;
                  }
               }

               if (target != null) {
                  mc.interactionManager.attackEntity(mc.player, target);
                  assertThat(!FeClient.modules().hitParticles.particles().isEmpty(), "attack() mixin must feed HitParticles a burst on a real hit");
               }
            }
         });
         context.waitTicks(3);
         context.takeScreenshot("15e-hitparticles-real-hit");
         context.runOnClient(mc -> {
            HitParticlesModule hp = FeClient.modules().hitParticles;
            hp.style.set("FE Logo");
            ClickGuiScreen.state().setExpanded("HitParticles@VISUALS", true);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(ClickGuiScreen.class);
         context.waitTicks(8);
         context.takeScreenshot("15f-hitparticles-settings");
         context.runOnClient(mc -> {
            FeClient.modules().hitParticles.setEnabled(false);
            ClickGuiScreen.state().setExpanded("HitParticles@VISUALS", false);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(null);
         context.runOnClient(mc -> {
            Modules.HudModule hudModule = FeClient.modules().hud;
            hudModule.notifications.set(true);
            hudModule.notifyDuration.set(6.0);
            NotificationManager toasts = FeClient.notifications();
            toasts.setPosition(0.99F, 0.71F);
            toasts.setScale(1.0F);
            toasts.push("SpeedMine", true);
            toasts.pushInfo("Sus chunk found  ·  1,247 blocks");
            toasts.pushWeather("Thunderstorm", "brewing overhead", NotificationManager.Weather.THUNDER, true);
         });
         context.waitTicks(12);
         context.takeScreenshot("16a-toasts-default-anchor");
         context.runOnClient(mc -> mc.setScreen(new ChatScreen("", false)));
         context.waitForScreen(ChatScreen.class);
         context.waitTicks(5);
         context.takeScreenshot("16b-toasts-editor-outline");
         context.runOnClient(mc -> {
            NotificationManager toasts = FeClient.notifications();
            toasts.setPosition(0.1F, 0.32F);
            toasts.setScale(1.3F);
         });
         context.waitTicks(8);
         context.takeScreenshot("16c-toasts-moved-scaled");
         context.runOnClient(
            mc -> {
               NotificationManager toasts = FeClient.notifications();
               assertThat(
                  Math.abs(toasts.getFx() - 0.1F) < 0.001F && Math.abs(toasts.getScale() - 1.3F) < 0.001F,
                  "notification anchor must hold its dragged position & scale"
               );
            }
         );
         context.runOnClient(mc -> mc.setScreen(null));
         context.waitForScreen(null);
         context.runOnClient(mc -> {
            FeClient.notifications().setPosition(0.99F, 0.71F);
            FeClient.notifications().setScale(1.0F);
         });
         server.runCommand("tp @a 8 -57 18");
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setYaw(0.0F);
               mc.player.setPitch(0.0F);
            }

            MotionBlurModule mb = FeClient.modules().motionBlur;
            mb.strength.set(85.0);
            mb.pinkTrails.set(true);
            mb.tint.set(45.0);
            mb.fpsCompensated.set(false);
            mb.setEnabled(true);
         });
         context.waitTicks(6);
         context.takeScreenshot("17a-motionblur-static");

         for (int i = 0; i < 12; i++) {
            float yaw = i * 22.0F;
            context.runOnClient(mc -> {
               if (mc.player != null) {
                  mc.player.setYaw(yaw);
               }
            });
            context.waitTicks(1);
         }

         context.runOnClient(
            mc -> assertThat(
               MotionBlurRenderer.framesRendered() > 0 && MotionBlurRenderer.lastRetention() > 0.0F,
               "MotionBlur render path must run with retention > 0, frames="
                  + MotionBlurRenderer.framesRendered()
                  + " retention="
                  + MotionBlurRenderer.lastRetention()
            )
         );
         context.takeScreenshot("17b-motionblur-spin-trails");
         context.runOnClient(mc -> ClickGuiScreen.state().setExpanded("MotionBlur@VISUALS", true));
         context.getInput().pressKey(344);
         context.waitForScreen(ClickGuiScreen.class);
         context.waitTicks(8);
         context.takeScreenshot("17c-motionblur-settings");
         context.runOnClient(mc -> {
            FeClient.modules().motionBlur.setEnabled(false);
            ClickGuiScreen.state().setExpanded("MotionBlur@VISUALS", false);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(null);
         server.runCommand("setblock 6 -59 30 minecraft:spawner{SpawnData:{entity:{id:\"minecraft:zombie\"}}}");
         server.runCommand("setblock 11 -59 30 minecraft:trial_spawner");
         server.runCommand("tp @a 3000 -58 3000");
         world.getClientWorld().waitForChunksRender();
         server.runCommand("tp @a 8 -57 22");
         world.getClientWorld().waitForChunksRender();
         context.waitTicks(10);
         server.runCommand("summon item 7 -58 28 {Item:{id:\"minecraft:diamond\",count:32}}");
         server.runCommand("summon item 9 -58 28 {Item:{id:\"minecraft:golden_apple\",count:3}}");
         server.runCommand("summon item 8 -58 27 {Item:{id:\"minecraft:netherite_ingot\",count:1}}");
         context.waitTicks(10);
         context.runOnClient(mc -> {
            NameTagsModule nt = FeClient.modules().nameTags;
            nt.items.set(true);
            nt.distance.set(true);
            nt.setEnabled(true);
            SpawnerNametagsModule sn = FeClient.modules().spawnerNametags;
            sn.nametag.set(true);
            sn.setEnabled(true);
            if (mc.player != null) {
               mc.player.setYaw(0.0F);
               mc.player.setPitch(12.0F);
            }
         });
         context.waitTicks(20);
         context.runOnClient(
            mc -> assertThat(!FeClient.modules().spawnerNametags.scan.get().isEmpty(), "SpawnerNametags scan must find the placed spawners")
         );
         context.takeScreenshot("18a-nametags-items-spawners");
         context.runOnClient(mc -> {
            NameProtectModule np = FeClient.modules().nameProtect;
            np.ownName.set("SixSeven");
            np.setEnabled(true);
            ClickGuiScreen.state().setExpanded("NameTags@MISC", true);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(ClickGuiScreen.class);
         context.waitTicks(8);
         context.takeScreenshot("18b-nametags-settings");
         context.runOnClient(mc -> ClickGuiScreen.state().setExpanded("NameTags@MISC", false));
         context.getInput().pressKey(344);
         context.waitForScreen(null);
         context.runOnClient(mc -> {
            FeClient.modules().nameTags.setEnabled(false);
            FeClient.modules().spawnerNametags.setEnabled(false);
            NameProtectModule np = FeClient.modules().nameProtect;
            np.selfOnly.set(false);
            np.setEnabled(true);
            if (mc.player != null) {
               String real = mc.player.getGameProfile().name();
               String out = np.censorChat(Text.literal("null left the game")).getString();
               assertThat(!out.contains(real) && out.contains("SixSeven"), "NameProtect must censor the player's name in chat, got: null");
               mc.inGameHud.getChatHud().addMessage(Text.literal("<null> gg wp"));
               mc.inGameHud.getChatHud().addMessage(Text.literal("null joined the game"));
            }
         });
         context.waitTicks(6);
         context.takeScreenshot("18c-nameprotect-chat");
         context.runOnClient(mc -> FeClient.modules().nameProtect.setEnabled(false));
         server.runCommand("item replace entity @a armor.head with minecraft:diamond_helmet");
         server.runCommand("item replace entity @a armor.chest with minecraft:netherite_chestplate");
         server.runCommand("item replace entity @a armor.legs with minecraft:iron_leggings");
         server.runCommand("item replace entity @a armor.feet with minecraft:golden_boots");
         server.runCommand("item replace entity @a weapon.mainhand with minecraft:diamond_sword");
         server.runCommand("item replace entity @a weapon.offhand with minecraft:shield");
         context.waitTicks(6);
         context.runOnClient(
            mc -> {
               NameTagsModule nt = FeClient.modules().nameTags;
               nt.setEnabled(true);
               nt.self.set(true);
               nt.armor.set(true);
               nt.heldItem.set(true);
               mc.options.setPerspective(Perspective.THIRD_PERSON_FRONT);
               if (mc.player != null) {
                  mc.player.setYaw(0.0F);
                  mc.player.setPitch(0.0F);
                  assertThat(
                     !mc.player.getMainHandStack().isEmpty() && !mc.player.getEquippedStack(EquipmentSlot.HEAD).isEmpty(),
                     "self player must be geared up for the armor/held-item tag"
                  );
               }
            }
         );
         context.waitTicks(15);
         context.takeScreenshot("18d-self-tag-armor-held-item");
         context.runOnClient(mc -> mc.options.setPerspective(Perspective.FIRST_PERSON));
         context.waitTicks(15);
         context.takeScreenshot("18d2-self-tag-hidden-first-person");
         context.runOnClient(mc -> {
            FeClient.modules().spawnerNametags.setEnabled(true);
            if (mc.player != null) {
               mc.player.setYaw(0.0F);
               mc.player.setPitch(12.0F);
            }
         });
         context.waitTicks(6);
         context.runOnClient(mc -> mc.setScreen(new InventoryScreen(mc.player)));
         context.waitForScreen(InventoryScreen.class);
         context.waitTicks(5);
         context.takeScreenshot("18e-nametags-hidden-in-inventory");
         context.runOnClient(mc -> {
            mc.setScreen(null);
            FeClient.modules().nameTags.setEnabled(false);
            FeClient.modules().spawnerNametags.setEnabled(false);
         });
         context.waitForScreen(null);
         context.runOnClient(mc -> FeClient.modules().customGlint.setEnabled(false));
         String[] glintGear = new String[]{"netherite_sword", "diamond_chestplate", "diamond_pickaxe", "bow", "golden_apple", "elytra"};

         for (String item : glintGear) {
            server.runCommand("give @a minecraft:null[enchantment_glint_override=true] 1");
         }

         context.waitTicks(10);
         context.runOnClient(mc -> mc.setScreen(new InventoryScreen(mc.player)));
         context.waitForScreen(InventoryScreen.class);
         context.waitTicks(5);
         context.takeScreenshot("19a-glint-vanilla-off");
         context.runOnClient(mc -> {
            CustomGlintModule cg = FeClient.modules().customGlint;
            cg.mode.set("Solid");
            cg.color.set(-49508);
            cg.strength.set(100.0);
            cg.setEnabled(true);
         });
         context.waitTicks(3);
         context.runOnClient(mc -> {
            CustomGlintModule cg = FeClient.modules().customGlint;
            assertThat(cg.isActive(), "CustomGlint must report active once enabled");
            assertThat(cg.glintColor() == -49508, "solid pink @100% must compute to PINK, got " + Integer.toHexString(cg.glintColor()));
         });
         context.takeScreenshot("19b-glint-solid-pink");
         context.runOnClient(mc -> {
            CustomGlintModule cg = FeClient.modules().customGlint;
            cg.strength.set(0.0);
            assertThat(cg.glintColor() == -16777216, "0% strength must zero the glint RGB, got " + Integer.toHexString(cg.glintColor()));
            cg.strength.set(100.0);
         });
         context.runOnClient(mc -> {
            for (Theme t : FeClient.themes().getThemes()) {
               if (t.getName().equals("Blue")) {
                  FeClient.themes().select(t);
               }
            }

            FeClient.modules().customGlint.mode.set("Theme");
         });
         context.waitTicks(3);
         context.runOnClient(
            mc -> {
               int accent = FeClient.themes().current().accent();
               assertThat(
                  FeClient.modules().customGlint.glintColor() == accent,
                  "theme mode @100% must equal the live accent, got " + Integer.toHexString(FeClient.modules().customGlint.glintColor())
               );
            }
         );
         context.takeScreenshot("19c-glint-theme-blue");
         context.runOnClient(mc -> {
            for (Theme t : FeClient.themes().getThemes()) {
               if (t.getName().equals("Pink")) {
                  FeClient.themes().select(t);
               }
            }
         });
         context.runOnClient(mc -> FeClient.modules().customGlint.mode.set("Rainbow"));
         context.waitTicks(3);
         context.takeScreenshot("19d-glint-rainbow");
         context.runOnClient(mc -> mc.setScreen(null));
         context.waitForScreen(null);
         context.runOnClient(mc -> {
            FeClient.modules().customGlint.mode.set("Solid");
            ClickGuiScreen.state().setExpanded("CustomGlint@MISC", true);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(ClickGuiScreen.class);
         context.waitTicks(8);
         context.takeScreenshot("19e-glint-settings");
         context.runOnClient(mc -> {
            FeClient.modules().customGlint.setEnabled(false);
            ClickGuiScreen.state().setExpanded("CustomGlint@MISC", false);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(null);
         server.runCommand("tp @a 500 -60 500 -90 0");
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> {
            FeClient.modules().susChunkFinder.setEnabled(false);
            FeClient.modules().blockOutline.setEnabled(false);
            FeClient.modules().motionBlur.setEnabled(false);
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            if (mc.player != null) {
               mc.player.setYaw(-90.0F);
               mc.player.setBodyYaw(-90.0F);
               mc.player.setPitch(0.0F);
            }

            CustomAccessoriesModule acc = FeClient.modules().customAccessories;
            acc.color.set(-49508);
            acc.rainbow.set(false);
            acc.glow.set(75.0);
            acc.cape.set(true);
            acc.capePhysics.set(true);
            acc.trail.set(false);
            acc.aura.set(false);
            acc.crown.set(false);
            acc.setEnabled(true);
         });
         context.waitTicks(20);

         for (String cs : new String[]{"FE Logo", "Wave", "Grid", "Solid"}) {
            context.runOnClient(mc -> FeClient.modules().customAccessories.capeStyle.set(cs));
            context.waitTicks(6);
            context.takeScreenshot("20-cape-" + cs.toLowerCase());
         }

         context.runOnClient(mc -> {
            CustomAccessoriesModule acc = FeClient.modules().customAccessories;
            acc.cape.set(false);
            acc.trail.set(true);
            acc.trailLength.set(2.0);
            acc.trailStyle.set("Ribbon");
         });
         walkEast(context, 500.0);
         context.runOnClient(
            mc -> assertThat(!FeClient.modules().customAccessories.trailNodes().isEmpty(), "CustomAccessories must record trail nodes while moving")
         );
         context.takeScreenshot("20-trail-ribbon");
         context.runOnClient(mc -> FeClient.modules().customAccessories.trailStyle.set("Sparkle"));
         walkEast(context, 510.0);
         context.takeScreenshot("20-trail-sparkle");
         context.runOnClient(mc -> FeClient.modules().customAccessories.trailStyle.set("Echo"));
         walkEast(context, 520.0);
         context.takeScreenshot("20-trail-echo");
         context.runOnClient(mc -> {
            CustomAccessoriesModule acc = FeClient.modules().customAccessories;
            acc.trail.set(false);
            acc.aura.set(true);
            acc.auraStyle.set("Orbit");
         });
         context.waitTicks(10);
         context.takeScreenshot("20-aura-orbit");
         context.runOnClient(mc -> FeClient.modules().customAccessories.auraStyle.set("Ring"));
         context.waitTicks(10);
         context.takeScreenshot("20-aura-ring");
         context.runOnClient(mc -> {
            CustomAccessoriesModule acc = FeClient.modules().customAccessories;
            acc.aura.set(false);
            acc.crown.set(true);
         });
         context.waitTicks(12);
         context.takeScreenshot("20-crown");
         context.runOnClient(mc -> {
            CustomAccessoriesModule acc = FeClient.modules().customAccessories;
            acc.cape.set(true);
            acc.capeStyle.set("FE Logo");
            acc.trail.set(true);
            acc.trailStyle.set("Ribbon");
            acc.aura.set(true);
            acc.auraStyle.set("Orbit");
            acc.crown.set(true);
         });
         walkEast(context, 500.0);
         context.takeScreenshot("20-all-combined");
         context.runOnClient(mc -> {
            mc.options.setPerspective(Perspective.FIRST_PERSON);
            ClickGuiScreen.state().setExpanded("CustomAccessories@VISUALS", true);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(ClickGuiScreen.class);
         context.waitTicks(8);
         context.takeScreenshot("20-accessories-settings");
         context.runOnClient(mc -> {
            FeClient.modules().customAccessories.setEnabled(false);
            ClickGuiScreen.state().setExpanded("CustomAccessories@VISUALS", false);
         });
         context.getInput().pressKey(344);
         context.waitForScreen(null);
         int[] savedRenderDistance = new int[]{12};
         context.runOnClient(mc -> {
            FeClient.modules().susChunkFinder.setEnabled(false);
            FeClient.modules().blockOutline.setEnabled(false);
            FeClient.modules().motionBlur.setEnabled(false);
            mc.options.setPerspective(Perspective.FIRST_PERSON);
            savedRenderDistance[0] = (Integer)mc.options.getViewDistance().getValue();
            mc.options.getViewDistance().setValue(8);
         });
         server.runCommand("tp @a 34 -59 40 0 0");
         world.getClientWorld().waitForChunksRender();
         context.waitTicks(10);
         context.runOnClient(mc -> {
            BlockState bs = mc.world.getBlockState(new BlockPos(40, 0, 40));
            BlockState bs2 = mc.world.getBlockState(new BlockPos(40, 0, 72));
            boolean loaded = mc.world.getChunkManager().getWorldChunk(2, 2, false) != null;
            boolean loaded2 = mc.world.getChunkManager().getWorldChunk(2, 4, false) != null;
            assertThat(bs.isOf(Blocks.BEEHIVE), "test setup: client must see the beehive at (40,0,40); saw " + bs + " (chunk loaded=" + loaded + ")");
            assertThat(bs2.isOf(Blocks.BEEHIVE), "test setup: client must see the second beehive at (40,0,72); saw " + bs2 + " (chunk loaded=" + loaded2 + ")");
         });
         context.runOnClient(mc -> {
            ChunkFinderModule cf = FeClient.modules().chunkFinder;
            cf.mergeRadius.set(1.0);
            cf.clear();
            cf.setEnabled(true);
         });
         context.waitTicks(260);
         context.runOnClient(
            mc -> {
               ChunkFinderModule cf = FeClient.modules().chunkFinder;
               long fullA = ChunkPos.toLong(2, 2);
               long fullB = ChunkPos.toLong(2, 4);
               long empty = ChunkPos.toLong(4, 2);
               assertThat(
                  cf.flaggedChunks().contains(fullA) && cf.flaggedChunks().contains(fullB),
                  "radius 1 must leave BOTH full-honey beehive chunks flagged (unmerged); flags=" + cf.flaggedChunks()
               );
               assertThat(!cf.flaggedChunks().contains(empty), "an empty (honey 0) beehive must NOT flag");
            }
         );
         server.runCommand("tp @a 40 78 26 0 42");
         context.waitTicks(20);
         context.takeScreenshot("21-chunkfinder-fullhoney");
         context.runOnClient(mc -> FeClient.modules().chunkFinder.mergeRadius.set(4.0));
         context.waitTicks(10);
         context.runOnClient(
            mc -> {
               ChunkFinderModule cf = FeClient.modules().chunkFinder;
               long middle = ChunkPos.toLong(2, 3);
               long fullA = ChunkPos.toLong(2, 2);
               long fullB = ChunkPos.toLong(2, 4);
               assertThat(cf.flaggedChunks().contains(middle), "Smart must flag the middle chunk (2,3) between the two hives; flags=" + cf.flaggedChunks());
               assertThat(
                  !cf.flaggedChunks().contains(fullA) && !cf.flaggedChunks().contains(fullB),
                  "Smart must merge the hive chunks away, leaving only the middle; flags=" + cf.flaggedChunks()
               );
            }
         );
         server.runCommand("tp @a 40 78 26 0 42");
         context.waitTicks(20);
         context.takeScreenshot("21b-chunkfinder-smart");
         context.runOnClient(mc -> {
            FreecamModule fc = FeClient.modules().freecam;
            fc.showPlayerModel.set(false);
            fc.smoothing.set(false);
            fc.setEnabled(true);
         });
         context.waitTicks(30);
         context.runOnClient(
            mc -> {
               assertThat(FeClient.modules().freecam.isActive(), "Freecam must be active for the regression shot");
               assertThat(
                  !FeClient.modules().chunkFinder.flaggedChunks().isEmpty(),
                  "chunk finder must still hold its flag under freecam; flags=" + FeClient.modules().chunkFinder.flaggedChunks()
               );
            }
         );
         context.takeScreenshot("21c-chunkfinder-freecam");
         context.runOnClient(mc -> FeClient.modules().freecam.setEnabled(false));
         context.waitTicks(5);
         context.runOnClient(mc -> {
            FeClient.modules().chunkFinder.setEnabled(false);
            mc.options.getViewDistance().setValue(savedRenderDistance[0]);
         });
         context.runOnClient(mc -> {
            FeClient.modules().blockOutline.setEnabled(false);
            mc.options.setPerspective(Perspective.FIRST_PERSON);
         });
         server.runCommand("tp @a 2 -58 0 0 0");
         context.waitTicks(20);

         for (int bx = 1; bx <= 3; bx++) {
            for (int by = -59; by <= -57; by++) {
               server.runCommand("setblock " + bx + " " + by + " 6 minecraft:diamond_ore");
            }
         }

         server.runCommand("setblock 6 -58 6 minecraft:emerald_ore");
         context.waitTicks(5);
         context.runOnClient(
            mc -> assertThat(mc.world.getBlockState(new BlockPos(2, -58, 6)).isOf(Blocks.DIAMOND_ORE), "test setup: client must see the placed diamond ore")
         );
         context.runOnClient(mc -> FeClient.modules().blockEsp.setEnabled(true));
         context.waitTicks(20);
         context.runOnClient(
            mc -> assertThat(
               BlockEspRenderer.cachedCount() > 0, "BlockESP must detect the enabled diamond ore (emerald is off); cached=" + BlockEspRenderer.cachedCount()
            )
         );
         server.runCommand("tp @a 2 -55 -4 0 22");
         context.waitTicks(20);
         context.takeScreenshot("30-blockesp-boxes");
         context.runOnClient(mc -> ClickGuiScreen.state().setExpanded("BlockESP@RENDER", true));
         context.getInput().pressKey(344);
         context.waitForScreen(ClickGuiScreen.class);
         context.waitTicks(8);
         context.takeScreenshot("31-blockesp-settings");
         context.runOnClient(mc -> {
            if (mc.currentScreen instanceof ClickGuiScreen cg) {
               cg.openBlockPicker(FeClient.modules().blockEsp.targets);
            }
         });
         context.waitForScreen(IconPickerScreen.class);
         context.waitTicks(8);
         context.takeScreenshot("32-blockpicker-grid");
         context.runOnClient(mc -> {
            if (mc.currentScreen instanceof IconPickerScreen picker) {
               picker.debugSetSearch("ore");
            }
         });
         context.waitTicks(8);
         context.takeScreenshot("33-blockpicker-search");
         context.runOnClient(mc -> {
            if (mc.currentScreen instanceof IconPickerScreen picker) {
               picker.debugOpenColor(0);
            }
         });
         context.waitTicks(8);
         context.takeScreenshot("34-blockpicker-color");
         context.runOnClient(mc -> {
            mc.setScreen(null);
            FeClient.modules().blockEsp.setEnabled(false);
            ClickGuiScreen.state().setExpanded("BlockESP@RENDER", false);
         });
         context.waitForScreen(null);
      } catch (Throwable var13) {
         if (world != null) {
            try {
               world.close();
            } catch (Throwable var12) {
               var13.addSuppressed(var12);
            }
         }

         throw var13;
      }

      if (world != null) {
         world.close();
      }
   }

   private static void walkEast(ClientGameTestContext context, double startX) {
      for (int i = 0; i < 10; i++) {
         double x = startX + i * 0.45;
         context.runOnClient(mc -> {
            if (mc.player != null) {
               mc.player.setPosition(x, -60.0, 500.0);
            }
         });
         context.waitTicks(1);
      }

      context.waitTicks(4);
   }

   private static void assertThat(boolean condition, String message) {
      if (!condition) {
         throw new AssertionError(message);
      }
   }
}



