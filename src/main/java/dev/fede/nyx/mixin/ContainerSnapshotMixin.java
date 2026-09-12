package dev.fede.nyx.mixin;

import dev.fede.nyx.storage.Chest;
import dev.fede.nyx.storage.Chest$KindUtils;
import dev.fede.nyx.storage.ContainerSnapshotMixinEntry;
import java.util.ArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HandledScreen.class})
public class ContainerSnapshotMixin {
   private static final int POS_SEARCH_RADIUS = 6;

   @Inject(
      method = {"removed"},
      at = {@At("HEAD")}
   )
   private void nyx$captureSnapshot(CallbackInfo var1) {
      HandledScreen var2 = (HandledScreen)(Object)this;
      ScreenHandler var3 = var2.getScreenHandler();
      if (var3 != null && var3.slots != null && !var3.slots.isEmpty()) {
         MinecraftClient var4 = MinecraftClient.getInstance();
         if (var4 != null && var4.player != null && var4.world != null) {
            ArrayList var5 = new ArrayList(var3.slots.size());
            int var6 = 0;

            for (Slot var8 : var3.slots) {
               if (var8 != null && var8.inventory != null && !(var8.inventory instanceof PlayerInventory)) {
                  ItemStack var9 = var8.getStack();
                  if (var9 != null && !var9.isEmpty()) {
                     ContainerSnapshotMixinEntry var10 = ContainerSnapshotMixinEntry.from(var9, var4.world.getRegistryManager());
                     var5.add(var10);
                     if (var10 != null) {
                        var6++;
                     }
                  } else {
                     var5.add(null);
                  }
               }
            }

            if (var6 != 0 && !var5.isEmpty()) {
               BlockPos var11 = resolveContainerPos(var4);
               if (var11 != null) {
                  Chest$KindUtils.updateSnapshot(var11, var5);
               }
            }
         }
      }
   }

   private static BlockPos resolveContainerPos(MinecraftClient var0) {
      double var1 = var0.player.getX();
      double var3 = var0.player.getY() + var0.player.getStandingEyeHeight();
      double var5 = var0.player.getZ();
      Chest var7 = null;
      double var8 = 37.0;

      for (Chest var11 : Chest$KindUtils.all()) {
         double var12 = var11.center().x - var1;
         double var14 = var11.center().y - var3;
         double var16 = var11.center().z - var5;
         double var18 = var12 * var12 + var14 * var14 + var16 * var16;
         if (var18 < var8) {
            var8 = var18;
            var7 = var11;
         }
      }

      return var7 == null ? null : var7.pos();
   }
}

