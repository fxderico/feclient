package dev.fede.zenithgui.utility.mixin.accessors;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({DrawContext.class})
public interface DrawContextAccessor {
    @Accessor("vertexConsumers")
    Immediate getVertexConsumers();

    @Invoker("drawItemBar")
    void callDrawItemBar(ItemStack var1, int var2, int var3);

    @Invoker("drawCooldownProgress")
    void callDrawCooldownProgress(ItemStack var1, int var2, int var3);
}
