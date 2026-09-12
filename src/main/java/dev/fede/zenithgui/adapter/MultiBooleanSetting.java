package dev.fede.zenithgui.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stub for Zenith's MultiBooleanSetting.
 * We don't have an equivalent in our settings, so this is never instantiated at runtime.
 * The Value inner class is needed so MenuSelectSetting compiles.
 */
public class MultiBooleanSetting extends Setting {

    private final List<Value> values = new ArrayList<>();

    public MultiBooleanSetting(String name) {
        super(name);
    }

    /** All options as Value objects. */
    public List<Value> getBooleanSettings() {
        return values;
    }

    /** Currently-selected (enabled) values. */
    public List<Value> getSelectedValues() {
        return values.stream().filter(Value::isEnabled).collect(Collectors.toList());
    }

    // ── Inner class ──────────────────────────────────────────────────────────

    public class Value {
        private final String name;
        private boolean enabled;

        public Value(String name, boolean enabled) {
            this.name = name;
            this.enabled = enabled;
        }

        public String getName() {
            return name;
        }

        public boolean isEnabled() {
            return enabled;
        }

        /** Toggle the enabled state of this option. */
        public void toggle() {
            enabled = !enabled;
        }

        public String getDescription() {
            return "";
        }
    }
}
