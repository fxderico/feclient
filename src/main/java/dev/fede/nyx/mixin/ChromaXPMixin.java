package dev.fede.nyx.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.fede.nyx.module.modules.client.ChromaXPModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ExperienceBar.class})
public class ChromaXPMixin {
   @Redirect(
      method = {"renderBar"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/Identifier;IIIIIIII)V"
      )
   )
   private void nyx$tintProgress(
      DrawContext var1, RenderPipeline var2, Identifier var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11
   ) {
      ChromaXPModule var12 = ChromaXPModule.chromaXPModule;
      if (var12 != null && var12.isEnabled3()) {
         var1.drawGuiTexture(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12.getInt());
      } else {
         var1.drawGuiTexture(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }
}

