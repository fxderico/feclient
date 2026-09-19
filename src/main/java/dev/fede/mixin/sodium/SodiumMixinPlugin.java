package dev.fede.mixin.sodium;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Gates every mixin in feclient.sodium.mixins.json behind "is Sodium actually
 * installed" — without this, anyone running feclient WITHOUT Sodium would
 * crash on startup the moment Mixin tries to find a class
 * (net.caffeinemc.mods.sodium.*) that doesn't exist.
 */
public final class SodiumMixinPlugin implements IMixinConfigPlugin {
   private static final boolean SODIUM_PRESENT = FabricLoader.getInstance().isModLoaded("sodium");

   @Override
   public void onLoad(String mixinPackage) {
   }

   @Override
   public String getRefMapperConfig() {
      return null;
   }

   @Override
   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      return SODIUM_PRESENT;
   }

   @Override
   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   @Override
   public List<String> getMixins() {
      return null;
   }

   @Override
   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   @Override
   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
