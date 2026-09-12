package dev.fede.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.item.Item;

public class IconListSetting extends Setting<List<IconListSetting.Entry>> {
   private final Map<String, IconListSetting.Entry> byKey = new LinkedHashMap<>();

   public IconListSetting(String name, String description) {
      super(name, description, new ArrayList<>());
   }

   public IconListSetting.Entry add(String key, String label, Item icon, boolean enabled, int color) {
      IconListSetting.Entry entry = new Entry(key, label, icon, enabled, color);
      this.value.add(entry);
      this.byKey.put(key, entry);
      return entry;
   }

   public List<IconListSetting.Entry> entries() {
      return this.value;
   }

   public IconListSetting.Entry get(String key) {
      return this.byKey.get(key);
   }

   public boolean isEnabled(String key) {
      IconListSetting.Entry e = this.byKey.get(key);
      return e != null && e.enabled.get();
   }

   public int color(String key) {
      IconListSetting.Entry e = this.byKey.get(key);
      return e != null ? e.color.get() : -1;
   }

   public int size() {
      return this.value.size();
   }

   public long enabledCount() {
      return this.value.stream().filter(e -> e.enabled.get()).count();
   }

   @Override
   public JsonElement toJson() {
      JsonArray arr = new JsonArray();

      for (IconListSetting.Entry e : this.value) {
         JsonObject o = new JsonObject();
         o.addProperty("key", e.key);
         o.addProperty("enabled", e.enabled.get());
         o.addProperty("color", e.color.get());
         arr.add(o);
      }

      return arr;
   }

   @Override
   public void fromJson(JsonElement element) {
      if (element != null && element.isJsonArray()) {
         for (JsonElement el : element.getAsJsonArray()) {
            if (el.isJsonObject()) {
               JsonObject o = el.getAsJsonObject();
               if (o.has("key")) {
                  IconListSetting.Entry e = this.byKey.get(o.get("key").getAsString());
                  if (e != null) {
                     if (o.has("enabled")) {
                        e.enabled.set(o.get("enabled").getAsBoolean());
                     }

                     if (o.has("color")) {
                        e.color.set(o.get("color").getAsInt());
                     }
                  }
               }
            }
         }
      }
   }

   public final class Entry {
      private String key;
      private String label;
      private Item icon;
      public BooleanSetting enabled;
      public ColorSetting color;

      Entry(String key, String label, Item icon, boolean enabled, int color) {
         this.key = key;
         this.label = label;
         this.icon = icon;
         this.enabled = new BooleanSetting("Enabled", "Highlight this type", enabled);
         this.color = new ColorSetting(label, "Highlight color", color);
      }

      public String key() {
         return this.key;
      }

      public String label() {
         return this.label;
      }

      public Item icon() {
         return this.icon;
      }

      public boolean matches(String lowerQuery) {
         return this.label.toLowerCase(Locale.ROOT).contains(lowerQuery) || this.key.contains(lowerQuery);
      }
   }
}

