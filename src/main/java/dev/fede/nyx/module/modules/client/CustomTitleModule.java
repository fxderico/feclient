package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.lang.reflect.Method;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import org.lwjgl.glfw.GLFW;

public class CustomTitleModule extends Module {
   private final StringSetting template = new StringSetting("Template", "Code Engine {fps} FPS - {server}", 256);
   private final NumberSetting refreshMs = new NumberSetting("RefreshEveryMs", 1000.0, 100.0, 10000.0, 100.0);
   private long longVal = 0L;

   public CustomTitleModule() {
      super("CustomTitle", "Overrides the Minecraft window title", Category.CLIENT);
      this.run6(new Setting[]{this.template, this.refreshMs});
   }

   @Override
   public void run() {
      this.longVal = 0L;
   }

   @Override
   public void run2() {
      this.run4();
      this.longVal = 0L;
   }

   @Override
   public void run3() {
      long var1 = System.currentTimeMillis();
      long var3 = var1 - this.longVal;
      if (this.longVal == 0L || var3 < 0L || var3 >= this.refreshMs.getValueLong()) {
         this.longVal = var1;

         try {
            if (class310 == null || class310.getWindow() == null) {
               return;
            }

            long var5 = class310.getWindow().getHandle();
            if (var5 == 0L) {
               return;
            }

            GLFW.glfwSetWindowTitle(var5, this.addSetting(this.template.getValue()));
         } catch (Throwable var7) {
            NyxClient.LOGGER.warn("[CustomTitle] set failed: {}", var7.toString());
         }
      }
   }

   private String addSetting(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         String var2 = var1;
         if (var1.contains("{fps}")) {
            var2 = var1.replace("{fps}", Integer.toString(this.getInt()));
         }

         if (var2.contains("{server}")) {
            var2 = var2.replace("{server}", this.getString());
         }

         if (var2.contains("{world}")) {
            var2 = var2.replace("{world}", this.getString2());
         }

         if (var2.contains("{player}")) {
            var2 = var2.replace("{player}", this.getString3());
         }

         if (var2.contains("{ping}")) {
            var2 = var2.replace("{ping}", Integer.toString(this.getInt2()));
         }

         if (var2.contains("{x}")) {
            var2 = var2.replace("{x}", this.stringOf(0));
         }

         if (var2.contains("{y}")) {
            var2 = var2.replace("{y}", this.stringOf(1));
         }

         if (var2.contains("{z}")) {
            var2 = var2.replace("{z}", this.stringOf(2));
         }

         return var2;
      } else {
         return "Minecraft";
      }
   }

   public int getInt() {
      try {
         return class310.getCurrentFps();
      } catch (Throwable var2) {
         return 0;
      }
   }

   public String getString() {
      try {
         ServerInfo var1 = class310.getCurrentServerEntry();
         if (var1 != null && var1.address != null && !var1.address.isEmpty()) {
            return var1.address;
         }

         if (class310.isIntegratedServerRunning()) {
            return "Singleplayer";
         }
      } catch (Throwable var2) {
      }

      return "-";
   }

   public String getString2() {
      try {
         if (class310.world != null) {
            return class310.world.getRegistryKey().getValue().getPath();
         }
      } catch (Throwable var2) {
      }

      return "-";
   }

   public String getString3() {
      try {
         if (class310.player != null) {
            return class310.player.getName().getString();
         }

         if (class310.getGameProfile() != null) {
            return class310.getGameProfile().name();
         }
      } catch (Throwable var2) {
      }

      return "-";
   }

   private String stringOf(int var1) {
      try {
         if (class310.player == null) {
            return "-";
         } else {
            double var2 = switch (var1) {
               case 0 -> class310.player.getX();
               case 1 -> class310.player.getY();
               default -> class310.player.getZ();
            };
            return String.format("%.1f", var2);
         }
      } catch (Throwable var4) {
         return "-";
      }
   }

   private int getInt2() {
      try {
         if (class310.player != null && class310.getNetworkHandler() != null) {
            PlayerListEntry var1 = class310.getNetworkHandler().getPlayerListEntry(class310.player.getUuid());
            if (var1 != null) {
               return var1.getLatency();
            }
         }
      } catch (Throwable var2) {
      }

      return 0;
   }

   private void run4() {
      try {
         if (class310 == null) {
            return;
         }

         try {
            Method var5 = class310.getClass().getDeclaredMethod("updateWindowTitle");
            var5.setAccessible(true);
            var5.invoke(class310);
            return;
         } catch (NoSuchMethodException var3) {
            if (class310.getWindow() != null) {
               long var1 = class310.getWindow().getHandle();
               if (var1 != 0L) {
                  GLFW.glfwSetWindowTitle(var1, "Minecraft 1.21.11");
               }
            }
         }
      } catch (Throwable var4) {
         NyxClient.LOGGER.warn("[CustomTitle] restore failed: {}", var4.toString());
      }
   }
}

