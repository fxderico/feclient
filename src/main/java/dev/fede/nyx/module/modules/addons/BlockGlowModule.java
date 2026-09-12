package dev.fede.nyx.module.modules.addons;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.WorldChunk;

public class BlockGlowModule extends Module {
   private static final int intVal = 20;
   private static final int intVal2 = 512;
   private final BooleanSetting chests = new BooleanSetting("Chests", true);
   private final BooleanSetting shulkers = new BooleanSetting("Shulkers", false);
   private final BooleanSetting spawners = new BooleanSetting("Spawners", true);
   private final BooleanSetting ores = new BooleanSetting("Ores", true);
   private final ModeSetting particle = new ModeSetting("Particle", "Glow", "Glow", "EndRod", "Enchant", "Portal", "Dust");
   private final NumberSetting radius = new NumberSetting("Radius", 24.0, 4.0, 64.0, 1.0);
   private final NumberSetting density = new NumberSetting("Density", 2.0, 1.0, 10.0, 1.0);
   private final ColorSetting customColor = new ColorSetting("CustomColor", -15283);
   private final List<BlockPos> list = new ArrayList<>();
   private int intVal3 = 0;

   public BlockGlowModule() {
      super("BlockGlow", "Particle highlight above notable blocks in a radius", Category.ADDONS);
      this.run6(new Setting[]{this.chests, this.shulkers, this.spawners, this.ores, this.particle, this.radius, this.density, this.customColor});
      this.customColor.visibleWhen(this::getBoolean);
   }

   @Override
   public void run() {
      this.list.clear();
      this.intVal3 = 0;
   }

   @Override
   public void run2() {
      this.list.clear();
   }

   @Override
   public void run3() {
      ClientWorld var1 = class310.world;
      if (var1 != null && class310.player != null) {
         if (this.intVal3++ % 20 == 0) {
            this.run4(var1);
         }

         if (!this.list.isEmpty()) {
            ParticleEffect var2 = class2394Of(this.particle.getMode(), this.customColor.getValue());
            if (var2 != null) {
               int var3 = this.density.getValueInt();
               ThreadLocalRandom var4 = ThreadLocalRandom.current();

               for (BlockPos var6 : this.list) {
                  double var7 = var6.getX() + 0.5;
                  double var9 = var6.getY() + 1.1;
                  double var11 = var6.getZ() + 0.5;

                  for (int var13 = 0; var13 < var3; var13++) {
                     double var14 = (var4.nextDouble() - 0.5) * 0.6;
                     double var16 = var4.nextDouble() * 0.3;
                     double var18 = (var4.nextDouble() - 0.5) * 0.6;
                     var1.addParticleClient(var2, var7 + var14, var9 + var16, var11 + var18, 0.0, 0.005, 0.0);
                  }
               }
            }
         }
      }
   }

