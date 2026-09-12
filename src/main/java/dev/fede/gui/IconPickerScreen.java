package dev.fede.gui;

import dev.fede.FeClient;
import dev.fede.gui.picker.PickerGrid;
import dev.fede.gui.widget.ColorWidget;
import dev.fede.render.NvgDrawable;
import dev.fede.render.OverlayRenderer;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.ColorSetting;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;

public class IconPickerScreen extends Screen implements NvgDrawable {
   private static final float HEADER_H = 42.0F;
   private static final float SEARCH_H = 34.0F;
   private static final float PAD = 12.0F;
   private static final float CELL = 34.0F;
   private static final float SEL_CHIP_W = 116.0F;
   private static final float PANEL_RADIUS = 14.0F;
   private Screen parent;
   private PickerGrid model;
   private ThemeManager themes;
   private final StringBuilder search = new StringBuilder();
   private boolean searchFocused;
   private boolean selectedOnly;
   private List<PickerGrid.Cell> filtered = List.of();
   private String lastQuery = null;
   private boolean lastSelectedOnly;
   private boolean filterDirty = true;
   private float scroll;
   private float maxScroll;
   private ColorSetting colorSetting;
   private String colorTitle;
   private ColorWidget colorWidget;
   private final float[] popupRect = new float[4];
   private final float[] closeRect = new float[4];
   private final float[] searchRect = new float[4];
   private final float[] selChipRect = new float[4];

   public IconPickerScreen(Screen parent, PickerGrid model, ThemeManager themes) {
      super(Text.literal(model.title()));
      this.parent = parent;
      this.model = model;
      this.themes = themes;
   }

   private void refreshFilter() {
      String q = this.search.toString().trim().toLowerCase(Locale.ROOT);
      if (this.filterDirty || !q.equals(this.lastQuery) || this.selectedOnly != this.lastSelectedOnly) {
         this.lastQuery = q;
         this.lastSelectedOnly = this.selectedOnly;
         this.filterDirty = false;
         List<PickerGrid.Cell> out = new ArrayList<>();

         for (PickerGrid.Cell cell : this.model.cells()) {
            if ((!this.selectedOnly || cell.selected()) && (q.isEmpty() || cell.matches(q))) {
               out.add(cell);
            }
         }

         this.filtered = out;
         this.scroll = 0.0F;
      }
   }

   private IconPickerScreen.Layout layout() {
      float uiW = OverlayRenderer.uiWidth();
      float uiH = OverlayRenderer.uiHeight();
      float panelW = Math.min(uiW * 0.82F, 940.0F);
      float panelH = Math.min(uiH * 0.84F, 660.0F);
      float px = (uiW - panelW) / 2.0F;
      float py = (uiH - panelH) / 2.0F;
      float gridX = px + 12.0F;
      float gridY = py + 42.0F + 34.0F;
      float gridW = panelW - 24.0F;
      float gridH = panelH - 42.0F - 34.0F - 12.0F;
      int cols = Math.max(1, (int)(gridW / 34.0F));
      float cell = gridW / cols;
      return new Layout(px, py, panelW, panelH, gridX, gridY, gridW, gridH, cols, cell, cell - 11.0F);
   }

   private float contentHeight(IconPickerScreen.Layout l) {
      int rows = (this.filtered.size() + l.cols() - 1) / l.cols();
      return rows * l.cell();
   }

   public void render(DrawContext gg, int mouseX, int mouseY, float partialTick) {
      this.refreshFilter();
      IconPickerScreen.Layout l = this.layout();
      this.maxScroll = Math.max(0.0F, this.contentHeight(l) - l.gridH());
      this.scroll = Math.clamp(this.scroll, 0.0F, this.maxScroll);
      float uiScale = OverlayRenderer.uiScale();
      double guiScale = this.client.getWindow().getScaleFactor();
      float k = (float)(uiScale / guiScale);
      gg.fill(0, 0, this.width, this.height, -670694391);
      fillRoundedRectV(gg, l.getFloat() * k, l.getFloat2() * k, l.panelW() * k, l.panelH() * k, 14.0F * k, -132771297, -133560304);
      gg.enableScissor(intOf(l.gridX() * k), intOf(l.gridY() * k), intOf((l.gridX() + l.gridW()) * k), intOf((l.gridY() + l.gridH()) * k));
      int first = Math.max(0, (int)(this.scroll / l.cell()) * l.cols());
      int perView = l.cols() * ((int)(l.gridH() / l.cell()) + 3);
      int last = Math.min(this.filtered.size(), first + perView);
      Matrix3x2fStack pose = gg.getMatrices();

      for (int i = first; i < last; i++) {
         PickerGrid.Cell cell = this.filtered.get(i);
         int col = i % l.cols();
         int row = i / l.cols();
         float cx = l.gridX() + col * l.cell() + (l.cell() - l.icon()) / 2.0F;
         float cy = l.gridY() - this.scroll + row * l.cell() + (l.cell() - l.icon()) / 2.0F;
         float sc = l.icon() * k / 16.0F;
         pose.pushMatrix();
         pose.translate(cx * k, cy * k);
         pose.scale(sc, sc);
         gg.drawItem(cell.icon(), 0, 0);
         pose.popMatrix();
      }

      gg.disableScissor();
   }

