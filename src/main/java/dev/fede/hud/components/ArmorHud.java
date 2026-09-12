package dev.fede.hud.components;

import dev.fede.hud.HudComponent;
import dev.fede.render.nanovg.NVGImages;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.theme.Theme;
import dev.fede.theme.ThemeManager;
import dev.fede.util.Colors;
import java.util.function.BooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ArmorHud extends HudComponent {
   private static final EquipmentSlot[] SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final float ICON = 22.0F;
   private static final float PAD = 7.0F;
   private static final float GAP = 6.0F;
   private static final float BAR_H = 3.0F;
   private ThemeManager themes;

   public ArmorHud(ThemeManager themes, BooleanSupplier visible) {
      super("armor", 0.5F, 0.8F, visible);
      this.themes = themes;
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      return 14.0F + SLOTS.length * 22.0F + (SLOTS.length - 1) * 6.0F;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      return 38.0F;
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
      Theme theme = this.themes.current();
      PlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null) {
         vg.rectGradient(x, y, w, h, 9.0F, theme.background(), theme.backgroundTo(), true);
         float ix = x + 7.0F;

         for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = player.getEquippedStack(slot);
            float iy = y + 4.0F;
            if (stack.isEmpty()) {
               vg.rectOutline(ix, iy, 22.0F, 22.0F, 5.0F, 1.0F, Colors.withAlpha(theme.textDisabled(), 0.5F));
            } else {
               Identifier itemId = Registries.ITEM.getId(stack.getItem());
               int image = NVGImages.fromResource(Identifier.of(itemId.getNamespace(), "textures/item/" + itemId.getPath() + ".png"));
               if (image > 0) {
                  vg.imagePattern(image, ix, iy, 22.0F, 22.0F, ix, iy, 22.0F, 22.0F, 1.0F);
               } else {
                  vg.rect(ix, iy, 22.0F, 22.0F, 5.0F, Colors.withAlpha(theme.accent(), 0.3F));
               }

               if (stack.isDamageable()) {
                  float frac = 1.0F - (float)stack.getDamage() / stack.getMaxDamage();
                  int barColor = Colors.lerp(-1684147, -11671924, frac);
                  float barY = iy + 22.0F + 3.0F;
                  vg.rect(ix, barY, 22.0F, 3.0F, 1.5F, Colors.withAlpha(-16777216, 0.45F));
                  vg.rect(ix, barY, Math.max(3.0F, 22.0F * frac), 3.0F, 1.5F, barColor);
               }
            }

            ix += 28.0F;
         }
      }
   }
}

