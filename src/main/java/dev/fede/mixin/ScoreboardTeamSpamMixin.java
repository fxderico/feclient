package dev.fede.mixin;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Log-noise fix. On DonutSMP (and any server that re-sends scoreboard team
 * creates for nametags), every duplicate ADD packet makes Scoreboard.addTeam
 * log "Requested creation of existing team '...'", which floods the console
 * with hundreds of WARN lines per session. Vanilla's own behavior in that case
 * is simply to return the already-existing team; we do exactly that at HEAD,
 * skipping only the warn. No behavior change, just a quiet log.
 */
@Mixin(Scoreboard.class)
public class ScoreboardTeamSpamMixin {
   @Inject(
      method = "addTeam(Ljava/lang/String;)Lnet/minecraft/scoreboard/Team;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void fe$silenceExistingTeamWarning(String name, CallbackInfoReturnable<Team> cir) {
      Team existing = ((Scoreboard)(Object)this).getTeam(name);
      if (existing != null) {
         cir.setReturnValue(existing);
      }
   }
}
