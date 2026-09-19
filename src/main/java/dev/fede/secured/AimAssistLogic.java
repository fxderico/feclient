package dev.fede.secured;

import dev.fede.module.AimAssistCompute;
import dev.fede.module.impl.AimAssistModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.RaycastContext;

/**
 * Real implementation behind AimAssistModule, loaded via ProtectedContent at
 * dev.fede.secured.AimAssistLogic. Was previously missing entirely (the
 * loader always returned null, silently — the module had a full settings
 * panel and did nothing).
 *
 * Behaviour, matching what was actually asked for: this is a LOCK, not a
 * continuous nearest-target snap. computePixels() only picks a new target
 * when there isn't a valid one already (none locked, or the locked one
 * died / got removed / left range). Once locked, it stays locked onto that
 * exact entity regardless of where the crosshair wanders, until the module
 * is disabled (reset()) or the target itself becomes invalid. Toggling the
 * module off and back on — via its keybind or the GUI — re-acquires whatever
 * is under the crosshair at that moment.
 *
 * Hooked from MouseHandlerMixin at the top of Mouse.updateMouse(double),
 * before cursorDeltaX/Y get run through the sensitivity curve and handed to
 * Entity.changeLookDirection(). That means the pixel offset this returns
 * has to be the INVERSE of that pipeline — see toRawDelta() — or the pull
 * speed would silently depend on the player's mouse sensitivity setting.
 */
public final class AimAssistLogic implements AimAssistCompute {

    // entities past this were never loaded/synced to begin with, so there's
    // no meaningful "further" than this for a raycast or search radius —
    // "infinite" in practice means "don't cut off before the game itself would".
    private static final double RAY_LENGTH_INFINITE = 512.0;

    private Entity locked;

    @Override
    public double[] computePixels(AimAssistModule module, double dt, double userDX, double userDY) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        ClientWorld world = mc.world;
        if (player == null || world == null) return null;

        if (!module.alwaysActive.get() && !mc.options.attackKey.isPressed()) {
            // paused, not dropped — the lock (if any) survives so it's
            // instant again the moment you start attacking.
            return null;
        }

        boolean infinite = module.infiniteRange.get();
        double range = infinite ? RAY_LENGTH_INFINITE : module.range.get();
        double rangeSq = infinite ? Double.MAX_VALUE : range * range;

        if (!isValidLock(this.locked, player, rangeSq)) {
            this.locked = acquire(module, mc, player, world, range, rangeSq, infinite);
        }
        if (this.locked == null) return null;

        Vec3d aimPoint = pointOn(this.locked, module.targetPart.get());
        Vec3d eye = player.getEyePos();
        double dx = aimPoint.x - eye.x;
        double dy = aimPoint.y - eye.y;
        double dz = aimPoint.z - eye.z;
        double distXZ = Math.sqrt(dx * dx + dz * dz);

        double desiredYaw = Math.toDegrees(MathHelper.atan2(dz, dx)) - 90.0;
        double desiredPitch = -Math.toDegrees(MathHelper.atan2(dy, distXZ));

        double yawDiff = MathHelper.wrapDegrees(desiredYaw - player.getYaw());
        double pitchDiff = module.vertical.get() ? MathHelper.clamp(desiredPitch - player.getPitch(), -90.0, 90.0) : 0.0;

        // ease toward the target rather than snapping — speed picks how fast
        // it converges, smoothness stretches that out. both are 1-10 sliders.
        double rate = Math.max(0.1, module.speed.get()) / Math.max(1.0, module.smoothness.get());
        double t = 1.0 - Math.exp(-rate * dt * 10.0);
        t = MathHelper.clamp(t, 0.0, 1.0);

        double yawStep = yawDiff * t;
        double pitchStep = pitchDiff * t;

