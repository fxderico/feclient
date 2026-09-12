package dev.fede.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({PlayerListHud.class})
public class PlayerTabOverlayMixin {
   @ModifyReturnValue(
      method = {"method_1918"},
      at = {@At("RETURN")}
   )
   private Text FeClient$protectTabName(Text original, PlayerListEntry playerInfo) {
      ModuleManager modules = FeClient.modules();
      if (modules == null) {
         return original;
      } else {
         GameProfile profile = playerInfo.getProfile();
         String name = profile == null ? null : profile.name();
         Text result = original;
         if (modules.nameProtect != null && modules.nameProtect.isEnabled() && name != null && !name.isEmpty()) {
            String replacement = modules.nameProtect.replacementForDisplay(name);
            if (replacement != null) {
               result = Text.literal(replacement);
            }
         }

         if (modules.fakeRoles != null && name != null) {
            result = modules.fakeRoles.decorateTab(result, name);
         }

         return result;
      }
   }
}



