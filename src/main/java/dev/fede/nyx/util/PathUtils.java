package dev.fede.nyx.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;

public final class PathUtils {
   private static final String DIR = "codeengine";
   private static final String SUBDIR = "configs";
   private static final String EXT = ".json";
   private static final int SCHEMA_VERSION = 1;
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   private PathUtils() {
   }

   public static List<String> list() {
      Path var0;
      try {
         var0 = dir();
      } catch (IOException var6) {
         return Collections.emptyList();
      }

      if (!Files.isDirectory(var0)) {
         return Collections.emptyList();
      } else {
         ArrayList var1 = new ArrayList();

         try (java.util.stream.Stream<java.nio.file.Path> var2 = Files.list(var0)) {
            var2.forEach(var1x -> {
               String var2x = var1x.getFileName().toString();
               if (var2x.endsWith(".json") && Files.isRegularFile((Path)var1x)) {
                  var1.add(var2x.substring(0, var2x.length() - 5));
               }
            });
         } catch (IOException var8) {
            return Collections.emptyList();
         }

         var1.sort(String.CASE_INSENSITIVE_ORDER);
         return var1;
      }
   }

   public static boolean exists(String name) {
      String var1 = sanitize(name);
      return var1.isEmpty() ? false : Files.isRegularFile(file(var1));
   }

   public static boolean saveCurrent(ModuleManager manager, String rawName) {
      if (manager == null) {
         return false;
      } else {
         String var2 = sanitize(rawName);
         if (var2.isEmpty()) {
            return false;
         } else {
            JsonObject var3 = snapshot(manager, var2);
            return writeAtomic(file(var2), var3);
         }
      }
   }

   public static boolean load(ModuleManager manager, String rawName) {
      if (manager == null) {
         return false;
      } else {
         String var2 = sanitize(rawName);
         if (var2.isEmpty()) {
            return false;
         } else {
            Path var3 = file(var2);
            if (!Files.isRegularFile(var3)) {
               return false;
            } else {
               JsonObject var4;
               try {
                  JsonElement var5 = JsonParser.parseString(Files.readString(var3));
                  if (var5 == null || !var5.isJsonObject()) {
                     return false;
                  }

                  var4 = var5.getAsJsonObject();
               } catch (RuntimeException | IOException var11) {
                  return false;
               }

               JsonElement var12 = var4.get("modules");
               if (var12 != null && var12.isJsonObject()) {
                  JsonObject var6 = var12.getAsJsonObject();

                  for (Entry var8 : var6.entrySet()) {
                     Module var9 = manager.moduleOf((String)var8.getKey());
                     if (var9 != null) {
                        JsonElement var10 = (JsonElement)var8.getValue();
                        if (var10 != null && var10.isJsonObject()) {
                           applyModule(var9, var10.getAsJsonObject());
                        }
                     }
                  }

                  return true;
               } else {
                  return false;
               }
            }
         }
      }
   }

   public static boolean delete(String rawName) {
      String var1 = sanitize(rawName);
      if (var1.isEmpty()) {
         return false;
      } else {
         try {
            return Files.deleteIfExists(file(var1));
         } catch (IOException var3) {
            return false;
         }
      }
   }

   public static String sanitize(String raw) {
      if (raw == null) {
         return "";
      } else {
         String var1 = raw.trim();
         if (var1.isEmpty()) {
            return "";
         } else {
            StringBuilder var2 = new StringBuilder(var1.length());

            for (int var3 = 0; var3 < var1.length(); var3++) {
               char var4 = var1.charAt(var3);
               if (var4 >= ' ' && var4 != 127) {
                  if (var4 != ':'
                     && var4 != '/'
                     && var4 != '\\'
                     && var4 != ' '
                     && var4 != '<'
                     && var4 != '>'
                     && var4 != '"'
                     && var4 != '|'
                     && var4 != '?'
                     && var4 != '*') {
                     var2.append(var4);
                  } else {
                     var2.append('_');
                  }
               }
            }

            while (var2.length() > 0 && var2.charAt(0) == '.') {
               var2.deleteCharAt(0);
            }

            while (var2.length() > 0 && (var2.charAt(var2.length() - 1) == '.' || var2.charAt(var2.length() - 1) == ' ')) {
               var2.deleteCharAt(var2.length() - 1);
            }

            if (var2.length() > 64) {
               var2.setLength(64);
            }

            return var2.toString();
         }
      }
   }

   private static JsonObject snapshot(ModuleManager manager, String name) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("schemaVersion", 1);
      var2.addProperty("name", name);
      JsonObject var3 = new JsonObject();

      for (Module var5 : manager.getList()) {
         JsonObject var6 = new JsonObject();
         var6.addProperty("enabled", var5.isEnabled3());
         var6.addProperty("key", var5.getInt());
         JsonObject var7 = new JsonObject();

         for (Setting var9 : var5.getList()) {
            encodeSetting(var7, var9);
         }

         var6.add("settings", var7);
         var3.add(var5.getString(), var6);
      }