   private static int intOf(float v) {
      return Math.round(v);
   }

   private static void fillRoundedRectV(DrawContext gg, float x, float y, float w, float h, float radius, int top, int bottom) {
      int x0 = Math.round(x);
      int y0 = Math.round(y);
      int x1 = Math.round(x + w);
      int y1 = Math.round(y + h);
      int height = y1 - y0;
      int width = x1 - x0;
      if (height > 0 && width > 0) {
         int r = Math.min(Math.round(radius), Math.min(width, height) / 2);

         for (int row = 0; row < height; row++) {
            int inset = 0;
            if (r > 0) {
               int dy = row < r ? row : (row >= height - r ? height - 1 - row : -1);
               if (dy >= 0) {
                  double off = r - dy - 0.5;
                  inset = (int)Math.round(r - Math.sqrt(Math.max(0.0, (double)r * r - off * off)));
               }
            }

            int color = Colors.lerp(top, bottom, height <= 1 ? 0.0F : (float)row / (height - 1));
            gg.fill(x0 + inset, y0 + row, x1 - inset, y0 + row + 1, color);
         }
      }
   }

   @Override
   public void renderNvg(NVGRenderer vg, float mouseX, float mouseY, float uiWidth, float uiHeight) {
      if (vg.hasFont()) {
         Theme theme = this.themes.current();
         IconPickerScreen.Layout l = this.layout();
         boolean blink = System.nanoTime() / 400000000L % 2L == 0L;
         vg.glow(l.getFloat(), l.getFloat2(), l.panelW(), l.panelH(), 14.0F, 17.0F, Colors.withAlpha(-16777216, 0.45F));
         vg.glow(l.getFloat(), l.getFloat2(), l.panelW(), l.panelH(), 14.0F, 8.0F, Colors.withAlpha(theme.accent(), 0.28F));
         vg.rectOutline(l.getFloat(), l.getFloat2(), l.panelW(), l.panelH(), 14.0F, 1.4F, Colors.withAlpha(theme.accentBright(), 0.75F));
         vg.rectOutline(
            l.getFloat() + 2.2F, l.getFloat2() + 2.2F, l.panelW() - 4.4F, l.panelH() - 4.4F, 11.8F, 1.0F, Colors.withAlpha(theme.accentBright(), 0.13F)
         );
         vg.textGradient(this.model.title(), l.getFloat() + 12.0F, l.getFloat2() + 16.0F, 16.0F, theme.accentBright(), theme.accent());
         String hint = "Left-click: toggle   ·   Right-click: color   ·   " + this.model.activeCount() + " active";
         vg.text(hint, l.getFloat() + 12.0F, l.getFloat2() + 31.0F, 11.0F, theme.textDisabled());
         float closeCx = l.getFloat() + l.panelW() - 12.0F - 3.0F;
         float closeCy = l.getFloat2() + 18.0F;
         boolean closeHover = Math.abs(mouseX - closeCx) < 10.0F && Math.abs(mouseY - closeCy) < 10.0F;
         vg.cross(closeCx - 6.0F, closeCy - 6.0F, 12.0F, 1.8F, closeHover ? theme.accentBright() : theme.textMuted());
         this.closeRect[0] = closeCx - 10.0F;
         this.closeRect[1] = closeCy - 10.0F;
         this.closeRect[2] = closeCx + 10.0F;
         this.closeRect[3] = closeCy + 10.0F;
         float sx = l.getFloat() + 12.0F;
         float sy = l.getFloat2() + 42.0F + 4.0F;
         float sw = l.panelW() - 24.0F - 116.0F - 8.0F;
         this.searchRect[0] = sx;
         this.searchRect[1] = sy;
         this.searchRect[2] = sx + sw;
         this.searchRect[3] = sy + 22.0F;
         vg.rect(sx, sy, sw, 22.0F, 22.0F / 2.0F, this.searchFocused ? Colors.withAlpha(theme.accent(), 0.16F) : Colors.withAlpha(-16777216, 0.4F));
         vg.rectOutline(
            sx,
            sy,
            sw,
            22.0F,
            22.0F / 2.0F,
            1.1F,
            Colors.withAlpha(this.searchFocused ? theme.accentBright() : theme.accent(), this.searchFocused ? 0.9F : 0.35F)
         );
         float ix = sx + 12.0F;
         float iy = sy + 22.0F / 2.0F;
         vg.circleOutline(ix, iy - 1.0F, 4.0F, 1.4F, theme.textMuted());
         vg.line(ix + 3.0F, iy + 2.0F, ix + 6.0F, iy + 5.0F, 1.4F, theme.textMuted());
         if (this.search.length() == 0 && !this.searchFocused) {
            vg.text("Search…  (" + this.filtered.size() + " shown)", sx + 24.0F, iy, 12.5F, theme.textDisabled());
         } else {
            float w = vg.text(this.search.toString(), sx + 24.0F, iy, 12.5F, theme.textPrimary());
            if (this.searchFocused && blink) {
               vg.rect(sx + 24.0F + w + 1.5F, iy - 6.0F, 1.4F, 12.0F, 0.7F, theme.accentBright());
            }
         }

         float chipX = sx + sw + 8.0F;
         this.selChipRect[0] = chipX;
         this.selChipRect[1] = sy;
         this.selChipRect[2] = chipX + 116.0F;
         this.selChipRect[3] = sy + 22.0F;
         boolean chipHover = inRect(mouseX, mouseY, this.selChipRect);
         vg.rect(chipX, sy, 116.0F, 22.0F, 22.0F / 2.0F, this.selectedOnly ? Colors.withAlpha(theme.accent(), 0.22F) : Colors.withAlpha(-16777216, 0.4F));
         vg.rectOutline(
            chipX,
            sy,
            116.0F,
            22.0F,
            22.0F / 2.0F,
            1.1F,
            Colors.withAlpha(!this.selectedOnly && !chipHover ? theme.accent() : theme.accentBright(), this.selectedOnly ? 0.9F : 0.4F)
         );
         float dotX = chipX + 13.0F;
         float dotY = sy + 22.0F / 2.0F;
         if (this.selectedOnly) {
            vg.circle(dotX, dotY, 4.0F, theme.statusEnabled());
            vg.circleGlow(dotX, dotY, 4.0F, 4.0F, Colors.withAlpha(theme.statusEnabled(), 0.5F));
         } else {
            vg.circleOutline(dotX, dotY, 4.0F, 1.4F, theme.textMuted());
         }

         vg.text("Selected only", chipX + 24.0F, dotY, 11.5F, this.selectedOnly ? theme.textPrimary() : theme.textMuted());
         vg.save();
         vg.scissor(l.gridX(), l.gridY(), l.gridW(), l.gridH());
         int first = Math.max(0, (int)(this.scroll / l.cell()) * l.cols());
         int perView = l.cols() * ((int)(l.gridH() / l.cell()) + 3);
         int last = Math.min(this.filtered.size(), first + perView);
         int hovered = this.cellAt(mouseX, mouseY, l);

         for (int i = first; i < last; i++) {
            PickerGrid.Cell cell = this.filtered.get(i);
            int col = i % l.cols();
            int row = i / l.cols();
            float cellX = l.gridX() + col * l.cell();
            float cellY = l.gridY() - this.scroll + row * l.cell();
            boolean tracked = cell.tracked();
            boolean active = cell.enabled();
            if (i == hovered) {
               vg.rect(cellX + 1.0F, cellY + 1.0F, l.cell() - 2.0F, l.cell() - 2.0F, 6.0F, Colors.withAlpha(theme.accent(), 0.12F));
            }

            if (tracked) {
               int base = cell.color();
               int ring = active ? base : Colors.withAlpha(base, 0.35F);
               vg.rectOutline(cellX + 1.5F, cellY + 1.5F, l.cell() - 3.0F, l.cell() - 3.0F, 6.0F, active ? 1.8F : 1.0F, ring | (active ? -16777216 : 0));
               vg.rect(cellX + 4.0F, cellY + l.cell() - 5.0F, l.cell() - 8.0F, 2.5F, 1.0F, base | 0xFF000000);
               if (active) {
                  vg.circle(cellX + l.cell() - 6.0F, cellY + 6.0F, 2.6F, theme.statusEnabled());
               }
            }
         }

         vg.restore();
         if (this.filtered.isEmpty() && this.selectedOnly) {
            vg.text("Nothing selected yet — turn off \"Selected only\" to browse.", l.gridX() + 4.0F, l.gridY() + 16.0F, 12.5F, theme.textDisabled());
         }

         if (this.maxScroll > 0.0F) {
            float trackX = l.getFloat() + l.panelW() - 6.0F;
            float thumbH = Math.max(24.0F, l.gridH() * (l.gridH() / this.contentHeight(l)));
            float thumbY = l.gridY() + (l.gridH() - thumbH) * (this.scroll / this.maxScroll);
            vg.rect(trackX, thumbY, 3.0F, thumbH, 1.5F, Colors.withAlpha(theme.accent(), 0.55F));
         }

         if (this.colorSetting != null && this.colorWidget != null) {
            this.renderColorPopup(vg, theme);
         } else {
            this.popupRect[0] = this.popupRect[1] = this.popupRect[2] = this.popupRect[3] = 0.0F;
         }
      }
   }

