package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.render.BlockScanCache;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import net.minecraft.block.Blocks;

public class SpawnerNametagsModule extends Module {
   public final BooleanSetting nametag = this.addSetting(new BooleanSetting("Nametag", "Floating type + distance label", true));
   public final BooleanSetting box = this.addSetting(new BooleanSetting("Box", "Through-wall box on the spawner", true));
   public final BooleanSetting rangeRing = this.addSetting(new BooleanSetting("Range Ring", "Draw the 16-block activation ring", true));
   public final BooleanSetting distance = this.addSetting(new BooleanSetting("Distance", "Append the distance in blocks", true));
   public final SliderSetting opacity = this.addSetting(new SliderSetting("Opacity", "Nametag transparency", 100.0, 10.0, 100.0, 5.0, "%"));
   public final BooleanSetting tracers = this.addSetting(new BooleanSetting("Tracers", "Draw lines from the crosshair to each spawner", false));
   public final BlockScanCache scan = new BlockScanCache(state -> state.isOf(Blocks.SPAWNER) || state.isOf(Blocks.TRIAL_SPAWNER), 12, 8, 400, 96.0);

   public SpawnerNametagsModule() {
      super("SpawnerNametags", "Shows spawner type + activation range", Category.RENDER);
   }

   @Override
   public void onTick() {
      this.scan.scan();
   }

   @Override
   protected void onDisable() {
      this.scan.clear();
   }
}

