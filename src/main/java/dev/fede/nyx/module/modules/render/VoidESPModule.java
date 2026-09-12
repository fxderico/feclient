package dev.fede.nyx.module.modules.render;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos.Mutable;

public class VoidESPModule extends Module {
   private final ColorSetting color = new ColorSetting("Color", -1057017808);
   private final NumberSetting radius = new NumberSetting("Radius", 8.0, 3.0, 24.0, 1.0);
   private final NumberSetting lineWidth = new NumberSetting("Line Width", 1.5, 0.5, 5.0, 0.1);
   private final BooleanSetting includeFluids = new BooleanSetting("Fluids Count As Solid", false);
   private static final int intVal = 16;
   private static final int intVal2 = 5;
   private final List<BlockPos> list = new ArrayList<>();
   private int intVal3;

   public VoidESPModule() {
      super("VoidESP", "Highlights ledges that fall straight through to the void", Category.RENDER);
      this.run6(new Setting[]{this.color, this.radius, this.lineWidth, this.includeFluids});
   }

   @Override
   public void run2() {
      this.list.clear();
      this.intVal3 = 0;
   }

   @Override
   public void run() {
      if (class310.player != null && class310.world != null) {
         if (this.intVal3++ % 5 == 0) {
            this.run3();
         }
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.player != null && class310.world != null && !this.list.isEmpty()) {
         int var3 = this.color.getValue();
         float var4 = this.lineWidth.getValueFloat();

         for (BlockPos var6 : this.list) {
            ListUtils.run5(new Box(var6.getX(), var6.getY(), var6.getZ(), var6.getX() + 1.0, var6.getY() + 1.0, var6.getZ() + 1.0), var3, var4, true);
         }
      }
   }

   public void run3() {
      this.list.clear();
      int var1 = this.radius.getValueInt();
      int var2 = (int)Math.floor(class310.player.getX());
      int var3 = (int)Math.floor(class310.player.getY());
      int var4 = (int)Math.floor(class310.player.getZ());
      int var5 = class310.world.getBottomY();
      int var6 = var5 - 16;
      boolean var7 = this.includeFluids.getValue();
      Mutable var8 = new Mutable();

      for (int var9 = -var1; var9 <= var1; var9++) {
         for (int var10 = -var1; var10 <= var1; var10++) {
            int var11 = var2 + var9;
            int var12 = var4 + var10;
            BlockPos var13 = this.class2338Of(var8, var11, var3, var12, var6, var7);
            if (var13 != null) {
               this.list.add(var13);
            }
         }
      }
   }

   private BlockPos class2338Of(Mutable var1, int var2, int var3, int var4, int var5, boolean var6) {
      int var7 = var3;
      int var8 = Integer.MIN_VALUE;

      for (int var9 = Math.max(var5, class310.world.getBottomY() - 16); var7 > var9; var7--) {
         var1.set(var2, var7, var4);
         BlockState var10 = class310.world.getBlockState(var1);
         if (check(var10, var6)) {
            var8 = var7;
            break;
         }
      }

      if (var8 == Integer.MIN_VALUE) {
         return null;
      } else {
         for (int var11 = var8 - 1; var11 >= var5; var11--) {
            var1.set(var2, var11, var4);
            if (check(class310.world.getBlockState(var1), var6)) {
               return null;
            }
         }

         return new BlockPos(var2, var8, var4);
      }
   }

   private static boolean check(BlockState var0, boolean var1) {
      return var0.isAir() ? false : var1 || var0.getFluidState().isEmpty();
   }
}

