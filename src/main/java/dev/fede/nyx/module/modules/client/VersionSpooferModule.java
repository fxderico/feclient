package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import net.minecraft.text.Text;

/**
 * VersionSpoofer — spoofs the client brand string the game reports to the server.
 *
 * A Fabric client sends "fabric" as its brand (ClientBrandRetriever.getClientModName),
 * which instantly outs you as modded to any server or staff tool reading the brand
 * channel. This swaps that string for "vanilla" (or whatever you pick) so you read
 * as a stock client.
 *
 * The brand is only sent once, right after you join a world, so a change only takes
 * effect on your next reconnect — the module says so when you toggle it. The actual
 * swap is done by ClientBrandRetrieverMixin, which reads {@link #spoofedBrand()}.
 */
public final class VersionSpooferModule extends Module {
   private final ModeSetting brand = new ModeSetting("Brand", "vanilla",
      "vanilla", "fabric", "forge", "neoforge", "optifine", "custom");
   private final StringSetting custom = new StringSetting("Custom", "vanilla", 32);

   // read by ClientBrandRetrieverMixin on the brand-send path (join). null = don't spoof.
   private static volatile String spoofed = null;

   public VersionSpooferModule() {
      super("VersionSpoofer", "Spoofs the client brand sent to the server (hide \"fabric\")", Category.CLIENT);
      this.run6(new Setting[]{this.brand, this.custom});
      this.custom.visibleWhen(() -> "custom".equals(this.brand.getMode()));
   }

   @Override
   public void run() {
      spoofed = resolve();
      if (class310.player != null) {
         class310.player.sendMessage(
            Text.literal("§b[VersionSpoofer] §rbrand → §f" + spoofed + " §7(reconnect for it to take effect)"), false);
      }
   }

   @Override
   public void run2() {
      spoofed = null;
   }

   // keep the static in sync if the setting changes while enabled
   @Override
   public void run3() {
      String r = resolve();
      if (!r.equals(spoofed)) {
         spoofed = r;
      }
   }

   private String resolve() {
      String mode = this.brand.getMode();
      if ("custom".equals(mode)) {
         String c = this.custom.getValue();
         return c == null || c.isEmpty() ? "vanilla" : c;
      }
      return mode;
   }

   public static String spoofedBrand() {
      return spoofed;
   }

   @Override
   public String getString3() {
      return spoofed != null ? "§7" + spoofed : null;
   }
}
