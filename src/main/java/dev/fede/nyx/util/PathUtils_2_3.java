package dev.fede.nyx.util;

import dev.fede.nyx.storage.ContainerSnapshotMixinEntry;
import net.minecraft.util.math.Vec3d;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.math.BlockPos;

public final class PathUtils_2_3 {
   private static final Pattern SAFE = Pattern.compile("[^A-Za-z0-9._-]");
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final String DIR = "codeengine";
   private static final String LEGACY_DIR = "nyx";

   private PathUtils_2_3() {
   }

   public static CompletableFuture<Void> save(MinecraftClient var0, Map<Long, PathUtils_2_3.Inner1> var1) {
      if (var0 != null && var1 != null) {
         String var2 = serverKey(var0);
         ArrayList var3 = new ArrayList(var1.values());
         return CompletableFuture.runAsync(() -> writeAtomic(var2, var3));
      } else {
         return CompletableFuture.completedFuture(null);
      }
   }

   public static Map<Long, PathUtils_2_3.Inner1> load(MinecraftClient var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      if (var0 == null) {
         return var1;
      } else {
         Path var2 = file(serverKey(var0));
         if (!Files.exists(var2)) {
            Path var3 = legacyFile(serverKey(var0));
            if (!Files.exists(var3)) {
               return var1;
            }

            var2 = var3;
         }

         try {
            String var9 = Files.readString(var2);
            PathUtils_2_3.Inner2 var10 = (PathUtils_2_3.Inner2)GSON.fromJson(var9, PathUtils_2_3.Inner2.class);
            if (var10 != null && var10.entries != null) {
               for (PathUtils_2_3.Inner1 var6 : var10.entries) {
                  if (var6 != null && var6.kind != null) {
                     var1.put(BlockPos.asLong(var6.intVal, var6.intVal2, var6.intVal3), var6);
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
               System.err.println("[Persistence] failed to quarantine corrupt file " + var2 + ": " + var7.getMessage());
            }

            System.err.println("[Persistence] corrupt store " + var2 + " renamed; starting empty. Cause: " + var8.getMessage());
            return new LinkedHashMap<>();
         }
      }
   }

   public static String serverKey(MinecraftClient var0) {
      String var1;
      if (var0.isIntegratedServerRunning()) {
         var1 = "singleplayer";
      } else {
         ServerInfo var2 = var0.getCurrentServerEntry();
         var1 = var2 != null && var2.address != null && !var2.address.isEmpty() ? var2.address : "unknown";
      }

      String var3;
      if (var0.world != null) {
         var3 = var0.world.getRegistryKey().getValue().toString();
      } else {
         var3 = "unknown";
      }

      return SAFE.matcher(var1).replaceAll("_") + "__" + SAFE.matcher(var3).replaceAll("_");
   }

   private static Path dir() throws IOException {
      Path var0 = FabricLoader.getInstance().getConfigDir();
      Files.createDirectories(var0);
      return var0;
   }

   private static Path file(String var0) {
      try {
         return dir().resolve("chests-null.json");
      } catch (IOException var2) {
         return FabricLoader.getInstance().getConfigDir().resolve("chests-null.json");
      }
   }

   private static Path legacyFile(String var0) {
      return FabricLoader.getInstance().getConfigDir().resolve("nyx").resolve("chests-null.json");
   }

   private static void writeAtomic(String var0, List<PathUtils_2_3.Inner1> var1) {
      Path var2 = file(var0);
      Path var3 = var2.resolveSibling(var2.getFileName().toString() + ".tmp");
      PathUtils_2_3.Inner2 var4 = new PathUtils_2_3.Inner2();
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
         System.err.println("[Persistence] save failed for " + var2 + ": " + var10.getMessage());

         try {
            Files.deleteIfExists(var3);
         } catch (IOException var8) {
         }
      }
   }

   public static HashMap<Long, PathUtils_2_3.Inner1> loadAsHashMap(MinecraftClient var0) {
      return new HashMap<>(load(var0));
   }

public final static class Inner1 {
   public int intVal;
   public int intVal2;
   public int intVal3;
   public String kind;
   public long lastSeenMs;
   public List<ContainerSnapshotMixinEntry> snapshot;
   public long snapshotAtMs;

   public Inner1() {
   }

   public Inner1(int var1, int var2, int var3, String var4, long var5) {
      this.intVal = var1;
      this.intVal2 = var2;
      this.intVal3 = var3;
      this.kind = var4;
      this.lastSeenMs = var5;
   }

   public Inner1(int var1, int var2, int var3, String var4, long var5, List<ContainerSnapshotMixinEntry> var7, long var8) {
      this(var1, var2, var3, var4, var5);
      this.snapshot = var7;
      this.snapshotAtMs = var8;
   }

   public BlockPos pos() {
      return new BlockPos(this.intVal, this.intVal2, this.intVal3);
   }

   public Vec3d center() {
      return Vec3d.ofCenter(this.pos());
   }
}

final static class Inner2 {
   List<PathUtils_2_3.Inner1> entries;

   private Inner2() {
   }
}
}

