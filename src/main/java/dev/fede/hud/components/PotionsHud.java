package dev.fede.hud.components;

import dev.fede.hud.HudComponent;
import dev.fede.render.nanovg.NVGImages;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class PotionsHud extends HudComponent {
   private static final float ROW = 24.0F;
   private static final float ICON = 18.0F;
   private static final float PAD = 7.0F;
   private static final float FONT = 12.5F;
   private ThemeManager themes;

   public PotionsHud(ThemeManager themes, BooleanSupplier visible) {
      super("potions", 0.995F, 0.6F, visible);
      this.themes = themes;
   }

   private List<StatusEffectInstance> effects() {
      PlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         return List.of();
      } else {
         List<StatusEffectInstance> list = new ArrayList<>(player.getStatusEffects());
         list.sort(Comparator.comparingInt(StatusEffectInstance::getDuration).reversed());
         return list;
      }
   }

   private static String label(StatusEffectInstance effect) {
      String name = ((StatusEffect)effect.getEffectType().value()).getName().getString();
      int amp = effect.getAmplifier();
      return amp > 0 ? name + " " + (amp + 1) : name;
   }

   private static String timer(StatusEffectInstance effect) {
      if (effect.isInfinite()) {
         return "∞";
      } else {
         int seconds = effect.getDuration() / 20;
         return String.format("%d:%02d", seconds / 60, seconds % 60);
      }
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      float max = 110.0F;

      for (StatusEffectInstance effect : this.effects()) {
         max = Math.max(max, 31.0F + vg.textWidth(label(effect), 12.5F) + 10.0F + vg.textWidth(timer(effect), 12.5F) + 7.0F);
      }

      return max;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      return Math.max(24.0F, this.effects().size() * 24.0F);
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
      Theme theme = this.themes.current();
      List<StatusEffectInstance> effects = this.effects();
      if (!effects.isEmpty()) {
         boolean right = this.rightAnchored();
         float rowY = y;

         for (StatusEffectInstance effect : effects) {
            float labelW = vg.textWidth(label(effect), 12.5F);
            float timerW = vg.textWidth(timer(effect), 12.5F);
            float rowW = 31.0F + labelW + 10.0F + timerW + 7.0F;
            float rowX = right ? x + w - rowW : x;
            float cy = rowY + 12.0F;
            vg.rect(rowX, rowY + 1.0F, rowW, 22.0F, 7.0F, Colors.withAlpha(-15462118, 0.78F));
            Identifier effectId = effect.getEffectType().getKey().map(k -> k.getValue()).orElse(null);
            if (effectId != null) {
               int image = NVGImages.fromResource(Identifier.of(effectId.getNamespace(), "textures/mob_effect/" + effectId.getPath() + ".png"));
               if (image > 0) {
                  vg.imagePattern(image, rowX + 7.0F, cy - 9.0F, 18.0F, 18.0F, rowX + 7.0F, cy - 9.0F, 18.0F, 18.0F, 1.0F);
               }
            }

            vg.text(label(effect), rowX + 7.0F + 18.0F + 6.0F, cy, 12.5F, theme.textPrimary());
            vg.textGradient(timer(effect), rowX + rowW - 7.0F - timerW, cy, 12.5F, theme.accentBright(), theme.accent());
            rowY += 24.0F;
         }
      }
   }
}

