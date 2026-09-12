package dev.fede.zenithgui.adapter;

import net.minecraft.item.Item;
import java.util.HashSet;
import java.util.Set;

/** Stub for Zenith's ToolSelectSetting. Not used in feClient modules. */
public class ToolSelectSetting extends Setting {
    private final Set<Item> selected = new HashSet<>();

    public ToolSelectSetting(String name) {
        super(name);
    }

    public boolean contains(Item item) {
        return selected.contains(item);
    }

    public void add(Item item) {
        selected.add(item);
    }

    public void remove(Item item) {
        selected.remove(item);
    }
}
