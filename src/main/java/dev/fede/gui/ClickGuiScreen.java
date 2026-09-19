package dev.fede.gui;

import dev.fede.FeClient;
import dev.fede.gui.config.ConfigPanel;
import dev.fede.gui.panel.CategoryPanel;
import dev.fede.gui.panel.Panel;
import dev.fede.gui.panel.ThemesPanel;
import dev.fede.gui.picker.BlockGridModel;
import dev.fede.gui.picker.IconListGridModel;
import dev.fede.module.Category;
import dev.fede.module.ModuleManager;
import dev.fede.module.Modules;
import dev.fede.module.impl.BlockEspModule;
import dev.fede.render.BlurHook;
import dev.fede.render.NvgDrawable;
import dev.fede.render.OverlayRenderer;
import dev.fede.render.anim.Animation;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.BlockListSetting;
import dev.fede.settings.IconListSetting;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen implements NvgDrawable {
   private static final ClickGuiState STATE = new ClickGuiState();
   private final List<Panel> panels = new ArrayList<>();
   private final Animation openAnim = new Animation(180.0F, 0.0F);
   private boolean closing;
   private final ConfigPanel configPanel = new ConfigPanel();
   private static final float FIDGET_W = 118.0F;
   private static final float FIDGET_H = 28.0F;
   private boolean fidgetHovered;
   private static final float SEARCH_W = 280.0F;
   private static final float SEARCH_H = 32.0F;
   private final StringBuilder search = new StringBuilder();
   private boolean searchFocused;
   private Panel dragging;
   private float dragOffsetX;
   private float dragOffsetY;
   private float pressX;
   private float pressY;
   private boolean dragMoved;
   private Panel pressedContentPanel;
   private Screen parent;

   public ClickGuiScreen() {
      this(null);
   }

   public ClickGuiScreen(Screen parent) {
      super(Text.literal("Client ClickGUI"));
      this.parent = parent;
      STATE.ensureDefaultLayout(OverlayRenderer.uiWidth(), OverlayRenderer.uiHeight());
      ModuleManager modules = FeClient.modules();
      ThemeManager themes = FeClient.themes();

      for (Category category : Category.values()) {
         // skip categories with zero registered modules — an empty tab (Misc
         // and Themes both land here once their contents get sorted into
         // better-fitting categories) is just clutter, not a real section.
         if (modules.inCategory(category).isEmpty()) continue;
         this.panels.add(new CategoryPanel(category, modules, themes, STATE));
      }

      this.panels.add(new ThemesPanel(themes, STATE));
      this.openAnim.setTarget(1.0F);
   }

   public void onDisplayed() {
      super.onDisplayed();
      UiSounds.guiOpen();
   }

   public static ClickGuiState state() {
      return STATE;
   }

   private Modules.ClickGuiModule guiModule() {
      return FeClient.modules().clickGui;
   }

   public boolean shouldPause() {
      return false;
   }

   public void close() {
      if (!this.closing) {
         this.closing = true;
         this.openAnim.setTarget(0.0F);
         UiSounds.guiClose();
      }
   }

   public void removed() {
      BlurHook.clear();
      FeClient.config().save();
   }

   public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
      int alpha = (int)(48.0F * this.openAnim.value());
      guiGraphics.fill(0, 0, this.width, this.height, alpha << 24 | 394506);
      this.finishCloseIfDone();
   }

   public void tick() {
      this.finishCloseIfDone();
   }

   private void finishCloseIfDone() {
      if (this.closing && this.openAnim.isDone()) {
         this.client.setScreen(this.parent);
      }
   }

   public void renderBackground(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (this.client.world == null) {
         this.renderPanoramaBackground(guiGraphics, partialTick);
      }

      if (this.guiModule().blur.get()) {
         BlurHook.set(this.guiModule().blurStrength.getFloat() * this.openAnim.value());
         guiGraphics.applyBlur();
      } else {
         BlurHook.clear();
      }
   }

   @Override
   public void renderNvg(NVGRenderer vg, float mouseX, float mouseY, float uiWidth, float uiHeight) {
      vg.setFontMode(this.guiModule().font.get());
      if (vg.hasFont()) {
         float t = this.openAnim.value();
         if (!(t <= 0.002F) || !this.closing) {
            vg.save();
            vg.alpha(t);
            float scale = 0.96F + 0.04F * t;
            vg.translate(uiWidth / 2.0F, uiHeight / 2.0F);
            vg.scale(scale);
            vg.translate(-uiWidth / 2.0F, -uiHeight / 2.0F);
            String query = this.search.toString();

            for (Panel panel : this.panels) {
               if (panel instanceof CategoryPanel categoryPanel) {
                  categoryPanel.setFilter(query);
               }
            }

            for (int i = this.panels.size() - 1; i >= 0; i--) {
               this.panels.get(i).render(vg, mouseX, mouseY, uiWidth, uiHeight);
            }

            this.renderSearchBar(vg, uiWidth);
            this.renderFidget(vg, mouseX, mouseY, uiWidth, uiHeight);
            this.configPanel.render(vg, mouseX, mouseY, uiWidth, uiHeight);
            vg.restore();
         }
      }
   }

   private void renderFidget(NVGRenderer vg, float mouseX, float mouseY, float uiWidth, float uiHeight) {
      Theme theme = FeClient.themes().current();
      float fx = fidgetX(uiWidth);
      float fy = fidgetY(uiHeight);
      boolean hover = mouseX >= fx && mouseX <= fx + 118.0F && mouseY >= fy && mouseY <= fy + 28.0F;
      boolean lit = hover || this.configPanel.isOpen();
      if (lit != this.fidgetHovered) {
         this.fidgetHovered = lit;
         if (lit) {
            UiSounds.hover();
         }
      }

      vg.glow(fx, fy, 118.0F, 28.0F, 14.0F, lit ? 7.0F : 4.0F, Colors.withAlpha(theme.accent(), lit ? 0.28F : 0.14F));
      vg.rectGradient(fx, fy, 118.0F, 28.0F, 14.0F, theme.headerTop(), theme.headerBottom(), true);
      vg.rectOutline(fx, fy, 118.0F, 28.0F, 14.0F, 1.2F, Colors.withAlpha(lit ? theme.accentBright() : theme.accent(), lit ? 0.9F : 0.4F));
      float gx = fx + 16.0F;
      float gy = fy + 14.0F - 6.0F;
      vg.rect(gx + 3.0F, gy + 3.0F, 11.0F, 9.0F, 2.5F, Colors.withAlpha(theme.accent(), 0.5F));
      vg.rect(gx, gy, 11.0F, 9.0F, 2.5F, lit ? theme.accentBright() : theme.accent());
      vg.text("Configs", fx + 36.0F, fy + 14.0F, 13.5F, lit ? theme.textPrimary() : theme.textMuted());
   }

   private static float fidgetX(float uiWidth) {
      return (uiWidth - 118.0F) / 2.0F;
   }

   private static float fidgetY(float uiHeight) {
      return uiHeight - 28.0F - 14.0F;
   }

   private boolean fidgetHit(float mx, float my) {
      float fx = fidgetX(OverlayRenderer.uiWidth());
      float fy = fidgetY(OverlayRenderer.uiHeight());
      return mx >= fx && mx <= fx + 118.0F && my >= fy && my <= fy + 28.0F;
   }

   private void renderSearchBar(NVGRenderer vg, float uiWidth) {
      Theme theme = FeClient.themes().current();
      float sx = (uiWidth - 280.0F) / 2.0F;
      vg.glow(sx, 19.0F, 280.0F, 32.0F, 16.0F, 6.0F, Colors.withAlpha(theme.accent(), this.searchFocused ? 0.25F : 0.12F));
      vg.rectGradient(sx, 19.0F, 280.0F, 32.0F, 16.0F, theme.headerTop(), theme.headerBottom(), true);
      vg.rectOutline(
         sx, 19.0F, 280.0F, 32.0F, 16.0F, 1.2F, Colors.withAlpha(this.searchFocused ? theme.accentBright() : theme.accent(), this.searchFocused ? 0.9F : 0.4F)
      );
      float ix = sx + 16.0F;
      float iy = 19.0F + 16.0F;
      vg.circleOutline(ix, iy - 1.5F, 4.5F, 1.6F, theme.textMuted());
      vg.line(ix + 3.4F, iy + 2.2F, ix + 6.5F, iy + 5.4F, 1.6F, theme.textMuted());
      float textX = sx + 30.0F;
      if (this.search.isEmpty() && !this.searchFocused) {
         vg.text("Search modules...", textX, iy, 13.0F, theme.textDisabled());
      } else {
         float w = vg.text(this.search.toString(), textX, iy, 13.0F, theme.textPrimary());
         if (this.searchFocused && System.nanoTime() / 400000000L % 2L == 0L) {
            vg.rect(textX + w + 2.0F, iy - 7.0F, 1.5F, 14.0F, 0.75F, theme.accentBright());
         }
      }

      if (!this.search.isEmpty()) {
         vg.cross(sx + 280.0F - 26.0F, iy - 6.0F, 12.0F, 1.6F, theme.textMuted());
      }
   }

   public void openSearch(String query) {
      this.search.setLength(0);
      this.search.append(query);
      this.searchFocused = true;
   }

   public ConfigPanel configPanel() {
      return this.configPanel;
   }

   private boolean searchBarHit(float mx, float my) {
      float sx = (OverlayRenderer.uiWidth() - 280.0F) / 2.0F;
      return mx >= sx && mx <= sx + 280.0F && my >= 19.0F && my <= 51.0F;
   }

   private float uiX(double guiX) {
      return OverlayRenderer.guiToUi(guiX);
   }

   private float uiY(double guiY) {
      return OverlayRenderer.guiToUi(guiY);
   }

   public boolean mouseClicked(Click event, boolean doubled) {
      float mx = this.uiX(event.x());
      float my = this.uiY(event.y());
      NVGRenderer vg = NVGRenderer.get();
      float uiHeight = OverlayRenderer.uiHeight();
      if (this.configPanel.isOpen()) {
         this.configPanel.mouseClicked(mx, my, event.button());
         return true;
      } else if (this.fidgetHit(mx, my)) {
         this.configPanel.open();
         UiSounds.guiOpen();
         return true;
      } else if (!this.searchBarHit(mx, my)) {
         this.searchFocused = false;

         for (Panel panel : this.panels) {
            if (panel.headerHit(mx, my)) {
               this.bringToFront(panel);
               if (event.button() == 0) {
                  this.dragging = panel;
                  this.dragOffsetX = mx - panel.getFloat();
                  this.dragOffsetY = my - panel.getFloat2();
                  this.pressX = mx;
                  this.pressY = my;
                  this.dragMoved = false;
               } else {
                  panel.toggleCollapsed();
                  UiSounds.panelCollapse();
               }

               return true;
            }

            if (panel.bodyHit(vg, mx, my, uiHeight)) {
               this.bringToFront(panel);
               this.pressedContentPanel = panel;
               panel.mouseClicked(mx, my, event.button());
               return true;
            }
         }

         return true;
      } else {
         float clearX = (OverlayRenderer.uiWidth() + 280.0F) / 2.0F - 26.0F;
         if (!this.search.isEmpty() && mx >= clearX - 4.0F && mx <= clearX + 16.0F) {
            this.search.setLength(0);
         } else {
            this.searchFocused = true;
         }

         UiSounds.select();
         return true;
      }
   }

   public boolean mouseDragged(Click event, double dx, double dy) {
      if (this.configPanel.isOpen()) {
         return true;
      } else {
         float mx = this.uiX(event.x());
         float my = this.uiY(event.y());
         if (this.dragging != null) {
            if (Math.abs(mx - this.pressX) + Math.abs(my - this.pressY) > 3.0F) {
               this.dragMoved = true;
            }

            if (this.dragMoved) {
               this.dragging.moveTo(mx - this.dragOffsetX, my - this.dragOffsetY);
            }

            return true;
         } else if (this.pressedContentPanel != null) {
            this.pressedContentPanel.mouseDragged(mx, my);
            return true;
         } else {
            return true;
         }
      }
   }

   public boolean mouseReleased(Click event) {
      if (this.configPanel.isOpen()) {
         return true;
      } else if (this.dragging == null) {
         if (this.pressedContentPanel != null) {
            this.pressedContentPanel.mouseReleased();
            this.pressedContentPanel = null;
         }

         return true;
      } else {
         if (!this.dragMoved && event.button() == 0) {
            this.dragging.toggleCollapsed();
            UiSounds.panelCollapse();
         } else if (this.dragMoved) {
            STATE.markCustomized();
         }

         this.dragging = null;
         return true;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.configPanel.isOpen()) {
         return true;
      } else {
         float mx = this.uiX(mouseX);
         float my = this.uiY(mouseY);
         NVGRenderer vg = NVGRenderer.get();
         float uiHeight = OverlayRenderer.uiHeight();

         for (Panel panel : this.panels) {
            if (panel.bodyHit(vg, mx, my, uiHeight) || panel.headerHit(mx, my)) {
               panel.onScroll(scrollY);
               return true;
            }
         }

         return true;
      }
   }

   public boolean keyPressed(KeyInput event) {
      if (this.configPanel.isOpen()) {
         this.configPanel.keyPressed(event.key());
         return true;
      } else {
         for (Panel panel : this.panels) {
            if (panel.isListening()) {
               panel.keyPressed(event.key());
               return true;
            }
         }

         if (this.searchFocused) {
            switch (event.key()) {
               case 256:
                  this.search.setLength(0);
                  this.searchFocused = false;
                  break;
               case 257:
               case 335:
                  this.searchFocused = false;
                  break;
               case 259:
                  if (!this.search.isEmpty()) {
                     this.search.deleteCharAt(this.search.length() - 1);
                  }
            }

            return true;
         } else if (!event.isEscape() && !this.guiModule().getKeybind().matches(event.key())) {
            return super.keyPressed(event);
         } else {
            this.close();
            return true;
         }
      }
   }

   public boolean charTyped(CharInput event) {
      if (this.configPanel.isOpen()) {
         this.configPanel.charTyped(event.codepoint());
         return true;
      } else {
         for (Panel panel : this.panels) {
            if (panel.isListening()) {
               panel.charTyped(event.codepoint());
               return true;
            }
         }

         if (this.searchFocused && event.isValidChar()) {
            if (this.search.length() < 32) {
               this.search.append(event.asString());
            }

            return true;
         } else {
            return super.charTyped(event);
         }
      }
   }

   private void bringToFront(Panel panel) {
      if (this.panels.remove(panel)) {
         this.panels.addFirst(panel);
      }
   }

   public void openBlockPicker(BlockListSetting setting) {
      BlockGridModel model = new BlockGridModel(setting, () -> {
         BlockEspModule blockEsp = FeClient.modules().blockEsp;
         return blockEsp != null ? blockEsp.lineColor.get() : -16711736;
      }, "Pick Block");
      this.client.setScreen(new IconPickerScreen(this, model, FeClient.themes()));
   }

   public void openIconPicker(IconListSetting setting) {
      IconListGridModel model = new IconListGridModel(setting);
      this.client.setScreen(new IconPickerScreen(this, model, FeClient.themes()));
   }
}


