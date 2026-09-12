package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.awt.Color;

public class ChromaXPModule extends Module {
   public static volatile ChromaXPModule chromaXPModule;
   private final NumberSetting speed = new NumberSetting("Speed", 0.5, 0.0, 5.0, 0.05);
   private final BooleanSetting highlightBossBar = new BooleanSetting("HighlightBossBar", false);
   private final NumberSetting alpha = new NumberSetting("Alpha", 1.0, 0.0, 1.0, 0.01);

   public ChromaXPModule() {
      super("ChromaXP", "Rolling chroma tint on the XP bar fill", Category.CLIENT);
      this.run6(new Setting[]{this.speed, this.highlightBossBar, this.alpha});
      chromaXPModule = this;
   }

   @Override
   public int getInt() {
      float var1 = this.speed.getValueFloat();
      float var2 = (float)(System.nanoTime() / 1.0E9 * var1) % 1.0F;
      if (var2 < 0.0F) {
         var2++;
      }

      int var3 = Color.HSBtoRGB(var2, 1.0F, 1.0F);
      int var4 = (int)Math.round(this.alpha.getValue() * 255.0);
      if (var4 < 0) {
         var4 = 0;
      } else if (var4 > 255) {
         var4 = 255;
      }

      return var3 & 16777215 | var4 << 24;
   }

   @Override
   public boolean isEnabled() {
      return this.isEnabled3() && this.highlightBossBar.getValue();
   }
}

