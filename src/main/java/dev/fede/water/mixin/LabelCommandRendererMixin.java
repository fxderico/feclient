package dev.fede.water.mixin;

import dev.fede.water.utils.NametagRenderState;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.LabelCommandRenderer;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({LabelCommandRenderer.class})
public class LabelCommandRendererMixin {
   @Redirect(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V",
         ordinal = 1
      ),
      require = 0
   )
   private void water$drawHealthLabelsWithOutline(
      TextRenderer var1,
      Text var2,
      float var3,
      float var4,
      int var5,
      boolean var6,
      Matrix4f var7,
      VertexConsumerProvider var8,
      TextLayerType var9,
      int var10,
      int var11
   ) {
      if (!NametagRenderState.isOutlinedLabel(var2)) {
         var1.draw(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      } else {
         var1.drawWithOutline(var2.asOrderedText(), var3, var4, var5, -16777216, var7, var8, var11);
      }
   }
}

