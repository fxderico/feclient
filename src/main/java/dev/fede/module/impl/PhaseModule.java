package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;
import dev.fede.settings.BooleanSetting;
import dev.fede.settings.SliderSetting;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Phase — a single blink through a thin wall, not continuous noclip.
 *
 * Scans forward from the player's facing direction, block by block, up to
 * Max Thickness. If every block in that run is solid (checked at both feet
 * and head height) and the block right after the run is clear, teleports
 * the player straight there via Entity.setPosition() — the normal per-tick
 * movement-packet loop (Mouse.updateMouse -> sendMovementPackets, the same
 * automatic loop NoFall and AimAssist's mixins already sit inside) picks
 * the new position up and sends it on its own; nothing here builds or
 * sends a packet by hand.
 *
 * Whether the server accepts the result depends entirely on that specific
 * server's own movement validation — some only check that consecutive
 * reported positions are within a plausible speed of each other and never
 * verify collision along the path, which is the gap this exploits. Others
 * simulate real physics between packets and will reject or correct it
 * immediately. This module doesn't know or try to detect which kind of
 * server it's talking to — it just performs the clip and lets the server's
 * own response decide what happens next.
 */
public class PhaseModule extends Module {
    public final SliderSetting maxThickness = addSetting(
        new SliderSetting("Max Thickness", "Widest wall (in blocks) it'll try to clip through", 2.0, 1.0, 4.0, 1.0)
    );
    public final SliderSetting cooldownTicks = addSetting(
        new SliderSetting("Cooldown", "Ticks to wait between attempts", 10.0, 0.0, 40.0, 1.0, "t")
    );
    public final BooleanSetting requireForwardHeld = addSetting(
        new BooleanSetting("Require Forward", "Only trigger while holding the forward key into the wall", true)
    );

    private int cooldown;

    public PhaseModule() {
        super("Phase", "Clips through a thin wall in one blink — checks thickness first, does nothing if it can't clear it.", Category.WORLD);
    }

    @Override
    protected void onDisable() {
        this.cooldown = 0;
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.world;
        if (player == null || world == null) return;

        if (this.cooldown > 0) {
            this.cooldown--;
            return;
        }
        if (this.requireForwardHeld.get() && !mc.options.forwardKey.isPressed()) return;
        if (player.hasVehicle()) return;

        Direction facing = player.getHorizontalFacing();
        BlockPos feet = player.getBlockPos();
        int max = Math.max(1, this.maxThickness.getInt());

        int thickness = 0;
        BlockPos cursor = feet.offset(facing);
        while (thickness < max && isWall(world, cursor)) {
            thickness++;
            cursor = cursor.offset(facing);
        }
        // nothing solid immediately ahead, or ran the full scan and it's
        // still solid past Max Thickness — either way, no clean clip here.
        if (thickness == 0 || isWall(world, cursor)) return;

        double newX = cursor.getX() + 0.5;
        double newZ = cursor.getZ() + 0.5;
        player.setPosition(newX, player.getY(), newZ);
        this.cooldown = Math.max(0, this.cooldownTicks.getInt());
    }

    /** Solid at either feet or head height counts as part of the wall — player is 2 tall. */
    private boolean isWall(ClientWorld world, BlockPos pos) {
        return isSolid(world, pos) || isSolid(world, pos.up());
    }

    private boolean isSolid(ClientWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return !state.getCollisionShape(world, pos).isEmpty();
    }
}
