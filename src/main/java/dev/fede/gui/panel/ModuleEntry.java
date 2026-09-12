package dev.fede.gui.panel;

import dev.fede.gui.ClickGuiState;
import dev.fede.gui.widget.BlockListWidget;
import dev.fede.gui.widget.BooleanWidget;
import dev.fede.gui.widget.ColorWidget;
import dev.fede.gui.widget.IconListWidget;
import dev.fede.gui.widget.KeybindWidget;
import dev.fede.gui.widget.ModeWidget;
import dev.fede.gui.widget.SettingWidget;
import dev.fede.gui.widget.SliderWidget;
import dev.fede.gui.widget.StringWidget;
import dev.fede.module.Module;
import dev.fede.render.anim.Animation;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.BlockListSetting;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.IconListSetting;
import dev.fede.settings.KeybindSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.Setting;
import dev.fede.settings.SliderSetting;
import dev.fede.settings.StringSetting;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;
import java.util.ArrayList;
import java.util.List;

public class ModuleEntry {
   public static final float ROW_H = 26.0F;
   private static final float RADIUS = 8.0F;
   private static final float SETTING_INDENT = 12.0F;
   private Module module;
   private ThemeManager themes;
   private ClickGuiState state;
   private String key;
   private final List<SettingWidget> widgets = new ArrayList<>();
   private final Animation hover = new Animation(140.0F, 0.0F);
   private final Animation enable = new Animation(160.0F, 0.0F);
   private Animation expand;
   private float floatVal;
   private float floatVal2;
   private float width;

   public ModuleEntry(Module module, ThemeManager themes, ClickGuiState state) {
      this.module = module;
      this.themes = themes;
      this.state = state;
      this.key = module.getName() + "@" + module.getCategory().name();
      this.expand = new Animation(190.0F, state.isExpanded(this.key) ? 1.0F : 0.0F);
      this.enable.snapTo(module.isEnabled() ? 1.0F : 0.0F);

      for (Setting<?> setting : module.getSettings()) {
         if (setting instanceof BooleanSetting b) {
            this.widgets.add(new BooleanWidget(themes, b));
         } else if (setting instanceof SliderSetting s) {
            this.widgets.add(new SliderWidget(themes, s));
         } else if (setting instanceof ModeSetting m) {
            this.widgets.add(new ModeWidget(themes, m));
         } else if (setting instanceof ColorSetting c) {
            this.widgets.add(new ColorWidget(themes, c));
         } else if (setting instanceof BlockListSetting bl) {
            this.widgets.add(new BlockListWidget(themes, bl));
         } else if (setting instanceof IconListSetting il) {
            this.widgets.add(new IconListWidget(themes, il));
         } else if (setting instanceof StringSetting str) {
            this.widgets.add(new StringWidget(themes, str));
         } else if (setting instanceof KeybindSetting k) {
            this.widgets.add(new KeybindWidget(themes, k));
         }
      }

      this.widgets.add(new KeybindWidget(themes, module.getKeybind()));
   }

   public Module getModule() {
      return this.module;
   }

   public void setBounds(float x, float y, float width) {
      this.floatVal = x;
      this.floatVal2 = y;
      this.width = width;
   }

   private float settingsHeight(NVGRenderer vg) {
      float h = 6.0F;

      for (SettingWidget widget : this.widgets) {
         if (widget.isVisible()) {
            h += widget.height(vg) + 3.0F;
         }
      }

      return h + 3.0F;
   }

   public float height(NVGRenderer vg) {
      float t = this.expand.value();
      return 26.0F + (t <= 0.005F ? 0.0F : t * this.settingsHeight(vg));
   }

