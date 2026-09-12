package dev.fede.gui.panel;

import dev.fede.gui.ClickGuiState;
import dev.fede.render.anim.Animation;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.UiSounds;

public abstract class Panel {
   public static final float WIDTH = 175.0F;
   public static final float HEADER_H = 32.0F;
   public static final float RADIUS = 12.0F;
   protected static final float CONTENT_PAD = 6.0F;
   private static final float FADE_ZONE = 16.0F;
   protected ThemeManager themes;
   protected ClickGuiState.PanelState clickGuiStatePanelState;
   private Animation open;
   private final Animation scroll = new Animation(200.0F, 0.0F);
   private float maxScroll;
   private boolean headerHovered;

   protected Panel(ThemeManager themes, ClickGuiState.PanelState ps) {
      this.themes = themes;
      this.clickGuiStatePanelState = ps;
      this.open = new Animation(200.0F, ps.collapsed ? 0.0F : 1.0F);
   }

   protected Theme theme() {
      return this.themes.current();
   }

   public float getFloat() {
      return this.clickGuiStatePanelState.floatVal;
   }

   public float getFloat2() {
      return this.clickGuiStatePanelState.floatVal2;
   }

   public void moveTo(float x, float y) {
      this.clickGuiStatePanelState.floatVal = x;
      this.clickGuiStatePanelState.floatVal2 = y;
   }

   public void toggleCollapsed() {
      this.clickGuiStatePanelState.collapsed = !this.clickGuiStatePanelState.collapsed;
      this.open.setTarget(this.clickGuiStatePanelState.collapsed ? 0.0F : 1.0F);
   }

   protected abstract String title();

   protected int icon() {
      return -1;
   }

   protected abstract float contentHeight(NVGRenderer var1);

   protected abstract void renderContent(NVGRenderer var1, float var2, float var3, float var4, float var5, float var6);

   protected float maxViewHeight(float uiHeight) {
      return Math.max(60.0F, uiHeight - this.clickGuiStatePanelState.floatVal2 - 32.0F - 24.0F);
   }

   protected float viewHeight(NVGRenderer vg, float uiHeight) {
      return Math.min(this.contentHeight(vg), this.maxViewHeight(uiHeight)) * this.open.value();
   }

   public float totalHeight(NVGRenderer vg, float uiHeight) {
      return 32.0F + this.viewHeight(vg, uiHeight);
   }

   protected float edgeFade(float rowTop, float rowBottom, float viewTop, float viewBottom) {
      float fadeTop = Math.clamp((rowBottom - viewTop) / 16.0F, 0.0F, 1.0F);
      float fadeBottom = Math.clamp((viewBottom - rowTop) / 16.0F, 0.0F, 1.0F);
      return Math.min(fadeTop, fadeBottom);
   }

