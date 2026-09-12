package dev.fede.module.impl;

import dev.fede.FeClient;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.settings.StringSetting;
import dev.fede.staff.StaffDetector;
import dev.fede.staff.StaffEntry;
import dev.fede.staff.StaffTracker;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class StaffListModule extends Module {
   public final ModeSetting detectBy = this.addSetting(
      new ModeSetting(
         "Detect By",
         "How staff are recognised: the DonutSMP coloured star, a text rank prefix, or both",
         "Star + Rank",
         "Star + Rank",
         "Star Only",
         "Rank Only",
         "Names Only"
      )
   );
   public final StringSetting staffNames = this.addSetting(
      new StringSetting("Staff Names", "Extra known staff usernames (comma-separated) — always shown when online", "", 220, "e.g. Notch, jeb_")
   );
   public final StringSetting rankKeywords = this.addSetting(
      new StringSetting(
         "Rank Keywords",
         "Words in a name tag that mark staff (comma-separated), most senior first",
         StaffDetector.DEFAULT_RANK_KEYWORDS_STRING,
         256,
         "owner, admin, mod…"
      )
   );
   public final StringSetting starSymbols = this.addSetting(
      new StringSetting(
         "Star Symbols", "Marker glyphs that mean 'staff'. Paste the server's star here if detection misses", "★☆✦✧✪✩✫✬✭✮✯⭐✰❂⚝✴✵✶✷✸✹⍟", 96, "★ ✦ ⭐"
      )
   );
   public final BooleanSetting fontIcons = this.addSetting(
      new BooleanSetting("Font Icons", "Also treat custom resource-pack icons (private-use glyphs) as staff stars", true)
   );
   public final BooleanSetting showRank = this.addSetting(
      new BooleanSetting("Show Rank", "Show each staff member's rank label when the server exposes one", true)
   );
   public final BooleanSetting showPing = this.addSetting(new BooleanSetting("Show Ping", "Show each staff member's latency", false));
   public final BooleanSetting vanished = this.addSetting(
      new BooleanSetting("Vanished Staff", "Include soft-vanished staff (spectator / hidden from tab), marked separately", true)
   );
   public final SliderSetting maxRows = this.addSetting(
      new SliderSetting("Max Rows", "Most staff rows to show before collapsing into a '+N more' line", 6.0, 1.0, 20.0, 1.0)
   );
   public final ModeSetting alerts = this.addSetting(
      new ModeSetting("Alerts", "Announce when a new staff member appears in your tab", "Toast", "Toast", "Chat", "Off")
   );
   public final BooleanSetting alertSound = this.addSetting(new BooleanSetting("Alert Sound", "Play a bell when a staff alert fires", true));
   public final StaffTracker tracker = new StaffTracker(this);

   public StaffListModule() {
      super("StaffList", "Lists online staff on the HUD (DonutSMP coloured-star detection)", Category.DONUT);
      this.rankKeywords.visibleWhen(this::usesRank);
      this.starSymbols.visibleWhen(this::usesStar);
      this.fontIcons.visibleWhen(this::usesStar);
      this.alertSound.visibleWhen(() -> !this.alerts.check("Off"));
   }

   private boolean usesStar() {
      return this.detectBy.check("Star + Rank") || this.detectBy.check("Star Only");
   }

   private boolean usesRank() {
      return this.detectBy.check("Star + Rank") || this.detectBy.check("Rank Only");
   }

   @Override
   protected void onEnable() {
      this.tracker.reset();
   }

   @Override
   protected void onDisable() {
      this.tracker.clear();
   }

   @Override
   public void onTick() {
      this.tracker.tick();
   }

   public List<StaffEntry> staff() {
      return this.tracker.current();
   }

   public StaffDetector.DetectConfig detectConfig() {
      List<String> keywords = parseKeywords(this.rankKeywords.get());
      return new StaffDetector.DetectConfig(
         this.detectBy.get(),
         parseNames(this.staffNames.get()),
         keywords.isEmpty() ? StaffDetector.DEFAULT_RANK_KEYWORDS : keywords,
         this.starSymbols.get(),
         this.fontIcons.get(),
         this.vanished.get()
      );
   }

   public void onStaffAppear(StaffEntry e) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null && !this.alerts.check("Off")) {
         String rank = e.rankLabel().isEmpty() ? "Staff" : e.rankLabel();
         String tail = e.vanished() ? " (vanished)" : "";
         if (this.alerts.check("Toast")) {
            if (FeClient.notifications() != null) {
               FeClient.notifications().pushInfo(rank + " " + e.name() + " online" + tail);
            }
         } else if (this.alerts.check("Chat")) {
            mc.player.sendMessage(Text.literal("§d[FE] §f" + rank + " §b" + e.name() + "§7 is online" + tail), false);
         }

         // sound suppressed
      }
   }

   private static Set<String> parseNames(String raw) {
      Set<String> out = new HashSet<>();

      for (String part : raw.split("[,\\s]+")) {
         if (!part.isEmpty()) {
            out.add(part.toLowerCase(Locale.ROOT));
         }
      }

      return out;
   }

   private static List<String> parseKeywords(String raw) {
      List<String> out = new ArrayList<>();

      for (String part : raw.split("[,\\s]+")) {
         String kw = part.trim().toLowerCase(Locale.ROOT);
         if (!kw.isEmpty()) {
            out.add(kw);
         }
      }

      return out;
   }
}



