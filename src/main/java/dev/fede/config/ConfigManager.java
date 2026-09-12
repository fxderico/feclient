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



