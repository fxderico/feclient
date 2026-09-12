package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;

public class FakeRolesModule extends Module {
   public static final String ROLE_NONE = "None";
   public static final String ROLE_SRMOD = "SR.MOD";
   public static final String ROLE_MEDIA = "MEDIA";
   public static final String ROLE_SRADMIN = "SR.ADMIN";
   private static final int GRAY = 8355711;
   private static final int GREEN = 5635925;
   private static final int PINK = 16733695;
   private static final int RED = 16733269;
   private static final int WHITE = 16777215;
   public final ModeSetting role = this.addSetting(
      new ModeSetting("Role", "Which fake rank tag to wear in front of your name", "None", "None", "SR.MOD", "MEDIA", "SR.ADMIN")
   );
   public final BooleanSetting nametag = this.addSetting(new BooleanSetting("Nametag", "Show the tag on your floating nametag (3rd person)", true));
   public final BooleanSetting tabList = this.addSetting(new BooleanSetting("Tab List", "Show the tag before your name in the player list (Tab)", true));
   public final BooleanSetting chat = this.addSetting(new BooleanSetting("Chat", "Show the tag before your name in chat messages", true));

   public FakeRolesModule() {
      super("FakeRoles", "Fake [SR.MOD] / [MEDIA] / [SR.ADMIN] rank tag on your own name", Category.DONUT);
   }

   public boolean isActive() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return this.isEnabled() && !this.role.check("None") && mc.player != null;
   }

   private String selfName() {
      MinecraftClient mc = MinecraftClient.getInstance();
      return mc.player == null ? null : mc.player.getGameProfile().name();
   }

   private boolean isSelf(String exactName) {
      String me = this.selfName();
      return me != null && !me.isEmpty() && me.equals(exactName);
   }

   private int roleColor() {
      String var1 = this.role.get();
      switch (var1.hashCode()) {
         case -1841572141:
            if (var1.equals("SR.MOD")) {
               return 5635925;
            }
            break;
         case -235700512:
            if (var1.equals("SR.ADMIN")) {
               return 16733269;
            }
            break;
         case 73234372:
            if (var1.equals("MEDIA")) {
               return 16733695;
            }
      }

      return 16777215;
   }

   private Style bracketStyle() {
      return Style.EMPTY.withColor(8355711).withBold(false);
   }

   private Style tagStyle() {
      return Style.EMPTY.withColor(this.roleColor()).withBold(true);
   }

   private Style nameStyle() {
      String var1 = this.role.get();
      switch (var1.hashCode()) {
         case -1841572141:
            if (var1.equals("SR.MOD")) {
               return Style.EMPTY.withColor(5635925).withBold(true);
            }
            break;
         case -235700512:
            if (var1.equals("SR.ADMIN")) {
               return Style.EMPTY.withColor(16733269).withBold(true);
            }
            break;
         case 73234372:
            if (var1.equals("MEDIA")) {
               return Style.EMPTY.withColor(16777215).withBold(false);
            }
      }

      return Style.EMPTY;
   }

   public Text tagComponent() {
      return Text.empty()
         .append(Text.literal("[").setStyle(this.bracketStyle()))
         .append(Text.literal(this.role.get()).setStyle(this.tagStyle()))
         .append(Text.literal("] ").setStyle(this.bracketStyle()));
   }

   public Text buildPrefixedDisplayName(String name) {
      return this.isActive() && name != null ? Text.empty().append(this.tagComponent()).append(Text.literal(name).setStyle(this.nameStyle())) : null;
   }

   public Text decorateNametag(Text tag) {
      return this.isActive() && this.nametag.get() ? this.modifyText(tag) : tag;
   }

   public Text decorateTab(Text display, String realName) {
      if (this.isActive() && this.tabList.get()) {
         Text prefixed = this.isSelf(realName) ? this.buildPrefixedDisplayName(realName) : null;
         return prefixed != null ? prefixed : display;
      } else {
         return display;
      }
   }

   public Text decorateChat(Text input) {
      return this.isActive() && this.chat.get() ? this.modifyText(input) : input;
   }

   private Text modifyText(Text input) {
      if (input == null) {
         return null;
      } else {
         String me = this.selfName();
         return me != null && !me.isBlank() && input.getString().contains(me) ? this.splice(input, me, new boolean[]{false}) : input;
      }
   }

   private Text splice(Text c, String name, boolean[] done) {
      TextContent contents = c.getContent();
      MutableText result;
      if (!done[0] && contents instanceof PlainTextContent ptc) {
         String text = ptc.string();
         int idx = text.indexOf(name);
         if (idx >= 0) {
            done[0] = true;
            result = Text.empty();
            if (idx > 0) {
               result.append(Text.literal(text.substring(0, idx)).setStyle(c.getStyle()));
            }

            result.append(this.buildPrefixedDisplayName(name));
            int end = idx + name.length();
            if (end < text.length()) {
               result.append(Text.literal(text.substring(end)).setStyle(c.getStyle()));
            }
         } else {
            result = MutableText.of(contents).setStyle(c.getStyle());
         }
      } else if (!done[0] && contents instanceof TranslatableTextContent tc) {
         Object[] args = tc.getArgs();
         Object[] out = new Object[args.length];

         for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof Text ac) {
               out[i] = this.splice(ac, name, done);
            } else if (!done[0] && arg instanceof String s && s.contains(name)) {
               done[0] = true;
               int idx = s.indexOf(name);
               MutableText split = Text.empty();
               if (idx > 0) {
                  split.append(Text.literal(s.substring(0, idx)));
               }

               split.append(this.buildPrefixedDisplayName(name));
               int end = idx + name.length();
               if (end < s.length()) {
                  split.append(Text.literal(s.substring(end)));
               }

               out[i] = split;
            } else {
               out[i] = arg;
            }
         }

         result = MutableText.of(new TranslatableTextContent(tc.getKey(), tc.getFallback(), out)).setStyle(c.getStyle());
      } else {
         result = MutableText.of(contents).setStyle(c.getStyle());
      }

      for (Text sibling : c.getSiblings()) {
         result.append(this.splice(sibling, name, done));
      }

      return result;
   }
}

