package dev.fede.nyx.module.modules.player;

import dev.fede.nyx.mixin.MinecraftClientAccessor;
import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class FastPlaceModule extends Module {
   private final NumberSetting delay = new NumberSetting("Delay", 0.0, 0.0, 4.0, 1.0);
   private final BooleanSetting onlyBlocks = new BooleanSetting("OnlyBlocks", true);

   public FastPlaceModule() {
      super("FastPlace", "Removes the delay between right-click block placements", Category.PLAYER);
      this.run6(new Setting[]{this.delay, this.onlyBlocks});
   }

   @Override
   public void run2() {
      if (class310.player != null && class310.world != null) {
         if (this.onlyBlocks.getValue()) {
            ItemStack var1 = class310.player.getMainHandStack();
            if (var1 == null || var1.isEmpty() || !(var1.getItem() instanceof BlockItem)) {
               return;
            }
         }

         run7(this.delay.getValueInt());
      }
   }

   public void run7(int var0) {
      MinecraftClientAccessor var1 = (MinecraftClientAccessor)MinecraftClient.getInstance();
      if (var1.nyx$getItemUseCooldown() > var0) {
         var1.nyx$setItemUseCooldown(var0);
      }
   }
}

