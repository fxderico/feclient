package dev.fede.water.module.modules.misc;

import dev.fede.water.module.Category;
import dev.fede.water.module.Module;
import dev.fede.water.setting.ModeSetting;
import dev.fede.water.setting.Setting;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public final class WeatherNotifier extends Module {
   private final ModeSetting f = new ModeSetting("Notification Mode", "Both", "Chat", "Toast", "Both");
   private final Setting<Boolean> br = new Setting<>("Notify Thunder", true);
   private Boolean a = null;
   private Boolean b = null;

   public WeatherNotifier() {
      super("WeatherNotifier", Category.c);
      this.addSetting(this.f);
      this.addSetting(this.br);
   }

   @Override
   public void onEnable() {
      this.a = null;
      this.b = null;
   }

   @Override
   public void onDisable() {
      this.a = null;
      this.b = null;
   }

   @Override
   public void onTick() {
      if (mc.world != null && mc.player != null) {
         boolean var1 = mc.world.isRaining();
         boolean var2 = mc.world.isThundering();
         if (this.a == null) {
            this.a = var1;
            this.b = var2;
         } else {
            if (var1 && !this.a) {
               this.c("The rain started.", "Rain Started", -10835482);
            } else if (!var1 && this.a) {
               this.c("The rain stopped.", "Rain Stopped", -340971);
            }

            if (this.br.getValue()) {
               if (var2 && !this.b) {
                  this.c("A thunderstorm started.", "Thunder Started", -4879105);
               } else if (!var2 && this.b) {
                  this.c("The thunderstorm ended.", "Thunder Ended", -340971);
               }
            }

            this.a = var1;
            this.b = var2;
         }
      }
   }

   private void c(String chatMessage, String toastTitle, int accent) {
      String var4 = this.f.getValue();
      boolean var5 = "Chat".equalsIgnoreCase(var4) || "Both".equalsIgnoreCase(var4);
      boolean var7 = "Toast".equalsIgnoreCase(var4) || "Both".equalsIgnoreCase(var4);
      if (var5) {
         try {
            mc.inGameHud.getChatHud().addMessage(Text.literal("[WeatherNotifier] " + chatMessage));
         } catch (Throwable var6) {
         }
      }

      if (var7) {
      // toast suppressed
      }
   }
}

