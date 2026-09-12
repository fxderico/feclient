package dev.fede.theme;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
   public static final int PINK = -49508;
   private final List<Theme> themes = new ArrayList<>();
   private Theme current;

   public ThemeManager() {
      this.themes.add(new Theme("Pink", -49508, false));
      this.themes.add(new Theme("Purple", -5743361, false));
      this.themes.add(new Theme("Blue", -11689985, false));
      this.themes.add(new Theme("Red", -45715, false));
      this.themes.add(new Theme("Emerald", -12654960, false));
      this.themes.add(new RainbowTheme());
      this.current = this.themes.getFirst();
   }

   public Theme current() {
      return this.current;
   }

   public List<Theme> getThemes() {
      return this.themes;
   }

   public void select(Theme theme) {
      if (this.themes.contains(theme)) {
         this.current = theme;
      }
   }

   public Theme addCustom(int accent) {
      int n = 1;

      for (Theme t : this.themes) {
         if (t.isCustom()) {
            n++;
         }
      }

      Theme theme = new Theme("Custom " + n, accent, true);
      this.themes.add(theme);
      return theme;
   }

   public void removeCustom(Theme theme) {
      if (theme.isCustom() && this.themes.remove(theme) && this.current == theme) {
         this.current = this.themes.getFirst();
      }
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("current", this.current.getName());
      JsonArray customs = new JsonArray();

      for (Theme t : this.themes) {
         if (t.isCustom()) {
            JsonObject entry = new JsonObject();
            entry.addProperty("name", t.getName());
            entry.addProperty("accent", t.accent());
            customs.add(entry);
         }
      }

      json.add("custom", customs);
      return json;
   }

   public void fromJson(JsonObject json) {
      if (json != null) {
         this.themes.removeIf(Theme::isCustom);
         if (json.has("custom")) {
            for (JsonElement el : json.getAsJsonArray("custom")) {
               JsonObject entry = el.getAsJsonObject();
               this.themes.add(new Theme(entry.get("name").getAsString(), entry.get("accent").getAsInt(), true));
            }
         }

         if (json.has("current")) {
            String name = json.get("current").getAsString();

            for (Theme t : this.themes) {
               if (t.getName().equals(name)) {
                  this.current = t;
                  break;
               }
            }
         }
      }
   }
}