        double[] raw = toRawDelta(mc, yawStep, pitchStep);
        return raw;
    }

    @Override
    public void reset() {
        this.locked = null;
    }

    private boolean isValidLock(Entity target, ClientPlayerEntity player, double rangeSq) {
        return target != null
            && target.isAlive()
            && !target.isRemoved()
            && player.squaredDistanceTo(target) <= rangeSq;
    }

    private Entity acquire(AimAssistModule module, MinecraftClient mc, ClientPlayerEntity player, ClientWorld world, double range, double rangeSq, boolean infinite) {
        Vec3d eye = player.getEyePos();
        Vec3d look = player.getRotationVector();
        double fovCos = Math.cos(Math.toRadians(Math.min(179.9, module.fov.get()) / 2.0));
        boolean wallCheck = module.wallCheck.get();

        java.util.List<Entity> candidates = new java.util.ArrayList<>();
        // Box.expand() with a huge radius risks precision/overflow weirdness at
        // the extremes, and there's no reason to build one at all when every
        // loaded entity is in bounds anyway — just walk everything the client
        // actually has loaded instead of asking for a region that contains it.
        Iterable<Entity> pool = infinite
            ? world.getEntities()
            : world.getOtherEntities(player, player.getBoundingBox().expand(range), e -> true);
        for (Entity e : pool) {
            if (e == player || !e.isAlive() || e.isRemoved() || !isEligible(module, e)) continue;
            if (player.squaredDistanceTo(e) > rangeSq) continue;
            candidates.add(e);
        }

        Entity bestByRay = null;
        double bestRayDist = Double.MAX_VALUE;
        Entity bestByAngle = null;
        double bestAngleCos = fovCos;

        Vec3d rayEnd = eye.add(look.multiply(range));

        for (Entity candidate : candidates) {
            if (candidate.isInvisible() && !module.invisibles.get()) continue;
            Vec3d aimPoint = pointOn(candidate, module.targetPart.get());

            if (wallCheck && !hasLineOfSight(world, player, eye, aimPoint)) continue;

            // direct-raycast pass: does the crosshair's forward ray actually
            // clip this entity's hitbox (slightly padded for forgiveness)?
            Box padded = candidate.getBoundingBox().expand(0.15);
            java.util.Optional<Vec3d> hit = padded.raycast(eye, rayEnd);
            if (hit.isPresent()) {
                double d = hit.get().squaredDistanceTo(eye);
                if (d < bestRayDist) {
                    bestRayDist = d;
                    bestByRay = candidate;
                }
            }

            // angle-to-crosshair fallback pass, within the FOV cone
            Vec3d toTarget = aimPoint.subtract(eye).normalize();
            double cos = toTarget.dotProduct(look);
            if (cos > bestAngleCos) {
                bestAngleCos = cos;
                bestByAngle = candidate;
            }
        }

        return bestByRay != null ? bestByRay : bestByAngle;
    }

    private boolean isEligible(AimAssistModule module, Entity e) {
        if (e instanceof PlayerEntity) return module.players.get();
        if (e instanceof HostileEntity) return module.hostiles.get();
        return module.passive.get();
    }

    private boolean hasLineOfSight(ClientWorld world, ClientPlayerEntity player, Vec3d eye, Vec3d aimPoint) {
        RaycastContext ctx = new RaycastContext(
            eye, aimPoint,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            player
        );
        BlockHitResult result = world.raycast(ctx);
        return result.getType() == HitResult.Type.MISS;
    }

    private Vec3d pointOn(Entity target, String part) {
        Box box = target.getBoundingBox();
        double cx = (box.minX + box.maxX) / 2.0;
        double cz = (box.minZ + box.maxZ) / 2.0;
        return switch (part) {
            case "Head" -> target.getEyePos();
            case "Feet" -> new Vec3d(cx, box.minY, cz);
            case "Nearest" -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                Vec3d eye = mc.player != null ? mc.player.getEyePos() : new Vec3d(cx, box.minY, cz);
                double nx = MathHelper.clamp(eye.x, box.minX, box.maxX);
                double ny = MathHelper.clamp(eye.y, box.minY, box.maxY);
                double nz = MathHelper.clamp(eye.z, box.minZ, box.maxZ);
                yield new Vec3d(nx, ny, nz);
            }
            default -> new Vec3d(cx, box.minY + (box.maxY - box.minY) * 0.58, cz); // "Body" — torso, not dead center
        };
    }

    /**
     * Inverse of Mouse.updateMouse()'s transform: sensitivity curve
     * ((sens*0.6+0.2)^3 * 8.0) then Entity.changeLookDirection()'s fixed
     * 0.15-per-unit multiplier. Both constants pulled from the actual
     * mapped 1.21.11 client, not guessed — this hook adds its return value
     * to cursorDeltaX/Y BEFORE that whole pipeline runs, so working
     * backwards from the desired yaw/pitch step is the only way the pull
     * speed doesn't end up silently tied to the player's mouse sensitivity.
     */
    private double[] toRawDelta(MinecraftClient mc, double yawStep, double pitchStep) {
        double sensitivity = mc.options.getMouseSensitivity().getValue();
        double curved = sensitivity * 0.6 + 0.2;
        double scale = curved * curved * curved * 8.0;
        double denom = 0.15 * scale;
        if (denom < 1.0E-6) return new double[]{0.0, 0.0};

        double rawX = yawStep / denom;
        double rawY = pitchStep / denom;

        if (mc.options.getInvertMouseX().getValue()) rawX = -rawX;
        if (mc.options.getInvertMouseY().getValue()) rawY = -rawY;

        return new double[]{rawX, rawY};
    }
}
