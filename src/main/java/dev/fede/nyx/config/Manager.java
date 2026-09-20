package dev.fede.nyx.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.module.ModuleManager;
import dev.fede.nyx.setting.BindSetting;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.DoubleListSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.loader.api.FabricLoader;

public final class Manager {
   public static final Manager INSTANCE = new Manager();
   private static final int SCHEMA_VERSION = 1;
   private static final String DIR = "codeengine";
   private static final String LEGACY_DIR = "nyx";
   private static final String FILE = "client.json";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private volatile boolean dirty;

   private Manager() {
   }

   public void load(ModuleManager var1) {
      if (var1 != null) {
         Path var2 = file();
         if (!Files.exists(var2)) {
            Path var3 = legacyFile();
            if (!Files.exists(var3)) {
               return;
            }

            var2 = var3;
         }

         JsonObject var12;
         try {
            String var4 = Files.readString(var2);
            JsonElement var5 = JsonParser.parseString(var4);
            if (var5 == null || !var5.isJsonObject()) {
               quarantine(var2, "root is not a JSON object");
               return;
            }

            var12 = var5.getAsJsonObject();
         } catch (RuntimeException | IOException var11) {
            quarantine(var2, var11.getMessage());
            return;
         }

         JsonElement var13 = var12.get("schemaVersion");
         if (var13 != null && var13.isJsonPrimitive()) {
            int var14 = var13.getAsInt();
            if (var14 > 1) {
               System.err.println("[c] file " + var2 + " has schemaVersion=" + var14 + " > " + 1 + "; loading best-effort, unknown fields will be ignored.");
            }
         }

         JsonElement var15 = var12.get("modules");
         if (var15 != null && var15.isJsonObject()) {
            JsonObject var6 = var15.getAsJsonObject();

            for (Entry var8 : var6.entrySet()) {
               Module var9 = var1.moduleOf((String)var8.getKey());
               if (var9 != null) {
                  JsonElement var10 = (JsonElement)var8.getValue();
                  if (var10 != null && var10.isJsonObject()) {
                     this.applyModule(var9, var10.getAsJsonObject());
                  }
               }
            }
         }
      }
   }

   public CompletableFuture<Void> save(ModuleManager var1) {
      if (var1 == null) {
         return CompletableFuture.completedFuture(null);
      } else {
         JsonObject var2 = this.snapshot(var1);
         this.dirty = false;
         return CompletableFuture.runAsync(() -> writeAtomic(var2));
      }
   }

