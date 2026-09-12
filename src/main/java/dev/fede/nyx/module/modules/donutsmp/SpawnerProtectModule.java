package dev.fede.nyx.module.modules.donutsmp;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;

public class SpawnerProtectModule extends Module {
   private static final long longVal = 3000L;
   private final BooleanSetting enableSound = new BooleanSetting("EnableSound", true);
   private long longVal2;

   public SpawnerProtectModule() {
      super("SpawnerProtect", "Cancels your own break attempts on spawners so a stray click can't destroy them", Category.DONUTSMP);
      this.run6(new Setting[]{this.enableSound});
   }

   @Override
   public void run() {
      this.longVal2 = 0L;
   }

   @Override
   public void run2() {
      this.longVal2 = 0L;
   }

   @Override
   public void run3() {
      if (class310.player != null && class310.world != null && class310.interactionManager != null) {
         if (class310.options != null && class310.options.attackKey.isPressed()) {
            if (class310.crosshairTarget instanceof BlockHitResult var2 && var2.getType() == Type.BLOCK) {
               try {
                  BlockPos var3 = var2.getBlockPos();
                  BlockState var4 = class310.world.getBlockState(var3);
                  if (var4 == null || !var4.isOf(Blocks.SPAWNER)) {
                     return;
                  }

                  class310.interactionManager.cancelBlockBreaking();
                  this.run4();
               } catch (Throwable var5) {
               }
            }
         }
      }
   }

   private void run4() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.longVal2 >= 3000L) {
         this.longVal2 = var1;
         if (class310.inGameHud != null) {
            class310.inGameHud.getChatHud().addMessage(Text.literal("§c[SpawnerProtect] blocked spawner break attempt"));
         }

         // sound suppressed
      }
   }
}

