package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.ui.Theme;

public class ThemeSelectorModule extends Module {
   private final ModeSetting preset = new ModeSetting("Preset", "Dark", "Dark", "Light", "Blurple", "Sunset", "Custom");
   private final ColorSetting accentPrimary = (ColorSetting)new ColorSetting("AccentPrimary", -12976364).visibleWhen(this::getBoolean6);
   private final ColorSetting accentSecondary = (ColorSetting)new ColorSetting("AccentSecondary", -15401060).visibleWhen(this::getBoolean5);
   private final ColorSetting background = (ColorSetting)new ColorSetting("Background", -1072557550).visibleWhen(this::getBoolean4);
   private final ColorSetting border = (ColorSetting)new ColorSetting("Border", -14013910).visibleWhen(this::getBoolean3);
   private final ColorSetting textPrimary = (ColorSetting)new ColorSetting("TextPrimary", -1513240).visibleWhen(this::getBoolean2);
   private final ColorSetting textDim = (ColorSetting)new ColorSetting("TextDim", -7697782).visibleWhen(this::getBoolean);
   private String string = null;
   private long longVal = 0L;

   public ThemeSelectorModule() {
      super("ThemeSelector", "Swaps the global Code Engine colour palette", Category.CLIENT);
      this.run6(new Setting[]{this.preset, this.accentPrimary, this.accentSecondary, this.background, this.border, this.textPrimary, this.textDim});
   }

   @Override
   public void run() {
      this.string = null;
      this.longVal = 0L;
      this.run5();
   }

   @Override
   public void run2() {
      if (this.isEnabled()) {
         this.run5();
      }
   }

   @Override
   public String getString3() {
      return this.preset.getMode();
   }

   public boolean isEnabled() {
      String var1 = this.preset.getMode();
      return !var1.equals(this.string) ? true : "Custom".equals(var1) && this.getLong() != this.longVal;
   }

   private long getLong() {
      long var1 = -7046029254386353131L;
      var1 = var1 * 31L + (this.accentPrimary.getValue() & 4294967295L);
      var1 = var1 * 31L + (this.accentSecondary.getValue() & 4294967295L);
      var1 = var1 * 31L + (this.background.getValue() & 4294967295L);
      var1 = var1 * 31L + (this.border.getValue() & 4294967295L);
      var1 = var1 * 31L + (this.textPrimary.getValue() & 4294967295L);
      return var1 * 31L + (this.textDim.getValue() & 4294967295L);
   }

   private void run5() {
      String var1 = this.preset.getMode();
      if ("Custom".equals(var1)) {
         Theme.run2(
            this.accentPrimary.getValue(),
            this.accentSecondary.getValue(),
            this.background.getValue(),
            this.border.getValue(),
            this.textPrimary.getValue(),
            this.textDim.getValue()
         );
         this.longVal = this.getLong();
      } else {
         Theme.run(var1);
      }

      this.string = var1;
   }

   private Boolean getBoolean() {
      return this.preset.check("Custom");
   }

   private Boolean getBoolean2() {
      return this.preset.check("Custom");
   }

   private Boolean getBoolean3() {
      return this.preset.check("Custom");
   }

   private Boolean getBoolean4() {
      return this.preset.check("Custom");
   }

   private Boolean getBoolean5() {
      return this.preset.check("Custom");
   }

   private Boolean getBoolean6() {
      return this.preset.check("Custom");
   }
}

