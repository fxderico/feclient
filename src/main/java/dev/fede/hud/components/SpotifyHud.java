package dev.fede.hud.components;

import dev.fede.hud.HudComponent;
import dev.fede.module.Modules;
import dev.fede.render.nanovg.NVGImages;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.spotify.SpotifyService;
import dev.fede.spotify.SpotifyState;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

public class SpotifyHud extends HudComponent {
   public static final float WIDTH = 252.0F;
   public static final float HEIGHT = 74.0F;
   private static final float ART = 54.0F;
   private static final float TEXT_X = 76.0F;
   private static final float VOL_ROW = 18.0F;
   private static final float VBAR_X = 36.0F;
   private static final float VBAR_TRIM = 46.0F;
   private static final float VOL_CY = 80.0F;
   private Modules.SpotifyModule module;
   private SpotifyService service;
   private ThemeManager themes;
   private static final long DEMO_START = System.nanoTime();

   public SpotifyHud(Modules.SpotifyModule module, SpotifyService service, ThemeManager themes) {
      super("spotify", 0.5F, 0.965F, module::isEnabled);
      this.module = module;
      this.service = service;
      this.themes = themes;
   }

   private boolean demo() {
      return this.module.source.check("Demo");
   }

   private SpotifyState state() {
      if (this.demo()) {
         long pos = (System.nanoTime() - DEMO_START) / 1000000L % 227000L;
         return new SpotifyState(true, "Neon Nights", "Demo Artist", pos, 227000L, true, true, 0, 70, System.nanoTime());
      } else {
         return this.service.state();
      }
   }

   private boolean showVolume() {
      return this.module.volume.get() && this.state().active();
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      return 252.0F;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      return this.showVolume() ? 92.0F : 74.0F;
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
      SpotifyState state = this.state();
      Theme theme = this.themes.current();
      if (state.active() || !this.module.hideWhenIdle.get()) {
         vg.glow(x, y, w, h, 13.0F, 8.0F, Colors.withAlpha(-16777216, 0.3F));
         vg.rectGradient(x, y, w, h, 13.0F, Colors.withAlpha(-15264995, 0.92F), Colors.withAlpha(-15856878, 0.92F), true);
         float ax = x + 10.0F;
         float ay = y + 10.0F;
         int art = this.demo() ? -1 : NVGImages.fromFile(this.service.artPath(), state.artVersion());
         if (!this.demo() && art > 0) {
            this.drawRoundedImage(vg, art, ax, ay, 54.0F, 8.0F);
         } else {
            this.drawVinyl(vg, theme, ax, ay, 54.0F);
         }

         if (!state.active()) {
            vg.text("Nothing playing", x + 76.0F, y + h / 2.0F, 13.5F, theme.textMuted());
         } else {
            float controlsX = this.module.controls.get() ? x + w - 84.0F : x + w - 12.0F;
            vg.textTruncated(state.title(), x + 76.0F, y + 20.0F, 14.0F, theme.textPrimary(), controlsX - (x + 76.0F) - 6.0F);
            vg.textTruncated(state.artist(), x + 76.0F, y + 38.0F, 11.5F, theme.textMuted(), controlsX - (x + 76.0F) - 6.0F);
            if (this.module.controls.get()) {
               float cy = y + 22.0F;
               int idle = theme.textMuted();
               this.drawPrev(vg, x + w - 76.0F, cy, idle);
               this.drawPlayPause(vg, x + w - 52.0F, cy, theme.accentBright(), state.playing());
               this.drawNext(vg, x + w - 28.0F, cy, idle);
            }

            float barX = x + 76.0F;
            float barW = w - 76.0F - 12.0F;
            float barY = y + 58.0F;
            float frac = state.durMs() > 0L ? Math.clamp((float)state.livePosMs() / (float)state.durMs(), 0.0F, 1.0F) : 0.0F;
            vg.text(time(state.livePosMs()), barX, y + 49.0F, 9.5F, theme.textDisabled());
            String total = time(state.durMs());
            vg.text(total, barX + barW - vg.textWidth(total, 9.5F), y + 49.0F, 9.5F, theme.textDisabled());
            vg.rect(barX, barY, barW, 4.0F, 2.0F, Colors.withAlpha(-16777216, 0.5F));
            vg.rectGradient(barX, barY, Math.max(4.0F, barW * frac), 4.0F, 2.0F, theme.accent(), theme.accentBright(), false);
            vg.circle(barX + barW * frac, barY + 2.0F, 4.0F, -1);
            if (this.module.volume.get()) {
               int vol = state.volume();
               float vFrac = Math.clamp((vol < 0 ? 0 : vol) / 100.0F, 0.0F, 1.0F);
               float vBarX = x + 36.0F;
               float vBarW = w - 36.0F - 46.0F;
               float vBarY = y + 80.0F - 2.0F;
               this.drawSpeaker(vg, x + 18.0F, y + 80.0F, theme.textMuted(), vol == 0);
               vg.rect(vBarX, vBarY, vBarW, 4.0F, 2.0F, Colors.withAlpha(-16777216, 0.5F));
               vg.rectGradient(vBarX, vBarY, Math.max(4.0F, vBarW * vFrac), 4.0F, 2.0F, theme.accent(), theme.accentBright(), false);
               vg.circle(vBarX + vBarW * vFrac, y + 80.0F, 4.0F, -1);
               String label = vol < 0 ? "--" : vol + "%";
               vg.text(label, x + w - vg.textWidth(label, 9.5F) - 12.0F, y + 80.0F, 9.5F, theme.textDisabled());
            }
         }
      }
   }

