package dev.fede.gui.panel;

import dev.fede.FeClient;
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

      for (SettingWidget widget : this.soundWidgets) {
         h += widget.height(vg) + 3.0F;
      }

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

      if (newHoveredRow != this.hoveredRow && newHoveredRow != -1) {
         UiSounds.hover();
      }

      this.hoveredRow = newHoveredRow;
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
      return this.accentWidget.keyPressed(keyCode);
   }

   @Override
   public boolean isListening() {
      return this.accentWidget.isListening();
   }
}



