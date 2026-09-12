package dev.fede.gui.widget;

import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.Setting;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;

public abstract class SettingWidget {
   protected ThemeManager themes;
   private final Setting<?> boundSetting;
   protected float floatVal;
   protected float floatVal2;
   protected float width;

   protected SettingWidget(ThemeManager themes, Setting<?> boundSetting) {
      this.themes = themes;
      this.boundSetting = boundSetting;
   }

   public boolean isVisible() {
      return this.boundSetting == null || this.boundSetting.isVisible();
   }

   protected Theme theme() {
      return this.themes.current();
   }

   public void setBounds(float x, float y, float width) {
      this.floatVal = x;
      this.floatVal2 = y;
      this.width = width;
   }

   public boolean contains(float mx, float my) {
      return mx >= this.floatVal && mx <= this.floatVal + this.width && my >= this.floatVal2 && my <= this.floatVal2 + this.height(null);
   }

   public abstract float height(NVGRenderer var1);

   public abstract void render(NVGRenderer var1, float var2, float var3);

   public boolean mouseClicked(float mx, float my, int button) {
      return false;
   }

   public void mouseDragged(float mx, float my) {
   }

   public void mouseReleased() {
   }

   public boolean keyPressed(int keyCode) {
      return false;
   }

   public boolean charTyped(int codepoint) {
      return false;
   }

   public boolean isListening() {
      return false;
   }
}

