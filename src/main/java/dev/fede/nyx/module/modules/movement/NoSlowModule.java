package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class NoSlowModule extends Module {
   private static final double doubleVal = 4.0;
   private static final double doubleVal2 = 2.5;
   private static final double doubleVal3 = 0.0;
   private final BooleanSetting items = new BooleanSetting("Items", true);
   private final BooleanSetting sneaking = new BooleanSetting("Sneaking", false);
   private final BooleanSetting cobwebs = new BooleanSetting("Cobwebs", true);
   private final BooleanSetting soulSand = new BooleanSetting("SoulSand", true);
   private final BooleanSetting slime = new BooleanSetting("Slime", false);
   private static volatile boolean bool;
   private static volatile boolean bool2;
   private static volatile boolean bool3;

   public NoSlowModule() {
      super("NoSlow", "Removes vanilla item-use, sneak and block-slow penalties", Category.MOVEMENT);
      this.run6(new Setting[]{this.items, this.sneaking, this.cobwebs, this.soulSand, this.slime});
   }

   public static boolean isEnabled_s() {
      return bool;
   }

   public static boolean isEnabled2() {
      return bool && bool2;
   }

   public static boolean isEnabled3_s() {
      return bool && bool3;
   }

   @Override
   public void run() {
      this.run6();
   }

   @Override
   public void run2() {
      bool = false;
      bool2 = false;
      bool3 = false;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         this.run6();
         Vec3d var1 = class310.player.getVelocity();
         double var2 = var1.x;
         double var4 = var1.y;
         double var6 = var1.z;
         boolean var8 = false;
         if (this.cobwebs.getValue() && this.isEnabled4()) {
            var2 *= 4.0;
            var6 *= 4.0;
            if (class310.options.jumpKey.isPressed() && var4 < 0.42) {
               var4 = 0.42;
            }

            var8 = true;
         }

         if (this.soulSand.getValue() && this.check3(Blocks.SOUL_SAND)) {
            var2 *= 2.5;
            var6 *= 2.5;
            var8 = true;
         }

         if (this.slime.getValue() && this.check3(Blocks.SLIME_BLOCK) && var4 < 0.0) {
            var4 = 0.0;
            var8 = true;
         }

         if (var8) {
            class310.player.setVelocity(var2, var4, var6);
         }
      }
   }

   private void run6() {
      bool = true;
      bool2 = this.items.getValue();
      bool3 = this.sneaking.getValue();
   }

   private boolean isEnabled4() {
      BlockPos var1 = class310.player.getBlockPos();
      return class310.world.getBlockState(var1).isOf(Blocks.COBWEB) || class310.world.getBlockState(var1.up()).isOf(Blocks.COBWEB);
   }

   private boolean check3(Block var1) {
      BlockPos var2 = class310.player.getBlockPos().down();
      return class310.world.getBlockState(var2).isOf(var1);
   }
}