   public void render(NVGRenderer vg, float mx, float my, float uiWidth, float uiHeight) {
      this.clickGuiStatePanelState.floatVal = Math.clamp(this.clickGuiStatePanelState.floatVal, -170.0F, uiWidth - 40.0F);
      this.clickGuiStatePanelState.floatVal2 = Math.clamp(this.clickGuiStatePanelState.floatVal2, 0.0F, uiHeight - 32.0F);
      Theme theme = this.theme();
      float viewH = this.viewHeight(vg, uiHeight);
      boolean hasBody = viewH > 0.5F;
      boolean hoveredNow = this.headerHit(mx, my);
      if (hoveredNow && !this.headerHovered) {
         UiSounds.hover();
      }

      this.headerHovered = hoveredNow;
      vg.glow(
         this.clickGuiStatePanelState.floatVal, this.clickGuiStatePanelState.floatVal2, 175.0F, 32.0F + viewH, 12.0F, 14.0F, Colors.withAlpha(-16777216, 0.35F)
      );
      if (hasBody) {
         vg.rectVaryingGradient(
            this.clickGuiStatePanelState.floatVal,
            this.clickGuiStatePanelState.floatVal2 + 32.0F,
            175.0F,
            viewH,
            0.0F,
            0.0F,
            12.0F,
            12.0F,
            theme.background(),
            theme.backgroundTo()
         );
      }

      float bottomRadius = hasBody ? 0.0F : 12.0F;
      vg.rectVaryingGradient(
         this.clickGuiStatePanelState.floatVal,
         this.clickGuiStatePanelState.floatVal2,
         175.0F,
         32.0F,
         12.0F,
         12.0F,
         bottomRadius,
         bottomRadius,
         theme.headerTop(),
         theme.headerBottom()
      );
      if (hasBody) {
         vg.rectGradient(
            this.clickGuiStatePanelState.floatVal + 1.0F,
            this.clickGuiStatePanelState.floatVal2 + 32.0F - 1.5F,
            208.0F,
            1.5F,
            0.75F,
            Colors.withAlpha(theme.accent(), 0.85F),
            Colors.withAlpha(theme.accentBright(), 0.5F),
            false
         );
      }

      float textX = this.clickGuiStatePanelState.floatVal + 12.0F;
      int iconHandle = this.icon();
      if (iconHandle > 0) {
         vg.image(
            iconHandle,
            this.clickGuiStatePanelState.floatVal + 11.0F,
            this.clickGuiStatePanelState.floatVal2 + 19.0F - 9.0F,
            18.0F,
            18.0F,
            theme.accentBright()
         );
         textX = this.clickGuiStatePanelState.floatVal + 36.0F;
      }

      vg.text(this.title(), textX, this.clickGuiStatePanelState.floatVal2 + 19.0F, 16.5F, theme.textPrimary());
      float chevX = this.clickGuiStatePanelState.floatVal + 175.0F - 16.0F;
      float chevY = this.clickGuiStatePanelState.floatVal2 + 19.0F;
      vg.save();
      vg.translate(chevX, chevY);
      vg.rotate((float)(this.open.value() * Math.PI / 2.0));
      vg.chevron(0.0F, 0.0F, 4.5F, 1.8F, theme.textMuted(), false);
      vg.restore();
      if (hasBody) {
         float viewTop = this.clickGuiStatePanelState.floatVal2 + 32.0F;
         float viewBottom = viewTop + viewH;
         this.maxScroll = Math.max(0.0F, this.contentHeight(vg) - this.maxViewHeight(uiHeight));
         this.scroll.setTarget(Math.clamp(this.scroll.getTarget(), 0.0F, this.maxScroll));
         vg.save();
         vg.scissor(this.clickGuiStatePanelState.floatVal, viewTop, 175.0F, viewH);
         this.renderContent(vg, viewTop + 6.0F - this.scroll.value(), mx, my, viewTop, viewBottom);
         vg.restore();
      }
   }

   public boolean headerHit(float mx, float my) {
      return mx >= this.clickGuiStatePanelState.floatVal
         && mx <= this.clickGuiStatePanelState.floatVal + 175.0F
         && my >= this.clickGuiStatePanelState.floatVal2
         && my <= this.clickGuiStatePanelState.floatVal2 + 32.0F;
   }

   public boolean bodyHit(NVGRenderer vg, float mx, float my, float uiHeight) {
      float viewH = this.viewHeight(vg, uiHeight);
      return mx >= this.clickGuiStatePanelState.floatVal
         && mx <= this.clickGuiStatePanelState.floatVal + 175.0F
         && my >= this.clickGuiStatePanelState.floatVal2 + 32.0F
         && my <= this.clickGuiStatePanelState.floatVal2 + 32.0F + viewH;
   }

   public void onScroll(double amount) {
      if (!(this.maxScroll <= 0.0F)) {
         this.scroll.setTarget(Math.clamp(this.scroll.getTarget() - (float)amount * 32.0F, 0.0F, this.maxScroll));
      }
   }

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

