package dev.fede.hud.components;

import dev.fede.hud.HudComponent;
import dev.fede.render.anim.Animation;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import dev.fede.util.CpsTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

public class KeystrokesHud extends HudComponent {
   private static final float KEY = 26.0F;
   private static final float GAP = 3.0F;
   private static final float FONT = 12.5F;
   private ThemeManager themes;
   private final Map<String, Animation> press = new HashMap<>();

   public KeystrokesHud(ThemeManager themes, BooleanSupplier visible) {
      super("keystrokes", 0.03F, 0.72F, visible);
      this.themes = themes;
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      return 84.0F;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      float h = 55.0F;
      h += 29.0F;
      return h + 18.6F;
   }

   private float pressT(String id, boolean down) {
      Animation anim = this.press.computeIfAbsent(id, k -> new Animation(110.0F, 0.0F));
      anim.setTarget(down ? 1.0F : 0.0F);
      return anim.value();
   }

   private void key(NVGRenderer vg, Theme theme, String id, String label, boolean down, float x, float y, float w, float h) {
      float t = this.pressT(id, down);
      int bg = Colors.lerp(Colors.withAlpha(-15462118, 0.78F), Colors.withAlpha(theme.accent(), 0.85F), t);
      vg.rect(x, y, w, h, 6.0F, bg);
      int fg = Colors.lerp(theme.textMuted(), -1, t);
      vg.text(label, x + (w - vg.textWidth(label, 12.5F)) / 2.0F, y + h / 2.0F, 12.5F, fg);
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
      Theme theme = this.themes.current();
      GameOptions options = MinecraftClient.getInstance().options;
      this.key(vg, theme, "w", "W", isDown(options.forwardKey), x + 26.0F + 3.0F, y, 26.0F, 26.0F);
      float row = y + 29.0F;
      this.key(vg, theme, "a", "A", isDown(options.leftKey), x, row, 26.0F, 26.0F);
      this.key(vg, theme, "s", "S", isDown(options.backKey), x + 26.0F + 3.0F, row, 26.0F, 26.0F);
      this.key(vg, theme, "d", "D", isDown(options.rightKey), x + 58.0F, row, 26.0F, 26.0F);
      row += 29.0F;
      float half = (w - 3.0F) / 2.0F;
      this.key(vg, theme, "lmb", "LMB " + CpsTracker.get(0), isDown(options.attackKey), x, row, half, 26.0F);
      this.key(vg, theme, "rmb", "RMB " + CpsTracker.get(1), isDown(options.useKey), x + half + 3.0F, row, half, 26.0F);
      row += 29.0F;
      half = this.pressT("space", isDown(options.jumpKey));
      int bg = Colors.lerp(Colors.withAlpha(-15462118, 0.78F), Colors.withAlpha(theme.accent(), 0.85F), half);
      vg.rect(x, row, w, 15.6F, 6.0F, bg);
      vg.rect(x + w * 0.25F, row + 15.6F / 2.0F - 1.25F, w * 0.5F, 2.5F, 1.25F, Colors.lerp(theme.textMuted(), -1, half));
   }

   private static boolean isDown(KeyBinding mapping) {
      return mapping.isPressed();
   }
}

