package dev.fede.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.fede.FeClient;
import dev.fede.module.Module;
import dev.fede.module.ModuleManager;
import dev.fede.settings.Setting;
import dev.fede.theme.ThemeManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigManager {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final Path file = FabricLoader.getInstance().getConfigDir().resolve("FeClient.json");
   private ModuleManager modules;
   private ThemeManager themes;
   private final Map<String, ConfigManager.Section> sections = new LinkedHashMap<>();

   public ConfigManager(ModuleManager modules, ThemeManager themes) {
      this.modules = modules;
      this.themes = themes;
   }

   public void addSection(String key, Supplier<JsonObject> save, Consumer<JsonObject> load) {
      this.sections.put(key, new Section(save, load));
   }

   public JsonObject captureState() {
      JsonObject root = new JsonObject();
      root.add("theme", this.themes.toJson());
      JsonObject moduleJson = new JsonObject();

      for (Module module : this.modules.all()) {
         JsonObject m = new JsonObject();
         m.addProperty("enabled", module.isEnabled());
         m.add("keybind", module.getKeybind().toJson());
         JsonObject settings = new JsonObject();

         for (Setting<?> setting : module.getSettings()) {
            settings.add(setting.getName(), setting.toJson());
         }

         m.add("settings", settings);
         moduleJson.add(module.getName() + "@" + module.getCategory().name(), m);
      }

      root.add("modules", moduleJson);

      for (Entry<String, ConfigManager.Section> entry : this.sections.entrySet()) {
         root.add(entry.getKey(), (JsonElement)entry.getValue().save().get());
      }

      return root;
   }

   public void applyState(JsonObject root) {
      if (root != null) {
         if (root.has("theme") && root.get("theme").isJsonObject()) {
            this.themes.fromJson(root.getAsJsonObject("theme"));
         }

         if (root.has("modules") && root.get("modules").isJsonObject()) {
            JsonObject moduleJson = root.getAsJsonObject("modules");

            for (Module module : this.modules.all()) {
               JsonObject m = moduleJson.getAsJsonObject(module.getName() + "@" + module.getCategory().name());
               if (m != null) {
                  if (m.has("enabled") && m.get("enabled").getAsBoolean() != module.isEnabled()) {
                     module.setEnabled(m.get("enabled").getAsBoolean());
                  }

                  if (m.has("keybind")) {
                     module.getKeybind().fromJson(m.get("keybind"));
                  }

                  JsonObject settings = m.getAsJsonObject("settings");
                  if (settings != null) {
                     for (Setting<?> setting : module.getSettings()) {
                        if (settings.has(setting.getName())) {
                           setting.fromJson(settings.get(setting.getName()));
                        }
                     }
                  }
               }
            }
         }

         for (Entry<String, ConfigManager.Section> entry : this.sections.entrySet()) {
            if (root.has(entry.getKey()) && root.get(entry.getKey()).isJsonObject()) {
               entry.getValue().load().accept(root.getAsJsonObject(entry.getKey()));
            }
         }
      }
   }

   public synchronized void save() {
      try {
         Files.createDirectories(this.file.getParent());
         Files.writeString(this.file, GSON.toJson(this.captureState()));
      } catch (IOException var2) {
         FeClient.LOGGER.error("Failed to save config", var2);
      }
   }

   // ── named configs ──────────────────────────────────────────────────────────
   // Saved presets live in config/feclient-configs/<name>.json. This is the
   // backing for the Themes → Configs dropdown (create / load / rename / delete).
   private final Path configsDir = FabricLoader.getInstance().getConfigDir().resolve("feclient-configs");

   private Path configFile(String name) {
      return this.configsDir.resolve(sanitize(name) + ".json");
   }

   /** Strip anything that isn't a safe filename char so a config name can't escape the dir. */
   private static String sanitize(String name) {
      return name == null ? "" : name.replaceAll("[^A-Za-z0-9 _\\-]", "").trim();
   }

   /** Alphabetical list of saved config names (no extension). */
   public synchronized java.util.List<String> listConfigs() {
      java.util.List<String> out = new java.util.ArrayList<>();
      if (Files.isDirectory(this.configsDir)) {
         try (java.util.stream.Stream<Path> s = Files.list(this.configsDir)) {
            s.filter(p -> p.getFileName().toString().endsWith(".json"))
             .forEach(p -> out.add(p.getFileName().toString().replaceFirst("\\.json$", "")));
         } catch (IOException var3) {
            FeClient.LOGGER.error("Failed to list configs", var3);
         }
      }
      out.sort(String.CASE_INSENSITIVE_ORDER);
      return out;
   }

   /** Write the current live state to a named preset (create or overwrite). */
   public synchronized boolean saveConfig(String name) {
      String clean = sanitize(name);
      if (clean.isEmpty()) return false;
      try {
         Files.createDirectories(this.configsDir);
         Files.writeString(this.configFile(clean), GSON.toJson(this.captureState()));
         return true;
      } catch (IOException var4) {
         FeClient.LOGGER.error("Failed to save config '{}'", clean, var4);
         return false;
      }
   }

   /** Apply a named preset to the live state and persist it as the active config. */
   public synchronized boolean loadConfig(String name) {
      Path p = this.configFile(name);
      if (!Files.exists(p)) return false;
      try {
         this.applyState(JsonParser.parseString(Files.readString(p)).getAsJsonObject());
         this.save(); // make the loaded preset the active/persisted config too
         return true;
      } catch (Exception var4) {
         FeClient.LOGGER.error("Failed to load config '{}'", name, var4);
         return false;
      }
   }

   public synchronized boolean deleteConfig(String name) {
      try {
         return Files.deleteIfExists(this.configFile(name));
      } catch (IOException var3) {
         FeClient.LOGGER.error("Failed to delete config '{}'", name, var3);
         return false;
      }
   }

   public synchronized boolean renameConfig(String oldName, String newName) {
      String clean = sanitize(newName);
      if (clean.isEmpty()) return false;
      Path from = this.configFile(oldName);
      Path to = this.configFile(clean);
      if (!Files.exists(from)) return false;
      try {
         Files.move(from, to, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
         return true;
      } catch (IOException var6) {
         FeClient.LOGGER.error("Failed to rename config '{}' -> '{}'", oldName, clean, var6);
         return false;
      }
   }

   public synchronized void load() {
      if (Files.exists(this.file)) {
         JsonObject root;
         try {
            root = JsonParser.parseString(Files.readString(this.file)).getAsJsonObject();
         } catch (Exception var3) {
            FeClient.LOGGER.error("Failed to read config, using defaults", var3);
            return;
         }

         this.applyState(root);
      }
   }

   public final class Section {
      private Supplier<JsonObject> save;
      private Consumer<JsonObject> load;

      public Section(Supplier<JsonObject> save, Consumer<JsonObject> load) {
         this.save = save;
         this.load = load;
      }

      public Supplier<JsonObject> save() {
         return this.save;
      }

      public Consumer<JsonObject> load() {
         return this.load;
      }
   }
}



