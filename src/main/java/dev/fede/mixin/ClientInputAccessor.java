package dev.fede.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Input.class})
public interface ClientInputAccessor {
   @Accessor("field_54155")
   void FeClient$setKeyPresses(PlayerInput var1);

   @Accessor("field_55868")
   void FeClient$setMoveVector(Vec2f var1);
}

