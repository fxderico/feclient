package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.NyxClient;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.network.ServerInfo;

public class RPCModule extends Module {
   private final StringSetting appId = new StringSetting("AppID", "1234567890123456789", 64);
   private final BooleanSetting showServer = new BooleanSetting("ShowServer", true);
   private final BooleanSetting showWorld = new BooleanSetting("ShowWorld", true);
   private final BooleanSetting showElapsed = new BooleanSetting("ShowElapsed", true);
   private Object object;
   private Constructor<?> constructor;
   private Method method;
   private Method method2;
   private Method method3;
   private long longVal = 0L;
   private int intVal = 0;
   private static final int intVal2 = 300;

   public RPCModule() {
      super("RPC", "Discord Rich Presence for Code Engine", Category.CLIENT);
      this.run6(new Setting[]{this.appId, this.showServer, this.showWorld, this.showElapsed});
   }

   @Override
   public void run() {
      Object var1;
      Constructor var2;
      Method var3;
      Method var4;
      Method var5;
      Object var6;
      Method var7;
      try {
         Class var8 = Class.forName("club.minnced.discord.rpc.DiscordRPC");
         Field var9 = var8.getField("INSTANCE");
         var1 = var9.get(null);
         if (var1 == null) {
            NyxClient.LOGGER.warn("[RPC] Discord library present but INSTANCE is null; disabling");
            this.run5(false);
            return;
         }

         Class var10 = Class.forName("club.minnced.discord.rpc.DiscordEventHandlers");
         var6 = var10.getDeclaredConstructor().newInstance();
         Class var11 = Class.forName("club.minnced.discord.rpc.DiscordRichPresence");
         var2 = var11.getDeclaredConstructor();
         var7 = var8.getMethod("Discord_Initialize", String.class, var10, boolean.class, String.class);
         var3 = var8.getMethod("Discord_UpdatePresence", var11);
         var4 = var8.getMethod("Discord_Shutdown");
         var5 = var8.getMethod("Discord_RunCallbacks");
      } catch (ClassNotFoundException var14) {
         NyxClient.LOGGER.warn("[RPC] Discord library not present — install via mods to enable");
         this.run5(false);
         return;
      } catch (Throwable var15) {
         NyxClient.LOGGER.warn("[RPC] Discord SDK layout unexpected, disabling: {}", var15.toString());
         this.run5(false);
         return;
      }

      boolean var16 = false;

      try {
         var7.invoke(var1, this.appId.getValue(), var6, true, "");
         var16 = true;
         this.object = var1;
         this.constructor = var2;
         this.method = var3;
         this.method2 = var4;
         this.method3 = var5;
         this.longVal = System.currentTimeMillis() / 1000L;
         this.intVal = 0;
         this.run4();
         NyxClient.LOGGER.info("[RPC] initialised with app id {}", this.appId.getValue());
      } catch (Throwable var13) {
         NyxClient.LOGGER.warn("[RPC] init failed: {}", var13.toString());
         if (var16) {
            try {
               var4.invoke(var1);
            } catch (Throwable var12) {
            }
         }

         this.run7();
         this.run5(false);
      }
   }

   @Override
   public void run2() {
      Object var1 = this.object;
      Method var2 = this.method2;
      this.run7();
      if (var1 != null && var2 != null) {
         try {
            var2.invoke(var1);
         } catch (Throwable var4) {
            NyxClient.LOGGER.warn("[RPC] shutdown failed: {}", var4.toString());
         }
      }
   }

   @Override
   public void run3() {
      if (this.object != null) {
         if (this.method3 != null) {
            try {
               this.method3.invoke(this.object);
            } catch (Throwable var2) {
            }
         }

         if (++this.intVal >= 300) {
            this.intVal = 0;
            this.run4();
         }
      }
   }

   private void run4() {
      if (this.object != null && this.constructor != null && this.method != null) {
         try {
            Object var1 = this.constructor.newInstance();
            run5(var1, "details", this.getString());
            run5(var1, "state", this.getString2());
            run6(var1, "startTimestamp", this.showElapsed.getValue() ? this.longVal : 0L);
            run5(var1, "largeImageKey", "codeengine");
            run5(var1, "largeImageText", "Code Engine 1.21.11");
            this.method.invoke(this.object, var1);
         } catch (Throwable var2) {
            NyxClient.LOGGER.warn("[RPC] presence update failed: {}", var2.toString());
         }
      }
   }

   public String getString() {
      if (this.showServer.getValue()) {
         try {
            ServerInfo var1 = class310.getCurrentServerEntry();
            if (var1 != null && var1.address != null && !var1.address.isEmpty()) {
               return "Playing on " + var1.address;
            }

            if (class310.isIntegratedServerRunning()) {
               return "Singleplayer";
            }
         } catch (Throwable var2) {
         }
      }

      return "In menu";
   }

   public String getString2() {
      StringBuilder var1 = new StringBuilder(48);

      try {
         if (this.showWorld.getValue() && class310.world != null) {
            var1.append(class310.world.getRegistryKey().getValue().getPath());
         }
      } catch (Throwable var5) {
      }

      int var2 = NyxClient.MODULES == null ? 0 : NyxClient.MODULES.getList2().size();
      if (var1.length() > 0) {
         var1.append(" | ");
      }

      var1.append(var2).append(" modules");

      try {
         if (class310.getNetworkHandler() != null) {
            int var3 = class310.getNetworkHandler().getPlayerList().size();
            var1.append(" | ").append(var3).append(" players");
         }
      } catch (Throwable var4) {
      }

      return var1.toString();
   }

   private static void run5(Object var0, String var1, String var2) {
      try {
         Field var3 = var0.getClass().getField(var1);
         var3.set(var0, var2 == null ? "" : var2);
      } catch (Throwable var4) {
      }
   }

   private static void run6(Object var0, String var1, long var2) {
      try {
         Field var4 = var0.getClass().getField(var1);
         var4.setLong(var0, var2);
      } catch (Throwable var5) {
      }
   }

   private void run7() {
      this.object = null;
      this.constructor = null;
      this.method = null;
      this.method2 = null;
      this.method3 = null;
   }
}

