package dev.fede.render.nanovg;

import dev.fede.FeClient;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.system.MemoryUtil;

public final class NVGImages {
   private static final Map<Identifier, Integer> RESOURCE_CACHE = new HashMap<>();
   private static final Map<Integer, Integer> GL_HANDLE_CACHE = new HashMap<>();
   private static int fileImageHandle = -1;
   private static int fileImageVersion = -1;
   private static Path fileImagePath;

   private NVGImages() {
   }

   public static int fromResource(Identifier location) {
      return RESOURCE_CACHE.computeIfAbsent(location, NVGImages::loadResource);
   }

   private static int loadResource(Identifier location) {
      long ctx = NVGRenderer.get().ctx();
      Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(location);
      if (resource.isEmpty()) {
         return -1;
      } else {
         try {
            int var7;
            try (InputStream in = resource.get().getInputStream()) {
               byte[] bytes = in.readAllBytes();
               ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);

               try {
                  buffer.put(bytes).flip();
                  var7 = NanoVG.nvgCreateImageMem(ctx, 32, buffer);
               } finally {
                  MemoryUtil.memFree(buffer);
               }
            }

            return var7;
         } catch (Exception var16) {
            FeClient.LOGGER.warn("Failed to load image {}", location, var16);
            return -1;
         }
      }
   }

   public static int fromFile(Path path, int version) {
      if (version == fileImageVersion && path.equals(fileImagePath)) {
         return fileImageHandle;
      } else {
         long ctx = NVGRenderer.get().ctx();
         if (fileImageHandle > 0) {
            NanoVG.nvgDeleteImage(ctx, fileImageHandle);
            fileImageHandle = -1;
         }

         fileImageVersion = version;
         fileImagePath = path;

         try {
            byte[] bytes = Files.readAllBytes(path);
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);

            try {
               buffer.put(bytes).flip();
               fileImageHandle = NanoVG.nvgCreateImageMem(ctx, 0, buffer);
            } finally {
               MemoryUtil.memFree(buffer);
            }
         } catch (Exception var10) {
            fileImageHandle = -1;
         }

         return fileImageHandle;
      }
   }

   public static int wrapGlTexture(Identifier textureId, int width, int height) {
      AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(textureId);
      if (texture != null && texture.getGlTexture() instanceof GlTexture glTexture) {
         int var6 = glTexture.getGlId();
         return GL_HANDLE_CACHE.computeIfAbsent(var6, id -> NanoVGGL3.nvglCreateImageFromHandle(NVGRenderer.get().ctx(), id, width, height, 65536));
      } else {
         return -1;
      }
   }

   public static int createDynamic(int width, int height) {
      long ctx = NVGRenderer.get().ctx();
      ByteBuffer zero = MemoryUtil.memCalloc(width * height * 4);

      int var5;
      try {
         var5 = NanoVG.nvgCreateImageRGBA(ctx, width, height, 32, zero);
      } finally {
         MemoryUtil.memFree(zero);
      }

      return var5;
   }

   public static void updateDynamic(int handle, ByteBuffer rgba) {
      if (handle > 0) {
         NanoVG.nvgUpdateImage(NVGRenderer.get().ctx(), handle, rgba);
      }
   }

   public static void deleteImage(int handle) {
      if (handle > 0) {
         NanoVG.nvgDeleteImage(NVGRenderer.get().ctx(), handle);
      }
   }

   public static void drawSubImage(
      NVGRenderer vg, int image, float texW, float texH, float u0, float v0, float u1, float v1, float x, float y, float w, float h, float alpha
   ) {
      if (image > 0) {
         float scaleX = w / (u1 - u0);
         float scaleY = h / (v1 - v0);
         vg.imagePattern(image, x - u0 * scaleX, y - v0 * scaleY, texW * scaleX, texH * scaleY, x, y, w, h, alpha);
      }
   }
}