   private void drawSpeaker(NVGRenderer vg, float cx, float cy, int color, boolean muted) {
      long ctx = vg.ctx();
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NVGColor col = NanoVG.nvgRGBA(
            (byte)Colors.red(color), (byte)Colors.green(color), (byte)Colors.blue(color), (byte)Colors.alpha(color), NVGColor.malloc(stack)
         );
         vg.rect(cx - 7.0F, cy - 3.0F, 5.0F, 6.0F, 1.0F, color);
         NanoVG.nvgFillColor(ctx, col);
         NanoVG.nvgBeginPath(ctx);
         NanoVG.nvgMoveTo(ctx, cx - 6.0F, cy);
         NanoVG.nvgLineTo(ctx, cx + 2.0F, cy - 7.0F);
         NanoVG.nvgLineTo(ctx, cx + 2.0F, cy + 7.0F);
         NanoVG.nvgClosePath(ctx);
         NanoVG.nvgFill(ctx);
         NanoVG.nvgStrokeColor(ctx, col);
         NanoVG.nvgStrokeWidth(ctx, 1.7F);
         NanoVG.nvgLineCap(ctx, 1);
         if (muted) {
            NanoVG.nvgBeginPath(ctx);
            NanoVG.nvgMoveTo(ctx, cx + 5.0F, cy - 3.5F);
            NanoVG.nvgLineTo(ctx, cx + 10.0F, cy + 3.5F);
            NanoVG.nvgMoveTo(ctx, cx + 10.0F, cy - 3.5F);
            NanoVG.nvgLineTo(ctx, cx + 5.0F, cy + 3.5F);
            NanoVG.nvgStroke(ctx);
         } else {
            for (float r : new float[]{4.5F, 8.0F}) {
               NanoVG.nvgBeginPath(ctx);
               NanoVG.nvgArc(ctx, cx + 2.0F, cy, r, -0.6F, 0.6F, 2);
               NanoVG.nvgStroke(ctx);
            }
         }
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

   private static String time(long ms) {
      long s = ms / 1000L;
      return String.format("%d:%02d", s / 60L, s % 60L);
   }

   private void drawRoundedImage(NVGRenderer vg, int image, float x, float y, float size, float radius) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         long ctx = vg.ctx();
         NVGPaint paint = NVGPaint.malloc(stack);
         NanoVG.nvgImagePattern(ctx, x, y, size, size, 0.0F, image, 1.0F, paint);
         NanoVG.nvgBeginPath(ctx);
         NanoVG.nvgRoundedRect(ctx, x, y, size, size, radius);
         NanoVG.nvgFillPaint(ctx, paint);
         NanoVG.nvgFill(ctx);
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

   private void drawVinyl(NVGRenderer vg, Theme theme, float x, float y, float size) {
      float cx = x + size / 2.0F;
      float cy = y + size / 2.0F;
      vg.rect(x, y, size, size, 8.0F, Colors.withAlpha(-16119795, 0.9F));
      vg.circle(cx, cy, size * 0.4F, -15330789);
      vg.circleOutline(cx, cy, size * 0.3F, 1.0F, Colors.withAlpha(theme.accent(), 0.35F));
      vg.circleOutline(cx, cy, size * 0.22F, 1.0F, Colors.withAlpha(theme.accent(), 0.25F));
      vg.circle(cx, cy, size * 0.12F, theme.accent());
      vg.textGradient("fe", cx - vg.textWidth("fe", 9.0F) / 2.0F, cy, 9.0F, -1, -1122834);
   }

   private void drawPrev(NVGRenderer vg, float cx, float cy, int color) {
      this.triangle(vg, cx + 4.0F, cy, -7.0F, color);
      vg.rect(cx - 7.0F, cy - 5.5F, 2.0F, 11.0F, 1.0F, color);
   }

   private void drawNext(NVGRenderer vg, float cx, float cy, int color) {
      this.triangle(vg, cx - 4.0F, cy, 7.0F, color);
      vg.rect(cx + 5.0F, cy - 5.5F, 2.0F, 11.0F, 1.0F, color);
   }

   private void drawPlayPause(NVGRenderer vg, float cx, float cy, int color, boolean playing) {
      vg.circleOutline(cx, cy, 10.0F, 1.4F, color);
      if (playing) {
         vg.rect(cx - 3.5F, cy - 4.5F, 2.4F, 9.0F, 1.2F, color);
         vg.rect(cx + 1.1F, cy - 4.5F, 2.4F, 9.0F, 1.2F, color);
      } else {
         this.triangle(vg, cx - 2.5F, cy, 7.0F, color);
      }
   }

   private void triangle(NVGRenderer vg, float x, float cy, float dir, int color) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         long ctx = vg.ctx();
         NanoVG.nvgBeginPath(ctx);
         NanoVG.nvgMoveTo(ctx, x, cy - 5.5F);
         NanoVG.nvgLineTo(ctx, x, cy + 5.5F);
         NanoVG.nvgLineTo(ctx, x + dir, cy);
         NanoVG.nvgClosePath(ctx);
         NanoVG.nvgFillColor(
            ctx,
            NanoVG.nvgRGBA((byte)Colors.red(color), (byte)Colors.green(color), (byte)Colors.blue(color), (byte)Colors.alpha(color), NVGColor.malloc(stack))
         );
         NanoVG.nvgFill(ctx);
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

   @Override
   public boolean onEditClick(float lx, float ly) {
      SpotifyState state = this.state();
      if (!state.active()) {
         return false;
      } else {
         if (this.module.controls.get() && ly >= 10.0F && ly <= 34.0F) {
            if (hit(lx, 176.0F)) {
               if (this.demo()) {
                  return true;
               }

               this.service.previous();
               return true;
            }

            if (hit(lx, 200.0F)) {
               if (this.demo()) {
                  return true;
               }

               this.service.togglePlay();
               return true;
            }

            if (hit(lx, 224.0F)) {
               if (this.demo()) {
                  return true;
               }

               this.service.next();
               return true;
            }
         }

         if (ly >= 52.0F && ly <= 66.0F && lx >= 76.0F && lx <= 76.0F + 164.0F && state.canSeek()) {
            long target = (long)((lx - 76.0F) / 164.0F * (float)state.durMs());
            if (!this.demo()) {
               this.service.seekTo(target);
            }

            return true;
         } else {
            float vBarX = 36.0F;
            if (this.module.volume.get() && ly >= 73.0F && ly <= 87.0F && lx >= vBarX && lx <= vBarX + 170.0F) {
               int pct = Math.round((lx - vBarX) / 170.0F * 100.0F);
               if (!this.demo()) {
                  this.service.setVolume(pct);
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   private static boolean hit(float lx, float centerX) {
      return Math.abs(lx - centerX) <= 11.0F;
   }
}

