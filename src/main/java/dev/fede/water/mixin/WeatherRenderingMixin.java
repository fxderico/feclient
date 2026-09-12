package dev.fede.water.mixin;

import dev.fede.water.module.modules.render.NoRender;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.Precipitation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({WeatherRendering.class})
public abstract class WeatherRenderingMixin {
   @Invoker("getPrecipitationAt")
   protected abstract Precipitation water$getPrecipitationAt(World var1, BlockPos var2);

   @Redirect(
      method = {"buildPrecipitationPieces"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/WeatherRendering;getPrecipitationAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"
      )
   )
   private Precipitation water$filterRenderedPrecipitation(WeatherRendering var1, World var2, BlockPos var3) {
      return NoRender.filterPrecipitation(this.water$getPrecipitationAt(var2, var3));
   }

   @Redirect(
      method = {"addParticlesAndSound"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/WeatherRendering;getPrecipitationAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"
      )
   )
   private Precipitation water$filterWeatherParticlesAndSounds(WeatherRendering var1, World var2, BlockPos var3) {
      return NoRender.filterPrecipitation(this.water$getPrecipitationAt(var2, var3));
   }
}