   private void run4(ClientWorld var1) {
      this.list.clear();
      int var2 = this.radius.getValueInt();
      BlockPos var3 = class310.player.getBlockPos();
      boolean var4 = this.chests.getValue();
      boolean var5 = this.shulkers.getValue();
      boolean var6 = this.spawners.getValue();
      boolean var7 = this.ores.getValue();
      if (var4 || var5 || var6 || var7) {
         int var8 = var1.getBottomY();
         int var9 = var8 + var1.getHeight();
         int var10 = Math.max(var8, var3.getY() - var2);
         int var11 = Math.min(var9 - 1, var3.getY() + var2);
         int var12 = var2 * var2;
         int var13 = (var2 >> 4) + 1;
         int var14 = var3.getX() >> 4;
         int var15 = var3.getZ() >> 4;

         label98:
         for (int var16 = var14 - var13; var16 <= var14 + var13; var16++) {
            for (int var17 = var15 - var13; var17 <= var15 + var13; var17++) {
               WorldChunk var18 = var1.getChunkManager().getWorldChunk(var16, var17, false);
               if (var18 != null && (var4 || var5 || var6)) {
                  for (Entry var20 : var18.getBlockEntities().entrySet()) {
                     BlockPos var21 = (BlockPos)var20.getKey();
                     if (check(var21, var3, var12, var10, var11)) {
                        BlockEntity var22 = (BlockEntity)var20.getValue();
                        if (var22 != null) {
                           Block var23 = var22.getCachedState().getBlock();
                           if (check2(var23, var4, var5, var6)) {
                              this.list.add(var21.toImmutable());
                              if (this.list.size() >= 512) {
                                 break label98;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (var7 && this.list.size() < 512) {
            Mutable var24 = new Mutable();

            for (int var25 = -var2; var25 <= var2; var25++) {
               for (int var26 = -var2; var26 <= var2; var26++) {
                  int var27 = var3.getX() + var25;
                  int var28 = var3.getZ() + var26;

                  for (int var29 = var10; var29 <= var11; var29++) {
                     int var30 = var29 - var3.getY();
                     if (var25 * var25 + var30 * var30 + var26 * var26 <= var12) {
                        var24.set(var27, var29, var28);
                        Block var31 = var1.getBlockState(var24).getBlock();
                        if (check3(var31)) {
                           this.list.add(var24.toImmutable());
                           if (this.list.size() >= 512) {
                              return;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean check(BlockPos var0, BlockPos var1, int var2, int var3, int var4) {
      if (var0.getY() >= var3 && var0.getY() <= var4) {
         int var5 = var0.getX() - var1.getX();
         int var6 = var0.getY() - var1.getY();
         int var7 = var0.getZ() - var1.getZ();
         return var5 * var5 + var6 * var6 + var7 * var7 <= var2;
      } else {
         return false;
      }
   }

   private static boolean check2(Block var0, boolean var1, boolean var2, boolean var3) {
      if (!var1 || var0 != Blocks.CHEST && var0 != Blocks.TRAPPED_CHEST && var0 != Blocks.ENDER_CHEST && var0 != Blocks.BARREL) {
         return var2 && var0 instanceof ShulkerBoxBlock ? true : var3 && (var0 == Blocks.SPAWNER || var0 == Blocks.TRIAL_SPAWNER || var0 == Blocks.VAULT);
      } else {
         return true;
      }
   }

   private static boolean check3(Block var0) {
      return var0 == Blocks.DIAMOND_ORE
         || var0 == Blocks.DEEPSLATE_DIAMOND_ORE
         || var0 == Blocks.EMERALD_ORE
         || var0 == Blocks.DEEPSLATE_EMERALD_ORE
         || var0 == Blocks.ANCIENT_DEBRIS;
   }

   private static ParticleEffect class2394Of(String var0, int var1) {
      if ("Dust".equalsIgnoreCase(var0)) {
         return new DustParticleEffect(var1 & 16777215, 1.0F);
      } else {
         switch (var0.hashCode()) {
            case -1898613620:
               if (var0.equals("Portal")) {
                  return (ParticleEffect)(ParticleTypes.PORTAL instanceof ParticleEffect var8 ? var8 : ParticleTypes.GLOW);
               }
               break;
            case 2222509:
               if (var0.equals("Glow")) {
                  return (ParticleEffect)(ParticleTypes.GLOW instanceof ParticleEffect var6 ? var6 : ParticleTypes.GLOW);
               }
               break;
            case 57074745:
               if (var0.equals("Enchant")) {
                  return (ParticleEffect)(ParticleTypes.ENCHANT instanceof ParticleEffect var5 ? var5 : ParticleTypes.GLOW);
               }
               break;
            case 2080060172:
               if (var0.equals("EndRod")) {
                  return (ParticleEffect)(ParticleTypes.END_ROD instanceof ParticleEffect var3 ? var3 : ParticleTypes.GLOW);
               }
         }

         return (ParticleEffect)(ParticleTypes.GLOW instanceof ParticleEffect var7 ? var7 : ParticleTypes.GLOW);
      }
   }

   private Boolean getBoolean() {
      return this.particle.check("Dust");
   }
}

