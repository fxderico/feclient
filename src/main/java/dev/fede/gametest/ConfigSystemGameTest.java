package dev.fede.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.fede.FeClient;
import dev.fede.config.ConfigStore;
import dev.fede.gui.ClickGuiScreen;
import dev.fede.module.Modules;
import dev.fede.module.impl.FullbrightModule;
import dev.fede.theme.Theme;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

public class ConfigSystemGameTest implements FabricClientGameTest {
   public void runTest(ClientGameTestContext context) {
      TestSingleplayerContext world = context.worldBuilder().create();

      try {
         world.getClientWorld().waitForChunksRender();
         context.runOnClient(mc -> mc.options.pauseOnLostFocus = false);
         context.getInput().resizeWindow(1600, 900);
         context.waitTicks(2);
         context.runOnClient(mc -> {
            ConfigStore store = FeClient.configStore();
            clearDir(store.directory());
            store.loadAll();

            for (int i = 0; i < 5; i++) {
               assertThat(!store.slot(i).filled(), "slot " + i + " must start empty");
            }
         });
         String[] exported = new String[]{null};
         context.runOnClient(mc -> {
            ConfigStore store = FeClient.configStore();
            selectTheme("Blue");
            FullbrightModule fullbright = FeClient.modules().fullbright;
            Modules.SusChunkFinderModule finder = FeClient.modules().susChunkFinder;
            fullbright.setEnabled(true);
            fullbright.getKeybind().set(71);
            finder.sensitivity.set(7.0);
            assertThat(store.save(0), "save(0) must succeed");
            assertThat(store.slot(0).filled(), "slot 0 must be filled after save");
            assertThat(Files.exists(store.directory().resolve("slot1.json")), "slot1.json must exist on disk after save");
            assertThat(store.rename(0, "PvP"), "rename(0) must succeed");
            assertThat(store.slot(0).name().equals("PvP"), "slot 0 name must be PvP");
         });
         context.runOnClient(
            mc -> {
               ConfigStore store = FeClient.configStore();
               selectTheme("Pink");
               FeClient.modules().fullbright.setEnabled(false);
               FeClient.modules().fullbright.getKeybind().set(-1);
               FeClient.modules().susChunkFinder.sensitivity.set(1.0);
               assertThat(store.activate(0), "activate(0) must succeed");
               assertThat(FeClient.modules().fullbright.isEnabled(), "activate must re-enable Fullbright");
               assertThat(FeClient.modules().fullbright.getKeybind().get() == 71, "activate must restore the keybind");
               assertThat(
                  Math.abs(FeClient.modules().susChunkFinder.sensitivity.getFloat() - 7.0F) < 0.001F,
                  "activate must restore the slider value, got " + FeClient.modules().susChunkFinder.sensitivity.getFloat()
               );
               assertThat(FeClient.themes().current().getName().equals("Blue"), "activate must restore the Blue theme");
               assertThat(store.activeIndex() == 0, "slot 0 must be marked active");
            }
         );
         context.runOnClient(mc -> {
            ConfigStore store = FeClient.configStore();
            String json = store.export(0);
            assertThat(json != null, "export(0) must return a string");
            JsonObject parsed = JsonParser.parseString(json).getAsJsonObject();
            assertThat("67client-config".equals(parsed.get("format").getAsString()), "export must carry the format tag");
            assertThat(parsed.get("version").getAsInt() == 1, "export must carry the version");
            assertThat(parsed.has("state") && parsed.getAsJsonObject("state").has("modules"), "export must embed the full state snapshot");
            exported[0] = json;
         });
         context.runOnClient(mc -> {
            ConfigStore store = FeClient.configStore();
            ConfigStore.ImportResult bad = store.importInto(1, "{ this is not json ]");
            assertThat(!bad.isEnabled(), "corrupt import must fail");
            assertThat(!store.slot(1).filled(), "failed import must leave slot 1 empty");
            ConfigStore.ImportResult notConfig = store.importInto(1, "{\"hello\":\"world\"}");
            assertThat(!notConfig.isEnabled(), "non-config JSON must fail import");
            assertThat(!store.slot(1).filled(), "slot 1 must still be empty");
            ConfigStore.ImportResult tooNew = store.importInto(1, "{\"format\":\"67client-config\",\"version\":2,\"state\":{\"modules\":{}}}");
            assertThat(!tooNew.isEnabled(), "a newer-version config must be refused");
         });
         context.runOnClient(mc -> {
            ConfigStore store = FeClient.configStore();
            ConfigStore.ImportResult good = store.importInto(2, exported[0]);
            assertThat(good.isEnabled(), "valid import must succeed: " + good.message());
            assertThat(store.slot(2).filled(), "slot 2 must be filled after import");
            assertThat(store.slot(2).name().equals("PvP"), "imported slot must carry the exported name, got " + store.slot(2).name());
            store.rename(2, "Legit");
         });
         context.runOnClient(mc -> {
            ConfigStore store = FeClient.configStore();
            store.save(3);
            store.rename(3, "Building");
         });
         context.getInput().pressKey(344);
         context.waitForScreen(ClickGuiScreen.class);
         context.waitTicks(8);
         context.takeScreenshot("config-01-menu-with-fidget");
         context.runOnClient(mc -> {
            if (mc.currentScreen instanceof ClickGuiScreen cg) {
               cg.configPanel().open();
            }
         });
         context.waitTicks(8);
         context.takeScreenshot("config-02-panel-slots");
         context.runOnClient(mc -> {
            if (mc.currentScreen instanceof ClickGuiScreen cg) {
               cg.configPanel().debugBeginRename(1);
            }
         });
         context.waitTicks(6);
         context.takeScreenshot("config-03-rename-field");
         context.runOnClient(mc -> {
            if (mc.currentScreen instanceof ClickGuiScreen cg) {
               cg.configPanel().debugConfirmOverwrite(3);
            }
         });
         context.waitTicks(6);
         context.takeScreenshot("config-04-overwrite-confirm");
         context.runOnClient(mc -> {
            if (mc.currentScreen instanceof ClickGuiScreen cg) {
               cg.configPanel().debugConfirmDelete(2);
            }
         });
         context.waitTicks(6);
         context.takeScreenshot("config-05-delete-confirm");
         context.runOnClient(mc -> {
            if (mc.currentScreen instanceof ClickGuiScreen cg) {
               cg.configPanel().close();
            }
         });
         context.getInput().pressKey(344);
         context.waitForScreen(null);
         context.runOnClient(mc -> {
            ConfigStore store = FeClient.configStore();
            assertThat(store.slot(3).filled(), "slot 3 must be filled before delete");
            assertThat(store.delete(3), "delete(3) must succeed");
            assertThat(!store.slot(3).filled(), "slot 3 must be empty after delete");
            assertThat(!Files.exists(store.directory().resolve("slot4.json")), "slot4.json must be gone after delete");
            assertThat(store.slot(3).name().equals(ConfigStore.defaultName(3)), "deleted slot name must reset to default, got " + store.slot(3).name());
            assertThat(store.activeIndex() == 0, "slot 0 must still be active before its delete");
            assertThat(store.delete(0), "delete(0) must succeed");
            assertThat(store.activeIndex() == -1, "deleting the active slot must clear active");
            assertThat(!store.slot(0).filled(), "slot 0 must be empty after delete");
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

   private static int KeybindNone() {
      return -1;
   }

   private static void selectTheme(String name) {
      for (Theme t : FeClient.themes().getThemes()) {
         if (t.getName().equals(name)) {
            FeClient.themes().select(t);
            return;
         }
      }
   }

   private static void clearDir(Path dir) {
      try {
         if (Files.exists(dir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
               for (Path p : stream) {
                  Files.deleteIfExists(p);
               }
            }
         }
      } catch (Exception var6) {
         throw new RuntimeException("Failed to clear config test dir", var6);
      }
   }

   private static void assertThat(boolean condition, String message) {
      if (!condition) {
         throw new AssertionError(message);
      }
   }
}



