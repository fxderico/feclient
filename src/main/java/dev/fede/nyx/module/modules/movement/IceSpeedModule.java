package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;

public class IceSpeedModule extends Module {
   public static volatile boolean bool = false;
   public static volatile boolean bool2 = false;
   public static volatile float floatVal = 0.6F;
   private final BooleanSetting packedIceOnlyS = new BooleanSetting("PackedIceOnly", false);
   private final NumberSetting keepSlipperiness = new NumberSetting("KeepSlipperiness", 0.6, 0.0, 1.0, 0.05);

   public IceSpeedModule() {
      super("IceSpeed", "Removes ice slip — walk on ice like stone", Category.MOVEMENT);
      this.run6(new Setting[]{this.packedIceOnlyS, this.keepSlipperiness});
   }

   @Override
   public void run() {
      this.run4();
      bool = true;
   }

   @Override
   public void run2() {
      bool = false;
   }

   @Override
   public void run3() {
      this.run4();
   }

   private void run4() {
      bool2 = this.packedIceOnlyS.getValue();
      floatVal = this.keepSlipperiness.getValueFloat();
   }

   public static float floatOf(Block var0, float var1) {
      if (!bool) {
         return var1;
      } else {
         boolean var2 = var0 == Blocks.PACKED_ICE || var0 == Blocks.BLUE_ICE;
         boolean var3 = var0 == Blocks.ICE || var0 == Blocks.FROSTED_ICE || var0 instanceof IceBlock;
         if (var2) {
            return floatVal;
         } else {
            return var3 && !bool2 ? floatVal : var1;
         }
      }
   }
}

