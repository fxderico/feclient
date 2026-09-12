package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;

/**
 * AutoRespawn — instantly respawns after death.
 * Ported from Zenith DLC 2.0.
 */
public final class AutoRespawnModule extends Module {

    public AutoRespawnModule() {
        super("AutoRespawn", "Automatically respawns after death", Category.PLAYER);
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;
        if (mc.currentScreen instanceof DeathScreen && mc.player.deathTime > 5) {
            mc.player.requestRespawn();
            mc.setScreen(null);
        }
    }
}
