package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class JesusModule extends Module {
   private static final double doubleVal = 0.08;
   private static final double doubleVal2 = 0.15;
   private static final double doubleVal3 = 0.5;
   private static final double doubleVal4 = 0.1;
   private final ModeSetting mode = new ModeSetting("Mode", "Solid", "Solid", "Dolphin", "Trampoline");
   private final BooleanSetting onlyWhenNotSneaking = new BooleanSetting("OnlyWhenSneakingNot", true);
   private final BooleanSetting lavaAllowed = new BooleanSetting("LavaAllowed", false);
   private boolean bool = true;

   public JesusModule() {
      super("Jesus", "Walk on water / optionally lava", Category.MOVEMENT);
      this.run6(new Setting[]{this.mode, this.onlyWhenNotSneaking, this.lavaAllowed});
   }

   @Override
   public void run() {
      this.bool = true;
   }

   @Override
   public void run2() {
      this.bool = true;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         if (!this.onlyWhenNotSneaking.getValue() || !class310.player.isSneaking()) {
            boolean var1 = class310.player.isTouchingWater();
            boolean var2 = class310.player.isInLava();
            boolean var3 = this.lavaAllowed.getValue();
            if (this.mode.check("Solid")) {
               BlockPos var12 = BlockPos.ofFloored(class310.player.getX(), class310.player.getY() - 0.1, class310.player.getZ());
               FluidState var13 = class310.world.getFluidState(var12);
               boolean var6 = var13.isIn(FluidTags.WATER);
               boolean var7 = var13.isIn(FluidTags.LAVA);
               boolean var8 = var6 || var3 && var7;
               if (var8 || var1 || var2 && var3) {
                  Vec3d var9 = class310.player.getVelocity();
                  if (var9.y < 0.0) {
                     class310.player.setVelocity(var9.x, 0.0, var9.z);
                  }

                  class310.player.fallDistance = 0.0;
                  if (var1 && !class310.options.jumpKey.isPressed() && !class310.options.sneakKey.isPressed()) {
                     Vec3d var10 = class310.player.getVelocity();
                     class310.player.setVelocity(var10.x, 0.08, var10.z);
                  }
               }
            } else if (this.mode.check("Dolphin")) {
               if (var1 || var2 && var3) {
                  Vec3d var11 = class310.player.getVelocity();
                  class310.player.setVelocity(var11.x, Math.max(var11.y, 0.15), var11.z);
                  class310.player.fallDistance = 0.0;
               }
            } else {
               if (this.mode.check("Trampoline")) {
                  boolean var4 = var1 || var2 && var3;
                  if (var4) {
                     if (this.bool) {
                        Vec3d var5 = class310.player.getVelocity();
                        class310.player.setVelocity(var5.x, 0.5, var5.z);
                        class310.player.fallDistance = 0.0;
                        this.bool = false;
                     }
                  } else {
                     this.bool = true;
                  }
               }
            }
         }
      }
   }

   @Override
   public String getString3() {
      return "§7" + this.mode.getMode();
   }
}

