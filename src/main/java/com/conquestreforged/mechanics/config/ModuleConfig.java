package com.conquestreforged.mechanics.config;

public class ModuleConfig extends ObjectMap {

    public ModuleConfig() {
        set("enabled", true);
    }

    public boolean isEnabled() {
        Object value = get("enabled");
        if (value == null) {
            put("enabled", true);
            return true;
        }
        return value == Boolean.TRUE;
    }
}
