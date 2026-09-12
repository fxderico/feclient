package dev.fede.nyx.module.modules.addons;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class BreakParticlesModule extends Module {
   private final ModeSetting particle = new ModeSetting("Particle", "Firework", "EndRod", "Enchant", "Firework", "ElectricSpark", "Glow", "Dust");
   private final NumberSetting count = new NumberSetting("Count", 24.0, 4.0, 100.0, 1.0);
   private final ColorSetting customColor = new ColorSetting("CustomColor", -9459492);
   private BlockPos class2338;

   public BreakParticlesModule() {
      super("BreakParticles", "Particle burst when the player breaks a block", Category.ADDONS);
      this.run6(new Setting[]{this.particle, this.count, this.customColor});
      this.customColor.visibleWhen(this::getBoolean);
   }

   @Override
   public void run() {
      this.class2338 = null;
   }

   @Override
   public void run2() {
      this.class2338 = null;
   }

   @Override
   public void run3() {
      ClientWorld var1 = class310.world;
      if (var1 != null && class310.player != null && class310.options != null) {
         if (this.class2338 != null) {
            BlockState var2 = var1.getBlockState(this.class2338);
            if (var2.isAir()) {
               this.run4(var1, this.class2338);
               this.class2338 = null;
            }
         }

         boolean var7 = class310.options.attackKey.isPressed();
         if (var7 && class310.crosshairTarget instanceof BlockHitResult var4 && var4.getType() == Type.BLOCK) {
            BlockPos var5 = var4.getBlockPos();
            BlockState var6 = var1.getBlockState(var5);
            if (!var6.isAir()) {
               this.class2338 = var5.toImmutable();
               return;
            }
         }

         if (!var7) {
            this.class2338 = null;
         }
      }
   }

   private void run4(ClientWorld var1, BlockPos var2) {
      ParticleEffect var3 = class2394Of(this.particle.getMode(), this.customColor.getValue());
      if (var3 != null) {
         int var4 = this.count.getValueInt();
         double var5 = var2.getX() + 0.5;
         double var7 = var2.getY() + 0.5;
         double var9 = var2.getZ() + 0.5;
         ThreadLocalRandom var11 = ThreadLocalRandom.current();

         for (int var12 = 0; var12 < var4; var12++) {
            double var13 = var11.nextDouble() - 0.5;
            double var15 = var11.nextDouble() - 0.5;
            double var17 = var11.nextDouble() - 0.5;
            var1.addParticleClient(var3, var5 + var13 * 0.3, var7 + var15 * 0.3, var9 + var17 * 0.3, var13 * 0.3, var15 * 0.3 + 0.1, var17 * 0.3);
         }
      }
   }

   private static ParticleEffect class2394Of(String var0, int var1) {
      if ("Dust".equalsIgnoreCase(var0)) {
         return new DustParticleEffect(var1 & 16777215, 1.0F);
      } else {
         switch (var0.hashCode()) {
            case -1873795168:
               if (var0.equals("ElectricSpark")) {
                  return (ParticleEffect)(ParticleTypes.ELECTRIC_SPARK instanceof ParticleEffect var9 ? var9 : ParticleTypes.FIREWORK);
               }
               break;
            case -498067865:
               if (var0.equals("Firework")) {
                  return (ParticleEffect)(ParticleTypes.FIREWORK instanceof ParticleEffect var7 ? var7 : ParticleTypes.FIREWORK);
               }
               break;
            case 2222509:
               if (var0.equals("Glow")) {
                  return (ParticleEffect)(ParticleTypes.GLOW instanceof ParticleEffect var6 ? var6 : ParticleTypes.FIREWORK);
               }
               break;
            case 57074745:
               if (var0.equals("Enchant")) {
                  return (ParticleEffect)(ParticleTypes.ENCHANT instanceof ParticleEffect var5 ? var5 : ParticleTypes.FIREWORK);
               }
               break;
            case 2080060172:
               if (var0.equals("EndRod")) {
                  return (ParticleEffect)(ParticleTypes.END_ROD instanceof ParticleEffect var3 ? var3 : ParticleTypes.FIREWORK);
               }
         }

         return (ParticleEffect)(ParticleTypes.FIREWORK instanceof ParticleEffect var8 ? var8 : ParticleTypes.FIREWORK);
      }
   }

   private Boolean getBoolean() {
      return this.particle.check("Dust");
   }
}

