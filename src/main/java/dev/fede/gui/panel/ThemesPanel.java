package dev.fede.gui.panel;

import dev.fede.FeClient;
import dev.fede.config.ConfigManager;
import dev.fede.gui.ClickGuiState;
import dev.fede.gui.widget.BooleanWidget;
import dev.fede.gui.widget.ColorWidget;
import dev.fede.gui.widget.SettingWidget;
import dev.fede.gui.widget.SliderWidget;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.Setting;
import dev.fede.settings.SliderSetting;
import dev.fede.theme.SoundSettings;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ThemesPanel extends Panel {
   // ThemesPanel was authored at 210px wide but the base Panel scissors and
   // draws every panel at Panel.WIDTH (175px), so the right ~35px of every row,
   // slider and the custom-theme delete cross got clipped — that's the "themes
   // tab is cut off". All the X-axis geometry below is rescaled to 175: row
   // width 198->163, widget width 182->147, right-edge refs 210->175.
   private static final float ROW_H = 26.0F;
   private static final float ADD_ROW_H = 28.0F;
   private static final float SECTION_H = 24.0F;
   private ColorWidget accentWidget;
   private ColorSetting accentProxy;
   private final List<SettingWidget> soundWidgets = new ArrayList<>();
   private static final int STARTUP_FIRST_WIDGET = 5;
   private int hoveredRow = -1;
   private float lastStartY = Float.MIN_VALUE;

   // ── Configs (cfg) section state ──────────────────────────────────────────
   private final List<String> configNames = new ArrayList<>();
   private boolean configsLoaded = false;
   private String selectedConfig = "";
   private String cfgNameInput = "";
   private boolean cfgNameFocused = false;
   // hit-test rects captured during render, consumed in mouseClicked (same
   // pattern ModeWidget uses): [x, y, w, h]
   private float[] cfgFieldBounds;
   private final float[][] cfgButtonBounds = new float[4][]; // Save, Load, Rename, Delete
   private final List<float[]> cfgRowBounds = new ArrayList<>();
   private float[] discordToggleBounds; // Discord Presence toggle hit-rect

   public ThemesPanel(ThemeManager themes, ClickGuiState state) {
      super(themes, state.panel("__themes__"));
      this.accentProxy = new ColorSetting("Accent", "Custom theme accent color", themes.current().accent()) {
         public void set(Integer value) {
            super.set(value);
            Theme current = themes.current();
            if (current.isCustom()) {
               current.setAccent(value | 0xFF000000);
            }
         }
      };
      this.accentWidget = new ColorWidget(themes, this.accentProxy);
      SoundSettings sounds = FeClient.sounds();
      if (sounds != null) {
         for (Setting<?> setting : sounds.all()) {
            if (setting instanceof SliderSetting slider) {
               this.soundWidgets.add(new SliderWidget(themes, slider));
            } else if (setting instanceof BooleanSetting bool) {
               this.soundWidgets.add(new BooleanWidget(themes, bool));
            }
         }
      }
   }

   @Override
   protected String title() {
      return "Themes";
   }

   @Override
   protected float contentHeight(NVGRenderer vg) {
      float h = 12.0F + this.themes.getThemes().size() * 26.0F + 28.0F;
      if (this.themes.current().isCustom()) {
         h += this.accentWidget.height(vg) + 6.0F;
      }

      h += 48.0F;
      h += 44.0F; // Discord section: header(24) + toggle row(20)

      for (SettingWidget widget : this.soundWidgets) {
         h += widget.height(vg) + 3.0F;
      }

      // Configs section: header(24) + name field(23) + 2 button rows(24+24) +
      // gap(6) + one row(18) per saved config (min 1 for the "none" line).
      if (!this.configsLoaded) {
         this.refreshConfigs();
      }
      h += 24.0F + 23.0F + 24.0F + 24.0F + 6.0F + Math.max(1, this.configNames.size()) * 18.0F;

      return h;
   }

   @Override
   protected void renderContent(NVGRenderer vg, float startY, float mx, float my, float viewTop, float viewBottom) {
      this.lastStartY = startY;
      Theme active = this.themes.current();
      float rowY = startY;
      int rowIndex = 0;
      int newHoveredRow = -1;

      for (Theme theme : this.themes.getThemes()) {
         float fade = this.edgeFade(rowY, rowY + 26.0F, viewTop, viewBottom);
         vg.save();
         vg.alpha(fade);
         boolean selected = theme == active;
         boolean hovered = my >= rowY
            && my <= rowY + 26.0F
            && mx >= this.clickGuiStatePanelState.floatVal + 6.0F
            && mx <= this.clickGuiStatePanelState.floatVal + 175.0F - 6.0F;
         if (hovered) {
            newHoveredRow = rowIndex;
         }

         rowIndex++;
         if (selected || hovered) {
            vg.rect(this.clickGuiStatePanelState.floatVal + 6.0F, rowY, 163.0F, 26.0F, 7.0F, Colors.withAlpha(this.theme().accent(), selected ? 0.16F : 0.08F));
         }

         float sy = rowY + 13.0F;
         vg.circle(this.clickGuiStatePanelState.floatVal + 20.0F, sy, 6.0F, theme.accent());
         if (selected) {
            vg.rectOutline(this.clickGuiStatePanelState.floatVal + 20.0F - 9.0F, sy - 9.0F, 18.0F, 18.0F, 9.0F, 1.5F, this.theme().accentBright());
         }

         vg.text(theme.getName(), this.clickGuiStatePanelState.floatVal + 36.0F, sy, 13.5F, selected ? this.theme().textPrimary() : this.theme().textMuted());
         if (theme.isCustom()) {
            vg.cross(this.clickGuiStatePanelState.floatVal + 175.0F - 28.0F, sy - 6.0F, 12.0F, 1.6F, this.theme().textDisabled());
         }

         vg.restore();
         rowY += 26.0F;
      }

      if (active.isCustom()) {
         this.accentWidget.setBounds(this.clickGuiStatePanelState.floatVal + 14.0F, rowY + 3.0F, 147.0F);
         this.accentWidget.render(vg, mx, my);
         rowY += this.accentWidget.height(vg) + 6.0F;
      }

      float fadex = this.edgeFade(rowY, rowY + 28.0F, viewTop, viewBottom);
      vg.save();
      vg.alpha(fadex);
      boolean hoveredx = my >= rowY
         && my <= rowY + 28.0F - 4.0F
         && mx >= this.clickGuiStatePanelState.floatVal + 6.0F
         && mx <= this.clickGuiStatePanelState.floatVal + 175.0F - 6.0F;
      vg.rect(this.clickGuiStatePanelState.floatVal + 6.0F, rowY, 163.0F, 24.0F, 7.0F, Colors.withAlpha(this.theme().accent(), hoveredx ? 0.22F : 0.12F));
      String label = "+  Add Custom";
      vg.text(
         label,
         this.clickGuiStatePanelState.floatVal + (175.0F - vg.textWidth(label, 13.0F)) / 2.0F,
         rowY + 12.0F,
         13.0F,
         hoveredx ? this.theme().accentBright() : this.theme().textPrimary()
      );
      vg.restore();
      if (hoveredx) {
         newHoveredRow = 999;
      }

      rowY += 28.0F;

      // ── Discord Presence toggle ──
      rowY = this.sectionHeader(vg, "Discord", rowY, viewTop, viewBottom);
      {
         float px = this.clickGuiStatePanelState.floatVal;
         Theme t = this.theme();
         boolean on = this.themes.isDiscordPresence();
         float ty = rowY;
         vg.text("Discord Presence", px + 14.0F, ty + 9.0F, 12.5F, t.textMuted());
         // toggle pill on the right
         float pw = 26.0F;
         float ph = 14.0F;
         float pxx = px + 175.0F - 14.0F - pw;
         float py = ty + 2.0F;
         vg.rect(pxx, py, pw, ph, ph / 2.0F, Colors.withAlpha(on ? t.accent() : -16777216, on ? 0.55F : 0.45F));
         float knob = ph - 4.0F;
         float kx = on ? pxx + pw - knob - 2.0F : pxx + 2.0F;
         vg.circle(kx + knob / 2.0F, py + ph / 2.0F, knob / 2.0F, on ? t.accentBright() : t.textDisabled());
         this.discordToggleBounds = new float[]{px + 14.0F, ty, 175.0F - 28.0F, 18.0F};
         rowY += 20.0F;
      }

      rowY = this.sectionHeader(vg, "Sounds", rowY, viewTop, viewBottom);

      for (int i = 0; i < this.soundWidgets.size(); i++) {
         if (i == 5) {
            rowY = this.sectionHeader(vg, "Startup Sound", rowY, viewTop, viewBottom);
         }

         SettingWidget widget = this.soundWidgets.get(i);
         widget.setBounds(this.clickGuiStatePanelState.floatVal + 14.0F, rowY, 147.0F);
         float wFade = this.edgeFade(rowY, rowY + widget.height(vg), viewTop, viewBottom);
         vg.save();
         vg.alpha(wFade);
         widget.render(vg, mx, my);
         vg.restore();
         rowY += widget.height(vg) + 3.0F;
      }

      rowY = this.renderConfigsSection(vg, rowY, mx, my, viewTop, viewBottom);

      if (newHoveredRow != this.hoveredRow && newHoveredRow != -1) {
         UiSounds.hover();
      }

      this.hoveredRow = newHoveredRow;
   }

   private float renderConfigsSection(NVGRenderer vg, float rowY, float mx, float my, float viewTop, float viewBottom) {
      if (!this.configsLoaded) {
         this.refreshConfigs();
      }

      float px = this.clickGuiStatePanelState.floatVal;
      Theme theme = this.theme();
      rowY = this.sectionHeader(vg, "Configs", rowY, viewTop, viewBottom);

      // name input field
      float fx = px + 14.0F;
      float fw = 147.0F;
      float fh = 18.0F;
      this.cfgFieldBounds = new float[]{fx, rowY, fw, fh};
      vg.rect(fx, rowY, fw, fh, 9.0F, Colors.withAlpha(-16777216, 0.45F));
      vg.rectOutline(fx, rowY, fw, fh, 9.0F, 1.1F, Colors.withAlpha(this.cfgNameFocused ? theme.accentBright() : theme.accent(), this.cfgNameFocused ? 0.9F : 0.35F));
      if (this.cfgNameInput.isEmpty() && !this.cfgNameFocused) {
         vg.text("config name…", fx + 8.0F, rowY + 9.0F, 12.0F, theme.textDisabled());
      } else {
         float tw = vg.text(this.cfgNameInput, fx + 8.0F, rowY + 9.0F, 12.0F, theme.textPrimary());
         if (this.cfgNameFocused && System.nanoTime() / 400000000L % 2L == 0L) {
            vg.rect(fx + 8.0F + tw + 1.5F, rowY + 3.0F, 1.4F, 12.0F, 0.7F, theme.accentBright());
         }
      }
      rowY += fh + 5.0F;

      // action buttons: [Save] [Load] / [Rename] [Delete]
      float bw = (fw - 5.0F) / 2.0F;
      float bh = 18.0F;
      float gap = 5.0F;
      this.cfgButtonBounds[0] = this.drawCfgButton(vg, fx, rowY, bw, bh, "Save", mx, my);
      this.cfgButtonBounds[1] = this.drawCfgButton(vg, fx + bw + gap, rowY, bw, bh, "Load", mx, my);
      rowY += bh + gap;
      this.cfgButtonBounds[2] = this.drawCfgButton(vg, fx, rowY, bw, bh, "Rename", mx, my);
      this.cfgButtonBounds[3] = this.drawCfgButton(vg, fx + bw + gap, rowY, bw, bh, "Delete", mx, my);
      rowY += bh + 6.0F;

      // saved-config list (click a row to select it — fills the name field)
      this.cfgRowBounds.clear();
      if (this.configNames.isEmpty()) {
         vg.text("no saved configs", fx + 2.0F, rowY + 9.0F, 11.5F, theme.textDisabled());
         rowY += 18.0F;
      } else {
         for (String name : this.configNames) {
            boolean sel = name.equals(this.selectedConfig);
            boolean hov = my >= rowY && my <= rowY + 18.0F && mx >= fx && mx <= fx + fw;
            if (sel || hov) {
               vg.rect(fx, rowY, fw, 18.0F, 6.0F, Colors.withAlpha(theme.accent(), sel ? 0.18F : 0.08F));
            }

            vg.text(name, fx + 6.0F, rowY + 9.0F, 12.0F, sel ? theme.textPrimary() : theme.textMuted());
            this.cfgRowBounds.add(new float[]{fx, rowY, fw, 18.0F});
            rowY += 18.0F;
         }
      }

      return rowY;
   }

   private float[] drawCfgButton(NVGRenderer vg, float x, float y, float w, float h, String label, float mx, float my) {
      Theme t = this.theme();
      boolean hov = mx >= x && mx <= x + w && my >= y && my <= y + h;
      vg.rect(x, y, w, h, h / 2.0F, Colors.withAlpha(t.accent(), hov ? 0.28F : 0.14F));
      vg.text(label, x + (w - vg.textWidth(label, 11.5F)) / 2.0F, y + h / 2.0F, 11.5F, hov ? t.accentBright() : t.textPrimary());
      return new float[]{x, y, w, h};
   }

   private void refreshConfigs() {
      ConfigManager cfg = FeClient.config();
      this.configNames.clear();
      if (cfg != null) {
         this.configNames.addAll(cfg.listConfigs());
      }

      this.configsLoaded = true;
   }

   private static boolean cfgHit(float mx, float my, float[] b) {
      return b != null && mx >= b[0] && mx <= b[0] + b[2] && my >= b[1] && my <= b[1] + b[3];
   }

   private boolean handleConfigClick(float mx, float my, int button) {
      if (button != 0) {
         return false;
      }

      ConfigManager cfg = FeClient.config();
      if (cfg == null) {
         return false;
      }

      if (cfgHit(mx, my, this.cfgFieldBounds)) {
         this.cfgNameFocused = true;
         UiSounds.select();
         return true;
      }

      String typed = this.cfgNameInput.trim();
      if (cfgHit(mx, my, this.cfgButtonBounds[0])) { // Save current state under the typed name
         if (!typed.isEmpty() && cfg.saveConfig(typed)) {
            this.selectedConfig = typed;
            this.refreshConfigs();
         }
         this.cfgNameFocused = false;
         UiSounds.select();
         return true;
      }
      if (cfgHit(mx, my, this.cfgButtonBounds[1])) { // Load typed-or-selected
         String target = !typed.isEmpty() ? typed : this.selectedConfig;
         if (!target.isEmpty() && cfg.loadConfig(target)) {
            this.selectedConfig = target;
         }
         this.cfgNameFocused = false;
         UiSounds.select();
         return true;
      }
      if (cfgHit(mx, my, this.cfgButtonBounds[2])) { // Rename selected -> typed
         if (!this.selectedConfig.isEmpty() && !typed.isEmpty() && cfg.renameConfig(this.selectedConfig, typed)) {
            this.selectedConfig = typed;
            this.refreshConfigs();
         }
         this.cfgNameFocused = false;
         UiSounds.select();
         return true;
      }
      if (cfgHit(mx, my, this.cfgButtonBounds[3])) { // Delete selected-or-typed
         String target = !this.selectedConfig.isEmpty() ? this.selectedConfig : typed;
         if (!target.isEmpty() && cfg.deleteConfig(target)) {
            if (target.equals(this.selectedConfig)) {
               this.selectedConfig = "";
            }
            this.refreshConfigs();
         }
         this.cfgNameFocused = false;
         UiSounds.select();
         return true;
      }

      for (int i = 0; i < this.cfgRowBounds.size() && i < this.configNames.size(); i++) {
         if (cfgHit(mx, my, this.cfgRowBounds.get(i))) {
            this.selectedConfig = this.configNames.get(i);
            this.cfgNameInput = this.configNames.get(i);
            UiSounds.select();
            return true;
         }
      }

      return false;
   }

   private float sectionHeader(NVGRenderer vg, String title, float rowY, float viewTop, float viewBottom) {
      float fade = this.edgeFade(rowY, rowY + 24.0F, viewTop, viewBottom);
      vg.save();
      vg.alpha(fade);
      float cy = rowY + 12.0F + 3.0F;
      vg.textGradient(
         title.toUpperCase(Locale.ROOT), this.clickGuiStatePanelState.floatVal + 14.0F, cy, 12.0F, this.theme().accentBright(), this.theme().accent()
      );
      float lineX = this.clickGuiStatePanelState.floatVal + 14.0F + vg.textWidth(title.toUpperCase(Locale.ROOT), 12.0F) + 8.0F;
      vg.rect(
         lineX,
         cy - 0.5F,
         Math.max(0.0F, this.clickGuiStatePanelState.floatVal + 175.0F - 14.0F - lineX),
         1.0F,
         0.5F,
         Colors.withAlpha(this.theme().accent(), 0.3F)
      );
      vg.restore();
      return rowY + 24.0F;
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (this.accentWidget.mouseClicked(mx, my, button)) {
         return true;
      } else {
         for (SettingWidget widget : this.soundWidgets) {
            if (widget.mouseClicked(mx, my, button)) {
               return true;
            }
         }

         if (this.handleConfigClick(mx, my, button)) {
            return true;
         }

         if (button == 0 && cfgHit(mx, my, this.discordToggleBounds)) {
            this.themes.setDiscordPresence(!this.themes.isDiscordPresence());
            UiSounds.select();
            return true;
         }

         // a left-click that missed the name field unfocuses it
         if (button == 0 && !cfgHit(mx, my, this.cfgFieldBounds)) {
            this.cfgNameFocused = false;
         }

         if (button != 0) {
            return false;
         } else {
            float rowY = this.firstRowY();
            if (rowY == Float.MIN_VALUE) {
               return false;
            } else {
               for (Theme theme : this.themes.getThemes()) {
                  if (my >= rowY
                     && my <= rowY + 26.0F
                     && mx >= this.clickGuiStatePanelState.floatVal + 6.0F
                     && mx <= this.clickGuiStatePanelState.floatVal + 175.0F - 6.0F) {
                     if (theme.isCustom() && mx >= this.clickGuiStatePanelState.floatVal + 175.0F - 34.0F) {
                        this.themes.removeCustom(theme);
                     } else {
                        this.themes.select(theme);
                        if (theme.isCustom()) {
                           this.accentProxy.set(theme.accent());
                        }
                     }

                     return true;
                  }

                  rowY += 26.0F;
               }

               if (this.themes.current().isCustom()) {
                  rowY += this.accentWidget.height(null) + 6.0F;
               }

               if (my >= rowY
                  && my <= rowY + 28.0F - 4.0F
                  && mx >= this.clickGuiStatePanelState.floatVal + 6.0F
                  && mx <= this.clickGuiStatePanelState.floatVal + 175.0F - 6.0F) {
                  Theme custom = this.themes.addCustom(this.themes.current().accent());
                  this.themes.select(custom);
                  this.accentProxy.set(custom.accent());
                  return true;
               } else {
                  return false;
               }
            }
         }
      }
   }

   private float firstRowY() {
      return this.lastStartY;
   }

   @Override
   public void mouseDragged(float mx, float my) {
      this.accentWidget.mouseDragged(mx, my);

      for (SettingWidget widget : this.soundWidgets) {
         widget.mouseDragged(mx, my);
      }
   }

   @Override
   public void mouseReleased() {
      this.accentWidget.mouseReleased();

      for (SettingWidget widget : this.soundWidgets) {
         widget.mouseReleased();
      }
   }

   @Override
   public boolean keyPressed(int keyCode) {
      if (this.cfgNameFocused) {
         switch (keyCode) {
            case 256: // escape
            case 257: // enter
            case 335: // numpad enter
               this.cfgNameFocused = false;
               break;
            case 259: // backspace
               if (!this.cfgNameInput.isEmpty()) {
                  this.cfgNameInput = this.cfgNameInput.substring(0, this.cfgNameInput.length() - 1);
               }
         }

         return true;
      }

      return this.accentWidget.keyPressed(keyCode);
   }

   @Override
   public boolean charTyped(int codepoint) {
      if (this.cfgNameFocused) {
         if (this.cfgNameInput.length() < 24 && Character.isValidCodePoint(codepoint) && !Character.isISOControl(codepoint)) {
            this.cfgNameInput = this.cfgNameInput + new String(Character.toChars(codepoint));
         }

         return true;
      }

      return false;
   }

   @Override
   public boolean isListening() {
      return this.cfgNameFocused || this.accentWidget.isListening();
   }
}



