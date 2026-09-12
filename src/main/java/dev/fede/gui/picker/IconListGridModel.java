package dev.fede.gui.picker;

import dev.fede.settings.ColorSetting;
import dev.fede.settings.IconListSetting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

public final class IconListGridModel implements PickerGrid {
   private IconListSetting setting;
   private List<PickerGrid.Cell> cells;

   public IconListGridModel(IconListSetting setting) {
      this.setting = setting;
   }

   @Override
   public String title() {
      return this.setting.getName();
   }

   @Override
   public long activeCount() {
      return this.setting.enabledCount();
   }

   @Override
   public List<PickerGrid.Cell> cells() {
      if (this.cells == null) {
         List<PickerGrid.Cell> out = new ArrayList<>(this.setting.entries().size());

         for (IconListSetting.Entry entry : this.setting.entries()) {
            out.add(new EntryCell(entry));
         }

         this.cells = out;
      }

      return this.cells;
   }

   final class EntryCell implements PickerGrid.Cell {
      private IconListSetting.Entry entry;
      private ItemStack icon;

      EntryCell(IconListSetting.Entry entry) {
         this.entry = entry;
         this.icon = new ItemStack(entry.icon());
      }

      @Override
      public ItemStack icon() {
         return this.icon;
      }

      @Override
      public String label() {
         return this.entry.label();
      }

      @Override
      public boolean matches(String lowerQuery) {
         return this.entry.matches(lowerQuery);
      }

      @Override
      public boolean tracked() {
         return true;
      }

      @Override
      public boolean enabled() {
         return this.entry.enabled.get();
      }

      @Override
      public boolean selected() {
         return this.entry.enabled.get();
      }

      @Override
      public int color() {
         return this.entry.color.get();
      }

      @Override
      public void toggle() {
         this.entry.enabled.toggle();
      }

      @Override
      public ColorSetting colorTarget() {
         return this.entry.color;
      }
   }
}