   private void renderColorPopup(NVGRenderer vg, Theme theme) {
      this.colorWidget.setExpanded(true);
      float uiW = OverlayRenderer.uiWidth();
      float uiH = OverlayRenderer.uiHeight();
      float wx = (uiW - 220.0F) / 2.0F + 12.0F;
      float bodyH = this.colorWidget.height(vg);
      float cardH = 26.0F + bodyH + 12.0F;
      float cardX = (uiW - 220.0F) / 2.0F;
      float cardY = (uiH - cardH) / 2.0F;
      this.popupRect[0] = cardX;
      this.popupRect[1] = cardY;
      this.popupRect[2] = cardX + 220.0F;
      this.popupRect[3] = cardY + cardH;
      vg.glow(cardX, cardY, 220.0F, cardH, 12.0F, 14.0F, Colors.withAlpha(theme.accent(), 0.3F));
      vg.rectGradient(cardX, cardY, 220.0F, cardH, 12.0F, theme.background(), theme.backgroundTo(), true);
      vg.rectOutline(cardX, cardY, 220.0F, cardH, 12.0F, 1.4F, Colors.withAlpha(theme.accentBright(), 0.7F));
      vg.textGradient(this.colorTitle, cardX + 12.0F, cardY + 14.0F, 12.5F, theme.accentBright(), theme.accent());
      this.colorWidget.setBounds(wx, cardY + 26.0F, 220.0F - 24.0F);
      this.colorWidget.render(vg, OverlayRenderer.uiMouseX(), OverlayRenderer.uiMouseY());
   }

