package dev.fede.nyx.imgui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.util.Identifier;

public final class ImGuiSkinCache {
   public static final int INVALID = -1;
   private static final Map<Identifier, Integer> CACHE = new HashMap<>();
   private static final long FLUSH_INTERVAL_MS = 20000L;
   private static long lastFlushMs = 0L;

   private ImGuiSkinCache() {
   }

   public static int glHandleForSkin(Identifier var0) {
      if (var0 == null) {
         return -1;
      } else {
         Integer var1 = CACHE.get(var0);
         if (var1 != null && var1 > 0) {
            maybeFlush();
            return var1;
         } else {
            MinecraftClient var2 = MinecraftClient.getInstance();
            if (var2 != null && var2.getTextureManager() != null) {
               AbstractTexture var3 = var2.getTextureManager().getTexture(var0);
               if (var3 == null) {
                  return -1;
               } else if (var3.getGlTexture() instanceof GlTexture var5) {
                  int var6 = var5.getGlId();
                  if (var6 <= 0) {
                     return -1;
                  } else {
                     CACHE.put(var0, var6);
                     maybeFlush();
                     return var6;
                  }
               } else {
                  return -1;
               }
            } else {
               return -1;
            }
         }
      }
   }

   public static void invalidate(Identifier var0) {
      if (var0 != null) {
         CACHE.remove(var0);
      }
   }

   public static void clear() {
      CACHE.clear();
   }

   private static void maybeFlush() {
      long var0 = System.currentTimeMillis();
      if (var0 - lastFlushMs >= 20000L) {
         lastFlushMs = var0;
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 != null && var2.getTextureManager() != null) {
            Iterator var3 = CACHE.entrySet().iterator();

            while (var3.hasNext()) {
               Entry var4 = (Entry)var3.next();
               AbstractTexture var5 = var2.getTextureManager().getTexture((Identifier)var4.getKey());
               if (var5 == null || !(var5.getGlTexture() instanceof GlTexture var6 && var6.getGlId() > 0)) {
                  var3.remove();
               }
            }
         }
      }
   }
}