   public void render(NVGRenderer vg, float mx, float my, float fadeAlpha) {
      Theme theme = this.theme();
      boolean hovered = mx >= this.floatVal && mx <= this.floatVal + this.width && my >= this.floatVal2 && my <= this.floatVal2 + 26.0F;
      if (hovered && this.hover.getTarget() < 0.5F) {
         UiSounds.hover();
      }

      this.hover.setTarget(hovered ? 1.0F : 0.0F);
      this.enable.setTarget(this.module.isEnabled() ? 1.0F : 0.0F);
      float hoverT = this.hover.value();
      float enableT = this.enable.value();
      float expandT = this.expand.value();
      vg.save();
      vg.alpha(fadeAlpha);
      if (enableT > 0.01F) {
         vg.save();
         vg.alpha(enableT);
         vg.rect(this.floatVal, this.floatVal2, this.width, 26.0F, 8.0F, theme.moduleActiveFill());
         vg.glow(this.floatVal, this.floatVal2, this.width, 26.0F, 8.0F, 4.0F, Colors.withAlpha(theme.accent(), 0.16F * enableT));
         vg.restore();
      }

      if (hoverT > 0.01F) {
         vg.rect(this.floatVal, this.floatVal2, this.width, 26.0F, 8.0F, Colors.withAlpha(theme.accent(), 0.1F * hoverT));
      }

      float textY = this.floatVal2 + 15.0F;
      String displayName = this.module.getDisplayName();

      if (enableT > 0.01F) {
         vg.save();
         vg.alpha(enableT);
         vg.textGlow(displayName, this.floatVal + 10.0F, textY, 14.5F, Colors.withAlpha(theme.accent(), 0.75F));
         vg.textGradient(displayName, this.floatVal + 10.0F, textY, 14.5F, theme.accentBright(), theme.accent());
         vg.restore();
      }

      if (enableT < 0.99F) {
         vg.save();
         vg.alpha(1.0F - enableT);
         int idle = Colors.lerp(theme.textMuted(), theme.textPrimary(), hoverT);
         vg.text(displayName, this.floatVal + 10.0F, textY, 14.5F, idle);
         vg.restore();
      }

      float dotX = this.floatVal + this.width - 12.0F;
      int dot = Colors.lerp(theme.statusDisabled(), theme.statusEnabled(), enableT);
      if (enableT > 0.3F) {
         vg.circleGlow(dotX, textY, 3.0F, 4.0F, Colors.withAlpha(dot, 0.5F * enableT));
      }

      vg.circle(dotX, textY, 3.0F, dot);
      if (expandT > 0.005F) {
         float settingsH = this.settingsHeight(vg) * expandT;
         vg.save();
         vg.scissor(this.floatVal, this.floatVal2 + 26.0F, this.width, settingsH);
         vg.alpha(expandT);
         vg.rect(this.floatVal + 4.0F, this.floatVal2 + 26.0F - 4.0F, this.width - 8.0F, settingsH + 0.0F, 6.0F, Colors.withAlpha(-15988208, 0.55F));
         float wy = this.floatVal2 + 26.0F + 6.0F;

         for (SettingWidget widget : this.widgets) {
            if (widget.isVisible()) {
               widget.setBounds(this.floatVal + 12.0F, wy, this.width - 24.0F);
               widget.render(vg, mx, my);
               wy += widget.height(vg) + 3.0F;
            }
         }

         vg.restore();
      }

      vg.restore();
   }

   private Theme theme() {
      return this.themes.current();
   }

   public boolean mouseClicked(float mx, float my, int button) {
      if (mx >= this.floatVal && mx <= this.floatVal + this.width && my >= this.floatVal2 && my <= this.floatVal2 + 26.0F) {
         if (button == 0) {
            this.module.toggle();
            UiSounds.toggle(this.module.isEnabled());
         } else if (button == 1) {
            boolean expanded = !(this.expand.getTarget() > 0.5F);
            this.expand.setTarget(expanded ? 1.0F : 0.0F);
            this.state.setExpanded(this.key, expanded);
            UiSounds.select();
         }

         return true;
      } else if (this.expand.getTarget() > 0.5F && my >= this.floatVal2 + 26.0F && my <= this.floatVal2 + this.height(null)) {
         for (SettingWidget widget : this.widgets) {
            if (widget.isVisible() && widget.mouseClicked(mx, my, button)) {
               return true;
            }
         }

         return mx >= this.floatVal && mx <= this.floatVal + this.width;
      } else {
         return false;
      }
   }

   public void mouseDragged(float mx, float my) {
      for (SettingWidget widget : this.widgets) {
         widget.mouseDragged(mx, my);
      }
   }

   public void mouseReleased() {
      for (SettingWidget widget : this.widgets) {
         widget.mouseReleased();
      }
   }

   public boolean keyPressed(int keyCode) {
      for (SettingWidget widget : this.widgets) {
         if (widget.keyPressed(keyCode)) {
            return true;
         }
      }

      return false;
   }

   public boolean charTyped(int codepoint) {
      for (SettingWidget widget : this.widgets) {
         if (widget.charTyped(codepoint)) {
            return true;
         }
      }

      return false;
   }

   public boolean isListening() {
      for (SettingWidget widget : this.widgets) {
         if (widget.isListening()) {
            return true;
         }
      }

      return false;
   }
}

