package dev.fede.module.impl;

import dev.fede.module.Category;
import dev.fede.module.Module;

/**
 * Criticals — fakes a tiny fall distance on attack so the server registers a critical hit.
 * Hooks into MultiPlayerGameModeAttackMixin.
 * Ported from Zenith DLC 2.0 (Grim-compatible mode).
 */
public final class CriticalsModule extends Module {

    public static volatile boolean ACTIVE = false;

    public CriticalsModule() {
        super("Criticals", "Always land critical hits on attacks", Category.COMBAT);
    }

    @Override
    protected void onEnable()  { ACTIVE = true;  }
    @Override
    protected void onDisable() { ACTIVE = false; }
}
