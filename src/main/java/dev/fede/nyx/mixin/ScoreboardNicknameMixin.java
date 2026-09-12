package dev.fede.nyx.mixin;
import net.minecraft.scoreboard.ScoreboardObjective;

import dev.fede.nyx.module.modules.donutsmp.NicknameModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({InGameHud.class})
public class ScoreboardNicknameMixin {
   @Redirect(
      method = {"renderScoreboardSidebar(Lnet/minecraft/DrawContext;Lnet/minecraft/ScoreboardObjective;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/DrawContext;drawText(Lnet/minecraft/TextRenderer;Lnet/minecraft/OrderedText;IIIZ)V"
      ),
      require = 0
   )
   private void codeengine$swapOrderedText(DrawContext var1, TextRenderer var2, OrderedText var3, int var4, int var5, int var6, boolean var7) {
      String var8 = orderedToString(var3);
      String var9 = maybeSwap(var8);
      if (var9 == var8) {
         var1.drawText(var2, var3, var4, var5, var6, var7);
      } else {
         var1.drawText(var2, var9, var4, var5, var6, var7);
      }
   }

   @Redirect(
      method = {"renderScoreboardSidebar(Lnet/minecraft/DrawContext;Lnet/minecraft/ScoreboardObjective;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/DrawContext;drawText(Lnet/minecraft/TextRenderer;Lnet/minecraft/Text;IIIZ)V"
      ),
      require = 0
   )
   private void codeengine$swapText(DrawContext var1, TextRenderer var2, Text var3, int var4, int var5, int var6, boolean var7) {
      String var8 = var3 != null ? var3.getString() : "";
      String var9 = maybeSwap(var8);
      if (var9 == var8) {
         var1.drawText(var2, var3, var4, var5, var6, var7);
      } else {
         var1.drawText(var2, Text.literal(var9), var4, var5, var6, var7);
      }
   }

   @Redirect(
      method = {"renderScoreboardSidebar(Lnet/minecraft/DrawContext;Lnet/minecraft/ScoreboardObjective;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/DrawContext;drawText(Lnet/minecraft/TextRenderer;Ljava/lang/String;IIIZ)V"
      ),
      require = 0
   )
   private void codeengine$swapString(DrawContext var1, TextRenderer var2, String var3, int var4, int var5, int var6, boolean var7) {
      String var8 = maybeSwap(var3);
      var1.drawText(var2, var8, var4, var5, var6, var7);
   }

   private static String maybeSwap(String s) {
      if (s != null && !s.isEmpty()) {
         NicknameModule var1 = NicknameModule.nicknameModule;
         if (var1 != null && var1.isEnabled3()) {
            String var2 = var1.getString();
            if (var2 != null && !var2.isEmpty()) {
               MinecraftClient var3 = MinecraftClient.getInstance();
               if (var3 != null && var3.getSession() != null) {
                  String var4 = var3.getSession().getUsername();
                  if (var4 != null && !var4.isEmpty() && s.contains(var4)) {
                     String var5 = var3.player != null ? var3.player.getUuidAsString() : "";
                     String var6 = NicknameModule.stringOf(var4, var5);
                     return s.replace(var4, var6);
                  } else {
                     return s;
                  }
               } else {
                  return s;
               }
            } else {
               return s;
            }
         } else {
            return s;
         }
      } else {
         return s;
      }
   }

   private static String orderedToString(OrderedText var0) {
      if (var0 == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder();
         var0.accept((var1x, var2, var3) -> {
            var1.appendCodePoint(var3);
            return true;
         });
         return var1.toString();
      }
   }
}

