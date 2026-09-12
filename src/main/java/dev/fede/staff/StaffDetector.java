package dev.fede.staff;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

public final class StaffDetector {
   public static final String MODE_STAR_RANK = "Star + Rank";
   public static final String MODE_STAR = "Star Only";
   public static final String MODE_RANK = "Rank Only";
   public static final String MODE_NAMES = "Names Only";
   public static final String DEFAULT_SYMBOLS = "★☆✦✧✪✩✫✬✭✮✯⭐✰❂⚝✴✵✶✷✸✹⍟";
   public static final List<String> DEFAULT_RANK_KEYWORDS = List.of(
      "coowner",
      "owner",
      "manager",
      "administrator",
      "admin",
      "developer",
      "dev",
      "srmod",
      "seniormod",
      "moderator",
      "mod",
      "srhelper",
      "seniorhelper",
      "helper",
      "trialmod",
      "trial",
      "builder",
      "support",
      "staff"
   );
   public static final String DEFAULT_RANK_KEYWORDS_STRING = String.join(", ", DEFAULT_RANK_KEYWORDS);
   private static final Map<String, String> RANK_LABELS = Map.ofEntries(
      Map.entry("coowner", "Co-Owner"),
      Map.entry("owner", "Owner"),
      Map.entry("manager", "Manager"),
      Map.entry("administrator", "Admin"),
      Map.entry("admin", "Admin"),
      Map.entry("developer", "Dev"),
      Map.entry("dev", "Dev"),
      Map.entry("srmod", "Sr.Mod"),
      Map.entry("seniormod", "Sr.Mod"),
      Map.entry("moderator", "Mod"),
      Map.entry("mod", "Mod"),
      Map.entry("srhelper", "Sr.Helper"),
      Map.entry("seniorhelper", "Sr.Helper"),
      Map.entry("helper", "Helper"),
      Map.entry("trialmod", "Trial"),
      Map.entry("trial", "Trial"),
      Map.entry("builder", "Builder"),
      Map.entry("support", "Support"),
      Map.entry("staff", "Staff")
   );

   private StaffDetector() {
   }

   public static StaffEntry classify(
      String name, Text display, Text teamPrefix, Text teamSuffix, String teamName, boolean vanished, int latency, StaffDetector.DetectConfig cfg
   ) {
      if (name != null && !name.isEmpty()) {
         boolean listed = cfg.names().contains(name.toLowerCase(Locale.ROOT));
         Integer starColor = scanMarker(display, cfg);
         if (starColor == null) {
            starColor = scanMarker(teamPrefix, cfg);
         }

         if (starColor == null) {
            starColor = scanMarker(teamSuffix, cfg);
         }

         boolean hasStar;
         StaffDetector.Rank rank;
         boolean hasRank;
         boolean var10000;
         label85: {
            hasStar = starColor != null;
            String combined = plain(display) + " " + plain(teamPrefix) + " " + plain(teamSuffix) + " " + (teamName == null ? "" : teamName);
            rank = deriveRank(stripName(combined, name), cfg.rankKeywords());
            hasRank = rank != null;
            String color = cfg.mode();
            byte label = -1;
            switch (color.hashCode()) {
               case -203046726:
                  if (color.equals("Star Only")) {
                     var10000 = hasStar;
                     break label85;
                  }
                  break;
               case 1302991648:
                  if (color.equals("Rank Only")) {
                     var10000 = hasRank;
                     break label85;
                  }
                  break;
               case 2083116484:
                  if (color.equals("Names Only")) {
                     var10000 = false;
                     break label85;
                  }
            }

            var10000 = hasStar || hasRank;
         }

         boolean isStaff = var10000;
         if (!listed && !isStaff) {
            return null;
         } else {
            int color = hasStar ? starColor : 0;
            String label = hasRank ? rank.label() : "";
            int priority = hasRank ? rank.priority() : (hasStar ? 1 : 0);
            return new StaffEntry(name, label, color, vanished, latency, priority);
         }
      } else {
         return null;
      }
   }

   static Integer scanMarker(Text c, StaffDetector.DetectConfig cfg) {
      return c == null ? null : (Integer)c.visit((style, str) -> {
         int i = 0;

         while (i < str.length()) {
            int cp = str.codePointAt(i);
            i += Character.charCount(cp);
            if (isMarker(cp, cfg)) {
               TextColor tc = style.getColor();
               return Optional.of(tc != null ? 0xFF000000 | tc.getRgb() : 0);
            }
         }

         return Optional.empty();
      }, Style.EMPTY).orElse(null);
   }

   private static boolean isMarker(int cp, StaffDetector.DetectConfig cfg) {
      if (cfg.symbols().indexOf(cp) >= 0) {
         return true;
      } else {
         return !cfg.fontIcons() ? false : cp >= 57344 && cp <= 63743 || cp >= 983040 && cp <= 1048573 || cp >= 1048576 && cp <= 1114109;
      }
   }

   private static StaffDetector.Rank deriveRank(String tagText, List<String> keywords) {
      String cleaned = lettersOnly(tagText.toLowerCase(Locale.ROOT));
      if (cleaned.isEmpty()) {
         return null;
      } else {
         for (int i = 0; i < keywords.size(); i++) {
            String kw = keywords.get(i);
            if (!kw.isEmpty() && cleaned.contains(kw)) {
               new Rank(labelFor(kw), keywords.size() - i);
            }
         }

         return null;
      }
   }

   private static String labelFor(String keyword) {
      String known = RANK_LABELS.get(keyword);
      if (known != null) {
         return known;
      } else {
         return keyword.isEmpty() ? "Staff" : Character.toUpperCase(keyword.charAt(0)) + keyword.substring(1);
      }
   }

   private static String plain(Text c) {
      return c == null ? "" : c.getString();
   }

   private static String lettersOnly(String s) {
      StringBuilder sb = new StringBuilder(s.length());

      for (int i = 0; i < s.length(); i++) {
         char ch = s.charAt(i);
         if (ch >= 'a' && ch <= 'z') {
            sb.append(ch);
         }
      }

      return sb.toString();
   }

   private static String stripName(String text, String name) {
      return name != null && !name.isEmpty() ? text.replaceAll("(?i)" + Pattern.quote(name), " ") : text;
   }

   public final static class DetectConfig {
      private String mode;
      private Set<String> names;
      private List<String> rankKeywords;
      private String symbols;
      private boolean fontIcons;
      private boolean showVanished;

      public DetectConfig(String mode, Set<String> names, List<String> rankKeywords, String symbols, boolean fontIcons, boolean showVanished) {
         this.mode = mode;
         this.names = names;
         this.rankKeywords = rankKeywords;
         this.symbols = symbols;
         this.fontIcons = fontIcons;
         this.showVanished = showVanished;
      }

      public String mode() {
         return this.mode;
      }

      public Set<String> names() {
         return this.names;
      }

      public List<String> rankKeywords() {
         return this.rankKeywords;
      }

      public String symbols() {
         return this.symbols;
      }

      public boolean fontIcons() {
         return this.fontIcons;
      }

      public boolean showVanished() {
         return this.showVanished;
      }
   }

   final static class Rank {
      private String label;
      private int priority;

      private Rank(String label, int priority) {
         this.label = label;
         this.priority = priority;
      }

      public String label() {
         return this.label;
      }

      public int priority() {
         return this.priority;
      }
   }
}

