package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.ArrayList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class ProjectileArcModule extends Module {
   private final ColorSetting color = new ColorSetting("Color", -1066627841);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.5, 0.5, 4.0, 0.1);
   private final NumberSetting maxSteps = new NumberSetting("MaxSteps", 120.0, 20.0, 500.0, 10.0);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true);
   private final BooleanSetting landingMarker = new BooleanSetting("LandingMarker", true);
   private static final double doubleVal = 0.99;
   private static final double doubleVal2 = 0.8;
   private static final ProjectileArcModule.Inner1 projectileArcModuleInner1 = new ProjectileArcModule.Inner1(1.5, 0.03);
   private static final ProjectileArcModule.Inner1 pa2 = new ProjectileArcModule.Inner1(0.5, 0.05);
   private static final ProjectileArcModule.Inner1 pa3 = new ProjectileArcModule.Inner1(2.5, 0.05);

   public ProjectileArcModule() {
      super("ProjectileArc", "Predicts the arc of the throwable in your main hand — ender pearl, snowball, egg, potion, trident.", Category.RENDER);
      this.run6(new Setting[]{this.color, this.lineWidth, this.maxSteps, this.throughWalls, this.landingMarker});
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.player != null && class310.world != null) {
         ItemStack var3 = class310.player.getMainHandStack();
         if (var3 != null && !var3.isEmpty()) {
            ProjectileArcModule.Inner1 var4 = paOf(var3.getItem());
            if (var4 != null) {
               int var5 = this.color.getValue();
               float var6 = this.lineWidth.getValueFloat();
               boolean var7 = this.throughWalls.getValue();
               int var8 = this.maxSteps.getValueInt();
               Vec3d var9 = class310.player.getEyePos();
               Vec3d var10 = class310.player.getRotationVec(var2);
               Vec3d var11 = var10.multiply(var4.initialSpeed);
               ArrayList var12 = new ArrayList(var8 + 2);
               var12.add(var9);

               for (int var14 = 0; var14 < var8; var14++) {
                  Vec3d var13 = var9;
                  var9 = var9.add(var11);
                  BlockHitResult var15 = class310.world.raycast(new RaycastContext(var13, var9, ShapeType.COLLIDER, FluidHandling.NONE, class310.player));
                  if (var15 != null && var15.getType() == Type.BLOCK) {
                     var12.add(var15.getPos());
                     break;
                  }

                  var12.add(var9);
                  var11 = var11.multiply(0.99);
                  var11 = var11.add(0.0, -var4.gravity, 0.0);
               }

               int var18 = 0;

               for (int var20 = var12.size() - 1; var18 < var20; var18++) {
                  ListUtils.run12((Vec3d)var12.get(var18), (Vec3d)var12.get(var18 + 1), var5, var6, var7);
               }

               if (this.landingMarker.getValue() && var12.size() >= 2) {
                  Vec3d var19 = (Vec3d)var12.get(var12.size() - 1);
                  double var21 = 0.3;
                  ListUtils.run12(var19.add(-0.3, 0.0, -0.3), var19.add(0.3, 0.0, 0.3), var5, var6, var7);
                  ListUtils.run12(var19.add(-0.3, 0.0, 0.3), var19.add(0.3, 0.0, -0.3), var5, var6, var7);
                  ListUtils.run12(var19.add(0.0, -0.3, 0.0), var19.add(0.0, 0.3, 0.0), var5, var6, var7);
               }
            }
         }
      }
   }

   private static ProjectileArcModule.Inner1 paOf(Item var0) {
      if (var0 == Items.ENDER_PEARL || var0 == Items.SNOWBALL || var0 == Items.EGG || var0 == Items.EXPERIENCE_BOTTLE) {
         return projectileArcModuleInner1;
      } else if (var0 == Items.SPLASH_POTION || var0 == Items.LINGERING_POTION) {
         return pa2;
      } else {
         return var0 == Items.TRIDENT ? pa3 : null;
      }
   }

record Inner1(double initialSpeed, double gravity) {

}
}

