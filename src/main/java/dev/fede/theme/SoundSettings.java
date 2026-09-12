package dev.fede.theme;

import com.google.gson.JsonObject;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.Setting;
import dev.fede.settings.SliderSetting;
import java.util.List;

public class SoundSettings {
   public final SliderSetting masterVolume = new SliderSetting("Master Volume", "Volume for all client sounds", 60.0, 0.0, 100.0, 5.0, "%");
   public final BooleanSetting guiSounds = new BooleanSetting("GUI Open/Close", "Whoosh when the menu opens and closes", true);
   public final BooleanSetting hoverSounds = new BooleanSetting("Hover", "Soft ticks when hovering elements", true);
   public final BooleanSetting clickSounds = new BooleanSetting("Clicks & Toggles", "Pops for toggles, sliders and keybinds", true);
   public final BooleanSetting notificationSounds = new BooleanSetting("Notifications", "Chimes with toggle toasts", true);
   public final BooleanSetting startup67 = new BooleanSetting("Startup Track", "Startup track candidate", true);
   public final BooleanSetting startupSad = new BooleanSetting("Sad Song", "Startup track candidate", false);
   public final BooleanSetting startupSong = new BooleanSetting("Song", "Startup track candidate", false);
   public final BooleanSetting startupTiki = new BooleanSetting("Tiki Phonk", "Startup track candidate", false);
   private final List<Setting<?>> all = List.of(
      this.masterVolume,
      this.guiSounds,
      this.hoverSounds,
      this.clickSounds,
      this.notificationSounds,
      this.startup67,
      this.startupSad,
      this.startupSong,
      this.startupTiki
   );

   public List<Setting<?>> all() {
      return this.all;
   }

   public float volume() {
      return this.masterVolume.getFloat() / 100.0F;
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();

      for (Setting<?> setting : this.all) {
         json.add(setting.getName(), setting.toJson());
      }

      return json;
   }

   public void fromJson(JsonObject json) {
      for (Setting<?> setting : this.all) {
         if (json.has(setting.getName())) {
            setting.fromJson(json.get(setting.getName()));
         }
      }
   }
}

