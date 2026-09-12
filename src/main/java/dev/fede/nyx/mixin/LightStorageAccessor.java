package dev.fede.nyx.mixin;

import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.light.LightStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LightStorage.class})
public interface LightStorageAccessor {
   @Accessor("storage")
   ChunkToNibbleArrayMap<?> nyx$getStorage();
}

