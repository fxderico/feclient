package dev.fede.module.impl;

import dev.fede.FeClient;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.notification.NotificationManager;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class WeatherNotifierModule extends Module {
   public final BooleanSetting rain = this.addSetting(new BooleanSetting("Rain", "Announce when rain starts/stops", true));
   public final BooleanSetting thunder = this.addSetting(new BooleanSetting("Thunder", "Announce when a thunderstorm starts/stops", true));
   public final BooleanSetting sound = this.addSetting(new BooleanSetting("Sound", "Play a ping on each change", true));
   public final ModeSetting output = this.addSetting(new ModeSetting("Output", "How the change is shown", "Notification", "Notification", "Chat", "Action Bar"));
   private Boolean lastRaining;
   private Boolean lastThundering;

   public WeatherNotifierModule() {
      super("WeatherNotifier", "Themed toast when the weather changes", Category.MISC);
   }

   @Override
   protected void onEnable() {
      this.lastRaining = null;
      this.lastThundering = null;
   }

   @Override
   public void onTick() {
      MinecraftClient mc = MinecraftClient.getInstance();
      World level = mc.world;
      if (level != null && mc.player != null) {
         boolean raining = level.isRaining();
         boolean thundering = level.isThundering();
         if (this.lastRaining != null && this.lastThundering != null) {
            if (thundering != this.lastThundering) {
               this.lastThundering = thundering;
               if (this.thunder.get()) {
                  if (thundering) {
                     this.notify(mc, "Thunderstorm", "A storm rolls in", NotificationManager.Weather.THUNDER, true);
                  } else {
                     this.notify(mc, "Storm cleared", "The thunder has passed", NotificationManager.Weather.CLEAR, false);
                  }
               }
            }

            if (raining != this.lastRaining) {
               this.lastRaining = raining;
               if (this.rain.get() && !thundering) {
                  if (raining) {
                     this.notify(mc, "Rain", "Rain starts to fall", NotificationManager.Weather.RAIN, true);
                  } else {
                     this.notify(mc, "Skies cleared", "The rain has stopped", NotificationManager.Weather.CLEAR, false);
                  }
               }
            }
         } else {
            this.lastRaining = raining;
            this.lastThundering = thundering;
         }
      }
   }

   private void notify(MinecraftClient mc, String title, String subtitle, NotificationManager.Weather weather, boolean started) {
      if (mc.player != null) {
         if (this.output.check("Notification")) {
            if (FeClient.notifications() != null) {
               FeClient.notifications().pushWeather(title, subtitle, weather, started);
            }
         } else {
            boolean actionBar = this.output.check("Action Bar");
            mc.player.sendMessage(Text.literal("§d[FE] §fnull — null"), actionBar);
         }

         // sound suppressed
      }
   }
}



