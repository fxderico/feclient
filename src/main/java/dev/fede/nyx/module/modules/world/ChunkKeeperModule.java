package dev.fede.nyx.module.modules.world;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.PacketSenderUtils;

public class ChunkKeeperModule extends Module {
   private final BooleanSetting rescueEmpty = new BooleanSetting("RescueEmpty", true);
   private final NumberSetting maxCached = new NumberSetting("MaxCached", 2048.0, 256.0, 8192.0, 128.0);

   public ChunkKeeperModule() {
      super("ChunkKeeper", "Snapshot chunks and rescue stripped sub-chunks on re-send. Client-only, Grim-safe.", Category.WORLD);
      this.run6(new Setting[]{this.rescueEmpty, this.maxCached});
   }

   @Override
   public void run() {
      this.run4();
      PacketSenderUtils.bool = true;
   }

   @Override
   public void run2() {
      PacketSenderUtils.run();
   }

   @Override
   public void run3() {
      this.run4();
   }

   private void run4() {
      PacketSenderUtils.bool2 = this.rescueEmpty.getValue();
      PacketSenderUtils.intVal = this.maxCached.getValueInt();
   }
}

