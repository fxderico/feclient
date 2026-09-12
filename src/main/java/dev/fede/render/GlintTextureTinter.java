package dev.fede.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import dev.fede.FeClient;
import dev.fede.module.impl.CustomGlintModule;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

public final class GlintTextureTinter {
   private static final Identifier[] TEXTURES = new Identifier[]{ItemRenderer.ITEM_ENCHANTMENT_GLINT, ItemRenderer.ENTITY_ENCHANTMENT_GLINT};
   private static final NativeImage[] originals = new NativeImage[TEXTURES.length];
   private static final NativeImage[] scratch = new NativeImage[TEXTURES.length];
   private static final GpuTexture[] lastUploaded = new GpuTexture[TEXTURES.length];
   private static final Map<String, NativeImage> customCache = new HashMap<>();
   private static boolean loaded;
   private static boolean written;
   private static String lastKey;

   private GlintTextureTinter() {
   }

   public static void tick() {
      CustomGlintModule module = FeClient.modules() == null ? null : FeClient.modules().customGlint;
      if (module != null) {
         if (module.isActive()) {
            if (module.usesTexture()) {
               applyTexture(module.textureName(), module.strengthUnit());
            } else {
               applyTint(module.glintColor());
            }
         } else if (written) {
            restore();
         }
      }
   }

   private static void ensureLoaded() {
      if (!loaded) {
         loaded = true;
         MinecraftClient mc = MinecraftClient.getInstance();

         for (int i = 0; i < TEXTURES.length; i++) {
            Optional<Resource> res = mc.getResourceManager().getResource(TEXTURES[i]);
            if (!res.isEmpty()) {
               try (InputStream in = res.get().getInputStream()) {
                  originals[i] = NativeImage.read(in);
               } catch (Exception var8) {
                  originals[i] = null;
               }
            }
         }
      }
   }

   private static void applyTint(int color) {
      ensureLoaded();
      upload("tint:" + color, i -> tintInto(i, originals[i], color));
   }

   private static void applyTexture(String name, float strength) {
      ensureLoaded();
      NativeImage custom = loadCustom(name);
      if (custom != null) {
         int strq = Math.round(strength * 255.0F);
         upload("tex:" + name + ":" + strq, i -> sampleInto(i, custom, strength));
      }
   }

   private static void upload(String key, GlintTextureTinter.Source source) {
      TextureManager tm = MinecraftClient.getInstance().getTextureManager();
      boolean texChanged = false;

      for (int i = 0; i < TEXTURES.length; i++) {
         AbstractTexture t = tm.getTexture(TEXTURES[i]);
         if (t != null && t.getGlTexture() != lastUploaded[i]) {
            texChanged = true;
         }
      }

      if (!written || !key.equals(lastKey) || texChanged) {
         for (int ix = 0; ix < TEXTURES.length; ix++) {
            if (originals[ix] != null) {
               AbstractTexture t = tm.getTexture(TEXTURES[ix]);
               GpuTexture gpu = t == null ? null : t.getGlTexture();
               if (gpu != null) {
                  NativeImage img = source.build(ix);
                  if (img != null) {
                     RenderSystem.getDevice().createCommandEncoder().writeToTexture(gpu, img);
                     lastUploaded[ix] = gpu;
                  }
               }
            }
         }

         written = true;
         lastKey = key;
      }
   }

   private static void restore() {
      TextureManager tm = MinecraftClient.getInstance().getTextureManager();

      for (int i = 0; i < TEXTURES.length; i++) {
         NativeImage src = originals[i];
         if (src != null) {
            AbstractTexture t = tm.getTexture(TEXTURES[i]);
            GpuTexture gpu = t == null ? null : t.getGlTexture();
            if (gpu != null) {
               RenderSystem.getDevice().createCommandEncoder().writeToTexture(gpu, src);
               lastUploaded[i] = gpu;
            }
         }
      }

      written = false;
      lastKey = null;
   }

