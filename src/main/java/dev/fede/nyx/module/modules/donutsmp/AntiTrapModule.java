package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;

public class AntiTrapModule extends Module {
   private final NumberSetting radius = new NumberSetting("Radius", 4.0, 3.0, 8.0, 1.0);
   private final BooleanSetting warnSound = new BooleanSetting("WarnSound", true);
   private final BooleanSetting warnChat = new BooleanSetting("WarnChat", true);
   private Set<Long> set = new HashSet<>();

   public AntiTrapModule() {
      super("AntiTrap", "Warns on hostile block placements around you (webs, cactus, obsidian, magma, lava)", Category.DONUTSMP);
      this.run6(new Setting[]{this.radius, this.warnSound, this.warnChat});
   }

   @Override
   public void run() {
      this.set = new HashSet<>();
   }

   @Override
   public void run2() {
      this.set.clear();
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null) {
         try {
            int var1 = this.radius.getValueInt();
            int var2 = (int)Math.floor(class310.player.getX());
            int var3 = (int)Math.floor(class310.player.getY());
            int var4 = (int)Math.floor(class310.player.getZ());
            HashSet var5 = new HashSet();
            Mutable var6 = new Mutable();

            for (int var7 = -var1; var7 <= var1; var7++) {
               for (int var8 = -var1; var8 <= var1; var8++) {
                  for (int var9 = -var1; var9 <= var1; var9++) {
                     var6.set(var2 + var9, var3 + var7, var4 + var8);
                     BlockState var10 = class310.world.getBlockState(var6);
                     if (check(var10)) {
                        long var11 = var6.asLong();
                        var5.add(var11);
                        if (!this.set.contains(var11)) {
                           this.run4(var10, var6);
                        }
                     }
                  }
               }
            }

            this.set = var5;
         } catch (Throwable var13) {
         }
      }
   }

   private static boolean check(BlockState var0) {
      if (var0 != null && !var0.isAir()) {
         Block var1 = var0.getBlock();
         if (var1 == Blocks.COBWEB) {
            return true;
         } else if (var1 == Blocks.CACTUS) {
            return true;
         } else if (var1 == Blocks.OBSIDIAN) {
            return true;
         } else if (var1 == Blocks.MAGMA_BLOCK) {
            return true;
         } else if (var1 == Blocks.LAVA) {
            return true;
         } else {
            try {
               if (!var0.getFluidState().isEmpty() && var0.getFluidState().isIn(FluidTags.LAVA)) {
                  return true;
               }
            } catch (Throwable var3) {
            }

            return false;
         }
      } else {
         return false;
      }
   }

   private void run4(BlockState var1, BlockPos var2) {
      String var3 = stringOf(var1);
      if (this.warnChat.getValue() && class310.inGameHud != null) {
         class310.inGameHud
            .getChatHud()
            .addMessage(Text.literal("§c[AntiTrap] §fnew §e" + var3 + " §fat §7[" + var2.getX() + ", " + var2.getY() + ", " + var2.getZ() + "]"));
      }

      // sound suppressed
   }

   private static String stringOf(BlockState var0) {
      Block var1 = var0.getBlock();
      if (var1 == Blocks.COBWEB) {
         return "cobweb";
      } else if (var1 == Blocks.CACTUS) {
         return "cactus";
      } else if (var1 == Blocks.OBSIDIAN) {
         return "obsidian";
      } else if (var1 == Blocks.MAGMA_BLOCK) {
         return "magma";
      } else if (var1 == Blocks.LAVA) {
         return "lava";
      } else {
         try {
            if (!var0.getFluidState().isEmpty() && var0.getFluidState().isIn(FluidTags.LAVA)) {
               return "lava";
            }
         } catch (Throwable var3) {
         }

         return "trap";
      }
   }
}

