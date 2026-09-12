package dev.fede.nyx.mixin;

import net.minecraft.world.chunk.ChunkNibbleArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ChunkNibbleArray.class})
public interface ChunkNibbleArrayAccessor {
   @Accessor("bytes")
   byte[] nyx$getBytes();
}

