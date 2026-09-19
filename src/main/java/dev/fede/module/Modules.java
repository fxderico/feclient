package dev.fede.module;

import dev.fede.gui.ClickGuiScreen;
import dev.fede.render.SusChunkRenderer;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ColorSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.Setting;
import dev.fede.settings.SliderSetting;
import dev.fede.suschunk.SusChunkScanner;
import net.minecraft.client.MinecraftClient;

public final class Modules {
   private Modules() {
   }

   public static class BlockOutlineModule extends Module {
      public final ColorSetting color = this.addSetting(new ColorSetting("Color", "Outline color", -49508));
      public final BooleanSetting rainbow = this.addSetting(new BooleanSetting("Rainbow", "Cycle the outline through the rainbow", false));
      public final SliderSetting glow = this.addSetting(new SliderSetting("Glow", "Outer glow intensity", 60.0, 0.0, 100.0, 5.0, "%"));
      public final SliderSetting thickness = this.addSetting(new SliderSetting("Thickness", "Core line width", 2.5, 1.0, 6.0, 0.5, "px"));
      public final SliderSetting fillOpacity = this.addSetting(new SliderSetting("Fill", "Transparent fill inside the block", 12.0, 0.0, 60.0, 2.0, "%"));
      public final ModeSetting animation = this.addSetting(new ModeSetting("Animation", "Outline animation style", "Pulse", "Pulse", "Gradient Flow", "Static"));

      public BlockOutlineModule() {
         super("CustomBlockOutline", "Glowing animated outline on the targeted block", Category.ADDONS);
         this.setEnabled(true);
      }
   }

   public static class ClickGuiModule extends Module {
      public final BooleanSetting blur = this.addSetting(new BooleanSetting("Blur", "Gaussian-blur the world behind the GUI", true));
      public final SliderSetting blurStrength = this.addSetting(new SliderSetting("Blur Strength", "How strong the background blur is", 6.0, 1.0, 10.0, 1.0));
      public final ModeSetting font = this.addSetting(new ModeSetting("Font", "GUI font (Xuong TTF or vanilla-style)", "Xuong", "Xuong", "Vanilla"));

      public ClickGuiModule() {
         super("ClickGUI", "The Client menu. In menus, Shift opens it too.", Category.CLIENT);
         this.getKeybind().set(344);
      }

      @Override
      protected void onEnable() {
         // Clicking the module in the GUI (or via keybind) should open the screen.
         // Defer to next tick so we're not opening a screen inside a screen event.
         MinecraftClient mc = MinecraftClient.getInstance();
         mc.execute(() -> {
            // was opening zenithgui's MenuScreen here — inconsistent with the
            // keybind path (FeClient wires the keybind to ClickGuiScreen), so
            // clicking this module vs pressing the key opened two different
            // menus. now both paths agree.
            mc.setScreen(new ClickGuiScreen());
            setEnabled(false); // ClickGUI isn't a persistent on/off toggle
         });
      }
   }

   public static class HudModule extends Module {
      public final BooleanSetting watermark = this.addSetting(new BooleanSetting("Watermark", "The FEClient watermark badge", true));
      public final BooleanSetting arrayList = this.addSetting(new BooleanSetting("ArrayList", "Enabled modules list", true));
      public final BooleanSetting fps = this.addSetting(new BooleanSetting("FPS", "Framerate readout", true));
      public final BooleanSetting ping = this.addSetting(new BooleanSetting("Ping", "Latency readout", false));
      public final BooleanSetting coordinates = this.addSetting(new BooleanSetting("Coordinates", "Block position readout", true));
      public final BooleanSetting direction = this.addSetting(new BooleanSetting("Direction", "Facing readout", true));
      public final BooleanSetting tps = this.addSetting(new BooleanSetting("TPS", "Server tick rate estimate", false));
      public final BooleanSetting cps = this.addSetting(new BooleanSetting("CPS", "Clicks per second", false));
      public final BooleanSetting armor = this.addSetting(new BooleanSetting("Armor", "Equipped armor + durability", false));
      public final BooleanSetting potions = this.addSetting(new BooleanSetting("Potions", "Active effects with timers", false));
      public final BooleanSetting keystrokes = this.addSetting(new BooleanSetting("Keystrokes", "WASD + mouse + space display", false));
      public final BooleanSetting radar = this.addSetting(new BooleanSetting("Radar", "Circular player radar", true));
      public final BooleanSetting themeSync = this.addSetting(new BooleanSetting("List Theme Sync", "ArrayList follows the theme color", true));
      public final ColorSetting listColor = this.addSetting(new ColorSetting("List Color", "ArrayList color when Theme Sync is off", -49508));
      public final SliderSetting radarRange = this.addSetting(new SliderSetting("Radar Range", "Scan radius in blocks", 48.0, 16.0, 128.0, 4.0, "m"));
      public final BooleanSetting radarHeads = this.addSetting(new BooleanSetting("Radar Heads", "Skin faces instead of dots", true));
      public final BooleanSetting notifications = this.addSetting(
         new BooleanSetting("Notifications", "Themed toasts when modules toggle & the weather changes", true)
      );
      public final SliderSetting notifyDuration = this.addSetting(
         new SliderSetting("Notify Duration", "How long a toast lingers before fading out", 2.5, 1.0, 6.0, 0.5, "s")
      );

