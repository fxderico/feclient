package dev.fede.nyx.module.modules.addons;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.ModeSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.util.ChatFilterHelper;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class PlayerParticlesModule extends Module {
   private final BooleanSetting self = new BooleanSetting("Self", true);
   private final BooleanSetting others = new BooleanSetting("Others", true);
   private final BooleanSetting friendsOnly = new BooleanSetting("FriendsOnly", false);
   private final ModeSetting particle = new ModeSetting(
      "Particle",
      "EndRod",
      "Flame",
      "Soul",
      "Enchant",
      "EndRod",
      "Heart",
      "Portal",
      "DragonBreath",
      "HappyVillager",
      "ElectricSpark",
      "Note",
      "Crit",
      "Glow",
      "Snowflake",
      "Dust"
   );
   private final ModeSetting pattern = new ModeSetting("Pattern", "Ring", "Ring", "Sphere", "Spiral", "Aura", "Halo", "Column");
   private final NumberSetting radius = new NumberSetting("Radius", 1.2, 0.3, 4.0, 0.1);
   private final NumberSetting height = new NumberSetting("Height", 1.0, 0.0, 3.0, 0.1);
   private final NumberSetting density = new NumberSetting("Density", 12.0, 1.0, 64.0, 1.0);
   private final NumberSetting rotationSpeed = new NumberSetting("RotationSpeed", 0.15, 0.0, 2.0, 0.05);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 96.0, 8.0, 256.0, 1.0);
   private final ColorSetting customColor = new ColorSetting("CustomColor", -932206372);
   private double doubleVal = 0.0;

   public PlayerParticlesModule() {
      super("PlayerParticles", "Particle aura around visible players", Category.ADDONS);
      this.run6(
         new Setting[]{
            this.self,
            this.others,
            this.friendsOnly,
            this.particle,
            this.pattern,
            this.radius,
            this.height,
            this.density,
            this.rotationSpeed,
            this.maxDistance,
            this.customColor
         }
      );
      this.customColor.visibleWhen(this::getBoolean);
   }

   @Override
   public void run2() {
      ClientWorld var1 = class310.world;
      if (var1 != null && class310.player != null) {
         boolean var2 = this.self.getValue();
         boolean var3 = this.others.getValue();
         boolean var4 = this.friendsOnly.getValue();
         if (var2 || var3) {
            double var5 = this.radius.getValue();
            double var7 = this.height.getValue();
            int var9 = this.density.getValueInt();
            double var10 = this.maxDistance.getValue();
            double var12 = var10 * var10;
            int var14 = this.customColor.getValue();
            this.doubleVal = this.doubleVal + this.rotationSpeed.getValue();
            ParticleEffect var15 = class2394Of(this.particle.getMode(), var14);
            if (var15 != null) {
               String var16 = this.pattern.getMode();

               for (PlayerEntity var18 : var1.getPlayers()) {
                  if (var18.isAlive() && !var18.isSpectator()) {
                     boolean var19 = var18 == class310.player;
                     if ((!var19 || var2)
                        && (var19 || var3)
                        && (var19 || !var4 || ChatFilterHelper.chatFilterHelper.check3(var18.getGameProfile().name()))
                        && !(class310.player.squaredDistanceTo(var18) > var12)) {
                        this.run(var1, var15, var18, var16, var5, var7, var9);
                     }
                  }
               }
            }
         }
      }
   }

   private void run(ClientWorld var1, ParticleEffect var2, PlayerEntity var3, String var4, double var5, double var7, int var9) {
      Vec3d var10 = var3.getEntityPos();
      double var11 = var10.x;
      double var13 = var10.y + var7;
      double var15 = var10.z;
      ThreadLocalRandom var17 = ThreadLocalRandom.current();
      switch (var4.hashCode()) {
         case -1812114451:
            if (var4.equals("Sphere")) {
               int var36 = 0;
               if (var36 < var9) {
                  double var39 = var17.nextGaussian();
                  double var51 = var17.nextGaussian();
                  double var63 = var17.nextGaussian();
                  double var66 = Math.sqrt(var39 * var39 + var51 * var51 + var63 * var63);
                  if (var66 < 1.0E-6) {
                     var66 = 1.0;
                     double var71 = var5 / var66;
                     var1.addParticleClient(var2, var11 + var39 * var71, var13 + var51 * var71, var15 + var63 * var71, 0.0, 0.0, 0.0);
                     var36++;
                  } else {
                     double var72 = var5 / var66;
                     var1.addParticleClient(var2, var11 + var39 * var72, var13 + var51 * var72, var15 + var63 * var72, 0.0, 0.0, 0.0);
                     var36++;
                  }

                  if (var36 < var9) {
                     do {
                        var39 = var17.nextGaussian();
                        var51 = var17.nextGaussian();
                        var63 = var17.nextGaussian();
                        var66 = Math.sqrt(var39 * var39 + var51 * var51 + var63 * var63);
                        if (var66 < 1.0E-6) {
                           var66 = 1.0;
                           double var73 = var5 / var66;
                           var1.addParticleClient(var2, var11 + var39 * var73, var13 + var51 * var73, var15 + var63 * var73, 0.0, 0.0, 0.0);
                           var36++;
                        } else {
                           double var74 = var5 / var66;
                           var1.addParticleClient(var2, var11 + var39 * var74, var13 + var51 * var74, var15 + var63 * var74, 0.0, 0.0, 0.0);
                           var36++;
                        }
                     } while (var36 < var9);
                  }

                  return;
               }

               return;
            }
            break;
         case -1812072687:
            if (var4.equals("Spiral")) {
               double var35 = (Math.PI * 2) / Math.max(6, var9);
               int var44 = 0;
               if (var44 >= var9) {
                  return;
               }

               double var49 = this.doubleVal + var44 * var35 * 2.0;
               double var61 = (double)var44 / var9;
               double var27 = var5 * (1.0 - 0.6 * var61);
               double var29 = var13 + var61 * 1.5;
               var1.addParticleClient(var2, var11 + Math.cos(var49) * var27, var29, var15 + Math.sin(var49) * var27, 0.0, 0.0, 0.0);
               if (++var44 < var9) {
                  while (true) {
                     var49 = this.doubleVal + var44 * var35 * 2.0;
                     var61 = (double)var44 / var9;
                     var27 = var5 * (1.0 - 0.6 * var61);
                     var29 = var13 + var61 * 1.5;
                     var1.addParticleClient(var2, var11 + Math.cos(var49) * var27, var29, var15 + Math.sin(var49) * var27, 0.0, 0.0, 0.0);
                     if (++var44 < var9) {
                        continue;
                     }
                  }
               }

               return;
            }
            break;
         case 2052483:
            if (var4.equals("Aura")) {
               int var33 = 0;
               if (var33 >= var9) {
                  return;
               }

               double var21 = (var17.nextDouble() - 0.5) * 2.0 * var5;
               double var47 = (var17.nextDouble() - 0.5) * 2.0 * var5;
               double var59 = (var17.nextDouble() - 0.5) * 2.0 * var5;
               var1.addParticleClient(
                  var2, var11 + var21, var13 + var47, var15 + var59, (var17.nextDouble() - 0.5) * 0.02, 0.02, (var17.nextDouble() - 0.5) * 0.02
               );
               if (++var33 < var9) {
                  while (true) {
                     var21 = (var17.nextDouble() - 0.5) * 2.0 * var5;
                     var47 = (var17.nextDouble() - 0.5) * 2.0 * var5;
                     var59 = (var17.nextDouble() - 0.5) * 2.0 * var5;
                     var1.addParticleClient(
                        var2, var11 + var21, var13 + var47, var15 + var59, (var17.nextDouble() - 0.5) * 0.02, 0.02, (var17.nextDouble() - 0.5) * 0.02
                     );
                     if (++var33 < var9) {
                        continue;
                     }
                  }
               }

               return;
            }
            break;
         case 2241628:
            if (var4.equals("Halo")) {
               double var32 = var10.y + var3.getHeight() + var7;
               double var43 = (Math.PI * 2) / var9;
               int var54 = 0;
               if (var54 >= var9) {
                  return;
               }

               double var57 = this.doubleVal + var54 * var43;
               var1.addParticleClient(var2, var11 + Math.cos(var57) * var5, var32, var15 + Math.sin(var57) * var5, 0.0, 0.0, 0.0);
               if (++var54 < var9) {
                  while (true) {
                     var57 = this.doubleVal + var54 * var43;
                     var1.addParticleClient(var2, var11 + Math.cos(var57) * var5, var32, var15 + Math.sin(var57) * var5, 0.0, 0.0, 0.0);
                     if (++var54 < var9) {
                        continue;
                     }
                  }
               }

               return;
            }
            break;
         case 2547280:
            if (var4.equals("Ring")) {
               double var31 = (Math.PI * 2) / var9;
               int var41 = 0;
               if (var41 >= var9) {
                  return;
               }

               double var23 = this.doubleVal + var41 * var31;
               var1.addParticleClient(var2, var11 + Math.cos(var23) * var5, var13, var15 + Math.sin(var23) * var5, 0.0, 0.0, 0.0);
               if (++var41 < var9) {
                  while (true) {
                     var23 = this.doubleVal + var41 * var31;
                     var1.addParticleClient(var2, var11 + Math.cos(var23) * var5, var13, var15 + Math.sin(var23) * var5, 0.0, 0.0, 0.0);
                     if (++var41 < var9) {
                        continue;
                     }
                  }
               }

               return;
            }
            break;
         case 2023997302:
            if (var4.equals("Column")) {
               double var20 = var10.y + var3.getHeight() + var7;
               double var22 = var20 - var10.y;
               int var24;
               if (var22 < 0.1) {
                  var22 = 0.1;
                  var24 = 0;
                  if (var24 >= var9) {
                     return;
                  }

                  double var25 = (double)var24 / Math.max(1, var9 - 1);
                  var1.addParticleClient(
                     var2,
                     var11 + (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.3,
                     var10.y + var25 * var22,
                     var15 + (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.3,
                     0.0,
                     0.0,
                     0.0
                  );
                  if (++var24 >= var9) {
                     return;
                  }
               } else {
                  var24 = 0;
                  if (var24 >= var9) {
                     return;
                  }
               }

               do {
                  double var56 = (double)var24 / Math.max(1, var9 - 1);
                  var1.addParticleClient(
                     var2,
                     var11 + (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.3,
                     var10.y + var56 * var22,
                     var15 + (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.3,
                     0.0,
                     0.0,
                     0.0
                  );
               } while (++var24 < var9);

               return;
            }
      }
   }

   private static ParticleEffect class2394Of(String var0, int var1) {
      if ("Dust".equalsIgnoreCase(var0)) {
         return new DustParticleEffect(var1 & 16777215, 1.0F);
      } else {
         switch (var0.hashCode()) {
            case -1898613620:
               if (var0.equals("Portal")) {
                  return (ParticleEffect)(ParticleTypes.PORTAL instanceof ParticleEffect var17 ? var17 : ParticleTypes.END_ROD);
               }
               break;
            case -1873795168:
               if (var0.equals("ElectricSpark")) {
                  return (ParticleEffect)(ParticleTypes.ELECTRIC_SPARK instanceof ParticleEffect var15 ? var15 : ParticleTypes.END_ROD);
               }
               break;
            case -855004045:
               if (var0.equals("DragonBreath")) {
                  return (ParticleEffect)(ParticleTypes.DRAGON_BREATH instanceof ParticleEffect var14 ? var14 : ParticleTypes.END_ROD);
               }
               break;
            case 2108922:
               if (var0.equals("Crit")) {
                  return (ParticleEffect)(ParticleTypes.CRIT instanceof ParticleEffect var13 ? var13 : ParticleTypes.END_ROD);
               }
               break;
            case 2222509:
               if (var0.equals("Glow")) {
                  return (ParticleEffect)(ParticleTypes.GLOW instanceof ParticleEffect var12 ? var12 : ParticleTypes.END_ROD);
               }
               break;
            case 2434066:
               if (var0.equals("Note")) {
                  return (ParticleEffect)(ParticleTypes.NOTE instanceof ParticleEffect var11 ? var11 : ParticleTypes.END_ROD);
               }
               break;
            case 2583059:
               if (var0.equals("Soul")) {
                  return (ParticleEffect)(ParticleTypes.SOUL instanceof ParticleEffect var10 ? var10 : ParticleTypes.END_ROD);
               }
               break;
            case 57074745:
               if (var0.equals("Enchant")) {
                  return (ParticleEffect)(ParticleTypes.ENCHANT instanceof ParticleEffect var9 ? var9 : ParticleTypes.END_ROD);
               }
               break;
            case 67960595:
               if (var0.equals("Flame")) {
                  return (ParticleEffect)(ParticleTypes.FLAME instanceof ParticleEffect var8 ? var8 : ParticleTypes.END_ROD);
               }
               break;
            case 69599270:
               if (var0.equals("Heart")) {
                  return (ParticleEffect)(ParticleTypes.HEART instanceof ParticleEffect var7 ? var7 : ParticleTypes.END_ROD);
               }
               break;
            case 1848638502:
               if (var0.equals("HappyVillager")) {
                  return (ParticleEffect)(ParticleTypes.HAPPY_VILLAGER instanceof ParticleEffect var6 ? var6 : ParticleTypes.END_ROD);
               }
               break;
            case 1973786418:
               if (var0.equals("Snowflake")) {
                  return (ParticleEffect)(ParticleTypes.SNOWFLAKE instanceof ParticleEffect var5 ? var5 : ParticleTypes.END_ROD);
               }
               break;
            case 2080060172:
               if (var0.equals("EndRod")) {
                  return (ParticleEffect)(ParticleTypes.END_ROD instanceof ParticleEffect var3 ? var3 : ParticleTypes.END_ROD);
               }
         }

         return (ParticleEffect)(ParticleTypes.END_ROD instanceof ParticleEffect var16 ? var16 : ParticleTypes.END_ROD);
      }
   }

   private Boolean getBoolean() {
      return this.particle.check("Dust");
   }
}

