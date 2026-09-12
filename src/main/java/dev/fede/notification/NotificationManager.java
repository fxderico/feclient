package dev.fede.notification;

import dev.fede.hud.HudComponent;
import dev.fede.hud.HudDragController;
import dev.fede.module.Modules;
import dev.fede.render.anim.Animation;
import dev.fede.render.anim.Easing;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationManager extends HudComponent {
   private static final float WIDTH_PAD = 12.0F;
   private static final float HEIGHT = 30.0F;
   private static final float HEIGHT_WEATHER = 42.0F;
   private static final float GAP = 6.0F;
   private static final float FONT = 13.5F;
   private static final float FONT_SUB = 11.0F;
   private static final float floatVal = 26.0F;
   private static final float ANCHOR_W = 168.0F;
   private static final float ANCHOR_H = 30.0F;
   private ThemeManager themes;
   private Modules.HudModule hud;
   private final List<NotificationManager.Toast> toasts = new ArrayList<>();

   public NotificationManager(ThemeManager themes, Modules.HudModule hud) {
      super("notifications", 0.99F, 0.71F, () -> hud.isEnabled() && hud.notifications.get());
      this.themes = themes;
      this.hud = hud;
   }

   private boolean enabled() {
      return this.hud.isEnabled() && this.hud.notifications.get();
   }

   private float lifeSeconds() {
      return this.hud.notifyDuration.getFloat();
   }

   public void push(String moduleName, boolean enabled) {
      this.add(new Toast(moduleName + (enabled ? " enabled" : " disabled"), null, null, enabled));
   }

   public void pushInfo(String text) {
      this.add(new Toast(text, null, null, true));
   }

   public void pushWeather(String title, String subtitle, NotificationManager.Weather weather, boolean starting) {
      this.add(new Toast(title, subtitle, weather, starting));
   }

   private void add(NotificationManager.Toast toast) {
      if (this.enabled()) {
         synchronized (this.toasts) {
            this.toasts.add(toast);
            if (this.toasts.size() > 6) {
               this.toasts.removeFirst();
            }
         }
      }
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      return 168.0F;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      return 30.0F;
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
   }

   public void renderToasts(NVGRenderer vg, float uiWidth, float uiHeight) {
      Theme theme = this.themes.current();
      float life = this.lifeSeconds();
      float lifeNanos = life * 1.E9F;
      synchronized (this.toasts) {
         Iterator<NotificationManager.Toast> it = this.toasts.iterator();
         List<NotificationManager.Toast> alive = new ArrayList<>();

         while (it.hasNext()) {
            NotificationManager.Toast toast = it.next();
            if ((float)(System.nanoTime() - toast.bornNanos) > lifeNanos + 3.E8F) {
               it.remove();
            } else {
               alive.add(toast);
            }
         }

         boolean editing = HudDragController.isEditing();
         if (!alive.isEmpty() || editing) {
            float scale = this.getScale();
            float w = 168.0F * scale;
            float h = 30.0F * scale;
            float boxX = this.getFx() * (uiWidth - w);
            float boxY = this.getFy() * (uiHeight - h);
            vg.save();
            vg.translate(Math.round(boxX), Math.round(boxY));
            vg.scale(scale);
            float yCursor = 30.0F;

            for (int i = alive.size() - 1; i >= 0; i--) {
               NotificationManager.Toast toast = alive.get(i);
               float age = (float)(System.nanoTime() - toast.bornNanos) / 1.E9F;
               float fadeOut = Math.clamp((life + 0.3F - age) / 0.3F, 0.0F, 1.0F);
               float slide = toast.slide.value();
               float toastH = this.drawToast(vg, theme, 168.0F, yCursor, slide, fadeOut, toast.title, toast.subtitle, toast.weather, toast.accent);
               yCursor -= toastH + 6.0F;
            }

            if (alive.isEmpty() && editing) {
               this.drawToast(vg, theme, 168.0F, yCursor, 1.0F, 0.5F, "Notification", null, null, true);
            }

            vg.restore();
         }
      }
   }

   private float drawToast(
      NVGRenderer vg,
      Theme theme,
      float rightEdge,
      float bottom,
      float slide,
      float fadeOut,
      String title,
      String subtitle,
      NotificationManager.Weather weather,
      boolean accent
   ) {
      boolean twoLine = subtitle != null;
      float h = twoLine ? 42.0F : 30.0F;
      float glyphW = weather != null ? 26.0F : 0.0F;
      float textStart = 16.0F + glyphW;
      float textW = Math.max(vg.textWidth(title, 13.5F), twoLine ? vg.textWidth(subtitle, 11.0F) : 0.0F);
      float w = textStart + textW + 12.0F;
      float x = rightEdge - w + (1.0F - slide) * (w + 10.0F);
      float y = bottom - h;
      int strip = accent ? theme.accent() : theme.textDisabled();
      vg.save();
      vg.alpha(fadeOut);
      vg.rectGradient(x, y, w, h, 9.0F, theme.background(), theme.backgroundTo(), true);
      vg.rect(x + 3.0F, y + 5.0F, 3.0F, h - 10.0F, 1.5F, strip);
      vg.glow(x, y, w, h, 9.0F, 5.0F, Colors.withAlpha(strip, 0.2F * fadeOut));
      if (weather != null) {
         this.drawWeatherGlyph(vg, x + 12.0F + 2.0F + glyphW / 2.0F - 4.0F, y + h / 2.0F, weather, strip, theme.accentBright());
      }

      if (twoLine) {
         if (accent) {
            vg.textGradient(title, x + textStart, y + h / 2.0F - 8.0F, 13.5F, theme.accentBright(), theme.accent());
         } else {
            vg.text(title, x + textStart, y + h / 2.0F - 8.0F, 13.5F, theme.textPrimary());
         }

         vg.text(subtitle, x + textStart, y + h / 2.0F + 8.0F, 11.0F, theme.textMuted());
      } else if (accent) {
         vg.textGradient(title, x + textStart, y + h / 2.0F, 13.5F, theme.accentBright(), theme.accent());
      } else {
         vg.text(title, x + textStart, y + h / 2.0F, 13.5F, theme.textMuted());
      }

      vg.restore();
      return h;
   }

   private void drawWeatherGlyph(NVGRenderer vg, float cx, float cy, NotificationManager.Weather weather, int color, int boltColor) {
      switch (weather) {
         case RAIN:
            this.drawCloud(vg, cx, cy - 3.0F, color);

            for (int k = -1; k <= 1; k++) {
               float dx = cx + k * 4.0F;
               vg.line(dx + 1.0F, cy + 4.0F, dx - 1.0F, cy + 9.0F, 1.6F, color);
            }
            break;
         case THUNDER:
            this.drawCloud(vg, cx, cy - 3.0F, color);
            vg.line(cx + 1.5F, cy + 2.0F, cx - 2.5F, cy + 6.0F, 1.9F, boltColor);
            vg.line(cx - 2.5F, cy + 6.0F, cx + 1.5F, cy + 6.0F, 1.9F, boltColor);
            vg.line(cx + 1.5F, cy + 6.0F, cx - 2.5F, cy + 11.0F, 1.9F, boltColor);
            break;
         case CLEAR:
            vg.circle(cx, cy, 4.5F, boltColor);

            for (int k = 0; k < 8; k++) {
               double ang = k * Math.PI / 4.0;
               float dxu = (float)Math.cos(ang);
               float dyu = (float)Math.sin(ang);
               vg.line(cx + dxu * 6.5F, cy + dyu * 6.5F, cx + dxu * 9.0F, cy + dyu * 9.0F, 1.6F, color);
            }
      }
   }

   private void drawCloud(NVGRenderer vg, float cx, float cy, int color) {
      vg.rect(cx - 7.0F, cy - 1.0F, 14.0F, 5.0F, 2.5F, color);
      vg.circle(cx - 4.5F, cy + 1.0F, 4.0F, color);
      vg.circle(cx + 4.5F, cy + 1.0F, 4.0F, color);
      vg.circle(cx, cy - 2.5F, 5.0F, color);
   }

   class Toast {
      final String title;
      final String subtitle;
      final NotificationManager.Weather weather;
      final boolean accent;
      final long bornNanos = System.nanoTime();
      final Animation slide = new Animation(220.0F, 0.0F, Easing.EASE_OUT_CUBIC);

      Toast(String title, String subtitle, NotificationManager.Weather weather, boolean accent) {
         this.title = title;
         this.subtitle = subtitle;
         this.weather = weather;
         this.accent = accent;
         this.slide.setTarget(1.0F);
      }
   }

   public enum Weather {
      RAIN,
      THUNDER,
      CLEAR;

      private static NotificationManager.Weather[] $values() {
         return new NotificationManager.Weather[]{RAIN, THUNDER, CLEAR};
      }
   }
}

