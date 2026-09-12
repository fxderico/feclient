package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import dev.fede.nyx.ui.INFO;
import dev.fede.nyx.ui.NotificationUtils;
import dev.fede.nyx.util.ChatFilterHelper;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChatFilter extends Module {
   public static volatile ChatFilter chatFilter;
   private final StringSetting patterns = new StringSetting("Patterns", "", 512);
   private final ModeSetting action = new ModeSetting("Action", "Hide", "Hide", "Grey", "Prefix");
   private final BooleanSetting whitelistFriends = new BooleanSetting("WhitelistFriends", true);
   private final BooleanSetting notify = new BooleanSetting("Notify", false);
   private volatile String string = null;
   private volatile Pattern[] patternArray = null;

   public ChatFilter() {
      super("ChatFilter", "Filters incoming chat messages by regex", Category.CLIENT);
      this.run6(new Setting[]{this.patterns, this.action, this.whitelistFriends, this.notify});
      chatFilter = this;
   }

   @Override
   public void run2() {
      String var1 = this.patterns.getValue();
      if (!Objects.equals(var1, this.string)) {
         this.string = var1;
         this.patternArray = null;
      }
   }

   public ChatFilter.Action chatFilterActionOf(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         Pattern[] var2 = this.patternArray;
         if (var2 == null) {
            String var3 = this.string;
            if (var3 == null) {
               var3 = this.patterns.getValue();
            }

            var2 = patternArrayOf(var3);
            this.patternArray = var2;
         }

         if (var2.length == 0) {
            return null;
         } else {
            if (this.whitelistFriends.getValue()) {
               Set var8 = ChatFilterHelper.chatFilterHelper.getSet();
               if (!var8.isEmpty()) {
                  String var4 = var1.toLowerCase(Locale.ROOT);

                  for (String var6 : (java.util.List<String>)var8) {
                     if (var4.contains(var6)) {
                        return null;
                     }
                  }
               }
            }

            String var9 = null;

            for (Pattern var7 : var2) {
               if (var7.matcher(var1).find()) {
                  var9 = var7.pattern();
                  break;
               }
            }

            if (var9 == null) {
               return null;
            } else {
               String var13 = this.action.getMode();

               ChatFilter.Action var11 = switch (var13) {
                  case "Hide" -> ChatFilter.Action.HIDE;
                  case "Grey" -> ChatFilter.Action.GREY;
                  case "Prefix" -> ChatFilter.Action.PREFIX;
                  default -> null;
               };
               if (var11 != null && this.notify.getValue()) {
                  NotificationUtils.run8("ChatFilter", var11.name() + ": /" + var9 + "/", INFO.UNKNOWN);
               }

               return var11;
            }
         }
      } else {
         return null;
      }
   }

   private static Pattern[] patternArrayOf(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String[] var1 = var0.split(",");
         ArrayList var2 = new ArrayList(var1.length);

         for (String var6 : var1) {
            String var7 = var6.trim();
            if (!var7.isEmpty()) {
               try {
                  var2.add(Pattern.compile(var7));
               } catch (PatternSyntaxException var9) {
               }
            }
         }

         return (Pattern[])(var2.toArray(new Object[0]));
      } else {
         return new Pattern[0];
      }
   }

   public static enum Action {
      HIDE,
      GREY,
      PREFIX;

      private static final ChatFilter.Action[] chatFilterActionArray = getChatFilterActionArray();

      private static ChatFilter.Action[] getChatFilterActionArray() {
         return new ChatFilter.Action[]{HIDE, GREY, PREFIX};
      }
   }
}

