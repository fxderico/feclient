package dev.fede.render.nanovg;

import dev.fede.FeClient;
import dev.fede.util.Colors;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class NVGRenderer {
   public static final String FONT_XUONG = "xuong";
   public static final String FONT_VANILLA = "vanilla";
   private static NVGRenderer instance;
   private long ctx;
   private boolean xuongLoaded;
   private boolean vanillaLoaded;
   private final List<ByteBuffer> retainedFontData = new ArrayList<>();
   private String activeFont = "xuong";
   private final ArrayDeque<Float> alphaStack = new ArrayDeque<>();
   private float appliedAlpha = 1.0F;

   private NVGRenderer() {
      this.ctx = NanoVGGL3.nvgCreate(1);
      if (this.ctx == 0L) {
         throw new IllegalStateException("Failed to create NanoVG context");
      } else {
         this.xuongLoaded = this.loadFont("xuong", "assets/feclient/fonts/Xuong-Regular.ttf");
         this.vanillaLoaded = this.loadFont("vanilla", "assets/feclient/fonts/Monocraft.ttf");
         if (this.xuongLoaded && this.vanillaLoaded) {
            NanoVG.nvgAddFallbackFont(this.ctx, "xuong", "vanilla");
         }
      }
   }

   public static NVGRenderer get() {
      if (instance == null) {
         instance = new NVGRenderer();
      }

      return instance;
   }

   public long ctx() {
      return this.ctx;
   }

   private boolean loadFont(String name, String resourcePath) {
      try {
         boolean var7;
         try (InputStream in = NVGRenderer.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
               FeClient.LOGGER.warn("Font resource missing: {}", resourcePath);
               return false;
            }

            byte[] bytes = in.readAllBytes();
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
            buffer.put(bytes).flip();
            int handle = NanoVG.nvgCreateFontMem(this.ctx, name, buffer, false);
            if (handle == -1) {
               MemoryUtil.memFree(buffer);
               FeClient.LOGGER.warn("NanoVG rejected font {}", resourcePath);
               return false;
            }

            this.retainedFontData.add(buffer);
            var7 = true;
         }

         return var7;
      } catch (IOException var10) {
         FeClient.LOGGER.error("Failed to load font {}", resourcePath, var10);
         return false;
      }
   }

   public void setFontMode(String mode) {
      this.activeFont = "Xuong".equals(mode) && this.xuongLoaded ? "xuong" : (this.vanillaLoaded ? "vanilla" : (this.xuongLoaded ? "xuong" : null));
   }

   public boolean hasFont() {
      return this.activeFont != null;
   }

   public void beginFrame(float width, float height, float pixelRatio) {
      this.alphaStack.clear();
      this.appliedAlpha = 1.0F;
      NanoVG.nvgBeginFrame(this.ctx, width, height, pixelRatio);
   }

   public void endFrame() {
      NanoVG.nvgEndFrame(this.ctx);
   }

   public void save() {
      this.alphaStack.push(this.appliedAlpha);
      NanoVG.nvgSave(this.ctx);
   }

   public void restore() {
      if (!this.alphaStack.isEmpty()) {
         this.appliedAlpha = this.alphaStack.pop();
      }

      NanoVG.nvgRestore(this.ctx);
   }

   public void scale(float s) {
      NanoVG.nvgScale(this.ctx, s, s);
   }

   public void translate(float x, float y) {
      NanoVG.nvgTranslate(this.ctx, x, y);
   }

   public void alpha(float a) {
      this.appliedAlpha = this.appliedAlpha * Math.clamp(a, 0.0F, 1.0F);
      NanoVG.nvgGlobalAlpha(this.ctx, this.appliedAlpha);
   }

   public void scissor(float x, float y, float w, float h) {
      NanoVG.nvgIntersectScissor(this.ctx, x, y, w, h);
   }

   public void rect(float x, float y, float w, float h, float radius, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRect(this.ctx, x, y, w, h, radius);
         NanoVG.nvgFillColor(this.ctx, color(stack, argb));
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void rectGradient(float x, float y, float w, float h, float radius, int from, int to, boolean vertical) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NVGPaint paint = NVGPaint.malloc(stack);
         float ex = vertical ? x : x + w;
         float ey = vertical ? y + h : y;
         NanoVG.nvgLinearGradient(this.ctx, x, y, ex, ey, color(stack, from), color(stack, to), paint);
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRect(this.ctx, x, y, w, h, radius);
         NanoVG.nvgFillPaint(this.ctx, paint);
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var14) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }
         }

         throw var14;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void rectVaryingGradient(float x, float y, float w, float h, float rtl, float rtr, float rbr, float rbl, int from, int to) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NVGPaint paint = NVGPaint.malloc(stack);
         NanoVG.nvgLinearGradient(this.ctx, x, y, x, y + h, color(stack, from), color(stack, to), paint);
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRectVarying(this.ctx, x, y, w, h, rtl, rtr, rbr, rbl);
         NanoVG.nvgFillPaint(this.ctx, paint);
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var15) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }
         }

         throw var15;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void chevron(float cx, float cy, float size, float stroke, int argb, boolean pointDown) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         if (pointDown) {
            NanoVG.nvgMoveTo(this.ctx, cx - size, cy - size / 2.0F);
            NanoVG.nvgLineTo(this.ctx, cx, cy + size / 2.0F);
            NanoVG.nvgLineTo(this.ctx, cx + size, cy - size / 2.0F);
         } else {
            NanoVG.nvgMoveTo(this.ctx, cx - size / 2.0F, cy - size);
            NanoVG.nvgLineTo(this.ctx, cx + size / 2.0F, cy);
            NanoVG.nvgLineTo(this.ctx, cx - size / 2.0F, cy + size);
         }

         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, stroke);
         NanoVG.nvgLineCap(this.ctx, 1);
         NanoVG.nvgLineJoin(this.ctx, 1);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void triangle(float x0, float y0, float x1, float y1, float x2, float y2, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgMoveTo(this.ctx, x0, y0);
         NanoVG.nvgLineTo(this.ctx, x1, y1);
         NanoVG.nvgLineTo(this.ctx, x2, y2);
         NanoVG.nvgClosePath(this.ctx);
         NanoVG.nvgFillColor(this.ctx, color(stack, argb));
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var12) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void rectOutline(float x, float y, float w, float h, float radius, float stroke, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRect(this.ctx, x + stroke / 2.0F, y + stroke / 2.0F, w - stroke, h - stroke, radius);
         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, stroke);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var12) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void glow(float x, float y, float w, float h, float radius, float spread, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NVGPaint paint = NVGPaint.malloc(stack);
         NanoVG.nvgBoxGradient(this.ctx, x, y, w, h, radius, spread * 2.0F, color(stack, argb), color(stack, Colors.withAlpha(argb, 0)), paint);
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRect(this.ctx, x - spread, y - spread, w + spread * 2.0F, h + spread * 2.0F, radius + spread);
         NanoVG.nvgFillPaint(this.ctx, paint);
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var12) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void circle(float cx, float cy, float r, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgCircle(this.ctx, cx, cy, r);
         NanoVG.nvgFillColor(this.ctx, color(stack, argb));
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var9) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void circleGlow(float cx, float cy, float r, float spread, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NVGPaint paint = NVGPaint.malloc(stack);
         NanoVG.nvgRadialGradient(this.ctx, cx, cy, r * 0.25F, r + spread, color(stack, argb), color(stack, Colors.withAlpha(argb, 0)), paint);
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgCircle(this.ctx, cx, cy, r + spread);
         NanoVG.nvgFillPaint(this.ctx, paint);
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var10) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void line(float x1, float y1, float x2, float y2, float width, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgMoveTo(this.ctx, x1, y1);
         NanoVG.nvgLineTo(this.ctx, x2, y2);
         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, width);
         NanoVG.nvgLineCap(this.ctx, 1);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void checkmark(float x, float y, float size, float stroke, int argb) {
      float x1 = x + size * 0.22F;
      float y1 = y + size * 0.55F;
      float x2 = x + size * 0.42F;
      float y2 = y + size * 0.74F;
      float x3 = x + size * 0.78F;
      float y3 = y + size * 0.3F;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgMoveTo(this.ctx, x1, y1);
         NanoVG.nvgLineTo(this.ctx, x2, y2);
         NanoVG.nvgLineTo(this.ctx, x3, y3);
         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, stroke);
         NanoVG.nvgLineCap(this.ctx, 1);
         NanoVG.nvgLineJoin(this.ctx, 1);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var16) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var15) {
               var16.addSuppressed(var15);
            }
         }

         throw var16;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void cross(float x, float y, float size, float stroke, int argb) {
      float pad = size * 0.3F;
      this.line(x + pad, y + pad, x + size - pad, y + size - pad, stroke, argb);
      this.line(x + size - pad, y + pad, x + pad, y + size - pad, stroke, argb);
   }

   public float text(String str, float x, float y, float size, int argb) {
      return this.text(str, x, y, size, argb, this.activeFont);
   }

   public float text(String str, float x, float y, float size, int argb, String font) {
      if (font == null) {
         return 0.0F;
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         float var8;
         try {
            NanoVG.nvgFontFace(this.ctx, font);
            NanoVG.nvgFontSize(this.ctx, size);
            NanoVG.nvgTextAlign(this.ctx, 17);
            NanoVG.nvgFillColor(this.ctx, color(stack, argb));
            var8 = NanoVG.nvgText(this.ctx, x, y, str) - x;
         } catch (Throwable var11) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (stack != null) {
            stack.close();
         }

         return var8;
      }
   }

   public float textGradient(String str, float x, float y, float size, int top, int bottom) {
      if (this.activeFont == null) {
         return 0.0F;
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         float var9;
         try {
            NVGPaint paint = NVGPaint.malloc(stack);
            NanoVG.nvgLinearGradient(this.ctx, x, y - size / 2.0F, x, y + size / 2.0F, color(stack, top), color(stack, bottom), paint);
            NanoVG.nvgFontFace(this.ctx, this.activeFont);
            NanoVG.nvgFontSize(this.ctx, size);
            NanoVG.nvgTextAlign(this.ctx, 17);
            NanoVG.nvgFillPaint(this.ctx, paint);
            var9 = NanoVG.nvgText(this.ctx, x, y, str) - x;
         } catch (Throwable var11) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (stack != null) {
            stack.close();
         }

         return var9;
      }
   }

   public void textGlow(String str, float x, float y, float size, int argb) {
      if (this.activeFont != null) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            NanoVG.nvgFontFace(this.ctx, this.activeFont);
            NanoVG.nvgFontSize(this.ctx, size);
            NanoVG.nvgTextAlign(this.ctx, 17);
            NanoVG.nvgFontBlur(this.ctx, 4.0F);
            NanoVG.nvgFillColor(this.ctx, color(stack, argb));
            NanoVG.nvgText(this.ctx, x, y, str);
            NanoVG.nvgFontBlur(this.ctx, 0.0F);
         } catch (Throwable var10) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (stack != null) {
            stack.close();
         }
      }
   }

   public float textTruncated(String str, float x, float y, float size, int argb, float maxWidth) {
      if (this.textWidth(str, size) <= maxWidth) {
         return this.text(str, x, y, size, argb);
      } else {
         String cut = str;

         while (cut.length() > 1 && this.textWidth("null…", size) > maxWidth) {
            cut = cut.substring(0, cut.length() - 1);
         }

         return this.text("null…", x, y, size, argb);
      }
   }

   public float textWidth(String str, float size) {
      if (this.activeFont == null) {
         return 0.0F;
      } else {
         NanoVG.nvgFontFace(this.ctx, this.activeFont);
         NanoVG.nvgFontSize(this.ctx, size);
         NanoVG.nvgTextAlign(this.ctx, 17);
         return NanoVG.nvgTextBounds(this.ctx, 0.0F, 0.0F, str, (FloatBuffer)null);
      }
   }

   public void rotate(float radians) {
      NanoVG.nvgRotate(this.ctx, radians);
   }

   public void imagePattern(int image, float patternX, float patternY, float patternW, float patternH, float x, float y, float w, float h, float alphaMul) {
      if (image > 0) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            NVGPaint paint = NVGPaint.malloc(stack);
            NanoVG.nvgImagePattern(this.ctx, patternX, patternY, patternW, patternH, 0.0F, image, alphaMul, paint);
            NanoVG.nvgBeginPath(this.ctx);
            NanoVG.nvgRect(this.ctx, x, y, w, h);
            NanoVG.nvgFillPaint(this.ctx, paint);
            NanoVG.nvgFill(this.ctx);
         } catch (Throwable var15) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }
            }

            throw var15;
         }

         if (stack != null) {
            stack.close();
         }
      }
   }

   public void circleOutline(float cx, float cy, float r, float stroke, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgCircle(this.ctx, cx, cy, r);
         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, stroke);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var10) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void image(int image, float x, float y, float w, float h, int tint) {
      if (image > 0) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            NVGPaint paint = NVGPaint.malloc(stack);
            NanoVG.nvgImagePattern(this.ctx, x, y, w, h, 0.0F, image, 1.0F, paint);
            paint.innerColor(color(stack, tint));
            NanoVG.nvgBeginPath(this.ctx);
            NanoVG.nvgRect(this.ctx, x, y, w, h);
            NanoVG.nvgFillPaint(this.ctx, paint);
            NanoVG.nvgFill(this.ctx);
         } catch (Throwable var11) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (stack != null) {
            stack.close();
         }
      }
   }

   private static NVGColor color(MemoryStack stack, int argb) {
      return NanoVG.nvgRGBA((byte)Colors.red(argb), (byte)Colors.green(argb), (byte)Colors.blue(argb), (byte)Colors.alpha(argb), NVGColor.malloc(stack));
   }
}



