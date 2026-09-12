package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ArrowESPModule extends Module {
   private final BooleanSetting arrow = new BooleanSetting("Arrow", true);
   private final BooleanSetting trident = new BooleanSetting("Trident", true);
   private final BooleanSetting snowball = new BooleanSetting("Snowball", false);
   private final BooleanSetting egg = new BooleanSetting("Egg", false);
   private final BooleanSetting enderPearl = new BooleanSetting("EnderPearl", true);
   private final BooleanSetting windCharge = new BooleanSetting("WindCharge", true);
   private final ColorSetting color = new ColorSetting("Color", -24576);
   private final NumberSetting maxDistance = new NumberSetting("MaxDistance", 64.0, 8.0, 256.0, 1.0);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.5, 0.5, 5.0, 0.1);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private static final double doubleVal = 0.15;

   public ArrowESPModule() {
      super("ArrowESP", "Highlights projectiles in flight (arrows, tridents, pearls, ...)", Category.RENDER);
      this.run6(
         new Setting[]{
            this.arrow,
            this.trident,
            this.snowball,
            this.egg,
            this.enderPearl,
            this.windCharge,
            this.color,
            this.maxDistance,
            this.lineWidth,
            this.throughWalls
         }
      );
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.world != null && class310.player != null) {
         Vec3d var3 = class310.player.getEntityPos();
         double var4 = this.maxDistance.getValue() * this.maxDistance.getValue();
         int var6 = this.color.getValue();
         float var7 = this.lineWidth.getValueFloat();
         boolean var8 = this.throughWalls.getValue();

         for (Entity var10 : class310.world.getEntities()) {
            if (var10.isAlive() && this.check(var10) && !(var10.getEntityPos().squaredDistanceTo(var3) > var4)) {
               Box var11 = var10.getBoundingBox().expand(0.15);
               ListUtils.run5(var11, var6, var7, var8);
            }
         }
      }
   }

   private boolean check(Entity var1) {
      if (var1 instanceof TridentEntity) {
         return this.trident.getValue();
      } else if (var1 instanceof SnowballEntity) {
         return this.snowball.getValue();
      } else if (var1 instanceof EggEntity) {
         return this.egg.getValue();
      } else if (var1 instanceof EnderPearlEntity) {
         return this.enderPearl.getValue();
      } else if (var1 instanceof WindChargeEntity) {
         return this.windCharge.getValue();
      } else {
         return !(var1 instanceof ArrowEntity) && !(var1 instanceof SpectralArrowEntity) && !(var1 instanceof PersistentProjectileEntity)
            ? false
            : this.arrow.getValue();
      }
   }
}

