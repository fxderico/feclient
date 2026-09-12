package dev.fede.gui.widget;

import dev.fede.render.anim.Animation;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.ColorSetting;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;

public class ColorWidget extends SettingWidget {
   private static final float ROW = 22.0F;
   private static final float SV_H = 74.0F;
   private static final float HUE_H = 10.0F;
   private static final float BOTTOM_H = 22.0F;
   private static final float GAP = 6.0F;
   private ColorSetting setting;
   private final Animation expand = new Animation(170.0F, 0.0F);
   private boolean expanded;
   private float hue;
   private float sat;
   private float val;
   private boolean draggingSv;
   private boolean draggingHue;
   private boolean hexFocused;
   private final StringBuilder hexBuffer = new StringBuilder();

   public ColorWidget(ThemeManager themes, ColorSetting setting) {
      super(themes, setting);
      this.setting = setting;
      this.syncFromSetting();
   }

   private void syncFromSetting() {
      float[] hsv = Colors.rgbToHsv(this.setting.get());
      this.hue = hsv[0];
      this.sat = hsv[1];
      this.val = hsv[2];
   }

   public void setExpanded(boolean value) {
      if (value && !this.expanded) {
         this.syncFromSetting();
      }

      this.expanded = value;
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   private void apply() {
      this.setting.set(Colors.hsvToRgb(this.hue, this.sat, this.val));
   }

   @Override
   public float height(NVGRenderer vg) {
      return 22.0F + this.expand.value() * 124.0F;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme theme = this.theme();
      float cy = this.floatVal2 + 11.0F;
      vg.text(this.setting.getName(), this.floatVal, cy, 12.5F, theme.textMuted());
      vg.rect(this.floatVal + this.width - 26.0F, cy - 14.0F / 2.0F, 26.0F, 14.0F, 5.0F, this.setting.get() | 0xFF000000);
      vg.rectOutline(this.floatVal + this.width - 26.0F, cy - 14.0F / 2.0F, 26.0F, 14.0F, 5.0F, 1.0F, Colors.withAlpha(-1, 0.25F));
      this.expand.setTarget(this.expanded ? 1.0F : 0.0F);
      float t = this.expand.value();
      if (!(t < 0.01F)) {
         vg.save();
         vg.scissor(this.floatVal - 4.0F, this.floatVal2 + 22.0F, this.width + 8.0F, t * 124.0F);
         vg.alpha(t);
         float svY = this.svTop();
         int hueColor = Colors.hsvToRgb(this.hue, 1.0F, 1.0F);
         vg.rect(this.floatVal, svY, this.width, 74.0F, 6.0F, hueColor);
         vg.rectGradient(this.floatVal, svY, this.width, 74.0F, 6.0F, -1, 16777215, false);
         vg.rectGradient(this.floatVal, svY, this.width, 74.0F, 6.0F, 0, -16777216, true);
         float knobX = this.floatVal + this.sat * this.width;
         float knobY = svY + (1.0F - this.val) * 74.0F;
         vg.circle(knobX, knobY, 6.0F, -1);
         vg.circle(knobX, knobY, 4.2F, Colors.hsvToRgb(this.hue, this.sat, this.val));
         float hueY = this.hueTop();
         float seg = this.width / 6.0F;

         for (int i = 0; i < 6; i++) {
            int from = Colors.hsvToRgb(i * 60, 1.0F, 1.0F);
            int to = Colors.hsvToRgb((i + 1) * 60 % 360 == 0 ? 359.9F : (i + 1) * 60, 1.0F, 1.0F);
            vg.rectGradient(this.floatVal + i * seg, hueY, seg + 0.5F, 10.0F, 0.0F, from, to, false);
         }

         vg.rectOutline(this.floatVal, hueY, this.width, 10.0F, 5.0F, 1.5F, Colors.withAlpha(-15988208, 0.9F));
         float hueKnobX = this.floatVal + this.hue / 360.0F * this.width;
         vg.circle(hueKnobX, hueY + 5.0F, 6.0F, -1);
         vg.circle(hueKnobX, hueY + 5.0F, 4.2F, hueColor);
         float botY = this.bottomTop();
         vg.rect(this.floatVal, botY, 30.0F, 18.0F, 5.0F, this.setting.get() | 0xFF000000);
         vg.rectOutline(this.floatVal, botY, 30.0F, 18.0F, 5.0F, 1.0F, Colors.withAlpha(-1, 0.25F));
         float hexX = this.floatVal + 38.0F;
         float hexW = this.width - 38.0F;
         int boxFill = this.hexFocused ? Colors.withAlpha(theme.accent(), 0.18F) : Colors.withAlpha(-16777216, 0.45F);
         vg.rect(hexX, botY, hexW, 18.0F, 5.0F, boxFill);
         if (this.hexFocused) {
            vg.rectOutline(hexX, botY, hexW, 18.0F, 5.0F, 1.2F, theme.accentBright());
         }

         String text = this.hexFocused ? "#" + this.hexBuffer + (System.nanoTime() / 400000000L % 2L == 0L ? "_" : "") : this.setting.hex();
         vg.text(text, hexX + 8.0F, botY + 9.0F, 12.0F, this.hexFocused ? theme.textPrimary() : theme.textMuted());
         vg.restore();
      }
   }

   private float svTop() {
      return this.floatVal2 + 22.0F + 2.0F;
   }

   private float hueTop() {
      return this.svTop() + 74.0F + 6.0F;
   }

   private float bottomTop() {
      return this.hueTop() + 10.0F + 6.0F;
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (button != 0) {
         return false;
      } else if (my >= this.floatVal2 && my <= this.floatVal2 + 22.0F && mx >= this.floatVal && mx <= this.floatVal + this.width) {
         this.expanded = !this.expanded;
         if (this.expanded) {
            this.syncFromSetting();
         }

         this.hexFocused = false;
         UiSounds.select();
         return true;
      } else if (!this.expanded) {
         return false;
      } else if (inRect(mx, my, this.floatVal, this.svTop(), this.width, 74.0F)) {
         this.draggingSv = true;
         this.applySv(mx, my);
         return true;
      } else if (inRect(mx, my, this.floatVal, this.hueTop() - 3.0F, this.width, 16.0F)) {
         this.draggingHue = true;
         this.applyHue(mx);
         return true;
      } else if (inRect(mx, my, this.floatVal + 38.0F, this.bottomTop(), this.width - 38.0F, 18.0F)) {
         this.hexFocused = true;
         this.hexBuffer.setLength(0);
         UiSounds.select();
         return true;
      } else {
         if (this.hexFocused) {
            this.commitHex();
         }

         return false;
      }
   }

   private static boolean inRect(float mx, float my, float rx, float ry, float rw, float rh) {
      return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
   }

   private void applySv(float mx, float my) {
      this.sat = Math.clamp((mx - this.floatVal) / this.width, 0.0F, 1.0F);
      this.val = 1.0F - Math.clamp((my - this.svTop()) / 74.0F, 0.0F, 1.0F);
      this.apply();
   }

   private void applyHue(float mx) {
      this.hue = Math.clamp((mx - this.floatVal) / this.width, 0.0F, 1.0F) * 359.9F;
      this.apply();
   }

   @Override
   public void mouseDragged(float mx, float my) {
      if (this.draggingSv) {
         this.applySv(mx, my);
      }

      if (this.draggingHue) {
         this.applyHue(mx);
      }
   }

   @Override
   public void mouseReleased() {
      this.draggingSv = false;
      this.draggingHue = false;
   }

   @Override
   public boolean keyPressed(int keyCode) {
      if (!this.hexFocused) {
         return false;
      } else if (keyCode == 256) {
         this.hexFocused = false;
         return true;
      } else if (keyCode == 257 || keyCode == 335) {
         this.commitHex();
         return true;
      } else if (keyCode == 259) {
         if (!this.hexBuffer.isEmpty()) {
            this.hexBuffer.deleteCharAt(this.hexBuffer.length() - 1);
         }

         return true;
      } else {
         char c = hexChar(keyCode);
         if (c != 0 && this.hexBuffer.length() < 6) {
            this.hexBuffer.append(c);
            if (this.hexBuffer.length() == 6) {
               this.commitHex();
            }
         }

         return true;
      }
   }

   private static char hexChar(int keyCode) {
      if (keyCode >= 48 && keyCode <= 57) {
         return (char)(48 + keyCode - 48);
      } else if (keyCode >= 320 && keyCode <= 329) {
         return (char)(48 + keyCode - 320);
      } else {
         return keyCode >= 65 && keyCode <= 70 ? (char)(65 + keyCode - 65) : '\u0000';
      }
   }

   private void commitHex() {
      this.hexFocused = false;
      if (this.hexBuffer.length() == 6) {
         try {
            this.setting.set(0xFF000000 | Integer.parseInt(this.hexBuffer.toString(), 16));
            this.syncFromSetting();
            UiSounds.keybindSet();
         } catch (NumberFormatException var2) {
         }
      }

      this.hexBuffer.setLength(0);
   }

   @Override
   public boolean isListening() {
      return this.hexFocused;
   }
}

