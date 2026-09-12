package dev.fede.gui.widget;

import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.ModeSetting;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.UiSounds;
import java.util.ArrayList;
import java.util.List;

public class ModeWidget extends SettingWidget {
   private static final float LINE_HEIGHT = 16.0F;
   private static final float FONT_SIZE = 12.5F;
   private static final float GAP = 10.0F;
   private ModeSetting setting;
   private final List<float[]> optionBounds = new ArrayList<>();
   private int lines = 1;

   public ModeWidget(ThemeManager themes, ModeSetting setting) {
      super(themes, setting);
      this.setting = setting;
   }

   @Override
   public float height(NVGRenderer vg) {
      return 17.0F + this.lines * 16.0F + 3.0F;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme theme = this.theme();
      vg.text(this.setting.getName(), this.floatVal, this.floatVal2 + 8.0F, 12.5F, theme.textMuted());
      this.optionBounds.clear();
      float cx = this.floatVal;
      float cy = this.floatVal2 + 17.0F + 8.0F;
      this.lines = 1;

      for (String mode : this.setting.getModes()) {
         float w = vg.textWidth(mode, 12.5F);
         if (cx + w > this.floatVal + this.width && cx > this.floatVal) {
            cx = this.floatVal;
            cy += 16.0F;
            this.lines++;
         }

         boolean selected = this.setting.check(mode);
         boolean hovered = mx >= cx && mx <= cx + w && my >= cy - 8.0F && my <= cy + 8.0F;
         if (selected) {
            vg.textGradient(mode, cx, cy, 12.5F, theme.accentBright(), theme.accent());
         } else {
            vg.text(mode, cx, cy, 12.5F, hovered ? theme.textPrimary() : theme.textDisabled());
         }

         this.optionBounds.add(new float[]{cx, cy - 8.0F, w});
         cx += w + 10.0F;
      }
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (button != 0) {
         return false;
      } else {
         List<String> modes = this.setting.getModes();

         for (int i = 0; i < this.optionBounds.size() && i < modes.size(); i++) {
            float[] b = this.optionBounds.get(i);
            if (mx >= b[0] && mx <= b[0] + b[2] && my >= b[1] && my <= b[1] + 16.0F) {
               this.setting.set(modes.get(i));
               UiSounds.select();
               return true;
            }
         }

         return false;
      }
   }
}

