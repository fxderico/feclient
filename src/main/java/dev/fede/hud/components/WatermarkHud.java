package dev.fede.hud.components;

import dev.fede.hud.HudComponent;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import java.util.function.BooleanSupplier;

public class WatermarkHud extends HudComponent {
   private static final float HEIGHT = 30.0F;
   private static final float PAD = 13.0F;
   private static final float LOGO_SIZE = 18.0F;
   private static final float TEXT_SIZE = 14.0F;
   private ThemeManager themes;

   public WatermarkHud(ThemeManager themes, BooleanSupplier visible) {
      super("watermark", 0.006F, 0.01F, visible);
      this.themes = themes;
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      return 13.0F + vg.textWidth("fe", 18.0F) + 7.0F + vg.textWidth("client", 14.0F) + 13.0F;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      return 30.0F;
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
      Theme theme = this.themes.current();
      float cy = y + h / 2.0F;
      float breathe = (float)(0.5 + 0.5 * Math.sin(System.nanoTime() / 8.E8));
      float drift = (float)(0.5 + 0.5 * Math.sin(System.nanoTime() / 4.E8));
      int gradTop = Colors.lerp(theme.accentBright(), theme.accent(), drift);
      int gradBottom = Colors.lerp(theme.accent(), theme.accentBright(), drift);
      vg.glow(x, y, w, h, h / 2.0F, 8.0F, Colors.withAlpha(theme.accent(), 0.1F + 0.1F * breathe));
      vg.rectGradient(x, y, w, h, h / 2.0F, Colors.withAlpha(-15264995, 0.88F), Colors.withAlpha(-15856878, 0.88F), true);
      vg.rectOutline(x, y, w, h, h / 2.0F, 1.0F, Colors.withAlpha(Colors.lerp(theme.accent(), theme.accentBright(), breathe), 0.55F));
      float tx = x + 13.0F;
      vg.textGlow("fe", tx, cy, 18.0F, Colors.withAlpha(theme.accent(), 0.45F + 0.3F * breathe));
      vg.textGradient("fe", tx, cy, 18.0F, gradTop, gradBottom);
      tx += vg.textWidth("fe", 18.0F) + 7.0F;
      vg.circle(tx - 4.5F, cy, 1.4F, Colors.withAlpha(theme.textMuted(), 0.8F));
      vg.text("client", tx, cy, 14.0F, Colors.withAlpha(-856073, 0.92F));
   }
}

