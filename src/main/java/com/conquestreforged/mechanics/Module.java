package com.conquestreforged.mechanics;

import com.conquestreforged.mechanics.config.Config;

import java.util.HashMap;
import java.util.Map;

public interface Module {

    Map<String, Module> REGISTRY = new HashMap<>();

    void init();

    void onLoad(Config config);

    boolean isEnabled(Config config);

    void addConfigDefaults(Config config);
}
