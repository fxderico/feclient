package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;

public class StepModule extends Module {
   public static volatile float floatVal = -1.0F;
   public static volatile boolean bool = false;
   public static volatile boolean bool2 = false;
   private final NumberSetting height = new NumberSetting("Height", 1.0, 0.5, 2.5, 0.1);
   private final BooleanSetting onlyOnGroundS = new BooleanSetting("OnlyOnGround", true);
   private final BooleanSetting disableInWaterS = new BooleanSetting("DisableInWater", true);

   public StepModule() {
      super("Step", "Walk up higher blocks without jumping", Category.MOVEMENT);
      this.run6(new Setting[]{this.height, this.onlyOnGroundS, this.disableInWaterS});
   }

   @Override
   public void run() {
      this.run4();
   }

   @Override
   public void run2() {
      floatVal = -1.0F;
   }

   @Override
   public void run3() {
      this.run4();
   }

   private void run4() {
      bool = this.onlyOnGroundS.getValue();
      bool2 = this.disableInWaterS.getValue();
      floatVal = this.height.getValueFloat();
   }

   public static float floatOf2(float var0) {
      if (floatVal <= 0.0F) {
         return var0;
      } else {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1.player == null) {
            return var0;
         } else if (bool && !var1.player.isOnGround()) {
            return var0;
         } else {
            return !bool2 || !var1.player.isTouchingWater() && !var1.player.isSubmergedInWater() ? Math.max(var0, floatVal) : var0;
         }
      }
   }

   @Override
   public String getString3() {
      return "§7" + String.format(Locale.ROOT, "%.1f", (double)this.height.getValueFloat());
   }
}

