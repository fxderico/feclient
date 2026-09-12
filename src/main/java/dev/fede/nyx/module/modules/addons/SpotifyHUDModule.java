package dev.fede.nyx.module.modules.addons;

import dev.fede.nyx.imgui.AlbumArtCache;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class SpotifyHUDModule extends Module {
   public static SpotifyHUDModule spotifyHUDModule;
   private static boolean bool = false;
   public final NumberSetting refreshSec = new NumberSetting("RefreshSec", 1.0, 1.0, 30.0, 1.0);
   public final BooleanSetting showArt = new BooleanSetting("ShowAlbumArt", true);
   public final NumberSetting posX = new NumberSetting("PosX", 6.0, 0.0, 2000.0, 1.0);
   public final NumberSetting posY = new NumberSetting("PosY", 40.0, 0.0, 1000.0, 1.0);
   public final ModeSetting anchor = new ModeSetting("Anchor", "BottomLeft", "BottomLeft", "BottomRight", "TopLeft", "TopRight");
   private SmtcMediaClient smtcMediaClient;

   public SpotifyHUDModule() {
      super("SpotifyHUD", "Now-Playing HUD via Windows SMTC", Category.ADDONS, 0);
      spotifyHUDModule = this;
      this.run6(new Setting[]{this.refreshSec, this.showArt, this.posX, this.posY, this.anchor});
   }

   @Override
   public void run() {
      if (!isEnabled2() && !bool) {
         bool = true;
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1 != null) {
            var1.execute(() -> this.run3());
         }
      }

      this.smtcMediaClient = new SmtcMediaClient(this::getLong);
      this.smtcMediaClient.run();
   }

   @Override
   public void run2() {
      if (this.smtcMediaClient != null) {
         this.smtcMediaClient.run2();
         this.smtcMediaClient = null;
      }

      AlbumArtCache.clear();
   }

   public SmtcMediaClient.Inner1 getSmtcMediaClienta() {
      SmtcMediaClient var1 = this.smtcMediaClient;
      return var1 != null ? var1.getSmtcMediaClienta() : null;
   }

   @Override
   public int getInt() {
      SmtcMediaClient.Inner1 var1 = this.getSmtcMediaClienta();
      return var1 == null ? 0 : AlbumArtCache.glHandleFor(var1.artPath(), var1.artKey());
   }

   @Override
   public boolean isEnabled() {
      SmtcMediaClient.Inner1 var1 = this.getSmtcMediaClienta();
      return var1 != null && var1.playing();
   }

   public SmtcMediaClient.State getSmtcMediaClientState() {
      SmtcMediaClient var1 = this.smtcMediaClient;
      return var1 != null ? var1.getSmtcMediaClientState() : SmtcMediaClient.State.IDLE;
   }

   public SmtcMediaClient getSmtcMediaClient() {
      return this.smtcMediaClient;
   }

   private static boolean isEnabled2() {
      return System.getProperty("os.name", "").toLowerCase().contains("win");
   }

   private long getLong() {
      return this.refreshSec.getValueLong();
   }

   private static void run3(MinecraftClient var0) {
      if (var0.player != null) {
         var0.player.sendMessage(Text.literal("§8[§aCode Engine§8] §cSpotifyHUD needs Windows SMTC - module will show nothing"), true);
      }
   }
}

