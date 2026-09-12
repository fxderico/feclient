package dev.fede.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import dev.fede.module.impl.ArmorTrimHiderModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({EquipmentRenderer.class})
public class EquipmentLayerRendererMixin {
   @ModifyExpressionValue(
      method = {"method_64078(Lnet/minecraft/class_10186$class_10190;Lnet/minecraft/class_5321;Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_1799;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_2960;II)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1799;method_58694(Lnet/minecraft/class_9331;)Ljava/lang/Object;"
      )}
   )
   private Object FeClient$armorTrim(Object original, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) Object renderState) {
      ModuleManager modules = FeClient.modules();
      if (modules == null) {
         return original;
      } else {
         ArmorTrimHiderModule module = modules.armorTrimHider;
         if (module == null || !module.isEnabled()) {
            return original;
         } else {
            return !module.affectsOwn() && FeClient$isLocalPlayer(renderState) ? original : module.mapTrim(itemStack, (ArmorTrim)original);
         }
      }
   }

   private static boolean FeClient$isLocalPlayer(Object renderState) {
      if (!(renderState instanceof PlayerEntityRenderState avatar)) {
         return false;
      } else {
         MinecraftClient mc = MinecraftClient.getInstance();
         return mc.player != null && avatar.id == mc.player.getId();
      }
   }
}



