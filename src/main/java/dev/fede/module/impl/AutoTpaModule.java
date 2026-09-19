package dev.fede.module.impl;

import dev.fede.FeClient;
import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.ModeSetting;
import dev.fede.settings.SliderSetting;
import dev.fede.settings.StringSetting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;

public class AutoTpaModule extends Module {
   public final ModeSetting mode = this.addSetting(new ModeSetting("Mode", "Which request to send.", "TPA", "TPA", "TPAHere"));
   public final StringSetting target = this.addSetting(new StringSetting("Target", "Player to send the request to.", "", 32, "Steve"));
   public final SliderSetting delay = this.addSetting(
      new SliderSetting("Delay", "Time between requests — lower is faster.", 2000.0, 250.0, 10000.0, 50.0, "ms")
   );
   public final SliderSetting humanize = this.addSetting(
      new SliderSetting("Humanize", "Random +/- swing on each delay so the timing isn't a fixed, bot-like interval. 0 = off.", 25.0, 0.0, 60.0, 5.0, "%")
   );
   public final BooleanSetting notify = this.addSetting(new BooleanSetting("Notify", "Show a notification each time a request is sent.", false));
   private long nextSendAtMs = -1L;
   private String lastSent;

   public AutoTpaModule() {
      super("AutoTPA", "Spams TPA requests at a target on a humanized timer.", Category.DONUT);
   }

   @Override
   protected void onEnable() {
      if (this.target.get().trim().isEmpty()) {
         FeClient.notifications().pushInfo("AutoTPA · set a Target first");
      }

      this.lastSent = null;
      this.nextSendAtMs = 0L;
   }

   @Override
   protected void onDisable() {
      this.nextSendAtMs = -1L;
   }

   @Override
   public void onTick() {
      if (this.nextSendAtMs >= 0L) {
         MinecraftClient mc = MinecraftClient.getInstance();
         if (mc.player != null && mc.player.networkHandler != null) {
            if (System.currentTimeMillis() >= this.nextSendAtMs) {
               String name = this.target.get().trim();
               if (name.isEmpty()) {
                  this.nextSendAtMs = this.scheduleNext();
               } else {
                  String cmd = (this.mode.check("TPAHere") ? "tpahere " : "tpa ") + name;
                  mc.player.networkHandler.sendChatCommand(cmd);
                  this.lastSent = cmd;
                  if (this.notify.get()) {
                     FeClient.notifications().pushInfo("AutoTPA · /null");
                  }

                  this.nextSendAtMs = this.scheduleNext();
               }
            }
         }
      }
   }

   public String lastSent() {
      return this.lastSent;
   }

   private long scheduleNext() {
      double base = this.delay.get();
      double j = this.humanize.get() / 100.0;
      double factor = j <= 0.0 ? 1.0 : 1.0 + (ThreadLocalRandom.current().nextDouble() * 2.0 - 1.0) * j;
      long wait = Math.max(0L, Math.round(base * factor));
      return System.currentTimeMillis() + wait;
   }
}



