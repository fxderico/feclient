package dev.fede.nyx.module.modules.addons;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BlockOutlineModule extends Module {
   private final ColorSetting color = new ColorSetting("Color", -9663233);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.5, 0.5, 4.0, 0.1);
   private final BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", false);
   private final BooleanSetting filled = new BooleanSetting("Filled", false);
   private final ColorSetting fillColor = new ColorSetting("FillColor", 812420351);
   private final NumberSetting animSpeed = new NumberSetting("AnimationSpeed", 0.25, 0.05, 1.0, 0.05);
   private final NumberSetting expand = new NumberSetting("Expand", 0.002, 0.0, 0.05, 0.001);
   private double doubleVal;
   private double doubleVal2;
   private double doubleVal3;
   private double doubleVal4;
   private double doubleVal5;
   private double doubleVal6;
   private double doubleVal7;
   private double doubleVal8;
   private double doubleVal9;
   private double doubleVal10;
   private double doubleVal11;
   private double doubleVal12;
   private boolean bool;
   private boolean bool2;

   public BlockOutlineModule() {
      super("BlockOutline", "Smooth animated outline on the block your crosshair is on.", Category.ADDONS);
      this.run6(new Setting[]{this.color, this.lineWidth, this.throughWalls, this.filled, this.fillColor, this.animSpeed, this.expand});
      this.fillColor.visibleWhen(this.filled::getValue);
   }

   @Override
   public void run2() {
      this.bool = false;
      this.bool2 = false;
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null) {
         HitResult var1 = class310.crosshairTarget;
         if (var1 instanceof BlockHitResult var2 && var1.getType() == Type.BLOCK) {
            BlockPos var3 = var2.getBlockPos();
            if (var3 == null) {
               this.bool = false;
               return;
            }

            BlockState var4 = class310.world.getBlockState(var3);
            if (var4.isAir()) {
               this.bool = false;
               return;
            }

            Box var5;
            try {
               var5 = var4.getOutlineShape(class310.world, var3).getBoundingBox().offset(var3);
            } catch (Throwable var7) {
               var5 = new Box(var3);
            }

            this.doubleVal7 = var5.minX;
            this.doubleVal8 = var5.minY;
            this.doubleVal9 = var5.minZ;
            this.doubleVal10 = var5.maxX;
            this.doubleVal11 = var5.maxY;
            this.doubleVal12 = var5.maxZ;
            this.bool = true;
         } else {
            this.bool = false;
         }
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.player != null && class310.world != null && this.bool) {
         if (!this.bool2) {
            this.doubleVal = this.doubleVal7;
            this.doubleVal2 = this.doubleVal8;
            this.doubleVal3 = this.doubleVal9;
            this.doubleVal4 = this.doubleVal10;
            this.doubleVal5 = this.doubleVal11;
            this.doubleVal6 = this.doubleVal12;
            this.bool2 = true;
         }

         double var3 = doubleOf(this.animSpeed.getValue());
         double var5 = 1.0 - Math.pow(1.0 - var3, Math.max(0.001, (double)var2));
         this.doubleVal = this.doubleVal + (this.doubleVal7 - this.doubleVal) * var5;
         this.doubleVal2 = this.doubleVal2 + (this.doubleVal8 - this.doubleVal2) * var5;
         this.doubleVal3 = this.doubleVal3 + (this.doubleVal9 - this.doubleVal3) * var5;
         this.doubleVal4 = this.doubleVal4 + (this.doubleVal10 - this.doubleVal4) * var5;
         this.doubleVal5 = this.doubleVal5 + (this.doubleVal11 - this.doubleVal5) * var5;
         this.doubleVal6 = this.doubleVal6 + (this.doubleVal12 - this.doubleVal6) * var5;
         double var7 = this.expand.getValue();
         Box var9 = new Box(
            this.doubleVal - var7, this.doubleVal2 - var7, this.doubleVal3 - var7, this.doubleVal4 + var7, this.doubleVal5 + var7, this.doubleVal6 + var7
         );
         int var10 = this.color.getValue();
         boolean var11 = this.throughWalls.getValue();
         if (this.filled.getValue()) {
            int var12 = this.fillColor.getValue();
            if (ListUtils.isEnabled8()) {
               ListUtils.run3(var9, var12, var11);
            }
         }

         ListUtils.run5(var9, var10, this.lineWidth.getValueFloat(), var11);
      }
   }

   private static double doubleOf(double var0) {
      return var0 < 0.0 ? 0.0 : (var0 > 1.0 ? 1.0 : var0);
   }

   private static Vec3d getclass243() {
      return Vec3d.ZERO;
   }
}

