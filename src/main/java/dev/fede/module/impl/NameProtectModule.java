package dev.fede.module.impl;

import com.mojang.authlib.GameProfile;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.StringSetting;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;

public class NameProtectModule extends Module {
   public final StringSetting ownName = this.addSetting(new StringSetting("Your Alias", "What your own name is replaced with", "You", 16, "You"));
   public final ModeSetting style = this.addSetting(
      new ModeSetting("Others", "How other players' names are replaced", "Aliases", "Aliases", "Blank", "Player #")
   );
   public final BooleanSetting selfOnly = this.addSetting(new BooleanSetting("Self Only", "Only hide your own name, leave others alone", false));
   private static final int SEEN_CAP = 256;
   private final LinkedHashMap<String, String> seen = new LinkedHashMap<String, String>(16, 0.75F, true) {
      @Override
      protected boolean removeEldestEntry(Entry<String, String> eldest) {
         return this.size() > 256;
      }
   };
   private String cacheSig = null;
   private Map<String, String> cachedTargets = Map.of();
   private Pattern cachedPattern = null;
   private ClientPlayNetworkHandler lastConnection = null;

   public NameProtectModule() {
      super("NameProtect", "Hides player names in clips", Category.CLIENT);
   }

   private String selfName() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return mc.player == null ? null : mc.player.getGameProfile().name();
   }

   private boolean isSelf(String realName) {
      String self = this.selfName();
      return self != null && self.equalsIgnoreCase(realName);
   }

   public String styledFor(String realName) {
      if (!this.isSelf(realName)) {
         if (this.style.check("Blank")) {
            return "";
         } else {
            return this.style.check("Player #") ? "Player " + (Math.floorMod(realName.toLowerCase(Locale.ROOT).hashCode(), 99) + 1) : "Player";
         }
      } else {
         String alias = this.ownName.get();
         return alias != null && !alias.isBlank() ? alias : "You";
      }
   }

   private Map<String, String> buildTargets() {
      Map<String, String> map = new LinkedHashMap<>();
      MinecraftClient mc = MinecraftClient.getInstance();
      ClientPlayNetworkHandler connection = mc.getNetworkHandler();
      if (connection != this.lastConnection) {
         this.seen.clear();
         this.lastConnection = connection;
      }

      String self = this.selfName();
      if (self != null && !self.isEmpty()) {
         this.seen.put(self.toLowerCase(Locale.ROOT), self);
         map.put(self, this.styledFor(self));
      }

      boolean others = !this.selfOnly.get();
      if (connection != null) {
         for (PlayerListEntry info : connection.getPlayerList()) {
            GameProfile profile = info.getProfile();
            String name = profile == null ? null : profile.name();
            if (name != null && !name.isEmpty()) {
               this.seen.put(name.toLowerCase(Locale.ROOT), name);
               if (others) {
                  map.putIfAbsent(name, this.styledFor(name));
               }
            }
         }
      }

      if (others) {
         for (String name : new ArrayList<>(this.seen.values())) {
            map.putIfAbsent(name, this.styledFor(name));
         }
      }

      map.entrySet().removeIf(e -> e.getKey().equalsIgnoreCase(e.getValue()));
      return map;
   }

   private void ensureCache() {
      MinecraftClient mc = MinecraftClient.getInstance();
      int tick = mc.player == null ? -1 : mc.player.age;
      String sig = tick + "|" + this.selfOnly.get() + "|" + this.style.get() + "|" + this.ownName.get();
      if (!sig.equals(this.cacheSig)) {
         this.cacheSig = sig;
         this.cachedTargets = this.buildTargets();
         this.cachedPattern = buildPattern(this.cachedTargets);
      }
   }

   private static Pattern buildPattern(Map<String, String> targets) {
      if (targets.isEmpty()) {
         return null;
      } else {
         List<String> names = new ArrayList<>(targets.keySet());
         names.sort((a, b) -> Integer.compare(b.length(), a.length()));
         StringBuilder sb = new StringBuilder("(?i)(?<![A-Za-z0-9_])(");

         for (int i = 0; i < names.size(); i++) {
            if (i > 0) {
               sb.append('|');
            }

            sb.append(Pattern.quote(names.get(i)));
         }

         sb.append(")(?![A-Za-z0-9_])");
         return Pattern.compile(sb.toString());
      }
   }

   public String replacementForDisplay(String display) {
      if (display != null && !display.isEmpty()) {
         this.ensureCache();
         if (this.cachedPattern == null) {
            return null;
         } else {
            String replaced = this.replaceNames(display);
            return replaced.equals(display) ? null : replaced;
         }
      } else {
         return null;
      }
   }

   public Text censorChat(Text input) {
      if (input == null) {
         return null;
      } else {
         this.ensureCache();
         return this.cachedPattern == null ? input : this.rewrite(input);
      }
   }

   private Text rewrite(Text c) {
      TextContent contents = c.getContent();
      MutableText result;
      if (contents instanceof PlainTextContent ptc) {
         result = MutableText.of(PlainTextContent.of(this.replaceNames(ptc.string())));
      } else if (contents instanceof TranslatableTextContent tc) {
         Object[] args = tc.getArgs();
         Object[] newArgs = new Object[args.length];

         for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof Text ac) {
               newArgs[i] = this.rewrite(ac);
            } else if (arg instanceof String s) {
               newArgs[i] = this.replaceNames(s);
            } else {
               newArgs[i] = arg;
            }
         }

         result = MutableText.of(new TranslatableTextContent(tc.getKey(), tc.getFallback(), newArgs));
      } else {
         result = MutableText.of(contents);
      }

      result.setStyle(c.getStyle());

      for (Text sibling : c.getSiblings()) {
         result.append(this.rewrite(sibling));
      }

      return result;
   }

   private String replaceNames(String text) {
      if (text != null && !text.isEmpty() && this.cachedPattern != null) {
         Matcher m = this.cachedPattern.matcher(text);
         if (!m.find()) {
            return text;
         } else {
            StringBuilder out = new StringBuilder(text.length());
            int last = 0;

            do {
               String matched = m.group(1);
               String alias = this.aliasFor(matched);
               out.append(text, last, m.start());
               out.append(alias != null ? alias : matched);
               last = m.end();
            } while (m.find());

            out.append(text, last, text.length());
            return out.toString();
         }
      } else {
         return text;
      }
   }

   private String aliasFor(String matched) {
      String direct = this.cachedTargets.get(matched);
      if (direct != null) {
         return direct;
      } else {
         for (Entry<String, String> e : this.cachedTargets.entrySet()) {
            if (e.getKey().equalsIgnoreCase(matched)) {
               return e.getValue();
            }
         }

         return null;
      }
   }
}

