package dev.fede.water.module.modules.render;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.Setting;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.biome.Biome.Precipitation;

public final class NoRender extends Module {
   public static NoRender instance;
   private final Setting<Boolean> rain = new Setting<>("Rain", true);
   private final Setting<Boolean> snow = new Setting<>("Snow", true);
   private final Setting<Boolean> thunder = new Setting<>("Thunder", true);

   public NoRender() {
      super("NoRender", Category.b);
      instance = this;
      this.addSetting(this.rain);
      this.addSetting(this.snow);
      this.addSetting(this.thunder);
   }

   public static boolean isActive() {
      return instance != null && instance.isEnabled() && mc != null && mc.world != null;
   }

   public static boolean hideRain() {
      return isActive() && instance.rain.getValue();
   }

   public static boolean hideSnow() {
      return isActive() && instance.snow.getValue();
   }

   public static boolean hideThunder() {
      return isActive() && instance.thunder.getValue();
   }

   public static boolean hideAllPrecipitation() {
      return hideRain() && hideSnow();
   }

   public static boolean hideRainGradient() {
      return hideAllPrecipitation();
   }

   public static Precipitation filterPrecipitation(Precipitation precipitation) {
      if (!isActive() || precipitation == null) {
         return precipitation;
      } else if (precipitation == Precipitation.RAIN && hideRain()) {
         return Precipitation.NONE;
      } else {
         return precipitation == Precipitation.SNOW && hideSnow() ? Precipitation.NONE : precipitation;
      }
   }

   public static boolean shouldCancelWeatherSound(SoundEvent soundEvent) {
      if (isActive() && soundEvent != null) {
         return !hideRain() || soundEvent != SoundEvents.WEATHER_RAIN && soundEvent != SoundEvents.WEATHER_RAIN_ABOVE
            ? hideThunder() && (soundEvent == SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER || soundEvent == SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT)
            : true;
      } else {
         return false;
      }
   }
}

