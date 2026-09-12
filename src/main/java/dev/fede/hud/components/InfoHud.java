package dev.fede.hud.components;

import dev.fede.hud.HudComponent;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class InfoHud extends HudComponent {
   private static final float HEIGHT = 22.0F;
   private static final float FONT_SIZE = 13.0F;
   private static final float PAD_X = 9.0F;
   private ThemeManager themes;
   private String label;
   private Supplier<String> value;

   public InfoHud(String id, ThemeManager themes, String label, Supplier<String> value, float defaultFx, float defaultFy, BooleanSupplier visible) {
      super(id, defaultFx, defaultFy, visible);
      this.themes = themes;
      this.label = label;
      this.value = value;
   }

   private String currentValue() {
      try {
         return this.value.get();
      } catch (Exception var2) {
         return "?";
      }
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      return 9.0F + vg.textWidth(this.label, 13.0F) + 5.0F + vg.textWidth(this.currentValue(), 13.0F) + 9.0F;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      return 22.0F;
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
      Theme theme = this.themes.current();
      float cy = y + h / 2.0F;
      vg.rectGradient(x, y, w, h, h / 2.0F, theme.background(), theme.backgroundTo(), true);
      float tx = x + 9.0F;
      tx += vg.textGradient(this.label, tx, cy, 13.0F, theme.accentBright(), theme.accent());
      vg.text(this.currentValue(), tx + 5.0F, cy, 13.0F, theme.textPrimary());
   }
}

