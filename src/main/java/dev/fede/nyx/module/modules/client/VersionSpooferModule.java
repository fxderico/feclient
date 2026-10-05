package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import net.minecraft.text.Text;

/**
 * VersionSpoofer — two independent spoofs of what the server believes about you.
 *
 * 1. BRAND: the client brand string (ClientBrandRetriever.getClientModName), which
 *    a Fabric client sends as "fabric" and instantly outs you as modded. Swapped
 *    for "vanilla"/etc by ClientBrandRetrieverMixin. Always works, you stay on
 *    1.21.11, takes effect on your next reconnect.
 *
 * 2. PROTOCOL: the actual version the server thinks you're on, driven through
 *    ViaFabricPlus (ProtocolTranslator.setTargetVersion) by reflection — no hard
 *    dependency, it no-ops with a note if VFP isn't installed. This is REAL: on a
 *    server that runs ViaVersion (DonutSMP does), you connect as the chosen
 *    version and Via translates both ways, so the server genuinely sees you on,
 *    say, 1.20.1. Useful because Grim-style anticheat applies version-specific
 *    checks. Caveat: you play within that version's mechanics, and it only
 *    applies to connections made AFTER you set it — reconnect.
 */
public final class VersionSpooferModule extends Module {
   private final ModeSetting brand = new ModeSetting("Brand", "vanilla",
      "vanilla", "fabric", "forge", "neoforge", "optifine", "custom");
   private final StringSetting custom = new StringSetting("Custom", "vanilla", 32);
   private final ModeSetting protocol = new ModeSetting("Protocol", "Off",
      "Off", "1.21.5", "1.21.4", "1.21", "1.20.6", "1.20.1", "1.19.4", "1.18.2", "1.17.1", "1.16.5", "1.12.2", "1.8.9");

   // read by ClientBrandRetrieverMixin on the brand-send path (join). null = don't spoof.
   private static volatile String spoofed = null;
   private String lastProtocol = "";
   private boolean viaWarned;

   public VersionSpooferModule() {
      super("VersionSpoofer", "Spoofs client brand (hide \"fabric\") and protocol version via ViaFabricPlus", Category.CLIENT);
      this.run6(new Setting[]{this.brand, this.custom, this.protocol});
      this.custom.visibleWhen(() -> "custom".equals(this.brand.getMode()));
   }

   @Override
   public void run() {
      spoofed = resolveBrand();
      this.lastProtocol = "";
      this.viaWarned = false;
      applyProtocol();
      if (class310.player != null) {
         class310.player.sendMessage(
            Text.literal("§b[VersionSpoofer] §rbrand → §f" + spoofed
               + (isProtocolOn() ? " §7| protocol → §f" + this.protocol.getMode() : "")
               + " §7(reconnect to apply)"), false);
      }
   }

   @Override
   public void run2() {
      spoofed = null;
      // hand the protocol back to native so Via stops translating next connect
      this.lastProtocol = "";
      try {
         setViaTarget(closestVersion("1.21.11"));
      } catch (Throwable ignored) {
      }
   }

   @Override
   public void run3() {
      String r = resolveBrand();
      if (!r.equals(spoofed)) {
         spoofed = r;
      }
      if (!this.protocol.getMode().equals(this.lastProtocol)) {
         applyProtocol();
      }
   }

   private boolean isProtocolOn() {
      return !"Off".equals(this.protocol.getMode());
   }

   private void applyProtocol() {
      String v = this.protocol.getMode();
      this.lastProtocol = v;
      try {
         Object target = closestVersion("Off".equals(v) ? "1.21.11" : v);
         if (target != null) {
            setViaTarget(target);
         }
      } catch (ClassNotFoundException e) {
         if (!this.viaWarned && class310.player != null) {
            this.viaWarned = true;
            class310.player.sendMessage(Text.literal("§b[VersionSpoofer] §cProtocol spoof needs ViaFabricPlus installed — brand still works."), false);
         }
      } catch (Throwable t) {
         if (!this.viaWarned && class310.player != null) {
            this.viaWarned = true;
            class310.player.sendMessage(Text.literal("§b[VersionSpoofer] §cProtocol set failed: " + t.getClass().getSimpleName()), false);
         }
      }
   }

   // reflection into ViaFabricPlus / ViaVersion so feClient needn't hard-depend on them
   private static Object closestVersion(String name) throws Exception {
      Class<?> pv = Class.forName("com.viaversion.viaversion.api.protocol.version.ProtocolVersion");
      return pv.getMethod("getClosest", String.class).invoke(null, name);
   }

   private static void setViaTarget(Object protocolVersion) throws Exception {
      if (protocolVersion == null) {
         return;
      }
      Class<?> pt = Class.forName("com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator");
      Class<?> pv = Class.forName("com.viaversion.viaversion.api.protocol.version.ProtocolVersion");
      pt.getMethod("setTargetVersion", pv).invoke(null, protocolVersion);
   }

   private String resolveBrand() {
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
      if (isProtocolOn()) {
         return "§7" + this.protocol.getMode();
      }
      return spoofed != null ? "§7" + spoofed : null;
   }
}
