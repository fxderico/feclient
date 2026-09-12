package dev.fede.gui;

import com.google.gson.JsonObject;
import dev.fede.module.Category;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ClickGuiState {
   public static final String THEMES_PANEL = "__themes__";
   private final Map<String, ClickGuiState.PanelState> panels = new LinkedHashMap<>();
   private final Set<String> expandedModules = new HashSet<>();
   private boolean laidOut;
   private float lastLayoutWidth = -1.0F;
   private float lastLayoutHeight = -1.0F;
   private static final int LAYOUT_VERSION = 4;
   private static final float PANEL_W = 175.0F;
   private static final float PANEL_GAP = 12.0F;
   private static final float SEARCH_W = 280.0F;

   public void markCustomized() {
      // no-op — positions always save now
   }

   public ClickGuiState() {
      float x = 16.0F;
      for (Category category : Category.values()) {
         this.panels.put(category.name(), new PanelState(x, 16.0F));
         x += PANEL_W + PANEL_GAP;
      }
      this.panels.put("__themes__", new PanelState(x, 16.0F));
   }

   public void ensureDefaultLayout(float uiWidth, float uiHeight) {
      if (!this.laidOut || uiWidth != this.lastLayoutWidth || uiHeight != this.lastLayoutHeight) {
         this.laidOut = true;
         this.lastLayoutWidth = uiWidth;
         this.lastLayoutHeight = uiHeight;
         String[] order = new String[]{
            Category.COMBAT.name(), Category.MISC.name(), Category.MOVEMENT.name(),
            Category.RENDER.name(), Category.PLAYER.name(), Category.WORLD.name(),
            Category.DONUT.name(), Category.ADDONS.name(), Category.CLIENT.name(), "__themes__"
         };
         float spacing = PANEL_W + PANEL_GAP;
         int perRow = Math.max(1, (int)((uiWidth - 16.0F) / spacing));
         for (int ix = 0; ix < order.length; ix++) {
            ClickGuiState.PanelState ps = this.panel(order[ix]);
            ps.floatVal = 12.0F + (ix % perRow) * spacing;
            ps.floatVal2 = 48.0F + (ix / perRow) * (uiHeight * 0.46F);
            ps.collapsed = ix / perRow > 0;
         }
      }
   }

   public ClickGuiState.PanelState panel(String key) {
      return this.panels.computeIfAbsent(key, k -> new PanelState(16.0F, 16.0F));
   }

   public boolean isExpanded(String moduleKey) {
      return this.expandedModules.contains(moduleKey);
   }

   public void setExpanded(String moduleKey, boolean expanded) {
      if (expanded) {
         this.expandedModules.add(moduleKey);
      } else {
         this.expandedModules.remove(moduleKey);
      }
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("v", LAYOUT_VERSION);
      for (Entry<String, ClickGuiState.PanelState> entry : this.panels.entrySet()) {
         JsonObject p = new JsonObject();
         p.addProperty("x", entry.getValue().floatVal);
         p.addProperty("y", entry.getValue().floatVal2);
         p.addProperty("collapsed", entry.getValue().collapsed);
         json.add(entry.getKey(), p);
      }
      return json;
   }

   public void fromJson(JsonObject json) {
      if (json.has("v") && json.get("v").getAsInt() >= 4) {
         this.laidOut = true;
         for (Entry<String, ClickGuiState.PanelState> entry : this.panels.entrySet()) {
            JsonObject p = json.getAsJsonObject(entry.getKey());
            if (p != null) {
               if (p.has("x")) entry.getValue().floatVal  = p.get("x").getAsFloat();
               if (p.has("y")) entry.getValue().floatVal2 = p.get("y").getAsFloat();
               if (p.has("collapsed")) entry.getValue().collapsed = p.get("collapsed").getAsBoolean();
            }
         }
      }
   }

   public class PanelState {
      public float floatVal;
      public float floatVal2;
      public boolean collapsed;

      PanelState(float x, float y) {
         this.floatVal = x;
         this.floatVal2 = y;
      }
   }
}
