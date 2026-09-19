package dev.fede.module.impl;

import dev.fede.FeClient;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.StringSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

/**
 * "Spectate" — type a username, enable, and freecam's camera + chunk
 * loading (see FreecamModule.followEntity) lock onto that player's live
 * position/rotation, same as looking through their eyes, chunks loading
 * around THEM instead of you. Doesn't reimplement any camera plumbing —
 * this is a thin controller over the existing, already-proven freecam
 * camera hijack (CameraFreecamMixin) and chunk sync
 * (FreecamModule.syncFreecamChunkLoading), just fed a different position
 * source (the target entity instead of WASD).
 */
public class ViewModule extends Module {
   public final StringSetting username = this.addSetting(new StringSetting("Username", "Player to spectate", "", 16, "IGN"));

   public ViewModule() {
      super("View", "Spectate a player — camera + chunk loading follow them like freecam does you.", Category.MOVEMENT);
   }

   @Override
   protected void onEnable() {
      if (!tryStart()) {
         this.setEnabled(false);
      }
   }

   @Override
   protected void onDisable() {
      FreecamModule freecam = FreecamModule.get();
      if (freecam != null) {
         freecam.stopFollowing();
      }
   }

   @Override
   public void onTick() {
      FreecamModule freecam = FreecamModule.get();
      // freecam stops following on its own if the target dies, disconnects,
      // or changes worlds (see FreecamModule.onTick) — notice that here and
      // turn ourselves off with it instead of silently doing nothing.
      if (freecam == null || !freecam.isFollowing()) {
         this.setEnabled(false);
      }
   }

   private boolean tryStart() {
      String name = this.username.get();
      if (name == null || name.isBlank()) {
         FeClient.LOGGER.warn("[View] no username set");
         return false;
      }

      MinecraftClient mc = MinecraftClient.getInstance();
      ClientWorld level = mc.world;
      if (level == null) {
         return false;
      }

      AbstractClientPlayerEntity target = null;
      for (AbstractClientPlayerEntity player : level.getPlayers()) {
         if (player.getGameProfile().name().equalsIgnoreCase(name.trim())) {
            target = player;
            break;
         }
      }

      if (target == null) {
         FeClient.LOGGER.warn("[View] '{}' isn't a visible online player", name);
         return false;
      }

      if (target == mc.player) {
         FeClient.LOGGER.warn("[View] that's you");
         return false;
      }

      FreecamModule freecam = FreecamModule.get();
      if (freecam == null) {
         return false;
      }

      freecam.startFollowing(target);
      return true;
   }
}
