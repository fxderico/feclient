package dev.fede.gui.picker;

import dev.fede.settings.ColorSetting;
import java.util.List;
import net.minecraft.item.ItemStack;

public interface PickerGrid {
   String title();

   long activeCount();

   List<PickerGrid.Cell> cells();

   public interface Cell {
      ItemStack icon();

      String label();

      boolean matches(String var1);

      boolean tracked();

      boolean enabled();

      boolean selected();

      int color();

      void toggle();

      ColorSetting colorTarget();
   }
}

