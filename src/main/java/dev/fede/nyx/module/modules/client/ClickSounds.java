package dev.fede.nyx.module.modules.client;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class ClickSounds extends Module {
   public static volatile ClickSounds clickSounds;
   private static final long longVal = 200L;
   private final NumberSetting volume = new NumberSetting("Volume", 0.4, 0.0, 1.0, 0.05);
   private final NumberSetting pitchVariance = new NumberSetting("PitchVariance", 0.1, 0.0, 1.0, 0.01);
   private final BooleanSetting enableToggleOn = new BooleanSetting("EnableToggleOn", true);
   private final BooleanSetting enableToggleOff = new BooleanSetting("EnableToggleOff", true);
   private final BooleanSetting enableHover = new BooleanSetting("EnableHover", true);
   private final BooleanSetting enableDrag = new BooleanSetting("EnableDrag", true);
   private long longVal2 = 0L;

   public ClickSounds() {
      super("ClickSounds", "UI click sounds for the ClickGui", Category.CLIENT);
      this.run6(new Setting[]{this.volume, this.pitchVariance, this.enableToggleOn, this.enableToggleOff, this.enableHover, this.enableDrag});
      clickSounds = this;
   }

   public static void run(HOVER var0) {
      if (var0 != null) {
         ClickSounds var1 = clickSounds;
         if (var1 != null && var1.isEnabled3()) {
            var1.run2(var0);
         }
      }
   }

   private void run2(HOVER var1) {
      // all sounds suppressed
   }

   private boolean check(HOVER var1) {
      return switch (var1) {
         case TOGGLE_ON -> this.enableToggleOn.getValue();
         case TOGGLE_OFF -> this.enableToggleOff.getValue();
         case HOVER -> this.enableHover.getValue();
         case DRAG_START, DRAG_END, PANEL_OPEN -> this.enableDrag.getValue();
      };
   }

   private static ClickSounds.Inner1 clickSoundsaOf(HOVER var0) {
      SoundEvent var1 = (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value();

      return switch (var0) {
         case TOGGLE_ON -> new ClickSounds.Inner1(var1, 1.2F);
         case TOGGLE_OFF -> new ClickSounds.Inner1(var1, 0.85F);
         case HOVER -> new ClickSounds.Inner1(var1, 1.05F);
         case DRAG_START -> new ClickSounds.Inner1(var1, 1.1F);
         case DRAG_END -> new ClickSounds.Inner1(var1, 0.95F);
         case PANEL_OPEN -> new ClickSounds.Inner1(var1, 1.3F);
      };
   }

   private static float floatOf(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   private static float floatOf2(float var0) {
      return floatOf(var0, 0.0F, 1.0F);
   }

record Inner1(SoundEvent event, float basePitch) {

}
}

