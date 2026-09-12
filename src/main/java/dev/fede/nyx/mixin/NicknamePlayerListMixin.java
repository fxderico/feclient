package dev.fede.nyx.mixin;
import net.minecraft.client.network.PlayerListEntry;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.fede.nyx.util.StringBuilderUtils;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({PlayerListHud.class})
public class NicknamePlayerListMixin {
   @ModifyReturnValue(
      method = {"getPlayerName(Lnet/minecraft/PlayerListEntry;)Lnet/minecraft/Text;"},
      at = {@At("RETURN")},
      require = 0
   )
   private Text nyx$swapTabName(Text var1) {
      return StringBuilderUtils.addSetting2(var1);
   }
}

