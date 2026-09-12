package dev.fede.gui.widget;

import dev.fede.render.anim.Animation;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.BooleanSetting;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;

public class BooleanWidget extends SettingWidget {
   public static final float HEIGHT = 22.0F;
   private static final float BOX = 14.0F;
   private BooleanSetting setting;
   private final Animation check = new Animation(150.0F, 0.0F);

   public BooleanWidget(ThemeManager themes, BooleanSetting setting) {
      super(themes, setting);
      this.setting = setting;
      this.check.snapTo(setting.get() ? 1.0F : 0.0F);
   }

   @Override
   public float height(NVGRenderer vg) {
      return 22.0F;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme theme = this.theme();
      float cy = this.floatVal2 + 11.0F;
      vg.text(this.setting.getName(), this.floatVal, cy, 12.5F, theme.textMuted());
      this.check.setTarget(this.setting.get() ? 1.0F : 0.0F);
      float t = this.check.value();
      float bx = this.floatVal + this.width - 14.0F;
      float by = cy - 7.0F;
      int fill = Colors.lerp(Colors.withAlpha(-16777216, 0.45F), theme.accent(), t);
      vg.rect(bx, by, 14.0F, 14.0F, 4.0F, fill);
      if (t > 0.02F) {
         vg.save();
         vg.alpha(t);
         vg.checkmark(bx, by, 14.0F, 2.0F, -1);
         vg.restore();
      }

      if (t < 0.98F) {
         vg.save();
         vg.alpha(1.0F - t);
         vg.cross(bx, by, 14.0F, 1.8F, theme.textDisabled());
         vg.restore();
      }
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (button == 0 && this.contains(mx, my)) {
         this.setting.toggle();
         UiSounds.checkbox(this.setting.get());
         return true;
      } else {
         return false;
      }
   }
}

