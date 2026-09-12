package dev.fede.nyx.imgui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class AlbumArtCache {
   public static final int NONE = 0;
   private static final Identifier ART_ID = Identifier.of("codeengine", "spotifyhud/album_art");
   private static String currentKey = "";
   private static NativeImageBackedTexture currentTex = null;
   private static String lastLoggedFailure = "";

   private AlbumArtCache() {
   }

   public static int glHandleFor(String artPath, String artKey) {
      if (artPath != null && !artPath.isEmpty() && artKey != null && !artKey.isEmpty()) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 != null && var2.getTextureManager() != null) {
            if (!artKey.equals(currentKey)) {
               NativeImageBackedTexture var3 = loadFromDisk(artPath);
               if (var3 == null) {
                  return currentGlId();
               }

               var2.getTextureManager().registerTexture(ART_ID, var3);
               if (currentTex != null && currentTex != var3) {
                  try {
                     currentTex.close();
                  } catch (Throwable var5) {
                  }
               }

               currentTex = var3;
               currentKey = artKey;
            }

            return currentGlId();
         } else {
            return 0;
         }
      } else {
         clear();
         return 0;
      }
   }

   public static void clear() {
      if (currentTex != null) {
         try {
            currentTex.close();
         } catch (Throwable var1) {
         }

         currentTex = null;
      }

      currentKey = "";
   }

   private static int currentGlId() {
      if (currentTex == null) {
         return 0;
      } else if (currentTex.getGlTexture() instanceof GlTexture var1) {
         int var2 = var1.getGlId();
         return var2 > 0 ? var2 : 0;
      } else {
         return 0;
      }
   }

   private static NativeImageBackedTexture loadFromDisk(String var0) {
      Path var1 = Path.of(var0);
      if (!Files.exists(var1)) {
         return null;
      } else {
         try {
            NativeImageBackedTexture var6;
            try (InputStream var2 = Files.newInputStream(var1)) {
               NativeImage var10 = NativeImage.read(var2);
               NativeImage var4 = cropToOpaque(var10);
               if (var4 != var10) {
                  var10.close();
               }

               String label = null;
               var6 = new NativeImageBackedTexture(() -> label, var4);
            }

            return var6;
         } catch (RuntimeException | IOException var9) {
            String var3 = var9.getClass().getSimpleName().toLowerCase(Locale.ROOT);
            if (!var3.equals(lastLoggedFailure)) {
               lastLoggedFailure = var3;
               System.err.println("[c] AlbumArtCache: decode failed: " + var9.getClass().getSimpleName() + ": " + var9.getMessage());
            }

            return null;
         }
      }
   }

   private static NativeImage cropToOpaque(NativeImage var0) {
      int var1 = var0.getWidth();
      int var2 = var0.getHeight();
      int var3 = var1;
      int var4 = var2;
      int var5 = -1;
      int var6 = -1;

      for (int var7 = 0; var7 < var2; var7++) {
         for (int var8 = 0; var8 < var1; var8++) {
            int var9 = var0.getColorArgb(var8, var7);
            int var10 = var9 >>> 24 & 0xFF;
            if (var10 > 10) {
               if (var8 < var3) {
                  var3 = var8;
               }

               if (var7 < var4) {
                  var4 = var7;
               }

               if (var8 > var5) {
                  var5 = var8;
               }

               if (var7 > var6) {
                  var6 = var7;
               }
            }
         }
      }

      if (var5 < 0) {
         return var0;
      } else if (var3 == 0 && var4 == 0 && var5 == var1 - 1 && var6 == var2 - 1) {
         return var0;
      } else {
         int var13 = var5 - var3 + 1;
         int var14 = var6 - var4 + 1;
         int var15;
         if (var14 > var13) {
            var15 = var13;
         } else if (var13 > var14) {
            var15 = var14;
         } else {
            var15 = var13;
         }

         NativeImage var16 = new NativeImage(var15, var15, true);

         for (int var11 = 0; var11 < var15; var11++) {
            for (int var12 = 0; var12 < var15; var12++) {
               var16.setColorArgb(var12, var11, var0.getColorArgb(var3 + var12, var4 + var11));
            }
         }

         return var16;
      }
   }
}

