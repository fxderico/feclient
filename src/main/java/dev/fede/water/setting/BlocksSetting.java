package dev.fede.water.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class BlocksSetting extends Setting<Set<Block>> {
   private final List<Block> availableBlocks = Registries.BLOCK
      .stream()
      .filter(block -> block != Blocks.AIR)
      .sorted(Comparator.comparing(this::getDisplayName, String.CASE_INSENSITIVE_ORDER))
      .toList();
   private long version;

   public BlocksSetting(String name, Block... defaults) {
      super(name, createDefaultSet(defaults));
   }

   public void setValue(Set<Block> value) {
      LinkedHashSet var2 = new LinkedHashSet();
      if (value != null) {
         for (Block var3 : value) {
            if (var3 != null && var3 != Blocks.AIR) {
               var2.add(var3);
            }
         }
      }

      super.setValue(var2);
      this.version++;
   }

   public boolean contains(Block block) {
      return block != null && this.getValue().contains(block);
   }

   public void toggle(Block block) {
      if (block != null && block != Blocks.AIR) {
         LinkedHashSet var2 = new LinkedHashSet<>(this.getValue());
         if (!var2.add(block)) {
            var2.remove(block);
         }

         this.setValue(var2);
      }
   }

   public void clear() {
      if (!this.getValue().isEmpty()) {
         this.setValue(Collections.emptySet());
      }
   }

   public int size() {
      return this.getValue().size();
   }

   public long getVersion() {
      return this.version;
   }

   public Set<Block> getSelectedBlocks() {
      return Collections.unmodifiableSet(this.getValue());
   }

   public List<Block> getAvailableBlocks() {
      return this.availableBlocks;
   }

   public List<Block> filter(String query) {
      query = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
      if (query.isEmpty()) {
         return this.availableBlocks;
      } else {
         ArrayList var2 = new ArrayList();

         for (Block var4 : this.availableBlocks) {
            String var5 = this.getDisplayName(var4).toLowerCase(Locale.ROOT);
            Identifier var6 = Registries.BLOCK.getId(var4);
            String var8 = var6 == null ? "" : var6.toString().toLowerCase(Locale.ROOT);
            if (var5.contains(query) || var8.contains(query)) {
               var2.add(var4);
            }
         }

         return var2;
      }
   }

   public String getDisplayName(Block block) {
      try {
         return block.getName().getString();
      } catch (Exception var2) {
         Identifier var3 = Registries.BLOCK.getId(block);
         return var3 == null ? "Block" : var3.getPath();
      }
   }

   public String getSummary() {
      if (this.getValue().isEmpty()) {
         return "None";
      } else {
         Block var1 = this.getValue().iterator().next();
         String var3 = this.getDisplayName(var1);
         int var2 = this.getValue().size() - 1;
         return var2 > 0 ? var3 + " +" + var2 : var3;
      }
   }

   private static Set<Block> createDefaultSet(Block... defaults) {
      LinkedHashSet var1 = new LinkedHashSet();
      if (defaults != null) {
         Collections.addAll(var1, defaults);
         var1.remove(null);
         var1.remove(Blocks.AIR);
      }

      return var1;
   }
}

