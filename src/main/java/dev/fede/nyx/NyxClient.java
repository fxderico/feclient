package dev.fede.nyx;

import dev.fede.nyx.module.ModuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal NyxClient stub — keeps CodeEngine's internal module lookups working.
 * MODULES is populated by NyxModuleBridge as each nyx module is bridged.
 */
public final class NyxClient {

    public static final Logger     LOGGER  = LoggerFactory.getLogger("feClient/nyx");
    public static final ModuleManager MODULES = new ModuleManager();

    private NyxClient() {}
}
