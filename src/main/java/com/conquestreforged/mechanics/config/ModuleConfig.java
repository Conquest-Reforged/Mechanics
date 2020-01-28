package com.conquestreforged.mechanics.config;

public class ModuleConfig extends ObjectMap {

    public ModuleConfig() {
        put("enabled", true);
    }

    @Override
    public Object put(String key, Object value) {
        if (key.equals("enabled")) {
            super.put(key, value);
            return null;
        }
        return super.put(key, value);
    }

    public boolean isEnabled() {
        Object enabled = get("enabled");
        if (enabled == null) {
            put("enabled", true);
            return true;
        }

        if (enabled instanceof Boolean) {
            return enabled.equals(Boolean.TRUE);
        }

        return (boolean) enabled;
    }
}
