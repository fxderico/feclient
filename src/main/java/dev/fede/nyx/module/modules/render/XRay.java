package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.auth.AuthGate;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class XRay extends Module {
   private static volatile Set<Block> set = Set.of();
   private static volatile boolean bool = false;
   private final BooleanSetting coal = new BooleanSetting("Coal", true);
   private final BooleanSetting iron = new BooleanSetting("Iron", true);
   private final BooleanSetting gold = new BooleanSetting("Gold", true);
   private final BooleanSetting diamond = new BooleanSetting("Diamond", true);
   private final BooleanSetting emerald = new BooleanSetting("Emerald", true);
   private final BooleanSetting redstone = new BooleanSetting("Redstone", true);
   private final BooleanSetting lapis = new BooleanSetting("Lapis", true);
   private final BooleanSetting copper = new BooleanSetting("Copper", true);
   private final BooleanSetting ancient = new BooleanSetting("AncientDebris", true);
   private final BooleanSetting netherite = new BooleanSetting("NetheriteBlock", false);
   private final StringSetting custom = new StringSetting("Custom", "minecraft:spawner, minecraft:vault", 512);
   private final BooleanSetting reloadOnToggle = new BooleanSetting("Reload On Toggle", true);
   private int intVal = 0;
   private static final Map<String, List<String>> map;

   public XRay() {
      super("XRay", "Hide non-whitelisted blocks so ores pop out of terrain", Category.RENDER);
      this.run6(
         new Setting[]{
            this.coal,
            this.iron,
            this.gold,
            this.diamond,
            this.emerald,
            this.redstone,
            this.lapis,
            this.copper,
            this.ancient,
            this.netherite,
            this.custom,
            this.reloadOnToggle
         }
      );
   }

   @Override
   public void run() {
      this.run4();
      bool = true;
      this.run6();
   }

   @Override
   public void run2() {
      bool = false;
      set = Set.of();
      this.run6();
   }

   @Override
   public void run3() {
      if (++this.intVal >= 20) {
         this.intVal = 0;
         this.run4();
      }
   }

   public static boolean isEnabled_s() {
      return bool;
   }

   public static boolean check3(Block var0) {
      return bool && set.contains(var0);
   }

   private void run4() {
      HashSet var1 = new HashSet();
      run5(var1, this.coal, map.get("coal"));
      run5(var1, this.iron, map.get("iron"));
      run5(var1, this.gold, map.get("gold"));
      run5(var1, this.diamond, map.get("diamond"));
      run5(var1, this.emerald, map.get("emerald"));
      run5(var1, this.redstone, map.get("redstone"));
      run5(var1, this.lapis, map.get("lapis"));
      run5(var1, this.copper, map.get("copper"));
      run5(var1, this.ancient, map.get("ancient_debris"));
      run5(var1, this.netherite, map.get("netherite"));
      String var2 = this.custom.getValue();
      if (var2 != null && !var2.isBlank()) {
         for (String var6 : var2.split(",")) {
            String var7 = var6.trim();
            if (!var7.isEmpty()) {
               Identifier var8 = Identifier.tryParse(var7);
               if (var8 != null) {
                  Block var9 = (Block)Registries.BLOCK.get(var8);
                  if (var9 != null && var9 != Blocks.AIR) {
                     var1.add(var9);
                  }
               }
            }
         }
      }

      set = Set.copyOf(var1);
   }

   private static void run5(Set<Block> var0, BooleanSetting var1, List<String> var2) {
      if (var1.getValue() && var2 != null) {
         for (String var4 : var2) {
            Identifier var5 = Identifier.tryParse(var4);
            if (var5 != null) {
               Block var6 = (Block)Registries.BLOCK.get(var5);
               if (var6 != null && var6 != Blocks.AIR) {
                  var0.add(var6);
               }
            }
         }
      }
   }

   private void run6() {
      if (this.reloadOnToggle.getValue()) {
         if (class310 != null && class310.worldRenderer != null) {
            try {
               class310.worldRenderer.reload();
            } catch (Throwable var2) {
               var2.printStackTrace();
            }
         }
      }
   }

   static {
      LinkedHashMap var0 = new LinkedHashMap();
      var0.put("coal", Arrays.asList("minecraft:coal_ore", "minecraft:deepslate_coal_ore"));
      var0.put("iron", Arrays.asList("minecraft:iron_ore", "minecraft:deepslate_iron_ore", "minecraft:raw_iron_block"));
      var0.put("gold", Arrays.asList("minecraft:gold_ore", "minecraft:deepslate_gold_ore", "minecraft:nether_gold_ore", "minecraft:raw_gold_block"));
      var0.put("diamond", Arrays.asList("minecraft:diamond_ore", "minecraft:deepslate_diamond_ore", "minecraft:diamond_block"));
      var0.put("emerald", Arrays.asList("minecraft:emerald_ore", "minecraft:deepslate_emerald_ore", "minecraft:emerald_block"));
      var0.put("redstone", Arrays.asList("minecraft:redstone_ore", "minecraft:deepslate_redstone_ore"));
      var0.put("lapis", Arrays.asList("minecraft:lapis_ore", "minecraft:deepslate_lapis_ore"));
      var0.put("copper", Arrays.asList("minecraft:copper_ore", "minecraft:deepslate_copper_ore", "minecraft:raw_copper_block"));
      var0.put("ancient_debris", Collections.singletonList("minecraft:ancient_debris"));
      var0.put("netherite", Collections.singletonList("minecraft:netherite_block"));
      map = Collections.unmodifiableMap(var0);
   }
}