      var2.add("modules", var3);
      return var2;
   }

   private static void encodeSetting(JsonObject out, Setting setting) {
      if (setting instanceof BooleanSetting var2) {
         out.addProperty(setting.getName(), var2.getValue());
      } else if (setting instanceof NumberSetting var3) {
         out.addProperty(setting.getName(), var3.getValue());
      } else if (setting instanceof ModeSetting var4) {
         out.addProperty(setting.getName(), var4.getMode());
      } else if (setting instanceof StringSetting var5) {
         out.addProperty(setting.getName(), var5.getValue());
      } else if (setting instanceof ColorSetting var6) {
         out.addProperty(setting.getName(), var6.toHex());
      } else if (setting instanceof BindSetting var7) {
         out.addProperty(setting.getName(), var7.getValue());
      } else if (setting instanceof DoubleListSetting var8) {
         JsonArray var9 = new JsonArray();

         for (Double var11 : var8.getValue()) {
            var9.add(var11);
         }

         out.add(setting.getName(), var9);
      }
   }

   private static void applyModule(Module module, JsonObject mObj) {
      JsonElement var2 = mObj.get("settings");
      if (var2 != null && var2.isJsonObject()) {
         JsonObject var3 = var2.getAsJsonObject();

         for (Setting var5 : module.getList()) {
            JsonElement var6 = var3.get(var5.getName());
            if (var6 != null && !var6.isJsonNull()) {
               applySetting(var5, var6);
            }
         }
      }

      JsonElement var7 = mObj.get("key");
      if (var7 != null && var7.isJsonPrimitive() && var7.getAsJsonPrimitive().isNumber()) {
         module.run7(var7.getAsInt());
      }

      JsonElement var8 = mObj.get("enabled");
      if (var8 != null && var8.isJsonPrimitive() && var8.getAsJsonPrimitive().isBoolean()) {
         module.run5(var8.getAsBoolean());
      }
   }

   private static void applySetting(Setting var0, JsonElement var1) {
      try {
         if (var0 instanceof BooleanSetting var2) {
            if (var1.isJsonPrimitive() && var1.getAsJsonPrimitive().isBoolean()) {
               var2.setValue(var1.getAsBoolean());
            }
         } else if (var0 instanceof NumberSetting var3) {
            if (var1.isJsonPrimitive() && var1.getAsJsonPrimitive().isNumber()) {
               var3.setValue(var1.getAsDouble());
            }
         } else if (var0 instanceof ModeSetting var4) {
            if (var1.isJsonPrimitive() && var1.getAsJsonPrimitive().isString()) {
               var4.setMode(var1.getAsString());
            }
         } else if (var0 instanceof StringSetting var5) {
            if (var1.isJsonPrimitive() && var1.getAsJsonPrimitive().isString()) {
               var5.setValue(var1.getAsString());
            }
         } else if (var0 instanceof ColorSetting var6) {
            if (var1.isJsonPrimitive() && var1.getAsJsonPrimitive().isString()) {
               var6.fromHex(var1.getAsString());
            }
         } else if (var0 instanceof BindSetting var7) {
            if (var1.isJsonPrimitive() && var1.getAsJsonPrimitive().isNumber()) {
               var7.setValue(var1.getAsInt());
            }
         } else if (var0 instanceof DoubleListSetting var8 && var1.isJsonArray()) {
            ArrayList var9 = new ArrayList();

            for (JsonElement var11 : var1.getAsJsonArray()) {
               if (var11 != null && var11.isJsonPrimitive() && var11.getAsJsonPrimitive().isNumber()) {
                  var9.add(var11.getAsDouble());
               }
            }

            var8.setValues(var9);
         }
      } catch (RuntimeException var12) {
      }
   }

   private static Path dir() throws IOException {
      Path var0 = FabricLoader.getInstance().getConfigDir().resolve("codeengine").resolve("configs");
      Files.createDirectories(var0);
      return var0;
   }

   private static Path file(String var0) {
      try {
         return dir().resolve("null.json");
      } catch (IOException var2) {
         return FabricLoader.getInstance().getConfigDir().resolve("codeengine").resolve("configs").resolve("null.json");
      }
   }

   private static boolean writeAtomic(Path file, JsonObject root) {
      Path var2 = file.resolveSibling(file.getFileName().toString() + ".tmp");
      String var3 = GSON.toJson(root);

      try {
         Files.createDirectories(file.getParent());
         Files.writeString(var2, var3, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.SYNC);

         try {
            Files.move(var2, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException var7) {
            Files.move(var2, file, StandardCopyOption.REPLACE_EXISTING);
         }

         return true;
      } catch (IOException var8) {
         System.err.println("[ConfigStore] save failed for " + file + ": " + var8.getMessage());

         try {
            Files.deleteIfExists(var2);
         } catch (IOException var6) {
         }

         return false;
      }
   }
}

