package dev.fede.nyx.util;

import dev.fede.nyx.module.modules.donutsmp.NicknameModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public final class StringBuilderUtils {
   private StringBuilderUtils() {
   }

   public static String addSetting(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         String var1 = getString();
         if (var1 != null && var0.contains(var1)) {
            String var2 = "";
            MinecraftClient var3 = MinecraftClient.getInstance();
            if (var3 != null && var3.player != null) {
               var2 = var3.player.getUuidAsString();
            }

            String var4 = NicknameModule.stringOf(var1, var2);
            return var4 != null && !var4.equals(var1) ? var0.replace(var1, var4) : var0;
         } else {
            return var0;
         }
      } else {
         return var0;
      }
   }

   public static Text addSetting2(Text var0) {
      if (var0 == null) {
         return var0;
      } else {
         String var1 = var0.getString();
         String var2 = addSetting(var1);
         return (Text)(var2 == var1 ? var0 : Text.literal(var2));
      }
   }

   public static OrderedText addSetting3(OrderedText var0) {
      if (var0 == null) {
         return var0;
      } else {
         String var1 = stringOf(var0);
         String var2 = addSetting(var1);
         return var2 == var1 ? var0 : Text.literal(var2).asOrderedText();
      }
   }

   private static String getString() {
      NicknameModule var0 = NicknameModule.nicknameModule;
      if (var0 != null && var0.isEnabled3()) {
         String var1 = var0.getString();
         if (var1 != null && !var1.isEmpty()) {
            MinecraftClient var2 = MinecraftClient.getInstance();
            if (var2 != null && var2.getSession() != null) {
               String var3 = var2.getSession().getUsername();
               return var3 != null && !var3.isEmpty() ? var3 : null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String stringOf(OrderedText var0) {
      if (var0 == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         var0.accept((idx, style, cp) -> true);
         return var1.toString();
      }
   }

   private static boolean check(StringBuilder var0, int var1, Style var2, int var3) {
      var0.appendCodePoint(var3);
      return true;
   }
}

