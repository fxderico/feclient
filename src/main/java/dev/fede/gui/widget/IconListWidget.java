package dev.fede.gui.widget;

import dev.fede.gui.ClickGuiScreen;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.IconListSetting;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;
import net.minecraft.client.MinecraftClient;

public class IconListWidget extends SettingWidget {
   private static final float ROW = 26.0F;
   private IconListSetting setting;

   public IconListWidget(ThemeManager themes, IconListSetting setting) {
      super(themes, setting);
      this.setting = setting;
   }

   @Override
   public float height(NVGRenderer vg) {
      return 26.0F;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme theme = this.theme();
      float cy = this.floatVal2 + 13.0F;
      vg.text(this.setting.getName(), this.floatVal, cy, 12.5F, theme.textMuted());
      float bx = this.floatVal + this.width - 88.0F;
      float by = cy - 18.0F / 2.0F;
      boolean hovered = mx >= bx && mx <= bx + 88.0F && my >= by && my <= by + 18.0F;
      vg.rectGradient(bx, by, 88.0F, 18.0F, 18.0F / 2.0F, theme.headerTop(), theme.headerBottom(), true);
      vg.rectOutline(bx, by, 88.0F, 18.0F, 18.0F / 2.0F, 1.1F, Colors.withAlpha(hovered ? theme.accentBright() : theme.accent(), hovered ? 0.9F : 0.4F));
      float tw = vg.textWidth("Pick", 12.0F);
      vg.textGradient("Pick", bx + (88.0F - tw) / 2.0F, by + 18.0F / 2.0F, 12.0F, theme.accentBright(), theme.accent());
      String count = this.setting.enabledCount() + "/" + this.setting.size();
      vg.text(count, bx - 8.0F - vg.textWidth(count, 11.5F), cy, 11.5F, theme.textDisabled());
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (button == 0 && this.contains(mx, my)) {
         if (MinecraftClient.getInstance().currentScreen instanceof ClickGuiScreen gui) {
            gui.openIconPicker(this.setting);
            UiSounds.select();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}

