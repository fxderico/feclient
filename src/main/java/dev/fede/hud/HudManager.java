package dev.fede.hud;

import com.google.gson.JsonObject;
import dev.fede.hud.components.ArmorHud;
import dev.fede.hud.components.ArrayListHud;
import dev.fede.hud.components.InfoHud;
import dev.fede.hud.components.KeystrokesHud;
import dev.fede.hud.components.PotionsHud;
import dev.fede.hud.components.RadarHud;
import dev.fede.hud.components.RegionMapHud;
import dev.fede.hud.components.SpotifyHud;
import dev.fede.hud.components.StaffListHud;
import dev.fede.hud.components.WatermarkHud;
import dev.fede.module.ModuleManager;
import dev.fede.module.Modules;
import dev.fede.notification.NotificationManager;
import dev.fede.render.nanovg.NVGRenderer;
import dev.fede.spotify.SpotifyService;
import dev.fede.theme.ThemeManager;
import dev.fede.util.CpsTracker;
import dev.fede.util.TpsTracker;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class HudManager {
   private final List<HudComponent> components = new ArrayList<>();

   public HudManager(ModuleManager modules, ThemeManager themes, SpotifyService spotify, NotificationManager notifications) {
      Modules.HudModule hud = modules.hud;
      this.components.add(new WatermarkHud(themes, () -> hud.isEnabled() && hud.watermark.get()));
      this.components.add(new ArrayListHud(modules, hud, themes, () -> hud.isEnabled() && hud.arrayList.get()));
      this.components
         .add(
            new InfoHud(
               "fps",
               themes,
               "FPS",
               () -> Integer.toString(MinecraftClient.getInstance().getCurrentFps()),
               0.006F,
               0.985F,
               () -> hud.isEnabled() && hud.fps.get()
            )
         );
      this.components.add(new InfoHud("ping", themes, "Ping", HudManager::pingString, 0.055F, 0.985F, () -> hud.isEnabled() && hud.ping.get()));
      this.components.add(new InfoHud("coords", themes, "XYZ", HudManager::coordsString, 0.115F, 0.985F, () -> hud.isEnabled() && hud.coordinates.get()));
      this.components.add(new InfoHud("direction", themes, "Facing", HudManager::directionString, 0.24F, 0.985F, () -> hud.isEnabled() && hud.direction.get()));
      this.components
         .add(new InfoHud("tps", themes, "TPS", () -> String.format("%.1f", TpsTracker.get()), 0.33F, 0.985F, () -> hud.isEnabled() && hud.tps.get()));
      this.components
         .add(new InfoHud("cps", themes, "CPS", () -> CpsTracker.get(0) + " | " + CpsTracker.get(1), 0.4F, 0.985F, () -> hud.isEnabled() && hud.cps.get()));
      this.components.add(new ArmorHud(themes, () -> hud.isEnabled() && hud.armor.get()));
      this.components.add(new PotionsHud(themes, () -> hud.isEnabled() && hud.potions.get()));
      this.components.add(new KeystrokesHud(themes, () -> hud.isEnabled() && hud.keystrokes.get()));
      this.components.add(new RadarHud(hud, modules.susChunkFinder, themes, () -> hud.isEnabled() && hud.radar.get()));
      this.components.add(new RegionMapHud(modules.regionMap, themes));
      this.components.add(new StaffListHud(modules.staffList, themes));
      this.components.add(new SpotifyHud(modules.spotify, spotify, themes));
      this.components.add(notifications);
   }

   private static String coordsString() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         return "0, 0, 0";
      } else {
         BlockPos pos = player.getBlockPos();
         return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
      }
   }

   private static String pingString() {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && mc.getNetworkHandler() != null) {
         PlayerListEntry info = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
         return info == null ? "0ms" : info.getLatency() + "ms";
      } else {
         return "0ms";
      }
   }

   private static String directionString() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         return "N";
      } else {
         Direction dir = player.getHorizontalFacing();

         return switch (dir) {
            case NORTH -> "N  -Z";
            case SOUTH -> "S  +Z";
            case WEST -> "W  -X";
            case EAST -> "E  +X";
            default -> dir.getId().toUpperCase();
         };
      }
   }

   public List<HudComponent> getComponents() {
      return this.components;
   }

   public List<HudManager.Placement> layout(NVGRenderer vg, float uiWidth, float uiHeight, boolean includeHidden) {
      List<HudManager.Placement> placements = new ArrayList<>();

      for (HudComponent component : this.components) {
         if (includeHidden || component.visible()) {
            float scale = component.getScale();
            float w = component.measureWidth(vg) * scale;
            float h = component.measureHeight(vg) * scale;
            float x = component.getFx() * (uiWidth - w);
            float y = component.getFy() * (uiHeight - h);
            placements.add(new Placement(component, x, y, w, h));
         }
      }

      return placements;
   }

   public void render(NVGRenderer vg, float uiWidth, float uiHeight) {
      for (HudManager.Placement p : this.layout(vg, uiWidth, uiHeight, false)) {
         this.renderPlacement(vg, p);
      }
   }

   public void renderPlacement(NVGRenderer vg, HudManager.Placement p) {
      float scale = p.component().getScale();
      vg.save();
      vg.translate(Math.round(p.getFloat()), Math.round(p.getFloat2()));
      vg.scale(scale);
      p.component().render(vg, 0.0F, 0.0F, p.getFloat3() / scale, p.getFloat4() / scale);
      vg.restore();
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();

      for (HudComponent component : this.components) {
         JsonObject entry = new JsonObject();
         entry.addProperty("fx", component.getFx());
         entry.addProperty("fy", component.getFy());
         entry.addProperty("scale", component.getScale());
         json.add(component.getId(), entry);
      }

      return json;
   }

   public void fromJson(JsonObject json) {
      for (HudComponent component : this.components) {
         JsonObject entry = json.getAsJsonObject(component.getId());
         if (entry != null && entry.has("fx") && entry.has("fy")) {
            component.setPosition(entry.get("fx").getAsFloat(), entry.get("fy").getAsFloat());
            if (entry.has("scale")) {
               component.setScale(entry.get("scale").getAsFloat());
            }
         }
      }
   }

   public final class Placement {
      private HudComponent component;
      private float floatVal;
      private float floatVal2;
      private float floatVal3;
      private float floatVal4;

      public Placement(HudComponent component, float x, float y, float w, float h) {
         this.component = component;
         this.floatVal = x;
         this.floatVal2 = y;
         this.floatVal3 = w;
         this.floatVal4 = h;
      }

      /** Expanded hit area — 10 px padding on all sides makes HUDs much easier to grab. */
      public boolean contains(float px, float py) {
         float pad = 10.0F;
         return px >= this.floatVal - pad && px <= this.floatVal + this.floatVal3 + pad
             && py >= this.floatVal2 - pad && py <= this.floatVal2 + this.floatVal4 + pad;
      }

      public HudComponent component() {
         return this.component;
      }

      public float getFloat() {
         return this.floatVal;
      }

      public float getFloat2() {
         return this.floatVal2;
      }

      public float getFloat3() {
         return this.floatVal3;
      }

      public float getFloat4() {
         return this.floatVal4;
      }
   }
}

