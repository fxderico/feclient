package dev.fede.nyx.internal;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public final class InternalUtil {
   public static final Identifier class2960 = Identifier.of("codeengine", "textures/hud/stripes.png");
   public static final int intVal = 32;
   private static volatile boolean bool = false;

   private InternalUtil() {
   }

   public static void run() {
      if (!bool) {
         synchronized (InternalUtil.class) {
            if (!bool) {
               MinecraftClient var1 = MinecraftClient.getInstance();
               if (var1 != null) {
                  TextureManager var2 = var1.getTextureManager();
                  if (var2 != null) {
                     NativeImage var3 = new NativeImage(32, 32, false);

                     for (int var4 = 0; var4 < 32; var4++) {
                        for (int var5 = 0; var5 < 32; var5++) {
                           int var6 = (var5 + var4 & 7) < 4 ? 100 : 0;
                           int var7 = var6 << 24 | 16777215;
                           var3.setColorArgb(var5, var4, var7);
                        }
                     }

                     NativeImageBackedTexture var10 = new NativeImageBackedTexture(InternalUtil::getString, var3);
                     var2.registerTexture(class2960, var10);
                     bool = true;
                  }
               }
            }
         }
      }
   }

   private static String getString() {
      return "codeengine:stripes";
   }
}

