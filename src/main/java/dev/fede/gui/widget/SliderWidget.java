package dev.fede.gui.widget;

import dev.fede.render.anim.Animation;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.SliderSetting;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;

public class SliderWidget extends SettingWidget {
   public static final float HEIGHT = 27.0F;
   private static final float BAR_HEIGHT = 5.0F;
   private static final float KNOB_RADIUS = 5.0F;
   private SliderSetting setting;
   private final Animation fill = new Animation(90.0F, 0.0F);
   private boolean dragging;

   public SliderWidget(ThemeManager themes, SliderSetting setting) {
      super(themes, setting);
      this.setting = setting;
      this.fill.snapTo((float)setting.getNormalized());
   }

   @Override
   public float height(NVGRenderer vg) {
      return 27.0F;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme theme = this.theme();
      float textY = this.floatVal2 + 8.0F;
      vg.text(this.setting.getName(), this.floatVal, textY, 12.5F, theme.textMuted());
      String value = this.setting.formatValue();
      vg.text(value, this.floatVal + this.width - vg.textWidth(value, 12.5F), textY, 12.5F, theme.textPrimary());
      float barY = this.floatVal2 + 27.0F - 5.0F - 5.0F;
      this.fill.setTarget((float)this.setting.getNormalized());
      float t = Math.clamp(this.fill.value(), 0.0F, 1.0F);
      vg.rect(this.floatVal, barY, this.width, 5.0F, 2.5F, Colors.withAlpha(-16777216, 0.45F));
      float fillW = Math.max(5.0F, t * this.width);
      vg.rectGradient(this.floatVal, barY, fillW, 5.0F, 2.5F, theme.accent(), theme.accentBright(), false);
      float knobX = this.floatVal + t * (this.width - 5.0F) + 2.5F;
      if (this.dragging) {
         vg.circleGlow(knobX, barY + 2.5F, 5.0F, 5.0F, theme.accentHover());
      }

      vg.circle(knobX, barY + 2.5F, 5.0F, -1);
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (button == 0 && this.contains(mx, my) && !(my < this.floatVal2 + 10.0F)) {
         this.dragging = true;
         this.applyMouse(mx);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void mouseDragged(float mx, float my) {
      if (this.dragging) {
         this.applyMouse(mx);
      }
   }

   @Override
   public void mouseReleased() {
      this.dragging = false;
   }

   private void applyMouse(float mx) {
      double before = this.setting.getNormalized();
      this.setting.setNormalized((mx - this.floatVal) / this.width);
      if (this.setting.getNormalized() != before) {
         UiSounds.sliderTick((float)this.setting.getNormalized());
      }
   }
}

