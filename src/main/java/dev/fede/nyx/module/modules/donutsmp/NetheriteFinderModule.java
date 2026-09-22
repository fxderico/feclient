package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.render.ListUtils;
import dev.fede.nyx.setting.ColorSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;

public class NetheriteFinderModule extends Module {
   // max bumped 64 -> 128 on request. Safe now that the scan is throttled to
   // every 10 ticks, and in the Nether the Y sweep is hard-capped to 8..22
   // (ancient debris band) so even a big radius stays a thin slab there.
   private final NumberSetting radius = new NumberSetting("Radius", 32.0, 8.0, 128.0, 1.0);
   private final NumberSetting alpha = new NumberSetting("Alpha", 0.7, 0.1, 1.0, 0.05);
   private final ColorSetting color = new ColorSetting("Color", -1056989645);
   private final NumberSetting lineWidth = new NumberSetting("LineWidth", 1.5, 0.5, 4.0, 0.1);
   private static final int intVal = 10;
   private static final int intVal2 = 8;
   private static final int intVal3 = 22;
   private final Set<BlockPos> set = new HashSet<>();
   private int intVal4;

   public NetheriteFinderModule() {
      super("NetheriteFinder", "Ancient-debris X-ray (through walls)", Category.DONUTSMP);
      this.run6(new Setting[]{this.radius, this.alpha, this.color, this.lineWidth});
   }

   @Override
   public void run2() {
      this.set.clear();
      this.intVal4 = 0;
   }

   // onEnable — just reset. The heavy scan must NOT live here: run() is called
   // once on toggle, so the % 10 throttle that used to gate it never advanced.
   @Override
   public void run() {
      this.set.clear();
      this.intVal4 = 0;
   }

   // onTick (bridge calls run3() every tick). The ~(2r+1)^2 * height block scan
   // was being run UNTHROTTLED every tick here (radius 32 => ~270k getBlockState
   // calls * 20/s) — that was the lag. Now gated to once every 10 ticks, which
   // is what the original throttle intended.
   @Override
   public void run3() {
      if (class310.player == null || class310.world == null) {
         return;
      }
      // Ancient debris ONLY generates in the Nether. Scanning the overworld/end
      // is pure wasted work — and at radius 128 in the overworld that's a ~17M
      // block sweep (the Nether caps Y to the 8..22 debris band, a thin slab).
      // Skip entirely and drop any stale results when you're not in the Nether.
      if (class310.world.getRegistryKey() != World.NETHER) {
         if (!this.set.isEmpty()) {
            this.set.clear();
         }
         return;
      }
      if (this.intVal4++ % 10 != 0) {
         return;
      }
      try {
         this.run5();
         this.scanNow();
      } catch (Throwable var2) {
         this.set.clear();
      }
   }

   @Override
   public void run4(DrawContext var1, float var2) {
      if (class310.player != null && class310.world != null && !this.set.isEmpty()) {
         int var3 = this.color.getValue();
         int var4 = var3 >>> 24 & 0xFF;
         int var5 = Math.max(0, Math.min(255, (int)Math.round(var4 * this.alpha.getValue())));
         int var6 = var5 << 24 | var3 & 16777215;
         float var7 = this.lineWidth.getValueFloat();

         for (BlockPos var9 : this.set) {
            Box var10 = new Box(var9.getX(), var9.getY(), var9.getZ(), var9.getX() + 1.0, var9.getY() + 1.0, var9.getZ() + 1.0);
            ListUtils.run5(var10, var6, var7, true);
         }
      }
   }

   private void scanNow() {
      int var1 = this.radius.getValueInt();
      int var2 = (int)Math.floor(class310.player.getX());
      int var3 = (int)Math.floor(class310.player.getY());
      int var4 = (int)Math.floor(class310.player.getZ());
      int var5;
      int var6;
      if (class310.world.getRegistryKey() == World.NETHER) {
         var5 = 8;
         var6 = 22;
      } else {
         var5 = var3 - var1;
         var6 = var3 + var1;
      }

      var5 = Math.max(var5, var3 - var1);
      var6 = Math.min(var6, var3 + var1);
      if (var5 <= var6) {
         Mutable var7 = new Mutable();

         for (int var8 = var5; var8 <= var6; var8++) {
            for (int var9 = -var1; var9 <= var1; var9++) {
               for (int var10 = -var1; var10 <= var1; var10++) {
                  int var11 = var2 + var10;
                  int var12 = var4 + var9;
                  var7.set(var11, var8, var12);
                  if (class310.world.getBlockState(var7).isOf(Blocks.ANCIENT_DEBRIS)) {
                     this.set.add(var7.toImmutable());
                  }
               }
            }
         }
      }
   }

   private void run5() {
      if (!this.set.isEmpty()) {
         Iterator var1 = this.set.iterator();

         while (var1.hasNext()) {
            BlockPos var2 = (BlockPos)var1.next();
            if (!class310.world.getBlockState(var2).isOf(Blocks.ANCIENT_DEBRIS)) {
               var1.remove();
            }
         }
      }
   }
}

