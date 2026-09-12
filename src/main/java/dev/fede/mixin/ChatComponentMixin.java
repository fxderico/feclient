package dev.fede.mixin;

import dev.fede.FeClient;
import dev.fede.module.ModuleManager;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({ChatHud.class})
public class ChatComponentMixin {
   @ModifyVariable(
      method = {"method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private Text FeClient$censorChat(Text component) {
      ModuleManager modules = FeClient.modules();
      if (modules == null) {
         return component;
      } else {
         Text result = component;
         if (modules.fakeRoles != null) {
            result = modules.fakeRoles.decorateChat(component);
         }

         if (modules.nameProtect != null && modules.nameProtect.isEnabled()) {
            result = modules.nameProtect.censorChat(result);
         }

         return result;
      }
   }
}



