package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.util.Colors;

public class CustomCrosshairModule extends Module {
   private static final int HOT_PINK = -49508;
   public final ModeSetting style = this.addSetting(
      new ModeSetting("Style", "Crosshair shape", "Cross", "Dot", "Cross", "Circle", "T-Shape", "Brackets", "Chevron", "FE Logo")
   );
   public final SliderSetting size = this.addSetting(new SliderSetting("Size", "Overall crosshair size", 7.0, 2.0, 24.0, 1.0, "px"));
   public final SliderSetting thickness = this.addSetting(new SliderSetting("Thickness", "Line / dot thickness", 2.0, 1.0, 6.0, 0.5, "px"));
   public final SliderSetting gap = this.addSetting(new SliderSetting("Gap", "Center gap for line styles", 3.0, 0.0, 12.0, 1.0, "px"));
   public final ColorSetting color = this.addSetting(new ColorSetting("Color", "Crosshair color", -49508));
   public final BooleanSetting rainbow = this.addSetting(new BooleanSetting("Rainbow", "Cycle through the rainbow", false));
   public final BooleanSetting centerDot = this.addSetting(new BooleanSetting("Center Dot", "Add a filled dot at the very center", false));
   public final BooleanSetting outline = this.addSetting(new BooleanSetting("Outline", "Dark border for contrast on any background", true));
   public final BooleanSetting glow = this.addSetting(new BooleanSetting("Glow", "Soft glow behind the crosshair", true));
   public final BooleanSetting hideVanilla = this.addSetting(new BooleanSetting("Hide Vanilla", "Hide Minecraft's default crosshair", true));

   public CustomCrosshairModule() {
      super("CustomCrosshair", "Draws a custom crosshair", Category.MISC);
   }

   public boolean shouldHideVanilla() {
      return this.isEnabled() && this.hideVanilla.get();
   }

   private int resolveColor() {
      if (this.rainbow.get()) {
         float hue = (float)(System.nanoTime() % 3000000000L) / 3.E9F;
         return 0xFF000000 | Colors.hsvToRgb(hue, 0.85F, 1.0F) & 16777215;
      } else {
         return this.color.get();
      }
   }

   public void render(NVGRenderer vg, float cx, float cy) {
      int col = this.resolveColor();
      int line = this.outline.get() ? -1342177280 : 0;
      float t = this.thickness.getFloat();
      float s = this.size.getFloat();
      float g = this.gap.getFloat();
      if (this.glow.get()) {
         vg.circleGlow(cx, cy, s + 2.0F, 6.0F, Colors.withAlpha(col, 0.45F));
      }

      label58: {
         String var9 = this.style.get();
         switch (var9.hashCode()) {
            case -1887427045:
               if (var9.equals("Chevron")) {
                  if (line != 0) {
                     vg.chevron(cx, cy + s * 0.15F, s + 2.0F, t + 2.0F, line, true);
                  }

                  vg.chevron(cx, cy + s * 0.15F, s, t, col, true);
                  break label58;
               }
               break;
            case -1390943032:
               if (var9.equals("T-Shape")) {
                  this.cross(vg, cx, cy, s, t, g, col, line, false, true);
                  break label58;
               }
               break;
            case -291913588:
               if (var9.equals("FE Logo")) {
                  this.logo(vg, cx, cy, s, col);
                  break label58;
               }
               break;
            case 68905:
               if (var9.equals("Dot")) {
                  this.dot(vg, cx, cy, Math.max(1.5F, s * 0.35F), col, line);
                  break label58;
               }
               break;
            case 29314283:
               if (var9.equals("Brackets")) {
                  this.brackets(vg, cx, cy, s, t, g, col, line);
                  break label58;
               }
               break;
            case 65382432:
               if (var9.equals("Cross")) {
                  this.cross(vg, cx, cy, s, t, g, col, line, true, true);
                  break label58;
               }
               break;
            case 2018617584:
               if (var9.equals("Circle")) {
                  this.ring(vg, cx, cy, s, t, col, line);
                  break label58;
               }
         }

         this.cross(vg, cx, cy, s, t, g, col, line, true, true);
      }

      if (this.centerDot.get() && !this.style.check("Dot") && !this.style.check("FE Logo")) {
         this.dot(vg, cx, cy, Math.max(1.2F, t * 0.8F), col, line);
      }
   }

   private void dot(NVGRenderer vg, float cx, float cy, float r, int col, int line) {
      if (line != 0) {
         vg.circle(cx, cy, r + 1.0F, line);
      }

      vg.circle(cx, cy, r, col);
   }

   private void ring(NVGRenderer vg, float cx, float cy, float r, float t, int col, int line) {
      if (line != 0) {
         vg.circleOutline(cx, cy, r, t + 2.0F, line);
      }

      vg.circleOutline(cx, cy, r, t, col);
   }

   private void cross(NVGRenderer vg, float cx, float cy, float len, float t, float g, int col, int line, boolean top, boolean bottom) {
      if (line != 0) {
         this.segments(vg, cx, cy, len, t + 2.0F, g, line, top, bottom);
      }

      this.segments(vg, cx, cy, len, t, g, col, top, bottom);
   }

   private void segments(NVGRenderer vg, float cx, float cy, float len, float t, float g, int argb, boolean top, boolean bottom) {
      vg.line(cx + g, cy, cx + g + len, cy, t, argb);
      vg.line(cx - g, cy, cx - g - len, cy, t, argb);
      if (top) {
         vg.line(cx, cy - g, cx, cy - g - len, t, argb);
      }

      if (bottom) {
         vg.line(cx, cy + g, cx, cy + g + len, t, argb);
      }
   }

   private void brackets(NVGRenderer vg, float cx, float cy, float len, float t, float g, int col, int line) {
      float arm = Math.max(2.0F, len * 0.5F);
      float d = len + g * 0.4F;
      if (line != 0) {
         this.drawBrackets(vg, cx, cy, d, arm, t + 2.0F, line);
      }

      this.drawBrackets(vg, cx, cy, d, arm, t, col);
   }

   private void drawBrackets(NVGRenderer vg, float cx, float cy, float d, float arm, float t, int argb) {
      vg.line(cx - d, cy - d, cx - d + arm, cy - d, t, argb);
      vg.line(cx - d, cy - d, cx - d, cy - d + arm, t, argb);
      vg.line(cx + d, cy - d, cx + d - arm, cy - d, t, argb);
      vg.line(cx + d, cy - d, cx + d, cy - d + arm, t, argb);
      vg.line(cx - d, cy + d, cx - d + arm, cy + d, t, argb);
      vg.line(cx - d, cy + d, cx - d, cy + d - arm, t, argb);
      vg.line(cx + d, cy + d, cx + d - arm, cy + d, t, argb);
      vg.line(cx + d, cy + d, cx + d, cy + d - arm, t, argb);
   }

   private void logo(NVGRenderer vg, float cx, float cy, float s, int col) {
      float font = s * 2.4F;
      float w = vg.textWidth("fe", font);
      float tx = cx - w / 2.0F;
      vg.textGlow("fe", tx, cy, font, Colors.withAlpha(-49508, 0.6F));
      vg.text("fe", tx, cy, font, col);
   }
}

