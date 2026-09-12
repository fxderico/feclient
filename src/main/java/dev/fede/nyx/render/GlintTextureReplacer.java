package dev.fede.nyx.render;

import java.io.InputStream;
import java.util.Optional;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public final class GlintTextureReplacer {
   private static final Identifier ITEM_ID = Identifier.of("minecraft", "textures/misc/enchanted_glint_item.png");
   private static final Identifier ENTITY_ID = Identifier.of("minecraft", "textures/misc/enchanted_glint_entity.png");
   private static final Identifier RELOAD_LISTENER_ID = Identifier.of("codeengine", "glint_recolor");
   private static NativeImage vanillaItem;
   private static NativeImage vanillaEntity;
   private static NativeImageBackedTexture currentItem;
   private static NativeImageBackedTexture currentEntity;
   private static int color = -1;
   private static float intensity = 1.0F;
   private static boolean enabled = false;

   private GlintTextureReplacer() {
   }

   public static void init() {
      try {
         ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            public Identifier getFabricId() {
               return GlintTextureReplacer.RELOAD_LISTENER_ID;
            }

            public void reload(ResourceManager var1) {
               GlintTextureReplacer.loadVanillaGlints(var1);
               if (GlintTextureReplacer.enabled) {
                  GlintTextureReplacer.applyTintInternal(GlintTextureReplacer.color, GlintTextureReplacer.intensity);
               } else {
                  GlintTextureReplacer.restoreVanillaInternal();
               }
            }
         });
      } catch (Throwable var1) {
         System.err.println("[Glint] reload-listener register failed: null");
      }
   }

   public static void enable(int argb, float intens) {
      enabled = true;
      color = argb;
      intensity = intens;
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null) {
         var2.execute(() -> {
            if (vanillaItem == null || vanillaEntity == null) {
               loadVanillaGlints(var2.getResourceManager());
            }

            applyTintInternal(color, intensity);
         });
      }
   }

   public static void disable() {
      enabled = false;
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null) {
         var0.execute(GlintTextureReplacer::restoreVanillaInternal);
      }
   }

   public static void applyTint(int argb, float intens) {
      color = argb;
      intensity = intens;
      if (enabled) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2 != null) {
            var2.execute(() -> applyTintInternal(color, intensity));
         }
      }
   }

   public static void onColorSettingChanged(int argb) {
      applyTint(argb, intensity);
   }

   public static void loadVanillaGlints(ResourceManager var0) {
      if (var0 != null) {
         NativeImage var1 = readImage(var0, ITEM_ID);
         NativeImage var2 = readImage(var0, ENTITY_ID);
         if (var1 != null) {
            if (vanillaItem != null) {
               try {
                  vanillaItem.close();
               } catch (Throwable var5) {
               }
            }

            vanillaItem = var1;
         }

         if (var2 != null) {
            if (vanillaEntity != null) {
               try {
                  vanillaEntity.close();
               } catch (Throwable var4) {
               }
            }

            vanillaEntity = var2;
         }
      }
   }

   private static NativeImage readImage(ResourceManager var0, Identifier var1) {
      try {
         Optional var2 = var0.getResource(var1);
         if (var2.isEmpty()) {
            System.err.println("[Glint] resource missing: null");
            return null;
         } else {
            NativeImage var4;
            try (InputStream var3 = ((Resource)var2.get()).getInputStream()) {
               var4 = NativeImage.read(var3);
            }

            return var4;
         }
      } catch (Throwable var8) {
         System.err.println("[Glint] read failed for null: null");
         return null;
      }
   }

   private static void applyTintInternal(int argb, float intens) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null && var2.getTextureManager() != null) {
         if (vanillaItem != null) {
            NativeImage var3 = tint(vanillaItem, argb, intens);
            currentItem = register(ITEM_ID, var3, "nyx-glint-item");
         }

         if (vanillaEntity != null) {
            NativeImage var4 = tint(vanillaEntity, argb, intens);
            currentEntity = register(ENTITY_ID, var4, "nyx-glint-entity");
         }
      }
   }

   private static void restoreVanillaInternal() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null && var0.getTextureManager() != null) {
         if (vanillaItem != null) {
            NativeImage var1 = copy(vanillaItem);
            currentItem = register(ITEM_ID, var1, "nyx-glint-item-vanilla");
         }

         if (vanillaEntity != null) {
            NativeImage var2 = copy(vanillaEntity);
            currentEntity = register(ENTITY_ID, var2, "nyx-glint-entity-vanilla");
         }
      }
   }

   private static NativeImageBackedTexture register(Identifier var0, NativeImage var1, String var2) {
      try {
         NativeImageBackedTexture var3 = new NativeImageBackedTexture(() -> var2, var1);
         MinecraftClient.getInstance().getTextureManager().registerTexture(var0, var3);
         return var3;
      } catch (Throwable var6) {
         System.err.println("[Glint] registerTexture failed for null: null");

         try {
            var1.close();
         } catch (Throwable var5) {
         }

         return null;
      }
   }

   private static NativeImage tint(NativeImage var0, int var1, float var2) {
      int var3 = var0.getWidth();
      int var4 = var0.getHeight();
      NativeImage var5 = new NativeImage(var3, var4, false);
      int var6 = var1 >> 16 & 0xFF;
      int var7 = var1 >> 8 & 0xFF;
      int var8 = var1 & 0xFF;
      float var9 = (var2 - 1.0F) * 0.25F;
      if (var9 < 0.0F) {
         var9 = 0.0F;
      }

      if (var9 > 1.0F) {
         var9 = 1.0F;
      }

      for (int var10 = 0; var10 < var4; var10++) {
         for (int var11 = 0; var11 < var3; var11++) {
            int var12 = var0.getColorArgb(var11, var10);
            int var13 = var12 >>> 24 & 0xFF;
            int var14 = var12 >> 16 & 0xFF;
            int var15 = var12 >> 8 & 0xFF;
            int var16 = var12 & 0xFF;
            float var17 = (0.2126F * var14 + 0.7152F * var15 + 0.0722F * var16) / 255.0F;
            float var18 = var17 + var9 * 0.7F * (1.0F - var17);
            if (var18 > 1.0F) {
               var18 = 1.0F;
            }

            int var19 = Math.min(255, (int)(var18 * var6));
            int var20 = Math.min(255, (int)(var18 * var7));
            int var21 = Math.min(255, (int)(var18 * var8));
            int var22 = Math.min(255, (int)(var13 * var2));
            if (var22 < 0) {
               var22 = 0;
            }

            var5.setColorArgb(var11, var10, var22 << 24 | var19 << 16 | var20 << 8 | var21);
         }
      }

      return var5;
   }

   private static NativeImage copy(NativeImage var0) {
      int var1 = var0.getWidth();
      int var2 = var0.getHeight();
      NativeImage var3 = new NativeImage(var1, var2, false);

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var1; var5++) {
            var3.setColorArgb(var5, var4, var0.getColorArgb(var5, var4));
         }
      }

      return var3;
   }

   public static void tickGuard() {
      if (enabled) {
         MinecraftClient var0 = MinecraftClient.getInstance();
         if (var0 != null && var0.getTextureManager() != null) {
            if (var0.getTextureManager().getTexture(ITEM_ID) != currentItem || var0.getTextureManager().getTexture(ENTITY_ID) != currentEntity) {
               applyTintInternal(color, intensity);
            }
         }
      }
   }
}

