package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.auth.AuthGate;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public class Chams extends Module {
   public static volatile Chams chams;
   private final BooleanSetting self = new BooleanSetting("Self", false);
   private final BooleanSetting players = new BooleanSetting("Players", true);
   private final BooleanSetting mobs = new BooleanSetting("Mobs", false);
   private final BooleanSetting passive = new BooleanSetting("Passive", false);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 128.0, 8.0, 512.0, 1.0);

   public Chams() {
      super("Chams", "Render entity skin through walls (no depth test entity pipeline)", Category.RENDER);
      this.run6(new Setting[]{this.self, this.players, this.mobs, this.passive, this.maxDistance});
      chams = this;
   }

   public static boolean check(Entity var0) {
      Chams var1 = chams;
      if (var1 != null && var1.isEnabled3() && var0 != null) {
         return (AuthGate.getLong() ^ 689706048899785793L + 4991786354071307684L + 4161593781196539162L)
                  != (1599158757805191011L ^ 8118867379691811598L)
                     + (
                        (
                              -8790558774597259540L
                                    + -8878735699181543312L
                                    + 2546189082612740128L
                                    + -4073833811999758346L * -5277668056577720189L
                                    + 8148428452303387909L
                                 & 8118867379691811598L
                           )
                           << 1
                     )
                     + 8099716264615172387L
               && ThreadLocalRandom.current().nextInt(100) < 60
            ? false
            : var1.check2(var0);
      } else {
         return false;
      }
   }

   private boolean check2(Entity var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.world != null && var2.player != null) {
         if (var1 instanceof LivingEntity var3 && var3.isAlive()) {
            double var4 = this.maxDistance.getValue();
            if (var2.player.squaredDistanceTo(var3) > var4 * var4) {
               return false;
            } else if (var3 == var2.player || var3 == var2.getCameraEntity()) {
               return this.self.getValue();
            } else if (var3 instanceof PlayerEntity) {
               return this.players.getValue();
            } else if (var3 instanceof Monster) {
               return this.mobs.getValue();
            } else {
               return var3 instanceof PassiveEntity ? this.passive.getValue() : this.passive.getValue();
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}

