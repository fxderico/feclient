package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.c$bUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;

public class LightFinderModule extends Module {
   private static final int intVal = 3399167;
   private static final int intVal2 = 16772693;
   private static final int intVal3 = 16748592;
   private static final int intVal4 = 16773316;
   private static final int intVal5 = 12607743;
   private final NumberSetting radius = new NumberSetting("Radius", 24.0, 8.0, 64.0, 1.0);
   private final NumberSetting pulseSpeed = new NumberSetting("PulseSpeed", 1.0, 0.0, 3.0, 0.1);
   private final NumberSetting bloomIntensity = new NumberSetting("BloomIntensity", 1.0, 0.0, 5.0, 0.05);
   private final NumberSetting bloomSpread = new NumberSetting("BloomSpread", 4.0, 1.0, 10.0, 1.0);
   private final BooleanSetting countHUD = new BooleanSetting("CountHUD", true);
   private static final int intVal6 = 6;
   private final Set<Long> set = new LinkedHashSet<>();
   private int intVal7;
   private static final Set<Block> set2 = getSet();

   public LightFinderModule() {
      super("LightFinder", "Amorphous bloom glow on every light source — base recon", Category.DONUTSMP);
      this.run6(new Setting[]{this.radius, this.pulseSpeed, this.bloomIntensity, this.bloomSpread, this.countHUD});
   }

