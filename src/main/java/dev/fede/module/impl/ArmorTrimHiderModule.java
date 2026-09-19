package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public class ArmorTrimHiderModule extends Module {
   public final ModeSetting mode = this.addSetting(
      new ModeSetting("Mode", "Hide wipes worn trims; Random gives every piece a random one", "Hide", "Hide", "Random")
   );
   public final BooleanSetting ownArmor = this.addSetting(new BooleanSetting("Own Armor", "Also affect your own worn armor (F5 / inventory)", true));
   private DynamicRegistryManager cachedAccess;
   private final List<RegistryEntry<ArmorTrimMaterial>> materials = new ArrayList<>();
   private final List<RegistryEntry<ArmorTrimPattern>> patterns = new ArrayList<>();

   public ArmorTrimHiderModule() {
      super("ArmorTrimHider", "Hides or randomizes worn armor trims", Category.MISC);
   }

   public boolean affectsOwn() {
      return this.ownArmor.get();
   }

   @Nullable
   public ArmorTrim mapTrim(ItemStack stack, @Nullable ArmorTrim original) {
      if (this.mode.check("Random")) {
         ArmorTrim random = this.randomTrim(stack);
         return random != null ? random : original;
      } else {
         return null;
      }
   }

   @Nullable
   private ArmorTrim randomTrim(ItemStack stack) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.world == null) {
         return null;
      } else {
         DynamicRegistryManager access = mc.world.getRegistryManager();
         if (access != this.cachedAccess) {
            this.rebuildCache(access);
         }

         if (!this.materials.isEmpty() && !this.patterns.isEmpty()) {
            Random rng = new Random(stack.getItem().getTranslationKey().hashCode());
            RegistryEntry<ArmorTrimMaterial> material = this.materials.get(rng.nextInt(this.materials.size()));
            RegistryEntry<ArmorTrimPattern> pattern = this.patterns.get(rng.nextInt(this.patterns.size()));
            return new ArmorTrim(material, pattern);
         } else {
            return null;
         }
      }
   }

   private void rebuildCache(DynamicRegistryManager access) {
      this.cachedAccess = access;
      this.materials.clear();
      this.patterns.clear();

      try {
         access.getOrThrow(RegistryKeys.TRIM_MATERIAL).streamEntries().forEach(this.materials::add);
         access.getOrThrow(RegistryKeys.TRIM_PATTERN).streamEntries().forEach(this.patterns::add);
      } catch (Exception var3) {
         this.materials.clear();
         this.patterns.clear();
      }
   }

   @Override
   protected void onDisable() {
      this.cachedAccess = null;
      this.materials.clear();
      this.patterns.clear();
   }
}

