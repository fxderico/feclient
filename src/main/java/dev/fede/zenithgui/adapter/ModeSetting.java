package dev.fede.zenithgui.adapter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter wrapping our dev.fede.settings.ModeSetting into the Zenith ModeSetting API
 * expected by MenuModeSetting.
 *
 * Value instances are created once and reused so that identity comparisons (==)
 * in MenuModeSetting work correctly.
 */
public class ModeSetting extends Setting {

    private final dev.fede.settings.ModeSetting wrapped;
    private final List<Value> values;

    public ModeSetting(dev.fede.settings.ModeSetting wrapped) {
        super(wrapped.getName());
        this.wrapped = wrapped;
        this.visible = () -> wrapped.isVisible();
        // Create stable Value objects once so == comparisons stay consistent
        this.values = wrapped.getModes().stream()
                .map(mode -> new Value(this, mode))
                .collect(Collectors.toList());
    }

    /** All available values (stable list — same instances each call). */
    public List<Value> getValues() {
        return values;
    }

    /** The currently selected value (same instance as in getValues()). */
    public Value getValue() {
        String current = wrapped.get();
        return values.stream()
                .filter(v -> v.getName().equals(current))
                .findFirst()
                .orElse(values.isEmpty() ? null : values.get(0));
    }

    public void set(String modeName) {
        wrapped.set(modeName);
    }

    /** Sets the current value by Value reference (used by Zenith UI). */
    public void setValue(Value value) {
        if (value != null) {
            wrapped.set(value.getName());
        }
    }

    public String get() {
        return wrapped.get();
    }

    // ── inner Value class mirrors zenith.zov.client.modules.api.setting.impl.ModeSetting.Value ──

    public static class Value {
        private final ModeSetting parent;
        private final String name;

        Value(ModeSetting parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return "";
        }

        public Value select() {
            parent.wrapped.set(name);
            return this;
        }

        public boolean isSelected() {
            return parent.wrapped.get().equals(name);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
