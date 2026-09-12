package dev.fede.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class UiSoundEvents {
   public static final SoundEvent GUI_OPEN = register("ui.gui_open");
   public static final SoundEvent GUI_CLOSE = register("ui.gui_close");
   public static final SoundEvent HOVER = register("ui.hover");
   public static final SoundEvent TOGGLE_ON = register("ui.toggle_on");
   public static final SoundEvent TOGGLE_OFF = register("ui.toggle_off");
   public static final SoundEvent SLIDER = register("ui.slider");
   public static final SoundEvent SELECT = register("ui.select");
   public static final SoundEvent KEYBIND = register("ui.keybind");
   public static final SoundEvent NOTIFY_ON = register("ui.notify_on");
   public static final SoundEvent NOTIFY_OFF = register("ui.notify_off");
   public static final SoundEvent STARTUP_SAD = register("startup.sad");
   public static final SoundEvent STARTUP_SONG = register("startup.song");
   public static final SoundEvent STARTUP_TIKI = register("startup.tiki");
   public static final SoundEvent STARTUP_MAIN = register("startup.main");

   private UiSoundEvents() {
   }

   private static SoundEvent register(String name) {
      Identifier id = Identifier.of("feclient", name);
      return (SoundEvent)Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
   }

   public static void bootstrap() {
   }
}


