package dev.fede.zenithgui;

import net.minecraft.util.Identifier;
import dev.fede.FeClient;

/**
 * Utility class providing Zenith-scoped resource identifiers.
 * In the original Zenith source this was a top-level registry class;
 * here it's a thin shim that routes resources under the feClient mod namespace.
 */
public final class Zenith {
    private Zenith() {}

    /** Returns an Identifier in the feClient namespace. */
    public static Identifier id(String path) {
        return Identifier.of(FeClient.MOD_ID, path);
    }
}