   public void markDirty() {
      this.dirty = true;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   private JsonObject snapshot(ModuleManager var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("schemaVersion", 1);
      JsonObject var3 = new JsonObject();

      for (Module var5 : var1.getList()) {
         JsonObject var6 = new JsonObject();
         var6.addProperty("enabled", var5.isEnabled3());
         var6.addProperty("key", var5.getInt());
         JsonObject var7 = new JsonObject();

         for (Setting var9 : var5.getList()) {
            this.encodeSetting(var7, var9);
         }

         var6.add("settings", var7);
         var3.add(var5.getString(), var6);
      }

      var2.add("modules", var3);
      return var2;
   }

   private void encodeSetting(JsonObject var1, Setting var2) {
      if (var2 instanceof BooleanSetting var3) {
         var1.addProperty(var2.getName(), var3.getValue());
      } else if (var2 instanceof NumberSetting var4) {
         var1.addProperty(var2.getName(), var4.getValue());
      } else if (var2 instanceof ModeSetting var5) {
         var1.addProperty(var2.getName(), var5.getMode());
      } else if (var2 instanceof StringSetting var6) {
         var1.addProperty(var2.getName(), var6.getValue());
      } else if (var2 instanceof ColorSetting var7) {
         var1.addProperty(var2.getName(), var7.toHex());
      } else if (var2 instanceof BindSetting var8) {
         var1.addProperty(var2.getName(), var8.getValue());
      } else if (var2 instanceof DoubleListSetting var9) {
         JsonArray var10 = new JsonArray();

         for (Double var12 : var9.getValue()) {
            var10.add(var12);
         }

         var1.add(var2.getName(), var10);
      }
   }

   private void applyModule(Module var1, JsonObject var2) {
      JsonElement var3 = var2.get("enabled");
      if (var3 != null && var3.isJsonPrimitive() && var3.getAsJsonPrimitive().isBoolean()) {
         var1.run5(var3.getAsBoolean());
      }

      JsonElement var4 = var2.get("key");
      if (var4 != null && var4.isJsonPrimitive() && var4.getAsJsonPrimitive().isNumber()) {
         var1.run7(var4.getAsInt());
      }

      JsonElement var5 = var2.get("settings");
      if (var5 != null && var5.isJsonObject()) {
         JsonObject var6 = var5.getAsJsonObject();

         for (Setting var8 : var1.getList()) {
            JsonElement var9 = var6.get(var8.getName());
            if (var9 != null && !var9.isJsonNull()) {
               this.applySetting(var8, var9);
            }
         }
      }
   }

   private void applySetting(Setting var1, JsonElement var2) {
      try {
         if (var1 instanceof BooleanSetting var3) {
            if (var2.isJsonPrimitive() && var2.getAsJsonPrimitive().isBoolean()) {
               var3.setValue(var2.getAsBoolean());
            }
         } else if (var1 instanceof NumberSetting var4) {
            if (var2.isJsonPrimitive() && var2.getAsJsonPrimitive().isNumber()) {
               var4.setValue(var2.getAsDouble());
            }
         } else if (var1 instanceof ModeSetting var5) {
            if (var2.isJsonPrimitive() && var2.getAsJsonPrimitive().isString()) {
               var5.setMode(var2.getAsString());
            }
         } else if (var1 instanceof StringSetting var6) {
            if (var2.isJsonPrimitive() && var2.getAsJsonPrimitive().isString()) {
               var6.setValue(var2.getAsString());
            }
         } else if (var1 instanceof ColorSetting var7) {
            if (var2.isJsonPrimitive() && var2.getAsJsonPrimitive().isString()) {
               var7.fromHex(var2.getAsString());
            }
         } else if (var1 instanceof BindSetting var8) {
            if (var2.isJsonPrimitive() && var2.getAsJsonPrimitive().isNumber()) {
               var8.setValue(var2.getAsInt());
            }
         } else if (var1 instanceof DoubleListSetting var9 && var2.isJsonArray()) {
            ArrayList var10 = new ArrayList();

            for (JsonElement var12 : var2.getAsJsonArray()) {
               if (var12 != null && var12.isJsonPrimitive() && var12.getAsJsonPrimitive().isNumber()) {
                  var10.add(var12.getAsDouble());
               }
            }

            var9.setValues(var10);
         }
      } catch (RuntimeException var13) {
      }
   }

   private static Path dir() throws IOException {
      Path var0 = FabricLoader.getInstance().getConfigDir();
      Files.createDirectories(var0);
      return var0;
   }

   private static Path file() {
      try {
         return dir().resolve("client.json");
      } catch (IOException var1) {
         return FabricLoader.getInstance().getConfigDir().resolve("client.json");
      }
   }

   private static Path legacyFile() {
      return FabricLoader.getInstance().getConfigDir().resolve("nyx").resolve("client.json");
   }

   private static void writeAtomic(JsonObject var0) {
      Path var1 = file();
      Path var2 = var1.resolveSibling(var1.getFileName().toString() + ".tmp");
      String var3 = GSON.toJson(var0);

      try {
         Files.writeString(var2, var3, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.SYNC);

         try {
            Files.move(var2, var1, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException var7) {
            Files.move(var2, var1, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var8) {
         System.err.println("[c] save failed for " + var1 + ": " + var8.getMessage());

         try {
            Files.deleteIfExists(var2);
         } catch (IOException var6) {
         }
      }
   }

   private static void quarantine(Path var0, String var1) {
      try {
         Path var2 = var0.resolveSibling(var0.getFileName().toString() + ".corrupt-" + System.currentTimeMillis());
         Files.move(var0, var2, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException var3) {
         System.err.println("[c] failed to quarantine corrupt file " + var0 + ": " + var3.getMessage());
      }

      System.err.println("[c] corrupt store null renamed; starting empty. Cause: null");
   }

   private static JsonPrimitive asPrimitiveOrNull(JsonElement el) {
      return el != null && el.isJsonPrimitive() ? el.getAsJsonPrimitive() : null;
   }
}

