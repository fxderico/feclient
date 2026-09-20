package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

/**
 * NoClip — sets Entity.noClip = true, the same field vanilla itself uses
 * for spectators. Free movement through blocks, camera and body together.
 *
 * This is NOT a server-side bypass: continuously walking through solid
 * blocks like this is exactly the pattern a real movement-verifying
 * anticheat corrects or flags immediately. It's genuinely useful
 * singleplayer, in creative, or on anything that doesn't validate
 * movement server-side — for the "phase through one thin wall on a live
 * server" case, see the separate Phase module instead, which is a single
 * teleport rather than sustained no-collision movement.
 */
public class NoClipModule extends Module {

    public NoClipModule() {
        super("NoClip", "Disables your own collision — free movement through blocks. Singleplayer/creative use; see Phase for live-server wall clips.", Category.WORLD);
    }

    @Override
    protected void onEnable() {
        Entity player = MinecraftClient.getInstance().player;
        if (player != null) player.noClip = true;
    }

    @Override
    protected void onDisable() {
        Entity player = MinecraftClient.getInstance().player;
        if (player != null) player.noClip = false;
    }

    @Override
    public void onTick() {
        // re-assert every tick — some vanilla code paths (e.g. re-entering
        // a dimension, respawning) can silently reset noClip back to false
        // while the module's still meant to be on.
        Entity player = MinecraftClient.getInstance().player;
        if (player != null) player.noClip = true;
    }
}
