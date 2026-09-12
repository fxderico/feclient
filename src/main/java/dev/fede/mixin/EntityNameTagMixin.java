package dev.fede.mixin;

import com.mojang.authlib.GameProfile;
import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.FakeRolesModule;
import dev.fede.module.impl.NameTagsModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public class EntityNameTagMixin {
   @Inject(
      method = {"method_3926"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void FeClient$nameTag(
      EntityRenderState state, MatrixStack poseStack, OrderedRenderCommandQueue collector, CameraRenderState cameraRenderState, CallbackInfo ci
   ) {
      ModuleManager modules = FeClient.modules();
      if (modules != null && state.displayName != null) {
         NameTagsModule nameTags = modules.nameTags;
         boolean nameTagsOn = nameTags != null && nameTags.isEnabled();
         if (nameTagsOn) {
            String display = state.displayName.getString();
            if (isLocalPlayer(display)) {
               if (nameTags.hideOwnTag.get() || nameTags.players.get() && nameTags.self.get()) {
                  ci.cancel();
                  return;
               }
            } else if (isOnlinePlayer(display)) {
               if (nameTags.players.get() || nameTags.hidePlayerTags.get()) {
                  ci.cancel();
                  return;
               }
            } else if (nameTags.hideOtherTags.get()) {
               ci.cancel();
               return;
            }
         }

         if (modules.nameProtect != null && modules.nameProtect.isEnabled()) {
            String replacement = modules.nameProtect.replacementForDisplay(state.displayName.getString());
            if (replacement != null) {
               state.displayName = Text.literal(replacement);
            }
         }

         FakeRolesModule fakeRoles = modules.fakeRoles;
         if (fakeRoles != null && isLocalPlayer(state.displayName.getString())) {
            state.displayName = fakeRoles.decorateNametag(state.displayName);
         }
      }
   }

   private static boolean isLocalPlayer(String display) {
      if (display != null && !display.isEmpty()) {
         ClientPlayerEntity self = MinecraftClient.getInstance().player;
         if (self == null) {
            return false;
         } else {
            String name = self.getGameProfile().name();
            return name != null && !name.isEmpty() && display.contains(name);
         }
      } else {
         return false;
      }
   }

   private static boolean isOnlinePlayer(String display) {
      if (display != null && !display.isEmpty()) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.getNetworkHandler() == null) {
            return false;
         } else {
            for (PlayerListEntry info : mc.getNetworkHandler().getPlayerList()) {
               GameProfile profile = info.getProfile();
               String name = profile == null ? null : profile.name();
               if (name != null && !name.isEmpty() && display.contains(name)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }
}



