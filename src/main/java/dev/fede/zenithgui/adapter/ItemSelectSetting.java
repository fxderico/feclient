package dev.fede.zenithgui.adapter;

import net.minecraft.block.Block;
import java.util.HashSet;
import java.util.Set;

/** Stub for Zenith's ItemSelectSetting. Not used in feClient modules. */
public class ItemSelectSetting extends Setting {
    private final Set<Block> selected = new HashSet<>();

    public ItemSelectSetting(String name) {
        super(name);
    }

    public boolean contains(Block block) {
        return selected.contains(block);
    }

    public void add(Block block) {
        selected.add(block);
    }

    public void remove(Block block) {
        selected.remove(block);
    }
}