      public HudModule() {
         super("HUD", "All HUD elements — move & resize via chat (T)", Category.CLIENT);
         this.setEnabled(true);
         this.notifyDuration.visibleWhen(this.notifications::get);
      }
   }

   public static class Placeholder extends Module {
      public Placeholder(String name, String description, Category category, Setting<?>... settings) {
         super(name, description, category);

         for (Setting<?> setting : settings) {
            this.addSetting(setting);
         }
      }
   }

   public static class SpotifyModule extends Module {
      public final ModeSetting source = this.addSetting(
         new ModeSetting("Source", "Auto uses the real Spotify session (Windows); Demo shows sample data", "Auto", "Auto", "Demo")
      );
      public final BooleanSetting controls = this.addSetting(new BooleanSetting("Controls", "Show prev / play / next buttons", true));
      public final BooleanSetting volume = this.addSetting(new BooleanSetting("Volume", "Show a slider for Spotify's app volume (Windows)", true));
      public final BooleanSetting hideWhenIdle = this.addSetting(new BooleanSetting("Hide When Idle", "Hide the card when nothing plays", true));

      public SpotifyModule() {
         super("SpotifyHUD", "Now playing — skip and seek from the HUD", Category.CLIENT);
         this.setEnabled(true);
      }
   }

   public static class SusChunkFinderModule extends Module {
      public final SliderSetting sensitivity = this.addSetting(
         new SliderSetting("Sensitivity", "Higher = stricter: more weighted evidence before a chunk flags", 3.0, 1.0, 10.0, 1.0)
            .withLabel(v -> (int)v + " (" + (int)v * 5 + ")")
      );
      public final BooleanSetting amethyst = this.addSetting(
         new BooleanSetting("Amethyst", "Hidden grown clusters via server block light 5 — the strongest signal", true)
      );
      public final BooleanSetting kelp = this.addSetting(new BooleanSetting("Kelp", "Fully grown / unusually tall kelp columns", true));
      public final BooleanSetting bamboo = this.addSetting(new BooleanSetting("Bamboo", "Fully grown, max-height bamboo", true));
      public final BooleanSetting berries = this.addSetting(new BooleanSetting("Berries", "Sweet berry bushes at max growth stage", true));
      public final BooleanSetting vines = this.addSetting(new BooleanSetting("Vines", "Vines grown far down from their support", true));
      public final BooleanSetting dripstone = this.addSetting(new BooleanSetting("Dripstone", "Dripstone spikes longer than natural generation", true));
      public final SliderSetting scanSpeed = this.addSetting(new SliderSetting("Scan Speed", "Chunks scanned per tick", 10.0, 2.0, 24.0, 1.0));
      public final SliderSetting renderY = this.addSetting(
         new SliderSetting("Render Y", "Height the flat chunk highlights render at", 100.0, -64.0, 320.0, 1.0)
      );
      public final SliderSetting fillOpacity = this.addSetting(new SliderSetting("Fill Opacity", "Fill opacity of the chunk quads", 90.0, 0.0, 255.0, 1.0));
      public final BooleanSetting outline = this.addSetting(new BooleanSetting("Outline", "Crisp border along the chunk edges", true));
      public final SliderSetting outlineOpacity = this.addSetting(new SliderSetting("Outline Opacity", "Border opacity", 200.0, 0.0, 255.0, 1.0));
      public final BooleanSetting smartMode = this.addSetting(new BooleanSetting("Smart Mode", "Merge nearby flags into zones with a centroid marker", true));
      public final SliderSetting mergeRadius = this.addSetting(
         new SliderSetting("Merge Radius", "Flags within this many chunks merge into one zone", 3.0, 1.0, 8.0, 1.0, " ch")
      );
      public final BooleanSetting centroidMarker = this.addSetting(
         new BooleanSetting("Centroid Marker", "Mark each zone's weighted centre — the likely base spot", true)
      );
      public final BooleanSetting showOnRadar = this.addSetting(
         new BooleanSetting("Show on Radar", "Pulse sus zones on the Radar; far zones clamp to the edge", true)
      );
      public final ModeSetting notifications = this.addSetting(
         new ModeSetting("Notifications", "Announce each new zone once, with coords and distance", "Toast", "Toast", "Chat", "Off")
      );
      public final SusChunkScanner scanner = new SusChunkScanner(this);

      public SusChunkFinderModule() {
         super("SusChunkFinder", "Finds long-loaded chunks — bases — via amethyst light & growth", Category.RENDER);
         this.outlineOpacity.visibleWhen(this.outline::get);
      }

      @Override
      public void onTick() {
         this.scanner.tick();
      }

      @Override
      protected void onDisable() {
         this.scanner.clear();
         SusChunkRenderer.reset();
      }
   }
}


