package dev.fede.render.nanovg;

import dev.fede.FeClient;
import dev.fede.module.Category;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.nanovg.NSVGImage;
import org.lwjgl.nanovg.NanoSVG;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public final class NVGIcons {
   private static final int RASTER_SIZE = 64;
   private static final Map<String, Integer> ICONS = new HashMap<>();
   private static boolean loaded;

   private NVGIcons() {
   }

   public static int get(Category category) {
      ensureLoaded();
      return ICONS.getOrDefault(category.getIconId(), -1);
   }

   private static void ensureLoaded() {
      if (!loaded) {
         loaded = true;

         for (Category category : Category.values()) {
            int handle = loadSvg("assets/feclient/icons/" + category.getIconId() + ".svg");
            if (handle > 0) {
               ICONS.put(category.getIconId(), handle);
            }
         }
      }
   }

   private static int loadSvg(String resourcePath) {
      long ctx = NVGRenderer.get().ctx();

      try {
         int var12;
         try (InputStream in = NVGIcons.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
               FeClient.LOGGER.warn("Icon resource missing: {}", resourcePath);
               return -1;
            }

            String svg = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            svg = svg.replaceAll("#[0-9a-fA-F]{6}", "#ffffff")
               .replaceAll("#[0-9a-fA-F]{3}\\b", "#fff")
               .replace("currentColor", "#ffffff")
               .replace("\"black\"", "\"white\"");
            ByteBuffer svgData = MemoryUtil.memUTF8(svg, true);
            ByteBuffer units = MemoryUtil.memASCII("px");
            NSVGImage image = null;
            long rasterizer = 0L;
            ByteBuffer pixels = null;
            boolean var18 = false;

            label193: {
               byte scale;
               try {
                  var18 = true;
                  image = NanoSVG.nsvgParse(svgData, units, 96.0F);
                  if (image != null) {
                     rasterizer = NanoSVG.nsvgCreateRasterizer();
                     float scalex = 64.0F / Math.max(image.width(), image.height());
                     pixels = MemoryUtil.memAlloc(16384);
                     NanoSVG.nsvgRasterize(rasterizer, image, 0.0F, 0.0F, scalex, pixels, 64, 64, 256);
                     var12 = NanoVG.nvgCreateImageRGBA(ctx, 64, 64, 0, pixels);
                     var18 = false;
                     break label193;
                  }

                  scale = -1;
                  var18 = false;
               } finally {
                  if (var18) {
                     if (pixels != null) {
                        MemoryUtil.memFree(pixels);
                     }

                     if (rasterizer != 0L) {
                        NanoSVG.nsvgDeleteRasterizer(rasterizer);
                     }

                     if (image != null) {
                        NanoSVG.nsvgDelete(image);
                     }

                     MemoryUtil.memFree(svgData);
                     MemoryUtil.memFree(units);
                  }
               }

               if (image != null) {
                  NanoSVG.nsvgDelete(image);
               }

               MemoryUtil.memFree(svgData);
               MemoryUtil.memFree(units);
               return scale;
            }

            if (pixels != null) {
               MemoryUtil.memFree(pixels);
            }

            if (rasterizer != 0L) {
               NanoSVG.nsvgDeleteRasterizer(rasterizer);
            }

            if (image != null) {
               NanoSVG.nsvgDelete(image);
            }

            MemoryUtil.memFree(svgData);
            MemoryUtil.memFree(units);
         }

         return var12;
      } catch (Exception var22) {
         FeClient.LOGGER.error("Failed to rasterize icon {}", resourcePath, var22);
         return -1;
      }
   }
}



