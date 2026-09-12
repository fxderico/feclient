package dev.fede.gui.picker;

import dev.fede.settings.BlockListSetting;
import dev.fede.settings.ColorSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.IntSupplier;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class BlockGridModel implements PickerGrid {
   private static List<Block> allBlocks;
   private BlockListSetting setting;
   private IntSupplier defaultColor;
   private String title;
   private List<PickerGrid.Cell> cells;

   public BlockGridModel(BlockListSetting setting, IntSupplier defaultColor, String title) {
      this.setting = setting;
      this.defaultColor = defaultColor;
      this.title = title;
   }

   private static List<Block> allBlocks() {
      if (allBlocks == null) {
         List<Block> list = new ArrayList<>();

         for (Block block : Registries.BLOCK) {
            if (block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR) {
               list.add(block);
            }
         }

         allBlocks = list;
      }

      return allBlocks;
   }

   @Override
   public String title() {
      return this.title;
   }

   @Override
   public long activeCount() {
      return this.setting.enabledCount();
   }

   @Override
   public List<PickerGrid.Cell> cells() {
      if (this.cells == null) {
         List<PickerGrid.Cell> out = new ArrayList<>(allBlocks().size());

         for (Block block : allBlocks()) {
            out.add(new BlockGridModel.BlockCell(block));
         }

         this.cells = out;
      }

      return this.cells;
   }

   final class BlockCell implements PickerGrid.Cell {
      private Block block;
      private String search;
      private ItemStack icon;

      BlockCell(Block block) {
         this.block = block;
         Identifier id = Registries.BLOCK.getId(block);
         String path = id != null ? id.getPath() : "";
         String ns = id != null ? id.getNamespace() : "";
         this.search = (path + " " + ns + " " + BlockListSetting.displayName(block)).toLowerCase(Locale.ROOT);
      }

      @Override
      public ItemStack icon() {
         if (this.icon == null) {
            Item item = this.block.asItem();
            this.icon = new ItemStack(item == Items.AIR ? Items.BARRIER : item);
         }

         return this.icon;
      }

      @Override
      public String label() {
         return BlockListSetting.displayName(this.block);
      }

      @Override
      public boolean matches(String lowerQuery) {
         return this.search.contains(lowerQuery);
      }

      @Override
      public boolean tracked() {
         return BlockGridModel.this.setting.find(this.block) != null;
      }

      @Override
      public boolean enabled() {
         BlockListSetting.Target t = BlockGridModel.this.setting.find(this.block);
         return t != null && t.enabled.get();
      }

      @Override
      public boolean selected() {
         return BlockGridModel.this.setting.find(this.block) != null;
      }

      @Override
      public int color() {
         BlockListSetting.Target t = BlockGridModel.this.setting.find(this.block);
         return t != null ? t.color.get() : BlockGridModel.this.defaultColor.getAsInt();
      }

      @Override
      public void toggle() {
         BlockListSetting.Target t = BlockGridModel.this.setting.find(this.block);
         if (t == null) {
            BlockGridModel.this.setting.add(this.block, true, BlockGridModel.this.defaultColor.getAsInt());
         } else {
            t.enabled.toggle();
         }
      }

      @Override
      public ColorSetting colorTarget() {
         BlockListSetting.Target t = BlockGridModel.this.setting.find(this.block);
         if (t == null) {
            t = BlockGridModel.this.setting.add(this.block, true, BlockGridModel.this.defaultColor.getAsInt());
         }

         return t != null ? t.color : null;
      }
   }
}

