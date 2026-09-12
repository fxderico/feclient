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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;

public final class ChatFilterHelper {
   public static final ChatFilterHelper chatFilterHelper = new ChatFilterHelper();
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final String string = "codeengine";
   private static final String string2 = "nyx";
   private static final Pattern pattern = Pattern.compile("[^A-Za-z0-9._-]");
   private static final String string3 = "friends.json";
   private final Set<String> set = ConcurrentHashMap.newKeySet();

   private ChatFilterHelper() {
   }

   public boolean check(String var1) {
      String var2 = addSetting(var1);
      return var2 == null ? false : this.set.add(var2);
   }

   public boolean check2(String var1) {
      String var2 = addSetting(var1);
      return var2 == null ? false : this.set.remove(var2);
   }

   public boolean check3(String var1) {
      String var2 = addSetting(var1);
      return var2 == null ? false : this.set.contains(var2);
   }

   public Set<String> getSet() {
      return Collections.unmodifiableSet(new LinkedHashSet<>(this.set));
   }

   public void run2() {
      this.set.clear();
   }

   public CompletableFuture<Void> completableFutureOf(String var1) {
      ArrayList var2 = new ArrayList<>(this.set);
      Collections.sort(var2);
      String var3 = addSetting2(var1);
      return CompletableFuture.runAsync((java.lang.Runnable)() -> {});
   }

   public void run(String var1) {
      String var2 = addSetting2(var1);
      Path var3 = pathOf(var2);
      if (!Files.exists(var3)) {
         Path var4 = pathOf2(var2);
         if (!Files.exists(var4)) {
            return;
         }

         var3 = var4;
      }

      try {
         String var10 = Files.readString(var3);
         InternalData2 var5 = (InternalData2)gson.fromJson(var10, InternalData2.class);
         if (var5 == null || var5.list == null) {
            return;
         }

         for (String var7 : var5.list) {
            String var8 = addSetting(var7);
            if (var8 != null) {
               this.set.add(var8);
            }
         }
      } catch (RuntimeException | IOException var9) {
         run4(var3, var9.getMessage());
      }
   }

   private static String addSetting(String var0) {
      if (var0 == null) {
         return null;
      } else {
         String var1 = var0.trim();
         return var1.isEmpty() ? null : var1.toLowerCase(Locale.ROOT);
      }
   }

   private static String addSetting2(String var0) {
      if (var0 == null) {
         return "friends.json";
      } else {
         String var1 = var0.trim();
         if (var1.isEmpty()) {
            return "friends.json";
         } else {
            String var2 = pattern.matcher(var1).replaceAll("_");
            return var2.chars().allMatch(ChatFilterHelper::check4) ? "friends.json" : var2;
         }
      }
   }

   private static Path getPath() throws IOException {
      Path var0 = FabricLoader.getInstance().getConfigDir().resolve("codeengine");
      Files.createDirectories(var0);
      return var0;
   }

   private static Path pathOf(String var0) {
      try {
         return getPath().resolve(var0);
      } catch (IOException var2) {
         return FabricLoader.getInstance().getConfigDir().resolve("codeengine").resolve(var0);
      }
   }

   private static Path pathOf2(String var0) {
      return FabricLoader.getInstance().getConfigDir().resolve("nyx").resolve(var0);
   }

   private static void run3(String var0, List<String> var1) {
      Path var2 = pathOf(var0);
      Path var3 = var2.resolveSibling(var2.getFileName().toString() + ".tmp");
      InternalData2 var4 = InternalData2.getINSTANCE();
      var4.list = var1;
      String var5 = gson.toJson(var4);

      try {
         Files.writeString(var3, var5, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.SYNC);

         try {
            Files.move(var3, var2, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException var9) {
            Files.move(var3, var2, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (IOException var10) {
         System.err.println("[FriendList] save failed for " + var2 + ": " + var10.getMessage());

         try {
            Files.deleteIfExists(var3);
         } catch (IOException var8) {
         }
      }
   }

   private static void run4(Path var0, String var1) {
      try {
         Path var2 = var0.resolveSibling(var0.getFileName().toString() + ".corrupt-" + System.currentTimeMillis());
         Files.move(var0, var2, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException var3) {
         System.err.println("[FriendList] failed to quarantine corrupt file " + var0 + ": " + var3.getMessage());
      }

      System.err.println("[FriendList] corrupt store null renamed; starting empty. Cause: null");
   }

   private static boolean check4(int var0) {
      return var0 == 46;
   }

   private static void run5(String var0, List var1) {
      run3(var0, var1);
   }
}

