package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import dev.fede.nyx.setting.StringSetting;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;

public class AutoSpawnerSellModule extends Module {
   private static final int intVal = 8;
   private static final int intVal2 = 32;
   private final StringSetting command = new StringSetting("Command", "sell spawner", 64);
   private final NumberSetting delayTicks = new NumberSetting("DelayTicks", 20.0, 0.0, 200.0, 1.0);
   private final Set<Long> set = new HashSet<>();
   private final Set<Long> set2 = new HashSet<>();
   private final LinkedList<Long> linkedList = new LinkedList<>();
   private long longVal;

   public AutoSpawnerSellModule() {
      super("AutoSpawnerSell", "Auto-runs /sell spawner shortly after a nearby spawner disappears", Category.DONUTSMP);
      this.run6(new Setting[]{this.command, this.delayTicks});
   }

   @Override
   public void run() {
      this.set.clear();
      this.set2.clear();
      this.linkedList.clear();
      this.longVal = 0L;
   }

   @Override
   public void run2() {
      this.set.clear();
      this.set2.clear();
      this.linkedList.clear();
      this.longVal = 0L;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.player.networkHandler != null) {
         this.longVal++;
         this.run6();
         this.set2.clear();

         try {
            this.run4(class310.player.getBlockPos());
         } catch (Throwable var5) {
            return;
         }

         if (!this.set.isEmpty()) {
            BlockPos var1 = class310.player.getBlockPos();

            for (Long var3 : this.set) {
               if (!this.set2.contains(var3)) {
                  BlockPos var4 = BlockPos.fromLong(var3);
                  if (check(var4, var1)) {
                     this.run5();
                     if (this.linkedList.size() >= 32) {
                        break;
                     }
                  }
               }
            }
         }

         this.set.clear();
         this.set.addAll(this.set2);
      } else {
         this.set.clear();
         this.set2.clear();
         this.linkedList.clear();
      }
   }

   private void run4(BlockPos var1) {
      Mutable var3 = new Mutable();

      for (int var4 = -8; var4 <= 8; var4++) {
         for (int var5 = -8; var5 <= 8; var5++) {
            for (int var6 = -8; var6 <= 8; var6++) {
               var3.set(var1.getX() + var4, var1.getY() + var5, var1.getZ() + var6);
               BlockState var7 = class310.world.getBlockState(var3);
               if (var7.isOf(Blocks.SPAWNER)) {
                  this.set2.add(var3.asLong());
               }
            }
         }
      }
   }

   private static boolean check(BlockPos var0, BlockPos var1) {
      int var2 = Math.abs(var0.getX() - var1.getX());
      int var3 = Math.abs(var0.getY() - var1.getY());
      int var4 = Math.abs(var0.getZ() - var1.getZ());
      return var2 <= 8 && var3 <= 8 && var4 <= 8;
   }

   private void run5() {
      long var1 = this.longVal + Math.max(0L, this.delayTicks.getValueLong());
      this.linkedList.addLast(var1);
   }

   private void run6() {
      if (!this.linkedList.isEmpty()) {
         Iterator var1 = this.linkedList.iterator();
         String var2 = this.command.getValue();
         boolean var3 = var2 != null && !var2.isBlank();
         String var4 = var3 ? (var2.charAt(0) == '/' ? var2.substring(1) : var2) : null;
         boolean var5 = var4 != null && !var4.isBlank();

         while (var1.hasNext()) {
            long var6 = (Long)var1.next();
            if (var6 > this.longVal) {
               break;
            }

            var1.remove();
            if (var5) {
               try {
                  class310.player.networkHandler.sendChatCommand(var4);
               } catch (Throwable var9) {
               }
            }
         }
      }
   }
}

