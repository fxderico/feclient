package dev.fede.nyx.mixin;

import dev.fede.nyx.module.modules.client.ChatFilter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayNetworkHandler.class})
public class ChatFilterMixin {
   @Inject(
      method = {"onGameMessage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void nyx$onGameMessage(GameMessageS2CPacket var1, CallbackInfo var2) {
      if (!var1.overlay()) {
         ChatFilter var3 = ChatFilter.chatFilter;
         if (var3 != null && var3.isEnabled3()) {
            Text var4 = var1.content();
            if (var4 != null) {
               String var5 = var4.getString();
               ChatFilter.Action var6 = var3.chatFilterActionOf(var5);
               if (var6 != null) {
                  MinecraftClient var7 = MinecraftClient.getInstance();
                  switch (var6) {
                     case HIDE:
                        var2.cancel();
                        break;
                     case GREY:
                        var2.cancel();
                        if (var7 != null) {
                           var7.execute(() -> {
                              if (var7.inGameHud != null) {
                                 var7.inGameHud.getChatHud().addMessage(Text.literal(var5).formatted(Formatting.DARK_GRAY));
                              }
                           });
                        }
                        break;
                     case PREFIX:
                        var2.cancel();
                        if (var7 != null) {
                           var7.execute(() -> {
                              if (var7.inGameHud != null) {
                                 MutableText var2x = Text.literal("[F] ").formatted(Formatting.GRAY);
                                 var2x.append(var4);
                                 var7.inGameHud.getChatHud().addMessage(var2x);
                              }
                           });
                        }
                  }
               }
            }
         }
      }
   }
}

