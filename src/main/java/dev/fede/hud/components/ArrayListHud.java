package dev.fede.hud.components;

import dev.fede.hud.HudComponent;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.module.ModuleManager;
import dev.fede.module.Modules;
import dev.fede.render.anim.Animation;
import dev.fede.render.anim.Easing;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class ArrayListHud extends HudComponent {
   private static final float ENTRY_HEIGHT = 20.0F;
   private static final float FONT_SIZE = 13.5F;
   private static final float PAD_X = 8.0F;
   private static final float STRIP_W = 2.5F;
   private ModuleManager modules;
   private Modules.HudModule hudModule;
   private ThemeManager themes;
   private final Map<Module, Animation> slide = new HashMap<>();

   public ArrayListHud(ModuleManager modules, Modules.HudModule hudModule, ThemeManager themes, BooleanSupplier visible) {
      super("arraylist", 1.0F, 0.008F, visible);
      this.modules = modules;
      this.hudModule = hudModule;
      this.themes = themes;
   }

   private List<Module> animatedEntries(NVGRenderer vg) {
      List<Module> list = new ArrayList<>();

      for (Module module : this.modules.all()) {
         if (module.getCategory() != Category.CLIENT) {
            Animation anim = this.slide.computeIfAbsent(module, m -> new Animation(240.0F, m.isEnabled() ? 1.0F : 0.0F, Easing.EASE_OUT_CUBIC));
            anim.setTarget(module.isEnabled() ? 1.0F : 0.0F);
            if (module.isEnabled() || anim.value() > 0.01F) {
               list.add(module);
            }
         }
      }

      list.sort(Comparator.<Module>comparingDouble(m -> vg.textWidth(m.getName(), 13.5F)).reversed());
      return list;
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      float max = 40.0F;

      for (Module m : this.animatedEntries(vg)) {
         max = Math.max(max, vg.textWidth(m.getName(), 13.5F) + 16.0F + 2.5F + 2.0F);
      }

      return max;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      float h = 0.0F;

      for (Module m : this.animatedEntries(vg)) {
         h += 20.0F * this.slide.get(m).value();
      }

      return Math.max(20.0F, h);
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
      Theme theme = this.themes.current();
      boolean sync = this.hudModule.themeSync.get();
      boolean right = this.rightAnchored();
      float rowY = y;

      for (Module m : this.animatedEntries(vg)) {
         float t = this.slide.get(m).value();
         if (!(t <= 0.01F)) {
            int accent = sync ? theme.accent() : this.hudModule.listColor.get();
            int accentBright = sync ? theme.accentBright() : Colors.lighten(accent, 0.35F);
            float textW = vg.textWidth(m.getName(), 13.5F);
            float barW = textW + 16.0F + 2.5F + 2.0F;
            float slideOff = (1.0F - t) * (barW + 12.0F) * (right ? 1 : -1);
            float barX = Math.round((right ? x + w - barW : x) + slideOff);
            float rowTop = Math.round(rowY);
            vg.save();
            vg.alpha(t);
            vg.rectGradient(barX, rowTop, barW, 20.0F, 5.0F, Colors.withAlpha(-15330788, 0.86F), Colors.withAlpha(-15791084, 0.86F), true);
            float stripX = right ? barX + barW - 2.5F : barX;
            vg.glow(stripX - 1.0F, rowTop + 2.0F, 4.5F, 16.0F, 2.0F, 3.0F, Colors.withAlpha(accent, 0.35F));
            vg.rect(stripX, rowTop + 2.0F, 2.5F, 16.0F, 1.25F, accent);
            float textX = Math.round(right ? barX + 8.0F : barX + 2.5F + 2.0F + 8.0F - 2.0F);
            vg.textGradient(m.getName(), textX, rowTop + 10.0F, 13.5F, accentBright, accent);
            vg.restore();
            rowY += 20.0F * t;
         }
      }
   }
}

