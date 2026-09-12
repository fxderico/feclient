package dev.fede.water.utils.renderer;

import dev.fede.water.utils.renderer.texture.TexturePipeline;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class AwtFontRenderer {
   public static final AwtFontRenderer INSTANCE = new AwtFontRenderer();
   private static volatile AwtFontRenderer.FontChoice currentFont = AwtFontRenderer.FontChoice.BLISS_BLOOM;
   private Font awtFont;
   private AwtFontRenderer.FontChoice loadedFont = null;
   private static volatile int clipBottom = -1;
   private final AtomicInteger idCounter = new AtomicInteger(0);
   private final LinkedHashMap<String, AwtFontRenderer.Entry> cache = new LinkedHashMap<String, AwtFontRenderer.Entry>() {
      @Override
      protected boolean removeEldestEntry(java.util.Map.Entry<String, AwtFontRenderer.Entry> var1) {
         if (this.size() > 300) {
            MinecraftClient var2 = MinecraftClient.getInstance();
            if (var2 != null) {
               try {
                  var2.getTextureManager().destroyTexture(((AwtFontRenderer.Entry)var1.getValue()).id);
               } catch (Exception var4) {
               }
            } else {
               ((AwtFontRenderer.Entry)var1.getValue()).tex.close();
            }

            return true;
         } else {
            return false;
         }
      }
   };

   public static void setClipBottom(int var0) {
      clipBottom = var0;
   }

   public static void clearClip() {
      clipBottom = -1;
   }

   public static void setFont(AwtFontRenderer.FontChoice var0) {
      if (var0 != currentFont) {
         currentFont = var0;
         INSTANCE.awtFont = null;
         INSTANCE.loadedFont = null;
         INSTANCE.cache.clear();
      }
   }

   public static AwtFontRenderer.FontChoice getFont() {
      return currentFont;
   }

   public static String[] getFontNames() {
      AwtFontRenderer.FontChoice[] var0 = AwtFontRenderer.FontChoice.values();
      String[] var1 = new String[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = var0[var2].displayName;
      }

      return var1;
   }

   private void initFont() {
      AwtFontRenderer.FontChoice var1 = AwtFontRenderer.FontChoice.BLISS_BLOOM;
      currentFont = AwtFontRenderer.FontChoice.BLISS_BLOOM;
      if (this.loadedFont != var1 || this.awtFont == null) {
         if (var1 == AwtFontRenderer.FontChoice.VANILLA) {
            this.awtFont = null;
            this.loadedFont = var1;
         } else {
            this.awtFont = this.loadBundledFont(AwtFontRenderer.FontChoice.BLISS_BLOOM);
            if (this.awtFont == null) {
               this.awtFont = this.loadBundledFont(AwtFontRenderer.FontChoice.MINECRAFT_TEN);
            }

            if (this.awtFont == null) {
               this.awtFont = this.loadBundledFont(AwtFontRenderer.FontChoice.INTER);
            }

            if (this.awtFont == null) {
               this.awtFont = new Font("Monospaced", 1, 16);
            }

            this.loadedFont = var1;
         }
      }
   }

   private Font loadBundledFont(AwtFontRenderer.FontChoice var1) {
      if (var1 != null && var1.resourcePath != null) {
         byte[] var2 = this.readBundledFontBytes(var1);
         if (var2 != null && var2.length != 0) {
            Font var3 = this.createFontFromBytes(var2, 0, var1.size);
            if (var3 == null) {
               var3 = this.createFontFromBytes(var2, 1, var1.size);
            }

            if (var3 == null) {
               return null;
            } else {
               try {
                  GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(var3);
               } catch (Throwable var5) {
               }

               return var3.deriveFont(0, var1.size);
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private Font createFontFromBytes(byte[] var1, int var2, float var3) {
      try {
         Font var5;
         try (ByteArrayInputStream var4 = new ByteArrayInputStream(var1)) {
            var5 = Font.createFont(var2, var4).deriveFont(0, var3);
         }

         return var5;
      } catch (Throwable var9) {
         return null;
      }
   }

   private byte[] readBundledFontBytes(AwtFontRenderer.FontChoice var1) {
      String var2 = var1.resourcePath;

      try {
         label138: {
            byte[] var4;
            try (InputStream var3 = this.getClass().getResourceAsStream(var2)) {
               if (var3 == null) {
                  break label138;
               }

               var4 = var3.readAllBytes();
            }

            return var4;
         }
      } catch (Throwable var18) {
      }

      try {
         ClassLoader var19 = Thread.currentThread().getContextClassLoader();
         String var21 = var2.startsWith("/") ? var2.substring(1) : var2;
         if (var19 != null) {
            try (InputStream var5 = var19.getResourceAsStream(var21)) {
               if (var5 != null) {
                  return var5.readAllBytes();
               }
            }
         }
      } catch (Throwable var16) {
      }

      try {
         MinecraftClient var20 = MinecraftClient.getInstance();
         if (var20 != null && var20.getResourceManager() != null) {
            String var22 = var2.startsWith("/assets/water/") ? var2.substring("/assets/water/".length()) : var2;
            if (var22.startsWith("/")) {
               var22 = var22.substring(1);
            }

            Identifier var23 = Identifier.of("water", var22);
            Optional var6 = var20.getResourceManager().getResource(var23);
            if (var6.isPresent()) {
               byte[] var8;
               try (InputStream var7 = ((Resource)var6.get()).getInputStream()) {
                  var8 = var7.readAllBytes();
               }

               return var8;
            }
         }
      } catch (Throwable var14) {
      }

      return null;
   }

   private AwtFontRenderer.Entry get(String var1) {
      String var2 = currentFont.name() + ":" + var1;
      AwtFontRenderer.Entry var3 = this.cache.get(var2);
      if (var3 != null) {
         return var3;
      } else {
         var3 = this.build(var1);
         if (var3 != null) {
            this.cache.put(var2, var3);
         }

         return var3;
      }
   }

   private AwtFontRenderer.Entry build(String var1) {
      this.initFont();
      if (this.awtFont == null) {
         return null;
      } else {
         BufferedImage var2 = new BufferedImage(1, 1, 2);
         Graphics2D var3 = var2.createGraphics();
         var3.setFont(this.awtFont);
         FontRenderContext var4 = var3.getFontRenderContext();
         Rectangle2D var5 = this.awtFont.getStringBounds(var1, var4);
         var3.dispose();
         int var6 = Math.max(1, (int)Math.ceil(var5.getWidth()) + 6);
         int var7 = Math.max(1, (int)Math.ceil(this.awtFont.getSize() * 1.3F));
         BufferedImage var8 = new BufferedImage(var6, var7, 2);
         Graphics2D var9 = var8.createGraphics();
         var9.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         var9.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         var9.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
         var9.setFont(this.awtFont);
         var9.setColor(Color.WHITE);
         var9.drawString(var1, 1, this.awtFont.getSize() - 1);
         var9.dispose();

         try {
            ByteArrayOutputStream var10 = new ByteArrayOutputStream();
            ImageIO.write(var8, "png", var10);
            NativeImage var11 = NativeImage.read(new ByteArrayInputStream(var10.toByteArray()));
            NativeImageBackedTexture var12 = new NativeImageBackedTexture(() -> "water_font_cache", var11);
            Identifier var13 = Identifier.of("water", "font_cache_" + this.idCounter.getAndIncrement());
            MinecraftClient var14 = MinecraftClient.getInstance();
            if (var14 != null) {
               var14.getTextureManager().registerTexture(var13, var12);
            }

            return new AwtFontRenderer.Entry(var12, var13, var6 / 2, var7 / 2);
         } catch (Exception var15) {
            return null;
         }
      }
   }

   public void drawString(DrawContext var1, String var2, float var3, float var4, int var5) {
      if (var2 != null && !var2.isEmpty()) {
         if (clipBottom < 0 || !(var4 >= clipBottom)) {
            if (currentFont == AwtFontRenderer.FontChoice.VANILLA) {
               MinecraftClient var8 = MinecraftClient.getInstance();
               if (var8 != null) {
                  var1.drawText(var8.textRenderer, var2, (int)var3, (int)var4, var5, false);
               }
            } else {
               AwtFontRenderer.Entry var6 = this.get(var2);
               if (var6 != null && var6.tex.getGlTextureView() != null) {
                  Matrix4f var7 = RenderUtil.createProjection(var1);
                  TexturePipeline.drawWH(var7, var3, var4, var6.displayW, var6.displayH, var6.tex.getGlTextureView(), var5, 0.0F);
               }
            }
         }
      }
   }

   public int getWidth(String var1) {
      if (var1 == null || var1.isEmpty()) {
         return 0;
      } else if (currentFont == AwtFontRenderer.FontChoice.VANILLA) {
         MinecraftClient var5 = MinecraftClient.getInstance();
         return var5 != null ? var5.textRenderer.getWidth(var1) : var1.length() * 6;
      } else {
         this.initFont();
         if (this.awtFont == null) {
            return var1.length() * 6;
         } else {
            BufferedImage var2 = new BufferedImage(1, 1, 2);
            Graphics2D var3 = var2.createGraphics();
            var3.setFont(this.awtFont);
            int var4 = var3.getFontMetrics().stringWidth(var1) / 2;
            var3.dispose();
            return var4;
         }
      }
   }

   public int getWidth(CharSequence var1) {
      return this.getWidth(var1 != null ? var1.toString() : "");
   }

   private static class Entry {
      NativeImageBackedTexture tex;
      Identifier id;
      int displayW;
      int displayH;

      Entry(NativeImageBackedTexture var1, Identifier var2, int var3, int var4) {
         this.tex = var1;
         this.id = var2;
         this.displayW = var3;
         this.displayH = var4;
      }
   }

   public static enum FontChoice {
      INTER("Inter", "/assets/water/font/inter.ttf", 16.0F, 0),
      DEKATRON("Dekatron", "/assets/water/font/dekatron.otf", 16.0F, 0),
      MINECRAFT_TEN("Minecraft Ten", "/assets/water/font/minecraft_ten.ttf", 16.0F, 0),
      BASKETBALL("Basketball", "/assets/water/font/basketball.otf", 16.0F, 0),
      SECOND_TIME("Second Time", "/assets/water/font/second_time_demo.ttf", 16.0F, 0),
      UNDER_TRAINED("Under Trained", "/assets/water/font/under_trained.ttf", 16.0F, 0),
      BELASH("Belash", "/assets/water/font/belash.ttf", 16.0F, 0),
      BLISS_BLOOM("Bliss Bloom", "/assets/water/font/bliss_bloom.otf", 16.0F, 0),
      VANILLA("Vanilla", null, 16.0F, -1);

      public final String displayName;
      public final String resourcePath;
      public final float size;
      public final int fontType;

      private FontChoice(String var3, String var4, float var5, int var6) {
         this.displayName = var3;
         this.resourcePath = var4;
         this.size = var5;
         this.fontType = var6;
      }
   }
}

