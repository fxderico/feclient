package dev.fede.zenithgui.adapter;

import net.minecraft.entity.EntityType;
import java.util.HashSet;
import java.util.Set;

/** Stub for Zenith's EntitySelectSetting. Not used in feClient modules. */
public class EntitySelectSetting extends Setting {
    private final Set<EntityType<?>> selected = new HashSet<>();

    public EntitySelectSetting(String name) {
        super(name);
    }

    public boolean contains(EntityType<?> type) {
        return selected.contains(type);
    }

    public void add(EntityType<?> type) {
        selected.add(type);
    }

    public void remove(EntityType<?> type) {
        selected.remove(type);
    }
}
