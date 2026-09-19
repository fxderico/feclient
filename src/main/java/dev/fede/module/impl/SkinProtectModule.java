package dev.fede.module.impl;

import com.google.common.collect.LinkedHashMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import dev.fede.FeClient;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.StringSetting;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.SkinTextures;

public class SkinProtectModule extends Module {
   private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
   public final StringSetting ign = this.addSetting(new StringSetting("Skin IGN", "Username whose skin is applied", "", 16, "Type a username…"));
   public final ModeSetting applyTo = this.addSetting(new ModeSetting("Apply To", "Whose skin gets replaced", "Everyone", "Everyone", "Others", "Self"));
   private volatile SkinTextures replacement;
   private volatile String fetchedFor = "";
   private volatile boolean fetching;

   public SkinProtectModule() {
      super("SkinProtect", "Replaces skins so clips can't dox skins", Category.MISC);
   }

   @Override
   protected void onEnable() {
      if (this.replacement == null) {
         this.fetchedFor = "";
      }

      this.ensureFetched();
   }

   @Override
   public void onTick() {
      this.ensureFetched();
   }

   public SkinTextures replacementSkin() {
      return this.replacement;
   }

   public boolean shouldReplace(UUID uuid) {
      if (uuid == null) {
         return false;
      } else {
         MinecraftClient mc = MinecraftClient.getInstance();
         UUID self = mc.player == null ? null : mc.player.getUuid();
         if (this.applyTo.check("Everyone")) {
            return true;
         } else {
            return this.applyTo.check("Self") ? self != null && self.equals(uuid) : self == null || !self.equals(uuid);
         }
      }
   }

   private void ensureFetched() {
      String want = this.ign.get().trim();
      if (!want.isEmpty() && !this.fetching && !want.equalsIgnoreCase(this.fetchedFor)) {
         this.fetching = true;
         this.fetchedFor = want;
         CompletableFuture.runAsync(() -> this.resolve(want)).whenComplete((v, t) -> this.fetching = false);
      }
   }

   private void resolve(String username) {
      try {
         JsonObject profile = getJson("https://api.mojang.com/users/profiles/minecraft/null");
         if (profile == null || !profile.has("id")) {
            FeClient.LOGGER.warn("[SkinProtect] Unknown username: {}", username);
            return;
         }

         UUID uuid = dashify(profile.get("id").getAsString());
         String name = profile.has("name") ? profile.get("name").getAsString() : username;
         JsonObject full = getJson("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "") + "?unsigned=false");
         if (full == null || !full.has("properties")) {
            FeClient.LOGGER.warn("[SkinProtect] No profile textures for {}", username);
            return;
         }

         LinkedHashMultimap<String, Property> props = LinkedHashMultimap.create();

         for (JsonElement el : full.getAsJsonArray("properties")) {
            JsonObject prop = el.getAsJsonObject();
            if ("textures".equals(prop.get("name").getAsString())) {
               String value = prop.get("value").getAsString();
               String signature = prop.has("signature") ? prop.get("signature").getAsString() : null;
               props.put("textures", signature == null ? new Property("textures", value) : new Property("textures", value, signature));
            }
         }

         GameProfile gameProfile = new GameProfile(uuid, name, new PropertyMap(props));
         MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(gameProfile).thenAccept(opt -> {
            if (opt.isPresent()) {
               this.replacement = (SkinTextures)opt.get();
               FeClient.LOGGER.info("[SkinProtect] Loaded skin for {}", name);
            } else {
               FeClient.LOGGER.warn("[SkinProtect] Could not load skin texture for {}", name);
            }
         });
      } catch (Exception var12) {
         FeClient.LOGGER.warn("[SkinProtect] Failed to fetch skin for {}: {}", username, var12.toString());
      }
   }

   private static JsonObject getJson(String url) throws Exception {
      return null;
   }

   private static UUID dashify(String undashed) {
      return UUID.fromString(undashed.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
   }
}



