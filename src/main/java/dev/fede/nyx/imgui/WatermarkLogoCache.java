package dev.fede.nyx.imgui;

import java.io.InputStream;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public final class WatermarkLogoCache {
   public static final int NONE = 0;
   private static final Identifier RESOURCE_ID = Identifier.of("codeengine", "textures/hud/watermark_logo.png");
   private static final Identifier TEX_ID = Identifier.of("codeengine", "hud/watermark_logo");
   private static NativeImageBackedTexture currentTex;
   private static int currentW = 0;
   private static int currentH = 0;
   private static boolean loadAttempted = false;

   private WatermarkLogoCache() {
   }

   public static int width() {
      return currentW;
   }

   public static int height() {
      return currentH;
   }

   public static int glId() {
      if (currentTex == null && !loadAttempted) {
         loadAttempted = true;
         tryLoad();
      }

      return currentGlId();
   }

   public static void clear() {
      if (currentTex != null) {
         try {
            currentTex.close();
         } catch (Throwable var1) {
         }

         currentTex = null;
      }

      currentW = 0;
      currentH = 0;
      loadAttempted = false;
   }

   private static void tryLoad() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null && var0.getResourceManager() != null && var0.getTextureManager() != null) {
         try {
            Optional var1 = var0.getResourceManager().getResource(RESOURCE_ID);
            if (var1.isEmpty()) {
               System.err.println("[c] WatermarkLogoCache: resource missing " + RESOURCE_ID);
               return;
            }

            try (InputStream var2 = ((Resource)var1.get()).getInputStream()) {
               NativeImage var3 = NativeImage.read(var2);
               currentW = var3.getWidth();
               currentH = var3.getHeight();
               NativeImageBackedTexture var4 = new NativeImageBackedTexture(() -> "codeengine-watermark", var3);
               var0.getTextureManager().registerTexture(TEX_ID, var4);
               currentTex = var4;
            }
         } catch (Throwable var7) {
            System.err.println("[c] WatermarkLogoCache: load failed: null");
         }
      }
   }

   private static int currentGlId() {
      if (currentTex == null) {
         return 0;
      } else {
         try {
            if (currentTex.getGlTexture() instanceof GlTexture var1) {
               int var2 = var1.getGlId();
               return var2 > 0 ? var2 : 0;
            } else {
               return 0;
            }
         } catch (Throwable var3) {
            return 0;
         }
      }
   }
}

