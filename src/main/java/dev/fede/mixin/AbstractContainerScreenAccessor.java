package dev.fede.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({HandledScreen.class})
public interface AbstractContainerScreenAccessor {
   @Accessor("field_2787")
   Slot getHoveredSlot();

   @Accessor("field_2776")
   int getLeftPos();

   @Accessor("field_2800")
   int getTopPos();

   @Accessor("field_2792")
   int getImageWidth();

   @Accessor("field_2779")
   int getImageHeight();
}