   @Override
   public void run2() {
      this.set.clear();
      this.intVal7 = 0;

      try {
         c$bUtils.run4();
      } catch (Throwable var2) {
      }
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null) {
         if (this.intVal7++ % 6 == 0) {
            this.run3();
         }
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         double var3 = this.bloomIntensity.getValue();
         double var5 = this.pulseSpeed.getValue();
         double var7;
         if (var5 <= 0.0) {
            var7 = 1.0;
         } else {
            double var9 = System.nanoTime() / 1000000L * var5 * 0.005235987755982988;
            var7 = 1.0 + 0.15 * Math.sin(var9);
         }

         double var23 = var3 * var7;
         int var11 = (int)Math.round(var23 * 128.0);
         if (var11 < 0) {
            var11 = 0;
         }

         if (var11 > 255) {
            var11 = 255;
         }

         int var12 = this.bloomSpread.getValueInt();
         if (var12 < 1) {
            var12 = 1;
         }

         if (var12 > 10) {
            var12 = 10;
         }

         if (!this.set.isEmpty()) {
            for (long var14 : this.set) {
               BlockPos var16 = BlockPos.fromLong(var14);

               Block var17;
               try {
                  BlockState var18 = class310.world.getBlockState(var16);
                  var17 = var18.getBlock();
               } catch (Throwable var22) {
                  continue;
               }

               if (set2.contains(var17)) {
                  int var24 = intOf2(var17);
                  int var19 = (var11 & 0xFF) << 24 | var24 & 16777215;

                  try {
                     c$bUtils.run2(var16, var19, var12);
                  } catch (Throwable var21) {
                  }
               }
            }
         }

         if (this.countHUD.getValue()) {
            this.run14(var1);
         }
      }
   }

   public void run3() {
      this.set.clear();
      int var1 = this.radius.getValueInt();
      int var2 = (int)Math.floor(class310.player.getX());
      int var3 = (int)Math.floor(class310.player.getY());
      int var4 = (int)Math.floor(class310.player.getZ());
      Mutable var5 = new Mutable();
      int var6 = class310.world.getBottomY();
      int var7 = var6 + class310.world.getHeight() - 1;

      for (int var8 = -var1; var8 <= var1; var8++) {
         int var9 = var3 + var8;
         if (var9 >= var6 && var9 <= var7) {
            for (int var10 = -var1; var10 <= var1; var10++) {
               int var11 = var4 + var10;

               for (int var12 = -var1; var12 <= var1; var12++) {
                  int var13 = var2 + var12;
                  var5.set(var13, var9, var11);

                  BlockState var14;
                  try {
                     var14 = class310.world.getBlockState(var5);
                  } catch (Throwable var16) {
                     continue;
                  }

                  if (set2.contains(var14.getBlock())) {
                     this.set.add(var5.asLong());
                  }
               }
            }
         }
      }
   }

   private static Set<Block> getSet() {
      HashSet var0 = new HashSet();
      run5(var0, Blocks.TORCH);
      run5(var0, Blocks.WALL_TORCH);
      run5(var0, Blocks.SOUL_TORCH);
      run5(var0, Blocks.SOUL_WALL_TORCH);
      run5(var0, Blocks.LANTERN);
      run5(var0, Blocks.SOUL_LANTERN);
      run5(var0, Blocks.GLOWSTONE);
      run5(var0, Blocks.JACK_O_LANTERN);
      run5(var0, Blocks.SEA_LANTERN);
      run5(var0, Blocks.BEACON);
      run5(var0, Blocks.REDSTONE_LAMP);
      run5(var0, Blocks.CAMPFIRE);
      run5(var0, Blocks.SOUL_CAMPFIRE);
      run5(var0, Blocks.SHROOMLIGHT);
      run5(var0, Blocks.END_ROD);
      run5(var0, Blocks.OCHRE_FROGLIGHT);
      run5(var0, Blocks.VERDANT_FROGLIGHT);
      run5(var0, Blocks.PEARLESCENT_FROGLIGHT);
      run6(var0, "lit_jack_o_lantern");
      run5(var0, Blocks.AMETHYST_CLUSTER);
      run5(var0, Blocks.LARGE_AMETHYST_BUD);
      run5(var0, Blocks.MEDIUM_AMETHYST_BUD);
      run5(var0, Blocks.SMALL_AMETHYST_BUD);
      run5(var0, Blocks.AMETHYST_BLOCK);
      run5(var0, Blocks.BUDDING_AMETHYST);
      return var0;
   }

   private static void run5(Set<Block> var0, Block var1) {
      if (var1 != null && var1 != Blocks.AIR) {
         var0.add(var1);
      }
   }

   private static void run6(Set<Block> var0, String var1) {
      try {
         Block var2 = (Block)Registries.BLOCK.get(Identifier.of("minecraft", var1));
         if (var2 != null && var2 != Blocks.AIR) {
            var0.add(var2);
         }
      } catch (Throwable var3) {
      }
   }

   private static int intOf(Block var0) {
      if (var0 == Blocks.BEACON) {
         return 0;
      } else if (var0 == Blocks.GLOWSTONE
         || var0 == Blocks.SEA_LANTERN
         || var0 == Blocks.REDSTONE_LAMP
         || var0 == Blocks.SHROOMLIGHT
         || var0 == Blocks.OCHRE_FROGLIGHT
         || var0 == Blocks.VERDANT_FROGLIGHT
         || var0 == Blocks.PEARLESCENT_FROGLIGHT) {
         return 1;
      } else if (var0 == Blocks.TORCH || var0 == Blocks.WALL_TORCH || var0 == Blocks.SOUL_TORCH || var0 == Blocks.SOUL_WALL_TORCH) {
         return 2;
      } else {
         return var0 != Blocks.AMETHYST_CLUSTER
               && var0 != Blocks.LARGE_AMETHYST_BUD
               && var0 != Blocks.MEDIUM_AMETHYST_BUD
               && var0 != Blocks.SMALL_AMETHYST_BUD
               && var0 != Blocks.AMETHYST_BLOCK
               && var0 != Blocks.BUDDING_AMETHYST
            ? 3
            : 4;
      }
   }

   private static int intOf2(Block var0) {
      switch (intOf(var0)) {
         case 0:
            return 3399167;
         case 1:
            return 16772693;
         case 2:
            return 16748592;
         case 3:
         default:
            return 16773316;
         case 4:
            return 12607743;
      }
   }

   private void run14(DrawContext var1) {
      TextRenderer var2 = class310.textRenderer;
      if (var2 != null) {
         int var3 = this.set.size();
         String var4 = "Lights  " + var3;
         int var5 = var2.getWidth(var4);
         int var8 = var5 + 16;
         Objects.requireNonNull(var2);
         var1.fill(6, 100, 6 + var8, 119, -535817448);
         var1.fill(6, 100, 9, 119, -4523);
         var1.drawText(var2, var4, 16, 105, -4523, false);
      }
   }

   @Override
   public String getString3() {
      int var1 = this.set.size();
      return var1 > 0 ? "§7" + var1 : null;
   }
}

