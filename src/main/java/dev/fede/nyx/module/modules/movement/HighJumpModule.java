package dev.fede.nyx.module.modules.movement;

import dev.fede.nyx.module.Category;
import dev.fede.nyx.module.Module;
import dev.fede.nyx.setting.BooleanSetting;
import dev.fede.nyx.setting.NumberSetting;
import dev.fede.nyx.setting.Setting;

/**
 * Jump — jump height only, nothing else.
 *
 * Used to apply the boosted velocity from onTick() (run3), gated on
 * "did the jump key just go from unpressed to pressed AND is the player
 * on ground THIS tick". That's a real race: vanilla's own jump impulse
 * (LivingEntity.jump(), 0.42 up) fires during the same game tick's input/
 * movement pass, which can run before or after this module's onTick
 * depending on event ordering — so isOnGround() at the moment this checked
 * could already read false because vanilla's own jump already took the
 * player off the ground. That's exactly "sometimes registers, sometimes
 * needs a couple presses" — pure timing luck per tick, not a settings bug.
 *
 * Fixed by moving the actual velocity override into LivingEntityJumpMixin,
 * injected straight into LivingEntity.jump() (method_6043) — the exact
 * vanilla method that applies the jump impulse. No tick-order race left:
 * this module now just holds the settings (ACTIVE flag + jumpVelocity +
 * onlyOnGround, exposed statically) and the mixin fires every single time
 * jump() actually runs.
 */
public class HighJumpModule extends Module {
   public static volatile boolean ACTIVE = false;
   public static volatile double JUMP_VELOCITY = 5.0;
   public static volatile boolean ONLY_ON_GROUND = true;

   private final NumberSetting jumpVelocity = new NumberSetting("JumpVelocity", 5.0, 0.5, 5.0, 0.1);
   private final BooleanSetting onlyOnGround = new BooleanSetting("OnlyOnGround", true);

   public HighJumpModule() {
      super("Jump", "Amplifies the initial jump impulse — controls jump height only, nothing else.", Category.MOVEMENT);
      this.run6(new Setting[]{this.jumpVelocity, this.onlyOnGround});
   }

   @Override
   public void run() {
      ACTIVE = true;
      sync();
   }

   @Override
   public void run2() {
      ACTIVE = false;
   }

   @Override
   public void run3() {
      // settings can change live while enabled — keep the static mirror current
      sync();
   }

   private void sync() {
      JUMP_VELOCITY = this.jumpVelocity.getValue();
      ONLY_ON_GROUND = this.onlyOnGround.getValue();
   }
}
