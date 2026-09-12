package dev.fede.nyx.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.fede.nyx.NyxClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public final class PathUtils_2_3_4 {
   private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
   private static final String DIR = "codeengine";
   private static final String FILE_PREFIX = "sus-chunks-";
   private static final String FILE_SUFFIX = ".json";
   public static final int CURRENT_SCHEMA_VERSION = 6;

   private static Path dir() {
      return FabricLoader.getInstance().getConfigDir().resolve("codeengine");
   }

   private static String sanitizeDim(String dim) {
      if (dim != null && !dim.isEmpty()) {
         StringBuilder var1 = new StringBuilder(dim.length());

         for (int var2 = 0; var2 < dim.length(); var2++) {
            char var3 = dim.charAt(var2);
            if (var3 != ':' && var3 != '/' && var3 != '\\' && var3 != ' ') {
               var1.append(var3);
            } else {
               var1.append('_');
            }
         }

         return var1.toString();
      } else {
         return "unknown";
      }
   }

   private PathUtils_2_3_4() {
   }

   private static Path file(String serverKey, String dim) {
      return dir().resolve("sus-chunks-" + serverKey + "-" + sanitizeDim(dim) + ".json");
   }

   public static CompletableFuture<Void> save(MinecraftClient var0, List<PathUtils_2_3_4.Inner1> var1, String var2) {
      if (var0 != null && var1 != null && !var1.isEmpty()) {
         String var3 = PathUtils_2_3.serverKey(var0);
         Path var4 = file(var3, var2);
         ArrayList var5 = new ArrayList(var1);
         return CompletableFuture.runAsync(() -> {
            try {
               Files.createDirectories(var4.getParent());
               String var2x = GSON.toJson(new PathUtils_2_3_4.Inner2(6, var5));
               atomicWrite(var4, var2x);
            } catch (Throwable var3x) {
               NyxClient.LOGGER.warn("[SusChunkPersistence] save failed: {}", var3x.toString());
            }
         });
      } else {
         return CompletableFuture.completedFuture(null);
      }
   }

   public static Map<Long, PathUtils_2_3_4.Inner1> load(MinecraftClient var0, String var1) {
      if (var0 == null) {
         return new LinkedHashMap<>();
      } else {
         Path var2 = file(PathUtils_2_3.serverKey(var0), var1);
         if (!Files.exists(var2)) {
            return new LinkedHashMap<>();
         } else {
            try {
               String var3 = Files.readString(var2, StandardCharsets.UTF_8);
               PathUtils_2_3_4.Inner2 var10 = (PathUtils_2_3_4.Inner2)GSON.fromJson(var3, PathUtils_2_3_4.Inner2.class);
               if (var10 != null && var10.entries != null) {
                  if (var10.version < 6) {
                     Path var11 = var2.resolveSibling(var2.getFileName() + ".legacy-v" + var10.version + "-" + System.currentTimeMillis());
                     Files.move(var2, var11, StandardCopyOption.REPLACE_EXISTING);
                     NyxClient.LOGGER.warn("[SusChunkPersistence] legacy save (v{}) quarantined to {}", var10.version, var11);
                     return new LinkedHashMap<>();
                  } else {
                     LinkedHashMap var5 = new LinkedHashMap(var10.entries.size());

                     for (PathUtils_2_3_4.Inner1 var7 : var10.entries) {
                        if (var7 != null) {
                           var5.put(chunkKey(var7.chunkX, var7.chunkZ), var7);
                        }
                     }

                     return var5;
                  }
               } else {
                  return new LinkedHashMap<>();
               }
            } catch (RuntimeException | IOException var9) {
               try {
                  Path var4 = var2.resolveSibling(var2.getFileName() + ".corrupt-" + System.currentTimeMillis());
                  Files.move(var2, var4, StandardCopyOption.REPLACE_EXISTING);
                  NyxClient.LOGGER.warn("[SusChunkPersistence] corrupt file quarantined: {}", var4);
               } catch (IOException var8) {
               }

               return new LinkedHashMap<>();
            }
         }
      }
   }

   public static long chunkKey(int cx, int cz) {
      return (long)cx << 32 | cz & 4294967295L;
   }

   private static void atomicWrite(Path target, String json) throws IOException {
      Path var2 = target.resolveSibling(target.getFileName() + ".tmp");
      Files.writeString(var2, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.SYNC);

      try {
         Files.move(var2, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
      } catch (AtomicMoveNotSupportedException var4) {
         Files.move(var2, target, StandardCopyOption.REPLACE_EXISTING);
      }
   }

public final static class Inner1 {
   public int chunkX;
   public int chunkZ;
   public int totalScore;
   public int containerScore;
   public int artificialScore;
   public int lightScore;
   public int entityScore;
   public int patternScore;
   public int containerCount;
   public int artificialCount;
   public int lightCount;
   public int entityCount;
   public int patternCount;
   public int surfaceY;
}

final static class Inner2 {
   int version;
   List<PathUtils_2_3_4.Inner1> entries;

   Inner2() {
   }

   Inner2(int var1, List<PathUtils_2_3_4.Inner1> var2) {
      this.version = var1;
      this.entries = var2;
   }
}
}