   private int cellAt(float mx, float my, IconPickerScreen.Layout l) {
      if (!(mx < l.gridX()) && !(mx > l.gridX() + l.gridW()) && !(my < l.gridY()) && !(my > l.gridY() + l.gridH())) {
         int col = (int)((mx - l.gridX()) / l.cell());
         int row = (int)((my - (l.gridY() - this.scroll)) / l.cell());
         if (col >= 0 && col < l.cols() && row >= 0) {
            int index = row * l.cols() + col;
            return index >= 0 && index < this.filtered.size() ? index : -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private float floatOf(double guiX) {
      return OverlayRenderer.guiToUi(guiX);
   }

   public boolean mouseClicked(Click event, boolean doubled) {
      float mx = this.floatOf(event.x());
      float my = this.floatOf(event.y());
      IconPickerScreen.Layout l = this.layout();
      if (this.colorSetting != null) {
         if (inRect(mx, my, this.popupRect)) {
            this.colorWidget.mouseClicked(mx, my, event.button());
         } else {
            this.closeColor();
            UiSounds.select();
         }

         return true;
      } else if (inRect(mx, my, this.closeRect)) {
         this.close();
         return true;
      } else if (inRect(mx, my, this.selChipRect)) {
         this.selectedOnly = !this.selectedOnly;
         this.filterDirty = true;
         this.searchFocused = false;
         UiSounds.toggle(this.selectedOnly);
         return true;
      } else if (inRect(mx, my, this.searchRect)) {
         this.searchFocused = true;
         UiSounds.select();
         return true;
      } else {
         this.searchFocused = false;
         if (!(mx < l.getFloat()) && !(mx > l.getFloat() + l.panelW()) && !(my < l.getFloat2()) && !(my > l.getFloat2() + l.panelH())) {
            int index = this.cellAt(mx, my, l);
            if (index >= 0) {
               PickerGrid.Cell cell = this.filtered.get(index);
               if (event.button() == 1) {
                  this.openColor(cell.colorTarget(), cell.label());
               } else if (event.button() == 0) {
                  boolean wasSelected = cell.selected();
                  cell.toggle();
                  UiSounds.toggle(cell.enabled());
                  if (this.selectedOnly && wasSelected && !cell.selected()) {
                     this.filterDirty = true;
                  }
               }

               return true;
            } else {
               return true;
            }
         } else {
            this.close();
            return true;
         }
      }
   }

   private void openColor(ColorSetting target, String title) {
      if (target != null) {
         this.colorSetting = target;
         this.colorTitle = title;
         this.colorWidget = new ColorWidget(this.themes, target);
         this.colorWidget.setExpanded(true);
         this.searchFocused = false;
         UiSounds.select();
      }
   }

   private void closeColor() {
      this.colorSetting = null;
      this.colorTitle = null;
      this.colorWidget = null;
   }

   public boolean mouseDragged(Click event, double dx, double dy) {
      if (this.colorSetting != null && this.colorWidget != null) {
         this.colorWidget.mouseDragged(this.floatOf(event.x()), this.floatOf(event.y()));
      }

      return true;
   }

   public boolean mouseReleased(Click event) {
      if (this.colorWidget != null) {
         this.colorWidget.mouseReleased();
      }

      return true;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.colorSetting == null) {
         this.scroll = Math.clamp(this.scroll - (float)(scrollY * this.layout().cell()), 0.0F, this.maxScroll);
      }

      return true;
   }

   public boolean keyPressed(KeyInput event) {
      int key = event.key();
      if (this.colorSetting != null) {
         if (this.colorWidget != null && this.colorWidget.isListening()) {
            this.colorWidget.keyPressed(key);
            return true;
         } else if (key == 256) {
            this.closeColor();
            return true;
         } else {
            return true;
         }
      } else if (this.searchFocused) {
         switch (key) {
            case 256:
            case 257:
            case 335:
               this.searchFocused = false;
               break;
            case 259:
               if (this.search.length() > 0) {
                  this.search.deleteCharAt(this.search.length() - 1);
                  this.filterDirty = true;
               }
         }

         return true;
      } else if (key == 256) {
         this.close();
         return true;
      } else {
         return super.keyPressed(event);
      }
   }

   public boolean charTyped(CharInput event) {
      if (this.colorSetting != null) {
         return true;
      } else if (!this.searchFocused) {
         return true;
      } else if (this.search.length() >= 48) {
         return true;
      } else {
         char c = (char)event.codepoint();
         if (c == ' ' || c == '_' || c == ':' || c == '/' || c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
            this.search.append(Character.toLowerCase(c));
            this.filterDirty = true;
         }

         return true;
      }
   }

   public void close() {
      FeClient.config().save();
      this.client.setScreen(this.parent);
   }

   public boolean shouldPause() {
      return false;
   }

   public void debugSetSearch(String query) {
      this.search.setLength(0);
      this.search.append(query);
      this.searchFocused = true;
      this.filterDirty = true;
      this.refreshFilter();
   }

   public void debugSetSelectedOnly(boolean value) {
      this.selectedOnly = value;
      this.filterDirty = true;
      this.refreshFilter();
   }

   public void debugOpenColor(int filteredIndex) {
      if (filteredIndex >= 0 && filteredIndex < this.filtered.size()) {
         PickerGrid.Cell cell = this.filtered.get(filteredIndex);
         this.openColor(cell.colorTarget(), cell.label());
      }
   }

   public int debugFilteredCount() {
      return this.filtered.size();
   }

   private static boolean inRect(float mx, float my, float[] r) {
      return mx >= r[0] && mx <= r[2] && my >= r[1] && my <= r[3];
   }

   final class Layout {
      private float floatVal;
      private float floatVal2;
      private float panelW;
      private float panelH;
      private float gridX;
      private float gridY;
      private float gridW;
      private float gridH;
      private int cols;
      private float cell;
      private float icon;

      private Layout(float px, float py, float panelW, float panelH, float gridX, float gridY, float gridW, float gridH, int cols, float cell, float icon) {
         this.floatVal = px;
         this.floatVal2 = py;
         this.panelW = panelW;
         this.panelH = panelH;
         this.gridX = gridX;
         this.gridY = gridY;
         this.gridW = gridW;
         this.gridH = gridH;
         this.cols = cols;
         this.cell = cell;
         this.icon = icon;
      }

      public float getFloat() {
         return this.floatVal;
      }

      public float getFloat2() {
         return this.floatVal2;
      }

      public float panelW() {
         return this.panelW;
      }

      public float panelH() {
         return this.panelH;
      }

      public float gridX() {
         return this.gridX;
      }

      public float gridY() {
         return this.gridY;
      }

      public float gridW() {
         return this.gridW;
      }

      public float gridH() {
         return this.gridH;
      }

      public int cols() {
         return this.cols;
      }

      public float cell() {
         return this.cell;
      }

      public float icon() {
         return this.icon;
      }
   }
}



