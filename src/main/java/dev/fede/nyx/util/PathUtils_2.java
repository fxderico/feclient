package dev.fede.nyx.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
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
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public final class PathUtils_2 {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final String DIR = "codeengine";

   private PathUtils_2() {
   }

   public static CompletableFuture<Void> save(MinecraftClient var0, Map<Long, PathUtils_2.Inner1> var1) {
      if (var0 != null && var1 != null) {
         String var2 = PathUtils_2_3.serverKey(var0);
         ArrayList var3 = new ArrayList(var1.values());
         return CompletableFuture.runAsync(() -> writeAtomic(var2, var3));
      } else {
         return CompletableFuture.completedFuture(null);
      }
   }

   public static Map<Long, PathUtils_2.Inner1> load(MinecraftClient var0) {
      ConcurrentHashMap var1 = new ConcurrentHashMap();
      if (var0 == null) {
         return var1;
      } else {
         Path var2 = file(PathUtils_2_3.serverKey(var0));
         if (!Files.exists(var2)) {
            return var1;
         } else {
            try {
               String var3 = Files.readString(var2);
               PathUtils_2.Inner2 var9 = (PathUtils_2.Inner2)GSON.fromJson(var3, PathUtils_2.Inner2.class);
               if (var9 != null && var9.entries != null) {
                  for (PathUtils_2.Inner1 var6 : var9.entries) {
                     if (var6 != null && var6.count > 0) {
                        var1.put(chunkKey(var6.intVal, var6.cz), var6);
                     }
                  }

                  return var1;
               } else {
                  return var1;
               }
            } catch (RuntimeException | IOException var8) {
               try {
                  Path var4 = var2.resolveSibling(var2.getFileName().toString() + ".corrupt-" + System.currentTimeMillis());
                  Files.move(var2, var4, StandardCopyOption.REPLACE_EXISTING);
               } catch (IOException var7) {
                  System.err.println("[HeatMapPersistence] failed to quarantine corrupt file " + var2 + ": " + var7.getMessage());
               }

               System.err.println("[HeatMapPersistence] corrupt store " + var2 + " renamed; starting empty. Cause: " + var8.getMessage());
               return new ConcurrentHashMap<>();
            }
         }
      }
   }

   public static long chunkKey(int cx, int cz) {
      return cx & 4294967295L | (cz & 4294967295L) << 32;
   }

   private static Path dir() throws IOException {
      Path var0 = FabricLoader.getInstance().getConfigDir();
      Files.createDirectories(var0);
      return var0;
   }

   private static Path file(String var0) {
      try {
         return dir().resolve("heatmap-null.json");
      } catch (IOException var2) {
         return FabricLoader.getInstance().getConfigDir().resolve("heatmap-null.json");
      }
   }

   private static void writeAtomic(String var0, List<PathUtils_2.Inner1> var1) {
      Path var2 = file(var0);
      Path var3 = var2.resolveSibling(var2.getFileName().toString() + ".tmp");
      PathUtils_2.Inner2 var4 = new PathUtils_2.Inner2();
      var4.entries = var1;
      String var5 = GSON.toJson(var4);

      try {
         Files.writeString(var3, var5, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.SYNC);

         try {
            Files.move(var3, var2, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException var9) {
            Files.move(var3, var2, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var10) {
         System.err.println("[HeatMapPersistence] save failed for " + var2 + ": " + var10.getMessage());

         try {
            Files.deleteIfExists(var3);
         } catch (IOException var8) {
         }
      }
   }

   public static LinkedHashMap<Long, PathUtils_2.Inner1> loadAsLinkedHashMap(MinecraftClient var0) {
      return new LinkedHashMap<>(load(var0));
   }

public final static class Inner1 {
   public int intVal;
   public int cz;
   public int count;
   public long lastVisitMs;

   public Inner1() {
   }

   public Inner1(int var1, int var2, int var3, long var4) {
      this.intVal = var1;
      this.cz = var2;
      this.count = var3;
      this.lastVisitMs = var4;
   }
}

final static class Inner2 {
   List<PathUtils_2.Inner1> entries;

   private Inner2() {
   }
}
}

