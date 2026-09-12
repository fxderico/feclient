package dev.fede.water.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Input.class})
public interface InputAccessor {
   @Accessor("playerInput")
   void water$setPlayerInput(PlayerInput var1);

   @Accessor("movementVector")
   void water$setMovementVector(Vec2f var1);
}