   private static NativeImage tintInto(int i, NativeImage src, int color) {
      int w = src.getWidth();
      int h = src.getHeight();
      NativeImage dst = ensureScratch(i, w, h);
      int tr = ColorHelper.getRed(color);
      int tg = ColorHelper.getGreen(color);
      int tb = ColorHelper.getBlue(color);

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            int p = src.getColorArgb(x, y);
            int v = Math.max(ColorHelper.getRed(p), Math.max(ColorHelper.getGreen(p), ColorHelper.getBlue(p)));
            dst.setColorArgb(x, y, ColorHelper.getArgb(ColorHelper.getAlpha(p), tr * v / 255, tg * v / 255, tb * v / 255));
         }
      }

      return dst;
   }

   private static NativeImage sampleInto(int i, NativeImage src, float strength) {
      int w = originals[i].getWidth();
      int h = originals[i].getHeight();
      int sw = src.getWidth();
      int sh = src.getHeight();
      NativeImage dst = ensureScratch(i, w, h);

      for (int y = 0; y < h; y++) {
         float fy = (y + 0.5F) * sh / h - 0.5F;

         for (int x = 0; x < w; x++) {
            float fx = (x + 0.5F) * sw / w - 0.5F;
            int p = bilinearWrapped(src, fx, fy, sw, sh);
            int r = Math.round(ColorHelper.getRed(p) * strength);
            int g = Math.round(ColorHelper.getGreen(p) * strength);
            int b = Math.round(ColorHelper.getBlue(p) * strength);
            dst.setColorArgb(x, y, ColorHelper.getArgb(ColorHelper.getAlpha(p), r, g, b));
         }
      }

      return dst;
   }

   private static NativeImage ensureScratch(int i, int w, int h) {
      NativeImage dst = scratch[i];
      if (dst == null || dst.getWidth() != w || dst.getHeight() != h) {
         if (dst != null) {
            dst.close();
         }

         dst = new NativeImage(w, h, false);
         scratch[i] = dst;
      }

      return dst;
   }

   private static int bilinearWrapped(NativeImage img, float fx, float fy, int sw, int sh) {
      int x0 = Math.floorMod((int)Math.floor(fx), sw);
      int y0 = Math.floorMod((int)Math.floor(fy), sh);
      int x1 = (x0 + 1) % sw;
      int y1 = (y0 + 1) % sh;
      float dx = fx - (float)Math.floor(fx);
      float dy = fy - (float)Math.floor(fy);
      int p00 = img.getColorArgb(x0, y0);
      int p10 = img.getColorArgb(x1, y0);
      int p01 = img.getColorArgb(x0, y1);
      int p11 = img.getColorArgb(x1, y1);
      int a = mix(ColorHelper.getAlpha(p00), ColorHelper.getAlpha(p10), ColorHelper.getAlpha(p01), ColorHelper.getAlpha(p11), dx, dy);
      int r = mix(ColorHelper.getRed(p00), ColorHelper.getRed(p10), ColorHelper.getRed(p01), ColorHelper.getRed(p11), dx, dy);
      int g = mix(ColorHelper.getGreen(p00), ColorHelper.getGreen(p10), ColorHelper.getGreen(p01), ColorHelper.getGreen(p11), dx, dy);
      int b = mix(ColorHelper.getBlue(p00), ColorHelper.getBlue(p10), ColorHelper.getBlue(p01), ColorHelper.getBlue(p11), dx, dy);
      return ColorHelper.getArgb(a, r, g, b);
   }

   private static int mix(int c00, int c10, int c01, int c11, float dx, float dy) {
      float top = c00 + (c10 - c00) * dx;
      float bot = c01 + (c11 - c01) * dx;
      return Math.round(top + (bot - top) * dy);
   }

   private static NativeImage loadCustom(String name) {
      if (customCache.containsKey(name)) {
         return customCache.get(name);
      } else {
         NativeImage img = null;
         Identifier id = Identifier.of("feclient", "textures/misc/glints/null.png");
         Optional<Resource> res = MinecraftClient.getInstance().getResourceManager().getResource(id);
         if (res.isPresent()) {
            try (InputStream in = res.get().getInputStream()) {
               img = NativeImage.read(in);
            } catch (Exception var9) {
               img = null;
            }
         }

         customCache.put(name, img);
         return img;
      }
   }

   interface Source {
      NativeImage build(int var1);
   }
}



