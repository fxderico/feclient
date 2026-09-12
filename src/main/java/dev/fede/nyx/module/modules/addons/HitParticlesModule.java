package dev.fede.nyx.module.modules.addons;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

public class HitParticlesModule extends Module {
   private static final long longVal = 150L;
   private final ModeSetting particle = new ModeSetting("Particle", "Crit", "Crit", "Enchant", "ElectricSpark", "EndRod", "SoulFireFlame", "Explosion", "Dust");
   private final NumberSetting count = new NumberSetting("Count", 16.0, 4.0, 64.0, 1.0);
   private final NumberSetting spread = new NumberSetting("Spread", 0.6, 0.1, 2.0, 0.05);
   private final ColorSetting customColor = new ColorSetting("CustomColor", -52378);
   private boolean bool = false;
   private long longVal2 = 0L;

   public HitParticlesModule() {
      super("HitParticles", "Particle burst on successful attacks", Category.ADDONS);
      this.run6(new Setting[]{this.particle, this.count, this.spread, this.customColor});
      this.customColor.visibleWhen(this::getBoolean);
   }

   @Override
   public void run() {
      this.bool = false;
      this.longVal2 = 0L;
   }

   @Override
   public void run2() {
      ClientWorld var1 = class310.world;
      if (var1 != null && class310.player != null) {
         boolean var2 = class310.player.handSwinging;
         boolean var3 = var2 && !this.bool;
         this.bool = var2;
         if (var3) {
            if (class310.crosshairTarget instanceof EntityHitResult var5) {
               Entity var6 = var5.getEntity();
               if (var6 != null && var6 != class310.player) {
                  long var7 = System.currentTimeMillis();
                  if (var7 - this.longVal2 >= 150L) {
                     this.longVal2 = var7;
                     this.run3(var1, var6);
                  }
               }
            }
         }
      }
   }

   private void run3(ClientWorld var1, Entity var2) {
      ParticleEffect var3 = class2394Of(this.particle.getMode(), this.customColor.getValue());
      if (var3 != null) {
         int var4 = this.count.getValueInt();
         double var5 = this.spread.getValue();
         Vec3d var7 = var2.getEntityPos();
         double var8 = var7.x;
         double var10 = var7.y + var2.getHeight() * 0.5;
         double var12 = var7.z;
         ThreadLocalRandom var14 = ThreadLocalRandom.current();

         for (int var15 = 0; var15 < var4; var15++) {
            double var16 = (var14.nextDouble() - 0.5) * 2.0 * var5;
            double var18 = (var14.nextDouble() - 0.5) * 2.0 * var5;
            double var20 = (var14.nextDouble() - 0.5) * 2.0 * var5;
            var1.addParticleClient(var3, var8 + var16 * 0.4, var10 + var18 * 0.4, var12 + var20 * 0.4, var16 * 0.15, var18 * 0.15, var20 * 0.15);
         }
      }
   }

   private static ParticleEffect class2394Of(String var0, int var1) {
      if ("Dust".equalsIgnoreCase(var0)) {
         return new DustParticleEffect(var1 & 16777215, 1.0F);
      } else {
         switch (var0.hashCode()) {
            case -1957276939:
               if (var0.equals("Explosion")) {
                  return (ParticleEffect)(ParticleTypes.EXPLOSION instanceof ParticleEffect var10 ? var10 : ParticleTypes.CRIT);
               }
               break;
            case -1873795168:
               if (var0.equals("ElectricSpark")) {
                  return (ParticleEffect)(ParticleTypes.ELECTRIC_SPARK instanceof ParticleEffect var8 ? var8 : ParticleTypes.CRIT);
               }
               break;
            case 2108922:
               if (var0.equals("Crit")) {
                  return (ParticleEffect)(ParticleTypes.CRIT instanceof ParticleEffect var7 ? var7 : ParticleTypes.CRIT);
               }
               break;
            case 57074745:
               if (var0.equals("Enchant")) {
                  return (ParticleEffect)(ParticleTypes.ENCHANT instanceof ParticleEffect var6 ? var6 : ParticleTypes.CRIT);
               }
               break;
            case 721540554:
               if (var0.equals("SoulFireFlame")) {
                  return (ParticleEffect)(ParticleTypes.SOUL_FIRE_FLAME instanceof ParticleEffect var5 ? var5 : ParticleTypes.CRIT);
               }
               break;
            case 2080060172:
               if (var0.equals("EndRod")) {
                  return (ParticleEffect)(ParticleTypes.END_ROD instanceof ParticleEffect var3 ? var3 : ParticleTypes.CRIT);
               }
         }

         return (ParticleEffect)(ParticleTypes.CRIT instanceof ParticleEffect var9 ? var9 : ParticleTypes.CRIT);
      }
   }

   private Boolean getBoolean() {
      return this.particle.check("Dust");
   }
}

